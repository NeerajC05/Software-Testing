package com.pdiot.harty.profile

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.R

/* This Kotlin class allows a user to view their historic data. */
class HistoricDataActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView : RecyclerView
    private lateinit var noDataText : TextView
    private lateinit var noDataReminder : TextView
    private lateinit var dataArrayList : ArrayList<HistoricData>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_historic_data)

        setUpNavigation()

        recyclerView = findViewById(R.id.dataList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(SimpleItemDecoration(applicationContext))

        dataArrayList = arrayListOf<HistoricData>()
        getData()
    }

    //Main process for retrieving the data from the database.
    private fun getData() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            database = FirebaseDatabase.getInstance().getReference(currentUser.uid)
        }

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    noDataText = findViewById(R.id.none)
                    noDataReminder = findViewById(R.id.none1)
                    noDataText.visibility = View.GONE
                    noDataReminder.visibility = View.GONE
                    for (dataSnapshot in snapshot.children) {
                        val data = dataSnapshot.getValue(HistoricData::class.java)
                        dataArrayList.add(data!!)
                    }
                    recyclerView.adapter = RecycleViewAdapter(dataArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //Sets the required navigation for the page
    private fun setUpNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.backNavigationView)

        bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.back -> {
                    finish()
                }
            }
            true
        }
    }

    class SimpleItemDecoration(context: Context, space: Int = 10) : RecyclerView.ItemDecoration() {

        private val spaceInDp = dpToPx(context, space)

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            outRect.left = spaceInDp
            outRect.right = spaceInDp
            outRect.bottom = spaceInDp
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = spaceInDp
            }
        }

        fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }
}