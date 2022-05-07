package pl.pszklarska.pubversionchecker.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


class AppSettingsComponent {
    val panel: JPanel
    private val includePreReleasesCheckBox = JBCheckBox("Include prerelease and preview versions")

    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(includePreReleasesCheckBox, 1)
            .addTooltip("If not set, only stable versions are included")
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = includePreReleasesCheckBox

    var includePreReleases: Boolean
        get() = includePreReleasesCheckBox.isSelected
        set(newStatus) {
            includePreReleasesCheckBox.isSelected = newStatus
        }
}