package org.igye.jfxutils.dialog

import java.io.File
import javafx.scene.control.TextField

import org.apache.logging.log4j.Logger
import org.igye.commonutils.GeneralCaseInsensitiveStringFilter
import org.igye.jfxutils.autocomplete._

object TextFieldFileChooser {
    def apply(textField: TextField, width: Double, maxHeight: Double)
             (implicit log: Logger, executor : scala.concurrent.ExecutionContext): Unit = {
        val font = textField.getFont
        AutocompleteList.addAutocomplete(
            textField = textField,
            width = width,
            maxHeight = maxHeight,
            calcInitParams = (initStr, pos) => {
                val pathAndFilter = extractPathAndFilter(initStr.substring(0, pos))
                TextFieldAutocompleteInitParams(
                    caretPositionToOpenListAt = pathAndFilter.path.length,
                    query = new BasicAutocompleteQuery(() => {
                        val filter = new GeneralCaseInsensitiveStringFilter(pathAndFilter.filter)
                        if (pathAndFilter.path.isEmpty) {
                            File.listRoots()
                                .filter(f => filter.matches(f.getAbsolutePath))
                                .sortWith((f1, f2) => f1.getName.compareTo(f2.getName) < 0)
                                .map(f => new AutocompleteTextItem(f.getAbsolutePath.replaceAllLiterally("\\", "/"), font)).toList
                        } else {
                            val path = new File(pathAndFilter.path)
                            if (path.exists()) {
                                path.listFiles()
                                    .filter(f => filter.matches(f.getName))
                                    .sortWith((f1, f2) =>
                                        f1.isDirectory && !f2.isDirectory
                                            || (
                                            (f1.isDirectory && f2.isDirectory || !f1.isDirectory && !f2.isDirectory)
                                                && f1.getName.compareTo(f2.getName) < 0
                                            )
                                    )
                                    .map(f => new AutocompleteTextItem(f.getName + (if (f.isDirectory) "/" else ""), font)).toList
                            } else {
                                List(new AutocompleteTextItem("Error: directory doesn't exist.", font, Some(false)))
                            }
                        }
                    }),
                    userData = pathAndFilter
                )
            },
            modifyTextFieldWithResultParams = (userData, item) => {
                val path = if (userData.asInstanceOf[PathAndFilter].path != null) userData.asInstanceOf[PathAndFilter].path else ""
                val filter = if (userData.asInstanceOf[PathAndFilter].filter != null) userData.asInstanceOf[PathAndFilter].filter else ""
                if (item.asInstanceOf[AutocompleteTextItem].userData.exists(!_.asInstanceOf[Boolean])) {
                    val newFullPath = path + filter
                    ModifyTextFieldWithResultParams(newFullPath, newFullPath.length)
                } else {
                    val newFullPath = path + item.asInstanceOf[AutocompleteTextItem].text
                    ModifyTextFieldWithResultParams(newFullPath, newFullPath.length)
                }
            }
        )
    }

    protected[dialog] case class PathAndFilter(path: String, filter: String)
    private val pathAndFilterPat = """(.*[\\/])?([^\\/]+)?""".r
    protected[dialog] def extractPathAndFilter(input: String): PathAndFilter = {
        val pathAndFilterPat(path, filter) = input
        PathAndFilter(if (path != null) path else "", if (filter != null) filter else "")
    }
}
