package pl.pszklarska.pubversionchecker.reporting

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import io.sentry.Sentry
import pl.pszklarska.pubversionchecker.util.Secrets

class CrashReporting {

    private val sentryDsn = Secrets.sentryDsn

    fun init() {
        try {
            val plugin = PluginManagerCore.getPlugin(PluginId.getId("pl.pszklarska.pubversionchecker"))
            Sentry.init { options ->
                options.dsn = sentryDsn
                options.release = "pub-version-checker@${plugin?.version}"
                options.serverName = ""
            }
        } catch (e: Throwable) {
            // no-op
        }
    }
}