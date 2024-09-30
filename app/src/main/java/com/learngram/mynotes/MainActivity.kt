package com.learngram.mynotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.learngram.mynotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().reference
        binding.createnotebutton.setOnClickListener{
           startActivity(Intent(this,AddNoteActivity::class.java))
        }

        binding.opennotebutton.setOnClickListener{
            startActivity(Intent(this,AllNotes::class.java))
        }

        binding.logoutButton.setOnClickListener{
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Log out?")
                .setContentText("Are you sure want to log out?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener {
                    auth.signOut()
                    startActivity(Intent(this,LoginSignupActivity::class.java))
                    finish()
                }
                .show()
        }

        binding.deleteAccountButton.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Delete Account?")
                .setContentText("Are you sure you want to delete your account?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener{
                    val user = auth.currentUser
                    user?.let { currentUser->
                        val userReference=databaseReference.child("users")
                        userReference.child(currentUser.uid).removeValue()
                    }
                    user?.delete()?.addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Deleted!")
                                .setContentText("Your account has been deleted.")
                                .setConfirmClickListener {
                                    startActivity(Intent(this, LoginSignupActivity::class.java))
                                    finish()
                                }
                                .show()

                        } else {
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Could not delete account.")
                                .show()
                        }
                    }
                }
                .show()
        }

    }
}