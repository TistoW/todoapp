package com.inyongtisto.todoapp.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.inyongtisto.todoapp.activity.main.MainActivity
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.helper.MyAlert
import com.inyongtisto.todoapp.helper.SharePref
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), AutListener {

    private lateinit var mViewModel: AuthViewModel
    private var fcm: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Helper.blackStatusBar(this)
        mViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        mViewModel.listener = this

        //get Fcm
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                fcm = task.result?.token!!
                Log.d("Token:", fcm)
            }

        mainButton()
    }

    private fun mainButton() {
        btn_login.setOnClickListener {
            if (edt_email.text.isEmpty()) {
                edt_email.error = "Field emails can't be empty"
                edt_email.requestFocus()
                return@setOnClickListener
            } else if (edt_password.text.isEmpty()) {
                edt_password.error = "Field passwords can't be empty"
                edt_password.requestFocus()
                return@setOnClickListener
            } else if (!edt_email.text.contains("@")) {
                edt_email.error = "Email format is wrong"
                edt_email.requestFocus()
                return@setOnClickListener
            }

            MyAlert.loading(this, "Loading...")
            val user = User()
            user.email = edt_email.text.toString()
            user.password = edt_password.text.toString()
            user.fcm = fcm
            mViewModel.onProgress()
            mViewModel.login(user)
        }

        btn_lupaPassword.setOnClickListener {
            if (edt_email.text.isEmpty()) {
                edt_email.error = "Please enter your correct email address"
                edt_email.requestFocus()
                return@setOnClickListener
            }
            div_login.visibility = View.GONE
            div_password.visibility = View.VISIBLE
        }

        btn_changePassword.setOnClickListener {

            val pas1 = edt_password1.text.toString()
            val pas2 = edt_password2.text.toString()
            Log.d("pass", "ps1:$pas1 ps2:$pas2")
            if (pas1 != pas2) {
                edt_password1.error = "Password doesn't match"
                edt_password1.requestFocus()
                return@setOnClickListener
            } else if (edt_password1.text.isEmpty()) {
                edt_password1.error = "Field passwords can't be empty"
                edt_password1.requestFocus()
                return@setOnClickListener
            }

            MyAlert.loading(this, "Loading...")
            val user = User()
            user.email = edt_email.text.toString()
            user.password = edt_password1.text.toString()

            mViewModel.lupaPassword(user)
        }
    }


    override fun onSuccess(data: ResponModel) {
        MyAlert.alertDismis()
        if (data.message == "Password Changed") {
            MyAlert.success(this, "Success", "Your password has been changed\ntry to login with the new password")
            onBackPressed()
            return
        }

        val s = SharePref(this)
        s.setStatusLogin(true)
        s.setUser(data.user)
        s.setListTask(data)
        s.setTaskAktive(data.task)
        val i = Intent(this, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
    }

    override fun onFailure(message: String) {
        MyAlert.alertDismis()
        when (message) {
            "Email tidak terdaftar" -> {
                val intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra("email", edt_email.text.toString())
                startActivity(intent)
                finish()
            }
            "Password Salah" -> {
                edt_password.error = "Password Salah"
                edt_password.requestFocus()
            }
            else -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (div_password.visibility == View.VISIBLE) {
            div_password.visibility = View.GONE
            div_login.visibility = View.VISIBLE
            return
        } else {
            super.onBackPressed()
        }
    }
}
