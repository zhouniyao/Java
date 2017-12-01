package com.airhockey1.android;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;




import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.FloatMath;
import android.widget.Toast;

import com.airhockey1.android.util.Geometry.Plane;
import com.airhockey1.android.util.Geometry.Point;
import com.airhockey1.android.util.Geometry.Ray;
import com.airhockey1.android.util.Geometry.Sphere;
import com.airhockey1.android.util.Geometry.Vector;
import com.airhockey1.android.util.MatrixHelper;
import com.airhockey1.android.util.TextureHelper;
import com.airhockey1.android.util.Geometry;
import com.airhockey1.android.util.Geometry.*;
import com.airhockey1.android.objects.Mallet;
import com.airhockey1.android.objects.Puck;
import com.airhockey1.android.objects.Table;
import com.airhockey1.android.program.ColorShaderProgram;
import com.airhockey1.android.program.TextureShaderProgram;
import com.niming.airhockey1.R;

public class AirHockeyRenderer implements Renderer {
	/*��Ȼ�����Ѿ��Ѷ������ݺ���ɫ������ֱ���ڲ�ͬ�������ˣ����ھͿ��Ը�����Ⱦ��*/
	private final Context context;
	
	private final float[] projectionMatrix = new float[16];//ͶӰ���󣬴�����ά�þ�
	private final float[] modelMatrix = new float[16];//ģ�;��������������������ռ�(World Space)����ϵ��
	
	private final float[] viewMatrix = new float[16];//��ͼ����ƽ�ȵ�Ӱ�쳡���е�ÿ�����塣
	private final float[] viewProjectionMatrix = new float[16];
	private final float[] modelViewProjectionMatrix = new float[16];
	
	/**
	 * vertex_clip = ProjectionMatrix * vertex_eye
	 * vertex_clip = ProjectionMatrix * ViewMatrix * vertex_world
	 * vertex_clip = ProjectionMatrix * ViewMatrix * ModelMatrix * vertex_model
	 */
	
	private Table table;
	private Mallet mallet;
	private Puck puck;
	
	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;
	
	private int texture;
	
	public AirHockeyRenderer(Context context) {

		this.context = context;
	}
	
	/*�ھ���*/
	/**
	 * ���ཻ���ԡ�
	 * 1)���ȣ�����Ҫ�Ѷ�ά��Ļ����ת������ά�ռ䣬���������Ǵ�����ʲô��Ҫ������㣬����Ҫ��
	 * �������ĵ�Ͷ��Ӱ��һ�������ϣ��������ߴ����ǵ��ӵ��Խ�Ǹ���ά������
	 * 2)Ȼ��������Ҫ��鿴�����������Ƿ���ľ��ཻ��Ϊ��ʹ�����Щ�����Ǽٶ�
	 * �Ǹ�ľ�ʵ������һ�����ͬ����С�İ�Χ��Ȼ������Ǹ���
	 */
	private boolean malletPressed = false; //malletPressed����ľ鳵�ǰ�Ƿ񱻰���
	private boolean redMalletPressed = false; //malletRed���� �� ľ鳵�ǰ�Ƿ񱻰���
	private Point blueMalletPosition; //ľ鳵ĵ�ǰλ�ô洢��blueMalletPosition,������ʼ��һ��Ĭ��ֵ
	private Point redMalletPosition; //ľ鳵ĵ�ǰλ�ô洢��blueMalletPosition,������ʼ��һ��Ĭ��ֵ
	
	//ʵ������Ŀ�꣬���Ƕ���һ����ת��������ȡ����ͼ�����ͶӰ�����Ч����
	private final float[] invertedViewProjectionMatrix = new float[16];
	
	/**
	 * [9.4.1]ľ鳱߽綨��
	 */
	private final float leftBound = -0.5f;
	private final float rightBound = 0.5f;
	private final float farBound = -0.8f;
	private final float nearBound = 0.8f;
	
	float lookX = 0f;//eyeX
	float lookY = 1.2f;//eyeY
	float preLookX, preLookY;
	/**
	 * [9.4.2]�����ٶȺͷ���
	 * 1)ľ�Ҫ�ƶ���飿
	 * 2)ľ�Ҫ���ĸ������ƶ���
	 * ���ľ鳵����⣺����ʱ��仯��������ľ�������ƶ���
	 */
	private Point previousBlueMalletPosition;
	private Point puckPosition;//����λ��
	private Vector puckVector;//ʹ��������¼������ٶȺͷ���

	
	
	/**
	 * �Ƿ�����mallet's bounding sphere
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchPress(float normalizedX, float normalizedY){
		this.preLookX = normalizedX;
		this.preLookY = normalizedY;

		Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
		
		//Now test if this ray intersects with the mallet
		//by creating a bounding sphere that wraps the mallet.
		Sphere malletBoundingSphere = new Sphere(
				new Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), 
				mallet.height / 2f);
	
		//If the ray intersects(if the user touched a part of the screen that
		//intersects the mallet's bounding sphere), then set malletPressed = true.
		malletPressed = Geometry.intersects(malletBoundingSphere, ray);
		
		/*����ľ鳲���ͬʱ��*/
		if(malletPressed == false){
			//����ľ��Ƿ񱻴���
			Sphere redMalletBoundingSphere = new Sphere(
					new Point(redMalletPosition.x, redMalletPosition.y, redMalletPosition.z), 
					mallet.height / 2f);
			redMalletPressed = Geometry.intersects(redMalletBoundingSphere, ray);
		}
	}
	
	/**
	 * ��ײ���
	 */
	private boolean isImpack(Point malletPosition, Point puckPosition) {
		/*��ײ���*/
		float distance = 
//				Geometry.vectorBetween(malletPosition, puckPosition).length();
				FloatMath.sqrt(
						(malletPosition.x - puckPosition.x) * (malletPosition.x - puckPosition.x) + 
						(malletPosition.y - puckPosition.y) * (malletPosition.y - puckPosition.y) + 
						(malletPosition.z - puckPosition.z) * (malletPosition.z - puckPosition.z));
		
		 if(distance < (puck.radius + mallet.radius)){
			 return true;
		 }
		 return false;
	}
	
	/**
	 * 9.3 ͨ���϶��ƶ�����
	 * 
	 */
	public void handleTouchDrag(float normalizedX, float normalizedY){
		
		if(malletPressed){
			Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
			//Define a plane representing our air hockey table.
			Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
			
			//Find out where the touched point intersects the plane representing out table.
			//We'll move the mallet along this plane.
			Point touchedPoint = Geometry.intersectsPoint(ray, plane);//������ƽ���ཻ��
			
			previousBlueMalletPosition = blueMalletPosition;
			/*Ҫ����ľ鳲��������ӱ߽磬touchedPoint�޶���������*/
			blueMalletPosition = 
//					new Point(touchedPoint.x, mallet.height /2f, touchedPoint.z);
					new Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
							mallet.height / 2f,
							clamp(touchedPoint.z, 0f + mallet.radius, nearBound - mallet.radius)
							);
			
//			/*��ײ���*/
			if(isImpack(blueMalletPosition, puckPosition)){
				//The mallet has struck the puck.Now send the puck flying
				//based on the mallet velocity(����).
				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
			}

//			float distance = 
//					Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
//			
//			if(distance < (puck.radius + mallet.radius)){
//				//The mallet has struck the puck.Now send the puck flying
//				//based on the mallet velocity(����).
//				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
//			}
		}else if(redMalletPressed){//Զ�˺�ľ鳶�
			Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
			//Define a plane representing our air hockey table.
			Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
			
			//Find out where the touched point intersects the plane representing out table.
			//We'll move the mallet along this plane.
			Point touchedPoint = Geometry.intersectsPoint(ray, plane);//����������ƽ���ཻ��
			
			previousBlueMalletPosition = redMalletPosition;
			/*Ҫ����ľ鳲��������ӱ߽磬touchedPoint�޶���������*/
			redMalletPosition = 
					new Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
							mallet.height / 2f,
							clamp(touchedPoint.z, farBound + mallet.radius, 0f - mallet.radius)
							);
			/*��ײ���*/
			if(isImpack(redMalletPosition, puckPosition)){
				//The mallet has struck the puck.Now send the puck flying
				//based on the mallet velocity(����).
				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, redMalletPosition);
			}

//			float distance = 
//					Geometry.vectorBetween(redMalletPosition, puckPosition).length();
//			
//			if(distance < (puck.radius + mallet.radius)){
//				//The mallet has struck the puck.Now send the puck flying
//				//based on the mallet velocity(����).
//				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, redMalletPosition);
//			}
			
		}else{
			/*��ת�����ӵ�*/
			float moveX = Math.abs(normalizedX - preLookX);
			float moveY = Math.abs(normalizedY - preLookY);
			//����͵������������ת�ӵ��� ������ת ����, ˭��˭�任����һ��Ĭ�ϲ��䣨���ܲ��ˣ�ά��ԭ״��
//			if(Math.abs(normalizedX - preLookX) < 0.01f && Math.abs(normalizedY - preLookY) < 0.01f ){/*����̫�̣�����*/}
//			else{
//				if(Math.abs(normalizedX - preLookX) > 0.2f){//���Ǻ�����ת
//					lookX = -(normalizedX - preLookX);
//				}else if(Math.abs(normalizedY - preLookY) > 0.2f ){//����
//					lookY = normalizedY - preLookY;
//				}
//			}
			/*�Ż���֧*/
			switch ((int)(moveX + moveY + 0.8)) {
			case 0:
				break;

			default:
				if(moveY > 0.2f){
					lookY = normalizedY - preLookY;
				}else if(moveX > 0.2f){
					lookX = -(normalizedX - preLookX);
				}
			}//switch end
		}
	}
	/*clamp�н�����ס*/
	private float clamp(float value, float min, float max){
		return Math.min(max, Math.max(value, min));
	}
	/*9.2.1�ѱ������ĵ���չΪ��άֱ��*/
	/* Ϊ�˰ѱ������ĵ�ת��Ϊһ����ά���ߣ�ʵ����������Ҫȡ��͸��ͶӰ��͸�ӳ�����
	 * ���ǰѱ������ĵ�ӳ�䵽��ά�ռ��һ��ֱ�ߣ�ֱ�ߵĽ���ӳ�䵽frustum��ͶӰ�����ж���Ľ�ƽ�棬ֱ�ߵ�Զ��ӳ�䵽��׵���Զƽ�档
	 */
	/**
	 * ��һ����2D���꣬ת�� ����άRay
	 * @param normalizedX
	 * @param normalizedY
	 * @return
	 */
	private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
		//We'll convert these normalized device coordinates into world-space coordinates.
		//We'll pick a point on the near and far planes, and draw a line between them.
		//To do this transform, we need to first multiply by the inverse matrix, and
		//then we need to undo the perspective divide.
		final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};//��һ���豸����
		final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
		
		final float[] nearPointWorld = new float[4];//����ռ�����
		final float[] farPointWorld = new float[4];
		/*�����invertedViewProjectionMatrix(��ת����ͼͶӰ����)��˺�nearPointWorld��farPointWorldʵ���Ͼͺ��з�ת��wֵ��
		* ������ΪͶӰ�������Ҫ������Ǵ�����ͬ��wֵ���Ա�͸�ӳ�������ʩ������ħ��(α��ά)��
		*/
		multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
		multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
		
		/*
		 * ���ǰ�x��y��z���ԡ����ǳ��𣿡���Щ��ת���w�������ͳ�����͸�ӳ�����Ӱ�졣
		 */
		divideByW(nearPointWorld);
		divideByW(farPointWorld);
        // We don't care about the W value anymore, because our points are now
        // in world coordinates.
		Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
		Point farPointRay  = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
		return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
	}
	
	void divideByW(float[] vector){
		vector[0] /= vector[3];
		vector[1] /= vector[3];
		vector[2] /= vector[3];
	}
	

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0f, 0f, 0f, 0f);
		
		
		table = new Table();
		mallet = new Mallet(0.08f, 0.15f, 32);
		puck = new Puck(0.06f, 0.02f, 32);
		
		blueMalletPosition = new Point(0f, mallet.height / 2f, 0.4f);//����ľ�
		redMalletPosition = new Point(0f, mallet.height / 2f, -0.4f);//Զ��ľ�
		
		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
		
		texture = TextureHelper.loadTexture(context, R.drawable.taiji);
		
		/*9.4.2 ��ʼ��*/
		puckPosition = new Point(0f, puck.height / 2f, 0f);
		puckVector = new Vector(0, 0, 0);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		/*��OpenGL��ӳ���ʱ������Ѵ�(-1,-1,-1)��(1,1,1)��Χӳ�䵽�Ǹ�Ϊ��ʾ��Ԥ���Ĵ����ϡ�
		 *�����Χ֮��Ĺ�һ���豸����ᱻ�ü����� 
		 */
		glViewport(0, 0, width, height);//�ӿ�
		/*����ͶӰ����*/
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width/(float)height, 1f, 10f);
		
//		//��׵��ƽ��
//		setIdentityM(modelMatrix, 0);//��һ��
//		translateM(modelMatrix, 0, 0f, 0f, -3f);//ת��
//		rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
//		//��������
//		//vertex_eye = ModelMatrix * vertex_model//ͳһ����������ϵ
//		//vertex_clip = ProjectionMatrix * vertex_eye
//		//vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
//		final float[] temp = new float[16];
//		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
		
		/*8.6.3 ������ͼ����*/
//		Define a viewing transformation in terms of an eye point, a center of view, and an up vector.
		setLookAtM(viewMatrix, 0, lookX, lookY, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
		/**
		 * setLookAtM(rm, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		 * float[] rm : ����Ŀ������������ĳ���Ӧ����������16��Ԫ�أ��Ա����ܴ洢��ͼ��֤��
		 * int rmOffset : setLookAtM()��ѽ����rmOffset�����ƫ��ֵ��ʼ���rm
		 * float eyeX, eyeY, eyeZ : �����۾����ڵ�λ�á������е����ж��������������Ǵ������۲�����һ��
		 * float centerX, centerY, centerZ : �����۾����ڿ��ĵط������λ�ó������������������
		 * float upX, upY, upZ : Ҫ�����Ǹղ�������������۾�����ô�������ͷָ��ĵط���upY = 1;��ζ�����ͷ��ֱָ���Ϸ���
		 */
	}
	
	@Override
	/**
	 * �������е�display()������Ʋ���
	 * 1)����glClear()������������ݣ�
	 * 2)����OpenGL��������Ⱦ����
	 * 3)������ͼ���������Ļ��ÿһ֡��
	 */
	public void onDrawFrame(GL10 glUnused) {
		//Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);
		
		puckPosition = puckPosition.translate(puckVector);//�����ƶ�
		
		/*���������ñ߽�*/
		if(puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius){
			puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
			puckVector = puckVector.scale(0.85f);//��������
		}
		if(puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius){
			puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
			puckVector = puckVector.scale(0.85f);//��������
		}
		
		/*�ٴ���ײMallet*/
		if(isImpack(blueMalletPosition, puckPosition)){
			puckVector = new Vector(-puckVector.x, puckVector.y, -puckVector.z);
			puckVector = puckVector.scale(0.9f);//��������
			//��ֹ������ľ��غϣ�������Ϸʧ��
		}
		if(isImpack(redMalletPosition, puckPosition))
		{
			puckVector = new Vector(-puckVector.x, puckVector.y, -puckVector.z);
			puckVector = puckVector.scale(0.9f);//��������
		}
		//Clamp the puck position
		puckPosition = new Point(
				clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius), 
				puckPosition.y, 
				clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));
		//�����ӵ㣬������������Ϸ�
//		lookX += lookX;//���У��޷���ֹ�ӵ���������
//		lookY += lookY;
		lookY = (float) Math.max(lookY, 0.02f);
		
//		setLookAtM(viewMatrix, 0, lookX, lookY, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);//�ӵ�任
		
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);//����һ����ת����
		
		//Draw the table.
		positionTableInScene();
		textureProgram.useProgram();
		textureProgram.setUniforms(modelViewProjectionMatrix, texture);
		table.bindData(textureProgram);
		table.draw();
		
		colorProgram.useProgram();
		//Draw the puck
		positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
		puck.bindData(colorProgram);
		puck.draw();
		puckVector = puckVector.scale(0.99f);//��������Ħ��
		
		//Draw the blue mallets.
		positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
		mallet.bindData(colorProgram);
		mallet.draw();
		
		//Red Mallets
		positionObjectInScene(redMalletPosition.x, redMalletPosition.y, redMalletPosition.z);
//		positionObjectInScene(0f, mallet.height/2f, -0.4f);
		colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
		mallet.draw();
		
		
	}

	private void positionTableInScene(){
		//The table is defined in terms of X & Y coordinates, so we rotate it 
		//90 degrees to lie flat on the XZ plane
		setIdentityM(modelMatrix, 0);
		rotateM(modelMatrix, 0, -90, 1f, 0f, 0f);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
	}

	private void positionObjectInScene(float x, float y, float z){
		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, x, y, z);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
		
	}
	
	

}
