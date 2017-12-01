package niming.vos;

import java.nio.FloatBuffer;

import niming.interfaces.IDirtyParent;
import niming.parent.Utils;


/**
 * Light must be added to Scene to take effect.
 * 
 * Eg, "scene.lights().add(myLight);"  
 */
public class Light extends AbstractDirtyManaged implements IDirtyParent
{
	/**
	 * Position is relative to eye space, not world space.
	 */
	public Number3dManaged 		position;
	
	/**
	 * Direction is a vector and should be normalized.
	 */
	public Number3dManaged 		direction; //方向 
	
	public Color4Managed 		ambient;//环境光
	public Color4Managed 		diffuse;//漫反射
	public Color4Managed 		specular;//镜面反射
	public Color4Managed 		emissive;//发射，点光
	
	private LightType			_type;

	public Light()
	{
		super(null);
		
		 ambient = new Color4Managed(128,128,128, 255, this);
		 diffuse = new Color4Managed(255,255,255, 255, this);
		 specular = new Color4Managed(0,0,0,255, this);
		 emissive = new Color4Managed(0,0,0,255, this);
		 
		 position = new Number3dManaged(0f, 0f, 1f, this);//z轴正1，屏幕外一点 			
		 
		 direction = new Number3dManaged(0f, 0f, -1f, this);//z轴负1，这是方向向量	
		 _spotCutoffAngle = new FloatManaged(180, this);//指定光源的最大散布角，它是光源发射光线所覆盖角度的一半，取值180，表示覆盖360，即它不是聚光灯，而是向周围都发射光线 	
		 _spotExponent = new FloatManaged(0f, this);	//指定聚焦光源指数		
		 
		 _attenuation = new Number3dManaged(1f,0f,0f, this); //变薄; 弄细; 稀薄化; 减少;	
		 
		 _type = LightType.DIRECTIONAL; //平行光线								
		 
		 _isVisible = new BooleanManaged(true, this);
		 
		 _positionAndTypeBuffer = Utils.makeFloatBuffer4(0,0,0,0);//位置存入FloatBuffer
		 
		 setDirtyFlag();
	}
	/**
	 * @return _isVisible.get();
	 */
	public boolean isVisible()
	{
		return _isVisible.get();
	}
	/**
	 * @param _isVisible.set($b);
	 */
	public void isVisible(Boolean $b)
	{
		_isVisible.set($b);
	}
	
	/**
	 * Default is DIRECTIONAL, matching OpenGL's default value.
	 */
	public LightType type()
	{
		return _type;
	}
	/**
	 * 设置 LightType _type
	 * @param LightType $type
	 */
	public void type(LightType $type)
	{
		_type = $type;
		position.setDirtyFlag(); // .. position and type share same data structure
	}
	
	/**
	 * 返回 聚焦光源指数
	 * 0 = no attenuation towards edges of spotlight. Max is 128.
	 * Default is 0, matching OpenGL's default value.
	 */
	public float spotExponent()
	{
		return _spotExponent.get();
	}
	/**
	 * 设置 聚焦光源指数
	 * @param Float $f
	 */
	public void spotExponent(float $f)
	{
		if ($f < 0) $f = 0;
		if ($f > 128) $f = 128;
		_spotExponent.set($f);
	}
	
	/**
	 * 返回 光源的最大散布角。
	 * Legal range is 0 to 90, plus 180, which is treated by OpenGL to mean no cutoff（中止）.
	 * Default is 180, matching OpenGL's default value.
	 */
	public float spotCutoffAngle()
	{
		return _spotCutoffAngle.get();
	}
	/**
	 * 设置 光源的最大散布角
	 * @param Float $f
	 */
	public void spotCutoffAngle(Float $f)
	{
		if ($f < 0) 
			_spotCutoffAngle.set(0);
		else if ($f <= 90)
			_spotCutoffAngle.set($f);
		else if ($f == 180)
			_spotCutoffAngle.set($f);
		else
			_spotCutoffAngle.set(90);
	}
	
	/**
	 * No cutoff angle (ie, no spotlight effect)
	 * (represented internally with a value of 180)
	 */
	public void spotCutoffAngleNone()
	{
		_spotCutoffAngle.set(180);
	}
	
	public float attenuationConstant()
	{
		return _attenuation.getX();
	}
	public void attenuationConstant(float $normalizedValue)
	{
		_attenuation.setX($normalizedValue);
		setDirtyFlag();
	}
	
	public float attenuationLinear()
	{
		return _attenuation.getY();
	}
	public void attenuationLinear(float $normalizedValue)
	{
		_attenuation.setY($normalizedValue);
		setDirtyFlag();
	}
	
	public float attenuationQuadratic()
	{
		return _attenuation.getZ();
	}
	public void attenuationQuadratic(float $normalizedValue)
	{
		_attenuation.setZ($normalizedValue);
		setDirtyFlag();
	}
	
	/**
	 * Defaults are 1,0,0 (resulting in no attenuation over distance), 
	 * which match OpenGL default values. 
	 */
	public void attenuationSetAll(float $constant, float $linear, float $quadratic)
	{
		_attenuation.setAll($constant, $linear, $quadratic);
		setDirtyFlag();
	}

	//
	
	public void setAllDirty()
	{
		position.setDirtyFlag();
		ambient.setDirtyFlag();
		diffuse.setDirtyFlag();
		specular.setDirtyFlag();
		emissive.setDirtyFlag();
		direction.setDirtyFlag();
		_spotCutoffAngle.setDirtyFlag();
		_spotExponent.setDirtyFlag();
		_attenuation.setDirtyFlag();
		_isVisible.setDirtyFlag();
	}

	public void onDirty()
	{
		setDirtyFlag();
	}
	
	/**
	 * Used by Renderer
	 * Normal clients of this class should use "isVisible" getter/setter.
	 */
	public BooleanManaged _isVisible;

	/**
	 * Used by Renderer
	 */
	public FloatBuffer _positionAndTypeBuffer;

	/**
	 * Used by Renderer
	 */
	public void commitPositionAndTypeBuffer()
	{
		// GL_POSITION takes 4 arguments, the first 3 being x/y/z position, 
		// and the 4th being what we're calling 'type' (positional or directional)
		
		_positionAndTypeBuffer.position(0);
		_positionAndTypeBuffer.put(position.getX());
		_positionAndTypeBuffer.put(position.getY());
		_positionAndTypeBuffer.put(position.getZ());
		_positionAndTypeBuffer.put(_type.glValue());
		_positionAndTypeBuffer.position(0);
	}
	
	/**
	 * Used by Renderer. 指定聚焦光源指数		
	 * Normal clients of this class should use "useSpotProperties" getter/setter.
	 */
	public FloatManaged _spotExponent; 

	/**
	 * 指定光源的最大散布角，它是光源发射光线所覆盖角度的一半，取值180，表示覆盖360，即它不是聚光灯，而是向周围都发射光线 	
	 * Used by Renderer. Normal clients of this class should use "useSpotProperties" getter/setter.
	 */
	public FloatManaged _spotCutoffAngle;
	
	/**
	 * 变薄，稀薄化
	 * Used by Renderer. Normal clients of this class should use attenuation getter/setters.
	 */
	public Number3dManaged _attenuation; // (the 3 properties of N3D used for the 3 attenuation properties)
}
