package org.thelaughingdev.cronscheduler

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SectionTest {

	@Nested
	inner class `Given Second` {

		@Test
		fun `with AllCron`() {
			val section = Second()
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with single`() {
			val i = 29
			val section = Second(i)
			assertThat(section.possibleValues()).isEqualTo(listOf(i))
		}

		@Test
		fun `with range`() {
			val i = 5
			val j = 10
			val section = Second(i..j)
			assertThat(section.possibleValues()).containsAll(i..j)
		}

		@Test
		fun `with step 1`() {
			val step = 1
			val section = Second(CronStep(step))
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with list 1,3,5`() {
			val arr = intArrayOf(1, 3, 5)
			val section = Second(CronList(*arr))
			assertThat(section.possibleValues()).containsAll(arr.toList())
		}

		@Test
		fun `with toString`() {
			val section = Second()
			assertThat(section.toString()).isEqualTo("Second(*)")
		}
	}

	@Nested
	inner class `Given Minute` {

		@Test
		fun `with AllCron`() {
			val section = Minute()
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with single`() {
			val i = 29
			val section = Minute(i)
			assertThat(section.possibleValues()).isEqualTo(listOf(i))
		}

		@Test
		fun `with range`() {
			val i = 5
			val j = 10
			val section = Minute(i..j)
			assertThat(section.possibleValues()).containsAll(i..j)
		}

		@Test
		fun `with step 1`() {
			val step = 1
			val section = Minute(CronStep(step))
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with list 1,3,5`() {
			val arr = intArrayOf(1, 3, 5)
			val section = Minute(CronList(*arr))
			assertThat(section.possibleValues()).containsAll(arr.toList())
		}

		@Test
		fun `with toString`() {
			val section = Minute()
			assertThat(section.toString()).isEqualTo("Minute(*)")
		}
	}

	@Nested
	inner class `Given Hour` {

		@Test
		fun `with AllCron`() {
			val section = Hour()
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with single`() {
			val i = 14
			val section = Hour(i)
			assertThat(section.possibleValues()).isEqualTo(listOf(i))
		}

		@Test
		fun `with range`() {
			val i = 5
			val j = 10
			val section = Hour(i..j)
			assertThat(section.possibleValues()).containsAll(i..j)
		}

		@Test
		fun `with step 1`() {
			val step = 1
			val section = Hour(CronStep(step))
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with list 1,3,5`() {
			val arr = intArrayOf(1, 3, 5)
			val section = Hour(CronList(*arr))
			assertThat(section.possibleValues()).containsAll(arr.toList())
		}

		@Test
		fun `with toString`() {
			val section = Hour()
			assertThat(section.toString()).isEqualTo("Hour(*)")
		}
	}

	@Nested
	inner class `Given DayOfMonth` {

		@Test
		fun `with AllCron`() {
			val section = DayOfMonth()
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with single`() {
			val i = 14
			val section = DayOfMonth(i)
			assertThat(section.possibleValues()).isEqualTo(listOf(i))
		}

		@Test
		fun `with range`() {
			val i = 5
			val j = 10
			val section = DayOfMonth(i..j)
			assertThat(section.possibleValues()).containsAll(i..j)
		}

		@Test
		fun `with step 1`() {
			val step = 1
			val section = DayOfMonth(CronStep(step))
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with list 1,3,5`() {
			val arr = intArrayOf(1, 3, 5)
			val section = DayOfMonth(CronList(*arr))
			assertThat(section.possibleValues()).containsAll(arr.toList())
		}

		@Test
		fun `with toString`() {
			val section = DayOfMonth()
			assertThat(section.toString()).isEqualTo("DayOfMonth(*)")
		}
	}

	@Nested
	inner class `Given Month` {

		@Test
		fun `with AllCron`() {
			val section = Month()
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with single`() {
			val i = 7
			val section = Month(i)
			assertThat(section.possibleValues()).isEqualTo(listOf(i))
		}

		@Test
		fun `with range`() {
			val i = 5
			val j = 10
			val section = Month(i..j)
			assertThat(section.possibleValues()).containsAll(i..j)
		}

		@Test
		fun `with step 1`() {
			val step = 1
			val section = Month(CronStep(step))
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with list 1,3,5`() {
			val arr = intArrayOf(1, 3, 5)
			val section = Month(CronList(*arr))
			assertThat(section.possibleValues()).containsAll(arr.toList())
		}

		@Test
		fun `with toString`() {
			val section = Month()
			assertThat(section.toString()).isEqualTo("Month(*)")
		}
	}

	@Nested
	inner class `Given DayOfWeek` {

		@Test
		fun `with AllCron`() {
			val section = DayOfWeek()
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with single`() {
			val i = 5
			val section = DayOfWeek(i)
			assertThat(section.possibleValues()).isEqualTo(listOf(i))
		}

		@Test
		fun `with range`() {
			val i = 1
			val j = 4
			val section = DayOfWeek(i..j)
			assertThat(section.possibleValues()).containsAll(i..j)
		}

		@Test
		fun `with step 1`() {
			val step = 1
			val section = DayOfWeek(CronStep(step))
			assertThat(section.possibleValues()).containsAll(section.range)
		}

		@Test
		fun `with list 1,3,5`() {
			val arr = intArrayOf(1, 3, 5)
			val section = DayOfWeek(CronList(*arr))
			assertThat(section.possibleValues()).containsAll(arr.toList())
		}

		@Test
		fun `with toString`() {
			val section = DayOfWeek()
			assertThat(section.toString()).isEqualTo("DayOfWeek(*)")
		}
	}

}