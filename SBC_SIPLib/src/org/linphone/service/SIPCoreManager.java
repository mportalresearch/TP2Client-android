package org.linphone.service;

import static android.media.AudioManager.STREAM_VOICE_CALL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneFriend.SubscribePolicy;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PresenceActivityType;
import org.linphone.core.PresenceBasicStatus;
import org.linphone.core.PresenceModel;
import org.linphone.core.Reason;
import org.linphone.core.VideoSize;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration.AndroidCamera;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.mportal.logger.SIPLogger;
import com.mportal.sbc.siplib.R;
import com.mportal.sipdata.IncomingSIPCall;
import com.mportal.sipdata.SIPCall;
import com.mportal.siputil.PreferenceUtil;

public class SIPCoreManager
{
    private LinphoneCore mLc;

    private static SIPCoreManager _instance;

    private static AndroidCamera[] camera;

    private static int[] cameraDeviceId;

    private AudioManager mAudioManager;

    private String loggedInUserName;

    private static final int dbStep = 4;

    private Hashtable<SIPEvent, LinphoneEvent> eventHashTable;

    private ArrayList<SIPEvent> rawEventList;

    private Hashtable<String, Integer> retryCountTable;

    private Hashtable<String, Boolean> subscribeRequiredTable;

    private Handler customEventHandler = new Handler();

    private SIPCoreState coreState = SIPCoreState.SIPCORE_NONE;

    private SIPOPState opState = SIPOPState.SIPOP_NONE;
    
   // private ArrayList<EchoSetting> echoSettingsList;

    public static SIPCoreManager getInstance()
    {
	if (_instance == null)
	{
	    _instance = new SIPCoreManager();
	}
	return _instance;
    }

    public void setLinphoneCore(LinphoneCore lc)
    {
	this.mLc = lc;
    }

    public LinphoneCore getLinphoneCore()
    {
	return this.mLc;
    }

    public void enableOrDisableHD(Context mContext, boolean isEnableHD)
    {
	if (isEnableHD)
	{
	    VideoSize vsize = VideoSize.createStandard(VideoSize.HD, false);
	    mLc.setPreferredVideoSize(vsize);
	    mLc.setUploadBandwidth(1024 * 1024);
	    mLc.setDownloadBandwidth(1024 * 1024);
	}
	else
	{
	    // TODO : sandy : need to reset back to original value on resetting
	}
	boolean isVideoEnabled = PreferenceUtil.getPrefBoolean(mContext, R.string.pref_video_enable_key, false);
	mLc.enableVideo(isVideoEnabled, isVideoEnabled);
    }

    public boolean isAlreadyFriend(String address)
    {
	if (mLc != null && address != null)
	{
	    LinphoneFriend friend = mLc.findFriendByAddress(address);
	    if (friend != null)
	    {
		return true;
	    }
	}
	return false;
    }

    // At present windowId param is not used, its for future use
    public void startRefferedCall(SIPCall call, String windowId)
    {
	SIPLogger.d("HARISH", "APP HANDLING SERVER REFER");
	if (SIPService.MEETME_TRANSFER_APP_HANDLING)
	{
	    LinphoneCall lCall = (LinphoneCall) call.getCall();
	    if (call != null && call.getCall() != null)
	    {
		LinphoneCallParams params = lCall.getRemoteParams();
		if (params != null && params.getVideoEnabled())
		{
		    SIPLogger.d("HARISH", "IsVideo Enabled RemoteCallCopy : " + params.getVideoEnabled());
		    params = lCall.getCurrentParamsCopy();
		    SIPLogger.d("HARISH", "IsVideo Enabled localCopy : " + params.getVideoEnabled());
		    params.setVideoEnabled(true);
		    mLc.startReferedCall(lCall, params);
		}
	    }
	}
    }

    public boolean addFriend(String address)
    {
	if (mLc == null)
	{
	    return false;
	}
	if (address == null || (address != null && address.length() == 0))
	{
	    return false;
	}
	boolean isAlreadyAFriend = false;
	LinphoneFriend friend = mLc.findFriendByAddress(address);
	if (friend != null)
	{
	    isAlreadyAFriend = true;
	    if (!friend.isSubscribesEnabled())
	    {
		friend.edit();
		friend.enableSubscribes(true);
		friend.setIncSubscribePolicy(SubscribePolicy.SPAccept);
		friend.done();
	    }
	}
	return isAlreadyAFriend;
    }

    public boolean enablePublish(boolean value)
    {

	// LinphoneProxyConfig config = mLc.getDefaultProxyConfig();
	// config.edit();
	// config.enablePublish(value);
	// config.done();
	return true;
	// try
	// {
	// mLc.addProxyConfig(config);
	// return true;
	// }
	// catch (LinphoneCoreException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return false;
	// }
    }

    public void adjustVolume(Context mContext, int i)
    {
	if (mLc == null)
	{
	    return;
	}
	if (mAudioManager == null && mContext != null)
	{
	    mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	if (mAudioManager == null)
	{
	    return;
	}

	if (Build.VERSION.SDK_INT < 15)
	{

	    int oldVolume = mAudioManager.getStreamVolume(STREAM_VOICE_CALL);
	    int maxVolume = mAudioManager.getStreamMaxVolume(STREAM_VOICE_CALL);

	    int nextVolume = oldVolume + i;
	    if (nextVolume > maxVolume)
		nextVolume = maxVolume;
	    if (nextVolume < 0)
		nextVolume = 0;

	    mLc.setPlaybackGain((nextVolume - maxVolume) * dbStep);
	}
	else
	{
	    // GG_KG_TBD
	    // starting from ICS, volume must be adjusted by the application, at
	    // least for STREAM_VOICE_CALL volume stream
	    // mAudioManager.setSpeakerphoneOn(true);
	    // mAudioManager.setStreamVolume(STREAM_VOICE_CALL, mAudioManager.getStreamMaxVolume(STREAM_VOICE_CALL), 0);
	    mAudioManager.adjustStreamVolume(STREAM_VOICE_CALL, i < 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE, 0);
	}
    }

    public boolean createFriend(String address)
    {
	if (mLc == null)
	{
	    return false;
	}

	if (address == null || (address != null && address.length() == 0))
	{
	    return false;
	}
	LinphoneFriend friend = LinphoneCoreFactory.instance().createLinphoneFriend(address);
	friend.enableSubscribes(true);
	friend.setIncSubscribePolicy(LinphoneFriend.SubscribePolicy.SPAccept);
	try
	{
	    mLc.addFriend(friend);
	    return true;
	}
	catch (LinphoneCoreException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean removeFriend(String address)
    {
	if (mLc != null && address != null)
	{
	    LinphoneFriend friend = mLc.findFriendByAddress(address);
	    if (friend != null)
	    {
		mLc.removeFriend(friend);
		if (mLc.findFriendByAddress(address) != null)
		{
		    SIPLogger.d("SIP", "DELETE FRIEND IS UNSUCESSFULL");
		}
		else
		{
		    return true;
		}

	    }
	}
	return false;
    }

    public boolean unSubscribeFriend(String address)
    {
	if (mLc != null && address != null)
	{
	    LinphoneFriend friend = mLc.findFriendByAddress(address);
	    if (friend != null)
	    {
		friend.edit();
		friend.enableSubscribes(false);
		friend.setIncSubscribePolicy(SubscribePolicy.SPAccept);
		friend.done();
		return true;
	    }
	}
	return false;
    }

    public void refreshRegister()
    {
	if (mLc != null)
	{
	    mLc.refreshRegisters();
	}
    }

    public void ignoreCall(IncomingSIPCall call)
    {
	if (mLc == null)
	{
	    return;
	}
	if (call == null)
	{
	    return;
	}
	Object lCall = call.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    mLc.declineCall((LinphoneCall) lCall, Reason.Declined);
	}

    }

    public boolean transferCall(SIPCall call, String address)
    {
	if (mLc == null)
	{
	    return false;
	}
	if (call == null || address == null)
	{
	    return false;
	}
	Object obj = call.getCallObject();
	if (obj instanceof LinphoneCall)
	{
	    SIPLogger.d("HARISH", "transferCall : call - " + call.getCallNumber() + "  ConferenceId - " + address);
	    mLc.transferCall((LinphoneCall) obj, address);
	    return true;
	}
	return false;
    }

    public void declineCall(IncomingSIPCall call)
    {
	if (mLc == null)
	{
	    return;
	}
	if (call == null)
	{
	    return;
	}
	Object lCall = call.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    mLc.declineCall((LinphoneCall) lCall, Reason.DoNotDisturb);
	}

    }

    public void declineCall(IncomingSIPCall call, Reason reason)
    {
	if (mLc == null)
	{
	    return;
	}
	if (call == null)
	{
	    return;
	}
	Object lCall = call.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    mLc.declineCall((LinphoneCall) lCall, reason);
	}

    }

    public void setPresence(PresenceActivityType status)
    {
	setCustomPresence(status, null);
    }

    public void setCustomPresence(PresenceActivityType option, String status)
    {
	if (mLc == null)
	{
	    return;
	}
	PresenceModel model = mLc.getPresenceModel();

	if (status != null)
	{

	    model.setActivity(PresenceActivityType.Other, status);

	    // Custom presence is only supported for Busy & away. Connected state is not supported
	    if (option == PresenceActivityType.Away || option == PresenceActivityType.Online)
	    {
		model.setBasicStatus(PresenceBasicStatus.Open);
	    }
	    else
	    {
		model.setBasicStatus(PresenceBasicStatus.Closed);
	    }

	    SIPLogger.d("HARISH", "Setting Custom presence status  " + status);
	}
	else
	{
	    model.setActivity(option, status);
	}

	mLc.setPresenceModel(model);

    }

    public void addAllToConference(ArrayList<SIPCall> activeCalls)
    {
	if (activeCalls == null)
	{
	    return;
	}
	boolean conferenceAdded = false;
	for (SIPCall calls : activeCalls)
	{
	    Object lCall = calls.getCallObject();
	    if (lCall instanceof LinphoneCall)
	    {
		LinphoneCall call = (LinphoneCall) lCall;
		if (!call.isInConference())
		{
		    mLc.addToConference(call);
		    conferenceAdded = true;
		}
	    }
	}
	if (conferenceAdded)
	{
	    if (activeCalls.size() > 1 && mLc != null)
	    {
		mLc.enterConference();
	    }
	}
	// resumeCall(activeCalls);
    }

    public void removeAllFromConference(ArrayList<SIPCall> activeCalls)
    {
	if (mLc == null)
	{
	    return;
	}
	if (activeCalls == null)
	{
	    return;
	}
	for (SIPCall calls : activeCalls)
	{
	    removeFromConference(calls);
	}
    }

    public void enterCnference()
    {
	if (mLc != null)
	{
	    mLc.enterConference();
	}
    }

    public void leaveConference()
    {
	if (mLc != null)
	{
	    mLc.leaveConference();
	}
    }

    public void removeFromConference(SIPCall call)
    {
	Object lCall = call.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    if (((LinphoneCall) lCall).isInConference())
	    {
		mLc.removeFromConference((LinphoneCall) lCall);
	    }
	}
    }

    public void resumeCall(ArrayList<SIPCall> activeCalls)
    {
	if (activeCalls == null)
	{
	    return;
	}
	for (SIPCall call : activeCalls)
	{
	    resumeCall(call);
	}
    }

    public void resumeCall(SIPCall activeCall)
    {
	if (mLc == null)
	{
	    return;
	}
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    LinphoneCall call = (LinphoneCall) lCall;
	    if (call.getState() == State.Paused || call.getState() == State.Pausing)
	    {
		mLc.resumeCall(call);
	    }

	}
    }

    public boolean isInConference(SIPCall call)
    {
	if (call != null)
	{
	    LinphoneCall lCall = (LinphoneCall) call.getCallObject();
	    if (lCall != null)
	    {
		return lCall.isInConference();
	    }
	}
	return false;
    }

    public void pauseCall(ArrayList<SIPCall> activeCalls)
    {
	if (activeCalls == null)
	{
	    return;
	}

	for (SIPCall call : activeCalls)
	{
	    pauseCall(call);
	}

	/*
	 * if (activeCalls.size() > 1) { for(SIPCall call : activeCalls) {
	 * 
	 * if(isInConference(call)) { removeFromConference(call); try { Thread.sleep(800); } catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } else { pauseCall(call); } } //mLc.leaveConference(); } else { for (SIPCall call : activeCalls) { pauseCall(call); }
	 * 
	 * }
	 */

    }

    public boolean isInComingCall(SIPCall sipCall)
    {
	if (sipCall == null)
	{
	    return false;
	}
	Object lCall = sipCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    return (((LinphoneCall) lCall).getDirection() == CallDirection.Incoming);
	}
	return false;
    }

    public void pauseCall(SIPCall activeCall)
    {
	if (activeCall == null || mLc == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    LinphoneCall call = (LinphoneCall) lCall;
	    if (call.getState() != State.Paused && call.getState() != State.Pausing && call.getState() != State.PausedByRemote)
	    {
		mLc.pauseCall(call);
	    }

	}
    }

    public void enableSpeaker(boolean enable)
    {
	if (mLc == null)
	{
	    return;
	}
	mLc.enableSpeaker(enable);
    }

    public void enableMic(boolean enable)
    {
	if (mLc == null)
	{
	    return;
	}
	mLc.muteMic(enable);
    }

    public boolean isMicMuted()
    {
	if (mLc == null)
	{
	    return false;
	}
	return mLc.isMicMuted();
    }

    public void acceptCall(SIPCall activeCall)
    {
	if (mLc == null)
	{
	    return;
	}
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    try
	    {
		mLc.acceptCall((LinphoneCall) lCall);
	    }
	    catch (LinphoneCoreException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public void acceptVideoCallWithParam(LinphoneCall call, boolean videoEnabled)
    {
	if (mLc == null)
	{
	    return;
	}
	LinphoneCallParams params = null;
	if (call instanceof LinphoneCall)
	{
	    params = ((LinphoneCall) call).getCurrentParamsCopy();
	    // if (videoEnabled)
	    {
		SIPLogger.d("ssss", "debug acceptvideocallwithparam   " + videoEnabled + "\t param" + call.toString());
		params.setVideoEnabled(videoEnabled);
		// mLc.enableVideo(videoEnabled, videoEnabled);
	    }
	}
	try
	{
	    mLc.acceptCallUpdate((LinphoneCall) call, params);
	}
	catch (LinphoneCoreException e)
	{
	    e.printStackTrace();
	}

    }

    public void acceptVideoCallWithParam(SIPCall activeCall, boolean videoEnabled)
    {
	if (mLc == null)
	{
	    return;
	}
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	LinphoneCallParams params = null;
	if (lCall instanceof LinphoneCall)
	{
	    params = ((LinphoneCall) lCall).getCurrentParamsCopy();
	    // if (videoEnabled)
	    {
		params.setVideoEnabled(videoEnabled);
		// mLc.enableVideo(videoEnabled, videoEnabled);
	    }
	}
	try
	{
	    mLc.acceptCallWithParams((LinphoneCall) lCall, params);
	}
	catch (LinphoneCoreException e)
	{
	    e.printStackTrace();
	}

    }

    public void acceptCallWithParams(SIPCall activeCall)
    {
	if (mLc == null)
	{
	    return;
	}
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    try
	    {
		mLc.acceptCall((LinphoneCall) lCall);
	    }
	    catch (LinphoneCoreException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public void enableCamera(SIPCall activeCall, boolean enableOrDisable)
    {
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    ((LinphoneCall) lCall).enableCamera(enableOrDisable);
	}
    }

    public boolean isCameraEnabled(SIPCall activeCall)
    {
	if (activeCall == null)
	{
	    return false;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    return ((LinphoneCall) lCall).cameraEnabled();
	}
	return false;
    }

    public int switchCamera(SIPCall activeCall, int type, SurfaceView preview)
    {
	if (mLc == null)
	{
	    return -1;
	}
	if (activeCall == null)
	{
	    return -1;
	}
	try
	{
	    Object lCall = activeCall.getCallObject();
	    if (lCall instanceof LinphoneCall)
	    {
		int videoDeviceId = mLc.getVideoDevice();
		if (type == videoDeviceId)
		{
		    type = (videoDeviceId + 1) % 2;

		}
		LinphoneCallParams param = ((LinphoneCall) lCall).getCurrentParamsCopy();
		mLc.setVideoDevice(type);
		// Update the call with params.. refer base code.
		mLc.updateCall((LinphoneCall) lCall, null);

	    }

	    // int videoDeviceId = LinphoneManager.getLc().getVideoDevice();
	    // videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
	    // CallManager.getInstance().updateCall();

	    if (preview != null)
	    {
		mLc.setPreviewWindow(preview);
	    }
	    return type;
	}
	catch (ArithmeticException ae)
	{
	    SIPLogger.d("Video", "Cannot swtich camera : no camera");
	    return -1;
	}
    }

    public int[] getCameraDeviceId()
    {
	if (camera == null)
	{
	    camera = AndroidCameraConfiguration.retrieveCameras();
	}
	if (cameraDeviceId == null)
	{
	    cameraDeviceId = new int[camera.length];
	}
	if (cameraDeviceId != null && camera != null)
	{
	    for (int i = 0; i < camera.length; i++)
	    {
		cameraDeviceId[i] = camera[i].id;
	    }
	}
	return cameraDeviceId;
    }

    public boolean isVideoActivated(SIPCall activeCall)
    {
	if (activeCall == null)
	{
	    return false;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    LinphoneCallParams params = ((LinphoneCall) lCall).getCurrentParamsCopy();
	    if (params.getVideoEnabled())
	    {
		return true;
	    }
	}
	return false;
    }

    public void activateAudioCallToVideo(SIPCall activeCall)
    {
	if (mLc == null)
	{
	    return;
	}
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    LinphoneCall call = (LinphoneCall) lCall;
	    call.enableCamera(true);

	    LinphoneCallParams params = ((LinphoneCall) lCall).getCurrentParamsCopy();
	    if (params.getVideoEnabled())
	    {
		return;
	    }
	    params.setVideoEnabled(true);
	    params.setAudioBandwidth(0);

	    // check bandwidth
	    // Abort if not enough bandwidth...
	    mLc.updateCall((LinphoneCall) lCall, params);
	}
    }

    public void deactivateVideoCallToAudio(SIPCall activeCall)
    {
	if (mLc == null)
	{
	    return;
	}
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    LinphoneCall call = (LinphoneCall) lCall;
	    call.enableCamera(false);

	    LinphoneCallParams params = ((LinphoneCall) lCall).getCurrentParamsCopy();
	    if (!params.getVideoEnabled())
	    {
		return;
	    }
	    params.setVideoEnabled(false);
	    params.setAudioBandwidth(83);
	    // check bandwidth
	    // Abort if not enough bandwidth...
	    mLc.updateCall((LinphoneCall) lCall, params);
	}
    }

    public void zoomVideo(SIPCall call, float factor, float cx, float cy)
    {
	if (call != null && mLc != null)
	{
	    LinphoneCall lCall = (LinphoneCall) call.getCallObject();
	    if (lCall != null && isVideoActivated(call))
	    {
		lCall.zoomVideo(factor, cx, cy);
	    }
	}
    }

    public void startRecording(SIPCall activeCall)
    {
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    LinphoneCall call = (LinphoneCall) lCall;
	    call.startRecording();
	}
    }

    public void stopRecording(SIPCall activeCall)
    {
	if (activeCall == null)
	{
	    return;
	}
	Object lCall = activeCall.getCallObject();
	if (lCall instanceof LinphoneCall)
	{
	    LinphoneCall call = (LinphoneCall) lCall;
	    call.stopRecording();
	}
    }

    public void startConferenceRecording(String path)
    {
	if (mLc != null)
	{
	    mLc.startConferenceRecording(path);
	}
    }

    public void stopConferenceRecording()
    {
	if (mLc != null)
	{
	    mLc.stopConferenceRecording();
	}
    }

    public void createServerChatRoom()
    {

    }

    public void killCore()
    {
	mLc = null;
	camera = null;
	mAudioManager = null;
    }

    public void cleanup()
    {
	mLc = null;
	_instance = null;
	camera = null;
	mAudioManager = null;
    }

    public String createWindowId(String userName)
    {
	if (userName == null)
	{
	    userName = "defaultuser";
	}
	if (userName != null)
	{
	    int index = userName.indexOf('@');
	    if (index > -1)
	    {
		userName = userName.substring(0, index);
	    }
	    if (userName.length() > 0)
	    {
		char c = userName.toCharArray()[0];
		boolean isFirstCharacterDigit = Character.isDigit(c) || c == '+';
		if (isFirstCharacterDigit)
		{
		    if (loggedInUserName != null)
		    {
			userName = loggedInUserName;
		    }
		    else
		    {
			userName = "defaultuser";
		    }
		}
	    }
	    String random = "-" + new Random().nextInt(9999) + '-' + new Random().nextInt(9999);
	    int len = 20 - random.length();
	    if (userName.length() > len)
	    {
		userName = userName.substring(0, len);
	    }
	    userName = userName + random;
	}
	return userName;
    }

    public void terminateAllCall()
    {
	if (mLc != null)
	{
	    SIPLogger.d("gg", "Before SIP call total call" + mLc.getCallsNb());
	    mLc.terminateAllCalls();
	    SIPLogger.d("gg", "After SIP call total call" + mLc.getCallsNb());
	}
    }

    public void terminateConference()
    {
	if (mLc != null)
	{
	    mLc.terminateConference();
	}
    }

    public RegistrationState getRegistrationState()
    {
	if (mLc != null)
	{
	    LinphoneProxyConfig[] list = mLc.getProxyConfigList();
	    if (list != null && list.length > 0)
	    {
		LinphoneCore.RegistrationState registrationState = list[0].getState();
		if (registrationState != null)
		{
		    SIPLogger.d("HARISH", "Registration state :" + registrationState.toString());
		    return registrationState;
		}
	    }
	}

	SIPLogger.d("HARISH", "Registration state is Returned NULLLLLLLL");
	return null;
    }

    public void setLoggedInUserName(String loggedInUserName)
    {
	this.loggedInUserName = loggedInUserName;
    }

    private SIPEvent getSIPEventForEventName(String eventName)
    {
	if (eventName != null && eventHashTable != null && eventHashTable.size() > 0)
	{
	    SIPEvent tempEvent = new SIPEvent();
	    tempEvent.setEventName(eventName);

	    Enumeration<SIPEvent> enumeration = eventHashTable.keys();
	    while (enumeration.hasMoreElements())
	    {
		SIPEvent sipEvent = enumeration.nextElement();
		if (sipEvent != null && sipEvent.equals(tempEvent))
		{
		    return sipEvent;
		}
	    }
	}
	return null;
    }

    public SIPEvent getSubscribeSIPEvent(LinphoneEvent ev)
    {
	if (ev != null && eventHashTable != null && eventHashTable.size() > 0)
	{
	    SIPEvent tempEvent = new SIPEvent();
	    tempEvent.setEventName(ev.getEventName());

	    Enumeration<SIPEvent> enumeration = eventHashTable.keys();
	    while (enumeration.hasMoreElements())
	    {
		SIPEvent sipEvent = enumeration.nextElement();
		if (sipEvent != null && sipEvent.equals(tempEvent))
		{
		    return sipEvent;
		}
	    }
	}
	return null;
    }

    public void cancelSubscribeForEvent(SIPEvent sipEvent)
    {
	int index = -1;
	if (customEventHandler != null)
	{
	    if (subscribeRunnable != null && subscribeRunnable.size() > 0)
	    {
		for (MyRunnable myRunnable : subscribeRunnable)
		{
		    if (myRunnable.sipEvent.equals(sipEvent))
		    {
			index++;
			SIPLogger.d("HARISH", "Cancelling the Runnable for event " + myRunnable.sipEvent.getEventName());
			customEventHandler.removeCallbacks(myRunnable);
			break;
		    }

		}
		if (index > -1)
		{
		    subscribeRunnable.remove(index);
		}
	    }
	}
    }

    public void cancelSubscribeEventsInQueue()
    {
	if (customEventHandler != null)
	{
	    if (subscribeRunnable != null && subscribeRunnable.size() > 0)
	    {
		for (MyRunnable myRunnable : subscribeRunnable)
		{
		    SIPLogger.d("HARISH", "Cancelling the Runnable for event " + myRunnable.sipEvent.getEventName());
		    customEventHandler.removeCallbacks(myRunnable);
		}
		subscribeRunnable.clear();
	    }

	}
    }

    public class MyRunnable implements Runnable
    {

	private SIPEvent sipEvent;

	public void setSipEvent(SIPEvent event)
	{
	    sipEvent = event;
	}

	@Override
	public void run()
	{
	    if (isUserRegistered())
	    {
		subscribeCustomEvent(sipEvent);
	    }

	}
    }

    ArrayList<MyRunnable> subscribeRunnable = new ArrayList<MyRunnable>();

    private boolean publishRequired;

    private boolean userProfileDND;

    public void postSubscribeEvent(LinphoneEvent ev, int delayInMilliSec)
    {
	final SIPEvent sipEvent = getSubscribeSIPEvent(ev);
	if (sipEvent != null)
	{
	    cancelSubscribeForEvent(sipEvent);

	    MyRunnable myRunnable = new MyRunnable();
	    myRunnable.setSipEvent(sipEvent);
	    subscribeRunnable.add(myRunnable);
	    customEventHandler.postDelayed(myRunnable, delayInMilliSec);
	    SIPLogger.d("HARISH", "Subscribe will happen after " + delayInMilliSec + "ms for eventName : " + sipEvent.getEventName());
	}
	else
	{
	    SIPLogger.d("HARISH", "Subscribe event is ******NULL****** for " + ev.getEventName());
	}
    }

    public void doInitialSubscribeCustomEvent(SIPEvent sipEvent)
    {
	if (rawEventList == null)
	{
	    rawEventList = new ArrayList<SIPEvent>();
	}
	try
	{
	    SIPEvent event = (SIPEvent) sipEvent.clone();
	    if (event != null)
	    {
		if (!rawEventList.contains(event))
		{
		    rawEventList.add(event);
		}
		subscribeCustomEvent(event);
	    }
	}
	catch (CloneNotSupportedException e)
	{
	    e.printStackTrace();
	}

    }

    private void subscribeCustomEvent(SIPEvent sipEvent)
    {
	if (mLc != null && sipEvent != null && isUserRegistered())
	{
	    try
	    {
		LinphoneAddress lAddress = null;
		if (!TextUtils.isEmpty(sipEvent.getlAddress()))
		{
		    lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(sipEvent.getlAddress());
		}
		else if (!TextUtils.isEmpty(sipEvent.getUsername()) && !TextUtils.isEmpty(sipEvent.getDomain()))
		{
		    lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(sipEvent.getUsername(), sipEvent.getDomain(), sipEvent.getDisplayname());
		}
		if (lAddress != null)
		{
		    LinphoneContent lContent = null;
		    if (!TextUtils.isEmpty(sipEvent.getContentType()))
		    {
			lContent = LinphoneCoreFactory.instance().createLinphoneContent(sipEvent.getContentType(), sipEvent.getSubContentType(), new String(sipEvent.getContent()));
		    }
		    LinphoneEvent event = mLc.subscribe(lAddress, sipEvent.getEventName(), (int) sipEvent.getExpire(), lContent);
		    if (event != null)
		    {
			SIPLogger.d("HARISH", "sipEvent : " + sipEvent.getEventName());

			if (eventHashTable == null)
			{
			    eventHashTable = new Hashtable<SIPEvent, LinphoneEvent>();
			}
			SIPLogger.d("HARISH", "eventHashTable : " + eventHashTable.size());
			if (eventHashTable.containsKey(sipEvent))
			{
			    SIPLogger.d("HARISH", "removed eventHashTable : " + eventHashTable.size());
			    eventHashTable.remove(sipEvent);
			    SIPLogger.d("HARISH", "after remove eventHashTable : " + eventHashTable.size());
			}
			eventHashTable.put(sipEvent, event);
		    }
		}
	    }
	    catch (LinphoneCoreException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public boolean reSubscribeCustomEvent(LinphoneEvent ev, int retryDelayInMilliSec)
    {
	if (isUserRegistered())
	{
	    // unsubscribeCustomEvent(ev);
	    postSubscribeEvent(ev, retryDelayInMilliSec);
	    return true;
	}
	else
	{
	    // HARISH : For TWC server, cleanup the event to avoid cumulated events
	    // ev.terminate();
	}

	return false;

    }

    void resubscribeCustomEvent()
    {
	unsubscribeCustomEvent(true);
	if (mLc != null && rawEventList != null)
	{
	    for (SIPEvent event : rawEventList)
	    {
		if (event != null)
		{
		    try
		    {
			subscribeCustomEvent((SIPEvent) event.clone());
		    }
		    catch (CloneNotSupportedException e)
		    {
			e.printStackTrace();
		    }
		}
	    }
	}
    }

    void unsubscribeCustomEvent(LinphoneEvent ev)
    {
	if (ev != null)
	{
	    SIPLogger.d("HARISH", "unsubscribeCustomEvent : " + ev.getEventName());
	    ev.terminate();
	}
    }

    public void unsubscribeCustomEvent(boolean forceTerminate)
    {
	if (mLc != null && eventHashTable != null)
	{
	    Enumeration<LinphoneEvent> enumeration = eventHashTable.elements();
	    while (enumeration.hasMoreElements())
	    {
		LinphoneEvent levent = enumeration.nextElement();
		if (levent != null)
		{
		    boolean terminateAllow = false;
		    if (forceTerminate)
		    {
			terminateAllow = true;
		    }
		    else
		    {
			if (SIPCoreManager.getInstance().isUserRegistered())
			{
			    terminateAllow = true;
			}
		    }
		    if (terminateAllow)
		    {
			levent.terminate();
		    }
		}
	    }
	}
    }

    public SIPEvent updateCustomEvent(String eventName, LinphoneContent content)
    {
	if (mLc != null && eventHashTable != null)
	{
	    SIPEvent event = new SIPEvent();
	    event.setEventName(eventName);

	    SIPLogger.d("event", "eventName " + eventName);
	    Enumeration<SIPEvent> enumeration = eventHashTable.keys();
	    SIPEvent foundEvent = null;
	    while (enumeration.hasMoreElements())
	    {
		SIPEvent tempEvent = enumeration.nextElement();
		SIPLogger.d("event", "eventName inside enumeration" + tempEvent.getEventName());
		if (tempEvent != null && tempEvent.equals(event))
		{
		    tempEvent.setNotifyContent(content.getData());
		    tempEvent.setNotifySubContentType(content.getSubtype());
		    tempEvent.setNotifyContentType(content.getType());
		    tempEvent.setNotifyContentEncoding(content.getEncoding());

		    foundEvent = tempEvent;
		    SIPLogger.d("event", "found event and added content");
		    break;
		}
	    }

	    if (foundEvent != null)
	    {
		SIPLogger.d("event", "returning found event ");
		return foundEvent;
	    }
	}
	return null;
    }

    public void doActionOnSubscriptionActive(LinphoneEvent ev, Messenger messenger)
    {
	String eventName = ev.getEventName();
	if (eventName != null && messenger != null)
	{
	    if (eventName.equalsIgnoreCase("presence") || eventName.equalsIgnoreCase("reg"))
	    {
		if (eventHashTable != null)
		{
		    SIPEvent event = new SIPEvent();
		    event.setEventName(eventName);

		    Enumeration<SIPEvent> enumeration = eventHashTable.keys();
		    SIPEvent foundEvent = null;
		    while (enumeration.hasMoreElements())
		    {
			SIPEvent tempEvent = enumeration.nextElement();
			if (tempEvent != null && tempEvent.equals(event))
			{
			    foundEvent = tempEvent;
			    // subscription success.
			    foundEvent.setInitialSubscribe(false);
			    eventHashTable.put(foundEvent, ev);
			    break;
			}
		    }
		    if (eventName.equalsIgnoreCase("presence"))
		    {
			if (foundEvent != null)
			{
			    Message message = new Message();
			    message.what = SIPConstant.UPDATE_SUBSCRIBE_STATE_CHANGED;
			    message.obj = foundEvent;
			    try
			    {
				messenger.send(message);
			    }
			    catch (RemoteException e)
			    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}
		    }
		}
	    }

	}
    }

    public boolean updateSubscribeForEventName(String eventName)
    {
	if (eventName != null && eventName.length() > 0)
	{
	    SIPEvent sipEvent = getSIPEventForEventName(eventName);
	    if (sipEvent != null && eventHashTable != null)
	    {
		LinphoneEvent event = eventHashTable.get(sipEvent);
		if (event != null)
		{
		    if (updateSubscribeCustomEvent(event))
		    {
			return true;
		    }
		    else
		    {
			cancelSubscribeForEvent(sipEvent);
			subscribeCustomEvent(sipEvent);
		    }

		}
	    }
	}
	return false;
    }

    public boolean updateSubscribeCustomEvent(LinphoneEvent ev)
    {
	if (isUserRegistered())
	{
	    SIPEvent sipEvent = getSubscribeSIPEvent(ev);
	    if (sipEvent != null && isUserRegistered())
	    {
		LinphoneContent content = null;
		if (!TextUtils.isEmpty(sipEvent.getContentType()))
		{
		    content = LinphoneCoreFactory.instance().createLinphoneContent(sipEvent.getContentType(), sipEvent.getSubContentType(), new String(sipEvent.getContent()));
		}
		LinphoneEvent levent = eventHashTable.get(sipEvent);
		if (levent != null)
		{
		    SIPLogger.d("HARISH", "updateSubscribeCustomEvent updating the old subscribe");
		    levent.updateSubscribe(content);
		    return true;
		}
		else
		{
		    SIPLogger.d("HARISH", "updateSubscribeCustomEvent SOMETHING WRONG, not updated the subscribe");
		}
	    }
	    else
	    {
		SIPLogger.d("HARISH", "updateSubscribeCustomEvent failed may be due to event is null or user is Not Registered");
	    }
	}
	return false;
    }

    public void updateSubscribeCustomEvent()
    {
	if (mLc != null && eventHashTable != null)
	{
	    Enumeration<SIPEvent> enumeration = eventHashTable.keys();
	    while (enumeration.hasMoreElements())
	    {
		SIPEvent event = enumeration.nextElement();
		if (event != null)
		{
		    LinphoneContent content = null;
		    if (!TextUtils.isEmpty(event.getContentType()))
		    {
			// content = LinphoneCoreFactory.instance().createLinphoneContent("application","pidf+xml", "");
			content = LinphoneCoreFactory.instance().createLinphoneContent(event.getContentType(), event.getSubContentType(), new String(event.getContent()));
		    }
		    LinphoneEvent levent = eventHashTable.get(event);
		    if (levent != null)
		    {
			levent.updateSubscribe(content);
		    }
		}
	    }
	}
    }

    public void subscribeCustomEvent(boolean error)
    {
	if (mLc != null && eventHashTable != null)
	{
	    Enumeration<SIPEvent> enumeration = eventHashTable.keys();
	    while (enumeration.hasMoreElements())
	    {
		SIPEvent event = enumeration.nextElement();
		if (event != null)
		{
		    if (error)
		    {
			int retryCount = SIPCoreManager.getInstance().getRetryCount(event.getEventName());
			if (retryCount == -1 || retryCount == 0)
			{
			    SIPCoreManager.getInstance().incrementRetryCount(event.getEventName());
			    subscribeCustomEvent(event);
			}
		    }
		}
	    }
	}
    }

    public boolean isUserRegistered()
    {
	RegistrationState regState = SIPCoreManager.getInstance().getRegistrationState();
	// SIPLogger.d("HARISH", "RegistrationState : " + regState.toString());
	if (regState == RegistrationState.RegistrationOk)
	{
	    return true;
	}
	return false;
    }

    public void setSIPCoreState(SIPCoreState state)
    {
	coreState = state;
	SIPLogger.d("gg", "SIP core state " + state);
    }

    public SIPCoreState getSIPCoreState()
    {
	return coreState;
    }

    public void setSIPOPState(SIPOPState state)
    {
	opState = state;
	SIPLogger.d("gg", "SIP op state " + state);
    }

    public SIPOPState getSIPOPState()
    {
	return opState;
    }

    public void removeCustomEvent(String eventName)
    {
	if (mLc != null && eventHashTable != null)
	{
	    SIPEvent event = new SIPEvent();
	    event.setEventName(eventName);

	    Enumeration<SIPEvent> enumeration = eventHashTable.keys();
	    SIPEvent foundEvent = null;
	    while (enumeration.hasMoreElements())
	    {
		SIPEvent tempEvent = enumeration.nextElement();
		if (tempEvent != null && tempEvent.equals(event))
		{
		    foundEvent = tempEvent;
		    break;
		}
	    }
	    if (foundEvent != null)
	    {
		eventHashTable.remove(foundEvent);
	    }
	}
    }

    public void setPublishRequired(boolean publishRequired)
    {
	this.publishRequired = publishRequired;
    }

    public boolean isPublishRequired()
    {
	return this.publishRequired;
    }

    public void setSubscribeRequired(String eventName, boolean required)
    {
	if (subscribeRequiredTable == null)
	{
	    subscribeRequiredTable = new Hashtable<String, Boolean>();
	}
	if (subscribeRequiredTable != null)
	{
	    subscribeRequiredTable.put(eventName, required);
	}
    }

    public boolean getSubscribeRequired(String eventName)
    {
	if (subscribeRequiredTable != null)
	{
	    if (subscribeRequiredTable.containsKey(eventName))
	    {
		return subscribeRequiredTable.get(eventName);
	    }
	}
	return false;
    }

    public void incrementRetryCount(String name)
    {
	if (TextUtils.isEmpty(name))
	{
	    return;
	}
	if (retryCountTable == null)
	{
	    retryCountTable = new Hashtable<String, Integer>();
	}
	if (!retryCountTable.containsKey(name))
	{
	    retryCountTable.put(name, 0);
	}
	if (retryCountTable.containsKey(name))
	{
	    int count = retryCountTable.get(name);
	    count++;
	    retryCountTable.put(name, count);
	}

    }

    public int getRetryCount(String name)
    {
	if (TextUtils.isEmpty(name))
	{
	    return -1;
	}

	if (retryCountTable != null)
	{
	    if (retryCountTable.containsKey(name))
	    {
		return retryCountTable.get(name);
	    }
	}
	return -1;
    }

    public void clearRetryCount(String name)
    {
	if (TextUtils.isEmpty(name))
	{
	    return;
	}

	if (retryCountTable != null)
	{
	    retryCountTable.remove(name);
	}
    }

    public boolean isUserDND()
    {
	return userProfileDND;
    }

    public void setUserDND(boolean value)
    {
	userProfileDND = value;
    }

    public void setSIPLogging(Context context, boolean value)
    {
	LinphoneCoreFactory.instance().setDebugMode(value, PreferenceUtil.getString(context, (R.string.app_name)));
    }

    public int getLocalConferenceSize()
    {
	if (mLc != null)
	{
	    return mLc.getConferenceSize();
	}
	return -1;
    }

    public boolean isInConference()
    {
	if (mLc != null)
	{
	    return mLc.isInConference();
	}
	return false;
    }

//    public ArrayList<EchoSetting> getEchoSettingsList()
//    {
//	return echoSettingsList;
//    }
//
//    public void setEchoSettingsList(ArrayList<EchoSetting> echoSettingsList)
//    {
//	this.echoSettingsList = echoSettingsList;
//    }

    /**
     * Testing purpose start
     */
    // private Context context;
    //
    // public void setContext(Context context)
    // {
    // this.context = context;
    // }
    //
    // public void addAuthInfo()
    // {
    // String username = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_username_key), null);
    // String password = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_passwd_key), null);
    // String domain = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_domain_key), null);
    // String privateId = PreferenceUtil.getPrefString(context, PreferenceUtil.getString(context, R.string.pref_privateid_key), null);
    // // password = "+1" + password;
    // LinphoneAuthInfo lAuthInfo = LinphoneCoreFactory.instance().createAuthInfo("9193020064", "9193020064", password, null, "ims.eng.rr.com", domain);
    // if (privateId != null)
    // {
    // lAuthInfo.setUserId(privateId);
    // }
    // mLc.addAuthInfo(lAuthInfo);
    // }
    /**
     * Testing purpose end
     */

}
