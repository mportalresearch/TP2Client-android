package com.mportal.handlemanagement.model;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "conv-diff", strict = false)
public class RemoveParticipantDiff
{

    @Element(name = "remove")
    private RemoveParticipant modifyParticipant;

    public static class RemoveParticipant
    {
	@Attribute(name = "handle", required = false)
	private String handle;

	@ElementList(name = "participants")
	private ArrayList<Participant> participantList;

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
	if (modifyParticipant != null)
	{
	    list = modifyParticipant.getParticipantList();
	}
	return list;
    }

    public String getHandle()
    {
	String handle = null;

	if (modifyParticipant != null)
	{
	    handle = modifyParticipant.getHandle();
	}

	return handle;
    }
}
