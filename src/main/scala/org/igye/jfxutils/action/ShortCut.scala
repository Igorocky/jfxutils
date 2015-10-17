package org.igye.jfxutils.action

import javafx.scene.input.{KeyCode, KeyEvent}

case class Shortcut(keys: KeyCode*) {
    def matches(keyEvent: KeyEvent): Boolean = {
        if (keys.contains(KeyCode.CONTROL) && !keyEvent.isControlDown ||
            !keys.contains(KeyCode.CONTROL) && keyEvent.isControlDown ||
            keys.contains(KeyCode.ALT) && !keyEvent.isAltDown ||
            !keys.contains(KeyCode.ALT) && keyEvent.isAltDown ||
            keys.contains(KeyCode.SHIFT) && !keyEvent.isShiftDown ||
            !keys.contains(KeyCode.SHIFT) && keyEvent.isShiftDown
        ) {
            false
        } else {
            keys.nonEmpty &&
                keys.filter(keyCode => keyCode != KeyCode.CONTROL && keyCode != KeyCode.ALT && keyCode != KeyCode.SHIFT)
                    .forall(_ == keyEvent.getCode)
        }
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