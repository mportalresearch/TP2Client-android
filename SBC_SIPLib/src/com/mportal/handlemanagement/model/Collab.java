package com.mportal.handlemanagement.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mportal.handlemanagement.contants.HandleManagementConstants;

@Root(strict = false)
public class Collab extends Session
{
    @Element(name = "scopeid", required = false)
    private String scopeid;    
    public Collab()
    {
	super.setType(HandleManagementConstants.SessionType.COLLAB_SESSION);
    }
    public String getScopeid()
    {
        return scopeid;
    }
    public void setScopeid(String scopeid)
    {
        this.scopeid = scopeid;
    }
}
