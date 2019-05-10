package pl.pszklarska.pubversionchecker

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.util.NotNullLazyValue

class NotificationLoadedComponent : ProjectComponent {

    override fun projectOpened() {}

    override fun projectClosed() {}

    override fun initComponent() {
        ApplicationManager.getApplication()
            .invokeLater({
                Notifications.Bus.notify(
                    NOTIFICATION_GROUP.value
                        .createNotification(
                            "Pub Packages Checker Active",
                            "Plugin checks for the Pub Packages latest versions",
                            NotificationType.INFORMATION,
                            null
                        )
                )
            }, ModalityState.NON_MODAL)
    }

    override fun disposeComponent() {}

    override fun getComponentName(): String {
        return NOTIFICATION_LOADED_COMPONENT
    }

    companion object {
        private const val NOTIFICATION_LOADED_COMPONENT =
            "NotificationLoadedComponent"

        private val NOTIFICATION_GROUP = object :
            NotNullLazyValue<NotificationGroup>() {
            override fun compute(): NotificationGroup {
                return NotificationGroup(
                    "Packages version checking",
                    NotificationDisplayType.STICKY_BALLOON,
                    true
                )
            }
        }
    }
}