package pl.pszklarska.pubversionchecker.util.exceptions

class UnableToGetCurrentVersionException(dependency: String, e: Throwable) :
    Exception("Cannot get current version number for package: $dependency", e)