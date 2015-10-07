package com.mportal.sipdata;

import org.linphone.core.CallDirection;
import org.linphone.core.ErrorInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.service.SIPCoreManager;

import android.text.TextUtils;

import com.mportal.logger.SIPLogger;

public class SIPCall
{
    private String sipProvider;

    private Object callObject;

    private String callNumber;

    private int callState;

    private int callTransferState = -1;

    private String participantId;

    private long startTimestamp = -1;

    private String errorMessage;

    private String message;

    private ErrorInfo errorInfo;
    
    private String sessionName;

    public String getErrorMessage()
    {
	return errorMessage;
    }

    public void setErrorMessage(String message)
    {
	this.errorMessage = message;
    }

    public void setMessage(String message)
    {
	this.message = message;
    }

    public String getMessage()
    {
	return this.message;
    }

    public int getCallTransferState()
    {
	return callTransferState;
    }

    public void setCallTransferState(int callTransferState)
    {
	this.callTransferState = callTransferState;
    }

    private SIPVideoViewListener videoListener;

    private String paiHeader;

    public SIPCall(String sipProvider, Object callObject)
    {
	this.sipProvider = sipProvider;
	this.callObject = callObject;
    }

    public String getSipProvider()
    {
	return sipProvider;
    }

    public void setSipProvider(String sipProvider)
    {
	this.sipProvider = sipProvider;
    }

    public Object getCallObject()
    {
	return callObject;
    }

    public void setCallObject(Object obj)
    {
	this.callObject = obj;
    }

    public Object getCall()
    {
	return callObject;
    }

    public void setPAIHeader(String paiHeader)
    {
	this.paiHeader = paiHeader;
    }

    public String getPAIHeader()
    {
	return this.paiHeader;
    }

    public void setCallNumber(String callNumber)
    {
	this.callNumber = callNumber;
	if (!TextUtils.isEmpty(this.callNumber))
	{
	    if (this.callNumber.startsWith("+1"))
	    {
		this.callNumber = this.callNumber.substring(2);
	    }

	    int idx = this.callNumber.indexOf("@");
	    if (idx != -1)
	    {
		this.callNumber = this.callNumber.substring(0, idx);
	    }
	    this.callNumber = this.callNumber.split(";")[0];
	}

    }

    public String getCallNumber()
    {
	return this.callNumber;
    }

    public void setCallState(int callState)
    {
	this.callState = callState;
    }

    public int getCallState()
    {
	return this.callState;
    }

    public void terminateCall()
    {
	if (callObject instanceof LinphoneCall && SIPCoreManager.getInstance().getLinphoneCore() != null)
	{
	    SIPLogger.d("HARISH", "callObject : " + callObject);
	    SIPLogger.d("HARISH", "callObject typecast : " + (callObject instanceof LinphoneCall));
	    SIPLogger.d("HARISH", "LinphoneCore : " + SIPCoreManager.getInstance().getLinphoneCore());
	    SIPCoreManager.getInstance().getLinphoneCore().terminateCall((LinphoneCall) callObject);
	}
    }

    public SIPVideoViewListener getVideoListener()
    {
	return videoListener;
    }

    public void setVideoListener(SIPVideoViewListener videoListener)
    {
	this.videoListener = videoListener;
    }

    public boolean isIncomingCall()
    {
	return SIPCoreManager.getInstance().isInComingCall(this);
    }

    private String conferenceId;

    private boolean voiceMailStreaming;

    private boolean replaceContact;

    private String callId;

    private String from;

    private String to;

    public String getConferenceId()
    {
	return conferenceId;
    }

    public void setConferenceId(String conferenceId)
    {
	this.conferenceId = conferenceId;
    }

    public void setVoiceMailStreaming(boolean b)
    {
	this.voiceMailStreaming = b;
    }

    public boolean isVoiceMailStreaming()
    {
	return this.voiceMailStreaming;
    }

    public void setParticipantId(String participantId)
    {
	this.participantId = participantId;
    }

    public String getParticipantId()
    {
	return this.participantId;
    }

    public void setReplaceContactCard(boolean b)
    {
	this.replaceContact = b;
    }

    public boolean canReplaceContact()
    {
	return replaceContact;
    }

    public void setStartTimestamp(long starttimestamp)
    {
	this.startTimestamp = starttimestamp;
    }

    public long getStartTimestamp()
    {
	return this.startTimestamp;
    }

    public CallDirection getCallDirection()
    {
	Object lCall = callObject;
	if (lCall instanceof LinphoneCall)
	{
	    return ((LinphoneCall) lCall).getDirection();
	}
	return null;
    }

    public void setCallId(String callId)
    {
	this.callId = callId;
    }

    public String getCallId()
    {
	return this.callId;
    }

    public void setFrom(String from)
    {
	this.from = from;
    }

    public String getFrom()
    {
	return this.from;
    }

    public void setTo(String to)
    {
	this.to = to;
    }

    public String getTo()
    {
	return this.to;
    }

    public void setErrorInfo(ErrorInfo errorInfo)
    {
	this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo()
    {
	return this.errorInfo;
    }

    /**
     * @return the sessionName
     */
    public String getSessionName()
    {
	return sessionName;
    }

    /**
     * @param sessionName the sessionName to set
     */
    public void setSessionName(String sessionName)
    {
	this.sessionName = sessionName;
    }
}
