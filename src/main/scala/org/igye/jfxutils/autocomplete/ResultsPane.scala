package org.igye.jfxutils.autocomplete

import java.lang
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.concurrency.RunInJfxThread
import org.igye.jfxutils.properties.Expr
import org.igye.jfxutils.{JfxUtils, nodeToHasEvens, propertyToPropertyOperators}

import scala.collection.JavaConversions._

private class ResultsPane(width: Double, maxHeight: Double)(implicit log: Logger) extends ScrollPane {
    this.hnd(MouseEvent.ANY){e => e.consume()}//for the upper pane not to close the list
    setPrefWidth(width) //fix width
    setMinWidth(getPrefWidth)
    setMaxWidth(getPrefWidth)
    private val vbox = new VBox()
    setContent(vbox)
    vbox.setBackground(JfxUtils.createBackground(Color.WHITE))
    vbox.setMinWidth(width - 2)

    def correctViewport(y1: Double, y2: Double): Unit = {
        val H = vbox.getLayoutBounds.getHeight
        val h = getViewportBounds.getHeight
        val y = getVvalue*(H - h)
        val ys = y + h
        if (y1 < y) {
            setVvalue(y1/(H - h))
        } else if (y2 > ys) {
            setVvalue((y2 - h)/(H - h))
        }
    }

    def correctViewportM(y1: Double, y2: Double): Unit = {
        val ym = (y1 + y2)/2
        val H = vbox.getLayoutBounds.getHeight
        val h = getViewportBounds.getHeight
        var vv = (ym - h/2)/(H - h)
        if (vv < getVmin) {
            vv = getVmin
        }
        if (vv > getVmax) {
            vv = getVmax
        }
        setVvalue(vv)
    }

    def correctHeight(): Unit = {
        RunInJfxThread {
            prefHeightProperty() <== Expr(vbox.layoutBoundsProperty()) {
                val height = if (vbox.layoutBoundsProperty().get().getHeight <= maxHeight) {
                    vbox.layoutBoundsProperty().get().getHeight
                } else {
                    maxHeight
                }
                height + 2
            }
            minHeightProperty() <== prefHeightProperty()
            maxHeightProperty() <== prefHeightProperty()
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