package org.igye.jfxutils.autocomplete

import org.igye.commonutils.Enum

case class ListDirection(name: String)
object ListDirection extends Enum[ListDirection] {
    val UP = addElem(ListDirection("UP"))
    val DOWN = addElem(ListDirection("DOWN"))
}