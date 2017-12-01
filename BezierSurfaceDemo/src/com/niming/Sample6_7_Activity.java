package com.niming;
import com.bn.Sample6_7.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class Sample6_7_Activity extends Activity {
	private MySurfaceView mGLSurfaceView;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����Ϊȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//����Ϊ����
		// ��ʼ��GLSurfaceView
		mGLSurfaceView = new MySurfaceView(this);
		// �л���������
		setContentView(R.layout.main);	
		LinearLayout ll = (LinearLayout) findViewById(R.id.main_liner);
		ll.addView(mGLSurfaceView);
        //��ͨ�������������Ĵ�������
        SeekBar sb=(SeekBar)this.findViewById(R.id.SeekBar01);
        sb.setAlpha(0.05f);
        sb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//�ɹ��������������
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			}
		});
        sb.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener()
            {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mGLSurfaceView.setLightOffset((seekBar.getMax()/2.0f-progress)/(seekBar.getMax()/2.0f)*-4);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {	}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) { }            	
            }
        );
	}

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause(); 
    } 
}