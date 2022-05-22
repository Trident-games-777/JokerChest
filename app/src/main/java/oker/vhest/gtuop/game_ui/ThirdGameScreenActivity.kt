package oker.vhest.gtuop.game_ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import oker.vhest.gtuop.databinding.ActivityThirdGameScreenBinding

class ThirdGameScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityThirdGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val win = intent.getBooleanExtra("result", false)
        binding.tvResult.text = if (win) "You won!" else "You lose"
        binding.btGoNext.setOnClickListener {
            startActivity(Intent(this, SecondGameScreenActivity::class.java))
            finish()
        }
    }
}