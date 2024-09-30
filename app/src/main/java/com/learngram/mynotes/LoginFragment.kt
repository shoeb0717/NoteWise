package com.learngram.mynotes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import de.hdodenhof.circleimageview.CircleImageView


class LoginFragment : Fragment() {

    private lateinit var signUpButton: TextView
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var forgotPassword:TextView
    private lateinit var phoneImage: CircleImageView
    private lateinit var googleImage: CircleImageView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userFilledEmail:EditText
    private lateinit var userFilledPassword:EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpButton=view.findViewById(R.id.loginPageSigninButton)
        loginButton=view.findViewById(R.id.loginPageloginButton)
        forgotPassword=view.findViewById(R.id.forgotPassword)
        phoneImage=view.findViewById(R.id.loginPhone)
        googleImage=view.findViewById(R.id.loginGoogle)
        userFilledEmail=view.findViewById(R.id.login_useremail)
        userFilledPassword=view.findViewById(R.id.login_userpassword)
        auth=FirebaseAuth.getInstance()


        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient=GoogleSignIn.getClient(requireContext(),gso)

        signUpButton.setOnClickListener{
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginSignupFragmentContainer, SignUpFragment())
            fragmentTransaction.commit()
        }

        loginButton.setOnClickListener{
            val userEmail=userFilledEmail.text.toString()
            val userPassword=userFilledPassword.text.toString()

            if(userEmail.isEmpty() || userPassword.isEmpty()){
                Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT).show()
                if(userEmail.isEmpty()){
                    userFilledEmail.error = "Please fill all details"
                }
                if(userPassword.isEmpty()){
                    userFilledPassword.error = "Please fill all details"
                }
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                userFilledEmail.error = "Please enter a valid email"
                return@setOnClickListener
            }
            if(userPassword.length<6 || userPassword.length>8){
                userFilledPassword.error="Password must 6 to 8 characters"
                return@setOnClickListener
            }
            else{
                auth.signInWithEmailAndPassword(userEmail,userPassword)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(requireContext(), "Login successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                        else{
                            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Sign-In failed")
                                .setContentText("No such user found..")
                                .show()
                        }

                    }
            }
        }

        forgotPassword.setOnClickListener{
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginSignupFragmentContainer, ResetPasswordFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        phoneImage.setOnClickListener{
            Toast.makeText(requireContext(), "Phone clicked", Toast.LENGTH_SHORT).show()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginSignupFragmentContainer, PhoneAuthFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        googleImage.setOnClickListener {
            Toast.makeText(requireContext(), "Google clicked", Toast.LENGTH_SHORT).show()

            googleSignInClient.signOut().addOnCompleteListener {
                signInWithGoogle()
            }


        }

    }
    private fun signInWithGoogle()
    {
        val signInIntent=googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {result ->
        if(result.resultCode==Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
       if(task.isSuccessful){
           val account:GoogleSignInAccount?=task.result
           if(account!=null){
               updateUI(account)
           }
       }
        else{
           Toast.makeText(requireContext(), "Sign in Failed, Please try again later..", Toast.LENGTH_SHORT).show()
       }
    }

    private fun updateUI(account: GoogleSignInAccount) {
       val credential=GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(requireContext(), "Login successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
            else{
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Sign-In failed")
                    .setContentText("No such user found..")
                    .show()
            }
        }
    }

    companion object {

    }
}