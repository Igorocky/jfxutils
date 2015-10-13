package org.igye.jfxutils.action

import javafx.scene.input.{KeyCode, KeyEvent}

import org.igye.jfxutils.action.Shortcut.keyMatchers

case class Shortcut(keys: List[KeyCode]) {
    def matches(keyEvent: KeyEvent): Boolean = {
        keys.nonEmpty && keys.forall(keyCode =>
            if (keyMatchers.contains(keyCode)) keyMatchers(keyCode)(keyEvent) else keyCode == keyEvent.getCode
        )
    }

    override def toString: String = {
        keys.map({
            case KeyCode.CONTROL => "Ctrl"
            case KeyCode.ALT => "Alt"
            case KeyCode.SHIFT => "Shift"
            case other@_ => other
        }).mkString("+")
    }
}

object Shortcut {
    private val keyMatchers = Map[KeyCode, KeyEvent => Boolean](
        KeyCode.CONTROL -> (e => e.isControlDown)
        ,KeyCode.ALT -> (e => e.isAltDown)
        ,KeyCode.SHIFT -> (e => e.isShiftDown)
    )
}