package com.example.verynicephotoeditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ButtonAdapter(private val buttonList: List<ButtonModel>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_txt, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val currentItem = buttonList[position]
        holder.button.text = currentItem.buttonText

        holder.button.setOnClickListener {
            val newFragment = when (currentItem.buttonText) {
                "Rotate" -> RotateFragment()
                "Filter" -> FilterFragment()
                "Size" -> SizeFragment()
                "Draw" -> DrawFragment()
                "Face" -> FaceFragment()
                //"Cube" -> CubeFragment()
                //"Masking" -> MaskingFragment()
                else -> null
            }

            newFragment?.let {
                fragmentManager.beginTransaction()
                    .replace(R.id.framelayout, it)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun getItemCount() = buttonList.size
}