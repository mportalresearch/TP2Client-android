package org.linphone.service;

public enum SIPCoreState
{
    SIPCORE_NONE(0), SIPCORE_INIT_SETTING_PARAMS(1), SIPCORE_INIT_STARTED(2), SIPCORE_INIT_COMPLETED(3), SIPCORE_REFRESH_SYSTEM_CONFIG_START(4), SIPCORE_REFRESH_SYSTEM_CONFIG_COMPLETED(
	    5), SIPCORE_SYSTEM_CONFIG_START(6), SIPCORE_SYSTEM_CONFIG_COMPLETED(7), SIPCORE_READY(8);

    private int mValue;

    private SIPCoreState(int value)
    {
	mValue = value;
    }

    public static SIPCoreState fromInt(int value)
    {
	switch (value)
	{
	case 0:
	    return SIPCORE_NONE;
	case 1:
	    return SIPCORE_INIT_SETTING_PARAMS;
	case 2:
	    return SIPCORE_INIT_STARTED;
	case 3:
	    return SIPCORE_INIT_COMPLETED;
	case 4:
	    return SIPCORE_REFRESH_SYSTEM_CONFIG_START;
	case 5:
	    return SIPCORE_REFRESH_SYSTEM_CONFIG_COMPLETED;
	case 6:
	    return SIPCORE_SYSTEM_CONFIG_START;
	case 7:
	    return SIPCORE_SYSTEM_CONFIG_COMPLETED;
	case 8:
	    return SIPCORE_READY;
	default:
	    return SIPCORE_NONE;
	}
    }

    private String getString()
    {
	switch (mValue)
	{
	case 0:
	    return "SIPCORE_NONE";
	case 1:
	    return "SIPCORE_INIT_SETTING_PARAMS";
	case 2:
	    return "SIPCORE_INIT_STARTED";
	case 3:
	    return "SIPCORE_INIT_COMPLETED";
	case 4:
	    return "SIPCORE_REFRESH_SYSTEM_CONFIG_START";
	case 5:
	    return "SIPCORE_REFRESH_SYSTEM_CONFIG_COMPLETED";
	case 6:
	    return "SIPCORE_SYSTEM_CONFIG_START";
	case 7:
	    return "SIPCORE_SYSTEM_CONFIG_COMPLETED";
	case 8:
	    return "SIPCORE_READY";
	default:
	    return "SIPCORE_NONE";
	}
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
