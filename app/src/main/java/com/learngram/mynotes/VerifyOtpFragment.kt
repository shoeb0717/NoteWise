package com.learngram.mynotes

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class VerifyOtpFragment : Fragment() {

    private lateinit var otp:EditText
    private lateinit var verifyButton: Button
    private lateinit var phoneNumber:String
    private lateinit var otpid:String
    private lateinit var auth:FirebaseAuth
    private lateinit var checkError:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Safely retrieve the phone number from arguments
//        phoneNumber = arguments?.getString("mobile") ?: run {
//            Toast.makeText(requireContext(), "Phone number is missing", Toast.LENGTH_SHORT).show()
//            return
//        }
        loadPhoneNumber()

        otp=view.findViewById(R.id.editTextPhone)
        checkError=view.findViewById(R.id.checkError)
        verifyButton=view.findViewById(R.id.verifyOtpButton)
        auth=FirebaseAuth.getInstance()

        Toast.makeText(requireContext(),phoneNumber, Toast.LENGTH_SHORT).show()
        initiiateOTP()

        verifyButton.setOnClickListener {
            if(otp.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Blank field can not be processed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(otp.text.toString().length!=6){
                Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                val credential:PhoneAuthCredential=PhoneAuthProvider.getCredential(otpid,otp.text.toString())
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun loadPhoneNumber() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        val gotData= sharedPreferences.getString("phone_number", null)

        if (gotData != null) {
            phoneNumber=gotData
            Toast.makeText(requireContext(), "Loaded phone number: $phoneNumber", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No phone number found", Toast.LENGTH_SHORT).show()
        }


    }

    private fun initiiateOTP() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    checkError.text = e.message
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    otpid=verificationId
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(requireContext(),MainActivity::class.java))
                    requireActivity().finish()

                } else {
                    Toast.makeText(requireContext(), "Sign-in Code Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {

    }
}