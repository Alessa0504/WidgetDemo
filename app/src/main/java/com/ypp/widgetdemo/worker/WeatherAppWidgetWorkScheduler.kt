package com.ypp.widgetdemo.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/12/23 20:52
 */
object WeatherAppWidgetWorkScheduler {

    /**
     * 一次性任务
     *
     * @param context
     */
    fun scheduleOneShotWork(context: Context) {
        //设置约束(触发条件)：要有网络
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString(
                WeatherAppWidgetWorker.WEATHER_APP_WIDGET_WORK_TYPE,
                WeatherAppWidgetWorker.ONE_TIME_WORK
            ).build()

        //OneTimeWorkRequestBuilder:一次性执行的任务
        val workRequest = OneTimeWorkRequestBuilder<WeatherAppWidgetWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)   //参数传递
            .setBackoffCriteria(   //指数退避策略：当doWork返回Result.retry()未成功重试时，每隔多久重试
                BackoffPolicy.LINEAR,   //线性:倍数增长
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,  //OneTimeWorkRequest.MIN_BACKOFF_MILLIS 的值是 10 秒（10000 毫秒），它表示当 Work 失败后，第一次重试将在失败后的 10 秒后开始
                TimeUnit.MILLISECONDS
            ).build()

        //加入队列执行
        WorkManager.getInstance(context).enqueueUniqueWork(
            WeatherAppWidgetWorker.ONE_TIME_WORK,
            ExistingWorkPolicy.REPLACE,   //ExistingWorkPolicy.REPLACE 的含义是如果已存在具有相同标识符的任务，则取消/删除现有的任务，并使用新的任务替换它
            workRequest
        )
    }

    /**
     * 周期性任务
     *
     * @param context
     */
    fun schedulePeriodicWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString(
                WeatherAppWidgetWorker.WEATHER_APP_WIDGET_WORK_TYPE,
                WeatherAppWidgetWorker.PERIODIC_WORK
            ).build()

        //PeriodicWorkRequestBuilder：周期性任务，默认不少于15min执行一次周期性任务
        val workRequest = PeriodicWorkRequestBuilder<WeatherAppWidgetWorker>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS
        ).addTag(WeatherAppWidgetWorker.PERIODIC_WORK)
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeatherAppWidgetWorker.PERIODIC_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        /**
         * 如果不用@HiltWorker，@Assisted和@Inject HiltWorkerFactory，就需要手动创建WeatherAppWidgetWorker；
         * 现在内部已经处理了
         */
//        val appContext = applicationContext
//        val workerParams = WorkerParameters.Builder()
//            .setWorkerClassName(WeatherAppWidgetWorker::class.java.name)
//            .build()
//
//        val myWorker = WeatherAppWidgetWorker(appContext, workerParams, myDependency)  //直接new，而非通过工厂模式创建WeatherAppWidgetWorker
//
//        WorkManager.getInstance(context).enqueue(myWorker)
    }
}