package org.linphone.service;

@SuppressWarnings("serial")
public class SIPException extends Exception
{
    public static final int CONFIG_EXCEPTION = -900;
    private int errorCode;

    public SIPException(int errorCode, String message)
    {
	super(message);
	this.errorCode = errorCode;
    }

    public int getErrorCode()
    {
	return this.errorCode;
    }
}
