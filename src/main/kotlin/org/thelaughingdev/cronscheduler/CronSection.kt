package org.thelaughingdev.cronscheduler

/**
 * Represents a section in a cron string, such as seconds, minutes, day of month, etc.
 * @param cron The cron type in the section.
 * @param range The range of possible values the section can have.
 * @param textValues The text values that represent values in the section, if appropriate.
 */
sealed class Section(val cron: CronType, val range: IntRange, val textValues: Map<String, Int>) {

	init {
		val outsideValue = possibleValues().firstOrNull { it !in range }

		if(outsideValue != null)
			throw CronValidationException("Value $outsideValue is out of range for section ${this::class.java.name}")
	}

	/**
	 * The list of possible values that the section can have. This is determinted by the section range and possible
	 * values of the crontype.
	 */
	fun possibleValues(): List<Int> = cron.possibleValues(range)

	/**
	 * Makes a copy of the section.
	 */
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

/**
 * The seconds section.
 * @param cron The cron type.
 */
class Second(cron: CronType): Section(cron, 0..59, mapOf()) {
	/**
	 * Convience constructor to create the section using a AllCron.
	 */
	constructor(): this(AllCron())
	/**
	 * Convience constructor to creates section using the value as a SingleCron.
	 * @param value The int value of the single cron.
	 */
	constructor(value: Int): this(SingleCron(value))
	/**
	 * Convience constructor to create the section using the range as a RangeCron.
	 * @param range The range for the RangeCron.
	 */
	constructor(range: IntRange): this(RangeCron(range))
	/**
	 * Convience constructor to create the section using the params as a StepCron.
	 * @param range The range for the step.
	 * @param step The step amount.
	 */
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Second(cron)

	override fun toString(): String = "Second($cron)"
}

/**
 * The minutes section.
 * @param cron The cron type.
 */
class Minute(cron: CronType): Section(cron, 0..59, mapOf()) {
	/**
	 * Convience constructor to create the section using a AllCron.
	 */
	constructor(): this(AllCron())
	/**
	 * Convience constructor to creates section using the value as a SingleCron.
	 * @param value The int value of the single cron.
	 */
	constructor(value: Int): this(SingleCron(value))
	/**
	 * Convience constructor to create the section using the range as a RangeCron.
	 * @param range The range for the RangeCron.
	 */
	constructor(range: IntRange): this(RangeCron(range))
	/**
	 * Convience constructor to create the section using the params as a StepCron.
	 * @param range The range for the step.
	 * @param step The step amount.
	 */
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Minute(cron)

	override fun toString(): String = "Minute($cron)"
}

/**
 * The hours section.
 * @param cron The cron type.
 */
class Hour(cron: CronType): Section(cron, 0..23, mapOf()) {
	/**
	 * Convience constructor to create the section using a AllCron.
	 */
	constructor(): this(AllCron())
	/**
	 * Convience constructor to creates section using the value as a SingleCron.
	 * @param value The int value of the single cron.
	 */
	constructor(value: Int): this(SingleCron(value))
	/**
	 * Convience constructor to create the section using the range as a RangeCron.
	 * @param range The range for the RangeCron.
	 */
	constructor(range: IntRange): this(RangeCron(range))
	/**
	 * Convience constructor to create the section using the params as a StepCron.
	 * @param range The range for the step.
	 * @param step The step amount.
	 */
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Hour(cron)

	override fun toString(): String = "Hour($cron)"
}

/**
 * The day of month section.
 * @param cron The cron type.
 */
class DayOfMonth(cron: CronType): Section(cron, 1..31, mapOf()) {
	/**
	 * Convience constructor to create the section using a AllCron.
	 */
	constructor(): this(AllCron())
	/**
	 * Convience constructor to creates section using the value as a SingleCron.
	 * @param value The int value of the single cron.
	 */
	constructor(value: Int): this(SingleCron(value))
	/**
	 * Convience constructor to create the section using the range as a RangeCron.
	 * @param range The range for the RangeCron.
	 */
	constructor(range: IntRange): this(RangeCron(range))
	/**
	 * Convience constructor to create the section using the params as a StepCron.
	 * @param range The range for the step.
	 * @param step The step amount.
	 */
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = DayOfMonth(cron)

	override fun toString(): String = "DayOfMonth($cron)"
}

/**
 * The month section.
 * @param cron The cron type.
 */
class Month(cron: CronType): Section(cron, 1..12, mapOf(Pair("JAN", 1), Pair("FEB", 2), Pair("MAR", 3), Pair("APR", 4),
	Pair("MAY", 5), Pair("JUN", 6), Pair("JUL", 7), Pair("AUG", 8), Pair("SEP", 9),
	Pair("OCT", 10), Pair("NOV", 11), Pair("DEC", 12))) {
	/**
	 * Convience constructor to create the section using a AllCron.
	 */
	constructor(): this(AllCron())
	/**
	 * Convience constructor to creates section using the value as a SingleCron.
	 * @param value The int value of the single cron.
	 */
	constructor(value: Int): this(SingleCron(value))
	/**
	 * Convience constructor to create the section using the range as a RangeCron.
	 * @param range The range for the RangeCron.
	 */
	constructor(range: IntRange): this(RangeCron(range))
	/**
	 * Convience constructor to create the section using the params as a StepCron.
	 * @param range The range for the step.
	 * @param step The step amount.
	 */
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = Month(cron)

	override fun toString(): String = "Month($cron)"
}

/**
 * The day of week section.
 * @param cron The cron type.
 */
class DayOfWeek(cron: CronType): Section(cron, 0..6, mapOf(Pair("SUN", 0), Pair("MON", 1), Pair("TUE", 2), Pair("WED", 3),
	Pair("THR", 4), Pair("FRI", 5), Pair("SAT", 6))) {
	/**
	 * Convience constructor to create the section using a AllCron.
	 */
	constructor(): this(AllCron())
	/**
	 * Convience constructor to creates section using the value as a SingleCron.
	 * @param value The int value of the single cron.
	 */
	constructor(value: Int): this(SingleCron(value))
	/**
	 * Convience constructor to create the section using the range as a RangeCron.
	 * @param range The range for the RangeCron.
	 */
	constructor(range: IntRange): this(RangeCron(range))
	/**
	 * Convience constructor to create the section using the params as a StepCron.
	 * @param range The range for the step.
	 * @param step The step amount.
	 */
	constructor(range: IntRange, step: Int): this(StepCron(range, step))

	override fun copy(cron: CronType) = DayOfWeek(cron)

	override fun toString(): String = "DayOfWeek($cron)"
}

