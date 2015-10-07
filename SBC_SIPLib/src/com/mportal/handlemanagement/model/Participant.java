package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mportal.handlemanagement.contants.HandleManagementConstants;

@Root(strict = false)
public class Participant
{

    @Attribute(name = "id", required = false)
    private String id;

    @Attribute(name = "sip", required = false)
    private String sip;

    @Attribute(name = "disp", required = false)
    private String disp;

    @Attribute(name = "media", required = false)
    private String media;

    @Attribute(name = "state", required = false)
    private String state;

    @Element(name = "sipcallid", required = false)
    private String sipcallid;

    @Element(name = "callerpasscode", required = false)
    private String callerpasscode;

    @Element(name = "moderator", required = false)
    private String moderator;

    @Element(name = "hold", required = false)
    private String hold;

    @Element(name = "mute", required = false)
    private String mute;

    @Element(name = "kick", required = false)
    private String kick;

    private String moderatorPasscode;
    
    private boolean newlyActive;

    public Participant()
    {
    }

    public Participant(String sip)
    {
	this.sip = sip;
    }

    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public String getSip()
    {
	return sip;
    }

    public void setSip(String sip)
    {
	this.sip = sip;
    }

    public String getDisp()
    {
	return disp;
    }

    public void setDisp(String disp)
    {
	this.disp = disp;
    }

    public String getMedia()
    {
	if (media != null)
	{
	    String mediaTemp = media;
	    int length = mediaTemp.length();
	    int checkLength = HandleManagementConstants.MAX_MEDIA_BITS - length;
	    if (checkLength != 0)
	    {
		int i = 0;
		char[] tempChar = new char[checkLength];
		while (i < checkLength)
		{
		    tempChar[i] = '0';
		    i++;
		}
		media = new String(tempChar) + media;
	    }
	}
	return media;
    }

    public void setMedia(int index, boolean newBit)
    {
	if (media != null)
	{
	    char[] bits = media.toCharArray();
	    if (newBit)
	    {
		bits[index] = '1';
	    }
	    else
	    {
		bits[index] = '0';
	    }
	    media = new String(bits);
	}

    }

    public void setMedia(String media)
    {
	this.media = media;
    }

    public String getState()
    {
	return state;
    }

    public void setState(String state)
    {
	this.state = state;
    }

    public String getSipcallid()
    {
	return sipcallid;
    }

    public void setSipcallId(String sipcallid)
    {
	this.sipcallid = sipcallid;
    }

    public String getCallerpasscode()
    {
	return callerpasscode;
    }

    public void setCallerpasscode(String callerpasscode)
    {
	this.callerpasscode = callerpasscode;
    }

    public String getModerator()
    {
	return moderator;
    }

    // public ArrayList<Enum> activeSessions;

    public void setModerator(String moderator)
    {
	this.moderator = moderator;
    }

    public String getModeratorPasscode()
    {
	return moderatorPasscode;
    }

    public void setModeratorPasscode(String moderatorPasscode)
    {
	this.moderatorPasscode = moderatorPasscode;
    }

    public String getHold()
    {
	return hold;
    }

    public void setHold(String hold)
    {
	this.hold = hold;
    }

    public String getMute()
    {
	return mute;
    }

    public void setMute(String mute)
    {
	this.mute = mute;
    }

    public String getKick()
    {
	return kick;
    }

    public void setKick(String kick)
    {
	this.kick = kick;
    }

    public boolean isNewlyActive()
    {
        return newlyActive;
    }

    public void setNewlyActive(boolean newlyActive)
    {
        this.newlyActive = newlyActive;
    }

    @Override
    public boolean equals(Object o)
    {

	boolean retVal = false;
	if (o != null && sip != null && ((Participant) o).sip.equalsIgnoreCase(sip))
	{
	    retVal = true;
	}
	return retVal;
    }

}
