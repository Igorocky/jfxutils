package org.igye.jfxutils.events

import javafx.event.{ActionEvent, Event, EventHandler, EventType}

case class EventHandlerInfo[T <: Event](eventType: EventType[T], handler: EventHandler[T])

object Hnd {
    def apply[T <: Event](eventType: EventType[T])(hnd: T => Unit): EventHandlerInfo[T] = {
        EventHandlerInfo(
            eventType,
            new EventHandler[T] {
                override def handle(event: T): Unit = {
                    hnd(event)
                }
            }
        )
    }
}