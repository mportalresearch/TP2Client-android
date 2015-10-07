package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mportal.handlemanagement.contants.HandleManagementConstants;

@Root(strict = false)
public class Chat extends Session
{

    @Element(name = "roomid", required = false)
    private String roomid;

    public Chat()
    {
	super.setType(HandleManagementConstants.SessionType.CHAT_SESSION);
    }

    public String getRoomid()
    {
	return roomid;
    }

}