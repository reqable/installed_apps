class AppInfo {

  final String name;
  final String packageName;
  final String? versionName;
  final int? versionCode;
  final bool isSystem;
  final int? updateTime;

  const AppInfo({
    required this.name,
    required this.packageName,
    this.versionName,
    this.versionCode,
    this.isSystem = false,
    this.updateTime,
  });

  const AppInfo.unknown(String packageName) : this(
    name: '',
    packageName: packageName,
  );

  factory AppInfo.create(dynamic data) {
    return AppInfo(
      name: data['name'],
      packageName: data['package_name'],
      versionName: data['version_name'],
      versionCode: data['version_code'],
      isSystem: data['system'] ?? false,
      updateTime: data['update_time'],
    );
  }

  String getVersionInfo() {
    return '$versionName ($versionCode)';
  }
}
