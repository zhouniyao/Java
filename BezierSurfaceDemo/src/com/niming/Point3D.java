package com.niming;

public class Point3D{
	public float x, y, z;
	public void setXYZ(double x, double y, double z) {
		this.x = (float)x+5;
		this.y = (float)y+5;
		this.z = (float)z+5;
	}
	public void setXYZ(float x, float y, float z) {
		this.x = x+5;
		this.y = y+5;
		this.z = z+5;
	}
}