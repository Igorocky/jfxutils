package org.igye.jfxutils.autocomplete

import javafx.scene.Node

trait AutocompleteItem extends Node {
    def select(): Unit
    def unselect(): Unit
    def hasFocus: Boolean
}
