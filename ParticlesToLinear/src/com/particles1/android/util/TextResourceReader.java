package com.particles1.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextResourceReader {
	/**
	 *  readTextFileFromResoure()方法，工作原理是，在程序代码中通过传递Android上下文(context)及资源标识符(resourceID)调用 readTextFileFromResoure()。
	 *  例如，要读入片段着色器，我们需要用代码 readTextFileFromResoure(this.context, R.raw.simple_fragment_shader).
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
