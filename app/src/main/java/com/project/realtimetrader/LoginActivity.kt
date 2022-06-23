package com.project.realtimetrader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.firestore.model.User
import com.project.firestore.model.network.Callback
import com.project.firestore.model.network.FirestoreService
import com.project.firestore.model.network.USER_COLLECTION_NAME
import java.lang.Exception

const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {


    private val TAG = "LoginActivity"

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
    }

    fun onStartClicked(view: View) {
        view.isEnabled = false // Deshabilitar el boton Start
        val username : EditText = findViewById(R.id.username)
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful){
                username.text.toString()
                val user = User()
                user.username = username.toString()
                saveUserAndStartMainActivity(user, view)
            } else {
                showErrorMessage(view)
                view.isEnabled = true
            }
        }
    }

    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreService.setDocument(
            user,
            USER_COLLECTION_NAME,
            user.username,
            object :
                Callback<Void> {
                override fun onSuccess(result: Void?) {
                    startMainActivity(user.username)
                }

                override fun onFailed(exception: Exception) {
                    showErrorMessage(view)
                    Log.e(TAG, "error", exception)
                    view.isEnabled = true
                }
            })
    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }
}