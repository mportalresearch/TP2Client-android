package org.linphone.mediastream.video;

/*
 AndroidVideoWindowImpl.java
 Copyright (C) 2010  Belledonne Communications, Grenoble, France

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.linphone.mediastream.Log;
import org.linphone.mediastream.video.display.OpenGLESDisplay;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class AndroidVideoWindowImpl
{

    private static final String TAG = "AndroidVideoWindowImpl";
    private SurfaceView mVideoRenderingView;
    private SurfaceView mVideoPreviewView;

    private boolean useGLrendering;
    private Bitmap mBitmap;

    private Surface mSurface;
    private VideoWindowListener mListener;
    private Renderer renderer;

    // mPortal added
    private Callback renderingViewCallback;
    private Callback previewViewCallback;

    /**
     * Utility listener interface providing callback for Android events useful to Mediastreamer.
     */
    public static interface VideoWindowListener
    {
	void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface);

	void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw);

	void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface);

	void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw);

	void onDrawReady();
    };

    /**
     * @param renderingSurface
     *            Surface created by the application that will be used to render decoded video stream
     * @param previewSurface
     *            Surface created by the application used by Android's Camera preview framework
     */
    public AndroidVideoWindowImpl(SurfaceView renderingSurface, SurfaceView previewSurface)
    {
	mVideoRenderingView = renderingSurface;
	mVideoPreviewView = previewSurface;

	useGLrendering = (renderingSurface instanceof GLSurfaceView);

	mBitmap = null;
	mSurface = null;
	mListener = null;
    }

    public void init()
    {
	if (renderingViewCallback != null && mVideoRenderingView != null)
	{
	    mVideoRenderingView.getHolder().removeCallback(renderingViewCallback);
	    renderingViewCallback = null;
	}

	if (renderingViewCallback == null)
	{
	    renderingViewCallback = new Callback()
	    {
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
		    Log.i("Video display surface is being changed.");
		    if (!useGLrendering)
		    {
			synchronized (AndroidVideoWindowImpl.this)
			{
			    mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
			    mSurface = holder.getSurface();
			}
		    }
		    if (mListener != null)
			mListener.onVideoRenderingSurfaceReady(AndroidVideoWindowImpl.this, mVideoRenderingView);
		    Log.w("Video display surface changed");
		}

		public void surfaceCreated(SurfaceHolder holder)
		{
		    Log.w("Video display surface created");
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
		    if (!useGLrendering)
		    {
			synchronized (AndroidVideoWindowImpl.this)
			{
			    mSurface = null;
			    mBitmap = null;
			}
		    }
		    if (mListener != null)
			mListener.onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl.this);
		    Log.d("Video display surface destroyed");
		}
	    };
	}
	// register callback for rendering surface events
	mVideoRenderingView.getHolder().addCallback(renderingViewCallback);

	if (previewViewCallback != null && mVideoPreviewView != null)
	{
	    mVideoPreviewView.getHolder().removeCallback(previewViewCallback);
	    previewViewCallback = null;
	}

	if (previewViewCallback == null)
	{
	    previewViewCallback = new Callback()
	    {
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
		    Log.i("Video preview surface is being changed.");
		    if (mListener != null)
			mListener.onVideoPreviewSurfaceReady(AndroidVideoWindowImpl.this, mVideoPreviewView);
		    Log.w("Video preview surface changed");
		}

		public void surfaceCreated(SurfaceHolder holder)
		{
		    Log.w("Video preview surface created");
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
		    if (mListener != null)
			mListener.onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl.this);
		    Log.d("Video preview surface destroyed");
		}
	    };
	}
	// register callback for preview surface events
	if (mVideoPreviewView != null)
	{
	    mVideoPreviewView.getHolder().addCallback(previewViewCallback);
	}

	if (useGLrendering)
	{
	    renderer = new Renderer();
	    ((GLSurfaceView) mVideoRenderingView).setRenderer(renderer);
	    ((GLSurfaceView) mVideoRenderingView).setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
    }

    public void release()
    {
	// mSensorMgr.unregisterListener(this);
    }

    public void setListener(VideoWindowListener l)
    {
	mListener = l;
    }

    public Surface getSurface()
    {
	if (useGLrendering)
	    Log.e("View class does not match Video display filter used (you must use a non-GL View)");
	return mVideoRenderingView.getHolder().getSurface();
    }

    public Bitmap getBitmap()
    {
	if (useGLrendering)
	    Log.e("View class does not match Video display filter used (you must use a non-GL View)");
	return mBitmap;
    }

    public void setOpenGLESDisplay(int ptr)
    {
	if (!useGLrendering)
	    Log.e("View class does not match Video display filter used (you must use a GL View)");
	renderer.setOpenGLESDisplay(ptr);
    }

    public void requestRender()
    {
	((GLSurfaceView) mVideoRenderingView).requestRender();
    }

    // Called by the mediastreamer2 android display filter
    public synchronized void update()
    {
	if (mSurface != null)
	{
	    try
	    {
		Canvas canvas = mSurface.lockCanvas(null);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		mSurface.unlockCanvasAndPost(canvas);

	    }
	    catch (IllegalArgumentException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    catch (OutOfResourcesException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    private class Renderer implements GLSurfaceView.Renderer
    {
	int ptr;
	boolean initPending;
	int width, height;

	public Renderer()
	{
	    ptr = 0;
	    initPending = false;
	}

	public void setOpenGLESDisplay(int ptr)
	{
	    /*
	     * Synchronize this with onDrawFrame: - they are called from different threads (Rendering thread and Linphone's one) - setOpenGLESDisplay can modify
	     * ptr while onDrawFrame is using it
	     */
	    synchronized (this)
	    {
		if (this.ptr != 0 && ptr != this.ptr)
		{
		    initPending = true;
		}
		this.ptr = ptr;
	    }
	}

	public void onDrawFrame(GL10 gl)
	{
	    /*
	     * See comment in setOpenGLESDisplay
	     */
	    synchronized (this)
	    {
		if (ptr == 0)
		    return;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		if (initPending)
		{
		    OpenGLESDisplay.init(ptr, width, height);
		    initPending = false;
		}
		OpenGLESDisplay.render(ptr);
	    }
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
	    Log.d(TAG, "Virus99--" + "onSurfaceChanged() called..");
	    /* delay init until ptr is set */
	    this.width = width;
	    this.height = height;
	    if (height == 0)
	    height = 1;   // To prevent divide by zero
	    float aspect = (float)width / height;
	    gl.glViewport(0, 0, width, height);
	    gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
	    gl.glLoadIdentity();                 // Reset projection matrix
	      // Use perspective projection
	    GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);
	  
	    gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
	    gl.glLoadIdentity();  
	    initPending = true;
	    if (mListener != null)
	    {

		mListener.onDrawReady();
	    }
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
	    Log.d(TAG, "Virus99--" + "onSurfaceCreated() called..");
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
	    gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
	    gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
	    gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
	    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
	    gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
	    gl.glDisable(GL10.GL_DITHER);   

	}
    }

    public static int rotationToAngle(int r)
    {
	switch (r)
	{
	case Surface.ROTATION_0:
	    return 0;
	case Surface.ROTATION_90:
	    return 90;
	case Surface.ROTATION_180:
	    return 180;
	case Surface.ROTATION_270:
	    return 270;
	}
	return 0;
    }

    public void cleanup()
    {
	if (renderingViewCallback != null && mVideoRenderingView != null)
	{
	    mVideoRenderingView.getHolder().removeCallback(renderingViewCallback);
	    renderingViewCallback = null;
	}

	if (previewViewCallback != null && mVideoPreviewView != null)
	{
	    mVideoPreviewView.getHolder().removeCallback(previewViewCallback);
	    previewViewCallback = null;
	}
    }
}
