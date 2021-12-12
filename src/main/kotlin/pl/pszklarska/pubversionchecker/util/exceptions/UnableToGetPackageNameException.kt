package pl.pszklarska.pubversionchecker.util.exceptions

class UnableToGetPackageNameException(dependency: String, e: Throwable) :
    Exception("Cannot read package name for package: $dependency", e)
