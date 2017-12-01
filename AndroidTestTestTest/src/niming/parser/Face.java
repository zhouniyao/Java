package niming.parser;

/**
 * Simple VO with three properties representing vertex indicies. 
 * Is not necessary for functioning of engine, just a convenience.
 * 面上的3个顶点索引
 * 方便使用
 */
public class Face 
{
	public short a;
	public short b;
	public short c;
	
	public Face(){}
	public Face(short $a, short $b, short $c)
	{
		a = $a;
		b = $b;
		c = $c;
	}
	
	/**
	 * Convenience method to cast int arguments to short's 
	 */
	public Face(int $a, int $b, int $c)
	{
		a = (short) $a;
		b = (short) $b;
		c = (short) $c;
	}
}
