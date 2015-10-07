package org.linphone.service;

public class SIPPublishEvent
{
    private String username;

    private String domain;

    private String displayname;

    private String lAddress;

    private String eventName;

    private String contentType;

    private String subContentType;

    private long expire;

    private byte[] content;

    public String getUsername()
    {
	return username;
    }

    public void setUsername(String username)
    {
	this.username = username;
    }

    public String getDomain()
    {
	return domain;
    }

    public void setDomain(String domain)
    {
	this.domain = domain;
    }

    public String getDisplayname()
    {
	return displayname;
    }

    public void setDisplayname(String displayname)
    {
	this.displayname = displayname;
    }

    public String getlAddress()
    {
	return lAddress;
    }

    public void setlAddress(String lAddress)
    {
	this.lAddress = lAddress;
    }

    public String getEventName()
    {
	return eventName;
    }

    public void setEventName(String eventName)
    {
	this.eventName = eventName;
    }

    public String getContentType()
    {
	return contentType;
    }

    public void setContentType(String contentType)
    {
	this.contentType = contentType;
    }

    public String getSubContentType()
    {
	return subContentType;
    }

    public void setSubContentType(String subContentType)
    {
	this.subContentType = subContentType;
    }

    public long getExpire()
    {
	return expire;
    }

    public void setExpire(long expire)
    {
	this.expire = expire;
    }

    public byte[] getContent()
    {
	return content;
    }

    public void setContent(byte[] content)
    {
	this.content = content;
    }

    @Override
    public int hashCode()
    {
	return 12121;
    }

}
