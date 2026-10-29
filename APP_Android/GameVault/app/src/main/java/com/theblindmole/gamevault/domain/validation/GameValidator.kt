package com.theblindmole.gamevault.domain.validation

import java.time.Year

sealed interface ValidationError {
    data object Required : ValidationError
    data class TooLong(val max: Int) : ValidationError
    data object InvalidNumber : ValidationError
    data class OutOfRange(val min: Int, val max: Int) : ValidationError
}

object GameValidator {
    const val NAME_MAX_LENGTH = 100
    const val FIELD_MAX_LENGTH = 50
    const val NOTES_MAX_LENGTH = 500
    const val YEAR_LENGTH = 4
    const val MIN_YEAR = 1950
    private const val FUTURE_YEARS = 2

    fun maxYear(currentYear: Int = Year.now().value): Int = currentYear + FUTURE_YEARS

    fun validateName(value: String): ValidationError? = validateText(value, NAME_MAX_LENGTH)

    fun validateField(value: String): ValidationError? = validateText(value, FIELD_MAX_LENGTH)

    fun validateYear(value: String, currentYear: Int = Year.now().value): ValidationError? {
        if (value.isBlank()) return ValidationError.Required
        val year = value.trim().toIntOrNull() ?: return ValidationError.InvalidNumber
        val max = maxYear(currentYear)
        return if (year in MIN_YEAR..max) null else ValidationError.OutOfRange(MIN_YEAR, max)
    }

    private fun validateText(value: String, maxLength: Int): ValidationError? = when {
        value.isBlank() -> ValidationError.Required
        value.trim().length > maxLength -> ValidationError.TooLong(maxLength)
        else -> null
    }
}
