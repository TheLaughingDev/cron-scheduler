package org.thelaughingdev.cronscheduler

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
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
			assertThat(cronParser.parseSchedule("1 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)))
			assertThat(cronParser.parseSchedule("1-10 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(StepCron(AllCron(SECOND), 5))
			assertThat(cronParser.parseSchedule("*/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(StepCron(SingleCron(SECOND, 2), 5))
			assertThat(cronParser.parseSchedule("2/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(StepCron(RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)), 5))
			assertThat(cronParser.parseSchedule("1-10/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(ListCron(SECOND, listOf<ContinuousRangeCron>(SingleCron(SECOND, 1), RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)),
				StepCron(AllCron(SECOND), 5),	StepCron(SingleCron(SECOND, 2), 5), StepCron(RangeCron(SingleCron(SECOND, 1), SingleCron(SECOND, 10)), 5))))
			assertThat(cronParser.parseSchedule("1,1-10,*/5,2/5,1-10/5 * * * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given minutes` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(minute = SingleCron(MINUTE, 1))
			assertThat(cronParser.parseSchedule("* 1 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(minute = RangeCron(SingleCron(MINUTE, 1), SingleCron(MINUTE, 10)))
			assertThat(cronParser.parseSchedule("* 1-10 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(minute = StepCron(AllCron(MINUTE), 5))
			assertThat(cronParser.parseSchedule("* */5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(minute = StepCron(SingleCron(MINUTE, 2), 5))
			assertThat(cronParser.parseSchedule("* 2/5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(minute = StepCron(RangeCron(SingleCron(MINUTE, 1), SingleCron(MINUTE, 10)), 5))
			assertThat(cronParser.parseSchedule("* 1-10/5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(minute = ListCron(MINUTE, listOf<ContinuousRangeCron>(SingleCron(MINUTE, 1),
				RangeCron(SingleCron(MINUTE, 1), SingleCron(MINUTE, 10)),	StepCron(AllCron(MINUTE), 5),	StepCron(SingleCron(MINUTE, 2), 5),
				StepCron(RangeCron(SingleCron(MINUTE, 1), SingleCron(MINUTE, 10)), 5))))
			assertThat(cronParser.parseSchedule("* 1,1-10,*/5,2/5,1-10/5 * * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given hours` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(hour = SingleCron(HOUR, 1))
			assertThat(cronParser.parseSchedule("* * 1 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(hour = RangeCron(SingleCron(HOUR, 1), SingleCron(HOUR, 10)))
			assertThat(cronParser.parseSchedule("* * 1-10 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(hour = StepCron(AllCron(HOUR), 5))
			assertThat(cronParser.parseSchedule("* * */5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(hour = StepCron(SingleCron(HOUR, 2), 5))
			assertThat(cronParser.parseSchedule("* * 2/5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(hour = StepCron(RangeCron(SingleCron(HOUR, 1), SingleCron(HOUR, 10)), 5))
			assertThat(cronParser.parseSchedule("* * 1-10/5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(hour = ListCron(HOUR, listOf<ContinuousRangeCron>(SingleCron(HOUR, 1),
				RangeCron(SingleCron(HOUR, 1), SingleCron(HOUR, 10)),	StepCron(AllCron(HOUR), 5),	StepCron(SingleCron(HOUR, 2), 5),
				StepCron(RangeCron(SingleCron(HOUR, 1), SingleCron(HOUR, 10)), 5))))
			assertThat(cronParser.parseSchedule("* * 1,1-10,*/5,2/5,1-10/5 * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of month` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(dayOfMonth = SingleCron(DAY_OF_MONTH, 1))
			assertThat(cronParser.parseSchedule("* * * 1 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(dayOfMonth = RangeCron(SingleCron(DAY_OF_MONTH, 1), SingleCron(DAY_OF_MONTH, 10)))
			assertThat(cronParser.parseSchedule("* * * 1-10 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(dayOfMonth = StepCron(AllCron(DAY_OF_MONTH), 5))
			assertThat(cronParser.parseSchedule("* * * */5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(dayOfMonth = StepCron(SingleCron(DAY_OF_MONTH, 2), 5))
			assertThat(cronParser.parseSchedule("* * * 2/5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(dayOfMonth = StepCron(RangeCron(SingleCron(DAY_OF_MONTH, 1), SingleCron(DAY_OF_MONTH, 10)), 5))
			assertThat(cronParser.parseSchedule("* * * 1-10/5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(dayOfMonth = ListCron(DAY_OF_MONTH, listOf<ContinuousRangeCron>(SingleCron(DAY_OF_MONTH, 1),
				RangeCron(SingleCron(DAY_OF_MONTH, 1), SingleCron(DAY_OF_MONTH, 10)),	StepCron(AllCron(DAY_OF_MONTH), 5),	StepCron(SingleCron(DAY_OF_MONTH, 2), 5),
				StepCron(RangeCron(SingleCron(DAY_OF_MONTH, 1), SingleCron(DAY_OF_MONTH, 10)), 5))))
			assertThat(cronParser.parseSchedule("* * * 1,1-10,*/5,2/5,1-10/5 * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given month numbers` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(month = SingleCron(MONTH, 1))
			assertThat(cronParser.parseSchedule("* * * * 1 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(month = RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)))
			assertThat(cronParser.parseSchedule("* * * * 1-10 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(month = StepCron(AllCron(MONTH), 5))
			assertThat(cronParser.parseSchedule("* * * * */5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(month = StepCron(SingleCron(MONTH, 2), 5))
			assertThat(cronParser.parseSchedule("* * * * 2/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(month = StepCron(RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)), 5))
			assertThat(cronParser.parseSchedule("* * * * 1-10/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(month = ListCron(MONTH, listOf<ContinuousRangeCron>(SingleCron(MONTH, 1),
				RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)),	StepCron(AllCron(MONTH), 5),	StepCron(SingleCron(MONTH, 2), 5),
				StepCron(RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)), 5))))
			assertThat(cronParser.parseSchedule("* * * * 1,1-10,*/5,2/5,1-10/5 *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given month symbols` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(month = SingleCron(MONTH, 1))
			assertThat(cronParser.parseSchedule("* * * * JAN *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(month = RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)))
			assertThat(cronParser.parseSchedule("* * * * JAN-OCT *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(month = StepCron(SingleCron(MONTH, 2), 5))
			assertThat(cronParser.parseSchedule("* * * * FEB/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(month = StepCron(RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)), 5))
			assertThat(cronParser.parseSchedule("* * * * JAN-OCT/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(month = ListCron(MONTH, listOf<ContinuousRangeCron>(SingleCron(MONTH, 1),
				RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)),	StepCron(AllCron(MONTH), 5),	StepCron(SingleCron(MONTH, 2), 5),
				StepCron(RangeCron(SingleCron(MONTH, 1), SingleCron(MONTH, 10)), 5))))
			assertThat(cronParser.parseSchedule("* * * * JAN,JAN-OCT,*/5,FEB/5,JAN-OCT/5 *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of week numbers` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(dayOfWeek = SingleCron(DAY_OF_WEEK, 1))
			assertThat(cronParser.parseSchedule("* * * * * 1")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)))
			assertThat(cronParser.parseSchedule("* * * * * 1-6")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(dayOfWeek = StepCron(AllCron(DAY_OF_WEEK), 5))
			assertThat(cronParser.parseSchedule("* * * * * */5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(dayOfWeek = StepCron(SingleCron(DAY_OF_WEEK, 2), 5))
			assertThat(cronParser.parseSchedule("* * * * * 2/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = StepCron(RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)), 5))
			assertThat(cronParser.parseSchedule("* * * * * 1-6/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(dayOfWeek = ListCron(DAY_OF_WEEK, listOf<ContinuousRangeCron>(SingleCron(DAY_OF_WEEK, 1),
				RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)),	StepCron(AllCron(DAY_OF_WEEK), 5),	StepCron(SingleCron(DAY_OF_WEEK, 2), 5),
				StepCron(RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)), 5))))
			assertThat(cronParser.parseSchedule("* * * * * 1,1-6,*/5,2/5,1-6/5")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of week symbols` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(dayOfWeek = SingleCron(DAY_OF_WEEK, 1))
			assertThat(cronParser.parseSchedule("* * * * * MON")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)))
			assertThat(cronParser.parseSchedule("* * * * * MON-SAT")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(dayOfWeek = StepCron(SingleCron(DAY_OF_WEEK, 2), 5))
			assertThat(cronParser.parseSchedule("* * * * * TUE/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = StepCron(RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)), 5))
			assertThat(cronParser.parseSchedule("* * * * * MON-SAT/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(dayOfWeek = ListCron(DAY_OF_WEEK, listOf<ContinuousRangeCron>(SingleCron(DAY_OF_WEEK, 1),
				RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)),	StepCron(AllCron(DAY_OF_WEEK), 5),	StepCron(SingleCron(DAY_OF_WEEK, 2), 5),
				StepCron(RangeCron(SingleCron(DAY_OF_WEEK, 1), SingleCron(DAY_OF_WEEK, 6)), 5))))
			assertThat(cronParser.parseSchedule("* * * * * MON,MON-SAT,*/5,TUE/5,MON-SAT/5")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given parser errors` {

		@Test
		fun `with bad @ symbol`() {
			assertThrows<CronParseException> { cronParser.parseSchedule("@bad") }
		}

		@Test
		fun `with invalid character`() {
			assertThrows<CronParseException> { cronParser.parseSchedule("* * * blah * *") }
		}

		@Test
		fun `with too many sections`() {
			assertThrows<CronParseException> { cronParser.parseSchedule("* * * * * * *") }
		}

		@Test
		fun `with not enough sections`() {
			assertThrows<CronParseException> { cronParser.parseSchedule("* * * * *") }
		}

		@Test
		fun `with invalid all cron`() {
			assertThrows<CronParseException> { cronParser.parseSchedule("*12 * * * * *") }
		}

		@Test
		fun `with invalid range cron`() {
			assertThrows<CronParseException> { cronParser.parseSchedule("1-* * * * * *") }
		}

		@Test
		fun `with invalid step cron`() {
			assertThrows<CronParseException> { cronParser.parseSchedule("*/* * * * * *") }
		}

	}

}