package com.example.util

import android.icu.text.DateFormat
import android.icu.util.ULocale
import com.example.model.DigitsMode
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object FaFormat {
    private val FA_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toDigits(input: String, mode: DigitsMode): String {
        if (mode != DigitsMode.FA) return input
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(FA_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun fmt(number: Int, mode: DigitsMode): String = fmt(number.toLong(), mode)

    fun fmt(number: Long, mode: DigitsMode): String {
        val neg = number < 0
        val raw = abs(number).toString()
        val sep = if (mode == DigitsMode.FA) '٬' else ','
        val grouped = StringBuilder(raw.length + raw.length / 3)
        var count = 0
        for (i in raw.length - 1 downTo 0) {
            if (count > 0 && count % 3 == 0) {
                grouped.append(sep)
            }
            grouped.append(raw[i])
            count++
        }
        grouped.reverse()
        val withSign = if (neg) "−$grouped" else grouped.toString()
        return toDigits(withSign, mode)
    }

    fun fmtTile(value: Int, mode: DigitsMode): String = toDigits(value.toString(), mode)

    fun fmtTime(seconds: Int, mode: DigitsMode): String {
        val safe = seconds.coerceAtLeast(0)
        val h = safe / 3600
        val m = (safe % 3600) / 60
        val s = safe % 60
        val raw = if (h > 0) {
            String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        } else {
            String.format(Locale.US, "%02d:%02d", m, s)
        }
        return toDigits(raw, mode)
    }

    fun formatDate(timestampMillis: Long, mode: DigitsMode): String {
        return try {
            val locale = if (mode == DigitsMode.FA) {
                ULocale("fa_IR@calendar=persian")
            } else {
                ULocale("en_GB")
            }
            val df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale)
            toDigits(df.format(Date(timestampMillis)), mode)
        } catch (_: Exception) {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = timestampMillis }
            val raw = String.format(
                Locale.US,
                "%04d/%02d/%02d %02d:%02d",
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH),
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE)
            )
            toDigits(raw, mode)
        }
    }
}
