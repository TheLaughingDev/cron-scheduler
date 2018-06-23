package org.thelaughingdev.cronscheduler

import java.time.LocalDateTime

interface CronScheduleHelper {
	val YEARLY get() = CronSchedule(SingleCron(CronSection.SECOND, 0), SingleCron(CronSection.MINUTE, 0), SingleCron(CronSection.HOUR, 0), SingleCron(CronSection.DAY_OF_MONTH, 1), SingleCron(CronSection.MONTH, 1))
	val MONTHLY get() = CronSchedule(SingleCron(CronSection.SECOND, 0), SingleCron(CronSection.MINUTE, 0), SingleCron(CronSection.HOUR, 0), SingleCron(CronSection.DAY_OF_MONTH, 1))
	val WEEKLY get() = CronSchedule(second = SingleCron(CronSection.SECOND, 0), minute = SingleCron(CronSection.MINUTE, 0), hour = SingleCron(CronSection.HOUR, 0), dayOfWeek = SingleCron(CronSection.DAY_OF_WEEK, 0))
	val DAILY get() = CronSchedule(SingleCron(CronSection.SECOND, 0), SingleCron(CronSection.MINUTE, 0), SingleCron(CronSection.HOUR, 0))
	val HOURLY get() = CronSchedule(SingleCron(CronSection.SECOND, 0), SingleCron(CronSection.MINUTE, 0))

	fun now(): CronSchedule {
		val now = LocalDateTime.now()
		return CronSchedule(SingleCron(CronSection.SECOND, now.second), SingleCron(CronSection.MINUTE, now.minute), SingleCron(CronSection.HOUR, now.hour),
			SingleCron(CronSection.DAY_OF_MONTH, now.dayOfMonth),	SingleCron(CronSection.MONTH, now.monthValue))
	}
}

data class CronSchedule(
	val second: CronType = AllCron(CronSection.SECOND),
	val minute: CronType = AllCron(CronSection.MINUTE),
	val hour: CronType = AllCron(CronSection.HOUR),
	val dayOfMonth: CronType = AllCron(CronSection.DAY_OF_MONTH),
	val month: CronType = AllCron(CronSection.MONTH),
	val dayOfWeek: CronType = AllCron(CronSection.DAY_OF_WEEK)) {

	companion object Helper : CronScheduleHelper
}