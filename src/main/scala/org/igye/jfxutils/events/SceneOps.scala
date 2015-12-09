package org.igye.jfxutils.events

import javafx.event.{Event, EventHandler, EventType}
import javafx.scene.Scene
import javafx.scene.input.KeyEvent

import org.igye.jfxutils.Implicits.sceneToSceneOps
import org.igye.jfxutils.action.ActionType.{FILTER, HANDLER}
import org.igye.jfxutils.action.{Action, ShortcutActionTrigger}

class SceneOps(scene: Scene) {
    def hnd[T <: Event](eventType: EventType[T])(hnd: T => Unit): EventHandlerInfo[T] = {
        val eh = new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        }
        scene.addEventHandler(eventType, eh)
        EventHandlerInfo(eventType, eh)
    }

    def hnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): EventHandlerInfo[T] = {
        scene.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        eventHandlerInfo
    }

    def remHnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): Unit = {
        scene.removeEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
    }

    def hnd[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): Seq[EventHandlerInfo[T]] = {
        eventHandlerInfos.foreach(info => scene.addEventHandler(info.eventType, info.handler))
        eventHandlerInfos
    }


    def flt[T <: Event](eventType: EventType[T])(hnd: T => Unit): EventHandlerInfo[T] = {
        val eh = new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        }
        scene.addEventFilter(eventType, eh)
        EventHandlerInfo(eventType, eh)
    }

    def flt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): EventHandlerInfo[T] = {
        scene.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        eventHandlerInfo
    }

    def remFlt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): Unit = {
        scene.removeEventFilter(eventHandlerInfo.eventType, eventHandlerInfo.handler)
    }

    def flt[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): Seq[EventHandlerInfo[T]] = {
        eventHandlerInfos.foreach(info => scene.addEventFilter(info.eventType, info.handler))
        eventHandlerInfos
    }

    def remAll[T <: Event](eventHandlerInfosList: List[EventHandlerInfo[T]]): Unit = {
        eventHandlerInfosList.foreach{hnd =>
            scene.remHnd(hnd)
            scene.remFlt(hnd)
        }
    }

    def rem[T <: Event](eventHandlerInfos: EventHandlerInfo[T]*): Unit = {
        scene.remAll(eventHandlerInfos.toList)
    }

    def bind(actions: Action*): List[EventHandlerInfo[KeyEvent]] = {
        bind(actions.toList)
    }

    def bind(actionsList: List[Action]): List[EventHandlerInfo[KeyEvent]] = {
        val filtersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == FILTER))
        val handlersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == HANDLER))
        scene.flt(KeyEvent.ANY) { e => filtersShortcutActionTrigger.triggerActionIfNecessary(e) } ::
            scene.hnd(KeyEvent.ANY) { e => handlersShortcutActionTrigger.triggerActionIfNecessary(e) } ::
            Nil
    }
}