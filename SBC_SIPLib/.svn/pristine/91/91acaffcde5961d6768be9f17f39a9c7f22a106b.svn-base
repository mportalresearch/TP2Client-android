package org.linphone.service;

import android.os.Parcel;
import android.os.Parcelable;

public class SIPCustomHeader implements Parcelable
{

    private String headerName;

    private String headerValue;

    public SIPCustomHeader()
    {

    }

    public SIPCustomHeader(String headerName, String headerValue)
    {
	this.headerName = headerName;
	this.headerValue = headerValue;
    }

    @Override
    public int describeContents()
    {
	return 0;
    }

    public static final Parcelable.Creator<SIPCustomHeader> CREATOR = new Parcelable.Creator<SIPCustomHeader>()
    {
	public SIPCustomHeader createFromParcel(Parcel in)
	{
	    SIPCustomHeader data = new SIPCustomHeader();
	    data.readData(in);
	    return data;
	}

	public SIPCustomHeader[] newArray(int size)
	{
	    return new SIPCustomHeader[size];
	}
    };

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
	dest.writeString(headerName);
	dest.writeString(headerValue);
    }

    protected void readData(Parcel in)
    {
	setHeaderName(in.readString());
	setHeaderValue(in.readString());
    }

    public void setHeaderValue(String value)
    {
	this.headerValue = value;
    }

    public void setHeaderName(String value)
    {
	this.headerName = value;
    }

    public String getHeaderName()
    {
	return headerName;
    }

    public String getHeaderValue()
    {
	return headerValue;
    }

}
