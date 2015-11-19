package org.igye.jfxutils.autocomplete

import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox

import org.igye.jfxutils.{propertyToPropertyOperators, nodeToHasEvens}

import scala.collection.JavaConversions._

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