package com.example.receiptwise

import com.example.receiptwise.scan.ReceiptInfo
import com.example.receiptwise.scan.parseReceipt
import org.junit.Assert.assertEquals
import org.junit.Test

// These tests check the reading rules with made up receipt text
class ReceiptParserTest {

    @Test
    fun findsShopDateAndTotal() {
        val text = "TESCO\n12/11/2026\nMilk 1.20\nBread 1.50\nSubtotal 13.00\nTOTAL 14.50"
        val result = parseReceipt(text)

        assertEquals("TESCO", result.shop)
        assertEquals("12/11/2026", result.date)
        assertEquals("14.50", result.total)
    }

    @Test
    fun usesBiggestAmountWhenThereIsNoTotalWord() {
        val result = parseReceipt("Cafe Nero\nLatte 3.40\nCake 2.95")
        assertEquals("3.40", result.total)
    }

    @Test
    fun emptyTextGivesEmptyFields() {
        assertEquals(ReceiptInfo("", "", ""), parseReceipt(""))
    }
}