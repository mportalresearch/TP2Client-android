package com.mportal.sipdata;

import org.linphone.core.ErrorInfo;

public class SIPChat
{
    String callingNumber;

    String chatMsg;

    String recvdTime;

    String recvdDate;

    boolean isSentMessage;

    String windowId;

    String callInfoHeader;

    String sid;

    int chatStatus = -1;

    int chatType;

    boolean compose;

    public int getChatStatus()
    {
	return chatStatus;
    }

    public void setChatStatus(int chatStatus)
    {
	this.chatStatus = chatStatus;
    }

    boolean statusMessage;

    private String senderNumber;

    private ErrorInfo errorInfo;

    public boolean isStatusMessage()
    {
	return statusMessage;
    }

    public void setStatusMessage(boolean statusMessage)
    {
	this.statusMessage = statusMessage;
    }

    public String getSid()
    {
	return sid;
    }

    public void setSid(String sid)
    {
	this.sid = sid;
    }

    public void setWindowId(String windowId)
    {
	this.windowId = windowId;
    }

    public String getWindowId()
    {
	return this.windowId;
    }

    public boolean isSentMessage()
    {
	return isSentMessage;
    }

    public void setSentMessage(boolean isSentMessage)
    {
	this.isSentMessage = isSentMessage;
    }

    public String getCallingNumber()
    {
	return callingNumber;
    }

    public void setCallingNumber(String callingNumber)
    {
	this.callingNumber = callingNumber;
    }

    public void setSenderNumber(String senderNumber)
    {
	this.senderNumber = senderNumber;
    }

    public String getSenderNumber()
    {
	return this.senderNumber;
    }

    public String getChatMsg()
    {
	return chatMsg;
    }

    public void setChatMsg(String chatMsg)
    {
	this.chatMsg = chatMsg;
    }

    public String getRecvdTime()
    {
	return recvdTime;
    }

    public void setRecvdTime(String recvdTime)
    {
	this.recvdTime = recvdTime;
    }

    public String getRecvdDate()
    {
	return recvdDate;
    }

    public void setRecvdDate(String recvdDate)
    {
	this.recvdDate = recvdDate;
    }

    public boolean isComposing()
    {
	return compose;
    }

    public void setComposeStatus(boolean composing)
    {
	this.compose = composing;
    }

    public void setCallInfoHeader(String callInfoHeader)
    {
	this.callInfoHeader = callInfoHeader;
    }

    public String getCallInfoHeader()
    {
	return this.callInfoHeader;
    }

    public void setChatType(int chatType)
    {
	this.chatType = chatType;
    }

    public int getChatType()
    {
	return this.chatType;
    }
    
    public void setErrorInfo(ErrorInfo errorInfo)
    {
	this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo()
    {
	return this.errorInfo;
    }

    @Override
    public boolean equals(Object o)
    {
	SIPChat temp = (SIPChat) o;
	if ((callingNumber != null && callingNumber.equalsIgnoreCase(temp.getCallingNumber())) /*
											        * && (recvdTime != null &&
											        * recvdTime.equalsIgnoreCase(temp.getRecvdTime()))
											        */&& (chatMsg != null && chatMsg.equalsIgnoreCase(temp.getChatMsg())))
	{
	    return true;
	}
	return false;
    }

}
