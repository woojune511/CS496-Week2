package com.example.madcamp_week2.ui.main.gallery

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.madcamp_week2.MainActivity
import com.example.madcamp_week2.R
import java.io.File
import java.util.*

class SliderAdapter(private val Cont: Context) : PagerAdapter() {
    var images: MutableList<String> = ArrayList()
    private var inflater: LayoutInflater? = null
    var delButton: ImageButton? = null
    var imageView: ImageView? = null
    var storageDir: File?
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = Cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflater!!.inflate(R.layout.item_image_slider, container, false)

        delButton = v.findViewById<View>(R.id.imageButton) as ImageButton
        imageView = v.findViewById<View>(R.id.imageView) as ImageView
        val bmOptions = BitmapFactory.Options()
        val myBitmap = BitmapFactory.decodeFile(images[position], bmOptions)
        imageView!!.setImageBitmap(myBitmap)
        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 1200)
        imageView!!.layoutParams = layoutParams
        imageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView!!.rotation = 90f
        container.addView(v)
        delButton!!.setOnClickListener {
            try {
                val tmpFile = File(images[position])
                if (tmpFile.exists()) {
                    //해당 경로에 파일 존재하는지 확인
                    tmpFile.delete()
                    val intent = Intent(Cont, MainActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("item_num", 1)
                    intent.putExtras(bundle)
                    Cont.startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return v
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.invalidate()
    }

    //BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
    //myImage.setImageBitmap(myBitmap);
    init {
        storageDir = Cont.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val files = storageDir!!.listFiles()
        val directory_size = files.size
        for (i in 0 until directory_size) {
            images.add(storageDir.toString() + "/" + files[i].name)
        }
    }
}