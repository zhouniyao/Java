package niming.beziertest;

import java.util.ArrayList;
import java.util.List;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
/**
 * ���ױ���������
 * <p/>
 * 2�����ݵ��2�����Ƶ�
 * ��ʽ��B(t) = P0 * (1-t)^3 + 3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3 ����
 */
public class Bezier3 extends View {

	private Paint paint;
    private int centerX, centerY;
    private List<PointF> points;
	private FloatEvaluator evaluator;//������ι��ɡ�������ɷ���
	private float fraction;//����ֵ[0.0~1.0]
	
    public Bezier3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
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
//  				Log.i("fraction", ": "+ ++count); //count[1~295]
  				invalidate();
  			}
  		});
  		animator.start();
  	}
    //��ʼ�����ݵ�Ϳ��Ƶ��λ��
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        points = new ArrayList<PointF>();
        points.add(new PointF(centerX - 150, centerY));
        points.add(new PointF(centerX - 50, centerY - 300));
        points.add(new PointF(centerX + 50, centerY + 300));
        points.add(new PointF(centerX + 150, centerY));
    }

    //���ݴ���λ�ø��¿��Ƶ㣬����ʾ�ػ�
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF control = new PointF(0, 0);
        control.x = event.getX();
        control.y = event.getY();

        double distance1 = getDistance(control, points.get(1));
        double distance2 = getDistance(control, points.get(2));

        if (distance1 < distance2) {
            points.set(1, control);
        } else {
            points.set(2, control);
        }
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPoint(canvas, points, Color.BLACK);
        drawLine(canvas, points, Color.GRAY);
        drawBezierCurve(canvas);
    }

    // �������ݵ�
    private void drawPoint(Canvas canvas, List<PointF> data, int color) {
        paint.setColor(color);
        paint.setStrokeWidth(20);
        for (int i = 0; i < data.size(); i++) {
            PointF pointF = data.get(i);
            canvas.drawPoint(pointF.x, pointF.y, paint);
        }
    }

    //���ƻ�׼��
    private void drawLine(Canvas canvas, List<PointF> data, int color) {
        paint.setColor(color);
        paint.setStrokeWidth(4);
        for (int i = 0; i < data.size() - 1; i++) {
            PointF start = data.get(i);
            PointF end = data.get(i + 1);
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
        }
    }

    //���Ʊ���������
    private void drawBezierCurve(Canvas canvas) {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        Path path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);
        //����ʵ��3�鱴��������,(x1,y1) Ϊ���Ƶ㣬(x2,y2)Ϊ���Ƶ㣬(x3,y3) Ϊ�����㡣
        //������ǲ��� moveTo �أ�����(0,0)Ϊ���
        path.cubicTo(points.get(1).x, points.get(1).y, points.get(2).x, points.get(2).y, points.get(3).x, points.get(3).y);
        canvas.drawPath(path, paint);
    }

    //��ȡ����ľ���
    private double getDistance(PointF start, PointF end) {
        return Math.sqrt((end.y - start.y) * (end.y - start.y) + (end.x - start.x) * (end.x - start.x));
    }


}
