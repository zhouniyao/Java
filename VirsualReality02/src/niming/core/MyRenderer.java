package niming.core;


import static android.opengl.GLES20.*;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.geometric.Number3d;
import com.geometric.Uv;

import niming.parser.ObjParser;
import niming.parser.ParseObjectData;
import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import niming.util.TextureHelper;
import niming.util.Utils;
import niming.virsualreality02.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
/**
 * 着色器程序为：texture_vertex_shader.glsl
 * u_Color着色器变量不可用
 * 设置触控图形
 * 设置缩放比例
 * 设置平移
 * 设置旋转 
 */
public class MyRenderer implements GLSurfaceView.Renderer {
	private final Context context;
	/*定义各类变换矩阵*/
	private float[] modelMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] projectionMatrix = new float[16];
    private float[] MVMatrix = new float[16];//矩阵连乘的中间结果
    private float[] MVPMatrix = new float[16];//最终的矩阵
    /*顶点数据准备*/
    /* Store our model data in a float buffer. */
    private FloatBuffer objPositions;
    private FloatBuffer objNormalsss;
    private FloatBuffer objTextureCoodinates;
    private ShortBuffer objVerticesSeq;
    /*openGL绑定的着色器程序ID*/
    private int program;
    /*定义常量区*/
    private static final int BYTES_PER_FLOAT = 4;
	private static final int POSITION_COMPONENT_COUNT = 2;
	/*private static final int POSITION_COMPONENT_COUNT = 4;*/
	private static final int COLOR_COMPONENT_COUNT = 3;
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String A_TEXCOORD = "a_TexCoordinate";
    private static final String U_MVPMATRIX = "u_MVPMatrix";
    
    public final int POINTS = 0;
    public final int LINES  = 1;
    public final int FACES  = 2;
   
    
    /*管线装配时，各着色器变量在OpenGL内的Location*/
    private int aPositionLocation;
    private int aColorLocation;
    private int aTextureCoordLocation;
    private int uTextureUnitLoaction;
    private int uMVPMatrixLocation;
    
    /*整个屏幕的宽高px值*/
    private int screenWidth;
    private int screenHeight;
    float aspectRatio;//屏幕宽高比，宽/高 > 1
    /* 设置触控图形*/
    float touchX;
    float touchY;
    /* 设置缩放比例*/
    float scaleX = 1f;
    float scaleY = 1f;
    float scaleZ = 1f;
    /* 设置平移*/
    float translateX = 0f;
    float translateY = 0f;
    float translateZ = 0f;
    /* 设置旋转 */
    float rotateX = 0f;
    float rotateY = 1f;//旋转矩阵必须有个参数不为0
    float rotateZ = 0f;
    float speed = 0f; //旋转速度
    /*保存触屏位置*/
    float oldX = 0f;
    float oldY = 0f;
    /*纹理ID*/
    final int texResourceId;
//  int texture1;
    int texture2;
    /**构造器中的3个初始化量*/
    final ObjParser obj;
    final Handler hd;
    int TYPE = FACES; //以什么形式绘制图形，默认是三角面绘制
    
//    public void setTexture(int resourceID) {
//		/*加载纹理图片*/
//		texture1 = TextureHelper.loadTexture(context, R.drawable.wl, true);//四只小蜜蜂
//		texture2 = TextureHelper.loadTexture(context, resourceID);
//	}
    /**
     * 设置图形整体平移
     * @param x y z
     */
    public void setTranslate(float x, float y, float z) {
        translateX += x;
        translateY += y;
        translateZ += z;
	}
    /**
     * 设置图形的缩放比例
     * @param x、y、z的缩放比例
     */
    public void setScale(float x, float y, float z) {
    	if(x <= 0 || y <= 0 || z <= 0){
    		Toast.makeText(context, "输入的比例数据有误，请重新输入",Toast.LENGTH_LONG).show();
    		return;
    	}
        scaleX = x;
        scaleY = y;
        scaleZ = z;
	}
    
    /**
     * 设置图形输出类型
     * @param type
     */
    public void setTYPE(int type) {
    	switch (type) {
		case 0:
			TYPE = POINTS;
			break;
		case 1:
			TYPE = LINES;
			break;
		case 2:
			TYPE = FACES;
			break;
		default:
			Toast.makeText(context, "输出类型错误，请选择以下类型：POINTS、LINES、FACES",Toast.LENGTH_LONG).show();
			return;
		}
	}
	/**
	 * 渲染构造器
	 * @param 当前活动的activity
	 * @param OBJ文件objResource
	 * @param 纹理resourceID
	 */
    public MyRenderer(MyActivity activity, String objResource, int resourceID) {
    	this.context = activity;
		this.hd = activity.hd;
		this.texResourceId = resourceID;
		/*OBJ文件导入*/
		obj = new ObjParser(context, objResource, true);
		obj.parse();//解析OBJ文件
		ParseObjectData car = obj.getObjData();

		//===================================顶点数据===============================================================
		Number3d[] vertices = new Number3d[obj.verticesNum];
		car.vertices.toArray(vertices);//ArrayList<Num3d>--> Num3d[]
		float[] positions = new float[obj.verticesNum * 3];
		for (int i = 0, j = 0; i < obj.verticesNum; i++) { //Number3d[] --> float[]
			float[] tmp = Utils.Number3dTofloat(vertices[i]);
			positions[j++] = tmp[0];
			positions[j++] = tmp[1];
			positions[j++] = tmp[2];
		}
		//===================================纹理数据===============================================================
		Uv[] textureTmp = new Uv[obj.UVCoordsNum];
		car.texCoords.toArray(textureTmp);//ArrayList<Uv> --> Uv[]
		//===================================法向量数据================================================================
		Number3d[] normalTmp = new Number3d[obj.normalsNum];
		car.normals.toArray(normalTmp);
		
		//====================================face数据==================================================================
		//face 顶点序列
		short[] faceVerticesSeq = new short[obj.numFaces * 3]; //face 顶点序列
		for (int i = 0; i < obj.numFaces * 3; i++) {
			faceVerticesSeq[i] = car.faces.vSeq.get(i);
		}
		
		float[] realTextureCoords = {0};
		//纹理坐标与纹理序列不为空
		if(car.texCoords.size() > 0 && car.faces.uvSeq.size() > 0){
			Short[] faceTextureSeq = new Short[obj.numFaces * 3]; //得到纹理序列Short[]数组
			car.faces.uvSeq.toArray(faceTextureSeq);
			//纹理序列中的每个元素是一个纹理坐标uv的集合
			realTextureCoords = new float[faceTextureSeq.length * 2];//这个才是真正的传入着色器内的纹理坐标，与顶点序列的顶点坐标一一对应。
			int j = 0;
			//通过纹理序列，找到对应纹理坐标
			for (int index : faceTextureSeq) { //遍历纹理序列
				realTextureCoords[j++] = textureTmp[index].u;
				realTextureCoords[j++] = textureTmp[index].v;
			}
		}// End If
		
		//顶点法向量坐标和法向量序列都不为空执行，否则跳过
		float[] realNomalsCoords = {0};
		if(car.normals.size() > 0 && car.faces.nSeq.size() > 0){
			Short[] faceNormalSeq = new Short[obj.numFaces * 3]; 
			car.faces.nSeq.toArray(faceNormalSeq);
			//face 法向量序列的每个元素是一个纹理坐标
			realNomalsCoords = new float[faceNormalSeq.length * 3];//这个才是真正的传入着色器内的法向量坐标，与顶点序列的顶点坐标一一对应。
			int k = 0;
			//通过序列，找到对应坐标
			for (int index55 : faceNormalSeq) { //遍历序列
				float[] tmp = Utils.Number3dTofloat(normalTmp[index55]);
				realNomalsCoords[k++] = tmp[0];
				realNomalsCoords[k++] = tmp[1];
				realNomalsCoords[k++] = tmp[2];
			}
		}//End If
		
        // Initialize the buffers.
        objPositions = ByteBuffer.allocateDirect(positions.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objPositions.put(positions).position(0);

        objNormalsss = ByteBuffer.allocateDirect(realNomalsCoords.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objNormalsss.put(realNomalsCoords).position(0);

        objTextureCoodinates = ByteBuffer.allocateDirect(realTextureCoords.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objTextureCoodinates.put(realTextureCoords).position(0);
        //face顶点序列，ShortBuffer
        objVerticesSeq = ByteBuffer.allocateDirect(faceVerticesSeq.length * 2)
        				.order(ByteOrder.nativeOrder()).asShortBuffer();
        objVerticesSeq.put(faceVerticesSeq).position(0);
        
//        Log.i("顶点缓存容量：", ""+objPositions.capacity());
//        Log.i("Texture容量：", ""+objTextureCoodinates.capacity());
//        Log.i("顶点序列：", ""+objVerticesSeq.capacity());
	}
    
	/**
	 * 是否触摸到mallet's bounding sphere
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchPress(float normalizedX, float normalizedY){
		oldX = normalizedX;
		oldY = normalizedY;
	}
	/**
	 * 拖拽, 物体保存中心不变，旋转起来，而且越来越慢，直到静止。
	 */
	public void handleTouchDrag(float normalizedX, float normalizedY){
		float x = Math.abs(normalizedX - oldX);
		float y = Math.abs(normalizedY - oldY);
		if(speed == 0){
			//rotate Y
			if(x/y > 10){
				rotateY = 1f;
				rotateX = rotateZ = 0f;
				speed = y;
			}else if(x/y < 0.1){ //rotate X
				rotateX = 1f;
				speed = x;
				rotateY = rotateZ = 0f;
			}else if(x/y > 0.9 && x/y < 1.1){ //接近1，rotate Z
				rotateZ = 1f;
				speed = (float) (Math.sqrt(2) * (normalizedX - oldX)); //1.414 * 方向向量
				rotateX = rotateY = 0f;
			}else{
				speed = (float) (Math.sqrt(x*x + y*y));
				rotateX = rotateY = 1f;
			}
		}
		oldX = normalizedX;
		oldY = normalizedY;
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//黑色	//清屏用的颜色
        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.0 Enable texture mapping
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        
		/**
		 * 【第一步】将资源文件（着色器源代码）读取至 String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.texture_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.texture_fragment_shader);
		/**
		 * 【第二步】编译着色器源代码，返回OpenGL生成的ID
		 */
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		/**
		 * 【第三步】一个OpenGL程序就是把一个顶点着色器和一个片段着色器链接在一起变成单个对象，顶点着色器和片段着色器总是一起工作的。 
		 */
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		glUseProgram(program);
		/*attribute变量定位*/
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aColorLocation = glGetAttribLocation(program, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
		/*uniform常量定位*/
		uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
		uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
		
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, objPositions);
		glEnableVertexAttribArray(aPositionLocation);
		//u_Color不使用
//			glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, );
//			glEnableVertexAttribArray(aColorLocation);
		glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, objTextureCoodinates);
		glEnableVertexAttribArray(aTextureCoordLocation);
		
		/*纹理*/
//		texture2 = TextureHelper.loadTexture(context, R.drawable.camaro);//汽车
		//加载纹理
		texture2 = TextureHelper.loadTexture(context, texResourceId);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
//		Log.i(TAG, "$宽3："+ width + "    " + "$高3：" + height);
		screenWidth = width;
		screenHeight= height;
		/**screenWidth/2 < screenHeight */ 
		//屏幕高-宽比(两个半屏),即aspecRatio > 1
		aspectRatio = (width/2) > height ?
				(float)(width/2)/(float)height : (float)height/(float)(width/2);

	}
	
	/**时间和旋转角度*/
	long time;
	long startTime = SystemClock.uptimeMillis();
	
	float angleInDegrees;
	float binocularDistance = 0f;//双目距离
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//清屏所有颜色，用之前的glClearColor填充颜色
        // Do a complete rotation every 10 seconds.
        time = SystemClock.uptimeMillis() % 10000L;
        angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        // 因为屏幕分成左右两半
        glViewport(0, 0, screenWidth/2, screenHeight);
    	// 透视投影
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        // 视点位置
        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		setIdentityM(modelMatrix, 0);// 归一化
//		translateM(modelMatrix, 0, 0f, 0f, -2f);// 沿z轴平移-3
//		scaleM(modelMatrix, 0, 1f, 1f, 1f);
//		rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);//绕x轴旋转angleInDegrees
		translateM(modelMatrix, 0, translateX, translateY, translateZ - 2f);//沿z轴平移-3
		scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);
		rotateM(modelMatrix, 0, speed, rotateX, rotateY, rotateZ);// 旋转矩阵必须有个参数不为0
//		rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);// 绕x轴旋转angleInDegrees
//		rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);// 绕x轴旋转angleInDegrees
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	// 给图像添加纹理
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
        
    	drawObject();
 /**************************************第二个物体【右边】************************************************/      
        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
    	//透视投影
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        //重设变换矩阵
        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			setIdentityM(modelMatrix, 0);//归一化
//			translateM(modelMatrix, 0, 0f, 0f, -3f);//沿z轴平移-3
//			scaleM(modelMatrix, 0, 1f, 1f, 1f);
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//绕x轴旋转-60°
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	//给图像添加纹理
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
    	
//    	GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//三角面
    	drawObject();
        
    	speed += -0.001;// 速度的阻尼系数
    	
        //打印双目距离-->textView
        long endTime = SystemClock.uptimeMillis();
        if((endTime - startTime) > 1000){//每秒输出一次
	        /*更改视点的距离*/
//	        	Log.i("双目距离put:", ""+binocularDistance);
//	        	Log.i("startTime:", ""+startTime);
//	        	Log.i("endTime:", ""+endTime);
        	DecimalFormat dFormat = new DecimalFormat("##0.000");
        	String msg = dFormat.format(binocularDistance);
			Bundle bd = new Bundle();//创建Bundle对象
			bd.putString("msg", msg);//向Bundle中添加信息，putString(String key, String value)键值对形式
			Message tempMessage = new Message();//创建Message对象
			tempMessage.setData(bd);//向Message中添加数据
			tempMessage.what = 0;
			hd.sendMessage(tempMessage);//【重】调用主控制类中的Handler对象发送消息
        	startTime = endTime;
        	binocularDistance += 0.001f;
        	if(binocularDistance > 2)
        		binocularDistance = 0.0f;
        }
	}//End OnDrawFrame
	
	/**
     * Draws a Object.默认是三角面的绘制
     */
//    private void drawObject()
//    {
////	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
////	    	GLES20.glDrawArrays(GLES20.GL_POINTS, 0, obj.verticesNum);
////	    	GLES20.glDrawElements(GL_LINES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq); //网格线
//    	GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//三角面
////	    	GLES20.glDrawElements(GL_POINTS, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//点
//    }

    /**
     * Draws a Object.默认是三角面的绘制
     */
    private void drawObject()
    {
    	switch (TYPE) {
		case POINTS:
			GLES20.glDrawElements(GL_POINTS, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//点的形式绘制
			break;
		case LINES:
			GLES20.glDrawElements(GL_LINES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq); //网格线的形式绘制
			break;
		case FACES:
			GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//三角面
			break;
		default:
			GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//三角面
			break;
		}
    }


}
