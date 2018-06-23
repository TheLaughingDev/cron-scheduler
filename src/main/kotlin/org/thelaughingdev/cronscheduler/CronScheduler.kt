package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.util.*

import org.thelaughingdev.cronscheduler.CronSection.*

interface CronScheduler {

	fun nextTime(schedule: CronSchedule, start: LocalDateTime = LocalDateTime.now()): LocalDateTime

	fun nextTimes(schedule: CronSchedule, start: LocalDateTime, times: Int): List<LocalDateTime> {
		val timeList = mutableListOf(nextTime(schedule, start))
		0.until(times-1).forEach {
			timeList += nextTime(schedule, timeList[timeList.lastIndex].plusSeconds(1))
		}

		return timeList.toList()
	}

	fun scheduleNext(schedule: CronSchedule, start: LocalDateTime = LocalDateTime.now()): TimerTask
}

class BasicScheduler : CronScheduler {

	private companion object {
		val TIME_NOT_FOUND = -1

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

	private fun findNextValidTime(time: Int, scheduleSection: CronType) = scheduleSection.find {it >= time} ?: TIME_NOT_FOUND

	private tailrec fun resetTime(time: LocalDateTime, schedulesList: List<CronType>): LocalDateTime = if(schedulesList.isEmpty())
		time
	else
		resetTime(time.with(schedulesList.first().section, schedulesList.first().first()), schedulesList.takeLast(schedulesList.size - 1))

	override fun nextTime(schedule: CronSchedule, start: LocalDateTime): LocalDateTime {
		var nextTime = start.withNano(0)
		var found = false

		val schedulesList = listOf(schedule.second, schedule.minute, schedule.hour, schedule.dayOfMonth, schedule.month, schedule.dayOfWeek)

		while(!found) {
			for(i in schedulesList.indices) {
				val scheduleSection = schedulesList[i]
				val originalTime = nextTime[scheduleSection.section]
				val newTime = findNextValidTime(originalTime, scheduleSection)

				if(newTime != TIME_NOT_FOUND) {
					nextTime = nextTime.with(scheduleSection.section, newTime)

					if(newTime > originalTime)
						nextTime = resetTime(nextTime, schedulesList.take(i))
				}
				else {
					nextTime = resetTime(nextTime, schedulesList.take(i + 1))
					nextTime = nextTime.incrementSection(schedulesList[i + 1].section)
					break
				}
			}

			found = true
		}

		return nextTime
	}

	override fun scheduleNext(schedule: CronSchedule, start: LocalDateTime): TimerTask {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}