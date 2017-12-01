/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.particles.android.util;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    /**
     * Loads a texture from a resource ID, returning the OpenGL ID for that
     * texture. Returns 0 if the load failed.
     * 
     * @param context
     * @param resourceId
     * @return
     */
    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }

            return 0;
        }
        
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(
            context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId
                    + " could not be decoded.");
            }

            glDeleteTextures(1, textureObjectIds, 0);

            return 0;
        } 
        
        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be
        // black.
        glTexParameteri(GL_TEXTURE_2D,
            GL_TEXTURE_MIN_FILTER,
            GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,
            GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.

        glGenerateMipmap(GL_TEXTURE_2D);

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle();

        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];        
    }
    
    public static int loadCubeMap(Context context, int[] cubeResources) {
        
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        
        if(textureObjectIds[0] == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not generate a new OpenGL texture object");
            }
            return 0;
        }
        final BitmapFactory.Options options = new  BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];
        
        for (int i = 0; i < 6; i++) {
            cubeBitmaps[i] = 
                BitmapFactory.decodeResource(context.getResources(), cubeResources[i], options);
            if (cubeBitmaps[i] == null) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Resource ID " + cubeResources[i]
                        + " could not be decoded.");
                }

                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            } //End all if
            
        }//End for
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        /* ��ÿ��ͼ�������Ӧ����������ͼ�����������
         * ����������ͼ������
         * ���������ڲ�ʹ����������ϵ�����������ⲿʹ����������ϵ��
         * */
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);
        
        glBindTexture(GL_TEXTURE_2D, 0); //����������
        for (Bitmap bitmap : cubeBitmaps) {
            bitmap.recycle(); //�������е�λͼ����
            
        }
        return textureObjectIds[0]; //����OpenGL���������ID��������
        
        /*һ��ֻ����һ��λͼ*/
        
//        final int[] textureObjectIds = new int[1];
//        glGenTextures(1, textureObjectIds, 0);
//        if (textureObjectIds[0] == 0) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Could not generate a new OpenGL texture object.");
//            }
//            return 0;
//        }   
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inScaled = false;
//        
//        // Bind to the texture in OpenGL
//        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
//        
//        // Linear filtering for minification and magnification
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);  
//        
//        /*���ص�һ��*/
//        int next = 0;
//        
//        Bitmap bm = BitmapFactory.decodeResource(
//            context.getResources(), cubeResources[next], options);
//        if (bm == null) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Resource ID " + cubeResources[next]
//                    + " could not be decoded.");
//            }
//            glDeleteTextures(1, textureObjectIds, 0);
//            return 0;
//        }       
//        // Load the bitmap into the bound texture.
//        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, bm, 0);
//        
//        /*�ڶ���*/
//        next++;
//        bm = BitmapFactory.decodeResource(
//            context.getResources(), cubeResources[next], options);
//        if (bm == null) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Resource ID " + cubeResources[next]
//                    + " could not be decoded.");
//            }
//            glDeleteTextures(1, textureObjectIds, 0);
//            return 0;
//        }       
//        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, bm, 0);
//        /*������*/
//        next++;
//        bm = BitmapFactory.decodeResource(
//            context.getResources(), cubeResources[next], options);
//        if (bm == null) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Resource ID " + cubeResources[next]
//                    + " could not be decoded.");
//            }
//            glDeleteTextures(1, textureObjectIds, 0);
//            return 0;
//        }       
//        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, bm, 0);
//        /*���ķ�*/
//        next++;
//        bm = BitmapFactory.decodeResource(
//            context.getResources(), cubeResources[next], options);
//        if (bm == null) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Resource ID " + cubeResources[next]
//                    + " could not be decoded.");
//            }
//            glDeleteTextures(1, textureObjectIds, 0);
//            return 0;
//        }       
//        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, bm, 0);
//        /*�����*/
//        next++;
//        bm = BitmapFactory.decodeResource(
//            context.getResources(), cubeResources[next], options);
//        if (bm == null) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Resource ID " + cubeResources[next]
//                    + " could not be decoded.");
//            }
//            glDeleteTextures(1, textureObjectIds, 0);
//            return 0;
//        }       
//        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, bm, 0);
//        /*������*/
//        next++;
//        bm = BitmapFactory.decodeResource(
//            context.getResources(), cubeResources[next], options);
//        if (bm == null) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Resource ID " + cubeResources[next]
//                    + " could not be decoded.");
//            }
//            glDeleteTextures(1, textureObjectIds, 0);
//            return 0;
//        }       
//        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, bm, 0);
//        
//        glBindTexture(GL_TEXTURE_2D, 0);
//        bm.recycle();
//        
//        return textureObjectIds[0];
//       
        
    }
}
