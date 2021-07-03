package com.aarti.mbassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cardview_my_contact.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            intent.putExtra("fragment_id", "1")
            startActivity(intent)
        }

        cardview_favourite.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            intent.putExtra("fragment_id", "2")
            startActivity(intent)
        }

        cardview_deleted.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            intent.putExtra("fragment_id", "3")
            startActivity(intent)
        }
    }
}