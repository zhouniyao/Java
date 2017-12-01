package niming.vos;

import java.nio.FloatBuffer;

import niming.interfaces.IDirtyParent;
import niming.parent.Utils;

import android.util.Log;


/**
 * 'Managed' version of Number3d VO 
 */
public class Number3dManaged extends AbstractDirtyManaged 
{
	private float _x;
	private float _y;
	private float _z;
	
	private FloatBuffer _fb;
	/**
	 * x,y,z为0
	 * @param IDirtyParent $parent
	 */
	public Number3dManaged(IDirtyParent $parent)
	{
		super($parent);
		_x = 0;
		_y = 0;
		_z = 0;
		_fb = this.toFloatBuffer();
		setDirtyFlag();
	}
	/**
	 * x,y,z赋值
	 * @param float $x, float $y, float $z,
	 * @param IDirtyParent $parent
	 */
	public Number3dManaged(float $x, float $y, float $z, IDirtyParent $parent)
	{
		super($parent);
		_x = $x;
		_y = $y;
		_z = $z;
		_fb = this.toFloatBuffer();
		setDirtyFlag();
	}
	
	public float getX() {
		return _x;
	}

	public void setX(float x) {
		_x = x;
		setDirtyFlag();
	}

	public float getY() {
		return _y;
	}

	public void setY(float y) {
		_y = y;
		setDirtyFlag();
	}

	public float getZ() {
		return _z;
	}

	public void setZ(float z) {
		_z = z;
		setDirtyFlag();
	}
	/**
	 * 设置 x,y,z
	 * @param $x
	 * @param $y
	 * @param $z
	 */
	public void setAll(float $x, float $y, float $z)
	{
		_x = $x;
		_y = $y;
		_z = $z;
		setDirtyFlag();
	}
	/**
	 * 设置 x,y,z
	 * @param Number3d $n
	 */
	public void setAllFrom(Number3d $n)
	{
		_x = $n.x;
		_y = $n.y;
		_z = $n.z;
		setDirtyFlag();
	}

	public void setAllFrom(Number3dManaged $n)
	{
		_x = $n.getX();
		_y = $n.getY();
		_z = $n.getZ();
		setDirtyFlag();
	}

	public Number3d toNumber3d()
	{
		return new Number3d(_x,_y,_z);
	}
	
	@Override
	public String toString()
	{
		return _x + "," + _y + "," + _z; 
	}
	
	/**
	 * @return Utils.makeFloatBuffer3(_x, _y, _z);
	 */
	public FloatBuffer toFloatBuffer()
	{
		return Utils.makeFloatBuffer3(_x, _y, _z);
	}
	/**
	 * 	$floatBuffer.position(0);
		$floatBuffer.put(_x);
		$floatBuffer.put(_y);
		$floatBuffer.put(_z);
		$floatBuffer.position(0);
	 * @param FloatBuffer $floatBuffer
	 */
	public void toFloatBuffer(FloatBuffer $floatBuffer)
	{
		$floatBuffer.position(0);
		$floatBuffer.put(_x);
		$floatBuffer.put(_y);
		$floatBuffer.put(_z);
		$floatBuffer.position(0);
	}

	/**
	 * Used by Renderer
	 * @return FloatBuffer fb
	 */
	public FloatBuffer floatBuffer()
	{
		return _fb;
	}

	/**
	 * Used by Renderer
	 * @see toFloatBuffer(_fb);
	 */
	public void commitToFloatBuffer()
	{
		toFloatBuffer(_fb);
	}
}
