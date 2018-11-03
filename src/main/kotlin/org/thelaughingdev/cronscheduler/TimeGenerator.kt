package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime
import java.time.temporal.ChronoField.*

/**
 * Determines the next time a cron event fires given a schedule.
 */
interface TimeGenerator {

	/**
	 * Given a schedule a start time determines the next time the cron schedule will fire. This could be on
	 * or after the start time.
	 * @param schedule The cron schedule.
	 * @param start The start time.
	 * @return The next time cron event will fire.
	 */
	fun nextTime(schedule: Schedule, start: LocalDateTime = LocalDateTime.now()): LocalDateTime

	/**
	 * Generates a lazy sequence of cron times given a schedule and start time. Each increment will generate the next schedule.
	 * @param schedule The cron schedule.
	 * @param start The start time.
	 * @return Lazy sequence of cron times.
	 */
	fun nextTimes(schedule: Schedule, start: LocalDateTime = LocalDateTime.now()) = generateSequence(nextTime(schedule, start)) { t ->
		nextTime(schedule, t.plusSeconds(1))
	}

}

/**
 * Basic scheduler implementation.
 */
class BasicTimeGenerator : TimeGenerator {

	/**
	 * Companion object to hold helper functions.
	 */
	private companion object {
		/** When their is no time found. */
		const val TIME_NOT_FOUND = -1

		/**
		 * Helper function to increment a LocalDateTime object for a given section.
		 */
		fun LocalDateTime.incrementSection(section: Section): LocalDateTime = when(section) {
			is Second -> this.plusSeconds(1)
			is Minute -> this.plusMinutes(1)
			is Hour -> this.plusHours(1)
			is DayOfMonth, is DayOfWeek -> this.plusDays(1)
			is Month -> this.plusMonths(1)
		}

		/**
		 * Helper function to set the ammount of a LocalDateTime object for a given section.
		 */
		fun LocalDateTime.with(section: Section, amount: Int): LocalDateTime = when(section) {
			is Second -> this.with(SECOND_OF_MINUTE, amount.toLong())
			is Minute -> this.with(MINUTE_OF_HOUR, amount.toLong())
			is Hour -> this.with(HOUR_OF_DAY, amount.toLong())
			is DayOfMonth -> this.with(DAY_OF_MONTH, amount.toLong())
			is Month -> this.with(MONTH_OF_YEAR, amount.toLong())
			is DayOfWeek -> this.with(DAY_OF_WEEK, amount.toLong())
		}

		/**
		 * Helper operator to get the value of a LocalDateTime section given a CronSection.
		 */
		operator fun LocalDateTime.get(section: Section): Int = when(section) {
			is Second -> this[SECOND_OF_MINUTE]
			is Minute -> this[MINUTE_OF_HOUR]
			is Hour -> this[HOUR_OF_DAY]
			is DayOfMonth -> this[DAY_OF_MONTH]
			is Month -> this[MONTH_OF_YEAR]
			is DayOfWeek -> this[DAY_OF_WEEK]
		}
	}

	/**
	 * Converts the day of week values to a list of days in the current month.
	 * @param startTime The date time.
	 * @param dayOfWeek The day of week cron.
	 * @return The days of the month that correspond to the days of the week.
	 */
	private fun convertDayOfWeekToDayOfMonth(startTime: LocalDateTime, dayOfWeek: DayOfWeek): List<Int> {
		var time = startTime.withDayOfMonth(1)
		val currentMonth = startTime[MONTH_OF_YEAR]
		val daysOfMonth = mutableListOf<Int>()

		while(currentMonth == time[MONTH_OF_YEAR]) {
			val weekDay = if(time[DAY_OF_WEEK] == 7) 0 else time[DAY_OF_WEEK]

			if(weekDay in dayOfWeek.possibleValues())
				daysOfMonth += time[DAY_OF_MONTH]

			time = time.plusDays(1)
		}

		return daysOfMonth
	}

	/**
	 * Finds the next time that is greater than time and still in the set of possibleValues.
	 * @param time The time to start from.
	 * @param possibleValues The set of all possible values.
	 * @return The time or if one isn't found, TIME_NOT_FOUND.
	 */
	private fun findNextValidTime(time: Int, possibleValues: List<Int>) = possibleValues.find { it >= time }
		?: TIME_NOT_FOUND

	/**
	 * Finds the next valid date given the time, day of month and day of week crons. This is separate from findNextValidTime
	 * because day of month and day of week work are or'd together instead of and'd together like the other sections.
	 * @param time The time.
	 * @param dayOfMonth The day of month schedule.
	 * @param dayOfWeek The day of week schedule.
	 * @return The next valid value for the day, or TIME_NOT_FOUND.
	 */
	private fun findNextValidTimeDay(time: LocalDateTime, dayOfMonth: DayOfMonth, dayOfWeek: DayOfWeek): Int {
		val day = time[DAY_OF_MONTH]

		if(dayOfWeek.cron is CronAll)
			return findNextValidTime(day, dayOfMonth.possibleValues())
		else if(dayOfMonth.cron is CronAll)
			return findNextValidTime(day, convertDayOfWeekToDayOfMonth(time, dayOfWeek))
		else
			return findNextValidTime(day, (dayOfMonth.possibleValues() + convertDayOfWeekToDayOfMonth(time, dayOfWeek)).distinct().sorted())
	}

	/**
	 * Resets each time section to the first possible value. Called when no valid time can be found and the next time must
	 * be switched to.
	 * @param time The time.
	 * @param schedulesList The list of schedules.
	 * @return Time with values reset.
	 */
	private tailrec fun resetTime(time: LocalDateTime, schedulesList: List<Section>): LocalDateTime = if(schedulesList.isEmpty())
		time
	else
		resetTime(time.with(schedulesList.first(), schedulesList.first().possibleValues().first()), schedulesList.takeLast(schedulesList.size - 1))

	override fun nextTime(schedule: Schedule, start: LocalDateTime): LocalDateTime {
		var nextTime = start.withNano(0)
		var found = false
		val dayOfWeekSchedule = schedule.dayOfWeek

		val schedulesList = listOf(schedule.second, schedule.minute, schedule.hour, schedule.dayOfMonth, schedule.month)

		while(!found) {
			found = true
			for(i in schedulesList.indices) {
				val scheduleSection = schedulesList[i]
				val originalSectionValue = nextTime[scheduleSection]
				val newSectionValue = if(scheduleSection is DayOfMonth)
					findNextValidTimeDay(nextTime, scheduleSection, dayOfWeekSchedule)
				else
					findNextValidTime(originalSectionValue, scheduleSection.possibleValues())

				if(newSectionValue != TIME_NOT_FOUND) {
					nextTime = nextTime.with(scheduleSection, newSectionValue)

					if(newSectionValue > originalSectionValue)
						nextTime = resetTime(nextTime, schedulesList.take(i))
				} else {
					nextTime = resetTime(nextTime, schedulesList.take(i + 1))
					nextTime = if(scheduleSection is Month) nextTime.plusYears(1) else nextTime.incrementSection(schedulesList[i + 1])
					found = false
					break
				}
			}
		}

		return nextTime
	}
}