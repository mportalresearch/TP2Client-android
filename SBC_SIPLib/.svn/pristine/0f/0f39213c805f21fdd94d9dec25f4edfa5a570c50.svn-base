package com.mportal.sipdata;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.AndroidVideoWindowImpl.VideoWindowListener;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration.AndroidCamera;
import org.linphone.service.SIPCoreManager;

import android.opengl.GLSurfaceView;
import android.view.SurfaceView;
import android.view.View;

import com.mportal.logger.SIPLogger;

public class SIPVideoData
{

    private SurfaceView renderingSurface;

    private SurfaceView previewSurface;

    private VideoWindowListener mListener;

    private AndroidVideoWindowImpl videoImpl;

    private boolean initialized;

    private SIPVideoViewListener sipVideoListener;

    private SIPCall sipcall;

    private int cameraId = -1;

    public SIPVideoData()
    {

    }

    public void setSIPCall(SIPCall videoCall)
    {
	this.sipcall = videoCall;
    }

    public SIPCall getSIPCall()
    {
	return this.sipcall;
    }

    public void setVideoViewListener(SIPVideoViewListener sipVideoListener)
    {
	this.sipVideoListener = sipVideoListener;
    }

    public SIPVideoViewListener getVideoViewListener()
    {
	return this.sipVideoListener;
    }

    public void setVideoData(SurfaceView renderingSurface, SurfaceView previewSurface, VideoWindowListener l)
    {
	this.renderingSurface = renderingSurface;
	this.previewSurface = previewSurface;
	mListener = l;
	// GG_KG_TBD - based on the configuration, this will get change.
	if (renderingSurface == null || previewSurface == null)
	{
	    return;
	}
	// doInitialization();
    }

    public void doInitialization(boolean VIDEO_ACCEPT_MODE)
    {
	videoImpl = new AndroidVideoWindowImpl(renderingSurface, previewSurface);
	setFrontCamera();
	videoImpl.setListener(new AndroidVideoWindowImpl.VideoWindowListener()
	{
	    public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface)
	    {
		LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
		if (lcore != null)
		{
		    lcore.setVideoWindow(vw);
		}
		renderingSurface = surface;
	    }

	    public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw)
	    {
		LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
		if (lcore != null)
		{
		    lcore.setVideoWindow(null);
		}
	    }

	    public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface)
	    {
		previewSurface = surface;

		LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
		if (lcore != null)
		{
		    lcore.setPreviewWindow(previewSurface);
		}

		SIPLogger.d("ddd", "GG:VIDEO " + previewSurface);

		if (cameraId == -1)
		{
		    resetCameraToFront();
		}
		else
		{
		    setVideoCamera(cameraId);
		}
	    }

	    public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw)
	    {
		// Remove references kept in jni code and restart camera
		LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
		if (lcore != null)
		{
		    lcore.setPreviewWindow(null);
		}
	    }

	    @Override
	    public void onDrawReady()
	    {
		if (sipVideoListener != null)
		{
		    sipVideoListener.setProgressVisibility(View.GONE);
		}

	    }
	});

	videoImpl.init();
	if (videoImpl != null)
	{
	    if (VIDEO_ACCEPT_MODE)
	    {
		sipVideoListener.setRemotePreviewVisibility(View.VISIBLE);
		sipVideoListener.setSelfPreviewVisibility(View.VISIBLE);
		synchronized (videoImpl)
		{
		    LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
		    if (lcore != null)
		    {
			lcore.setVideoWindow(videoImpl);
		    }
		}
	    }
	}

	initialized = true;

    }

    private void setFrontCamera()
    {
	int camId = -1;
	AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
	for (AndroidCamera androidCamera : cameras)
	{
	    if (androidCamera.frontFacing == true)
		camId = androidCamera.id;
	}
	LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
	if (lcore != null)
	{
	    lcore.setVideoDevice(camId);
	}
    }

    private void setVideoCamera(int camId)
    {
	AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
	for (AndroidCamera androidCamera : cameras)
	{
	    if (androidCamera.id == camId)
	    {
		LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
		if (lcore != null)
		{
		    lcore.setVideoDevice(camId);
		    if (sipcall != null)
		    {
			lcore.updateCall((LinphoneCall) sipcall.getCallObject(), null);
		    }
		}
		return;
	    }
	}

	resetCameraToFront();
    }

    private void resetCameraToFront()
    {
	int camId = -1;
	AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
	for (AndroidCamera androidCamera : cameras)
	{
	    if (androidCamera.frontFacing == true)
	    {
		camId = androidCamera.id;
	    }
	}
	cameraId = camId;
	LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
	if (lcore != null)
	{
	    lcore.setVideoDevice(camId);
	    if (sipcall != null)
	    {
		lcore.updateCall((LinphoneCall) sipcall.getCallObject(), null);
	    }
	}
    }

    public void onPause()
    {
	if (videoImpl != null)
	{
	    // GG_KG_TBD Should we use synchornized
	    LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
	    if (lcore != null)
	    {
		lcore.setVideoWindow(null);
	    }
	}
	if (renderingSurface instanceof GLSurfaceView)
	{
	    ((GLSurfaceView) renderingSurface).onPause();
	}
    }

    public void onResume()
    {
	if (renderingSurface instanceof GLSurfaceView)
	{
	    ((GLSurfaceView) renderingSurface).onResume();
	}
	if (videoImpl != null)
	{
	    LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
	    if (lcore != null)
	    {
		lcore.setVideoWindow(videoImpl);
	    }
	}
    }

    public boolean isInitialized()
    {
	return initialized;
    }

    public void reInitialize(boolean VIDEO_ACCEPT_MODE)
    {
	if (VIDEO_ACCEPT_MODE)
	{
	    if (videoImpl != null)
	    {
		sipVideoListener.setRemotePreviewVisibility(View.VISIBLE);
		sipVideoListener.setSelfPreviewVisibility(View.VISIBLE);
		synchronized (videoImpl)
		{
		    LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
		    if (lcore != null)
		    {
			lcore.setVideoWindow(videoImpl);
		    }
		}
	    }
	}
    }

    public void cleanup()
    {
//	releaseResource();
	if (videoImpl != null)
	{
	    videoImpl.cleanup();
	    videoImpl = null;
	}
	sipVideoListener = null;
	renderingSurface = null;
	previewSurface = null;
	cameraId = -1;
    }

    public void releaseResource()
    {
	if (videoImpl != null)
	{
	    LinphoneCore lcore = SIPCoreManager.getInstance().getLinphoneCore();
	    if (lcore != null)
	    {
		lcore.setVideoWindow(null);
		lcore.setPreviewWindow(null);
	    }
	    sipVideoListener.setRemotePreviewVisibility(View.GONE);
	    sipVideoListener.setSelfPreviewVisibility(View.GONE);
	}
    }

    public void setCameraId(int camType)
    {
	cameraId = camType;
    }
    
    public int getCameraId()
    {
	return cameraId;
    }

}
