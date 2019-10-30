package com.rbiggin.currency.converter.feature.conversion.entity

import com.rbiggin.currency.converter.model.ConversionDto
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class ConversionMapperTest {

    private val dtoObject: ConversionDto = mockk(relaxed = true)

    @Test
    fun `when dto object mapped to entity expect native currency code to match`() {
        val nativeCurrencyCode = "JPN"
        every { dtoObject.nativeCode } returns nativeCurrencyCode

        assertEquals(nativeCurrencyCode,
            ConversionMapper.convertDtoToEntity(dtoObject).subjectCode)
    }

    @Test
    fun `when dto object mapped to entity expect foreign currency code to match`() {
        val foreignCurrencyCode = "EUR"
        every { dtoObject.foreignCode } returns foreignCurrencyCode

        assertEquals(foreignCurrencyCode,
            ConversionMapper.convertDtoToEntity(dtoObject).targetCode)
    }

    @Test
    fun `when dto object mapped to entity expect conversion rate to match`() {
        val conversionRate = 9.1
        every { dtoObject.conversionRate } returns conversionRate

        assertEquals(conversionRate,
            ConversionMapper.convertDtoToEntity(dtoObject).conversionRate, 0.0)
    }
}