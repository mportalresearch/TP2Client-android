package org.linphone.service;

public class SIPEvent implements Cloneable
{

    private String username;

    private String domain;

    private String displayname;

    private String lAddress;

    private String eventName;

    private String contentType;

    private String subContentType;

    private long expire;

    private String content;

    private byte[] notifyContent;

    private String notifyContentType;

    private String notifySubContentType;

    private String notifyContentEncoding;
    
    private boolean isInitialSubscribe;

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

    public void setContent(String content)
    {
	this.content = content;
    }

    public String getContent()
    {
	return this.content;
    }

    public void setNotifyContent(byte[] notifyContent)
    {
	this.notifyContent = notifyContent;
    }

    public byte[] getNotifyContent()
    {
	return this.notifyContent;
    }

    public void setNotifyContentType(String value)
    {
	this.notifyContentType = value;
    }

    public void setNotifySubContentType(String value)
    {
	this.notifySubContentType = value;
    }

    public void setNotifyContentEncoding(String value)
    {
	this.notifyContentEncoding = value;
    }

    public String getNotifyContentType()
    {
	return this.notifyContentType;
    }

    public String getNotifySubContentType()
    {
	return this.notifySubContentType;
    }

    public String getNotifyContentEncoding()
    {
	return this.notifyContentEncoding;
    }
    
    
    @Override
    public int hashCode()
    {
	int result = 1111;

	if(eventName != null)
	{
	    result = 31 * result + eventName.hashCode(); 
	}
	
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
	SIPEvent event = (SIPEvent) o;
	if (event.getEventName() == null || eventName == null)
	{
	    return false;
	}
	if (event.getEventName().equalsIgnoreCase(eventName))
	{
	    return true;
	}
	return false;
    }

    /**
     * @return the isInitialSubscribe
     */
    public boolean isInitialSubscribe()
    {
	return isInitialSubscribe;
    }

    /**
     * @param isInitialSubscribe the isInitialSubscribe to set
     */
    public void setInitialSubscribe(boolean isInitialSubscribe)
    {
	this.isInitialSubscribe = isInitialSubscribe;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

}
