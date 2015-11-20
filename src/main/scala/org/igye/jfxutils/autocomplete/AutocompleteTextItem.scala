package org.igye.jfxutils.autocomplete

import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.{Font, Text}

import org.igye.jfxutils.JfxUtils

class AutocompleteTextItem(val text: String, font: Font, val userData: Option[Any] = None) extends HBox({val t = new Text(text); t.setFont(font); t}) with AutocompleteItem {
    private val initialBackground = getBackground
    private val initialTextFill = getChildren.get(0).asInstanceOf[Text].getFill

    override def select(): Unit = {
        setBackground(JfxUtils.createBackground(Color.rgb(0, 82, 164)))
        getChildren.get(0).asInstanceOf[Text].setFill(Color.WHITE)
    }

    override def unselect(): Unit = {
        setBackground(initialBackground)
        getChildren.get(0).asInstanceOf[Text].setFill(initialTextFill)
    }

    override def hasFocus: Boolean = isFocused || getChildren.get(0).isFocused
}
