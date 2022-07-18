package com.edu.sdproject2

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s): Bishnu Bhusal
 * Description: App 02 - MainActivity (controller) class
 */

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var dbHelper: DBHelper


    // TODO #1: create the ItemHolder inner class
    // a holder object saves the references to view components of a recycler view item
    private inner class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.txtId)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val txtCreatedDate: TextView = view.findViewById(R.id.txtCreationDate)
        val txtUpdatedDate: TextView = view.findViewById(R.id.txtUpdateDate)
        val imgView: ImageView = view.findViewById(R.id.imgView)

    }

    // TODO #2: create the ItemAdapter inner class
    // an item adapter binds items from a list to holder objects in a recycler view
    private inner class ItemAdapter(var bucketlist: List<Item>, var onClickListener: View.OnClickListener, var onLongClickListener: View.OnLongClickListener): RecyclerView.Adapter<ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
            //view.setOnClickListener(onClickListener)
            //view.setOnLongClickListener(onLongClickListener)
            return ItemHolder(view)

        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item = bucketlist[position]
            holder.id.text = item.id.toString()
            holder.txtDescription.text = item.description
            holder.txtCreatedDate.text = DBHelper.USA_FORMAT.format(item.creationDate)
            holder.txtUpdatedDate.text = DBHelper.USA_FORMAT.format(item.updateDate)
            if (item.status == Item.SCHEDULED)
                holder.imgView.setImageResource(R.drawable.scheduled_item)
            else if (item.status == Item.ARCHIVED)
                holder.imgView.setImageResource(R.drawable.archived_item)
            else
                holder.imgView.setImageResource(R.drawable.completed_item)


            // sets the holder's listener
            holder.itemView.setOnClickListener(onClickListener)
            // set the holder's (long click) listener
            holder.itemView.setOnLongClickListener(onLongClickListener)
        }

        override fun getItemCount(): Int {
            return bucketlist.size
        }
    }

    // TODO #3: populate the recycler view
    // this function should query the database for all of the bucket list items; then use the list to update the recycler view's adapter
    // don't forget to call "sort()" on your list so the items are displayed in the correct order

    fun populateRecyclerView() {
        val db = dbHelper.readableDatabase
        val items = mutableListOf<Item>()
        val cursor = db.query(
            "bucketlist",
            arrayOf<String>("rowid, description, creation_date, update_date, status"),
            null,
            null,
            null,
            null,
            null
        )
        with (cursor) {
            while (moveToNext()) {
                val id = getInt(0)
                val description = getString(1)
                // parse is used to convert string to int, format is used to convert to string
                val createDate = DBHelper.ISO_FORMAT.parse(getString(2))
                val updateDate = DBHelper.ISO_FORMAT.parse(getString(3))
                val status = getInt(4)
                val item = Item(id, description, createDate!!, updateDate!!, status )
                items.add(item)
                items.sort()

            }
        }
        recyclerView.adapter = ItemAdapter(items, this,this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO #4: create and populate the recycler view
        dbHelper = DBHelper(this)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        populateRecyclerView()


        // TODO #5: initialize the floating action button
        val fabCreate: FloatingActionButton = findViewById(R.id.fabCreate)
        fabCreate.setOnClickListener {
            // calls CreateUpdateActivity for create
            val intent = Intent(this, CreateUpdateActivity::class.java)
            intent.putExtra("op", CreateUpdateActivity.CREATE_OP)
            startActivity(intent)
        }

    }
    // This method is called when CreateUpdateActivity finishes
    override fun onResume() {
        super.onResume()
        populateRecyclerView()
    }


    // TODO #6: call CreateUpdateActivity for update
    // don't forget to pass the item's id to the CreateUpdateActivity via the intent
    override fun onClick(view: View?) {
        if (view != null) {
            val id = view.findViewById<TextView>(R.id.txtId).text
            val intent = Intent(this, CreateUpdateActivity::class.java)
            intent.putExtra("op", CreateUpdateActivity.UPDATE_OP)
            intent.putExtra("id",id)
            startActivity(intent)

        }
    }

    // TODO #7: delete the long tapped item after a yes/no confirmation dialog
    override fun onLongClick(view: View?): Boolean {

        class MyDialogInterfaceListener(val id: Int): DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface?, which: Int) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    try {
                        val db = dbHelper.writableDatabase
                        db.execSQL("""
                            DELETE FROM items
                            WHERE rowid = "{id}"
                        """)
                        populateRecyclerView()

                    } catch (ex: Exception) {


                    }
                }
            }
        }

        if (view != null) {
            val name = view.findViewById<TextView>(R.id.txtId).text.toString()

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Are you sure you want to delete item named ${name}?")
            alertDialogBuilder.setPositiveButton("Yes", MyDialogInterfaceListener(name.toInt()))
            alertDialogBuilder.setNegativeButton("No", MyDialogInterfaceListener(name.toInt()))
            alertDialogBuilder.show()
            return true
        }
        return false
    }
}