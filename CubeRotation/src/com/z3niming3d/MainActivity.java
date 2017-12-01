package com.z3niming3d;



import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener{
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.custom_layout_example);
	    
	    glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);            
        
        // Assign our renderer.
        glSurfaceView.setRenderer(new dim3Renderer(this));
        rendererSet = true;
        Button b;
        b = (Button) this.findViewById(R.id.layoutOkay);
        b.setOnClickListener(this);
        
        LinearLayout ni = (LinearLayout) this.findViewById(R.id.scene1Holder);
        ni.addView(glSurfaceView);
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
	@Override
	public void onClick(View v) {
		finish();
	}
}
