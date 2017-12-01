package niming.vos;

import niming.interfaces.IDirtyParent;


public class FloatManaged extends AbstractDirtyManaged 
{
	private float _f;
	/**
	 * 脏数据，并设置值 float _f，只是添加了一个 IDirtyParent $parent【脏数据管理】
	 * @param float $value
	 * @param IDirtyParent $parent
	 */
	public FloatManaged(float $value, IDirtyParent $parent)
	{
		super($parent);
		set($value);
	}
	
	public float get()
	{
		return _f;
	}
	public void set(float $f)
	{
		_f = $f;
		setDirtyFlag();
	}
}
