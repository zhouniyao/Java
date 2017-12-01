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
     * 初始化选项菜单，该方法只在第一次显示菜单时调用，
     * 如果需要每次显示菜单时更新菜单项，则需要重写onPrepareOptionsMenu(Menu)方法
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/*
    	 * 
    	 * add()方法的四个参数，依次是：
    	 * 
    	 * 1、组别，如果不分组的话就写Menu.NONE,
    	 * 
    	 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单
    	 * 
    	 * 3、顺序，那个菜单现在在前面由这个参数的大小决定
    	 * 
    	 * 4、文本，菜单的显示文本
    	 */
    	
    	menu.add(Menu.NONE, Menu.FIRST + 1, 5, "删除").setIcon(android.R.drawable.ic_menu_delete);// setIcon()方法为菜单设置图标
    	/*
    	 MenuItem android.view.Menu.add(int groupId, int itemId, int order, CharSequence title);
			Add a new item to the menu. This item displays the given title for its label.
			Parameters:
				groupId 子菜单所在的组Id，通过分组可以对子菜单进行批量操作，无组NONE。
				itemId    Unique item ID. Use NONE if you do not need a unique ID.
				order     The order for the item. Use NONE if you do not care about the order. See MenuItem.getOrder().
				title     The text to display for the item.
			Returns:
				The newly added menu item.
    	 */
    	menu.add(Menu.NONE, Menu.FIRST + 2, 2, "保存").setIcon( android.R.drawable.ic_menu_edit);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 3, 6, "帮助").setIcon( android.R.drawable.ic_menu_help);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 4, 1, "添加").setIcon( android.R.drawable.ic_menu_add);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 5, 4, "详细").setIcon( android.R.drawable.ic_menu_info_details);
    	
    	menu.add(Menu.NONE, Menu.FIRST + 6, 3, "发送").setIcon( android.R.drawable.ic_menu_send);
    	
    	return true;
    }
    /**
     * 当选项菜单中某个选项被选中时，调用该方法，默认是一个返回false的空实现
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case Menu.FIRST + 1:
			showInfo("【删除】菜单被点击");
			break;
		case Menu.FIRST + 2:
			showInfo("【保存】菜单被点击");
			break;
		case Menu.FIRST + 3:
			showInfo("【帮助】菜单被点击");
			break;
		case Menu.FIRST + 4:
			showInfo("【添加】菜单被点击");
			break;
		case Menu.FIRST + 5:
			showInfo("【详细】菜单被点击");
			break;
		case Menu.FIRST + 6:
			showInfo("【发送】菜单被点击");
			break;

		default:
			break;
		}
    	return false;
    }
    /**
     * 当选项菜单关闭或者是由于用户按下返回键亦或是选择了某个菜单选项时调用该方法。
     */
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_LONG).show();
    }
    /**
     * 为程序准备选项菜单，每次选项菜单显示前都会调用该方法，可以通过这个方法设置某些菜单项可用或者不可用，
     * 同时也可以修改菜单项的内容，重写该方法时返回true，否则选项菜单不会显示
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Toast.makeText(this,
                "选项菜单显示之前onPrepareOptionsMenu方法会被调用",
                Toast.LENGTH_SHORT).show();

        // 如果返回false，此方法就把用户点击menu的动作给取消，onCreateOptionsMenu方法将不会被调用
        return true;
    }
    
    public void showInfo(String message){
    	toast.setText(message);
    	toast.show();
    }
}
