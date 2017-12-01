package niming.beziertest;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawAnimatorTest extends View {
    private Point currentPoint;  
    List<PointF> myData;
    private Paint paint;  
	public DrawAnimatorTest(Context context, AttributeSet attrs) {
		super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        myData = new ArrayList<PointF>();
        paint.setColor(Color.RED);  
//		float[] position = new float[2];//´íÎó
        int[] position = new int[2];
        this.getLocationInWindow(position);
        int x = position[0];
        int y = position[1];
        myData.add(new PointF(x+150, y+150));
        myData.add(new PointF(x+550, y+150));
        myData.add(new PointF(x+850, y+150));
	}
	@Override
	protected void onDraw(Canvas canvas) {
		
		drawPoint(canvas, myData);
		drawPath(canvas, myData);
	}
	
	private void drawPoint(Canvas canvas, List<PointF> data) {
		paint.setStrokeWidth(20);
		paint.setColor(Color.BLACK);
		for(int i = 0; i < data.size(); i++){
			PointF pointF = data.get(i);
			canvas.drawPoint(pointF.x, pointF.y, paint);
		}
	}
	private void drawPath(Canvas canvas, List<PointF> data) {
		Path path = new Path();
		PointF start = data.get(0);
		path.moveTo(start.x, start.y);
		
		Log.i("data.size:" , ""+data.size());//size = 3
		for (int i = 0; i < data.size(); i++) {
			PointF point = data.get(i);
			path.lineTo(point.x, point.y);
		}
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(4);
		canvas.drawPath(path, paint);//»æÖÆÂ·¾¶
	}
}
