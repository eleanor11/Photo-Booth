package eleanor.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import eleanor.photobooth.Functions.EFFECTCODE;
import eleanor.photobooth.Functions.FunctionAccessor;
import eleanor.photobooth.Functions.FunctionImpl;

/**
 * Created by Eleanor on 2015/11/17.
 */
public class ChooseEffectDynamicActivity extends Activity implements SurfaceHolder.Callback {
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;

    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth";
    Bitmap originPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_effect_camera);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView4);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

    }

    private void workoutEffects(){
        if (originPhoto == null){
            Log.d(TAG, "origin photo null");
            return;
        }
//        Log.d(TAG, Integer.toString(originPhoto.getHeight()));
//        Log.d(TAG, Integer.toString(originPhoto.getWidth()));

        Bitmap smallPhoto = fa.rescale_photo(originPhoto, 0.3f);

        ImageView iv0 = (ImageView) findViewById(R.id.imageView0);
        iv0.setImageBitmap(fa.squeeze(smallPhoto));
        iv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 0);
                startActivity(interaction);
            }
        });

        ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
        iv1.setImageBitmap(fa.mirrorUp(smallPhoto));
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 1);
                startActivity(interaction);
            }
        });

        ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
        iv2.setImageBitmap(fa.stretch(smallPhoto));
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 2);
                startActivity(interaction);
            }
        });

        ImageView iv3 = (ImageView) findViewById(R.id.imageView3);
        iv3.setImageBitmap(fa.mirrorLeft(smallPhoto));
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 3);
                startActivity(interaction);
            }
        });

        SurfaceView sv4 = (SurfaceView) findViewById(R.id.surfaceView4);
        sv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 4);
                startActivity(interaction);
            }
        });

        ImageView iv5 = (ImageView) findViewById(R.id.imageView5);
        iv5.setImageBitmap(fa.mirrorRight(smallPhoto));
        iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 5);
                startActivity(interaction);
            }
        });

        ImageView iv6 = (ImageView) findViewById(R.id.imageView6);
        iv6.setImageBitmap(fa.twirl(smallPhoto));
        iv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 6);
                startActivity(interaction);
            }
        });

        ImageView iv7 = (ImageView) findViewById(R.id.imageView7);
        iv7.setImageBitmap(fa.mirrorDown(smallPhoto));
        iv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(ChooseEffectDynamicActivity.this, InteractionDynamicActivity.class);
                interaction.putExtra("interactionType", 7);
                startActivity(interaction);
            }
        });

        ImageView iv8 = (ImageView) findViewById(R.id.imageView8);
        iv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseEffect = new Intent(ChooseEffectDynamicActivity.this, ChooseEffectDynamicActivity2.class);
                startActivity(chooseEffect);
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();

//                surfaceView.setDrawingCacheEnabled(true);
//                surfaceView.buildDrawingCache();
//                originPhoto = Bitmap.createBitmap(surfaceView.getDrawingCache());

                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data,Camera cam)
        {
            //cam.setDisplayOrientation(90);
            if (cam == null) {
                return;
            }

            Camera.Size previewSize = cam.getParameters().getPreviewSize();
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);
            byte[] jdata = baos.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);

            originPhoto = fa.rotate_bitmap(bitmap);
            workoutEffects();


//            Log.d(TAG, "ok?");
//            fa.save_photo(fa.rotate_bitmap(bitmap));
        }
    };

    private boolean cameraUsed() {
        boolean flag = false;
        Camera camera = null;

        try {
            camera = Camera.open();
        } catch (Exception e) {
            flag = true;
        }
        if (!flag){
            camera.release();
        }

        return flag;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        while (cameraUsed()){}
        try {
            Log.d(TAG, "cam open");
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.setPreviewCallback(previewCallback);
        } catch (Exception e) {

        }

        Log.d(TAG, "cam open end");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.d(TAG,"cam close");
//        if (camera == null){
//            return;
//        }
//        camera.setPreviewCallback(null);
//        camera.stopPreview();
//        camera.release();
//        camera = null;
//        previewing = false;
//        Log.d(TAG,"cam close end");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "resume");
        //load OpenCV engine and init OpenCV library
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "pause");

        Log.d(TAG,"cam close");
        if (camera == null){
            return;
        }
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
        Log.d(TAG,"cam close end");
    }

    @Override
    protected void onStop() {
        super.onStop();;
        Log.d(TAG, "stop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroy");
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };


}
