package com.niming.menudemo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        setContentView(R.layout.activity_main);
    }
    /**
     * ��ʼ��ѡ��˵����÷���ֻ�ڵ�һ����ʾ�˵�ʱ���ã�
     * �����Ҫÿ����ʾ�˵�ʱ���²˵������Ҫ��дonPrepareOptionsMenu(Menu)����
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/*
    	 * 
    	 * add()�������ĸ������������ǣ�
    	 * 
    	 * 1��������������Ļ���дMenu.NONE,
    	 * 
    	 * 2��Id���������Ҫ��Android�������Id��ȷ����ͬ�Ĳ˵�
    	 * 
    	 * 3��˳���Ǹ��˵�������ǰ������������Ĵ�С����
    	 * 
    	 * 4���ı����˵�����ʾ�ı�
    	 */
    	
    	menu.add(Menu.NONE, Menu.FIRST + 1, 5, "ɾ��").setIcon(android.R.drawable.ic_menu_delete);// setIcon()����Ϊ�˵�����ͼ��
    	/*
    	 MenuItem android.view.Menu.add(int groupId, int itemId, int order, CharSequence title);
			Add a new item to the menu. This item displays the given title for its label.
			Parameters:
				groupId �Ӳ˵����ڵ���Id��ͨ��������Զ��Ӳ˵�������������������NONE��
				itemId    Unique item ID. Use NONE if you do not need a unique ID.
				order     The order for the item. Use NONE if you do not care about the order. See MenuItem.getOrder().
				title     The text to display for the item.
			Returns:
				The newly added menu item.
    	 */
    	menu.add(Menu.NONE, Menu.FIRST + 2, 2, "����").setIcon( android.R.drawable.ic_menu_edit);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 3, 6, "����").setIcon( android.R.drawable.ic_menu_help);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 4, 1, "���").setIcon( android.R.drawable.ic_menu_add);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 5, 4, "��ϸ").setIcon( android.R.drawable.ic_menu_info_details);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 6, 3, "����").setIcon( android.R.drawable.ic_menu_send);
    	
    	return true;
    }
    /**
     * ��ѡ��˵���ĳ��ѡ�ѡ��ʱ�����ø÷�����Ĭ����һ������false�Ŀ�ʵ��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case Menu.FIRST + 1:
			showInfo("��ɾ�����˵������");
			break;
		case Menu.FIRST + 2:
			showInfo("�����桿�˵������");
			break;
		case Menu.FIRST + 3:
			showInfo("���������˵������");
			break;
		case Menu.FIRST + 4:
			showInfo("����ӡ��˵������");
			break;
		case Menu.FIRST + 5:
			showInfo("����ϸ���˵������");
			break;
		case Menu.FIRST + 6:
			showInfo("�����͡��˵������");
			break;

		default:
			break;
		}
    	return false;
    }
    /**
     * ��ѡ��˵��رջ����������û����·��ؼ������ѡ����ĳ���˵�ѡ��ʱ���ø÷�����
     */
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        Toast.makeText(this, "ѡ��˵��ر���", Toast.LENGTH_LONG).show();
    }
    /**
     * Ϊ����׼��ѡ��˵���ÿ��ѡ��˵���ʾǰ������ø÷���������ͨ�������������ĳЩ�˵�����û��߲����ã�
     * ͬʱҲ�����޸Ĳ˵�������ݣ���д�÷���ʱ����true������ѡ��˵�������ʾ
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Toast.makeText(this,
                "ѡ��˵���ʾ֮ǰonPrepareOptionsMenu�����ᱻ����",
                Toast.LENGTH_SHORT).show();

        // �������false���˷����Ͱ��û����menu�Ķ�����ȡ����onCreateOptionsMenu���������ᱻ����
        return true;
    }
    
    public void showInfo(String message){
    	toast.setText(message);
    	toast.show();
    }
}
