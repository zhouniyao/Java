package min3d.vos;

import min3d.interfaces.IDirtyManaged;
import min3d.interfaces.IDirtyParent;


public abstract class AbstractDirtyManaged implements IDirtyManaged
{
	protected IDirtyParent _parent;
	protected boolean _dirty;
	
	public AbstractDirtyManaged(IDirtyParent $parent)
	{
		_parent = $parent;
	}
	
	/**
	 * @return boolean _dirty
	 */
	public boolean isDirty()
	{
		return _dirty;
	}
	/**
	 * set-> boolean _dirty
	 */
	public void setDirtyFlag()
	{
		_dirty = true;
		if (_parent != null) _parent.onDirty();
	}
	/**
	 * set-> _dirty = false
	 */
	public void clearDirtyFlag()
	{
		_dirty = false;
	}
}
