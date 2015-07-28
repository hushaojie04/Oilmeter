package sj.android.oilmeter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2015/7/27.
 */
public class MainActivity extends Activity {
    OilMeterView oilMeterView;
    TextView textView;
    OilMeterView2 oilMeterView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        oilMeterView = (OilMeterView) findViewById(R.id.oil);
        textView = (TextView) findViewById(R.id.textView);
        oilMeterView2 = (OilMeterView2) findViewById(R.id.image);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("husj", "onProgressChanged " + progress);
                float value = 10 * ((float) progress / 1000.0f);
                DecimalFormat df = new DecimalFormat("0.00");
                textView.setText(df.format(value));
//                oilMeterView.setFloatToAngle(value);
                oilMeterView2.setFloatToAngle(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}
