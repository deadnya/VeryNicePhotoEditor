import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.verynicephotoeditor.DrawFragment
import com.example.verynicephotoeditor.FaceFragment
import com.example.verynicephotoeditor.FilterFragment
import com.example.verynicephotoeditor.R
import com.example.verynicephotoeditor.RotateFragment
import com.example.verynicephotoeditor.SizeFragment


class MyAdapter(private val icons: List<Int>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageButton: ImageButton = view.findViewById(R.id.imageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.imageButton.setImageResource(icons[position])
        holder.imageButton.setOnClickListener {
            when (icons[position]) {
                R.drawable.rotate_icon -> {
                    fragmentManager.beginTransaction().replace(R.id.framelayout, RotateFragment::class.java, null).commit()
                }
                R.drawable.filter_icon -> {
                    fragmentManager.beginTransaction().replace(R.id.framelayout, FilterFragment::class.java, null).commit()
                }
                R.drawable.size_icon -> {
                    fragmentManager.beginTransaction().replace(R.id.framelayout, SizeFragment::class.java, null).commit()
                }
                R.drawable.draw_icon -> {
                    fragmentManager.beginTransaction().replace(R.id.framelayout, DrawFragment::class.java, null).commit()
                }
                R.drawable.face_icon -> {
                    fragmentManager.beginTransaction().replace(R.id.framelayout, FaceFragment::class.java, null).commit()
                }

            }
        }
    }

    override fun getItemCount() = icons.size
}