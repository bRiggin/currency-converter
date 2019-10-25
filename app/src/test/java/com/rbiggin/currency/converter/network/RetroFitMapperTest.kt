package com.rbiggin.currency.converter.network

import com.rbiggin.currency.converter.model.CurrencyDto
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RetroFitMapperTest {

    private val defaultBase = "EUR"
    private val defaultDate = "22/10/2019"
    private val defaultRates = mapOf(
        "AUD" to 1.6196,
        "BGN" to 1.9597,
        "BRL" to 4.8014,
        "CAD" to 1.5369,
        "CHF" to 1.1298,
        "CNY" to 7.961,
        "CZK" to 25.767
    )

    private val retroFitDto: RetrofitCurrencyDto = mockk {
        every { base } returns defaultBase
        every { date } returns defaultDate
        every { rates } returns defaultRates
    }

    @Test
    fun `when mapper request to map to dto and no rates in response expect set containing only base currency`() {
        every { retroFitDto.rates } returns mapOf()
        assertEquals(1, RetroFitMapper.retrofitDtoToCurrencyDto(retroFitDto).size)
    }

    @Test
    fun `when mapper request to map to dto expect all dtos foreign code to equal base`() {
        RetroFitMapper.retrofitDtoToCurrencyDto(retroFitDto).forEach {
            assertEquals(defaultBase, it.foreignCode)
        }
    }

    @Test
    fun `when mapper request to map to dto expect all dtos dates to equal to passed date`() {
        RetroFitMapper.retrofitDtoToCurrencyDto(retroFitDto).forEach {
            assertEquals(defaultDate, it.date)
        }
    }

    @Test
    fun `when mapper request to map to dto expect set size to match number of rates plus base currency`() {
        assertEquals(defaultRates.size + 1, RetroFitMapper.retrofitDtoToCurrencyDto(retroFitDto).size)
    }

    @Test
    fun `when mapper request to map to dto expect each rate key to have been transformed into dto`() {
        val dtoSet = RetroFitMapper.retrofitDtoToCurrencyDto(retroFitDto)

        retroFitDto.rates.forEach { (key, value) ->
            assertTrue(dtoSet.contains(CurrencyDto(defaultDate, key, defaultBase, value)))
        }
    }

    @Test
    fun `when mapper requested to map dto expect dto base object included with 1 conversion rate`() {
        val dtoSet = RetroFitMapper.retrofitDtoToCurrencyDto(retroFitDto)

        assertTrue(dtoSet.contains(CurrencyDto(defaultDate, defaultBase, defaultBase, 1.0)))
    }
}