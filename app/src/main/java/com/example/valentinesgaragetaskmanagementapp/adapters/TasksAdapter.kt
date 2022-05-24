package com.example.valentinesgaragetaskmanagementapp.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.databinding.TaskItemContainerBinding
import com.example.valentinesgaragetaskmanagementapp.listeners.TaskListener
import com.example.valentinesgaragetaskmanagementapp.models.Task
import com.makeramen.roundedimageview.RoundedImageView

class TasksAdapter(private val taskList: ArrayList<Task>): RecyclerView.Adapter<TasksAdapter.TaskViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_item_container, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TasksAdapter.TaskViewHolder, position: Int) {
        val task: Task = taskList[position]
        holder.taskName.text = task.task
        holder.receiverEmail.text = task.receiver
        holder.receiverImg.setImageBitmap(getTaskOwnerImage(task.receiverImg))
    }
    override fun getItemCount(): Int {
       return taskList.size
    }
    public class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val taskName: TextView = itemView.findViewById(R.id.textTaskName)
        val receiverEmail: TextView = itemView.findViewById(R.id.textTaskReciever)
        val receiverImg: RoundedImageView = itemView.findViewById(R.id.receiverProfileImg)
    }
    companion object {
        fun getTaskOwnerImage(encodedImage: String): Bitmap {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }
}