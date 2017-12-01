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
	private Paint paint;//画笔
	private int centerX, centerY;
	private List<PointF> points;//PointF holds two float coordinates 
	private FloatEvaluator evaluator;//动画如何过渡【计算过渡法】
	private float fraction;//过渡值[0.0~1.0]
	private Map<Integer, Integer> colors;
	private List<PointF> destPoints;	
	private int count = 0;//计算曲线上点的总数
	private BezierUtil2 beUtil = null;
	
	public BezierArbitrary(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);//抗锯齿，平滑
		paint.setStyle(Paint.Style.STROKE);
		evaluator = new FloatEvaluator();
		beUtil = new BezierUtil2();
		
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
//					Log.i("fraction", ": "+ ++count); //count[1~295]
				invalidate();
			}
		});
		animator.start();
	}
	
	 //生成随机颜色
    private int getRanColor() {
        return 0xff000000 | new Random().nextInt(0x00ffffff);
    }
    
    //初始化数据点和控制点
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
        points.add(new PointF(centerX - 50, centerY - 300));//4介贝塞尔测试用
        points.add(new PointF(centerX + 50, centerY + 300));
        points.add(new PointF(centerX + 150, centerY));
    	
    	
        beUtil.al = (ArrayList<PointF>) points;//初始化控制点
    	colors = new HashMap<Integer, Integer>();
    	for (int i = 0; i < points.size(); i++) {
			colors.put(i, getRanColor());//每个点得到一个颜色
		}
    	destPoints = new ArrayList<PointF>();//第一个点
    	destPoints.add(points.get(0));
    }
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	//静态
    	drawPoint(canvas, points, Color.BLACK);//绘制点
    	drawLine(canvas, points, Color.GRAY);//已知确定点、控制点与点间连线
    	//动态
//    	destPoints = beUtil.getBezierData(fraction);//第一种，一次性绘制
    	destPoints.add(beUtil.getBezierData(fraction));//给产生数据源动态的fraction后，动态产生一个点
    	drawPath2(canvas, destPoints);
    }
    
	private void drawPoint(Canvas canvas, List<PointF> data, int color) {
		paint.setColor(color);//颜色
		paint.setStrokeWidth(20);//笔粗细v
		for (int i = 0; i < data.size(); i++) {
			PointF pointF = data.get(i);
			canvas.drawPoint(pointF.x, pointF.y, paint);
		}
	}
	//绘制基准线
	private void drawLine(Canvas canvas, List<PointF> data, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(4);//画笔笔粗细
		for(int i=0; i<data.size() -1; i++){
			PointF start = data.get(i);
			PointF end = data.get(i+1);
			canvas.drawLine(start.x, start.y, end.x, end.y, paint);
		}
	}
	//绘制路径
	private void drawPath2(Canvas canvas, List<PointF> data){
		Path path = new Path();
		PointF startF = data.get(0);//第一个点
		path.moveTo(startF.x, startF.y);
		for (int i = 0; i < data.size(); i++) {
			PointF point = data.get(i);
			path.lineTo(point.x, point.y);
		}
		paint.setColor(Color.RED);
		paint.setStrokeWidth(4);
		canvas.drawPath(path, paint);//绘制路径
	}
	
	
}
