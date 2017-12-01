package niming.parent;

import niming.core.Renderer;
import niming.core.TextureManager;
import android.content.Context;

/**
 * Holds static references to TextureManager, Renderer, and the application Context. 
 */
public class Shared 
{
	private static Context _context;
	private static Renderer _renderer;
	private static TextureManager _textureManager;

	/**
	 * @return _context
	 */
	public static Context context()
	{
		return _context;
	}
	/**
	 * 设置context
	 * @param Context $c
	 */
	public static void context(Context $c)
	{
		_context = $c;
	}
	/**
	 * @return _renderer
	 */
	public static Renderer renderer()
	{
		return _renderer;
	}
	/**
	 * 设置Renderer
	 * @param Renderer $r
	 */
	public static void renderer(Renderer $r)
	{
		_renderer = $r;
	}
	
	/**
	 * You must access the TextureManager instance through this accessor
	 * 你必须通过访问器访问texturemanager实例
	 * @return _textureManager
	 */
	public static TextureManager textureManager()
	{
		return _textureManager;
	}
	/**
	 * 设置 TextureManager
	 * @param TextureManager $bm
	 */
	public static void textureManager(TextureManager $bm)
	{
		_textureManager = $bm;
	}
}
