package org.igye.jfxutils

import javafx.event.{EventHandler, Event}

object JfxEventHandler {
    def apply(hnd: Event => Any) = new EventHandler[Event] {
        override def handle(event: Event): Unit = hnd(event)
    }
}
