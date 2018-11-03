package org.thelaughingdev.cronscheduler

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CronTypeTest {

	@Nested
	inner class `Given all cron` {

		@Test
		fun `with possibleValues`() {
			val range = 1..100
			val cron = CronAll()
			assertThat(cron.possibleValues(range)).containsAll(range)
			assertThat(cron.possibleValues(range)).doesNotContain(range.first - 1, range.last + 1)
		}

		@Test
		fun `with toString`() {
			val cron = CronAll()
			assertThat(cron.toString()).isEqualTo("*")
		}
	}

	@Nested
	inner class `Given single` {

		@Test
		fun `with possibleValues`() {
			val i = 100
			val range = i..i
			val cron = CronSingle(i)
			assertThat(cron.possibleValues(range)).containsAll(range)
			assertThat(cron.possibleValues(range)).doesNotContain(i - 1, i + 1)
		}

		@Test
		fun `with toString`() {
			val i = 300
			val cron = CronSingle(i)
			assertThat(cron.toString()).isEqualTo(i.toString())
		}
	}

	@Nested
	inner class `Given range` {

		@Test
		fun `with possibleValues`() {
			val range = 300..400
			val cron = CronRange(range)
			assertThat(cron.possibleValues(range)).containsAll(range)
			assertThat(cron.possibleValues(range)).doesNotContain(range.first - 1, range.last + 1)
		}

		@Test
		fun `with toString`() {
			val range = 100..200
			val cron = CronRange(range)
			assertThat(cron.toString()).isEqualTo("${range.first}-${range.last}")
		}
	}

	@Nested
	inner class `Given step` {

		@Test
		fun `with possibleValues`() {
			val range = 0..30
			val step = 5
			val cron = CronStep(range, step)
			assertThat(cron.possibleValues(range)).containsAll(listOf(0, 5, 10, 15, 20, 25, 30))
			assertThat(cron.possibleValues(range)).doesNotContain(-1, 6, 31)
		}

		@Test
		fun `with toString`() {
			val range = 0..30
			val step = 5
			val cron = CronStep(range, step)
			assertThat(cron.toString()).isEqualTo("0-30/5")
		}
	}

	@Nested
	inner class `Given list` {

		@Test
		fun `with possibleValues`() {
			val arr = intArrayOf(3, 100, 50)
			val cron = CronList(*arr)
			assertThat(cron.possibleValues(0..500)).containsAll(arr.toList())
			assertThat(cron.possibleValues(0..500)).doesNotContain(1, 2, 101)
		}

		@Test
		fun `with toString`() {
			val cron = CronList(CronRange(1..5), CronSingle(10), CronStep(11..20, 5))
			assertThat(cron.toString()).isEqualTo("1-5,10,11-20/5")
		}
	}
}