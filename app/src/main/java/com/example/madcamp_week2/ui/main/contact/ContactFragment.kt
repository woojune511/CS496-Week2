package com.example.madcamp_week2.ui.main.contact

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.madcamp_week2.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.typeOf

class ContactFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var pBooksList: MutableList<PhoneBook>
    val DIALOG_REQUEST_CODE: Int = 1234
    lateinit var addButton: ImageButton
    var compositeDisposable = CompositeDisposable()
    var iMyService: IMyService? = null
    private val REQUEST: Int = 0
    private val REQUEST2: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
        var retrofit: Retrofit
        val retrofitClient = RetrofitClient.instance

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return inflater.inflate(R.layout.fragment_contact, container, false)
        //setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        val cl = view.findViewById<View>(R.id.ConstraintLayout) as ConstraintLayout
        recyclerView = view.findViewById(R.id.phoneList)
        recyclerView.apply {
            adapter = ContactViewAdapter(context, mutableListOf())
            layoutManager = LinearLayoutManager(context)
        }

        var addButton = view.findViewById<View>(R.id.AddButton) as ImageButton
        addButton.setOnClickListener {
            Log.d("button", "add button!")
            val intent = Intent(activity, ContactFAB::class.java)
            startActivityForResult(intent, 1)
        }

        showContacts()
//        if (checkLocationPermission()) showContacts()
//        checkWritePermission()

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //deleteContact(context, pBooksList.get(viewHolder.adapterPosition).getId().toLong())
                    refresh()
                }
            })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        addButton = view.findViewById<View>(R.id.AddButton) as ImageButton
        val mSwipeRefreshLayout = view.findViewById<View>(R.id.swipe_layout) as SwipeRefreshLayout

        mSwipeRefreshLayout.setOnRefreshListener {
            Log.d("show", "refresh")
            showContacts()
            mSwipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    private fun registerUser(email: String, name: String, password: String) {
        compositeDisposable.add(iMyService!!.registerUser(email, name, password)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<String?> { response ->
                    Toast.makeText(activity, "" + response, Toast.LENGTH_SHORT).show()
                })
        )
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        var newName: String? = ""
//        var newNum: String? = ""
//
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                newName = data?.getStringExtra("newName")
//                newNum = data?.getStringExtra("newNum")
////                iMyService!!.addContact(newName, newNum, uid)
//            }
//        }
//    }


    fun deleteContact(context: Context, contactId: Long) {
        context.contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI,
            ContactsContract.RawContacts.CONTACT_ID + "=" + contactId, null)
    }

    private fun refresh() {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.detach(this).attach(this).commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO : implement when item.itemId is "Contact" or "Gallery" or "etc"...

        return super.onOptionsItemSelected(item)
    }

    /***********************************************************/
    /************** legacy codes -> used in week 1 *************/
    /***********************************************************/

    fun showContacts() {
        Log.d("show", "sC call")
        // FIXME: getContacts() -> Data from server
        pBooksList = getContacts()

        recyclerView.apply {
            adapter = ContactViewAdapter(context, pBooksList)
            //layoutManager = LinearLayoutManager(context)
        }
    }

    fun getContacts(): MutableList<PhoneBook> {
        // 데이터베이스 혹은 content resolver 를 통해 가져온 데이터를 적재할 저장소를 먼저 정의
        var data: MutableList<PhoneBook> = ArrayList()

        val API_URL: String = "http://192.249.19.242:6180/"

        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()

        var iMyService: IMyService = Retrofit.Builder().baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build().create<IMyService>(IMyService::class.java)

            var success : Boolean = true
            var req: Call<List<PhoneBook>> = iMyService.getAllContacts(app.prefs.id.toString())
//        Log.d("findUserbyFB", user_id)
            req.enqueue(object: Callback<List<PhoneBook>> {
                override fun onResponse(call: Call<List<PhoneBook>>, response: Response<List<PhoneBook>>){
                    //Log.d("userinfo", response.body().  )
                    //Log.d("userinfo", response.body()!![1].id)
                    data = response.body() as MutableList<PhoneBook>
                    Log.d("getAllContacts", response.body().toString())
//                    for (i in 1 until response.body()!!.size) {
////                        var tmpcontact = PhoneBook(
////                            response.body()!![i].id,
////                            response.body()!![i].name,
////                            response.body()!![i].number
////                        )
//                        var tmpcontact = PhoneBook()
//                        tmpcontact.id = response.body()!![i].id
//                        tmpcontact.name = response.body()!![i].name
//                        tmpcontact.number = response.body()!![i].number
//                        //Log.d("for loop", response.body()!![i].name)
//                        //Log.d("for loop", tmpcontact.name)
//                        data.add(tmpcontact)
//                    }
//                    data = response.body()
                }

                override fun onFailure(call: Call<List<PhoneBook>>, t: Throwable) {
//                Log.d("userinfo", "fuck")
                }
            })

        // 이름순으로 정렬한다
//        datas.sortWith(kotlin.Comparator { data1, data2 ->
//            data1.name!!.compareTo(data2.name!!)})
        //Log.d("contact", data.toString())
        return data
    }

    fun writeContact(displayName: String, number: String) {
        Log.i("contact", "writeContact")
        val contentProviderOperations = ArrayList<ContentProviderOperation>()
        //insert raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(
                    ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )
        //insert contact display name using Data.CONTENT_URI
        contentProviderOperations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName).build()
        )
        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number).withValue(
                    ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()
        )
        try {
            requireContext()
                .getContentResolver()
                .applyBatch(ContactsContract.AUTHORITY, contentProviderOperations)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        }
    }

    private fun checkLocationPermission() : Boolean {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                1//MY_PERMISSIONS_REQUEST_PERMISSION
            )
            return false
        }
        return true
    }

    private fun checkWritePermission() : Boolean {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_CONTACTS),
                2//MY_PERMISSIONS_REQUEST_PERMISSION
            )
            return false
        }
        return true
    }

    /***********************************************************/
    /************ use REQUEST_CODE?  is it necessary? **********/
    /***********************************************************/

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            1 -> {
//                // If request is cancelled, the result arrays are empty.
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    pBooksList = getContacts()
//                    recyclerView.apply {
//                        adapter = ContactViewAdapter(context, pBooksList)
//                        Log.d(ContentValues.TAG, "permission granted!")
//                    }
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Log.d(ContentValues.TAG, "permission not granted")
//                }
//                return
//            }
//            2 -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    //val newFrag = ContactAdd(this)
//                    //newFrag.setTargetFragment(this, DIALOG_REQUEST_CODE)
//                    //newFrag.show(fragmentManager!!.beginTransaction(), "dialog")
//                    Log.d(ContentValues.TAG, "permission granted!")
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Log.d(ContentValues.TAG, "permission not granted")
//                }
//                return
//            }
//            // Add other 'when' lines to check for other
//            // permissions this app might request.
//            else -> {
//                // Ignore all other requests.
//                Log.d(ContentValues.TAG, "some other request")
//            }
//        }
//    }

//    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
//        TODO("Not yet implemented")
//    }
//
//    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onLoaderReset(loader: Loader<Cursor>) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        TODO("Not yet implemented")
//    }

}