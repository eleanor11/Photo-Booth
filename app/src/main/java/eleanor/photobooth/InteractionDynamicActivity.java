package eleanor.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.ImageButton;
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
public class InteractionDynamicActivity extends Activity implements SurfaceHolder.Callback{
    Camera camera;
    SurfaceView surfaceView;
    ImageView imageView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    int effectCode;

    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth1";
    Bitmap originPhoto;
    Bitmap photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.interaction_camera);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        final Intent intent = getIntent();
        if (intent != null) {
            effectCode = intent.getIntExtra("effectCode", -1);
            Log.d(TAG, Integer.toString(effectCode));
        }

        ImageButton btn0 = (ImageButton) findViewById(R.id.btn_cancel);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(InteractionDynamicActivity.this, MainActivity.class);
                interaction.putExtra("resultName", "");
                interaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(interaction);
            }
        });

        ImageButton btn1 = (ImageButton) findViewById(R.id.btn_OK);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(InteractionDynamicActivity.this, MainActivity.class);
                interaction.putExtra("resultName", fa.save_photo(photo));
                interaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(interaction);
            }
        });
    }

    private void workoutEffects() {

        imageView = (ImageView) findViewById(R.id.imageView);
        if (originPhoto == null){
            Log.d(TAG, "origin photo null");
            return;
        }

        photo = originPhoto;

        if (effectCode == EFFECTCODE.MIRROE.toInt()) {
                photo = fa.mirrorUp(originPhoto);
        }

        imageView.setImageBitmap(photo);
//        fa.save_photo(originPhoto);
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
            Log.d(TAG, "cam open1");
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.setPreviewCallback(previewCallback);
        } catch (Exception e) {

        }

        Log.d(TAG, "cam open1 end");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.d(TAG,"cam close1");
//        if (camera == null){
//            Log.d(TAG, "cam null");
//            return;
//        }
//        camera.setPreviewCallback(null);
//        camera.stopPreview();
//        camera.release();
//        camera = null;
//        previewing = false;
//        Log.d(TAG,"cam close1 end");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "resume1");
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
        Log.d(TAG, "pause1");

        Log.d(TAG,"cam close1");
        if (camera == null){
            Log.d(TAG, "cam null");
            return;
        }
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
        Log.d(TAG,"cam close1 end");
    }

    @Override
    protected void onStop() {
        super.onStop();;
        Log.d(TAG, "stop1");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroy1");
    }


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
