package org.thelaughingdev.cronscheduler

sealed class Section(val cron: CronType, val range: IntRange, val textValues: Map<String, Int>) {

	init {
		val outsideValue = possibleValues().firstOrNull { it !in range }

		if(outsideValue != null)
			throw CronValidationException("Value $outsideValue is out of range for section ${this::class.java.name}")
	}

	fun possibleValues(): List<Int> = cron.possibleValues(range)

	abstract fun copy(cron: CronType): Section

	override fun equals(other: Any?): Boolean {
		if(this === other) return true
		if(other !is Section) return false

		if(cron != other.cron) return false

		return true
	}

	override fun hashCode(): Int {
		return cron.hashCode()
	}
}

class Second(cron: CronType): Section(cron, 0..59, mapOf()) {
	constructor(): this(AllCron())
	constructor(value: Int): this(SingleCron(value))
	constructor(range: IntRange): this(RangeCron(range))
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Second(cron)

	override fun toString(): String = "Second($cron)"
}

class Minute(cron: CronType): Section(cron, 0..59, mapOf()) {
	constructor(): this(AllCron())
	constructor(value: Int): this(SingleCron(value))
	constructor(range: IntRange): this(RangeCron(range))
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Minute(cron)

	override fun toString(): String = "Minute($cron)"
}

class Hour(cron: CronType): Section(cron, 0..23, mapOf()) {
	constructor(): this(AllCron())
	constructor(value: Int): this(SingleCron(value))
	constructor(range: IntRange): this(RangeCron(range))
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Hour(cron)

	override fun toString(): String = "Hour($cron)"
}

class DayOfMonth(cron: CronType): Section(cron, 1..31, mapOf()) {
	constructor(): this(AllCron())
	constructor(value: Int): this(SingleCron(value))
	constructor(range: IntRange): this(RangeCron(range))
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = DayOfMonth(cron)

	override fun toString(): String = "DayOfMonth($cron)"
}

class Month(cron: CronType): Section(cron, 1..12, mapOf(Pair("JAN", 1), Pair("FEB", 2), Pair("MAR", 3), Pair("APR", 4),
	Pair("MAY", 5), Pair("JUN", 6), Pair("JUL", 7), Pair("AUG", 8), Pair("SEP", 9),
	Pair("OCT", 10), Pair("NOV", 11), Pair("DEC", 12))) {
	constructor(): this(AllCron())
	constructor(value: Int): this(SingleCron(value))
	constructor(range: IntRange): this(RangeCron(range))
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Month(cron)

	override fun toString(): String = "Month($cron)"
}

class DayOfWeek(cron: CronType): Section(cron, 0..6, mapOf(Pair("SUN", 0), Pair("MON", 1), Pair("TUE", 2), Pair("WED", 3),
	Pair("THR", 4), Pair("FRI", 5), Pair("SAT", 6))) {
	constructor(): this(AllCron())
	constructor(value: Int): this(SingleCron(value))
	constructor(range: IntRange): this(RangeCron(range))
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = DayOfWeek(cron)

	override fun toString(): String = "DayOfWeek($cron)"
}

