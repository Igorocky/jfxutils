package org.igye.jfxutils.dialog

import java.io.File
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.{ButtonType, Alert, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}

import org.apache.logging.log4j.Logger
import org.igye.commonutils.Implicits._
import org.igye.commonutils.{Enum, GeneralCaseInsensitiveStringFilter}
import org.igye.jfxutils.Implicits.nodeToNodeOps
import org.igye.jfxutils.autocomplete._

case class FileChooserType(name: String)
object FileChooserType extends Enum[FileChooserType] {
    val DIRS_AND_FILES = addElem(FileChooserType("DIRS_AND_FILES"))
    val DIRS_ONLY = addElem(FileChooserType("DIRS_ONLY"))
}

object TextFieldFileChooser {
    def apply(textField: TextField, width: Double, maxHeight: Double,
              fileChooserType: FileChooserType)
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
                                .map(f => new AutocompleteTextItem(f.getAbsolutePath, font)).toList
                        } else {
                            val path = new File(pathAndFilter.path)
                            if (path.exists()) {
                                path.listFiles()
                                    .filter(f =>
                                        (if (fileChooserType == FileChooserType.DIRS_ONLY) f.isDirectory else true)
                                            && filter.matches(f.getName)
                                    )
                                    .sortWith((f1, f2) =>
                                        f1.isDirectory && !f2.isDirectory
                                            || (
                                            (f1.isDirectory && f2.isDirectory || !f1.isDirectory && !f2.isDirectory)
                                                && f1.getName.compareTo(f2.getName) < 0
                                            )
                                    )
                                    .map(f => new AutocompleteTextItem(f.getName + (if (f.isDirectory) File.separator else ""), font)).toList
                            } else {
                                List(new AutocompleteTextItem("Error: directory doesn't exist.", font, Some(false)))
                            }
                        }
                    }),
                    userData = pathAndFilter
                )
            },
            modifyTextFieldWithResultParams = (userData, item) => {
                val path = userData.asInstanceOf[PathAndFilter].path
                val filter = userData.asInstanceOf[PathAndFilter].filter
                if (item.asInstanceOf[AutocompleteTextItem].userData.exists(!_.asInstanceOf[Boolean])) {
                    val newFullPath = path + filter
                    ModifyTextFieldWithResultParams(newFullPath, newFullPath.length)
                } else {
                    val newFullPath = path + item.asInstanceOf[AutocompleteTextItem].text
                    ModifyTextFieldWithResultParams(newFullPath, newFullPath.length)
                }
            }
        )
        textField.hnd(KeyEvent.KEY_PRESSED){e=>
            if (e.getCode == KeyCode.M && e.isControlDown) {
                val path = (if (textField.getText != null) textField.getText else "").trim
                if (path.nonEmpty) {
                    val file = new File(path)
                    if (!file.exists()) {
                        new Alert(AlertType.CONFIRMATION, s"Directory '${file.getAbsolutePath}' will be created.", ButtonType.OK, ButtonType.CANCEL)
                            .showAndWait().ifPresent(consumer(t => {
                            if (t == ButtonType.OK) {
                                file.mkdirs()
                                textField.setText(file.getAbsolutePath + File.separator)
                                textField.positionCaret(textField.getText.length)
                            }
                        }))
                    } else {
                        new Alert(AlertType.INFORMATION, s"Directory '${file.getAbsolutePath}' already exists.", ButtonType.OK).showAndWait()
                    }
                }
            }
        }
    }

    protected[dialog] case class PathAndFilter(path: String, filter: String)
    private val pathAndFilterPat = """(.*[\\/])?([^\\/]+)?""".r
    protected[dialog] def extractPathAndFilter(input: String): PathAndFilter = {
        val pathAndFilterPat(path, filter) = input
        PathAndFilter(if (path != null) path else "", if (filter != null) filter else "")
    }
}
