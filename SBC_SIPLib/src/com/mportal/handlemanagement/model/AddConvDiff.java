package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "conv-diff", strict = false)
public class AddConvDiff
{
    @Element(name = "add")
    private AddConv add;

    public static class AddConv
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

	if (add != null)
	{
	    conv = add.getConv();
	}
	return conv;
    }

}
