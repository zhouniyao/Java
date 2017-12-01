package com.geometric;

import android.util.FloatMath;


public class Vector{
	public final float x, y, z;//������ͬ�ڵ㣬�з���ʹ�С������ķ���ָ��С��
	
	public Vector(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public float length(){
		return FloatMath.sqrt(x * x + y * y + z * z);
	}
	//http://en.wikipedia.org/wiki/Cross_product
	/**
	 * ���
	 */
	public Vector crossProduct(Vector other){
		return new Vector(
				(y * other.z) - (z * other.y),
				(z * other.x) - (x * other.z),
				(x * other.y) - (y * other.x));
	}
	/**
	 * �������������Ӧ����˻�֮��
	 */
	public float dotProduct(Vector other){
		return x * other.x + y * other.y + z * other.z;
	}
	/**
	 * �������ţ����򲻱�
	 */
	public Vector scale(float f){
		return new Vector(f * x, f * y, f * z);
	}
//���������з������д�С��
	public static Vector vectorBetween(Number3d from, Number3d to){
		return new Vector(
				to.x - from.x,
				to.y - from.y,
				to.z - from.z
				);
	}
}