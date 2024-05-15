package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import java.io.ByteArrayOutputStream


class Util {

    companion object {

        fun convertAppToMap(
            packageManager: PackageManager,
            app: ApplicationInfo,
        ): HashMap<String, Any?> {
            val map = HashMap<String, Any?>()
            map["name"] = packageManager.getApplicationLabel(app)
            if (map["name"] == app.packageName) {
                packageManager.getLaunchIntentForPackage(app.packageName)?.let {
                    val activityList: List<ResolveInfo> = packageManager.queryIntentActivities(it, 0)
                    if (activityList.isNotEmpty()) {
                        val activityName = activityList[0].activityInfo.loadLabel(packageManager)
                        if (activityName.isNotEmpty()) {
                            map["name"] = activityName
                        }
                    }
                }
            }
            map["package_name"] = app.packageName
            map["system"] = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val packageInfo = packageManager.getPackageInfo(app.packageName, 0)
            map["update_time"] = packageInfo.lastUpdateTime
            return map
        }

        fun drawableToByteArray(drawable: Drawable): ByteArray {
            val bitmap = drawableToBitmap(drawable)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
            return stream.toByteArray()
        }

        private fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            val bitmap = Bitmap.createBitmap(
                128.coerceAtMost(drawable.intrinsicWidth),
                128.coerceAtMost(drawable.intrinsicHeight),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun getPackageManager(context: Context): PackageManager {
            return context.packageManager
        }

        @Suppress("DEPRECATION")
        private fun getVersionCode(packageInfo: PackageInfo): Long {
            return if (SDK_INT < P) packageInfo.versionCode.toLong()
            else packageInfo.longVersionCode
        }

    }

}