package example.test.phong.plaidstudy.dagger

import com.bumptech.glide.util.ViewPreloadSizeProvider
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import example.test.phong.core.data.DataManager
import example.test.phong.core.data.api.model.Shot
import example.test.phong.core.data.pocket.PocketUtils
import example.test.phong.core.data.prefs.SourceManager
import example.test.phong.core.ui.FeedAdapter
import example.test.phong.core.ui.FilterAdapter
import example.test.phong.plaidstudy.MainActivity
import example.test.phong.plaidstudy.R

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun contributeMainActivity(): MainActivity
}

@Module
class MainActivityModule {
    @Provides
    fun provideFilterAdapter(activity: MainActivity, sourceManager: SourceManager): FilterAdapter {
        return FilterAdapter(activity, sourceManager.getSources())
    }

    @Provides
    fun provideViewPreloadSizeProvider(): ViewPreloadSizeProvider<Shot> {
        return ViewPreloadSizeProvider<Shot>()
    }

    @Provides
    fun provideFeedAdapter(activity: MainActivity, dataManager: DataManager, viewPreloadSizeProvider: ViewPreloadSizeProvider<Shot>): FeedAdapter {
        return FeedAdapter(activity, dataManager, activity.resources.getInteger(R.integer.num_columns),
                           PocketUtils.isPocketInstalled(activity), viewPreloadSizeProvider)
    }
}
