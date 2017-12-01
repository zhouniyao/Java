package niming.beziertest;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
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
/**
 *  
 * 生成贝塞尔曲线-De Casteljau算法
 */
public class BezierGen02 extends View {
	private Paint paint;
    private int centerX, centerY;
    private List<PointF> points;
    private float fraction;
    private List<PointF> destPoints;
    private int count = 0;

	public BezierGen02(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
	}
	//初始化数据点和控制点的位置
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        points = new ArrayList<PointF>();
        //3介测试用例
//        points.add(new PointF(centerX - 150, centerY));
//        points.add(new PointF(centerX - 50, centerY - 300));
//        points.add(new PointF(centerX + 50, centerY + 300));
//        points.add(new PointF(centerX + 150, centerY));
        
        
        points.add(new PointF(centerX - 250, centerY));
        points.add(new PointF(centerX - 300, centerY - 300));
        points.add(new PointF(centerX - 50, centerY - 300));//4介贝塞尔测试用
        points.add(new PointF(centerX + 50, centerY + 300));
        points.add(new PointF(centerX + 150, centerY));
        destPoints = new ArrayList<PointF>();
        destPoints.add(points.get(0));
        startAnim();
    }

    private void startAnim() {
    	ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction = animation.getAnimatedFraction();//取出过渡值fraction [0.0~1.0]
//                Log.i("fraction", ": "+ ++count);//fraction [0.0~1.0],count[1~297]
//                PointF pointF = deCasteljau(fraction, points);//生成贝塞尔曲线-De Casteljau算法
                
                
//				try {
//					//自定义3介贝塞尔曲线
//					PointF pointF = Bezier3(fraction, points);
//					destPoints.add(pointF);
//				} catch (Exception e) {
//					System.exit(0); //关闭当前activity
//				}
//                invalidate();
                
				try {
					//自定义4介贝塞尔曲线
					PointF pointF = Bezier4(fraction, points);
					destPoints.add(pointF);
				} catch (Exception e) {
					System.exit(0); //关闭当前activity
				}
                invalidate();
            }
        });
        animator.start();

	}
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //静态的
        drawPoint(canvas, points, Color.BLACK);
        drawLine(canvas, points, Color.GRAY);
        //动态的
        drawPath(canvas, destPoints);
    }
	
	private void drawPoint(Canvas canvas, List<PointF> data, int color) {
		paint.setColor(color);
        paint.setStrokeWidth(20);
        for (int i = 0; i < data.size(); i++) {
            PointF pointF = data.get(i);
            canvas.drawPoint(pointF.x, pointF.y, paint);
        }
		
	}
	private void drawLine(Canvas canvas, List<PointF> data, int color) {
		paint.setColor(color);
        paint.setStrokeWidth(4);
        for (int i = 0; i < data.size() - 1; i++) {
            PointF start = data.get(i);
            PointF end = data.get(i + 1);
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
        }
	}
	//绘制路径
	private void drawPath(Canvas canvas, List<PointF> data) {
		Path path = new Path();
        PointF start = data.get(0);
        path.moveTo(start.x, start.y);
        for (int i = 1; i < data.size() - 1; i++) {
            PointF point = data.get(i);
            path.lineTo(point.x, point.y);
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        canvas.drawPath(path, paint);

	}

	//深复制
    private List<PointF> copyData(List<PointF> points) {
        List<PointF> data = new ArrayList<PointF>();
        for (int i = 0; i < points.size(); i++) {
            PointF point = points.get(i);
            data.add(new PointF(point.x, point.y));
        }
        return data;
    }

    //De Casteljau算法
    public PointF deCasteljau(float fraction, List<PointF> points) {
        List<PointF> data = copyData(points);
        final int n = data.size();
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < n - i; j++) {
                data.get(j).x = (1 - fraction) * data.get(j).x + fraction * data.get(j + 1).x;//x0 + t * (x1 - x0)
                data.get(j).y = (1 - fraction) * data.get(j).y + fraction * data.get(j + 1).y;
            }
        }
        return data.get(0);
    }

    //3介贝塞尔曲线
    public PointF Bezier3(float fraction, List<PointF> points) throws Exception {
		PointF point = new PointF();//临时点
		if(points.size() == 4){
		    float a1 = (float)Math.pow((1 - fraction), 3);
		    float a2 = (float)Math.pow((1 - fraction), 2) * 3 * fraction;
		    float a3 = 3 * fraction*fraction*(1 - fraction);
		    float a4 = fraction*fraction*fraction;

			point.x = a1 * points.get(0).x + a2 * points.get(1).x + a3 * points.get(2).x + a4 * points.get(3).x;
			point.y = a1 * points.get(0).y + a2 * points.get(1).y + a3 * points.get(2).y + a4 * points.get(3).y;
			
			return point;
		}else{
			throw new Exception("非3介贝塞尔曲线");
//			return null;
		}
	}
    
    //4介贝塞尔曲线
    public PointF Bezier4(float fraction, List<PointF> points) throws Exception {
		PointF point = new PointF();//临时点
		if(points.size() == 5){
		    float a1 = (float)Math.pow((1 - fraction), 4);
		    float a2 = (float)Math.pow((1 - fraction), 3) * 4 * fraction;
		    float a3 = 6 * fraction*fraction*(1 - fraction)*(1 - fraction);
		    float a4 = 4 * fraction*fraction*fraction*(1 - fraction);
		    float a5 = fraction*fraction*fraction*fraction;

			point.x = a1 * points.get(0).x + a2 * points.get(1).x + a3 * points.get(2).x + a4 * points.get(3).x + a5 * points.get(4).x;
			point.y = a1 * points.get(0).y + a2 * points.get(1).y + a3 * points.get(2).y + a4 * points.get(3).y + a5 * points.get(4).y;
			
			return point;
		}else{
			throw new Exception("非4介贝塞尔曲线");
//			return null;
		}
	}
}
