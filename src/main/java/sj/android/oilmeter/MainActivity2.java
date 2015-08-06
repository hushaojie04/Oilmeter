package sj.android.oilmeter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2015/7/27.
 */
public class MainActivity2 extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String data = getIntent().getStringExtra("data");
        if (data.equals("jihe")) {
            String type = getIntent().getStringExtra("type");
            setContentView(R.layout.main_layout3);
            CanvasView canvasView = (CanvasView) findViewById(R.id.canvasView);
            int index = Integer.parseInt(type.substring(type.indexOf("_")+1));
            canvasView.doDraw(index);
        } else if (data.equals("gallery")) {
            setContentView(R.layout.main_layout2);
        }
//        GalleryView view = (GalleryView) findViewById(R.id.galleryView);
    }


}
