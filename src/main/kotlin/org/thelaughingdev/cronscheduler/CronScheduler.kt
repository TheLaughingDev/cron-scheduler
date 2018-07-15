package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime
import java.time.temporal.ChronoField

import org.thelaughingdev.cronscheduler.CronSection.*

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

		fun LocalDateTime.incrementSection(section: CronSection): LocalDateTime = when(section) {
			SECOND -> this.plusSeconds(1)
			MINUTE -> this.plusMinutes(1)
			HOUR -> this.plusHours(1)
			DAY_OF_MONTH, DAY_OF_WEEK -> this.plusDays(1)
			MONTH -> this.plusMonths(1)
		}

		fun LocalDateTime.with(section: CronSection, amount: Int): LocalDateTime = when(section) {
			SECOND -> this.with(ChronoField.SECOND_OF_MINUTE, amount.toLong())
			MINUTE -> this.with(ChronoField.MINUTE_OF_HOUR, amount.toLong())
			HOUR -> this.with(ChronoField.HOUR_OF_DAY, amount.toLong())
			DAY_OF_MONTH -> this.with(ChronoField.DAY_OF_MONTH, amount.toLong())
			MONTH -> this.with(ChronoField.MONTH_OF_YEAR, amount.toLong())
			DAY_OF_WEEK -> this.with(ChronoField.DAY_OF_WEEK, amount.toLong())
		}

		operator fun LocalDateTime.get(section: CronSection): Int = when(section) {
			SECOND -> this[ChronoField.SECOND_OF_MINUTE]
			MINUTE -> this[ChronoField.MINUTE_OF_HOUR]
			HOUR -> this[ChronoField.HOUR_OF_DAY]
			DAY_OF_MONTH -> this[ChronoField.DAY_OF_MONTH]
			MONTH -> this[ChronoField.MONTH_OF_YEAR]
			DAY_OF_WEEK -> this[ChronoField.DAY_OF_WEEK]
		}
	}

	private fun convertDayOfWeekToDayOfMonth(startTime: LocalDateTime, dayOfWeek: CronType): List<Int> {
		var time = startTime.withDayOfMonth(1)
		val currentMonth = startTime[MONTH]
		val daysOfMonth = mutableListOf<Int>()

		while(currentMonth == time[MONTH]) {
			val weekDay = if(time[DAY_OF_WEEK] == 7) 0 else time[DAY_OF_WEEK]

			if(weekDay in dayOfWeek)
				daysOfMonth += time[DAY_OF_MONTH]

			time = time.plusDays(1)
		}

		return daysOfMonth
	}

	private fun findNextValidTime(time: Int, scheduleSection: Iterable<Int>) = scheduleSection.find {it >= time} ?: TIME_NOT_FOUND

	private fun findNextValidTimeDay(time: LocalDateTime, dayOfMonth: CronType, dayOfWeek: CronType): Int {
		val day = time[DAY_OF_MONTH]

		if(dayOfWeek is AllCron)
			return findNextValidTime(day, dayOfMonth)
		else if(dayOfMonth is AllCron)
			return findNextValidTime(day, convertDayOfWeekToDayOfMonth(time, dayOfWeek))
		else
			return findNextValidTime(day, (dayOfMonth + convertDayOfWeekToDayOfMonth(time, dayOfWeek)).distinct().sorted())
	}

	private tailrec fun resetTime(time: LocalDateTime, schedulesList: List<CronType>): LocalDateTime = if(schedulesList.isEmpty())
		time
	else
		resetTime(time.with(schedulesList.first().section, schedulesList.first().first()), schedulesList.takeLast(schedulesList.size - 1))

	override fun nextTime(schedule: CronSchedule, start: LocalDateTime): LocalDateTime {
		var nextTime = start.withNano(0)
		var found = false
		val dayOfWeekSchedule = schedule.dayOfWeek

		val schedulesList = listOf(schedule.second, schedule.minute, schedule.hour, schedule.dayOfMonth, schedule.month)

		while(!found) {
			found = true
			for(i in schedulesList.indices) {
				val scheduleSection = schedulesList[i]
				val originalSectionValue = nextTime[scheduleSection.section]
				val newSectionValue = if(scheduleSection.section == DAY_OF_MONTH)
					findNextValidTimeDay(nextTime, scheduleSection, dayOfWeekSchedule)
				else
					findNextValidTime(originalSectionValue, scheduleSection)

				if(newSectionValue != TIME_NOT_FOUND) {
					nextTime = nextTime.with(scheduleSection.section, newSectionValue)

					if(newSectionValue > originalSectionValue)
						nextTime = resetTime(nextTime, schedulesList.take(i))
				}
				else {
					nextTime = resetTime(nextTime, schedulesList.take(i + 1))
					nextTime = if(scheduleSection.section == MONTH)	nextTime.plusYears(1)	else nextTime.incrementSection(schedulesList[i + 1].section)
					found = false
					break
				}
			}
		}

		return nextTime
	}
}