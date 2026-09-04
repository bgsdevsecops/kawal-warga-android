package id.myindo.platform.kawalwarga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.ui.screens.CitizenScreen
import id.myindo.platform.kawalwarga.ui.screens.DuesScreen
import id.myindo.platform.kawalwarga.ui.screens.HomeScreen
import id.myindo.platform.kawalwarga.ui.screens.LetterScreen
import id.myindo.platform.kawalwarga.ui.screens.SecurityScreen
import id.myindo.platform.kawalwarga.ui.theme.KawalWargaTheme
import id.myindo.platform.kawalwarga.ui.theme.TealPrimary
import id.myindo.platform.kawalwarga.ui.viewmodel.MainTab
import id.myindo.platform.kawalwarga.ui.viewmodel.RtRwViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: RtRwViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KawalWargaTheme {
                MainRtRwApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRtRwApp(viewModel: RtRwViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isSosActive by viewModel.isSosAlertActive.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "WargaKu RT/RW",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "RT 02 / RW 05 Sukamaju",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE0F2F1),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = currentTab.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    NavItem(MainTab.BERANDA, Icons.Filled.Home, Icons.Outlined.Home, "Beranda"),
                    NavItem(MainTab.WARGA, Icons.Filled.Groups, Icons.Outlined.Groups, "Warga"),
                    NavItem(MainTab.SURAT, Icons.Filled.Assignment, Icons.Outlined.Assignment, "Surat"),
                    NavItem(MainTab.KEAMANAN, Icons.Filled.Shield, Icons.Outlined.Shield, "Keamanan"),
                    NavItem(MainTab.IURAN, Icons.Filled.Payments, Icons.Outlined.Payments, "Iuran")
                )

                navItems.forEach { item ->
                    val selected = currentTab == item.tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.setTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            indicatorColor = Color(0xFFE0F2F1)
                        ),
                        modifier = Modifier.testTag("tab_${item.tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Active Panic Alert Warning Bar
            AnimatedVisibility(
                visible = isSosActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFDC2626)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SIAGA SOS AKTIF: Petugas Ronda & Warga sedang merespon!",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { viewModel.dismissSosAlert() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (currentTab) {
                    MainTab.BERANDA -> HomeScreen(viewModel = viewModel)
                    MainTab.WARGA -> CitizenScreen(viewModel = viewModel)
                    MainTab.SURAT -> LetterScreen(viewModel = viewModel)
                    MainTab.KEAMANAN -> SecurityScreen(viewModel = viewModel)
                    MainTab.IURAN -> DuesScreen(viewModel = viewModel)
                }
            }
        }
    }
}

private data class NavItem(
    val tab: MainTab,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)
