package pl.pszklarska.pubversionchecker.util

import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import pl.pszklarska.pubversionchecker.settings.AppSettingsState
import kotlin.test.assertEquals

class VersionsRepositoryTest {

    @Test
    fun getLatestVersionIfShouldNotIncludePreReleases() {
        val httpClient = mock<DependencyHttpClient> {
            on { getContentAsString(any()) } doReturn """
                {
                  "name": "test_package",
                  "latest": {
                    "version": "0.13.4",
                    "published": "2022-12-11T21:38:04.577020Z"
                  },
                  "versions": [
                    {
                      "version": "0.13.3",
                      "published": "2022-11-30T20:40:39.500320Z"
                    },
                    {
                      "version": "0.13.4",
                      "published": "2022-12-11T21:38:04.577020Z"
                    },
                    {
                      "version": "0.13.5",
                      "published": "2022-12-18T18:00:14.561230Z"
                    }
                  ]
                }
            """.trimIndent()
        }

        val appSettingsState = mock<AppSettingsState> {
            on { includePreRelease } doReturn false
        }

        val versionsRepository = VersionsRepository(httpClient, appSettingsState)
        val actualLatestVersion = versionsRepository.getLatestVersion("test_package")
        val expectedLatestVersion = "0.13.4"

        assertEquals(expectedLatestVersion, actualLatestVersion)
    }
    @Test
    fun getLatestVersionIfShouldIncludePreReleases() {
        val httpClient = mock<DependencyHttpClient> {
            on { getContentAsString(any()) } doReturn """
                {
                  "name": "test_package",
                  "latest": {
                    "version": "0.13.4",
                    "published": "2022-12-11T21:38:04.577020Z"
                  },
                  "versions": [
                    {
                      "version": "0.13.3",
                      "published": "2022-11-30T20:40:39.500320Z"
                    },
                    {
                      "version": "0.13.4",
                      "published": "2022-12-11T21:38:04.577020Z"
                    },
                    {
                      "version": "0.13.5",
                      "published": "2022-12-18T18:00:14.561230Z"
                    }
                  ]
                }
            """.trimIndent()
        }

        val appSettingsState = mock<AppSettingsState> {
            on { includePreRelease } doReturn true
        }

        val versionsRepository = VersionsRepository(httpClient, appSettingsState)
        val actualLatestVersion = versionsRepository.getLatestVersion("test_package")
        val expectedLatestVersion = "0.13.5"

        assertEquals(expectedLatestVersion, actualLatestVersion)
    }
}