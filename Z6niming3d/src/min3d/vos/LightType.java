package min3d.vos;

public enum LightType 
{
	DIRECTIONAL (0f),
	POSITIONAL (1f); // Any value other than 0 treated as non-directional
	
	private final float _glValue;
	
	/**
	 * ������float _glValue ��ֵ
	 * private function
	 */
	private LightType(float $glValue)
	{
		_glValue = $glValue;
	}
	/**
	 * @return float _glValue
	 */
	public float glValue()
	{
		return _glValue;
	}
}
