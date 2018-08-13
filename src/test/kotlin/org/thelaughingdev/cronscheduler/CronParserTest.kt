package org.thelaughingdev.cronscheduler

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

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
			val defaultSchedule = CronSchedule(Second(1))
			assertThat(cronParser.parseSchedule("1 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(Second(1..10))
			assertThat(cronParser.parseSchedule("1-10 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(Second(StepCron(AllCron(), 5)))
			assertThat(cronParser.parseSchedule("*/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(Second(StepCron(SingleCron(2), 5)))
			assertThat(cronParser.parseSchedule("2/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(Second(StepCron(1..10, 5)))
			assertThat(cronParser.parseSchedule("1-10/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(Second(ListCron(listOf<ContinuousRangeCron>(SingleCron(1), RangeCron(1..10),
				StepCron(AllCron(), 5),	StepCron(SingleCron(2), 5), StepCron(1..10, 5)))))
			assertThat(cronParser.parseSchedule("1,1-10,*/5,2/5,1-10/5 * * * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given minutes` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(minute = Minute(1))
			assertThat(cronParser.parseSchedule("* 1 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(minute = Minute(RangeCron(1..10)))
			assertThat(cronParser.parseSchedule("* 1-10 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(minute = Minute(StepCron(AllCron(), 5)))
			assertThat(cronParser.parseSchedule("* */5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(minute = Minute(StepCron(SingleCron(2), 5)))
			assertThat(cronParser.parseSchedule("* 2/5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(minute = Minute(StepCron(1..10, 5)))
			assertThat(cronParser.parseSchedule("* 1-10/5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(minute = Minute(ListCron(listOf(SingleCron(1),
				RangeCron(1..10),	StepCron(AllCron(), 5),	StepCron(SingleCron(2), 5),
				StepCron(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* 1,1-10,*/5,2/5,1-10/5 * * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given hours` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(hour = Hour(1))
			assertThat(cronParser.parseSchedule("* * 1 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(hour = Hour(RangeCron(1..10)))
			assertThat(cronParser.parseSchedule("* * 1-10 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(hour = Hour(StepCron(AllCron(), 5)))
			assertThat(cronParser.parseSchedule("* * */5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(hour = Hour(StepCron(SingleCron(2), 5)))
			assertThat(cronParser.parseSchedule("* * 2/5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(hour = Hour(StepCron(1..10, 5)))
			assertThat(cronParser.parseSchedule("* * 1-10/5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(hour = Hour(ListCron(listOf(SingleCron(1),
				RangeCron(1..10),	StepCron(AllCron(), 5),	StepCron(SingleCron(2), 5),
				StepCron(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * 1,1-10,*/5,2/5,1-10/5 * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of month` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(dayOfMonth = DayOfMonth(1))
			assertThat(cronParser.parseSchedule("* * * 1 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(dayOfMonth = DayOfMonth(RangeCron(1..10)))
			assertThat(cronParser.parseSchedule("* * * 1-10 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(dayOfMonth = DayOfMonth(StepCron(AllCron(), 5)))
			assertThat(cronParser.parseSchedule("* * * */5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(dayOfMonth = DayOfMonth(StepCron(SingleCron(2), 5)))
			assertThat(cronParser.parseSchedule("* * * 2/5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(dayOfMonth = DayOfMonth(StepCron(1..10, 5)))
			assertThat(cronParser.parseSchedule("* * * 1-10/5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(dayOfMonth = DayOfMonth(ListCron(listOf(SingleCron(1),
				RangeCron(1..10),	StepCron(AllCron(), 5),	StepCron(SingleCron(2), 5),
				StepCron(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * * 1,1-10,*/5,2/5,1-10/5 * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given month numbers` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(month = Month(1))
			assertThat(cronParser.parseSchedule("* * * * 1 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(month = Month(1..10))
			assertThat(cronParser.parseSchedule("* * * * 1-10 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(month = Month(StepCron(AllCron(), 5)))
			assertThat(cronParser.parseSchedule("* * * * */5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(month = Month(StepCron(SingleCron(2), 5)))
			assertThat(cronParser.parseSchedule("* * * * 2/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(month = Month(1..10, 5))
			assertThat(cronParser.parseSchedule("* * * * 1-10/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(month = Month(ListCron(listOf(SingleCron(1),
				RangeCron(1..10),	StepCron(AllCron(), 5),	StepCron(SingleCron(2), 5),
				StepCron(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * * * 1,1-10,*/5,2/5,1-10/5 *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given month symbols` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(month = Month(1))
			assertThat(cronParser.parseSchedule("* * * * JAN *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(month = Month(1..10))
			assertThat(cronParser.parseSchedule("* * * * JAN-OCT *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(month = Month(StepCron(2, 5)))
			assertThat(cronParser.parseSchedule("* * * * FEB/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(month = Month(1..10, 5))
			assertThat(cronParser.parseSchedule("* * * * JAN-OCT/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(month = Month(ListCron(listOf(SingleCron(1),
				RangeCron(1..10),	StepCron(5),	StepCron(2, 5),
				StepCron(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * * * JAN,JAN-OCT,*/5,FEB/5,JAN-OCT/5 *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of week numbers` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(1))
			assertThat(cronParser.parseSchedule("* * * * * 1")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(1..6))
			assertThat(cronParser.parseSchedule("* * * * * 1-6")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(StepCron(5)))
			assertThat(cronParser.parseSchedule("* * * * * */5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(StepCron(2, 5)))
			assertThat(cronParser.parseSchedule("* * * * * 2/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(1..6, 5))
			assertThat(cronParser.parseSchedule("* * * * * 1-6/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(ListCron(listOf(SingleCron(1),
				RangeCron(1..6), StepCron(5), StepCron(2, 5),
				StepCron(1..6, 5)))))
			assertThat(cronParser.parseSchedule("* * * * * 1,1-6,*/5,2/5,1-6/5")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of week symbols` {

		@Test
		fun `with digit`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(1))
			assertThat(cronParser.parseSchedule("* * * * * MON")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(1..6))
			assertThat(cronParser.parseSchedule("* * * * * MON-SAT")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(StepCron(2, 5)))
			assertThat(cronParser.parseSchedule("* * * * * TUE/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(1..6, 5))
			assertThat(cronParser.parseSchedule("* * * * * MON-SAT/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = CronSchedule(dayOfWeek = DayOfWeek(ListCron(listOf(SingleCron(1),
				RangeCron(1..6),	StepCron(5),	StepCron(2, 5),
				StepCron(1..6, 5)))))
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