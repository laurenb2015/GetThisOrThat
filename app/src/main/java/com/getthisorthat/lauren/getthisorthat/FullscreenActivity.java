package com.getthisorthat.lauren.getthisorthat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * SurfaceHolder.Callback necessary for camera preview
 * therefore this class must implement onDestroy, etc methods
 */
public class FullscreenActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    // 2 LINES HERE
    // Camera class is deprecated but still fully functional
    // android.hardware.camera2 will only work with phones with APIs 21 and up
    // to work on older models must use older class
    Camera mCamera;
    // surfaceview lays beneath its parent and is used to show camera preview
    SurfaceView mPreview;

    private OrientationEventListener mOrientationEventListener;
    private int mOrientation =  -1;
    private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.preview);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // 4 LINES HERE
        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

     //   mCamera = Camera.open();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
    // 2 METHODS HERE
    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
   //     mCamera.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
        Log.i("PREVIEW", "onDestroy called");
    }

    // 3 METHODS HERE
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width, selected.height);
  //      params.set("camera-id", 2);
        mCamera.setParameters(params);
  //      int cameraId = 2;

    //    onOrientationChanged(this.getResources().getConfiguration().orientation);

      //  params.setRotation(0);

        /*
        if (getResources().getConfiguration().orientation !=
                Configuration.ORIENTATION_LANDSCAPE)
        {
            params.set("orientation", "portrait");
            Log.v("GETTHISORTHAT", "potrait now");
/*
            // CameraApi is a wrapper to check for backwards compatibility
            if (CameraApi.isSetRotationSupported())
            {
                CameraApi.setRotation(p, 90);
            }

        }
*/
        setCameraDisplayOrientation(this,mCamera);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // if camera not released when pressing home button
        // then native camera app will fail
        mCamera.release();
        Log.i("PREVIEW", "surfaceDestroyed");
    }


    public static void setCameraDisplayOrientation(Activity activity, android.hardware.Camera camera) {
    //    android.hardware.Camera.CameraInfo info =
  //              new android.hardware.Camera.CameraInfo();
  //      android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
       /* if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
         */
//        result = (degrees + 180) % 360;
        result = degrees;

        //}
        camera.setDisplayOrientation(result);
    }
/*
    //@Override
    public void onOrientationChanged(int orientation) {

        // determine our orientation based on sensor response
        int lastOrientation = mOrientation;

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getOrientation() == Surface.ROTATION_0) {   // landscape oriented devices
            if (orientation >= 315 || orientation < 45) {
                if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                    mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                }
            } else if (orientation < 315 && orientation >= 225) {
                if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                    mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                }
            } else if (orientation < 225 && orientation >= 135) {
                if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                    mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                }
            } else if (orientation <135 && orientation > 45) {
                if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                    mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                }
            }
        } else {  // portrait oriented devices
            if (orientation >= 315 || orientation < 45) {
                if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                    mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                }
            } else if (orientation < 315 && orientation >= 225) {
                if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                    mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                }
            } else if (orientation < 225 && orientation >= 135) {
                if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                    mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                }
            } else if (orientation <135 && orientation > 45) {
                if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                    mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                }
            }
        }
/*
        if (lastOrientation != mOrientation) {
            changeRotation(mOrientation, lastOrientation);
        }
    }
*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
