package com.example.augmented;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity  implements OnClickListener {
	
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	private Camera camera;
	private boolean inPreview;
	
	Button etch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
		setContentView(R.layout.activity_main);
		
		etch = (Button) findViewById(R.id.button1);
		preview=(SurfaceView)findViewById(R.id.surfaceView1);
		
		etch.setOnClickListener(this);

		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		previewHolder.setFixedSize(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight());
	}

	@Override
	public void onResume() {
		super.onResume();
		camera=Camera.open();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera=null;
		inPreview=false;
		super.onPause();
	}

	private Camera.Size getBestPreviewSize(int width, int height,Camera.Parameters parameters){
		Camera.Size result=null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width<=width && size.height<=height) {
				if (result==null) {
					result=size;
				}
				else {
					int resultArea=result.width*result.height;
					int newArea=size.width*size.height;
					if (newArea>resultArea) {
						result=size;
					}
				}
			}
		}
		return(result);
	}

	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback(){
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast
				.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
				.show();
			}
		}

		public void surfaceChanged(SurfaceHolder holder,
				int format, int width,
				int height) {
			Camera.Parameters parameters=camera.getParameters();
			Camera.Size size=getBestPreviewSize(width, height,
					parameters);

			if (size!=null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);
				camera.startPreview();
				inPreview=true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	};
	
	
	public void captureImage() {
		camera.takePicture(null, null, jpeg);
		jpeg.onPictureTaken(null, camera);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			captureImage();
		}	
	}
	
	PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String extr = Environment.getExternalStorageDirectory().toString();
			File mFolder = new File(extr + "/Etch/");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
			try {	
				File f = new File(mFolder.getAbsolutePath(), "file");
			    FileOutputStream fos = new FileOutputStream(f);
			    fos.write(data);
			    Bitmap bm = preview.getDrawingCache();
			    bm.compress(CompressFormat.JPEG, 100, fos);
			    fos.flush();
			    fos.close();
			} 
			catch (Exception error) {
			}	
		}
		
	};	
}
	