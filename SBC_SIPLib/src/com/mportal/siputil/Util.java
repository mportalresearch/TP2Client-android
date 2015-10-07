package com.mportal.siputil;

public class Util
{
    public static String MEETME_PREFIX = "";

    public static enum CallMode
    {
	AUDIO_MODE, VIDEO_MODE, CHAT_MODE;
    }

    public static enum CallType
    {
	INCOMING, OUTGOING, MISSED;
    }

    public static enum CallStatus
    {
	NOT_IN_CALL, IS_IN_PROGRESS, IS_IN_CALL_QUEUE, IS_IN_CALL, IS_IN_STREAM, IS_IN_HOLD, CALL_ENDED;
    }

    public static enum WindowCallType
    {
	SINGLE_CALL, CONFERENCE_CALL;
    }

    public static int parseInt(String aa, String strdefaultValue)
    {
	int defaultValue = 0;
	try
	{
	    defaultValue = Integer.parseInt(strdefaultValue);
	}
	catch (Exception e)
	{

	}
	return parseInt(aa, defaultValue);
    }

    public static int parseInt(String aa, int defaultValue)
    {
	if (aa == null || (aa != null && aa.trim().length() == 0))
	{
	    return defaultValue;
	}

	int ret = defaultValue;
	try
	{
	    ret = Integer.parseInt(aa);
	}
	catch (Exception e)
	{

	}
	return ret;
    }

    public static int parseInt(String aa)
    {
	return parseInt(aa, 0);
    }

    public static String removePrefixCallNumber(String callNumber)
    {
	if (callNumber == null || callNumber.length() == 0)
	{
	    return callNumber;
	}
	String[] tempNumber = callNumber.split("@");
	if (tempNumber.length > 0)
	{
	    callNumber = tempNumber[0];
	}
	if (callNumber.startsWith("+1"))
	{
	    callNumber = callNumber.substring(2);
	}
	callNumber = callNumber.split(";")[0];

	return callNumber;
    }

}
