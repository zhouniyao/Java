package com.niming.objloader;

public class Vertices3 {
	private static final float[] matrix = new float[16];
	private static final float[] inVec = new float[4];
	private static final float[] outVec = new float[4];
	public float x, y, z;
	
	public Vertices3(){}
	public Vertices3(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vertices3(Vertices3 other){
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	public Vertices3 set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	public void setVertices(float[] verts, int i, int length) {
		
	}
	
}
