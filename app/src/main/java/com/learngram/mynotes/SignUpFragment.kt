package com.learngram.mynotes

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {

    private lateinit var signinButton: TextView
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatpasswordEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signinButton=view.findViewById(R.id.signupPageSigninButton)
        registerButton=view.findViewById(R.id.signupPageRegisterButton)
        emailEditText=view.findViewById(R.id.signup_email)
        usernameEditText=view.findViewById(R.id.signup_username)
        passwordEditText=view.findViewById(R.id.signup_password)
        repeatpasswordEditText=view.findViewById(R.id.signup_repeat_password)
        auth=FirebaseAuth.getInstance()
        signinButton.setOnClickListener{
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginSignupFragmentContainer,LoginFragment())
            fragmentTransaction.commit()
        }

        registerButton.setOnClickListener{
            val email=emailEditText.text.toString()
            val username=usernameEditText.text.toString()
            val password=passwordEditText.text.toString()
            val repeatpassword=repeatpasswordEditText.text.toString()

            if(email.isEmpty() || username.isEmpty() || password.isEmpty() || repeatpassword.isEmpty()){
                Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT).show()
                if(email.isEmpty()){
                    emailEditText.error = "Please fill all details"
                }
                if(username.isEmpty()){
                    usernameEditText.error = "Please fill all details"
                }
                if(password.isEmpty()){
                    passwordEditText.error="Please fill all details"
                }
                if(repeatpassword.isEmpty()){
                    repeatpasswordEditText.error="Please fill all details"
                }
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                emailEditText.error="Please enter a valid email"
                return@setOnClickListener
            }
            if (!isValidUsername(username)) {
                // Valid username
                Toast.makeText(requireContext(), "Username must contain at least one alphabet and no special symbols.", Toast.LENGTH_SHORT).show()
                usernameEditText.error="Username must contain at least one alphabet and no special symbols."
                return@setOnClickListener
            }
            if(password.length<6 || password.length>8){
                Toast.makeText(requireContext(), "Password length must be 6 to 8", Toast.LENGTH_SHORT).show()
                passwordEditText.error="Password length must be 6 to 8"
                return@setOnClickListener
            }
            else if(password!=repeatpassword){
                Toast.makeText(requireContext(), "Repeat password must be same", Toast.LENGTH_SHORT).show()
                passwordEditText.error="Repeat password must be same"
                repeatpasswordEditText.error="Repeat password must be same as password"
                return@setOnClickListener
            }

            else{
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Registered successfully")
                                .setContentText("Now you can login !")
                                .show()

                            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.loginSignupFragmentContainer,LoginFragment())
                            fragmentTransaction.commit()
                        }
                        else{
                            SweetAlertDialog(requireContext(),SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Registered failed")
                                .setContentText("Please try again..")
                                .show()
                        }
                    }
            }
        }
    }

    private fun isValidUsername(username: String): Boolean {
        // Regex to check for at least one letter and no special symbols
        val regex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+$".toRegex()
        return regex.matches(username)
    }

    companion object {


    }
}