package com.theblindmole.gamevault.domain

import com.theblindmole.gamevault.domain.validation.GameValidator
import com.theblindmole.gamevault.domain.validation.ValidationError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameValidatorTest {

    private val currentYear = 2026

    @Test
    fun nameBlankIsRequired() {
        assertEquals(ValidationError.Required, GameValidator.validateName("   "))
    }

    @Test
    fun nameTooLongIsRejected() {
        val name = "a".repeat(GameValidator.NAME_MAX_LENGTH + 1)
        assertEquals(ValidationError.TooLong(GameValidator.NAME_MAX_LENGTH), GameValidator.validateName(name))
    }

    @Test
    fun validNameIsAccepted() {
        assertNull(GameValidator.validateName("Hollow Knight"))
    }

    @Test
    fun yearOutsideRangeIsRejected() {
        val max = GameValidator.maxYear(currentYear)
        assertEquals(
            ValidationError.OutOfRange(GameValidator.MIN_YEAR, max),
            GameValidator.validateYear("1900", currentYear),
        )
        assertEquals(
            ValidationError.OutOfRange(GameValidator.MIN_YEAR, max),
            GameValidator.validateYear((max + 1).toString(), currentYear),
        )
    }

    @Test
    fun yearNotNumericIsRejected() {
        assertEquals(ValidationError.InvalidNumber, GameValidator.validateYear("20a4", currentYear))
    }

    @Test
    fun validYearIsAccepted() {
        assertNull(GameValidator.validateYear("2017", currentYear))
    }
}
