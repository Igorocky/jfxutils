package org.igye.jfxutils.properties

import javafx.collections.ObservableList

class SyncronizableList[E] extends SyncronizableListJ[E] {
  def <==[S](sourceList: ObservableList[S], targetElemConstructor: S => E, targetElemDestructor: E => Unit = null): Unit = {
    unbind()
    bind(sourceList, ListOperators.createListener(getDelegate, targetElemConstructor, targetElemDestructor))
  }
}