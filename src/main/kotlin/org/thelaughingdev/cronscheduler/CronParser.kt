package org.thelaughingdev.cronscheduler

import org.thelaughingdev.cronscheduler.ParserData.ParserSymbol.*

interface CronParser {
	fun parseSchedule(strSchedule: String): CronSchedule
}

class BasicParser(private val cronSchedulerHelper: CronScheduleHelper = CronSchedule.Helper) : CronParser {

	private fun readConstant(data: ParserData, section: Section): Int = when(data.symbol) {
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

	private fun parseStep(data: ParserData, base: ContinuousRangeCron): StepCron = when(data.symbol) {
		DIGIT -> StepCron(base, data.readDigits())
		else ->  throw CronParseException("Step value must be integer. Unexpected symbol ${data.symbol}", data)
	}

	private fun parseRange(data: ParserData, start: Int, section: Section): RangeCron = when(data.symbol) {
		DIGIT, UPPER_CHAR -> RangeCron(start..readConstant(data, section))
		else -> throw CronParseException("Unexpected symbol ${data.symbol}", data)
	}

	private fun parseContinuousRangeCron(data: ParserData, section: Section): ContinuousRangeCron = when(data.symbol) {
		ASTERISK -> {
			when(data.next().symbol) {
				WHITE_SPACE, NONE -> AllCron()
				SLASH -> parseStep(data.next(), AllCron())
				else -> throw CronParseException("Unexpected symbol ${data.symbol}", data)
			}
		}
		DIGIT, UPPER_CHAR -> {
			val value = readConstant(data, section)
			when(data.symbol) {
				HYPHEN -> {
					val range = parseRange(data.next(), value, section)
					if(data.symbol == SLASH)
						parseStep(data.next(), range)
					else
						range
				}
				SLASH -> parseStep(data.next(), SingleCron(value))
				else -> SingleCron(value)
			}
		}
		else -> throw CronParseException("Unexpected symbol ${data.symbol}", data)
	}

	private fun parseList(list: List<ContinuousRangeCron>, section: Section, data: ParserData): List<ContinuousRangeCron> {
		val cronType = parseContinuousRangeCron(data, section)

		return if(data.symbol == COMMA)
			parseList(list.plusElement(cronType), section, data.next())
		else
			list.plusElement(cronType)
	}

	private fun parseSection(section: Section, data: ParserData): Section {
		data.readWhiteSpace()

		val firstItem = parseContinuousRangeCron(data, section)
		return if(data.symbol == COMMA) section.copy(ListCron(parseList(listOf(firstItem), section, data.next()))) else section.copy(firstItem)
	}

	override fun parseSchedule(strSchedule: String): CronSchedule {

		val data = ParserData.create(strSchedule.trim())

		if(data.symbol == AT)
			return parseSpecialAttributes(data.next())

		val schedule = CronSchedule(parseSection(Second(), data) as Second,
			parseSection(Minute(), data.next()) as Minute,
			parseSection(Hour(), data.next()) as Hour,
			parseSection(DayOfMonth(), data.next()) as DayOfMonth,
			parseSection(Month(), data.next()) as Month,
			parseSection(DayOfWeek(), data.next()) as DayOfWeek)

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