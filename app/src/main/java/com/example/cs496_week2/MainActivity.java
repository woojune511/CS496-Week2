package com.example.cs496_week2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.tabs.TabLayout;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private PagerAdapter mPagerAdapter;
    Button btn_custom_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_custom_logout = (Button) findViewById(R.id.btn_custom_logout);
        btn_custom_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(), "Signed out successfully", LENGTH_LONG).show();
            }
        });

        TextView textView = findViewById(R.id.txtv);

//        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
//
//        TabLayout.Tab phoneTab = mTabLayout.newTab();
//        phoneTab.setText("연락처");
//        mTabLayout.addTab(phoneTab);
//
//        TabLayout.Tab galleryTab = mTabLayout.newTab();
//        galleryTab.setText("갤러리");
//        mTabLayout.addTab(galleryTab);
//
//        TabLayout.Tab thirdTab = mTabLayout.newTab();
//        thirdTab.setText("세번째탭");
//        mTabLayout.addTab(thirdTab);
//
//        mViewPager = (ViewPager) findViewById(R.id.pager_content);
//        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
//        mViewPager.setAdapter(mPagerAdapter);
//        mViewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
//
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        if(bundle!=null) {
//            int tab_num = bundle.getInt("item_num");
//            System.out.println("num : "+tab_num);
//            mViewPager.setCurrentItem(tab_num);
//        } else{
//            System.out.println("bundle null!");
//        }
//
//
//        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int pos = tab.getPosition();
//                mViewPager.setCurrentItem(pos);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                int pos = tab.getPosition();
//
//            }
//        };
//        onTabSelectedListener.onTabSelected(mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()));
//        mTabLayout.addOnTabSelectedListener(onTabSelectedListener);
    }
}