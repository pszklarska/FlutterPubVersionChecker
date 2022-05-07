package pl.pszklarska.pubversionchecker.parsing

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import pl.pszklarska.pubversionchecker.dto.Dependency
import pl.pszklarska.pubversionchecker.dto.DependencyDescription
import pl.pszklarska.pubversionchecker.util.VersionsRepository
import kotlin.test.assertEquals

class YamlParserTest {

    @Test
    fun parseAndReturnAllDependenciesIfVersionsDoNotMatch() {
        runBlocking {
            val versionsRepository = mock<VersionsRepository> {
                on { getLatestVersion(any()) } doReturn "10.0.0"
            }

            val expectedDependencies = listOf(
                DependencyDescription(Dependency("google_sign_in", "5.1.1", 213), "10.0.0"),
                DependencyDescription(Dependency("flutter_svg", "0.20.0+1", 238), "10.0.0"),
                DependencyDescription(Dependency("http", "0.13.3", 254), "10.0.0"),
                DependencyDescription(Dependency("equatable", "2.0.2", 274), "10.0.0"),
                DependencyDescription(Dependency("meta", "1.3.0", 289), "10.0.0"),
                DependencyDescription(Dependency("flutter_redux", "0.8.2", 313), "10.0.0"),
                DependencyDescription(Dependency("intl", "0.17.0", 329), "10.0.0"),
                DependencyDescription(Dependency("share", "2.0.1", 345), "10.0.0"),
                DependencyDescription(Dependency("firebase_core", "1.2.0", 369), "10.0.0"),
                DependencyDescription(Dependency("camera", "0.8.1", 386), "10.0.0"),
                DependencyDescription(Dependency("mocktail", "0.1.2", 457), "10.0.0"),
            )

            val yamlParser = YamlParser(testYaml, versionsRepository)
            val actualDependencies = yamlParser.inspectFile()

            assertEquals(expectedDependencies, actualDependencies)
        }
    }

    @Test
    fun parseAndReturnNoDependenciesIfAllVersionsMatch() {
        runBlocking {
            val versionsRepository = mock<VersionsRepository> {
                on { getLatestVersion("google_sign_in") } doReturn "5.1.1"
                on { getLatestVersion("flutter_svg") } doReturn "0.20.0+1"
                on { getLatestVersion("http") } doReturn "0.13.3"
                on { getLatestVersion("equatable") } doReturn "2.0.2"
                on { getLatestVersion("meta") } doReturn "1.3.0"
                on { getLatestVersion("flutter_redux") } doReturn "0.8.2"
                on { getLatestVersion("intl") } doReturn "0.17.0"
                on { getLatestVersion("share") } doReturn "2.0.1"
                on { getLatestVersion("firebase_core") } doReturn "1.2.0"
                on { getLatestVersion("camera") } doReturn "0.8.1"
                on { getLatestVersion("mocktail") } doReturn "0.1.2"
            }

            val expectedDependencies = emptyList<DependencyDescription>()

            val yamlParser = YamlParser(testYaml, versionsRepository)
            val actualDependencies = yamlParser.inspectFile()

            assertEquals(expectedDependencies, actualDependencies)
        }
    }
}

val testYaml = """
    name: test
    description: test
    version: 1.0.0+1000
    publish_to: none

    environment:
      sdk: ">=2.12.0 <3.0.0"

    dependencies:
      flutter:
        sdk: flutter
      flutter_localizations:
        sdk: flutter
      google_sign_in: ^5.1.1
      flutter_svg: ^0.20.0+1
      http: ^0.13.3
      equatable: ^2.0.2
      meta: ^1.3.0
      flutter_redux: ^0.8.2
      intl: ^0.17.0
      share: ^2.0.1
      firebase_core: ^1.2.0
      camera: ^0.8.1

    dev_dependencies:
      flutter_test:
        sdk: flutter
      mocktail: ^0.1.2

    flutter:
      uses-material-design: true
      assets:
        - assets/

      fonts:
        - family: Rubik
          fonts:
            - asset: fonts/Rubik-Light.ttf

""".trimIndent()