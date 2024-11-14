package com.example.sharemomentsapplication.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sharemomentsapplication.databinding.HorizontalMomentItemBinding
import interfaces.MenteeCallback
import interfaces.MomentCallback
import models.Moment
import utilities.Constants
import utilities.ImageLoader
import java.time.format.DateTimeFormatter
import java.util.function.Consumer
import kotlin.math.max

class MomentAdapter (var moments:List<Moment>) : RecyclerView.Adapter<MomentAdapter.MomentViewHolder>() {


    var momentCallback: MomentCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val binding = HorizontalMomentItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MomentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return moments.size
    }
    fun getItem(position: Int) = moments.get(position)

    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        with (holder) {
            with(moments.get(position)) {
                binding.momentLBLName.text = volunteerName
                binding.momentLBLDate.text = creationDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toString()
                binding.momentLBLText.text = description
                val momentPhotoURLTEST = "https://cdn.pixabay.com/photo/2023/10/14/09/20/mountains-8314422_1280.png"
                val momentPhotoReal = momentPhotoUrl
                ImageLoader.getInstance().load(momentPhotoReal, binding.momentIMGPhoto)
                binding.momentLBLLikes.text = likesCount.toString()

                binding.momentCVText.setOnClickListener{
                    val animatorSet = ArrayList<ObjectAnimator>()

                    if(isCollapsed) {
                        animatorSet
                            .add(
                                ObjectAnimator.ofInt(binding.momentLBLText,
                                                    "maxLines",
                                                     binding.momentLBLText.lineCount
                                ).setDuration(
                                    (max((
                                            binding.momentLBLText.lineCount - Constants.Animation.DESCRIPTION_MIN_LINES).toDouble(),
                                        0.0)*50L
                                    ).toLong()

                                )

                            )

                    } else {
                        animatorSet
                                .add(
                                    ObjectAnimator.ofInt(binding.momentLBLText,
                                        "maxLines",
                                        Constants.Animation.DESCRIPTION_MIN_LINES
                                    ).setDuration(
                                        (max((
                                                binding.momentLBLText.lineCount - Constants.Animation.DESCRIPTION_MIN_LINES).toDouble(),
                                            0.0)*50L
                                                ).toLong()

                                    )

                                )
                    }
                    toggleCollapse()
                    animatorSet.forEach(Consumer { obj : ObjectAnimator -> obj.start() })
                }



            }
        }
    }

    inner class MomentViewHolder(val binding : HorizontalMomentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.likeAnimationView.setOnClickListener {
                if(momentCallback != null) {
                    momentCallback!!.likeClickedCallback(
                        getItem(adapterPosition),
                        adapterPosition
                    )
                }
                playLikeAnimation()
                val likes = binding.momentLBLLikes.text.toString()
                var likesCount = likes.toInt()
                likesCount++
                binding.momentLBLLikes.text = likesCount.toString()
            }
            binding.likeAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    binding.likeAnimationView.progress = 0f
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }
            })
        }

        private fun playLikeAnimation() {
            binding.likeAnimationView.cancelAnimation()
            binding.likeAnimationView.progress = 0f

            // Start the animation
            binding.likeAnimationView.playAnimation()
        }

    }
}