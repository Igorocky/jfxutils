package org.igye.jfxutils

import javafx.event.{EventHandler, EventType, Event}
import javafx.scene.Node

class HasEvents(node: Node) {
    def hnd[T <: Event](eventType: EventType[T])(hnd: T => Unit): HasEvents = {
        node.addEventHandler(eventType, new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        })
        this
    }

    def hnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): HasEvents = {
        node.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        this
    }

    def hnd[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): HasEvents = {
        eventHandlerInfos.foreach(info => node.addEventHandler(info.eventType, info.handler))
        this
    }

    def flt[T <: Event](eventType: EventType[T])(hnd: T => Unit): HasEvents = {
        node.addEventFilter(eventType, new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        })
        this
    }

    def flt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): HasEvents = {
        node.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        this
    }

    def flt[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): HasEvents = {
        eventHandlerInfos.foreach(info => node.addEventHandler(info.eventType, info.handler))
        this
    }
}