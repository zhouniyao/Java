package com.niming.bezierex;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MyPanel extends JPanel implements MouseListener,MouseMotionListener
{
	BezierExMain father;
	
	public MyPanel(BezierExMain father)
	{
		this.father=father;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
    public void paint(Graphics g)
    {
    	g.setColor(new Color(0,0,0));	//黑色背景
    	
    	
    	g.fillRect(0, 0, 1000, 1000);    	
    	
    	father.jta.setText("以下为控制点列表：\n");   //jta是文本框
    	father.jta.append("  x\t  y\n"); 
    	for(int i=0;i<BezierUtil.al.size()-1;i++)//a1是控制点集
    	{
    		BNPosition start=BezierUtil.al.get(i);
    		BNPosition end=BezierUtil.al.get(i+1);    		
    		if(Constant.XSKZD)
    		{
    			g.setColor(new Color(255,0,255));
    			
    			//控制点间的连线
    			g.drawLine(start.x, Constant.HEIGHT-start.y, end.x, Constant.HEIGHT-end.y);
    			
    			g.setColor(new Color(255,255,255));
        		
    			
    			g.drawRect(start.x-3, Constant.HEIGHT-start.y-3, 7, 7);//画控制点——小方块
    		}    		
    		father.jta.append(start.x+"\t"+start.y+"\n"); 
    		
    	}
    	if(BezierUtil.al.size()>0)
    	{
    		BNPosition yilou=BezierUtil.al.get(BezierUtil.al.size()-1);//最后一个点-【遗漏】
        	father.jta.append(yilou.x+"\t"+yilou.y+"\n");
        	if(Constant.XSKZD)
    		{
	        	g.setColor(new Color(255,255,255));
	        	
	    		g.drawRect(yilou.x-3, Constant.HEIGHT-yilou.y-3, 7, 7);
    		}
    	}
    	father.jta.append("=========================\n"); 
    	father.jta.append("以下为Bezier曲线中点列表(分段"+Constant.FD+")：\n"); 
    	father.jta.append("  x\t  y\n"); 
    	g.setColor(new Color(0,255,0));    	
    	
    	
    	ArrayList<BNPosition> list=BezierUtil.getBezierData(1.0f/Constant.FD);//获得Bezier曲线点集
    	for(int i=0;i<list.size()-1;i++)
    	{
    		BNPosition start=list.get(i);
    		BNPosition end=list.get(i+1);
    		g.drawLine(start.x, Constant.HEIGHT-start.y, end.x, Constant.HEIGHT-end.y);
    		father.jta.append(start.x+"\t"+start.y+"\n"); 
    	}
    	if(list.size()>0)
    	{
    		BNPosition yilou=list.get(list.size()-1);
        	father.jta.append(yilou.x+"\t"+yilou.y+"\n");
    	}
    	father.jta.append("=========================\n"); 
    	father.jta.append("以下为自动生成的代码：\n");
    	father.jta.append("//加入数据点\n");
    	for(BNPosition pos:BezierUtil.al)
    	{
    		father.jta.append("BezierUtil.al.add(new BNPosition("+pos.x+", "+pos.y+"));\n");
    	}
    	father.jta.setCaretPosition(0);
    }

    int state=0;//0-初始态  1-抓住
    int currIndex=-1;
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
    	
	}

	@Override
	public void mouseEntered(MouseEvent arg0){}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		int x=arg0.getX();
		int y=arg0.getY();
		//判断有没有按下控制点
		for(int i=0;i<BezierUtil.al.size();i++)
		{
			BNPosition bnp=BezierUtil.al.get(i);
			if(Math.abs(x-bnp.x)<=3&&Math.abs(y-(Constant.HEIGHT-bnp.y))<=3)//判断是否是已经存在的【控制点】
			{
				currIndex=i;
				state=1;//按住了该控制点
				break;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0)//鼠标释放，激活事件
	{
		if(state==1)
		{
			state=0;
			currIndex=-1;
		}
		else
		{
			BezierUtil.al.add(new BNPosition(arg0.getX(),Constant.HEIGHT-arg0.getY()));
	    	this.repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) //鼠标拖拽
	{ 
		if(state==1)
		{
			BezierUtil.al.get(currIndex).x=arg0.getX();
			BezierUtil.al.get(currIndex).y=Constant.HEIGHT-arg0.getY();
			this.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{ 
		
	}
}
