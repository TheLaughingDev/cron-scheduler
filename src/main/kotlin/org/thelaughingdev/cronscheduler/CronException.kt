package org.thelaughingdev.cronscheduler

/**
 * Core cron exception class.
 */
sealed class CronException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)

/**
 * Exception thrown during data parsing.
 */
class CronParseException(msg: String, position: Int, cause: Throwable? = null) : CronException(msg + ": column [$position]", cause)

/**
 * Exception thrown during validation.
 */
class CronValidationException(msg: String, cause: Throwable? = null) : CronException(msg, cause)
