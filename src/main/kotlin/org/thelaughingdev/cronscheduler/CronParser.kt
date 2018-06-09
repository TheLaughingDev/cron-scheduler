package org.thelaughingdev.cronscheduler

import org.thelaughingdev.cronscheduler.ParserData.ParserSymbol.*
import org.thelaughingdev.cronscheduler.CronSection.*

interface CronParser {
	fun parseSchedule(strSchedule: String): CronSchedule
}

class BasicParser(private val cronSchedulerHelper: CronScheduleHelper = CronSchedule.Helper) : CronParser {

	private fun readConstant(data: ParserData, section: CronSection): Int = when(data.symbol) {
		DIGIT -> data.readDigits()
		UPPER_CHAR -> {
			val constantValue = data.readUpper()
			section.textValues[constantValue] ?: throw CronParseException("Unknown constant $constantValue for section $section", data)
		}
		else -> throw CronParseException("Unexpected symbol", data)
	}

	private fun parseSpecialAttributes(data: ParserData): CronSchedule {
		val specialAttribute = data.readLower()

		return when(specialAttribute) {
			"yearly", "annually" -> cronSchedulerHelper.YEARLY
			"monthly" -> cronSchedulerHelper.MONTHLY
			"weekly" -> cronSchedulerHelper.WEEKLY
			"daily" -> cronSchedulerHelper.DAILY
			"hourly" -> cronSchedulerHelper.HOURLY
			"reboot" -> cronSchedulerHelper.now()
			"now" -> cronSchedulerHelper.now()
			else -> throw CronParseException("@$specialAttribute is not a valid expression", data)
		}
	}

	private fun parseStep(data: ParserData, type: ContinuousRangeCron) = if(data.symbol == DIGIT) {
		StepCron(type, data.readDigits())
	}
	else
		throw CronParseException("Step value must be integer. Unexpected symbol ${data.symbol}", data)

	private fun parseRange(data: ParserData, start: SingleCron) = when(data.symbol) {
		DIGIT, UPPER_CHAR -> RangeCron(start, SingleCron(start.section, readConstant(data, start.section)))
		else -> throw CronParseException("Unexpected symbol ${data.symbol}", data)
	}

	private fun parseContinuousRangeCron(section: CronSection, data: ParserData): ContinuousRangeCron = when(data.symbol) {
		ASTERISK -> {
			val allCron = AllCron(section)
			when(data.next().symbol) {
				WHITE_SPACE, NONE -> allCron
				SLASH -> parseStep(data.next(), allCron)
				else -> throw CronParseException("Unexpected symbol ${data.symbol}", data)
			}
		}
		DIGIT -> {
			val singleCron = SingleCron(section, readConstant(data, section))
			when(data.symbol) {
				HYPHEN -> {
					val range = parseRange(data.next(), singleCron)
					if(data.symbol == SLASH)
						parseStep(data.next(), range)
					else
						range
				}
				SLASH -> parseStep(data.next(), singleCron)
				else -> singleCron
			}
		}
		else -> throw CronParseException("Unexpected symbol ${data.symbol}", data)
	}

	private fun parseSection(section: CronSection, data: ParserData): CronType {
		data.readWhiteSpace()

		val cronType = parseContinuousRangeCron(section, data)

		return if(data.symbol == COMMA)	ListCron(section, parseList(listOf(cronType), section, data.next())) else cronType
	}

	private fun parseList(list: List<ContinuousRangeCron>, section: CronSection, data: ParserData): List<ContinuousRangeCron> {
		val cronType = parseContinuousRangeCron(section, data)

		return if(data.symbol == COMMA)
			parseList(list.plusElement(cronType), section, data.next())
		else
			list.plusElement(cronType)
	}

	override fun parseSchedule(strSchedule: String): CronSchedule {

		val data = ParserData.create(strSchedule)

		if(data.symbol == AT)
			return parseSpecialAttributes(data.next())

		val schedule = CronSchedule(parseSection(SECOND, data), parseSection(MINUTE, data.next()), parseSection(HOUR, data.next()),
			parseSection(DAY_OF_MONTH, data.next()), parseSection(MONTH, data.next()), parseSection(YEAR, data.next()), parseSection(DAY_OF_WEEK, data.next()))

		if(data.symbol != NONE)
			throw CronParseException("Extra data after parsing schedule", data)

		return schedule
	}

}

class ParserData(private val schedule: String) {

	enum class ParserSymbol(val str: String) {
		HYPHEN("-"), COMMA(","), ASTERISK("*"), SLASH("/"), DIGIT("0123456789"), AT("@"), LOWER_CHAR("abcdefghijklmnopqrstuvwxyz"),
		UPPER_CHAR("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),	WHITE_SPACE(" \t"), NONE("")
	}

	companion object Factory {
		fun create(schedule: String) = ParserData(schedule)
	}

	var position = 0
	private set

	val symbol get() = if(character.isEmpty()) NONE else ParserSymbol.values().find { character in it.str }

	val character get() = if(position >= schedule.length) "" else schedule[position].toString()

	fun next(): ParserData {
		position++
		return this
	}

	private fun readString(symbol: ParserSymbol): String {
		val builder = StringBuilder()

		while(this.symbol == symbol) {
			builder.append(character)
			next()
		}

		return builder.toString()
	}

	fun readDigits() = readString(DIGIT).toInt()

	fun readUpper() = readString(UPPER_CHAR)

	fun readLower() = readString(LOWER_CHAR)

	fun readWhiteSpace() = readString(WHITE_SPACE)
}