package com.learngram.mynotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.hbb20.CountryCodePicker


class PhoneAuthFragment : Fragment() {
    private lateinit var ccp:CountryCodePicker
    private lateinit var phone:EditText
    private lateinit var getOtpButton: Button
    private lateinit var fullPhoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ccp=view.findViewById(R.id.ccp)
        phone=view.findViewById(R.id.editTextPhone)
        getOtpButton=view.findViewById(R.id.verifyOtpButton)
        ccp.registerCarrierNumberEditText(phone)
        getOtpButton.setOnClickListener {
             val enteredPhone=phone.text.toString()
             if(enteredPhone.replace(" ","").length<10){
                 Toast.makeText(requireContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
             }
             else{
                 fullPhoneNumber=ccp.fullNumberWithPlus.replace(" ","")
                 Toast.makeText(requireContext(),fullPhoneNumber, Toast.LENGTH_SHORT).show()
                 // Create an instance of VerifyOtpFragment with the phone number
//                 VerifyOtpFragment().apply {
//                     arguments = Bundle().apply {
//                         putString("mobile", fullPhoneNumber)
//                     }
//                 }
                 savePhoneNumber(fullPhoneNumber)
                 val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                 val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                 fragmentTransaction.replace(R.id.loginSignupFragmentContainer, VerifyOtpFragment())
                 fragmentTransaction.addToBackStack(null)
                 fragmentTransaction.commit()

             }
        }
    }

    private fun savePhoneNumber(fullPhoneNumber: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("phone_number", fullPhoneNumber)
        editor.apply() // or editor.commit() if you want to wait for the operation to complete
    }

    companion object {

    }
}