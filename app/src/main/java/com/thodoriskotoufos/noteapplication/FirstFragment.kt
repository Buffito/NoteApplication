package com.thodoriskotoufos.noteapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thodoriskotoufos.noteapplication.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var dbRef: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbRef = FirebaseDatabase.getInstance().getReference("notes")


        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = ArrayList<Note>()

                if(!snapshot.exists() || !snapshot.hasChildren()){
                    Toast.makeText(requireActivity().applicationContext,"List is empty!",
                        Toast.LENGTH_SHORT).show()
                    return
                }

                for (snap in snapshot.children) {
                    val t = snap.child("title").value.toString()
                    val d = snap.child("datetime").value.toString()
                    val c = snap.child("content").value.toString()

                    val note =  Note(t,c, d)
                    temp.add(note)
                }

                val recyclerViewAdapter = RecyclerViewAdapter(temp)
                val recyclerView: RecyclerView = binding.noteList
                recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
                recyclerView.adapter = recyclerViewAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.fab.setOnClickListener { findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

}