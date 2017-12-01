package min3d.core;

import java.util.ArrayList;

import min3d.interfaces.IObject3dContainer;

public class Object3dContainer extends Object3d implements IObject3dContainer
{
	protected ArrayList<Object3d> _children = new ArrayList<Object3d>();

	public Object3dContainer()
	{
		super(0, 0, false, false, false);
	}
	/**
	 * Adds container functionality to Object3d.
	 * 
	 * Subclass Object3dContainer instead of Object3d if you
	 * believe you may want to add children to that object. 
	 * 子类object3dcontainer代替Object3D如果你认为你可能想添加孩子的对象。
	 */
	public Object3dContainer(int $maxVerts, int $maxFaces)
	{
		super($maxVerts, $maxFaces, true,true,true);
	}

	public Object3dContainer(int $maxVerts, int $maxFaces,  Boolean $useUvs, Boolean $useNormals, Boolean $useVertexColors)
	{
		super($maxVerts, $maxFaces, $useUvs,$useNormals,$useVertexColors);
	}
	
	/**
	 * This constructor is convenient for cloning purposes 
	 */
	public Object3dContainer(Vertices $vertices, FacesBufferedList $faces, TextureList $textures)
	{
		super($vertices, $faces, $textures);
	}
	/**
	 * 追加新Object3d
	 * @param Object3d $o
	 */
	public void addChild(Object3d $o)
	{
		_children.add($o);
		
		$o.parent(this);
		$o.scene(this.scene());
	}
	/**
	 * 指定位置追加新Object3d
	 */
	public void addChildAt(Object3d $o, int $index) 
	{
		_children.add($index, $o);
		
		$o.parent(this);
		$o.scene(this.scene());
	}

	public boolean removeChild(Object3d $o)
	{
		boolean b = _children.remove($o);
		
		if (b) {
			$o.parent(null);
			$o.scene(null);
		}
		return b;
	}
	/**
	 * 指定位置移除Object3d
	 */
	public Object3d removeChildAt(int $index) 
	{
		Object3d o = _children.remove($index);
		if (o != null) {
			o.parent(null);
			o.scene(null);
		}
		return o;
	}
	/**
	 * 指定位置得到Object3d对象
	 */
	public Object3d getChildAt(int $index) 
	{
		return _children.get($index);
	}

	/**
	 * TODO: Use better lookup 
	 * 依照名称得到Object3d
	 * @param String $name
	 */
	public Object3d getChildByName(String $name)
	{
		for (int i = 0; i < _children.size(); i++)
		{
			if (_children.get(i).name().equals($name)) return _children.get(i); 
		}
		return null;
	}
	/**
	 * 依照Object对象，获得其在ArrayList<Object3d> 索引号
	 */
	public int getChildIndexOf(Object3d $o) 
	{
		return _children.indexOf($o);		
	}

	/**
	 * ArrayList<Object3d> 大小
	 */
	public int numChildren() 
	{
		return _children.size();
	}
	
	/*package-private*/ 
	/**
	 * @return  _children
	 */
	ArrayList<Object3d> children()
	{
		return _children;
	}
	
	public Object3dContainer clone()
	{
		Vertices v = _vertices.clone();
		FacesBufferedList f = _faces.clone();

		Object3dContainer clone = new Object3dContainer(v, f, _textures);
		
		clone.position().x = position().x;
		clone.position().y = position().y;
		clone.position().z = position().z;
		
		clone.rotation().x = rotation().x;
		clone.rotation().y = rotation().y;
		clone.rotation().z = rotation().z;
		
		clone.scale().x = scale().x;
		clone.scale().y = scale().y;
		clone.scale().z = scale().z;
		
		for(int i = 0; i< this.numChildren();i++)
		{
			 clone.addChild(this.getChildAt(i));
		}
		 
		return clone;
	}

}
