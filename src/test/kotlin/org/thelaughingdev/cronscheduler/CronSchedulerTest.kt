package org.thelaughingdev.cronscheduler

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CronSchedulerTest {

	private val parser = BasicParser()

	private val cronScheduler = BasicScheduler()

	@Nested
	inner class `Given nextTime` {

		@Nested
		inner class `with seconds` {

			@Test
			fun `in same minute`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 1, 0, 0, 30)
				val schedule = parser.parseSchedule("30 * * * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `in next minute`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 30)
				val expected = LocalDateTime.of(2000, 1, 1, 0, 1, 0)
				val schedule = parser.parseSchedule("0 * * * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}
		}

		@Nested
		inner class `with minutes` {

			@Test
			fun `in same hour`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 1, 0, 30, 0)
				val schedule = parser.parseSchedule("* 30 * * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `in next hour`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 30, 0)
				val expected = LocalDateTime.of(2000, 1, 1, 1, 0, 0)
				val schedule = parser.parseSchedule("* 0 * * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}
		}

		@Nested
		inner class `with hours` {

			@Test
			fun `in same day`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 1, 6, 0, 0)
				val schedule = parser.parseSchedule("* * 6 * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `in next day`() {
				val current = LocalDateTime.of(2000, 1, 1, 6, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 2, 0, 0, 0)
				val schedule = parser.parseSchedule("* * 0 * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}
		}

		@Nested
		inner class `with day of month and day of week` {

			@Test
			fun `with day of month in same month`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 15, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * 15 * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `with day of month in next month`() {
				val current = LocalDateTime.of(2000, 1, 15, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 2, 1, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * 1 * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `with day of week in same month`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 2, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * * * SUN")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `with day of week in next month`() {
				val current = LocalDateTime.of(2000, 1, 30, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 2, 1, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * * * TUE")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `with day of month before day of week`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 2, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * 2 * THR")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `with day of week before day of month`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 5, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * 15 * WED")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}
		}

		@Nested
		inner class `with month` {

			@Test
			fun `in same year`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 6, 1, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * * JUN *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `in next year`() {
				val current = LocalDateTime.of(2000, 6, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2001, 1, 1, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * * JAN *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}
		}

		@Nested
		inner class `with complex schedule` {

			@Test
			fun `using multiple singles`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 3, 2, 5, 30, 24)
				val schedule = parser.parseSchedule("24 30 5 2 MAR *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `using a middle of the day of month range`() {
				val current = LocalDateTime.of(2000, 1, 3, 0, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 3, 0, 0, 0)
				val schedule = parser.parseSchedule("* * * 2-5 * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `using a list with hours`() {
				val current = LocalDateTime.of(2000, 1, 1, 5, 0, 0)
				val expected = LocalDateTime.of(2000, 1, 1, 5, 0, 0)
				val schedule = parser.parseSchedule("* * 3,4,5 * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}

			@Test
			fun `using a step with minutes`() {
				val current = LocalDateTime.of(2000, 1, 1, 0, 1, 0)
				val expected = LocalDateTime.of(2000, 1, 1, 0, 5, 0)
				val schedule = parser.parseSchedule("* */5 * * * *")

				assertThat(expected).isEqualTo(cronScheduler.nextTime(schedule, current))
			}
		}
	}

	@Nested
	inner class `Given nextTimes` {

		@Test
		fun `with 1 time`() {
			val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
			val expected = listOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0))
			val schedule = parser.parseSchedule("0 * * * * *")

			assertThat(expected).isEqualTo(cronScheduler.nextTimes(schedule, 1, current))
		}

		@Test
		fun `with 2 times`() {
			val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
			val expected = listOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0), LocalDateTime.of(2000, 1, 1, 0, 1, 0))
			val schedule = parser.parseSchedule("0 * * * * *")

			assertThat(expected).isEqualTo(cronScheduler.nextTimes(schedule, 2, current))
		}

		@Test
		fun `with 3 times`() {
			val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
			val expected = listOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0), LocalDateTime.of(2000, 1, 1, 0, 1, 0), LocalDateTime.of(2000, 1, 1, 0, 2, 0))
			val schedule = parser.parseSchedule("0 * * * * *")

			assertThat(expected).isEqualTo(cronScheduler.nextTimes(schedule, 3, current))
		}

		@Test
		fun `with 3 times different years`() {
			val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
			val expected = listOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0), LocalDateTime.of(2001, 1, 1, 0, 0, 0), LocalDateTime.of(2002, 1, 1, 0, 0, 0))
			val schedule = parser.parseSchedule("0 0 0 1 JAN *")

			assertThat(expected).isEqualTo(cronScheduler.nextTimes(schedule, 3, current))
		}

		@Test()
		fun `with 5 time`() {
			val current = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
			val schedule = parser.parseSchedule("0 * * * * *")

			assertThrows<IllegalArgumentException> { cronScheduler.nextTimes(schedule, 0, current) }
		}

	}

}