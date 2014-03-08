package com.example.augmented;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	private Camera camera;
	private boolean inPreview;
	Canvas c;
	private Paint p = new Paint();

	
	DrawView drv;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
		setContentView(R.layout.activity_main);
		

		preview=(SurfaceView)findViewById(R.id.surfaceView1);
		drv = new DrawView(this);

		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		previewHolder.setFixedSize(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight());
		
		c = new Canvas();
		p.setColor(Color.BLACK);
		addContentView(drv, preview.getLayoutParams());
		c.drawCircle(100, 100, 50, p);

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
			// no-op
		}
	};

}