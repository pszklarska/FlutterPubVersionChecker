package pl.pszklarska.pubversionchecker.quickfix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import pl.pszklarska.pubversionchecker.resources.Strings


private const val PUB_DEV_PACKAGES_URL = "https://pub.dev/packages/"

internal class GoToPubDevQuickFix(
    private val packageName: String
) : BaseIntentionAction() {

    override fun getText(): String {
        return Strings.goToPubDevFixDescription.format(packageName)
    }

    override fun getFamilyName(): String {
        return Strings.fixFamilyName
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        BrowserUtil.browse(PUB_DEV_PACKAGES_URL + packageName)
    }
}