package com.example.diybutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * һ���Զ��忪�ذ�ť(����)������set����Ľӿ�
 * ��һ��������ӿڣ��ӿ������и� ���õ���ļ���
 * �ڶ������ڵ�ǰ���ж���һ���ӿڵı������Ա����������ʵ�ֽӿں󴫹����Ľӿ�
 * ������������ǰ����дһ�� public�����ýӿڵķ���������������д�Ľӿ�
 * ���Ĳ��������ǵ���Ҫ�ĵط�ִ��������Ҫ��������������
 */
public class DiyToggle extends View{
	
	private Bitmap mBackground;
    // �ڶ������ڵ�ǰ���ж���һ���ӿڵı������Ա����������ʵ�ֽӿں󴫹����Ľӿ�
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
    	//������
    	if(mBackground != null){
    		canvas.drawBitmap(mBackground, 0, 0, null);
    	}
    }
    //����������ǰ����дһ��public�����ýӿڵķ���������������д�Ľӿ�
    //����������һ��������setToggleClickListener���ͱ���ʵ������д�Ľӿ�
    public void setToggleClickListener(OnToggleClickListener onToggleClickListener2){
    	if(onToggleClickListener2 != null){
    		this.onToggleClickListener = onToggleClickListener2;
    	}
    }
 // ���Ĳ��������ǵ���Ҫ�ĵط�ִ��������Ҫ��������������
    // �������Ǹ�д��ϵͳ�Դ���onTouchEvent��up������Ҳ���ǵ�������ɿ���ָʱִ��
    // �������ɿ���ָ�󣬾͵��������Լ��ӿڵ�  onDoClick������������ʲô�����ǲ�֪����Ҳ����֪��
    // ������˵���������ͺá�
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action==MotionEvent.ACTION_UP){
            onToggleClickListener.onDoClick();
        }
        return true;
    }
    
}
