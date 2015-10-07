package com.mportal.handlemanagement.model;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mportal.handlemanagement.model.ModifyParticipantDiff.ModifyParticipant;

@Root(name = "conv-diff", strict = false)
public class AddSessionDiff
{

    @Element(name = "add", required = false)
    private AddSession addSession;

    @Element(name = "modify", required = false)
    private ModifyParticipant modifyParticipant;

    public static class AddSession
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
	SessionList sessionList = null;

	if (addSession != null)
	{
	    sessionList = addSession.getSessionList();
	}

	return sessionList;
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

    public String getHandle()
    {
	String handle = null;

	if (addSession != null)
	{
	    handle = addSession.getHandle();
	}

	return handle;
    }
}
