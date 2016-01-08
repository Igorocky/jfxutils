package org.igye.jfxutils.action

import javafx.beans.property.{SimpleBooleanProperty, BooleanProperty}
import javafx.scene.control.{Button, TextField, Tooltip}
import javafx.scene.input.{KeyCode, KeyEvent}

import org.igye.commonutils.Enum
import org.igye.jfxutils.Implicits.{observableValueToObservableValueOperators, nodeToNodeOps}
import org.igye.jfxutils.events.Hnd
import org.igye.jfxutils.properties.ChgListener

case class ActionType(name: String)
object ActionType extends Enum[ActionType] {
    val FILTER = addElem(ActionType("FILTER"))
    val HANDLER = addElem(ActionType("HANDLER"))
}

trait Action {
    val description: String
    protected def onAction()

    private var boundObjects: Vector[ActionStateAware] = Vector[ActionStateAware]()

    var actionType = ActionType.FILTER
    private var shortcut: Option[Shortcut] = None
    val enabled: SimpleBooleanProperty = new SimpleBooleanProperty(true)

    enabled ==> ChgListener{chg =>
        if (chg.newValue) {
            boundObjects.foreach(_.actionWasEnabled())
        } else {
            boundObjects.foreach(_.actionWasDisabled())
        }
    }

    def setShortcut(shortcut: Shortcut) = {
        this.shortcut = Some(shortcut)
        boundObjects.foreach(_.shortcutSet(shortcut))
    }

    def removeShortcut(): Unit = {
        this.shortcut = None
        boundObjects.foreach(_.shortcutRemoved())
    }

    def getShortcut = shortcut

    def isEnabled = enabled.get()

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
    //todo: make it implicit method on Action
    def bind(action: Action, button: Button): Unit = {
        action.bind(new ActionStateAware {
            private val initialButtonText = button.getText
            private val initialButtonTooltip = button.getTooltip
            private val initialButtonState = button.isDisabled
            private val initialButtonOnActionEventHandler = button.getOnAction

            override def shortcutSet(newShortcut: Shortcut): Unit = {
                button.setText(initialButtonText + s" (${newShortcut.toString})")
            }

            override def shortcutRemoved(): Unit = {
                button.setText(initialButtonText)
            }

            override def actionWasEnabled(): Unit = button.setDisable(false)

            override def actionWasDisabled(): Unit = button.setDisable(true)

            override def thisWasBoundToAction(action: Action): Unit = {
                button.setTooltip(new Tooltip(action.description))
                button.setDisable(!action.isEnabled)
                button.setOnAction(Hnd { e =>
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

    def bind(action: Action, textField: TextField): Unit = {
        action.bind(new ActionStateAware {
            private val hnd = Hnd(KeyEvent.KEY_PRESSED){e=>
                if (e.getCode == KeyCode.ENTER) {
                    action.trigger()
                }
            }

            override def shortcutSet(newShortcut: Shortcut): Unit = {}

            override def shortcutRemoved(): Unit = {}

            override def actionWasEnabled(): Unit = {}

            override def actionWasDisabled(): Unit = {}

            override def thisWasBoundToAction(action: Action): Unit = {
                textField.hnd(hnd)
            }

            override def thisWasUnboundFromAction(action: Action): Unit = {
                textField.remHnd(hnd)
            }
        })
    }
}