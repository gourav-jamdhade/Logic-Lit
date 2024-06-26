package com.example.rationalbooks

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rationalbooks.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SingUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.btnSignUp.setOnClickListener {
            
            val email = binding.etEmailLogin2.text.toString()
            val password = binding.etPasswordSignUp2.text.toString()
            val confirmPassword = binding.etConfirmPasswordSignUp2.text.toString()
            
            if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }else if(!password.equals(confirmPassword)){
                Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show()
            }else{
                if(!checkEmailExist(email)){
                    createUser(email, password)
                }else{
                    Toast.makeText(this, "User already exist with this email address!", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    private fun checkEmailExist(email: String): Boolean {

        var check = false
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    val result = task.result
                    if(result.signInMethods!!.isEmpty()){
                        check = false;
                    }else{
                        check = true
                    }
                }

            }

        return check
    }

    private fun createUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "SignUp Failed", Toast.LENGTH_SHORT).show()
                }

            }
    }
}