package com.example.madcamp_week2.ui.main.contact

import android.Manifest
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Intent
import android.content.OperationApplicationException
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week2.R
import java.util.*

class ContactFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var pBooksList: List<PhoneBook>
    val DIALOG_REQUEST_CODE: Int = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return inflater.inflate(R.layout.fragment_contact, container, false)
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        recyclerView = view.findViewById(R.id.phoneList)
        recyclerView.apply {
            adapter = ContactViewAdapter(context, mutableListOf())
            layoutManager = LinearLayoutManager(context)
        }

        //checkLocationPermission()
        //if(checkLocationPermission())
            //showContacts()

        val addButton = view.findViewById<View>(R.id.AddButton) as ImageButton

        addButton.setOnClickListener {
            val intent = Intent(activity, ContactFAB::class.java)
            startActivityForResult(intent, 1)
        }

        return view
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        var newName: String? = ""
//        var newNum: String? = ""
//
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                newName = data.getStringExtra("newName")
//                newNum = data.getStringExtra("newNum")
//                if (checkWritePermission())
//                    writeContact(newName!!, newNum!!)
//            }
//        }
//    }

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
        // FIXME: getContacts() -> Data from server
        pBooksList = getContacts()
        recyclerView.apply {
            adapter = ContactViewAdapter(context, pBooksList)
            //Log.d(TAG, "permission granted!")
        }
    }

    fun getContacts(): List<PhoneBook> {
        // 데이터베이스 혹은 content resolver 를 통해 가져온 데이터를 적재할 저장소를 먼저 정의
        val datas: MutableList<PhoneBook> = ArrayList()

        // 1. Resolver 가져오기(데이터베이스 열어주기)
        // 전화번호부에 이미 만들어져 있는 ContentProvider 를 통해 데이터를 가져올 수 있음
        // 다른 앱에 데이터를 제공할 수 있도록 하고 싶으면 ContentProvider 를 설정
        // 핸드폰 기본 앱 들 중 데이터가 존재하는 앱들은 Content Provider 를 갖는다
        // ContentResolver 는 ContentProvider 를 가져오는 통신 수단
        val resolver = requireContext().contentResolver

        // 2. 전화번호가 저장되어 있는 테이블 주소값(Uri)을 가져오기
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        // 3. 테이블에 정의된 칼럼 가져오기
        // ContactsContract.CommonDataKinds.Phone 이 경로에 상수로 칼럼이 정의
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID // 인덱스 값, 중복될 수 있음 -- 한 사람 번호가 여러개인 경우
            , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            , ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // 4. ContentResolver로 쿼리를 날림 -> resolver 가 provider 에게 쿼리하겠다고 요청
        val cursor =
            resolver.query(phoneUri, projection, null, null, null)

        // 4. 커서로 리턴된다. 반복문을 돌면서 cursor 에 담긴 데이터를 하나씩 추출
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 4.1 이름으로 인덱스를 찾아준다
                val idIndex = cursor.getColumnIndex(projection[0]) // 이름을 넣어주면 그 칼럼을 가져와준다.
                val nameIndex = cursor.getColumnIndex(projection[1])
                val numberIndex = cursor.getColumnIndex(projection[2])
                // 4.2 해당 index 를 사용해서 실제 값을 가져온다.
                val id = cursor.getString(idIndex)
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)
                val phoneBook = PhoneBook()
                phoneBook.id = id
                phoneBook.name = name
                phoneBook.number = number
                Log.d("TAG", "$id, $name, $number")
                datas.add(phoneBook)
            }
        }
        // 데이터 계열은 반드시 닫아줘야 한다.
        cursor!!.close()

        // 이름순으로 정렬한다
        datas.sortWith(kotlin.Comparator { data1, data2 ->
            data1.name!!.compareTo(data2.name!!)})

        return datas
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    pBooksList = getContacts()
                    recyclerView.apply {
                        adapter = ContactViewAdapter(context, pBooksList)
                        Log.d(ContentValues.TAG, "permission granted!")
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(ContentValues.TAG, "permission not granted")
                }
                return
            }
            2 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val newFrag = ContactAdd(this)
                    newFrag.setTargetFragment(this, DIALOG_REQUEST_CODE)
                    newFrag.show(fragmentManager!!.beginTransaction(), "dialog")
                    Log.d(ContentValues.TAG, "permission granted!")
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(ContentValues.TAG, "permission not granted")
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
                Log.d(ContentValues.TAG, "some other request")
            }
        }
    }

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