package niming.beziertest;

import android.animation.TypeEvaluator;
/**
 * FloatEvaluator����ͨ�������֪����ϵͳ��δӳ�ʼֵ���ȵ�����ֵ
 *
 */
public class PointEvaluator implements TypeEvaluator {

	@Override
	public Object evaluate(float fraction, Object startValue, Object endValue) {
		Point startPoint = (Point)startValue;
		Point endPoint = (Point)endValue;
		float x = startPoint.getX() + fraction * (endPoint.getX() - startPoint.getX());
		float y = startPoint.getY() + fraction * (endPoint.getY() - startPoint.getY());
		Point point = new Point(x, y);
		return point;
	}

}
//Ӧ��ʾ��
//Point point1 = new Point(0, 0);  
//Point point2 = new Point(300, 300);  
//ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), point1, point2);  
//anim.setDuration(5000);  
//anim.start(); 

/**
 * ���ͣ������ͽ��в�����,���԰����Ͳ���������ʹ�ò���������ʱָ�������͵�һ��ռλ�������񷽷�����ʽ����������ʱ���ݵ�ֵ��ռλ��һ����
 * �����������������е�ǿ������ת����ͬʱ���һ�����ӵ����ͼ��㣬�ü�����Է�ֹ���˽��������͵ļ���ֵ�����ڼ����С�����Ƿ��������Ĺ�����
 * 1 ���Ͱ�ȫ-> ���͵���ҪĿ������� Java ��������Ͱ�ȫ��ͨ��֪��ʹ�÷��Ͷ���ı������������ƣ�������������һ���ߵö�ĳ̶�����֤���ͼ��衣
 * 2 ����ǿ������ת��-> ������һ�� Map<K, V> ���͵ı���ʱ�������ڷ���֮������һ������Լ���������ݸ� add() ��ֵ���� get() ���ص�ֵ��������ͬ��
 * 
 */