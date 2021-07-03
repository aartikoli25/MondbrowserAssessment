package com.aarti.mbassignment.adapter

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aarti.mbassignment.R
import com.aarti.mbassignment.model.ContactData
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import java.io.IOException
import java.io.InputStream

class DeletedListAdapter(
    var context: Context,
    var listdata: List<ContactData>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<DeletedListAdapter.DeletedListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedListHolder {
        var inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.deleted_item, parent, false)
        var holder =
            DeletedListHolder(
                view
            )
        return holder
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: DeletedListHolder, position: Int) {

        holder.text_user_name.text = listdata.get(position).name
        holder.text_user_mobile_no.text = listdata.get(position).mobile

        val firstLetter: String =
            listdata.get(position).name.substring(0, 1).toUpperCase()
        val drawable: TextDrawable = TextDrawable.builder().buildRound(firstLetter, -0xc04018)

        var photo = retrieveContactPhoto(context, listdata.get(position).mobile)

        if (photo != null) {
            Glide.with(context)
                .load(photo)
                .centerCrop()
                .placeholder(R.drawable.default_user)
                .into(holder.image_view_user)
        } else {
            holder.image_view_user.setImageDrawable(drawable)
        }

        holder.bindView(listdata[position], listener)
    }

    class DeletedListHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image_view_user = view.findViewById<ImageView>(R.id.image_view_user)
        var text_user_name = view.findViewById<TextView>(R.id.text_user_name)
        var text_user_mobile_no = view.findViewById<TextView>(R.id.text_user_mobile_no)
        var imageview_delete = view.findViewById<ImageView>(R.id.imageview_delete)

        fun bindView(contactData: ContactData, listener: OnItemClickListener) {
            imageview_delete.setOnClickListener {
                listener.onItemClick(
                    contactData,
                    adapterPosition, imageview_delete
                )
            }
        }
    }

    fun retrieveContactPhoto(
        context: Context,
        number: String?
    ): Bitmap? {
        val contentResolver = context.contentResolver
        var contactId: String? = null
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val projection = arrayOf(
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup._ID
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }
        var photo: Bitmap? = null
        try {
            if (contactId != null) {
                val inputStream: InputStream? =
                    ContactsContract.Contacts.openContactPhotoInputStream(
                        context.contentResolver,
                        ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI,
                            contactId.toLong()
                        )
                    )
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream)
                }
                if (inputStream != null) {
                    inputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return photo
    }

    interface OnItemClickListener {
        fun onItemClick(contactData: ContactData, pos: Int, imageview_delete: ImageView)
    }
}