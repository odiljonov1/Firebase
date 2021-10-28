package com.example.firebaseauthcodial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebaseauthcodial.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN = 1
    lateinit var binding: ActivityMainBinding
    lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "MainActivity"
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        binding.btnSign.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN){
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val  account = task.getResult(ApiException::class.java)!!
                Log.d(TAG,"firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                Log.w(TAG, "Google sign in failed",e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){  task ->
                if (task.isSuccessful){

                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser


                    Toast.makeText(this, "${user?.email}", Toast.LENGTH_SHORT).show()
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }

            }
    }
}