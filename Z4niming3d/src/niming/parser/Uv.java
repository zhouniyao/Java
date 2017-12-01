package niming.parser;

/**
 * ÎÆÀíUV×ø±ê
 */
public class Uv 
{
	public float u;
	public float v;
	
	public Uv()
	{
		u = 0;
		v = 0;
	}
	
	public Uv(float $u, float $v)
	{
		u = $u;
		v = $v;
	}
	
	public Uv clone()
	{
		return new Uv(u, v);
	}
	public String toString(){
		return ""+ u + "   " + v;
	}
	// Rem, v == 0 @ 'bottom', v == 1 @ 'top'
}
