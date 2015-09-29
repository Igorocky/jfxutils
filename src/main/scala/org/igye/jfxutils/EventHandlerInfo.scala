package org.igye.jfxutils

import javafx.event.{EventHandler, EventType, Event}

case class EventHandlerInfo[T <: Event](eventType: EventType[T], handler: EventHandler[T])
