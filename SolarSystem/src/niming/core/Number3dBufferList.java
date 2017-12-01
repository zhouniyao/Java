package niming.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import niming.vos.Number3d;


public class Number3dBufferList
{
	/**
	 * 一个元素跨度，跨几个值
	 */
	public static final int PROPERTIES_PER_ELEMENT = 3;
	/**
	 * 单位值包含的byte数
	 */
	public static final int BYTES_PER_PROPERTY = 4;

	private FloatBuffer _b;
	/**
	 * 元素的个数
	 */
	private int _numElements = 0;
	/**
	 * 设置本地内存缓存区，及容量与元素个数
	 */
	public Number3dBufferList(FloatBuffer $b, int $size)
	{
		//缓冲区是特定基本类型元素的线性有限序列
		ByteBuffer bb = ByteBuffer.allocateDirect($b.limit() * BYTES_PER_PROPERTY); 
		bb.order(ByteOrder.nativeOrder());
		_b = bb.asFloatBuffer();
		_b.put($b);
		_numElements = $size;
	}
	/**
	 * 按容量设置空本地内存缓存区
	 * @param int $maxElements
	 */
	public Number3dBufferList(int $maxElements)
	{
		int numBytes = $maxElements * PROPERTIES_PER_ELEMENT * BYTES_PER_PROPERTY;
		ByteBuffer bb = ByteBuffer.allocateDirect(numBytes); 
		bb.order(ByteOrder.nativeOrder());
		
		_b  = bb.asFloatBuffer();
	}
	
	/**
	 * The number of items in the list. 
	 */
	public int size()
	{
		return _numElements;
	}
	
	/**
	 * @return 容量 / 3
	 * The _maximum_ number of items that the list can hold, as defined on instantiation.
	 * (Not to be confused with the Buffer's capacity)
	 */
	public int capacity()
	{
		return _b.capacity() / PROPERTIES_PER_ELEMENT;
	}
	
	/**
	 * Clear object in preparation for garbage collection
	 */
	public void clear()
	{
		_b.clear();
	}
	
	//
	/**
	 * 依据索引，从本地内存缓存返回3个float
	 * @param $index
	 * @return new Number3d( _b.get(), _b.get(), _b.get() );
	 */
	public Number3d getAsNumber3d(int $index)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		return new Number3d( _b.get(), _b.get(), _b.get() );
	}
	/**
	 * 依据索引，为指定的Number3d 从本地内存缓存中读取3个float
	 * @param int $index, Number3d $number3d
	 */
	public void putInNumber3d(int $index, Number3d $number3d)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		$number3d.x = _b.get();
		$number3d.y = _b.get();
		$number3d.z = _b.get();
	}
	/**
	 * 从本地内存缓存中的指定位置返回X分量
	 * @param $index
	 * @return PropertyX
	 */
	public float getPropertyX (int $index)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		return _b.get();
	}
	/**
	 * 从本地内存缓存中的指定位置返回Y分量
	 * @param $index
	 * @return PropertyY
	 */
	public float getPropertyY(int $index)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT + 1);
		return _b.get();
	}
	/**
	 * 从本地内存缓存中的指定位置返回Z分量
	 * @param $index
	 * @return PropertyZ
	 */
	public float getPropertyZ(int $index)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT + 2);
		return _b.get();
	}
	
	//
	
	public void add(Number3d $n)
	{
		set( _numElements, $n );
		_numElements++;
	}
	
	public void add(float $x, float $y, float $z)
	{
		set( _numElements, $x,$y,$z );
		_numElements++;
	}
	
	/**
	 * 指定位置，本地内存缓存设置元素
	 */
	public void set(int $index, Number3d $n)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		_b.put($n.x);
		_b.put($n.y);
		_b.put($n.z);
	}
	/**
	 * 指定位置，元素分量(3float)设置本地内存缓存元素
	 */
	public void set(int $index, float $x, float $y, float $z)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		_b.put($x);
		_b.put($y);
		_b.put($z);
	}
	/**
	 * 设置指定位置的X分量
	 */
	public void setPropertyX(int $index, float $x)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		_b.put($x);
	}
	/**
	 * 设置指定位置的Y分量
	 */
	public void setPropertyY(int $index, float $y)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT + 1);
		_b.put($y);
	}
	/**
	 * 设置指定位置的Z分量
	 */
	public void setPropertyZ(int $index, float $z)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT + 2);
		_b.put($z);
	}
	
	//
	/**
	 * @return FloatBuffer _b
	 */
	public FloatBuffer buffer()
	{
		return _b;
	}
	/**
	 * 重新本地内存缓存
	  	_b.position(0);
		_b.put($newVals);
	 * @param float[]
	 */
	public void overwrite(float[] $newVals)
	{
		_b.position(0);
		_b.put($newVals);
	}
	/**
	 * 拷贝一模一样的buffers
	 */
	public Number3dBufferList clone()
	{
		_b.position(0);
		Number3dBufferList c = new Number3dBufferList(_b, size());
		return c;
	}
}
