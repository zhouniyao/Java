package niming.interfaces;

public interface IDirtyManaged 
{
	/**
	 * @return boolean isDirty()
	 */
	public boolean isDirty();
	/**
	 * void setDirtyFlag()
	 */
	public void setDirtyFlag();
	/**
	 * void clearDirtyFlag()
	 */
	public void clearDirtyFlag();
}
