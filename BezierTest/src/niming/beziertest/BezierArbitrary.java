 package niming.beziertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class BezierArbitrary extends View {
	private Paint paint;//����
	private int centerX, centerY;
	private List<PointF> points;//PointF holds two float coordinates 
	private FloatEvaluator evaluator;//������ι��ɡ�������ɷ���
	private float fraction;//����ֵ[0.0~1.0]
	private Map<Integer, Integer> colors;
	private List<PointF> destPoints;	
	private int count = 0;//���������ϵ������
	private BezierUtil2 beUtil = null;
	
	public BezierArbitrary(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);//����ݣ�ƽ��
		paint.setStyle(Paint.Style.STROKE);
		evaluator = new FloatEvaluator();
		beUtil = new BezierUtil2();
		
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
//					Log.i("fraction", ": "+ ++count); //count[1~295]
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
//        points.add(new PointF(centerX - 150, centerY));
//        points.add(new PointF(centerX - 50, centerY - 300));
//        points.add(new PointF(centerX + 50, centerY + 300));
//        points.add(new PointF(centerX + 150, centerY));
    	
        points.add(new PointF(centerX - 250, centerY));
        points.add(new PointF(centerX - 300, centerY - 300));
        points.add(new PointF(centerX - 50, centerY - 300));//4�鱴����������
        points.add(new PointF(centerX + 50, centerY + 300));
        points.add(new PointF(centerX + 150, centerY));
    	
    	
        beUtil.al = (ArrayList<PointF>) points;//��ʼ�����Ƶ�
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
//    	destPoints = beUtil.getBezierData(fraction);//��һ�֣�һ���Ի���
    	destPoints.add(beUtil.getBezierData(fraction));//����������Դ��̬��fraction�󣬶�̬����һ����
    	drawPath2(canvas, destPoints);
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
	private void drawPath2(Canvas canvas, List<PointF> data){
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
	
	
}
