package com.example.madcamp_week2.ui.main.chatting

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.bumptech.glide.Glide
import com.example.madcamp_week2.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView


class ChatActivity : AppCompatActivity() {
    class MessageViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        var messageTextView: TextView
        var messageImageView: ImageView
        var messengerTextView: TextView
        var messengerImageView: CircleImageView

        init {
            messageTextView =
                itemView.findViewById<View>(R.id.messageTextView) as TextView
            messageImageView =
                itemView.findViewById<View>(R.id.messageImageView) as ImageView
            messengerTextView =
                itemView.findViewById<View>(R.id.messengerTextView) as TextView
            messengerImageView =
                itemView.findViewById<View>(R.id.messengerImageView) as CircleImageView
        }
    }

    private var mUsername: String? = null
    private var mPhotoUrl: String? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mSignInClient: GoogleSignInClient? = null
    private var mSendButton: Button? = null
    private var mMessageRecyclerView: RecyclerView? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var mProgressBar: ProgressBar? = null
    private var mMessageEditText: EditText? = null
    private var mAddMessageImageView: ImageView? = null

    // Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chatting)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        // Set default username is anonymous.
        mUsername = ANONYMOUS
//        val gso =
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//        mSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth!!.currentUser
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            Log.d("token", "firebaseuser null")
            startActivity(Intent(this, getTokenCheck::class.java))
            finish()
            return
        } else {
            Log.d("token", "user not null")
            mUsername = mFirebaseUser!!.displayName
            if (mFirebaseUser!!.photoUrl != null) {
                mPhotoUrl = mFirebaseUser!!.photoUrl.toString()
            }
        }

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        mMessageRecyclerView =
            findViewById<View>(R.id.messageRecyclerView) as RecyclerView
        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager!!.stackFromEnd = true
        mMessageRecyclerView!!.layoutManager = mLinearLayoutManager

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        val parser: SnapshotParser<FriendlyMessage> =
            SnapshotParser { dataSnapshot ->
                val friendlyMessage = dataSnapshot.getValue(
                    FriendlyMessage::class.java
                )
                friendlyMessage?.id = dataSnapshot.key
                friendlyMessage!!
            }
        val messagesRef =
            mFirebaseDatabaseReference!!.child(MESSAGES_CHILD)
        val options =
            FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                .setQuery(messagesRef, parser)
                .build()
        mFirebaseAdapter = object :
            FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            override fun onCreateViewHolder(
                viewGroup: ViewGroup,
                i: Int
            ): MessageViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                return MessageViewHolder(
                    inflater.inflate(
                        R.layout.item_message,
                        viewGroup,
                        false
                    )
                )
            }

            override fun onBindViewHolder(
                viewHolder: MessageViewHolder,
                position: Int,
                friendlyMessage: FriendlyMessage
            ) {
                mProgressBar!!.visibility = ProgressBar.INVISIBLE
                if (friendlyMessage.text != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.text)
                    viewHolder.messageTextView.visibility = TextView.VISIBLE
                    viewHolder.messageImageView.visibility = ImageView.GONE
                } else if (friendlyMessage.imageUrl != null) {
                    val imageUrl: String = friendlyMessage.imageUrl!!
                    if (imageUrl.startsWith("gs://")) {
                        val storageReference = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(imageUrl)
                        storageReference.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUrl = task.result.toString()
                                Glide.with(viewHolder.messageImageView.context)
                                    .load(downloadUrl)
                                    .into(viewHolder.messageImageView)
                            } else {
                                Log.w(
                                    TAG,
                                    "Getting download url was not successful.",
                                    task.exception
                                )
                            }
                        }
                    } else {
                        Glide.with(viewHolder.messageImageView.context)
                            .load(friendlyMessage.imageUrl)
                            .into(viewHolder.messageImageView)
                    }
                    viewHolder.messageImageView.visibility = ImageView.VISIBLE
                    viewHolder.messageTextView.visibility = TextView.GONE
                }
                viewHolder.messengerTextView.setText(friendlyMessage.name)
                if (friendlyMessage.photoUrl == null) {
                    viewHolder.messengerImageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ChatActivity,
                            R.drawable.ic_account_circle_black_36dp
                        )
                    )
                } else {
                    Glide.with(this@ChatActivity)
                        .load(friendlyMessage.photoUrl)
                        .into(viewHolder.messengerImageView)
                }
            }
        }
        mFirebaseAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(
                positionStart: Int,
                itemCount: Int
            ) {
                super.onItemRangeInserted(positionStart, itemCount)
                val friendlyMessageCount = mFirebaseAdapter.getItemCount()
                val lastVisiblePosition =
                    mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                    positionStart >= friendlyMessageCount - 1 &&
                    lastVisiblePosition == positionStart - 1
                ) {
                    mMessageRecyclerView!!.scrollToPosition(positionStart)
                }
            }
        })
        mMessageRecyclerView!!.adapter = mFirebaseAdapter
        mMessageEditText = findViewById<View>(R.id.messageEditText) as EditText
        mMessageEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (charSequence.toString().trim { it <= ' ' }.length > 0) {
                    mSendButton!!.isEnabled = true
                } else {
                    mSendButton!!.isEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        mSendButton = findViewById<View>(R.id.sendButton) as Button
        mSendButton!!.setOnClickListener { // Send messages on click.
            val friendlyMessage = FriendlyMessage(
                mMessageEditText!!.text.toString(),
                mUsername,
                mPhotoUrl,
                null /* no image */
            )
            mFirebaseDatabaseReference!!.child(MESSAGES_CHILD)
                .push().setValue(friendlyMessage)
            mMessageEditText!!.setText("")
        }
        mAddMessageImageView =
            findViewById<View>(R.id.addMessageImageView) as ImageView
        mAddMessageImageView!!.setOnClickListener { // Select image for image message on click.
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    public override fun onPause() {
        mFirebaseAdapter!!.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        mFirebaseAdapter!!.startListening()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.sign_out_menu -> {
//                mFirebaseAuth!!.signOut()
//                mSignInClient!!.signOut()
//                mUsername = ANONYMOUS
//                startActivity(Intent(this, SignInActivity::class.java))
//                finish()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
        super.onOptionsItemSelected(item)
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val uri = data.data
                    Log.d(TAG, "Uri: " + uri.toString())
                    val tempMessage = FriendlyMessage(
                        null, mUsername, mPhotoUrl,
                        LOADING_IMAGE_URL
                    )
                    mFirebaseDatabaseReference!!.child(MESSAGES_CHILD).push()
                        .setValue(
                            tempMessage
                        ) { databaseError, databaseReference ->
                            if (databaseError == null) {
                                val key = databaseReference.key
                                val storageReference =
                                    FirebaseStorage.getInstance()
                                        .getReference(mFirebaseUser!!.uid)
                                        .child(key!!)
                                        .child(uri!!.lastPathSegment!!)
                                putImageInStorage(storageReference, uri, key)
                            } else {
                                Log.w(
                                    TAG,
                                    "Unable to write message to database.",
                                    databaseError.toException()
                                )
                            }
                        }
                }
            }
        }
    }

    private fun putImageInStorage(
        storageReference: StorageReference,
        uri: Uri?,
        key: String?
    ) {
        storageReference.putFile(uri!!).addOnCompleteListener(
            this@ChatActivity
        ) { task ->
            if (task.isSuccessful) {
                task.result!!.metadata!!.reference!!.downloadUrl
                    .addOnCompleteListener(
                        this@ChatActivity
                    ) { task ->
                        if (task.isSuccessful) {
                            val friendlyMessage = FriendlyMessage(
                                null, mUsername, mPhotoUrl,
                                task.result.toString()
                            )
                            mFirebaseDatabaseReference!!.child(MESSAGES_CHILD)
                                .child(key!!)
                                .setValue(friendlyMessage)
                        }
                    }
            } else {
                Log.w(
                    TAG, "Image upload task was not successful.",
                    task.exception
                )
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        const val MESSAGES_CHILD = "messages"
        private const val REQUEST_INVITE = 1
        private const val REQUEST_IMAGE = 2
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
        const val DEFAULT_MSG_LENGTH_LIMIT = 10
        const val ANONYMOUS = "anonymous"
        private const val MESSAGE_SENT_EVENT = "message_sent"
        private const val MESSAGE_URL = "http://friendlychat.firebase.google.com/message/"
    }
}