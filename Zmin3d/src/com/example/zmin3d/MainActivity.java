package com.example.zmin3d;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
import min3d.vos.Light;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends RendererActivity {  
	//继承RendererActivity，如果还有其他需要集成的，可以在RendererActivity.java里面继承。  
	    Object3dContainer faceObject3D;     //3D对象  
	    float x, y, z;  
	    String str = "";  
	    protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.activity_main);  
		    FrameLayout ll = (FrameLayout) this.findViewById(R.id.frame_3d);//在xml中，指定显示的控件，这里是一个FrameLayout。  
		  
		    ll.addView(_glSurfaceView);//加入到_glSurfaceView中显示。  
		    Toast.makeText(MainActivity.this, "执行正常", Toast.LENGTH_LONG).show();  
	    }  
	    public void initScene() {  
	       scene.backgroundColor().setAll(0x0);//设置场景底色        
	       scene.lights().add(new Light()); //设置灯光，否则一片黑，看不见  
	       faceObject3D = new Box(1,1,1);//新建一个立方体，大小是长宽高各一个单位  
	       faceObject3D.position().x =0;//设置初始位置，平截头体中xyz的坐标位置  
           faceObject3D.position().y =0;  
           faceObject3D.position().z =0;                                           
           scene.addChild(faceObject3D);//把模型加入到场景，这样模型才能显示，还可以在faceObject3D中加入新模型，同步显示。  
	       //这样这个函数的基本功能就写完了多个模型需要多个 3D对象，天空盒是SkyBox，还有椭圆，纹理，初始化的部分，都在这个方法里面实现。  
	   } 
	    public void updateScene() {      
	        faceObject3D.rotation().x++;//围绕着X轴旋转，正数为顺时针，速度由X的数值变化决定    
//	        faceObject3D.rotation().y++;//可以单独围绕着Y轴(或XZ）旋转，这个任意，根据外部传感器同步姿态也可以。    
//	        faceObject3D.rotation().z++;//旋转在飞行器上的参数，对应的是pitch,roll,yaw   
	    }  
}