package com.devwilltech.otimizacao

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devwilltech.otimizacao.ui.screens.*
import com.devwilltech.otimizacao.ui.theme.OtimizacaoWillTechTheme
import com.devwilltech.otimizacao.ui.theme.NeonYellow
import com.devwilltech.otimizacao.ui.theme.DeepPurple
import kotlinx.coroutines.delay

enum class Screen {
    LOGIN, OPTIMIZATION, BYPASS, SENSI
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            
            LaunchedEffect(uiState.isHideStreamEnabled) {
                if (uiState.isHideStreamEnabled) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            OtimizacaoWillTechTheme {
                var currentScreen by remember { mutableStateOf(if (uiState.isLoggedIn) Screen.OPTIMIZATION else Screen.LOGIN) }

                if (currentScreen == Screen.LOGIN && !uiState.isLoggedIn) {
                    LoginScreen(viewModel = viewModel, onLoginSuccess = { currentScreen = Screen.OPTIMIZATION })
                } else {
                    Scaffold(
                        topBar = {
                            KeyTimerHeader(viewModel)
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color.Black,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.OPTIMIZATION,
                                    onClick = { currentScreen = Screen.OPTIMIZATION },
                                    label = { Text("OTM", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    icon = {},
                                    colors = NavigationBarItemDefaults.colors(selectedTextColor = NeonYellow, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent)
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.BYPASS,
                                    onClick = { currentScreen = Screen.BYPASS },
                                    label = { Text("BYPASS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    icon = {},
                                    colors = NavigationBarItemDefaults.colors(selectedTextColor = DeepPurple, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent)
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.SENSI,
                                    onClick = { currentScreen = Screen.SENSI },
                                    label = { Text("SENSI", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    icon = {},
                                    colors = NavigationBarItemDefaults.colors(selectedTextColor = Color.Cyan, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent)
                                )
                            }
                        }
                    ) { innerPadding ->
                        Surface(modifier = Modifier.padding(innerPadding)) {
                            when (currentScreen) {
                                Screen.OPTIMIZATION -> MainScreen(viewModel = viewModel)
                                Screen.BYPASS -> BypassScreen(viewModel = viewModel)
                                Screen.SENSI -> SensiScreen(viewModel = viewModel)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeyTimerHeader(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var timeLeft by remember { mutableStateOf("00d 00h 00m 00s") }

    LaunchedEffect(uiState.keyData) {
        while (true) {
            val now = System.currentTimeMillis()
            val firstUsedStr = uiState.keyData?.firstUsed
            val firstUsed = firstUsedStr?.toLongOrNull() ?: now
            val validityDays = uiState.keyData?.validity ?: 0
            val expirationTime = firstUsed + (validityDays.toLong() * 24 * 60 * 60 * 1000L)
            val diff = expirationTime - now

            if (diff <= 0) {
                timeLeft = "EXPIRADA"
                break
            }

            val d = diff / (24 * 60 * 60 * 1000L)
            val h = (diff / (60 * 60 * 1000L)) % 24
            val m = (diff / (60 * 1000L)) % 60
            val s = (diff / 1000L) % 60
            timeLeft = String.format("%02dd %02dh %02dm %02ds", d, h, m, s)
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(top = 40.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                .background(Color(0xFF1A1A1A), RoundedCornerShape(99.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(Color.Green, RoundedCornerShape(50.dp)))
            Spacer(Modifier.width(8.dp))
            Text("KEY EXPIRA EM: ", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(timeLeft, color = NeonYellow, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}
