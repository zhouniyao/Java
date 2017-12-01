package com.example.menucustomdemo;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	private boolean isMore = false;// menu�˵���ҳ����
    AlertDialog menuDialog;// menu�˵�Dialog
    GridView menuGrid;
    View menuView;
    
    private final int ITEM_SEARCH = 0;// ����
    private final int ITEM_FILE_MANAGER = 1;// �ļ�����
    private final int ITEM_DOWN_MANAGER = 2;// ���ع���
    private final int ITEM_FULLSCREEN = 3;// ȫ��
    private final int ITEM_MORE = 11;// �˵�

    
    /** �˵�ͼƬ **/
    int[] menu_image_array = { R.drawable.m1,
            R.drawable.m2, R.drawable.m3,
            R.drawable.m4, R.drawable.m5,
            R.drawable.m6, R.drawable.m7,
            R.drawable.m8, R.drawable.m9,
            R.drawable.m10, R.drawable.m11,
            android.R.drawable.ic_menu_more };
    /** �˵����� **/
    String[] menu_name_array = { "����", "�ļ�����", "���ع���", "ȫ��", "��ַ", "��ǩ",
            "������ǩ", "����ҳ��", "�˳�", "ҹ��ģʽ", "ˢ��", "����" };
    /** �˵�ͼƬ2 **/
    int[] menu_image_array2 = { R.drawable.m12,
            R.drawable.m2, R.drawable.m13,
            R.drawable.m14, R.drawable.m15,
            R.drawable.m16, R.drawable.m17,
            R.drawable.m18, R.drawable.m19,
            R.drawable.m20, R.drawable.m11,
            android.R.drawable.ic_menu_more };
    /** �˵�����2 **/
    String[] menu_name_array2 = { "�Զ�����", "��ѡģʽ", "�Ķ�ģʽ", "���ģʽ", "��ݷ�ҳ",
            "������", "�������", "��ʱˢ��", "����", "����", "����", "����" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        menuView = View.inflate(this, R.layout.gridview, null);
        // ����AlertDialog
        menuDialog = new AlertDialog.Builder(this).create();
        menuDialog.setView(menuView);
        menuDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// ��������
                    dialog.dismiss();
                return false;
            }
        });

        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        /** ����menuѡ�� **/
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                switch (arg2) {
                case ITEM_SEARCH:// ����

                    break;
                case ITEM_FILE_MANAGER:// �ļ�����

                    break;
                case ITEM_DOWN_MANAGER:// ���ع���

                    break;
                case ITEM_FULLSCREEN:// ȫ��

                    break;
                case ITEM_MORE:// ��ҳ
                    if (isMore) {
                        menuGrid.setAdapter(getMenuAdapter(menu_name_array2,
                                menu_image_array2));
                        isMore = false;
                    } else {// ��ҳ
                        menuGrid.setAdapter(getMenuAdapter(menu_name_array,
                                menu_image_array));
                        isMore = true;
                    }
                    menuGrid.invalidate();// ����menu
                    menuGrid.setSelection(ITEM_MORE);
                    break;
                }
                
                
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// ���봴��һ��
        return super.onCreateOptionsMenu(menu);
    }
    
    private SimpleAdapter getMenuAdapter(String[] menuNameArray,
            int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.menu_stye, new String[] { "itemImage", "itemText" },
                new int[] { R.id.item_image, R.id.item_text });
        return simperAdapter;
    }
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
        } else {
            menuDialog.show();
        }
        return false;// ����Ϊtrue ����ʾϵͳmenu
    }
    
}
    

