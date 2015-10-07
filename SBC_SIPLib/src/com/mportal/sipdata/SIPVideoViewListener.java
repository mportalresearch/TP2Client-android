package com.mportal.sipdata;

import org.linphone.mediastream.video.display.GL2JNIView;

import android.view.SurfaceView;

public interface SIPVideoViewListener
{
    public void setSelfPreviewVisibility(int visible);

    public void setRemotePreviewVisibility(int visible);

    public SurfaceView getSelfPreviewView();

    public GL2JNIView getRemotePreviewView();

    public void setProgressVisibility(int visible);

    public String getVideoId();

}
