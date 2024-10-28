package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sharemomentsapplication.databinding.HorizontalMomentItemBinding
import models.Moment
import utilities.ImageLoader
import java.time.format.DateTimeFormatter

class MomentAdapter (private val moments:List<Moment>) : RecyclerView.Adapter<MomentAdapter.MomentViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val binding = HorizontalMomentItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MomentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return moments.size
    }

    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        with (holder) {
            with(moments.get(position)) {
                binding.feedLBLName.text = volunteerName
                binding.momentLBLDate.text = creationDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toString()
                binding.momentLBLText.text = description
                ImageLoader.getInstance().load(momentPhoto, binding.momentIMGPhoto)
                binding.momentLBLLikes.text = likesCount.toString()

            }
        }
    }

    inner class MomentViewHolder(val binding : HorizontalMomentItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}