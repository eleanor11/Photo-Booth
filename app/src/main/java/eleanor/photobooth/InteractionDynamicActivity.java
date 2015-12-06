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
import android.view.MotionEvent;
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
    int typeNo;

    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth";
    Bitmap originPhoto;
    Bitmap photo;

    Boolean longPress = false;
    Boolean moveRow = false;
    Boolean moveCol = false;
    Boolean lineMoved = false;

    Boolean moveCircle = false;
    Boolean circleMoved = false;

    Boolean pointMoved = false;

    float circleScale = 0.3f;

    int moveX, moveY;
    int pointX, pointY;
    int iWidth, iHeight;
    int pWidth, pHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.interaction_camera);

        //load OpenCV engine and init OpenCV library
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        final Intent intent = getIntent();
        if (intent != null) {
            typeNo = intent.getIntExtra("interactionType", 4);
            Log.d(TAG, Integer.toString(typeNo));
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Log.d(TAG, "long click");
                longPress = true;
                lineMoved = false;
                circleMoved = false;
                pointMoved = false;
                return false;
            }
        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();
                switch (eventAction) {
                    case MotionEvent.ACTION_DOWN:
//                        Log.d(TAG, "down");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "up");
                        longPress = false;
                        moveRow = false;
                        moveCol = false;
                        if (lineMoved) {
                            redrawPhoto();
                            lineMoved = false;
                        }

                        moveCircle = false;
                        if (circleMoved) {
                            redrawPhoto();
                            circleMoved = false;
                        }

                        if (pointMoved) {
                            redrawPhoto();
                            ;
                            pointMoved = false;
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Log.d(TAG, "move");
                        moveX = (int) event.getX();
                        moveY = (int) event.getY();
                        moveLine();

                        break;
                    default:

                }
                return false;
            }
        });

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

        if (originPhoto == null){
            Log.d(TAG, "origin photo null");
            return;
        }

        photo = originPhoto;

        switch (typeNo) {
            case 0:
                photo = fa.squeeze(originPhoto);
                imageView.setImageBitmap(fa.addCircle(photo));
                break;
            case 1:
                photo = fa.mirrorUp(originPhoto);
                imageView.setImageBitmap(fa.addLineRow(photo));
                break;
            case 2:
                photo = fa.stretch(originPhoto);
                imageView.setImageBitmap(fa.addCircle(photo));
                break;
            case 3:
                photo = fa.mirrorLeft(originPhoto);
                imageView.setImageBitmap(fa.addLineCol(photo));
                break;
            case 4:
                photo = originPhoto;
                imageView.setImageBitmap(photo);
                break;
            case 5:
                photo = fa.mirrorRight(originPhoto);
                imageView.setImageBitmap(fa.addLineCol(photo));
                break;
            case 6:
                photo = fa.kaleidoscope(originPhoto);
                imageView.setImageBitmap(fa.addPoint(photo));
                break;
            case 7:
                photo = fa.mirrorDown(originPhoto);
                imageView.setImageBitmap(fa.addLineRow(photo));
                break;
            default:
                photo = originPhoto;
                imageView.setImageBitmap(photo);

        }
//        fa.save_photo(originPhoto);
    }

    void redrawPhoto() {
        switch (typeNo) {
            case 0: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.squeeze(originPhoto, px, py, circleScale);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
            case 1: {
//                Log.d(TAG, "redraw");
                int row = pointY * pHeight / iHeight;
                photo = fa.mirrorUp(originPhoto, row);
                imageView.setImageBitmap(fa.addLineRow(photo, row));
                break;
            }
            case 2: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.stretch(originPhoto, px, py, circleScale);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
            case 3: {
                int col = pointX * pWidth / iWidth;
                photo = fa.mirrorLeft(originPhoto, col);
                imageView.setImageBitmap(fa.addLineCol(photo, col));
                break;
            }
            case 4:
                photo = originPhoto;
                imageView.setImageBitmap(photo);
                break;
            case 5: {
                int col = pointX * pWidth / iWidth;
                photo = fa.mirrorRight(originPhoto, originPhoto.getWidth() - col);
                imageView.setImageBitmap(fa.addLineCol(photo, col));
                break;
            }
            case 6:{
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.kaleidoscope(originPhoto, px, py);
                imageView.setImageBitmap(fa.addPoint(photo, px, py));
                break;
            }
            case 7: {
                int row = pointY * pHeight / iHeight;
                photo = fa.mirrorDown(originPhoto, originPhoto.getHeight() - row);
                imageView.setImageBitmap(fa.addLineRow(photo, row));
                break;
            }
        }
    }

    void moveLine() {

        int threshold = 7;
        String color = "red";
        int dis;
        Log.d(TAG, "type " + Integer.toString(typeNo));

        switch (typeNo) {
            case 0: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));

                break;
            }
            case 1: {
                Log.d(TAG, "case 1 " + Integer.toString(moveY) + " " + Integer.toString(pointY));

                lineMoved = true;
                pointY = moveY;
                int row = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addLineRow(photo, row, color));

                break;
            }
            case 2: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));

                break;
            }
            case 3: {
                lineMoved = true;
                pointX = moveX;
                int col = pointX * pWidth / iWidth;
                imageView.setImageBitmap(fa.addLineCol(photo, col, color));

                break;
            }
            case 4:
                break;
            case 5:{
                lineMoved = true;
                pointX = moveX;
                int col = pointX * pWidth / iWidth;
                imageView.setImageBitmap(fa.addLineCol(photo, col, color));

                break;
            }
            case 6:
                pointMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addPoint(photo, px, py, color));
                break;
            case 7: {
                lineMoved = true;
                pointY = moveY;
                int row = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addLineRow(photo, row, color));

                break;

            }

        }
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

//            Log.d(TAG, "origin photo got");
            originPhoto = fa.rescale_photo(fa.rotate_bitmap(bitmap), iWidth / 2, iHeight / 2);
            pWidth = originPhoto.getWidth();
            pHeight = originPhoto.getHeight();

            if (!longPress) {
                redrawPhoto();
            }


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
//            Log.d(TAG, "cam open1");
//            Log.d(TAG, "surface created");
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.setPreviewCallback(previewCallback);
        } catch (Exception e) {

        }

//        Log.d(TAG, "cam open1 end");
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "start1");
//    }

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

//    @Override
//    protected void onStop() {
//        super.onStop();;
//        Log.d(TAG, "stop1");
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "destroy1");
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

//        Log.d(TAG, "surface size");
        iWidth = imageView.getWidth();
        iHeight = imageView.getHeight();
//        Log.d(TAG, Integer.toString(iWidth) + " " + Integer.toString(iHeight));

        pointX = iWidth / 2;
        pointY = iHeight / 2;


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
