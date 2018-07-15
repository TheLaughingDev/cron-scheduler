package org.thelaughingdev.cronscheduler

sealed class CronType() : Iterable<Int> {

	protected abstract fun validate()

	abstract val section: CronSection

	abstract fun toCronString(): String
}

sealed class ContinuousRangeCron() : CronType()

data class RangeCron(val start: SingleCron, val end: SingleCron) : ContinuousRangeCron() {

	init {
		validate()
	}

	override val section = start.section

	override fun toCronString() = "${start.toCronString()}-${end.toCronString()}"

	override fun iterator() = (start.value..end.value).iterator()

	override fun validate() {
		if(start.section != end.section)
			throw CronValidationException("start and end must be in the same section.")

		if(end.value < start.value)
			throw CronValidationException("start must come before end.")
	}
}

data class StepCron(val base: ContinuousRangeCron, val step: Int) : ContinuousRangeCron() {

	init {
		validate()
	}

	override val section = base.section

	override fun toCronString() = "${base.toCronString()}/$step"

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

	init {
		validate()
	}

	override fun toCronString() = value.toString()

	override fun iterator() = arrayOf(value).iterator()

	override fun validate() {
		if(value !in section.range)
			throw CronValidationException(value, section)
	}
}

data class AllCron(override val section: CronSection) : ContinuousRangeCron() {

	init {
		validate()
	}

	override fun toCronString() = "*"

	override fun iterator() = section.range.iterator()

	override fun validate() = Unit
}

data class ListCron(override val section: CronSection, val list: List<ContinuousRangeCron>) : CronType() {

	init {
		validate()
	}

	override fun toCronString() = list.map { it.toCronString() }.joinToString(",")

	override fun iterator() = list.flatMap { it }.distinct().sorted().iterator()

	override fun validate() {
		if(list.isEmpty())
			throw CronValidationException("list cannot be empty.")

		for(type in list)
			if(type.section != section)
				throw CronValidationException("All items in ${ListCron::class.java} must have the same section.")
	}
}