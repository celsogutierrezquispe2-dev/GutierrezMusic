package com.gutierrez.music
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.time.LocalTime
class ZaraResolver(private val context:Context) {
 fun resolve(tree:String, time:LocalTime, format:String):List<Uri> {
  val root=DocumentFile.fromTreeUri(context,Uri.parse(tree))?:return emptyList()
  val hh="%02d".format(time.hour);val mm="%02d".format(time.minute)
  fun file(dir:DocumentFile?,name:String):Uri?=dir?.findFile(name)?.takeIf { it.isFile }?.uri
  fun combined():List<Uri> = listOf("${hh}_${mm}.mp3","${hh}-${mm}.mp3","${hh}${mm}.mp3").firstNotNullOfOrNull { file(root,it) }?.let { listOf(it) }?: emptyList()
  fun separated():List<Uri> {val h=root.findFile("horas")?:root.findFile("hours");val m=root.findFile("minutos")?:root.findFile("minutes");val a=file(h,"$hh.mp3");val b=file(m,"$mm.mp3");return if(a!=null&&b!=null) listOf(a,b) else emptyList()}
  fun hourFolder():List<Uri> = file(root.findFile(hh),"$mm.mp3")?.let { listOf(it) }?: emptyList()
  fun minuteOnly():List<Uri> = file(root,"$mm.mp3")?.let { listOf(it) }?: emptyList()
  return when(format){"HH_MM"->combined();"SEPARATE"->separated();"MINUTES"->minuteOnly();else->combined().ifEmpty { separated() }.ifEmpty { hourFolder() }.ifEmpty { minuteOnly() }}
 }
}
