package com.mportal.sipdata;

import java.util.Vector;

public class SIPCallState
{

    static private Vector<SIPCallState> values = new Vector<SIPCallState>();
    private final int mValue;

    public final int value()
    {
	return mValue;
    }

    private final String mStringValue;
    /**
     * Idle
     */
    public final static SIPCallState Idle = new SIPCallState(0, "Idle");
    /**
     * Incoming call received.
     */
    public final static SIPCallState IncomingReceived = new SIPCallState(1, "IncomingReceived");
    /**
     * Outgoing call initialiazed.
     */
    public final static SIPCallState OutgoingInit = new SIPCallState(2, "OutgoingInit");
    /**
     * Outgoing call in progress.
     */
    public final static SIPCallState OutgoingProgress = new SIPCallState(3, "OutgoingProgress");
    /**
     * Outgoing call ringing.
     */
    public final static SIPCallState OutgoingRinging = new SIPCallState(4, "OutgoingRinging");
    /**
     * Outgoing call early media
     */
    public final static SIPCallState OutgoingEarlyMedia = new SIPCallState(5, "OutgoingEarlyMedia");
    /**
     * Connected
     */
    public final static SIPCallState Connected = new SIPCallState(6, "Connected");
    /**
     * Streams running
     */
    public final static SIPCallState StreamsRunning = new SIPCallState(7, "StreamsRunning");
    /**
     * Pausing
     */
    public final static SIPCallState Pausing = new SIPCallState(8, "Pausing");
    /**
     * Paused
     */
    public final static SIPCallState Paused = new SIPCallState(9, "Paused");
    /**
     * Resuming
     */
    public final static SIPCallState Resuming = new SIPCallState(10, "Resuming");
    /**
     * Refered
     */
    public final static SIPCallState Refered = new SIPCallState(11, "Refered");
    /**
     * Error
     */
    public final static SIPCallState Error = new SIPCallState(12, "Error");
    /**
     * Call end
     */
    public final static SIPCallState CallEnd = new SIPCallState(13, "CallEnd");

    /**
     * Paused by remote
     */
    public final static SIPCallState PausedByRemote = new SIPCallState(14, "PausedByRemote");

    /**
     * The call's parameters are updated, used for example when video is asked by remote
     */
    public static final SIPCallState CallUpdatedByRemote = new SIPCallState(15, "UpdatedByRemote");

    /**
     * We are proposing early media to an incoming call
     */
    public static final SIPCallState CallIncomingEarlyMedia = new SIPCallState(16, "IncomingEarlyMedia");

    /**
     * The remote accepted the call update initiated by us
     */
    public static final SIPCallState CallUpdating = new SIPCallState(17, "Updating");

    /**
     * The call object is now released.
     */
    public static final SIPCallState CallReleased = new SIPCallState(18, "Released");

    private SIPCallState(int value, String stringValue)
    {
	mValue = value;
	values.addElement(this);
	mStringValue = stringValue;
    }

    public static SIPCallState fromInt(int value)
    {

	for (int i = 0; i < values.size(); i++)
	{
	    SIPCallState state = (SIPCallState) values.elementAt(i);
	    if (state.mValue == value)
		return state;
	}
	throw new RuntimeException("state not found [" + value + "]");
    }

    public static SIPCallState getStateValue(String value)
    {
	for (int i = 0; i < values.size(); i++)
	{
	    SIPCallState state = (SIPCallState) values.elementAt(i);
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
