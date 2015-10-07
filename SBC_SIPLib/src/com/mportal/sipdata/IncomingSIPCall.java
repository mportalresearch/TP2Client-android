package com.mportal.sipdata;

public class IncomingSIPCall extends SIPCall
{

    private String callerName;

    private String windowId;

    private String displayCallNumber;
    
    private boolean isVideoEnabled;

    public boolean isVideoEnabled()
    {
        return isVideoEnabled;
    }

    public void setVideoEnabled(boolean isVideoEnabled)
    {
        this.isVideoEnabled = isVideoEnabled;
    }

    public String getDisplayCallNumber()
    {
	return displayCallNumber;
    }

    public void setDisplayCallNumber(String displayName)
    {
	this.displayCallNumber = displayName;
    }

    public String getWindowId()
    {
	return windowId;
    }

    public void setWindowId(String windowId)
    {
	this.windowId = windowId;
    }

    public String getCallerName()
    {
	return callerName;
    }

    public void setCallerName(String callerName)
    {
	this.callerName = callerName;
    }

    private String callInfoHeaderUrl;
    private boolean intercomCall;

    private String remoteContact;
    private String remoteUserName;
    private String remoteDisplayName;
    public String getCallInfoHeaderUrl()
    {
	return callInfoHeaderUrl;
    }

    public void setCallInfoHeaderUrl(String callInfoHeaderUrl)
    {
	this.callInfoHeaderUrl = callInfoHeaderUrl;
    }

    public IncomingSIPCall(String sipProvider, Object callObject)
    {
	super(sipProvider, callObject);
    }

    public void setInterComCall(boolean intercomCall)
    {
	this.intercomCall = intercomCall;
    }

    public boolean isInterComCall()
    {
	return intercomCall;
    }

    public void terminateIntercomCall()
    {
	this.terminateCall();
    }

    public void setRemoteContact(String remoteAddress)
    {
	this.remoteContact = remoteAddress;
    }
    public String getRemoteContact()
    {
	return this.remoteContact;
    }
    public void setRemoteUserName(String userName)
    {
	this.remoteUserName = userName;
    }
    public String getRemoteUserName()
    {
	return this.remoteUserName;
    }
    public void setRemoteDisplayName(String displayName)
    {
	this.remoteDisplayName = displayName;
    }
    public String getRemoteDisplayName()
    {
	return this.remoteDisplayName;
    }
}
