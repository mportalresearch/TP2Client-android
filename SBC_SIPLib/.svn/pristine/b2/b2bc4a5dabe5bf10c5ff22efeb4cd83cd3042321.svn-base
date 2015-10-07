package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "conv-diff", strict = false)
public class RemoveConvDiff
{

    @Element(name = "remove")
    private RemoveConv removeConv;

    public static class RemoveConv
    {
	@Attribute(name = "handle", required = false)
	private String handle;

	@Element(name = "conv")
	private Conversation conv;

	public Conversation getConv()
	{
	    return conv;
	}

	public String getHandle()
	{
	    return handle;
	}
    }

    public Conversation getConv()
    {
	Conversation conv = null;

	if (removeConv != null)
	{
	    conv = removeConv.getConv();
	}
	return conv;
    }

    public String getHandle()
    {
	String handle = null;

	if (removeConv != null)
	{
	    handle = removeConv.getHandle();
	}

	return handle;
    }

}
