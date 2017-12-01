package niming.parser;

import java.util.ArrayList;


import android.content.Context;
import android.content.res.Resources;

public class Parser {
	public static String TAG = "Obj Load...@author nimig";
	
	protected Context context;
	protected int resourceId;
	public ArrayList<Number3d> vertices;//顶点坐标
	public ArrayList<Uv> texCoords; //纹理坐标
	public ArrayList<Number3d> normals;//顶点法向量
	public ArrayList<Face> faces;//面
	public ArrayList<Byte> vIndex;//面上顶点索引
	public ArrayList<Byte> vtIndex;//面上纹理索引
	public ArrayList<Byte> vnIndex;//面上法向量索引
	
	public Parser() {
		vertices = new ArrayList<Number3d>();
		texCoords = new ArrayList<Uv>();
		normals = new ArrayList<Number3d>();
		faces = new ArrayList<Face>();
		vIndex = new ArrayList<Byte>();
		vtIndex = new ArrayList<Byte>();
		vnIndex = new ArrayList<Byte>();
	}
}
