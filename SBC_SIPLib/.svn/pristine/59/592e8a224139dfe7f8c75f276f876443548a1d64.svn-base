package com.mportal.handlemanagement.model;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "conv", strict = false)
public class Conversation
{

    @Attribute(required = false)
    private String id;

    @Attribute(required = false)
    private String handle;

    @Attribute(required = false)
    private String state;

    @ElementList(name = "participants", required = false)
    private ArrayList<Participant> participantList;

    @Element(name = "sessions", required = false)
    private SessionList sessionList;

    private String moderatorId;

    private long startTime;

    private long endTime;

    private String windowTitle;

    private int callType = -1;

    public String getWindowTitle()
    {
	return windowTitle;
    }

    public void setWindowTitle(String windowTitle)
    {
	this.windowTitle = windowTitle;
    }

    public long getStartTime()
    {
	return startTime;
    }

    public void setStartTime(long startTime)
    {
	this.startTime = startTime;
    }

    public long getEndTime()
    {
	return endTime;
    }

    public void setEndTime(long endTime)
    {
	this.endTime = endTime;
    }

    public Conversation()
    {
    }

    public Conversation(String handleId)
    {
	handle = handleId;
    }

    public String getId()
    {
	return id;
    }

    public String getHandle()
    {
	return handle;
    }

    public String getState()
    {
	return state;
    }

    public synchronized ArrayList<Participant> getParticipantList()
    {
	return participantList;
    }

    public void setParticpantList(ArrayList<Participant> participantList)
    {
	this.participantList = participantList;
    }

    public void setSessiontList(SessionList sessionList)
    {
	this.sessionList = sessionList;
    }

    public SessionList getSessionList()
    {
	return sessionList;
    }

    @Override
    public boolean equals(Object o)
    {

	boolean retVal = false;
	if (o != null && handle != null && ((Conversation) o).handle.equalsIgnoreCase(handle))
	{
	    retVal = true;
	}
	return retVal;
    }

    public String getModeratorId()
    {
	return moderatorId;
    }

    public void setModeratorId(String moderatorId)
    {
	this.moderatorId = moderatorId;
    }

    public int getCallType()
    {
	return callType;
    }

    public void setCallType(int callType)
    {
	this.callType = callType;
    }
    
    public boolean isVideoSession()
    {
	if(sessionList != null && sessionList.getCall() != null && sessionList.getCall().getVideo().equalsIgnoreCase("1"))
	{
	    return true;
	}
	return false;
    }
    
}
