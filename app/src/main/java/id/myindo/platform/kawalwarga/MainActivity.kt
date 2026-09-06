package id.myindo.platform.kawalwarga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.core.auth.AuthState
import id.myindo.platform.kawalwarga.core.model.Role
import id.myindo.platform.kawalwarga.core.model.UserContext
import id.myindo.platform.kawalwarga.core.sync.SyncState
import id.myindo.platform.kawalwarga.ui.screens.*
import id.myindo.platform.kawalwarga.ui.theme.CivicAmberTertiary
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
    val activeContext by viewModel.activeContext.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showContextSwitcher by remember { mutableStateOf(false) }

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
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showContextSwitcher = true }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF0D5447), Color(0xFF136449))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            CivicAmberTertiary.copy(alpha = 0.35f)
                                        ),
                                        RoundedCornerShape(11.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Kawal Warga",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.5.sp,
                                        letterSpacing = (-0.2).sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Switch Context",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = activeContext?.label ?: "Memuat scope wilayah...",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    },
                    actions = {
                        // Sync Status Indicator & Action
                        IconButton(
                            onClick = { viewModel.syncNow() },
                            modifier = Modifier.testTag("sync_button")
                        ) {
                            if (syncState == SyncState.SYNCING) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = TealPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sinkronisasi Server",
                                    tint = if (syncState == SyncState.ERROR) Color(0xFFD32F2F) else TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Context Switcher Chip / Avatar
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { showContextSwitcher = true }
                                .testTag("role_context_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = when (activeContext?.role) {
                                        Role.BENDAHARA -> Icons.Default.AccountBalanceWallet
                                        Role.PETUGAS_KEAMANAN -> Icons.Default.LocalPolice
                                        Role.KETUA_RT, Role.KETUA_RW -> Icons.Default.AdminPanelSettings
                                        Role.SEKRETARIS -> Icons.Default.Description
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeContext?.role?.displayName ?: "Warga",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                )
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                )
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    MainTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = getTabIcon(tab, isSelected),
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            onClick = { viewModel.setTab(tab) },
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}"),
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealPrimary,
                                selectedTextColor = TealPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.BERANDA -> HomeScreen(viewModel = viewModel)
                MainTab.WARGA -> CitizenScreen(viewModel = viewModel)
                MainTab.SURAT -> LetterScreen(viewModel = viewModel)
                MainTab.KEAMANAN -> SecurityScreen(viewModel = viewModel)
                MainTab.IURAN -> DuesScreen(viewModel = viewModel)
            }

            // Global SOS Emergency Active Banner
            AnimatedVisibility(
                visible = isSosActive,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ALERT DARURAT SOS AKTIF!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Receipt server terkonfirmasi. Petugas ronda merapat ke lokasi.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        TextButton(
                            onClick = { viewModel.dismissSosAlert() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("Tutup", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Multi-role Context Switcher Modal
    if (showContextSwitcher) {
        val bootstrap = (authState as? AuthState.Authenticated)?.bootstrap
        val availableContexts = bootstrap?.availableContexts ?: emptyList()

        AlertDialog(
            onDismissRequest = { showContextSwitcher = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwitchAccount,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ganti Context / Role", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Akun Anda memiliki akses ke beberapa peran di lingkungan RT/RW. Pilih peran aktif:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableContexts) { ctx ->
                            val isCurrent = ctx.contextId == activeContext?.contextId
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.switchContext(ctx.contextId)
                                        showContextSwitcher = false
                                    }
                                    .border(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = if (isCurrent) TealPrimary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ctx.role.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Scope: RT ${ctx.rtNumber} / RW ${ctx.rwNumber}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isCurrent) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Aktif",
                                            tint = TealPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SSO: Keycloak (htz-auth)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        TextButton(
                            onClick = {
                                viewModel.syncNow()
                                showContextSwitcher = false
                            }
                        ) {
                            Text("Sinkronkan", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContextSwitcher = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

private fun getTabIcon(tab: MainTab, isSelected: Boolean): ImageVector {
    return when (tab) {
        MainTab.BERANDA -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
        MainTab.WARGA -> if (isSelected) Icons.Filled.People else Icons.Outlined.People
        MainTab.SURAT -> if (isSelected) Icons.Filled.Description else Icons.Outlined.Description
        MainTab.KEAMANAN -> if (isSelected) Icons.Filled.Shield else Icons.Outlined.Shield
        MainTab.IURAN -> if (isSelected) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet
    }
}
