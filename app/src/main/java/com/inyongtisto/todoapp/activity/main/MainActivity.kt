package com.inyongtisto.todoapp.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.activity.AboutUsActivity
import com.inyongtisto.todoapp.activity.splash.SplashActivity
import com.inyongtisto.todoapp.adapter.AdapterTask
import com.inyongtisto.todoapp.fragment.TodoFragment
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.helper.MyAlert
import com.inyongtisto.todoapp.helper.SharePref
import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.Todo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_list_task.*


class MainActivity : AppCompatActivity(), MainListener, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var viewModel: MainViewModel
    private var lstTask: ArrayList<Task> = ArrayList()
    private lateinit var s: SharePref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Helper.blackStatusBar(this)
        s = SharePref(this)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getTask(s.getUser()!!)
        viewModel.listener = this
        viewModel.listTask.observe(this, Observer {
            lstTask = it
        })

        cekDate()
        mainButton()

        Helper.pullRefrash(swipeRefrash, object : Helper.OnRefrashListeners {
            override fun onRefrash() {
                swipeRefrash.isRefreshing = false
            }
        })
    }

    private fun cekDate() {
        if (SharePref(this).getListTask() != null) {
            lstTask = s.getListTask()!!.tasks
            if (s.getTaskAktive() != null) {
                openFragment(s.getTaskAktive()!!, true)
                return
            }

            openFragment(lstTask[0], true)
        }
    }

    private fun mainButton() {
        btn_menu.setOnClickListener {
            bottomSheet()
        }

        btn_detailTask.setOnClickListener {
            detailTaskSheet()
        }
    }

    private var dialogDetail: BottomSheetDialog? = null
    private fun detailTaskSheet() {
        val view: View = layoutInflater.inflate(R.layout.layout_detail_task, null)
        dialogDetail = BottomSheetDialog(this)
        dialogDetail!!.setContentView(view)
        dialogDetail!!.show()

        val btnShare = view.findViewById<LinearLayout>(R.id.btn_share)
        val btnRename = view.findViewById<LinearLayout>(R.id.btn_rename)
        val btnDelete = view.findViewById<LinearLayout>(R.id.btn_delete)
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvWaring = view.findViewById<TextView>(R.id.tv_waring)

        tvNama.text = s.getTaskAktive()!!.task

        if (lstTask.size == 1) {
            tvStatus.setTextColor(resources.getColor(R.color.grey_5))
            tvWaring.visibility = View.VISIBLE
        }

        btnShare.setOnClickListener {
            shareTask()
            dialogDetail!!.dismiss()
        }

        btnRename.setOnClickListener {
            createTaskSheet(1)
            dialogDetail!!.dismiss()
        }

        btnDelete.setOnClickListener {
            if (lstTask.size == 1) {
                return@setOnClickListener
            }
            dialogDetail!!.dismiss()
            viewModel.deleteTask(s.getTaskAktive()!!)
            lstTask.removeAt(s.getTaskAktive()!!.position)
            openFragment(lstTask[0], false)
        }
    }

    private fun shareTask() {
        if (s.getListTodo()!!.todos.size == 0) {
            MyAlert.info(this, "Oops...", "You don't have any tasks yet")
            return
        }

        var lstTodo = "**To-do On Progress**\n"
        var lstTodoComplete = "\n**Completed To-do**\n"
        for (t: Todo in s.getListTodo()!!.todos) {
            if (t.status == "0")
                lstTodoComplete += "${t.todo}\n"
            else
                lstTodo += "${t.todo}\n"
        }

        val message = "https://wetheapp.com/\n\n$lstTodo $lstTodoComplete"
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share to.."))
    }

    private fun bottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.layout_list_task, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()

        val btnShare = view.findViewById<LinearLayout>(R.id.btn_share)
        val btnSignOut = view.findViewById<LinearLayout>(R.id.btn_signOut)
        val btnCreate = view.findViewById<LinearLayout>(R.id.btn_createTask)
        val btnAboutUs = view.findViewById<LinearLayout>(R.id.btn_aboutUs)
        val rvTask = view.findViewById<RecyclerView>(R.id.rv_task)
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email)

        tvNama.text = s.getUser()?.name
        tvEmail.text = s.getUser()?.email

        btnShare.setOnClickListener {
            val message = "https://wetheapp.com/\n\nHai, saya ada aplikasi keren nih, teman-teman bisa coba untuk mencatat pekerjaan teman-teman semua\nhttps://inyongtisto.com/todoapps"
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share to.."))
            dialog.dismiss()
        }

        btnAboutUs.setOnClickListener {
            MyAlert.loading(this, "Loading...")
            startActivity(Intent(this, AboutUsActivity::class.java))
            dialog.dismiss()
        }

        btnSignOut.setOnClickListener {
            s.clearAll()
            val i = Intent(applicationContext, SplashActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("EXIT", true)
            startActivity(i)
            finish()
            signOut()
        }

        btnCreate.setOnClickListener {
            createTaskSheet(0)
            dialog.dismiss()
        }

        val layoutManager2 = LinearLayoutManager(this)
        layoutManager2.orientation = LinearLayoutManager.VERTICAL

        rvTask.setHasFixedSize(false)
        rvTask.layoutManager = layoutManager2
        rvTask.adapter = AdapterTask(lstTask, object : AdapterTask.Listener {
            override fun onClick(data: Task) {
                openFragment(data, false)
                dialog.dismiss()
                s.setTaskAktive(data)
            }
        })
    }

    private var googleApiClient: GoogleApiClient? = null
    fun signOut() {
        googleApiClient!!.connect()
        Auth.GoogleSignInApi.signOut(googleApiClient)
    }

    fun openFragment(task: Task, b: Boolean) {
        val bundle = Bundle()
        bundle.putString("task", "" + task.id)
        bundle.putString("title", "" + task.task)
        val frag = TodoFragment()
        frag.arguments = bundle
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (b) {
            transaction.add(R.id.container, frag, "Todo Fragment")
        } else {
            transaction.replace(R.id.container, frag, "Todo Fragment")
        }
        transaction.commit()
    }

    lateinit var dialogCreate: BottomSheetDialog

    @SuppressLint("SetTextI18n")
    private fun createTaskSheet(i: Int) {
        val view: View = layoutInflater.inflate(R.layout.layout_create_task, null)
        dialogCreate = BottomSheetDialog(this)
        dialogCreate.setContentView(view)
        dialogCreate.show()
        dialogCreate.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val btnCreate = view.findViewById<TextView>(R.id.btn_done)
        val btnClose = view.findViewById<RelativeLayout>(R.id.btn_close)

        val tvHeader = view.findViewById<TextView>(R.id.tv_header)
        val edtTitle = view.findViewById<EditText>(R.id.edt_title)
        val pd = view.findViewById<ProgressBar>(R.id.pd)

        if (i == 1) {
            tvHeader.text = "Rename list"
            edtTitle.hint = "Enter List title"
            edtTitle.setText(s.getTaskAktive()!!.task.toString())
        }

        btnCreate.setOnClickListener {
            if (edtTitle.text.isEmpty()) {
                dialogCreate.dismiss()
                return@setOnClickListener
            } else {
                if (i == 0) {
                    val task = Task()
                    task.user_id = s.getUser()!!.id
                    task.task = edtTitle.text.toString()
                    viewModel.createTask(task)
                    viewModel.onProgress()

                    viewModel.showProgress.observe(this, Observer {
                        if (it) {
                            pd.visibility = View.VISIBLE
                        } else {
                            pd.visibility = View.GONE
                        }
                    })
                } else {
                    val mTask = s.getTaskAktive()!!
                    var task = Task()
                    task.id = mTask.id
                    task.task = edtTitle.text.toString()
                    viewModel.updateTask(task)
                    task = mTask
                    task.task = edtTitle.text.toString()
                    s.setTaskAktive(task)
                    dialogCreate.dismiss()
                    lstTask[mTask.position] = task
                    openFragment(task, false)
                }
            }
        }

        btnClose.setOnClickListener {
            dialogCreate.dismiss()
        }
    }

    override fun onSuccess(data: Task) {
        dialogCreate.dismiss()
        openFragment(data, false)
        data.position = (lstTask.size - 1)
        s.setTaskAktive(data)

    }

    override fun onFailure(message: String) {
        dialogCreate.dismiss()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        googleApiClient!!.connect()
        super.onResume()
    }
}
