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
import org.opencv.core.Mat;

import eleanor.photobooth.Functions.FunctionAccessor;
import eleanor.photobooth.Functions.FunctionImpl;
import eleanor.photobooth.Functions.PHOTOREQUESTCODE;

/**
 * Created by Eleanor on 2015/11/15.
 */
public class ChooseEffectActivity extends Activity {
    FunctionAccessor fa = new FunctionImpl();
    private static final String TAG = "photo_booth";
    Bitmap originPhoto;

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
            originPhoto = fa.get_photo(fn);

            ImageView iv4 = (ImageView) findViewById(R.id.imageView4);
            iv4.setImageBitmap(originPhoto);
            iv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    interaction.putExtra("interactionFileName", fn);
                    startActivity(interaction);
                }
            });

            //workout effects
            ImageView iv0 = (ImageView) findViewById(R.id.imageView0);
            final Bitmap bmp0 = fa.mirrorRight(originPhoto);
            iv0.setImageBitmap(bmp0);
            iv0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    String nfn = fa.save_photo(bmp0);
                    interaction.putExtra("interactionFileName", nfn);
                    interaction.putExtra("interactionType", 0);
                    startActivity(interaction);
                }
            });

            ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
            final Bitmap bmp1 = fa.mirrorUp(originPhoto);
            iv1.setImageBitmap(bmp1);
            iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    String nfn = fa.save_photo(bmp1);
                    interaction.putExtra("interactionFileName", nfn);
                    interaction.putExtra("interactionType", 1);
                    startActivity(interaction);
                }
            });

            ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
            final Bitmap bmp2 = fa.mirrorLeft(originPhoto);
            iv2.setImageBitmap(bmp2);
            iv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    String nfn = fa.save_photo(bmp2);
                    interaction.putExtra("interactionFileName", nfn);
                    interaction.putExtra("interactionType", 2);
                    startActivity(interaction);
                }
            });

            ImageView iv3 = (ImageView) findViewById(R.id.imageView3);
            final Bitmap bmp3 = fa.mirrorLeft(originPhoto);
            iv3.setImageBitmap(bmp3);
            iv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    String nfn = fa.save_photo(bmp3);
                    interaction.putExtra("interactionFileName", nfn);
                    interaction.putExtra("interactionType", 3);
                    startActivity(interaction);
                }
            });

            ImageView iv5 = (ImageView) findViewById(R.id.imageView5);
            final Bitmap bmp5 = fa.mirrorRight(originPhoto);
            iv5.setImageBitmap(bmp5);
            iv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    String nfn = fa.save_photo(bmp5);
                    interaction.putExtra("interactionFileName", nfn);
                    interaction.putExtra("interactionType", 5);
                    startActivity(interaction);
                }
            });

            ImageView iv6 = (ImageView) findViewById(R.id.imageView6);
            final Bitmap bmp6 = fa.mirrorUp(originPhoto);
            iv6.setImageBitmap(bmp6);
            iv6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    String nfn = fa.save_photo(bmp6);
                    interaction.putExtra("interactionFileName", nfn);
                    interaction.putExtra("interactionType", 6);
                    startActivity(interaction);
                }
            });

            ImageView iv7 = (ImageView) findViewById(R.id.imageView7);
            final Bitmap bmp7 = fa.mirrorDown(originPhoto);
            iv7.setImageBitmap(bmp7);
            iv7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interaction = new Intent(ChooseEffectActivity.this, InteractionActivity.class);
                    String nfn = fa.save_photo(bmp7);
                    interaction.putExtra("interactionFileName", nfn);
                    interaction.putExtra("interactionType", 7);
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
