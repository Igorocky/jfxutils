package org.igye.jfxutils

import javafx.event.{EventHandler, EventType, Event}
import javafx.scene.Node

class HasEvents(node: Node) {
    def hnd[T <: Event](eventType: EventType[T])(hnd: T => Unit) {
        node.addEventHandler(eventType, new EventHandler[T] {
            override def handle(event: T): Unit = {
                hnd(event)
            }
        })
    }

    def hnd[T <: Event](eventHandlerInfo: EventHandlerInfo[T]) {
        node.addEventHandler(eventHandlerInfo.eventType, eventHandlerInfo.handler)
    }
}