package niming.util;
/**
 * ����任�����ջ,ֻ�ܱ���һ������
 */
public class MatrixStack {
    //�����任�����ջ
    static float[][] mStack=new float[1][16];
    static int stackTop=-1;
//    public static float[] currMatrix;//��ǰ�任����
    
    public static void pushMatrix(float[] currMatrix)//�����任����
    {
    	if(stackTop < 1){
    		stackTop++;
    		for(int i=0;i<16;i++)
    		{
    			mStack[stackTop][i]=currMatrix[i];
    		}
    	}
    }
    
    public static void popMatrix(float[] currMatrix)//�ָ��任����
    {
    	if(stackTop == -1) return;
    	for(int i=0;i<16;i++)
    	{
    		currMatrix[i]=mStack[stackTop][i];
    	}
    	stackTop--;
    }
}
