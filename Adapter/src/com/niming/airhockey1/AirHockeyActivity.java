package com.niming.airhockey1;

import com.niming.airhockey1.AirHockeyRender;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class AirHockeyActivity extends Activity {
	/*
	 * Hold a reference to our GLSurfaceView
	 */
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	
	protected void onCreate(Bundle savedInstanceState) {
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

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);            
            
            // Assign our renderer.
            glSurfaceView.setRenderer(new AirHockeyRender(this));
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
	    setContentView(glSurfaceView);    
	 }
	protected void onPause(){
		super.onPause();
		if(rendererSet)
			glSurfaceView.onPause();
	}
	protected void onResume(){
		super.onResume();
		if(rendererSet)
			glSurfaceView.onResume();
	}
}