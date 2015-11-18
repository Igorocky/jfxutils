package org.igye.jfxutils.properties

import javafx.collections.ObservableList

import org.igye.jfxutils.exceptions.JfxUtilsException

import scala.collection.JavaConversions._

class ListOperators[T](targetList: java.util.List[T]) {
    def <== [S] (sourceList: ObservableList[S],
                 targetElemConstructor: S => T, targetElemDestructor: T => Unit = null): Unit = {
        sourceList.addListener(ListChgListener[S]{chg =>
            def handleAdded(): Unit = {
                targetList.addAll(
                    chg.getFrom,
                    chg.getAddedSubList.map(targetElemConstructor)
                )
            }

            def handleRemoved(): Unit = {
                for (cnt <- 0 until chg.getRemovedSize) {
                    val removedElem = targetList.remove(chg.getFrom)
                    if (targetElemDestructor != null) {
                        targetElemDestructor(removedElem)
                    }
                }
            }

            while (chg.next()) {
                if (chg.wasPermutated) {
                    throw new JfxUtilsException("chg.wasPermutated is not supported yet.")
                } else if (chg.wasUpdated) {
                    throw new JfxUtilsException("chg.wasUpdated is not supported yet.")
                } else if (chg.wasReplaced) {
                    handleRemoved()
                    handleAdded()
                } else {
                    if (chg.wasRemoved) {
                        handleRemoved()
                    } else if (chg.wasAdded) {
                        handleAdded()
                    }
                }
            }
        })
    }
}
