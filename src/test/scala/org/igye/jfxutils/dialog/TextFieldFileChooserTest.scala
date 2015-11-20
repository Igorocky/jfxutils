package org.igye.jfxutils.dialog

import org.junit.{Assert, Test}

class TextFieldFileChooserTest {
    @Test
    def extractPathAndFilterTest(): Unit = {
        var res = TextFieldFileChooser.extractPathAndFilter("C:/dir1/fil")
        Assert.assertEquals("C:/dir1/", res.path)
        Assert.assertEquals("fil", res.filter)

        res = TextFieldFileChooser.extractPathAndFilter("C:/dir1/")
        Assert.assertEquals("C:/dir1/", res.path)
        Assert.assertEquals("", res.filter)

        res = TextFieldFileChooser.extractPathAndFilter("C:")
        Assert.assertEquals("", res.path)
        Assert.assertEquals("C:", res.filter)

        res = TextFieldFileChooser.extractPathAndFilter("")
        Assert.assertEquals("", res.path)
        Assert.assertEquals("", res.filter)
    }
}
