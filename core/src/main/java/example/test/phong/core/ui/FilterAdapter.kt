package example.test.phong.core.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import example.test.phong.core.R
import example.test.phong.core.data.Source
import example.test.phong.core.data.prefs.SourceManager
import example.test.phong.core.ui.recyclerview.FilterSwipeDismissListener
import example.test.phong.core.util.AnimUtils
import example.test.phong.core.util.ColorUtils
import example.test.phong.core.util.ViewUtils
import javax.inject.Inject

class FilterAdapter @Inject constructor(private var context: Context, sourceManager: SourceManager) : RecyclerView
.Adapter<FilterViewHolder>(), FilterSwipeDismissListener {
    companion object {
        private val FILTER_ICON_ENABLED_ALPHA = 179 // 70%
        private val FILTER_ICON_DISABLED_ALPHA = 51 // 20%
    }

    val filters: List<Source>

    init {
        context = context.applicationContext
        filters = sourceManager.getSources()
    }

    private val callbacks: MutableList<FiltersChangedCallbacks> by lazy {
        ArrayList<FiltersChangedCallbacks>()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)).apply {
            itemView.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
    }

    class FilterAnimator : DefaultItemAnimator() {
        companion object {
            val FILTER_ENABLED = 1
            val FILTER_DISABLED = 2
            val HIGHLIGHT = 3
        }

        override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun obtainHolderInfo(): ItemHolderInfo {
            return FilterHolderInfo()
        }

        override fun recordPreLayoutInformation(state: RecyclerView.State, viewHolder: RecyclerView.ViewHolder, changeFlags: Int, payloads: MutableList<Any>): ItemHolderInfo {
            val info = super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads) as FilterHolderInfo
            if (payloads.isNotEmpty()) {
                info.doEnable = payloads.contains(FILTER_ENABLED)
                info.doDisable = payloads.contains(FILTER_DISABLED)
                info.doHighLight = payloads.contains(HIGHLIGHT)
            }
            return info
        }

        override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder, preInfo: ItemHolderInfo, postInfo: ItemHolderInfo): Boolean {
            if (newHolder is FilterViewHolder && preInfo is FilterHolderInfo) {
                if (preInfo.doEnable || preInfo.doDisable) {
                    ObjectAnimator.ofInt(newHolder.filterIcon, ViewUtils.IMAGE_ALPHA, if (preInfo.doEnable)
                        FILTER_ICON_ENABLED_ALPHA
                    else
                        FILTER_ICON_DISABLED_ALPHA).apply {
                        duration = 300L
                        interpolator = AnimUtils.getFastOutSlowInInterpolator(newHolder.itemView.context)
                        addListener {
                            doOnStart {
                                dispatchChangeStarting(newHolder, false)
                                newHolder.itemView.setHasTransientState(true)
                            }
                            doOnEnd {
                                newHolder.itemView.setHasTransientState(true)
                                dispatchChangeFinished(newHolder, false)
                            }
                        }
                        start()
                    }
                } else if (preInfo.doHighLight) {
                    val highlightColor = ContextCompat.getColor(newHolder.itemView.context, R.color.accent)
                    val fadeFromTo = ColorUtils.modifyAlpha(highlightColor, 0)
                    ObjectAnimator.ofArgb(newHolder.itemView, ViewUtils.BACKGROUND_COLOR, fadeFromTo, highlightColor, fadeFromTo)
                            .apply {
                                duration = 1000L
                                interpolator = LinearInterpolator()
                                addListener {
                                    doOnStart {
                                        dispatchChangeStarting(newHolder, false)
                                        newHolder.itemView.setHasTransientState(true)
                                    }
                                    doOnEnd {
                                        newHolder.itemView.background = null
                                        newHolder.itemView.setHasTransientState(false)
                                        dispatchChangeFinished(newHolder, false)
                                    }
                                }
                                start()
                            }
                }
            }
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        }
    }

    override fun onItemDissmiss(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun registerFilterChangedCallback(callback: FiltersChangedCallbacks) {
        callbacks.add(callback)
    }

    fun getEnabledSourcesCount(): Int {
        var count = 0
        for (source in filters) {
            if (source.active)
                count++
        }
        return count
    }
}

interface FiltersChangedCallbacks {
    fun onFilterChanged(changedFilter: Source)
    fun onFilterRemoved(removed: Source)
}

class FilterHolderInfo : RecyclerView.ItemAnimator.ItemHolderInfo() {
    var doEnable: Boolean = false
    var doDisable: Boolean = false
    var doHighLight: Boolean = false
}

class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val filterName = itemView.findViewById<TextView>(R.id.filter_name)
    val filterIcon = itemView.findViewById<ImageView>(R.id.filter_icon)
    var isSwipeable: Boolean = false
}