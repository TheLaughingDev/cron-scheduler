package org.thelaugingdev.cronscheduler

sealed class CronException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)

class CronParseException(msg: String, cause: Throwable? = null) : CronException(msg, cause)

class CronValidationException(msg: String, cause: Throwable? = null) : CronException(msg, cause) {
	constructor(value: Any, section: CronSection) : this("$value is not valid for $section")
}