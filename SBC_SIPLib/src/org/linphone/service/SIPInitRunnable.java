package org.linphone.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.FirewallPolicy;
import org.linphone.core.LinphoneCore.MediaEncryption;
import org.linphone.core.LinphoneCore.Transports;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PayloadType;
import org.linphone.mediastream.Log;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.mportal.logger.SIPLogger;
import com.mportal.sbc.siplib.R;
import com.mportal.siputil.PreferenceUtil;
import com.mportal.siputil.Util;

public class SIPInitRunnable implements Runnable
{
    /**
     * All the initialization part should place under this runnable which will be called from the service thread.
     */
    private Context mContext;

    private Resources resource;

    private LinphoneCore mLinphoneCore;

    private int state;

    public static final int STATE_NONE = 0;

    public static final int STATE_CONFIG_CHANGE = 1;

    public static final int STATE_INITIALIZATION = 2;

    public static final int STATE_DESTROY = 3;

    private static final String[] BROADCAST_RECEIVER_ACTIONS = new String[] { Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF, ConnectivityManager.CONNECTIVITY_ACTION

    };

    private SIPBroadcastReceiver broadcastReceiver;

    private Transports initialTransports;

    // have to change
    private int extraAccountNumber = 2;

    public SIPInitRunnable(Context context, LinphoneCore linphoneCore)
    {
	this.mContext = context;
	this.resource = context.getResources();
	this.mLinphoneCore = linphoneCore;
	broadcastReceiver = new SIPBroadcastReceiver();
    }

    public void setState(int state, int substate)
    {
	this.state = state;
    }

    @Override
    public void run()
    {
	if (mContext == null)
	{
	    return;
	}
	if (state == STATE_NONE)
	{
	    return;
	}
	if (state == STATE_INITIALIZATION)
	{
	    initFromConfTunnel();
	    registerAllReceivers();
	    // All initialization will be done here
	}
	else if (state == STATE_CONFIG_CHANGE)
	{
	    initFromConfTunnel();
	}
	else if (state == STATE_DESTROY)
	{
	    unregisterAllReceivers();
	}
    }

    public void unregisterAllReceivers()
    {
	try
	{
	    if (broadcastReceiver != null)
	    {
		mContext.unregisterReceiver(broadcastReceiver);
	    }
	}
	catch (Exception e)
	{

	}

    }

    private void registerAllReceivers()
    {
	for (String action : BROADCAST_RECEIVER_ACTIONS)
	{
	    IntentFilter lFilter = new IntentFilter(action);
	    mContext.registerReceiver(broadcastReceiver, lFilter);
	}
    }

    public void initFromConf() throws SIPException
    {
	boolean isDebugLogEnabled = false;
	// !(resource.getBoolean(R.bool.disable_every_log)) &&
	// PreferenceUtil.getPrefBoolean(mContext, R.string.pref_debug_key,
	// resource.getBoolean(R.bool.pref_debug_default));
	LinphoneCoreFactory.instance().setDebugMode(SIPConstant.SIP_LOG_ENABLED, PreferenceUtil.getString(mContext, (R.string.app_name)));
	initFromConfTunnel();

	if (initialTransports == null)
	    initialTransports = mLinphoneCore.getSignalingTransportPorts();

	setSignalingTransportsFromConfiguration(initialTransports);
	initMediaEncryption();

	mLinphoneCore.setVideoPolicy(isAutoInitiateVideoCalls(), isAutoAcceptCamera());

	readAndSetAudioAndVideoPorts();

	String defaultIncomingCallTimeout = PreferenceUtil.getString(mContext, R.string.pref_incoming_call_timeout_default);
	int incomingCallTimeout = Util.parseInt(PreferenceUtil.getPrefString(mContext, R.string.pref_incoming_call_timeout_key, defaultIncomingCallTimeout), defaultIncomingCallTimeout);
	mLinphoneCore.setIncomingTimeout(incomingCallTimeout);

	try
	{
	    // Configure audio codecs
	    // enableDisableAudioCodec("speex", 32000, 1,
	    // R.string.pref_codec_speex32_key);
	    enableDisableAudioCodec("speex", 32000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_SPEEX32_CODEC_VALUE, false));
	    enableDisableAudioCodec("speex", 16000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_SPEEX16_CODEC_VALUE, false));
	    enableDisableAudioCodec("speex", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_SPEEX8_CODEC_VALUE, false));
	    enableDisableAudioCodec("iLBC", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_ILBC_CODEC_VALUE, false));
	    enableDisableAudioCodec("GSM", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_GSM_CODEC_VALUE, false));
	    enableDisableAudioCodec("G722", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_G722_CODEC_VALUE, false));
	    enableDisableAudioCodec("G729", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_G729_CODEC_VALUE, false));
	    enableDisableAudioCodec("PCMU", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_PCMU_CODEC_VALUE, false));
	    enableDisableAudioCodec("PCMA", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_PCMA_CODEC_VALUE, false));

	    enableDisableAudioCodec("AMR", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_AMR_CODEC_VALUE, false));
	    enableDisableAudioCodec("AMR-WB", 16000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_AMR_WB_CODEC_VALUE, false));
	    // enableDisableAudioCodec("SILK", 24000, 1,
	    // R.string.pref_codec_silk24_key);
	    enableDisableAudioCodec("SILK", 24000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_SILK24_CODEC_VALUE, false));
	    enableDisableAudioCodec("SILK", 16000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_SILK16_CODEC_VALUE, false));
	    // enableDisableAudioCodec("SILK", 12000, 1,
	    // R.string.pref_codec_silk12_key);
	    enableDisableAudioCodec("SILK", 12000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_SILK12_CODEC_VALUE, false));
	    enableDisableAudioCodec("SILK", 8000, 1, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_SILK8_CODEC_VALUE, false));
	    enableDisableAudioCodec("opus", 48000, 2, PreferenceUtil.getPrefBoolean(mContext, SIPConstant.SIP_OPUS_CODEC_VALUE, false));

	    // Configure video codecs
	    for (PayloadType videoCodec : mLinphoneCore.getVideoCodecs())
	    {
		enableDisableVideoCodecs(videoCodec);
	    }

	    boolean useEC = PreferenceUtil.getPrefBoolean(mContext, R.string.pref_echo_cancellation_key, resource.getBoolean(R.bool.pref_echo_canceller_default));
	    // mLinphoneCore.enableEchoCancellation(useEC);
	    mLinphoneCore.enableEchoCancellation(true);
	}
	catch (LinphoneCoreException e)
	{
	    throw new SIPException(SIPException.CONFIG_EXCEPTION, e.getMessage());
	}
	boolean isVideoEnabled = isVideoEnabled();
	mLinphoneCore.enableVideo(isVideoEnabled, isVideoEnabled);

	// stun server
	String lStun = PreferenceUtil.getPrefString(mContext, R.string.pref_stun_server_key, PreferenceUtil.getString(mContext, R.string.default_stun));
	boolean useICE = PreferenceUtil.getPrefBoolean(mContext, R.string.pref_ice_enable_key, resource.getBoolean(R.bool.pref_ice_enabled_default));
	mLinphoneCore.setStunServer(null);
	if (lStun != null && lStun.length() > 0)
	{
	    mLinphoneCore.setFirewallPolicy(useICE ? FirewallPolicy.UseIce : FirewallPolicy.UseStun);
	}
	else
	{
	    mLinphoneCore.setFirewallPolicy(FirewallPolicy.NoFirewall);
	}

	mLinphoneCore.setUseRfc2833ForDtmfs(PreferenceUtil.getPrefBoolean(mContext, R.string.pref_rfc2833_dtmf_key, resource.getBoolean(R.bool.pref_rfc2833_dtmf_default)));
	mLinphoneCore.setUseSipInfoForDtmfs(PreferenceUtil.getPrefBoolean(mContext, R.string.pref_sipinfo_dtmf_key, resource.getBoolean(R.bool.pref_sipinfo_dtmf_default)));

	String displayName = PreferenceUtil.getPrefString(mContext, R.string.pref_display_name_key, PreferenceUtil.getString(mContext, R.string.pref_display_name_default));
	String username = PreferenceUtil.getPrefString(mContext, R.string.pref_user_name_key, PreferenceUtil.getString(mContext, R.string.pref_user_name_default));
	mLinphoneCore.setPrimaryContact(displayName, username);

	// accounts
	// try
	{
	    // initAccounts();

	    // init network state
	    ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
	    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
	    boolean wifiOnly = pref.getBoolean(PreferenceUtil.getString(mContext, R.string.pref_wifi_only_key), resource.getBoolean(R.bool.pref_wifi_only_default));
	    boolean isConnected = false;
	    if (networkInfo != null)
	    {
		isConnected = networkInfo.getState() == NetworkInfo.State.CONNECTED && (networkInfo.getTypeName().equalsIgnoreCase("wifi") || (networkInfo.getTypeName().length() > 0 && !wifiOnly));
	    }
	    mLinphoneCore.setNetworkReachable(isConnected);
	}
	// catch (LinphoneCoreException e)
	// {
	// throw new SIPException(SIPException.CONFIG_EXCEPTION, e.getMessage());
	// }

	registerAllReceivers();
    }

    public void displayProxyConfig(String place)
    {
	if (mLinphoneCore != null)
	{
	    LinphoneProxyConfig[] proxyConfig = mLinphoneCore.getProxyConfigList();
	    if (proxyConfig == null || (proxyConfig != null && proxyConfig.length == 0))
	    {
		SIPLogger.d("gg", "mPortal SIP proxyConfig length is 0" + "\t" + place);
	    }
	    else
	    {
		SIPLogger.d("gg", "mPortal SIP proxyConfig length is " + proxyConfig.length + "\t" + place);
	    }
	}
    }

    public void initAccounts() throws LinphoneCoreException
    {
	mLinphoneCore.clearAuthInfos();
	mLinphoneCore.clearProxyConfigs();

	displayProxyConfig("initAccounts after clearProxyConfig");
	// PreferenceUtil.getPrefInt(mContext, R.string.pref_extra_accounts, 1);
	for (int i = 0; i < extraAccountNumber; i++)
	{
	    String key = i == 0 ? "" : String.valueOf(i);
	    if (!PreferenceUtil.getPrefBoolean(mContext, PreferenceUtil.getString(mContext, R.string.pref_disable_account_key) + key, false))
	    {
		// initAccount(key, i == PreferenceUtil.getPrefInt(mContext,
		// R.string.pref_default_account_key, 0));
		initAccount(key, i == 0 ? true : false);
	    }
	}
	displayProxyConfig("initAccounts after initAccount");

	LinphoneProxyConfig lDefaultProxyConfig = mLinphoneCore.getDefaultProxyConfig();
	if (lDefaultProxyConfig != null)
	{
	    // prefix
	    String lPrefix = PreferenceUtil.getPrefString(mContext, R.string.pref_prefix_key, null);
	    if (lPrefix != null)
	    {
		lDefaultProxyConfig.setDialPrefix(lPrefix);
	    }
	    // escape +
	    lDefaultProxyConfig.setDialEscapePlus(PreferenceUtil.getPrefBoolean(mContext, R.string.pref_escape_plus_key, false));
	}
    }

    private void initAccount(String key, boolean defaultAccount) throws LinphoneCoreException
    {
	String username = PreferenceUtil.getPrefString(mContext, PreferenceUtil.getString(mContext, R.string.pref_username_key) + key, null);
	String password = PreferenceUtil.getPrefString(mContext, PreferenceUtil.getString(mContext, R.string.pref_passwd_key) + key, null);
	String domain = PreferenceUtil.getPrefString(mContext, PreferenceUtil.getString(mContext, R.string.pref_domain_key) + key, null);
	String privateId = PreferenceUtil.getPrefString(mContext, PreferenceUtil.getString(mContext, R.string.pref_privateid_key) + key, null);
	if (username != null && username.length() > 0 && password != null)
	{
	    if (domain != null && domain.length() > 0)
	    {
		// String identity = "sip:" + username + "@" + domain;
		String identity = username + " <sip:" + username + "@" + domain + ">";
		String proxy = PreferenceUtil.getPrefString(mContext, SIPConstant.SIP_SERVER_IP, null);
		// PreferenceUtil.getString(mContext, R.string.pref_proxy_key) + key, null);
		if (proxy == null || proxy.length() == 0)
		{
		    proxy = "sip:" + domain;
		}
		if (!proxy.startsWith("sip:"))
		{
		    proxy = "sip:" + proxy;
		}

		boolean customAuthorizationHeader = false;
		LinphoneAuthInfo lAuthInfo = LinphoneCoreFactory.instance().createAuthInfo(username, username, password, null, null, domain);
		if (privateId != null && privateId.length() > 0)
		{
		    // String[] temp = privateId.split("@");
		    // String newUserName = temp[0];
		    //
		    // lAuthInfo = LinphoneCoreFactory.instance().createAuthInfo(newUserName, newUserName, password, null, null, domain);
		    lAuthInfo.setUserId(privateId);
		    customAuthorizationHeader = false;
		}
		mLinphoneCore.addAuthInfo(lAuthInfo);

		LinphoneProxyConfig proxycon = mLinphoneCore.createProxyConfig(identity, proxy, null, true);
		proxycon.setRealm(domain);
		String defaultExpire = PreferenceUtil.getString(mContext, R.string.pref_expire_default);
		proxycon.setExpires(Util.parseInt(PreferenceUtil.getPrefString(mContext, R.string.pref_expire_key, defaultExpire), defaultExpire));

		if (privateId != null && privateId.length() > 0)
		{
		    proxycon.setContactParameters("+sip.message=\"TRUE\";" + "+sip." + username);
		}
		// Add parameters for push notifications
		if (resource.getBoolean(R.bool.enable_push_id))
		{
		    String regId = PreferenceUtil.getPrefString(mContext, R.string.push_reg_id_key, null);
		    String appId = PreferenceUtil.getString(mContext, R.string.push_sender_id);
		    if (regId != null && PreferenceUtil.getPrefBoolean(mContext, R.string.pref_push_notification_key, resource.getBoolean(R.bool.pref_push_notification_default)))
		    {
			String contactInfos = "app-id=" + appId + ";pn-type=google;pn-tok=" + regId + ";pn-msg-str=IM_MSG;pn-call-str=IC_MSG;pn-call-snd=ring.caf;pn-msg-snd=msg.caf;";
			proxycon.setContactParameters(contactInfos);
		    }
		}
		proxycon.enablePublish(false);
		proxycon.enableRegister(false);
		if (defaultAccount)
		{
		    proxycon.enableRegister(true);
		}

		if (customAuthorizationHeader)
		{
		    StringBuffer buf = new StringBuffer();
		    String header = mContext.getResources().getString(R.string.authorization_header);
		    header = String.format(header, privateId);
		    buf.append(header);
		    String valueFormatString = mContext.getResources().getString(R.string.authorization_value);
		    String value = null;
		    if (valueFormatString != null)
		    {
			value = String.format(valueFormatString, privateId, domain, domain);
			try
			{
			    value = URLEncoder.encode(value, "UTF-8");
			    buf.append(value.replace("+", "%20"));
			    buf.append(">");
			}
			catch (UnsupportedEncodingException e)
			{
			    value = null;
			}
		    }
		    if (value != null)
		    {
			proxycon.setIdentity(buf.toString());
		    }

		}
		// outbound proxy
		if (PreferenceUtil.getPrefBoolean(mContext, PreferenceUtil.getString(mContext, R.string.pref_enable_outbound_proxy_key) + key, false))
		{
		    proxycon.setRoute(proxy);
		}
		else
		{
		    proxycon.setRoute(null);
		}

		mLinphoneCore.addProxyConfig(proxycon);

		// proxycon.done();

		if (defaultAccount)
		{
		    mLinphoneCore.setDefaultProxyConfig(proxycon);
		}
	    }
	}
    }

    public boolean isAutoAcceptCamera()
    {
	return isVideoEnabled() && PreferenceUtil.getPrefBoolean(mContext, R.string.pref_video_automatically_accept_video_key, false);
    }

    public boolean isAutoInitiateVideoCalls()
    {
	return isVideoEnabled() && PreferenceUtil.getPrefBoolean(mContext, R.string.pref_video_initiate_call_with_video_key, false);
    }

    private void initMediaEncryption()
    {
	String pref = PreferenceUtil.getPrefString(mContext, R.string.pref_media_encryption_key, R.string.pref_media_encryption_key_none);
	MediaEncryption me = MediaEncryption.None;
	if (pref.equals(PreferenceUtil.getString(mContext, R.string.pref_media_encryption_key_srtp)))
	{
	    me = MediaEncryption.SRTP;
	}
	else if (pref.equals(PreferenceUtil.getString(mContext, R.string.pref_media_encryption_key_zrtp)))
	{
	    me = MediaEncryption.ZRTP;
	}
	SIPLogger.i("SIPInitRunnable", "Media encryption set to " + pref);
	mLinphoneCore.setMediaEncryption(me);
    }

    private void readAndSetAudioAndVideoPorts() throws NumberFormatException
    {
	int aPortStart, aPortEnd, vPortStart, vPortEnd;
	int defaultAudioPort, defaultVideoPort;
	defaultAudioPort = Integer.parseInt(PreferenceUtil.getString(mContext, R.string.default_audio_port));
	defaultVideoPort = Integer.parseInt(PreferenceUtil.getString(mContext, R.string.default_video_port));
	aPortStart = aPortEnd = defaultAudioPort;
	vPortStart = vPortEnd = defaultVideoPort;

	String audioPort = PreferenceUtil.getPrefString(mContext, R.string.pref_audio_port_key, String.valueOf(aPortStart));
	String videoPort = PreferenceUtil.getPrefString(mContext, R.string.pref_video_port_key, String.valueOf(vPortStart));

	if (audioPort.contains("-"))
	{
	    // Port range
	    aPortStart = Integer.parseInt(audioPort.split("-")[0]);
	    aPortEnd = Integer.parseInt(audioPort.split("-")[1]);
	}
	else
	{
	    try
	    {
		aPortStart = aPortEnd = Integer.parseInt(audioPort);
	    }
	    catch (NumberFormatException nfe)
	    {
		aPortStart = aPortEnd = defaultAudioPort;
	    }
	}

	if (videoPort.contains("-"))
	{
	    // Port range
	    vPortStart = Integer.parseInt(videoPort.split("-")[0]);
	    vPortEnd = Integer.parseInt(videoPort.split("-")[1]);
	}
	else
	{
	    try
	    {
		vPortStart = vPortEnd = Integer.parseInt(videoPort);
	    }
	    catch (NumberFormatException nfe)
	    {
		vPortStart = vPortEnd = defaultVideoPort;
	    }
	}

	if (aPortStart >= aPortEnd)
	{
	    mLinphoneCore.setAudioPort(aPortStart);
	}
	else
	{
	    mLinphoneCore.setAudioPortRange(aPortStart, aPortEnd);
	}

	if (vPortStart >= vPortEnd)
	{
	    mLinphoneCore.setVideoPort(vPortStart);
	}
	else
	{
	    mLinphoneCore.setVideoPortRange(vPortStart, vPortEnd);
	}
    }

    private void setSignalingTransportsFromConfiguration(Transports t)
    {
	Transports ports = new Transports(t);
	boolean useRandomPort = false;
	// PreferenceUtil.getPrefBoolean(mContext, R.string.pref_transport_use_random_ports_key,
	// resource.getBoolean(R.bool.pref_transport_use_random_ports_default));
	int lPreviousPort = Util.parseInt(PreferenceUtil.getPrefString(mContext, SIPConstant.SIP_SERVER_port, "5060"));
	// R.string.pref_sip_port_key, PreferenceUtil.getString(mContext, R.string.pref_sip_port_default)), 5060);
	if (lPreviousPort > 0xFFFF || useRandomPort)
	{
	    lPreviousPort = (int) (Math.random() * (0xFFFF - 1024)) + 1024;
	    SIPLogger.w("SIPInitRunnable", "Using random port " + lPreviousPort);

	}

	lPreviousPort = -1;

	// String transport = PreferenceUtil.getPrefString(mContext, R.string.pref_transport_key, PreferenceUtil.getString(mContext,
	// R.string.pref_transport_tcp_key));
	String transport = PreferenceUtil.getPrefString(mContext, SIPConstant.SIP_TRANSPORT, "TCP");
	if (transport.equalsIgnoreCase(PreferenceUtil.getString(mContext, R.string.pref_transport_tcp_key)))
	{
	    ports.udp = 0;
	    ports.tls = 0;
	    ports.tcp = lPreviousPort;
	}
	else if (transport.equalsIgnoreCase(PreferenceUtil.getString(mContext, R.string.pref_transport_tls_key)))
	{
	    ports.udp = 0;
	    ports.tcp = 0;
	    ports.tls = lPreviousPort;
	}
	else if (transport.equalsIgnoreCase(PreferenceUtil.getString(mContext, R.string.pref_transport_udp_key)))
	{
	    ports.tcp = 0;
	    ports.tls = 0;
	    ports.udp = lPreviousPort;
	}
	mLinphoneCore.setSignalingTransportPorts(ports);
    }

    private void enableDisableAudioCodec(String codec, int rate, int channels, int key) throws LinphoneCoreException
    {
	PayloadType pt = mLinphoneCore.findPayloadType(codec, rate, channels);
	if (pt != null)
	{
	    boolean enable = PreferenceUtil.getPrefBoolean(mContext, key, false);
	    mLinphoneCore.enablePayloadType(pt, enable);
	}
    }

    public boolean isVideoEnabled()
    {
	return PreferenceUtil.getPrefBoolean(mContext, R.string.pref_video_enable_key, false);
    }

    private void enableDisableAudioCodec(String codec, int rate, int channels, boolean enable) throws LinphoneCoreException
    {
	PayloadType pt = mLinphoneCore.findPayloadType(codec, rate, channels);
	if (pt != null)
	{
	    mLinphoneCore.enablePayloadType(pt, enable);
	}
    }

    private void enableDisableVideoCodecs(PayloadType videoCodec) throws LinphoneCoreException
    {
	String mime = videoCodec.getMime();
	int key;
	int defaultValueKey;
	SIPLogger.d("gopal", "gopal video codec" + mime);
	if ("MP4V-ES".equals(mime))
	{
	    key = R.string.pref_video_codec_mpeg4_key;
	    defaultValueKey = R.bool.pref_video_codec_mpeg4_default;
	}
	else if ("H264".equals(mime))
	{
	    key = R.string.pref_video_codec_h264_key;
	    videoCodec.setSendFmtp("profile-level-id=42801f");
	    videoCodec.setRecvFmtp("profile-level-id=42801f");
	    defaultValueKey = R.bool.pref_video_codec_h264_default;
	}
	else if ("H263-1998".equals(mime))
	{
	    key = R.string.pref_video_codec_h263_key;
	    defaultValueKey = R.bool.pref_video_codec_h263_default;
	}
	else if ("VP8".equals(mime))
	{
	    key = R.string.pref_video_codec_vp8_key;
	    defaultValueKey = R.bool.pref_video_codec_vp8_default;
	}
	else
	{
	    SIPLogger.e("Unhandled video codec ", mime);
	    mLinphoneCore.enablePayloadType(videoCodec, false);
	    return;
	}

	boolean enable = PreferenceUtil.getPrefBoolean(mContext, key, resource.getBoolean(defaultValueKey));
	mLinphoneCore.enablePayloadType(videoCodec, enable);
    }

    private void initFromConfTunnel()
    {
	if (!mLinphoneCore.isTunnelAvailable())
	{
	    return;
	}
	ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (manager != null)
	{
	    NetworkInfo info = manager.getActiveNetworkInfo();
	    mLinphoneCore.tunnelCleanServers();
	    // GG_KG_TBD
	    String host = null;
	    // getString(R.string.tunnel_host);
	    if (host == null || host.length() == 0)
	    {
		host = PreferenceUtil.getPrefString(mContext, mContext.getString(R.string.pref_tunnel_host_key), "");
	    }
	    int port = Integer.parseInt(PreferenceUtil.getPrefString(mContext, mContext.getString(R.string.pref_tunnel_port_key), "443"));
	    mLinphoneCore.tunnelAddServerAndMirror(host, port, 12345, 500);
	    manageTunnelServer(info);
	}
    }

    public void manageTunnelServer(NetworkInfo info)
    {
	if (mLinphoneCore == null)
	    return;
	if (!mLinphoneCore.isTunnelAvailable())
	    return;

	SIPLogger.i("SIPInitRunnable", "Managing tunnel");
	if (isTunnelNeeded(info))
	{
	    SIPLogger.i("SIPInitRunnable", "Tunnel need to be activated");
	    mLinphoneCore.tunnelEnable(true);
	}
	else
	{
	    SIPLogger.i("SIPInitRunnable", "Tunnel should not be used");
	    String pref = PreferenceUtil.getPrefString(mContext, R.string.pref_tunnel_mode_key, R.string.default_tunnel_mode_entry_value);
	    mLinphoneCore.tunnelEnable(false);
	    if (mContext.getString(R.string.tunnel_mode_entry_value_auto).equals(pref))
	    {
		mLinphoneCore.tunnelAutoDetect();
	    }
	}
    }

    private boolean isTunnelNeeded(NetworkInfo info)
    {
	if (info == null)
	{
	    SIPLogger.i("SIPInitRunnable", "No connectivity: tunnel should be disabled");
	    return false;
	}

	String pref = PreferenceUtil.getPrefString(mContext, R.string.pref_tunnel_mode_key, R.string.default_tunnel_mode_entry_value);

	if (mContext.getString(R.string.tunnel_mode_entry_value_always).equals(pref))
	{
	    return true;
	}
	if (info.getType() != ConnectivityManager.TYPE_WIFI && mContext.getString(R.string.tunnel_mode_entry_value_3G_only).equals(pref))
	{
	    SIPLogger.i("SIPInitRunnable", "need tunnel: 'no wifi' connection");
	    return true;
	}

	return false;
    }

    public void clearAuth()
    {
	mLinphoneCore.clearAuthInfos();
    }

    public void removeAccounts()
    {
	if (mLinphoneCore != null)
	{
	    // mLinphoneCore.clearAuthInfos();
	    mLinphoneCore.clearProxyConfigs();
	}

	for (int i = 0; i < extraAccountNumber; i++)
	{
	    String key = i == 0 ? "" : String.valueOf(i);
	    if (!PreferenceUtil.getPrefBoolean(mContext, PreferenceUtil.getString(mContext, R.string.pref_disable_account_key) + key, false))
	    {
		// initAccount(key, i == PreferenceUtil.getPrefInt(mContext,
		// R.string.pref_default_account_key, 0));
		PreferenceUtil.putPrefBoolean(mContext, PreferenceUtil.getString(mContext, R.string.pref_disable_account_key) + key, false);
		PreferenceUtil.putPrefString(mContext, PreferenceUtil.getString(mContext, R.string.pref_username_key) + key, null);
		PreferenceUtil.putPrefString(mContext, PreferenceUtil.getString(mContext, R.string.pref_passwd_key) + key, null);
		PreferenceUtil.putPrefString(mContext, PreferenceUtil.getString(mContext, R.string.pref_domain_key) + key, null);
	    }
	}
    }

}
