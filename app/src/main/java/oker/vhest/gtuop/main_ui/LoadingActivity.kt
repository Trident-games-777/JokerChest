package oker.vhest.gtuop.main_ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import oker.vhest.gtuop.R
import oker.vhest.gtuop.game_ui.FirstGameScreenActivity
import oker.vhest.gtuop.repos.JokerRepositoryImpl
import oker.vhest.gtuop.view_models.JokerViewModel
import oker.vhest.gtuop.view_models.JokerViewModelFactory
import java.io.File

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        if (checkADB() == "1" || checkRoots()) {
            startGame()
        } else {
            val repository = JokerRepositoryImpl(application)
            val factory = JokerViewModelFactory(application, repository)
            val viewModel = ViewModelProvider(this, factory)[JokerViewModel::class.java]

            viewModel.isFirstLaunch.observe(this) { isFirstLaunch ->
                if (isFirstLaunch) {
                    viewModel.link.observe(this) { link ->
                        viewModel.saveLink(link)
                        startWebView(link)
                    }
                } else {
                    startWebView(viewModel.getLink())
                }
            }
        }
    }

    private fun startGame() {
        val intent = Intent(this, FirstGameScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startWebView(link: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("link", link)
        startActivity(intent)
        finish()
    }

    private fun checkRoots(): Boolean {
        val dirs = arrayOf(
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
        )
        try {
            for (dir in dirs) {
                if (File(dir + "su").exists()) return true
            }
        } catch (t: Throwable) {
        }
        return false
    }

    private fun checkADB(): String {
        return Settings.Global.getString(this.contentResolver, Settings.Global.ADB_ENABLED)
            ?: "null"
    }
}