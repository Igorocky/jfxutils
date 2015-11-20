package org.igye.jfxutils.autocomplete

import java.lang
import javafx.scene.control.TextField
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import javafx.scene.layout._
import javafx.scene.text.Text

import com.sun.javafx.tk.Toolkit
import org.apache.logging.log4j.Logger
import org.igye.jfxutils.concurrency.{RunInJfxThread, RunInJfxThreadForcibly}
import org.igye.jfxutils.exceptions.JfxUtilsException
import org.igye.jfxutils.properties.{ChgListener, Expr}
import org.igye.jfxutils.{JfxUtils, nodeToHasEvens, observableValueToObservableValueOperators, propertyToPropertyOperators}

import scala.concurrent.Future

/*
    Test notes:
    1) check that all scroll bars work.
    2) it is possible to scroll the horizontal bar to the rightmost position and then navigate through item by up/down keys
    3) when textedit looses focus the autocomplete should close
 */
private class AutocompleteList(posX: Int, posY: Int, direction: ListDirection, width: Double, maxHeight: Double,
                               stackPane: StackPane,
                               loadingImage: ImageView, query: AutocompleteQuery,
                               itemSelectedEventHandler: () => Unit)
                              (implicit log: Logger, executor : scala.concurrent.ExecutionContext) {
    private val upperPane = new Pane()
    upperPane.hnd(MouseEvent.MOUSE_CLICKED){e=>close()}
    @volatile
    private var resultsAreReady = false
    @volatile
    private var closed = false

    private var queryResult: Option[List[AutocompleteItem]] = None
    private var selectedIdx = 0
    private var resultsPane: ResultsPane = _

    RunInJfxThread {
        stackPane.getChildren.add(upperPane)
        open()
    }

    private def showLoadingPane(): Unit = {
        RunInJfxThread {
            if (!closed && !resultsAreReady) {
                upperPane.getChildren.clear()
                upperPane.getChildren.add(createLoadingPane())
            }
        }
    }

    private def showResults(list: List[AutocompleteItem]): Unit = {
        RunInJfxThread {
            if (!closed && !resultsAreReady) {
                resultsAreReady = true
                upperPane.getChildren.clear()
                resultsPane = createPaneWithResults(list)
                upperPane.getChildren.add(resultsPane)
                Future {
                    Thread.sleep(50)
                    resultsPane.correctHeight()
                }
            }
        }
    }

    private def prepareDropDownPane(pane: Region): Unit = {
        pane.setLayoutX(posX)
        if (direction == ListDirection.UP) {
            pane.layoutYProperty() <== Expr(pane.heightProperty()){
                new lang.Double(posY - pane.heightProperty().get()).asInstanceOf[Number]
            }
        } else {
            pane.setLayoutY(posY)
        }
    }

    private def createLoadingPane() = {
        val loadingPane = new VBox(loadingImage)
        prepareDropDownPane(loadingPane)
        loadingPane.hnd(MouseEvent.ANY){e => e.consume()}
        loadingPane
    }

    private def createPaneWithResults(list: List[AutocompleteItem]) = {
        val paneWithResults = new ResultsPane(width, maxHeight)
        prepareDropDownPane(paneWithResults)
        if (list.isEmpty) {
            paneWithResults.addItem(new Text("Nothing found"))
        } else {
            queryResult = Option(list)
            list(0).select()
            for (i <- 0 until list.size) {
                val node = list(i)
                paneWithResults.addItem(node)
                node.hnd(MouseEvent.MOUSE_CLICKED){e =>
                    queryResult.get(selectedIdx).unselect()
                    selectedIdx = i
                    queryResult.get(selectedIdx).select()
                    if (e.getClickCount == 2) {
                        itemSelectedEventHandler()
                    }
                }
            }
            paneWithResults.hnd(KeyEvent.KEY_PRESSED){e=>
                if (e.getCode == KeyCode.ESCAPE) {
                    close()
                } else if (e.getCode == KeyCode.UP) {
                    up()
                } else if (e.getCode == KeyCode.DOWN) {
                    down()
                } else if (e.getCode == KeyCode.ENTER) {
                    itemSelectedEventHandler()
                }
            }
        }
        paneWithResults
    }

    private def open(): Unit = {
        query.execute(showResults)
        Future {
            Thread.sleep(300)
            RunInJfxThread {
                showLoadingPane()
            }
        }
    }

    def close(): Unit = {
        RunInJfxThread {
            closed = true
            query.cancel()
            stackPane.getChildren.remove(upperPane)
        }
    }

    def down(): Unit = {
        if (queryResult.isDefined) {
            RunInJfxThread {
                queryResult.get(selectedIdx).unselect()
                selectedIdx += 1
                if (selectedIdx >= queryResult.get.size) {
                    selectedIdx = 0
                }
                val item = queryResult.get(selectedIdx)
                item.select()
                correctViewport()
            }
        }
    }

    def up(): Unit = {
        if (queryResult.isDefined) {
            RunInJfxThread {
                queryResult.get(selectedIdx).unselect()
                selectedIdx -= 1
                if (selectedIdx <= -1) {
                    selectedIdx = queryResult.get.size - 1
                }
                val item = queryResult.get(selectedIdx)
                item.select()
                correctViewport()
            }
        }
    }

    private def correctViewport(): Unit = {
        if (queryResult.isDefined) {
            val selectedItem = queryResult.get(selectedIdx)
            resultsPane.correctViewportM(selectedItem.getLayoutY, selectedItem.getLayoutY + selectedItem.getLayoutBounds.getHeight)
        }
    }

    def getSelected: Option[AutocompleteItem] = {
        if (queryResult.isDefined) {
            Option(queryResult.get(selectedIdx))
        } else {
            None
        }
    }

    def hasFocus = resultsPane != null && resultsPane.hasFocus
}

case class TextFieldAutocompleteInitParams(
                                              caretPositionToOpenListAt: Int
                                              , query: AutocompleteQuery
                                              , userData: Any
                                          )

case class ModifyTextFieldWithResultParams(newText: String, placeCaretAt: Int)

object AutocompleteList {
    private val imageView: ImageView = new ImageView(new Image("ajax-loader.gif"))
    private var lastCreatedInst: Option[AutocompleteList] = None

    private def apply(posX: Double, posY: Double, direction: ListDirection, width: Double, height: Double,
                      stackPane: StackPane,
                      loadingImage: ImageView, query: AutocompleteQuery,
                      itemSelectedEventHandler: () => Unit)
                    (implicit log: Logger, executor : scala.concurrent.ExecutionContext): AutocompleteList = {
        lastCreatedInst.foreach(_.close())
        lastCreatedInst = Some(new AutocompleteList(
            posX.toInt, posY.toInt, direction, width, height, stackPane, loadingImage, query, itemSelectedEventHandler
        ))
        lastCreatedInst.get
    }

    def addAutocomplete(textField: TextField, width: Double, maxHeight: Double,
                        calcInitParams: (String/*all text*/, Int/*caret position*/) => TextFieldAutocompleteInitParams,
                        modifyTextFieldWithResultParams: (Any/*userData*/, AutocompleteItem/*selected item*/) => ModifyTextFieldWithResultParams)
                       (implicit log: Logger, executor : scala.concurrent.ExecutionContext): Unit = {
        var autoCmp: Option[AutocompleteList] = None
        var userData: Any = null
        def onItemSelected(): Unit = {
            if (autoCmp.isDefined) {
                val resultParams = modifyTextFieldWithResultParams(userData, autoCmp.get.getSelected.get)
                textField.setText(resultParams.newText)
                RunInJfxThreadForcibly {
                    textField.requestFocus()
                    textField.positionCaret(resultParams.placeCaretAt)
                }
                autoCmp.get.close()
                autoCmp = None
            }
        }
        def onEscape(): Unit = {
            if (autoCmp.isDefined) {
                autoCmp.get.close()
                autoCmp = None
            }
        }
        textField.hnd(KeyEvent.KEY_PRESSED){e=>
            if (e.getCode == KeyCode.SPACE && e.isControlDown) {
                autoCmp.foreach(_.close())
                var initText = textField.getText
                if (initText == null) {
                    initText = ""
                }
                val initParams = calcInitParams(initText, textField.getCaretPosition)
                userData = initParams.userData
                val (direction, posY, height) = calcDirectionYposAndHeight(textField, maxHeight)
                autoCmp = Some(AutocompleteList(
                    posX = calcXPos(initText, textField, initParams.caretPositionToOpenListAt, width),
                    posY = posY,
                    direction = direction,
                    width = width,
                    height = height,
                    stackPane = JfxUtils.findParent[StackPane](textField)
                        .getOrElse(throw new JfxUtilsException("The TextField should be contained in a StackPane.")),
                    loadingImage = imageView,
                    query = initParams.query,
                    itemSelectedEventHandler = () => onItemSelected()
                ))
                e.consume()
            } else if (autoCmp.isDefined) {
                if (e.getCode == KeyCode.ESCAPE) {
                    onEscape()
                    e.consume()
                } else if (e.getCode == KeyCode.UP) {
                    autoCmp.get.up()
                    e.consume()
                } else if (e.getCode == KeyCode.DOWN) {
                    autoCmp.get.down()
                    e.consume()
                } else if (e.getCode == KeyCode.ENTER) {
                    onItemSelected()
                    e.consume()
                }
            }
        }
        textField.focusedProperty() ==> ChgListener{chg=>
            if (autoCmp.isDefined && !chg.newValue && !autoCmp.get.hasFocus) {
                onEscape()
            }
        }
    }

    private def calcXPos(initText: String, textField: TextField, caretPosition: Int, width: Double): Double = {
        val substr = initText.substring(0, caretPosition)
        val requiredXPos = textField.localToScene(0, 0).getX +
            Toolkit.getToolkit().getFontLoader().computeStringWidth(substr, textField.getFont) +
            textField.getPadding.getLeft
        val maxPossibleXPos = textField.getScene.getWidth - width
        if (requiredXPos < maxPossibleXPos) {
            requiredXPos
        } else {
            maxPossibleXPos
        }
    }

    private def calcDirectionYposAndHeight(textField: TextField, maxHeight: Double) = {
        val lowerY = textField.localToScene(0, textField.getLayoutBounds.getHeight).getY
        val lowerHeight = textField.getScene.getHeight - lowerY
        if (maxHeight <= lowerHeight) {
            (ListDirection.DOWN, lowerY, maxHeight)
        } else {
            val upperY = textField.localToScene(0, 0).getY
            val upperHeight = upperY
            if (maxHeight <= upperHeight) {
                (ListDirection.UP, upperY, maxHeight)
            } else {
                if (lowerHeight >= upperHeight) {
                    (ListDirection.DOWN, lowerY, lowerHeight)
                } else {
                    (ListDirection.UP, upperY, upperHeight)
                }
            }
        }
    }
}