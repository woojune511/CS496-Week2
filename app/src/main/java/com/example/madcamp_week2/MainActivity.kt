package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.madcamp_week2.ui.main.SectionsPagerAdapter
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.Path

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var intent: Intent = intent
        var user_name: String = intent.getStringExtra("user_name")!!
        var user_email: String = intent.getStringExtra("user_email")!!
        var user_id: String = intent.getStringExtra("user_id")!!

        // DB에서 유저 검색하고 uid 가져옴
        // id와 이메일이 둘 다 존재하지 않는다? -> 새로운 페이스북 계정 등록,
        // id는 없는데 이메일이 존재한다? -> 기존 계정에 페이스북 ID만 등록
        // id와 이메일이 둘 다 존재한다? 혹은 id만 있고 이메일이 없다?-> uid만 가져오고 끝

//        findUserbyFB(user_id, email)


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

//        var session: Session = Session.getActive

//        val fab: FloatingActionButton = findViewById(R.id.fab)
//
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
    }
}