package pl.pszklarska.pubversionchecker.util

import org.junit.Test
import kotlin.test.assertEquals

class DependencyUtilTest {

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
}