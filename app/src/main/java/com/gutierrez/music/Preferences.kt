package com.gutierrez.music
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
private val Context.store by preferencesDataStore("zara_settings")
data class Settings(val enabled:Boolean=false,val interval:Int=15,val folder:String="",val mode:String="DUCK",val format:String="AUTO")
class Preferences(private val context:Context) {
 private val enabled=booleanPreferencesKey("enabled"); private val interval=intPreferencesKey("interval")
 private val folder=stringPreferencesKey("folder"); private val mode=stringPreferencesKey("mode");private val format=stringPreferencesKey("format")
 val values=context.store.data.map { Settings(it[enabled]?:false,it[interval]?:15,it[folder]?:"",it[mode]?:"DUCK",it[format]?:"AUTO") }
 suspend fun save(s:Settings) { context.store.edit { it[enabled]=s.enabled;it[interval]=s.interval;it[folder]=s.folder;it[mode]=s.mode;it[format]=s.format } }
}
