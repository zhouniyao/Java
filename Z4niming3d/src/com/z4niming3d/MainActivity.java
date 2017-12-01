package com.z4niming3d;
import static niming.util.Constants.*;
import java.util.Iterator;



import niming.parser.Number3d;
import niming.parser.ParseObj;
import niming.parser.Parser;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

public class MainActivity extends Activity  {  
	private static final int NUM_PER_VER = 3;
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        
        glSurfaceView = new GLSurfaceView(this);
        
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
                glSurfaceView.setRenderer(new ObjRenderer(this));
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
        
        
        /*顶点转换成数组【成功】*/
//        float[] verArray = new float[obj1.verticesNum * NUM_PER_VER];
//        int j = 0;
//        Iterator iter = obj1.vertices.iterator();
//        while(iter.hasNext()){
//        	Number3d vertex = (Number3d) iter.next();
//        	verArray[j++] = vertex.getX();
//        	verArray[j++] = vertex.getY();
//        	verArray[j++] = vertex.getZ();
//        }//拆ArrayList<Number3d> vertices 包
//        System.out.println("&========");
////        System.out.println("size===" + verArray.length);//24
//        for (int i = 0; i < verArray.length; i++) {
//			System.out.println(" " + verArray[i]);
//		}
        
        
//        Log.d(Parser.TAG, "verticesNum: " + obj.verticesNum );
//        Log.d(Parser.TAG, "UVCoordsNum: " + obj.UVCoordsNum );
//        Log.d(Parser.TAG, "normalsNum: " + obj.normalsNum );
//        Log.d(Parser.TAG, "facesNum: " + obj.facesNum );
////        sb.append(obj.body);
//        
//        
//        Iterator it = obj.vertices.iterator();
//        while(it.hasNext()){
//        	sb.append(" " + it.next());
//        }
//        it = obj.texCoords.iterator();
//        sb.append("\n");
//        while(it.hasNext()){
//        	sb.append(" " + it.next());
//        }
//        it = obj.normals.iterator();
//        sb.append("\n");
//        while(it.hasNext()){
//        	sb.append(" " + it.next());
//        }
//        it = obj.vIndex.iterator();
//        sb.append("\n====================");
//        while(it.hasNext()){
//        	sb.append(" " + it.next());
//        }
//        it = obj.vtIndex.iterator();
//        sb.append("\n===================");
//        while(it.hasNext()){
//        	sb.append(" " + it.next());
//        }
//        it = obj.vnIndex.iterator();
//        sb.append("\n===================");
//        while(it.hasNext()){
//        	sb.append(" " + it.next());
//        }
//
//        
//        TextView tv = new TextView(this);
//        tv.setHint("我在这儿");
//        tv.setText(sb);
//        ((FrameLayout) this.findViewById(R.id.frame3d)).addView(tv);
        
        
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

