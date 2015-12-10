package org.igye.jfxutils.events

import javafx.event.{Event, EventHandler, EventType}
import javafx.scene.Node
import javafx.scene.input.KeyEvent

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.Implicits.nodeToNodeOps
import org.igye.jfxutils.action.ActionType.{FILTER, HANDLER}
import org.igye.jfxutils.action.{Action, ShortcutActionTrigger}
import org.igye.jfxutils.concurrency.RunInJfxThreadForcibly

class NodeOps(node: Node) {
    def hnd[T <: Event](eventType: EventType[T])(hnd: T => Unit): EventHandlerInfo[T] = {
        val eh = new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        }
        node.addEventHandler(eventType, eh)
        EventHandlerInfo(eventType, eh)
    }

    def hnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): EventHandlerInfo[T] = {
        node.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        eventHandlerInfo
    }

    def remHnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): Unit = {
        node.removeEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
    }

    def hnd[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): Seq[EventHandlerInfo[T]] = {
        eventHandlerInfos.foreach(info => node.addEventHandler(info.eventType, info.handler))
        eventHandlerInfos
    }


    def flt[T <: Event](eventType: EventType[T])(hnd: T => Unit): EventHandlerInfo[T] = {
        val eh = new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        }
        node.addEventFilter(eventType, eh)
        EventHandlerInfo(eventType, eh)
    }

    def flt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): EventHandlerInfo[T] = {
        node.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        eventHandlerInfo
    }

    def remFlt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): Unit = {
        node.removeEventFilter(eventHandlerInfo.eventType, eventHandlerInfo.handler)
    }

    def flt[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): Seq[EventHandlerInfo[T]] = {
        eventHandlerInfos.foreach(info => node.addEventFilter(info.eventType, info.handler))
        eventHandlerInfos
    }

    def remAll[T <: Event](eventHandlerInfosList: List[EventHandlerInfo[T]]): Unit = {
        eventHandlerInfosList.foreach{ hnd =>
            node.remHnd(hnd)
            node.remFlt(hnd)
        }
    }

    def rem[T <: Event](eventHandlerInfos: EventHandlerInfo[T]*): Unit = {
        node.remAll(eventHandlerInfos.toList)
    }

    def bind(actions: Action*): List[EventHandlerInfo[KeyEvent]] = {
        bind(actions.toList)
    }

    def bind(actionsList: List[Action]): List[EventHandlerInfo[KeyEvent]] = {
        val filtersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == FILTER))
        val handlersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == HANDLER))
        node.flt(KeyEvent.ANY) { e => filtersShortcutActionTrigger.triggerAction(e) } ::
            node.hnd(KeyEvent.ANY) { e => handlersShortcutActionTrigger.triggerAction(e) } ::
            Nil
    }

    def focus()(implicit log: Logger): Unit = {
        RunInJfxThreadForcibly {
            node.requestFocus()
        }
    }
}