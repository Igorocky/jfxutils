package org.igye.jfxutils.properties

import javafx.beans.property.SimpleIntegerProperty

import org.apache.logging.log4j.{LogManager, Logger}
import org.igye.jfxutils.{WritableObservableValueToBidirectionalBindingTarget, WritableValueToBindingTarget}
import org.junit.{Assert, Test}

class BindingOperatorsTest {
    implicit val log: Logger = LogManager.getLogger()

    @Test
    def testUnidirectionalBinding(): Unit = {
        val target = new SimpleIntegerProperty(0)
        val source = new SimpleIntegerProperty(1)

        target <== source

        Assert.assertEquals(1, target.get())
        Assert.assertEquals(1, source.get())

        source.setValue(2)
        Assert.assertEquals(2, target.get())
        Assert.assertEquals(2, source.get())
    }

    @Test
    def testBidirectionalBinding(): Unit = {
        val left = new SimpleIntegerProperty(0)
        val right = new SimpleIntegerProperty(1)

        left <==> right

        Assert.assertEquals(1, left.get())
        Assert.assertEquals(1, right.get())

        right.setValue(2)
        Assert.assertEquals(2, left.get())
        Assert.assertEquals(2, right.get())

        left.setValue(3)
        Assert.assertEquals(3, right.get())
        Assert.assertEquals(3, left.get())
    }
}
