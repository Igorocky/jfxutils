package org.igye.jfxutils.action

import javafx.scene.control.{Button, Tooltip}

import org.igye.jfxutils.JfxActionEventHandler

trait Action {
    val description: String
    protected[this] def onAction()

    private var boundObjects: Vector[ActionStateAware] = Vector[ActionStateAware]()

    private var shortcut: Option[Shortcut] = None
    private var enabled: Boolean = true

    def setShortcut(shortcut: Shortcut) = {
        this.shortcut = Some(shortcut)
        boundObjects.foreach(_.shortcutSet(shortcut))
    }

    def removeShortcut(): Unit = {
        this.shortcut = None
        boundObjects.foreach(_.shortcutRemoved())
    }

    def getShortcut = shortcut

    def enable(): Unit = {
        enabled = true
        boundObjects.foreach(_.actionWasEnabled())
    }

    def disable(): Unit = {
        enabled = false
        boundObjects.foreach(_.actionWasDisabled())
    }

    def isEnabled = enabled

    def setEnabled(enabled: Boolean): Unit = {
        if (enabled) {
            enable()
        } else {
            disable()
        }
    }

    def trigger(): Unit = {
        if (isEnabled) {
            onAction()
        }
    }

    def bind(actionStateAware: ActionStateAware): Unit = {
        boundObjects :+= actionStateAware
        actionStateAware.thisWasBoundToAction(this)
    }

    def unbind(actionStateAware: ActionStateAware): Unit = {
        boundObjects = boundObjects.filterNot(_ eq actionStateAware)
        actionStateAware.thisWasUnboundFromAction(this)
    }
}

object Action {
    def bind(action: Action, button: Button): Unit = {
        action.bind(new ActionStateAware {
            private val initialButtonText = button.getText
            private val initialButtonTooltip = button.getTooltip
            private val initialButtonState = button.isDisabled
            private val initialButtonOnActionEventHandler = button.getOnAction

            override def shortcutSet(newShortcut: Shortcut): Unit = {
                button.setText(initialButtonText + s" [${newShortcut.toString}]")
            }

            override def shortcutRemoved(): Unit = {
                button.setText(initialButtonText)
            }

            override def actionWasEnabled(): Unit = button.setDisable(false)

            override def actionWasDisabled(): Unit = button.setDisable(true)

            override def thisWasBoundToAction(action: Action): Unit = {
                button.setTooltip(new Tooltip(action.description))
                button.setDisable(!action.isEnabled)
                button.setOnAction(JfxActionEventHandler {e =>
                    action.trigger()
                })
                if (action.getShortcut.isDefined) {
                    shortcutSet(action.getShortcut.get)
                }
            }

            override def thisWasUnboundFromAction(action: Action): Unit = {
                button.setText(initialButtonText)
                button.setTooltip(initialButtonTooltip)
                button.setDisable(initialButtonState)
                button.setOnAction(initialButtonOnActionEventHandler)
            }
        })
    }
}