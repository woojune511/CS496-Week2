package com.example.madcamp_week2.ui.main.gallery

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.madcamp_week2.IMyService
import com.example.madcamp_week2.R
import com.example.madcamp_week2.UserInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

    var mBitmap: Bitmap? = null
    var mFilePath: String? = null
    var imageView: ImageView? = null
    var picUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    val API_URL: String = "http://192.249.19.242:6180/"
    private val IMAGE_RESULT = 200

    var userinfo: UserInfo? = null

    val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS).build()

    val iMyService: IMyService = Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client).build().create<IMyService>(IMyService::class.java)


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


    fun getPickImageChooserIntent(): Intent? {
        // image choosing intent
        val outputFileUri: Uri = getCaptureImageOutputUri()
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager: PackageManager = context!!.getPackageManager()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam =
            packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery =
            packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }
        var mainIntent = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component
                    !!.className == "com.android.documentsui.DocumentsActivity"
            ) {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())
        return chooserIntent
    }

    private fun getCaptureImageOutputUri(): Uri {
        var outputFileUri: Uri? = null
        val getImage: File? = context!!.getExternalFilesDir("")
        if (getImage != null) {
            outputFileUri =
                Uri.fromFile(File(getImage.path, "profile.png"))
        }
        return outputFileUri!!
    }

     override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == IMAGE_RESULT) {

                // 보내는 file path
                mFilePath = getImageFilePath(data)
                if (mFilePath != null) {
                    println(mFilePath)
                    mBitmap = BitmapFactory.decodeFile(mFilePath)
                }
            }
        }
    }

    private fun getImageFromFilePath(data: Intent?): String? {
        val isCamera = data == null || data.data == null
        return if (isCamera) getCaptureImageOutputUri()!!.path else getPathFromURI(data!!.data)
    }

    fun getImageFilePath(data: Intent?): String? {
        return getImageFromFilePath(data)
    }

    private fun getPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor =
            context!!.getContentResolver().query(contentUri!!, proj, null, null, null)!!
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("pic_uri", picUri)
    }



    private fun showMessageOKCancel(
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    private fun multipartImageUpload() {
        try {
            val filesDir: File = context!!.getFilesDir()
            val file = File(filesDir, "image" + ".png")
            val bos = ByteArrayOutputStream()
            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
            val body =
                MultipartBody.Part.createFormData("upload", file.name, reqFile)
            println("file path : $mFilePath")
            val pathName: RequestBody =
                RequestBody.create(MediaType.parse("text/plain"), mFilePath)
            val name =
                RequestBody.create(MediaType.parse("text/plain"), "upload")

            // for image upload
            val req: Call<ResponseBody?>? = iMyService.postImage(body, name, pathName)
            req?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.code() == 200 || response.code() == 404) {
                        textView!!.text = "Uploaded Successfully!"
                        textView!!.setTextColor(Color.BLUE)
                    }
                    Toast.makeText(
                        context,
                        response.code().toString() + " ",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                ) {
                    textView!!.text = "Uploaded Failed!"
                    textView!!.setTextColor(Color.RED)
                    Toast.makeText(context, "Request failed", Toast.LENGTH_SHORT)
                        .show()
                    t.printStackTrace()
                }
            })
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
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
                        if (mBitmap != null)
                            multipartImageUpload()

                    }
                }
            }
        }
        return view
    }

    companion object {
        const val REQUEST = 0
        const val REQUEST2 = 0
    }
}