package niming.beziertest;

import java.util.ArrayList;

import android.graphics.PointF;
/**
 * 动态产生Bezier曲线的点的数据源
 *
 */
public class BezierUtil2 
{
   ArrayList<PointF> al=new ArrayList<PointF>();	//贝塞尔曲线基于的点的列表[控制点]
   
   public PointF getBezierData(float t)//求贝塞尔曲线上点序列的方法，fraction是不断变化中的t
   {
	   int n=al.size()-1;	//控制点线段数，也是贝塞尔的【介数】
	   
	   if(n<1)	//基于的点的数少于1，无贝塞尔曲线
	   {
		   return null;
	   }
	   long[] jiechengNA=new long[n+1];	//声明一个长度为n+1的阶乘数组
	   
	   for(int i=0;i<=n;i++){	//求0到n的阶乘
		   jiechengNA[i]=jiecheng(i);
	   }
	   if(t>1){ t=1; }//t的值在0-1之间
	   float xf=0;//贝塞尔曲线上点x的坐标
	   float yf=0;//贝塞尔曲线上点y的坐标
	   float[] tka=new float[n+1];//新建float数组
	   float[] otka=new float[n+1];
	   for(int j=0;j<=n;j++)
	   {
		   tka[j]=(float) Math.pow(t, j); //计算t的j次幂
		   otka[j]=(float) Math.pow(1-t, j); //计算1-t的j次幂
	   }
	   for(int k=0;k<=n;k++)//循环 n+1 次计算贝塞尔曲线上各个点的坐标
	   {
		   float xs=(jiechengNA[n]/(jiechengNA[k]*jiechengNA[n-k]))*tka[k]*otka[n-k];//贝塞尔曲线的参数方程系数
		   xf=xf+al.get(k).x*xs;
		   yf=yf+al.get(k).y*xs;
	   }
	   
	   return new PointF(xf, yf);//返回贝塞尔曲线上的点的列表
   }
   
   //求阶乘的方法
   public  static long jiecheng(int n){
	   long result=1;	//声明一个long型的变量
	   if(n==0)			//0的阶乘为1
	   {
		   return 1;
	   }
	   
	   for(int i=2;i<=n;i++){	//求大于等于2的数的阶乘
		   result=result*i;
	   }
	   
	   return result;	//返回结果
   }
}
