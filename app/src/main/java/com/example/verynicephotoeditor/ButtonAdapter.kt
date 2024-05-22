package com.example.verynicephotoeditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                "Scale" -> ScaleFragment()
                "Grayscale" -> GrayscaleFragment()
                "Contrast" -> ContrastFragment()
                "Pixelate" -> PixelateFragment()
                "Sepia" -> SepiaFragment()
                "Solarize" -> SolarizeFragment()
                "Dither" -> DitherFragment()
                "Edge" -> EdgeFragment()
                "Blur" -> BlurFragment()
                "Glass" -> GlassFragment()
                "Oil" -> OilFragment()
                "Emboss" -> EmbossFragment()
                "Wave" -> WaveFragment()
                "Mask" -> MaskFragment()
                "Encode1" -> Encode1Fragment()
                "Decode1" -> Decode1Fragment()
                "Encode2" -> Encode2Fragment()
                "Decode2" -> Decode2Fragment()
                "Encode3" -> Encode3Fragment()
                "Decode3" -> Decode3Fragment()
                else -> null
            }

            newFragment?.let {
                fragmentManager.beginTransaction()
                    .replace(R.id.frame, it)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun getItemCount() = buttonList.size
}