package org.igye.jfxutils.events

import javafx.event.{Event, EventHandler, EventType}
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

    def remHnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): HasEvents = {
        node.removeEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
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

    def remFlt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): HasEvents = {
        node.removeEventFilter(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        this
    }

    def flt[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): HasEvents = {
        eventHandlerInfos.foreach(info => node.addEventHandler(info.eventType, info.handler))
        this
    }
}