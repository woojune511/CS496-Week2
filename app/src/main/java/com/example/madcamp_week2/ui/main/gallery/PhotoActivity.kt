package com.example.madcamp_week2.ui.main.gallery

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.madcamp_week2.R

class PhotoActivity : AppCompatActivity() {
    var mImageView: ImageView? = null
    var mViewPager: ViewPager? = null
    var mAdapter: SliderAdapter? = null
    var delButton: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_image)
        mViewPager = findViewById<View>(R.id.viewpager) as ViewPager
        val intent = intent
        val pos = intent.extras!!.getInt("pos")
        mAdapter = SliderAdapter(this)
        delButton = mAdapter!!.delButton
        mViewPager!!.adapter = mAdapter
        mViewPager!!.currentItem = pos
    }
}