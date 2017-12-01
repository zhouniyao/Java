package com.airhockey1.android.util;

import java.util.Vector;

import com.airhockey1.android.util.Geometry.Plane;
import com.airhockey1.android.util.Geometry.Point;
import com.airhockey1.android.util.Geometry.Ray;

import android.util.FloatMath;



public class Geometry {
	//�ڲ��ࡪ����
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
	
	//�ڲ��ࡪ��Բ
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
	
	//Բ����
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
	//�ڲ��ࡪ��Ray
	public static class Ray{
		public final Point point;
		public final Vector vector;
		
		public Ray(Point point, Vector vector){
			this.point = point;
			this.vector = vector;
		}
	}
	//�ڲ��ࡪ��Vector
	public static class Vector{
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
	}
	//���������з������д�С��
	public static Vector vectorBetween(Point from, Point to){
		return new Vector(
				to.x - from.x,
				to.y - from.y,
				to.z - from.z
				);
	}
	
	//�ڲ��ࡪ��Sphere
	public static class Sphere{
		public final Point center;
		public final float radius;
		
		public Sphere(Point center, float radius) {
			this.center = center;
			this.radius = radius;
		}
	}
	//�ཻ����
	public static boolean intersects(Sphere sphere, Ray ray){
		return distanceBetween(sphere.center, ray) < sphere.radius;
	}
	//�������ĵ����ߵľ���
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
		//1)�������Ĳ漯������Χ�ɵ�ƽ���ı�����Ӱ�������
		//2)ƽ���ı��ε���� = �� x �ߡ�
		float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
		return distanceFromPointToRay;
	}
	
	//�ڲ��ࡪ��ƽ��
	/**
	 * �㷨ʽ����ƽ��
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
		Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);//δͨ���볡
		/* Ϊ�˼������������scaleFactor�����ǿ��������ߵ�ƽ��(ray-to-plane)��������ƽ�淨�����ĵ�� �����ԡ�
		 *  ����������ƽ�淨�����ĵ��
		 */
		float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
							/ ray.vector.dotProduct(plane.normal);//scaleFactor ��������
		
		Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
		return intersectionPoint;
	}
}
