package niming.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ParseObj extends Parser {
	
	//计数
	public int facesNum = 0;
	public int normalsNum = 0;
	public int UVCoordsNum = 0;
	public int verticesNum = 0;
	public int vIndexNum = 0;
	//构造函数
	public ParseObj(Context context, int resourceId) {
		this.resourceId = resourceId;
		this.context = context;
		
	}

	/**
	 * 处理纹理行
	 */
	private void processVTLine(String line) {
		String[] tokens = line.split("[ ]+");
		int c = tokens.length;
		Uv uv = new Uv();
		uv.u = Float.valueOf(tokens[1]);
		uv.v = Float.valueOf(tokens[2]);
		
		texCoords.add(uv);
	}
	/**
	 * 处理法向量行
	 */
	private void processVNLine(String line) {
		String[] tokens = line.split("[ ]+");
		Number3d vertex = new Number3d();
		vertex.x = Float.parseFloat(tokens[1]);
		vertex.y = Float.parseFloat(tokens[2]);
		vertex.z = Float.parseFloat(tokens[3]);
//		vertex.x = Float.valueOf(tokens[0]);
//		vertex.y = Float.valueOf(tokens[1]);
//		vertex.z = Float.valueOf(tokens[2]);
		
		normals.add(vertex);
	}
	/**
	 * 处理面行
	 */
	private void processFLine(String line) {
		String[] tokens = line.split("[ ]+");
		int c = tokens.length;//c = 5
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
	}
	/**
	 * 面处理（部分）
	 */
	private void caseFEqThree(String[] tokens, int c) {
		byte s;
		for (int i = 1; i < c; i++)
		{
			String[] parts = tokens[i].split("/");
			
			s = Byte.valueOf(parts[0]);
			vIndex.add(s);
			
			s = Byte.valueOf(parts[1]);
			vtIndex.add(s);
			
			s = Byte.valueOf(parts[2]);
			vnIndex.add(s);
			
			vIndexNum ++;
		}
	}
	private void caseFEqOneAndThree(String[] tokens, int c) {
		Byte s;
		for (int i = 1; i < c; i++)
		{
			String[] parts = tokens[i].split("/");
			
			s = Byte.valueOf(parts[0]);
			vIndex.add(s);
			
//			s = Byte.valueOf(parts[1]);
//			vtIndex.add(s);
			
			s = Byte.valueOf(parts[2]);
			vnIndex.add(s);
		}
	}
	private void caseFEqTwo(String[] tokens, int c) {
		Byte s;
		for (int i = 1; i < c; i++)
		{
			String[] parts = tokens[i].split("/");
			
			s = Byte.valueOf(parts[0]);
			vIndex.add(s);
			
			s = Byte.valueOf(parts[1]);
			vtIndex.add(s);
			
//			s = Byte.valueOf(parts[2]);
//			vnIndex.add(s);
		}
	}
	private void caseFEqOne(String[] tokens, int c) {
		Byte s;
		for (int i = 1; i < c; i++)
		{
			String[] parts = tokens[i].split("/");
			
			s = Byte.valueOf(parts[0]);
			vIndex.add(s);
			
//			s = Byte.valueOf(parts[1]);
//			vtIndex.add(s);
			
//			s = Byte.valueOf(parts[2]);
//			vnIndex.add(s);
		}
	}
	
	/**
	 * 处理顶点行
	 */
	private void processVLine(final String line) {
		String[] tokens = line.split("[ ]+");
		Number3d vertex = new Number3d();
//		vertex.x = Float.parseFloat(tokens[0]);
//		vertex.y = Float.parseFloat(tokens[1]);
//		vertex.z = Float.parseFloat(tokens[2]);
		//tokens[0]是"v"
		vertex.x = Float.valueOf(tokens[1]);
		vertex.y = Float.valueOf(tokens[2]);
		vertex.z = Float.valueOf(tokens[3]);
		vertices.add(vertex);
	}
	
	public void parser(){
		long startTime = Calendar.getInstance().getTimeInMillis();
		//导入文件
        InputStream inputStream = 
                context.getResources().openRawResource(resourceId);
        InputStreamReader inputStreamReader = 
                new InputStreamReader(inputStream);
		BufferedReader buffer = new BufferedReader(inputStreamReader);
		String line;
		Log.d(TAG, "Start parsing object " + resourceId);
		Log.d(TAG, "Start time " + startTime);
		
		try {
			while((line = buffer.readLine()) != null){//读取一行
				char[] test = line.toCharArray();
				
				switch (test[0]) {
				case 'v':
					if(test[1] == 't'){
						UVCoordsNum++;
						processVTLine(line);
					}else if(test[1] == 'n'){
						normalsNum++;
						processVNLine(line);
					}else{
						verticesNum++;
						processVLine(line);
					}
					break;
				case 'f':
					facesNum++;
					processFLine(line);
					break;

				default:
					break;
				}
				
			}
			
		} catch (Exception e) {
		}
	}
}
