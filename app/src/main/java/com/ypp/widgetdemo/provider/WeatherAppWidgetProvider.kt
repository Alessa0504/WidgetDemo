package com.ypp.widgetdemo.provider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import android.widget.RemoteViews
import com.ypp.widgetdemo.MainActivity
import com.ypp.widgetdemo.R

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/12/22 15:07
 */
class WeatherAppWidgetProvider: AppWidgetProvider() {

    /**
     * 当 app_widget_info.xml 中定义的 AppWidgetProviderInfo 的 updatePeriodMillis 设置的时间间隔到期，需要更新小部件时调用此方法。
     * 当用户将小部件添加到主屏幕或启动器时也会调用此方法。
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds 包含要更新的所有小部件的标识符（ID）。每个小部件都有一个唯一的标识符，以便对其进行操作
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds.forEach { appWidgetId ->
            updateWeatherAppWidget(context, appWidgetId, appWidgetManager)
        }
    }

    /**
     * 小组件尺寸或配置发生变化会触发该方法，第一次添加小组件时不会被调用
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        Log.d(TAG, "onAppWidgetOptionsChanged:")
        updateWeatherAppWidget(context, appWidgetId, appWidgetManager)
    }

    /**
     * 调用时机：1.用户手动从桌面移除小部件 2.小部件所在的主机应用被卸载
     *
     * @param context
     * @param appWidgetIds
     */
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        Log.d(TAG, "onDeleted:")
    }

    /**
     * 调用时机：第一次添加小部件时
     *
     * @param context
     */
    override fun onEnabled(context: Context?) {
        Log.d(TAG, "onEnabled:")
        //todo worker update
    }

    /**
     * 调用时机：最后一个小部件实例被从桌面移除
     *
     * @param context
     */
    override fun onDisabled(context: Context?) {
        Log.d(TAG, "onDisabled:")
        //todo worker canceled
    }

    /**
     * 调用时机：接收到小部件相关的广播事件，可以是用户与小部件进行交互（例如点击小部件）或发送特定的广播
     * 无需实现，内部会自行调度
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive:")
    }

    companion object {
        private val TAG = WeatherAppWidgetProvider::class.java.simpleName

        fun updateWeatherAppWidget(
            context: Context,
            appWidgetId: Int,
            appWidgetManager: AppWidgetManager
        ) {
            // 该方法返回一个 Bundle 对象，其中包含了当前小部件的选项设置。选项设置可能包括小部件的大小、最小宽度和高度等信息
            val appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val minWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            val minHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            createRemoteViews(context, SizeF(minWidth.toFloat(), minHeight.toFloat()))?.let {
                appWidgetManager.updateAppWidget(appWidgetId, it)  //小组件更新
            }
        }

        /**
         *
         *
         * @param context
         * @param size  一般情况下，1dp 等于屏幕密度的一个像素。例如，如果屏幕密度为 2.0，则 1dp 等于 2px
         * @return
         */
        private fun createRemoteViews(context: Context, size: SizeF): RemoteViews? {
            Log.d(TAG, "createRemoteViews: size: $size")
            if (size.width in 150f..269f && size.height in 75f..269f) {
                return createSmallWeatherWidget(context)
            } else if (size.width in 270f..329f && size.height in 75f..150f) {
                return createMediumWeatherWidget(context)
            } else if (size.width in 270f..570f && size.height in 75f..450f) {
                return createLargeWeatherWidget(context)
            }
            return null
        }

        private fun createSmallWeatherWidget(context: Context): RemoteViews {
            return RemoteViews(
                context.packageName,
                R.layout.layout_app_widget_weather_small
            ).apply {
                setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
            }
        }

        private fun createMediumWeatherWidget(context: Context): RemoteViews {
            return RemoteViews(
                context.packageName,
                R.layout.layout_app_widget_weather_medium
            ).apply {
                setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
            }
        }

        private fun createLargeWeatherWidget(context: Context): RemoteViews {
            return RemoteViews(
                context.packageName,
                R.layout.layout_app_widget_weather_large
            ).apply {
                setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
            }
        }

        private fun getWidgetPendingIntent(context: Context): PendingIntent {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                flags
            )
        }
    }
}