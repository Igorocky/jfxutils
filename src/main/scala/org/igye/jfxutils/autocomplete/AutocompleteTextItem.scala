package org.igye.jfxutils.autocomplete

import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Text

import org.igye.jfxutils.JfxUtils

class AutocompleteTextItem(val text: String) extends AutocompleteItem {
    override val node = new HBox(new Text(text))
    private val initialBackground = node.getBackground
    private val initialTextFill = node.getChildren.get(0).asInstanceOf[Text].getFill

    override def select(): Unit = {
        node.setBackground(JfxUtils.createBackground(Color.rgb(0, 82, 164)))
        node.getChildren.get(0).asInstanceOf[Text].setFill(Color.WHITE)
    }

    override def unselect(): Unit = {
        node.setBackground(initialBackground)
        node.getChildren.get(0).asInstanceOf[Text].setFill(initialTextFill)
    }
}
