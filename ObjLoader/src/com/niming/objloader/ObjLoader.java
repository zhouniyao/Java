package com.niming.objloader;

import java.io.InputStream;
import java.util.List;


public class ObjLoader {
	
	static int getIndex(String index, int size){
		int idx = Integer.parseInt(index);
		if(idx < 0){
			return size + idx;
		}else{
			return idx - 1;
		}
	}
	
	public static Vertices3 load(GLGame game, String file){
		InputStream in = null;
		try{
			in = game.getFileIO().readAsset(file);
			List<String> lines = readLines(in);
			
			float[] vertices = new float[lines.size() * 3];
			float[] normals = new float[lines.size() * 3];
			float[] uv = new float[lines.size() * 2];
			
			int numVertices = 0;
			int numNormals = 0;
			int numUV = 0;
			int numFaces = 0;
			
			int[] facesVerts = new int[lines.size() * 3];
			int[] facesNormals = new int[lines.size() * 3];
			int[] facesUV = new int[lines.size() * 3];
			int vertexIndex = 0;
			int normalIndex = 0;
			int uvIndex = 0;
			int faceIndex = 0;
			
			for(int i = 0; i < lines.size(); i++){
				String line = lines.get(i);
				
				if(line.startsWith("v " )){
					/*用空格分隔该行*/
					String[] tokens = line.split("[ ]+");
					vertices[vertexIndex] = Float.parseFloat(tokens[1]);
					vertices[vertexIndex+1] = Float.parseFloat(tokens[2]);
					vertices[vertexIndex+2] = Float.parseFloat(tokens[3]);
					vertexIndex += 3;
					numVertices++;
					continue;
				}
				if(line.startsWith("vn")){
					String[] tokens = line.split("[ ]+");
					normals[vertexIndex] = Float.parseFloat(tokens[1]);
					normals[vertexIndex+1] = Float.parseFloat(tokens[2]);
					normals[vertexIndex+2] = Float.parseFloat(tokens[3]);
					normalIndex += 3;
					numNormals++;
					continue;
				}
				if(line.startsWith("vt")){
					String[] tokens = line.split("[ ]+");
					uv[uvIndex] = Float.parseFloat(tokens[1]);
					uv[uvIndex + 1] = Float.parseFloat(tokens[2]);
					uvIndex += 2;
					numUV++;
					continue;
				}
				/*f面读取*/
				if(line.startsWith("f ")){
					String[] tokens = line.split("[ ]+");
					String[] parts = tokens[1].split("/");
					facesVerts[faceIndex] = getIndex(parts[0], numVertices);
					if(parts.length > 2){
						facesNormals[faceIndex] = getIndex(parts[2], numNormals);
					}
					if(parts.length > 1){
						facesUV[faceIndex] = getIndex(parts[1], numUV);
					}
					faceIndex++;
					
					parts = tokens[2].split("/");
					facesVerts[faceIndex] = getIndex(parts[0], numVertices);
					if(parts.length > 2){
						facesNormals[faceIndex] = getIndex(parts[2], numNormals);
					}
					if(parts.length > 1){
						facesUV[faceIndex] = getIndex(parts[1], numUV);
					}
					faceIndex++;
					
					parts = tokens[3].split("/");
					facesVerts[faceIndex] = getIndex(parts[0], numVertices);
					if(parts.length > 2){
						facesNormals[faceIndex] = getIndex(parts[2], numNormals);
					}
					if(parts.length > 1){
						facesUV[faceIndex] = getIndex(parts[1], numUV);
					}
					faceIndex++;
					numFaces++;
					continue;
				}
			}
			
			float[] verts = new float[(numFaces * 3)*(3 + (numNormals > 0?3:0) + (numUV > 0?2:0))];
			
			for (int j = 0, vi = 0; j < numFaces * 3; j++) {
				int vertexIdx = facesVerts[j] * 3;
				verts[vi++] = vertices[vertexIdx];
				verts[vi++] = vertices[vertexIdx + 1];
				verts[vi++] = vertices[vertexIdx + 2];
				
				if(numUV > 0){
					int uvIdx = facesUV[j] * 2;
					verts[vi++] = uv[uvIdx];
					verts[vi++] = 1 - uv[uvIdx + 1];
				}
				
				if(numNormals > 0){
					int normalIdx = facesNormals[j] * 3;
					verts[vi++] = normals[normalIdx];
					verts[vi++] = normals[normalIdx + 1];
					verts[vi++] = normals[normalIdx + 2];
				}
			}
			
			Vertices3 model = new Vertices3(game.getGLGraphics(), numFaces * 3,
					0, false, numUV > 0, numNormals > 0);
			model.setVertices(verts, 0, verts.length);
			return model;
		}catch(Exception e){
			throw new RuntimeException("couldn't load'" + file + "'", e);
		} finally {
			if(in != null){
				try{
					in.close();
				}catch(Exception e){
					
				}
			}
		}//End try
	}
	
	
}
