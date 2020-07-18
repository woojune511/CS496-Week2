package com.example.madcamp_week1

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.example.madcamp_week1.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.exitProcess

val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

class MainActivity : ThemeChangeActivity(true) {
    private val MY_PERMISSIONS_REQUEST_PERMISSION = 1
    private val REQUEST_SETTING = 2
    private val TAG = "mainTAG"
    private lateinit var curTheme: String
    private fun writeImagesToStorage() {
        Log.d("Gallery", ">> writeImagesToStorage")
        val assetManager = assets
//        if (File(filesDir.absolutePath + File.separator + "images").exists()){
//            Log.d("Gallery", "images directory already exists")
//            return
//        }
        val images = assetManager.list("gallery")
        if (images != null) {
            //Log.d("Gallery", "${imagePaths.get(1)}")
            val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            if(dir.list()!!.isNotEmpty()) return
            for(image in images){
                //Log.d("Gallery", "$image")
                try{
                    val srcFile = "gallery" + File.separator +"$image"
                    val input = assetManager.open(srcFile)
                    val output = FileOutputStream(File(dir, image))
                    input.copyTo(output)

                }catch(e: IOException){
                    Log.d("Gallery", "IO Exception")
                }catch(e: FileNotFoundException){
                    Log.d("Gallery", "File Not Found Exception")
                }catch(e: Exception){
                    e.printStackTrace()
                    exitProcess(0)
                }
            }
        }

        Log.d("Gallery", "<< writeImagesToStorage")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(true)
        ab.setTitle(TAB_TITLES[0])

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                ab.setTitle(TAB_TITLES[position])
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                invalidateOptionsMenu()
            }
        })
        viewPager.offscreenPageLimit = 3

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        writeImagesToStorage()
    }

    // Adjust visibility of each options in menu_main
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        when(findViewById<ViewPager>(R.id.view_pager).currentItem){
            0 -> {
                menu!!.setGroupVisible(R.id.menu_group_contact, true)
                menu!!.setGroupVisible(R.id.menu_group_gallery, false)
                menu!!.setGroupVisible(R.id.menu_group_tab3, false)

            }
            1 -> {
                menu!!.setGroupVisible(R.id.menu_group_contact, false)
                menu!!.setGroupVisible(R.id.menu_group_gallery, true)
                menu!!.setGroupVisible(R.id.menu_group_tab3, false)
            }
            2 -> {
                menu!!.setGroupVisible(R.id.menu_group_contact, false)
                menu!!.setGroupVisible(R.id.menu_group_gallery, false)
                menu!!.setGroupVisible(R.id.menu_group_tab3, true)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_settings -> {
//                setTheme(R.style.AppTheme2)
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, REQUEST_SETTING)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_SETTING -> {
//                if( data.extras["hei"] != cur_theme)
                Log.d("TAG", ">>Recreated")
                this.recreate()
            }
        }
    }
}