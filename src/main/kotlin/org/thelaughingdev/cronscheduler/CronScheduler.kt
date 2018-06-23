package org.thelaughingdev.cronscheduler

import java.time.ZonedDateTime
import java.util.*

import org.thelaughingdev.cronscheduler.CronSection.*

interface CronScheduler {

	fun nextTime(schedule: CronSchedule, start: ZonedDateTime = ZonedDateTime.now()): ZonedDateTime

	fun nextTimes(schedule: CronSchedule, start: ZonedDateTime, times: Int): List<ZonedDateTime> {
		val timeList = mutableListOf(nextTime(schedule, start))
		0.until(times-1).forEach {
			timeList += nextTime(schedule, timeList[timeList.lastIndex].plusSeconds(1))
		}

		return timeList.toList()
	}

	fun scheduleNext(schedule: CronSchedule, start: ZonedDateTime = ZonedDateTime.now()): TimerTask
}

class BasicScheduler : CronScheduler {

	private companion object {
		val TIME_NOT_FOUND = -1

		fun ZonedDateTime.incrementSection(section: CronSection): ZonedDateTime = when(section) {
			SECOND -> this.plusSeconds(1)
			MINUTE -> this.plusMinutes(1)
			HOUR -> this.plusHours(1)
			DAY_OF_MONTH -> this.plusDays(1)
			MONTH -> this.plusMonths(1)
			DAY_OF_WEEK -> TODO("Implement this")
		}
	}

	private fun findNextValidTime(time: Int, scheduleSection: CronType) = scheduleSection.find {it >= time} ?: TIME_NOT_FOUND

	private fun rollTime(time: ZonedDateTime, schedulesList: List<CronType>, index: Int): ZonedDateTime {
		var newTime = time

		for(i in 0.until(index)) {
			val section = schedulesList[i].section
			newTime = newTime.with(section.timeField, section.range.start.toLong())
		}

		return newTime.incrementSection(schedulesList[index].section)
	}

	override fun nextTime(schedule: CronSchedule, start: ZonedDateTime): ZonedDateTime {
		var nextTime = start
		var found = false

		val schedulesList = listOf(schedule.second, schedule.minute, schedule.hour, schedule.dayOfMonth, schedule.month, schedule.dayOfWeek)

		while(!found) {
			for(i in schedulesList.indices) {
				val scheduleSection = schedulesList[i]
				val newTime = findNextValidTime(nextTime[scheduleSection.section.timeField], scheduleSection)

				if(newTime != TIME_NOT_FOUND)
					nextTime = nextTime.with(scheduleSection.section.timeField, newTime.toLong())
				else {
					nextTime = rollTime(nextTime, schedulesList, i)
					break
				}
			}

			found = true
		}

		return nextTime
	}

	override fun scheduleNext(schedule: CronSchedule, start: ZonedDateTime): TimerTask {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}