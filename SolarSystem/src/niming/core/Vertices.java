package niming.core;

import niming.vos.Color4;
import niming.vos.Number3d;
import niming.vos.Uv;


public class Vertices
{
	private Number3dBufferList _points;/*顶点列表*/
	private UvBufferList _uvs;/*纹理列表*/
	private Number3dBufferList _normals;/*顶点法向量列表*/
	private Color4BufferList _colors;/*颜色列表*/
	
	private boolean _hasUvs;/*有纹理*/
	private boolean _hasNormals;/*有顶点法向量*/
	/**
	 * 是否存在颜色，true存在
	 */
	private boolean _hasColors;/*有颜色*/
	
	
	/**
	 * Used by Object3d to hold the lists of vertex points, texture coordinates (UV), normals, and vertex colors. 
	 * Use "addVertex()" to build the vertex data for the Object3d instance associated with this instance. 
	 * 
	 * Direct manipulation of position, UV, normal, or color data can be done directly through the associated 
	 * 'buffer list' instances contained herein.
	 */
	public Vertices(int $maxElements)
	{
		_points = new Number3dBufferList($maxElements);
		
		_hasUvs = true;
		_hasNormals = true;
		_hasColors = true;
		
		if (_hasUvs) _uvs = new UvBufferList($maxElements);
		if (_hasNormals) _normals = new Number3dBufferList($maxElements);
		if (_hasColors) _colors = new Color4BufferList($maxElements);
	}

	/**
	 * This version of the constructor adds 3 boolean arguments determine whether 
	 * uv, normal, and color lists will be used by this instance.
	 * Set to false when appropriate to save memory, increase performance. 
	 */
	public Vertices(int $maxElements, Boolean $useUvs, Boolean $useNormals, Boolean $useColors)
	{
		_points = new Number3dBufferList($maxElements);
		
		_hasUvs = $useUvs;
		_hasNormals = $useNormals;
		_hasColors = $useColors;
		
		if (_hasUvs) _uvs = new UvBufferList($maxElements);
		if (_hasNormals) _normals = new Number3dBufferList($maxElements);
		if (_hasColors) _colors = new Color4BufferList($maxElements);
	}
	
	public Vertices(Number3dBufferList $points, UvBufferList $uvs, Number3dBufferList $normals,
			Color4BufferList $colors)
	{
		_points = $points;
		_uvs = $uvs;
		_normals = $normals;
		_colors = $colors;
		
		_hasUvs = _uvs != null && _uvs.size() > 0;
		_hasNormals = _normals != null && _normals.size() > 0;
		_hasColors = _colors != null && _colors.size() > 0;
	}
	
	public int size()
	{
		return _points.size();
	}
	
	public int capacity()
	{
		return _points.capacity();
	}
	
	public boolean hasUvs()
	{
		return _hasUvs;
	}

	public boolean hasNormals()
	{
		return _hasNormals;
	}
	
	public boolean hasColors()
	{
		return _hasColors;
	}
	
	
	/**
	 * Use this to populate an Object3d's vertex data.
	 * Return's newly added vertex's index 
	 * @return 顶点数，从0开始
	 *  	If hasUvs, hasNormals, or hasColors was set to false, 
	 * 		their corresponding arguments are just ignored.
	 */
	public short addVertex(
		float $pointX, float $pointY, float $pointZ,  
		float $textureU, float $textureV,  
		float $normalX, float $normalY, float $normalZ,  
		short $colorR, short $colorG, short $colorB, short $colorA)
	{
		_points.add($pointX, $pointY, $pointZ);
		
		if (_hasUvs) _uvs.add($textureU, $textureV);
		if (_hasNormals) _normals.add($normalX, $normalY, $normalZ);
		if (_hasColors) _colors.add($colorR, $colorG, $colorB, $colorA);
		
		return (short)(_points.size()-1);
	}
	
	/**
	 * More structured-looking way of adding a vertex (but potentially wasteful).
	 * The VO's taken in as arguments are only used to read the values they hold
	 * (no references are kept to them).  
	 * Return's newly added vertex's index 
	 * 		If hasUvs, hasNormals, or hasColors was set to false, 
	 * 		their corresponding arguments are just ignored.
	 */
	public short addVertex(Number3d $point, Uv $textureUv, Number3d $normal, Color4 $color)
	{
		_points.add($point);
		
		if (_hasUvs) _uvs.add($textureUv);
		if (_hasNormals) _normals.add($normal);
		if (_hasColors) _colors.add($color);
		
		return (short)(_points.size()-1);
	}
	/**
	 * 重写顶点
	 */
	public void overwriteVerts(float[] $newVerts)
	{
		_points.overwrite($newVerts);
	}
	/**
	 * 重写顶点法向量
	 */
	public void overwriteNormals(float[] $newNormals)
	{
		_normals.overwrite($newNormals);
	}
	/**
	 * 返回顶点
	 * @return _points;
	 */
	Number3dBufferList points() /*package-private*/
	{
		return _points;
	}
	
	/**
	 * List of texture coordinates
	 */
	UvBufferList uvs() /*package-private*/
	{
		return _uvs;
	}
	
	/**
	 * List of normal values 
	 */
	Number3dBufferList normals() /*package-private*/
	{
		return _normals;
	}
	
	/**
	 * List of color values
	 */
	Color4BufferList colors() /*package-private*/
	{
		return _colors;
	}
	
	public Vertices clone()
	{
		Vertices v = new Vertices(_points.clone(), _uvs.clone(), _normals.clone(), _colors.clone());
		return v;
	}
}
