package org.linphone.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.linphone.core.ErrorInfo;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatMessage.StateListener;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCore.RemoteProvisioningState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphonePrivacyMask;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PresenceActivityType;
import org.linphone.core.PresenceBasicStatus;
import org.linphone.core.PresenceModel;
import org.linphone.core.PublishState;
import org.linphone.core.Reason;
import org.linphone.core.SubscriptionState;
import org.linphone.mediastream.MediastreamerAndroidContext;
import org.linphone.mediastream.Version;
import org.linphone.mediastream.video.capture.hwconf.Hacks;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Xml;
import android.widget.Toast;

import com.mportal.handlemanagement.contants.HandleManagementConstants;
import com.mportal.handlemanagement.controller.EventPublisher;
import com.mportal.handlemanagement.controller.HandleManager;
import com.mportal.logger.SIPLogger;
import com.mportal.model.ParticipantBean;
import com.mportal.sbc.siplib.R;
import com.mportal.sipcontext.ISIPContext;
import com.mportal.sipdata.IncomingSIPCall;
import com.mportal.sipdata.SIPCall;
import com.mportal.sipdata.SIPCallState;
import com.mportal.sipdata.SIPChat;
import com.mportal.sipdata.SIPRegistrationState;
import com.mportal.sipdata.SipFriend;
import com.mportal.siputil.FileUtil;
import com.mportal.siputil.PreferenceUtil;
import com.mportal.siputil.Util;

public class SIPService extends Service implements LinphoneCoreListener, StateListener
{

    // public SIPService()
    // {
    // super("SIPServiceThread");
    // setIntentRedelivery(true);
    // }
    //
    // public SIPService(String name)
    // {
    // super(name);
    // }

    /**
     * 
     */
    private ArrayList<LinphoneCall> currentCalls = new ArrayList<LinphoneCall>();

    private LinphoneCore mLc;

    private String mLinphoneInitialConfigFile;
    private String mLinphoneRootCaFile;
    private String mLinphoneConfigFile;
    private String basePath;

    private ConnectivityManager mConnectivityManager;

    private Timer mTimer;

    public static final String WINDOW_ID_HEADER = "x-nt-service";

    public static final String USER_ID_HEADER = "userId";

    public static final String JOIN_INFO_HEADER = "join-info";
    public static final String CALL_INFO_ID_HEADER = "Call-Info";

    public static final String MODERATOR_ID_HEADER = "moderator-id";

    public static final String HANDLE_ID_HEADER = "Handle-Id";

    public static final String RETRY_AFTER_ID_HEADER = "Retry-After";

    public static String INIT_SIP;

    public static String HANDLE_MGMT_ADD_CALLBACK;

    public static String HANDLE_MGMT_REMOVE_CALLBACK;

    public static String REGISTER_SIP;

    public static String REGISTER_ALARM;

    public static String UNREGISTER_SIP;

    public static String NETWORK_STATE_CHANGE;

    public static String SCREEN_STATE_CHANGE;

    public static String START_CALL;

    public static String ACCEPT_INCOMING_CALL;

    public static String ACCEPT_SWITCH_VIDEO;

    public static String END_CALL;

    public static String CONFERNCE_ACTION;

    public static String CHAT_MESSAGE;

    public static String MESSAGE_ACTION_SEND = "com.mportal.sipservice.message_send";

    public static String ADD_TO_CONFERENCE = "com.mportal.sipservice.addToConference";

    public static String LEAVE_CONFERENCE = "com.mportal.sipservice.leaveToConference";

    public static String SERVER_BASED_CONFERENCE = "com.mportal.sipservice.serverBasedConference";

    private static String PUBLISH_SESSION = "com.mportal.sipservice.PUBLISH_SESSION";

    public static String SUBSCRIBE_HM_ACTIVE_CONVERSATION;

    public static String REFER_CALL = "com.mportal.sipservice.refer";

    public static String CUSTOM_FEATURE;

    public static String CONVERSATION_INVITATION;

    public static String CONVERSATION_INVITE_MESSAGE = "Join my conversation at:\n\n";

    private static String HEADER_REFERED_TO = "Refer-To";

    public static boolean MEETME_TRANSFER_APP_HANDLING = true;

    // public static String PARTICIPANT_INVITE_MESSAGE = "You have a conference invitation. Click here to join.sip:" + Util.MEETME_PREFIX + "@" +
    // SIPConstant.SIP_DOMAIN + "?";
    //
    // public static String ADD_SESSION_TO_CONV_MESSAGE = "A new session is added to the conversation. Click here to join.sip:" + Util.MEETME_PREFIX + "@" +
    // SIPConstant.SIP_DOMAIN + "?";
    //
    // public static String PARTICIPANT_SESSION_INVITE_MESSAGE = "You have a session invitation. Click here to join.sip:" + Util.MEETME_PREFIX + "@" +
    // SIPConstant.SIP_DOMAIN + "?";

    public static String PUBLISH_EVENTS;

    private SIPInitRunnable sipInitRunnable;

    private WifiManager mWifiManager;

    private WifiLock mWifiLock;

    private ArrayList<ParticipantBean> activeCalls;

    private Hashtable<LinphoneCall, SIPCallback> sipCallHashtable;

    private Messenger activityMessenger;

    private Messenger controllerMessenger;

    private static final String TAG = "SipService";

    private String conferenceContactId;

    private TimerTask lTask;

    private Handler tempHandler = new Handler();

    private String dupChatMsg;

    private Long dupChatMsgRecivedTime;

    private HashMap<String, String> currentChatRooms;

    private Hashtable<String, String> maSIPList;
    // private Hashtable<String, WeakReference<Messenger>> chatrooms;
    private Hashtable<String, Messenger> chatrooms;

    private String masIP;

    // Intentionally changed to 0 to allow user to send same messages
    // continuously.
    private static final long MIN_CHAT_DURATION = 0;

    private EventPublisher eventPublisher;

    private String windowId;

    public static final String CONSULTATIVE_TRANSFER_ID = "CONSULTATIVE_TRANSFER_ID";

    private Timer mSubscribeConvTimer;

    private TimerTask subscribeConvTask;

    private int customSubscribeRetryInterval = 30 * 60 * 1000;

    private boolean isIOErrForRegister;

    // As per the FS doc it should be 1800 sec i.e in milli sec 1800 * 1000
    private final int subscriptionRetryInterval = 1800 * 1000;

    private boolean refreshRegistration;

    private long lastRegisteredTimeStamp;

    private PendingIntent pAlarmIntent;

    @Override
    public void onCreate()
    {
	super.onCreate();
	initWIFILock();
	activeCalls = new ArrayList<ParticipantBean>();
	sipCallHashtable = new Hashtable<LinphoneCall, SIPCallback>();
	currentChatRooms = new HashMap<String, String>();
	chatrooms = new Hashtable<String, Messenger>();
	maSIPList = new Hashtable<String, String>();

    }

    private void initWIFILock()
    {
	mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	mWifiLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, this.getPackageName() + "-wifi-call-lock");
	mWifiLock.setReferenceCounted(false);

	if (!mWifiLock.isHeld())
	{
	    mWifiLock.acquire();
	}
    }

    public void initializePayloads(Context context)
    {
	SIPLogger.i("", "Initializing supported payloads");
	boolean fastCpu = Version.hasFastCpu();

	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_ice_enable_key), false);
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_gsm_key), true);
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_pcma_key), true);
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_pcmu_key), true);
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_speex8_key), true);
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_g722_key), false);
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_speex16_key), fastCpu);
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_speex32_key), fastCpu);
	/*
	 * PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_video_automatically_accept_video_key), true); PreferenceUtil.putPrefBoolean(context,
	 * getString(R.string.pref_video_initiate_call_with_video_key), true);
	 */

	boolean ilbc = mLc.findPayloadType("iLBC", 8000, 1) != null;
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_ilbc_key), ilbc);

	boolean amr = mLc.findPayloadType("AMR", 8000, 1) != null;
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_amr_key), amr);

	boolean amrwb = mLc.findPayloadType("AMR-WB", 16000, 1) != null;
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_amrwb_key), amrwb);

	boolean g729 = mLc.findPayloadType("G729", 8000, 1) != null;
	PreferenceUtil.putPrefBoolean(context, getString(R.string.pref_codec_g729_key), g729);

	/*
	 * if (Version.sdkStrictlyBelow(5) || !Version.hasNeon() || !Hacks.hasCamera()) { e.putBoolean(getString(R.string.pref_video_enable_key), false); }
	 */
    }

    private void initLinphoneCore(Context context)
    {

	try
	{
	    initSystemServices(context);
	    initializeRAWFolder(context);
	    // GG_KG_TBD : Log enble things need to handled dynamically. Now its
	    // hard coded to TRUE
	    boolean isDebugLogEnabled = true;
	    LinphoneCoreFactory.instance().setDebugMode(isDebugLogEnabled, getString(R.string.app_name));

	    mLc = LinphoneCoreFactory.instance().createLinphoneCore(this, mLinphoneConfigFile, mLinphoneInitialConfigFile, null, getApplicationContext());

	    // MediastreamerAndroidContext.addSoundDeviceDesc("LGE", "Nexus 5", "msm8974", 0, 0, 16000);
	    // MediastreamerAndroidContext.addSoundDeviceDesc("samsung", "SM-G900H", "exynos5", 0, 0, 16000);

	    // MediastreamerAndroidContext.addSoundDeviceDesc("LGE", "Nexus 5", "msm8974", 0, 0, 8000);
	    // MediastreamerAndroidContext.addSoundDeviceDesc("samsung", "SM-G900H", "exynos5", 0, 0, 8000);

	    mLc.clearProxyConfigs();
	    mLc.clearAuthInfos();

	    // AndroidVideoApi5JniWrapper.setAndroidSdkVersion(Version.sdk());

	    SIPCoreManager.getInstance().setLinphoneCore(mLc);
	    sipInitRunnable = new SIPInitRunnable(context, mLc);
	    eventPublisher = new EventPublisher(mLc, context);

	    mLc.getConfig().setInt("sip", "store_auth_info", 0);
	    mLc.setContext(context);
	    try
	    {
		String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		if (versionName == null)
		{
		    versionName = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
		}
		if (TextUtils.isEmpty(SIPConstant.SIP_USERAGENT))
		{
		    mLc.setUserAgent("SmartOffice_mp_beta", "1.1.0");
		}
		else
		{
		    mLc.setUserAgent(SIPConstant.SIP_USERAGENT, "");
		}
		// mLc.setUserAgent("SmartOffice_mp_beta", "1.1.0");
		// mLc.setUserAgent("MobileOffice_mp_beta", "1.1.0");
		// mLc.setUserAgent("smartofficentendpt", "1.1.0");
		// mLc.setUserAgent("A2PC 9.0.72", "");
	    }
	    catch (NameNotFoundException e)
	    {
		SIPLogger.e("nnee", "cannot get version name");
	    }

	    mLc.enableIpv6(PreferenceUtil.getPrefBoolean(getApplicationContext(), R.string.pref_ipv6_key, false));
	    mLc.setZrtpSecretsCache(basePath + "/zrtp_secrets");

	    mLc.setCallErrorTone(Reason.DoNotDisturb, basePath + "/busy.wav");
	    mLc.setCallErrorTone(Reason.BadCredentials, basePath + "/reorder.wav");
	    mLc.setCallErrorTone(Reason.IOError, basePath + "/reorder.wav");
	    mLc.setCallErrorTone(Reason.NoMatch, basePath + "/reorder.wav");
	    mLc.setCallErrorTone(Reason.Unauthorized, basePath + "/reorder.wav");
	    // mLc.setCallErrorTone(Reason.None, basePath + "/reorder.wav");
	    mLc.setCallErrorTone(Reason.NotFound, basePath + "/reorder.wav");
	    mLc.setCallErrorTone(Reason.Declined, basePath + "/busy.wav");
	    mLc.setCallErrorTone(Reason.Busy, basePath + "/busy.wav");

	    // mLc.setTone(ToneID.CallWaiting, basePath + "/busy.wav");

	    mLc.setCallErrorTone(Reason.Unknown, basePath + "/reorder.wav");

	    mLc.setRing(null);
	    mLc.setRootCA(mLinphoneRootCaFile);
	    // mLc.setPlayFile(mPauseSoundFile);

	    int availableCores = Runtime.getRuntime().availableProcessors();
	    SIPLogger.w("mediastreamer", "MediaStreamer : " + availableCores + " cores detected and configured");
	    mLc.setCpuCount(availableCores);

	    initializePayloads(context);
	    try
	    {
		sipInitRunnable.initFromConf();
	    }
	    catch (SIPException e)
	    {
		SIPLogger.w("e", "no config ready yet");
	    }

	    startIterateTimer();

	}
	catch (Exception e)
	{
	    SIPLogger.e("e", "Cannot start linphone");
	}
    }

    private void startIterateTimer()
    {
	if (mTimer == null)
	{
	    lTask = new TimerTask()
	    {
		@Override
		public void run()
		{
		    mLc.iterate();
		}
	    };

	    mTimer = new Timer();
	    mTimer.schedule(lTask, 0, 20);
	}
    }

    private void stopIterateTimer()
    {
	if (mTimer != null)
	{
	    mTimer.cancel();
	    mTimer = null;
	}
    }

    // private void startCustomSubscribeTimer()
    // {
    // if (mSubscribeConvTimer != null)
    // {
    // mSubscribeConvTimer.purge();
    // mSubscribeConvTimer.cancel();
    // mSubscribeConvTimer = null;
    // }
    //
    // if (subscribeConvTask != null)
    // {
    // subscribeConvTask.cancel();
    // subscribeConvTask = null;
    // }
    //
    // if (SIPCoreManager.getInstance().isUserRegistered())
    // {
    //
    // mSubscribeConvTimer = new Timer();
    // subscribeConvTask = new TimerTask()
    // {
    // @Override
    // public void run()
    // {
    // SIPLogger.d("HARISH", "startCustomSubscribeTimer");
    // HandleManager.getInstance().subscribeForActiveConversation();
    // SIPCoreManager.getInstance().resubscribeCustomEvent();
    // }
    // };
    // mSubscribeConvTimer.schedule(subscribeConvTask, customSubscribeRetryInterval);
    // }
    //
    // }

    private void initializeRAWFolder(Context context)
    {
	Resources r = context.getResources();
	int[] resourceId = new int[] { R.raw.linphonerc, R.raw.rootca, R.raw.toy_mono, R.raw.oldphone_mono, R.raw.ringback, R.raw.busy, R.raw.error, R.raw.reorder };
	for (int i = 0; i < resourceId.length; i++)
	{
	    try
	    {
		InputStream s = r.openRawResource(resourceId[i]);
		byte[] data = FileUtil.readInputStream(s);
		if (data != null && data.length > 0)
		{
		    if (resourceId[i] == R.raw.linphonerc)
		    {
			FileUtil.writeFile(data, basePath, "linphonerc");
		    }
		    else if (resourceId[i] == R.raw.rootca)
		    {
			FileUtil.writeFile(data, basePath, "rootca.pem");
		    }
		    else if (resourceId[i] == R.raw.toy_mono)
		    {
			FileUtil.writeFile(data, basePath, "toy_mono.wav");
		    }
		    else if (resourceId[i] == R.raw.oldphone_mono)
		    {
			FileUtil.writeFile(data, basePath, "oldphone_mono.wav");
		    }
		    else if (resourceId[i] == R.raw.ringback)
		    {
			FileUtil.writeFile(data, basePath, "ringback.wav");
		    }
		    else if (resourceId[i] == R.raw.busy)
		    {
			FileUtil.writeFile(data, basePath, "busy.wav");
		    }
		    else if (resourceId[i] == R.raw.error)
		    {
			FileUtil.writeFile(data, basePath, "error.wav");
		    }
		    else if (resourceId[i] == R.raw.reorder)
		    {
			FileUtil.writeFile(data, basePath, "reorder.wav");
		    }
		}
	    }
	    catch (Exception e)
	    {

	    }
	}
    }

    @Override
    public void onDestroy()
    {
	super.onDestroy();
	killCore(false);
    }

    public void killCore(boolean partial)
    {
	try
	{
	    cancelRegisterAlarm();
	    if (mTimer != null)
	    {
		mTimer.cancel();
		mTimer = null;
	    }
	    if (mLc != null)
	    {
		mLc.destroy();
	    }
	    if (!partial)
	    {
		SIPCoreManager.getInstance().cleanup();
	    }
	    else
	    {
		SIPCoreManager.getInstance().killCore();
	    }
	    mWifiLock.release();
	}
	catch (RuntimeException e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    mLc = null;
	}
    }

    private void setConnectivityChanged(boolean noConnectivity)
    {

	Context context = getApplicationContext();
	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (cm == null)
	{
	    return;
	}
	NetworkInfo eventInfo = cm.getActiveNetworkInfo();

	if (noConnectivity || eventInfo == null || eventInfo.getState() == NetworkInfo.State.DISCONNECTED)
	{
	    SIPLogger.i("noconnectivity", "No connectivity: setting network unreachable");
	    if (mLc != null)
	    {
		mLc.setNetworkReachable(false);
	    }
	}
	else if (eventInfo.getState() == NetworkInfo.State.CONNECTED)
	{
	    if (sipInitRunnable != null)
	    {
		sipInitRunnable.manageTunnelServer(eventInfo);
		boolean wifiOnly = PreferenceUtil.getPrefBoolean(context, getString(R.string.pref_wifi_only_key), context.getResources().getBoolean(R.bool.pref_wifi_only_default));
		if (eventInfo.getTypeName().equalsIgnoreCase("wifi") || (eventInfo.getTypeName().length() > 0 && !wifiOnly))
		{
		    if (mLc != null)
		    {
			SIPCoreManager.getInstance().setPublishRequired(true);
			mLc.setNetworkReachable(true);
		    }
		    SIPLogger.i(eventInfo.getTypeName(), " connected: setting network reachable");
		}
		else
		{
		    if (mLc != null)
		    {
			mLc.setNetworkReachable(false);
		    }
		    SIPLogger.i(eventInfo.getTypeName(), " connected: wifi only activated, setting network unreachable");
		}
	    }
	}
    }

    private void initSystemServices(Context context)
    {
	// mListenerDispatcher = new ListenerDispatcher(listener);
	basePath = context.getFilesDir().getAbsolutePath();
	mLinphoneInitialConfigFile = basePath + "/linphonerc";
	mLinphoneConfigFile = basePath + "/.linphonerc";

	deleteAllOldFriends(mLinphoneConfigFile);

	mLinphoneRootCaFile = basePath + "/rootca.pem";
	// mRingSoundFile = basePath + "/oldphone_mono.wav";
	// mRingbackSoundFile = basePath + "/ringback.wav";
	// mPauseSoundFile = basePath + "/toy_mono.wav";

	// GG_KG_TBD: Need to handle Audio and others services
	/*
	 * sLPref = LinphonePreferenceManager.getInstance(context); mAudioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)); mVibrator
	 * = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); mPref = PreferenceManager.getDefaultSharedPreferences(c); mPowerManager =
	 * (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	 */
	mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void deleteAllOldFriends(String filePath)
    {
	File file = new File(mLinphoneConfigFile);
	if (file != null && file.exists())
	{
	    SIPLogger.d("HARISH", "linphonerc file start :" + System.currentTimeMillis());
	    BufferedReader bufferReader;
	    String writeString = new String();
	    try
	    {
		bufferReader = new BufferedReader(new FileReader(file));
		if (bufferReader != null && bufferReader.toString() != null && bufferReader.toString().length() > 0)
		{
		    String line;
		    int skipLines = -1;
		    try
		    {
			while ((line = bufferReader.readLine()) != null)
			{
			    SIPLogger.d("linphonerc", line);
			    if (line.startsWith("[friend"))
			    {
				skipLines = 0;
			    }
			    if (skipLines >= 0)
			    {
				skipLines++;
			    }

			    if (skipLines < 0)
			    {
				writeString += line + '\n';
			    }
			    if (skipLines >= 5)
			    {
				skipLines = -1;
			    }
			}
		    }
		    catch (IOException e)
		    {
			e.printStackTrace();
		    }

		    try
		    {
			file.delete();

			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(file));
			buffWriter.write(writeString);
			buffWriter.close();

		    }
		    catch (IOException e)
		    {
			e.printStackTrace();
		    }

		}
	    }
	    catch (FileNotFoundException e)
	    {
		e.printStackTrace();
	    }
	    SIPLogger.d("HARISH", "linphonerc file End :" + System.currentTimeMillis());
	}
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
	return null;
    }

    @Override
    public void authInfoRequested(LinphoneCore lc, String realm, String username, String domain)
    {
	SIPLogger.d("", "SIP CALLBACK authInfoRequested \t" + realm + "username :\t" + username);
	if (lc != null)
	{
	    Context context = getApplicationContext();
	    // String username = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_username_key), null);
	    String password = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_passwd_key), null);
	    // String domain = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_domain_key), null);
	    String privateId = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_privateid_key), null);
	    // password = "+1" + password;
	    LinphoneAuthInfo lAuthInfo = LinphoneCoreFactory.instance().createAuthInfo(username, username, password, null, realm, domain);
	    if (privateId != null)
	    {
		lAuthInfo.setUserId(privateId);
	    }
	    mLc.addAuthInfo(lAuthInfo);
	}
    }

    @Override
    public void globalState(LinphoneCore lc, GlobalState state, String message)
    {
	SIPLogger.d("", "SIP CALLBACK GlobalState \t" + state.toString() + "message :\t" + message);
	if (state == GlobalState.GlobalOn)
	{
	    SIPCoreManager.getInstance().setSIPCoreState(SIPCoreState.SIPCORE_READY);
	}
	else if (state == GlobalState.GlobalOff || state == GlobalState.GlobalShutdown)
	{
	    SIPCoreManager.getInstance().setSIPCoreState(SIPCoreState.SIPCORE_NONE);
	}
    }

    private Message getMessage(int what)
    {
	Message s = new Message();
	s.what = what;
	return s;
    }

    private Message getMessage(int what, String obj)
    {
	Message s = new Message();
	s.what = what;
	return s;
    }

    private synchronized void sendMessage(LinphoneCall call, Message message, int callState, String errorMessage)
    {
	/*
	 * if (true) { return; }
	 */
	SIPCallback callback = sipCallHashtable.get(call);
	SIPLogger.d("gopal", "gopal callback is " + callback + "\t" + call.getRemoteAddress());
	if (callback != null)
	{
	    SIPCall info = callback.getCallInfo();
	    if (info == null)
	    {
		info = new SIPCall("linphone", call);
		info.setCallNumber(callback.getCallNumber());
		callback.setCallInfo(info);
	    }
	    int what = message.what;
	    info.setMessage(errorMessage);
	    if (callState == State.Error.value())
	    {
		info.setErrorInfo(call.getErrorInfo());
		info.setErrorMessage(errorMessage);
	    }
	    SIPLogger.d("gopal", "gopal2 callback is " + callback + "\t" + callback.getCallNumber());

	    if (callState == State.Connected.value())
	    {
		LinphoneCallParams param = call.getRemoteParams();
		String paiHeader = null;
		if (param != null)
		{
		    info.setCallId(param.getCustomHeader("Call-ID"));
		    info.setFrom(param.getCustomHeader("From"));
		    info.setTo(param.getCustomHeader("To"));
		    paiHeader = param.getCustomHeader("P-Asserted-Identity");
		    info.setSessionName(param.getSessionName());
		    if (paiHeader == null)
		    {
			paiHeader = param.getCustomHeader("X-Nt-Party-Id");
			if (paiHeader != null && paiHeader.indexOf('/') > -1)
			{
			    paiHeader = paiHeader.substring(0, paiHeader.indexOf('/')) + "@" + SIPConstant.SIP_DOMAIN;
			}
		    }
		    info.setPAIHeader(paiHeader);
		}
		if (callback.getCallNumber().equalsIgnoreCase(SIPConstant.SERVER_CONFERENCE_ID))
		{
		    SIPLogger.d("HARISH", "Coference Contact Id : " + call.getRemoteContact());
		    info.setConferenceId(call.getRemoteContact());
		}
	    }
	    else if (callState == State.StreamsRunning.value())
	    {
		if (message.arg2 == 0 && isVoiceMailStreaming(call))
		{
		    info.setVoiceMailStreaming(true);
		}
	    }
	    else if (callState == State.CallEnd.value() || callState == State.Error.value())
	    {
		info.setVoiceMailStreaming(false);
	    }

	    if (what == SIPConstant.UPDATE_TRANSFER_STATE)
	    {
		info.setCallTransferState(callState);
	    }
	    else
	    {
		if (what != SIPConstant.UPDATE_CALL_STATE_ACTIVATE_VIDEO && what != SIPConstant.UPDATE_CALL_STATE_DEACTIVATE_VIDEO)
		{
		    info.setCallState(callState);
		    message.arg1 = callState;
		}
	    }
	    if (message.arg2 == 1 && callState == State.StreamsRunning.value())
	    {
		info.setReplaceContactCard(true);
	    }

	    message.obj = info;
	    try
	    {
		Messenger messenger = callback.getMessenger();
		if (messenger != null)
		{
		    messenger.send(message);
		}
		else
		{
		    if (activityMessenger != null)
		    {
			Message m = new Message();
			if (callback.getWindowId() != null && callback.getWindowId().equalsIgnoreCase(CONSULTATIVE_TRANSFER_ID))
			{
			    m.what = SIPConstant.UPDATE_CONSULTATIVE_TRANSFER_STATE;
			    info.setCallState(callState);
			    m.obj = info;
			}
			else if (callState == State.CallEnd.value() || callState == State.CallReleased.value() || callState == State.Error.value())
			{
			    if (callState != State.CallReleased.value())
			    {

				// Incoming call is being not attended by receiver.
				m.what = SIPConstant.UPDATE_INCOMING_CALL_ENDED;
			    }
			}
			if (m.what > 0)
			{
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
	    catch (RemoteException e)
	    {
		e.printStackTrace();
	    }
	}

    }

    private boolean isIntercomCall(LinphoneCallParams param)
    {
	// TODO: for now checking only Answer-Mode
	// Answer-Mode: Auto
	// Alert-Info: Auto Answer
	// Alert-Info: info=alert-autoanswer
	// Alert-Info: Ring Answer
	// Alert-Info: info=RingAnswer
	// Alert-Info: Intercom
	// Alert-Info: info=intercom
	// Call-Info: =\;answer-after=0
	// Call-Info: ;answer-after=0
	String answerMode = param.getCustomHeader("Answer-Mode");
	return answerMode != null && ("Auto".equalsIgnoreCase(answerMode.trim()));
    }

    @Override
    public void callState(LinphoneCore lc, final LinphoneCall call, State cstate, final String message)
    {
	SIPLogger.d("", "SIP CALLBACK callState \t" + call.getRemoteAddress() + "\t" + cstate.toString() + " \t" + message);
	boolean videoEnabled = call.getRemoteParams().getVideoEnabled();
	SIPLogger.d("@@@@@@@@@@", "wwwwwwwwwwwwww remote params callstate videoeanbled" + videoEnabled);

	videoEnabled = call.getCurrentParamsCopy().getVideoEnabled();
	SIPLogger.d("@@@@@@@@@@", "wwwwwwwwwwwwww custom params callstate videoeanbled" + videoEnabled);

	SIPLogger.d("@@@@@@@@@@", "wwwwwwwwwwwwww total calls in call state" + mLc.getCallsNb());

	// if (message != null)
	// {
	// String[] errorCode = message.split(":");
	// if (errorCode != null)
	// {
	// if (errorCode[0] != null && errorCode[0].equalsIgnoreCase("491"))
	// {
	// return;
	// }
	// }
	// }

	if (cstate == State.Refered && MEETME_TRANSFER_APP_HANDLING)
	{

	    String refferdTo = call.getRemoteParams().getCustomHeader(HEADER_REFERED_TO);
	    SIPLogger.d("HARISH", "sipservice is refered to : " + refferdTo);
	    if (refferdTo != null && refferdTo.contains(Util.MEETME_PREFIX + "@" + SIPConstant.SIP_DOMAIN))
	    {
		SIPCallback callBack = sipCallHashtable.get(call);
		if (callBack != null)
		{
		    SIPCall sipCall = callBack.getCallInfo();
		    SIPCoreManager.getInstance().startRefferedCall(sipCall, callBack.getWindowId());
		}
	    }
	    else
	    {
		// sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.Refered.value());
	    }
	}
	else if (cstate == State.OutgoingRinging)
	{
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.OutgoingRinging.value(), message);
	    SIPLogger.d("", "SIZE999 : " + activeCalls.size());
	}
	else if (cstate == State.IncomingReceived)
	{

	    LinphoneAddress displayAddress = null;
	    LinphoneAddress address = null;
	    LinphoneCallParams param = call.getRemoteParams();

	    String callInfoHeader = param.getCustomHeader(CALL_INFO_ID_HEADER);
	    String paiHeader = param.getCustomHeader("P-Asserted-Identity");
	    String windowId = getWindowId(param.getCustomHeader(WINDOW_ID_HEADER), "convid=");
	    String callId = param.getCustomHeader("Call-ID");
	    if (TextUtils.isEmpty(callId))
	    {
		callId = param.getCustomHeader("i");
	    }

	    boolean isVideoEnabled = param.getVideoEnabled();
	    String sessionName = param.getSessionName();
	    SIPLogger.d("SIPService", "IncomingReceived Call sessionName : " + sessionName);
	    if (windowId != null && windowId.contains("|"))
	    {
		String[] splitWindowId = windowId.split(java.util.regex.Pattern.quote("|"));
		windowId = splitWindowId[0];
	    }
	    else if (windowId == null)
	    {
		windowId = SIPCoreManager.getInstance().createWindowId(call.getRemoteAddress().getUserName());
	    }

	    String userName = null;
	    boolean checkFromAddress = false;
	    if (paiHeader != null)
	    {
		// First check in PAI Header
		try
		{
		    displayAddress = LinphoneCoreFactory.instance().createLinphoneAddress(paiHeader);
		    userName = displayAddress.getDisplayName();
		    String domain = displayAddress.getDomain();
		    if (domain != null)
		    {
			if (domain.startsWith("unknown"))
			{
			    checkFromAddress = true;
			}
		    }
		    if (userName == null)
		    {
			userName = call.getRemoteAddress().getDisplayName();
		    }
		}
		catch (LinphoneCoreException e)
		{
		    e.printStackTrace();
		}
	    }
	    boolean isUserNameIsPSTN = false;
	    boolean isCallerNumberPSTN = false;
	    if (displayAddress == null || checkFromAddress)
	    {
		// PAI header is null. So check in from address.

		displayAddress = call.getRemoteAddress();
		userName = displayAddress.getDisplayName();
		if (userName == null)
		{
		    userName = displayAddress.getUserName();
		    if (userName != null)
		    {
			String temp = userName;
			int indx = userName.indexOf('@');
			if (indx != -1)
			{
			    temp = userName.substring(0, indx);
			}

			if (android.util.Patterns.PHONE.matcher(temp).matches())
			{
			    isUserNameIsPSTN = true;
			    userName = temp;
			}
		    }
		}
		SIPLogger.d(TAG, "mportal's--found Normal--Address" + address);
	    }
	    displayAddress = null;

	    address = call.getRemoteAddress();

	    if (activityMessenger != null && address != null)
	    {

		String callerNumber = address.getUserName();

		if (callerNumber == null)
		{
		    callerNumber = address.getDisplayName();
		}

		if (userName == null)
		{
		    userName = callerNumber;
		}

		if (!isUserNameIsPSTN && android.util.Patterns.PHONE.matcher(callerNumber).matches())
		{
		    isCallerNumberPSTN = true;
		    isUserNameIsPSTN = true;
		}

		if (!isUserNameIsPSTN)
		{
		    callerNumber += '@' + address.getDomain();
		}
		else if (userName != null && !isCallerNumberPSTN)
		{
		    callerNumber = userName;
		}

		Message m = new Message();
		IncomingSIPCall sipCall = new IncomingSIPCall("linphone", call);
		sipCall.setWindowId(windowId);
		sipCall.setCallId(callId);
		if (paiHeader != null)
		{
		    sipCall.setPAIHeader(paiHeader);
		}
		boolean isIntercomCall = isIntercomCall(param);
		if (isIntercomCall)
		{
		    sipCall.setInterComCall(true);
		    try
		    {
			mLc.acceptCall(call);
		    }
		    catch (Exception e)
		    {
			e.printStackTrace();
			return;
		    }
		}

		sipCall.setCallNumber(isUserNameIsPSTN ? callerNumber : (address.getUserName() + "@" + address.getDomain()));
		sipCall.setWindowId(removePrefixCallNumber(sipCall.getCallNumber()));
		if (!TextUtils.isEmpty(callId))
		{
		    sipCall.setWindowId(sipCall.getWindowId() + "_" + sipCall.getCallId());
		}

		SIPCallback callback = new SIPCallback(null, sipCall.getWindowId(), sipCall.getCallNumber());
		if (!sipCallHashtable.containsKey(call))
		{
		    sipCallHashtable.put(call, callback);
		}

		sipCall.setDisplayCallNumber(isUserNameIsPSTN ? callerNumber : (address.getUserName() + "@" + address.getDomain()));
		sipCall.setCallerName(userName);
		sipCall.setCallState(SIPCallState.IncomingReceived.value());
		sipCall.setVideoEnabled(isVideoEnabled);
		sipCall.setSessionName(sessionName);

		if (callInfoHeader != null)
		{
		    SIPLogger.d(TAG, "mportal's--Call info header--" + callInfoHeader);
		    sipCall.setCallInfoHeaderUrl(callInfoHeader);
		}

		m.obj = sipCall;
		m.what = SIPConstant.UPDATE_INCOMING_CALL_RECEIVED;
		try
		{
		    activityMessenger.send(m);
		}
		catch (RemoteException e)
		{
		    e.printStackTrace();
		}
	    }

	    if (SIPCoreManager.getInstance().isUserDND())
	    {
		mLc.declineCall(call, Reason.Busy);
		// return;
	    }
	    
	    Context context = getApplicationContext();
	    if (context instanceof ISIPContext)
	    {
		ISIPContext sipContext = (ISIPContext) getApplicationContext();
		if (sipContext != null)
		{
		    if(!sipContext.checkIncomingOptedIn()) //incoming opt out
		    {
			mLc.declineCall(call, Reason.Busy);
		    }
		}
	    }
	    
	    // try
	    // {
	    // sendMessage(call, getMessage(1),
	    // SIPCallState.IncomingReceived.value());
	    // mLc.acceptCall(call);
	    // // changeParticipantCallStatus(UIWindowID, CallStatus.IS_IN_CALL,
	    // call);
	    // // GG_KG_TBD
	    // adjustVolume(100);
	    // }
	    // catch (LinphoneCoreException e)
	    // {
	    // // TODO Auto-generated catch block
	    // e.printStackTrace();
	    // }

	}
	else if (cstate == State.OutgoingInit)
	{
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.OutgoingInit.value(), message);
	}
	else if (cstate == State.Connected)
	{
	    /*
	     * if (currentCalls.size() == 0) { currentCalls.add(call); } else { if (!currentCalls.contains(call)) { currentCalls.add(call); } }
	     */
	    if (Hacks.needSoftvolume()) // GG_KG_TBD || sLPref.useSoftvolume())
	    {
		SIPCoreManager.getInstance().adjustVolume(getApplicationContext(), 0); // Synchronize
	    }
	    if (call.getRemoteAddress().getUserName().contains("confer"))
	    {
		conferenceContactId = call.getRemoteContact();
	    }
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.Connected.value(), message);
	    boolean isVideoEnabled = call.getRemoteParams().getVideoEnabled();
	    if (isVideoEnabled)
	    {
		sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_INITIALIZE_VIDEO), 0, message);
	    }
	}
	else if (cstate == State.StreamsRunning)
	{

	    SIPLogger.d("HARISH", "Streaming : for call : " + call.getRemoteAddress().getUserName());
	    /*
	     * if (mLc.isSpeakerEnabled()) { mLc.enableSpeaker(true); }
	     */
	    // History-Info
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.StreamsRunning.value(), message);
	    // changeParticipantCallStatus(UIWindowID, CallStatus.IS_IN_STREAM,
	    // call);
	    //
	    // Workaround bug current call seems to be updated after state
	    // changed
	    // to streams running
	    // if (!mWifiLock.isHeld())
	    // {
	    // mWifiLock.acquire();
	    // }
	    // SIPLogger.d("HARISH", "Video is Enabled : " +
	    // call.getRemoteParams().getVideoEnabled());
	    // // call.enableCamera(true);
	    // call.getVideoStats();

	    tempHandler.postDelayed(new Runnable()
	    {

		@Override
		public void run()
		{

		    if (call != null && call.getCurrentParamsCopy() != null)
		    {
			if (call.getCurrentParamsCopy().getVideoEnabled())
			{
			    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_ACTIVATE_VIDEO), 0, message); // Should display
			}
			else
			{
			    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_DEACTIVATE_VIDEO), 0, message); // Shouldn't display
			}
		    }
		}
	    }, 0);

	    //
	    // // GK_KG_TBD : Need to move this logic . This for Demo
	    // if (isWindowQualifiedForConfernce(UIWindowID, call) > 1 &&
	    // windowDetails.get(UIWindowID).getWindowCallType() !=
	    // WindowCallType.CONFERENCE_CALL)
	    // {
	    // addAllUsersWindowToConference(UIWindowID);
	    // }

	    /*
	     * if (cstate == State.CallReleased) //|| cstate == State.Error) && mLc.getCallsNb() < 1) { mWifiLock.release(); removeCallFromConfernce(UIWindowID,
	     * call); changeParticipantCallStatus(UIWindowID,CallStatus.NOT_IN_CALL, call); }
	     */
	}
	else if (cstate == State.CallEnd)
	{

	    LinphoneCall newCall = call.getTransferTargetCall();
	    if (newCall != null && newCall.getState() == State.StreamsRunning)
	    {
		SIPCallback callBack = sipCallHashtable.get(call);
		sipCallHashtable.remove(call);
		sipCallHashtable.put(newCall, callBack);
		if (callBack != null)
		{
		    SIPCall callInfo = callBack.getCallInfo();
		    if (callInfo != null)
		    {
			callInfo.setCallObject(newCall);
		    }
		}
		Message msg = getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED);
		msg.arg2 = 1;
		sendMessage(newCall, msg, SIPCallState.StreamsRunning.value(), message);
	    }
	    else
	    {
		/*
		 * if (currentCalls.contains(call)) { currentCalls.remove(call); }
		 */
		sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_DEACTIVATE_VIDEO), 0, message);
		sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.CallEnd.value(), message);
	    }
	}
	else if (cstate == State.Error)
	{
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.Error.value(), message);
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_DEACTIVATE_VIDEO), 0, message);
	}
	else if (cstate == State.CallReleased)
	{
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.CallReleased.value(), message);
	    if (sipCallHashtable.containsKey(call))
	    {
		sipCallHashtable.remove(call);
	    }
	}
	else if (cstate == State.CallUpdatedByRemote)
	{
	    LinphoneAddress address = null;
	    LinphoneCallParams param = call.getRemoteParams();

	    String callId = param.getCustomHeader("Call-ID");
	    if (TextUtils.isEmpty(callId))
	    {
		callId = param.getCustomHeader("i");
	    }

	    if (param.getVideoEnabled())
	    {
		LinphoneCallParams localParams = call.getCurrentParamsCopy();
		if (localParams.getVideoEnabled())
		{
		    try
		    {
			SIPLogger.d("HARISH", "Callupdate came active video call, So auto accpeting to keep session active");
			mLc.acceptCallUpdate(call, localParams);
		    }
		    catch (LinphoneCoreException e)
		    {
			e.printStackTrace();
		    }
		    return;
		}

		String callInfoHeader = param.getCustomHeader(CALL_INFO_ID_HEADER);
		String paiHeader = param.getCustomHeader("P-Asserted-Identity");
		if (paiHeader != null)
		{
		    try
		    {
			address = LinphoneCoreFactory.instance().createLinphoneAddress(paiHeader);
		    }
		    catch (LinphoneCoreException e)
		    {
			e.printStackTrace();
		    }
		}
		if (address == null || address.toString().toLowerCase().contains("unavailable"))
		{
		    address = call.getRemoteAddress();
		    SIPLogger.d(TAG, "mportal's--found Normal--Address" + address);
		}
		String userName = address.getUserName();
		if (userName == null)
		{
		    userName = ((address.getDisplayName() == null) ? "Unknown" : address.getDisplayName());
		}
		String incomingSipAddress = userName + '@' + call.getRemoteAddress().getDomain();
		String windowId = null;
		if (activityMessenger != null)
		{
		    SIPCallback callback = new SIPCallback(null, incomingSipAddress, incomingSipAddress);
		    if (!sipCallHashtable.containsKey(call))
		    {
			callback = findCallBack(callId);
			if (callback == null)
			{
			    sipCallHashtable.put(call, callback);
			}
			else
			{
			    windowId = callback.getWindowId();
			}
		    }
		    else
		    {
			callback = sipCallHashtable.get(call);
			windowId = callback.getWindowId();
		    }
		    // Meet me video feature
		    if (false)
		    {
			if (incomingSipAddress != null && incomingSipAddress.startsWith(Util.MEETME_PREFIX + "@" + SIPConstant.SIP_DOMAIN))
			{
			    SIPCallback meetMeCallBack = sipCallHashtable.get(call);
			    if (meetMeCallBack != null)
			    {
				Message msg = new Message();
				msg.what = SIPConstant.MEET_ME_VIDEO_UPDATE;
				msg.obj = call;

				Messenger messenger = meetMeCallBack.getMessenger();
				try
				{
				    messenger.send(msg);
				}
				catch (RemoteException e)
				{
				    e.printStackTrace();
				}
			    }
			    else
			    {
				SIPCoreManager.getInstance().acceptVideoCallWithParam(call, true);
			    }
			    // SIPCoreManager.getInstance().acceptVideoCallWithParam(call, true);

			}
		    }

		    Message m = new Message();
		    IncomingSIPCall sipCall = new IncomingSIPCall("linphone", call);
		    sipCall.setCallNumber(incomingSipAddress);
		    sipCall.setCallerName(userName);
		    sipCall.setCallState(SIPCallState.CallUpdatedByRemote.value());
		    if (callInfoHeader != null)
		    {
			SIPLogger.d(TAG, "mportal's--Call info header--" + callInfoHeader);
			sipCall.setCallInfoHeaderUrl(callInfoHeader);
		    }

		    sipCall.setWindowId(windowId);

		    m.obj = sipCall;
		    m.what = SIPConstant.UPDATE_CALL_FOR_VIDEO;
		    try
		    {
			mLc.deferCallUpdate(call);
		    }
		    catch (LinphoneCoreException e1)
		    {
			e1.printStackTrace();
		    }
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
	    else
	    {
		try
		{
		    SIPLogger.d("HARISH", "Callupdate came active audio call, So auto accpeting to keep session active");
		    mLc.acceptCallUpdate(call, call.getCurrentParamsCopy());
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}

	    }
	}
	else if (cstate == State.PausedByRemote)
	{
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.PausedByRemote.value(), message);
	}
	else
	{
	    sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.getStateValue(cstate.toString()).value(), message);
	}
    }

    private SIPCallback findCallBack(String callId)
    {
	if (sipCallHashtable == null)
	{
	    return null;
	}
	Enumeration<SIPCallback> enumCallback = sipCallHashtable.elements();
	while (enumCallback.hasMoreElements())
	{
	    SIPCallback temp = enumCallback.nextElement();
	    if (temp != null)
	    {
		if (temp.getWindowId().contains(callId))
		{
		    return temp;
		}
	    }
	}
	return null;
    }

    private String getWindowId(String value, String header)
    {
	String windowId = null;

	if (value != null && value.length() > 0)
	{
	    if (value != null && value.contains(","))
	    {
		String[] info = value.split(",");
		for (String str : info)
		{
		    if (str.contains(header) && str.split(header).length > 1)
		    {
			windowId = str.split(header)[1];
			break;
		    }
		}
	    }
	    else
	    {
		if (value.contains(header) && value.split(header).length > 1)
		{
		    windowId = value.split(header)[1];
		}
	    }
	}

	return windowId;
    }

    public String removePrefixCallNumber(String callNumber)
    {
	if (callNumber == null || callNumber.length() == 0)
	{
	    return callNumber;
	}
	String[] tempNumber = callNumber.split("@");
	if (tempNumber.length > 0)
	{
	    callNumber = tempNumber[0];
	}
	if (callNumber.startsWith("+1"))
	{
	    callNumber = callNumber.substring(2);
	}
	return callNumber;
    }

    // private void removeCallFromConfernce(String windowId, LinphoneCall call)
    // {
    // ArrayList<ParticipantBean> list =
    // windowDetails.get(windowId).getParticipantBean();
    // String userId = call.getRemoteAddress().getUserName();
    // for (ParticipantBean temp : list)
    // {
    // if (temp.getUserId().equalsIgnoreCase(userId))
    // {
    // temp.setLinphoneCall(null);
    // }
    //
    // }
    // windowDetails.get(windowId).setWindowCallType(WindowCallType.CONFERENCE_CALL);
    //
    // for (ParticipantBean temp : list)
    // {
    // mLc.resumeCall(temp.getLinphoneCall());
    // }
    //
    // }
    //
    // private void changeParticipantCallStatus(String windowId, CallStatus
    // status, LinphoneCall call)
    // {
    // String userId = call.getRemoteAddress().getUserName();
    //
    // ArrayList<ParticipantBean> list =
    // windowDetails.get(windowId).getParticipantBean();
    // for (ParticipantBean temp : list)
    // {
    // if (temp.getUserId().equalsIgnoreCase(userId))
    // {
    // temp.setCallStatus(status);
    // }
    // }
    //
    // }

    // private void addAllUsersWindowToConference(String windowId)
    // {
    // ArrayList<ParticipantBean> list =
    // windowDetails.get(windowId).getParticipantBean();
    // for (ParticipantBean temp : list)
    // {
    // mLc.addToConference(temp.getLinphoneCall());
    // }
    // windowDetails.get(windowId).setWindowCallType(WindowCallType.CONFERENCE_CALL);
    //
    // for (ParticipantBean temp : list)
    // {
    // mLc.resumeCall(temp.getLinphoneCall());
    // }
    //
    // }

    // private int isWindowQualifiedForConfernce(String windowId, LinphoneCall
    // call)
    // {
    // String userId = call.getRemoteAddress().getUserName();
    // ArrayList<ParticipantBean> list =
    // windowDetails.get(windowId).getParticipantBean();
    //
    // int count = 0;
    // for (ParticipantBean temp : list)
    // {
    // if (temp.getCallStatus() == CallStatus.IS_IN_STREAM)
    // {
    // count++;
    // }
    // }
    //
    // return count;
    // }

    private boolean isVoiceMailStreaming(LinphoneCall call)
    {
	String historyInfo = call.getRemoteParams().getCustomHeader("History-Info");
	if (historyInfo != null)
	{
	    try
	    {
		historyInfo = URLDecoder.decode(historyInfo, "UTF-8");
		historyInfo = historyInfo.toLowerCase();
		if (historyInfo.contains("cause=486") || historyInfo.contains("<sip:1000@"))
		{
		    return true;
		}
	    }
	    catch (UnsupportedEncodingException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return false;
    }

    private void showToastMsg(final String msg)
    {
	Handler handler = new Handler();
	handler.postDelayed(new Runnable()
	{

	    @Override
	    public void run()
	    {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	    }
	}, 100);

    }

    @Override
    public void callStatsUpdated(LinphoneCore lc, LinphoneCall call, LinphoneCallStats stats)
    {
	SIPLogger.d("", "SIP CALLBACK callStatsUpdated \t" + stats.toString());

    }

    @Override
    public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call, boolean encrypted, String authenticationToken)
    {
	SIPLogger.d("", "SIP CALLBACK callEncryptionChanged \t" + authenticationToken + "\t" + call.toString() + "\t" + encrypted);
    }

    @Override
    public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, RegistrationState cstate, String smessage)
    {
	SIPLogger.d("", "SIP CALLBACK registrationState \t" + lc.toString() + "\t" + cstate.toString() + "\t" + smessage);
	if (activityMessenger != null)
	{
	    Message m = new Message();
	    Bundle extraData = new Bundle();
	    ErrorInfo errorInfo = cfg.getErrorInfo();
	    if (errorInfo != null)
	    {
		m.obj = new Object[] { SIPRegistrationState.getStateValue(cstate.toString()), errorInfo.getProtocolCode(), errorInfo.getPhrase() };
	    }
	    else
	    {
		m.obj = new Object[] { SIPRegistrationState.getStateValue(cstate.toString()), 0, smessage };
	    }

	    m.what = SIPConstant.UPDATE_REGISTRATION_STATE_CHANGED;
	    if (cfg != null)
	    {
		m.arg1 = cfg.getError().getValue();
		if (cstate == RegistrationState.RegistrationNone)
		{
		    refreshRegistration = false;
		}
		else if (cstate == RegistrationState.RegistrationProgress)
		{

		    if (cfg.getError() == Reason.IOError)
		    {
			SIPLogger.d("REGISTER", " Processing IO error in Registration Progress");
			isIOErrForRegister = true;
			refreshRegistration = false;
			SIPCoreManager.getInstance().setPublishRequired(true);
			SIPCoreManager.getInstance().unsubscribeCustomEvent(true);
			SIPLogger.d("REGISTER", " Processing IO error in Registration Progress end");
		    }

		    if (!TextUtils.isEmpty(smessage))
		    {
			if (smessage.equalsIgnoreCase("Refresh registration"))
			{
			    refreshRegistration = true;
			}
		    }
		}
		else if (cstate == RegistrationState.RegistrationOk)
		{
		    int obtainedExpires = cfg.getObtainedExpires();
		    startRegisterAlarm(obtainedExpires);
		    SIPLogger.d("SipService", "DEBUG: obtained Expires " + obtainedExpires);
		    refreshRegistration = false;
		    SIPCoreManager.getInstance().clearRetryCount("REGISTER");
		    extraData.putBoolean("publish", false);

		    if (isIOErrForRegister)
		    {
			SIPCoreManager.getInstance().cancelSubscribeEventsInQueue();
			SIPLogger.d("HARISH", "IOERROR OCCURED, re subsubscribe");
			isIOErrForRegister = false;
			SIPCoreManager.getInstance().setPublishRequired(true);
			extraData.putBoolean("subscribe", true);
		    }

		    if (SIPCoreManager.getInstance().isPublishRequired())
		    {
			extraData.putBoolean("publish", true);
			SIPCoreManager.getInstance().setPublishRequired(false);
		    }

		}
		else if (cstate == RegistrationState.RegistrationFailed)
		{
		    cancelRegisterAlarm();
		    if (errorInfo != null)
		    {
			int errorCode = errorInfo.getProtocolCode();
			if (errorCode == 500) // yet to take care , rest of 6xx errors.
			{
			    if (refreshRegistration)
			    {
				if (sipInitRunnable != null)
				{
				    try
				    {
					SIPCoreManager.getInstance().incrementRetryCount("REGISTER");
					refreshRegistration = false;
					sipInitRunnable.initAccounts();
				    }
				    catch (LinphoneCoreException e)
				    {
					e.printStackTrace();
				    }
				}
			    }
			}
		    }
		    SIPCoreManager.getInstance().cancelSubscribeEventsInQueue();
		    refreshRegistration = false;
		}
		else if (cstate == RegistrationState.RegistrationCleared)
		{
		    refreshRegistration = false;
		    SIPCoreManager.getInstance().clearRetryCount("REGISTER");
		}
	    }

	    try
	    {
		m.setData(extraData);
		activityMessenger.send(m);
	    }
	    catch (RemoteException e)
	    {
		e.printStackTrace();
	    }
	}
	SIPLogger.d("REGISTER", " registrationState end");
    }

    private void startRegisterAlarm(int obtainedExpires)
    {
	cancelRegisterAlarm();
	lastRegisteredTimeStamp = System.currentTimeMillis();
	AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	if (alarmManager != null)
	{
	    SIPLogger.d("SIPService", "DEBUG: STARTING REGISTER REFRESH ALARM");

	    Intent t = new Intent(REGISTER_ALARM);
	    t.putExtra("obtainedExpires", obtainedExpires);
	    pAlarmIntent = PendingIntent.getService(getApplicationContext(), 100, t, PendingIntent.FLAG_UPDATE_CURRENT);

	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(lastRegisteredTimeStamp);

	    // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, pAlarmIntent);
	    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 600000, pAlarmIntent);
	}
    }

    private void cancelRegisterAlarm()
    {
	AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	if (alarmManager != null)
	{
	    SIPLogger.d("SIPService", "DEBUG: CANCEL REGISTER REFRESH ALARM");
	    if (pAlarmIntent != null)
	    {
		alarmManager.cancel(pAlarmIntent);
	    }
	}
    }

    @Override
    public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url)
    {
	SIPLogger.d("", "SIP CALLBACK newSubscriptionRequest \t" + lc.toString() + "\t" + lf.toString() + "\t" + url);
    }

    @Override
    public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf)
    {
	// SIPLogger.d("",
	// "SIP notifyPresenceReceived"+"Note--"+lf.getPresenceModel().getNote(null)+"--Activity--"+lf.getPresenceModel().getActivity().getDescription()+"--"+lf.getPresenceModel().getActivity().getType());
	SIPLogger.d("", "SIP CALLBACK notifyPresenceReceived \t" + lc.toString() + "\t" + lf.getAddress());
	if (activityMessenger != null)
	{
	    Message m = new Message();
	    String temp[] = lf.getAddress().asString().split(":");
	    SipFriend friend = new SipFriend();
	    friend.setAddress(temp[1].replace(">", "").trim());
	    PresenceModel newPresence = lf.getPresenceModel();
	    if (newPresence != null)
	    {
		PresenceBasicStatus basicStatus = newPresence.getBasicStatus();
		String presenceActivityStr = newPresence.getActivity().getDescription();
		if ((presenceActivityStr != null && presenceActivityStr.equalsIgnoreCase("unknown")) || newPresence.getActivity().getType() == PresenceActivityType.Unknown)
		{
		    if (basicStatus == PresenceBasicStatus.Open)
		    {
			friend.setStatus("Online");
		    }
		    else if (basicStatus == PresenceBasicStatus.Closed)
		    {
			friend.setStatus("Offline");
		    }
		    else
		    {
			friend.setStatus(lf.getStatus().toString());
		    }
		}
		else
		{
		    if (newPresence.getActivity().getType() == PresenceActivityType.Other && !(presenceActivityStr != null && presenceActivityStr.equalsIgnoreCase("Offline")))
		    {
			friend.setCustomePresence(true);

			friend.setCustomMessage(presenceActivityStr);
			if (basicStatus == PresenceBasicStatus.Open)
			{
			    friend.setStatus("away");
			}
			else
			{
			    friend.setStatus("busy");
			}
		    }
		    else
		    {
			if (presenceActivityStr != null)
			{
			    friend.setStatus(presenceActivityStr);
			}
			else
			{
			    friend.setStatus(lf.getStatus().toString());
			}
		    }
		}
	    }
	    else
	    {
		friend.setStatus(lf.getStatus().toString());
	    }
	    m.obj = friend;
	    m.what = SIPConstant.UPDATE_FRIEND_NOTIFY;
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

    @Override
    public void textReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneAddress from, String message)
    {
	SIPLogger.d("", "SIP CALLBACK textReceived \t" + lc.toString() + "\t" + cr.toString() + "\t" + from.toString() + "\t" + message);
    }

    @Override
    public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message)
    {
	SIPLogger.d("", "SIP CALLBACK messageReceived \t" + lc.toString() + "\t" + cr.toString() + "\t" + message);
	String incomingChatAddr = null;

	/* temp code to avoid Controle message */
	if (message != null)
	{
	    String msg = message.getText();
	    if (msg != null && msg.startsWith("CTRL|PRS"))
	    {
		return;
	    }
	}

	String partyId = message.getCustomHeader("x-nt-party-id");
	String sid = null;
	String port = null;
	String joinUrl = null;
	if (partyId != null)
	{
	    String header = "x-nt-mas-sid=";
	    int index = partyId.indexOf(header);
	    if (index != -1)
	    {
		index += header.length();
		sid = partyId.substring(index);
	    }
	    header = ";maddr=";
	    if (partyId.indexOf(header) != -1)
	    {
		port = partyId.substring(partyId.indexOf(':') + 1, partyId.indexOf(header));
	    }
	    header = "maddr=";
	    index = partyId.indexOf(header);
	    if (index != -1)
	    {
		index += header.length();
		int endIndex = partyId.indexOf('?', index);
		masIP = partyId.substring(index, endIndex);
	    }
	}
	if (port != null && masIP != null)
	{
	    joinUrl = "chat@" + SIPConstant.SIP_DOMAIN + ":" + port + ";maddr=" + masIP;
	}
	if (cr != null && cr.getPeerAddress() != null)
	{
	    try
	    {
		LinphoneCoreFactory.instance().createLinphoneAddress(message.getCustomHeader("From")).getDisplayName();
		String msg = message.getText();
		// TODO: temp need to check with mehmet on the right approach to
		// differentiate message from chatroom and user
		// we cannot hardcode domain name.
		// TODO: Gopal checked with mehmet and this is the condition

		if (msg.contains(CONVERSATION_INVITE_MESSAGE.trim()))
		{
		    processConversationInvite(message, msg);
		}
		else
		{
		    msg = message.getText().toLowerCase();

		    if (cr.getPeerAddress().getUserName().equals("chat")) // genband
		    {
			if (msg.endsWith("room already exists."))
			{
			    try
			    {
				int startIndex = msg.indexOf('"');
				startIndex++;
				int index = msg.indexOf('"', startIndex);
				String chatRoomName = msg.substring(startIndex, index);
				currentChatRooms.put(sid, chatRoomName);
				if (joinUrl != null && chatRoomName != null)
				{
				    maSIPList.put(chatRoomName, joinUrl);
				}
				Messenger messenger = chatrooms.get(chatRoomName);
				if (messenger != null)
				{
				    Message messageObj = new Message();
				    messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_EXISTS;
				    messageObj.obj = sid;
				    Bundle data = new Bundle();
				    data.putString("joinUrl", joinUrl);
				    messageObj.setData(data);
				    messenger.send(messageObj);
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.contains("room created.welcome")) // genband
			{
			    try
			    {
				// TODO: remove this and use chat room name from
				// header
				int startIndex = msg.indexOf('"');
				startIndex++;
				int index = msg.indexOf('"', startIndex);
				String chatRoomName = msg.substring(startIndex, index);

				currentChatRooms.put(sid, chatRoomName);
				if (joinUrl != null && chatRoomName != null)
				{
				    maSIPList.put(chatRoomName, joinUrl);
				}
				Messenger messenger = chatrooms.get(chatRoomName);
				if (messenger != null)
				{
				    Message messageObj = new Message();
				    messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_CREATED;
				    messageObj.obj = sid;
				    Bundle data = new Bundle();
				    data.putString("joinUrl", joinUrl);
				    messageObj.setData(data);
				    messenger.send(messageObj);
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.startsWith("welcome to the ")) // genband
			{
			    try
			    {
				String chatRoomName = currentChatRooms.get(sid);
				if (joinUrl != null && chatRoomName != null)
				{
				    maSIPList.put(chatRoomName, joinUrl);
				}
				Messenger messenger = chatrooms.get(chatRoomName);
				if (messenger != null)
				{
				    Message messageObj = new Message();
				    messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_ID;
				    messageObj.obj = sid;
				    Bundle data = new Bundle();
				    data.putString("joinUrl", joinUrl);
				    messageObj.setData(data);
				    messenger.send(messageObj);
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.startsWith("participants :"))
			{

			}
			else if (msg.endsWith("has left.")) // genband
			{
			    try
			    {
				String chatRoomName = currentChatRooms.get(sid);
				if (joinUrl != null && chatRoomName != null)
				{
				    maSIPList.put(chatRoomName, joinUrl);
				}
				int index = msg.indexOf('"', 1);
				if (index != -1)
				{
				    String username = msg.substring(1, index);
				    Messenger messenger = chatrooms.get(chatRoomName);
				    if (messenger != null)
				    {
					Message messageObj = new Message();
					messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_USER_LEFT;
					messageObj.obj = username;
					Bundle bundle = new Bundle();
					bundle.putLong("time", message.getTime());
					bundle.putString("joinUrl", joinUrl);
					messageObj.setData(bundle);
					messenger.send(messageObj);
				    }
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.toLowerCase().endsWith("goodbye.")) // genband
			{
			    try
			    {
				String chatRoomName = currentChatRooms.get(sid);
				if (chatRoomName != null)
				{
				    Messenger messenger = chatrooms.get(chatRoomName);
				    if (messenger != null)
				    {
					Message messageObj = new Message();
					if (joinUrl != null && chatRoomName != null)
					{
					    maSIPList.remove(chatRoomName);
					}
					if (msg.toLowerCase().equalsIgnoreCase("goodbye."))
					{
					    messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_GOODBYE;
					}
					else if (msg.toLowerCase().equalsIgnoreCase("your session has expired from inactivity.  goodbye."))
					{
					    messageObj.what = SIPConstant.UPDATE_PARTICIPANT_EXPIRY_GOODBYE;
					}
					// else if (msg.toLowerCase().equalsIgnoreCase("You have been kicked. Goodbye."))
					// {
					// messageObj.what = SIPConstant.UPDATE_PARTICIPANT_KICK_GOODBYE;
					// }
					Bundle bundle = new Bundle();
					bundle.putLong("time", message.getTime());
					bundle.putString("joinUrl", joinUrl);
					messageObj.setData(bundle);
					messenger.send(messageObj);
				    }
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.endsWith("use /go {room} to join a room or /room {room} to create a new room.")) // genband
			{
			    try
			    {
				String chatRoomName = currentChatRooms.get(sid);
				if (joinUrl != null && chatRoomName != null)
				{
				    maSIPList.put(chatRoomName, joinUrl);
				}
				if (chatRoomName != null)
				{
				    Messenger messenger = chatrooms.get(chatRoomName);
				    if (messenger != null)
				    {
					Message messageObj = new Message();
					messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_CLEAR_AND_CREATENEW;
					Bundle data = new Bundle();
					data.putString("joinUrl", joinUrl);
					messenger.send(messageObj);
				    }
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.endsWith("has joined.")) // genband
			{
			    try
			    {
				String chatRoomName = currentChatRooms.get(sid);
				int index = msg.indexOf('"', 1);
				if (index != -1)
				{
				    String sipAddress = null;
				    String username = msg.substring(1, index);
				    String[] split = msg.split("\\(");
				    if (split.length > 1)
				    {
					split = split[1].split("\\)");
					if (split.length > 1)
					{
					    sipAddress = split[0];
					}
				    }
				    Messenger messenger = chatrooms.get(chatRoomName);
				    if (joinUrl != null && chatRoomName != null)
				    {
					maSIPList.put(chatRoomName, joinUrl);
				    }
				    if (messenger != null)
				    {
					Message messageObj = new Message();
					messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_USER_JOINED;
					messageObj.obj = username;
					Bundle bundle = new Bundle();
					bundle.putLong("time", message.getTime());
					bundle.putString("joinUrl", joinUrl);
					if (sipAddress != null)
					{
					    bundle.putString("sipAddress", sipAddress);
					}
					messageObj.setData(bundle);
					messenger.send(messageObj);
				    }
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.contains("has invited you to join the ")) // genband
			{
			    try
			    {
				int index = msg.indexOf("has invited you to join the");
				index = msg.indexOf('"', index);
				index++;
				int endIndex = msg.indexOf('"', index);
				String chatRoomName = msg.substring(index, endIndex);
				currentChatRooms.put(sid, chatRoomName);
				if (joinUrl != null && chatRoomName != null)
				{
				    maSIPList.put(chatRoomName, joinUrl);
				}
				Message messageObj = new Message();
				messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_INVITE;
				messageObj.obj = sid + "|" + chatRoomName;
				activityMessenger.send(messageObj);
			    }
			    catch (Exception e)
			    {

			    }
			}
			else if (msg.contains("did not respond to your invitation")) // genband
			{
			    try
			    {
				String chatRoomName = currentChatRooms.get(sid);
				Messenger messenger = chatrooms.get(chatRoomName);
				SIPChat sipChat = new SIPChat();
				sipChat.setChatMsg(msg);
				sipChat.setRecvdTime(String.valueOf(System.currentTimeMillis()));
				// sipChat.setRecvdTime(String.valueOf(message.getTime()));
				sipChat.setStatusMessage(true);
				sipChat.setWindowId(chatRoomName);

				if (messenger != null)
				{
				    Message messageObj = new Message();
				    messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_STATUS_FAILED;
				    messageObj.obj = sipChat;
				    messenger.send(messageObj);
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.contains("room could not be created")) // genband
			{
			    String chatRoomName = currentChatRooms.get(sid);
			    Messenger messenger = chatrooms.get(chatRoomName);
			    SIPChat sipChat = new SIPChat();
			    sipChat.setChatMsg(msg);
			    sipChat.setRecvdTime(String.valueOf(message.getTime()));
			    sipChat.setStatusMessage(true);
			    sipChat.setWindowId(chatRoomName);

			    if (messenger != null)
			    {
				Message messageObj = new Message();
				messageObj.what = SIPConstant.UPDATE_CHAT_ROOM_STATUS_FAILED;
				messageObj.obj = sipChat;
				messenger.send(messageObj);
			    }

			}
			else if (msg.contains("users in the")) // genband
			{
			    String extractMsg = msg;
			    int idx = msg.indexOf("room:");
			    if (idx != -1)
			    {
				idx += "room:".length();
				extractMsg = msg.substring(idx);
				extractMsg = extractMsg.trim();
			    }

			    ArrayList<String> participantList = new ArrayList<String>();
			    String users[] = extractMsg.split(",");
			    for (int i = 0; i < users.length; i++)
			    {
				participantList.add(users[i].substring(users[i].indexOf("(") + 1, users[i].indexOf(")")));
			    }
			    try
			    {
				String chatRoomName = currentChatRooms.get(sid);
				Messenger messenger = chatrooms.get(chatRoomName);
				if (messenger != null)
				{
				    Message messageObj = new Message();
				    messageObj.what = SIPConstant.UPDATE_PARTICIPANTS_IN_CHAT_ROOM;
				    messageObj.obj = participantList;
				    messenger.send(messageObj);
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
			else if (msg.contains("available rooms")) // genband
			{
			    Messenger messenger = chatrooms.get(this.windowId);
			    if (messenger != null)
			    {
				Message messageObj = new Message();
				messageObj.what = SIPConstant.UPDATE_CHAT_MSG_REJOIN;
				currentChatRooms.put(sid, this.windowId);
				messageObj.obj = sid;
				Bundle data = new Bundle();
				data.putString("joinUrl", joinUrl);
				messenger.send(messageObj);
				return;
			    }
			}
			else if (msg.contains(":") && msg.contains("@" + SIPConstant.SIP_DOMAIN + ":"))
			{
			    int index = msg.indexOf(':');
			    incomingChatAddr = msg.substring(0, index);
			    handlePeerMessage(sid, cr, message, incomingChatAddr);
			}
		    }
		    else
		    {
			if (cr.getPeerAddress().getUserName().equals("chat"))
			{
			    int index = msg.indexOf(':');
			    incomingChatAddr = msg.substring(0, index);
			}
			else
			{
			    incomingChatAddr = cr.getPeerAddress().toString();
			}
			handlePeerMessage(sid, cr, message, incomingChatAddr);
		    }
		}

	    }
	    catch (Exception e)
	    {
	    }
	}
    }

    private void processConversationInvite(LinphoneChatMessage message, String msg) throws LinphoneCoreException
    {
	String header = message.getCustomHeader(WINDOW_ID_HEADER);

	if (header != null && header.length() > 0)
	{
	    String handleId = null;
	    String convId = null;

	    String[] convInfo = header.split(",");
	    for (String str : convInfo)
	    {
		if (str.contains("handle="))
		{
		    handleId = str.substring(str.indexOf("handle=") + "handle=".length());
		}

		if (str.contains("convid="))
		{
		    convId = str.substring(str.indexOf("convid=") + "convid=".length());
		}
	    }

	    String[] sessionInfo = msg.split("\n");
	    String joinInfo = "";
	    String chatRoomId = null;
	    String meetMeJoin = null;
	    String collabJoin = null;
	    String mediaBits = "0000";

	    for (String str : sessionInfo)
	    {
		String temp = null;
		if (str.contains("Chatroom"))
		{
		    temp = str.split("Chatroom")[1].trim();
		    if (temp.contains(":"))
		    {
			temp = temp.split(":")[1].trim();
		    }

		    chatRoomId = temp;
		    mediaBits = HandleManager.getInstance().getUpdatedMediaBits(mediaBits, 1, '1');
		}

		if (str.contains("SIP"))
		{
		    temp = str.split("SIP")[1].trim();
		    if (temp.contains(":"))
		    {
			temp = temp.split(":")[1].trim();
		    }
		    meetMeJoin = temp;
		    mediaBits = HandleManager.getInstance().getUpdatedMediaBits(mediaBits, 3, '1');
		}

		if (str.contains("Screen Share"))
		{

		    temp = str.split("Screen Share")[1].trim();
		    if (temp.contains(":"))
		    {
			temp = temp.split(":")[1].trim();
		    }
		    collabJoin = temp;
		    mediaBits = HandleManager.getInstance().getUpdatedMediaBits(mediaBits, 0, '1');
		}
	    }

	    if (collabJoin == null)
	    {
		joinInfo += "null" + HandleManagementConstants.DELIMETER;
	    }
	    else
	    {
		joinInfo += collabJoin + HandleManagementConstants.DELIMETER;
	    }

	    if (chatRoomId == null)
	    {
		joinInfo += "null" + HandleManagementConstants.DELIMETER;
	    }
	    else
	    {
		joinInfo += chatRoomId + HandleManagementConstants.DELIMETER;
	    }

	    if (meetMeJoin == null)
	    {
		joinInfo += "null" + HandleManagementConstants.DELIMETER;
	    }
	    else
	    {
		joinInfo += meetMeJoin + HandleManagementConstants.DELIMETER;
	    }

	    Messenger windowHandler = HandleManager.getInstance().getWindowcallBack(convId);

	    // if (msg.contains("You have a conference invitation."))
	    if (windowHandler == null && activityMessenger != null)
	    {
		Message m = new Message();
		m.what = SIPConstant.CONVERSATION_EVENT;
		m.arg1 = HandleManagementConstants.CONVERSATION_INVITE;
		LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(message.getCustomHeader("From"));
		if (address != null)
		{
		    String mesgObj = convId + HandleManagementConstants.DELIMETER + handleId + HandleManagementConstants.DELIMETER + mediaBits + HandleManagementConstants.DELIMETER + address.getUserName() + "@" + address.getDomain() + HandleManagementConstants.DELIMETER + joinInfo;
		    m.obj = mesgObj;
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
	    // else if (msg.contains("You have a session invitation."))
	    else if (windowHandler != null)
	    {
		Message m = new Message();
		m.what = SIPConstant.CONVERSATION_EVENT;
		m.arg1 = HandleManagementConstants.REINVITED_TO_SESSION;
		String mesgObj = joinInfo;
		m.obj = mesgObj;
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

    private void handlePeerMessage(String sid, LinphoneChatRoom cr, LinphoneChatMessage message, String incomingChatAddr)
    {

	if (activityMessenger != null && incomingChatAddr != null)
	{
	    Message msg = new Message();
	    SIPChat sipChat = new SIPChat();
	    sipChat.setErrorInfo(message.getErrorInfo());
	    LinphoneAddress resource = null;
	    try
	    {
		resource = LinphoneCoreFactory.instance().createLinphoneAddress(incomingChatAddr);
	    }
	    catch (LinphoneCoreException e1)
	    {
		e1.printStackTrace();

	    }
	    if (resource != null)
	    {
		sipChat.setCallingNumber(resource.getUserName() + '@' + resource.getDomain());
	    }
	    else
	    {
		sipChat.setCallingNumber(incomingChatAddr);
	    }

	    String windowId = getWindowId(message.getCustomHeader(WINDOW_ID_HEADER), "convid=");
	    if (windowId == null && sid != null)
	    {
		windowId = currentChatRooms.get(sid);
		sipChat.setSid(sid);
	    }
	    String sender = getWindowId(message.getCustomHeader(WINDOW_ID_HEADER), "sender=");
	    if (!TextUtils.isEmpty(sender))
	    {
		sipChat.setCallingNumber(sender);
	    }

	    sipChat.setWindowId(windowId);
	    String chatMsg = null;
	    if (sid != null)
	    {
		int idx = message.getText().indexOf(":");
		if (idx != -1)
		{
		    chatMsg = message.getText().substring(idx + 1);
		    sipChat.setChatMsg(chatMsg);
		}
	    }
	    else
	    {
		sipChat.setChatMsg(message.getText());
	    }

	    sipChat.setChatStatus(message.getStatus().value());
	    // Long timeInMilliSec = message.getTime();
	    Long timeInMilliSec = System.currentTimeMillis();
	    if (dupChatMsg != null && dupChatMsgRecivedTime != null)
	    {
		long timeDiff = timeInMilliSec.longValue() - dupChatMsgRecivedTime.longValue();
		if (timeDiff < MIN_CHAT_DURATION && dupChatMsg.equalsIgnoreCase(message.getText()))
		{
		    return;
		}
	    }

	    if (chatMsg != null && chatMsg.length() > 0)
	    {
		dupChatMsg = chatMsg;
	    }
	    else
	    {
		dupChatMsg = message.getText();
	    }
	    dupChatMsgRecivedTime = message.getTime();

	    if (timeInMilliSec != null)
	    {
		DateFormat df = new SimpleDateFormat("dd:LL:yyyy");
		if (df != null)
		{
		    sipChat.setRecvdDate(df.format(timeInMilliSec).toString());
		}
		df = new SimpleDateFormat("hh:mm:ss aa");
		if (df != null)
		{
		    sipChat.setRecvdTime(df.format(timeInMilliSec).toString());
		}
	    }
	    sipChat.setSentMessage(false);
	    sipChat.setRecvdTime(String.valueOf(System.currentTimeMillis()));
	    String callInfoHeaderVal = message.getCustomHeader("Call-Info");
	    if (!TextUtils.isEmpty(callInfoHeaderVal))
	    {
		callInfoHeaderVal = callInfoHeaderVal.trim();
		callInfoHeaderVal = callInfoHeaderVal.replaceAll("<sp:", "");
		callInfoHeaderVal = callInfoHeaderVal.replaceAll(">", "");
	    }
	    sipChat.setCallInfoHeader(callInfoHeaderVal);
	    msg.obj = sipChat;
	    msg.what = SIPConstant.INCOMING_CHAT_MESSAGE_RECIEVED;
	    try
	    {
		Message controllerMsg = new Message();
		controllerMsg.copyFrom(msg);
		controllerMessenger.send(controllerMsg);

		final Message finalMsg = msg;
		tempHandler.postDelayed(new Runnable()
		{

		    @Override
		    public void run()
		    {
			try
			{
			    if (activityMessenger != null && finalMsg != null)
			    {
				activityMessenger.send(finalMsg);
			    }
			}
			catch (RemoteException e)
			{
			    e.printStackTrace();
			}
		    }
		}, 500);
	    }
	    catch (RemoteException e)
	    {
		e.printStackTrace();
	    }
	}

    }

    @Override
    public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf)
    {
	try
	{
	    lc.playDtmf((char) dtmf, 500);
	    SIPLogger.d("", "SIP CALLBACK dtmfReceived \t" + lc.toString() + "\t" + call.toString() + "\t" + dtmf);
	}
	catch (Exception e)
	{

	}

    }

    @Override
    public void ecCalibrationStatus(LinphoneCore lc, EcCalibratorStatus status, int delay_ms, Object data)
    {
	try
	{
	    SIPLogger.d("", "SIP CALLBACK ecCalibrationStatus \t" + lc.toString() + "\t" + status.toString() + "\t" + delay_ms);

	    // int lenght = PreferenceUtil.getPrefInt(getApplicationContext(), SIPConstant.SIP_ECHO_LENGTH_VALUE, 0);
	    String manufacturer = "";
	    String model = "";
	    String platform = "";
	    int flags = 0;
	    int delay = 0;
	    int recommended_rate = 0;
	    // for (int index = 0; index < lenght; index++)
	    // {
	    // manufacturer = PreferenceUtil.getPrefString(getApplicationContext(), SIPConstant.SIP_ECHO_MANUFACTURE_VALUE + index, "");
	    // model = PreferenceUtil.getPrefString(getApplicationContext(), SIPConstant.SIP_ECHO_MODEL_VALUE + index, "");
	    // platform = PreferenceUtil.getPrefString(getApplicationContext(), SIPConstant.SIP_ECHO_PLATFORM_VALUE + index, "");
	    // delay = PreferenceUtil.getPrefInt(getApplicationContext(), SIPConstant.SIP_ECHO_DELAY_VALUE + index, 0);
	    // flags = PreferenceUtil.getPrefInt(getApplicationContext(), SIPConstant.SIP_ECHO_FLAGS_VALUE + index, 0);
	    // recommended_rate = PreferenceUtil.getPrefInt(getApplicationContext(), SIPConstant.SIP_ECHO_RECOMMENDED_RATE_VALUE + index, 0);
	    // SIPLogger.d("SIPService", "Adding device to addSoundDeviceDesc() manufacturer: " + manufacturer + " model: " + model + "  platform: " + platform
	    // + " flags:" + flags + " delay: " + delay + " recommended_rate: " + recommended_rate);
	    // MediastreamerAndroidContext.addSoundDeviceDesc(manufacturer, model, platform, flags, delay_ms, recommended_rate);
	    //
	    // }

	    // ArrayList<EchoSetting> echoSettingsList = SIPCoreManager.getInstance().getEchoSettingsList();
	    // if (echoSettingsList != null)
	    // {
	    // for (EchoSetting echoSetting : echoSettingsList)
	    // {
	    // // (String manufacturer, String model, String platform, int flags, int delay, int recommended_rate)
	    //
	    // manufacturer = echoSetting.getManufacturer();
	    // model = echoSetting.getModel();
	    // platform = echoSetting.getPlatform();
	    // flags = LibUtils.parseInteger(echoSetting.getFlags());
	    // delay = LibUtils.parseInteger(echoSetting.getDelay());
	    // recommended_rate = LibUtils.parseInteger(echoSetting.getRecommended_rate());
	    //
	    // SIPLogger.d("SIPService", "Adding device to addSoundDeviceDesc() manufacturer: " + manufacturer + " model: " + model + "  platform: " + platform
	    // + " flags:" + flags + " delay: " + delay + " recommended_rate: " + recommended_rate);
	    // MediastreamerAndroidContext.addSoundDeviceDesc(manufacturer, model, platform, flags, delay_ms, recommended_rate);
	    // }
	    //
	    // }
	    // else
	    {

		manufacturer = android.os.Build.MANUFACTURER;
		model = android.os.Build.MODEL;
		platform = getPlatform();
//		if (TextUtils.isEmpty(LibUtils.getRecommandedrate()))
//		{
//		    recommended_rate = 16000;
//		}
//		else
//		{
//		    recommended_rate = LibUtils.parseInteger(LibUtils.getRecommandedrate());
//		}
		recommended_rate = 16000;
		SIPLogger.d("SIPService", "Adding device to properties addSoundDeviceDesc() manufacturer: " + manufacturer + " model: " + model + "  platform: " + platform + " flags:" + flags + " delay: " + delay + " recommended_rate: " + recommended_rate);
		MediastreamerAndroidContext.addSoundDeviceDesc(manufacturer, model, platform, flags, delay_ms, recommended_rate);
		// // Add by default ...
		// SIPLogger.d("SIPService", "Added default device as LGE Nexus5 16000 and Samsung SM-G900H 16000");
		// MediastreamerAndroidContext.addSoundDeviceDesc("LGE", "Nexus 5", "msm8974", 0, delay_ms, 16000);
		// MediastreamerAndroidContext.addSoundDeviceDesc("samsung", "SM-G900H", "exynos5", 0, delay_ms, 16000);
	    }

	    // MediastreamerAndroidContext.addSoundDeviceDesc("LGE", "Nexus 5", "msm8974", 0, delay_ms, 8000);
	    // MediastreamerAndroidContext.addSoundDeviceDesc("samsung", "SM-G900H", "exynos5", 0, delay_ms, 8000);

	}
	catch (Exception e)
	{

	}

    }

    private String getPlatform()
    {
	Process p = null;
	String board_platform = "";
	try
	{
	    p = new ProcessBuilder("/system/bin/getprop", "ro.board.platform").redirectErrorStream(true).start();
	    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line = "";
	    while ((line = br.readLine()) != null)
	    {
		board_platform += line;
	    }
	    p.destroy();
	}
	catch (IOException e)
	{
	    if (p != null)
	    {
		p.destroy();
	    }
	    e.printStackTrace();
	}
	return board_platform;
    }

    @Override
    public void notifyReceived(LinphoneCore lc, LinphoneCall call, LinphoneAddress from, byte[] event)
    {
	try
	{
	    SIPLogger.d("", "SIP CALLBACK notifyReceived \t" + lc.toString() + "\t" + call.toString() + "\t" + from.toString());
	}
	catch (Exception e)
	{

	}

    }

    @Override
    public void show(LinphoneCore lc)
    {
	SIPLogger.d("", "SIP CALLBACK show \t" + lc.toString());
    }

    @Override
    public void displayStatus(LinphoneCore lc, String message)
    {
	SIPLogger.d("", "SIP CALLBACK displayStatus \t" + lc.toString() + "\t" + message);
    }

    @Override
    public void displayMessage(LinphoneCore lc, String message)
    {
	SIPLogger.d("", "SIP CALLBACK displayMessage \t" + lc.toString() + "\t" + message);
    }

    @Override
    public void displayWarning(LinphoneCore lc, String message)
    {
	SIPLogger.d("", "SIP CALLBACK displayWarning \t" + lc.toString() + "\t" + message);
    }

    private boolean preCheckWifi()
    {
	if (true)
	{
	    return true;
	}
	if (mConnectivityManager == null)
	{
	    Context context = getApplicationContext();
	    if (context != null)
	    {
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    }
	}

	if (mConnectivityManager == null)
	{
	    SIPLogger.d("SIPService", "Connectivity Manager is null");
	    return true;
	}

	NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
	boolean state = false;
	if (info != null)
	{
	    if (!(info.getState() == NetworkInfo.State.CONNECTED || info.getState() == NetworkInfo.State.CONNECTING))
	    {
		state = false;
	    }
	    else
	    {
		state = true;
	    }
	}
	if (!state)
	{
	    Message m = new Message();
	    m.what = SIPConstant.UPDATE_NETWORK_ERROR;
	    try
	    {
		if (activityMessenger != null)
		{
		    activityMessenger.send(m);
		}
	    }
	    catch (RemoteException e)
	    {
		e.printStackTrace();
	    }
	}
	return state;
    }

    @Override
    // protected void onHandleIntent(Intent intent)
    // @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
	if (intent == null)
	{
	    return START_STICKY;
	}

	String action = intent.getAction();
	SIPLogger.d("SmartOffice", "SmartOffice Version " + SIPConstant.APP_VERSION + " action: " + action);
	if (action.equalsIgnoreCase(INIT_SIP))
	{
	    reInitializeString();
	    SIPCoreManager.getInstance().setSIPCoreState(SIPCoreState.SIPCORE_INIT_STARTED);
	    initLinphoneCore(getApplicationContext());
	    startEchoCalibration(getApplicationContext());
	    SIPCoreManager.getInstance().setSIPCoreState(SIPCoreState.SIPCORE_INIT_COMPLETED);

	    // Notification notification = new Notification(R.drawable.icon, getText(R.string.app_name), System.currentTimeMillis());
	    // Intent notificationIntent = new Intent();
	    // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	    // notification.setLatestEventInfo(this, getText(R.string.app_name), "Connected", pendingIntent);
	    // startForeground(33333333, notification);
	}
	else if (action.equalsIgnoreCase(REGISTER_ALARM))
	{
	    int obtainedExpires = intent.getIntExtra("obtainedExpires", 0);
	    if (obtainedExpires > 0)
	    {
		long nextRegisterTime = lastRegisteredTimeStamp + (obtainedExpires * 1000);
		long diff = nextRegisterTime - System.currentTimeMillis();
		if (diff > 0)
		{
		    SIPLogger.d("SIPService", "DEBUG: REGISTER REFRESH CHECKING ");
		    if (diff <= 600000)
		    // if (System.currentTimeMillis() > lastRegisteredTimeStamp + (5 * 60000))
		    {
			SIPLogger.d("SIPService", "DEBUG: REGISTER REFRESH CHECKING - DOING refresh REGISTER");
			if (mLc != null)
			{
			    mLc.refreshRegisters();
			}
		    }
		}
	    }
	}
	else if (action.equalsIgnoreCase(REGISTER_SIP))
	{
	    SIPLogger.d("ss", "backend + REGISTER SIP message");
	    if (!preCheckWifi())
	    {
		return START_STICKY;
	    }

	    if (intent != null)
	    {
		activityMessenger = intent.getParcelableExtra("activityMessenger");
		HandleManager.getInstance().initCallBacks(getApplicationContext(), eventPublisher, activityMessenger);
		controllerMessenger = intent.getParcelableExtra("controllerMessenger");
	    }

	    if (sipInitRunnable != null)
	    {
		SIPCoreManager.getInstance().setPublishRequired(true);
		tempHandler.postDelayed(new Runnable()
		{

		    @Override
		    public void run()
		    {
			try
			{
			    sipInitRunnable.initAccounts();
			}
			catch (LinphoneCoreException e)
			{
			    e.printStackTrace();
			}
		    }
		}, 1000);

	    }

	}
	else if (action.equalsIgnoreCase(UNREGISTER_SIP))
	{
	    boolean logout = intent.getBooleanExtra("logout", false);
	    boolean killCore = intent.getBooleanExtra("killcore", false);
	    if (sipInitRunnable != null)
	    {
		// SIPCoreManager.getInstance().cancelSubscribeEventsInQueue();
		// SIPCoreManager.getInstance().unsubscribeCustomEvent();
		sipInitRunnable.displayProxyConfig("before logout from SIPService");
		HandleManager.getInstance().clearConversationEvent();
		sipInitRunnable.removeAccounts();
		if (logout || killCore)
		{
		    sipInitRunnable.unregisterAllReceivers();
		}
		sipInitRunnable.displayProxyConfig("after logout from SIPService");
	    }

	    if (!killCore)
	    {
		if (activityMessenger != null)
		{
		    Message message = new Message();
		    message.what = SIPConstant.UNREGISTER_SIP_DONE;
		    try
		    {
			activityMessenger.send(message);
		    }
		    catch (RemoteException e)
		    {
			e.printStackTrace();
		    }
		}
	    }
	    // if (mLc.getCallsNb() > 0)
	    // {
	    // mLc.terminateConference();
	    // mLc.terminateAllCalls();
	    // }
	    /*
	     * tempHandler.postDelayed(new Runnable() {
	     * 
	     * @Override public void run() { if (mTimer != null) { mTimer.cancel(); mTimer = null; } mLc.destroy(); mLc = null; } }, 1000);
	     */
	    if (logout)
	    {
		tempHandler.postDelayed(new Runnable()
		{

		    @Override
		    public void run()
		    {
			Message message = new Message();
			message.what = SIPConstant.LOGOUT_DONE;
			try
			{
			    if (activityMessenger != null)
			    {
				activityMessenger.send(message);
			    }
			}
			catch (RemoteException e)
			{
			    e.printStackTrace();
			}
			if (sipInitRunnable != null)
			{
			    sipInitRunnable.clearAuth();
			}
			activityMessenger = null;
			stopSelf();
		    }
		}, 1000);
	    }

	    if (killCore && !logout)
	    {
		if (sipInitRunnable != null)
		{
		    sipInitRunnable.clearAuth();
		}
		killCore(true);

	    }

	}
	else if (action.equalsIgnoreCase(PUBLISH_EVENTS))
	{
	    if (eventPublisher != null)
	    {
		eventPublisher.handleEvents(intent);
	    }
	}
	else if (action.equalsIgnoreCase(CONVERSATION_INVITATION))
	{
	    if (eventPublisher != null)
	    {
		eventPublisher.sendInvites(intent, this);
	    }
	}
	else if (action.equalsIgnoreCase(HANDLE_MGMT_ADD_CALLBACK))
	{
	    String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
	    Messenger messenger = intent.getParcelableExtra(HandleManagementConstants.WINDOW_MESSENGER);
	    if (windowId != null && messenger != null)
	    {
		HandleManager.getInstance().addWindowCallBack(windowId, messenger);
	    }
	}
	else if (action.equalsIgnoreCase(HANDLE_MGMT_REMOVE_CALLBACK))
	{
	    String windowId = intent.getStringExtra(HandleManagementConstants.WINDOW_ID);
	    if (windowId != null)
	    {
		HandleManager.getInstance().removeWindowCallBack(windowId);
	    }
	}
	else if (action.equalsIgnoreCase(ACCEPT_SWITCH_VIDEO))
	{
	    if (!preCheckWifi())
	    {
		return START_STICKY;
	    }
	    String address = intent.getStringExtra("address");
	    Messenger messenger = intent.getParcelableExtra("callback");
	    boolean enableVideo = intent.getBooleanExtra("enable", false);
	    Enumeration<SIPCallback> sipCallEnum = sipCallHashtable.elements();
	    String participantId = intent.getStringExtra("participantid");
	    String windowId = intent.getStringExtra("windowId");

	    SIPCallback foundCallback = null;
	    if (sipCallEnum != null)
	    {
		while (sipCallEnum.hasMoreElements())
		{
		    SIPCallback callback = sipCallEnum.nextElement();
		    String callNumber = callback.getCallNumber();
		    callNumber = Util.removePrefixCallNumber(callNumber);
		    address = Util.removePrefixCallNumber(address);
		    if (callNumber != null && callNumber.equalsIgnoreCase(address))// */&& windowId.equalsIgnoreCase(callback.getWindowId()))
		    {
			foundCallback = callback;
			if (callback.getMessenger() == null)
			{
			    callback.setMessanger(messenger);
			    callback.setParticipantId(participantId);
			}
			break;
		    }
		}

		if (foundCallback == null)
		{
		    sipCallEnum = sipCallHashtable.elements();
		    if (sipCallEnum != null)
		    {
			while (sipCallEnum.hasMoreElements())
			{
			    SIPCallback callback = sipCallEnum.nextElement();
			    String callNumber = callback.getCallNumber();
			    callNumber = Util.removePrefixCallNumber(callNumber);
			    address = Util.removePrefixCallNumber(address);
			    if (callNumber != null /* && callNumber.equalsIgnoreCase(address) */&& callback.getWindowId().contains(callNumber))
			    {
				foundCallback = callback;
				if (callback.getMessenger() == null)
				{
				    callback.setMessanger(messenger);
				    callback.setParticipantId(participantId);
				}
				break;
			    }
			}
		    }
		}
	    }

	    Enumeration<LinphoneCall> callKeyEnum = sipCallHashtable.keys();
	    if (callKeyEnum != null)
	    {
		while (callKeyEnum.hasMoreElements())
		{
		    final LinphoneCall lCall = callKeyEnum.nextElement();
		    // LinphoneAddress lAddress = lCall.getRemoteAddress();
		    // String incomingSipAddress = lAddress.getUserName() + "@" + lCall.getRemoteAddress().getDomain();
		    // if (incomingSipAddress.equalsIgnoreCase(address) || (incomingSipAddress.indexOf(address) != -1))
		    SIPCallback tempCallBack = sipCallHashtable.get(lCall);
		    if (tempCallBack.equals(foundCallback))
		    {
			if (enableVideo)
			{
			    boolean isVideoEnabled = mLc.isVideoEnabled();
			    if (!isVideoEnabled)
			    {
				SIPCoreManager.getInstance().acceptVideoCallWithParam(lCall, false);
				return START_STICKY;
			    }
			    if (lCall.getRemoteParams().getVideoEnabled())
			    {
				sendMessage(lCall, getMessage(SIPConstant.UPDATE_CALL_STATE_INITIALIZE_VIDEO), 0, "initvideo");
				tempHandler.postDelayed(new Runnable()
				{

				    @Override
				    public void run()
				    {
					// mLc.enableVideo(true, true);
					SIPCoreManager.getInstance().acceptVideoCallWithParam(lCall, true);
				    }
				}, 300);

			    }
			    else
			    {
				SIPCoreManager.getInstance().acceptVideoCallWithParam(lCall, false);
			    }
			}
			else
			{
			    SIPCoreManager.getInstance().acceptVideoCallWithParam(lCall, false);
			}
			break;
		    }
		}
	    }
	}
	else if (action.equalsIgnoreCase(ACCEPT_INCOMING_CALL))
	{
	    if (!preCheckWifi())
	    {
		return START_STICKY;
	    }
	    String address = intent.getStringExtra("address");
	    boolean acceptVideoEnabled = intent.getBooleanExtra("videoEnabled", false);
	    Messenger messenger = intent.getParcelableExtra("callback");
	    Enumeration<SIPCallback> sipCallEnum = sipCallHashtable.elements();
	    String recordFile = intent.getStringExtra("recordFile");
	    String participantId = intent.getStringExtra("participantid");
	    String windowId = intent.getStringExtra("windowId");

	    if (sipCallEnum != null)
	    {
		while (sipCallEnum.hasMoreElements())
		{
		    SIPCallback callback = sipCallEnum.nextElement();
		    String callNumber = callback.getCallNumber();
		    callNumber = Util.removePrefixCallNumber(callNumber);
		    address = Util.removePrefixCallNumber(address);
		    if (callNumber != null && callNumber.equalsIgnoreCase(address) && windowId.equalsIgnoreCase(callback.getWindowId()))
		    {
			if (callback.getMessenger() == null)
			{
			    callback.setMessanger(messenger);
			    callback.setParticipantId(participantId);
			}
			break;
		    }
		}
	    }

	    Enumeration<LinphoneCall> callKeyEnum = sipCallHashtable.keys();
	    if (callKeyEnum != null)
	    {
		ArrayList<LinphoneCall> removeList = new ArrayList<LinphoneCall>();
		while (callKeyEnum.hasMoreElements())
		{
		    final LinphoneCall lCall = callKeyEnum.nextElement();
		    LinphoneAddress lAddress = lCall.getRemoteAddress();
		    if (lAddress == null)
		    {
			SIPLogger.d("sipstack", "remote address is null , please verify the header" + lCall);
		    }
		    String incomingSipAddress = lAddress.getUserName() + "@" + lAddress.getDomain();
		    if (address.indexOf('@') == -1)
		    {
			incomingSipAddress = lAddress.getUserName();
		    }

		    if ((incomingSipAddress.equalsIgnoreCase(address) || (incomingSipAddress.indexOf(address) != -1)) && lCall.getState() == State.IncomingReceived)
		    {
			try
			{
			    LinphoneCallParams remoteParams = lCall.getRemoteParams();
			    boolean videoEnabled = false;
			    if (remoteParams != null)
			    {
				videoEnabled = remoteParams.getVideoEnabled();

				SIPLogger.d("SIPService", "remoteParams.getSessionName(): " + remoteParams.getSessionName());
			    }
			    if (recordFile != null || videoEnabled)
			    {
				LinphoneCallParams params = mLc.createDefaultCallParameters();
				// Added extra parameter .. like call is accepted using p2g client and has the video capability feature..
				params.setSessionName("p2g");
				// lCall.getCurrentParamsCopy();
				if (recordFile != null)
				{
				    params.setRecordFile(recordFile);
				}
				SIPLogger.d("ssss", "gopal " + "\t total call :" + mLc.getCallsNb() + "\t" + "Max Call: " + mLc.getMaxCalls());
				if (videoEnabled)
				{
				    params.setVideoEnabled(acceptVideoEnabled);
				    SIPCoreManager.getInstance().getLinphoneCore().acceptCallWithParams(lCall, params);
				    // mLc.enableVideo(videoEnabled,
				    // videoEnabled);
				    // sendMessage(lCall,
				    // getMessage(SIPConstant.UPDATE_CALL_STATE_INITIALIZE_VIDEO),
				    // 0);
				    // tempHandler.postDelayed(new Runnable()
				    // {
				    //
				    // @Override
				    // public void run()
				    // {
				    // try
				    // {
				    // //
				    // SIPCoreManager.getInstance().getLinphoneCore().acceptCall(lCall);
				    // SIPCoreManager.getInstance().getLinphoneCore().acceptCallWithParams(lCall,
				    // params);
				    // }
				    // catch (LinphoneCoreException e)
				    // {
				    // e.printStackTrace();
				    // }
				    // }
				    // }, 300);
				}
				else
				{
				    params.setVideoEnabled(videoEnabled);
				    SIPCoreManager.getInstance().getLinphoneCore().acceptCallWithParams(lCall, params);
				}
			    }
			    else
			    {
				SIPCoreManager.getInstance().getLinphoneCore().acceptCall(lCall);
			    }
			}
			catch (LinphoneCoreException e)
			{
			    e.printStackTrace();
			}
			break;
		    }
		    else
		    {
			State st = lCall.getState();
			if (st == State.CallEnd || st == State.CallReleased)
			{
			    removeList.add(lCall);
			}
		    }
		}

		for (LinphoneCall removeItem : removeList)
		{
		    sipCallHashtable.remove(removeItem);
		}
	    }

	}
	else if (action.equalsIgnoreCase(START_CALL))
	{
	    if (!preCheckWifi())
	    {
		return START_STICKY;
	    }
	    String address = intent.getStringExtra("address");

	    SIPLogger.d("SipService", "Virus99 address--" + address);
	    Messenger messenger = intent.getParcelableExtra("callback");
	    String windowId = intent.getStringExtra("windowId");
	    String recordFile = intent.getStringExtra("recordFile");
	    String participantId = intent.getStringExtra("participantid");

	    boolean videoEnableByDefault = intent.getBooleanExtra("videocall", false);
	    boolean isPrivacyEnanbled = intent.getBooleanExtra("privacy", false);
	    String loggedinUser = intent.getStringExtra("loggedInUser");
	    boolean proxyChange = intent.getBooleanExtra("proxyChange", false);
	    ArrayList<SIPCustomHeader> customHeaderList = intent.getParcelableArrayListExtra("customheader");
	    if (mLc != null)
	    {
		LinphoneAddress lAddress = null;
		try
		{
		    lAddress = mLc.interpretUrl(address);
		    SIPLogger.d("SipService", "Virus99 lAddress--" + lAddress);
		}
		catch (LinphoneCoreException e1)
		{
		    e1.printStackTrace();
		}
		if (lAddress != null)
		{
		    LinphoneCallParams params = mLc.createDefaultCallParameters();
		    if (isPrivacyEnanbled)
		    {
			params.setPrivacy(LinphonePrivacyMask.LinphonePrivacyId.getValue());
			// params.addCustomHeader("P-Asserted-Identity", "sip:" + loggedinUser);
		    }
		    if (windowId != null)
		    {
			params.addCustomHeader(WINDOW_ID_HEADER, "convid=" + windowId);
		    }
		    if (customHeaderList != null)
		    {
			for (SIPCustomHeader customHeader : customHeaderList)
			{
			    params.addCustomHeader(customHeader.getHeaderName(), customHeader.getHeaderValue());
			}
		    }

		    params.setAudioBandwidth(0);
		    if (recordFile != null)
		    {
			params.setRecordFile(recordFile);
		    }
		    SIPLogger.d("@@@@@@@@@@@@@", "Video is enabled during  outgoing call : " + videoEnableByDefault);
		    params.setVideoEnabled(videoEnableByDefault);
		    // Added extra parameter .. like call is accepted using p2g client and has the video capability feature..
		    params.setSessionName("p2g");
		    // params.enableLowBandwidth(false);
		    LinphoneProxyConfig defaultProxy = null;
		    try
		    {
			if (proxyChange)
			{
			    defaultProxy = mLc.getDefaultProxyConfig();
			    String proxyIdxStr = PreferenceUtil.getPrefString(getApplicationContext(), "pref_tn_proxy_idx", "0");
			    String sipIDWithNick = PreferenceUtil.getPrefString(getApplicationContext(), "pref_username_key", null);

			    int idx = Util.parseInt(proxyIdxStr);
			    LinphoneProxyConfig config = mLc.getProxyConfigList()[idx];
			    mLc.setDefaultProxyConfig(config);

			    String identity = mLc.getDefaultProxyConfig().getIdentity();
			    String contactPrimaryName = null;
			    String contactDisplayName = null;
			    if (!TextUtils.isEmpty(identity))
			    {
				String pattern = "(.*)<";
				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(identity);

				while (m.find())
				{
				    contactDisplayName = m.group(1).trim();
				}

				pattern = "<(.*)>";
				p = Pattern.compile(pattern);
				m = p.matcher(identity);
				while (m.find())
				{
				    contactPrimaryName = m.group(1);
				}
			    }
			    if (!TextUtils.isEmpty(contactPrimaryName) && !TextUtils.isEmpty(contactDisplayName))
			    {
				if (contactPrimaryName.startsWith("sip:"))
				{
				    contactPrimaryName = contactPrimaryName.substring(4);
				}
				contactPrimaryName = contactPrimaryName.split("@")[0];
				if (!TextUtils.isEmpty(sipIDWithNick))
				{
				    mLc.setPrimaryContact(sipIDWithNick, sipIDWithNick);
				}
				else
				{
				    mLc.setPrimaryContact(contactDisplayName, contactPrimaryName);
				}
			    }

			}

			params.addCustomHeader("P-Preferred-Identity", mLc.getDefaultProxyConfig().getIdentity());

			LinphoneCall call = mLc.inviteAddressWithParams(lAddress, params);
			String data = call.getCurrentParamsCopy().getCustomHeader("initializevideo");
			SIPLogger.d("@@@@@@@@@@", "wwwwwwwwwwwwww" + data);
			boolean videoEnabled = call.getCurrentParamsCopy().getVideoEnabled();
			SIPCallback callback = new SIPCallback(messenger, windowId, address);
			callback.setParticipantId(participantId);
			sipCallHashtable.put(call, callback);
			sendMessage(call, getMessage(SIPConstant.UPDATE_CALL_STATE_CHANGED), SIPCallState.OutgoingInit.value(), "localinit");
		    }
		    catch (LinphoneCoreException e)
		    {
			e.printStackTrace();
		    }
		    finally
		    {
			if (defaultProxy != null)
			{
			    mLc.setDefaultProxyConfig(defaultProxy);
			}
		    }
		}
	    }
	}
	else if (action.equalsIgnoreCase(REFER_CALL))
	{
	    if (!preCheckWifi())
	    {
		return START_STICKY;
	    }
	    LinphoneCall callToTransfer = null;
	    LinphoneCall destination = null;
	    String toTransfer = intent.getStringExtra("CallToTransfer");
	    String dstAdd = "conference@a2trail.com";
	    SIPLogger.d(TAG, "REFER_CALL : " + toTransfer);
	    LinphoneCall[] allCalls = mLc.getCalls();
	    for (LinphoneCall call : allCalls)
	    {
		if (toTransfer.startsWith(call.getRemoteAddress().getUserName()))
		{
		    callToTransfer = call;
		}
		else if (dstAdd.startsWith(call.getRemoteAddress().getUserName()))
		{
		    destination = call;
		}
	    }
	    mLc.transferCall(callToTransfer, conferenceContactId);
	}

	else if (action.equalsIgnoreCase(CHAT_MESSAGE))
	{
	    if (!preCheckWifi())
	    {
		return START_STICKY;
	    }
	    String callInfo = intent.getStringExtra("callInfo");
	    String senderId = intent.getStringExtra("senderId");
	    String userId = intent.getStringExtra("userId");
	    String msg = intent.getStringExtra("chatMsg");
	    String windowId = intent.getStringExtra("windowId");
	    Messenger messenger = intent.getParcelableExtra("callback");
	    String sid = intent.getStringExtra("sid");
	    ArrayList<SIPCustomHeader> customHeaderList = intent.getParcelableArrayListExtra("customheader");
	    boolean proxyChange = intent.getBooleanExtra("proxyChange", false);
	    boolean isPrivacyEnanbled = intent.getBooleanExtra("privacy", false);

	    if (maSIPList != null && windowId != null && !maSIPList.containsKey(windowId))
	    {
		if (masIP != null && userId.contains("chat@"))
		{
		    userId = userId + ";maddr=" + masIP;
		}
	    }
	    else if (maSIPList != null && windowId != null)
	    {
		userId = maSIPList.get(windowId);
	    }
	    if (msg != null && userId != null && mLc != null)
	    {
		if (msg.equals("/bye") && chatrooms != null && windowId != null)
		{
		    chatrooms.remove(windowId);
		}
		if (msg.contains("/rooms"))
		{
		    if (currentChatRooms.containsKey(sid))
		    {
			currentChatRooms.remove(sid);
		    }
		    this.windowId = windowId;
		    sid = null;
		}
		LinphoneChatRoom room = mLc.getOrCreateChatRoom(userId);
		if (messenger != null)
		{
		    if (chatrooms.containsKey(windowId))
		    {
			chatrooms.remove(windowId);
		    }
		    chatrooms.put(windowId, messenger);
		}
		LinphoneChatMessage mLcMsg = room.createLinphoneChatMessage(msg);
		StringBuffer buf = new StringBuffer();
		if (windowId != null)
		{
		    buf.append("convid=" + windowId);
		    if (!TextUtils.isEmpty(senderId))
		    {
			buf.append(",sender=" + senderId);
		    }
		    mLcMsg.addCustomHeader(WINDOW_ID_HEADER, buf.toString());
		}

		if (customHeaderList != null)
		{
		    for (SIPCustomHeader customHeader : customHeaderList)
		    {
			mLcMsg.addCustomHeader(customHeader.getHeaderName(), customHeader.getHeaderValue());
		    }
		}
		if (!TextUtils.isEmpty(callInfo))
		{
		    mLcMsg.addCustomHeader(CALL_INFO_ID_HEADER, "<sp:" + callInfo + ">");
		}
		else
		{
		    mLcMsg.addCustomHeader(CALL_INFO_ID_HEADER, "<sp:" + userId + ">");
		}

		if (sid != null)
		{
		    mLcMsg.addCustomHeader("x-nt-mas-sid", sid);
		}
		LinphoneProxyConfig defaultProxy = null;
		if (proxyChange)
		{
		    defaultProxy = mLc.getDefaultProxyConfig();
		    String proxyIdxStr = PreferenceUtil.getPrefString(getApplicationContext(), "pref_tn_proxy_idx", "0");
		    String sipIDWithNick = PreferenceUtil.getPrefString(getApplicationContext(), "pref_username_key", null);

		    int idx = Util.parseInt(proxyIdxStr);
		    LinphoneProxyConfig config = mLc.getProxyConfigList()[idx];
		    mLc.setDefaultProxyConfig(config);
		    String contactPrimaryName = null;
		    String contactDisplayName = null;
		    String identity = mLc.getDefaultProxyConfig().getIdentity();
		    if (!TextUtils.isEmpty(identity))
		    {
			String pattern = "(.*)<";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(identity);

			while (m.find())
			{
			    contactDisplayName = m.group(1).trim();
			}

			pattern = "<(.*)>";
			p = Pattern.compile(pattern);
			m = p.matcher(identity);
			while (m.find())
			{
			    contactPrimaryName = m.group(1);
			}
		    }
		    if (!TextUtils.isEmpty(contactPrimaryName) && !TextUtils.isEmpty(contactDisplayName))
		    {
			if (contactPrimaryName.startsWith("sip:"))
			{
			    contactPrimaryName = contactPrimaryName.substring(4);
			}
			contactPrimaryName = contactPrimaryName.split("@")[0];

			if (!TextUtils.isEmpty(sipIDWithNick))
			{
			    mLc.setPrimaryContact(sipIDWithNick, sipIDWithNick);
			}
			else
			{
			    mLc.setPrimaryContact(contactDisplayName, contactPrimaryName);
			}
		    }
		}
		if (isPrivacyEnanbled)
		{
		    // mLcMsg.add(LinphonePrivacyMask.LinphonePrivacyId.getValue());
		}
		mLcMsg.addCustomHeader("P-Preferred-Identity", mLc.getDefaultProxyConfig().getIdentity());
		room.sendMessage(mLcMsg, this);

		if (defaultProxy != null)
		{
		    mLc.setDefaultProxyConfig(defaultProxy);
		}
	    }
	}

	else if (action.equalsIgnoreCase(END_CALL))
	{
	    if (!preCheckWifi())
	    {
		return START_STICKY;
	    }
	    if (mLc.isIncall())
	    {
		mLc.terminateCall(mLc.getCurrentCall());
	    }
	}
	else if (action.equalsIgnoreCase(NETWORK_STATE_CHANGE))
	{
	    boolean state = intent.getBooleanExtra("connectionstate", false);
	    setConnectivityChanged(state);
	    Message msg = new Message();
	    msg.what = SIPConstant.NOTIFY_NETWORK_STATUS;
	    msg.obj = state;
	    if (activityMessenger != null)
	    {
		try
		{
		    activityMessenger.send(msg);
		}
		catch (RemoteException e)
		{
		    e.printStackTrace();
		}
	    }
	}
	else if (action.equalsIgnoreCase(CUSTOM_FEATURE))
	{
	    handleCustomFeature(intent);
	}
	else if (action.equalsIgnoreCase(SCREEN_STATE_CHANGE))
	{
	    boolean state = intent.getBooleanExtra("screenstate", false);
	    if (mLc != null)
	    {
		mLc.enableKeepAlive(state);
	    }
	}
	else if (action.equalsIgnoreCase(PUBLISH_SESSION))
	{
	    // mLc.publish();
	    Publish p = intent.getParcelableExtra("publish");
	    String xmlBody = writeXml(p);
	    LinphoneContent body = LinphoneCoreFactory.instance().createLinphoneContent("application".toString(), "active-conversations+xml".toString(), xmlBody);
	    // LinphoneAddress resource =
	    // LinphoneCoreFactory.instance().createLinphoneAddress("conversation",
	    // "a2trial.com", "");
	    // LinphoneAddress resource =
	    // LinphoneCoreFactory.instance().createLinphoneAddress("conversation",
	    // "smartoffice", "");
	    LinphoneAddress resource = LinphoneCoreFactory.instance().createLinphoneAddress(SIPConstant.SIP_CONVERSATION_ID, SIPConstant.SIP_CONVERSATION_DOMAIN, "");
	    mLc.publish(resource, "active-conversations", 300, body);
	}
	else if (false && action.equalsIgnoreCase(ADD_TO_CONFERENCE))
	{
	    if (currentCalls.size() > 1)
	    {
		for (LinphoneCall lCall : currentCalls)
		{
		    if (!lCall.isInConference())
		    {
			SIPLogger.d(TAG, "Adding To conference : " + lCall.getRemoteAddress().getUserName());
			mLc.addToConference(lCall);
		    }

		}
		mLc.enterConference();
	    }
	    SIPLogger.d(TAG, "Conference size Add Conf: " + mLc.getConferenceSize());

	}
	else if (false && action.equalsIgnoreCase(LEAVE_CONFERENCE))
	{
	    if (currentCalls.size() > 1)
	    {

		for (LinphoneCall lCall : currentCalls)
		{
		    mLc.pauseCall(lCall);
		}
		for (final LinphoneCall lCall : currentCalls)
		{
		    if (lCall.isInConference())
		    {
			// mLc.leaveConference();
			SIPLogger.d(TAG, "Leaving conference : " + lCall.getRemoteAddress().getUserName());
			// if(mLc.getConferenceSize() != 1)
			{
			    mLc.removeFromConference(lCall);
			}
		    }

		    /*
		     * tempHandler.postDelayed(new Runnable() {
		     * 
		     * @Override public void run() { if (lCall.getState() != State.Paused && lCall.getState() != State.Pausing) { mLc.pauseCall(lCall); }
		     * 
		     * } }, 1000);
		     */

		    /*
		     * else { if (lCall.getState() != State.Paused && lCall.getState() != State.Pausing) { mLc.pauseCall(lCall); } }
		     */
		}
		SIPLogger.d(TAG, "Conference size in LEAVE COnf: " + mLc.getConferenceSize());
	    }

	}
	else if (action.equalsIgnoreCase(SUBSCRIBE_HM_ACTIVE_CONVERSATION))
	{
	    if (intent != null)
	    {
		boolean doSubScribe = intent.getBooleanExtra("subscribe", false);
		if (doSubScribe)
		{
		    HandleManager.getInstance().subscribeForActiveConversation();
		}
		else
		{
		    HandleManager.getInstance().unSubscribeFromActiveConversation();
		}

	    }
	}
	return START_STICKY;
    }

    private void handleCustomFeature(Intent intent)
    {
	int id = intent.getIntExtra("customOpId", 0);
	if (id == SIPConstant.SIP_CUSTOM_MODIFY_MESSENGER)
	{
	    int customOpId = intent.getIntExtra("customOpId", 0);
	    if (customOpId == SIPConstant.SIP_CUSTOM_MODIFY_MESSENGER)
	    {
		String windowId = intent.getStringExtra("windowId");
		String participantId = intent.getStringExtra("participantId");
		Messenger messenger = intent.getParcelableExtra("messenger");
		if (messenger == null)
		{
		    return;
		}
		if (sipCallHashtable != null)
		{
		    Enumeration<SIPCallback> enumCallback = sipCallHashtable.elements();
		    while (enumCallback.hasMoreElements())
		    {
			SIPCallback callback = enumCallback.nextElement();
			if (callback != null)
			{
			    String callBackParticipantId = callback.getParticipantId();
			    String callWindowId = callback.getWindowId();

			    if (callBackParticipantId != null && callWindowId != null)
			    {
				if (callBackParticipantId.equalsIgnoreCase(participantId) && callWindowId.equalsIgnoreCase(windowId))
				{
				    callback.setMessanger(messenger);
				}
			    }
			}
		    }
		}
	    }
	}
    }

    private void reInitializeString()
    {
	// PARTICIPANT_INVITE_MESSAGE = "You have a conference invitation. Click here to join.sip:" + Util.MEETME_PREFIX + "@" + SIPConstant.SIP_DOMAIN + "?";
	//
	// ADD_SESSION_TO_CONV_MESSAGE = "A new session is added to the conversation. Click here to join.sip:" + Util.MEETME_PREFIX + "@" +
	// SIPConstant.SIP_DOMAIN + "?";
	//
	// PARTICIPANT_SESSION_INVITE_MESSAGE = "You have a session invitation. Click here to join.sip:" + Util.MEETME_PREFIX + "@" + SIPConstant.SIP_DOMAIN +
	// "?";
    }

    @Override
    public void onLinphoneChatMessageStateChanged(LinphoneChatMessage msg, LinphoneChatMessage.State state)
    {
	SIPLogger.d("", "SIP CHAT MSG : " + msg.getText() + " \\chatRoom: " + getWindowId(msg.getCustomHeader(WINDOW_ID_HEADER), "convid=") + "\tState " + state.toString());
	try
	{
	    String chatRoomName = getWindowId(msg.getCustomHeader(WINDOW_ID_HEADER), "convid=");
	    // String userName = (msg.getCustomHeader(USER_ID_HEADER) != null) ?
	    // msg.getCustomHeader(USER_ID_HEADER) : " ";
	    String userName = msg.getPeerAddress().getUserName();
	    Bundle bundle = new Bundle();
	    bundle.putString("user", userName);
	    bundle.putString("message", msg.getText());
	    bundle.putLong("time", msg.getTime());
	    bundle.putInt("chatStatus", state.toInt());

	    Messenger messenger = chatrooms.get(chatRoomName);
	    Message messageObj = new Message();

	    if (msg != null)
	    {
		ErrorInfo info = msg.getErrorInfo();
		if (info != null)
		{
		    int statusCode = info.getProtocolCode();
		    if (statusCode == 403)
		    {
			mLc.refreshRegisters();
		    }
		}
	    }

	    if (state.toString().equals(LinphoneChatMessage.State.NotDelivered.toString()))
	    {
		if (messenger != null)
		{
		    messageObj.what = SIPConstant.CHAT_MSG_NOT_DELIVERED;
		    messageObj.setData(bundle);
		    messenger.send(messageObj);
		}
	    }
	    else if (state.toString().equals(LinphoneChatMessage.State.Delivered.toString()))
	    {
		if (messenger != null)
		{
		    messageObj.what = SIPConstant.CHAT_MSG_DELIVERED;
		    messageObj.setData(bundle);
		    messenger.send(messageObj);
		}
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void transferState(LinphoneCore lc, LinphoneCall call, State new_call_state)
    {
	SIPLogger.d("HARISH", "transferState- ForCall " + call.getRemoteAddress().getUserName() + "  State : " + new_call_state.toString());

	if (new_call_state.value() == SIPCallState.Connected.value() || new_call_state.value() == SIPCallState.Error.value())
	{
	    sendMessage(call, getMessage(SIPConstant.UPDATE_TRANSFER_STATE), new_call_state.value(), "transferstate");
	}
    }

    @Override
    public void infoReceived(LinphoneCore lc, LinphoneCall call, LinphoneInfoMessage info)
    {
	SIPLogger.d("HARISH", "info Recived for call ");
	if (call != null && call.getRemoteAddress() != null && info != null && info.getContent() != null)
	{
	    SIPLogger.d("Info", "Info message content: " + call.getRemoteAddress().getUserName() + "  INFO String : " + info.getContent().getDataAsString());
	}
    }

    @Override
    public void subscriptionStateChanged(LinphoneCore lc, final LinphoneEvent ev, SubscriptionState state)
    {
	int errorCode = 0;
	if (ev != null)
	{
	    ErrorInfo info = ev.getErrorInfo();
	    if (info != null)
	    {
		errorCode = info.getProtocolCode();
	    }
	}
	SIPLogger.d("00", "subscriptionStateChanged out state : " + state + "errorCode : " + errorCode);
	if (state == SubscriptionState.Expiring)
	{
	    if (ev == HandleManager.getInstance().getSubscribeLpEvent())
	    {
		SIPLogger.d(HandleManager.TAG, "subscriptionStateChanged to Expiring: Refreshing the subscription");
		HandleManager.getInstance().subscribeForActiveConversation();
	    }
	    else
	    {
		SIPLogger.d("HARISH", "subscriptionStateChanged is expiring : going to update the subscribe");
		SIPCoreManager.getInstance().updateSubscribeCustomEvent(ev);
	    }
	    return;
	}

	if (state == SubscriptionState.Error)
	{
	    SIPLogger.d("HARISH", "subscriptionStateChanged state : " + state + "errorCode : " + errorCode);
	    if (errorCode == 503 || errorCode == 504 || errorCode == 408)
	    {
		// service unavailable . get the retry value if not retry value then set 1800 sec as default as per FS doc 2.10.6 Subscription Failure
		String retryAfter = ev.getCustomHeader(RETRY_AFTER_ID_HEADER);
		SIPLogger.d("Phone2Go", "subscriptionStateChanged Retry-After value : " + retryAfter);
		int retryInterval = subscriptionRetryInterval;
		if (TextUtils.isEmpty(retryAfter))
		{
		    SIPLogger.d("Phone2Go", "No Retry-After value so setting to default value as 1800 sec..: " + " default value (Milli Sec): " + retryInterval);
		    // retry value is not specified. so set the default value.
		    retryInterval = subscriptionRetryInterval;
		}
		else
		{
		    retryInterval = Integer.parseInt(retryAfter);
		}
		SIPLogger.d("Phone2Go", "Retry value in ((Milli Sec)): " + retryInterval);
		SIPEvent sipEvent = SIPCoreManager.getInstance().getSubscribeSIPEvent(ev);
		if (sipEvent != null)
		{
		    if (sipEvent.isInitialSubscribe())
		    {
			// it is initial subscription request .. retry after sometime..
			boolean ret = SIPCoreManager.getInstance().reSubscribeCustomEvent(ev, retryInterval);
			if (!ret)
			{
			    SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
			}
			return;

		    }
		}
	    }
	    else if (errorCode == 401)
	    {
		// If a subscription fails due to either subscription expiring (e.g. due to refresh failure) or to the receipt of a 481 in response to a
		// SUBSCRIBE
		// request to refresh, the UE shall consider the subscription invalid and start a new initial subscription according to RFC 3265 [28].
		SIPCoreManager.getInstance().subscribeCustomEvent(true);
		return;

	    }
	    else if (errorCode == 481)
	    {
		int retryCount = SIPCoreManager.getInstance().getRetryCount(ev.getEventName());
		if (retryCount == -1 || retryCount == 0)
		{
		    SIPCoreManager.getInstance().incrementRetryCount(ev.getEventName());
		    boolean ret = SIPCoreManager.getInstance().reSubscribeCustomEvent(ev, 2000);
		    if (!ret)
		    {
			SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
			SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
		    }
		}
		else if (retryCount == 1)
		{
		    SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
		    SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
		    boolean ret = SIPCoreManager.getInstance().reSubscribeCustomEvent(ev, customSubscribeRetryInterval);
		    if (!ret)
		    {
			SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
			SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
		    }
		}
		return;
	    }

	    if (ev != null && ev.getReason() != null)
	    {
		Reason reason = ev.getReason();
		// String erroReason = reason.toString();

		// SIPLogger.d("HARISH", "subscriptionStateChanged erroReason : " + erroReason);
		// if (reason.getValue() == Reason.NoMatch.getValue())
		// {
		// SIPLogger.d("HARISH", "subscriptionStateChanged Reason : NO Match : supposed to be 481 Error code");
		// SIPCoreManager.getInstance().updateSubscribeCustomEvent(ev);
		// }
		// else
		if (reason.getValue() == Reason.IOError.getValue())
		{
		    SIPLogger.d("HARISH", "subscriptionStateChanged Reason : IOError");
		    SIPCoreManager.getInstance().reSubscribeCustomEvent(ev, customSubscribeRetryInterval);
		}
		else
		{
		    SIPLogger.d("HARISH", "subscriptionStateChanged : Reason : " + reason.getValue() + "\t Event Name" + ev.getEventName());
		    // SIPCoreManager.getInstance().updateSubscribeCustomEvent(ev);
		}
	    }
	    SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
	}
	else if (state == SubscriptionState.OutoingProgress)
	{
	    if (errorCode == 408 || errorCode == 503)
	    {
		int retryCount = SIPCoreManager.getInstance().getRetryCount(ev.getEventName());
		if (retryCount == -1 || retryCount == 0)
		{
		    SIPCoreManager.getInstance().incrementRetryCount(ev.getEventName());
		    boolean ret = SIPCoreManager.getInstance().reSubscribeCustomEvent(ev, 2000);
		    if (!ret)
		    {
			SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
			SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
		    }
		}
		else if (retryCount == 1)
		{
		    SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
		    SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
		    boolean ret = SIPCoreManager.getInstance().reSubscribeCustomEvent(ev, customSubscribeRetryInterval);
		    if (!ret)
		    {
			SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
			SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
		    }
		}
		return;
	    }
	}
	else if (state == SubscriptionState.Active)
	{
	    SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), false);
	    SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
	    SIPCoreManager.getInstance().doActionOnSubscriptionActive(ev, activityMessenger);
	}
	else if (state == SubscriptionState.Terminated)
	{
	    SIPCoreManager.getInstance().setSubscribeRequired(ev.getEventName(), true);
	    if (errorCode == 0 || errorCode == 200)
	    {
		SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
	    }

	    boolean subscribeWith0 = false;
	    if (SIPCoreManager.getInstance().getSIPOPState() != SIPOPState.SIPOP_DEACTIVATION && SIPCoreManager.getInstance().getSIPOPState() != SIPOPState.SIPOP_LOGOUT && SIPCoreManager.getInstance().getSIPOPState() != SIPOPState.SIPOP_OFFLINE)
	    {
		subscribeWith0 = true;
		SIPCoreManager.getInstance().reSubscribeCustomEvent(ev, 0);
	    }

	    if (errorCode == 0 || errorCode == 200)
	    {
		// SIPCoreManager.getInstance().clearRetryCount(ev.getEventName());
		if (subscribeWith0)
		{
		    if (tempHandler != null)
		    {
			tempHandler.postDelayed(new Runnable()
			{
			    public void run()
			    {
				SIPCoreManager.getInstance().removeCustomEvent(ev.getEventName());
			    }
			}, 200);
		    }
		    else
		    {
			SIPCoreManager.getInstance().removeCustomEvent(ev.getEventName());
		    }
		}
		else
		{
		    SIPCoreManager.getInstance().removeCustomEvent(ev.getEventName());
		}
	    }
	    else if (errorCode == 503)
	    {
		SIPCoreManager.getInstance().removeCustomEvent(ev.getEventName());
	    }

	    // try
	    // {
	    // // if the device remotely deactivated sometimes this event is triggered. So handled this case as well.
	    // Message m = new Message();
	    // Reason reason = ev.getReason();
	    // String erroReason = reason.toString();
	    // m.obj = new Object[] { state, erroReason };
	    // m.what = SIPConstant.UPDATE_SUBSCRIBE_STATE_CHANGED;
	    // // user deactivated device remotely.
	    // activityMessenger.send(m);
	    // }
	    // catch (Exception e)
	    // {
	    // }
	}
    }

    @Override
    public void notifyReceived(LinphoneCore lc, LinphoneEvent ev, String eventName, LinphoneContent content)
    {

	String message = " notifyReceived:  " + eventName;
	SIPLogger.d("mahantesh", "notifyReceived: " + eventName);
	if (content != null)
	{
	    // SIPLogger.d("mahantesh", "content" + content.getDataAsString());
	}

	if (eventName.equalsIgnoreCase("active-conversations"))
	{
	    if (content != null && content.getEncoding() != null && content.getEncoding().equalsIgnoreCase("gzip") && content.getData() != null)
	    {
		// message = unzipData1(content.getData());
		try
		{
		    InputStream in = new GZIPInputStream(new ByteArrayInputStream(content.getData()));
		    ByteArrayOutputStream bout = new ByteArrayOutputStream();
		    byte[] buffer = new byte[102400];
		    int length;

		    while (true)
		    {
			length = in.read(buffer);
			if (length == -1)
			{
			    SIPLogger.d(HandleManager.TAG, "Length is -1 after reading " + bout.size());
			    break;
			}
			bout.write(buffer, 0, length);
		    }
		    String str = new String(bout.toByteArray(), "UTF-8");
		    message = str;
		    SIPLogger.d(HandleManager.TAG, "Completed reading " + str.length());
		    bout.close();
		    in.close();
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		SIPLogger.d(HandleManager.TAG, "notifyReceived " + message);
		System.out.println(message);
		SIPLogger.d(HandleManager.TAG, "notifyReceived size" + message.length());
		HandleManager.getInstance().parseXML(lc, ev, eventName, message);
	    }
	    else if (content != null && content.getDataAsString() != null)
	    {
		message = content.getDataAsString();
		SIPLogger.d(HandleManager.TAG, "notifyReceived " + content.getDataAsString());
		HandleManager.getInstance().parseXML(lc, ev, eventName, message);
	    }
	    // if (activityMessenger != null)
	    // {
	    // Message m = new Message();
	    // m.what = 909090;
	    // m.obj = message;
	    // try
	    // {
	    // activityMessenger.send(m);
	    // }
	    // catch (RemoteException e)
	    // {
	    // e.printStackTrace();
	    // }
	    // }
	}
	else if (eventName.equalsIgnoreCase("presence") || eventName.equalsIgnoreCase("reg"))
	{
	    SIPLogger.d("", "presence" + content.getData().toString());
	    SIPEvent event = SIPCoreManager.getInstance().updateCustomEvent(eventName, content);
	    if (event == null)
	    {
		event = new SIPEvent();
		event.setEventName(eventName);
		SIPLogger.d("event", "eventName is null so createing new SIP event object");
		// if (tempEvent != null && tempEvent.equals(event))
		{
		    event.setNotifyContent(content.getData());
		    event.setNotifySubContentType(content.getSubtype());
		    event.setNotifyContentType(content.getType());
		    event.setNotifyContentEncoding(content.getEncoding());
		    // SIPLogger.d("event", "found event and added content");
		}
	    }
	    if (event != null)
	    {
		SIPLogger.d("event", "event is not null");
		if (activityMessenger != null)
		{
		    SIPLogger.d("event", "activityMessenger is not null");
		    Message msg = new Message();
		    msg.what = SIPConstant.EVENT_NOTIFY_RECEIVED;
		    msg.obj = event;
		    try
		    {
			SIPLogger.d("event", "activityMessenger sending a message");
			activityMessenger.send(msg);
		    }
		    catch (RemoteException e)
		    {
			e.printStackTrace();
		    }
		}
		else
		{
		    SIPLogger.d("event", "activityMessenger is null");
		}
	    }
	    else
	    {
		SIPLogger.d("event", "event is null");
	    }
	}
    }

    private void parseConversation(LinphoneCore lc, LinphoneEvent ev, String eventName, LinphoneContent content)
    {
	/*
	 * DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); List<Message> messages = new ArrayList<Message>(); try { DocumentBuilder
	 * builder = factory.newDocumentBuilder(); Document dom = builder.parse(content.getDataAsString()); Element root = (Element) dom.getDocumentElement();
	 * NodeList items = root.getElementsByTagName(ITEM); for (int i=0;i<items.getLength();i++){ Message message = new Message(); Node item = items.item(i);
	 * NodeList properties = item.getChildNodes(); for (int j=0;j<properties.getLength();j++){ Node property = properties.item(j); String name =
	 * property.getNodeName(); if (name.equalsIgnoreCase(TITLE)){ message.setTitle(property.getFirstChild().getNodeValue()); } else if
	 * (name.equalsIgnoreCase(LINK)){ message.setLink(property.getFirstChild().getNodeValue()); } else if (name.equalsIgnoreCase(DESCRIPTION)){
	 * StringBuilder text = new StringBuilder(); NodeList chars = property.getChildNodes(); for (int k=0;k<chars.getLength();k++){
	 * text.append(chars.item(k).getNodeValue()); } message.setDescription(text.toString()); } else if (name.equalsIgnoreCase(PUB_DATE)){
	 * message.setDate(property.getFirstChild().getNodeValue()); } } messages.add(message); } } catch (Exception e) { throw new RuntimeException(e); }
	 * return messages;
	 */
    }

    private String writeXml(Publish p)
    {
	XmlSerializer serializer = Xml.newSerializer();
	StringWriter writer = new StringWriter();
	try
	{
	    serializer.setOutput(writer);
	    serializer.startDocument("UTF-8", true);
	    for (Add sadd : p.mAddSessionDetails)
	    {
		serializer.startTag("", "add");
		serializer.attribute("", "handle", sadd.handleId);
		for (session s : sadd.sessionDetails)
		{
		    serializer.startTag("", "sessions");
		    serializer.startTag("", s.sessionType);
		    serializer.attribute("", "id", s.sessionId.toString());
		    serializer.attribute("", "handle", String.valueOf(sadd.handleId));
		    serializer.attribute("", "join", s.joinStr);
		    serializer.attribute("", "state", s.state);
		    serializer.endTag("", s.sessionType);
		    serializer.endTag("", "sessions");
		}
	    }
	    serializer.endTag("", "add");
	    for (Modify mod : p.mModifySessionDetails)
	    {
		serializer.startTag("", "modify");
		serializer.attribute("", "handle", mod.handleId);
		for (participants p1 : mod.participantsDetails)
		{
		    serializer.startTag("", "participants");
		    serializer.startTag("", "par");
		    serializer.attribute("", "id", p1.id.toString());
		    serializer.attribute("", "media", p1.media);
		    serializer.endTag("", "par");
		    serializer.endTag("", "participants");
		}
	    }
	    serializer.endTag("", "modify");
	    serializer.endDocument();
	    return writer.toString();
	}
	catch (Exception e)
	{
	    throw new RuntimeException(e);
	}
    }

    @Override
    public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev, PublishState state)
    {
	int errorCode = 0;
	String reasonPhrase = null;
	if (ev != null)
	{
	    ErrorInfo info = ev.getErrorInfo();
	    if (info != null)
	    {
		errorCode = info.getProtocolCode();
		reasonPhrase = info.getPhrase();
	    }
	}
	SIPLogger.d("publish ", "publishStateChanged " + errorCode + " \t" + reasonPhrase);
	// HandleManager.getInstance().publishStateChanged(lc, ev, state);
	if (ev.getEventName().equalsIgnoreCase("presence") || ev.getEventName().equalsIgnoreCase("vq-rtcpxr"))
	{
	    if (state == PublishState.Error || state == PublishState.Cleared || state == PublishState.None)
	    {
		if (ev.getEventName().equalsIgnoreCase("presence"))
		{
		    SIPCoreManager.getInstance().setPublishRequired(true);
		}
	    }

	    if (activityMessenger != null)
	    {
		Message msg = new Message();
		msg.obj = ev;
		msg.arg1 = state.getValue();
		msg.what = SIPConstant.UPDATE_PUBLISH_STATE_CHANGE;

		try
		{
		    activityMessenger.send(msg);
		}
		catch (RemoteException e)
		{
		    e.printStackTrace();
		}
	    }
	}
    }

    @Override
    public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr)
    {
	String incomingChatAddr = cr.getPeerAddress().toString();
	if (activityMessenger != null && incomingChatAddr != null)
	{
	    Message msg = new Message();
	    SIPChat sipChat = new SIPChat();
	    LinphoneAddress resource = null;
	    try
	    {
		resource = LinphoneCoreFactory.instance().createLinphoneAddress(incomingChatAddr);
	    }
	    catch (LinphoneCoreException e1)
	    {
		e1.printStackTrace();

	    }
	    if (resource != null)
	    {
		sipChat.setCallingNumber(resource.getUserName() + '@' + resource.getDomain());
	    }
	    else
	    {
		sipChat.setCallingNumber(incomingChatAddr);
	    }
	    sipChat.setComposeStatus(cr.isRemoteComposing());

	    msg.obj = sipChat;
	    msg.what = SIPConstant.IM_COMPOSE_STATE_CHANGE;

	    if (activityMessenger != null && msg != null)
	    {
		try
		{
		    activityMessenger.send(msg);
		}
		catch (RemoteException e)
		{
		    e.printStackTrace();
		}
	    }
	}

    }

    @Override
    public void configuringStatus(LinphoneCore lc, RemoteProvisioningState state, String message)
    {

    }

    private void startEchoCalibration(Context mContext)
    {

	// AudioManager mAudioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	// int oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
	// int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
	// mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);
	try
	{
	    mLc.startEchoCalibration(null);
	}
	catch (LinphoneCoreException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	// mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, oldVolume, 0);
    }

    @Override
    public void fileTransferProgressIndication(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, int progress)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public void fileTransferRecv(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, String buffer, int size)
    {
	// TODO Auto-generated method stub

    }

    @Override
    public int fileTransferSend(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, ByteBuffer buffer, int size)
    {
	// TODO Auto-generated method stub
	return 0;
    }
}
