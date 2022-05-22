package oker.vhest.gtuop.game_ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import oker.vhest.gtuop.databinding.ActivitySecondGameScreenBinding

class SecondGameScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondGameScreenBinding
    private var finishers = mutableListOf<ImageView>()
    private var guess: ImageView? = null
    private var win = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initGame()
    }

    private fun initGame() {
        finishers.clear()
        val starters = mutableListOf(
            binding.guess1,
            binding.guess2,
            binding.guess3,
            binding.guess4,
            binding.guess5
        )
        starters.forEach {
            it.setOnClickListener { clickedView ->
                guess = clickedView as ImageView
                starters.forEach { elem ->
                    elem.isClickable = false
                    if (elem != guess) elem.visibility = View.INVISIBLE
                }
                startGame()
            }
        }
    }

    private fun startGame() {
        animate(binding.image1)
        animate(binding.image2)
        animate(binding.image3)
        animate(binding.image4)
        animate(binding.image5)
    }

    private fun endGame() {
        if (guess!!.tag == finishers.first().tag) win = true
        val intent = Intent(this, ThirdGameScreenActivity::class.java)
        intent.putExtra("result", win)
        startActivity(intent)
        finish()
    }

    private fun animate(view: ImageView) {
        ObjectAnimator.ofFloat(view, "translationX", 1500f).apply {
            duration = (2000..5000).random().toLong()
            start()
            doOnEnd {
                finishers.add(view)
                if (finishers.size == 5) endGame()
            }
        }
    }
}