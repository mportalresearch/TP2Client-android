package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mportal.handlemanagement.contants.HandleManagementConstants;

@Root(strict = false)
public class Call extends Session
{

    @Element(name = "conferenceid", required = false)
    private String conferenceid;

    @Element(name = "audio", required = false)
    private String audio;

    @Element(name = "video", required = false)
    private String video;

    @Element(name = "lock", required = false)
    private String lock;
    
    private boolean removed = false;

    public Call()
    {
	super.setType(HandleManagementConstants.SessionType.CALL_SESSION);
    }

    public String getConferenceid()
    {
	return conferenceid;
    }

    public void setConferenceid(String confId)
    {
	conferenceid = confId;
    }

    public String getAudio()
    {
	return audio;
    }

    public void setAudio(String audio)
    {
	this.audio = audio;
    }

    public String getVideo()
    {
	return video;
    }

    public void setVideo(String video)
    {
	this.video = video;
    }

    public boolean isRemoved()
    {
	return removed;
    }

    public void setRemoved(boolean removed)
    {
	this.removed = removed;
    }

    public String getLock()
    {
        return lock;
    }

    public void setLock(String lock)
    {
        this.lock = lock;
    }

}