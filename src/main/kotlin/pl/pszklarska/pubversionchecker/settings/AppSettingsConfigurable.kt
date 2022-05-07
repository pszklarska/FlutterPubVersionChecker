package pl.pszklarska.pubversionchecker.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {
    private var appSettingsComponent: AppSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Flutter Pub Version Checker Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return appSettingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        appSettingsComponent = AppSettingsComponent()
        return appSettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings: AppSettingsState = AppSettingsState.instance
        return appSettingsComponent!!.includePreReleases != settings.includePreRelease
    }

    override fun apply() {
        appSettingsComponent?.let {
            val settings: AppSettingsState = AppSettingsState.instance
            settings.includePreRelease = it.includePreReleases
        }
    }

    override fun reset() {
        appSettingsComponent?.let {
            val settings: AppSettingsState = AppSettingsState.instance
            it.includePreReleases = settings.includePreRelease
        }
    }

    override fun disposeUIResources() {
        appSettingsComponent = null
    }
}