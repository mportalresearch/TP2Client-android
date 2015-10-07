package com.bsft.android.linphoneclient.moderator.controls;

import com.bsft.android.linphoneclient.logger.MPLogger;


public class ModeratorEventsManager
{

    public static String TAG = ModeratorEventsManager.class.getSimpleName();

    public String confId = null;

    public static String domainAddress = null;

    public static String targetIP = null;

    private boolean isVersioned = false;

    private boolean isConferenceActivated = false;

    public ModeratorEventsManager()
    {
	if (domainAddress != null && targetIP != null)
	{
	    MPLogger.v(TAG, "init");

	    if (!domainAddress.endsWith("/"))
	    {
		domainAddress += "/";
	    }
	}
    }

    public void setConferenceId(String conferenceId)
    {
	this.confId = conferenceId;
    }

    public ModeratorApiExecutor init()
    {
	if (confId == null || targetIP == null || domainAddress == null)
	{
	    MPLogger.d(TAG, "Either domainAddress, confId or targetIp is null");
	    return null;
	}

	ModeratorApiExecutor moderatorApiExec = new ModeratorApiExecutor(this, confId, domainAddress, targetIP);
	return moderatorApiExec;
    }

    public boolean isVersioned()
    {
	return isVersioned;
    }

    public void setVersioned(boolean isVersioned)
    {
	this.isVersioned = isVersioned;
    }

    public boolean isConferenceActivated()
    {
	return isConferenceActivated;
    }

    public void setConferenceActivated(boolean isConferenceActivated)
    {
	this.isConferenceActivated = isConferenceActivated;
    }

}
