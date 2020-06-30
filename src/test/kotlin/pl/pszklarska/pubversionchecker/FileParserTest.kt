package pl.pszklarska.pubversionchecker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class FileParserTest {

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
    fun extractVersionIfContainsNameAndThreeNumbers() {
        assertEquals("1.0.0", "test:1.0.0".extractVersion())
    }

    @Test
    fun extractVersionIfContainsSuffix() {
        assertEquals("1.0.0+4", "test:1.0.0+4".extractVersion())
        assertEquals("1.0.0+hotfix.oopsie", "test:1.0.0+hotfix.oopsie".extractVersion())
        assertEquals("1.0.0-alpha.12", "test:1.0.0-alpha.12".extractVersion())
    }

    @Test
    fun extractVersionIfContainsComments() {
        assertEquals("1.0.0-alpha.12", "test: 1.0.0-alpha.12 # link.to.pub".extractVersion())
    }

    @Test
    fun extractVersionIfContainsDifferentVersionConstraints() {
        assertEquals("1.2.3", "test: ^1.2.3".extractVersion())
        assertEquals("1.2.3", "test: >=1.2.3".extractVersion())
        assertEquals("1.2.3", "test: >1.2.3".extractVersion())
        assertEquals("2.0.0", "test: <=2.0.0".extractVersion())
        assertEquals("2.0.0", "test: <2.0.0".extractVersion())
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