package org.igye.jfxutils.properties

import java.util
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty, SimpleStringProperty}
import javafx.collections.FXCollections

import org.igye.jfxutils.{listToListOperators, ChgListener, observableValueToObservableValueOperators, propertyToPropertyOperators}
import org.junit.{Assert, Test}

class BindingOperatorsTest {
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

    @Test
    def testListBinding(): Unit = {
        val source = FXCollections.observableArrayList[Int]()
        val target = new util.ArrayList[String]()
        val constructor = (i: Int) => i.toString + "_"
        var destructedElems = List[String]()
        val destructor = (s: String) => destructedElems ::= s

        target <== (sourceList = source, targetElemConstructor = constructor, targetElemDestructor = destructor)

        Assert.assertEquals(0, source.size())
        Assert.assertEquals(0, target.size())

        source.add(1)
        Assert.assertEquals(1, source.size())
        Assert.assertEquals(1, target.size())
        Assert.assertEquals("1_", target.get(0))
        Assert.assertEquals(1, source.get(0))

        source.addAll(2,3)
        source.remove(0)
        Assert.assertEquals(2, source.size())
        Assert.assertEquals(2, target.size())
        Assert.assertEquals("2_", target.get(0))
        Assert.assertEquals(2, source.get(0))
        Assert.assertEquals("3_", target.get(1))
        Assert.assertEquals(3, source.get(1))
        Assert.assertEquals(1, destructedElems.size)
        Assert.assertTrue(destructedElems.contains("1_"))

        source.clear()
        Assert.assertEquals(0, source.size())
        Assert.assertEquals(0, target.size())
        Assert.assertEquals(3, destructedElems.size)
        Assert.assertTrue(destructedElems.contains("1_"))
        Assert.assertTrue(destructedElems.contains("2_"))
        Assert.assertTrue(destructedElems.contains("3_"))
    }

    @Test
    def testAnyExpressionBinding(): Unit = {
        val intProp = new SimpleIntegerProperty(1)
        val boolProp = new SimpleBooleanProperty(false)
        val strProp = new SimpleStringProperty("A")
        val strProp2 = new SimpleStringProperty("Z")

        val targetStrProp = new SimpleStringProperty()

        targetStrProp <== Expr(intProp, boolProp, strProp){
            s"${intProp.get}:${boolProp.get}:${strProp.get}:${strProp2.get}"
        }

        Assert.assertEquals("1:false:A:Z", targetStrProp.get())

        intProp.set(2)
        Assert.assertEquals("2:false:A:Z", targetStrProp.get())

        boolProp.set(true)
        Assert.assertEquals("2:true:A:Z", targetStrProp.get())

        strProp.set("B")
        Assert.assertEquals("2:true:B:Z", targetStrProp.get())

        strProp2.set("X")
        Assert.assertEquals("2:true:B:Z", targetStrProp.get())
    }

    @Test
    def testObservableValueAddChangeListenerOperator(): Unit = {
        val value = new SimpleIntegerProperty(0)
        var mirror = 5
        value ==> ChgListener {chg =>
            mirror = chg.newValue.asInstanceOf[Int]
        }
        val unusedListener = ChgListener[Number] {chg =>
            Unit
        }

        Assert.assertEquals(5, mirror)

        value.set(6)
        Assert.assertEquals(6, mirror)

        value.set(0)
        Assert.assertEquals(0, mirror)

        value.removeListener(unusedListener)
    }
}
