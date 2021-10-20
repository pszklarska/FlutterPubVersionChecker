package pl.pszklarska.pubversionchecker.util

import org.junit.Test
import kotlin.test.assertEquals

class VersionsRepositoryTest {
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