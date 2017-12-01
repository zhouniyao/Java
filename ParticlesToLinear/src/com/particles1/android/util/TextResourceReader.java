package com.particles1.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextResourceReader {
	/**
	 *  readTextFileFromResoure()����������ԭ���ǣ��ڳ��������ͨ������Android������(context)����Դ��ʶ��(resourceID)���� readTextFileFromResoure()��
	 *  ���磬Ҫ����Ƭ����ɫ����������Ҫ�ô��� readTextFileFromResoure(this.context, R.raw.simple_fragment_shader).
	 * @param context
	 * @param resourceId
	 * @return bodyBuilder.toString
	 */
    public static String readTextFileFromResource(Context context,
            int resourceId) {
            StringBuilder body = new StringBuilder();

            try {
                InputStream inputStream = 
                    context.getResources().openRawResource(resourceId);
                InputStreamReader inputStreamReader = 
                    new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String nextLine;

                while ((nextLine = bufferedReader.readLine()) != null) {
                    body.append(nextLine);
                    body.append('\n');
                }
            } catch (IOException e) {
                throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
            } catch (Resources.NotFoundException nfe) {
                throw new RuntimeException("Resource not found: " + resourceId, nfe);
            }

            return body.toString();
        }

}
