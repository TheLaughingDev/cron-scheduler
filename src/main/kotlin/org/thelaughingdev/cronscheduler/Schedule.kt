package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime

/**
 * Helper for the cron schedule.
 */
interface ScheduleHelper {
	/** Returns a yearly schedule. */
	val YEARLY get() = Schedule(Second(0), Minute(0), Hour(0), DayOfMonth(1), Month(1))
	/** Returns a monthly schedule. */
	val MONTHLY get() = Schedule(Second(0), Minute(0), Hour(0), DayOfMonth(1))
	/** Returns a weekly schedule. */
	val WEEKLY get() = Schedule(second = Second(0), minute = Minute(0), hour = Hour(0), dayOfWeek = DayOfWeek(0))
	/** Returns a daily schedule. */
	val DAILY get() = Schedule(Second(0), Minute(0), Hour(0))
	/** Returns an hourly schedule. */
	val HOURLY get() = Schedule(Second(0), Minute(0))

	/**
	 * Returns a schedule set to run immediately.
	 */
	fun now(): Schedule {
		val now = LocalDateTime.now()
		return Schedule(
			Second(now.second),
			Minute(now.minute),
			Hour(now.hour),
			DayOfMonth(now.dayOfMonth),
			Month(now.monthValue)
		)
	}
}

/**
 * A class representation of a cron schedule.
 * @param second The seconds part.
 * @param minute The minutes part.
 * @param hour The hours part.
 * @param dayOfMonth The day of month part.
 * @param month The months part.
 * @param dayOfWeek The day of week part.
 */
data class Schedule(
	val second: Second = Second(),
	val minute: Minute = Minute(),
	val hour: Hour = Hour(),
	val dayOfMonth: DayOfMonth = DayOfMonth(),
	val month: Month = Month(),
	val dayOfWeek: DayOfWeek = DayOfWeek()
) {

	/**
	 * Helper object for the schedule.
	 */
	companion object Helper : ScheduleHelper

	/**
	 * Returns the schedule as it would appear in a cron string.
	 */
	override fun toString() = "$second $minute $hour $dayOfMonth $month $dayOfWeek"
}