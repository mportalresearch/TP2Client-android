package org.linphone.service;

public enum SIPOPState
{
    SIPOP_NONE(0), SIPOP_DEACTIVATION(1), SIPOP_LOGOUT(2), SIPOP_OFFLINE(3);

    private int mValue;

    private SIPOPState(int value)
    {
	mValue = value;
    }

    public static SIPOPState fromInt(int value)
    {
	switch (value)
	{
	case 0:
	    return SIPOP_NONE;
	case 1:
	    return SIPOP_DEACTIVATION;
	case 2:
	    return SIPOP_LOGOUT;
	case 3:
	    return SIPOP_OFFLINE;
	}
	return SIPOP_NONE;
    }

    private String getString()
    {
	switch (mValue)
	{
	case 0:
	    return "SIPOP_NONE";
	case 1:
	    return "SIPOP_LOGOUT";
	case 2:
	    return "SIPOP_DEACTIVATED";
	case 3:
	    return "SIPOP_OFFLINE";
	}
	return "";
    }

    public int getValue()
    {
	return mValue;
    }

    @Override
    public String toString()
    {
	return getString();
    }
}
