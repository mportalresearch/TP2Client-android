package com.mportal.logger;

import org.linphone.service.SIPConstant;

import android.util.Log;

public class SIPLogger
{
    public static void v(String tag, String msg)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.v(tag, msg);
	}
    }

    public static void v(String tag, String msg, Throwable tr)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.v(tag, msg, tr);
	}
    }

    public static void d(String tag, String msg)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.d(tag, msg);
	}
    }

    public static void d(String tag, String msg, Throwable tr)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.d(tag, msg, tr);
	}
    }

    public static void i(String tag, String msg)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.i(tag, msg);
	}
    }

    public static void i(String tag, String msg, Throwable tr)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.i(tag, msg, tr);
	}
    }

    public static void w(String tag, String msg)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.w(tag, msg);
	}
    }

    public static void w(String tag, String msg, Throwable tr)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.w(tag, msg, tr);
	}
    }

    public static void w(String tag, Throwable tr)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.w(tag, tr);
	}
    }

    public static void e(String tag, String msg)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.e(tag, msg);
	}
    }

    public static void e(String tag, String msg, Throwable tr)
    {
	if (SIPConstant.SIP_LOG_ENABLED)
	{
	    Log.e(tag, msg, tr);
	}
    }
}
