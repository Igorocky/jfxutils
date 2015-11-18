package org.igye.jfxutils.events

import javafx.event.{ActionEvent, EventHandler}

object JfxActionEventHandler {
    def apply(hnd: ActionEvent => Any) = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = hnd(event)
    }
}
