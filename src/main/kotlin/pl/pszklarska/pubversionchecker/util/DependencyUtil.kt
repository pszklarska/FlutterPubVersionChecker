package pl.pszklarska.pubversionchecker.util

import pl.pszklarska.pubversionchecker.dto.Dependency
import java.util.regex.Pattern

const val DEPENDENCIES_PATTERN = """^(?!#)\s*(?!(?:version|sdk|ref|url|flutter)\b)(\S+):\s*[<|=>^]*([0-9]+\.[0-9]+\.[0-9]+\+?\S*)"""


/**
 * Read characters from String and return list of [Dependency] including full package name (e.g. "intl"), current version
 * (e.g. "1.0.0") and index of the first character of version name in the file
 */

fun String.getDependencies(): List<Dependency> {
    val dependencyList = mutableListOf<Dependency>()
    var line = ""
    var counter = 0
    forEach {
        counter++
        if (it == '\n') {
            line = line.trim()
            if (line.isDependencyName()) {
                val packageName = line.getPackageName()
                val currentVersion = line.getVersionName()
                dependencyList.add(Dependency(packageName, currentVersion, counter - 2))
                printMessage("Found dependency: $line")
            }
            line = ""
        } else {
            line += it
        }
    }
    return dependencyList
}

/**
 * Return if given string is a valid dependency, i.e. contains valid package name and version number
 */

fun String.isDependencyName(): Boolean {
    val regexPattern = Pattern.compile(DEPENDENCIES_PATTERN)
    return regexPattern.matcher(this).find()
}

/**
 * Extract package name from the string, e.g. return "package_name" from string "package_name: 1.0.0"
 */

fun String.getPackageName(): String {
    val regex = DEPENDENCIES_PATTERN.toRegex()
    try {
        return regex.find(this)?.groupValues?.get(1)!!
    } catch (e: Exception) {
        print(e)
        throw UnableToGetPackageNameException(this)
    }
}

/**
 * Extract version number from the string, e.g. return "1.0.0" from string "package_name: 1.0.0"
 */

fun String.getVersionName(): String {
    val regex = DEPENDENCIES_PATTERN.toRegex()
    try {
        return regex.find(this)?.groupValues?.get(2)!!
    } catch (e: Exception) {
        print(e)
        throw UnableToReadCurrentVersionException(this)
    }
}

/**
 * Find index of the first character of version name from a file text content for a given package name, e.g.
 * return 14 for "package_name: ^1.0.0"
 */

fun String.findVersionIndexInFile(packageName: String): Int {
    val indexOfPackageName = packageName.toRegex()
        .findAll(this)
        .map { it.range.first }
        .first() + packageName.length

    return this.substring(indexOfPackageName).indexOfFirst { it.isDigit() } + indexOfPackageName
}

class UnableToReadCurrentVersionException(dependency: String) :
    Exception("Cannot read current version number for dependency: $dependency")