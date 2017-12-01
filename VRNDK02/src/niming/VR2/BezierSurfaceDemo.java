package niming.VR2;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;
import static niming.util.Constant.LINES;
import static niming.util.Constant.POINTS;
import static niming.util.Constant.TRIANGLES;
import static niming.util.Constant.TRIANGLE_FAN;
import static niming.util.Constant.TRIANGLE_STRIP;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.example.vrndk01.R;
import com.geometric.Ball;
import com.geometric.BezierSurface;
import com.geometric.BezierSurface2;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View.OnCreateContextMenuListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import niming.core.MyActivity;
import niming.util.Constant;
import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import niming.util.TextureHelper;

/**
 * ���ݴ���������+���Լ��ٶ���ת   ���� ��ʧ�ܡ�
 *
 */
public class BezierSurfaceDemo extends MyActivity {
	private float mPreviousY;//�ϴεĴ���λ��Y����
	private float mPreviousX;//�ϴεĴ���λ��X����
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLSurfaceView = new GLSurfaceView(this);
        // Request an OpenGL ES 2.0 compatible context.
       mGLSurfaceView.setEGLContextClientVersion(2);            
        
        // Assign our renderer.
//        glSurfaceView.setRenderer(new AirHockeyRenderer(this));
        mGLSurfaceView.setRenderer(new SceneRenderer(this));
		// �л���������
		setContentView(R.layout.activity_seekbar);	
		LinearLayout ll = (LinearLayout) findViewById(R.id.main_liner);
		ll.addView(mGLSurfaceView);
        //��ͨ�������������Ĵ������
        SeekBar sb=(SeekBar)this.findViewById(R.id.SeekBar01);
        sb.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener()
            {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
//					mGLSurfaceView.setLightOffset((seekBar.getMax()/2.0f-progress)/(seekBar.getMax()/2.0f)*-4);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {	}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) { }            	
            }
        );

	}
}
class SceneRenderer implements GLSurfaceView.Renderer 
{
	
	private final Context context;
	Ball ball;
	BezierSurface2 bezier;
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
	private static final String obj = null;
    
 
    
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
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float scaleZ = 1f;
    /* ����ƽ��*/
    private float translateX = 0f;
    private float translateY = 0f;
    private float translateZ = 0f;
    /* ������ת */
    private float rotateX = 0f;
    private float rotateY = 0f;//��Ҫ������ת����ת��������и�������Ϊ0
    private float rotateZ = 0f;
    private float speed = 0f; //��ת�ٶ�
    
	public float yAngle = 0;// ��y����ת�ĽǶ�
	public float xAngle = 0;// ��x����ת�ĽǶ�
	public float zAngle = 0;// ��z����ת�ĽǶ�
    
    public boolean isRotate = false;//��ת����
    public boolean isTouch = false;//���津������ת
	float rotate = 0;//������ת��λ�ã�������ٶȣ�ÿ�ζ������ٶ�
    /*���津��λ��*/
    float oldX = 0f;
    float oldY = 0f;
    /*����ID*/
    private int texResourceId;
//  int texture1;
    int texture2;
    /**�������е�3����ʼ����*/
    int TYPE = Constant.TRIANGLES; //��ʲô��ʽ����ͼ�Σ�Ĭ�������������
    
    /**
     * ����ͼ����ת
     */
    public void setRotate(float speed, float x, float y, float z) {
    	this.isRotate = true;
		this.rotateX  = x;
		this.rotateY  = y;
		this.rotateZ  = z;
		this.speed = speed;
	}
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
    		Toast.makeText(context, "����ı���������������������",Toast.LENGTH_SHORT).show();
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
			Toast.makeText(context, "������ʹ�����ѡ���������ͣ�Constant.POINTS��Constant.LINES��FACES",Toast.LENGTH_SHORT).show();
			return;
		}
	}
	/**
	 * ��Ⱦ������
	 * @param ��ǰ���activity
	 * @param OBJ�ļ�objResource
	 * @param ����resourceID
	 */
    public SceneRenderer(MyActivity activity) {
    	this.context = activity;
	}
	/**
	 * �Ƿ�����mallet's bounding sphere
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchPress(float normalizedX, float normalizedY){
		oldX = normalizedX;
		oldY = normalizedY;
//				isTouch = true;
	}
	/**
	 * ֹͣ������ֹͣ��ת
	 */
	public void handleTouchUp(float normalizedX, float normalizedY) {
//				isTouch = false;
	}
	/**
	 * ����ʱ, ͼ��������Ϊ���㣬��ת����Ҫչʾ���档
	 * ����������ͼ�ξ�ֹ��
	 */
	public void handleTouchDrag(float x, float y){
		speed = 0.2f;

		float tmp = x/y;
		if((int)tmp > 0){
			if(x>0){//��������
				if(tmp>10){rotateX = 1.0f;return;}
				if(x == y){rotateZ = 1.0f;return;}
				if(tmp<0.1f){rotateY = 1.0f;return;}
				rotateX = rotateY = 1.0f;return;
			}else{//���Ǹ���
				speed *= -1;
			}
		}else{//һ��һ��
//					if(x>0){//x��������y�Ǹ���
//						
//					}else{//
//						
//					}
			if(tmp<-10){rotateX = 1.0f;return;}
			if(x == -y){rotateZ = 1.0f;return;}
			if(tmp>-0.1f){rotateY = 1.0f;return;}
			rotateX = rotateY = 1.0f;return;
		}
		
	}
	/**
	 * �����������
	 */
	public void initTexture() {
		/*����*/
//				texture2 = TextureHelper.loadTexture(context, R.drawable.camaro);//����
		//��������
		texture2 = TextureHelper.loadTexture(context, R.drawable.camaro);
	}
	/**
	 * ������ɫ������Ӧ�ó�������ɫ���������С�����װ�䡿
	 */
	public void initShader(int vertex_shaderID, int fragment_shaderID) {
		/**
		 * ����һ��������Դ�ļ�����ɫ��Դ���룩��ȡ�� String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, vertex_shaderID);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, fragment_shaderID);
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
//					glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, );
//					glEnableVertexAttribArray(aColorLocation);
		glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, objTextureCoodinates);
		glEnableVertexAttribArray(aTextureCoordLocation);
	}

	@Override
	public void onSurfaceCreated(GL10 gl,
			javax.microedition.khronos.egl.EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//��ɫ	//�����õ���ɫ
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		// The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.0 Enable texture mapping
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		
		ball = new Ball(context);//��
		bezier = new BezierSurface2(context);
//		initShader(R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);//ʹ��������ɫ��
		initTexture();//�����������
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
//				Log.i(TAG, "$��3��"+ width + "    " + "$��3��" + height);
		screenWidth = width;
		screenHeight= height;
		/**screenWidth/2 < screenHeight */ 
		//��Ļ��-���(��������),��aspecRatio > 1
		aspectRatio = (width/2) > height ?
				(float)(width/2)/(float)height : (float)height/(float)(width/2);

	}
	
	/**ʱ�����ת�Ƕ�*/
	Long time;
	Long startTime = SystemClock.uptimeMillis();
	
	float angleInDegrees;
	float binocularDistance = 0.066f;//˫Ŀ����

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//����������ɫ����֮ǰ��glClearColor�����ɫ
        // Do a complete rotation every 10 seconds.
        time = SystemClock.uptimeMillis() % 10000L;
        angleInDegrees = (360.0f / 10000.0f) * ((long) time);
        // ��Ϊ��Ļ�ֳ���������
        glViewport(0, 0, screenWidth/2, screenHeight);
    	// ͸��ͶӰ
//					MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        // �ӵ�λ������
        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		setIdentityM(modelMatrix, 0);// ��һ��
//				translateM(modelMatrix, 0, 0f, 0f, -2f);// ��z��ƽ��-3
//				scaleM(modelMatrix, 0, 1f, 1f, 1f);
//				rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);//��x����תangleInDegrees
		translateM(modelMatrix, 0, translateX, translateY, translateZ - 2f);//��z��ƽ��-3
		scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);
		
		if(isRotate){  //��ת�ٶȲ�Ϊ0ʱ
			rotateM(modelMatrix, 0, rotate, rotateX, rotateY, rotateZ);// ��ת��������и�������Ϊ0
			rotate += speed;
//					MatrixStack.pushMatrix(modelMatrix);
		}else{
//					MatrixStack.popMatrix(modelMatrix);
		}
		
			
		if (isTouch) {
			rotateM(modelMatrix, 0, xAngle, 1, 0, 0);// ��x��תһ��xAngle��С�ĽǶ� 
			rotateM(modelMatrix, 0, yAngle, 0, 1, 0);// 
			rotateM(modelMatrix, 0, zAngle, 0, 0, 1);// 
		}
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	// ��ͼ���������
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
    	
//    	ball.drawSelf(MVPMatrix);
    	bezier.drawSelf(MVPMatrix);
        
 /**************************************�ڶ������塾�ұߡ�************************************************/      
        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
    	//͸��ͶӰ
//					MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        //����任����
        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	//��ͼ���������
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
    	
//    	bezier.drawSelf(MVPMatrix);
    	ball.drawSelf(MVPMatrix);
    	
//		    	GLES20.glDrawElements(GL_Constant.TRIANGLES, obj.facesNum * 3, GL_UNSIGNED_Integer, objVerticesSeq);//������
        
//		    	speed += -0.001;// �ٶȵ�����ϵ��
    	
        //��ӡ˫Ŀ����-->textView
//		        Integer endTime = SystemClock.uptimeMillis();
//		        if((endTime - startTime) > 1000){//ÿ�����һ��
//		        	DecimalFormat dFormat = new DecimalFormat("##0.000");
//		        	String msg = dFormat.format(binocularDistance);
//					Bundle bd = new Bundle();//����Bundle����
//					bd.putString("msg", msg);//��Bundle�������Ϣ��putString(String key, String value)��ֵ����ʽ
//					Message tempMessage = new Message();//����Message����
//					tempMessage.setData(bd);//��Message���������
//					tempMessage.what = 0;
//					hd.sendMessage(tempMessage);//���ء��������������е�Handler��������Ϣ
//		        	startTime = endTime;
//		        	binocularDistance += 0.001f;
//		        	if(binocularDistance > 2)
//		        		binocularDistance = 0.0f;
//		        }
	}//End OnDrawFrame


}



	
