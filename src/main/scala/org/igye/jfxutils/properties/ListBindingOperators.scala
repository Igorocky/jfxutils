package org.igye.jfxutils.properties

import javafx.collections.ObservableList

import org.igye.jfxutils.ListChgListener
import org.igye.jfxutils.exceptions.JfxUtilsException

import scala.collection.JavaConversions._

class ListBindingOperators[T](targetList: java.util.List[T]) {
    def <== [S] (sourceList: ObservableList[S], mapper: S => T): Unit = {
        sourceList.addListener(ListChgListener[S]{chg =>
            def handleRemoved(): Unit = {
                for (cnt <- 0 until chg.getRemovedSize) {
                    targetList.remove(chg.getFrom)
                }
            }

            def handleAdded(): Unit = {
                targetList.addAll(chg.getFrom, chg.getAddedSubList.map(mapper))
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
