package org.thelaugingdev.cronscheduler

import org.thelaugingdev.cronscheduler.ParserSymbol.*
import org.thelaugingdev.cronscheduler.CronSection.*

interface CronParser {
	fun parseSchedule(strSchedule: String): CronSchedule
}

class BasicParser(private val parserDataFactory: ParserData.Factory) : CronParser {

	private fun parseSpecialAttributes(data: ParserData): CronSchedule {
		val specialAttribute = data.readLower()

		return when(specialAttribute) {
			"yearly", "annually" -> CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0), SingleCron(HOUR, 0), SingleCron(DAY_OF_MONTH, 1), SingleCron(MONTH, 1))
			"monthly" -> CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0), SingleCron(HOUR, 0), SingleCron(DAY_OF_MONTH, 1))
			"weekly" -> CronSchedule(second = SingleCron(SECOND, 0), minute = SingleCron(MINUTE, 0), hour = SingleCron(HOUR, 0), dayOfWeek = SingleCron(DAY_OF_WEEK, 0))
			"daily" -> CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0), SingleCron(HOUR, 0))
			"hourly" -> CronSchedule(SingleCron(SECOND, 0), SingleCron(MINUTE, 0))
			else -> throw CronParseException("@$specialAttribute is not a valid expression.")
		}
	}

	private fun parseStep(data: ParserData, type: ContinuousRangeCron) = if(data.next().symbol == DIGIT) {
		val i = data.readDigits()
		StepCron(type, i)
	}
	else
		throw CronParseException("Exception while parsing step.")

	private fun parseRange(data: ParserData, start: SingleCron) = if(data.next().symbol == DIGIT) {
		val i = data.readDigits()
		RangeCron(start, SingleCron(start.section, i))
	}
	else
		throw CronParseException("Exception while parsing range.")

	private fun parseContinuousRangeCron(section: CronSection, data: ParserData): ContinuousRangeCron = when(data.symbol) {
		ASTERISK -> {
			val allCron = AllCron(section)
			when(data.next().symbol) {
				SLASH -> parseStep(data.next(), allCron)
				else -> throw CronParseException("")
			}
		}
		DIGIT -> {
			val singleCron = SingleCron(section, data.readDigits())
			when(data.symbol) {
				HYPHEN -> {
					val range = parseRange(data.next(), singleCron)
					if(data.symbol == SLASH)
						parseStep(data, range)
					else
						range
				}
				SLASH -> parseStep(data.next(), singleCron)
				else -> singleCron
			}
		}
		else -> throw CronParseException("")
	}

	private fun parseSection(section: CronSection, data: ParserData): CronType {
		data.readWhiteSpace()

		val cronType = parseContinuousRangeCron(section, data)

		if(data.symbol == COMMA) {
			val list = mutableListOf(cronType)

			while(data.symbol == COMMA)
				list.add(parseContinuousRangeCron(section, data))

			return ListCron(section, list)
		}

		return cronType
	}

	override fun parseSchedule(strSchedule: String): CronSchedule {

		val data = parserDataFactory.create(strSchedule)

		if(data.symbol == AT)
			return parseSpecialAttributes(data.next())

		val schedule = CronSchedule(parseSection(SECOND, data), parseSection(MINUTE, data.next()), parseSection(HOUR, data.next()),
			parseSection(DAY_OF_MONTH, data.next()), parseSection(MONTH, data.next()), parseSection(DAY_OF_WEEK, data.next()))

		if(data.symbol != NONE)
			throw CronParseException("Extra data after parsing schedule.")

		return schedule
	}

}

enum class ParserSymbol(val str: String) {
	HYPHEN("*"), COMMA(","), ASTERISK("*"), SLASH("/"), DIGIT("0123456789"), AT("@"), LOWER_CHAR("abcdefghijklmnopqrstuvwxyz"),
	UPPER_CHAR("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),	WHITE_SPACE(" \t"), NONE("")
}

class ParserData(private val schedule: String) {

	companion object Factory {
		fun create(schedule: String) = ParserData(schedule)
	}

	private var position = 0

	val symbol get() = ParserSymbol.values().find { character in it.str }

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