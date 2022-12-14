package com.example.teamup.adapters

import android.content.Context
import android.content.res.Resources
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamup.R
import com.example.teamup.activities.TaskListActivity
import com.example.teamup.models.Task
import kotlinx.android.synthetic.main.item_task.view.*

open class TaskListItemAdapter
    (private val context: Context, private var list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            if(position==list.size-1) {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_list.visibility = View.GONE
            }
            else {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_list.visibility = View.VISIBLE
            }

            holder.itemView.tv_task_list_title.text = model.title
            holder.itemView.tv_add_task_list.setOnClickListener{
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_list_name.setOnClickListener{
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE
            }

            holder.itemView.ib_done_list_name.setOnClickListener{
                val listName = holder.itemView.et_task_list_name.text.toString()
                if(listName.isNotEmpty()) {
                    if(context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                }
                else {
                    Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_LONG).show()
                }
            }

        }

        holder.itemView.ib_edit_list_name.setOnClickListener {
            holder.itemView.et_edit_task_list_name.setText(model.title)
            holder.itemView.ll_title_view.visibility = View.GONE
            holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
        }

        holder.itemView.ib_close_editable_view.setOnClickListener{
            holder.itemView.ll_title_view.visibility = View.VISIBLE
            holder.itemView.cv_edit_task_list_name.visibility = View.GONE
        }

        holder.itemView.ib_done_edit_list_name.setOnClickListener {
            val listName = holder.itemView.et_edit_task_list_name.text.toString()
            if(listName.isNotEmpty()) {
                if(context is TaskListActivity) {
                    context.updateTaskList(position, listName, model)
                }
            }
            else {
                Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_LONG).show()
            }
        }

        holder.itemView.ib_delete_list.setOnClickListener {
            if(context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        holder.itemView.tv_add_card.setOnClickListener {
            holder.itemView.tv_add_card.visibility = View.GONE
            holder.itemView.cv_add_card.visibility = View.VISIBLE
        }

        holder.itemView.ib_close_card_name.setOnClickListener {
            holder.itemView.tv_add_card.visibility = View.VISIBLE
            holder.itemView.cv_add_card.visibility = View.GONE
        }

        holder.itemView.ib_done_card_name.setOnClickListener{
            val cardName = holder.itemView.et_card_name.text.toString()
            if(cardName.isNotEmpty()) {
                if(context is TaskListActivity) {
                    context.addCardToTaskList(position, cardName)
                }
            }
            else {
                Toast.makeText(context, "Please Enter Card Name", Toast.LENGTH_LONG).show()
            }
        }

        holder.itemView.rv_card_list.layoutManager = LinearLayoutManager(context)
        holder.itemView.rv_card_list.setHasFixedSize(true)

        val adapter = CardListItemAdapter(context, model.cards)
        holder.itemView.rv_card_list.adapter = adapter

        adapter.setOnClickListener(
            object: CardListItemAdapter.OnClickListener{
                override fun onClick(cardPosition: Int) {
                    if(context is TaskListActivity) {
                        context.cardDetails(position, cardPosition)
                    }
                }
            }
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp(): Int = (this/Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this*Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}