package com.example.madcamp_week2.ui.main.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.madcamp_week2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class GalleryFragment : Fragment() {
    var deleteList: List<String> = ArrayList()
    var myImage: ImageView? = null
    var textView: TextView? = null
    private var fab_main: FloatingActionButton? = null
    private var fab_sub1: FloatingActionButton? = null
    private var fab_sub2: FloatingActionButton? = null
    private var isFabOpen = false
    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    var delButton: ImageButton? = null
    var addButton: ImageButton? = null
    var directory_size = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun FragmentRefresh() {
        val transaction =
            fragmentManager!!.beginTransaction()
        transaction.detach(this).attach(this).commit()
    }

    var mCurrentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val files = storageDir!!.listFiles()
        directory_size = files.size
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        mCurrentPhotoPath = image.absolutePath
        //System.out.println("directory : "+storageDir);
        FragmentRefresh()
        return image
    }

    private fun toggleFab() {
        if (isFabOpen) {
            fab_main!!.setImageResource(R.drawable.ic_add)
            fab_sub1!!.startAnimation(fab_close)
            fab_sub2!!.startAnimation(fab_close)
            fab_sub1!!.isClickable = false
            fab_sub2!!.isClickable = false
            isFabOpen = false
        } else {
            fab_main!!.setImageResource(R.drawable.ic_close)
            fab_sub1!!.startAnimation(fab_open)
            fab_sub2!!.startAnimation(fab_open)
            fab_sub1!!.isClickable = true
            fab_sub2!!.isClickable = true
            isFabOpen = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, containter: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_gallery, null)
        textView = view.findViewById(R.id.noImages)
        val storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val files = storageDir!!.listFiles()
        directory_size = files.size
        //System.out.println(directory_size);
        if (directory_size > 0) {
            textView!!.setVisibility(View.INVISIBLE)
        }
        val gridView = view.findViewById<GridView>(R.id.gridView)
        val imageAdapter = ImageAdapter(requireActivity())
        deleteList = imageAdapter.deleteImages
        gridView.adapter = imageAdapter
        //        delButton = view.findViewById(R.id.imageButton2);
//        addButton = view.findViewById(R.id.imageButton);
        val REQUEST_TAKE_PHOTO = 1
        //checkCameraPermission()
        fab_open = AnimationUtils.loadAnimation(activity, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(activity, R.anim.fab_close)
        fab_main = view.findViewById<View>(R.id.fab_main) as FloatingActionButton
        fab_sub1 = view.findViewById<View>(R.id.fab_sub1) as FloatingActionButton
        fab_sub2 = view.findViewById<View>(R.id.fab_sub2) as FloatingActionButton
        fab_main!!.setOnClickListener { toggleFab() }
        fab_sub1!!.setOnClickListener {
            toggleFab()
            if (deleteList.size > 0) {
                println("have delete items")
                try {
                    for (i in deleteList.indices) {
                        val tmpFile = File(deleteList[i])
                        if (tmpFile.exists()) {
                            //해당 경로에 파일 존재하는지 확인
                            tmpFile.delete()
                            FragmentRefresh()
                            //                                Intent intent = new Intent(getActivity(), MainActivity.class);
                            //                                Bundle bundle = new Bundle();
                            //                                bundle.putInt("item_num", 1);
                            //                                intent.putExtras(bundle);
                            //                                getActivity().startActivity(intent);
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                println("no delete items")
            }
        }
        fab_sub2!!.setOnClickListener {
            toggleFab()
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), 3/*REQUEST */)
            } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
                    //Create file where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(requireActivity(), "com.example.android.fileprovider", photoFile)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        val f = File(mCurrentPhotoPath!!)
                        val contentUri = Uri.fromFile(f)
                        mediaScanIntent.data = contentUri
                        activity!!.sendBroadcast(mediaScanIntent)
                    }
                }
            }
        }
        return view
    }

    private fun checkCameraPermission() : Boolean {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 3) //MY_PERMISSIONS_REQUEST_PERMISSION)
            return false
        }
        return true
    }

    companion object {
        const val REQUEST = 0
        const val REQUEST2 = 0
    }
}