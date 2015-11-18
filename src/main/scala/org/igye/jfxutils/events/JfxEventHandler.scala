package org.igye.jfxutils.events

import javafx.event.{Event, EventHandler}

object JfxEventHandler {
    def apply(hnd: Event => Any) = new EventHandler[Event] {
        override def handle(event: Event): Unit = hnd(event)
    }
}
