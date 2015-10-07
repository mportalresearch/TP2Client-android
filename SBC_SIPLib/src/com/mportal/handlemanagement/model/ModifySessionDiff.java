package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "conv-diff", strict = false)
public class ModifySessionDiff
{
    @Element(name = "modify")
    private ModifySession modifySession;

    public static class ModifySession
    {
	@Attribute(name = "handle", required = false)
	private String handle;

	@Element(name = "sessions", required = false)
	private SessionList sessionList;

	public SessionList getSessionList()
	{
	    return sessionList;
	}

	public String getHandle()
	{
	    return handle;
	}
    }

    public SessionList getSessionList()
    {
	SessionList sessionList = null;
	if (modifySession != null)
	{
	    sessionList = modifySession.getSessionList();
	}
	return sessionList;
    }

    public String getHandle()
    {
	String handle = null;

	if (modifySession != null)
	{
	    handle = modifySession.getHandle();
	}

	return handle;
    }

}
