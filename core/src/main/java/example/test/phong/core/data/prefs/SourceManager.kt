package example.test.phong.core.data.prefs

import android.content.Context
import android.content.SharedPreferences
import example.test.phong.core.R
import example.test.phong.core.data.DesignerNewsSearchSource
import example.test.phong.core.data.DesignerNewsSource
import example.test.phong.core.data.DribbbleSearchSource
import example.test.phong.core.data.Source
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class SourceManager @Inject constructor(private val context: Context, private val prefs: SharedPreferences){
    companion object {
        val SOURCE_DESIGNER_NEWS_POPULAR = "SOURCE_DESIGNER_NEWS_POPULAR"
        val SOURCE_PRODUCT_HUNT = "SOURCE_PRODUCT_HUNT"
        val SOURCES_PREF = "SOURCES_PREF"
        val KEY_SOURCES = "KEY_SOURCES"
    }

    fun getSources(): List<Source> {
        val sourceKeys = prefs.getStringSet(KEY_SOURCES, null)
        if (sourceKeys == null) {
            setupDefaultSources()
            return getDefaultSources()
        }
        val sources = ArrayList<Source>(sourceKeys.size)
        for (sourceKey in sourceKeys) {
            if (sourceKey.startsWith(DribbbleSearchSource.DRIBBBLE_QUERY_PREFIX)) {
                sources.add(DribbbleSearchSource(
                        query = sourceKey.replace(DribbbleSearchSource.DRIBBBLE_QUERY_PREFIX, ""),
                        active = prefs.getBoolean(sourceKey, false)))
            } else if (DesignerNewsV1SourceRemover.checkAndRemoveDesignerNewsRecentSource(sourceKey, prefs)) {
                continue
            } else if (sourceKey.startsWith(DesignerNewsSearchSource.DESIGNER_NEWS_QUERY_PREFIX)) {
                sources.add(DesignerNewsSearchSource(
                        sourceKey.replace(DesignerNewsSearchSource
                                                  .DESIGNER_NEWS_QUERY_PREFIX, ""),
                        prefs.getBoolean(sourceKey, false)))
            } else if (DribbbleV1SourceRemover.checkAndRemove(sourceKey, prefs)) {
                continue
            } else {
                val defaultSource = getSource(sourceKey, prefs.getBoolean(sourceKey, false))
                defaultSource?.let {
                    sources.add(it)
                }
            }
        }
        Collections.sort(sources, Source.SourceComparator())
        return sources
    }

    private fun getSource(key: String, active: Boolean): Source? {
        for (source in getDefaultSources()) {
            if (source.key == key) {
                source.active = active
                return source
            }
        }
        return null
    }

    private fun setupDefaultSources() {
        val editor = prefs.edit()
        val defaultSources = getDefaultSources()
        val keys = HashSet<String>(defaultSources.size)
        for (source in defaultSources) {
            keys.add(source.key)
            editor.putBoolean(source.key, source.active)
        }
        editor.putStringSet(KEY_SOURCES, keys)
        editor.apply()
    }

    private fun getDefaultSources(): ArrayList<Source> {
        val defaultSources = ArrayList<Source>(11)
        defaultSources.add(DesignerNewsSource(SOURCE_DESIGNER_NEWS_POPULAR, 100,
                                              context.getString(R.string.source_designer_news_popular), true))
        // 200 sort order range left for DN searches
        defaultSources.add(DribbbleSearchSource(context.getString(R.string.source_dribbble_search_material_design), true))
        // 400 sort order range left for dribbble searches
        defaultSources.add(Source(SOURCE_PRODUCT_HUNT, 500,
                                  context.getString(R.string.source_product_hunt),
                                  R.drawable.ic_product_hunt, false))
        return defaultSources
    }
}