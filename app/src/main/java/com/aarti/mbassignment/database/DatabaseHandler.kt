package com.aarti.mbassignment.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.aarti.mbassignment.model.ContactData

class DatabaseHandler(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ContactDatabase"
        private val TABLE_CONTACTS = "ContactTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_MOBILE = "mobile"
        private val KEY_IS_FAVOURITE = "is_favourite"
        private val KEY_IS_DELETED = "is_deleted"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_MOBILE + " TEXT," + KEY_IS_FAVOURITE + " TEXT," + KEY_IS_DELETED + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }

    fun addContacts(contactData: ContactData): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, contactData.id)
        contentValues.put(KEY_NAME, contactData.name)
        contentValues.put(KEY_MOBILE, contactData.mobile)
        contentValues.put(KEY_IS_FAVOURITE, contactData.isFavourite)
        contentValues.put(KEY_IS_DELETED, contactData.isDeleted)

        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }

    fun viewContacts(): List<ContactData> {
        val contactlist: ArrayList<ContactData> = ArrayList<ContactData>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Int
        var userName: String
        var userMobile: String
        var isFavourite: String
        var isDeleted: String
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getInt(cursor.getColumnIndex("id"))
                userName = cursor.getString(cursor.getColumnIndex("name"))
                userMobile = cursor.getString(cursor.getColumnIndex("mobile"))
                isFavourite = cursor.getString(cursor.getColumnIndex("is_favourite"))
                isDeleted = cursor.getString(cursor.getColumnIndex("is_deleted"))
                val contact = ContactData(
                    id = userId,
                    name = userName,
                    mobile = userMobile,
                    isFavourite = isFavourite,
                    isDeleted = isDeleted
                )
                contactlist.add(contact)
            } while (cursor.moveToNext())
        }
        return contactlist
    }

    fun updateContact(contactData: ContactData): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, contactData.id)
        contentValues.put(KEY_NAME, contactData.name)
        contentValues.put(KEY_MOBILE, contactData.mobile)
        contentValues.put(KEY_IS_FAVOURITE, contactData.isFavourite)
        contentValues.put(KEY_IS_DELETED, contactData.isDeleted)

        val success = db.update(TABLE_CONTACTS, contentValues, "id=" + contactData.id, null)
        db.close()

        return success
    }
}