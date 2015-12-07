package eleanor.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import eleanor.photobooth.Functions.EFFECTCODE;
import eleanor.photobooth.Functions.FunctionAccessor;
import eleanor.photobooth.Functions.FunctionImpl;

/**
 * Created by Eleanor on 2015/11/15.
 */
public class InteractionActivity extends Activity {
    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth";

    ImageView imageView;

    int type = 4;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interaction);

        //load OpenCV engine and init OpenCV library
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        Intent intent = getIntent();
        if (intent != null) {

            final String fn = intent.getStringExtra("originFileName");
            final int typeNo = intent.getIntExtra("interactionType", 4);

            originPhoto = fa.get_photo(fn);
            type = typeNo;

//            Log.d(TAG, "ori interaction" + Integer.toString(originPhoto.getWidth()) + " " + Integer.toString(originPhoto.getHeight()));
        }

        imageView = (ImageView) findViewById(R.id.imageInteraction);

        if (false) {
//            String nfn = Environment.getExternalStorageDirectory().getPath() + "/photo_booth_tmp.jpg";
            String fn = Environment.getExternalStorageDirectory().getPath() + "/photo_booth_ori.jpg";
            originPhoto = fa.get_photo(fn);
            photo = fa.kaleidoscope(originPhoto);
            type = 6;
        }

        switch (type) {
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
            case 9:
                photo = fa.water(originPhoto);
                imageView.setImageBitmap(fa.addCircle(photo));
                break;
            case 10:
                photo = fa.twirl(originPhoto);
                imageView.setImageBitmap(fa.addCircle(photo));
                break;
            case 11:
                photo = fa.ripple(originPhoto, 0);
                imageView.setImageBitmap(fa.addCircle(photo));
                break;
            case 12:
                photo = fa.ripple(originPhoto, 1);
                imageView.setImageBitmap(fa.addCircle(photo));
                break;
            case 13:
                photo = originPhoto;
                imageView.setImageBitmap(photo);
                break;
            case 14:
                photo = fa.ripple(originPhoto, 2);
                imageView.setImageBitmap(fa.addCircle(photo));
                break;



            default:
                photo = originPhoto;
                imageView.setImageBitmap(photo);

        }

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
//                            Log.d(TAG, "down");
                            break;
                        case MotionEvent.ACTION_UP:
//                            Log.d(TAG, "up");
//                            longPress = false;
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
                                redrawPhoto();;
                                pointMoved = false;
                            }

                            break;
                        case MotionEvent.ACTION_MOVE:
//                            Log.d(TAG, "move");
                            moveX = (int) event.getX();
                            moveY = (int) event.getY();
                            //if (longPress)
                            moveLine();
//                            String t = "mx = " + Integer.toString(moveX) + " my = " + Integer.toString(moveY);
//                            t += " xx = " + Integer.toString(lineX) + " yy = " + Integer.toString(lineY);
//                            t += " ix = " + Integer.toString(imageView.getWidth()) + " iy = " + Integer.toString(imageView.getHeight());
//                            Log.d(TAG, t);
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
                Intent interaction = new Intent(InteractionActivity.this, MainActivity.class);
                interaction.putExtra("resultName", "");
                interaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(interaction);
            }
        });

        ImageButton btn1 = (ImageButton) findViewById(R.id.btn_OK);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interaction = new Intent(InteractionActivity.this, MainActivity.class);
                interaction.putExtra("resultName", fa.save_photo(photo));
                interaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(interaction);
            }
        });

    };

    void redrawPhoto() {
        switch (type) {
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
            }
            case 9: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.water(originPhoto, px, py, circleScale);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
            case 10: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.twirl(originPhoto, px, py, circleScale);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
            case 11: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.ripple(originPhoto, px, py, circleScale, 0);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
            case 12: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.ripple(originPhoto, px, py, circleScale, 1);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
            case 14: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.ripple(originPhoto, px, py, circleScale, 2);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
            case 15: {
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                photo = fa.mosaic(originPhoto, px, py, circleScale);
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale));
                break;
            }
        }
    }

    void moveLine() {

        int threshold = 7;
        String color = "red";
        int dis;
        Log.d(TAG, "type " + Integer.toString(type));

        switch (type) {
            case 0: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));

//                if (Math.abs(moveX - pointX) >= threshold || Math.abs(moveY - pointY) >= threshold) {
//                    break;
//                }
//                dis = (int) Math.sqrt((moveX - pointX) * (moveX - pointX) + (moveY - pointY) * (moveY - pointY));
//                if (Math.abs(dis - circleScale) < threshold) {
//                    moveCircle = true;
//                    circleMoved = true;
//                }
//                if (moveCircle) {
//                    pointX = moveX;
//                    pointY = moveY;
//
//                    int px = pointX * pWidth / iWidth;
//                    int py = pointY * pHeight / iHeight;
//                    imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
//                }

                break;
            }
            case 1: {
                Log.d(TAG, "case 1 " + Integer.toString(moveY) + " " + Integer.toString(pointY));

                lineMoved = true;
                pointY = moveY;
                int row = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addLineRow(photo, row, color));

//                if (Math.abs(moveY - pointY) < threshold) {
//                    moveRow = true;
//                    lineMoved = true;
//                }
//                if (moveRow) {
////                    Log.d(TAG, "in?");
//                    pointY = moveY;
//                    int row = pointY * pHeight / iHeight;
////                    Log.d(TAG, Integer.toString(row));
//                    imageView.setImageBitmap(fa.addLineRow(photo, row, color));
//
//                }
                break;
            }
            case 2: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));

//                if (Math.abs(moveX - pointX) >= threshold || Math.abs(moveY - pointY) >= threshold) {
//                    break;
//                }
//                dis = (int) Math.sqrt((moveX - pointX) * (moveX - pointX) + (moveY - pointY) * (moveY - pointY));
//                if (Math.abs(dis - circleScale) < threshold) {
//                    moveCircle = true;
//                    circleMoved = true;
//                }
//                if (moveCircle) {
//                    pointX = moveX;
//                    pointY = moveY;
//
//                    int px = pointX * pWidth / iWidth;
//                    int py = pointY * pHeight / iHeight;
//                    imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
//                }

                break;
            }
            case 3: {
                lineMoved = true;
                pointX = moveX;
                int col = pointX * pWidth / iWidth;
                imageView.setImageBitmap(fa.addLineCol(photo, col, color));

//                if (Math.abs(moveX - pointX) < threshold) {
//                    moveCol = true;
//                    lineMoved = true;
//                }
//                if (moveCol) {
//                    pointX = moveX;
//                    int col = pointX * pWidth / iWidth;
//                    imageView.setImageBitmap(fa.addLineCol(photo, col, color));
//                }
                break;

            }


            case 4:
                break;
            case 5:{
                lineMoved = true;
                pointX = moveX;
                int col = pointX * pWidth / iWidth;
                imageView.setImageBitmap(fa.addLineCol(photo, col, color));

//                if (Math.abs(moveX - pointX) < threshold) {
//                    moveCol = true;
//                    lineMoved = true;
//                }
//                if (moveCol) {
//                    pointX = moveX;
//                    int col = pointX * pWidth / iWidth;
//                    imageView.setImageBitmap(fa.addLineCol(photo, col, color));
//                }
                break;
            }
            case 6: {
                pointMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addPoint(photo, px, py, color));
                break;
            }
            case 7: {
                lineMoved = true;
                pointY = moveY;
                int row = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addLineRow(photo, row, color));

//                if (Math.abs(moveY - pointY) < threshold) {
//                    moveRow = true;
//                    lineMoved = true;
//                }
//                if (moveRow) {
//                    pointY = moveY;
//                    int row = pointY * pHeight / iHeight;
//                    imageView.setImageBitmap(fa.addLineRow(photo, row, color));
//
//                }
                break;

            }
            case 9: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
                break;
            }
            case 10: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
                break;
            }
            case 11: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
                break;
            }
            case 12: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
                break;
            }
            case 14: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
                break;
            }
            case 15: {
                circleMoved = true;
                pointX = moveX;
                pointY = moveY;
                int px = pointX * pWidth / iWidth;
                int py = pointY * pHeight / iHeight;
                imageView.setImageBitmap(fa.addCircle(photo, px, py, circleScale, color));
                break;
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //load OpenCV engine and init OpenCV library
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }



    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        iWidth = imageView.getWidth();
        iHeight = imageView.getHeight();

        pWidth = photo.getWidth();
        pHeight = photo.getHeight();

        pointX = iWidth / 2;
        pointY = iHeight / 2;
//        String t = "1 ix = " + Integer.toString(iWidth) + " iy = " + Integer.toString(iHeight);
//        Log.d(TAG, t);

    }


    //OpenCV
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "加载成功");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

}
