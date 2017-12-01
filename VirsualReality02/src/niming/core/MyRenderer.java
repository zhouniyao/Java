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
 * ��ɫ������Ϊ��texture_vertex_shader.glsl
 * u_Color��ɫ������������
 * ���ô���ͼ��
 * �������ű���
 * ����ƽ��
 * ������ת 
 */
public class MyRenderer implements GLSurfaceView.Renderer {
	private final Context context;
	/*�������任����*/
	private float[] modelMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] projectionMatrix = new float[16];
    private float[] MVMatrix = new float[16];//�������˵��м���
    private float[] MVPMatrix = new float[16];//���յľ���
    /*��������׼��*/
    /* Store our model data in a float buffer. */
    private FloatBuffer objPositions;
    private FloatBuffer objNormalsss;
    private FloatBuffer objTextureCoodinates;
    private ShortBuffer objVerticesSeq;
    /*openGL�󶨵���ɫ������ID*/
    private int program;
    /*���峣����*/
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
   
    
    /*����װ��ʱ������ɫ��������OpenGL�ڵ�Location*/
    private int aPositionLocation;
    private int aColorLocation;
    private int aTextureCoordLocation;
    private int uTextureUnitLoaction;
    private int uMVPMatrixLocation;
    
    /*������Ļ�Ŀ��pxֵ*/
    private int screenWidth;
    private int screenHeight;
    float aspectRatio;//��Ļ��߱ȣ���/�� > 1
    /* ���ô���ͼ��*/
    float touchX;
    float touchY;
    /* �������ű���*/
    float scaleX = 1f;
    float scaleY = 1f;
    float scaleZ = 1f;
    /* ����ƽ��*/
    float translateX = 0f;
    float translateY = 0f;
    float translateZ = 0f;
    /* ������ת */
    float rotateX = 0f;
    float rotateY = 1f;//��ת��������и�������Ϊ0
    float rotateZ = 0f;
    float speed = 0f; //��ת�ٶ�
    /*���津��λ��*/
    float oldX = 0f;
    float oldY = 0f;
    /*����ID*/
    final int texResourceId;
//  int texture1;
    int texture2;
    /**�������е�3����ʼ����*/
    final ObjParser obj;
    final Handler hd;
    int TYPE = FACES; //��ʲô��ʽ����ͼ�Σ�Ĭ�������������
    
//    public void setTexture(int resourceID) {
//		/*��������ͼƬ*/
//		texture1 = TextureHelper.loadTexture(context, R.drawable.wl, true);//��ֻС�۷�
//		texture2 = TextureHelper.loadTexture(context, resourceID);
//	}
    /**
     * ����ͼ������ƽ��
     * @param x y z
     */
    public void setTranslate(float x, float y, float z) {
        translateX += x;
        translateY += y;
        translateZ += z;
	}
    /**
     * ����ͼ�ε����ű���
     * @param x��y��z�����ű���
     */
    public void setScale(float x, float y, float z) {
    	if(x <= 0 || y <= 0 || z <= 0){
    		Toast.makeText(context, "����ı���������������������",Toast.LENGTH_LONG).show();
    		return;
    	}
        scaleX = x;
        scaleY = y;
        scaleZ = z;
	}
    
    /**
     * ����ͼ���������
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
			Toast.makeText(context, "������ʹ�����ѡ���������ͣ�POINTS��LINES��FACES",Toast.LENGTH_LONG).show();
			return;
		}
	}
	/**
	 * ��Ⱦ������
	 * @param ��ǰ���activity
	 * @param OBJ�ļ�objResource
	 * @param ����resourceID
	 */
    public MyRenderer(MyActivity activity, String objResource, int resourceID) {
    	this.context = activity;
		this.hd = activity.hd;
		this.texResourceId = resourceID;
		/*OBJ�ļ�����*/
		obj = new ObjParser(context, objResource, true);
		obj.parse();//����OBJ�ļ�
		ParseObjectData car = obj.getObjData();

		//===================================��������===============================================================
		Number3d[] vertices = new Number3d[obj.verticesNum];
		car.vertices.toArray(vertices);//ArrayList<Num3d>--> Num3d[]
		float[] positions = new float[obj.verticesNum * 3];
		for (int i = 0, j = 0; i < obj.verticesNum; i++) { //Number3d[] --> float[]
			float[] tmp = Utils.Number3dTofloat(vertices[i]);
			positions[j++] = tmp[0];
			positions[j++] = tmp[1];
			positions[j++] = tmp[2];
		}
		//===================================��������===============================================================
		Uv[] textureTmp = new Uv[obj.UVCoordsNum];
		car.texCoords.toArray(textureTmp);//ArrayList<Uv> --> Uv[]
		//===================================����������================================================================
		Number3d[] normalTmp = new Number3d[obj.normalsNum];
		car.normals.toArray(normalTmp);
		
		//====================================face����==================================================================
		//face ��������
		short[] faceVerticesSeq = new short[obj.numFaces * 3]; //face ��������
		for (int i = 0; i < obj.numFaces * 3; i++) {
			faceVerticesSeq[i] = car.faces.vSeq.get(i);
		}
		
		float[] realTextureCoords = {0};
		//�����������������в�Ϊ��
		if(car.texCoords.size() > 0 && car.faces.uvSeq.size() > 0){
			Short[] faceTextureSeq = new Short[obj.numFaces * 3]; //�õ���������Short[]����
			car.faces.uvSeq.toArray(faceTextureSeq);
			//���������е�ÿ��Ԫ����һ����������uv�ļ���
			realTextureCoords = new float[faceTextureSeq.length * 2];//������������Ĵ�����ɫ���ڵ��������꣬�붥�����еĶ�������һһ��Ӧ��
			int j = 0;
			//ͨ���������У��ҵ���Ӧ��������
			for (int index : faceTextureSeq) { //������������
				realTextureCoords[j++] = textureTmp[index].u;
				realTextureCoords[j++] = textureTmp[index].v;
			}
		}// End If
		
		//���㷨��������ͷ��������ж���Ϊ��ִ�У���������
		float[] realNomalsCoords = {0};
		if(car.normals.size() > 0 && car.faces.nSeq.size() > 0){
			Short[] faceNormalSeq = new Short[obj.numFaces * 3]; 
			car.faces.nSeq.toArray(faceNormalSeq);
			//face ���������е�ÿ��Ԫ����һ����������
			realNomalsCoords = new float[faceNormalSeq.length * 3];//������������Ĵ�����ɫ���ڵķ��������꣬�붥�����еĶ�������һһ��Ӧ��
			int k = 0;
			//ͨ�����У��ҵ���Ӧ����
			for (int index55 : faceNormalSeq) { //��������
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
        //face�������У�ShortBuffer
        objVerticesSeq = ByteBuffer.allocateDirect(faceVerticesSeq.length * 2)
        				.order(ByteOrder.nativeOrder()).asShortBuffer();
        objVerticesSeq.put(faceVerticesSeq).position(0);
        
//        Log.i("���㻺��������", ""+objPositions.capacity());
//        Log.i("Texture������", ""+objTextureCoodinates.capacity());
//        Log.i("�������У�", ""+objVerticesSeq.capacity());
	}
    
	/**
	 * �Ƿ�����mallet's bounding sphere
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchPress(float normalizedX, float normalizedY){
		oldX = normalizedX;
		oldY = normalizedY;
	}
	/**
	 * ��ק, ���屣�����Ĳ��䣬��ת����������Խ��Խ����ֱ����ֹ��
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
			}else if(x/y > 0.9 && x/y < 1.1){ //�ӽ�1��rotate Z
				rotateZ = 1f;
				speed = (float) (Math.sqrt(2) * (normalizedX - oldX)); //1.414 * ��������
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
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//��ɫ	//�����õ���ɫ
        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.0 Enable texture mapping
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        
		/**
		 * ����һ��������Դ�ļ�����ɫ��Դ���룩��ȡ�� String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, R.raw.texture_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.texture_fragment_shader);
		/**
		 * ���ڶ�����������ɫ��Դ���룬����OpenGL���ɵ�ID
		 */
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		/**
		 * ����������һ��OpenGL������ǰ�һ��������ɫ����һ��Ƭ����ɫ��������һ���ɵ������󣬶�����ɫ����Ƭ����ɫ������һ�����ġ� 
		 */
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		glUseProgram(program);
		/*attribute������λ*/
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aColorLocation = glGetAttribLocation(program, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
		/*uniform������λ*/
		uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
		uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
		
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, objPositions);
		glEnableVertexAttribArray(aPositionLocation);
		//u_Color��ʹ��
//			glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, );
//			glEnableVertexAttribArray(aColorLocation);
		glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, objTextureCoodinates);
		glEnableVertexAttribArray(aTextureCoordLocation);
		
		/*����*/
//		texture2 = TextureHelper.loadTexture(context, R.drawable.camaro);//����
		//��������
		texture2 = TextureHelper.loadTexture(context, texResourceId);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
//		Log.i(TAG, "$��3��"+ width + "    " + "$��3��" + height);
		screenWidth = width;
		screenHeight= height;
		/**screenWidth/2 < screenHeight */ 
		//��Ļ��-���(��������),��aspecRatio > 1
		aspectRatio = (width/2) > height ?
				(float)(width/2)/(float)height : (float)height/(float)(width/2);

	}
	
	/**ʱ�����ת�Ƕ�*/
	long time;
	long startTime = SystemClock.uptimeMillis();
	
	float angleInDegrees;
	float binocularDistance = 0f;//˫Ŀ����
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//����������ɫ����֮ǰ��glClearColor�����ɫ
        // Do a complete rotation every 10 seconds.
        time = SystemClock.uptimeMillis() % 10000L;
        angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        // ��Ϊ��Ļ�ֳ���������
        glViewport(0, 0, screenWidth/2, screenHeight);
    	// ͸��ͶӰ
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        // �ӵ�λ��
        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		setIdentityM(modelMatrix, 0);// ��һ��
//		translateM(modelMatrix, 0, 0f, 0f, -2f);// ��z��ƽ��-3
//		scaleM(modelMatrix, 0, 1f, 1f, 1f);
//		rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);//��x����תangleInDegrees
		translateM(modelMatrix, 0, translateX, translateY, translateZ - 2f);//��z��ƽ��-3
		scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);
		rotateM(modelMatrix, 0, speed, rotateX, rotateY, rotateZ);// ��ת��������и�������Ϊ0
//		rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);// ��x����תangleInDegrees
//		rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);// ��x����תangleInDegrees
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	// ��ͼ���������
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
        
    	drawObject();
 /**************************************�ڶ������塾�ұߡ�************************************************/      
        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
    	//͸��ͶӰ
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        //����任����
        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			setIdentityM(modelMatrix, 0);//��һ��
//			translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
//			scaleM(modelMatrix, 0, 1f, 1f, 1f);
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//��x����ת-60��
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	//��ͼ���������
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
    	
//    	GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//������
    	drawObject();
        
    	speed += -0.001;// �ٶȵ�����ϵ��
    	
        //��ӡ˫Ŀ����-->textView
        long endTime = SystemClock.uptimeMillis();
        if((endTime - startTime) > 1000){//ÿ�����һ��
	        /*�����ӵ�ľ���*/
//	        	Log.i("˫Ŀ����put:", ""+binocularDistance);
//	        	Log.i("startTime:", ""+startTime);
//	        	Log.i("endTime:", ""+endTime);
        	DecimalFormat dFormat = new DecimalFormat("##0.000");
        	String msg = dFormat.format(binocularDistance);
			Bundle bd = new Bundle();//����Bundle����
			bd.putString("msg", msg);//��Bundle�������Ϣ��putString(String key, String value)��ֵ����ʽ
			Message tempMessage = new Message();//����Message����
			tempMessage.setData(bd);//��Message���������
			tempMessage.what = 0;
			hd.sendMessage(tempMessage);//���ء��������������е�Handler��������Ϣ
        	startTime = endTime;
        	binocularDistance += 0.001f;
        	if(binocularDistance > 2)
        		binocularDistance = 0.0f;
        }
	}//End OnDrawFrame
	
	/**
     * Draws a Object.Ĭ����������Ļ���
     */
//    private void drawObject()
//    {
////	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
////	    	GLES20.glDrawArrays(GLES20.GL_POINTS, 0, obj.verticesNum);
////	    	GLES20.glDrawElements(GL_LINES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq); //������
//    	GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//������
////	    	GLES20.glDrawElements(GL_POINTS, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//��
//    }

    /**
     * Draws a Object.Ĭ����������Ļ���
     */
    private void drawObject()
    {
    	switch (TYPE) {
		case POINTS:
			GLES20.glDrawElements(GL_POINTS, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//�����ʽ����
			break;
		case LINES:
			GLES20.glDrawElements(GL_LINES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq); //�����ߵ���ʽ����
			break;
		case FACES:
			GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//������
			break;
		default:
			GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//������
			break;
		}
    }


}
