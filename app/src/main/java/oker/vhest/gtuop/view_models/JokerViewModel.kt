package oker.vhest.gtuop.view_models

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.*
import oker.vhest.gtuop.repos.JokerRepository
import oker.vhest.gtuop.utils.Constants
import oker.vhest.gtuop.utils.LinkStatus
import timber.log.Timber
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class JokerViewModel(
    private val app: Application,
    private val repo: JokerRepository
) : AndroidViewModel(app) {

    val link: MutableLiveData<String> = MutableLiveData()
    val isFirstLaunch: MutableLiveData<Boolean> = MutableLiveData()

    init {
        viewModelScope.launch {
            val afLinkDeferred: Deferred<MutableMap<String, Any>?> = async { fetchAFLink() }
            val fbLinkDeferred: Deferred<String?> = async { fetchFBLink() }
            isFirstLaunch.postValue(afLinkDeferred.await()?.get("is_first_launch") as Boolean)
            sendOneSignalTag(afLinkDeferred.await(), fbLinkDeferred.await())
            withContext(Dispatchers.Default) {
                link.postValue(parseLink(fbLinkDeferred.await(), afLinkDeferred.await()))
            }
        }
    }

    private fun parseLink(fbLink: String?, afLink: MutableMap<String, Any>?): String {
        Timber.d("fbLink: $fbLink")
        Timber.d("afLink: $afLink")

        val googleAdvertisingId =
            AdvertisingIdClient.getAdvertisingIdInfo(getApplication()).id.toString()

        return Constants.BASE_LINK.toUri().buildUpon().apply {
            appendQueryParameter(Constants.SECURE_GET_PARAMETR, Constants.SECURE_KEY)
            appendQueryParameter(Constants.DEV_TMZ_KEY, TimeZone.getDefault().id)
            appendQueryParameter(Constants.GADID_KEY, googleAdvertisingId)
            appendQueryParameter(Constants.DEEPLINK_KEY, fbLink)
            appendQueryParameter(Constants.SOURCE_KEY, afLink?.get("media_source").toString())
            appendQueryParameter(
                Constants.AF_ID_KEY,
                AppsFlyerLib.getInstance().getAppsFlyerUID(getApplication())
            )
            appendQueryParameter(Constants.ADSET_ID_KEY, afLink?.get("adset_id").toString())
            appendQueryParameter(Constants.CAMPAIGN_ID_KEY, afLink?.get("campaign_id").toString())
            appendQueryParameter(Constants.APP_CAMPAIGN_KEY, afLink?.get("campaign").toString())
            appendQueryParameter(Constants.ADSET_KEY, afLink?.get("adset").toString())
            appendQueryParameter(Constants.ADGROUP_KEY, afLink?.get("adgroup").toString())
            appendQueryParameter(Constants.ORIG_COST_KEY, afLink?.get("orig_cost").toString())
            appendQueryParameter(Constants.AF_SITEID_KEY, afLink?.get("af_siteid").toString())
        }.toString()
    }

    private suspend fun fetchFBLink(): String? {
        return suspendCoroutine { continuation ->
            AppLinkData.fetchDeferredAppLinkData(getApplication()) {
                continuation.resume(it?.targetUri.toString())
            }
        }
    }

    private suspend fun fetchAFLink(): MutableMap<String, Any>? {
        return suspendCoroutine { continuation ->
            AppsFlyerLib.getInstance().init(
                Constants.APPS_DEV_KEY,
                object : AppsFlyerConversionListener {
                    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                        continuation.resume(p0)
                    }

                    override fun onConversionDataFail(p0: String?) {}
                    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
                    override fun onAttributionFailure(p0: String?) {}
                },
                app.applicationContext
            )
            AppsFlyerLib.getInstance().start(app.applicationContext)
        }
    }

    fun saveLink(link: String) {
        when (repo.getLinkStatus()) {
            LinkStatus.NONE.toString() -> {
                repo.setLinkStatus(LinkStatus.FIRST.toString())
                repo.saveLink(link)
            }
            LinkStatus.FIRST.toString() -> {
                repo.setLinkStatus(LinkStatus.LAST.toString())
                repo.saveLink(link)
            }
            LinkStatus.LAST.toString() -> {}
        }
    }

    fun getLink(): String = repo.getLink()

    private fun sendOneSignalTag(afLink: MutableMap<String, Any>?, fbLink: String?) {
        val campaign = afLink?.get("campaign").toString()

        if (campaign == "null" && (fbLink == "null" || fbLink == null)) {
            OneSignal.sendTag("key2", "organic")
        } else if (fbLink != "null") {
            OneSignal.sendTag("key2", fbLink?.replace("myapp://", "")?.substringBefore("/"))
        } else if (campaign != "null") {
            OneSignal.sendTag("key2", campaign.substringBefore("_"))
        }
    }
}