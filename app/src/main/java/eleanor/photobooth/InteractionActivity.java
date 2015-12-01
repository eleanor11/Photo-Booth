package eleanor.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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

    Bitmap originPhoto;
    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interaction);

        Intent intent = getIntent();
        if (intent != null) {
            //byte[] bis = intent.getByteArrayExtra("bitmap");
            //Bitmap photo=BitmapFactory.decodeByteArray(bis, 0, bis.length);

            //String picturePath = intent.getParcelableExtra("string");
            //Bitmap photo = BitmapFactory.decodeFile(picturePath);

//            Bundle bundle = intent.getExtras();
//            Bitmap photo = bundle.getParcelable("bitmap");

            final String fn = intent.getStringExtra("interactionFileName");
            final int type = intent.getIntExtra("interactionType", 4);
            originPhoto = fa.get_photo(fn);
            photo = originPhoto;

            ImageView imageView = (ImageView) findViewById(R.id.imageInteraction);

            switch (type) {
                case 0:
                    break;
                case 1:
                    imageView.setImageBitmap(fa.addLineRow(photo, -1));
                    break;
                case 2:
                    break;
                case 3:
                    imageView.setImageBitmap(fa.addLineCol(photo, -1));
                    break;
                case 4:
                    imageView.setImageBitmap(photo);
                    break;
                case 5:
                    imageView.setImageBitmap(fa.addLineCol(photo, -1));
                    break;
                case 6:
                    break;
                case 7:
                    imageView.setImageBitmap(fa.addLineRow(photo, -1));
                    break;
                default:
                    imageView.setImageBitmap(photo);

            }

        }

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
