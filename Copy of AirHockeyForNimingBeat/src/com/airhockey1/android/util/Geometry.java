package com.airhockey1.android.util;

import java.util.Vector;

import com.airhockey1.android.util.Geometry.Plane;
import com.airhockey1.android.util.Geometry.Point;
import com.airhockey1.android.util.Geometry.Ray;

import android.util.FloatMath;



public class Geometry {
	//内部类——点
	public static class Point{
		public final float x, y, z;
		public Point(float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public Point translateY(float distance) {
			return new Point(x, y + distance, z);
		}

		public Point translate(Vector vector) {
			return new Point(x + vector.x, y + vector.y, z + vector.z);
		}
	}
	
	//内部类——圆
	public static class Circle{
		public final Point center;
		public final float radius;
		
		public Circle(Point center, float radius){
			this.center = center;
			this.radius = radius;
		}
		
		public Circle scale(float scale){
			return new Circle(center, scale * radius);
		}
	}
	
	//圆柱体
	public static class Cylinder{
		public final Point center;
		public final float radius;
		public final float height;
		
		public Cylinder(Point center, float radius, float height){
			this.center = center;
			this.radius = radius;
			this.height = height;
		}
	}
	//内部类——Ray
	public static class Ray{
		public final Point point;
		public final Vector vector;
		
		public Ray(Point point, Vector vector){
			this.point = point;
			this.vector = vector;
		}
	}
	//内部类——Vector
	public static class Vector{
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
	}
	//向量，既有方向，又有大小。
	public static Vector vectorBetween(Point from, Point to){
		return new Vector(
				to.x - from.x,
				to.y - from.y,
				to.z - from.z
				);
	}
	
	//内部类——Sphere
	public static class Sphere{
		public final Point center;
		public final float radius;
		
		public Sphere(Point center, float radius) {
			this.center = center;
			this.radius = radius;
		}
	}
	//相交测试
	public static boolean intersects(Sphere sphere, Ray ray){
		return distanceBetween(sphere.center, ray) < sphere.radius;
	}
	//计算球心到射线的距离
	private static float distanceBetween(Point point, Ray ray) {
		Vector p1ToPoint = vectorBetween(ray.point, point);
		Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);
		//The length of the cross product gives the area of an imaginary parallelogram 
		//having the tow vectors as sides. A parallelogram can be
		//thought of as consisting of two triangles, so this is the same as
		//twice the area of the triangle defined by the two vectors.
		//http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
		float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
		float lengthOfBase = ray.vector.length();
		//1)两向量的叉集等于其围成的平行四边形阴影的面积。
		//2)平行四边形的面积 = 底 x 高。
		float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
		return distanceFromPointToRay;
	}
	
	//内部类——平面
	/**
	 * 点法式定义平面
	 */
	public static class Plane{
		public final Point point;
		public final Vector normal;
		

		public Plane(Point point2, Vector vector) {
			this.point = point2;
			this.normal = vector;
		}
	}
	public static Point intersectsPoint(Ray ray, Plane plane) {
		Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);//未通过半场
		/* 为了计算这个缩放量scaleFactor，我们可以用射线到平面(ray-to-plane)的向量与平面法向量的点积 【除以】
		 *  射线向量与平面法向量的点积
		 */
		float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
							/ ray.vector.dotProduct(plane.normal);//scaleFactor 缩放因子
		
		Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
		return intersectionPoint;
	}
}
