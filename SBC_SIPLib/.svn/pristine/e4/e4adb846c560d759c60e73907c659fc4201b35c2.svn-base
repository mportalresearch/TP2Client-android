package org.linphone.service;

import android.os.Messenger;

import com.mportal.sipdata.SIPCall;

public class SIPCallback
{
    private Messenger messenger;

    private String windowId;

    private String address;

    private SIPCall callInfo;

    private String participantId;

    SIPCallback(Messenger messenger, String windowId, String address)
    {
	this.messenger = messenger;
	this.windowId = windowId;
	this.address = address;
    }

    Messenger getMessenger()
    {
	return messenger;
    }

    void setMessanger(Messenger messenger)
    {
	this.messenger = messenger;
    }

    String getWindowId()
    {
	return windowId;
    }

    String getCallNumber()
    {
	return address;
    }

    public SIPCall getCallInfo()
    {
	return callInfo;
    }

    public void setCallInfo(SIPCall callInfo)
    {
	this.callInfo = callInfo;
	if(this.callInfo != null)
	{
	    this.callInfo.setParticipantId(participantId);
	}
    }

    public void setParticipantId(String participantId)
    {
	this.participantId = participantId;
	if(callInfo != null)
	{
	    this.callInfo.setParticipantId(participantId);
	}
    }
    
    public String getParticipantId()
    {
	return this.participantId;
    }
}
