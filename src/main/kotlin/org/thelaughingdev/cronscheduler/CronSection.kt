package org.thelaughingdev.cronscheduler

enum class CronSection(val range: IntRange, val textValues: Map<String, Int>) {
	SECOND(0..59, mapOf()),
	MINUTE(0..59, mapOf()),
	HOUR(0..23, mapOf()),
	DAY_OF_MONTH(1..31, mapOf()),
	MONTH(1..12, mapOf(Pair("JAN", 1), Pair("FEB", 2), Pair("MAR", 3), Pair("APR", 4),
		Pair("MAY", 5), Pair("JUN", 6), Pair("JUL", 7), Pair("AUG", 8), Pair("SEP", 9),
		Pair("OCT", 10), Pair("NOV", 11), Pair("DEC", 12))),
	DAY_OF_WEEK(0..6, mapOf(Pair("SUN", 0), Pair("MON", 1), Pair("TUE", 2), Pair("WED", 3),
		Pair("THR", 4), Pair("FRI", 5), Pair("SAT", 6)))
}

