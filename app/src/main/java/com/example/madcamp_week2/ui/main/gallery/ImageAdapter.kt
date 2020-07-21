package com.example.madcamp_week2.ui.main.gallery

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import com.example.madcamp_week2.R
import java.io.File
import java.util.*

class ImageAdapter(private val Cont: Context) : BaseAdapter() {
    var thumbImages: MutableList<String> =
        ArrayList()
    var deleteImages: MutableList<String> =
        ArrayList()
    var storageDir: File?
    override fun getCount(): Int {
        return thumbImages.size
    }

    override fun getItem(i: Int): Any {
        return 0
        //return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val vi = Cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val secondView: View = vi.inflate(R.layout.fragment_gallery, null)
        val imgView = ImageView(Cont)
        val delButton = secondView.findViewById<ImageButton>(R.id.fab_sub1)
        imgView.layoutParams = AbsListView.LayoutParams(370, 370)
        imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        imgView.setPadding(1, 1, 1, 1)
        val bmOptions = BitmapFactory.Options()
        val myBitmap = BitmapFactory.decodeFile(thumbImages[i], bmOptions)
        imgView.setImageBitmap(myBitmap)
        imgView.rotation = 90f
        notifyDataSetChanged()
        imgView.setOnClickListener {
            //Toast.makeText(Cont, Integer.toString(i), Toast.LENGTH_SHORT).show();
            val intent = Intent(Cont, PhotoActivity::class.java)
            intent.putExtra("pos", i)
            Cont.startActivity(intent)
            storageDir = Cont.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val files = storageDir!!.listFiles()
            val directory_size = files.size // 실제 경로에 있는 파일 수
            if (thumbImages.size != directory_size) {
                //만약 실제 경로의 파일 수와, 내 local 파일 수가 다르다면, 삭제를 했다고 가정
                thumbImages.removeAt(i)
                notifyDataSetChanged()
            }
        }
        imgView.setOnLongClickListener {
            if (imgView.colorFilter != null) {
                imgView.colorFilter = null
                deleteImages.remove(thumbImages[i])
            } else {
                imgView.setColorFilter(
                    Color.parseColor("#575655"),
                    PorterDuff.Mode.MULTIPLY
                )
                //System.out.println(imgView.getColorFilter().hashCode());
                deleteImages.add(thumbImages[i])
            }
            if (deleteImages.size > 0) {
                //System.out.println("delete");
                delButton.visibility = View.VISIBLE
            }
            true
        }
        return imgView
    }

    init {
        //Integer imgNumber = R.drawable.img1;
        storageDir = Cont.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val files = storageDir!!.listFiles()
        val directory_size = files.size
        for (i in 0 until directory_size) {
            thumbImages.add(storageDir.toString() + "/" + files[i].name)
        }
    }
}