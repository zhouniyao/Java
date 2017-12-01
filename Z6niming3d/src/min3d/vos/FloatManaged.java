package min3d.vos;

import min3d.interfaces.IDirtyParent;



public class FloatManaged extends AbstractDirtyManaged 
{
	private float _f;
	/**
	 * 脏数据，并设置值
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
