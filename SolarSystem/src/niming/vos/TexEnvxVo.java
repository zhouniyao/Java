package niming.vos;

import javax.microedition.khronos.opengles.GL10;

/**
 * Structure to hold one OpenGL texture environment command.
 * 一个OpenGL纹理环境命令的结构。
 * Is the equivalent of calling:
 * 		glTexEnvx(GL10.GL_TEXTURE_ENV, pname, param);
 */
public class TexEnvxVo 
{
	public int pname = GL10.GL_TEXTURE_ENV_MODE;
	public int param = GL10.GL_MODULATE;
	
	public TexEnvxVo()
	{
	}
	
	public TexEnvxVo(int $pname, int $param)
	{
		pname = $pname;
		param = $param;
	}

	/**
	 * Convenience method
	 */
	public void setAll(int $pname, int $param)
	{
		pname = $pname;
		param = $param;
	}
}
