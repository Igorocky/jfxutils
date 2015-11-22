package org.igye.jfxutils.dialog

import org.junit.{Assert, Test}

class TextFieldVarNameAutocompleteTest {
    @Test
    def extractPartsAndFilterTest(): Unit = {
        var res = TextFieldVarNameAutocomplete.extractPartsAndFilter("text1${varname}text2", 8)
        Assert.assertEquals("text1${", res.left)
        Assert.assertEquals("v", res.filter)
        Assert.assertEquals("}text2", res.right)

        res = TextFieldVarNameAutocomplete.extractPartsAndFilter("text1${v}text2", 8)
        Assert.assertEquals("text1${", res.left)
        Assert.assertEquals("v", res.filter)
        Assert.assertEquals("}text2", res.right)

        res = TextFieldVarNameAutocomplete.extractPartsAndFilter("text1${}text2", 7)
        Assert.assertEquals("text1${", res.left)
        Assert.assertEquals("", res.filter)
        Assert.assertEquals("}text2", res.right)

        res = TextFieldVarNameAutocomplete.extractPartsAndFilter("text1$}text2", 7)
        Assert.assertEquals("text1$}", res.left)
        Assert.assertEquals("", res.filter)
        Assert.assertEquals("text2", res.right)

        res = TextFieldVarNameAutocomplete.extractPartsAndFilter("text1$}text2", 6)
        Assert.assertEquals("text1$", res.left)
        Assert.assertEquals("", res.filter)
        Assert.assertEquals("}text2", res.right)

        res = TextFieldVarNameAutocomplete.extractPartsAndFilter("text1$}text2", 5)
        Assert.assertEquals("text1", res.left)
        Assert.assertEquals("", res.filter)
        Assert.assertEquals("$}text2", res.right)

        res = TextFieldVarNameAutocomplete.extractPartsAndFilter("abcd", 1)
        Assert.assertEquals("a", res.left)
        Assert.assertEquals("", res.filter)
        Assert.assertEquals("bcd", res.right)
    }
}
