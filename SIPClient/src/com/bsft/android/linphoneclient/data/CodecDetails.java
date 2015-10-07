package com.bsft.android.linphoneclient.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CodecDetails
{
    @JsonProperty("type")
    private String type;

    @JsonProperty("clockRate")
    private String clockRate;

    @JsonProperty("enable")
    private String enable;

    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public String getClockRate()
    {
	return clockRate;
    }

    @JsonIgnore
    public void setClockRate(String clockRate)
    {
	this.clockRate = clockRate;
    }

    public String getEnable()
    {
	return enable;
    }

    public void setEnable(String enable)
    {
	this.enable = enable;
    }
}