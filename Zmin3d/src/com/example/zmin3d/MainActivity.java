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
	//�̳�RendererActivity���������������Ҫ���ɵģ�������RendererActivity.java����̳С�  
	    Object3dContainer faceObject3D;     //3D����  
	    float x, y, z;  
	    String str = "";  
	    protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.activity_main);  
		    FrameLayout ll = (FrameLayout) this.findViewById(R.id.frame_3d);//��xml�У�ָ����ʾ�Ŀؼ���������һ��FrameLayout��  
		  
		    ll.addView(_glSurfaceView);//���뵽_glSurfaceView����ʾ��  
		    Toast.makeText(MainActivity.this, "ִ������", Toast.LENGTH_LONG).show();  
	    }  
	    public void initScene() {  
	       scene.backgroundColor().setAll(0x0);//���ó�����ɫ        
	       scene.lights().add(new Light()); //���õƹ⣬����һƬ�ڣ�������  
	       faceObject3D = new Box(1,1,1);//�½�һ�������壬��С�ǳ���߸�һ����λ  
	       faceObject3D.position().x =0;//���ó�ʼλ�ã�ƽ��ͷ����xyz������λ��  
           faceObject3D.position().y =0;  
           faceObject3D.position().z =0;                                           
           scene.addChild(faceObject3D);//��ģ�ͼ��뵽����������ģ�Ͳ�����ʾ����������faceObject3D�м�����ģ�ͣ�ͬ����ʾ��  
	       //������������Ļ������ܾ�д���˶��ģ����Ҫ��� 3D������պ���SkyBox��������Բ��������ʼ���Ĳ��֣����������������ʵ�֡�  
	   } 
	    public void updateScene() {      
	        faceObject3D.rotation().x++;//Χ����X����ת������Ϊ˳ʱ�룬�ٶ���X����ֵ�仯����    
//	        faceObject3D.rotation().y++;//���Ե���Χ����Y��(��XZ����ת��������⣬�����ⲿ������ͬ����̬Ҳ���ԡ�    
//	        faceObject3D.rotation().z++;//��ת�ڷ������ϵĲ�������Ӧ����pitch,roll,yaw   
	    }  
}