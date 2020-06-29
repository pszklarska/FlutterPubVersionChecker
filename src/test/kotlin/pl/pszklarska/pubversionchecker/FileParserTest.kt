package pl.pszklarska.pubversionchecker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
        assertTrue("test:1.0.0-alpha.12 # link.to.pub".isPackageName())
    }

    @Test
    fun packageNameDoesntContainVersionOrSdk() {
        assertFalse("version:1.0.0".isPackageName())
        assertFalse("sdk: '>=2.0.0 <3.0.0'".isPackageName())
    }
}