package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime
import java.time.temporal.ChronoField.*

interface CronScheduler {

	fun nextTime(schedule: CronSchedule, start: LocalDateTime = LocalDateTime.now()): LocalDateTime

	fun nextTimes(schedule: CronSchedule, times: Int, start: LocalDateTime = LocalDateTime.now()): List<LocalDateTime> {
		val timeList = mutableListOf(nextTime(schedule, start))
		0.until(times-1).forEach {
			timeList += nextTime(schedule, timeList[timeList.lastIndex].plusSeconds(1))
		}

		return timeList.toList()
	}
}

class BasicScheduler : CronScheduler {

	private companion object {
		const val TIME_NOT_FOUND = -1

		fun LocalDateTime.incrementSection(section: Section): LocalDateTime = when(section) {
			is Second -> this.plusSeconds(1)
			is Minute -> this.plusMinutes(1)
			is Hour -> this.plusHours(1)
			is DayOfMonth, is DayOfWeek -> this.plusDays(1)
			is Month -> this.plusMonths(1)
		}

		fun LocalDateTime.with(section: Section, amount: Int): LocalDateTime = when(section) {
			is Second -> this.with(SECOND_OF_MINUTE, amount.toLong())
			is Minute -> this.with(MINUTE_OF_HOUR, amount.toLong())
			is Hour -> this.with(HOUR_OF_DAY, amount.toLong())
			is DayOfMonth -> this.with(DAY_OF_MONTH, amount.toLong())
			is Month -> this.with(MONTH_OF_YEAR, amount.toLong())
			is DayOfWeek -> this.with(DAY_OF_WEEK, amount.toLong())
		}

		operator fun LocalDateTime.get(section: Section): Int = when(section) {
			is Second -> this[SECOND_OF_MINUTE]
			is Minute -> this[MINUTE_OF_HOUR]
			is Hour -> this[HOUR_OF_DAY]
			is DayOfMonth -> this[DAY_OF_MONTH]
			is Month -> this[MONTH_OF_YEAR]
			is DayOfWeek -> this[DAY_OF_WEEK]
		}
	}

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

	private fun findNextValidTime(time: Int, possibleValues: List<Int>) = possibleValues.find {it >= time} ?: TIME_NOT_FOUND

	private fun findNextValidTimeDay(time: LocalDateTime, dayOfMonth: DayOfMonth, dayOfWeek: DayOfWeek): Int {
		val day = time[DAY_OF_MONTH]

		if(dayOfWeek.cron is AllCron)
			return findNextValidTime(day, dayOfMonth.possibleValues())
		else if(dayOfMonth.cron is AllCron)
			return findNextValidTime(day, convertDayOfWeekToDayOfMonth(time, dayOfWeek))
		else
			return findNextValidTime(day, (dayOfMonth.possibleValues() + convertDayOfWeekToDayOfMonth(time, dayOfWeek)).distinct().sorted())
	}

	private tailrec fun resetTime(time: LocalDateTime, schedulesList: List<Section>): LocalDateTime = if(schedulesList.isEmpty())
		time
	else
		resetTime(time.with(schedulesList.first(), schedulesList.first().possibleValues().first()), schedulesList.takeLast(schedulesList.size - 1))

	override fun nextTime(schedule: CronSchedule, start: LocalDateTime): LocalDateTime {
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
				}
				else {
					nextTime = resetTime(nextTime, schedulesList.take(i + 1))
					nextTime = if(scheduleSection is Month)	nextTime.plusYears(1)	else nextTime.incrementSection(schedulesList[i + 1])
					found = false
					break
				}
			}
		}

		return nextTime
	}
}