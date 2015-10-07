package com.mportal.handlemanagement.contants;

public class HandleManagementConstants
{

    // Events

    public static final int ACTIVE_CONVERSATIONS = 0;
    public static final int CONVERSATION_INVITE = 1;
    public static final int ADD_CONVERSATION = 2;
    public static final int UPDATE_CONVERSATION = 3;
    public static final int MODIFY_CONVERSATION = 4;
    public static final int UPDATE_MODERATOR_INFO = 5;
    public static final int ADD_PARTICIPANT = 6;
    public static final int REMOVE_PARTICIPANT = 7;
    public static final int UPDATE_PARTICIPANT = 8;
    public static final int ADD_SESSION = 9;
    public static final int REMOVE_SESSION = 10;
    public static final int MODIFY_SESSION = 11;
    public static final int REMOVE_CONVERSATION = 12;
    public static final int UPDATE_CALL_LOG_FOR_MEETME = 13;
    public static final int REFRESH_CONVERSATION_LIST = 14;
    public static final int REINVITED_TO_SESSION = 15;

    // Other Constants

    public static final int PUBLISH_REFERESH_INTERVAL = 3600;
    public static final String PUBLISH_ADD_SESSION = "publish_add_session";
    public static final String PUBLISH_ADD_SESSION_COLLAB = "publish_add_session_collab";
    public static final String PUBLISH_ADD_SESSION_MEETME = "publish_add_session_meetme";
    public static final String PUBLISH_ADD_SESSION_CHAT = "publish_add_session_chat";
    public static final String PUBLISH_MODIFY_PARTICIPANT = "publish_modify_participant";
    public static final String PUBLISH_REMOVE_PARTICIPANT = "publish_remove_participant";
    public static final String PUBLISH_REMOVE_SESSION = "publish_remove_session";
    public static final String PUBLISH_REMOVE_CONVERSATION = "publish_remove_conv";
    public static final String PUBLISH_REFRESH_CONVERSATION = "publish_refresh_conv";
    public static final String PUBLISH_ADD_PARTICIPANT = "publish_add_participant";
    public static final String PUBLISH_CREATE_CONV = "publish_create_conv";
    public static final String KICK_FROM_SESSION = "publish_kick_from_session";
    public static final String PUBLISH_SESSION_TYPE = "publish_session_type";

    public static final String INVITE_TO_CONVERSATION = "invite_to_conv";
    public static final String REINVITE_TO_SESSION = "reinvite_to_session";
    public static final String SEND_ADD_SESSION_INVITE_MESSAGE = "send_add_session_msg";

    public static final String HANDLE_MGMT_STATE = "handle_mgmt_state";
    public static final String HANDLE_MGMT_ACTION = "handle_mgmt_action";
    public static final String HANDLE_MGMT_SESSION_TYPE = "handle_mgmt_session_type";
    public static final String WINDOW_ID = "window_id";
    public static final String JOIN_INFO = "join_info";
    public static final String CHAT_ROOM_ID = "chat_room_id";
    public static final String SEND_PUBLISH_ADD_PARTICIPANT = "send_publish_ add_par";
    public static final String WINDOW_MESSENGER = "windowMessenger";
    public static final String MODERATOR_ID = "moderator_id";
    public static final String HANDLE_ID = "handle_id";
    public static final String PARTICIPANT_STATE = "participant_state";
    public static final String PARTICIPANTS_LIST = "participants_lists";
    public static final String PARTICIPANTS_IDS = "participants_ids";
    public static final String SESSIONS = "sessions";
    public static final String CONFERENCE_ACCESSCODE = "conf_acessscode";
    public static final String MODERATOR_PASSCODE = "moderator_passcode";
    public static final String PARTICIPANT_ID = "partcipant_id";
    public static final String PARTICIPANT_NAME = "partcipant_name";
    public static final String PARTICIPANT_MEDIA_BITS_HEADER = "Media-Bits";
    public static final String DELIMETER = ";;";
    public static final int REFRESH_INTERVAL = 3600;

    public static final int MAX_MEDIA_BITS = 4;

    public static final int JOIN_INFO_COLLAB = 0;
    public static final int JOIN_INFO_CHAT = 1;
    public static final int JOIN_INFO_MEETME = 2;

    public static class SessionType
    {
	public static final int CALL_SESSION = 0;
	public static final int CHAT_SESSION = 1;
	public static final int COLLAB_SESSION = 2;
	public static final int VIDEO_SESSION = 3;
    }

}
