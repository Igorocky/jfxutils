package org.igye.jfxutils.properties

import javafx.beans.binding.ObjectBinding

import org.igye.jfxutils.Implicits.observableValueToObservableValueOperators

trait Trigger {
    var action: () => Unit = _
}

class UpFrontTrigger(private val condition: ObjectBinding[Boolean]) extends Trigger {
    condition ==> ChgListener{chg=>
        if (chg.newValue) {
            action()
        }
    }
}

class DownFrontTrigger(private val condition: ObjectBinding[Boolean]) extends Trigger {
    condition ==> ChgListener{chg=>
        if (!chg.newValue) {
            action()
        }
    }
}