package org.thelaughingdev.cronscheduler

import java.time.ZonedDateTime
import java.util.*

interface CronScheduler {

	fun next(): ZonedDateTime

	fun next(times: Int): List<ZonedDateTime>

	fun scheduleNext(): TimerTask

}