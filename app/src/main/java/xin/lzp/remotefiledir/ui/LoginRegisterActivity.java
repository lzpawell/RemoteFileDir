package xin.lzp.remotefiledir.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.ui.fragment.LoginFragment;
import xin.lzp.remotefiledir.ui.fragment.RegisterFragment;

public class LoginRegisterActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static final String ENTER_INTENT = "intent";
    public static final String ENTER_MAIN = "ENTER_MAIN";
    public static final String ENTER_REQUEST_LOGIN = "ENTER_REQUEST_LOGIN";


    public static final int EXIT_LOGIN_SUCCEED = 0;
    public static final int EXIT_LOGIN_FAILED = 1;

    private String enterIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enterIntent = getIntent().getStringExtra(ENTER_INTENT);
        if(enterIntent == null || enterIntent.equals(""))
            enterIntent = ENTER_MAIN;

        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp_login_register);

        final Fragment[] fragments = new Fragment[2];
        fragments[0] = LoginFragment.newInstance(new LoginFragment.OnRequestLoginCallback() {
            @Override
            public void loginResult(boolean result) {
                int loginResult;
                if(result == true){
                    loginResult = EXIT_LOGIN_SUCCEED;
                }else{
                    loginResult = EXIT_LOGIN_FAILED;
                }

                if(enterIntent.equals(ENTER_REQUEST_LOGIN)){
                    LoginRegisterActivity.this.setResult(loginResult);
                    LoginRegisterActivity.this.finish();
                }else{
                    if(result == true){
                        Intent intent = new Intent(LoginRegisterActivity.this, FTPFileListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        fragments[1] = new RegisterFragment();
        final String[] titles = new String[]{"登录", "注册"};

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.login_tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        tabLayout.addTab(tabLayout.newTab().setText(titles[0]));//添加tab选项卡
        tabLayout.addTab(tabLayout.newTab().setText(titles[1]));

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.setTabsFromPagerAdapter(fragmentPagerAdapter);
        UITool.setIndicator(tabLayout, 60, 60);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                // 监听到返回按钮点击事件
                this.setResult(EXIT_LOGIN_FAILED);
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
