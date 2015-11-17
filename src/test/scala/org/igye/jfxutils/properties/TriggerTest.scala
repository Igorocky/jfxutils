package org.igye.jfxutils.properties

import javafx.beans.property.SimpleBooleanProperty

import org.junit.{Assert, Test}

class TriggerTest {
    @Test
    def testUpFrontTrigger(): Unit = {
        val v1 = new SimpleBooleanProperty(false)
        val v2 = new SimpleBooleanProperty(false)
        var cnt = 0
        val trigger = new UpFrontTrigger(Expr(v1, v2){v1.get() && v2.get()})
        trigger.action = () => {cnt += 1}

        Assert.assertEquals(0, cnt)

        v1.set(true)
        Assert.assertEquals(0, cnt)
        v2.set(true)
        Assert.assertEquals(1, cnt)
        v1.set(false)
        Assert.assertEquals(1, cnt)
        v2.set(false)
        Assert.assertEquals(1, cnt)
    }

    @Test
    def testDownFrontTrigger(): Unit = {
        val v1 = new SimpleBooleanProperty(false)
        val v2 = new SimpleBooleanProperty(false)
        var cnt = 0
        val trigger = new DownFrontTrigger(Expr(v1, v2){v1.get() && v2.get()})
        trigger.action = () => {cnt += 1}

        Assert.assertEquals(0, cnt)

        v1.set(true)
        Assert.assertEquals(0, cnt)
        v2.set(true)
        Assert.assertEquals(0, cnt)
        v1.set(false)
        Assert.assertEquals(1, cnt)
        v2.set(false)
        Assert.assertEquals(1, cnt)
    }
}
