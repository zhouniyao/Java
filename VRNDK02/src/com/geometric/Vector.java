package com.geometric;

import android.util.FloatMath;


public class Vector{
	public final float x, y, z;//向量不同于点，有方向和大小。这里的分量指大小。
	
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
	 * 叉积
	 */
	public Vector crossProduct(Vector other){
		return new Vector(
				(y * other.z) - (z * other.y),
				(z * other.x) - (x * other.z),
				(x * other.y) - (y * other.x));
	}
	/**
	 * 点积，两向量对应坐标乘积之和
	 */
	public float dotProduct(Vector other){
		return x * other.x + y * other.y + z * other.z;
	}
	/**
	 * 向量缩放，方向不变
	 */
	public Vector scale(float f){
		return new Vector(f * x, f * y, f * z);
	}
//向量，既有方向，又有大小。
	public static Vector vectorBetween(Number3d from, Number3d to){
		return new Vector(
				to.x - from.x,
				to.y - from.y,
				to.z - from.z
				);
	}
}