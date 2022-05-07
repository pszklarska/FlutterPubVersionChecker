package pl.pszklarska.pubversionchecker.util

/**
 * Secrets are replaced by the CI's step. Ensure any changes in the filename or filepath are reflected there.
 */

class Secrets {
    companion object {
        const val sentryDsn = "https://24aac9c6a4234340ad74ce702884b69a@o1088436.ingest.sentry.io/6103170"
    }
}