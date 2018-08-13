package org.thelaughingdev.cronscheduler

sealed class CronException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)

class CronParseException(msg: String, data: ParserData, cause: Throwable? = null) : CronException(msg + ": column [${data.position}]", cause)

class CronValidationException(msg: String, cause: Throwable? = null) : CronException(msg, cause)
