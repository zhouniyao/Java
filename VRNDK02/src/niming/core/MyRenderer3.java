package niming.core;


import static android.opengl.GLES20.*;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static niming.util.Constant.LINES;
import static niming.util.Constant.POINTS;
import static niming.util.Constant.TRIANGLES;
import static niming.util.Constant.TRIANGLE_FAN;
import static niming.util.Constant.TRIANGLE_STRIP;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.vrndk01.R;
import com.geometric.Number3d;
import com.geometric.Uv;

import niming.parser.ObjParser;
import niming.parser.ParseObjectData;
import niming.util.*;


import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.nfc.cardemulation.CardEmulation;
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
 * 与MyRenderer2使用不同的着色程序。【无纹理着色器】
 * 按照顶点数组绘制，即DrawArrays()
 * 着色器程序为：simple_fragment_shader.glsl【无纹理着色器】
 * u_Color着色器变量不可用
 * 设置触控图形
 * 设置缩放比例
 * 设置平移
 * 设置旋转 
 */
public class MyRenderer3 implements GLSurfaceView.Renderer {
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
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float scaleZ = 1f;
    /* 设置平移*/
    private float translateX = 0f;
    private float translateY = 0f;
    private float translateZ = 0f;
    /* 设置旋转 */
    private float rotateX = 0f;
    private float rotateY = 0f;//若要物体旋转，旋转矩阵必须有个参数不为0
    private float rotateZ = 0f;
    private float speed = 0f; //旋转速度
    
    private boolean isRotate = false;
    private boolean isTouch = false;
	float rotate = 0;//物体旋转的位置，如果有速度，每次都增加速度
    /*保存触屏位置*/
    float oldX = 0f;
    float oldY = 0f;
//  int texture1;
    int texture2;
    /**构造器中的3个初始化量*/
    private ObjParser obj;
    private ParseObjectData car;
    final Handler hd;
    int TYPE = Constant.TRIANGLES; //以什么形式绘制图形，默认是三角面绘制
    
    /**
     * 设置图形旋转
     */
    public void setRotate(float speed, float x, float y, float z) {
    	this.isRotate = true;
		this.rotateX  = x;
		this.rotateY  = y;
		this.rotateZ  = z;
		this.speed = speed;
	}
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
    		Toast.makeText(context, "输入的比例数据有误，请重新输入",Toast.LENGTH_SHORT).show();
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
			type = TRIANGLE_STRIP;
			break;
		case 3:
			break;
		case 4:
			TYPE = TRIANGLES;
			break;
		case 5:
			TYPE = TRIANGLE_FAN;
			break;
		default:
			Toast.makeText(context, "输出类型错误，请选择以下类型：Constant.POINTS、Constant.LINES、FACES",Toast.LENGTH_SHORT).show();
			return;
		}
	}
	/**
	 * 渲染构造器
	 * @param 当前活动的activity
	 * @param OBJ文件objResource
	 * @param 纹理resourceID
	 */
    public MyRenderer3(MyActivity activity, String objResource) {
    	this.context = activity;
		this.hd = activity.hd;
		initObjData(objResource);//导入OBJ文件
		

	}
	/**
	 * ObjData解析-->floatBuffer
	 */
	public void initObjData(String objResource) {
		/*OBJ文件导入*/
		obj = new ObjParser(context, objResource, true);
		obj.parse();//解析OBJ文件
		car = obj.getObjData();

		//===================================顶点数据===============================================================
		Number3d[] vertices = new Number3d[obj.verticesNum];
		car.vertices.toArray(vertices);//ArrayList<Num3d>--> Num3d[]
		//===================================纹理数据===============================================================
		Uv[] textureTmp = new Uv[obj.UVCoordsNum];
		car.texCoords.toArray(textureTmp);//ArrayList<Uv> --> Uv[]
		//===================================法向量数据================================================================
		Number3d[] normalTmp = new Number3d[obj.normalsNum];
		car.normals.toArray(normalTmp);
		
		//====================================face数据==================================================================

		//顶点法向量坐标和法向量序列都不为空执行，否则跳过
		float[] realVertiseCoords = {0};
		Integer[] faceVerticesSeq = new Integer[obj.facesNum * 3]; //face 顶点序列
		car.faces.vSeq.toArray(faceVerticesSeq);
		//face 法向量序列的每个元素是一个纹理坐标
		realVertiseCoords = new float[faceVerticesSeq.length * 3];//顶点序列转换成顶点坐标
		int k = 0;
		//通过序列，找到对应坐标
		for (Integer index : faceVerticesSeq ) { //遍历序列
			float[] tmp = Utils.Number3dTofloat(vertices[index]);
			realVertiseCoords[k++] = tmp[0];
			realVertiseCoords[k++] = tmp[1];
			realVertiseCoords[k++] = tmp[2];
		}
		
		float[] realTextureCoords = {0};
		//纹理坐标与纹理序列不为空
		if(car.texCoords.size() > 0 && car.faces.uvSeq.size() > 0){
			Integer[] faceTextureSeq = new Integer[obj.facesNum * 3]; //得到纹理序列Integer[]数组
			car.faces.uvSeq.toArray(faceTextureSeq);
			//纹理序列中的每个元素是一个纹理坐标uv的集合
			realTextureCoords = new float[faceTextureSeq.length * 2];//这个才是真正的传入着色器内的纹理坐标，与顶点序列的顶点坐标一一对应。
			int j = 0;
			//通过纹理序列，找到对应纹理坐标
			for (Integer index : faceTextureSeq) { //遍历纹理序列
				realTextureCoords[j++] = textureTmp[index].u;
				realTextureCoords[j++] = 1-textureTmp[index].v;
			}
		}// End If
		
		//顶点法向量坐标和法向量序列都不为空执行，否则跳过
		float[] realNomalsCoords = {0};
		if(car.normals.size() > 0 && car.faces.nSeq.size() > 0){
			Integer[] faceNormalSeq = new Integer[obj.facesNum * 3]; 
			car.faces.nSeq.toArray(faceNormalSeq);
			//face 法向量序列的每个元素是一个纹理坐标
			realNomalsCoords = new float[faceNormalSeq.length * 3];//这个才是真正的传入着色器内的法向量坐标，与顶点序列的顶点坐标一一对应。
			k = 0;
			//通过序列，找到对应坐标
			for (int index55 : faceNormalSeq) { //遍历序列
				float[] tmp = Utils.Number3dTofloat(normalTmp[index55]);
				realNomalsCoords[k++] = tmp[0];
				realNomalsCoords[k++] = tmp[1];
				realNomalsCoords[k++] = tmp[2];
			}
		}//End If
		
        // Initialize the buffers.
        objPositions = ByteBuffer.allocateDirect(realVertiseCoords.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objPositions.put(realVertiseCoords).position(0);

        objNormalsss = ByteBuffer.allocateDirect(realNomalsCoords.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objNormalsss.put(realNomalsCoords).position(0);

        objTextureCoodinates = ByteBuffer.allocateDirect(realTextureCoords.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objTextureCoodinates.put(realTextureCoords).position(0);
	}
    
	/**
	 * 是否触摸到mallet's bounding sphere
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchPress(float normalizedX, float normalizedY){
		oldX = normalizedX;
		oldY = normalizedY;
//		isTouch = true;
	}
	/**
	 * 停止触摸，停止旋转
	 */
	public void handleTouchUp(float normalizedX, float normalizedY) {
//		isTouch = false;
	}
	/**
	 * 划屏时, 图形以中心为基点，旋转到需要展示的面。
	 * 触碰结束，图形静止。
	 */
	public void handleTouchDrag(float normalizedX, float normalizedY){
		float x = normalizedX - oldX;
		float y = normalizedY - oldY;
//		isTouch = true; 
		isRotate = true; 
		if(x>0){
			speed = 0.2f;
		}else {
			speed = -0.2f;
		}
		rotateY = 1.0f;
//		if(x>0 && x/Math.abs(y)>10){// Rotate Y
//			rotateY = 1.0f;
//			rotate = 5f;
//		}
//		
//		//rotate Y
//		if(x/y > 10.0f){
//			rotateY = 1f;
//			rotateX = rotateZ = 0f;
//		}else if(x/y < 0.1f){ //rotate X
//			rotateX = 1f;
//			rotateY = rotateZ = 0f;
//		}else if(x/y > 0.9f && x/y < 1.1f){ //接近1，rotate Z
//			rotateZ = 1f;
//			rotateX = rotateY = 0f;
//		}else{
//			rotateX = rotateY = 1f;
//		}
		
		oldX = normalizedX;
		oldY = normalizedY;
		
//		Log.i("今天你动了吗？", "normalize:"+"("+normalizedX+","+normalizedY+")");
	}

	/**
	 * 生成着色器程序，应用程序与着色器变量进行【管线装配】
	 */
	public void initShader(int vertex_shaderID, int fragment_shaderID) {
		/**
		 * 【第一步】将资源文件（着色器源代码）读取至 String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, vertex_shaderID);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, fragment_shaderID);
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
		
		initShader(R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);//使用纹理着色器
		
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
	Long time;
	Long startTime = SystemClock.uptimeMillis();
	
	float angleInDegrees;
	float binocularDistance = 0.066f;//双目距离

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//清屏所有颜色，用之前的glClearColor填充颜色
        // Do a complete rotation every 10 seconds.
        time = SystemClock.uptimeMillis() % 10000L;
        angleInDegrees = (360.0f / 10000.0f) * ((long) time);
        // 因为屏幕分成左右两半
        glViewport(0, 0, screenWidth/2, screenHeight);
    	// 透视投影
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        // 视点位置设置
        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		setIdentityM(modelMatrix, 0);// 归一化
//		translateM(modelMatrix, 0, 0f, 0f, -2f);// 沿z轴平移-3
//		scaleM(modelMatrix, 0, 1f, 1f, 1f);
//		rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);//绕x轴旋转angleInDegrees
		translateM(modelMatrix, 0, translateX, translateY, translateZ - 2f);//沿z轴平移-3
		scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);
		
		if(isRotate){  //旋转速度不为0时
			rotateM(modelMatrix, 0, rotate, rotateX, rotateY, rotateZ);// 旋转矩阵必须有个参数不为0
			rotate += speed;
//			MatrixStack.pushMatrix(modelMatrix);
		}else{
//			MatrixStack.popMatrix(modelMatrix);
		}
		
			
		if (isTouch) {
			rotateM(modelMatrix, 0, rotate, rotateX, rotateY, rotateZ);// 
		}
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
//    	// 给图像添加纹理
//    	glActiveTexture(GL_TEXTURE0);
//    	glBindTexture(GL_TEXTURE_2D, texture2);
//    	glUniform1i(uTextureUnitLoaction, 0);
        
    	drawObject();
 /**************************************第二个物体【右边】************************************************/      
        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
    	//透视投影
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        //重设变换矩阵
        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	//给图像添加纹理
//    	glActiveTexture(GL_TEXTURE0);
//    	glBindTexture(GL_TEXTURE_2D, texture2);
//    	glUniform1i(uTextureUnitLoaction, 0);
    	
//    	GLES20.glDrawElements(GL_Constant.TRIANGLES, obj.facesNum * 3, GL_UNSIGNED_Integer, objVerticesSeq);//三角面
    	drawObject();
        
//    	speed += -0.001;// 速度的阻尼系数
    	
        //打印双目距离-->textView
//        Integer endTime = SystemClock.uptimeMillis();
//        if((endTime - startTime) > 1000){//每秒输出一次
//        	DecimalFormat dFormat = new DecimalFormat("##0.000");
//        	String msg = dFormat.format(binocularDistance);
//			Bundle bd = new Bundle();//创建Bundle对象
//			bd.putString("msg", msg);//向Bundle中添加信息，putString(String key, String value)键值对形式
//			Message tempMessage = new Message();//创建Message对象
//			tempMessage.setData(bd);//向Message中添加数据
//			tempMessage.what = 0;
//			hd.sendMessage(tempMessage);//【重】调用主控制类中的Handler对象发送消息
//        	startTime = endTime;
//        	binocularDistance += 0.001f;
//        	if(binocularDistance > 2)
//        		binocularDistance = 0.0f;
//        }
	}//End OnDrawFrame

    /**
     * Draws a Object.默认是三角面的绘制
     */
    private void drawObject()
    {
    	switch (TYPE) {
		case Constant.POINTS:
			GLES20.glDrawArrays(Constant.POINTS, 0, obj.facesNum * 3);
			break;
		case Constant.LINES:
			GLES20.glDrawArrays(Constant.LINES, 0, obj.facesNum * 3);
			break;
		case Constant.TRIANGLE_STRIP:
			GLES20.glDrawArrays(GL_TRIANGLE_STRIP, 0, obj.facesNum * 3);
			break;
		case Constant.TRIANGLES:
			GLES20.glDrawArrays(GL_TRIANGLES, 0, obj.facesNum * 3);
			break;
		case Constant.TRIANGLE_FAN:
			GLES20.glDrawArrays(GL_TRIANGLE_STRIP, 0, obj.facesNum * 3);
			break;
		}
    }


}
