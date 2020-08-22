package com.inyongtisto.todoapp.activity.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.activity.EnterActivity
import com.inyongtisto.todoapp.activity.main.MainActivity
import com.inyongtisto.todoapp.activity.main.MainViewModel
import com.inyongtisto.todoapp.adapter.AdapterTask
import com.inyongtisto.todoapp.app.ApiConfig
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.helper.SharePref
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.Task
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity(), SplashListener {

    lateinit var s: SharePref
    private lateinit var mViewModel: SplashViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        s = SharePref(this)
        Helper.blackStatusBar(this)

        mViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        mViewModel.listener = this

        cekStatusLogin()
    }

    private fun cekStatusLogin() {
        val handler = Handler()
        handler.postDelayed({
            if (s.getStatusLogin())
                mViewModel.getTask(s.getUser()!!)
            else {
                startActivity(Intent(this, EnterActivity::class.java))
                finish()
            }
        }, 1000)

    }

    private fun startActivityMainDelay() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSuccess(data: ResponModel) {
        s.setListTask(data)
        startActivityMainDelay()
    }

    override fun onFailure(message: String) {
        Helper.toast(this, message)
    }
}
