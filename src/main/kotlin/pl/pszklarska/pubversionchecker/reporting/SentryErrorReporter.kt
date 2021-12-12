package pl.pszklarska.pubversionchecker.reporting

import com.intellij.diagnostic.IdeaReportingEvent
import com.intellij.ide.DataManager
import com.intellij.idea.IdeaLogger
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.ui.Messages
import com.intellij.util.Consumer
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import java.awt.Component


class SentryErrorReporter : ErrorReportSubmitter() {

    override fun getPrivacyNoticeText(): String {
        return "To help me fix this problem, you can send an anonymous crash report. This report will contain " +
                "information on the crash, including your device model, system version, the plugin’s version and " +
                "build number. By sending this report you hereby agree to this policy."
    }


    override fun getReportActionText(): String {
        return "Report to Author"
    }

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val context = DataManager.getInstance().getDataContext(parentComponent)
        val project = CommonDataKeys.PROJECT.getData(context)
        object : Backgroundable(project, "Sending error report") {
            override fun run(indicator: ProgressIndicator) {
                val event = SentryEvent()
                event.level = SentryLevel.ERROR

                events.filterIsInstance(IdeaReportingEvent::class.java).forEach {
                    Sentry.setExtra("last_action", IdeaLogger.ourLastActionId)
                    Sentry.setExtra("additional_info", it.data.additionalInfo)
                    it.data.message?.let { message -> Sentry.setExtra("message", message) }

                    val exception = it.data.throwable
                    Sentry.captureException(exception)
                }

                ApplicationManager.getApplication().invokeLater {
                    Messages.showInfoMessage(
                        parentComponent,
                        "Thank you for submitting your report!",
                        "Error Report"
                    )
                    consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
                }
            }
        }.queue()
        return true
    }
}