package com.inyongtisto.todoapp.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Helper.blackStatusBar(this)
        mViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        mViewModel.listener = this

        mainButton()
    }

    private fun mainButton() {
        btn_login.setOnClickListener {
            if (edt_email.text.isEmpty()) {
                edt_email.error = "Kolom Email tidak boleh kosong"
                edt_email.requestFocus()
                return@setOnClickListener
            } else if (edt_password.text.isEmpty()) {
                edt_password.error = "Kolom Password tidak boleh kosong"
                edt_password.requestFocus()
                return@setOnClickListener
            }

            MyAlert.loading(this, "Loading...")
            val user = User()
            user.email = edt_email.text.toString()
            user.password = edt_password.text.toString()

            mViewModel.onProgress()
            mViewModel.login(user)
        }
    }

    override fun onSuccess(data: ResponModel) {
        val i = Intent(this, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
        SharePref(this).setStatusLogin(true)
        SharePref(this).setUser(data.user)
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
}
