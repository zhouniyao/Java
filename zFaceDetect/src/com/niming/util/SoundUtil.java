package com.niming.util;

import java.util.HashMap;
import java.util.Map;

import com.niming.zfacedetect.R;

import android.R.integer;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/*
 * ����������Ҫ��һ��ʱ�䡿����Ȼͨ��staticInstance��̬�������ã�����ʼ��û����������ʱδ��
 */
public class SoundUtil {
	/*
		 ����һ��SoundPool��ʵ���������캯��������Ϊpublic SoundPool(int maxStream, int streamType, int srcQuality)�����������ĺ������£�
	����maxStream����ͬʱ���ŵ�������������
	����streamType�����������ͣ�һ��ΪSTREAM_MUSIC�����嶨�����AudioManager�ࣩ��
	����srcQuality����������ת��������ʹ��0��ΪĬ��ֵ��Ŀǰ���ò���Ч������
	 */
	public  Map<Integer, Integer> soundMap;
	private  SoundPool soundPool; 
	public  Context context;
	public static final int CAMERA =0;
	public static final int NOPICTURE =1;
	public static final int PICTURE =2;
	public static final int SELF_CAMERA =3;
	public static SoundUtil staticInstance; 
	
	private SoundUtil(Context context) {
		this.context = context;
	
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);//����10������
		soundMap = new HashMap<Integer, Integer>();
	}
	

	public int getMapValue(int key) {
		return staticInstance.soundMap.get(key);
	}
	public  void playSound(int key, int loop) {
		staticInstance.soundPool.play(getMapValue(key), 1, 1, 0, loop, 1);
	}
	//Ĭ�ϲ���һ��
	public void playSound(int key) {
		playSound(key, 0);
	}
	
	public static SoundUtil getInstance(Context context){
		staticInstance = new SoundUtil(context);
		staticInstance.soundMap.put(CAMERA, staticInstance.soundPool.load(context, R.raw.camera, 1));
		staticInstance.soundMap.put(NOPICTURE, staticInstance.soundPool.load(context, R.raw.nopicture, 1));
		staticInstance.soundMap.put(PICTURE, staticInstance.soundPool.load(context, R.raw.picture, 1));
		staticInstance.soundMap.put(SELF_CAMERA, staticInstance.soundPool.load(context, R.raw.self_camera, 1));
		return staticInstance;
	}
}
