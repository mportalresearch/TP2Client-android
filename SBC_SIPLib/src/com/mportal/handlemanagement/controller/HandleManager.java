package com.mportal.handlemanagement.controller;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.PublishState;
import org.linphone.service.SIPConstant;
import org.linphone.service.SIPCoreManager;
import org.linphone.service.SIPService;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.mportal.handlemanagement.contants.HandleManagementConstants;
import com.mportal.handlemanagement.model.AddConvDiff;
import com.mportal.handlemanagement.model.AddParticipantDiff;
import com.mportal.handlemanagement.model.AddSessionDiff;
import com.mportal.handlemanagement.model.Call;
import com.mportal.handlemanagement.model.Conversation;
import com.mportal.handlemanagement.model.ConversationList;
import com.mportal.handlemanagement.model.ModifyConvDiff;
import com.mportal.handlemanagement.model.ModifyParticipantDiff;
import com.mportal.handlemanagement.model.ModifySessionDiff;
import com.mportal.handlemanagement.model.Participant;
import com.mportal.handlemanagement.model.RemoveConvDiff;
import com.mportal.handlemanagement.model.RemoveParticipantDiff;
import com.mportal.handlemanagement.model.RemoveSessionDiff;
import com.mportal.handlemanagement.model.Session;
import com.mportal.handlemanagement.model.SessionList;
import com.mportal.logger.SIPLogger;
import com.mportal.siputil.PreferenceUtil;

public class HandleManager
{

    public static final String TAG = "HandleManagement";

    private ConversationList conversationList;

    private ArrayList<String> pendingCreateConversationList;

    private static HandleManager instance;

    public static final String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    public static final String CONV_LIST = "<conv-list>";

    public static final String CONV_DIFF = "<conv-diff>";

    public static final String ADD = "<add";

    public static final String PARTICIPANTS = "<participants";

    public static final String CONV = "<conv ";

    public static final String SESSION = "<sessions>";

    public static final String REMOVE = "<remove";

    public static final String MODIFY = "<modify";

    private EventPublisher publisher;

    private Context context;

    private Messenger activityMessenger;

    private Hashtable<String, Messenger> callbacksMapping;

    private Hashtable<String, Messenger> refreshCallbackMapping;

    private LinphoneEvent publishLpEvent;

    private LinphoneEvent subscribeLpEvent;

    public HandleManager()
    {
	conversationList = new ConversationList();
	pendingCreateConversationList = new ArrayList<String>();
	callbacksMapping = new Hashtable<String, Messenger>();
	refreshCallbackMapping = new Hashtable<String, Messenger>();
    }

    public static HandleManager getInstance()
    {
	if (instance == null)
	{
	    instance = new HandleManager();
	}

	return instance;
    }

    public void initCallBacks(Context context, EventPublisher publisher, Messenger activityMessenger)
    {
	this.publisher = publisher;
	this.context = context;
	this.activityMessenger = activityMessenger;
    }

    public void addWindowCallBack(String windowId, Messenger messenger)
    {
	if (windowId != null && messenger != null)
	{
	    if (callbacksMapping == null)
	    {
		callbacksMapping = new Hashtable<String, Messenger>();
	    }

	    if (!callbacksMapping.containsKey(windowId))
	    {
		callbacksMapping.put(windowId, messenger);
	    }
	}

    }

    public Messenger getWindowcallBack(String windowId)
    {
	Messenger messenger = null;
	if (windowId != null && callbacksMapping.containsKey(windowId))
	{
	    messenger = callbacksMapping.get(windowId);
	}

	return messenger;
    }

    public void addCallbackForRefresh(String windowId, Messenger messenger)
    {
	if (windowId != null && messenger != null)
	{
	    if (refreshCallbackMapping == null)
	    {
		refreshCallbackMapping = new Hashtable<String, Messenger>();
	    }

	    // if (!refreshCallbackMapping.containsKey(windowId))
	    {
		refreshCallbackMapping.put(windowId, messenger);
	    }
	}

    }

    public Messenger getWindowcallBackForRefresh(String windowId)
    {
	Messenger messenger = null;
	if (windowId != null && refreshCallbackMapping.containsKey(windowId))
	{
	    messenger = refreshCallbackMapping.get(windowId);
	}
	return messenger;
    }

    public void removeWindowCallBack(String windowId)
    {
	if (callbacksMapping != null && callbacksMapping.containsKey(windowId))
	{
	    callbacksMapping.remove(windowId);
	}
    }

    public void subscribeForActiveConversation()
    {
	boolean ret = PreferenceUtil.getPrefBoolean(context, HandleManagementConstants.HANDLE_MGMT_STATE, true);
	if (ret)
	{
	    LinphoneContent content = LinphoneCoreFactory.instance().createLinphoneContent("application".toString(), "active-conversations+xml".toString(), "".toString());
	    if (subscribeLpEvent == null && publisher != null)
	    {
		SIPLogger.d("HARISH", "new subscribeForActiveConversation()");
		LinphoneAddress resource = LinphoneCoreFactory.instance().createLinphoneAddress(SIPConstant.SIP_CONVERSATION_ID, SIPConstant.SIP_CONVERSATION_DOMAIN, "");
		subscribeLpEvent = publisher.getmLc().subscribe(resource, "active-conversations", 3600, content);
		SIPLogger.d("HARISH", "subscribe For Active Conversations");
	    }
	    else if (subscribeLpEvent != null)
	    {
		SIPLogger.d("HARISH", "ONLY PUBSLISH UPDATE");
		subscribeLpEvent.updatePublish(content);
	    }
	}
    }

    public void unSubscribeFromActiveConversation()
    {
	if (subscribeLpEvent != null && publisher != null && PreferenceUtil.getPrefBoolean(context, HandleManagementConstants.HANDLE_MGMT_STATE, true))
	{
	    SIPLogger.d("HARISH", "START unSubscribeFromActiveConversation()");
	    publishLpEvent = null;
	    RegistrationState regState = SIPCoreManager.getInstance().getRegistrationState();
	    SIPLogger.d("HARISH", "REGISTRATION STATE for UNSCUBSCRIBE CONVERSATION :" + regState.toString());
	    if (regState == RegistrationState.RegistrationOk)
	    {
		SIPLogger.d("HARISH", "START unSubscribe JAI");
		if (subscribeLpEvent != null)
		{
		    subscribeLpEvent.terminate();
		    subscribeLpEvent = null;
		}
		SIPLogger.d("HARISH", "END unSubscribeFromActiveConversation()");

	    }

	}
    }

    public void clearConversationEvent()
    {

	if (subscribeLpEvent != null)
	{
	    LinphoneContent body = LinphoneCoreFactory.instance().createLinphoneContent("application".toString(), "active-conversations+xml".toString(), "".toString());
	    LinphoneAddress resource = LinphoneCoreFactory.instance().createLinphoneAddress(SIPConstant.SIP_CONVERSATION_ID, SIPConstant.SIP_CONVERSATION_DOMAIN, "");
	    publisher.getmLc().subscribe(resource, "active-conversations", 0, body);

	    subscribeLpEvent.terminate();
	    subscribeLpEvent = null;
	}
	SIPLogger.d(TAG, "unSubscribe From Active Conversations");
    }

    public void parseXML(LinphoneCore lc, LinphoneEvent ev, String eventName, String message)
    {

	if (message != null)
	{
	    String xmlContent = message;
	    if (xmlContent.contains(CONV_LIST))
	    {
		createConversation(xmlContent);
	    }
	    else if (xmlContent.contains(CONV_DIFF))
	    {
		if (xmlContent.contains(ADD))
		{
		    if (xmlContent.contains(CONV) && xmlContent.contains(PARTICIPANTS))
		    {
			addConversation(xmlContent);
		    }
		    else if (xmlContent.contains(SESSION))
		    {
			addSession(xmlContent);
		    }
		    else if (xmlContent.contains(PARTICIPANTS))
		    {
			addParticipant(xmlContent);
		    }
		}
		else if (xmlContent.contains(REMOVE))
		{
		    if (xmlContent.contains(CONV))
		    {
			removeConversation(xmlContent);
		    }
		    else if (xmlContent.contains(SESSION))
		    {
			removeSession(xmlContent);
		    }
		    else if (xmlContent.contains(PARTICIPANTS))
		    {
			removePartcipant(xmlContent);
		    }
		}
		else if (xmlContent.contains(MODIFY))
		{
		    if (xmlContent.contains(SESSION) && xmlContent.contains(PARTICIPANTS))
		    {
			modifyConversation(xmlContent);
		    }
		    else if (xmlContent.contains(PARTICIPANTS) && !xmlContent.contains(SESSION))
		    {
			modifyPartcipant(xmlContent);
		    }
		    else if (xmlContent.contains(SESSION))
		    {
			modifySession(xmlContent);
		    }
		}
	    }
	}

    }

    private void addSession(String content)
    {
	if (conversationList != null)
	{

	    Serializer serializer = new Persister();
	    try
	    {
		AddSessionDiff addSessionDiff = serializer.read(AddSessionDiff.class, content);
		if (addSessionDiff != null)
		{
		    Conversation conv = conversationList.getConversationByHandle(addSessionDiff.getHandle());
		    if (conv != null)
		    {
			if (addSessionDiff.getSessionList() != null)
			{
			    int sessionType = conversationList.addSession(conv, addSessionDiff);
			    if (sessionType > -1)
			    {
				Messenger windowHandler = getWindowcallBack(conv.getId());

				if (windowHandler != null)
				{
				    Message m = new Message();
				    m.what = SIPConstant.CONVERSATION_EVENT;
				    m.arg1 = HandleManagementConstants.ADD_SESSION;
				    m.arg2 = sessionType;
				    if (sessionType == HandleManagementConstants.SessionType.CALL_SESSION)
				    {
					Call call = conv.getSessionList().getCall();
					if (call != null)
					{
					    call.setRemoved(false);
					}
				    }
				    m.obj = conv;
				    try
				    {
					windowHandler.send(m);
				    }
				    catch (RemoteException e)
				    {
					e.printStackTrace();
				    }

				}
			    }
			}
		    }

		}
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }

    private void removeSession(String content)
    {
	if (conversationList != null)
	{
	    Serializer serializer = new Persister();
	    try
	    {
		RemoveSessionDiff removeSessionDiff = serializer.read(RemoveSessionDiff.class, content);
		if (removeSessionDiff != null)
		{
		    Conversation conv = conversationList.getConversationByHandle(removeSessionDiff.getHandle());
		    if (conv != null)
		    {
			if (removeSessionDiff.getSessionList() != null)
			{
			    int sessionType = conversationList.removeSession(conv, removeSessionDiff);
			    if (sessionType > -1)
			    {
				Messenger windowHandler = getWindowcallBack(conv.getId());

				if (windowHandler != null)
				{
				    Message m = new Message();
				    m.what = SIPConstant.CONVERSATION_EVENT;
				    m.arg1 = HandleManagementConstants.REMOVE_SESSION;
				    m.arg2 = sessionType;
				    if (sessionType == HandleManagementConstants.SessionType.CALL_SESSION)
				    {
					Call call = conv.getSessionList().getCall();
					if (call != null)
					{
					    call.setRemoved(true);
					}
				    }
				    m.obj = conv;
				    try
				    {
					windowHandler.send(m);
				    }
				    catch (RemoteException e)
				    {
					e.printStackTrace();
				    }

				}

			    }
			}
		    }

		}
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }

    private void modifyPartcipant(String content)
    {
	if (conversationList != null)
	{

	    Serializer serializer = new Persister();
	    try
	    {
		ModifyParticipantDiff modifyParticipantDiff = serializer.read(ModifyParticipantDiff.class, content);

		if (modifyParticipantDiff != null && modifyParticipantDiff.getParticipantList() != null)
		{
		    ArrayList<Participant> parList = conversationList.updateParticipant(modifyParticipantDiff.getHandle(), modifyParticipantDiff.getParticipantList());
		    if (parList != null && parList.size() > 0)
		    {
			Conversation conv = conversationList.getConversationByHandle(modifyParticipantDiff.getHandle());
			if (conv != null && conv.getId() != null)
			{
			    Messenger windowHandler = getWindowcallBack(conv.getId());

			    if (windowHandler != null)
			    {
				ListIterator<Participant> iterator = parList.listIterator();

				while (iterator.hasNext())
				{
				    Participant participant = (Participant) iterator.next();
				    Message m = new Message();
				    m.what = SIPConstant.CONVERSATION_EVENT;

				    if (participant.isNewlyActive())
				    {
					SIPLogger.v(TAG, "new participant " + participant.getDisp());
					ArrayList<Participant> tempPList = new ArrayList<Participant>();
					tempPList.add(participant);
					m.arg1 = HandleManagementConstants.ADD_PARTICIPANT;
					m.obj = tempPList;
					participant.setNewlyActive(false);
				    }
				    else if (participant.getState().equalsIgnoreCase("removed"))
				    {
					m.arg1 = HandleManagementConstants.REMOVE_PARTICIPANT;
					m.obj = participant;
					if (conv.getParticipantList() != null)
					{
					    conv.getParticipantList().remove(participant);
					}
				    }
				    else
				    {
					int idx = conv.getParticipantList().indexOf(participant);
					if (idx != -1)
					{
					    m.arg1 = HandleManagementConstants.UPDATE_PARTICIPANT;
					    m.obj = participant;
					}
					else
					{
					    if (participant.getState().equalsIgnoreCase("active"))
					    {
						if (conv.getParticipantList() != null)
						{
						    conv.getParticipantList().add(participant);
						}

						ArrayList<Participant> tempPList = new ArrayList<Participant>();
						tempPList.add(participant);
						m.arg1 = HandleManagementConstants.ADD_PARTICIPANT;
						m.obj = tempPList;
					    }
					}
				    }

				    try
				    {
					windowHandler.send(m);
				    }
				    catch (RemoteException e)
				    {
					e.printStackTrace();
				    }

				}
			    }
			    else if (activityMessenger != null)
			    {
				if (conv != null && conv.getSessionList() != null)
				{
				    Message m = new Message();
				    m.what = SIPConstant.CONVERSATION_EVENT;
				    m.arg1 = HandleManagementConstants.REFRESH_CONVERSATION_LIST;
				    m.obj = conv;
				    try
				    {
					activityMessenger.send(m);
				    }
				    catch (RemoteException e)
				    {
					e.printStackTrace();
				    }
				}

			    }
			}
		    }

		}

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
    }

    private void removePartcipant(String content)
    {
	if (conversationList != null)
	{

	    Serializer serializer = new Persister();
	    try
	    {
		RemoveParticipantDiff removeParticipantDiff = serializer.read(RemoveParticipantDiff.class, content);

		if (removeParticipantDiff != null && removeParticipantDiff.getParticipantList() != null)
		{
		    ArrayList<Participant> parList = conversationList.removeParticipant(removeParticipantDiff.getHandle(), removeParticipantDiff.getParticipantList());
		    if (parList != null && parList.size() > 0)
		    {
			Conversation conv = conversationList.getConversationByHandle(removeParticipantDiff.getHandle());
			if (conv != null && conv.getId() != null)
			{
			    Messenger windowHandler = getWindowcallBack(conv.getId());

			    if (windowHandler != null)
			    {
				ListIterator<Participant> iterator = parList.listIterator();

				while (iterator.hasNext())
				{
				    Participant participant = (Participant) iterator.next();
				    Message m = new Message();
				    m.what = SIPConstant.CONVERSATION_EVENT;
				    m.arg1 = HandleManagementConstants.REMOVE_PARTICIPANT;
				    m.obj = participant;
				    if (conv.getParticipantList() != null)
				    {
					conv.getParticipantList().remove(participant);
				    }

				    try
				    {
					windowHandler.send(m);
				    }
				    catch (RemoteException e)
				    {
					e.printStackTrace();
				    }

				}
			    }
			    else if (activityMessenger != null)
			    {
				if (conv != null && conv.getSessionList() != null)
				{
				    Message m = new Message();
				    m.what = SIPConstant.CONVERSATION_EVENT;
				    m.arg1 = HandleManagementConstants.REFRESH_CONVERSATION_LIST;
				    m.obj = conv;
				    try
				    {
					activityMessenger.send(m);
				    }
				    catch (RemoteException e)
				    {
					e.printStackTrace();
				    }
				}

			    }
			}
		    }

		}

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
    }

    private void modifySession(String content)
    {
	if (conversationList != null)
	{

	    Serializer serializer = new Persister();
	    try
	    {
		ModifySessionDiff modifySessionDiff = serializer.read(ModifySessionDiff.class, content);

		int modifiedSession = conversationList.modifySessions(modifySessionDiff.getHandle(), modifySessionDiff.getSessionList());
		if (modifySessionDiff != null && modifiedSession > -1)
		{
		    Conversation conv = conversationList.getConversationByHandle(modifySessionDiff.getHandle());
		    if (conv != null && conv.getId() != null)
		    {
			Messenger windowHandler = getWindowcallBack(conv.getId());

			if (windowHandler != null)
			{
			    Message m = new Message();
			    m.what = SIPConstant.CONVERSATION_EVENT;
			    m.arg1 = HandleManagementConstants.MODIFY_SESSION;
			    m.arg2 = modifiedSession;
			    m.obj = conv;
			    try
			    {
				windowHandler.send(m);
			    }
			    catch (RemoteException e)
			    {
				e.printStackTrace();
			    }
			}

		    }

		}

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
    }

    private void modifyConversation(String content)
    {
	if (conversationList != null)
	{

	    Serializer serializer = new Persister();
	    try
	    {
		ModifyConvDiff modifyConvDiff = serializer.read(ModifyConvDiff.class, content);

		if (modifyConvDiff != null && modifyConvDiff.getConv() != null)
		{

		    Conversation conv = modifyConvDiff.getConv();
		    if (conversationList.modifyConversation(conv))
		    {
			Messenger windowHandler = getWindowcallBack(conv.getId());
			Messenger refreshHandler = getWindowcallBackForRefresh(conv.getId());
			if (refreshHandler != null)
			{
			    Message m = new Message();
			    m.what = SIPConstant.NOTIFY_REFRESH_CONVERSATION;
			    m.obj = conv;
			    refreshHandler.send(m);
			}

			if (windowHandler != null)
			{
			    Message m = new Message();
			    m.what = SIPConstant.CONVERSATION_EVENT;
			    m.arg1 = HandleManagementConstants.MODIFY_CONVERSATION;
			    m.obj = conv;
			    try
			    {
				windowHandler.send(m);
			    }
			    catch (RemoteException e)
			    {
				e.printStackTrace();
			    }

			}
			else if (activityMessenger != null)
			{
			    Message m = new Message();
			    m.what = SIPConstant.CONVERSATION_EVENT;
			    m.arg1 = HandleManagementConstants.MODIFY_CONVERSATION;
			    m.obj = modifyConvDiff.getConv();
			    try
			    {
				activityMessenger.send(m);
			    }
			    catch (RemoteException e)
			    {
				e.printStackTrace();
			    }
			}
			else
			{
			    SIPLogger.v(TAG, "Something is wrong in modify conversation !");
			}
		    }

		}
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
    }

    public void createConversation(String content)
    {

	if (conversationList == null)
	{
	    conversationList = new ConversationList();
	}

	Serializer serializer = new Persister();
	try
	{
	    ConversationList newConvList = serializer.read(ConversationList.class, content);

	    if (newConvList != null && newConvList.getConversation() != null)
	    {
		ListIterator<Conversation> iterator = newConvList.getConversation().listIterator();

		while (iterator.hasNext())
		{
		    Conversation conversation = (Conversation) iterator.next();
		    if (conversation.getId() == null || conversation.getId() != null && conversation.getId().trim().length() == 0)
		    {
			continue;
		    }
		    if (conversationList.addConversation(conversation))
		    {
			newConvList.setModerator(conversation);
		    }
		}
	    }

	    if (activityMessenger != null)
	    {
		Message m = new Message();
		m.what = SIPConstant.CONVERSATION_EVENT;
		m.arg1 = HandleManagementConstants.ACTIVE_CONVERSATIONS;
		m.obj = conversationList;
		try
		{
		    activityMessenger.send(m);
		}
		catch (RemoteException e)
		{
		    e.printStackTrace();
		}
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public void addConversation(String content)
    {

	if (conversationList != null)
	{

	    Serializer serializer = new Persister();
	    try
	    {
		AddConvDiff addConvDiff = serializer.read(AddConvDiff.class, content);
		if (addConvDiff != null && addConvDiff.getConv() != null)
		{
		    Conversation conv = addConvDiff.getConv();
		    if (conversationList.addConversation(conv))
		    {
			if (conv.getId() != null)
			{
			    Messenger windowHandler = getWindowcallBack(conv.getId());

			    if (windowHandler != null)
			    {
				inviteParticipantToConversation(addConvDiff.getConv());

				Message m = new Message();
				m.what = SIPConstant.CONVERSATION_EVENT;
				m.arg1 = HandleManagementConstants.ADD_CONVERSATION;
				m.obj = addConvDiff.getConv();
				try
				{
				    windowHandler.send(m);
				}
				catch (RemoteException e)
				{
				    e.printStackTrace();
				}

			    }
			    else if (activityMessenger != null)
			    {
				if (conv != null && conv.getSessionList() != null)
				{
				    Message m = new Message();
				    m.what = SIPConstant.CONVERSATION_EVENT;
				    m.arg1 = HandleManagementConstants.ADD_CONVERSATION;
				    m.obj = addConvDiff.getConv();
				    try
				    {
					activityMessenger.send(m);
				    }
				    catch (RemoteException e)
				    {
					e.printStackTrace();
				    }
				}

			    }
			}

		    }
		}

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
    }

    public void inviteParticipantToConversation(Conversation converation)
    {
	if (converation != null && converation.getParticipantList() != null && converation.getParticipantList().size() > 0)
	{

	    ListIterator<Participant> parIterator = converation.getParticipantList().listIterator();
	    while (parIterator.hasNext())
	    {
		Participant participant = (Participant) parIterator.next();

		if (!converation.getModeratorId().equalsIgnoreCase(participant.getSip()))
		{
		    Intent t = new Intent(SIPService.CONVERSATION_INVITATION);
		    t.putExtra(HandleManagementConstants.HANDLE_MGMT_ACTION, HandleManagementConstants.INVITE_TO_CONVERSATION);
		    t.putExtra(SIPService.WINDOW_ID_HEADER, converation.getId());
		    t.putExtra(HandleManagementConstants.PARTICIPANT_NAME, participant.getDisp());
		    t.putExtra(SIPService.USER_ID_HEADER, participant.getSip());
		    SessionList sessionList = converation.getSessionList();
		    String joinInfo = "";

		    if (sessionList.getCollab() != null)
		    {
			String url = sessionList.getCollab().getJoin();
			if (url != null)
			{
			    if (url.contains("sip:"))
			    {
				url = url.substring("sip:".length());
			    }
			    joinInfo += url + HandleManagementConstants.DELIMETER;
			}
			else
			{
			    joinInfo += "null" + HandleManagementConstants.DELIMETER;
			}
		    }
		    else
		    {
			joinInfo += "null" + HandleManagementConstants.DELIMETER;
		    }

		    if (sessionList.getChat() != null)
		    {
			joinInfo += sessionList.getChat().getRoomid() + HandleManagementConstants.DELIMETER;
		    }
		    else
		    {
			joinInfo += "null" + HandleManagementConstants.DELIMETER;
		    }

		    if (sessionList.getCall() != null)
		    {
			String url = sessionList.getCall().getJoin();
			if (url != null)
			{
			    if (url.contains("sip:"))
			    {
				url = url.substring("sip:".length());
			    }
			    joinInfo += url + HandleManagementConstants.DELIMETER;
			}
			else
			{
			    joinInfo += "null" + HandleManagementConstants.DELIMETER;
			}
		    }
		    else
		    {
			joinInfo += "null" + HandleManagementConstants.DELIMETER;
		    }

		    t.putExtra(SIPService.JOIN_INFO_HEADER, joinInfo);

		    context.startService(t);

		}
	    }
	}
    }

    public Conversation getConversation(String convId)
    {
	Conversation conv = null;
	if (conversationList != null)
	{
	    conv = conversationList.getConversation(convId);
	}

	return conv;
    }

    public Conversation getConversationByHandle(String handleId)
    {
	Conversation conv = null;

	if (conversationList != null)
	{
	    conv = conversationList.getConversationByHandle(handleId);
	}

	return conv;
    }

    public void addParticipant(String content)
    {
	if (conversationList != null)
	{
	    Serializer serializer = new Persister();
	    try
	    {
		AddParticipantDiff participant = serializer.read(AddParticipantDiff.class, content);
		if (participant != null && participant.getParticipantList() != null && conversationList.addParticipant(participant.getHandle(), participant.getParticipantList()))
		{
		    Conversation conv = conversationList.getConversationByHandle(participant.getHandle());
		    if (conv != null && conv.getId() != null)
		    {
			Messenger windowHandler = getWindowcallBack(conv.getId());

			if (windowHandler != null)
			{
			    Message m = new Message();
			    m.what = SIPConstant.CONVERSATION_EVENT;
			    m.arg1 = HandleManagementConstants.ADD_PARTICIPANT;
			    m.obj = new ArrayList<Participant>(participant.getParticipantList());
			    try
			    {
				windowHandler.send(m);
			    }
			    catch (RemoteException e)
			    {
				e.printStackTrace();
			    }
			}
		    }

		}

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
    }

    public void removeConversation(String content)
    {

	if (conversationList != null)
	{

	    Serializer serializer = new Persister();
	    try
	    {
		RemoveConvDiff removeConvDiff = serializer.read(RemoveConvDiff.class, content);
		if (removeConvDiff != null && removeConvDiff.getConv() != null)
		{
		    Conversation conv = conversationList.getConversationByHandle(removeConvDiff.getHandle());

		    if (conv != null && conv.getId() != null)
		    {
			// Need to check when this applies.
			// Messenger windowHandler = getWindowcallBack(conv.getId());
			//
			// if (windowHandler != null && conv.getSessionList() != null && (conv.getSessionList().getCall() != null ||
			// conv.getSessionList().getCollab() != null))
			// {
			// Message m = new Message();
			// m.what = SIPConstant.CONVERSATION_EVENT;
			// m.arg1 = HandleManagementConstants.REMOVE_CONVERSATION;
			// try
			// {
			// windowHandler.send(m);
			// }
			// catch (RemoteException e)
			// {
			// e.printStackTrace();
			// }
			// }
			// else

			if (activityMessenger != null)
			{
			    Message m = new Message();
			    m.what = SIPConstant.CONVERSATION_EVENT;
			    m.arg1 = HandleManagementConstants.REMOVE_CONVERSATION;
			    m.obj = removeConvDiff.getConv().getId() + HandleManagementConstants.DELIMETER + conv.getModeratorId();

			    try
			    {
				activityMessenger.send(m);
			    }
			    catch (RemoteException e)
			    {
				e.printStackTrace();
			    }

			}

			conversationList.removeConversation(removeConvDiff.getHandle());
		    }

		}
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}

    }

    public void addPendingCreateConversationReq(String callID)
    {
	if (pendingCreateConversationList != null && !pendingCreateConversationList.contains(callID))
	{
	    pendingCreateConversationList.add(callID);
	}
    }

    public Participant getPartcipant(Conversation conv, String sip)
    {
	Participant participant = null;

	if (conv != null && sip != null && conv.getParticipantList() != null && conv.getParticipantList().size() > 0)
	{
	    int index = conv.getParticipantList().indexOf(new Participant(sip));

	    if (index > -1)
	    {
		participant = conv.getParticipantList().get(index);
	    }
	}

	return participant;
    }

    public Session getSession(Conversation conv, int type)
    {
	Session session = null;

	if (conv != null && type > -1)
	{

	    switch (type)
	    {
	    case HandleManagementConstants.SessionType.CALL_SESSION:
		session = conv.getSessionList().getCall();
		break;
	    case HandleManagementConstants.SessionType.CHAT_SESSION:
		session = conv.getSessionList().getChat();
		break;

	    case HandleManagementConstants.SessionType.COLLAB_SESSION:
		session = conv.getSessionList().getCollab();
		break;

	    }
	}

	return session;
    }

    public String getUpdatedMediaBits(String media, int index, char character)
    {
	if (media != null)
	{
	    char[] bits = media.toCharArray();
	    bits[index] = character;
	    media = new String(bits);
	}
	return media;
    }

    public LinphoneEvent getLpEvent()
    {
	return publishLpEvent;
    }

    public void setPublishLpEvent(LinphoneEvent lpEvent)
    {
	this.publishLpEvent = lpEvent;
    }

    public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev, PublishState state)
    {
	if (ev == publishLpEvent)
	{
	    if (state == PublishState.Expiring)
	    {
		// Varun : TBD
	    }
	    else if (state == PublishState.Error)
	    {
		if (activityMessenger != null)
		{
		    Message m = new Message();
		    m.what = SIPConstant.OPERATION_FAILED_DIALOG;
		    try
		    {
			activityMessenger.send(m);
		    }
		    catch (RemoteException e)
		    {
			e.printStackTrace();
		    }
		}
	    }

	}
    }

    public LinphoneEvent getSubscribeLpEvent()
    {
	return subscribeLpEvent;
    }

    public void setSubscribeLpEvent(LinphoneEvent subscribeLpEvent)
    {
	this.subscribeLpEvent = subscribeLpEvent;
    }
}
