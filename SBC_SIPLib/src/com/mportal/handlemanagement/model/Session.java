package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Session
{
    private int type;

    @Attribute(name = "id", required = false)
    private String id;

    @Attribute(name = "join", required = false)
    private String join;

    @Attribute(name = "state", required = false)
    private String state;

    public String getId()
    {
	return id;
    }

    public String setId(String id)
    {
	return id;
    }

    public String getJoin()
    {
	return join;
    }

    public void setJoin(String join)
    {
	this.join = join;
    }

    public String getState()
    {
	return state;
    }

    public void setState(String state)
    {
	this.state = state;
    }

    public int getType()
    {
	return type;
    }

    public void setType(int type)
    {
	this.type = type;
    }
}
