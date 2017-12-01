package com.niming.bezierex;

import java.util.ArrayList;

public class BezierUtil 
{
   static ArrayList<BNPosition> al=new ArrayList<BNPosition>();	//���������߻��ڵĵ���б�[���Ƶ�]
   
   public static ArrayList<BNPosition> getBezierData(float span)//�����������ϵ����еķ�����span = 1.0f/Constant.FD
   {
	   ArrayList<BNPosition> result=new ArrayList<BNPosition>(); //��ű����������ϵ�Ľ���б�
	   
	   int n=al.size()-1;	//���Ƶ��߶�����Ҳ�Ǳ������ġ�������
	   
	   if(n<1)	//���ڵĵ��������1���ޱ���������
	   {
		   return result;
	   }
	   
	   int steps=(int) (1.0f/span);	//�ܵò������������ֶܷ����� Constant.FD = 20
	   long[] jiechengNA=new long[n+1];	//����һ������Ϊn+1�Ľ׳�����
	   
	   for(int i=0;i<=n;i++){	//��0��n�Ľ׳�
		   jiechengNA[i]=jiecheng(i);
	   }
	   //�ֶν���ѭ��
	   for(int i=0;i<=steps;i++)
	   {
		   float t=i*span;//ĳ���е�һС��λ����
		   if(t>1){ t=1; }//t��ֵ��0-1֮��
		   
		   float xf=0;//�����������ϵ�x������
		   float yf=0;//�����������ϵ�y������
		   
		   float[] tka=new float[n+1];//�½�float����
		   float[] otka=new float[n+1];
		   for(int j=0;j<=n;j++)
		   {
			   tka[j]=(float) Math.pow(t, j); //����t��j����
			   otka[j]=(float) Math.pow(1-t, j); //����1-t��j����
		   }
		   
		   for(int k=0;k<=n;k++)//ѭ�� n+1 �μ��㱴���������ϸ����������
		   {
			   float xs=(jiechengNA[n]/(jiechengNA[k]*jiechengNA[n-k]))*tka[k]*otka[n-k];
			   xf=xf+al.get(k).x*xs;
			   yf=yf+al.get(k).y*xs;
		   }
		   result.add(new BNPosition(xf,yf));//�õ��ĵ�������б�
	   }
	   
	   return result;//���ر����������ϵĵ���б�
   }
   
   //��׳˵ķ���
   public  static long jiecheng(int n){
	   long result=1;	//����һ��long�͵ı���
	   if(n==0)			//0�Ľ׳�Ϊ1
	   {
		   return 1;
	   }
	   
	   for(int i=2;i<=n;i++){	//����ڵ���2�����Ľ׳�
		   result=result*i;
	   }
	   
	   return result;	//���ؽ��
   }
}