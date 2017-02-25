package de.ironjan.pppb.core.model

import org.joda.time.{DateTime, Weeks}

object DateTimeHelper {

  implicit class DateTimeOps(dateTime: DateTime) {
    def toPredictionQuery: Array[Double] = Array(dateTime.hourOfDayDouble,
      dateTime.minuteOfHourDouble,
      dateTime.dayOfWeekDouble,
      dateTime.dayOfMonthDouble,
      dateTime.weekOfMonthDouble,
      dateTime.weekOfWeekyearDouble)

    def hourOfDayDouble: Double = dateTime.hourOfDay().get()

    def minuteOfHourDouble: Double = dateTime.minuteOfHour().get

    def dayOfWeekDouble: Double = dateTime.dayOfWeek().get

    def dayOfMonthDouble: Double = dateTime.dayOfMonth().get

    def weekOfMonthDouble: Double = weekOfMonth

    def weekOfWeekyearDouble: Double = dateTime.weekOfWeekyear().get

    def weekOfMonth = {
      Weeks.weeksBetween(dateTime.withDayOfMonth(1), dateTime).getWeeks + 1
    }
  }

}