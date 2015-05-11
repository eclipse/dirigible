/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.metrics;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
//import org.joda.time.DateTime;
//import org.joda.time.Period;

public class TimeUtils {
	
//	private static DateTime dateTimeCeiling(DateTime dt, Period p) {
//	    if (p.getYears() != 0) {
//	        return dt.yearOfEra().roundCeilingCopy().minusYears(dt.getYearOfEra() % p.getYears());
//	    } else if (p.getMonths() != 0) {
//	        return dt.monthOfYear().roundCeilingCopy().minusMonths((dt.getMonthOfYear() - 1) % p.getMonths());
//	    } else if (p.getWeeks() != 0) {
//	        return dt.weekOfWeekyear().roundCeilingCopy().minusWeeks((dt.getWeekOfWeekyear() - 1) % p.getWeeks());
//	    } else if (p.getDays() != 0) {
//	        return dt.dayOfMonth().roundCeilingCopy().minusDays((dt.getDayOfMonth() - 1) % p.getDays());
//	    } else if (p.getHours() != 0) {
//	        return dt.hourOfDay().roundCeilingCopy().minusHours(dt.getHourOfDay() % p.getHours());
//	    } else if (p.getMinutes() != 0) {
//	        return dt.minuteOfHour().roundCeilingCopy().minusMinutes(dt.getMinuteOfHour() % p.getMinutes());
//	    } else if (p.getSeconds() != 0) {
//	        return dt.secondOfMinute().roundCeilingCopy().minusSeconds(dt.getSecondOfMinute() % p.getSeconds());
//	    }
//	    return dt.millisOfSecond().roundCeilingCopy().minusMillis(dt.getMillisOfSecond() % p.getMillis());
//	}
	
	public static Date roundCeilingHour(Date date) {
//		DateTime bound = dateTimeCeiling(new DateTime(date.getTime()), Period.hours(1));
//		return new Date(bound.getMillis());
		
		return DateUtils.ceiling(date, Calendar.HOUR);
	}

}
