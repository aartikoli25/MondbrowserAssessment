package com.aarti.mbassignment.fragment

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aarti.mbassignment.R
import com.aarti.mbassignment.adapter.ContactListAdapter
import com.aarti.mbassignment.utils.Constants
import com.aarti.mbassignment.database.DatabaseHandler
import com.aarti.mbassignment.utils.SharePreferenceUtility
import com.aarti.mbassignment.model.ContactData
import com.github.florent37.runtimepermission.kotlin.askPermission
import kotlinx.android.synthetic.main.alert_dialog_view.view.*
import kotlinx.android.synthetic.main.recyclerview_item.*

class MyContactFragment : Fragment() {

    var recyclerview: RecyclerView? = null
    var datalist: ArrayList<ContactData> = arrayListOf()
    var availableContactList: ArrayList<ContactData> = arrayListOf()

    var userId: Int? = 0
    var isDataSave: Boolean = false

    companion object {
        fun newInstance() =
            MyContactFragment()
    }

    private var listener = object : ContactListAdapter.OnItemClickListener {
        override fun onItemClick(
            contactData: ContactData, pos: Int, delete: ImageView
        ) {
            alertDialogBox(
                context,
                "Do you really want to delete this contact?",
                contactData.id.toString(),
                contactData.name,
                contactData.mobile,
                contactData.isFavourite,
                contactData.isDeleted
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.recyclerview_item, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerview = view!!.findViewById(R.id.recyclerview)

        isDataSave = SharePreferenceUtility.getPreferences(
            context!!,
            Constants.isDataSave,
            SharePreferenceUtility.PREFTYPE_BOOLEAN
        ) as Boolean

        requestPermission()
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                permissionsCheck()
            }, 110)

        } else {
            if (!isDataSave) {
                getAllContacts()
            } else {
                Toast.makeText(context, "Fetching From Database..", Toast.LENGTH_LONG).show()
                viewRecord()
            }
        }
    }

    private fun permissionsCheck() {
        askPermission(
            Manifest.permission.READ_CONTACTS
        ) {
            if (!isDataSave) {
                getAllContacts()
            } else {
                viewRecord()
                Toast.makeText(context, "Fetching From Database..", Toast.LENGTH_LONG).show()
            }

        }.onDeclined { e ->
            if (e.hasDenied()) {
                e.denied.forEach {
                }
                AlertDialog.Builder(context!!)
                    .setCancelable(false)
                    .setMessage("Please accept the permissions")
                    .setPositiveButton("Ok") { dialog, which ->
                        e.askAgain();
                    }
                    .show();
            }

            if (e.hasForeverDenied()) {
                e.foreverDenied.forEach {

                }
                AlertDialog.Builder(context!!)
                    .setCancelable(false)
                    .setMessage("Please enable permissions from settings.")
                    .setPositiveButton("Go to settings") { dialog, which ->
                        dialog.dismiss()
                        e.goToSettings()
                    } //ask again
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun getAllContacts() {

        Toast.makeText(context, "Loading..", Toast.LENGTH_LONG).show()

        val cr: ContentResolver = activity!!.getContentResolver()
        val cur: Cursor? = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if ((if (cur != null) cur.getCount() else 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name: String = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        val phoneNo: String = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        userId = userId!!.toInt() + 1
                        datalist.add(ContactData(userId!!.toInt() + 1, name, phoneNo, "0", "0"))
                        saveRecord(userId!!, name, phoneNo)
                    }
                    pCur.close()
                }
            }
        }
        if (cur != null) {
            cur.close()
        }

        if (datalist.isNullOrEmpty()) {
            tv_empty_data.visibility = View.VISIBLE
            recyclerview!!.visibility = View.GONE
        } else {
            tv_empty_data.visibility = View.GONE

            var adapter = ContactListAdapter(context!!, datalist, listener)
            recyclerview!!.adapter = adapter
            var linearLayoutManager = LinearLayoutManager(context)
            recyclerview!!.layoutManager = linearLayoutManager
        }

        Toast.makeText(context, "Saved to Database!!", Toast.LENGTH_LONG).show()
        SharePreferenceUtility.saveBooleanPreferences(
            context,
            Constants.isDataSave, true
        )
    }

    fun saveRecord(id: Int, name: String, mobile: String) {
        val databaseHandler: DatabaseHandler = DatabaseHandler(context!!)
        databaseHandler.addContacts(ContactData(id, name, mobile, "0", "0"))

    }

    fun viewRecord() {
        userId = 0
        datalist = arrayListOf()
        availableContactList = arrayListOf()

        val databaseHandler: DatabaseHandler = DatabaseHandler(context!!)

        val contactData: List<ContactData> = databaseHandler.viewContacts()
        val contactArrayId = Array<String>(contactData.size) { "0" }
        val contactArrayName = Array<String>(contactData.size) { "null" }
        val contactArrayMobile = Array<String>(contactData.size) { "null" }
        val contactArrayIsFavourite = Array<String>(contactData.size) { "null" }
        val contactArrayIsDeleted = Array<String>(contactData.size) { "null" }
        var index = 0
        for (c in contactData) {
            contactArrayId[index] = c.id.toString()
            contactArrayName[index] = c.name
            contactArrayMobile[index] = c.mobile
            contactArrayIsFavourite[index] = c.isFavourite
            contactArrayIsDeleted[index] = c.isDeleted

            datalist.add(
                ContactData(
                    contactArrayId[index].toInt(),
                    contactArrayName[index],
                    contactArrayMobile[index],
                    contactArrayIsFavourite[index],
                    contactArrayIsDeleted[index]
                )
            )
            index++
        }

        for (i in 0 until datalist.size) {
            if (datalist.get(i).isDeleted.equals("0")) {
                availableContactList.add(
                    ContactData(
                        datalist.get(i).id,
                        datalist.get(i).name,
                        datalist.get(i).mobile,
                        datalist.get(i).isFavourite,
                        datalist.get(i).isDeleted
                    )
                )
            }
        }

        if (availableContactList.isNullOrEmpty()) {
            tv_empty_data.visibility = View.VISIBLE
            recyclerview!!.visibility = View.GONE
        } else {
            tv_empty_data.visibility = View.GONE

            var adapter = ContactListAdapter(context!!, availableContactList, listener)
            recyclerview!!.adapter = adapter
            var linearLayoutManager = LinearLayoutManager(context)
            recyclerview!!.layoutManager = linearLayoutManager
        }

    }

    fun alertDialogBox(
        context: Context?,
        msg: String,
        id: String,
        name: String,
        mobile: String,
        isFavourite: String,
        isDeleted: String
    ) {
        var mDialogView =
            LayoutInflater.from(context).inflate(R.layout.alert_dialog_view, null)

        val mBuilder = android.app.AlertDialog.Builder(context)
            .setView(mDialogView)
        var mAlertDialog = mBuilder.show()
        mDialogView.alertTitle.text = msg
        mBuilder.setCancelable(true)
        mAlertDialog.setCanceledOnTouchOutside(false)
        mDialogView.btn_yes.setOnClickListener(View.OnClickListener {
            updateRecord(id, name, mobile, isFavourite, "1")
            mAlertDialog.dismiss()
        })

        mDialogView.btn_no.setOnClickListener(View.OnClickListener {
            mAlertDialog.dismiss()
        })
    }

    fun updateRecord(
        id: String,
        name: String,
        mobile: String,
        isFavourite: String,
        isDeleted: String
    ) {
        val databaseHandler: DatabaseHandler = DatabaseHandler(context!!)
        val status = databaseHandler.updateContact(
            ContactData(
                id.toInt(),
                name,
                mobile,
                isFavourite,
                isDeleted
            )
        )
        if (status > -1) {
            Toast.makeText(context, "Contact Updated!!", Toast.LENGTH_LONG).show()
            viewRecord()
        }
    }

}