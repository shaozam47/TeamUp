package com.example.teamup.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamup.R
import com.example.teamup.adapters.TaskListItemAdapter
import com.example.teamup.firebase.FireStoreClass
import com.example.teamup.models.Board
import com.example.teamup.models.Task
import com.example.teamup.util.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog("Please Wait")
        FireStoreClass().getBoardDetails(this@TaskListActivity, boardDocumentId)
    }

    fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setUpActionBar()

        val addTaskList = Task("Add List")
        board.taskList.add(addTaskList)

        rv_task_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_task_list.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(this, board.taskList)
        rv_task_list.adapter = adapter
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_task_list_activity)
        val actionBar = supportActionBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }

        toolbar_task_list_activity.setNavigationOnClickListener{onBackPressed()}
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog("Please Wait")
        FireStoreClass().getBoardDetails(this, mBoardDetails.documentID)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog("Please Wait")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        showProgressDialog("Please Wait")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        showProgressDialog("Please Wait")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }
}