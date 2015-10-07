package com.mportal.sipdata;

import java.util.Vector;

public class SIPChatState
{
    static private Vector<SIPChatState> values = new Vector<SIPChatState>();

    private final int mValue;

    public final int value()
    {
	return mValue;
    }

    private final String mStringValue;
    /**
     * Idle
     */
    public final static SIPChatState Idle = new SIPChatState(0, "Idle");
    /**
     * Incoming call received.
     */
    public final static SIPChatState InProgress = new SIPChatState(1, "InProgress");
    /**
     * Outgoing call initialiazed.
     */
    public final static SIPChatState Delivered = new SIPChatState(2, "Delivered");
    /**
     * Outgoing call in progress.
     */
    public final static SIPChatState NotDelivered = new SIPChatState(3, "NotDelivered");

    private SIPChatState(int value, String stringValue)
    {
	mValue = value;
	values.addElement(this);
	mStringValue = stringValue;
    }

    public static SIPChatState fromInt(int value)
    {

	for (int i = 0; i < values.size(); i++)
	{
	    SIPChatState state = (SIPChatState) values.elementAt(i);
	    if (state.mValue == value)
		return state;
	}
	throw new RuntimeException("state not found [" + value + "]");
    }

    public String toString()
    {
	return mStringValue;
    }

    public int toInt()
    {
	return mValue;
    }

}
