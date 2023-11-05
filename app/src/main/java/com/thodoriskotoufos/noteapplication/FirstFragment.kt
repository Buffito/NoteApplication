package com.thodoriskotoufos.noteapplication

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class FirstFragment : Fragment(), RecyclerViewAdapter.ClickListener {

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private val noteList: ArrayList<Note> = ArrayList()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("notes")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        getNotesFromFirebase(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            goToSecond("")
        }
    }


    private fun getNotesFromFirebase(view: View) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    Toast.makeText(
                        requireActivity().applicationContext, "List is empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                for (snap in snapshot.children) {
                    val note = Note(
                        snap.key.toString(),
                        snap.child("title").value.toString(),
                        snap.child("content").value.toString(),
                        snap.child("datetime").value.toString()
                    )
                    noteList.add(note)

                }
                dbRef.removeEventListener(this)
                initRecyclerView(view)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireActivity().applicationContext, "Cancelled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun initRecyclerView(view: View) {
        noteList.sortBy { it.datetime }
        val recyclerView: RecyclerView = view.findViewById(R.id.noteList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerViewAdapter = RecyclerViewAdapter(noteList, this)
        recyclerView.adapter = recyclerViewAdapter

        val itemSwipeDelete = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showDialog(viewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY, actionState, isCurrentlyActive

                ).addBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity().applicationContext,
                        R.color.deleteColor
                    )
                )
                    .addActionIcon(R.drawable.delete_icon)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemSwipeEdit = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteList[viewHolder.adapterPosition].guid?.let { goToSecond(it) }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY, actionState, isCurrentlyActive

                ).addBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity().applicationContext,
                        R.color.editColor
                    )
                )
                    .addActionIcon(R.drawable.edit_icon)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val swipeDelete = ItemTouchHelper(itemSwipeDelete)
        swipeDelete.attachToRecyclerView(recyclerView)

        val swipeEdit = ItemTouchHelper(itemSwipeEdit)
        swipeEdit.attachToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(
            RecyclerViewItemDecoration(
                requireActivity().applicationContext,
                R.drawable.divider
            )
        )
    }

    private fun showDialog(viewHolder: RecyclerView.ViewHolder) {
        val builder = AlertDialog.Builder(activity)
        val position = viewHolder.adapterPosition
        builder.setTitle("Delete note")
        builder.setMessage("Are you sure you want to delete the note?")
        builder.setPositiveButton("Yes") { _, _ ->
            noteList[position].guid?.let { dbRef.child(it).removeValue() }
            noteList.removeAt(position)
            recyclerViewAdapter.notifyItemRemoved(position)
        }
        builder.setNegativeButton("No") { _, _ ->
            recyclerViewAdapter.notifyItemChanged(position)
        }
        builder.show()
    }

    override fun onItemClick(note: Note) {
        //goToThird(note.guid!!)
    }

    private fun goToSecond(key: String) {
        val fragment: Fragment = SecondFragment.newInstance(key)
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FirstFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}