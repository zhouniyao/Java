package niming.virsualreality02;


import static android.opengl.GLES20.*;

import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.geometric.Number3d;
import com.geometric.Uv;

import niming.core.MyActivity;
import niming.parser.ObjParser;
import niming.parser.ParseObjectData;
import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import niming.util.TextureHelper;
import niming.util.Utils;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * ������˫Ŀ [dragon]
 *
 */
public class womenOBJ extends MyActivity{
	private GLSurfaceView mGLSurfaceView;
	private LinearLayout ll;
	TextView textView ;
	TextView textView2 ;
	Handler hd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		mGLSurfaceView = new GLSurfaceView(this);
		  // Request an OpenGL ES 2.0 compatible context.
        mGLSurfaceView.setEGLContextClientVersion(2);            
        // Assign our renderer.
        mGLSurfaceView.setRenderer(new ScreenRenderer(womenOBJ.this));	//��cube
        ll = (LinearLayout) findViewById(R.id.glPlane);
	    ll.addView(mGLSurfaceView);   
        mGLSurfaceView.requestFocus();//��ȡ����
//        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���  
        mGLSurfaceView.setFocusableInTouchMode(false); 
        
        textView = (TextView) findViewById(R.id.copyLeft);
        textView2 = (TextView) findViewById(R.id.copyRight);
        /*
         * ������֪Android����ص�view�Ϳؼ������������̰߳�ȫ�ģ�����android�Ż��ֹ�ڷ�UI�̸߳���UI��������ʽ�ķǷ�������
         * ����˵ֱ����Activity�ﴴ�����̣߳�Ȼ��ֱ�������߳��в���UI�ȣ�Android��ֱ���쳣�˳�������ʾshould run on UIThread֮��Ĵ�����־��Ϣ��
         * Only the original thread that created a view hierarchy can touch its views����һ�����ӣ�������˼��ֻ�д�����ͼ��νṹ��ԭʼ�̲߳��ܲ�������View���������̰߳�ȫ��صġ�
         */
        /**ʵ���̼߳�ͨ��*/
        /*
         * Android����Handlerʵ���̼߳��ͨ�����UI����
         */
        hd = new Handler(){
    		//�ڲ���
    		/*
    		 * �������ʵ�����������ȥ������Ϣ
    		 */
    		public void handleMessage(Message msg) {//��дhandlerMessage����
    			switch (msg.what) {
    			case 0:
    				Bundle b = msg.getData();//���Bundle����
    				String str = b.getString("msg");//ͨ�����������Ϣ
    				textView.setText(str);
    				textView2.setText(str);
    				break;

    			default:
    				break;
    			}
    		}
    	};
	}
	/**
	 * ���ò˵�������
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//Renderer�ڲ���
	private class ScreenRenderer implements GLSurfaceView.Renderer{
		private static final String TAG = "niming";
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
	    /*����������*/
	    private static final int BYTES_PER_FLOAT = 4;
		private static final int POSITION_COMPONENT_COUNT = 2;
		/*private static final int POSITION_COMPONENT_COUNT = 4;*/
		private static final int COLOR_COMPONENT_COUNT = 3;
	    private static final String A_POSITION = "a_Position";
	    private static final String A_COLOR = "a_Color";
	    private static final String A_TEXCOORD = "a_TexCoordinate";
	    private static final String U_MVPMATRIX = "u_MVPMatrix";
	    private static final String U_COLOR = "u_Color";
	    
	    /*����װ��ʱ������ɫ��������OpenGL�ڵ�Location*/
	    private int aPositionLocation;
	    private int aColorLocation;
	    private int aTextureCoordLocation;
	    private int uTextureUnitLoaction;
	    private int uMVPMatrixLocation;
	    private int uColorLocation;
	    
	    /*������Ļ�Ŀ��pxֵ*/
	    private int screenWidth;
	    private int screenHeight;
	    float aspectRatio;//��Ļ��߱ȣ���/�� > 1
	    /*����ID*/
	    int texture1;
	    int texture2;
	    
	    final ObjParser obj;
		
		public ScreenRenderer(Context context) {//�������ͨ��,
			this.context = context;
			
			/*OBJ�ļ�����*/
			obj = new ObjParser(context, "niming.virsualreality02:raw/women_obj", true);
			obj.parse();//����OBJ�ļ�
			ParseObjectData car = obj.getObjData();
			
			//ArrayList-->float[]
			//===================================��������=========================================================================
			Number3d[] vertices = new Number3d[obj.verticesNum];
			car.vertices.toArray(vertices);
			float[] positions = new float[obj.verticesNum * 3];
			for (int i = 0, j = 0; i < obj.verticesNum; i++) {
				float[] tmp = Utils.Number3dTofloat(vertices[i]);
				positions[j++] = tmp[0];
				positions[j++] = tmp[1];
				positions[j++] = tmp[2];
			}
			
			/*���Զ���λ��*/
//			for (int i = 0; i < positions.length; ) {
//				Log.i("�����������У�", positions[i++]+" "+positions[i++]+" "+positions[i++]);//ÿ�����һ������xyz
//			}
			
			//===================================��������=========================================================================
			Uv[] textureTmp = new Uv[obj.UVCoordsNum];
			car.texCoords.toArray(textureTmp);
			float[] textureCoords = new float[obj.UVCoordsNum * 2];
			for (int i = 0, j = 0; i < obj.UVCoordsNum; i++) {
				float[] tmp = Utils.UvTofloat(textureTmp[i]);
				textureCoords[j++] = tmp[0];
				textureCoords[j++] = tmp[1];
			}
//			
//			/*������������*/
//			for (int i = 0; i < textureCoords.length; ) {
//				Log.i("�����������У�", textureCoords[i++]+" "+textureCoords[i++]);//ÿ�����һ������UV
//			}
			
			//===================================����������=========================================================================
			Number3d[] normalTmp = new Number3d[obj.normalsNum];
			car.normals.toArray(normalTmp);
			float[] normals = new float[obj.normalsNum * 3];
			for (int i = 0, j = 0; i < obj.normalsNum; i++) {
				float[] tmp = Utils.Number3dTofloat(normalTmp[i]);
				normals[j++] = tmp[0];
				normals[j++] = tmp[1];
				normals[j++] = tmp[2];
			}
			//===================================face����=========================================================================
			short[] faceVertices = new short[obj.numFaces * 3];
			for (int i = 0; i < obj.numFaces * 3; i++) {
				faceVertices[i] = car.faces.vSeq.get(i);
			}
		
//			Log.i("face�Ķ��������С", car.faces.vSeq.size()+"");
			/*����face�Ķ�������*/
//			for (int i = 0; i < faceVertices.length; ) {
//				Log.i("face�Ķ�������", faceVertices[i++] + " " + faceVertices[i++] + " " + faceVertices[i++]);
//			}
			
	        // Initialize the buffers.
	        objPositions = ByteBuffer.allocateDirect(positions.length * BYTES_PER_FLOAT)
	        .order(ByteOrder.nativeOrder()).asFloatBuffer();
	        objPositions.put(positions).position(0);

	        objNormalsss = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT)
	        .order(ByteOrder.nativeOrder()).asFloatBuffer();
	        objNormalsss.put(normals).position(0);

	        objTextureCoodinates = ByteBuffer.allocateDirect(textureCoords.length * BYTES_PER_FLOAT)
	        .order(ByteOrder.nativeOrder()).asFloatBuffer();
	        objTextureCoodinates.put(textureCoords).position(0);
	        //�������У�ShortBuffer
	        objVerticesSeq = ByteBuffer.allocateDirect(faceVertices.length * 2)
	        				.order(ByteOrder.nativeOrder()).asShortBuffer();
	        objVerticesSeq.put(faceVertices).position(0);
	        
	        
	
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
					.readTextFileFromResource(context, R.raw.simple_vertex_shader);
			String fragmentShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_fragment_shader);
//			String vertexShaderSource   = TextResourceReader
//					.readTextFileFromResource(context, R.raw.texture_vertex_shader);
//			String fragmentShaderSource = TextResourceReader
//					.readTextFileFromResource(context, R.raw.texture_fragment_shader);
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
			/*uniform����λ�ö�λ*/
//			uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
			uMVPMatrixLocation = glGetUniformLocation(program, "u_Matrix");//Ϊwomen����
			uColorLocation = glGetUniformLocation(program, "u_Color");//Ϊwomen����
			uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
			
			glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, objPositions);
			glEnableVertexAttribArray(aPositionLocation);
			glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, objNormalsss);
			glEnableVertexAttribArray(aColorLocation);
			glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, objTextureCoodinates);
			glEnableVertexAttribArray(aTextureCoordLocation);
			
			
			/*��������ͼƬ*/
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			glViewport(0, 0, width, height);
//			Log.i(TAG, "$��3��"+ width + "    " + "$��3��" + height);
			screenWidth = width;
			screenHeight= height;
			/**screenWidth/2 < screenHeight */ 
			//��Ļ��-���(��������),��aspecRatio > 1
			aspectRatio = (width/2) > height ?
					(float)(width/2)/(float)height : (float)height/(float)(width/2);
			        /*ƽ�ơ���������ת����*/
			setIdentityM(modelMatrix, 0);//��һ��
			translateM(modelMatrix, 0, 0f, -13f, -10f);//��z��ƽ��-3����y�������ƶ�-9f�����women��
			scaleM(modelMatrix, 0, 0.12f, 0.12f, 0.12f);//women̫����
			rotateM(modelMatrix, 0, 30, 0f, 1f, 0f);
		
		}
		/*ʱ�����ת�Ƕ�*/
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
	        //��Ϊ��Ļ�ֳ���������
	        glViewport(0, 0, screenWidth/2, screenHeight);
	    	//͸��ͶӰ
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
	        //�ӵ�λ��
	        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//	        /*ƽ�ơ���������ת����*/
//			setIdentityM(modelMatrix, 0);//��һ��
//			translateM(modelMatrix, 0, 0f, -13f, -10f);//��z��ƽ��-3����y�������ƶ�-9f�����women��
//			scaleM(modelMatrix, 0, 0.12f, 0.12f, 0.12f);//women̫����
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//��x����תangleInDegrees
//			rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);//��y����תangleInDegrees
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 1f, 0f);//��x,y����תangleInDegrees
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	    	//��ͼ�������ɫ���̶��ġ�
	        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
	        drawCube();
	 /**************************************�ڶ������塾�ұߡ�************************************************/      
	        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
	    	//͸��ͶӰ
//			MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
//			Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
	        //����任����
	        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			setIdentityM(modelMatrix, 0);//��һ��
//			translateM(modelMatrix, 0, 0f, 0f, -3f);//��z��ƽ��-3
//			scaleM(modelMatrix, 0, 1f, 1f, 1f);
//			rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);//��x����ת-60��
			
			Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
	        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
	        //��ͼ�������ɫ���̶��ġ�
	        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
	        drawCube();
	        
	        //��ӡ˫Ŀ����
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
		}
		/**
	     * Draws a cube.
	     */
	    private void drawCube()
	    {
//	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
//	    	GLES20.glDrawArrays(GLES20.GL_POINTS, 0, obj.verticesNum);
	    	/*�������л���*/
//	    	GLES20.glDrawElements(GL_POINTS, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//��
	    	GLES20.glDrawElements(GL_LINES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq); //������
//	    	GLES20.glDrawElements(GL_TRIANGLES, obj.numFaces * 3, GL_UNSIGNED_SHORT, objVerticesSeq);//������
	    }
		
	}
}



