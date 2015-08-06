package sj.android.oilmeter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/8/6.
 */
public class MainActivityMain extends Activity implements View.OnClickListener {
    ArrayAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        Spinner spinner2 = (Spinner) findViewById(R.id.Spinner01);

        //将可选内容与ArrayAdapter连接起来
         adapter2 = ArrayAdapter.createFromResource(this, R.array.jihe, android.R.layout.simple_spinner_item);

        //设置下拉列表的风格
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter2 添加到spinner中
        spinner2.setAdapter(adapter2);

        //添加事件Spinner事件监听
        spinner2.setOnItemSelectedListener(new SpinnerXMLSelectedListener());

        //设置默认值
        spinner2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                startActivity(MainActivityMain.this, MainActivity.class, "", "");
                break;
            case R.id.btn2:
                startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "");
                break;
        }
    }

    //使用XML形式操作
    class SpinnerXMLSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            Toast.makeText(MainActivityMain.this, (String) adapter2.getItem(arg2), Toast.LENGTH_LONG).show();

            switch (arg2) {
                case 1:
                    startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "jihe_1");
                    break;
                case 2:
                    startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "jihe_2");
                    break;
                case 3:
                    startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "jihe_3");
                    break;
                case 4:
                    startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "jihe_4");
                    break;
                case 5:
                    startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "jihe_5");
                    break;
                case 6:
                    startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "jihe_6");
                    break;
                case 7:
                    startActivity(MainActivityMain.this, MainActivity2.class, "jihe", "jihe_7");
                    break;
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {

        }

    }

    public void startActivity(Context packageContext, Class<?> cls, String data, String type) {
        Intent intent = new Intent(packageContext, cls);
        intent.putExtra("data", data);
        intent.putExtra("type", type);

        startActivity(intent);
    }
}
