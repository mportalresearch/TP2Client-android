package com.mportal.sipdata;

import java.util.Vector;

public class SIPRegistrationState
{

    private static Vector<SIPRegistrationState> values = new Vector<SIPRegistrationState>();
    /**
     * None
     */
    public static SIPRegistrationState RegistrationNone = new SIPRegistrationState(0, "RegistrationNone");
    /**
     * In Progress
     */
    public static SIPRegistrationState RegistrationProgress = new SIPRegistrationState(1, "RegistrationProgress");
    /**
     * Ok
     */
    public static SIPRegistrationState RegistrationOk = new SIPRegistrationState(2, "RegistrationOk");
    /**
     * Cleared
     */
    public static SIPRegistrationState RegistrationCleared = new SIPRegistrationState(3, "RegistrationCleared");
    /**
     * Failed
     */
    public static SIPRegistrationState RegistrationFailed = new SIPRegistrationState(4, "RegistrationFailed");

    private final int mValue;
    private final String mStringValue;

    private SIPRegistrationState(int value, String stringValue)
    {
	mValue = value;
	values.addElement(this);
	mStringValue = stringValue;
    }

    public static SIPRegistrationState fromInt(int value)
    {

	for (int i = 0; i < values.size(); i++)
	{
	    SIPRegistrationState state = (SIPRegistrationState) values.elementAt(i);
	    if (state.mValue == value)
		return state;
	}
	throw new RuntimeException("state not found [" + value + "]");
    }

    public static SIPRegistrationState getStateValue(String value)
    {
	for (int i = 0; i < values.size(); i++)
	{
	    SIPRegistrationState state = (SIPRegistrationState) values.elementAt(i);
	    if (state.mStringValue.equalsIgnoreCase(value))
	    {
		return state;
	    }
	}
	return null;
    }

    public String toString()
    {
	return mStringValue;
    }

}
