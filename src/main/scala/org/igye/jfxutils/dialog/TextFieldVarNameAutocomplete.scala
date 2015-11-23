package org.igye.jfxutils.dialog

import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent

import org.apache.logging.log4j.Logger
import org.igye.commonutils.GeneralCaseInsensitiveStringFilter
import org.igye.jfxutils.Implicits.nodeToNodeOps
import org.igye.jfxutils.autocomplete._

object TextFieldVarNameAutocomplete {
    def apply(textField: TextField, width: Double, maxHeight: Double, varNameProvider: => List[String])
             (implicit log: Logger, executor : scala.concurrent.ExecutionContext): Unit = {
        val font = textField.getFont
        AutocompleteList.addAutocomplete(
            textField = textField,
            width = width,
            maxHeight = maxHeight,
            calcInitParams = (initStr, pos) => {
                val partsAndFilter = extractPartsAndFilter(initStr, pos)
                TextFieldAutocompleteInitParams(
                    caretPositionToOpenListAt = partsAndFilter.left.length,
                    query = new BasicAutocompleteQuery(() => {
                        val filter = new GeneralCaseInsensitiveStringFilter(partsAndFilter.filter)
                        if (partsAndFilter.left.endsWith("${") && partsAndFilter.right.startsWith("}")) {
                            varNameProvider.filter(filter.matches(_)).map(new AutocompleteTextItem(_, font))
                        } else {
                            Nil
                        }
                    }),
                    userData = partsAndFilter
                )
            },
            modifyTextFieldWithResultParams = (userData, item) => {
                val left = userData.asInstanceOf[PartsAndFilter].left
                val right = userData.asInstanceOf[PartsAndFilter].right
                val vname = item.asInstanceOf[AutocompleteTextItem].text
                val strBeforeCur = left + vname
                val newText = strBeforeCur + right
                ModifyTextFieldWithResultParams(newText, strBeforeCur.length)
            }
        )
        textField.hnd(KeyEvent.KEY_TYPED){e=>
            if (e.getCharacter == "$") {
                val left = textField.getText.substring(0, textField.getCaretPosition)
                val right = textField.getText.substring(textField.getCaretPosition)
                textField.setText(left + "${}" + right)
                textField.positionCaret(left.length + 2)
                e.consume()
            }
        }
    }

    protected[dialog] case class PartsAndFilter(left: String, right: String, filter: String)
    private val leftPartAndFilterPat = """^(.*\$\{)([^\$\{]*)$""".r
    private val rightPartPat = """^[^\}]*(\}.*)$""".r
    protected[dialog] def extractPartsAndFilter(input: String, pos: Int): PartsAndFilter = {
        val leftPartAndFilterStr = input.substring(0, pos)
        val rightPartStr = input.substring(pos)

        val (leftPart, filter) = leftPartAndFilterStr match {
            case leftPartAndFilterPat(leftPart, filter) => (leftPart, filter)
            case _ => (leftPartAndFilterStr, "")
        }
        val rightPart = if (leftPart.endsWith("{")) {
            rightPartStr match {
                case rightPartPat(rightPart) => rightPart
                case _ => rightPartStr
            }
        } else {
            rightPartStr
        }
        PartsAndFilter(
            if (leftPart != null) leftPart else "",
            if (rightPart != null) rightPart else "",
            if (filter != null) filter else ""
        )
    }
}
