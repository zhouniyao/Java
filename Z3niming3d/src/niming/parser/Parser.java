package niming.parser;

import java.util.ArrayList;


import android.content.Context;
import android.content.res.Resources;

public class Parser {
	public static String TAG = "Obj Load...@author nimig";
	
	protected Context context;
	protected int resourceId;
	public ArrayList<Number3d> vertices;//��������
	public ArrayList<Uv> texCoords; //��������
	public ArrayList<Number3d> normals;//���㷨����
	public ArrayList<Face> faces;//��
	public ArrayList<Byte> vIndex;//���϶�������
	public ArrayList<Byte> vtIndex;//������������
	public ArrayList<Byte> vnIndex;//���Ϸ���������
	
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
