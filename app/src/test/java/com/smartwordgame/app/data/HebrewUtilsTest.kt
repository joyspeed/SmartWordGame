package com.smartwordgame.app.data

import org.junit.Assert.*
import org.junit.Test

class HebrewUtilsTest {

    @Test
    fun `stripNiqqud removes vowel marks`() {
        assertEquals("כלב", "כֶּלֶב".stripNiqqud())
    }

    @Test
    fun `stripNiqqud leaves plain Hebrew unchanged`() {
        assertEquals("שלום", "שלום".stripNiqqud())
    }

    @Test
    fun `stripNiqqud handles empty string`() {
        assertEquals("", "".stripNiqqud())
    }

    @Test
    fun `stripNiqqud handles mixed content`() {
        val input = "אֲחִיזָה - holding"
        val result = input.stripNiqqud()
        assertEquals("אחיזה - holding", result)
    }

    @Test
    fun `stripNiqqud handles only niqqud characters`() {
        // A string of just niqqud marks should become empty
        val niqqudOnly = "\u05B0\u05B1\u05B2"
        assertEquals("", niqqudOnly.stripNiqqud())
    }

    @Test
    fun `stripped words sort alphabetically ignoring niqqud`() {
        val words = listOf("גָּדוֹל", "אֲחִיזָה", "בַּיִת")
        val sorted = words.sortedBy { it.stripNiqqud() }
        assertEquals(listOf("אֲחִיזָה", "בַּיִת", "גָּדוֹל"), sorted)
    }
}
