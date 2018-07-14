package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime

import org.thelaughingdev.cronscheduler.CronSection.*

interface CronScheduleHelper {
	val YEARLY get() = CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0), SingleCron(HOUR, 0), SingleCron(DAY_OF_MONTH, 1), SingleCron(MONTH, 1))
	val MONTHLY get() = CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0), SingleCron(HOUR, 0), SingleCron(DAY_OF_MONTH, 1))
	val WEEKLY get() = CronSchedule(second = SingleCron(SECOND, 0), minute = SingleCron(MINUTE, 0), hour = SingleCron(HOUR, 0), dayOfWeek = SingleCron(DAY_OF_WEEK, 0))
	val DAILY get() = CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0), SingleCron(HOUR, 0))
	val HOURLY get() = CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0))

	fun now(): CronSchedule {
		val now = LocalDateTime.now()
		return CronSchedule(SingleCron(SECOND, now.second), SingleCron(MINUTE, now.minute), SingleCron(HOUR, now.hour),
			SingleCron(DAY_OF_MONTH, now.dayOfMonth),	SingleCron(MONTH, now.monthValue))
	}
}

data class CronSchedule(
	val second: CronType = AllCron(SECOND),
	val minute: CronType = AllCron(MINUTE),
	val hour: CronType = AllCron(HOUR),
	val dayOfMonth: CronType = AllCron(DAY_OF_MONTH),
	val month: CronType = AllCron(MONTH),
	val dayOfWeek: CronType = AllCron(DAY_OF_WEEK)) {

	init {
		if(second.section != SECOND)
			throw CronValidationException("Invalid value ${second.section} in the second place.")
		if(minute.section != MINUTE)
			throw CronValidationException("Invalid value ${minute.section} in the minute place.")
		if(hour.section != HOUR)
			throw CronValidationException("Invalid value ${hour.section} in the hour place.")
		if(dayOfMonth.section != DAY_OF_MONTH)
			throw CronValidationException("Invalid value ${dayOfMonth.section} in the dayOfMonth place.")
		if(month.section != MONTH)
			throw CronValidationException("Invalid value ${month.section} in the month place.")
		if(dayOfWeek.section != DAY_OF_WEEK)
			throw CronValidationException("Invalid value ${dayOfWeek.section} in the dayOfWeek place.")
	}

	companion object Helper : CronScheduleHelper
}