package oker.vhest.gtuop.view_models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import oker.vhest.gtuop.repos.JokerRepository

class JokerViewModelFactory(
    private val app: Application,
    private val repo: JokerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return JokerViewModel(app, repo) as T
    }
}