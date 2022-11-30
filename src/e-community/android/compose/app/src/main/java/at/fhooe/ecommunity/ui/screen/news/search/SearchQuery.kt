package at.fhooe.ecommunity.ui.screen.news.search

/**
 * search query with filters
 * @param query search query
 * @param userSearch filter for users
 * @param eCommSearch filter for eCommunities
 */
data class SearchQuery(val query: String?, var userSearch: Boolean, var eCommSearch: Boolean) {
}