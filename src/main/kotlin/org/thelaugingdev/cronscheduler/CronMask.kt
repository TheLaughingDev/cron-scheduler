package org.thelaugingdev.cronscheduler

sealed class CronMask {

	abstract fun generatePossibilities(section: CronSection): Iterable<Int>

	abstract fun validate(section: CronSection): Boolean

}

class AllCron : CronMask() {
	override fun generatePossibilities(section: CronSection) = section.range

	override fun validate(section: CronSection) = true
}

data class ListCron(val list: List<CronMask>) : CronMask() {
	override fun generatePossibilities(section: CronSection) = list.flatMap { it.generatePossibilities(section) }
		.distinct()
		.sorted()

	override fun validate(section: CronSection) = list.find { !it.validate(section) } == null
}

data class RangeCron(val range: IntRange) : CronMask() {
	override fun generatePossibilities(section: CronSection) = range.filter { it in section.range }

	override fun validate(section: CronSection) = range.find {it !in section.range} == null
}

