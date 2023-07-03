package com.example.madcamp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.databinding.ActivityMainBinding
import com.example.madcamp.databinding.FragmentOneBinding
import com.example.madcamp.databinding.ItemRecyclerview1Binding
import java.util.Random

class MyViewHolder1(val binding: ItemRecyclerview1Binding): RecyclerView.ViewHolder(binding.root)

class Person(val id:String, val name:String, val phone_number:String,val email:String) {
    var profile_num:Int = 0
    init {
        val random = Random()
        profile_num = random.nextInt(5)
    }
}

class MyAdapter1(val datas: MutableList<Person>,val fragmentBinding: FragmentOneBinding): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder1(ItemRecyclerview1Binding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    fun change_detail(position: Int) {
        when(datas[position].profile_num) {
            0 ->fragmentBinding.profileData.setBackgroundResource(R.drawable.round_button_1)
            1 ->fragmentBinding.profileData.setBackgroundResource(R.drawable.round_button_2)
            2 ->fragmentBinding.profileData.setBackgroundResource(R.drawable.round_button_3)
            3 ->fragmentBinding.profileData.setBackgroundResource(R.drawable.round_button_4)
            else ->fragmentBinding.profileData.setBackgroundResource(R.drawable.round_button_5)
        }
        fragmentBinding.position.text = datas[position].id
        fragmentBinding.profileData.text = datas[position].name.substring(0,1)
        fragmentBinding.name.text=datas[position].name
        if (datas[position].phone_number == "") {
            fragmentBinding.phone.visibility=View.GONE
        } else {
            fragmentBinding.phoneText.text=datas[position].phone_number
            fragmentBinding.phone.visibility=View.VISIBLE
        }
        if (datas[position].email == "") {
            fragmentBinding.email.visibility=View.GONE
        } else {
            fragmentBinding.emailText.text=datas[position].email
            fragmentBinding.email.visibility=View.VISIBLE
        }
        Log.d("chan","clickeditem: ${position}")
        fragmentBinding.detailInfo.visibility=View.VISIBLE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder1).binding
        binding.itemData.text = datas[position].name
        binding.profileData.text = datas[position].name.substring(0,1)

        val num = datas[position].profile_num
        when(num) {
            0 ->binding.profileData.setBackgroundResource(R.drawable.round_button_1)
            1 ->binding.profileData.setBackgroundResource(R.drawable.round_button_2)
            2 ->binding.profileData.setBackgroundResource(R.drawable.round_button_3)
            3 ->binding.profileData.setBackgroundResource(R.drawable.round_button_4)
            else ->binding.profileData.setBackgroundResource(R.drawable.round_button_5)
        }

        holder.binding.itemData.setOnClickListener {
            change_detail(position)
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class MyDecoration1(val context: Context): RecyclerView.ItemDecoration() {
    // 모든 항목이 출력된 후 호출
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        // 리사이클러 뷰의 크기 계산
        val width = parent.width
        val height = parent.height
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // 각 항목의 위치 획득
        val index = parent.getChildAdapterPosition(view) + 1

//        if(index % 3 == 0){
//            outRect.set(10, 10, 10, 60)
//        }else {
//            outRect.set(10, 10, 10, 0)
//        }
        outRect.set(10, 10, 10, 0)

        view.setBackgroundColor(Color.parseColor("#ffffff"))
        ViewCompat.setElevation(view, 5.0f)
    }
}

class OneFragment : Fragment() {
    val datas = mutableListOf<Person>()
    private var adapter: MyAdapter1? = null
    private lateinit var binding: FragmentOneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOneBinding.inflate(inflater, container, false)

        // Check if the app has permission to read contacts
        if (hasReadContactsPermission()) {
            // If the app has permission, fetch the contacts
            val contactsCursor = fetchContacts()
            if (contactsCursor != null) {
                // Process the contacts cursor and add the contacts to the datas list
                while (contactsCursor.moveToNext()) {
                    val contactId =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val name =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    val emailCursor = fetchEmail(contactId)
                    val email = if (emailCursor?.moveToNext() == true) {
                        emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    } else {
                        ""
                    }
                    emailCursor?.close()

                    val person = Person(contactId, name, phoneNumber, email)
                    datas.add(person)
                }
                contactsCursor.close()
            }
        } else {
            // If the app doesn't have permission, request it
            requestReadContactsPermission()
        }

        adapter = MyAdapter1(datas, binding)
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.addItemDecoration(MyDecoration1(activity as Context))

        binding.backButton.setOnClickListener {
            binding.detailInfo.visibility=View.GONE
        }

        binding.editButton.setOnClickListener {
            val position = id2position(binding.position.text.toString())
            val person = datas[position]
            val intent = Intent(Intent.ACTION_EDIT)
                .setDataAndType(
                    Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, person.id),
                    ContactsContract.Contacts.CONTENT_ITEM_TYPE
                )
            startActivity(intent)
        }

        binding.callButton.setOnClickListener {
            val position = id2position(binding.position.text.toString())
            val person = datas[position]
            val intent = Intent(Intent.ACTION_DIAL)
                .setData(Uri.parse("tel:${person.phone_number}"))
            startActivity(intent)
        }

        binding.msgButton.setOnClickListener {
            val position = id2position(binding.position.text.toString())
            val person = datas[position]
            val intent = Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("smsto:${person.phone_number}"))
            startActivity(intent)
        }

        binding.emailButton.setOnClickListener {
            val position = id2position(binding.position.text.toString())
            val person = datas[position]
            val intent = Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:${person.email}"))
            startActivity(intent)
        }

        binding.addContact.setOnClickListener {
            addContact()
        }
        return binding.root
    }

    private fun addContact() {
        val intent = Intent(Intent.ACTION_INSERT)
            .setType(ContactsContract.Contacts.CONTENT_TYPE)
            .putExtra(ContactsContract.Intents.Insert.NAME,"")
            .putExtra(ContactsContract.Intents.Insert.PHONE, "010")
            .putExtra(ContactsContract.Intents.Insert.EMAIL, "")
        startActivity(intent)
    }

    private fun refreshContacts() {
        datas.clear()

        // Fetch the contacts again
        val contactsCursor = fetchContacts()
        if (contactsCursor != null) {
            // Process the contacts cursor and add the contacts to the datas list
            while (contactsCursor.moveToNext()) {
                val contactId =
                    contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name =
                    contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber =
                    contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                val emailCursor = fetchEmail(contactId)
                val email = if (emailCursor?.moveToNext() == true) {
                    emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                } else {
                    ""
                }
                emailCursor?.close()

                val person = Person(contactId, name, phoneNumber, email)
                datas.add(person)
            }
            contactsCursor.close()
            Log.d("CHAN","refresh")

            adapter?.notifyDataSetChanged()
        } else {
            // Permission denied, show a message or handle the denial accordingly
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    fun change_detail(position: Int) {
        when(datas[position].profile_num) {
            0 ->binding.profileData.setBackgroundResource(R.drawable.round_button_1)
            1 ->binding.profileData.setBackgroundResource(R.drawable.round_button_2)
            2 ->binding.profileData.setBackgroundResource(R.drawable.round_button_3)
            3 ->binding.profileData.setBackgroundResource(R.drawable.round_button_4)
            else ->binding.profileData.setBackgroundResource(R.drawable.round_button_5)
        }
        binding.position.text = datas[position].id
        binding.profileData.text = datas[position].name.substring(0,1)
        binding.name.text=datas[position].name
        if (datas[position].phone_number == "") {
            binding.phone.visibility=View.GONE
        } else {
            binding.phoneText.text=datas[position].phone_number
            binding.phone.visibility=View.VISIBLE
        }
        if (datas[position].email == "") {
            binding.email.visibility=View.GONE
        } else {
            binding.emailText.text=datas[position].email
            binding.email.visibility=View.VISIBLE
        }
        Log.d("chan","clickeditem: ${position}")
        binding.detailInfo.visibility=View.VISIBLE
    }
    fun id2position(id: String): Int {
        var found = 0
        var count = 0
        for (person in datas) {
            if (person.id == id) {
                found=1
                break
            }
            count += 1
        }
        if (found == 1) {
            return count
        } else {
            return -1
        }
    }
    private fun editUpdate(id: String) {
        refreshContacts()
        var position = id2position(id)
        if (position > -1) {
            change_detail(position)
        } else {
            binding.detailInfo.visibility=View.GONE
        }
    }
    override fun onResume() {
        super.onResume()
        var num = datas.size
        editUpdate(binding.position.text.toString())
    }
    private fun hasReadContactsPermission(): Boolean {
        val permission = Manifest.permission.READ_CONTACTS
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadContactsPermission() {
        val permission = Manifest.permission.READ_CONTACTS
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)
    }

    private fun fetchContacts(): Cursor? {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS
        )

        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        val selection =
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} IS NOT NULL AND ${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} != ''"

        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        if (cursor?.columnCount ?: 0 <= 0) {
            // No columns found in the cursor
            cursor?.close()
            return null
        }

        return cursor
    }
    private fun fetchEmail(contactId: String): Cursor? {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS)
        val selection =
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ? AND ${ContactsContract.CommonDataKinds.Email.MIMETYPE} = ?"
        val selectionArgs = arrayOf(contactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

        return requireContext().contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
    }
    fun backButtonPressed(): Int {
        if (binding.detailInfo.visibility == View.VISIBLE) {
            Log.d("CHAN","visible")
            binding.detailInfo.visibility=View.GONE
            return 1
        }
        return 0
    }
}