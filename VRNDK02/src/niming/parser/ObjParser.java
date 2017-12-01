package niming.parser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import niming.util.TextureHelper;

import com.geometric.Color4;
import com.geometric.Face;
import com.geometric.NMIndex;
import com.geometric.Number3d;
import com.geometric.Uv;


import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;


public class ObjParser{
	private final String VERTEX = "v";
	private final String FACE = "f";
	private final String TEXCOORD = "vt";
	private final String NORMAL = "vn";
	private final String OBJECT = "o";
	private final String MATERIAL_LIB = "mtllib";
	private final String USE_MATERIAL = "usemtl";
	private final String NEW_MATERIAL = "newmtl";
	private final String DIFFUSE_COLOR = "Kd";
	private final String DIFFUSE_TEX_MAP = "map_Kd";
	
	//计数
	public int normalsNum;//法向量总共有多少种
	public int UVCoordsNum;//纹理总共有多少种
	public int verticesNum;//顶点总共有多少种
	public int facesNum ;//三角面的个数
	
	protected String currentMaterialKey;
	protected Context context;
	protected Resources resources;
	protected String resourceID;
	protected boolean generateMipMap;
//	public int textureID;
	public int bmResourceID;//纹理资源在R.java中的定位
	
	protected HashMap<String, Material> materialMap;
	
	ParseObjectData od;//封装_模型所有数据
	
//	protected ArrayList<Face> faces = new ArrayList<Face>();
//	protected int facesNum = 0;
//	protected ArrayList<Number3d> vertices = new ArrayList<Number3d>();//顶点
//	protected ArrayList<Uv> texCoords = new ArrayList<Uv>();		   //纹理
//	protected ArrayList<Number3d> normals = new ArrayList<Number3d>(); //法向量
	
	public String name;
	
	/**
	 * Construction
	 */
	public ObjParser(){
		od = new ParseObjectData();
		materialMap = new HashMap<String, Material>();
		normalsNum  = 0;
		UVCoordsNum = 0;
		verticesNum = 0;
		facesNum    = 0;
	}
	public ObjParser(Context context, String resourceID, boolean generateMipMap){
		this();
		this.context = context;
		this.resources = context.getResources();
		this.resourceID = resourceID;
		this.generateMipMap = generateMipMap;
	}
	/**
	 * 
	 */
	public void parse(){
		long startTime = Calendar.getInstance().getTimeInMillis();//Record Start Time
		InputStream file = this.resources.openRawResource(resources.getIdentifier(resourceID, null, null));
		BufferedReader buffer = new BufferedReader(new InputStreamReader(file));
		String line;//读取字符流一行
		Log.i("ObjParser ", "Start time: " + startTime);
		
		try {
			while((line = buffer.readLine()) != null){
				String[] tokens = line.split("[ ]+");
				if(tokens[0].equals(VERTEX)){
					Number3d vertex = new Number3d();
					//tokens[0]是"v"
					vertex.x = Float.valueOf(tokens[1]);
					vertex.y = Float.valueOf(tokens[2]);
					vertex.z = Float.valueOf(tokens[3]);
					od.vertices.add(vertex);
					verticesNum++;
				}else if(tokens[0].equals(FACE)){
					int c = tokens.length;
					/*顶点索引/uv点索引/法线索引*/
					if (tokens[1].matches("[0-9]+"))
					{
						caseFEqOne(tokens, c);
					}
					if (tokens[1].matches("[0-9]+/[0-9]+"))
					{
						caseFEqTwo(tokens, c);
					}
					if (tokens[1].matches("[0-9]+//[0-9]+"))
					{
						caseFEqOneAndThree(tokens, c);
					}
					if (tokens[1].matches("[0-9]+/[0-9]+/[0-9]+"))
					{
						caseFEqThree(tokens, c);
					}
				}else if(tokens[0].equals(TEXCOORD)){
					int c = tokens.length;
					Uv uv = new Uv();
					uv.u = Float.valueOf(tokens[1]);
					uv.v = Float.valueOf(tokens[2]);
					
					od.texCoords.add(uv);
					UVCoordsNum++;
				}else if(tokens[0].equals(NORMAL)){
					Number3d vertex = new Number3d();
					vertex.x = Float.valueOf(tokens[1]);
					vertex.y = Float.valueOf(tokens[2]);
					vertex.z = Float.valueOf(tokens[3]);
					od.normals.add(vertex);
					normalsNum++;
				}else if(tokens[0].equals(MATERIAL_LIB)){//mtllib
					readMaterialLib(tokens[1]);
				}else if(tokens[0].equals(USE_MATERIAL)){//usemtl  使用材质文件
					currentMaterialKey = tokens[1];
				}else if(tokens[0].equals(OBJECT)){//o
					//TODO
				}
//				else if(tokens[0].equals(NEW_MATERIAL)){
//					//TODO
//				}else if(tokens[0].equals(DIFFUSE_COLOR)){
//					//TODO
//				}else if(tokens[0].equals(DIFFUSE_TEX_MAP)){
//					//TODO
//				}
				
			}//End while
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.d("OBJ", "File read complete, " + od.vertices.size() + " vertices read.");
	}
	

	/**
	 * 最全面的一种面处理（"[0-9]+/[0-9]+/[0-9]+"）
	 */
	private void caseFEqThree(String[] tokens, int c) {
		int s;
		if(c == 4){
			for (int i = 1; i < c; i++)//跳过f 关键字
			{
				String[] parts = tokens[i].split("/");/*分解：111/222/333  成为：parts = {111, 222, 333}*/
//				Log.v("c4", "" + tokens[i]);
//				Log.i("单个", parts[0] + "   " + parts[1] + "   " + parts[2]);
				
				s = Integer.valueOf(parts[0]);
				s--;
				od.faces.vSeq.add(s);
				
				s = Integer.valueOf(parts[1]);
				s--;
				od.faces.uvSeq.add(s);
				
				s = Integer.valueOf(parts[2]);
				s--;
				od.faces.nSeq.add(s);
			}
			facesNum ++;
		}
		if(c == 5){
//			Log.v(TAG, "c5");
//			Integer[] first = new Integer[3];
			for (int i = 1; i < c-1; i++)
			{
				String[] parts = tokens[i].split("/");
				
				s = Integer.valueOf(parts[0]);
				s--;
				od.faces.vSeq.add(s);
				
				s = Integer.valueOf(parts[1]);
				s--;
				od.faces.uvSeq.add(s);
				
				s = Integer.valueOf(parts[2]);
				s--;
				od.faces.nSeq.add(s);
			}
			
			for (int i = 1; i < c; i++)
			{
				if(i == 2){continue;}
				String[] parts = tokens[i].split("/");
				
				s = Integer.valueOf(parts[0]);
				s--;
				od.faces.vSeq.add(s);
				
				s = Integer.valueOf(parts[1]);
				s--;
				od.faces.uvSeq.add(s);
				
				s = Integer.valueOf(parts[2]);
				s--;
				od.faces.nSeq.add(s);
			}
			facesNum += 2;
			
		}
	}
	private void caseFEqOneAndThree(String[] tokens, int c) {
		//TODO
	}
	/*111/222情形*/
	private void caseFEqTwo(String[] tokens, int c) {
		int s;
			for (int i = 1; i < c; i++)//跳过f 关键字
			{
				String[] parts = tokens[i].split("/");/*分解：111/222  成为：parts = {111, 222}*/
				
				s = Integer.valueOf(parts[0]);
				s--;
				od.faces.vSeq.add(s);
				
				s = Integer.valueOf(parts[1]);
				s--;
				od.faces.uvSeq.add(s);
				
			}
			facesNum ++;
	}
	/*111 仅有顶点序列*/
	private void caseFEqOne(String[] tokens, int c) {
		int s;
		for (int i = 1; i < c; i++)//跳过f 关键字
		{
//			String[] parts = tokens[i].split("/");/*分解：111/222  成为：parts = {111, 222}*/
			
			s = Integer.valueOf(tokens[i]);
			s--;
			od.faces.vSeq.add(s);
		}
		facesNum ++;
	}
	
	/**
	 * 
	 * @param Tokens
	 */
	private void readMaterialLib(String Tokens) {
		String packageID = "";
		if (resourceID.indexOf(":") > -1)
			packageID = resourceID.split(":")[0];
		
		StringBuffer resourceID = new StringBuffer(packageID);
		StringBuffer libIDSbuf = new StringBuffer(Tokens);
		int dotIndex = libIDSbuf.lastIndexOf(".");
		if (dotIndex > -1)
			libIDSbuf = libIDSbuf.replace(dotIndex, dotIndex + 1, "_");
		/*生成MaterialLib文件的目录*/
		resourceID.append(":raw/");
		resourceID.append(libIDSbuf.toString());
		
		Log.i("包名2", resourceID.toString());
		/*Parser*.mtl文件*/
		InputStream fileIn = resources.openRawResource(resources.getIdentifier(
				resourceID.toString(), null, null));
		BufferedReader buffer = new BufferedReader(
				new InputStreamReader(fileIn));
		String line;
		String currentMaterial = "";

		try {
			while ((line = buffer.readLine()) != null) {
				String[] parts = line.split(" ");
				if (parts.length == 0)//空行跳过
					continue;
				String type = parts[0];//行头——关键字
				if (type.equals(NEW_MATERIAL)) {//new_mtl 行
					if (parts.length > 1) {
						currentMaterial = parts[1];
						materialMap.put(currentMaterial, new Material(
								currentMaterial));
					}
				} else if(type.equals(DIFFUSE_COLOR) && !type.equals(DIFFUSE_TEX_MAP)) {//Kd or !map_Kd
					Color4 diffuseColor = new Color4(Float.parseFloat(parts[1]) * 255.0f,
													 Float.parseFloat(parts[2]) * 255.0f,
													 Float.parseFloat(parts[3]) * 255.0f, 
													 255.0f);
					materialMap.get(currentMaterial).diffuseColor = diffuseColor;//漫反射颜色
					
				}else if(type.equals("Ka")){
//					xxxxxxxxxxxxx
				}else if(type.equals("Ks")){
//					xxxxxxxxxxxxxx
				}				
				else if (type.equals(DIFFUSE_TEX_MAP)) {//图片贴图map_Kd
					if (parts.length > 1) {
						materialMap.get(currentMaterial).diffuseTextureMap = parts[1];//MaterialLib文件名
						StringBuffer texture = new StringBuffer(packageID);
						texture.append(":drawable/");
						//生成纹理贴图的文件全名
						StringBuffer textureName = new StringBuffer(parts[1]);
						dotIndex = textureName.lastIndexOf(".");
						if (dotIndex > -1)
							texture.append(textureName.substring(0, dotIndex));
						else
							texture.append(textureName);
						
						bmResourceID = resources.getIdentifier(texture
								.toString(), null, null);
						
//						Log.i("纹理资源ID数值：", bmResourceID+"");
//						Log.i("纹理资源ID", texture.toString());
//						System.out.println("打印纹理R.java值");
//						System.out.printf("%x", bmResourceID);//16进制输出
//						/*加载纹理贴图*///在渲染器renderer中加载纹理对象
//						textureID = TextureHelper.loadTexture(context, bmResourceID, generateMipMap);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ParseObjectData getObjData(){
		return od;
	}
	
}
class Material {
	public String name;
	public String diffuseTextureMap;
	public Color4 diffuseColor;

	public Material(String name) {
		this.name = name;
	}
}
