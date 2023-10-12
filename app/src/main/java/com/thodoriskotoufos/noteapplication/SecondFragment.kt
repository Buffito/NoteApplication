package com.thodoriskotoufos.noteapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thodoriskotoufos.noteapplication.databinding.FragmentSecondBinding
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
            /// TODO: add dialog to confirm cancellation
            goBackToFirst()
        }

        binding.saveButton.setOnClickListener {
            /// TODO: add save functionality, clear after saving, return to main fragment after saving
            if (binding.editTextNoteTitle.text.isNotEmpty() || binding.editTextNoteContents.text.isNotEmpty()){
                writeNoteToDB(binding.editTextNoteTitle.text.toString(),binding.editTextNoteContents.text.toString())

                binding.editTextNoteTitle.text.clear()
                binding.editTextNoteContents.text.clear()
            }


        }
    }

    private fun writeNoteToDB(title: String, contents: String){
        val note = Note(title,contents)
        val database = Firebase.database.reference
        val uuid = UUID.randomUUID().toString()
        database.root.child("notes").child(uuid).setValue(note)
        //database.child("notes").setValue(note)

    }

    private fun goBackToFirst(){
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}