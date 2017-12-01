package niming.interfaces;

import java.util.ArrayList;

import niming.core.Object3d;


/**
 * Using Actionscript 3 nomenclature for what are essentially "pass-thru" methods to an underlying ArrayList  
 * ʹ��ActionScript 3�������������ǡ�ֱͨ���ķ�����һ��Ǳ�ڵ�ArrayList
 */
public interface IObject3dContainer 
{
	/**
	 * ���Object3d����
	 * @param Object3d $child
	 */
	public void addChild(Object3d $child);
	/**
	 * ָ��λ�ã����Object3d����
	 * @param Object3d $child
	 */
	public void addChildAt(Object3d $child, int $index);
	/**
	 * �Ƴ�Object3d����
	 */
	public boolean removeChild(Object3d $child);
	/**
	 * �Ƴ�ָ��λ�õ�Object3d����
	 */
	public Object3d removeChildAt(int $index);
	/**
	 * �õ�ָ��λ�õ�Object3d����
	 */
	public Object3d getChildAt(int $index);
	/**
	 * �������Ƶõ�Object3d����
	 */
	public Object3d getChildByName(String $string);
	/**
	 * ����Object3d������***�е�λ��
	 * @return int Index
	 */
	public int getChildIndexOf(Object3d $o);	
	public int numChildren();
}
