package com.learngram.mynotes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.learngram.mynotes.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {
    private val binding : ActivityAddNoteBinding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Initailizing firebase database reference
        databaseReference=FirebaseDatabase.getInstance().reference
        auth=FirebaseAuth.getInstance()

        binding.saveNoteButton.setOnClickListener{
            //get text from edittext to save
            val title=binding.editTextTitle.text.toString()
            val description=binding.editTextDescription.text.toString()

            if(title.isEmpty() || description.isEmpty()){
                Toast.makeText(this, "Fill both fields", Toast.LENGTH_SHORT).show()
            }
            else{
                val currentUser=auth.currentUser
                currentUser?.let { user->
                    //Generate unique key for notes
                    val noteKey=databaseReference.child("users").child(user.uid).child("notes").push().key

                    //Note item
                    val noteItem=NoteItem(title,description,noteKey?:"")

                    if(noteKey!=null){
                        //add note to user note
                        databaseReference.child("users").child(user.uid).child("notes").child(noteKey).setValue(noteItem)
                            .addOnCompleteListener { task->
                                if(task.isSuccessful){
                                    Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                else{
                                    Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                }
            }

        }

    }
}