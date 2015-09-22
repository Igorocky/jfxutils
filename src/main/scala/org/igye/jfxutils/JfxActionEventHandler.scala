package org.igye.jfxutils

import javafx.event.{EventHandler, ActionEvent}

object JfxActionEventHandler {
    def apply(hnd: ActionEvent => Any) = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = hnd(event)
    }
}
