package com.inyongtisto.todoapp.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.inyongtisto.todoapp.activity.main.MainActivity
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.helper.MyAlert
import com.inyongtisto.todoapp.helper.SharePref
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.User
import kotlinx.android.synthetic.main.activity_login.btn_login
import kotlinx.android.synthetic.main.activity_login.edt_email
import kotlinx.android.synthetic.main.activity_login.edt_password
import kotlinx.android.synthetic.main.activity_login.pd
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), AutListener {

    private lateinit var mViewModel: AuthViewModel

    private var fcm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Helper.blackStatusBar(this)
        mViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        mViewModel.listener = this

        //get Fcm
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                fcm = task.result?.token!!
                Log.d("Token:", fcm)
            }

        setData()
        mainButton()
    }

    private fun setData() {
        val email = intent.getStringExtra("email")!!.toString()
        edt_email.setText(email)

        edt_name.setText(email.substring(0, email.indexOf("@")))
    }

    private fun mainButton() {
        btn_login.setOnClickListener {
            when {
                edt_name.text.isEmpty() -> {
                    edt_name.error = "Kolom Email tidak boleh kosong"
                    edt_name.requestFocus()
                    return@setOnClickListener
                }
                edt_email.text.isEmpty() -> {
                    edt_email.error = "Kolom Email tidak boleh kosong"
                    edt_email.requestFocus()
                    return@setOnClickListener
                }
                edt_password.text.isEmpty() -> {
                    edt_password.error = "Kolom Password tidak boleh kosong"
                    edt_password.requestFocus()
                    return@setOnClickListener
                }
                else -> {
                    MyAlert.loading(this, "Loading...")
                    val user = User()
                    user.name = edt_name.text.toString()
                    user.email = edt_email.text.toString()
                    user.password = edt_password.text.toString()
                    user.fcm = fcm
                    mViewModel.onProgress()
                    mViewModel.register(user)
                }
            }
        }
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
