package org.thelaughingdev.cronscheduler

sealed class CronType() {
	abstract fun possibleValues(range: IntRange): List<Int>

	override fun equals(other: Any?): Boolean {
		if(this === other) return true
		if(other !is CronType) return false
		return true
	}

	override fun hashCode(): Int {
		return javaClass.hashCode()
	}


}

sealed class ContinuousRangeCron() : CronType() {

	override fun equals(other: Any?): Boolean {
		if(this === other) return true
		if(other !is ContinuousRangeCron) return false
		if(!super.equals(other)) return false
		return true
	}

	override fun hashCode(): Int {
		return super.hashCode()
	}
}

data class RangeCron(val range: IntRange) : ContinuousRangeCron() {

	init {
		if(range.endInclusive < range.start)
			throw CronValidationException("start must come before end.")
	}

	override fun possibleValues(range: IntRange) = (this.range).toList()

	override fun toString() = "${range.start}-${range.endInclusive}"
}

data class StepCron(val base: ContinuousRangeCron, val step: Int) : ContinuousRangeCron() {

	init {
		if(base is StepCron)
			throw CronValidationException("base cannot be of type ${StepCron::class.java}.")
	}

	constructor(range: IntRange, step: Int): this(RangeCron(range.first..range.endInclusive), step)
	constructor(singleValue: Int, step: Int): this(SingleCron(singleValue), step)
	constructor(step: Int): this(AllCron(), step)

	override fun possibleValues(range: IntRange) = when(base) {
		is SingleCron -> (base.value..range.endInclusive).filterIndexed { i, _ -> i == 0 || i % step == 0 }.toList()
		else -> base.possibleValues(range).filterIndexed { i, _ -> i == 0 || i % step == 0 }.toList()
	}

	override fun toString() = "$base/$step"
}

data class SingleCron(val value :Int) : ContinuousRangeCron() {

	override fun possibleValues(range: IntRange) = listOf(value)

	override fun toString() = value.toString()
}

class AllCron() : ContinuousRangeCron() {

	override fun possibleValues(range: IntRange) = range.toList()

	override fun equals(other: Any?): Boolean {
		if(this === other) return true
		if(javaClass != other?.javaClass) return false
		return true
	}

	override fun hashCode(): Int {
		return javaClass.hashCode()
	}

	override fun toString() = "*"

}

data class ListCron(val list: List<ContinuousRangeCron>) : CronType() {

	init {
		if(list.isEmpty())
			throw CronValidationException("list cannot be empty.")
	}

	constructor(vararg l: ContinuousRangeCron): this(l.toList())

	constructor(vararg l: Int): this(l.map { SingleCron(it) })

	override fun possibleValues(range: IntRange) = list.flatMap { it.possibleValues(range) }.distinct().sorted().toList()

	override fun toString() = list.map { it.toString() }.joinToString(",")
}