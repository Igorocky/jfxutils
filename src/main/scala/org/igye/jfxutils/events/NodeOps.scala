package org.igye.jfxutils.events

import javafx.event.{Event, EventHandler, EventType}
import javafx.scene.Node

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.concurrency.RunInJfxThreadForcibly

class NodeOps(node: Node) {
    def hnd[T <: Event](eventType: EventType[T])(hnd: T => Unit): NodeOps = {
        node.addEventHandler(eventType, new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        })
        this
    }

    def hnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): NodeOps = {
        node.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        this
    }

    def remHnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): NodeOps = {
        node.removeEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        this
    }

    def hnd[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): NodeOps = {
        eventHandlerInfos.foreach(info => node.addEventHandler(info.eventType, info.handler))
        this
    }

    def flt[T <: Event](eventType: EventType[T])(hnd: T => Unit): NodeOps = {
        node.addEventFilter(eventType, new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        })
        this
    }

    def flt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): NodeOps = {
        node.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        this
    }

    def remFlt[T <: Event](eventHandlerInfo: EventHandlerInfo[T]): NodeOps = {
        node.removeEventFilter(eventHandlerInfo.eventType, eventHandlerInfo.handler)
        this
    }

    def flt[T <: Event](eventHandlerInfos: EventHandlerInfo[T] *): NodeOps = {
        eventHandlerInfos.foreach(info => node.addEventHandler(info.eventType, info.handler))
        this
    }

    def focus()(implicit log: Logger): Unit = {
        RunInJfxThreadForcibly {
            node.requestFocus()
        }
    }
}