package niming.beziertest;

import android.animation.TypeEvaluator;
/**
 * FloatEvaluator，它通过计算告知动画系统如何从初始值过度到结束值
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
//应用示例
//Point point1 = new Point(0, 0);  
//Point point2 = new Point(300, 300);  
//ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), point1, point2);  
//anim.setDuration(5000);  
//anim.start(); 

/**
 * 泛型，即类型进行参数化,可以把类型参数看作是使用参数化类型时指定的类型的一个占位符，就像方法的形式参数是运行时传递的值的占位符一样。
 * 可以让您消除代码中的强制类型转换，同时获得一个附加的类型检查层，该检查层可以防止有人将错误类型的键或值保存在集合中。这就是泛型所做的工作。
 * 1 类型安全-> 泛型的主要目标是提高 Java 程序的类型安全。通过知道使用泛型定义的变量的类型限制，编译器可以在一个高得多的程度上验证类型假设。
 * 2 消除强制类型转换-> 当创建一个 Map<K, V> 类型的变量时，您就在方法之间宣称一个类型约束。您传递给 add() 的值将与 get() 返回的值的类型相同。
 * 
 */