package eleanor.photobooth;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;

import eleanor.photobooth.Functions.FunctionAccessor;
import eleanor.photobooth.Functions.FunctionImpl;
import eleanor.photobooth.Functions.PHOTOREQUESTCODE;

public class MainActivity extends Activity {

    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth";

    public static final String IMAGE_UNSPECIFIED = "image/*";
    ImageView imageView = null;
    Button button0 = null;
    Button button1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageID);
        button0 = (Button) findViewById(R.id.btn_01);
        button1 = (Button) findViewById(R.id.btn_02);

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = fa.get_photo_from_album();
                startActivityForResult(intent, PHOTOREQUESTCODE.ORIGINPIC.toInt());
                //startActivityForResult(intent, PHOTOREQUESTCODE.PHOTOZOOM.toInt());
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = fa.take_photo();
                startActivityForResult(intent, PHOTOREQUESTCODE.PHOTORAPH.toInt());

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == PHOTOREQUESTCODE.NONE.toInt())
            return;
        if (requestCode == PHOTOREQUESTCODE.PHOTOZOOM.toInt()){
            if (data == null) return;
            Intent intent = fa.zoom_photo(data.getData(), false);
            startActivityForResult(intent, PHOTOREQUESTCODE.PHOTORESULT.toInt());
        }
        if (requestCode == PHOTOREQUESTCODE.PHOTORAPH.toInt()){
            File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
            Intent intent = fa.zoom_photo(Uri.fromFile(picture), false);
            startActivityForResult(intent, PHOTOREQUESTCODE.PHOTORESULT.toInt());
        }
        if (requestCode == PHOTOREQUESTCODE.PHOTORESULT.toInt()){
            if (data == null) return;
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件
                ImageView imageView = (ImageView) findViewById(R.id.imageID);
                imageView.setImageBitmap(photo);

                Intent chooseEffect = new Intent(MainActivity.this, ChooseEffectActivity.class);
                chooseEffect.putExtras(extras);
                startActivity(chooseEffect);
            }
        }
        if (requestCode == PHOTOREQUESTCODE.ORIGINPIC.toInt()) {
            if (data == null) return;

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            ImageView imageView = (ImageView) findViewById(R.id.imageID);
            imageView.setImageBitmap(bitmap);

            Intent chooseEffect = new Intent(MainActivity.this, ChooseEffectActivity.class);
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
            //byte[] bitmapByte = baos.toByteArray();
            //chooseEffect.putExtra("string", picturePath);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("bitmap", bitmap);
//            chooseEffect.putExtras(bundle);

            String fn = fa.save_photo(bitmap);
            chooseEffect.putExtra("fileName", fn);

            startActivity(chooseEffect);
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
