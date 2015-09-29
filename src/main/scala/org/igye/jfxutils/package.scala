package org.igye

import javafx.scene.Node

package object jfxutils {
    implicit def nodeToHasEvens(node: Node) = new HasEvents(node)
}
