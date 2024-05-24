package com.example.verynicephotoeditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.verynicephotoeditor.fragments.BlurFragment
import com.example.verynicephotoeditor.fragments.ContrastFragment
import com.example.verynicephotoeditor.fragments.Decode1Fragment
import com.example.verynicephotoeditor.fragments.Decode2Fragment
import com.example.verynicephotoeditor.fragments.Decode3Fragment
import com.example.verynicephotoeditor.fragments.DitherFragment
import com.example.verynicephotoeditor.fragments.EdgeFragment
import com.example.verynicephotoeditor.fragments.EmbossFragment
import com.example.verynicephotoeditor.fragments.Encode1Fragment
import com.example.verynicephotoeditor.fragments.Encode2Fragment
import com.example.verynicephotoeditor.fragments.Encode3Fragment
import com.example.verynicephotoeditor.fragments.FaceRecognitionFragment
import com.example.verynicephotoeditor.fragments.GlassFragment
import com.example.verynicephotoeditor.fragments.GrayscaleFragment
import com.example.verynicephotoeditor.fragments.MaskFragment
import com.example.verynicephotoeditor.fragments.OilFragment
import com.example.verynicephotoeditor.fragments.PixelateFragment
import com.example.verynicephotoeditor.fragments.RotateFragment
import com.example.verynicephotoeditor.fragments.ScaleFragment
import com.example.verynicephotoeditor.fragments.SepiaFragment
import com.example.verynicephotoeditor.fragments.SolarizeFragment
import com.example.verynicephotoeditor.fragments.WaveFragment

class ButtonAdapter(
    private val buttonList: List<ButtonModel>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_txt, parent, false)
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
                "Face Recognition" -> FaceRecognitionFragment()
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