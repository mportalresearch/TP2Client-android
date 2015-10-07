package com.mportal.siputil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.mportal.sbc.siplib.R;

public class PreferenceUtil
{
    public static String getString(Context context, int key)
    {
	if (context == null)
	{
	    return null;
	}
	return context.getResources().getString(key);
    }

    public static boolean getPrefBoolean(Context context, int key, boolean value)
    {
	if (context == null)
	{
	    return false;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Resources mR = context.getResources();
	return mPref.getBoolean(mR.getString(key), value);
    }

    public static boolean getPrefBoolean(Context context, String key, boolean value)
    {
	if (context == null)
	{
	    return false;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Resources mR = context.getResources();
	return mPref.getBoolean(key, value);
    }

    public static String getPrefString(Context context, int key, String value)
    {
	if (context == null)
	{
	    return null;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Resources mR = context.getResources();
	return mPref.getString(mR.getString(key), value);
    }

    public static int getPrefInt(Context context, int key, int value)
    {
	if (context == null)
	{
	    return 0;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Resources mR = context.getResources();
	return mPref.getInt(mR.getString(key), value);
    }
    
    public static int getPrefInt(Context context, String key, int value)
    {
	if (context == null)
	{
	    return 0;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	return mPref.getInt(key, value);
    }

    public static String getPrefString(Context context, int key, int value)
    {
	if (context == null)
	{
	    return null;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Resources mR = context.getResources();
	return mPref.getString(mR.getString(key), mR.getString(value));
    }

    public static String getPrefString(Context context, String key, String value)
    {
	if (context == null)
	{
	    return null;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Resources mR = context.getResources();
	return mPref.getString(key, value);
    }

    public static int getPrefExtraAccountsNumber(Context context)
    {
	if (context == null)
	{
	    return 0;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	return mPref.getInt(getString(context, R.string.pref_extra_accounts), 1);
    }

    public static void putPrefString(Context context, String key, String value)
    {
	if (context == null)
	{
	    return;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Editor edit = mPref.edit();
	edit.putString(key, value);
	edit.commit();
    }

    public static void putPrefBoolean(Context context, String key, boolean value)
    {
	if (context == null)
	{
	    return;
	}
	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
	Editor edit = mPref.edit();
	edit.putBoolean(key, value);
	edit.commit();
    }
}
