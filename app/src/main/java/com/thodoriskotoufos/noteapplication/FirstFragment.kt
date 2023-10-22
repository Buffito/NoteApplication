package com.thodoriskotoufos.noteapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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


    private fun getNotesFromFirebase(view: View){
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists() || !snapshot.hasChildren()){
                    Toast.makeText(requireActivity().applicationContext,"List is empty!",
                        Toast.LENGTH_SHORT).show()
                    return
                }
                for (snap in snapshot.children) {
                    val note =  Note(snap.key.toString(),snap.child("title").value.toString(),
                        snap.child("content").value.toString(), snap.child("datetime").value.toString())
                    noteList.add(note)

                }
                initRecyclerView(view)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity().applicationContext,"Cancelled",
                    Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun initRecyclerView(view: View){
        val recyclerView: RecyclerView = view.findViewById(R.id.noteList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerViewAdapter = RecyclerViewAdapter(noteList, this)
        recyclerView.adapter = recyclerViewAdapter
    }
    override fun onItemClick(note: Note) {
        goToSecond(note.guid!!)
    }

    private fun goToSecond(key: String){
        val fragment: Fragment = SecondFragment.newInstance(key)
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out)
        transaction.replace(R.id.frame_container,fragment)
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