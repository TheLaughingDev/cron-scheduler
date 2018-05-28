package org.thelaugingdev.cronscheduler

import org.thelaugingdev.cronscheduler.CronSection.*

sealed class CronType() : Iterable<Int> {

	init {
		validate()
	}

	protected abstract fun validate()

	abstract val section: CronSection
}

sealed class ContinuousRangeCron() : CronType()

data class RangeCron(val start: SingleCron, val end: SingleCron) : ContinuousRangeCron() {
	override val section = start.section

	override fun iterator() = (start.value..end.value).iterator()

	override fun validate() {
		if(start.section != end.section)
			throw CronValidationException("start and end must be in the same section.")
	}
}

data class StepCron(val base: ContinuousRangeCron, val step: Int) : ContinuousRangeCron() {
	override val section = base.section

	override fun iterator() = when(base) {
		is SingleCron -> (base.value..section.range.endInclusive).filterIndexed { i, _ -> i == 0 || i % step == 0 }.iterator()
		else -> base.filterIndexed { i, _ -> i == 0 || i % step == 0 }.iterator()
	}

	override fun validate() {
		if(base is StepCron)
			throw CronValidationException("base cannot be of type ${StepCron::class.java}.")
	}
}

data class SingleCron(override val section: CronSection, val value :Int) : ContinuousRangeCron() {
	override fun iterator() = arrayOf(value).iterator()

	override fun validate() {
		if(value !in section.range)
			throw CronValidationException(value, section)
	}
}

data class AllCron(override val section: CronSection) : ContinuousRangeCron() {
	override fun iterator() = section.range.iterator()

	override fun validate() = Unit
}

data class ListCron(override val section: CronSection, val list: List<ContinuousRangeCron>) : CronType() {
	override fun iterator() = list.flatMap { it }.distinct().sorted().iterator()

	override fun validate() {
		if(list.isEmpty())
			throw CronValidationException("list cannot be empty.")

		for(type in list)
			if(type.section != section)
				throw CronValidationException("All items in ${ListCron::class.java} must have the same section.")
	}
}

data class CronSchedule(
	val second: CronType = AllCron(SECOND),
	val minute: CronType = AllCron(MINUTE),
	val hour: CronType = AllCron(HOUR),
	val dayOfMonth: CronType = AllCron(DAY_OF_MONTH),
	val month: CronType = AllCron(MONTH),
	val year: CronType = AllCron(YEAR),
	val dayOfWeek: CronType = AllCron(DAY_OF_WEEK)
)