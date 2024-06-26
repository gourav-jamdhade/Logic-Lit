package com.example.rationalbooks

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rationalbooks.databinding.ActivityWelcomeScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val appName = binding.tvAppName.text.toString()

        val sharedPreferences = getSharedPreferences("Skipped", MODE_PRIVATE)
        val hasSkipped = sharedPreferences.getBoolean("hasSkipped", false)

        if (hasSkipped) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val paint: TextPaint = binding.tvAppName.paint
        val width:Float = paint.measureText(appName)
        val height:Float = binding.tvAppName.textSize

        val textShader: Shader = LinearGradient(
            0f, 0f, width, height,

            intArrayOf(
                Color.parseColor("#ff0000"),
                Color.parseColor("#ff5050"),
                Color.parseColor("#ff0066"),
                Color.parseColor("#ff3399"),
                Color.parseColor("#ff33cc"),
                Color.parseColor("#ff00ff"),
            ), null, Shader.TileMode.CLAMP)

        binding.tvAppName.paint.shader = textShader

        binding.btnSkip.setOnClickListener {

            val editor = sharedPreferences.edit()
            editor.putBoolean("hasSkipped", true);
            editor.apply();

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }

        // Google Sign IN

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)



        binding.btnGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent

            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.btnSignUp.setOnClickListener{
            startActivity(Intent(this, SingUpActivity::class.java))
            finish()
        }



    }

    private fun firebaseAuthWithGoogle(idToken: String?) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            }catch (e : ApiException){
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}