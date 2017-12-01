package com.example.diybutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 一个自定义开关按钮(假设)，附带set点击的接口
 * 第一步，定义接口，接口里面有个 设置点击的监听
 * 第二步，在当前类中定义一个接口的变量，以便接受其他类实现接口后传过来的接口
 * 第三步，当当前类中写一个 public的设置接口的方法，参数是我们写的接口
 * 第四步，在我们的需要的地方执行其他类要求我们做的事情
 */
public class DiyToggle extends View{
	
	private Bitmap mBackground;
    // 第二步，在当前类中定义一个接口的变量，以便接受其他类实现接口后传过来的接口
	private OnToggleClickListener onToggleClickListener;
	public DiyToggle(Context context) {
		super(context);
	}
	public DiyToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiyToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setBackground(int resourceId){
    	mBackground = BitmapFactory.decodeResource(getResources(), resourceId);
    }
    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
    	int measuredWidth = mBackground.getWidth();
        int measuredHeight = mBackground.getHeight();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }
    protected void onDraw(Canvas canvas){
    	//画背景
    	if(mBackground != null){
    		canvas.drawBitmap(mBackground, 0, 0, null);
    	}
    }
    //第三步，当前类中写一个public的设置接口的方法，参数是我们写的接口
    //这样其他类一旦调用了setToggleClickListener，就必须实现我们写的接口
    public void setToggleClickListener(OnToggleClickListener onToggleClickListener2){
    	if(onToggleClickListener2 != null){
    		this.onToggleClickListener = onToggleClickListener2;
    	}
    }
 // 第四步，在我们的需要的地方执行其他类要求我们做的事情
    // 这里我们复写了系统自带的onTouchEvent的up方法，也就是当点击后松开手指时执行
    // 我们在松开手指后，就调用我们自己接口的  onDoClick方法，具体做什么，我们不知道，也不用知道
    // 其他人说，我们做就好。
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action==MotionEvent.ACTION_UP){
            onToggleClickListener.onDoClick();
        }
        return true;
    }
    
}
