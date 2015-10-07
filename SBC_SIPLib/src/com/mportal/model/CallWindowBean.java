package com.mportal.model;

import java.util.ArrayList;

import com.mportal.siputil.Util.WindowCallType;

public class CallWindowBean
{
    WindowCallType windowCallType;
    
    ArrayList<ParticipantBean> participantBean;
    
    int confParticipantSize = 0;
    
    
    
    public int getConfParticipantSize()
    {
        return confParticipantSize;
    }

    public void setConfParticipantSize(int confParticipantSize)
    {
        this.confParticipantSize = confParticipantSize;
    }

    public WindowCallType getWindowCallType()
    {
        return windowCallType;
    }

    public void setWindowCallType(WindowCallType windowCallType)
    {
        this.windowCallType = windowCallType;
    }

    public ArrayList<ParticipantBean> getParticipantBean()
    {
        return participantBean;
    }

    public void addNewParticipant(ParticipantBean userDetails)
    {
	if(participantBean == null)
	{
	    participantBean = new ArrayList<ParticipantBean>();
	}
	
        participantBean.add(userDetails);
    }
    
    
}
