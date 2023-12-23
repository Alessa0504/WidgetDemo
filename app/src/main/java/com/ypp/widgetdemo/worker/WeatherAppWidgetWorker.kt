package com.ypp.widgetdemo.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ypp.widgetdemo.provider.WeatherAppWidgetProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/12/23 15:40
 */
// 通过 @HiltWorker 注解标记的 Worker 类将由 HiltWorkerFactory 创建，并自动进行依赖项注入。
// 这意味着，以 @HiltWorker 注解标记的 Worker 类可以直接使用 @HiltAndroidApp 应用程序范围内的依赖项(即@Inject标记的HiltWorkerFactory)，无需手动处理依赖项的实例化和注入。
@HiltWorker
class WeatherAppWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,   //Context不支持注入，所以不能用@Inject，只能用@Assisted
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params) {  //详见云笔记App Widget->JetPack -WorkManager

    companion object {
        private val TAG = WeatherAppWidgetWorker::class.java.simpleName
        const val WEATHER_APP_WIDGET_WORK_TYPE = "weather_app_widget_work_type"
        const val PERIODIC_WORK = "news_app_widget_periodic_work"
        const val ONE_TIME_WORK = "news_app_widget_one_time_work"
    }

    private val appWidgetManager = AppWidgetManager.getInstance(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {  //withContext(Dispatchers.IO)代表下面代码在IO线程中运行
        Log.d(TAG, "doWork:")
        val appWidgetIds = appWidgetManager.getAppWidgetIds(WeatherAppWidgetProvider.getProvider(context))
        appWidgetIds?.forEach { appWidgetId ->
            WeatherAppWidgetProvider.updateWeatherAppWidget(context, appWidgetId, appWidgetManager)
        }
        return@withContext Result.success()
    }
}