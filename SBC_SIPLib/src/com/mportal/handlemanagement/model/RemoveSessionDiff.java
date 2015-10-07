package com.mportal.handlemanagement.model;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mportal.handlemanagement.model.ModifyParticipantDiff.ModifyParticipant;

@Root(name = "conv-diff", strict = false)
public class RemoveSessionDiff
{

    @Element(name = "remove")
    private RemoveSession removeSession;

    @Element(name = "modify", required = false)
    private ModifyParticipant modifyParticipant;

    public static class RemoveSession
    {
	@Attribute(name = "handle", required = false)
	private String handle;

	@Element(name = "sessions", required = false)
	private SessionList sessionList;

	public String getHandle()
	{
	    return handle;
	}

	public SessionList getSessionList()
	{
	    return sessionList;
	}
    }

    public SessionList getSessionList()
    {
	SessionList list = null;
	if (removeSession != null)
	{
	    list = removeSession.getSessionList();
	}
	return list;
    }

    public String getHandle()
    {
	String handle = null;

	if (removeSession != null)
	{
	    handle = removeSession.getHandle();
	}

	return handle;
    }

    public ArrayList<Participant> getParticipantList()
    {
	ArrayList<Participant> list = null;
	if (modifyParticipant != null)
	{
	    list = modifyParticipant.getParticipantList();
	}
	return list;
    }
}
