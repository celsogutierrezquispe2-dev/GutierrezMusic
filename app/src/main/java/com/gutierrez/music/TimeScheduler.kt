package com.gutierrez.music
import android.app.*
import android.content.*
import android.os.Build
import java.time.ZonedDateTime
class TimeScheduler(private val context:Context) {
 private val manager=context.getSystemService(AlarmManager::class.java)
 private fun pending()=PendingIntent.getBroadcast(context,9,Intent(context,TimeAlarmReceiver::class.java),PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
 fun schedule(interval:Int){
  val now=ZonedDateTime.now();val next=now.withSecond(0).withNano(0).plusMinutes((interval-now.minute%interval).toLong())
  val whenMillis=next.toInstant().toEpochMilli()
  if(Build.VERSION.SDK_INT>=31 && !manager.canScheduleExactAlarms()) manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMillis,pending())
  else manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMillis,pending())
 }
 fun cancel()=manager.cancel(pending())
}
