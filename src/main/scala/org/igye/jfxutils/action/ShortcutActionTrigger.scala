package org.igye.jfxutils.action

import javafx.scene.input.KeyEvent

class ShortcutActionTrigger(actionsList: List[Action]) {
    def triggerAction(keyEvent: KeyEvent): Unit = {
        actionsList.find(act =>
            act.isEnabled && act.getShortcut.isDefined && act.getShortcut.get.matches(keyEvent)
        ).foreach(_.trigger())
    }

    def hasActionMatchingEvent(keyEvent: KeyEvent): Boolean = {
        actionsList.find(act =>
            act.getShortcut.isDefined && act.getShortcut.get.matches(keyEvent)
        ).isDefined
    }
}
