package com.aarti.mbassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aarti.mbassignment.fragment.DeletedFragment
import com.aarti.mbassignment.fragment.FavouriteFragment
import com.aarti.mbassignment.fragment.MyContactFragment

class FragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val intent = intent
        var fragmentId = intent.getStringExtra("fragment_id")

        if (fragmentId.equals("1")) {
            val fragment: MyContactFragment = MyContactFragment.newInstance()
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, "my_contact")
                    .commit()
            }
        }

        if (fragmentId.equals("2")) {
            val fragment: FavouriteFragment = FavouriteFragment.newInstance()
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, "favourite")
                    .commit()
            }
        }

        if (fragmentId.equals("3")) {
            val fragment: DeletedFragment = DeletedFragment.newInstance()
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, "deleted")
                    .commit()
            }
        }
    }
}