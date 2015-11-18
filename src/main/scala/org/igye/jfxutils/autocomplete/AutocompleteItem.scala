package org.igye.jfxutils.autocomplete

import javafx.scene.Node

trait AutocompleteItem {
    def node: Node
    def select()
    def unselect()
}
