package org.igye.jfxutils.autocomplete

import javafx.scene.control.{ScrollPane, TextField}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import javafx.scene.layout.{Pane, Region, StackPane, VBox}
import javafx.scene.paint.Color
import javafx.scene.text.Text

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.concurrency.{RunInJfxThread, RunInJfxThreadForcibly}
import org.igye.jfxutils.{JfxUtils, nodeToHasEvens}

import scala.concurrent.Future

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

    private val vbox = new VBox()
    vbox.setBackground(JfxUtils.createBackground(Color.WHITE))
    private val scrollPane = new ScrollPane(vbox)

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
                upperPane.getChildren.add(createPaneWithResults(list))
                RunInJfxThreadForcibly {
                    if (queryResult.isDefined) {
                        var scrollPanePrefHeight = 0.0
                        queryResult.get.foreach(scrollPanePrefHeight += _.node.getLayoutBounds.getHeight)
                        scrollPanePrefHeight += 25
                        if (scrollPanePrefHeight < height) {
                            scrollPane.setMinHeight(scrollPanePrefHeight)
                        } else {
                            scrollPane.setMinHeight(height)
                        }
                    }
                }
            }
        }
    }

    private def prepareDropDownPane(pane: Region): Unit = {
        pane.setBorder(JfxUtils.createBorder(Color.GRAY, 1))
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
        scrollPane.hnd(MouseEvent.ANY){e => e.consume()}
        prepareDropDownPane(scrollPane)
        if (list.isEmpty) {
            vbox.getChildren.add(new Text("Nothing found"))
        } else {
            queryResult = Option(list)
            list(0).select()
            for (i <- 0 until list.size) {
                val node = list(i).node
                vbox.getChildren.add(node)
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
        scrollPane
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

    private def correctViewPort(x1: Double, x2: Double): Unit = {
        val H = vbox.getLayoutBounds.getHeight
        val h = scrollPane.getViewportBounds.getHeight
        val x = scrollPane.getVvalue*(H - h)
        val xs = scrollPane.getVvalue*(H - h) + h
        if (x1 < x) {
            scrollPane.setVvalue(x1/(H - h))
        } else if (x2 > xs) {
            scrollPane.setVvalue((x2 - h)/(H - h))
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
                val node = item.node
                correctViewPort(node.getLayoutY, node.getLayoutY + node.getLayoutBounds.getHeight)
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
                val node = item.node
                correctViewPort(node.getLayoutY, node.getLayoutY + node.getLayoutBounds.getHeight)
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
    }
}