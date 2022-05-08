<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Flutter Pub Version Checker Changelog

## [Unreleased]

## [1.3.5] - 2022-05-07
### Added
- Support for preview and prerelease packages ([#38](https://github.com/pszklarska/FlutterPubVersionChecker/issues/38))
- Go to pub.dev from the quick-fix menu ([#54](https://github.com/pszklarska/FlutterPubVersionChecker/issues/54))

### Fixed
- Errors not being reported to crash tracker

## [1.3.4] - 2022-05-04
### Fixed
- UI is frozen when editing pubspec.yaml

## [1.3.3] - 2021-12-13
### Added
- Support for the older IntelliJ IDEA versions

## [1.3.2] - 2021-12-12
### Added
- Crash reporting

### Fixed
- Dependency versions not being retrieved properly
- Pub.dev API responses serialization

## [1.3.1] - 2021-10-31
### Fixed
- Excluding dependencies with reserved keywords such as flutter, sdk, url etc.

## [1.3.0] - 2021-10-30
### Added
- Functionality to update all dependencies

## [1.2.7] - 2021-10-23
### Removed
- Support for older IDE versions

## [1.2.6] - 2021-10-20
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

### Fixed
- Decrypting response from Pub.dev API that caused NullPointerException
- Crash for one dependency doesn't crash the whole file

## [1.2.5] - 2020-09-13
- Fixed crash when updating dependency
- Fixed checking dependencies with the same suffix ([#21](https://github.com/pszklarska/FlutterPubVersionChecker/issues/21))

## [1.2.4] - 2020-07-09
- Fixed crash on the startup

## [1.2.3] - 2020-06-30
- Fixed issue with custom hosted url addresses

## [1.2.2] - 2020-06-29
- Updated the plugin to work with the newest IntelliJ versions
- Fixed issues with hosted or Git packages

## [1.2.1] - 2020-01-16
- Fixed compatibility issues

## [1.2.0] - 2020-01-16
- Improved performance of checking dependencies
- Updated the plugin to work with IntelliJ 2019.3.x

## [1.1.1] - 2019-06-13
- Added support for dependencies with version code
- Removed notification on startup

## [1.1.0] - 2019-05-21
- Fixed performance issues
- Added quick fix option to update dependencies

## [1.0.1] - 2019-05-13
- Fixed bug caused by commented dependencies</li>

## [1.0.0] - 2019-05-10
- First version of plugin</li>
- Added support for checking packages version in a format x.x.x</li></ul>