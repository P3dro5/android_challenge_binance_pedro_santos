package com.example.binancews

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitTest {
    @Test
    fun percentageFormatting_isCorrect() {
        val change = 1.2345
        val pct = 0.5123
        assertEquals("+1.2345", String.format("%+.4f", change))
        assertEquals("+0.51%", String.format("%+.2f%%", pct))
    }
}
