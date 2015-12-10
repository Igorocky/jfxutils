package org.igye.jfxutils.action

import javafx.scene.input.KeyEvent

class ShortcutActionTrigger(actionsList: List[Action]) {
    def triggerAction(keyEvent: KeyEvent): Unit = {
        if (keyEvent.getEventType == KeyEvent.KEY_PRESSED) {
            actionsList.find(act =>
                act.isEnabled && act.getShortcut.isDefined && act.getShortcut.get.matches(keyEvent)
            ).foreach{a =>
                a.trigger()
                keyEvent.consume()
            }
        }
    }
}
