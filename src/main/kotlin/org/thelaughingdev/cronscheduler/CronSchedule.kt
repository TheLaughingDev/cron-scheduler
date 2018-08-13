package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime

interface CronScheduleHelper {
	val YEARLY get() = CronSchedule(Second(0), Minute(0), Hour(0), DayOfMonth(1), Month(1))
	val MONTHLY get() = CronSchedule(Second(0), Minute(0), Hour(0), DayOfMonth(1))
	val WEEKLY get() = CronSchedule(second = Second(0), minute = Minute(0), hour = Hour(0), dayOfWeek = DayOfWeek(0))
	val DAILY get() = CronSchedule(Second(0), Minute(0), Hour(0))
	val HOURLY get() = CronSchedule(Second(0), Minute(0))

	fun now(): CronSchedule {
		val now = LocalDateTime.now()
		return CronSchedule(Second(now.second), Minute(now.minute), Hour(now.hour),	DayOfMonth(now.dayOfMonth),	Month(now.monthValue))
	}
}

data class CronSchedule(
	val second: Second = Second(),
	val minute: Minute = Minute(),
	val hour: Hour = Hour(),
	val dayOfMonth: DayOfMonth = DayOfMonth(),
	val month: Month = Month(),
	val dayOfWeek: DayOfWeek = DayOfWeek()) {

	companion object Helper : CronScheduleHelper

	override fun toString() = "$second $minute $hour $dayOfMonth $month $dayOfWeek"
}