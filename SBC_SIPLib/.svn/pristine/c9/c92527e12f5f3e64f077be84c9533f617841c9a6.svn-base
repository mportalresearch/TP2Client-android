package com.mportal.handlemanagement.controller;

import java.util.ArrayList;
import java.util.ListIterator;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.service.SIPConstant;
import org.linphone.service.SIPService;

import android.content.Context;
import android.content.Intent;
import android.os.Messenger;

import com.mportal.handlemanagement.contants.HandleManagementConstants;
import com.mportal.handlemanagement.model.Conversation;
import com.mportal.handlemanagement.model.Participant;
import com.mportal.handlemanagement.model.Session;
import com.mportal.logger.SIPLogger;
import com.mportal.siputil.Util;

public class EventPublisher
{
    private LinphoneCore mLc;
    private Context mContext;

    public EventPublisher(LinphoneCore mLc, Context mContext)
    {
	this.mLc = mLc;
	this.mContext = mContext;
    }

    public synchronized void handleEvents(Intent intent)
    {
	String action = intent.getStringExtra(HandleManagementConstants.HANDLE_MGMT_ACTION);

	if (action != null)
	{
	    SIPLogger.d(HandleManager.TAG, "handleEvents " + action);
	    if (action.contains(HandleManagementConstants.PUBLISH_CREATE_CONV))
	    {
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
		String joinInfo = intent.getStringExtra(HandleManagementConstants.JOIN_INFO);
		String confAccessCode = intent.getStringExtra(HandleManagementConstants.CONFERENCE_ACCESSCODE);
		String moderatorCode = intent.getStringExtra(HandleManagementConstants.MODERATOR_PASSCODE);
		Messenger messenger = intent.getParcelableExtra(HandleManagementConstants.WINDOW_MESSENGER);
		int type = intent.getIntExtra(HandleManagementConstants.HANDLE_MGMT_SESSION_TYPE, -1);

		ArrayList<String> participants = intent.getStringArrayListExtra(HandleManagementConstants.PARTICIPANTS_LIST);
		ArrayList<Participant> participantList = new ArrayList<Participant>(participants.size());

		ListIterator<String> iterator = participants.listIterator();

		while (iterator.hasNext())
		{
		    String[] parInfo = (String[]) (iterator.next().split(HandleManagementConstants.DELIMETER));
		    Participant par = new Participant();

		    String id = null;
		    if (parInfo[0].contains("@"))
		    {
			id = parInfo[0].substring(0, parInfo[0].indexOf("@"));
		    }
		    else
		    {
			id = parInfo[0];
		    }
		    par.setId(windowId + "_" + id);
		    par.setSip(parInfo[0]);
		    par.setDisp(parInfo[1]);
		    if (parInfo.length > 2)
		    {
			par.setModerator(parInfo[2]);
			if (moderatorCode != null)
			{
			    par.setModeratorPasscode(moderatorCode);
			}

			if (confAccessCode != null)
			{
			    par.setCallerpasscode(confAccessCode);
			}
		    }

		    participantList.add(par);
		}

		createConversation(windowId, participantList, type, joinInfo);
		HandleManager.getInstance().addWindowCallBack(windowId, messenger);

	    }
	    else if (action.contains(HandleManagementConstants.PUBLISH_ADD_SESSION))
	    {
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
		String handleId = intent.getStringExtra(HandleManagementConstants.HANDLE_ID);
		String joinInfo = intent.getStringExtra(HandleManagementConstants.JOIN_INFO);
		int type = intent.getIntExtra(HandleManagementConstants.HANDLE_MGMT_SESSION_TYPE, -1);
		String confAccessCode = intent.getStringExtra(HandleManagementConstants.CONFERENCE_ACCESSCODE);
		// String moderatorCode = intent.getStringExtra(HandleManagementConstants.MODERATOR_PASSCODE);

		// ArrayList<String> participants = intent.getStringArrayListExtra(HandleManagementConstants.PARTICIPANTS_LIST);

		Conversation conv = HandleManager.getInstance().getConversationByHandle(handleId);
		if (conv != null && conv.getParticipantList() != null && conv.getModeratorId() != null && conv.getModeratorId().contains("@"))
		{
		    Participant moderator = HandleManager.getInstance().getPartcipant(conv, conv.getModeratorId());
		    addSessionToConv(windowId, handleId, moderator, type, joinInfo, confAccessCode);
		}

		// ListIterator<String> iterator = participants.listIterator();
		//
		// while (iterator.hasNext())
		// {
		// String[] parInfo = (String[]) (iterator.next().split(HandleManagementConstants.DELIMETER));
		// Participant par = new Participant();
		// String id = parInfo[0];
		// if (parInfo[0].contains("@"))
		// {
		// id = parInfo[0].substring(0, parInfo[0].indexOf("@"));
		// }
		// par.setId(windowId + "_" + id);
		// par.setSip(parInfo[0]);
		// par.setDisp(parInfo[1]);
		// if (parInfo.length > 2)
		// {
		// par.setModerator(parInfo[2]);
		// if (moderatorCode != null)
		// {
		// par.setModeratorPasscode(moderatorCode);
		// }
		//
		// if (confAccessCode != null)
		// {
		// par.setCallerpasscode(confAccessCode);
		// }
		// }
		//
		// participantList.add(par);
		// }

	    }
	    else if (action.contains(HandleManagementConstants.PUBLISH_ADD_PARTICIPANT))
	    {
		String windowId = intent.getStringExtra(SIPService.WINDOW_ID_HEADER);
		Conversation conv = HandleManager.getInstance().getConversation(windowId);
		if (conv != null && conv.getHandle() != null)
		{
		    String handleId = conv.getHandle();

		    Participant participant = new Participant();
		    participant.setDisp(intent.getStringExtra(HandleManagementConstants.PARTICIPANT_NAME));
		    String userId = intent.getStringExtra(SIPService.USER_ID_HEADER);
		    if (userId != null && userId.contains("@"))
		    {
			participant.setId(windowId + "_" + userId.substring(0, userId.indexOf("@")));
		    }
		    else
		    {
			participant.setId(windowId + "_" + userId);
		    }
		    participant.setSip(userId);
		    ArrayList<Participant> parList = new ArrayList<Participant>();
		    parList.add(participant);
		    addParticipant(handleId, parList);
		}
	    }
	    else if (action.contains(HandleManagementConstants.PUBLISH_REMOVE_PARTICIPANT))
	    {
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
		String handleId = null;
		String[] participantList = intent.getStringArrayExtra(HandleManagementConstants.PARTICIPANTS_IDS);

		ArrayList<Participant> parList = new ArrayList<Participant>();

		// if (intent.hasExtra(HandleManagementConstants.HANDLE_ID))
		// {
		// handleId = intent.getStringExtra(HandleManagementConstants.HANDLE_ID);
		// }

		// if (handleId == null)
		// {
		Conversation conv = HandleManager.getInstance().getConversation(windowId);
		if (conv != null && conv.getHandle() != null)
		{
		    handleId = conv.getHandle();
		    for (int i = 0; i < participantList.length; i++)
		    {
			Participant participant = HandleManager.getInstance().getPartcipant(conv, participantList[i]);
			if (participant != null)
			{
			    parList.add(participant);
			}
		    }
		}
		// }
		// else if (participantList != null)
		// {
		//
		// String[] parInfo = participantList[0].split(HandleManagementConstants.DELIMETER);
		//
		// if (parInfo != null && parInfo.length > 0)
		// {
		// Participant par = new Participant();
		// String id = parInfo[0];
		// if (parInfo[0].contains("@"))
		// {
		// id = parInfo[0].substring(0, parInfo[0].indexOf("@"));
		// }
		// par.setId(windowId + "_" + id);
		// par.setSip(parInfo[0]);
		// par.setDisp(parInfo[1]);
		// par.setMedia(parInfo[2]);
		// parList.add(par);
		// }
		// }

		if (handleId != null && parList != null && parList.size() > 0)
		{
		    removeParticipant(handleId, parList);
		}

	    }
	    else if (action.contains(HandleManagementConstants.PUBLISH_REMOVE_SESSION))
	    {
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
		int[] sessionTypes = intent.getIntArrayExtra(HandleManagementConstants.SESSIONS);

		if (windowId != null && sessionTypes != null && sessionTypes.length > 0)
		{
		    Conversation conv = HandleManager.getInstance().getConversation(windowId);

		    if (conv != null)
		    {
			for (int i = 0; i < sessionTypes.length; i++)
			{
			    int type = sessionTypes[i];

			    Session session = HandleManager.getInstance().getSession(conv, type);
			    if (session != null)
			    {
				removeSession(conv, session);
			    }
			}

		    }

		}
	    }
	    else if (action.contains(HandleManagementConstants.PUBLISH_REFRESH_CONVERSATION))
	    {
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
		Messenger messenger = intent.getParcelableExtra("controllerMessenger");
		HandleManager.getInstance().addCallbackForRefresh(windowId, messenger);
		if (windowId != null)
		{
		    Conversation conv = HandleManager.getInstance().getConversation(windowId);
		    if (conv != null)
		    {
			refreshConversation(conv.getHandle());
		    }
		}
	    }
	    else if (action.contains(HandleManagementConstants.PUBLISH_REMOVE_CONVERSATION))
	    {
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);

		if (windowId != null)
		{
		    // HandleManager.getInstance().removeWindowCallBack(windowId);
		    Conversation conv = HandleManager.getInstance().getConversation(windowId);
		    if (conv != null)
		    {
			removeConversation(conv.getHandle(), conv.getId());
		    }
		}
	    }
	    else if (action.contains(HandleManagementConstants.PUBLISH_MODIFY_PARTICIPANT))
	    {
		String parInfo[] = null;
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
		String handleId = intent.getStringExtra(HandleManagementConstants.HANDLE_ID);
		String state = intent.getStringExtra(HandleManagementConstants.PARTICIPANT_STATE);
		Participant par = null;
		String media = null;

		if (handleId != null)
		{
		    String[] participantList = intent.getStringArrayExtra(HandleManagementConstants.PARTICIPANTS_IDS);
		    parInfo = participantList[0].split(HandleManagementConstants.DELIMETER);

		    if (parInfo != null && parInfo.length > 2)
		    {
			media = parInfo[2];
		    }

		    Conversation conv = HandleManager.getInstance().getConversationByHandle(handleId);

		    if (conv != null && conv.getParticipantList() != null)
		    {
			par = HandleManager.getInstance().getPartcipant(conv, participantList[0].split(HandleManagementConstants.DELIMETER)[0]);
			if (state != null && !state.equalsIgnoreCase("active"))
			{
			    HandleManager.getInstance().removeWindowCallBack(conv.getId());
			}
		    }
		    else if (parInfo != null && parInfo.length > 2)
		    {
			if (state == null || state != null && state.length() == 0)
			{
			    state = "active";
			}

			if (parInfo != null && parInfo.length > 0)
			{
			    par = new Participant();
			    String id = parInfo[0];
			    if (parInfo[0].contains("@"))
			    {
				id = parInfo[0].substring(0, parInfo[0].indexOf("@"));
			    }
			    par.setId(windowId + "_" + id);
			    par.setSip(parInfo[0]);
			    par.setDisp(parInfo[1]);
			    par.setMedia(parInfo[2]);
			}
		    }

		    if (par != null)
		    {
			if (media == null || media != null && media.length() == 0)
			{
			    if (state.equalsIgnoreCase("inactive"))
			    {
				media = "0000";
			    }
			    else
			    {
				media = par.getMedia();
			    }

			}
			modifyParticipantSession(handleId, par, state, media);
		    }

		}
	    }
	    else if (action.contains(HandleManagementConstants.KICK_FROM_SESSION))
	    {
		String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
		String[] participantList = intent.getStringArrayExtra(HandleManagementConstants.PARTICIPANTS_IDS);
		String[] parInfo = participantList[0].split(HandleManagementConstants.DELIMETER);

		if (parInfo != null && parInfo.length > 1)
		{

		    Conversation conv = HandleManager.getInstance().getConversation(windowId);

		    if (conv != null && conv.getParticipantList() != null)
		    {
			Participant par = HandleManager.getInstance().getPartcipant(conv, parInfo[0]);

			if (par != null)
			{
			    modifyParticipantSession(conv.getHandle(), par, par.getState(), parInfo[1]);
			}
		    }
		}
	    }
	}

    }

    private void sendConversationInvite(Intent intent, SIPService service)
    {
	String handleId = null;
	String windowId = null;
	String userId = null;

	if (intent.hasExtra(SIPService.WINDOW_ID_HEADER) && intent.hasExtra(SIPService.JOIN_INFO_HEADER) && intent.hasExtra(SIPService.USER_ID_HEADER))
	{
	    userId = intent.getStringExtra(SIPService.USER_ID_HEADER);
	    LinphoneChatRoom room = mLc.getOrCreateChatRoom(userId);

	    String msg = SIPService.CONVERSATION_INVITE_MESSAGE;
	    windowId = intent.getStringExtra(SIPService.WINDOW_ID_HEADER);
	    Conversation conv = HandleManager.getInstance().getConversation(windowId);
	    String[] joininfo = intent.getStringExtra(SIPService.JOIN_INFO_HEADER).split(HandleManagementConstants.DELIMETER);

	    if (conv != null && conv.getParticipantList() != null && conv.getParticipantList().size() > 0)
	    {
		handleId = conv.getHandle();
		String headerMsg = null;

		if (handleId != null)
		{
		    // msg += "handleId=" + handleId;
		    headerMsg = "handle=" + handleId;
		}

		if (headerMsg != null)
		{
		    headerMsg += ", ";
		}
		headerMsg += "convid=" + windowId + ", invitation";

		String collbSessionInfo = joininfo[HandleManagementConstants.JOIN_INFO_COLLAB].equalsIgnoreCase("null") ? null : joininfo[HandleManagementConstants.JOIN_INFO_COLLAB];

		msg += "PSTN : \n";

		if (collbSessionInfo != null)
		{
		    msg += "Screen Share : " + collbSessionInfo + "\n";
		}

		String chatRoomId = joininfo[HandleManagementConstants.JOIN_INFO_CHAT].equalsIgnoreCase("null") ? null : joininfo[HandleManagementConstants.JOIN_INFO_CHAT];

		if (chatRoomId != null)
		{
		    msg += "Chatroom : " + chatRoomId + "\n";
		}

		String meetmeUrl = joininfo[HandleManagementConstants.JOIN_INFO_MEETME].equalsIgnoreCase("null") ? null : joininfo[HandleManagementConstants.JOIN_INFO_MEETME];

		if (meetmeUrl != null)
		{
		    msg += "SIP : " + meetmeUrl + "\n";
		}

		// String moderatorId = intent.getStringExtra(HandleManagementConstants.MODERATOR_ID);
		//
		// if (moderatorId != null && moderatorId.contains("@"))
		// {
		// moderatorId = windowId + "_" + moderatorId.substring(0, moderatorId.indexOf("@"));
		// }
		//
		// Participant par = HandleManager.getInstance().getPartcipant(conv, moderatorId);

		if (handleId != null)
		{
		    LinphoneChatMessage mLcMsg = room.createLinphoneChatMessage(msg);
		    // mLcMsg.addCustomHeader(HandleManagementConstants.PARTICIPANT_MEDIA_BITS_HEADER, par.getMedia());
		    // mLcMsg.addCustomHeader(SIPService.HANDLE_ID_HEADER, handleId);
		    // mLcMsg.addCustomHeader(SIPService.JOIN_INFO_HEADER, intent.getStringExtra(SIPService.JOIN_INFO_HEADER));
		    mLcMsg.addCustomHeader(SIPService.WINDOW_ID_HEADER, headerMsg);

		    if (intent.hasExtra(HandleManagementConstants.SEND_PUBLISH_ADD_PARTICIPANT) && intent.getBooleanExtra(HandleManagementConstants.SEND_PUBLISH_ADD_PARTICIPANT, false))
		    {
			intent.putExtra(HandleManagementConstants.HANDLE_MGMT_ACTION, HandleManagementConstants.PUBLISH_ADD_PARTICIPANT);
			handleEvents(intent);
		    }

		    room.sendMessage(mLcMsg, service);
		}

	    }

	}

    }

    public void sendInvites(Intent intent, SIPService service)
    {
	if (mLc != null && intent.hasExtra(SIPService.USER_ID_HEADER))
	{
	    String action = intent.getStringExtra(HandleManagementConstants.HANDLE_MGMT_ACTION);

	    SIPLogger.d(HandleManager.TAG, "sendInvites " + action);

	    if (action.contains(HandleManagementConstants.INVITE_TO_CONVERSATION) || action.contains(HandleManagementConstants.REINVITE_TO_SESSION) || action.contains(HandleManagementConstants.SEND_ADD_SESSION_INVITE_MESSAGE))
	    {
		sendConversationInvite(intent, service);
	    }
	    // if (action.contains(HandleManagementConstants.REINVITE_TO_SESSION))
	    // {
	    // sendSessionInviteToParticipant(intent, service);
	    // }
	    // else if (action.contains(HandleManagementConstants.SEND_ADD_SESSION_INVITE_MESSAGE))
	    // {
	    // sendAddSessionMessage(intent, service);
	    // }

	}

    }

    // public void sendSessionInviteToParticipant(Intent intent, SIPService service)
    // {
    // if (mLc != null && intent.hasExtra(SIPService.USER_ID_HEADER))
    // {
    // String handleId = null;
    // String windowId = null;
    // String userId = null;
    //
    // if (intent.hasExtra(SIPService.WINDOW_ID_HEADER) && intent.hasExtra(SIPService.JOIN_INFO_HEADER) && intent.hasExtra(SIPService.USER_ID_HEADER))
    // {
    // userId = intent.getStringExtra(SIPService.USER_ID_HEADER);
    // LinphoneChatRoom room = mLc.createChatRoom(userId);
    //
    // String msg = SIPService.PARTICIPANT_SESSION_INVITE_MESSAGE;
    // windowId = intent.getStringExtra(SIPService.WINDOW_ID_HEADER);
    // Conversation conv = HandleManager.getInstance().getConversation(windowId);
    // String[] joininfo = intent.getStringExtra(SIPService.JOIN_INFO_HEADER).split(HandleManagementConstants.DELIMETER);
    //
    // if (conv != null && conv.getParticipantList() != null && conv.getParticipantList().size() > 0)
    // {
    // handleId = conv.getHandle();
    // if (handleId != null)
    // {
    // msg += "handleId=" + handleId;
    // }
    //
    // String collbSessionInfo = joininfo[HandleManagementConstants.JOIN_INFO_COLLAB].equalsIgnoreCase("null") ? null :
    // joininfo[HandleManagementConstants.JOIN_INFO_COLLAB];
    //
    // if (collbSessionInfo != null)
    // {
    // msg += "&collabSessionKey=" + collbSessionInfo;
    // }
    //
    // String chatRoomId = joininfo[HandleManagementConstants.JOIN_INFO_CHAT].equalsIgnoreCase("null") ? null :
    // joininfo[HandleManagementConstants.JOIN_INFO_CHAT];
    //
    // if (chatRoomId != null)
    // {
    // msg += "&sessionId=" + chatRoomId;
    // }
    //
    // String meetmeUrl = joininfo[HandleManagementConstants.JOIN_INFO_MEETME].equalsIgnoreCase("null") ? null :
    // joininfo[HandleManagementConstants.JOIN_INFO_MEETME];
    //
    // if (meetmeUrl != null)
    // {
    // msg += "&join=" + meetmeUrl;
    // }
    //
    // LinphoneChatMessage mLcMsg = room.createLinphoneChatMessage(msg);
    // mLcMsg.addCustomHeader(SIPService.WINDOW_ID_HEADER, windowId);
    // room.sendMessage(mLcMsg, service);
    //
    // }
    //
    // }
    //
    // }
    // }
    //
    // public void sendAddSessionMessage(Intent intent, SIPService service)
    // {
    // if (mLc != null && intent.hasExtra(SIPService.USER_ID_HEADER))
    // {
    // String handleId = null;
    // String windowId = null;
    // String userId = null;
    //
    // if (intent.hasExtra(SIPService.WINDOW_ID_HEADER) && intent.hasExtra(SIPService.JOIN_INFO_HEADER) && intent.hasExtra(SIPService.USER_ID_HEADER))
    // {
    // userId = intent.getStringExtra(SIPService.USER_ID_HEADER);
    // LinphoneChatRoom room = mLc.createChatRoom(userId);
    //
    // String msg = SIPService.ADD_SESSION_TO_CONV_MESSAGE;
    // windowId = intent.getStringExtra(SIPService.WINDOW_ID_HEADER);
    // Conversation conv = HandleManager.getInstance().getConversation(windowId);
    // String[] joininfo = intent.getStringExtra(SIPService.JOIN_INFO_HEADER).split(HandleManagementConstants.DELIMETER);
    //
    // if (conv != null && conv.getParticipantList() != null && conv.getParticipantList().size() > 0)
    // {
    // handleId = conv.getHandle();
    // if (handleId != null)
    // {
    // msg += "handleId=" + handleId;
    // }
    //
    // String chatRoomId = joininfo[HandleManagementConstants.JOIN_INFO_CHAT].equalsIgnoreCase("null") ? null :
    // joininfo[HandleManagementConstants.JOIN_INFO_CHAT];
    //
    // if (chatRoomId != null)
    // {
    // msg += "&sessionId=" + chatRoomId;
    // }
    //
    // String meetmeUrl = joininfo[HandleManagementConstants.JOIN_INFO_MEETME].equalsIgnoreCase("null") ? null :
    // joininfo[HandleManagementConstants.JOIN_INFO_MEETME];
    //
    // if (meetmeUrl != null)
    // {
    // msg += "&join=" + meetmeUrl;
    // }
    //
    // LinphoneChatMessage mLcMsg = room.createLinphoneChatMessage(msg);
    // mLcMsg.addCustomHeader(SIPService.WINDOW_ID_HEADER, windowId);
    // room.sendMessage(mLcMsg, service);
    //
    // }
    //
    // }
    //
    // }
    // }

    public void createConversation(String convId, ArrayList<Participant> participants, int type, String joinInfo)
    {

	if (convId != null && convId.length() > 0 && participants != null && participants.size() > 0)
	{
	    String moderator = null;

	    String xmlBody = "<conv-diff><add handle=\"*\"><conv id=\"" + convId + "\" handle=\"*\" state=\"active\"><participants>";

	    // if(id != null)
	    // {
	    // xmlBody += "<sipcallid>"+id+"</sipcallid><sessions>";
	    //
	    // }

	    ListIterator<Participant> iterator = participants.listIterator();
	    String confAccessCode = null;

	    while (iterator.hasNext())
	    {
		Participant participant = (Participant) iterator.next();

		xmlBody += "<par id=\"" + participant.getId() + "\" sip=\"sip:" + participant.getSip() + "\" disp=\"" + participant.getDisp() + "\" ";

		if (participant.getModerator() != null && participant.getModerator().equalsIgnoreCase("1"))
		{
		    moderator = participant.getSip();
		    switch (type)
		    {
		    case HandleManagementConstants.SessionType.CALL_SESSION:
		    case HandleManagementConstants.SessionType.VIDEO_SESSION:
			xmlBody += "media=\"0000\" ";
			break;
		    case HandleManagementConstants.SessionType.CHAT_SESSION:
			xmlBody += "media=\"0100\" ";
			break;
		    case HandleManagementConstants.SessionType.COLLAB_SESSION:
			xmlBody += "media=\"1000\" ";
			break;
		    }

		    xmlBody += "state=\"active\"><moderator>1</moderator>";
		    if (participant.getModeratorPasscode() != null && participant.getModeratorPasscode().length() > 0)
		    {
			xmlBody += " <callerpasscode>" + participant.getModeratorPasscode() + "</callerpasscode>";
		    }

		    confAccessCode = participant.getCallerpasscode();
		}
		else
		{
		    xmlBody += "media=\"0000\" state=\"inactive\">";
		}

		xmlBody += "</par>";
	    }

	    xmlBody += "</participants><sessions>";

	    switch (type)
	    {

	    case HandleManagementConstants.SessionType.CALL_SESSION:
	    case HandleManagementConstants.SessionType.VIDEO_SESSION:

		int isVideoCall = (type == HandleManagementConstants.SessionType.VIDEO_SESSION ? 1 : 0);
		xmlBody += "<call id=\"" + convId + "_call" + "\" join=\"sip:" + Util.MEETME_PREFIX + "@" + SIPConstant.SIP_DOMAIN + ";accesscode=" + confAccessCode + "\" state=\"inactive\"><audio>1</audio><video>" + isVideoCall + "</video></call>";
		break;

	    case HandleManagementConstants.SessionType.CHAT_SESSION:

		String info[] = joinInfo.split(HandleManagementConstants.DELIMETER);
		if (info != null && info.length > 1)
		{
		    xmlBody += "<chat id=\"" + convId + "_chat" + "\" join=\"" + info[1] + "\" state=\"active\"><roomid>" + info[0] + "</roomid></chat>";
		}
		break;

	    case HandleManagementConstants.SessionType.COLLAB_SESSION:
		String join = "http://195.155.184.38/so20/premiumClient/collab/";
		if (moderator != null && moderator.length() > 1)
		{
		    join = join + moderator;
		}
		xmlBody += "<collab id=\"" + convId + "_collab" + "\" join=\"" + join + "\" state=\"active\"><scopeid>" + joinInfo + "</scopeid></collab>";
		break;

	    }

	    xmlBody += "</sessions></conv></add></conv-diff>";
	    postEvent(xmlBody);

	    SIPLogger.d(HandleManager.TAG, "createSession " + xmlBody);

	}
    }

    public void modifyParticipantSession(String handleId, Participant participant, String state, String media)
    {
	String xmlBody = "<conv-diff><modify handle=\"" + handleId + "\"><participants><par id=\"" + participant.getId() + "\" sip=\"" + participant.getSip() + "\" disp=\"" + participant.getDisp() + "\" media=\"" + media + "\" state=\"" + state + "\">";
	if (media.equalsIgnoreCase("0000"))
	{
	    if (participant.getMute() != null)
	    {
		xmlBody += "<mute>0</mute>";
	    }

	    if (participant.getHold() != null)
	    {
		xmlBody += "<hold>0</hold>";
	    }

	}
	if (participant.getModerator() != null && participant.getModerator().equals("1"))
	{
	    xmlBody += "<moderator>1</moderator>";
	}

	xmlBody += "</par></participants></modify></conv-diff>";
	postEvent(xmlBody);

	SIPLogger.d(HandleManager.TAG, "modifyParticipantSession " + xmlBody);

    }

    // public void modifyVideoCallSession(String handleId)
    // {
    // String xmlBody = "<conv-diff><add handle=\"" + handleId +
    // "\"><sessions><call id=789 join=\"sip:meetme@IP;accesscode=3332211\" state=\"active\"><conferenceid>890456</conferenceid><audio>1</audio><video>1</video></call></sessions></add><modify handle=\""
    // + handleId + "\"><participants><par id=123 media=\"1100\"/></participants></modify></conv-diff>";
    // LinphoneContent body = LinphoneCoreFactory.instance().createLinphoneContent("application".toString(), "active-conversations+xml".toString(), xmlBody);
    // // LinphoneAddress resource = LinphoneCoreFactory.instance().createLinphoneAddress("conversation", "smartoffice", "");
    // LinphoneAddress resource = LinphoneCoreFactory.instance().createLinphoneAddress(SIPConstant.SIP_CONVERSATION_ID, SIPConstant.SIP_CONVERSATION_DOMAIN,
    // "");
    // mLc.publish(resource, "active-conversations", 3600, body);
    //
    // }

    public void addParticipant(String handleId, ArrayList<Participant> participantList)
    {

	if (handleId != null && participantList != null && participantList.size() > 0)
	{
	    String xmlBody = "<conv-diff><add handle=\"" + handleId + "\"><participants>";

	    ListIterator<Participant> iterator = participantList.listIterator();

	    while (iterator.hasNext())
	    {
		Participant participant = (Participant) iterator.next();
		xmlBody += "<par id=\"" + participant.getId() + "\" sip=\"" + participant.getSip() + "\"  disp=\"" + participant.getDisp() + "\" media=\"0000\" state=\"inactive\"></par>";
	    }

	    xmlBody += "</participants></add></conv-diff>";
	    postEvent(xmlBody);
	    SIPLogger.d(HandleManager.TAG, "addParticipant " + xmlBody);
	}

    }

    public void removeParticipant(String handle, ArrayList<Participant> participantList)
    {

	if (handle != null && participantList != null && participantList.size() > 0)
	{
	    String xmlBody = "<conv-diff><modify handle=\"" + handle + "\"><participants>";

	    ListIterator<Participant> iterator = participantList.listIterator();

	    while (iterator.hasNext())
	    {
		Participant participant = (Participant) iterator.next();
		xmlBody += "<par id=\"" + participant.getId() + "\" sip=\"" + participant.getSip() + "\"  disp=\"" + participant.getDisp() + "\" media=\"0000\" state=\"removed\"></par>";
	    }

	    xmlBody += "</participants></modify></conv-diff>";
	    postEvent(xmlBody);
	    SIPLogger.d(HandleManager.TAG, "removeParticipant " + xmlBody);
	}

    }

    public void addSessionToConv(String convId, String handleId, Participant moderator, int type, String joinInfo, String conferenceCode)
    {

	if (convId != null && convId.length() > 0 && moderator != null)
	{

	    String xmlBody = "<conv-diff><add handle=\"" + handleId + "\"><sessions>";

	    switch (type)
	    {
	    case HandleManagementConstants.SessionType.CALL_SESSION:
	    case HandleManagementConstants.SessionType.VIDEO_SESSION:
		if (conferenceCode != null)
		{
		    String isVideoSession = (type == HandleManagementConstants.SessionType.VIDEO_SESSION ? "1" : "0");

		    xmlBody += "<call id=\"" + convId + "_call" + "\" join=\"sip:" + Util.MEETME_PREFIX + "@" + SIPConstant.SIP_DOMAIN + ";accesscode=" + conferenceCode + "\" state=\"inactive\"><audio>1</audio><video>" + isVideoSession + "</video></call>";
		}
		break;
	    case HandleManagementConstants.SessionType.CHAT_SESSION:

		String info[] = joinInfo.split(HandleManagementConstants.DELIMETER);
		if (info != null && info.length > 1)
		{
		    xmlBody += "<chat id=\"" + convId + "_chat" + "\" join=\"" + info[1] + "\" state=\"active\"><roomid>" + info[0] + "</roomid></chat>";
		}

		break;
	    case HandleManagementConstants.SessionType.COLLAB_SESSION:
		String join = "http://195.155.184.38/so20/premiumClient/collab/";
		if (moderator != null && moderator.getSip() != null)
		{
		    join = join + moderator.getSip();
		}
		xmlBody += "<collab id=\"" + convId + "_collab" + "\" join=\"" + join + "\" state=\"active\"><scopeid>" + joinInfo + "</scopeid></collab>";
		break;

	    }

	    if (type == HandleManagementConstants.SessionType.CHAT_SESSION || type == HandleManagementConstants.SessionType.COLLAB_SESSION)
	    {
		if (type == HandleManagementConstants.SessionType.CHAT_SESSION)
		{
		    moderator.setMedia(1, true);
		}
		else if (type == HandleManagementConstants.SessionType.COLLAB_SESSION)
		{
		    moderator.setMedia(0, true);
		}

		String modifyPartcipants = "<modify handle=\"" + handleId + "\"><participants>";
		modifyPartcipants += "<par id=\"" + moderator.getId() + "\" sip=\"" + moderator.getSip() + "\" disp=\"" + moderator.getDisp() + "\" ";
		modifyPartcipants += "media=\"" + moderator.getMedia() + "\" state=\"" + moderator.getState() + "\"><moderator>1</moderator></par>";
		modifyPartcipants += "</participants></modify>";

		xmlBody += "</sessions></add>" + modifyPartcipants + "</conv-diff>";
	    }
	    else
	    {
		xmlBody += "</sessions></add>" + "</conv-diff>";
	    }

	    postEvent(xmlBody);

	    SIPLogger.d(HandleManager.TAG, "createSession " + xmlBody);

	}
    }

    public void removeSession(Conversation conv, Session session)
    {

	if (conv != null && session != null)
	{
	    String xmlBody = "<conv-diff><remove handle=\"" + conv.getHandle() + "\"><sessions>";
	    if (session != null)
	    {
		switch (session.getType())
		{

		case HandleManagementConstants.SessionType.CALL_SESSION:
		    xmlBody += "<call id=\"" + session.getId() + "\"></call>";
		    break;
		case HandleManagementConstants.SessionType.CHAT_SESSION:
		    xmlBody += "<chat id=\"" + session.getId() + "\"></chat>";
		    break;
		case HandleManagementConstants.SessionType.COLLAB_SESSION:
		    xmlBody += "<collab id=\"" + session.getId() + "\"></collab>";
		    break;
		}

		xmlBody += "</sessions></remove><modify><participants>";

		int index = conv.getParticipantList().indexOf(new Participant(conv.getModeratorId()));

		if (index > -1)
		{
		    Participant moderator = conv.getParticipantList().get(index);
		    if (moderator != null)
		    {
			xmlBody += "<par id=\"" + moderator.getId() + "\" sip=\"sip:" + moderator.getSip() + "\" disp=\"" + moderator.getDisp() + "\" ";
			switch (session.getType())
			{
			case HandleManagementConstants.SessionType.CHAT_SESSION:
			    xmlBody += "media=\"" + HandleManager.getInstance().getUpdatedMediaBits(moderator.getMedia(), 1, '0') + "\" ";
			    break;
			case HandleManagementConstants.SessionType.COLLAB_SESSION:
			    xmlBody += "media=\"" + HandleManager.getInstance().getUpdatedMediaBits(moderator.getMedia(), 0, '0') + "\" ";
			    break;
			}

			xmlBody += "state=\"" + moderator.getState() + "\">";

			xmlBody += "<moderator>1</moderator></par>";

		    }

		}
		xmlBody += "</participants></modify></conv-diff>";

		// ListIterator<Participant> parIterator = conv.getParticipantList().listIterator();
		// while (parIterator.hasNext())
		// {
		// Participant participant = (Participant) parIterator.next();
		//
		// xmlBody += "<par id=\"" + participant.getId() + "\" sip=\"sip:" + participant.getSip() + "\" disp=\"" + participant.getDisp() + "\" ";
		//
		// switch (session.getType())
		// {
		// case HandleManagementConstants.SessionType.CHAT_SESSION:
		// xmlBody += "media=\"" + HandleManager.getInstance().getUpdatedMediaBits(participant.getMedia(), 1, '0') + "\" ";
		// break;
		// case HandleManagementConstants.SessionType.COLLAB_SESSION:
		// xmlBody += "media=\"" + HandleManager.getInstance().getUpdatedMediaBits(participant.getMedia(), 0, '0') + "\" ";
		// break;
		// }
		//
		// xmlBody += "state=\"active\">";
		//
		// xmlBody += "</par>";
		// }
		//
		// xmlBody += "</participants></modify>";

		postEvent(xmlBody);
		SIPLogger.d(HandleManager.TAG, "removeSession " + xmlBody);
	    }
	}
    }

    public void removeConversation(String handle, String convId)
    {
	String xmlBody = "<conv-diff><remove handle=\"" + handle + "\"><conv id=\"" + convId + "\"/></remove></conv-diff>";
	postEvent(xmlBody);
	SIPLogger.d(HandleManager.TAG, "removeConversation " + xmlBody);
    }

    public void refreshConversation(String handle)
    {
	String xmlBody = "<conv-diff><get handle=\"" + handle + "\"/></conv-diff>";
	postEvent(xmlBody);
	SIPLogger.d(HandleManager.TAG, "refreshConversation " + xmlBody);
    }

    private void postEvent(String xmlBody)
    {
	LinphoneContent body = LinphoneCoreFactory.instance().createLinphoneContent("application".toString(), "active-conversations+xml".toString(), xmlBody);
	LinphoneAddress resource = LinphoneCoreFactory.instance().createLinphoneAddress(SIPConstant.SIP_CONVERSATION_ID, SIPConstant.SIP_CONVERSATION_DOMAIN, "");

	if (HandleManager.getInstance().getLpEvent() == null)
	{
	    HandleManager.getInstance().setPublishLpEvent(mLc.publish(resource, "active-conversations", 3600, body));
	}
	else
	{
	    HandleManager.getInstance().getLpEvent().updatePublish(body);
	    // mLc.up(resource, "active-conversations", 3600, body);
	}

    }

    public LinphoneCore getmLc()
    {
	return mLc;
    }

}
