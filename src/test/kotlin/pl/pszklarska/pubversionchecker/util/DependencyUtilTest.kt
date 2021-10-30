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
    fun packageNameContainsNameAndThreeNumbers() {
        assertTrue("test:1.0.0".isPackageName())
        assertFalse(":1.0.0".isPackageName())
        assertFalse("test".isPackageName())
        assertFalse("description: Flutter POS application".isPackageName())
    }

    @Test
    fun packageNameContainsSuffix() {
        assertTrue("test:1.0.0+4".isPackageName())
        assertTrue("test:1.0.0+hotfix.oopsie".isPackageName())
        assertTrue("test:1.0.0-alpha.12".isPackageName())
    }

    @Test
    fun packageNameDoesNotContainKeywords() {
        assertFalse("version: 1.0.0".isPackageName())
        assertFalse("sdk: 2.0.0".isPackageName())
        assertFalse("ref: v1.0.0".isPackageName())
        assertFalse("url: http://0.0.0.0:4000".isPackageName())
    }

    @Test
    fun packageNameContainsComments() {
        assertTrue("test: 1.0.0-alpha.12 # link.to.pub".isPackageName())
        assertFalse("# link.to.pub".isPackageName())
    }

    @Test
    fun packageNameContainsDifferentVersionConstraints() {
        assertTrue("test: ^1.2.3".isPackageName())
        assertTrue("test: >=1.2.3".isPackageName())
        assertTrue("test: >1.2.3".isPackageName())
        assertTrue("test: <=2.0.0".isPackageName())
        assertTrue("test: <2.0.0".isPackageName())
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
}