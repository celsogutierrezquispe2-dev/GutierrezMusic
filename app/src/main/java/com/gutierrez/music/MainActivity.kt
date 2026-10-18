package com.gutierrez.music
import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import java.time.LocalTime
import android.provider.MediaStore
import androidx.media3.common.Player
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent { MusicApp() }}}
@Composable fun MusicApp(){
 val context=LocalContext.current;val scope=rememberCoroutineScope();val prefs=remember{Preferences(context)}
 val settings by prefs.values.collectAsState(initial=Settings())
 var tab by remember { mutableStateOf(0) };var controller by remember { mutableStateOf<MediaController?>(null) }
 var songs by remember { mutableStateOf<List<Pair<String,String>>>(emptyList()) }
 val permissions=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ granted ->
  if(granted.values.any{it}){songs=readSongs(context)}
 }
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()){uri->if(uri!=null){try{context.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)}catch(_:Exception){};scope.launch{prefs.save(settings.copy(folder=uri.toString()))}}}
 DisposableEffect(Unit){
  val future=MediaController.Builder(context,SessionToken(context,android.content.ComponentName(context,PlaybackService::class.java))).buildAsync()
  future.addListener({try{controller=future.get()}catch(_:Exception){}},ContextCompat.getMainExecutor(context))
  onDispose{controller=null;MediaController.releaseFuture(future)}
 }
 val scheme=darkColorScheme(background=Color.Black,surface=Color(0xFF191919),primary=Color(0xFF98C8FF))
 MaterialTheme(colorScheme=scheme){Column(Modifier.fillMaxSize().background(Color.Black).padding(18.dp)){
 Spacer(Modifier.height(36.dp));Text("Gutierrez Music",style=MaterialTheme.typography.headlineLarge,color=Color.White)
 Spacer(Modifier.height(22.dp))
 ScrollableTabRow(selectedTabIndex=tab,edgePadding=0.dp,containerColor=Color.Black){listOf("Pistas","Álbumes","Artistas","Carpetas","Locución").forEachIndexed{i,label->Tab(selected=tab==i,onClick={tab=i},text={Text(label)})}}
 Spacer(Modifier.height(16.dp))
 if(tab==4){Column(Modifier.weight(1f).verticalScroll(rememberScrollState())){
 Row(verticalAlignment=Alignment.CenterVertically){Text("Anunciar la hora",Modifier.weight(1f));Switch(settings.enabled,{v->scope.launch{prefs.save(settings.copy(enabled=v));if(v)TimeScheduler(context).schedule(settings.interval)else TimeScheduler(context).cancel()}})}
 Text("Frecuencia",style=MaterialTheme.typography.titleMedium)
 listOf(5,10,15).forEach{n->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(settings.interval==n,{scope.launch{prefs.save(settings.copy(interval=n));if(settings.enabled)TimeScheduler(context).schedule(n)}});Text("Cada $n minutos")}}
 Text("Audio",style=MaterialTheme.typography.titleMedium)
 listOf("DUCK" to "Atenuar música al 20 %","PAUSE" to "Pausar y reanudar").forEach{(value,label)->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(settings.mode==value,{scope.launch{prefs.save(settings.copy(mode=value))}});Text(label)}}
 Text("Formato ZaraRadio",style=MaterialTheme.typography.titleMedium)
 listOf("AUTO","HH_MM","SEPARATE","MINUTES").forEach{fmt->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(settings.format==fmt,{scope.launch{prefs.save(settings.copy(format=fmt))}});Text(fmt)}}
 Spacer(Modifier.height(12.dp));Button(onClick={picker.launch(null)},modifier=Modifier.fillMaxWidth()){Text("Seleccionar carpeta ZaraRadio")}
 Text(if(settings.folder.isBlank())"No hay carpeta seleccionada" else "Carpeta seleccionada",color=Color.LightGray)
 OutlinedButton(onClick={val files=if(settings.folder.isNotBlank())ZaraResolver(context).resolve(settings.folder,LocalTime.now(),settings.format)else emptyList();PlaybackService.deliverToActive(files,settings.mode)},enabled=settings.folder.isNotBlank(),modifier=Modifier.fillMaxWidth()){Text("Probar locución")}
 if(Build.VERSION.SDK_INT>=31){OutlinedButton(onClick={context.startActivity(Intent(AndroidSettings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))},modifier=Modifier.fillMaxWidth()){Text("Permitir alarmas exactas")}}
 }}else{Column(Modifier.weight(1f)){
 Button(onClick={val required=if(Build.VERSION.SDK_INT>=33)arrayOf(Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.POST_NOTIFICATIONS)else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);permissions.launch(required)},modifier=Modifier.fillMaxWidth()){Text("Permitir acceso a música")}
 LaunchedEffect(tab){songs=readSongs(context)}
 val shown=when(tab){0->songs;1->songs.distinctBy{it.first.substringBefore(" - ")};2->songs.distinctBy{it.first.substringAfter(" - ","")};else->songs}
 LazyColumn{items(shown){song->Column(Modifier.fillMaxWidth().clickable{controller?.apply{setMediaItem(MediaItem.fromUri(song.second));prepare();play()}}.padding(12.dp)){Text(song.first,color=Color.White);Text("Tocar para reproducir",color=Color.Gray,style=MaterialTheme.typography.bodySmall)};HorizontalDivider(color=Color.DarkGray)}}
 }}
 Surface(color=Color(0xFF262626),shape=RoundedCornerShape(18.dp)){Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically){Text("♫  Reproductor",Modifier.weight(1f));TextButton(onClick={controller?.seekToPreviousMediaItem()}){Text("◀◀")};TextButton(onClick={if(controller?.isPlaying==true)controller?.pause() else controller?.play()}){Text("▶ / ❚❚")};TextButton(onClick={controller?.seekToNextMediaItem()}){Text("▶▶")}}}
 }}}
fun readSongs(context:android.content.Context):List<Pair<String,String>> = try{
 val result=mutableListOf<Pair<String,String>>()
 context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME),"${MediaStore.Audio.Media.IS_MUSIC} != 0",null,"${MediaStore.Audio.Media.DISPLAY_NAME} ASC")?.use{c->val id=c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);val name=c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);while(c.moveToNext()){val uri=android.content.ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,c.getLong(id));result+=c.getString(name) to uri.toString()}}
 result
}catch(_:Exception){emptyList()}
