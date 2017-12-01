package com.niming.util;

import java.util.HashMap;
import java.util.Map;

import com.niming.zfacedetect.R;

import android.R.integer;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/*
 * 声音加载需要【一段时间】，虽然通过staticInstance静态对象引用，但是始终没有声音，暂时未用
 */
public class SoundUtil {
	/*
		 创建一个SoundPool类实例，（构造函数）方法为public SoundPool(int maxStream, int streamType, int srcQuality)。三个参数的含义如下：
	　　maxStream——同时播放的流的最大个数；
	　　streamType——流的类型（一般为STREAM_MUSIC，具体定义见在AudioManager类）；
	　　srcQuality——采样率转化质量，使用0作为默认值（目前设置并无效果）；
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
	
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);//容纳10个声音
		soundMap = new HashMap<Integer, Integer>();
	}
	

	public int getMapValue(int key) {
		return staticInstance.soundMap.get(key);
	}
	public  void playSound(int key, int loop) {
		staticInstance.soundPool.play(getMapValue(key), 1, 1, 0, loop, 1);
	}
	//默认播放一次
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
