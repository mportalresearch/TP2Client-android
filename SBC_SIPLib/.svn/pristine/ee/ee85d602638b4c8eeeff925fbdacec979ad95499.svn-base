package com.mportal.handlemanagement.model;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "conv-diff", strict = false)
public class AddParticipantDiff
{
    @Element(name = "add")
    private AddParticipant addPar;

    public static class AddParticipant
    {
	@Attribute(name = "handle", required = false)
	public String handle;

	@ElementList(name = "participants")
	public ArrayList<Participant> participantList;

	public ArrayList<Participant> getParticipantList()
	{
	    return participantList;
	}

	public String getHandle()
	{
	    return handle;
	}

    }

    public ArrayList<Participant> getParticipantList()
    {
	ArrayList<Participant> list = null;
	if (addPar != null)
	{
	    list = addPar.getParticipantList();
	}
	return list;
    }

    public String getHandle()
    {
	String handle = null;

	if (addPar != null)
	{
	    handle = addPar.getHandle();
	}

	return handle;
    }

}
