package niming.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *�Զ���TextView ��дisFocused()�����������Ż�trueҲ����һֱ��ȡ��
 *����Ч����ȻҲ�ͳ����ˣ�����ⶼ���ܽ���ǿ϶��Ͳ��ǽ��������ˡ�
 *�Ǿ�Ҫ�ҵ����⣬����취���
 */
public class MarqueTextView extends TextView {

    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueTextView(Context context) {
        super(context);
    }

    @Override

    public boolean isFocused() {
        return true;
    }
}
