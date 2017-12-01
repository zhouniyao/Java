package niming.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.geometric.Number3d;
import com.geometric.Uv;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

public class Utils 
{
	//弧度
	public static final float DEG = (float)(Math.PI / 180f);
		
	private static final int BYTES_PER_FLOAT = 4;  
	
	/**
	 * Convenience method to create a Bitmap given a Context's drawable resource ID. 
	 * 从资源文件夹读入图像文件，并把图形数据加载Bitmap.
	 */
	public static Bitmap makeBitmapFromResourceId(Context context, int id)
	{
		InputStream is = context.getResources().openRawResource(id);
		
		Bitmap bitmap;
		try {
		   bitmap = BitmapFactory.decodeStream(is);
		} finally {
		   try {
		      is.close();
		   } catch(IOException e) {
		      // Ignore.
		   }
		}
	      
		return bitmap;
	}
	
	
	/**
	 * Add two triangles to the Object3d's faces using the supplied indices
	 */
//	public static void addQuad(Object3D o, int upperLeft, int upperRight, int lowerRight, int lowerLeft)
//	{
//		o.faces().add((short)upperLeft, (short)lowerRight, (short)upperRight);
//		o.faces().add((short)upperLeft, (short)lowerLeft, (short)lowerRight);
//	}
//	

	
	/**
	 *  readTextFileFromResoure()方法，工作原理是，在程序代码中通过传递Android上下文(context)及资源标识符(resourceID)调用 readTextFileFromResoure()。
	 *  例如，要读入片段着色器，我们需要用代码 readTextFileFromResoure(this.context, R.raw.simple_fragment_shader).
	 * @param context
	 * @param resourceId
	 * @return bodyBuilder.toString
	 */
    public static String readTextFileFromResource(Resources resources,
	    int resourceId) {
	    StringBuilder body = new StringBuilder();
	
	    try {
	        InputStream inputStream = 
	        		resources.openRawResource(resourceId);
	        InputStreamReader inputStreamReader = 
	            new InputStreamReader(inputStream);
	        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	
	        String nextLine;
	
	        while ((nextLine = bufferedReader.readLine()) != null) {
	            body.append(nextLine);
	            body.append('\n');
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(
	            "Could not open resource: " + resourceId, e);
	    } catch (Resources.NotFoundException nfe) {
	        throw new RuntimeException("Resource not found: " + resourceId, nfe);
	    }
	
	    return body.toString();
	}
	
    
    /**
     * Helper function to compile a shader.
     *
     * @param shaderType-->The shader type.
     * @param shaderSource--> The shader source code.
     * @return An OpenGL handle to the shader.
     */
    public static int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            /*检查编译 结果*/
            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }

    /**
     * Helper function to compile and link a program.
     *
     * @param vertexShaderHandle--> An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle--> An OpenGL handle to an already-compiled fragment shader.
     * @param attributes Attributes--> that need to be bound to the program.
     * @return An OpenGL handle to the program.
     */
    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }
    
	public static FloatBuffer makeFloatBuffer3(float a, float b, float c)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(3 * BYTES_PER_FLOAT);
		buf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = buf.asFloatBuffer();
		buffer.put(a);
		buffer.put(b);
		buffer.put(c);
		buffer.position(0);
		return buffer;
	}

	public static FloatBuffer makeFloatBuffer4(float a, float b, float c, float d)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(4 * BYTES_PER_FLOAT);
		buf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = buf.asFloatBuffer();
		buffer.put(a);
		buffer.put(b);
		buffer.put(c);
		buffer.put(d);
		buffer.position(0);
		return buffer;
	}
    
    //获取整形缓冲数据 
    public static IntBuffer getIntBuffer(int[] vertexs) 
    { 
	    IntBuffer buffer; 
	    ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 4); 
	    qbb.order(ByteOrder.nativeOrder()); 
	    buffer = qbb.asIntBuffer(); 
	    buffer.put(vertexs); 
	    buffer.position(0); 
	    return buffer; 
    } 
    //获取浮点形缓冲数据 
    public static FloatBuffer getFloatBuffer(float[] vertexs) 
    { 
	    FloatBuffer buffer; 
	    ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 4); 
	    qbb.order(ByteOrder.nativeOrder()); 
	    buffer = qbb.asFloatBuffer(); 
	    buffer.put(vertexs); 
	    buffer.position(0); 
	    return buffer; 
    } 
    //获取字节型缓冲数据 
    public static ByteBuffer getByteBuffer(byte[] vertexs) 
    { 
	    ByteBuffer buffer = null; 
	    buffer = ByteBuffer.allocateDirect(vertexs.length); 
	    buffer.put(vertexs); 
	    buffer.position(0); 
	    return buffer; 
    } 
    
    
    //Number3d转换成float[]
    public static float[] Number3dTofloat(Number3d seq){
    	float[] a = new float[3];
    	a[0] = seq.x;
    	a[1] = seq.y;
    	a[2] = seq.z;
    	return a;
    }
    
    //Uv转换成float[]
    public static float[] UvTofloat(Uv seq) {
		float[] a = new float[2];
		a[0] = seq.u;
		a[1] = seq.v;
		return a;
	}

}
