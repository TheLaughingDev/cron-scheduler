package org.thelaughingdev.cronscheduler

import io.mockk.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.objectMockk
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

import org.thelaughingdev.cronscheduler.CronSection.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CronParserTest {

	private lateinit var cronScheduleHelper: CronScheduleHelper

	private lateinit var cronParser: BasicParser

	@BeforeEach
	fun before() {
		cronScheduleHelper = mockk<CronScheduleHelper>()
		cronParser = BasicParser(cronScheduleHelper)
	}

	@Nested
	inner class `Given special variable` {

		@Test
		fun `with @yearly`() {
			val defaultValue = CronSchedule()
			every { cronScheduleHelper.YEARLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@yearly")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @annually`() {
			val defaultValue = CronSchedule()
			every { cronScheduleHelper.YEARLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@annually")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @monthly`() {
			val defaultValue = CronSchedule()
			every { cronScheduleHelper.MONTHLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@monthly")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @daily`() {
			val defaultValue = CronSchedule()
			every { cronScheduleHelper.DAILY } returns defaultValue
			val schedule = cronParser.parseSchedule("@daily")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @hourly`() {
			val defaultValue = CronSchedule()
			every { cronScheduleHelper.HOURLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@hourly")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @now`() {
			val defaultValue = CronSchedule()
			every { cronScheduleHelper.now() } returns defaultValue
			val schedule = cronParser.parseSchedule("@now")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @reboot`() {
			val defaultValue = CronSchedule()
			every { cronScheduleHelper.now() } returns defaultValue
			val schedule = cronParser.parseSchedule("@reboot")
			assertThat(schedule).isEqualTo(defaultValue)
		}
	}

	@Nested
	inner class `Given seconds` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(SingleCron(SECOND, 1))
			assertThat(cronParser.parseSchedule("1 * * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)))
			assertThat(cronParser.parseSchedule("1-10 * * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(StepCron(AllCron(SECOND), 5))
			assertThat(cronParser.parseSchedule("*/5 * * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(StepCron(SingleCron(SECOND, 2), 5))
			assertThat(cronParser.parseSchedule("2/5 * * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(StepCron(RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)), 5))
			assertThat(cronParser.parseSchedule("1-10/5 * * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(ListCron(SECOND, listOf<ContinuousRangeCron>(SingleCron(SECOND, 1), RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)),
				StepCron(AllCron(SECOND), 5),	StepCron(SingleCron(SECOND, 2), 5), StepCron(RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)), 5))))
			assertThat(cronParser.parseSchedule("1,1-10,*/5,2/5,1-10/5 * * * * * *")).isEqualTo(defaultSchedule)
		}


	}

}