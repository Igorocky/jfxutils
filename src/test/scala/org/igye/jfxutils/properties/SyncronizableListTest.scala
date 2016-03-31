package org.igye.jfxutils.properties

import javafx.collections.FXCollections

import org.junit.{Assert, Test}

class SyncronizableListTest {
  @Test
  def test1(): Unit = {
    val target = new SyncronizableList[Int]
    val source = FXCollections.observableArrayList[Int]()

    source.addAll(1,2)

    Assert.assertTrue(target.isEmpty)
    target.addAll(source)
    Assert.assertEquals(2, target.size())
    Assert.assertEquals(1, target.get(0))
    Assert.assertEquals(2, target.get(1))

    target <== (source, (i: Int) => i + 10)

    source.add(5)
    Assert.assertEquals(3, target.size())
    Assert.assertEquals(1, target.get(0))
    Assert.assertEquals(2, target.get(1))
    Assert.assertEquals(15, target.get(2))

    source.remove(0)
    Assert.assertEquals(2, target.size())
    Assert.assertEquals(2, target.get(0))
    Assert.assertEquals(15, target.get(1))

    target.unbind()
    source.remove(0)
    Assert.assertEquals(2, target.size())
    Assert.assertEquals(2, target.get(0))
    Assert.assertEquals(15, target.get(1))
  }
}
