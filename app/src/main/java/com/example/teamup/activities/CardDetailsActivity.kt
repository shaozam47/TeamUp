package com.example.teamup.activities

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.teamup.R
import com.example.teamup.dialogs.LabelColorListDialog
import com.example.teamup.firebase.FireStoreClass
import com.example.teamup.models.Board
import com.example.teamup.models.Card
import com.example.teamup.models.Task
import com.example.teamup.util.Constants
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setUpActionBar()

        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()) {
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if(et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            }
            else{
                Toast.makeText(this, "Please Enter Card Name", Toast.LENGTH_SHORT).show()
            }
        }

        tv_select_label_color.setOnClickListener {
            labelColorListDialog()
        }
    }

    private fun colorList(): ArrayList<String> {
        val colorList: ArrayList<String> = ArrayList()
        colorList.add("#1C3144")
        colorList.add("#596F62")
        colorList.add("#7EA16B")
        colorList.add("#C3D898")
        colorList.add("#70161E")

        return colorList
    }

    private fun setColor() {
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun updateCardDetails() {
        val card = Card(
            et_name_card_details.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog("Please Wait")
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_card_details_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        toolbar.setNavigationOnClickListener{ onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getIntentData() {
        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }

        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_card -> {
                deleteCard()
                hideProgressDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteCard() {
        var cardList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardList
        showProgressDialog("Please Wait")
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun labelColorListDialog() {
        val colorList: ArrayList<String> = colorList()
        val listDialog = object : LabelColorListDialog(
            this,
            colorList,
            "Select Label Color", mSelectedColor) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }

        listDialog.show()
    }
}