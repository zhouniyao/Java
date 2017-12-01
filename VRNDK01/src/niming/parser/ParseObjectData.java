package niming.parser;

import java.util.ArrayList;


import com.geometric.Face;
import com.geometric.Number3d;
import com.geometric.Uv;

/**
 * OBJ�ļ��е�����Ԫ��
 *
 */
public class ParseObjectData {
	public Face faces;
	
	public ArrayList<Number3d> vertices;//����
	public ArrayList<Uv> texCoords;		//����
	public ArrayList<Number3d> normals; //������
	
	public ParseObjectData()
	{
		this.vertices  = new ArrayList<Number3d>();
		this.texCoords = new ArrayList<Uv>();
		this.normals   = new ArrayList<Number3d>();
		vertices.ensureCapacity(1024);
		texCoords.ensureCapacity(1024);
		normals.ensureCapacity(1024);
		faces = new Face();
	}
	public ParseObjectData(ArrayList<Number3d> vertices, ArrayList<Uv> texCoords, ArrayList<Number3d> normals, Face faces)
	{
		this.vertices = vertices;
		this.texCoords = texCoords;
		this.normals = normals;
		this.faces = faces;
	}
}
