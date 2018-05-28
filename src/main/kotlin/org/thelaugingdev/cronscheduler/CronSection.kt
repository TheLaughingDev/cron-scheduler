package org.thelaugingdev.cronscheduler

enum class CronSection(val range: IntRange) {
	SECOND(0..59), MINUTE(0..59), HOUR(0..23), DAY_OF_MONTH(1..31), MONTH(1..12), YEAR(0..9999), DAY_OF_WEEK(0..6)
}