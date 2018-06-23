package org.thelaughingdev.cronscheduler

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

import org.thelaughingdev.cronscheduler.CronSection.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CronTypeTest {

	@Nested
	inner class `Given all cron` {

		@Test
		fun `with seconds`() {
			val cron = AllCron(SECOND)
			assertThat(cron).containsAll(SECOND.range)
		}

		@Test
		fun `with minutes`() {
			val cron = AllCron(MINUTE)
			assertThat(cron).containsAll(MINUTE.range)
		}

		@Test
		fun `with hours`() {
			val cron = AllCron(HOUR)
			assertThat(cron).containsAll(HOUR.range)
		}

		@Test
		fun `with day of month`() {
			val cron = AllCron(DAY_OF_MONTH)
			assertThat(cron).containsAll(DAY_OF_MONTH.range)
		}

		@Test
		fun `with month`() {
			val cron = AllCron(MONTH)
			assertThat(cron).containsAll(MONTH.range)
		}

		@Test
		fun `with day of week`() {
			val cron = AllCron(DAY_OF_WEEK)
			assertThat(cron).containsAll(DAY_OF_WEEK.range)
		}
	}

	@Nested
	inner class `Given single cron` {

		@Test
		fun `with iterate value in section`() {
			val i = 29
			val cron = SingleCron(SECOND, i)
			assertThat(cron).contains(i)
		}

		@Test
		fun `not valid with value before section`() {
			val i = -1
			assertThrows<CronValidationException> { SingleCron(SECOND, i) }
		}

		@Test
		fun `not valid with value after section`() {
			val i = 60
			assertThrows<CronValidationException> { SingleCron(SECOND, i) }
		}
	}

	@Nested
	inner class `Given range cron` {

		@Test
		fun `iterate with seconds`() {
			val i = 5
			val j = 10
			val cron = RangeCron(SingleCron(SECOND, i), SingleCron(SECOND, j))
			assertThat(cron).containsAll(i..j)
		}

		@Test
		fun `not valid with different sections`() {
			assertThrows<CronValidationException> { RangeCron(SingleCron(SECOND, 0), SingleCron(MINUTE, 0)) }
		}

		@Test
		fun `not valid with start before end`() {
			val i = 5
			val j = 10
			assertThrows<CronValidationException> { RangeCron(SingleCron(SECOND, j), SingleCron(SECOND, i)) }
		}
	}

	@Nested
	inner class `Given step cron` {

		@Test
		fun `iterate all with step of 1`() {
			val step = 1
			val cron = StepCron(AllCron(SECOND), step)
			assertThat(cron).containsAll(SECOND.range)
		}

		@Test
		fun `iterate all with step of 2`() {
			val step = 2
			val cron = StepCron(AllCron(SECOND), step)
			assertThat(cron).containsAll(SECOND.range.step(step))
		}

		@Test
		fun `iterate single starting at 20 with step of 1`() {
			val i = 20
			val step = 1
			val cron = StepCron(SingleCron(SECOND, i), step)
			assertThat(cron).containsAll((i..SECOND.range.endInclusive).step(step))
		}

		@Test
		fun `iterate single starting at 20 with step of 5`() {
			val i = 20
			val step = 5
			val cron = StepCron(SingleCron(SECOND, i), step)
			assertThat(cron).containsAll((i..SECOND.range.endInclusive).step(step))
		}

		@Test
		fun `iterate range 20-40 with step of 1`() {
			val i = 20
			val j = 40
			val step = 1
			val cron = StepCron(RangeCron(SingleCron(SECOND, i), SingleCron(SECOND, j)), step)
			assertThat(cron).containsAll((i..j).step(step))
		}

		@Test
		fun `iterate range 20-40 with step of 3`() {
			val i = 20
			val j = 40
			val step = 1
			val cron = StepCron(RangeCron(SingleCron(SECOND, i), SingleCron(SECOND, j)), step)
			assertThat(cron).containsAll((i..j).step(step))
		}

		@Test
		fun `not valid with base of StepCron`() {
			assertThrows<CronValidationException> { StepCron(StepCron(AllCron(SECOND), 3), 1) }
		}
	}

	@Nested
	inner class `Given list cron` {

		@Test
		fun `iterate with 1,3,5`() {
			val arr = intArrayOf(1, 3, 5)
			val cron = ListCron(SECOND, listOf(SingleCron(SECOND, arr[0]), SingleCron(SECOND, arr[1]), SingleCron(SECOND, arr[2])))
			assertThat(cron).containsAll(arr.asIterable())
		}

		@Test
		fun `iterate with 3,3,3`() {
			val arr = intArrayOf(3, 3, 3)
			val cron = ListCron(SECOND, listOf(SingleCron(SECOND, arr[0]), SingleCron(SECOND, arr[1]), SingleCron(SECOND, arr[2])))
			assertThat(cron).containsAll(arr.asIterable())
		}

		@Test
		fun `iterate with 2,4,*2`() {
			val arr = intArrayOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31)
			val cron = ListCron(DAY_OF_MONTH, listOf(SingleCron(DAY_OF_MONTH, 2), SingleCron(DAY_OF_MONTH, 4), StepCron(AllCron(DAY_OF_MONTH), 2)))
			assertThat(cron).containsAll(arr.asIterable())
		}

		@Test
		fun `iterate with 1,3,5-7`() {
			val arr = intArrayOf(1, 3, 5, 6, 7)
			val cron = ListCron(SECOND, listOf(SingleCron(SECOND, arr[0]), SingleCron(SECOND, arr[1]),
				RangeCron(SingleCron(SECOND, arr[2]), SingleCron(SECOND, arr[4]))))
			assertThat(cron).containsAll(arr.asIterable())
		}

		@Test
		fun `iterate with 1,3,5-10*2`() {
			val arr = intArrayOf(1, 3, 5, 7, 9)
			val cron = ListCron(SECOND, listOf(SingleCron(SECOND, 1), SingleCron(SECOND, 3),
				StepCron(RangeCron(SingleCron(SECOND, 5), SingleCron(SECOND, 10)), 2)))
			assertThat(cron).containsAll(arr.asIterable())
		}

		@Test
		fun `not valid with different sections`() {
			assertThrows<CronValidationException> { ListCron(SECOND, listOf(SingleCron(SECOND, 5), SingleCron(MINUTE, 5))) }
		}
	}

}