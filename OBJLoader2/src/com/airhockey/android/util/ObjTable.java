package com.airhockey.android.util;

public class ObjTable {
	
	private float[] vertices;//顶点坐标数组
	private float[] normals;//顶点法向量坐标数组
	private float[] uv;//纹理坐标数组
	
	private int numVertices;//顶点数
	private int numNormals;//法向量数
	private int numUV;//纹理数
	private int numFaces;//三角形面数
	
	private int[] facesVerts;//面顶点索引
	private int[] facesNormals;//面法向量索引
	private int[] facesUV;//面纹理索引
	
	private int vertexIndex;//面顶点索引大小
	private int normalIndex;//面法向量索引大小
	private int uvIndex;//面纹理索引大小
	private int faceIndex;//面 索引大小
	
	public ObjTable(){
		
	}
	public ObjTable(){
		
	}
}
