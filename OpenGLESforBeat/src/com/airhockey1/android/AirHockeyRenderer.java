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
	/*既然我们已经把顶点数据和着色器程序分别放于不同的类中了，现在就可以更新渲染类*/
	private final Context context;
	
	private final float[] projectionMatrix = new float[16];//投影矩阵，创建三维幻景
	private final float[] modelMatrix = new float[16];//模型矩阵，用来把物体放在世界空间(World Space)坐标系。
	
	private final float[] viewMatrix = new float[16];//视图矩阵，平等地影响场景中的每个物体。
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
	
	/*第九章*/
	/**
	 * 【相交测试】
	 * 1)首先，我们要把二维屏幕坐标转换到三维空间，并看看我们触碰到什么。要做到这点，我们要把
	 * 被触碰的点投射影到一条射线上，这条射线从我们的视点跨越那个三维场景。
	 * 2)然后，我们需要检查看看这条射线是否与木槌相交。为了使事情简单些，我们假定
	 * 那个木槌实际上是一个差不多同样大小的包围球，然后测试那个球。
	 */
	private boolean malletPressed = false; //malletPressed跟踪木槌当前是否被按到
	private boolean redMalletPressed = false; //malletRed跟踪 红 木槌当前是否被按到
	private Point blueMalletPosition; //木槌的当前位置存储在blueMalletPosition,它被初始化一个默认值
	private Point redMalletPosition; //木槌的当前位置存储在blueMalletPosition,它被初始化一个默认值
	
	//实现上述目标，我们定义一个反转矩阵，它会取消视图矩阵和投影矩阵的效果。
	private final float[] invertedViewProjectionMatrix = new float[16];
	
	/**
	 * [9.4.1]木槌边界定义
	 */
	private final float leftBound = -0.5f;
	private final float rightBound = 0.5f;
	private final float farBound = -0.8f;
	private final float nearBound = 0.8f;
	
	float lookX = 0f;//eyeX
	float lookY = 1.2f;//eyeY
	float preLookX, preLookY;
	/**
	 * [9.4.2]增加速度和方向
	 * 1)木槌要移动多块？
	 * 2)木槌要向哪个方向移动？
	 * 打击木槌的问题：随着时间变化持续跟踪木槌是如何移动。
	 */
	private Point previousBlueMalletPosition;
	private Point puckPosition;//冰球位置
	private Vector puckVector;//使用向量记录冰球的速度和方向

	
	
	/**
	 * 是否触摸到mallet's bounding sphere
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
		
		/*两个木槌不能同时动*/
		if(malletPressed == false){
			//检测红木槌是否被触碰
			Sphere redMalletBoundingSphere = new Sphere(
					new Point(redMalletPosition.x, redMalletPosition.y, redMalletPosition.z), 
					mallet.height / 2f);
			redMalletPressed = Geometry.intersects(redMalletBoundingSphere, ray);
		}
	}
	
	/**
	 * 碰撞检测
	 */
	private boolean isImpack(Point malletPosition, Point puckPosition) {
		/*碰撞检测*/
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
	 * 9.3 通过拖动移动物体
	 * 
	 */
	public void handleTouchDrag(float normalizedX, float normalizedY){
		
		if(malletPressed){
			Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
			//Define a plane representing our air hockey table.
			Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
			
			//Find out where the touched point intersects the plane representing out table.
			//We'll move the mallet along this plane.
			Point touchedPoint = Geometry.intersectsPoint(ray, plane);//射线与平面相交点
			
			previousBlueMalletPosition = blueMalletPosition;
			/*要保持木槌不超过桌子边界，touchedPoint限定在桌子内*/
			blueMalletPosition = 
//					new Point(touchedPoint.x, mallet.height /2f, touchedPoint.z);
					new Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
							mallet.height / 2f,
							clamp(touchedPoint.z, 0f + mallet.radius, nearBound - mallet.radius)
							);
			
//			/*碰撞检测*/
			if(isImpack(blueMalletPosition, puckPosition)){
				//The mallet has struck the puck.Now send the puck flying
				//based on the mallet velocity(速率).
				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
			}

//			float distance = 
//					Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
//			
//			if(distance < (puck.radius + mallet.radius)){
//				//The mallet has struck the puck.Now send the puck flying
//				//based on the mallet velocity(速率).
//				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
//			}
		}else if(redMalletPressed){//远端红木槌动
			Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
			//Define a plane representing our air hockey table.
			Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
			
			//Find out where the touched point intersects the plane representing out table.
			//We'll move the mallet along this plane.
			Point touchedPoint = Geometry.intersectsPoint(ray, plane);//计算射线与平面相交点
			
			previousBlueMalletPosition = redMalletPosition;
			/*要保持木槌不超过桌子边界，touchedPoint限定在桌子内*/
			redMalletPosition = 
					new Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
							mallet.height / 2f,
							clamp(touchedPoint.z, farBound + mallet.radius, 0f - mallet.radius)
							);
			/*碰撞检测*/
			if(isImpack(redMalletPosition, puckPosition)){
				//The mallet has struck the puck.Now send the puck flying
				//based on the mallet velocity(速率).
				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, redMalletPosition);
			}

//			float distance = 
//					Geometry.vectorBetween(redMalletPosition, puckPosition).length();
//			
//			if(distance < (puck.radius + mallet.radius)){
//				//The mallet has struck the puck.Now send the puck flying
//				//based on the mallet velocity(速率).
//				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, redMalletPosition);
//			}
			
		}else{
			/*旋转场景视点*/
			float moveX = Math.abs(normalizedX - preLookX);
			float moveY = Math.abs(normalizedY - preLookY);
			//不能偷懒，将横向旋转视点与 纵向旋转 分列, 谁动谁变换，另一个默认不变（不管不顾，维持原状）
//			if(Math.abs(normalizedX - preLookX) < 0.01f && Math.abs(normalizedY - preLookY) < 0.01f ){/*触动太短，忽略*/}
//			else{
//				if(Math.abs(normalizedX - preLookX) > 0.2f){//这是横向旋转
//					lookX = -(normalizedX - preLookX);
//				}else if(Math.abs(normalizedY - preLookY) > 0.2f ){//纵向
//					lookY = normalizedY - preLookY;
//				}
//			}
			/*优化分支*/
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
	/*clamp夹紧、锁住*/
	private float clamp(float value, float min, float max){
		return Math.min(max, Math.max(value, min));
	}
	/*9.2.1把被触碰的点扩展为三维直线*/
	/* 为了把被触碰的点转换为一个三维射线，实质上我们需要取消透视投影和透视除法。
	 * 我们把被触碰的点映射到三维空间的一条直线：直线的近端映射到frustum在投影矩阵中定义的近平面，直线的远端映射到视椎体的远平面。
	 */
	/**
	 * 归一化的2D坐标，转换 成三维Ray
	 * @param normalizedX
	 * @param normalizedY
	 * @return
	 */
	private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
		//We'll convert these normalized device coordinates into world-space coordinates.
		//We'll pick a point on the near and far planes, and draw a line between them.
		//To do this transform, we need to first multiply by the inverse matrix, and
		//then we need to undo the perspective divide.
		final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};//归一化设备坐标
		final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
		
		final float[] nearPointWorld = new float[4];//世界空间坐标
		final float[] farPointWorld = new float[4];
		/*顶点和invertedViewProjectionMatrix(反转的视图投影矩阵)相乘后，nearPointWorld和farPointWorld实际上就含有反转的w值。
		* 这是因为投影矩阵的主要意义就是创建不同的w值，以便透视除法可以施加它的魔法(伪三维)。
		*/
		multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
		multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
		
		/*
		 * 我们把x、y、z除以【不是乘吗？】这些反转后的w，这样就撤销了透视除法的影响。
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
		
		blueMalletPosition = new Point(0f, mallet.height / 2f, 0.4f);//近点木槌
		redMalletPosition = new Point(0f, mallet.height / 2f, -0.4f);//远点木槌
		
		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
		
		texture = TextureHelper.loadTexture(context, R.drawable.taiji);
		
		/*9.4.2 初始化*/
		puckPosition = new Point(0f, puck.height / 2f, 0f);
		puckVector = new Vector(0, 0, 0);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		/*当OpenGL做映射的时候，它会把从(-1,-1,-1)至(1,1,1)范围映射到那个为显示而预留的窗口上。
		 *这个范围之外的归一化设备坐标会被裁剪掉。 
		 */
		glViewport(0, 0, width, height);//视口
		/*生成投影矩阵*/
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width/(float)height, 1f, 10f);
		
//		//视椎体平移
//		setIdentityM(modelMatrix, 0);//归一化
//		translateM(modelMatrix, 0, 0f, 0f, -3f);//转换
//		rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
//		//矩阵连乘
//		//vertex_eye = ModelMatrix * vertex_model//统一到世界坐标系
//		//vertex_clip = ProjectionMatrix * vertex_eye
//		//vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
//		final float[] temp = new float[16];
//		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
		
		/*8.6.3 定义视图矩阵*/
//		Define a viewing transformation in terms of an eye point, a center of view, and an up vector.
		setLookAtM(viewMatrix, 0, lookX, lookY, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
		/**
		 * setLookAtM(rm, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
		 * float[] rm : 这是目标矩阵，这个矩阵的长度应该至少容纳16个元素，以便它能存储视图举证。
		 * int rmOffset : setLookAtM()会把结果从rmOffset的这个偏移值开始存进rm
		 * float eyeX, eyeY, eyeZ : 这是眼睛所在的位置。场景中的所有东西开起来都像是从这个点观察它们一样
		 * float centerX, centerY, centerZ : 这是眼睛正在看的地方；这个位置出现在这个场景的中心
		 * float upX, upY, upZ : 要是我们刚才正在讨论你的眼睛，那么这是你的头指向的地方。upY = 1;意味着你的头笔直指向上方。
		 */
	}
	
	@Override
	/**
	 * 几乎所有的display()完成类似操作
	 * 1)调用glClear()来清除窗口内容；
	 * 2)调用OpenGL命令来渲染对象；
	 * 3)将最终图像输出到屏幕的每一帧。
	 */
	public void onDrawFrame(GL10 glUnused) {
		//Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);
		
		puckPosition = puckPosition.translate(puckVector);//冰球移动
		
		/*给冰球设置边界*/
		if(puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius){
			puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
			puckVector = puckVector.scale(0.85f);//反弹阻尼
		}
		if(puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius){
			puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
			puckVector = puckVector.scale(0.85f);//反弹阻尼
		}
		
		/*再次碰撞Mallet*/
		if(isImpack(blueMalletPosition, puckPosition)){
			puckVector = new Vector(-puckVector.x, puckVector.y, -puckVector.z);
			puckVector = puckVector.scale(0.9f);//反弹阻尼
			//防止冰球与木槌重合，导致游戏失真
		}
		if(isImpack(redMalletPosition, puckPosition))
		{
			puckVector = new Vector(-puckVector.x, puckVector.y, -puckVector.z);
			puckVector = puckVector.scale(0.9f);//反弹阻尼
		}
		//Clamp the puck position
		puckPosition = new Point(
				clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius), 
				puckPosition.y, 
				clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));
		//矫正视点，总是在桌面的上方
//		lookX += lookX;//不行，无法阻止视点重新设置
//		lookY += lookY;
		lookY = (float) Math.max(lookY, 0.02f);
		
//		setLookAtM(viewMatrix, 0, lookX, lookY, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);//视点变换
		
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);//创建一个反转矩阵
		
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
		puckVector = puckVector.scale(0.99f);//增加桌面摩擦
		
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
