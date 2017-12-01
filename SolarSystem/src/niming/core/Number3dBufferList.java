package niming.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import niming.vos.Number3d;


public class Number3dBufferList
{
	/**
	 * һ��Ԫ�ؿ�ȣ��缸��ֵ
	 */
	public static final int PROPERTIES_PER_ELEMENT = 3;
	/**
	 * ��λֵ������byte��
	 */
	public static final int BYTES_PER_PROPERTY = 4;

	private FloatBuffer _b;
	/**
	 * Ԫ�صĸ���
	 */
	private int _numElements = 0;
	/**
	 * ���ñ����ڴ滺��������������Ԫ�ظ���
	 */
	public Number3dBufferList(FloatBuffer $b, int $size)
	{
		//���������ض���������Ԫ�ص�������������
		ByteBuffer bb = ByteBuffer.allocateDirect($b.limit() * BYTES_PER_PROPERTY); 
		bb.order(ByteOrder.nativeOrder());
		_b = bb.asFloatBuffer();
		_b.put($b);
		_numElements = $size;
	}
	/**
	 * ���������ÿձ����ڴ滺����
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
	 * @return ���� / 3
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
	 * �����������ӱ����ڴ滺�淵��3��float
	 * @param $index
	 * @return new Number3d( _b.get(), _b.get(), _b.get() );
	 */
	public Number3d getAsNumber3d(int $index)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		return new Number3d( _b.get(), _b.get(), _b.get() );
	}
	/**
	 * ����������Ϊָ����Number3d �ӱ����ڴ滺���ж�ȡ3��float
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
	 * �ӱ����ڴ滺���е�ָ��λ�÷���X����
	 * @param $index
	 * @return PropertyX
	 */
	public float getPropertyX (int $index)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		return _b.get();
	}
	/**
	 * �ӱ����ڴ滺���е�ָ��λ�÷���Y����
	 * @param $index
	 * @return PropertyY
	 */
	public float getPropertyY(int $index)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT + 1);
		return _b.get();
	}
	/**
	 * �ӱ����ڴ滺���е�ָ��λ�÷���Z����
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
	 * ָ��λ�ã������ڴ滺������Ԫ��
	 */
	public void set(int $index, Number3d $n)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		_b.put($n.x);
		_b.put($n.y);
		_b.put($n.z);
	}
	/**
	 * ָ��λ�ã�Ԫ�ط���(3float)���ñ����ڴ滺��Ԫ��
	 */
	public void set(int $index, float $x, float $y, float $z)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		_b.put($x);
		_b.put($y);
		_b.put($z);
	}
	/**
	 * ����ָ��λ�õ�X����
	 */
	public void setPropertyX(int $index, float $x)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT);
		_b.put($x);
	}
	/**
	 * ����ָ��λ�õ�Y����
	 */
	public void setPropertyY(int $index, float $y)
	{
		_b.position($index * PROPERTIES_PER_ELEMENT + 1);
		_b.put($y);
	}
	/**
	 * ����ָ��λ�õ�Z����
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
	 * ���±����ڴ滺��
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
	 * ����һģһ����buffers
	 */
	public Number3dBufferList clone()
	{
		_b.position(0);
		Number3dBufferList c = new Number3dBufferList(_b, size());
		return c;
	}
}
