package com.geometric;
/**
 * ÆÁÄ»µÄ¶þÎ¬xy×ø±ê 
 *
 */
public class PointXY {
	private float x, y;
	public PointXY() {
		// TODO Auto-generated constructor stub
	}
	public PointXY(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public PointXY(PointXY p){
		this.x = p.getX();
		this.y = p.getY();
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
}
