package pl.pszklarska.pubversionchecker.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DependencyUtilTest {

    @Test
    fun extractVersionIfContainsNameAndThreeNumbers() {
        assertEquals("1.0.0", "test:1.0.0".getVersionName())
    }

    @Test
    fun extractVersionIfContainsSuffix() {
        assertEquals("1.0.0+4", "test:1.0.0+4".getVersionName())
        assertEquals("1.0.0+hotfix.oopsie", "test:1.0.0+hotfix.oopsie".getVersionName())
        assertEquals("1.0.0-alpha.12", "test:1.0.0-alpha.12".getVersionName())
    }

    @Test
    fun extractVersionIfContainsComments() {
        assertEquals("1.0.0-alpha.12", "test: 1.0.0-alpha.12 # link.to.pub".getVersionName())
    }

    @Test
    fun extractVersionIfContainsDifferentVersionConstraints() {
        assertEquals("1.2.3", "test: ^1.2.3".getVersionName())
        assertEquals("1.2.3", "test: >=1.2.3".getVersionName())
        assertEquals("1.2.3", "test: >1.2.3".getVersionName())
        assertEquals("2.0.0", "test: <=2.0.0".getVersionName())
        assertEquals("2.0.0", "test: <2.0.0".getVersionName())
    }


    @Test
    fun dependencyNameContainsNameAndThreeNumbers() {
        assertTrue("test:1.0.0".isDependencyName())
        assertFalse(":1.0.0".isDependencyName())
        assertFalse("test".isDependencyName())
        assertFalse("description: Flutter POS application".isDependencyName())
    }

    @Test
    fun dependencyNameContainsSuffix() {
        assertTrue("test:1.0.0+4".isDependencyName())
        assertTrue("test:1.0.0+hotfix.oopsie".isDependencyName())
        assertTrue("test:1.0.0-alpha.12".isDependencyName())
    }

    @Test
    fun dependencyNameDoesNotContainKeywords_invalidDependencyName() {
        assertFalse("version: 1.0.0".isDependencyName())
        assertFalse("sdk: 2.0.0".isDependencyName())
        assertFalse("ref: v1.0.0".isDependencyName())
        assertFalse("url: http://0.0.0.0:4000".isDependencyName())
        assertFalse("flutter: ^0.1.2".isDependencyName())
    }

    @Test
    fun dependencyNameDoesNotContainKeywords_validDependencyName() {
        assertTrue("flutter_svg: ^0.1.2".isDependencyName())
        assertTrue("test_version: 0.1.2".isDependencyName())
        assertTrue("test_sdk_test: 2.0.0".isDependencyName())
        assertTrue("url_match: 1.0.0".isDependencyName())
        assertTrue("abc_url: 1.0.0".isDependencyName())
    }

    @Test
    fun dependencyNameContainsComments() {
        assertTrue("test: 1.0.0-alpha.12 # link.to.pub".isDependencyName())
        assertFalse("#link.to.pub".isDependencyName())
        assertFalse("# link.to.pub".isDependencyName())
    }

    @Test
    fun dependencyNameContainsDifferentVersionConstraints() {
        assertTrue("test: ^1.2.3".isDependencyName())
        assertTrue("test: >=1.2.3".isDependencyName())
        assertTrue("test: >1.2.3".isDependencyName())
        assertTrue("test: <=2.0.0".isDependencyName())
        assertTrue("test: <2.0.0".isDependencyName())
    }

    @Test
    fun findsIndexOfVersionName() {
        val testFile = """
            test: ^1.2.3
            test_package: 2.0.0
            other_test_package:   1.0.0-alpha.12
        """.trimIndent()
        assertEquals(7, testFile.findVersionIndexInFile("test"))
        assertEquals(27, testFile.findVersionIndexInFile("test_package"))
        assertEquals(55, testFile.findVersionIndexInFile("other_test_package"))
    }

    @Test
    fun getPackageNameIfContainsNameAndThreeNumbers() {
        assertEquals("test", "test: ^1.0.0".getPackageName())
    }

    @Test
    fun getPackageNameIfContainsSuffix() {
        assertEquals("test", "test: 1.0.0+4".getPackageName())
        assertEquals("test", "test: 1.0.0+hotfix.oopsie".getPackageName())
        assertEquals("test", "test: 1.0.0-alpha.12".getPackageName())
    }
}