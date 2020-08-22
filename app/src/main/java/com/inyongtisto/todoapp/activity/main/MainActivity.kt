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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inyongtisto.todoapp.R
import com.inyongtisto.todoapp.activity.splash.SplashActivity
import com.inyongtisto.todoapp.adapter.AdapterTask
import com.inyongtisto.todoapp.fragment.TodoFragment
import com.inyongtisto.todoapp.helper.Helper
import com.inyongtisto.todoapp.helper.SharePref
import com.inyongtisto.todoapp.model.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_list_task.*


class MainActivity : AppCompatActivity(), MainListener {

    private lateinit var viewModel: MainViewModel
    private var lstTask: ArrayList<Task> = ArrayList()
    private lateinit var s: SharePref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Helper.blackStatusBar(this)
        s = SharePref(this)

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

    private fun bottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.layout_list_task, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()

        val btnSignOut = view.findViewById<LinearLayout>(R.id.btn_signOut)
        val btnCreate = view.findViewById<LinearLayout>(R.id.btn_createTask)
        val rvTask = view.findViewById<RecyclerView>(R.id.rv_task)

        btnSignOut.setOnClickListener {
            s.clearAll()
            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
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
}
