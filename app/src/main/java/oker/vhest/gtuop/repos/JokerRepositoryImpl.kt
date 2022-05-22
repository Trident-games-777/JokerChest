package oker.vhest.gtuop.repos

import android.app.Application
import android.content.Context
import oker.vhest.gtuop.utils.LinkStatus

private const val SHARED_PREF_NAME = "shared_pref"
private const val LINK_STATUS_KEY = "link_status_key"
private const val LINK_KEY = "link_key"

class JokerRepositoryImpl(val app: Application) : JokerRepository {
    private val pref = app.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override fun saveLink(link: String) {
        with(pref.edit()) {
            putString(LINK_KEY, link)
            apply()
        }
    }

    override fun getLink(): String = pref.getString(LINK_KEY, "")!!

    override fun getLinkStatus(): String =
        pref.getString(LINK_STATUS_KEY, LinkStatus.NONE.toString())!!

    override fun setLinkStatus(status: String) {
        with(pref.edit()) {
            putString(LINK_STATUS_KEY, status)
            apply()
        }
    }
}