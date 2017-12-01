package com.niming.parser;

import java.util.ArrayList;

import com.geometric.Face;
import com.geometric.Number3d;
import com.geometric.Uv;

public class ParseObjectData {
	protected ArrayList<Face> faces;
	protected int numFaces = 0;
	protected ArrayList<Number3d> vertices;//����
	protected ArrayList<Uv> texCoords;		   //����
	protected ArrayList<Number3d> normals; //������
	
	public ParseObjectData()
	{
		this.vertices = new ArrayList<Number3d>();
		this.texCoords = new ArrayList<Uv>();
		this.normals = new ArrayList<Number3d>();
		this.faces = new ArrayList<Face>();
	}
	public ParseObjectData(ArrayList<Number3d> vertices, ArrayList<Uv> texCoords, ArrayList<Number3d> normals)
	{
		this.vertices = vertices;
		this.texCoords = texCoords;
		this.normals = normals;
		
		this.faces = new ArrayList<Face>();
	}
}
