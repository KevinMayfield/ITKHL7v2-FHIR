package com.openMap1.mapper.FHIRConversions;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * class of generally useful FHIR property conversion methods, 
 * to be inherited or used by specific FHIR converter class for specific databases or XMLs.
 * 
 * @author Robert
 *
 */

public class FHIRConverters {
	
	/**
	 * convert an HL7 Version 2 date like '19750103'  to a FHIR date  '1975:01:03'
	 * @param lookup
	 * @param v2Date
	 * @return
	 */
	static public String date_V2_to_FHIR(Hashtable<?,?> lookup, String v2Date)
	{
		String fhirDate = "";
		if (v2Date == null) {}
		else if (v2Date.length() != 8) {}
		else
		{
			String year = v2Date.substring(0,4);
			String month = v2Date.substring(4,6);
			String day = v2Date.substring(6,8);
			fhirDate = year + "-" + month + "-" + day;
		}
		
		return fhirDate;
	}
	
	
	/**
	 * truncate a dateTime such as '1976:04:15 04:05:06Z' to a pure date '1976:04:15'
	 * for FHIR primitive type DateAndTime
	 * - 10 characters and no time zone
	 * @param lookup
	 * @param dateTime
	 * @return
	 */
	static public String truncateToDate(Hashtable<?,?> lookup, String dateTime)
	{
		return dateTime.substring(0,10);
	}

	
	/**
	 * add a duration in minutes to a time, assuming it does not run over midnight
	 * (most appointments do not)
	 * @param startTime
	 * @param minutes
	 * @return startTime string with minutes added
	 */
	static String addMinutes(String startTime, String minutes) 
	{
		int hours = 0;
		int mins = 0;
		int secs = 0;
		int duration = 0;
		String hourString = "00";
		String minString = "00";
		String secString = "00";
		try
		{
			StringTokenizer st = new StringTokenizer(startTime,":");
			if (st.countTokens() > 3) throw new Exception("Too many time units in " + startTime);
			hours = new Integer(st.nextToken()).intValue();
			if (st.hasMoreTokens()) mins = new Integer(st.nextToken()).intValue();
			if (st.hasMoreTokens()) secs = new Integer(st.nextToken()).intValue();
			duration  = new Integer(minutes).intValue();
			
			int newMins = mins + duration;
			int addHours = newMins/60;
			hours = hours + addHours;
			mins = newMins - 60*addHours;
			
			hourString = new Integer(hours).toString();
			if (hours < 10) hourString = "0" + hourString;
			minString = new Integer(mins).toString();
			if (mins < 10) minString = "0" + minString;
			secString = new Integer(secs).toString();
			if (secs < 10) secString = "0" + secString;
		}
		catch (Exception ex) {message("Time error: " + ex.getMessage() + " when adding " + minutes + " to " + startTime);}
		
		return (hourString + ":" + minString + ":" + secString);
	}
	
	/**
	 * Used for in-conversions to make a unique fhir id for a resource from two database fields
	 * @param lookup
	 * @param separator separator character, e.g. '_'
	 * @param first first field to concatenate
	 * @param second second field to concatenate
	 * @return
	 */
	static public String concatenate2(Hashtable<?,?> lookup, String separator, String first, String second)
	{
		return (first + separator + second);
	}
	
	/**
	 * Used for in-conversions to make a unique fhir id for a resource from three database fields
	 * @param lookup
	 * @param separator separator character, e.g. '_'
	 * @param first first field to concatenate
	 * @param second second field to concatenate
	 * @param third third field to concatenate
	 * @return
	 */
	static public String concatenate3(Hashtable<?,?> lookup, String separator, String first, String second, String third)
	{
		return (first + separator + second + separator + third);
	}
	
	/**
	 * 
	 * @param s
	 */
	static void message(String s) {System.out.println(s);}
	
	/**
	 * compute a FHIR id (a UUID) , or use one previously stored in the same transform for the same key value
	 * @return
	 */
	static public String patient_Id(Hashtable<String,String> values, String sourceValue)
	{
		String id = values.get(sourceValue);
		if (id == null)
		{
		       id = UUID.randomUUID().toString().toLowerCase();
		       values.put(sourceValue, id);			
		}
 		return id;
	}
	
	/**
	 * Normalise case for names and addresses
	 * @param lookup unused
	 * @param rawString
	 * @return all words in the string with initial upper case, all the rest lower case,
	 * and single spaces between words
	 */
	static public String normaliseCase(Hashtable<?,?> lookup, String rawString)
	{
		String result = "";
		
		StringTokenizer words = new StringTokenizer(rawString," ");
		
		while (words.hasMoreTokens())
		{
			String s = words.nextToken();
			result = result + s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
	    	if (words.hasMoreTokens()) result = result + " ";
		}
		
		return result;
	}

}
