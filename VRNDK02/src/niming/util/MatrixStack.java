package niming.util;
/**
 * 保存变换矩阵的栈,只能保存一个数组
 */
public class MatrixStack {
    //保护变换矩阵的栈
    static float[][] mStack=new float[1][16];
    static int stackTop=-1;
//    public static float[] currMatrix;//当前变换矩阵
    
    public static void pushMatrix(float[] currMatrix)//保护变换矩阵
    {
    	if(stackTop < 1){
    		stackTop++;
    		for(int i=0;i<16;i++)
    		{
    			mStack[stackTop][i]=currMatrix[i];
    		}
    	}
    }
    
    public static void popMatrix(float[] currMatrix)//恢复变换矩阵
    {
    	if(stackTop == -1) return;
    	for(int i=0;i<16;i++)
    	{
    		currMatrix[i]=mStack[stackTop][i];
    	}
    	stackTop--;
    }
}
