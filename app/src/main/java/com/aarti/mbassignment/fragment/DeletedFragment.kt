package com.aarti.mbassignment.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aarti.mbassignment.R
import com.aarti.mbassignment.adapter.DeletedListAdapter
import com.aarti.mbassignment.database.DatabaseHandler
import com.aarti.mbassignment.model.ContactData
import kotlinx.android.synthetic.main.alert_dialog_view.view.*
import kotlinx.android.synthetic.main.recyclerview_item.*

class DeletedFragment : Fragment() {

    var recyclerview: RecyclerView? = null
    var datalist: ArrayList<ContactData> = arrayListOf()
    var availableContactList: ArrayList<ContactData> = arrayListOf()

    companion object {
        fun newInstance() =
            DeletedFragment()
    }

    private var listener = object : DeletedListAdapter.OnItemClickListener {
        override fun onItemClick(
            contactData: ContactData, pos: Int, delete: ImageView
        ) {
            if (contactData.isDeleted.equals("1")) {
                alertDialogBox(
                    context,
                    "Do you really want to restore this contact?",
                    contactData.id.toString(),
                    contactData.name,
                    contactData.mobile,
                    contactData.isFavourite,
                    "0"
                )
            }
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
        viewRecord()
    }

    fun viewRecord() {
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
            if (datalist.get(i).isDeleted.equals("1")) {
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
            var adapter = DeletedListAdapter(context!!, availableContactList, listener)
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
            updateRecord(id, name, mobile, isFavourite, isDeleted)
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