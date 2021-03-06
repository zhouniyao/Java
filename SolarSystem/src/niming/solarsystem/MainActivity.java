package niming.solarsystem;

import niming.core.Object3dContainer;
import niming.core.RendererActivity;
import niming.object.Sphere;
import niming.parent.Shared;
import niming.parent.Utils;
import niming.vos.Light;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;

public class MainActivity extends RendererActivity {
	
	Object3dContainer _sun;//木星
	Object3dContainer _earth;
	Object3dContainer _moon;
	int _count;
	public void initScene() 
	{
		Light light = new Light();
		
//		light.ambient.setAll((short)64, (short)64, (short)64, (short)255);
		light.ambient.setAll((short)64, (short)64, (short)64, (short)255);
		light.specular.setAll((short)255, (short)255, (short)255, (short)255);
		light.position.setAll(0f, 0f, 3f);
		scene.lights().add(light);
//		
//		// Add Jupiter to scene
//		_sun = new Sphere(0.8f, 15, 10, true,true,false);
//		_sun.scale().x =_sun.scale().y =_sun.scale(). z = 2f;//缩放及缩放其child
//		scene.addChild(_sun);
//
		// Add Earth as a child of Jupiter
		_earth = new Sphere(0.4f, 12, 9, true,true,false);
//		_earth.position().x = 1.6f;
		_earth.rotation().x = 23;
//		_sun.addChild(_earth);
		
		/*test*/
		_earth.scale().x =_earth.scale().y =_earth.scale(). z = 1.4f;//缩放及缩放其child
		scene.addChild(_earth);
		
// 
		// Add the Moon as a child of Earth
		_moon = new Sphere(0.2f, 10, 8,  true,true,false);
		_moon.position().x = 1.2f;
		_earth.addChild(_moon);
//
		Bitmap b;
//		// Add textures to TextureManager		
//		b = Utils.makeBitmapFromResourceId(this, R.drawable.sun);
//		Shared.textureManager().addTextureId(b, "sun", false);
//		b.recycle();
//
		b = Utils.makeBitmapFromResourceId(this, R.drawable.earth_cloud);
		Shared.textureManager().addTextureId(b, "earth", false);
		b.recycle();
			
		b = Utils.makeBitmapFromResourceId(this, R.drawable.moon);
		Shared.textureManager().addTextureId(b, "moon", false);
		b.recycle();

		// Add textures to objects based on on the id's we assigned the textures in the texture manager
//		_sun.textures().addById("sun");
		_earth.textures().addById("earth");
		_moon.textures().addById("moon");
		
		
		_count = 0;
	}
	
	@Override 
	public void updateScene() 
	{
		//旋转球体
//		_sun.rotation().y += 1.0f;
		_earth.rotation().y += 1.5f;
		_moon.rotation().y -= 6.0f;
		
		// Wobble Jupiter a little just for fun 
		// 轻微晃动木星
//		_count++;
//		float mag = (float)(Math.sin(_count*0.2*Utils.DEG)) * 15;
//		_sun.rotation().z = (float)Math.sin(_count*.33*Utils.DEG) * mag;
//		
//		// Move camera around
//		scene.camera().position.y = 4.5f + (float)Math.sin(_sun.rotation().y * Utils.DEG);
//		scene.camera().target.x = (float)Math.sin((_sun.rotation().y + 90) * Utils.DEG) * 0.8f;
	}
	
}
