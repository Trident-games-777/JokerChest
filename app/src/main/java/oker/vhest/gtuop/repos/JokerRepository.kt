package oker.vhest.gtuop.repos

interface JokerRepository {
    fun saveLink(link: String)
    fun getLink(): String
    fun getLinkStatus():String
    fun setLinkStatus(status: String)
}