package com.example.z6niming3d;

import android.os.Bundle;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.core.Scene;
import min3d.parser.IParser;
import min3d.parser.ObjParser;
import min3d.parser.Parser;
import min3d.vos.Light;



	/**
	 * How to load a model from a .obj file
	 * 
	 * @author dennis.ippel
	 * 
	 */
	public class MainActivity extends RendererActivity {
		private Object3dContainer objModel;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
		}
		
		@Override
		public void initScene() {
			scene.lights().add(new Light());
			
			ObjParser parser = (ObjParser) Parser.createParser(Parser.Type.OBJ,
					this.getResources(), "com.example.z6niming3d:raw/umbrella_obj", true);//String ResourceId加载umbrella
			parser.parse();
			objModel = parser.getParsedObject();
			objModel.scale().x = objModel.scale().y = objModel.scale().z = .01f;

//					this.getResources(), "com.example.z6niming3d:raw/camaro_obj", true);//String ResourceId加载car
//			parser.parse();
//			objModel = parser.getParsedObject();
//			objModel.scale().x = objModel.scale().y = objModel.scale().z = .7f;
//		
					
//					this.getResources(), "com.example.z6niming3d:raw/dragon_obj", true);//String ResourceId加载Dragon
//			parser.parse();
//			objModel = parser.getParsedObject();
//			objModel.scale().x = objModel.scale().y = objModel.scale().z = .04f;
			
			
			
//					this.getResources(), "com.example.z6niming3d:raw/girl_obj", true);//String ResourceId加载girl
//			parser.parse();
//			objModel = parser.getParsedObject();
//			objModel.scale().x = objModel.scale().y = objModel.scale().z = 4f;

			
			scene.addChild(objModel);
		}

		@Override
		public void updateScene() {
//			objModel.rotation().x++;
			objModel.rotation().y++;
			objModel.rotation().z++;
		}
	}
