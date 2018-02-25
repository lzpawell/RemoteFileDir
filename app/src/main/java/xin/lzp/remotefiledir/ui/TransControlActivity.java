package xin.lzp.remotefiledir.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import xin.lzp.remotefiledir.R;

//传输控制页面， 这里
public class TransControlActivity extends AppCompatActivity {

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_trans_file);


    }

}
