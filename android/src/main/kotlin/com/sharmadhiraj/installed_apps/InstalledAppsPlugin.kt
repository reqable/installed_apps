package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.Manifest
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.sharmadhiraj.installed_apps.Util.Companion.convertAppToMap
import com.sharmadhiraj.installed_apps.Util.Companion.getPackageManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.Locale.ENGLISH


class InstalledAppsPlugin() : MethodCallHandler, FlutterPlugin, ActivityAware {

    companion object {

        var context: Context? = null

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            context = registrar.context()
            register(registrar.messenger())
        }

        @JvmStatic
        fun register(messenger: BinaryMessenger) {
            val channel = MethodChannel(messenger, "installed_apps")
            channel.setMethodCallHandler(InstalledAppsPlugin())
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        InstalledAppsPlugin.register(binding.getBinaryMessenger())
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        context = activityPluginBinding.getActivity()
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        context = activityPluginBinding.getActivity()
    }

    override fun onDetachedFromActivity() {}

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (context == null) {
            result.error("", "Something went wrong!", null)
            return
        }
        when (call.method) {
            "getInstalledApps" -> {
                Thread {
                    result.success(getInstalledApps())
                }.start()
            }
            "startApp" -> {
                val packageName: String? = call.argument("package_name")
                result.success(startApp(packageName))
            }
            "openSettings" -> {
                val packageName: String? = call.argument("package_name")
                openSettings(packageName)
            }
            "toast" -> {
                val message = call.argument("message") ?: ""
                val short = call.argument("short_length") ?: true
                toast(message, short)
            }
            "getAppInfo" -> {
                val packageName: String = call.argument("package_name") ?: ""
                result.success(getAppInfo(getPackageManager(context!!), packageName))
            }
            "isSystemApp" -> {
                val packageName: String = call.argument("package_name") ?: ""
                result.success(isSystemApp(getPackageManager(context!!), packageName))
            }
            "isInstalled" -> {
                val packageName: String = call.argument("package_name") ?: ""
                result.success(isInstalled(getPackageManager(context!!), packageName))
            }
            else -> result.notImplemented()
        }
    }

    private fun getInstalledApps(): List<Map<String, Any?>> {
        val packageManager = getPackageManager(context!!)
        var installedApps = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS).filter {
            app -> app.requestedPermissions?.contains(Manifest.permission.INTERNET) == true
        }
        return installedApps.map { app -> convertAppToMap(packageManager, app.applicationInfo) }
    }

    private fun startApp(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return try {
            val launchIntent = getPackageManager(context!!).getLaunchIntentForPackage(packageName)
            context!!.startActivity(launchIntent)
            true
        } catch (e: Exception) {
            print(e)
            false
        }
    }

    private fun toast(text: String, short: Boolean) {
        Toast.makeText(context!!, text, if (short) LENGTH_SHORT else LENGTH_LONG)
            .show()
    }

    private fun isSystemApp(packageManager: PackageManager, packageName: String): Boolean {
        return packageManager.getLaunchIntentForPackage(packageName) == null
    }

    private fun isInstalled(packageManager: PackageManager, packageName: String): Boolean {
        try {
            var app = packageManager.getApplicationInfo(packageName, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    private fun openSettings(packageName: String?) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        context!!.startActivity(intent)
    }

    private fun getAppInfo(
        packageManager: PackageManager,
        packageName: String
    ): Map<String, Any?>? {
        try {
            var app = packageManager.getApplicationInfo(packageName, 0)
            return convertAppToMap(packageManager, app)
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
    }

}
