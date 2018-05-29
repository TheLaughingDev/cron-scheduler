package org.thelaughingdev.cronscheduler

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.thelaughingdev.cronscheduler.CronSection.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CronParserTest {

	private val cronParser = BasicParser(ParserData.Factory)

	@Nested
	inner class `Given special variable` {

		@Test
		fun `with @yearly`() {
			val schedule = cronParser.parseSchedule("@yearly")
			assertThat(schedule).isEqualTo(CronSchedule.YEARLY)
		}

		@Test
		fun `with @annually`() {
			val schedule = cronParser.parseSchedule("@annually")
			assertThat(schedule).isEqualTo(CronSchedule.YEARLY)
		}

		@Test
		fun `with @monthly`() {
			val schedule = cronParser.parseSchedule("@monthly")
			assertThat(schedule).isEqualTo(CronSchedule.MONTHLY)
		}

		@Test
		fun `with @daily`() {
			val schedule = cronParser.parseSchedule("@daily")
			assertThat(schedule).isEqualTo(CronSchedule.DAILY)
		}

		@Test
		fun `with @hourly`() {
			val schedule = cronParser.parseSchedule("@hourly")
			assertThat(schedule).isEqualTo(CronSchedule.HOURLY)
		}

		@Test
		fun `not valid bad variable`() {
			assertThatThrownBy { cronParser.parseSchedule("@blah") }.isInstanceOf(CronParseException::class.java)
		}
	}

}