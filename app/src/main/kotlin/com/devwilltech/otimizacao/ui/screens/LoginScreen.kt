package com.devwilltech.otimizacao.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.MainViewModel
import com.devwilltech.otimizacao.ui.theme.NeonYellow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

@Composable
fun LoginScreen(viewModel: MainViewModel, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    
    var currentKey by remember { mutableStateOf(uiState.savedKey) }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val PROJECT = "principal-6bf6f"
    val API_KEY = "AIzaSyAmXzPrNaK_-Zr190oB8MuxA_sqI_ctetc"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopEnd) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (uiState.isHideStreamEnabled) "STREAM OCULTA" else "OCULTAR STREAM",
                    color = if (uiState.isHideStreamEnabled) NeonYellow else Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = uiState.isHideStreamEnabled,
                    onCheckedChange = { viewModel.toggleHideStream(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonYellow,
                        checkedTrackColor = NeonYellow.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NETFLIX",
                color = Color.Red,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF111111), RoundedCornerShape(12.dp))
                    .border(1.dp, if (error.isNotEmpty()) Color.Red else Color.DarkGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentKey.isEmpty()) "XXXX-XXXX" else formatKey(currentKey),
                    color = if (currentKey.isEmpty()) Color.DarkGray else Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val pasted = clip.getItemAt(0).text.toString().trim().uppercase()
                        currentKey = pasted.replace("-", "")
                        error = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("COLAR KEY", color = Color.Black, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (currentKey.isEmpty()) {
                        error = "COLE UMA KEY"
                        return@Button
                    }
                    isLoading = true
                    scope.launch {
                        val keyToValidate = formatKey(currentKey)
                        val result = validateAndRegisterKey(context, keyToValidate, PROJECT, API_KEY)
                        if (result == "SUCCESS") {
                            viewModel.login(currentKey)
                            onLoginSuccess()
                        } else {
                            error = when(result) {
                                "NOT_FOUND" -> "KEY NÃO ENCONTRADA"
                                "DEVICE_MISMATCH" -> "DISPOSITIVO NÃO PERMITIDO"
                                "EXPIRED" -> "KEY EXPIRADA"
                                else -> "ERRO DE CONEXÃO"
                            }
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("ACESSAR", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

private fun formatKey(key: String): String {
    if (key.length <= 4) return key
    return key.substring(0, 4) + "-" + key.substring(4)
}

private suspend fun validateAndRegisterKey(
    context: Context,
    key: String,
    project: String,
    apiKey: String
): String = withContext(Dispatchers.IO) {
    var connection: HttpURLConnection? = null
    try {
        val rawId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: (android.os.Build.MANUFACTURER + android.os.Build.MODEL)

        val runQuery = "https://firestore.googleapis.com/v1/projects/$project/databases/(default)/documents:runQuery?key=$apiKey"
        
        val body = JSONObject().apply {
            val sq = JSONObject().apply {
                val from = JSONObject().apply { put("collectionId", "keys") }
                put("from", JSONArray().put(from))
                val where = JSONObject().apply {
                    val fc = JSONObject().apply {
                        put("field", JSONObject().apply { put("fieldPath", "keyString") })
                        put("op", "EQUAL")
                        put("value", JSONObject().apply { put("stringValue", key) })
                    }
                    put("fieldFilter", fc)
                }
                put("where", where)
            }
            put("structuredQuery", sq)
        }

        val url = URL(runQuery)
        connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            connectTimeout = 15000
            readTimeout = 15000
        }

        connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        
        if (connection.responseCode != 200) return@withContext "CONN_ERR"

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val results = JSONArray(response)

        if (results.length() == 0 || !results.getJSONObject(0).has("document")) return@withContext "NOT_FOUND"
        
        val document = results.getJSONObject(0).getJSONObject("document")
        val docName = document.getString("name") 
        val fields = document.getJSONObject("fields")
        
        val status = fields.optJSONObject("status")?.optString("stringValue") ?: ""
        val devId = fields.optJSONObject("deviceId")?.optString("stringValue") ?: "null"

        // BLOQUEIO CRÍTICO: Se não for active, não permite o login
        if (status != "active") return@withContext "EXPIRED"
        
        if (devId == "null" || devId.isEmpty()) {
            val updateUrl = "https://firestore.googleapis.com/v1/$docName?updateMask.fieldPaths=deviceId&updateMask.fieldPaths=firstUsed&key=$apiKey"
            val now = Instant.now().toString()
            
            val updateBody = JSONObject().apply {
                put("fields", JSONObject().apply {
                    put("deviceId", JSONObject().apply { put("stringValue", rawId) })
                    put("firstUsed", JSONObject().apply { put("timestampValue", now) })
                })
            }
            
            val patchConn = (URL(updateUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "PATCH"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 15000
            }
            patchConn.outputStream.use { it.write(updateBody.toString().toByteArray(Charsets.UTF_8)) }
            
            if (patchConn.responseCode == 200) return@withContext "SUCCESS"
            else return@withContext "REG_ERR"
        }
        
        if (devId != rawId) return@withContext "DEVICE_MISMATCH"

        return@withContext "SUCCESS"
    } catch (e: Exception) {
        return@withContext "EXC"
    } finally {
        connection?.disconnect()
    }
}
