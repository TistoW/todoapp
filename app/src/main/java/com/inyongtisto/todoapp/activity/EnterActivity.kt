package com.inyongtisto.todoapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.activity.auth.LoginActivity
import com.inyongtisto.todoapp.activity.auth.AutListener
import com.inyongtisto.todoapp.activity.auth.AuthViewModel
import com.inyongtisto.todoapp.activity.main.MainActivity
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.helper.MyAlert
import com.inyongtisto.todoapp.helper.SharePref
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.User
import kotlinx.android.synthetic.main.activity_enter.*


class EnterActivity : AppCompatActivity(), AutListener {

    private lateinit var mViewModel: AuthViewModel
    private var googleApiClient: GoogleSignInClient? = null
    private var fcm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        Helper.blackStatusBar(this)

        //get Fcm
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                fcm = task.result?.token!!
                Log.d("Token:", fcm)
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleSignIn.getClient(this, gso)

        mViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        mViewModel.listener = this

        mainButton()
    }

    private val signIn = 90
    private fun mainButton() {
        btn_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btn_google.setOnClickListener {
            val signInIntent: Intent = googleApiClient!!.signInIntent
            startActivityForResult(signInIntent, signIn)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == signIn) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.w("cek", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun updateUI(account: GoogleSignInAccount?) {
        Log.d("GoogleAccount", "Nama:" + account!!.displayName
                + "\nEmail:" + account.email
                + "\nLastName:" + account.familyName
                + "\nFirstName:" + account.givenName
                + "\nphoto:" + account.photoUrl
        )

        MyAlert.loading(this, "Loading...")

        val user = User()
        user.email = account.email
        user.name = account.displayName
        user.photo = account.photoUrl.toString()
        user.password = account.email.toString().substring(0, account.email.toString().indexOf("@"))
        user.fcm = fcm
        mViewModel.loginGoogle(user)
    }

    override fun onSuccess(data: ResponModel) {
        MyAlert.alertDismis()
        val i = Intent(this, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
        val s = SharePref(this)
        s.setStatusLogin(true)
        s.setUser(data.user)
        s.setListTask(data)
        s.setTaskAktive(data.task)
    }

    override fun onFailure(message: String) {
        MyAlert.alertDismis()
        MyAlert.error(this, "Oops..", message)
    }
}
