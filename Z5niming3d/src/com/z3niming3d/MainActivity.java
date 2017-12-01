package com.z3niming3d;



import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    glSurfaceView = new GLSurfaceView(this);
	    
        glSurfaceView.setEGLContextClientVersion(2);            
        
        // Assign our renderer.
        glSurfaceView.setRenderer(new dim3Renderer(this));
        rendererSet = true;
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
