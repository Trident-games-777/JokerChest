package oker.vhest.gtuop.game_ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import oker.vhest.gtuop.databinding.ActivityFirstGameScreenBinding

class FirstGameScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFirstGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startGameButton.setOnClickListener {
            val intent = Intent(this, SecondGameScreenActivity::class.java)
            startActivity(intent)
        }
    }
}