package com.gutierrez.music
import android.net.Uri
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import kotlinx.coroutines.*
class PlaybackService:MediaSessionService(){
 companion object {
  @Volatile private var active:PlaybackService?=null
  fun deliverToActive(files:List<Uri>,mode:String){active?.announce(files,mode)}
 }
 private lateinit var music:ExoPlayer;private lateinit var voice:ExoPlayer;private lateinit var session:MediaSession
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Main.immediate)
 private var announcing=false
 override fun onCreate(){super.onCreate();music=ExoPlayer.Builder(this).build();voice=ExoPlayer.Builder(this).build();session=MediaSession.Builder(this,music).build();active=this}
 override fun onGetSession(controllerInfo:MediaSession.ControllerInfo):MediaSession=session
 private fun announce(files:List<Uri>,mode:String){scope.launch {if(announcing)return@launch;announcing=true
 val oldVolume=music.volume;val wasPlaying=music.isPlaying
 try {if(mode=="PAUSE"){if(wasPlaying)music.pause()}else if(wasPlaying)fade(oldVolume,oldVolume*0.2f)
 voice.setMediaItems(files.map { MediaItem.fromUri(it) });voice.prepare();voice.play()
 withTimeoutOrNull(120000L){while(voice.playbackState!=Player.STATE_ENDED && voice.playerError==null){delay(100)}}
 }finally{voice.stop();voice.clearMediaItems();if(mode=="PAUSE"){if(wasPlaying)music.play()}else fade(music.volume,oldVolume);announcing=false}}
 }
 private suspend fun fade(from:Float,to:Float){repeat(10){val f=(it+1)/10f;music.volume=from+(to-from)*f;delay(35)}}
 override fun onDestroy(){active=null;scope.cancel();session.release();voice.release();music.release();super.onDestroy()}
}
