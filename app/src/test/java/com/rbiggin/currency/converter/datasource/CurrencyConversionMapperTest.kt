package com.rbiggin.currency.converter.datasource

import com.rbiggin.currency.converter.datasource.CurrencyConversionMapper
import com.rbiggin.currency.converter.model.CurrencyDto
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyConversionMapperTest {

    private val dtoObject: CurrencyDto = mockk(relaxed = true)

    @Test
    fun `when dto object mapped to entity expect native currency code to match`() {
        val nativeCurrencyCode = "JPN"
        every { dtoObject.nativeCode } returns nativeCurrencyCode

        assertEquals(nativeCurrencyCode,
            CurrencyConversionMapper.convertDtoToEntity(dtoObject).nativeCode)
    }

    @Test
    fun `when dto object mapped to entity expect foreign currency code to match`() {
        val foreignCurrencyCode = "EUR"
        every { dtoObject.foreignCode } returns foreignCurrencyCode

        assertEquals(foreignCurrencyCode,
            CurrencyConversionMapper.convertDtoToEntity(dtoObject).foreignCode)
    }

    @Test
    fun `when dto object mapped to entity expect conversion rate to match`() {
        val conversionRate = 9.1
        every { dtoObject.conversionRate } returns conversionRate

        assertEquals(conversionRate,
            CurrencyConversionMapper.convertDtoToEntity(dtoObject).conversionRate, 0.0)
    }
}