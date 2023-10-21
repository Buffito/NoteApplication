package com.thodoriskotoufos.noteapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thodoriskotoufos.noteapplication.databinding.FragmentSecondBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            goBackToFirst()
        }

        binding.saveButton.setOnClickListener {
            if (binding.editTextNoteTitle.text.isNotEmpty() && binding.editTextNoteContents.text.isNotEmpty()){
                writeNoteToDB(binding.editTextNoteTitle.text.toString(),binding.editTextNoteContents.text.toString())

                goBackToFirst()
            }else{
                Toast.makeText(requireActivity().applicationContext,"Please fill BOTH title and contents",Toast.LENGTH_SHORT).show()
            }


        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun writeNoteToDB(title: String, contents: String){
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val current = formatter.format(time)

        val note =  Note(title,contents, current)

        val database = Firebase.database.reference
        database.root.child("notes").child(UUID.randomUUID().toString()).setValue(note)

    }

    private fun goBackToFirst(){
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}