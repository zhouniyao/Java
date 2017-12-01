package niming.interfaces;

import java.util.ArrayList;

import niming.core.Object3d;


/**
 * Using Actionscript 3 nomenclature for what are essentially "pass-thru" methods to an underlying ArrayList  
 * 使用ActionScript 3的命名基本上是“直通”的方法的一个潜在的ArrayList
 */
public interface IObject3dContainer 
{
	/**
	 * 添加Object3d对象
	 * @param Object3d $child
	 */
	public void addChild(Object3d $child);
	/**
	 * 指定位置，添加Object3d对象
	 * @param Object3d $child
	 */
	public void addChildAt(Object3d $child, int $index);
	/**
	 * 移除Object3d对象
	 */
	public boolean removeChild(Object3d $child);
	/**
	 * 移除指定位置的Object3d对象
	 */
	public Object3d removeChildAt(int $index);
	/**
	 * 得到指定位置的Object3d对象
	 */
	public Object3d getChildAt(int $index);
	/**
	 * 按照名称得到Object3d对象
	 */
	public Object3d getChildByName(String $string);
	/**
	 * 返回Object3d对象在***中的位置
	 * @return int Index
	 */
	public int getChildIndexOf(Object3d $o);	
	public int numChildren();
}
