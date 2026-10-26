package com.example.core.common

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

interface ClockProvider {
    fun currentLocalDate(): LocalDate
    fun currentTimeMillis(): Long
    fun currentZoneId(): ZoneId
    fun currentHourOfDay(): Int
}

object SystemClockProvider : ClockProvider {
    override fun currentLocalDate(): LocalDate = LocalDate.now()
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
    override fun currentZoneId(): ZoneId = ZoneId.systemDefault()
    override fun currentHourOfDay(): Int = LocalTime.now().hour
}
