package com.thodoriskotoufos.noteapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

private const val GUID = ""

class SecondFragment : Fragment() {

    private var key: String? = null
    private val database: DatabaseReference = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(GUID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleText: TextView = view.findViewById(R.id.editTextNoteTitle)
        val contentText: TextView = view.findViewById(R.id.editTextNoteContents)

        if (key!!.isNotEmpty())
            getFromFirebase(key!!, titleText, contentText)

        view.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            backToFirst()
        }

        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            if (key!!.isNotEmpty())
                updateFirebase(key!!, titleText.text.toString(), contentText.text.toString())
            else
                writeNoteToDB(titleText.text.toString(), contentText.text.toString())

        }

    }

    private fun updateFirebase(key: String, title: String, contents: String) {
        if (title.isEmpty() || contents.isEmpty()) {
            Toast.makeText(
                requireActivity().applicationContext, "Please fill BOTH title and contents",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val note = Note(key, title, contents, getCurrentDate())
        database.root.child("notes").child(key).setValue(note)
            .addOnSuccessListener {
                Toast.makeText(
                    requireActivity().applicationContext, "Updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireActivity().applicationContext, "Error updating",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getFromFirebase(key: String, titleText: TextView, contentsText: TextView) {
        database.child("notes").child(key).get().addOnSuccessListener {
            titleText.text = it.child("title").value.toString()
            contentsText.text = it.child("content").value.toString()
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)

            Toast.makeText(
                requireActivity().applicationContext, "Error getting data",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun writeNoteToDB(title: String, contents: String) {
        if (title.isEmpty() || contents.isEmpty()) {
            Toast.makeText(
                requireActivity().applicationContext, "Please fill BOTH title and contents",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val guid = UUID.randomUUID().toString()
        val note = Note(guid, title, contents, getCurrentDate())
        database.root.child("notes").child(guid).setValue(note)
            .addOnSuccessListener {
                Toast.makeText(
                    requireActivity().applicationContext, "Saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                backToFirst()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireActivity().applicationContext, "Error saving",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")

        return formatter.format(time)
    }

    private fun backToFirst() {
        val fragment: Fragment = FirstFragment.newInstance()
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(key: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(GUID, key)
                }
            }
    }
}