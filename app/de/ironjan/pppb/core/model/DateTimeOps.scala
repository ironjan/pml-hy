package de.ironjan.pppb.core.model

import org.joda.time.{DateTime, Weeks}

object DateTimeOps{
implicit class DateTimeOps(dateTime: DateTime) {
  def hourOfDayDouble: Double = dateTime.hourOfDay().get()
  def minuteOfHourDouble: Double = dateTime.minuteOfHour().get
  def dayOfWeekDouble: Double = dateTime.dayOfWeek().get
  def dayOfMonthDouble: Double = dateTime.dayOfMonth().get
  def weekOfMonthDouble: Double = weekOfMonth

  private def weekOfMonth = {
    Weeks.weeksBetween(dateTime.withDayOfMonth(1), dateTime).getWeeks + 1
  }

  def weekOfWeekyearDouble: Double = dateTime.weekOfWeekyear().get

  def toPredictionQuery:Array[Double] = Array(dateTime.hourOfDayDouble,
    dateTime.minuteOfHourDouble,
    dateTime.dayOfWeekDouble,
    dateTime.dayOfMonthDouble,
    dateTime.weekOfMonthDouble,
    dateTime.weekOfWeekyearDouble)
}
}