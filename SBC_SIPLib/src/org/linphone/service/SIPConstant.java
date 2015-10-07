package org.linphone.service;

public class SIPConstant
{
    public static boolean FEATURE_SUPPORT_HDVIDEO = false;

    public static String SERVER_CONFERENCE_ID = "conference";

    public static boolean SERVER_BASED_VIDEO_CONFERENCE = false;

    public static boolean SERVER_BASED_CONFERENCE = false;

    public static final int ERROR_PROCESSING_REQ = 97;

    public static final int OPERATION_FAILED_DIALOG = 98;

    public static final int CONVERSATION_EVENT = 99;

    public static final int UPDATE_MESSAGE_RECEIVED = 100;

    public static final int UPDATE_CALL_STATE_CHANGED = 101;

    public static final int UPDATE_REGISTRATION_STATE_CHANGED = 102;

    public static final int UPDATE_AUDIO_STATE_CHANGED = 103;

    public static final int UPDATE_NEW_SUBSCRIBER_REQUEST_REQUIRED = 104;

    public static final int UPDATE_PRESENCE_CHANGED = 105;

    public static final int UPDATE_CALL_ENCRYPTION_CHANGED = 106;

    public static final int UPDATE_INCOMING_CALL_RECEIVED = 107;

    public static final int UPDATE_CALL_STATE_ACTIVATE_VIDEO = 108;

    public static final int UPDATE_INCOMING_CALL_ENDED = 109;

    public static final int UPDATE_CALL_STATE_DEACTIVATE_VIDEO = 110;

    public static final int UPDATE_CALL_STATE_INITIALIZE_VIDEO = 111;

    public static final int UPDATE_FRIEND_NOTIFY = 112;

    public static final int INCOMING_CHAT_MESSAGE_RECIEVED = 113;

    public static final int UPDATE_CALL_FOR_VIDEO = 114;

    public static final int UPDATE_CHAT_ROOM_CREATED = 115;

    public static final int UPDATE_USER_INVITED_CREATED = 116;

    public static final int UPDATE_CHAT_ROOM_EXISTS = 117;

    public static final int UPDATE_CHAT_ROOM_INVITE = 118;

    public static final int UPDATE_CHAT_ROOM_USER_JOINED = 119;

    public static final int UPDATE_CHAT_ROOM_USER_LEFT = 120;

    public static final int CHAT_MSG_NOT_DELIVERED = 121;

    public static final int CHAT_MSG_DELIVERED = 122;

    public static final int UPDATE_CHAT_ROOM_STATUS_FAILED = 123;

    public static final int UPDATE_PARTICIPANTS_IN_CHAT_ROOM = 124;

    public static final int UPDATE_TRANSFER_STATE = 125;

    public static final int UPDATE_CHAT_ROOM_GOODBYE = 126;

    public static final int UPDATE_CHAT_ROOM_CLEAR_AND_CREATENEW = 127;

    public static final int UPDATE_CHAT_ROOM_ID = 128;

    public static final int UPDATE_NETWORK_ERROR = 129;

    public static final int UPDATE_CHAT_MSG_REJOIN = 130;

    public static final int UPDATE_PARTICIPANT_EXPIRY_GOODBYE = 131;

    public static final int LOGOUT_DONE = 132;

    public static final int NOTIFY_REFRESH_CONVERSATION = 133;

    public static final int NOTIFY_NETWORK_STATUS = 134;

    public static final int UPDATE_CONSULTATIVE_TRANSFER_STATE = 135;

    public static final int UPDATE_PARTICIPANT_KICK_GOODBYE = 136;

    public static final int MEET_ME_VIDEO_UPDATE = 137;

    public static final int EVENT_NOTIFY_RECEIVED = 138;

    public static final int IM_COMPOSE_STATE_CHANGE = 139;

    public static final int UPDATE_SUBSCRIBE_STATE_CHANGED = 140;

    public static final int UPDATE_PUBLISH_STATE_CHANGE = 150;

    public static final int UNREGISTER_SIP_DONE = 151;

    public static final boolean IS_LOCAL_CONFERENCE_ENABLED = true;

    public static final boolean IS_LOCAL_CONFERENCE_JOIN_BY_UI = true;

    public static final String SIP_ICE_ENABLE_VALUE = "sip_ice_enable_value";
    public static final String SIP_GSM_CODEC_VALUE = "sip_gsm_enable_value";
    public static final String SIP_PCMA_CODEC_VALUE = "sip_pcms_enable_value";
    public static final String SIP_PCMU_CODEC_VALUE = "sip_pcmu_enable_value";
    public static final String SIP_OPUS_CODEC_VALUE = "sip_opus_enable_value";
    public static final String SIP_SPEEX8_CODEC_VALUE = "sip_speex8_enable_value";
    public static final String SIP_G722_CODEC_VALUE = "sip_g722_enable_value";
    public static final String SIP_SPEEX16_CODEC_VALUE = "sip_speex16_enable_value";
    public static final String SIP_SPEEX32_CODEC_VALUE = "sip_speex32_enable_value";
    public static final String SIP_ILBC_CODEC_VALUE = "sip_ilbc_enable_value";
    public static final String SIP_AMR_CODEC_VALUE = "sip_amr_enable_value";
    public static final String SIP_AMR_WB_CODEC_VALUE = "sip_amrwb_enable_value";
    public static final String SIP_SILK24_CODEC_VALUE = "sip_silk24_enable_value";
    public static final String SIP_SILK16_CODEC_VALUE = "sip_silk16_enable_value";
    public static final String SIP_SILK8_CODEC_VALUE = "sip_silk8_enable_value";
    public static final String SIP_SILK12_CODEC_VALUE = "sip_silk12_enable_value";
    public static final String SIP_GSM_CODEC_CLOCK_VALUE = "sip_gsm_enable_CLOCK_VALUE";
    public static final String SIP_PCMA_CODEC_CLOCK_VALUE = "sip_pcma_enable_CLOCK_VALUE";
    public static final String SIP_PCMU_CODEC_CLOCK_VALUE = "sip_pcmu_enable_CLOCK_VALUE";
    public static final String SIP_OPUS_CODEC_CLOCK_VALUE = "sip_opus_enable_CLOCK_VALUE";
    public static final String SIP_SPEEX8_CODEC_CLOCK_VALUE = "sip_speex8_enable_CLOCK_VALUE";
    public static final String SIP_G722_CODEC_CLOCK_VALUE = "sip_g722_enable_CLOCK_VALUE";
    public static final String SIP_SPEEX16_CODEC_CLOCK_VALUE = "sip_speex16_enable_CLOCK_VALUE";
    public static final String SIP_SPEEX32_CODEC_CLOCK_VALUE = "sip_speex32_enable_CLOCK_VALUE";
    public static final String SIP_ILBC_CODEC_CLOCK_VALUE = "sip_ilbc_enable_CLOCK_VALUE";
    public static final String SIP_AMR_CODEC_CLOCK_VALUE = "sip_amr_enable_CLOCK_VALUE";
    public static final String SIP_AMR_WB_CODEC_CLOCK_VALUE = "sip_amrwb_enable_CLOCK_VALUE";
    public static final String SIP_SILK24_CODEC_CLOCK_VALUE = "sip_silk24_enable_CLOCK_VALUE";
    public static final String SIP_SILK16_CODEC_CLOCK_VALUE = "sip_silk16_enable_CLOCK_VALUE";
    public static final String SIP_SILK8_CODEC_CLOCK_VALUE = "sip_silk8_enable_CLOCK_VALUE";
    public static final String SIP_SILK12_CODEC_CLOCK_VALUE = "sip_silk12_enable_CLOCK_VALUE";
    public static final String SIP_SERVER_IP = "sipserverip";
    public static final String CDM_SERVER_IP = "cdmserverurl";
    public static final String SIP_SERVER_port = "sipserverport";
    public static final String SIP_TRANSPORT = "siptransport";
    public static final String PROFILE_PICTURE_SIZE = "profile_picture_size";
    public static final String PROFILE_PICTURE_SUPPORTED_TYPE = "profile_picture_type";
    public static final String SESSION_EXP_TIME = "sessionExpTime";
    public static final String SIP_MPEG4_CODEC_VALUE = "sip_mpeg_enable_value";
    public static final String SIP_G729_CODEC_VALUE = "sip_g729_enable_value";
    public static final String SIP_h263_CODEC_VALUE = "sip_h263_enable_value";
    public static final String SIP_vp8_CODEC_VALUE = "sip_vp8_enable_value";

    public static final String SIP_ECHO_MANUFACTURE_VALUE = "sip_echo_manufacture_value";
    public static final String SIP_ECHO_MODEL_VALUE = "sip_echo_model_value";
    public static final String SIP_ECHO_PLATFORM_VALUE = "sip_echo_platform_value";
    public static final String SIP_ECHO_FLAGS_VALUE = "sip_echo_flags_value";
    public static final String SIP_ECHO_DELAY_VALUE = "sip_echo_delay_value";
    public static final String SIP_ECHO_RECOMMENDED_RATE_VALUE = "sip_echo_recommended_rate_value";
    public static final String SIP_ECHO_LENGTH_VALUE = "sip_echo_length_value";

    public static String SIP_DOMAIN = null;
    public static String SIP_CONVERSATION_DOMAIN = null;

    public static String APP_VERSION = null;

    public static final String SIP_CONVERSATION_ID = "conversation";

    public static String SIP_USERAGENT = null;

    public static int SIP_CUSTOM_MODIFY_MESSENGER = 1000;

    public static boolean SIP_LOG_ENABLED = false;

}
