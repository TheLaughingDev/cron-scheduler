package org.thelaughingdev.cronscheduler

import org.thelaughingdev.cronscheduler.BasicParser.ParserData.ParserSymbol.*

/**
 * Interface for a cron parsing class.
 */
interface CronParser {

	/**
	 * Parses the given cron string.
	 * @return The schedule based off of the given string.
	 */
	fun parseSchedule(strSchedule: String): CronSchedule
}

/**
 * Parser for parsing standard cron schedules.
 * @param cronSchedulerHelper Helper class used during parsing.
 */
class BasicParser(private val cronSchedulerHelper: CronScheduleHelper = CronSchedule.Helper) : CronParser {

	/**
	 * Holds information about the string being parsed and the current parsing position.
	 * @param schedule The cron schedule to parse.
	 */
	private class ParserData(private val schedule: String) {

		/**
		 * Enum containing the symbols recognized by the parser, and the string the symbol matches to.
		 * @param str The string the symbol matches.
		 */
		enum class ParserSymbol(val str: String) {
			HYPHEN("-"), COMMA(","), ASTERISK("*"), SLASH("/"), DIGIT("0123456789"), AT("@"), LOWER_CHAR("abcdefghijklmnopqrstuvwxyz"),
			UPPER_CHAR("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),	WHITE_SPACE(" \t"), NONE("")
		}

		/**
		 * The position the parser is at in the string.
		 */
		var position = 0
			private set

		/**
		 * Returns the symbol for the current character.
		 */
		val symbol get() = if(character.isEmpty()) NONE else ParserSymbol.values().find { character in it.str }

		/**
		 * Returns the current character.
		 */
		val character get() = if(position >= schedule.length) "" else schedule[position].toString()

		/**
		 * Increments the position.
		 */
		fun next(): ParserData {
			position++
			return this
		}

		/**
		 * Reads the string for the given system. Will continue to read until the symbol changes to something else. So
		 * calling this will read an entire string of letters or numbers.
		 * @return The string that was read.
		 */
		private fun readString(symbol: ParserSymbol): String {
			val builder = StringBuilder()

			while(this.symbol == symbol) {
				builder.append(character)
				next()
			}

			return builder.toString()
		}

		/**
		 * Read numbers until the next symbol.
		 * @return The number read.
		 */
		fun readDigits() = readString(DIGIT).toInt()

		/**
		 * Read uppercase characters until the next symbol.
		 * @return The characters.
		 */
		fun readUpper() = readString(UPPER_CHAR)

		/**
		 * Read lowercase characters until the next symbol.
		 * @return The characters.
		 */
		fun readLower() = readString(LOWER_CHAR)

		/**
		 * Read white space characters until the next symbol.
		 * @return The characters.
		 */
		fun readWhiteSpace() = readString(WHITE_SPACE)
	}

	/**
	 * Reads a string of constants either numbers or upper case symbols.
	 * @param data The data being parsed.
	 * @param section The section being parsed.
	 * @return The numeric value of the parsed string.
	 */
	private fun readConstant(data: ParserData, section: Section): Int = when(data.symbol) {
		DIGIT -> data.readDigits()
		UPPER_CHAR -> {
			val constantValue = data.readUpper()
			section.textValues[constantValue] ?: throw CronParseException("Unknown constant $constantValue for section $section", data.position)
		}
		else -> throw CronParseException("Unexpected symbol", data.position)
	}

	/**
	 * Parses special attribute values and returns their schedule. These values represent an entire cron string.
	 * @param data The data being parsed.
	 * @return The schedule.
	 */
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
			else -> throw CronParseException("@$specialAttribute is not a valid expression", data.position)
		}
	}

	/**
	 * Parses a step cron type.
	 * @param data The data being parsed.
	 * @param base The base of the step.
	 * @return The built step cron type.
	 */
	private fun parseStep(data: ParserData, base: ContinuousRangeCron): StepCron = when(data.symbol) {
		DIGIT -> StepCron(base, data.readDigits())
		else ->  throw CronParseException("Step value must be integer. Unexpected symbol ${data.symbol}", data.position)
	}

	/**
	 * Parses a range cron type.
	 * @param data The data being parsed.
	 * @param start The start of the range.
	 * @param section The section being parsed.
	 * @return The built range cron type.
	 */
	private fun parseRange(data: ParserData, start: Int, section: Section): RangeCron = when(data.symbol) {
		DIGIT, UPPER_CHAR -> RangeCron(start..readConstant(data, section))
		else -> throw CronParseException("Unexpected symbol ${data.symbol}", data.position)
	}

	/**
	 * Parses all continuous range cron types.
	 * @param data The data being parsed.
	 * @param section The section being parsed.
	 * @return The continuous range cron type.
	 */
	private fun parseContinuousRangeCron(data: ParserData, section: Section): ContinuousRangeCron = when(data.symbol) {
		ASTERISK -> {
			when(data.next().symbol) {
				WHITE_SPACE, NONE -> AllCron()
				SLASH -> parseStep(data.next(), AllCron())
				else -> throw CronParseException("Unexpected symbol ${data.symbol}", data.position)
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
		else -> throw CronParseException("Unexpected symbol ${data.symbol}", data.position)
	}

	/**
	 * Parses a list cron type.
	 * @param section The section being parsed.
	 * @param data The data being parsed.
	 * @return The list cron type.
	 */
	private fun parseList(list: List<ContinuousRangeCron>, data: ParserData, section: Section): List<ContinuousRangeCron> {
		val cronType = parseContinuousRangeCron(data, section)

		return if(data.symbol == COMMA)
			parseList(list.plusElement(cronType), data.next(), section)
		else
			list.plusElement(cronType)
	}

	/**
	 * Parses a given section.
	 * @param section The section being parsed.
	 * @param data The data being parsed.
	 * @return The section.
	 */
	private fun parseSection(data: ParserData, section: Section): Section {
		data.readWhiteSpace()

		val firstItem = parseContinuousRangeCron(data, section)
		return if(data.symbol == COMMA) section.copy(ListCron(parseList(listOf(firstItem), data.next(), section))) else section.copy(firstItem)
	}

	override fun parseSchedule(strSchedule: String): CronSchedule {

		val data = ParserData(strSchedule.trim())

		if(data.symbol == AT)
			return parseSpecialAttributes(data.next())

		val schedule = CronSchedule(parseSection(data, Second()) as Second,
			parseSection(data.next(), Minute()) as Minute,
			parseSection(data.next(), Hour()) as Hour,
			parseSection(data.next(), DayOfMonth()) as DayOfMonth,
			parseSection(data.next(), Month()) as Month,
			parseSection(data.next(), DayOfWeek()) as DayOfWeek)

		if(data.symbol != NONE)
			throw CronParseException("Extra data after parsing schedule", data.position)

		return schedule
	}
}