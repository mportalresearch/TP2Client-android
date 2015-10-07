package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mportal.handlemanagement.contants.HandleManagementConstants;

@Root(strict = false)
public class SessionList
{

    @Element(name = "call", required = false)
    private Call call;

    @Element(name = "chat", required = false)
    private Chat chat;

    @Element(name = "collab", required = false)
    private Collab collab;

    public Call getCall()
    {
	return call;
    }

    public Chat getChat()
    {
	return chat;
    }

    public Collab getCollab()
    {
	return collab;
    }

    public void setCall(Call call)
    {
	this.call = call;
    }

    public void setChat(Chat chat)
    {
	this.chat = chat;
    }

    public void setCollab(Collab collab)
    {
	this.collab = collab;
    }

    public void updateSessionState(int type, String state)
    {
	switch (type)
	{
	case HandleManagementConstants.SessionType.CALL_SESSION:

	    if (call != null)
	    {
		call.setState(state);
	    }
	    break;
	case HandleManagementConstants.SessionType.CHAT_SESSION:

	    if (chat != null)
	    {
		chat.setState(state);
	    }
	    break;
	case HandleManagementConstants.SessionType.COLLAB_SESSION:

	    if (collab != null)
	    {
		collab.setState(state);
	    }
	    break;
	}
    }
}
