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

    /**
      *
      * @return (hod, moh, dow, dom, wom, woy)
      */
    def explode ={
      val hourOfDay = dateTime.getHourOfDay
      val minuteOfHour = dateTime.getMinuteOfHour
      val dayOfWeek = dateTime.getDayOfWeek
      val dayOfMonth = dateTime.getDayOfMonth
      val weekOfMonth = dateTime.weekOfMonth
      val weekOfYear = dateTime.getWeekOfWeekyear
      (hourOfDay, minuteOfHour, dayOfWeek, dayOfMonth, weekOfMonth, weekOfYear)
    }

    def isLessThan1DayOld = dateTime.isAfter(new DateTime().minusDays(1))
    def isLessThan2DaysOld = dateTime.isAfter(new DateTime().minusDays(2))
    def isLessThan1WeekOld = dateTime.isAfter(new DateTime().minusWeeks(1))
    def isLessThan1MonthOld = dateTime.isAfter(new DateTime().minusMonths(1))

  }


}