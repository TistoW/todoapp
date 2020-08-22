package com.inyongtisto.todoapp.activity.splash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
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
import com.inyongtisto.todoapp.helper.MyAlert
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

        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data

        handleIntent(intent!!)

        mViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        mViewModel.listener = this

        if (checkInternet()) {
            cekStatusLogin()
        } else {
            onInternetError()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent!!)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { recipeId ->
//                Uri.parse("content://com.recipe_app/recipe/")
                Uri.parse("https://inyongtisto.com/todoapps/")
                    .buildUpon()
                    .appendPath(recipeId)
                    .build().also { appData ->
                        Log.d("Cek", "appData:$appData")
//                        showRecipe(appData)
                    }
            }
        }
    }

    fun onInternetError() {
        MyAlert.alertDismis()
        MyAlert.info(this, "Oops", "Check your internet Connection", "Close App", "Try Angin", object : MyAlert.DefaultCallback() {
            override fun onCancelCliked() {
                MyAlert.loading(this@SplashActivity, "Cheking...")
                val handler = Handler()
                handler.postDelayed({
                    if (checkInternet())
                        mViewModel.getTask(s.getUser()!!)
                    else {
                        onInternetError()
                    }
                }, 2000)
            }

            override fun onConfirmCliked() {
                MyAlert.alertDismis()
                finish()
            }
        })
    }

    private fun checkInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun cekStatusLogin() {
        val handler = Handler()
        handler.postDelayed({
            if (s.getStatusLogin())
                if (s.getListTask() != null && s.getListTask()!!.tasks.size > 0) {
                    startActivityMainDelay()
                } else {
                    mViewModel.getTask(s.getUser()!!)
                }
            else {
                startActivity(Intent(this, EnterActivity::class.java))
                finish()
            }
        }, 500)

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
        onInternetError()
    }
}
