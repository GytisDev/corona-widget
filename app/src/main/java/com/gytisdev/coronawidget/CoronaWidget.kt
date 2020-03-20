package com.gytisdev.coronawidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Implementation of App Widget functionality.
 */
class CoronaWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        const val REFRESH_BUTTON_CLICK = "RefreshButtonClicked";
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if(REFRESH_BUTTON_CLICK == intent?.action){
            Toast.makeText(context, "Updating, please wait...", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = APIClient.getClient().create(ApiCall::class.java).getData()
                    withContext(Dispatchers.Main) {
                        updateWidget(context!!, response)
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Error while downloading data", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("Error", e.message)
                }
            }
        }
    }

    private fun updateWidget(context: Context, data: List<ApiResponseData>) {
        val views = RemoteViews(context.packageName, R.layout.corona_widget)

        val statsData = getStatsData(data)

        views.setTextViewText(R.id.tv_confirmed, "Confirmed: " + statsData.confirmed.toString())
        views.setTextViewText(R.id.tv_recovered, "Recovered: " + statsData.recovered.toString())
        views.setTextViewText(R.id.tv_deaths, "Deaths: " + statsData.deaths.toString())

        AppWidgetManager.getInstance(context).updateAppWidget(
            ComponentName(context, CoronaWidget::class.java), views
        )

        Toast.makeText(context, "Succesfully updated", Toast.LENGTH_SHORT).show()
    }

    fun getPendingSelfIntent(
        context: Context?,
        action: String?
    ): PendingIntent? {
        val intent = Intent(context, this::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.corona_widget)

        val intent = Intent(context, CoronaWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        views.setOnClickPendingIntent(R.id.btn_refresh, getPendingSelfIntent(context, CoronaWidget.REFRESH_BUTTON_CLICK))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getStatsData(data: List<ApiResponseData>) : StatsData {
        var r = 0
        var c = 0
        var d = 0

        data.forEach { data ->
            r += data.recovered
            c += data.confirmed
            d += data.deaths
        }

        return StatsData(r, c, d)
    }

    private data class StatsData(val recovered : Int, val confirmed : Int, val deaths : Int)

}

