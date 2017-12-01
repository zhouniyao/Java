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
 * 三阶贝塞尔曲线
 * <p/>
 * 2个数据点和2个控制点
 * 公式：B(t) = P0 * (1-t)^3 + 3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3 代入
 */
public class Bezier3 extends View {

	private Paint paint;
    private int centerX, centerY;
    private List<PointF> points;
	private FloatEvaluator evaluator;//动画如何过渡【计算过渡法】
	private float fraction;//过渡值[0.0~1.0]
	
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
  //属性动画设置
  	private void startAnim() {
  		ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
  		animator.setInterpolator(new LinearInterpolator());//线性插值
  		animator.setDuration(5000);
  		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
  			//通过addUpdateListener()方法来添加一个动画的监听器，在动画执行的过程中会不断地进行回调,
  			//把值从中取出来
  			@Override
  			public void onAnimationUpdate(ValueAnimator animation) {
  				fraction = animation.getAnimatedFraction();//取出过渡值fraction [0.0~1.0]
//  				Log.i("fraction", ": "+ ++count); //count[1~295]
  				invalidate();
  			}
  		});
  		animator.start();
  	}
    //初始化数据点和控制点的位置
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

    //根据触摸位置更新控制点，并提示重绘
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

    // 绘制数据点
    private void drawPoint(Canvas canvas, List<PointF> data, int color) {
        paint.setColor(color);
        paint.setStrokeWidth(20);
        for (int i = 0; i < data.size(); i++) {
            PointF pointF = data.get(i);
            canvas.drawPoint(pointF.x, pointF.y, paint);
        }
    }

    //绘制基准线
    private void drawLine(Canvas canvas, List<PointF> data, int color) {
        paint.setColor(color);
        paint.setStrokeWidth(4);
        for (int i = 0; i < data.size() - 1; i++) {
            PointF start = data.get(i);
            PointF end = data.get(i + 1);
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
        }
    }

    //绘制贝塞尔曲线
    private void drawBezierCurve(Canvas canvas) {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        Path path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);
        //用来实现3介贝塞尔曲线,(x1,y1) 为控制点，(x2,y2)为控制点，(x3,y3) 为结束点。
        //如果我们不加 moveTo 呢？则以(0,0)为起点
        path.cubicTo(points.get(1).x, points.get(1).y, points.get(2).x, points.get(2).y, points.get(3).x, points.get(3).y);
        canvas.drawPath(path, paint);
    }

    //获取两点的距离
    private double getDistance(PointF start, PointF end) {
        return Math.sqrt((end.y - start.y) * (end.y - start.y) + (end.x - start.x) * (end.x - start.x));
    }


}
