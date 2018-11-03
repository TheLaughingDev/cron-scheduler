package org.thelaughingdev.cronscheduler

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParserTest {

	private lateinit var scheduleHelper: ScheduleHelper

	private lateinit var cronParser: BasicParser

	@BeforeEach
	fun before() {
		scheduleHelper = mockk<ScheduleHelper>()
		cronParser = BasicParser(scheduleHelper)
	}

	@Nested
	inner class `Given special variable` {

		@Test
		fun `with @yearly`() {
			val defaultValue = Schedule()
			every { scheduleHelper.YEARLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@yearly")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @annually`() {
			val defaultValue = Schedule()
			every { scheduleHelper.YEARLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@annually")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @monthly`() {
			val defaultValue = Schedule()
			every { scheduleHelper.MONTHLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@monthly")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @daily`() {
			val defaultValue = Schedule()
			every { scheduleHelper.DAILY } returns defaultValue
			val schedule = cronParser.parseSchedule("@daily")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @hourly`() {
			val defaultValue = Schedule()
			every { scheduleHelper.HOURLY } returns defaultValue
			val schedule = cronParser.parseSchedule("@hourly")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @now`() {
			val defaultValue = Schedule()
			every { scheduleHelper.now() } returns defaultValue
			val schedule = cronParser.parseSchedule("@now")
			assertThat(schedule).isEqualTo(defaultValue)
		}

		@Test
		fun `with @reboot`() {
			val defaultValue = Schedule()
			every { scheduleHelper.now() } returns defaultValue
			val schedule = cronParser.parseSchedule("@reboot")
			assertThat(schedule).isEqualTo(defaultValue)
		}
	}

	@Nested
	inner class `Given seconds` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(Second(1))
			assertThat(cronParser.parseSchedule("1 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(Second(1..10))
			assertThat(cronParser.parseSchedule("1-10 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = Schedule(Second(CronStep(CronAll(), 5)))
			assertThat(cronParser.parseSchedule("*/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(Second(CronStep(CronSingle(2), 5)))
			assertThat(cronParser.parseSchedule("2/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(Second(CronStep(1..10, 5)))
			assertThat(cronParser.parseSchedule("1-10/5 * * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(Second(CronList(listOf<CronContinuousRange>(CronSingle(1), CronRange(1..10),
				CronStep(CronAll(), 5), CronStep(CronSingle(2), 5), CronStep(1..10, 5)))))
			assertThat(cronParser.parseSchedule("1,1-10,*/5,2/5,1-10/5 * * * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given minutes` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(minute = Minute(1))
			assertThat(cronParser.parseSchedule("* 1 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(minute = Minute(CronRange(1..10)))
			assertThat(cronParser.parseSchedule("* 1-10 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = Schedule(minute = Minute(CronStep(CronAll(), 5)))
			assertThat(cronParser.parseSchedule("* */5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(minute = Minute(CronStep(CronSingle(2), 5)))
			assertThat(cronParser.parseSchedule("* 2/5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(minute = Minute(CronStep(1..10, 5)))
			assertThat(cronParser.parseSchedule("* 1-10/5 * * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(minute = Minute(CronList(listOf(CronSingle(1),
				CronRange(1..10), CronStep(CronAll(), 5), CronStep(CronSingle(2), 5),
				CronStep(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* 1,1-10,*/5,2/5,1-10/5 * * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given hours` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(hour = Hour(1))
			assertThat(cronParser.parseSchedule("* * 1 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(hour = Hour(CronRange(1..10)))
			assertThat(cronParser.parseSchedule("* * 1-10 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = Schedule(hour = Hour(CronStep(CronAll(), 5)))
			assertThat(cronParser.parseSchedule("* * */5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(hour = Hour(CronStep(CronSingle(2), 5)))
			assertThat(cronParser.parseSchedule("* * 2/5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(hour = Hour(CronStep(1..10, 5)))
			assertThat(cronParser.parseSchedule("* * 1-10/5 * * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(hour = Hour(CronList(listOf(CronSingle(1),
				CronRange(1..10), CronStep(CronAll(), 5), CronStep(CronSingle(2), 5),
				CronStep(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * 1,1-10,*/5,2/5,1-10/5 * * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of month` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(dayOfMonth = DayOfMonth(1))
			assertThat(cronParser.parseSchedule("* * * 1 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(dayOfMonth = DayOfMonth(CronRange(1..10)))
			assertThat(cronParser.parseSchedule("* * * 1-10 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = Schedule(dayOfMonth = DayOfMonth(CronStep(CronAll(), 5)))
			assertThat(cronParser.parseSchedule("* * * */5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(dayOfMonth = DayOfMonth(CronStep(CronSingle(2), 5)))
			assertThat(cronParser.parseSchedule("* * * 2/5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(dayOfMonth = DayOfMonth(CronStep(1..10, 5)))
			assertThat(cronParser.parseSchedule("* * * 1-10/5 * *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(dayOfMonth = DayOfMonth(CronList(listOf(CronSingle(1),
				CronRange(1..10), CronStep(CronAll(), 5), CronStep(CronSingle(2), 5),
				CronStep(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * * 1,1-10,*/5,2/5,1-10/5 * *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given month numbers` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(month = Month(1))
			assertThat(cronParser.parseSchedule("* * * * 1 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(month = Month(1..10))
			assertThat(cronParser.parseSchedule("* * * * 1-10 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = Schedule(month = Month(CronStep(CronAll(), 5)))
			assertThat(cronParser.parseSchedule("* * * * */5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(month = Month(CronStep(CronSingle(2), 5)))
			assertThat(cronParser.parseSchedule("* * * * 2/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(month = Month(1..10, 5))
			assertThat(cronParser.parseSchedule("* * * * 1-10/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(month = Month(CronList(listOf(CronSingle(1),
				CronRange(1..10), CronStep(CronAll(), 5), CronStep(CronSingle(2), 5),
				CronStep(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * * * 1,1-10,*/5,2/5,1-10/5 *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given month symbols` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(month = Month(1))
			assertThat(cronParser.parseSchedule("* * * * JAN *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(month = Month(1..10))
			assertThat(cronParser.parseSchedule("* * * * JAN-OCT *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(month = Month(CronStep(2, 5)))
			assertThat(cronParser.parseSchedule("* * * * FEB/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(month = Month(1..10, 5))
			assertThat(cronParser.parseSchedule("* * * * JAN-OCT/5 *")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(month = Month(CronList(listOf(CronSingle(1),
				CronRange(1..10), CronStep(5), CronStep(2, 5),
				CronStep(1..10, 5)))))
			assertThat(cronParser.parseSchedule("* * * * JAN,JAN-OCT,*/5,FEB/5,JAN-OCT/5 *")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of week numbers` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(1))
			assertThat(cronParser.parseSchedule("* * * * * 1")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(1..6))
			assertThat(cronParser.parseSchedule("* * * * * 1-6")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step all`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(CronStep(5)))
			assertThat(cronParser.parseSchedule("* * * * * */5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(CronStep(2, 5)))
			assertThat(cronParser.parseSchedule("* * * * * 2/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(1..6, 5))
			assertThat(cronParser.parseSchedule("* * * * * 1-6/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(CronList(listOf(CronSingle(1),
				CronRange(1..6), CronStep(5), CronStep(2, 5),
				CronStep(1..6, 5)))))
			assertThat(cronParser.parseSchedule("* * * * * 1,1-6,*/5,2/5,1-6/5")).isEqualTo(defaultSchedule)
		}
	}

	@Nested
	inner class `Given day of week symbols` {

		@Test
		fun `with digit`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(1))
			assertThat(cronParser.parseSchedule("* * * * * MON")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with range`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(1..6))
			assertThat(cronParser.parseSchedule("* * * * * MON-SAT")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step single`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(CronStep(2, 5)))
			assertThat(cronParser.parseSchedule("* * * * * TUE/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with step range`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(1..6, 5))
			assertThat(cronParser.parseSchedule("* * * * * MON-SAT/5")).isEqualTo(defaultSchedule)
		}

		@Test
		fun `with list`() {
			val defaultSchedule = Schedule(dayOfWeek = DayOfWeek(CronList(listOf(CronSingle(1),
				CronRange(1..6), CronStep(5), CronStep(2, 5),
				CronStep(1..6, 5)))))
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