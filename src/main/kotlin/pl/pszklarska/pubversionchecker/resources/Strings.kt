package pl.pszklarska.pubversionchecker.resources

class Strings {

    companion object {
        const val annotationDescription = "Latest available version is:"
        const val updateDescription = "Update"
        const val updateAllFixDescription =
            "Update\u200B all" // zero-length whitespace to place "Update all" below other fixes
        const val fixFamilyName = "Update package"
        const val errorReportDescription =
            "To help me fix this problem, you can send an anonymous crash report. This report will contain " +
                    "information on the crash, including your device model, system version, the plugin’s version " +
                    "and build number. By sending this report you hereby agree to this policy."
        const val errorReportButtonText = "Report to Author"
        const val errorReportLoaderText = "Sending error report"
        const val errorReportThankYouDialogText = "Thank you for submitting your report!"
        const val errorReportThankYouDialogTitle = "Error Report"
        const val settingsTitle = "Flutter Pub Version Checker"
        const val settingsIncludePreReleasesTitle = "Include prerelease and preview versions"
        const val settingsIncludePreReleasesTooltip = "Only stable versions are included if not set"
    }
}