package com.geometric;

/**
 * 点法式定义平面
 */
public class Plane{
	public final Number3d Number3d;
	public final Vector normal;
	

	public Plane(Number3d Number3d2, Vector vector) {
		this.Number3d = Number3d2;
		this.normal = vector;
	}
}