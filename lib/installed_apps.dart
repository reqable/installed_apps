import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:installed_apps/app_info.dart';

class InstalledApps {
  static const MethodChannel _channel = const MethodChannel('installed_apps');

  static Future<List<AppInfo>> getInstalledApps([
    bool excludeSystemApps = true,
    bool withIcon = false,
    String packageNamePrefix = "",
  ]) async {
    List<dynamic> apps = await _channel.invokeMethod(
      'getInstalledApps',
      {
        "exclude_system_apps": excludeSystemApps,
        "with_icon": withIcon,
        "package_name_prefix": packageNamePrefix,
      },
    );
    return apps.map((app) => AppInfo.create(app)).toList();
  }

  static Future<bool?> startApp(String packageName) async {
    return _channel.invokeMethod(
      "startApp",
      {"package_name": packageName},
    );
  }

  static void openSettings(String packageName) {
    _channel.invokeMethod(
      "openSettings",
      {"package_name": packageName},
    );
  }

  static Future<AppInfo?> getAppInfo(String packageName) async {
    var app = await _channel.invokeMethod(
      "getAppInfo",
      {"package_name": packageName},
    );
    if (app == null) {
      return null;
    } else {
      return AppInfo.create(app);
    }
  }

  static Future<Uint8List?> getAppIcon(String packageName) async {
    var app = await _channel.invokeMethod(
      "getAppIcon",
      {"package_name": packageName},
    );
    return app['icon'];
  }

  static Future<bool?> isSystemApp(String packageName) async {
    return _channel.invokeMethod(
      "isSystemApp",
      {"package_name": packageName},
    );
  }

  static Future<bool?> isInstalled(String packageName) async {
    return _channel.invokeMethod(
      "isInstalled",
      {"package_name": packageName},
    );
  }
}
