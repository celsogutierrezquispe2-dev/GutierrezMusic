package com.gutierrez.music
import android.content.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.time.LocalTime
class TimeAlarmReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent?){val result=goAsync();CoroutineScope(Dispatchers.IO).launch {try{
 val settings=Preferences(context).values.first();if(!settings.enabled)return@launch
 TimeScheduler(context).schedule(settings.interval)
 if(settings.folder.isBlank())return@launch
 val files=ZaraResolver(context).resolve(settings.folder,LocalTime.now(),settings.format)
 if(files.isNotEmpty()) PlaybackService.deliverToActive(files,settings.mode)
 }finally{result.finish()}}}
}
class AlarmReschedulerReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent?){val result=goAsync();CoroutineScope(Dispatchers.IO).launch {try{val s=Preferences(context).values.first();if(s.enabled)TimeScheduler(context).schedule(s.interval)}finally{result.finish()}}}
}
