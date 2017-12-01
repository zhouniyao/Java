package niming.vos;

import niming.interfaces.IDirtyParent;


public class FloatManaged extends AbstractDirtyManaged 
{
	private float _f;
	/**
	 * �����ݣ�������ֵ float _f��ֻ�������һ�� IDirtyParent $parent�������ݹ���
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
