package eleanor.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import eleanor.photobooth.Functions.FunctionAccessor;
import eleanor.photobooth.Functions.FunctionImpl;
import eleanor.photobooth.Functions.PHOTOREQUESTCODE;

/**
 * Created by Eleanor on 2015/11/15.
 */
public class ChooseEffectActivity extends Activity {
    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_effect);

        Intent intent = getIntent();
        if (intent != null) {
            //byte[] bis = intent.getByteArrayExtra("bitmap");
            //Bitmap photo=BitmapFactory.decodeByteArray(bis, 0, bis.length);

            //String picturePath = intent.getParcelableExtra("string");
            //Bitmap photo = BitmapFactory.decodeFile(picturePath);

//            Bundle bundle = intent.getExtras();
//            Bitmap photo = bundle.getParcelable("bitmap");

            final String fn = intent.getStringExtra("fileName");
            Bitmap photo = fa.get_photo(fn);

            ImageView ib4 = (ImageView) findViewById(R.id.imageView4);
            ib4.setImageBitmap(photo);
            ib4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    interaction.putExtra("interactionFileName", fn);
                    startActivity(interaction);
                }
            });
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
