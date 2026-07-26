/**
 * 
 */
package com.nagpalaot.ecdra.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.constants.TrueFalseFlag;

/**
 * This utility will be used to convert text values into the appropriate data type.
 * Typically this will be used to convert the text values parsed from the LRDR extract file 
 * but it may be used for parsing similar files.
 * 
 * @author fernando.felixberto
 *
 */
public abstract class TextLineParser {

	private final static Logger log = LoggerFactory.getLogger(TextLineParser.class);

	private static final String NULL_DATE_VALUE = "00000000";	// this is the default placeholder in the text line 
																// for null dates 
	private static final String ABBREV_NULL_DATE_VALUE = "000000";	// this is used to check the first 6 places 
																	// of the default placeholder for date in the text line 
																	// if the first 6 places are 0s, then the date is null
	private static DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	private TextLineParser(){
		
	}
	
	/**
	 * This method will return a null if the textLine parameter is blank or null or 
	 * if the string in the specified location is a blank.
	 * 
	 * @param textLine
	 * @param start
	 * @param length
	 * @return
	 * @throws ParseException
	 */
	public static Integer readInteger(String textLine, int start, int length) throws ParseException{
		Integer result = null;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if(StringUtils.isNotBlank(tempResult)){
					result = Integer.valueOf(tempResult);
				}
				
			} catch (NumberFormatException ex) {
				String msg = "The value " + tempResult + " at position " 
						+ start + " length " + length + " cannot be converted into an Integer";
				log.error(msg);
				throw new NumberFormatException(msg);
			} catch(Exception ex){
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
		}
		return result;
	}
	
	/**
	 * This method will return a null if the textLine parameter is blank or null or 
	 * if the string in the specified location is a blank.
	 * 
	 * @param textLine
	 * @param start
	 * @param length
	 * @return
	 * @throws ParseException
	 */
	public static Long readLong(String textLine, int start, int length) throws ParseException{
		Long result = null;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if(StringUtils.isNotBlank(tempResult)){
					result = Long.valueOf(tempResult);
				}
				
			} catch (NumberFormatException ex) {
				String msg = "The value " + tempResult + " at position " 
						+ start + " length " + length + " cannot be converted into a Long";
				log.error(msg);
				throw new NumberFormatException(msg);
			} catch(Exception ex){
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
		}
		return result;
	}

	/**
	 * This method will return a null if the textLine parameter is blank or null or 
	 * if the string in the specified location is a blank.
	 * 
	 * @param textLine
	 * @param start
	 * @param length
	 * @return
	 * @throws ParseException
	 */
	public static Double readDouble(String textLine, int start, int length) throws ParseException{
		Double result = null;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if(StringUtils.isNotBlank(tempResult)){
					result = Double.valueOf(tempResult);
				}
				
			} catch (NumberFormatException ex) {
				String msg = "The value " + tempResult + " at position " 
						+ start + " length " + length + " cannot be converted into a Double";
				log.error(msg);
				throw new NumberFormatException(msg);
			} catch(Exception ex){
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
		}
		return result;
	}

	/**
	 * This method will return a null if the textLine parameter is blank or null or 
	 * if the string in the specified location is a blank.
	 * 
	 * It will set the scale of the BigDecimal to the value passed in the 
	 * scale parameter.  If the scale parameter is null, it will create the 
	 * BigDecimal with scale not set.
	 * 
	 * @param textLine
	 * @param start
	 * @param length
	 * @param scale
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal readBigDecimal(String textLine, int start, int length, Integer scale) throws ParseException{
		BigDecimal result = null;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if(StringUtils.isNotBlank(tempResult)){
					long tempValue = Long.parseLong(tempResult);
					if(scale == null){
						result = BigDecimal.valueOf(tempValue);
					}
					else{
						result = BigDecimal.valueOf(tempValue, scale.intValue());
					}
				}
				
			} catch (NumberFormatException ex) {
				String msg = "The value " + tempResult + " at position " 
						+ start + " length " + length + " cannot be converted into a BigDecimal";
				log.error(msg);
				throw new NumberFormatException(msg);
			} catch(Exception ex){
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
		}
		return result;
	}
	
	/**
	 * This method will return a null if the textLine parameter is blank or null or 
	 * if the string in the specified location is a blank.
	 * 
	 * It will also return a null if the string in the line location is equal to 
	 * the value of {@link #NULL_DATE_VALUE}.
	 * 
	 * It will convert the String to a Date value based on the format passed in the 
	 * dateFormat parameter.  If the dateFormat parameter is null, it will use the 
	 * default DateFormat df.
	 * 
	 * @param textLine
	 * @param start
	 * @param length
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date readDate(String textLine, int start, int length, DateFormat dateFormat) throws ParseException{
		if(dateFormat == null){
			dateFormat = df;
		}
		Date result = null;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if(StringUtils.isNotBlank(tempResult) && !NULL_DATE_VALUE.equals(tempResult)){
					if(!ABBREV_NULL_DATE_VALUE.equalsIgnoreCase(tempResult.substring(0, 6))){
						result = dateFormat.parse(tempResult);
					}
				}
			} catch (Exception ex) {
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
			
		}
		return result;
	}
	
	public static String readString(String textLine, int start, int length) throws ParseException{
		String result = null;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if(StringUtils.isNotBlank(tempResult)){
					result = tempResult;
				}
			} catch (Exception ex) {
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
			
		}
		return result;
	}
	
	/**
	 * @deprecated since 5.0
	 * @param textLine
	 * @param start
	 * @param length
	 * @return
	 * @throws ParseException
	 */
	public static boolean readYNBoolean(String textLine, int start, int length) throws ParseException{
		boolean result = false;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if("Y".equalsIgnoreCase(tempResult)){
					result = true;
				}
				else if("N".equalsIgnoreCase(tempResult)){
					result = false;
				}
				else{
					throw new ParseException("Cannot convert " + tempResult + " to a true or false value.", start);
				}
			} catch (Exception ex) {
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
		}
		else{
			throw new ParseException("Cannot get a boolean value from blank line", 0);
		}
		return result;
	}

	/**
	 * @deprecated since 5.0
	 * 
	 * @param textLine
	 * @param start
	 * @param length
	 * @return
	 * @throws ParseException
	 */
	public static boolean readTFBoolean(String textLine, int start, int length) throws ParseException{
		boolean result = false;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim();
				if("T".equalsIgnoreCase(tempResult)){
					result = true;
				}
				else if("F".equalsIgnoreCase(tempResult)){
					result = false;
				}
				else{
					throw new ParseException("Cannot convert " + tempResult + " to a true or false value.", start);
				}
			} catch (Exception ex) {
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
		}
		else{
			throw new ParseException("Cannot get a boolean value from blank line", 0);
		}
		return result;
	}
	
	/**
	 * Replacement for both {@link #readTFBoolean(String, int, int)} 
	 * and {@link #readYNBoolean(String, int, int)}
	 * 
	 * This method will return the correct boolean value whether the argument textLine 
	 * uses Y/N or T/F to indiate True and False respectively.  It will not properly 
	 * process true false scheme using numbers (e.g. 1 for true, 0 for false, etc...)
	 * 
	 * @author fernando.felixberto
	 * @since 5.0
	 * @param textLine
	 * @param start
	 * @param length
	 * @return true if textline is T, t, Y, y
	 * 			false if textline is any other character
	 * @throws ParseException
	 */
	public static boolean readBoolean(String textLine, int start, int length) throws ParseException{
		boolean result = false;
		String tempResult = null;
		if(StringUtils.isNotBlank(textLine)){
			try {
				tempResult = textLine.substring(start, start+length).trim().toUpperCase();
				TrueFalseFlag flagValue = TrueFalseFlag.get(tempResult);
				if(flagValue != null){
					result = flagValue.isValue();
				}
				else{
					log.warn("The character " + tempResult + " is not a valid boolean flag, a false was returned");
				}
			} catch (Exception ex) {
				log.error("There was a problem parsing the line at position " + start + " to " + (start+length));
				throw new ParseException(ex.getMessage(), start);
			}
		}
		else{
			throw new ParseException("Cannot get a boolean value from blank line", 0);
		}
		return result;
	}
	
}
