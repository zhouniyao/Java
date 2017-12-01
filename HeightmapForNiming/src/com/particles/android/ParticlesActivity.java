/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.particles.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class ParticlesActivity extends Activity {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    ParticlesRenderer particlesRenderer = new ParticlesRenderer(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager = 
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
            .getDeviceConfigurationInfo();
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
            configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                 && (Build.FINGERPRINT.startsWith("generic")
                  || Build.FINGERPRINT.startsWith("unknown")
                  || Build.MODEL.contains("google_sdk")
                  || Build.MODEL.contains("Emulator")
                  || Build.MODEL.contains("Android SDK built for x86")));

//        final ParticlesRenderer particlesRenderer = new ParticlesRenderer(this);
        
        if (supportsEs2) {
            // ...
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            glSurfaceView.setRenderer(particlesRenderer);
            rendererSet = true;
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since 
             * we're not doing anything, the app will crash if the device 
             * doesn't support OpenGL ES 2.0. If we publish on the market, we 
             * should also add the following to AndroidManifest.xml:
             * 
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             * 
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG).show();
            return;
        }
   glSurfaceView.setOnTouchListener(new OnTouchListener() {
            
       float previousX, previousY;

            public boolean onTouch(View v, MotionEvent event) {
                if(event != null){
                    //Convert touch coordinates into normalized device coordinates,
                    //keeping in mind that Android's Y coordinates are inverted.
                    //视图左上角映射为(0,0)，右下角映射到的坐标为视图的大小
                    /*
                     * 检查一下这个事件按压(press)还是拖拽(drag)
                     * 【重要提示】Android的UI运行在主线程，而OpenGL的GLSurfaceView运行在一个单独的线程中，
                     * 因此我们需要使用线程安全的技术在两个线程间通讯。
                     * 我们使用queueEvent()给OpenGL线程分发调用，对于按压事件，调用airHockeyRenderer.handleTouchPress()。
                     * 对于拖拽事件，调用airHockeyRender.handleTouchDrag()。 
                     */
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                        /*测量你的手指在每个连续的onTouch()调用之间滑动多远*/
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;
                        
                        previousX = event.getX();
                        previousY = event.getY();
                        
                        glSurfaceView.queueEvent(new Runnable(){

                            @Override
                            public void run() {
                                particlesRenderer.handleTouchDrag(deltaX, deltaY);
                            }
                        });
                    } 
//                  Toast.makeText(AirHockeyActivity.this, "X =" + normalizedX + "Y =" + normalizedY, Toast.LENGTH_SHORT ).show();
                    return true;
                }else{
                    return false;
                }
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}