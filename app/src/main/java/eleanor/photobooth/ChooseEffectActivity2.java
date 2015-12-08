package eleanor.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import eleanor.photobooth.Functions.FunctionAccessor;
import eleanor.photobooth.Functions.FunctionImpl;

/**
 * Created by Eleanor on 2015/12/7.
 */
public class ChooseEffectActivity2 extends Activity{
    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth";
    Bitmap originPhoto;
    Bitmap smallPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_effect_2);

        Intent intent = getIntent();
        if (intent != null) {
            final String fn = intent.getStringExtra("fileName");

            originPhoto = fa.get_photo(fn);
            Log.d(TAG, "ori0" + Integer.toString(originPhoto.getWidth()) + " " + Integer.toString(originPhoto.getHeight()));
            smallPhoto = fa.rescale_photo(originPhoto, 0.3f);
            Log.d(TAG, "ori1" + Integer.toString(originPhoto.getWidth()) + " " + Integer.toString(originPhoto.getHeight()));

            Log.d(TAG, "chooseeffectstart2");

            ImageView iv4 = (ImageView) findViewById(R.id.imageView4);
            iv4.setImageBitmap(smallPhoto);
            iv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity2.this, InteractionActivity.class);
                    interaction.putExtra("originFileName", fn);
                    startActivity(interaction);
                }
            });


            //workout effects
            ImageView iv0 = (ImageView) findViewById(R.id.imageView0);
            final Bitmap bmp0 = fa.water(smallPhoto);
            iv0.setImageBitmap(bmp0);
            iv0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity2.this, InteractionActivity.class);
                    interaction.putExtra("originFileName", fn);
                    interaction.putExtra("interactionType", 9);
                    startActivity(interaction);
                }
            });

            ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
            final Bitmap bmp1 = fa.mosaic(smallPhoto);
            iv1.setImageBitmap(bmp1);
            iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity2.this, InteractionActivity.class);
                    interaction.putExtra("originFileName", fn);
                    interaction.putExtra("interactionType", 10);
                    startActivity(interaction);
                }
            });

            ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
            final Bitmap bmp2 = fa.ripple(smallPhoto, 0);
            iv2.setImageBitmap(bmp2);
            iv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity2.this, InteractionActivity.class);
                    interaction.putExtra("originFileName", fn);
                    interaction.putExtra("interactionType", 11);
                    startActivity(interaction);
                }
            });

            ImageView iv3 = (ImageView) findViewById(R.id.imageView3);
            final Bitmap bmp3 = fa.ripple(smallPhoto, 1);
            iv3.setImageBitmap(bmp3);
            iv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity2.this, InteractionActivity.class);
                    interaction.putExtra("originFileName", fn);
                    interaction.putExtra("interactionType", 12);
                    startActivity(interaction);
                }
            });

            ImageView iv5 = (ImageView) findViewById(R.id.imageView5);
            final Bitmap bmp5 = fa.ripple(smallPhoto, 2);
            iv5.setImageBitmap(bmp5);
            iv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity2.this, InteractionActivity.class);
                    interaction.putExtra("originFileName", fn);
                    interaction.putExtra("interactionType", 14);
                    startActivity(interaction);
                }
            });

            ImageView iv6 = (ImageView) findViewById(R.id.imageView6);
            final Bitmap bmp6 = fa.kaleidoscope(smallPhoto);
            iv6.setImageBitmap(bmp6);
            iv6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity2.this, InteractionActivity.class);
                    interaction.putExtra("originFileName", fn);
                    interaction.putExtra("interactionType", 15);
                    startActivity(interaction);
                }
            });


            ImageView iv8 = (ImageView) findViewById(R.id.imageView8);
            iv8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChooseEffectActivity2.this.finish();
                }
            });

            Log.d(TAG, "chooseeffectend");
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
