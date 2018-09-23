package org.thelaughingdev.cronscheduler

/**
 * Base class for all cron types.
 */
sealed class CronType() {
	/**
	 * Returns the possible values given the range.
	 * @param range The range to use for possible values.
	 * @return The list of possible values.
	 */
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

/**
 * Base class for continuous range cron types. This is all types except list.
 */
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

/**
 * A range cron in the form of n-m.
 * @param range The range of values. m must be greater than n.
 */
data class RangeCron(val range: IntRange) : ContinuousRangeCron() {

	init {
		if(range.endInclusive < range.start)
			throw CronValidationException("start must come before end.")
	}

	override fun possibleValues(range: IntRange) = (this.range).toList()

	override fun toString() = "${range.start}-${range.endInclusive}"
}

/**
 * A step cron in the form of *\/s, n-m/s, n/s.
 * @param base The base continuous range cron type for the step. Cannot be a StepCron.
 * @param step The step value.
 */
data class StepCron(val base: ContinuousRangeCron, val step: Int) : ContinuousRangeCron() {

	init {
		if(base is StepCron)
			throw CronValidationException("base cannot be of type ${StepCron::class.java}.")
	}

	/**
	 * Convienence constructor for making a step with a range.
	 * @param range The range for the step.
	 * @param step The step value.
	 */
	constructor(range: IntRange, step: Int): this(RangeCron(range.first..range.endInclusive), step)

	/**
	 * Convienence constructor for making a step with a single value.
	 * @param singleValue The single value for the step.
	 * @param step The step value.
	 */
	constructor(singleValue: Int, step: Int): this(SingleCron(singleValue), step)

	/**
	 * Convienence constructor for making a step with an AllCron.
	 * @param step The step value.
	 */
	constructor(step: Int): this(AllCron(), step)

	override fun possibleValues(range: IntRange) = when(base) {
		is SingleCron -> (base.value..range.endInclusive).filterIndexed { i, _ -> i == 0 || i % step == 0 }.toList()
		else -> base.possibleValues(range).filterIndexed { i, _ -> i == 0 || i % step == 0 }.toList()
	}

	override fun toString() = "$base/$step"
}

/**
 * The single cron value in the form of n.
 * @param value The value.
 */
data class SingleCron(val value :Int) : ContinuousRangeCron() {

	override fun possibleValues(range: IntRange) = listOf(value)

	override fun toString() = value.toString()
}

/**
 * An all cron in the form of *.
 */
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

/**
 * A list cron in the form of x,y,z.
 * @param list The list of continuous range crons.
 */
data class ListCron(val list: List<ContinuousRangeCron>) : CronType() {

	init {
		if(list.isEmpty())
			throw CronValidationException("list cannot be empty.")
	}

	/**
	 * Convience constructor that takes a varag.
	 * @param l The list of continuous range crons.
	 */
	constructor(vararg l: ContinuousRangeCron): this(l.toList())

	/**
	 * Convience constructor that takes a vararg of ints as creates the list a list of SingleCrons
	 * @param l The list of ints.
	 */
	constructor(vararg l: Int): this(l.map { SingleCron(it) })

	override fun possibleValues(range: IntRange) = list.flatMap { it.possibleValues(range) }.distinct().sorted().toList()

	override fun toString() = list.map { it.toString() }.joinToString(",")
}