package com.mportal.handlemanagement.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.util.Log;

import com.mportal.handlemanagement.contants.HandleManagementConstants;
import com.mportal.handlemanagement.controller.HandleManager;
import com.mportal.logger.SIPLogger;

@Root(name = "conv-list")
public class ConversationList
{

    @ElementList(name = "conv", required = false, inline = true)
    private ArrayList<Conversation> conversationList;

    public ArrayList<Conversation> getConversation()
    {
	return conversationList;
    }

    public boolean addConversation(Conversation conversation)
    {

	boolean retVal = false;

	if (conversationList == null)
	{
	    conversationList = new ArrayList<Conversation>();
	}
	synchronized (conversationList)
	{
	    for (Conversation item : conversationList)
	    {
		if (item.getId() != null && conversation.getId() != null && item.getId().equals(conversation.getId()))
		{
		    conversationList.remove(item);
		    break;
		}
	    }

	    // if (index < 0)
	    {
		SessionList sessionList = conversation.getSessionList();
		if (sessionList != null && sessionList.getCall() != null)
		{
		    String joinInfo = sessionList.getCall().getJoin();
		    if (joinInfo != null)
		    {
			String[] split = joinInfo.split("=");
			if (split != null && split.length > 1)
			{
			    conversation.setWindowTitle("Meet Me " + split[1]);
			}
		    }
		}
		setModerator(conversation);
		conversationList.add(conversation);

		retVal = true;
	    }
	}

	return retVal;
    }

    public boolean modifyConversation(Conversation conversation)
    {

	boolean retVal = false;

	if (conversation != null && conversationList != null)
	{
	    synchronized (conversationList)
	    {
		int index = conversationList.indexOf(new Conversation(conversation.getHandle()));
		if (index > -1)
		{
		    conversationList.remove(index);
		}
		SessionList sessionList = conversation.getSessionList();
		if (sessionList != null && sessionList.getCall() != null)
		{
		    String joinInfo = sessionList.getCall().getJoin();
		    if (joinInfo != null)
		    {
			String[] split = joinInfo.split("=");
			if (split != null && split.length > 1)
			{
			    conversation.setWindowTitle("Meet Me " + split[1]);
			}
		    }
		}

		setModerator(conversation);
		conversationList.add(conversation);
		retVal = true;
	    }
	}

	return retVal;
    }

    public int modifySessions(String handleId, SessionList sessionList)
    {
	int removedSessionType = -1;
	int index = conversationList.indexOf(new Conversation(handleId));

	if (index > -1)
	{
	    Conversation conv = conversationList.get(index);

	    if (conv != null && conv.getSessionList() != null && conversationList != null && sessionList != null)
	    {
		if (sessionList.getCall() != null)
		{
		    Call newCall = sessionList.getCall();
		    Call oldCall = conv.getSessionList().getCall();
		    if (oldCall != null)
		    {
			if (newCall.getAudio() != null && newCall.getAudio().length() > 0)
			{
			    oldCall.setAudio(newCall.getAudio());
			}
			if (newCall.getConferenceid() != null && newCall.getConferenceid().length() > 0)
			{
			    oldCall.setConferenceid(newCall.getConferenceid());
			}
			if (newCall.getJoin() != null && newCall.getJoin().length() > 0)
			{
			    oldCall.setJoin(newCall.getJoin());
			}
			if (newCall.getState() != null && newCall.getState().length() > 0)
			{
			    oldCall.setState(newCall.getState());
			}
			if (newCall.getVideo() != null && newCall.getVideo().length() > 0)
			{
			    oldCall.setVideo(newCall.getVideo());
			}
			if (newCall.getLock() != null && newCall.getLock().length() > 0)
			{
			    oldCall.setLock(newCall.getLock());
			}
		    }
		    else
		    {
			conv.getSessionList().setCall(newCall);
		    }

		    removedSessionType = HandleManagementConstants.SessionType.CALL_SESSION;

		}

		if (sessionList.getChat() != null)
		{
		    Chat newChat = sessionList.getChat();
		    Chat oldChat = conv.getSessionList().getChat();

		    if (oldChat != null)
		    {
			if (newChat.getJoin() != null && newChat.getJoin().length() > 0)
			{
			    oldChat.setJoin(newChat.getJoin());
			}
			if (newChat.getState() != null && newChat.getState().length() > 0)
			{
			    oldChat.setState(newChat.getState());
			}
		    }
		    else
		    {
			conv.getSessionList().setChat(newChat);
		    }

		    removedSessionType = HandleManagementConstants.SessionType.CHAT_SESSION;
		}

		if (sessionList.getCollab() != null)
		{
		    Collab newCollab = sessionList.getCollab();
		    Collab oldCollab = conv.getSessionList().getCollab();

		    if (oldCollab != null)
		    {
			if (newCollab.getJoin() != null && newCollab.getJoin().length() > 0)
			{
			    oldCollab.setJoin(newCollab.getJoin());
			}
			if (newCollab.getState() != null && newCollab.getState().length() > 0)
			{
			    oldCollab.setState(newCollab.getState());
			}
			if (newCollab.getScopeid() != null && newCollab.getScopeid().length() > 0)
			{
			    oldCollab.setScopeid(newCollab.getScopeid());
			}

		    }
		    else
		    {
			conv.getSessionList().setCollab(newCollab);
		    }
		    removedSessionType = HandleManagementConstants.SessionType.COLLAB_SESSION;
		}
	    }
	}
	return removedSessionType;
    }

    public void setModerator(Conversation conversation)
    {
	if (conversation != null && conversation.getParticipantList() != null)
	{
	    String moderatorId = null;
	    ListIterator<Participant> parIterator = conversation.getParticipantList().listIterator();

	    while (parIterator.hasNext())
	    {
		Participant participant = (Participant) parIterator.next();
		if (participant.getModerator() != null && participant.getModerator().equalsIgnoreCase("1"))
		{
		    moderatorId = participant.getSip();
		    conversation.setModeratorId(moderatorId);
		    break;
		}
	    }

	}
    }

    public Conversation getConversation(String id)
    {
	Conversation conv = null;

	if (conversationList != null)
	{
	    synchronized (conversationList)
	    {
		Iterator<Conversation> iterator = conversationList.listIterator();

		while (iterator.hasNext())
		{
		    conv = (Conversation) iterator.next();

		    if (conv.getId().equalsIgnoreCase(id))
		    {
			break;
		    }
		    else
		    {
			conv = null;
		    }

		}
	    }

	}

	return conv;
    }

    public Conversation getConversationByHandle(String handleId)
    {
	Conversation conv = null;

	if (conversationList != null)
	{
	    synchronized (conversationList)
	    {
		int index = conversationList.indexOf(new Conversation(handleId));
		if (index > -1)
		{
		    conv = conversationList.get(index);
		}
	    }

	}

	return conv;
    }

    public void removeConversation(String handleId)
    {
	if (conversationList != null)
	{
	    synchronized (conversationList)
	    {
		int index = conversationList.indexOf(new Conversation(handleId));
		if (index > -1)
		{
		    conversationList.remove(index);
		}
	    }

	}
    }

    public ArrayList<Participant> removeParticipant(String handleId, ArrayList<Participant> participantList)
    {
	ArrayList<Participant> parList = new ArrayList<Participant>();
	if (conversationList != null && handleId != null && participantList != null && participantList.size() > 0)
	{
	    int index = conversationList.indexOf(new Conversation(handleId));

	    if (index > -1)
	    {
		Conversation conv = conversationList.get(index);

		if (conv.getParticipantList() == null)
		{
		    conv.setParticpantList(new ArrayList<Participant>());
		}

		synchronized (conv.getParticipantList())
		{
		    ListIterator<Participant> parIterator = participantList.listIterator();
		    while (parIterator.hasNext())
		    {
			Participant participant = (Participant) parIterator.next();
			ListIterator<Participant> iterator = conv.getParticipantList().listIterator();

			while (iterator.hasNext())
			{
			    Participant par = (Participant) iterator.next();
			    if (par.getId() != null && par.getId().equalsIgnoreCase(participant.getId()))
			    {
				parList.add(par);
				break;
			    }
			}
		    }
		}

	    }
	}

	return parList;
    }

    public ArrayList<Participant> updateParticipant(String handleId, ArrayList<Participant> participantList)
    {
	ArrayList<Participant> parList = new ArrayList<Participant>();

	if (conversationList != null && handleId != null && participantList != null && participantList.size() > 0)
	{
	    int index = conversationList.indexOf(new Conversation(handleId));

	    if (index > -1)
	    {
		Conversation conv = conversationList.get(index);

		if (conv.getParticipantList() == null)
		{
		    conv.setParticpantList(new ArrayList<Participant>());
		}

		synchronized (conv.getParticipantList())
		{
		    Iterator<Participant> parIterator = participantList.listIterator();
		    while (parIterator.hasNext())
		    {
			Participant par = (Participant) parIterator.next();
			index = conv.getParticipantList().indexOf(par);

			if (index > -1)
			{
			    Participant paticipant = conv.getParticipantList().get(index);

			    if (par.getId() != null && par.getId().length() > 0)
			    {
				paticipant.setId(par.getId());
			    }

			    if (par.getCallerpasscode() != null && par.getCallerpasscode().length() > 0)
			    {
				paticipant.setCallerpasscode(par.getCallerpasscode());
			    }

			    if (par.getMedia() != null && par.getMedia().length() > 0)
			    {
				paticipant.setMedia(par.getMedia());
			    }

			    if (par.getState() != null && par.getState().length() > 0)
			    {
				if (!par.getState().equalsIgnoreCase("removed") && paticipant.getState().equalsIgnoreCase("removed"))
				{
				    SIPLogger.v(HandleManager.TAG, "new participant is active now" + par.getDisp());
				    SIPLogger.v(HandleManager.TAG, "new participant state" + par.getState());
				    SIPLogger.v(HandleManager.TAG, "new participant old state" + paticipant.getState());
				    paticipant.setNewlyActive(true);
				}

				paticipant.setState(par.getState());
			    }

			    if (par.getSipcallid() != null && par.getSipcallid().length() > 0)
			    {
				paticipant.setSipcallId(par.getSipcallid());
			    }

			    paticipant.setModerator(par.getModerator());

			    if (par.getModerator() != null && par.getModerator().length() > 0 && paticipant.getModerator().equalsIgnoreCase("1") && conv.getModeratorId() != null && !conv.getModeratorId().equalsIgnoreCase(paticipant.getSip()))
			    {
				conv.setModeratorId(paticipant.getSip());
			    }

			    char[] sessionInfo = par.getMedia().toCharArray();
			    if (sessionInfo[3] == '0')
			    {
				paticipant.setMute(null);
				paticipant.setHold(null);
			    }
			    else
			    {
				if (par.getMute() != null && par.getMute().length() > 0)
				{
				    paticipant.setMute(par.getMute());
				}

				if (par.getKick() != null && par.getKick().length() > 0)
				{
				    paticipant.setKick(par.getMute());
				}

				if (par.getHold() != null && par.getHold().length() > 0)
				{
				    paticipant.setHold(par.getHold());
				}

			    }

			    parList.add(paticipant);
			}
			else
			{
			    if (par.getState() != null && par.getState().equalsIgnoreCase("active"))
			    {
				parList.add(par);
			    }
			}
		    }

		}

	    }
	}

	return parList;

    }

    public boolean addParticipant(String handleId, ArrayList<Participant> participantList)
    {
	boolean retVal = false;

	if (conversationList != null && handleId != null && participantList != null && participantList.size() > 0)
	{
	    int index = conversationList.indexOf(new Conversation(handleId));

	    if (index > -1)
	    {
		Conversation conv = conversationList.get(index);

		if (conv.getParticipantList() == null)
		{
		    conv.setParticpantList(new ArrayList<Participant>());
		}

		synchronized (conv.getParticipantList())
		{
		    Iterator<Participant> parIterator = participantList.listIterator();
		    while (parIterator.hasNext())
		    {

			Participant par = (Participant) parIterator.next();
			index = conv.getParticipantList().indexOf(par);

			if (index > -1)
			{
			    conv.getParticipantList().remove(index);
			}

			conv.getParticipantList().add(par);
			retVal = true;
		    }

		}

	    }
	}

	return retVal;

    }

    public int addSession(Conversation conv, AddSessionDiff addSessionDiff)
    {
	int sessionType = -1;
	SessionList sessionList = addSessionDiff.getSessionList();

	if (conv != null && sessionList != null)
	{

	    if (conv.getSessionList() == null)
	    {
		conv.setSessiontList(new SessionList());
	    }

	    synchronized (conv.getSessionList())
	    {
		if (sessionList.getCall() != null)
		{
		    conv.getSessionList().setCall(sessionList.getCall());
		    sessionType = conv.getSessionList().getCall().getType();
		}

		if (sessionList.getChat() != null)
		{
		    conv.getSessionList().setChat(sessionList.getChat());
		    sessionType = conv.getSessionList().getChat().getType();
		}

		if (sessionList.getCollab() != null)
		{
		    conv.getSessionList().setCollab(sessionList.getCollab());
		    sessionType = conv.getSessionList().getCollab().getType();
		}

	    }

	    if (addSessionDiff.getParticipantList() != null)
	    {
		updateParticipant(addSessionDiff.getHandle(), addSessionDiff.getParticipantList());
	    }

	}

	return sessionType;
    }

    public int removeSession(Conversation conv, RemoveSessionDiff removeSessionDiff)
    {
	int removedSessionType = -1;

	if (conversationList != null && conv != null && removeSessionDiff != null)
	{
	    SessionList sessionList = removeSessionDiff.getSessionList();

	    if (conv.getSessionList() != null)
	    {
		synchronized (conv.getSessionList())
		{
		    if (sessionList.getCall() != null)
		    {
			removedSessionType = HandleManagementConstants.SessionType.CALL_SESSION;
		    }

		    if (sessionList.getChat() != null)
		    {
			removedSessionType = HandleManagementConstants.SessionType.CHAT_SESSION;
		    }

		    if (sessionList.getCollab() != null)
		    {
			removedSessionType = HandleManagementConstants.SessionType.COLLAB_SESSION;
		    }

		    conv.getSessionList().updateSessionState(removedSessionType, "inactive");
		}
	    }

	    if (removeSessionDiff.getParticipantList() != null)
	    {
		ArrayList<Participant> parList = updateParticipant(conv.getHandle(), removeSessionDiff.getParticipantList());
		if (parList != null)
		{
		    ListIterator<Participant> iterator = parList.listIterator();

		    while (iterator.hasNext())
		    {
			Participant participant = (Participant) iterator.next();
			int idx = conv.getParticipantList().indexOf(participant);
			if (idx > -1)
			{
			    Participant par = conv.getParticipantList().get(idx);
			    par.setMedia(participant.getMedia());
			    par.setState(participant.getState());

			}

		    }

		}
	    }
	}

	return removedSessionType;
    }

    public void removeAllConversation()
    {
	if (conversationList != null)
	{
	    synchronized (conversationList)
	    {
		conversationList.clear();
	    }

	}

    }

}
