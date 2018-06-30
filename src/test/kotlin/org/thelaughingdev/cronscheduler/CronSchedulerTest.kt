package org.thelaughingdev.cronscheduler

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.thelaughingdev.cronscheduler.CronSection.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CronSchedulerTest {

	private val cronScheduler = BasicScheduler()

	@Test
	fun test() {
		println(cronScheduler.nextTime(CronSchedule(dayOfMonth = SingleCron(DAY_OF_MONTH, 1), dayOfWeek = SingleCron(DAY_OF_WEEK, 5))))
	}

}