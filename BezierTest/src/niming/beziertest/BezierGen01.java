package niming.beziertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.R.integer;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.provider.ContactsContract.Contacts.Data;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
/**
 * ���ɱ���������-������
 *	
 */
public class BezierGen01 extends View{
	private Paint paint;//����
	private int centerX, centerY;
	private List<PointF> points;//PointF holds two float coordinates 
	private FloatEvaluator evaluator;//������ι��ɡ�������ɷ���
	private float fraction;//����ֵ[0.0~1.0]
	private Map<Integer, Integer> colors;
	private List<PointF> destPoints;	
	private int count = 0;
	
	public BezierGen01(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);//����ݣ�ƽ��
		paint.setStyle(Paint.Style.STROKE);
		evaluator = new FloatEvaluator();
		startAnim();
		
	}
	//���Զ�������
	private void startAnim() {
		ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
		animator.setInterpolator(new LinearInterpolator());//���Բ�ֵ
		animator.setDuration(5000);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
			//ͨ��addUpdateListener()���������һ�������ļ��������ڶ���ִ�еĹ����л᲻�ϵؽ��лص�,
			//��ֵ����ȡ����
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				fraction = animation.getAnimatedFraction();//ȡ������ֵfraction [0.0~1.0]
//				Log.i("fraction", ": "+ ++count); //count[1~295]
				invalidate();
			}
		});
		animator.start();
	}
    //���������ɫ
    private int getRanColor() {
        return 0xff000000 | new Random().nextInt(0x00ffffff);
    }
    
    //��ʼ�����ݵ�Ϳ��Ƶ�
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
    	super.onSizeChanged(w, h, oldw, oldh);
    	centerX = w/2;
    	centerY = h/2;
    	points = new ArrayList<PointF>();
        points.add(new PointF(centerX - 150, centerY));
        points.add(new PointF(centerX - 50, centerY - 300));
        points.add(new PointF(centerX + 50, centerY + 300));
        points.add(new PointF(centerX + 150, centerY));
    	
    	colors = new HashMap<Integer, Integer>();
    	for (int i = 0; i < points.size(); i++) {
			colors.put(i, getRanColor());//ÿ����õ�һ����ɫ
		}
    	destPoints = new ArrayList<PointF>();//��һ����
    	destPoints.add(points.get(0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	//��̬
    	drawPoint(canvas, points, Color.BLACK);//���Ƶ�
    	drawLine(canvas, points, Color.GRAY);//��֪ȷ���㡢���Ƶ���������
    	//��̬
    	List<PointF> subData = getSubData(points, fraction);
    	drawData(canvas, subData, fraction);
    }
    
	private void drawPoint(Canvas canvas, List<PointF> data, int color) {
		paint.setColor(color);//��ɫ
		paint.setStrokeWidth(20);//�ʴ�ϸv
		for (int i = 0; i < data.size(); i++) {
			PointF pointF = data.get(i);
			canvas.drawPoint(pointF.x, pointF.y, paint);
		}
	}
	//���ƻ�׼��
	private void drawLine(Canvas canvas, List<PointF> data, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(4);//���ʱʴ�ϸ
		for(int i=0; i<data.size() -1; i++){
			PointF start = data.get(i);
			PointF end = data.get(i+1);
			canvas.drawLine(start.x, start.y, end.x, end.y, paint);
		}
	}
	//����·��
	private void drawPath(Canvas canvas, List<PointF> data){
		Path path = new Path();
		PointF startF = data.get(0);//��һ����
		path.moveTo(startF.x, startF.y);
		for (int i = 0; i < data.size(); i++) {
			PointF point = data.get(i);
			path.lineTo(point.x, point.y);
		}
		paint.setColor(Color.RED);
		paint.setStrokeWidth(4);
		canvas.drawPath(path, paint);//����·��
	}
	
	//���������Դ
	private List<PointF> getSubData(List<PointF> data, float fraction) {
		List<PointF> subData = new ArrayList<PointF>();//�� �б�
		for (int i = 0; i < data.size()-1; i++) {
			PointF start = data.get(i);
			PointF end = data.get(i + 1);
			float x = evaluator.evaluate(fraction, start.x, end.x);//result = x0 + t * (x1 - x0), 
			float y = evaluator.evaluate(fraction, start.y, end.y);//
			subData.add(new PointF(x, y));
		}
		return subData;
	}
	//�������Ƽ���
	private void drawData(Canvas canvas, List<PointF> data, float fraction) {
		if(data.size() == 1){
			drawPoint(canvas, data, Color.BLACK);
			destPoints.add(data.get(0));
			drawPath(canvas, destPoints);
		}else{
			drawLine(canvas, data, colors.get(data.size()-1));
			//����
			List<PointF> subData = getSubData(data, fraction);
			drawData(canvas, subData, fraction);
		}
		
	}
    
}
