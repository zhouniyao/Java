//package com.airhockey.android.util;
//
//import android.R.bool;
//import android.R.integer;
//import android.util.Log;
//import static android.opengl.GLES20.*;
//
///**
// * �༭ÿ����ɫ��
// * @author Niming
// */
//public class ShaderHelper {
//	private static final String TAG = "ShaderHelper";
//	
//	public static boolean validateProgram(int programObjectId) {
//		//����glValidateProgram()�������������Ȼ����GL_VALIDATA_STATUS��Ϊ��������glGetProgramiv()�������������
//		glValidateProgram(programObjectId);
//		
//		final int[] validateStatus = new int[1];
//		glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
//		Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog: " + glGetProgramInfoLog(programObjectId));
//		return validateStatus[0] != 0;
//	}
//	
//	public static int compileVertexShader(String shaderCode) {
//		return compileShader(GL_VERTEX_SHADER, shaderCode);
//	}
//	
//	public static int compileFragmentShader(String shaderCode){
//		return  compileShader(GL_FRAGMENT_SHADER, shaderCode);
//	}
//	
//	private static int compileShader(int type, String shaderCode) {
//		final int shaderObjectID = glCreateShader(type);
//		
//		if(shaderObjectID == 0){
//			if(LoggerConfig.ON){
//				Log.w(TAG, "Could not create new shader.");
//			}
//			return 0;
//		}
//		
//		// ����ɫ��Դ���볤������ɫ�������У�������ø���OpenGL�����ַ���shaderCoder�����Դ���룬��������shaderObjectId�����õ���ɫ���������������
//		glShaderSource(shaderObjectID, shaderCode);
//		glCompileShader(shaderObjectID);
//		
//		final int[] compileStatus = new int[1];
//		//����OpenGL��ȡ��shaderObjectId�����ı���״̬��������д��compileStatus�ĵ�0��Ԫ�ء�
//		glGetShaderiv(shaderObjectID, GL_COMPILE_STATUS, compileStatus, 0);
//		
//		if(LoggerConfig.ON){
//			//Print the shader info log to the Android log output.
//			Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n" + glGetShaderInfoLog(shaderObjectID));
//		}
//	
//		
//		
//		if(compileStatus[0] == 0){
//			//If it failed, delete the shader object.
//			glDeleteShader(shaderObjectID);
//			
//			if(LoggerConfig.ON){
//				Log.w(TAG, "Compilation of shader failed.");
//			}
//			return 0;
//		}
//		return shaderObjectID;
//	}
//	
//	public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
//		final int programObjectId = glCreateProgram();
//		
//		if(programObjectId == 0){
//			if(LoggerConfig.ON){
//				Log.w(TAG, "Could not create new program");
//			}
//			return 0;
//		}
//		
//		//������ɫ��
//		//glAttachShader()�Ѷ�����ɫ����Ƭ����ɫ�������ڳ�������ϡ�
//		glAttachShader(programObjectId, vertexShaderId);
//		glAttachShader(programObjectId, fragmentShaderId);
//		//����ɫ����������
//		glLinkProgram(programObjectId);
//		//�����������Ƿ�ɹ�
//		/*
//		 * ���ȣ�����һ���µ�����������������������Ȼ�󣬵���glGetProgramiv()�ѽ������������顣����Ҳ�������������Ϣ��־��
//		 */
//		final int[] linkStatus = new int[1];
//		glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
//		if(LoggerConfig.ON){
//			//Print the program info tag to the Android log output.
//			Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));
//		}
//		
//		//���ڼ������״̬
//		if(linkStatus[0] == 0){
//			//If it failed, delete the program object.
//			glDeleteProgram(programObjectId);
//			if(LoggerConfig.ON){
//				Log.w(TAG, "Linking of program failed.");
//			}
//			return 0;
//		}
//		return programObjectId;
//	}
//
//}


/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android.util;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;
import android.util.Log;

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";
    
    /**
     * Loads and compiles a vertex shader, returning the OpenGL object ID.
     */
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }
    
    /**
     * Loads and compiles a fragment shader, returning the OpenGL object ID.
     */
    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }
    
    /**
     * Compiles a shader, returning the OpenGL object ID.
     * compileShader()���������Ĳ����ǣ�
     * �½���ɫ������glCreateShader�������ϴ���ɫ��Դ���루glShaderSource������ȡ������״̬��glCompileShader��
     * ����ȡ����ɫ����Ϣ��־��glGetShaderiv��������֤����״̬��������ɫ������ID��compileStatus[0] == 0��
     */
    private static int compileShader(int type, String shaderCode) {
        
        // Create a new shader object.
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }

            return 0;
        }
       
        // Pass in the shader source.
        glShaderSource(shaderObjectId, shaderCode);

        // Compile the shader.
        glCompileShader(shaderObjectId);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerConfig.ON) {
            // Print the shader info log to the Android log output.
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" 
                + glGetShaderInfoLog(shaderObjectId));
        }

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);

            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed.");
            }

            return 0;
        }

        // Return the shader object ID.
        return shaderObjectId;
    }
    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program. Returns the OpenGL program object ID, or 0 if linking failed.
     * һ��OpenGL�ĳ�����ǰ�һ��������ɫ����һ��Ƭ����ɫ��������һ���ɵ�������
     * ����������������ϴ�����ͬ����Ҫ����Ϊ��
     * �½����򲢸�����ɫ�����󡪡����ӳ��򡪡���֤����״̬�����س������ID��������Ⱦ�������롣
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {

        // Create a new program object.
        final int programObjectId = glCreateProgram();
		
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program");
            }
			
            return 0;
        }

        // Attach the vertex shader to the program.
        glAttachShader(programObjectId, vertexShaderId);
        // Attach the fragment shader to the program.
        glAttachShader(programObjectId, fragmentShaderId);

        // Link the two shaders together into a program.
        glLinkProgram(programObjectId);

        // Get the link status.
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON) {
            // Print the program info log to the Android log output.
            Log.v(TAG, "Results of linking program:\n" 
                + glGetProgramInfoLog(programObjectId));			
        }

        // Verify the link status.
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }

        // Return the program object ID.
        return programObjectId;
    }
    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application.
     */
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
		
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0]
            + "\nLog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }
}
