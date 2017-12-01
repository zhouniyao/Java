package niming.vos;

import niming.interfaces.IDirtyManaged;
import niming.interfaces.IDirtyParent;


/**
 * 	protected IDirtyParent _parent;
	protected boolean _dirty;
 */
public abstract class AbstractDirtyManaged implements IDirtyManaged
{
	protected IDirtyParent _parent;
	protected boolean _dirty;
	/**
	 * @param IDirtyParent $parent
	 */
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
	 * set-> boolean _dirty = true
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
