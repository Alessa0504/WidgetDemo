package com.ypp.widgetdemo.app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/12/23 15:16
 */
//当使用 @HiltAndroidApp 注解标记一个类时，它会生成一个 Hilt 的组件（Component）并将其绑定到应用程序的生命周期。
@HiltAndroidApp
class WidgetsApp: Application(), Configuration.Provider {

    companion object {
        val TAG: String = WidgetsApp::class.java.simpleName
    }

    //HiltWorkerFactory 是一个特殊的工厂类，负责在 WorkManager 调度执行 Worker 时，根据 Worker 的类型来创建具有正确依赖注入的 Worker 实例。
    // 它是由 Hilt 在 YourApplication_HiltComponents 组件类中自动生成的。
    //加了@Inject后，无需手动new HiltWorkerFactory，内部自动联系@HiltWorker+@AssistedInject创建WeatherAppWidgetWorker。
    //参考 工厂模式创建实例 HiltWorkerFactory.createWorker -> WeatherAppWidgetWorker_AssistedFactory_Impl：
//    public WeatherAppWidgetWorker create(Context context, WorkerParameters parameters) {
//        return delegateFactory.get(context, parameters);
//    }
    // -> new WeatherAppWidgetWorker(context, params)
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * 通过重写 getWorkManagerConfiguration() 方法，你可以在应用程序启动时对 WorkManager 进行全局配置
     *
     * @return
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged:")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "onTerminate:")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate:")
//        WorkManager.initialize(applicationContext, workManagerConfiguration);
    }
}