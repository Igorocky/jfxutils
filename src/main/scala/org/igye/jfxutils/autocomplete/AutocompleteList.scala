package org.igye.jfxutils.autocomplete

import javafx.scene.Node
import javafx.scene.control.{ScrollPane, TextField}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.text.Text

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.concurrency.{RunInJfxThread, RunInJfxThreadForcibly}
import org.igye.jfxutils.properties.ChgListener
import org.igye.jfxutils.{JfxUtils, nodeToHasEvens, observableValueToObservableValueOperators, propertyToPropertyOperators}

import scala.collection.JavaConversions._
import scala.concurrent.Future

/*
    Test notes:
    1) check that all scroll bars work.
    2) it is possible to scroll the horizontal bar to the rightmost position and then navigate through item by up/down keys
    3) when textedit looses focus the autocomplete should close
 */
private class ResultsPane extends ScrollPane {
    private val vbox = new VBox()
    setContent(vbox)
    this.hnd(MouseEvent.ANY){e => e.consume()}
    vbox.backgroundProperty() <== backgroundProperty()

    def correctViewPort(y1: Double, y2: Double): Unit = {
        val H = vbox.getLayoutBounds.getHeight
        val h = getViewportBounds.getHeight
        val y = getVvalue*(H - h)
        val ys = getVvalue*(H - h) + h
        if (y1 < y) {
            setVvalue(y1/(H - h))
        } else if (y2 > ys) {
            setVvalue((y2 - h)/(H - h))
        }
    }

    def addItem(item: Node): Unit = {
        vbox.getChildren.add(item)
    }

    def hasFocus = {
        isFocused || vbox.isFocused ||
            vbox.getChildren.find{i =>
                i.isInstanceOf[AutocompleteItem] && i.asInstanceOf[AutocompleteItem].hasFocus ||
                i.isFocused
            }.isDefined
    }
}

private class AutocompleteList(posX: Double, posY: Double, width: Double, height: Double,
                       stackPane: StackPane, textToComplete: String,
                       loadingImage: ImageView, query: AutocompleteQuery,
                       itemSelectedEventHandler: () => Unit)
                              (implicit log: Logger, executor : scala.concurrent.ExecutionContext) {
    private def this(textField: TextField, height: Double, stackPane: StackPane,
                     loadingImage: ImageView, query: AutocompleteQuery,
                     itemSelectedEventHandler: ()=> Unit)
                    (implicit log: Logger, executor : scala.concurrent.ExecutionContext) = this(
        textField.localToScene(0, 0).getX,
        textField.localToScene(0, textField.getLayoutBounds.getHeight).getY,
        textField.getLayoutBounds.getWidth,
        height,
        stackPane,
        textField.getText,
        loadingImage,
        query,
        itemSelectedEventHandler
    )

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
        open(textToComplete)
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
                RunInJfxThreadForcibly {
                    if (queryResult.isDefined) {
                        var scrollPanePrefHeight = 0.0
                        queryResult.get.foreach(scrollPanePrefHeight += _.getLayoutBounds.getHeight)
                        scrollPanePrefHeight += 25
                        if (scrollPanePrefHeight < height) {
                            resultsPane.setMinHeight(scrollPanePrefHeight)
                        } else {
                            resultsPane.setMinHeight(height)
                        }
                    }
                }
            }
        }
    }

    private def prepareDropDownPane(pane: Region): Unit = {
        pane.setBorder(JfxUtils.createBorder(Color.GRAY))
        pane.setBackground(JfxUtils.createBackground(Color.WHITE))
        pane.setLayoutX(posX)
        pane.setLayoutY(posY)
        pane.setMinWidth(width)
        pane.setMaxWidth(width)
        pane.setMaxHeight(height)
    }

    private def createLoadingPane() = {
        val loadingPane = new VBox(loadingImage)
        prepareDropDownPane(loadingPane)
        loadingPane.hnd(MouseEvent.ANY){e => e.consume()}
        loadingPane
    }

    private def createPaneWithResults(list: List[AutocompleteItem]) = {
        val paneWithResults = new ResultsPane
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
        }
        paneWithResults
    }

    private def open(textToComplete: String): Unit = {
        query.query(textToComplete, showResults)
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
                resultsPane.correctViewPort(item.getLayoutY, item.getLayoutY + item.getLayoutBounds.getHeight)
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
                resultsPane.correctViewPort(item.getLayoutY, item.getLayoutY + item.getLayoutBounds.getHeight)
            }
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

object AutocompleteList {
    private val imageView: ImageView = new ImageView(new Image("ajax-loader.gif"))
    private var lastCreatedInst: Option[AutocompleteList] = None

    private def apply(textField: TextField, height: Double, stackPane: StackPane, textToComplete: String,
              query: AutocompleteQuery,
              itemSelectedEventHandler: ()=> Unit)
             (implicit log: Logger, executor : scala.concurrent.ExecutionContext): AutocompleteList = {
        lastCreatedInst.foreach(_.close())
        lastCreatedInst = Some(new AutocompleteList(
            textField, height, stackPane, imageView, query, itemSelectedEventHandler
        ))
        lastCreatedInst.get
    }

    def addAutocomplete(textField: TextField, height: Double, stackPane: StackPane,
                        query: AutocompleteQuery, getText: AutocompleteItem => String)
                       (implicit log: Logger, executor : scala.concurrent.ExecutionContext): Unit = {
        var autoCmp: Option[AutocompleteList] = None
        def onItemSelected(): Unit = {
            if (autoCmp.isDefined) {
                textField.setText(getText(autoCmp.get.getSelected.get))
                RunInJfxThreadForcibly {
                    textField.requestFocus()
                    textField.positionCaret(textField.getText.length)
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
                autoCmp = Some(AutocompleteList(
                    textField, height, stackPane, textField.getText, query, () => onItemSelected()
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
}