package com.example.madcamp_week1.ui.main.gallery

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week1.R
import com.example.madcamp_week1.utils.SpacesItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class GalleryFragment : Fragment(), NumberPicker.OnValueChangeListener{
    private lateinit var recyclerView : RecyclerView
    private lateinit var img_paths : MutableList<String>
    private var cnt = 1
    private var currentPhotoPath : String? = null

    val REQUEST_IMAGE_CAPTURE = 1

    /*
    private fun saveImage(drawableId : Int) : String {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId)
        var file = context?.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "image${cnt++}.jpg")
        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return file.toString()
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        img_paths = mutableListOf<String>()

        val imgDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        imgDir.list()?.forEach{
            img_paths.add(imgDir.toString() + File.separator + it)
            Log.d("ImgPath", img_paths[img_paths.size-1])
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.apply{
            layoutManager = GridLayoutManager(activity, 2)
            adapter = GalleryViewAdapter(context, img_paths){
                askRemove(it)
                true
            }
        }
        recyclerView.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_width)))
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_gallery1 -> showColumnOption()
            R.id.action_gallery2 -> getNewImage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showColumnOption(){
        val d = Dialog(requireContext())
        d.setTitle("NumberPicker")
        d.setContentView(R.layout.dialog_number)
        val b1 = d.findViewById<Button>(R.id.button1)
        val np = d.findViewById<NumberPicker>(R.id.numberPicker1)
        np.maxValue = 5; np.minValue = 1
        np.wrapSelectorWheel = false
        np.setOnValueChangedListener(this)
        b1.setOnClickListener(View.OnClickListener {
            val recyclerView = requireView().findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.layoutManager = GridLayoutManager(activity, np.value)
            d.dismiss()
        })
        d.show()
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {}

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = this.absolutePath
        }
    }

    private fun getNewImage() {
        Log.d("newImage", ">>getNewImage")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also{
                val photoFile: File? = try{
                    createImageFile()
                } catch (e: IOException) {
                    null
                }
                Log.d("newImage", photoFile.toString())
                photoFile?.also{
                    try{
                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.android.fileprovider",
                            it
                        )
                        Log.d("newImage", photoURI.toString())
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        Log.d("newImage", "successful")
                    }catch(e: Exception) {
                        Log.d("newImage", e.toString())
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && currentPhotoPath != null){
            val adapter = recyclerView.adapter
            Log.d("TAG", adapter!!.itemCount.toString())
            img_paths.add(currentPhotoPath!!)
            Log.d("TAG", adapter.itemCount.toString())
            adapter.notifyItemInserted(img_paths.size-1)
            Log.d("ActivityResult","Added image from $currentPhotoPath")
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED && currentPhotoPath != null){
            File(currentPhotoPath).delete()
            Log.d("ActivityResult","Removed image from $currentPhotoPath")
        }
    }

    private fun askRemove(position : Int) {
        val items = arrayOf("삭제")
        MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
            .setItems(items) { dialog, which ->
                // Respond to item chosen
                when(which) {
                    0 -> {
                        val adapter = recyclerView.adapter!!
                        val imgPath = img_paths[position]
                        img_paths.removeAt(position)
                        Log.d("TAG", adapter.itemCount.toString())
                        adapter.notifyItemRemoved(position)
                        File(imgPath).delete()
                    }
                }
            }
            .show()
    }
}