package id.myindo.platform.kawalwarga.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.core.auth.AuthState
import id.myindo.platform.kawalwarga.core.model.Role
import id.myindo.platform.kawalwarga.ui.components.MetricStatCard
import id.myindo.platform.kawalwarga.ui.components.StatusBadge
import id.myindo.platform.kawalwarga.ui.theme.*
import id.myindo.platform.kawalwarga.ui.viewmodel.MainTab
import id.myindo.platform.kawalwarga.ui.viewmodel.RtRwViewModel

@Composable
fun HomeScreen(
    viewModel: RtRwViewModel,
    modifier: Modifier = Modifier
) {
    val citizens by viewModel.allCitizens.collectAsState()
    val letters by viewModel.allLetters.collectAsState()
    val reports by viewModel.allSecurityReports.collectAsState()
    val dues by viewModel.allDues.collectAsState()
    val announcements by viewModel.allAnnouncements.collectAsState()
    val activeContext by viewModel.activeContext.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val lastSynced by viewModel.lastSynced.collectAsState()

    var showSosDialog by remember { mutableStateOf(false) }

    val userFullName = (authState as? AuthState.Authenticated)?.bootstrap?.user?.fullName ?: "Warga"
    val communityInfo = (authState as? AuthState.Authenticated)?.bootstrap?.community

    val pendingLettersCount = letters.count { it.status == "Diajukan" || it.status == "Diproses" }
    val activeReportsCount = reports.count { it.status != "Selesai" }
    val totalKasPaid = dues.filter { it.paymentStatus == "Lunas" }.sumOf { it.totalAmount }
    val pendingDuesVerifCount = dues.count { it.paymentStatus == "Menunggu Verifikasi" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Dynamic Neighborhood Banner (driven by backend context)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF07382F),
                                    Color(0xFF0D5447),
                                    Color(0xFF136449)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .border(0.8.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apartment,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = CivicAmberTertiary.copy(alpha = 0.22f),
                                        border = BorderStroke(0.8.dp, CivicAmberTertiary.copy(alpha = 0.45f))
                                    ) {
                                        Text(
                                            text = "RUKUN TETANGGA ${activeContext?.rtNumber ?: "02"} / RW ${activeContext?.rwNumber ?: "05"}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFDE68A),
                                            letterSpacing = 0.8.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = communityInfo?.name ?: "Paguyuban RW 05 Sukamaju",
                                        fontSize = 17.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.2).sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Greeting and Active Role
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Selamat Datang,",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp,
                                    letterSpacing = 0.1.sp
                                )
                                Text(
                                    text = userFullName,
                                    color = Color.White,
                                    fontSize = 16.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.18f),
                                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = activeContext?.role?.displayName ?: "Warga",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Last Synced Status bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = Color(0xFF6ED8C2),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Terakhir disinkronkan: ${viewModel.formatDate(lastSynced)}",
                                color = Color(0xFFB4EBE0),
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }
        }

        // SOS Panic Alert Button Card (Long-press to activate)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sos_emergency_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f)),
                border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFD32F2F))
                                .border(0.8.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "SOS",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Panggilan Darurat & SOS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Sinyal darurat tersambung pos pengamanan RT",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Button(
                        onClick = { showSosDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_sos_trigger")
                    ) {
                        Text("KIRIM SOS", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Operational Overview Section (Adapts strictly to active Role)
        item {
            Text(
                text = "Ringkasan Operasional (${activeContext?.role?.displayName})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            when (activeContext?.role) {
                Role.BENDAHARA -> {
                    // Bendahara overview
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricStatCard(
                                title = "Verifikasi Bukti",
                                value = "$pendingDuesVerifCount",
                                subtitle = "Menunggu dicek",
                                icon = Icons.Default.PendingActions,
                                accentColor = Color(0xFFF57C00),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.IURAN) }
                            )
                            MetricStatCard(
                                title = "Kas Masuk",
                                value = viewModel.formatRupiah(totalKasPaid),
                                subtitle = "Bulan September",
                                icon = Icons.Default.AccountBalanceWallet,
                                accentColor = TealPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.IURAN) }
                            )
                        }
                    }
                }
                Role.PETUGAS_KEAMANAN -> {
                    // Security Officer overview
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricStatCard(
                                title = "Laporan Aktif",
                                value = "$activeReportsCount",
                                subtitle = "Perlu penanganan",
                                icon = Icons.Default.Shield,
                                accentColor = Color(0xFFE53935),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.KEAMANAN) }
                            )
                            MetricStatCard(
                                title = "Ronda Malam",
                                value = "Aktif",
                                subtitle = "Pukul 22:00 WIB",
                                icon = Icons.Default.NightlightRound,
                                accentColor = Color(0xFF5E35B1),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.KEAMANAN) }
                            )
                        }
                    }
                }
                Role.KETUA_RT, Role.KETUA_RW, Role.SEKRETARIS -> {
                    // RT/RW Leader overview
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricStatCard(
                                title = "Surat Masuk",
                                value = "$pendingLettersCount",
                                subtitle = "Menunggu approval",
                                icon = Icons.Default.Description,
                                accentColor = Color(0xFF1976D2),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.SURAT) }
                            )
                            MetricStatCard(
                                title = "Warga Scoped",
                                value = "${citizens.size}",
                                subtitle = "Terdaftar di sistem",
                                icon = Icons.Default.People,
                                accentColor = TealPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.WARGA) }
                            )
                        }
                    }
                }
                else -> {
                    // Warga overview
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricStatCard(
                                title = "Surat Aktif",
                                value = "${letters.size}",
                                subtitle = "Permohonan pengantar",
                                icon = Icons.Default.Description,
                                accentColor = Color(0xFF1976D2),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.SURAT) }
                            )
                            MetricStatCard(
                                title = "Status Iuran",
                                value = if (dues.any { it.paymentStatus == "Belum Bayar" }) "Ada Tagihan" else "Lunas",
                                subtitle = "Iuran September 2026",
                                icon = Icons.Default.AccountBalanceWallet,
                                accentColor = if (dues.any { it.paymentStatus == "Belum Bayar" }) Color(0xFFE53935) else TealPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTab(MainTab.IURAN) }
                            )
                        }
                    }
                }
            }
        }

        // Quick Service Actions Grid
        item {
            Text(
                text = "Layanan Mandiri Warga",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = Icons.Default.Description,
                    label = "Ajukan Surat",
                    bgColor = Color(0xFFE3F2FD),
                    iconColor = Color(0xFF1976D2),
                    onClick = {
                        viewModel.setTab(MainTab.SURAT)
                        viewModel.openAddLetter()
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.Payment,
                    label = "Bayar Iuran",
                    bgColor = Color(0xFFE8F5E9),
                    iconColor = Color(0xFF388E3C),
                    onClick = { viewModel.setTab(MainTab.IURAN) }
                )
                QuickActionItem(
                    icon = Icons.Default.ReportProblem,
                    label = "Lapor Fasum",
                    bgColor = Color(0xFFFFF3E0),
                    iconColor = Color(0xFFF57C00),
                    onClick = {
                        viewModel.setTab(MainTab.KEAMANAN)
                        viewModel.openAddSecurityReport()
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.NightlightRound,
                    label = "Jadwal Ronda",
                    bgColor = Color(0xFFEDE7F6),
                    iconColor = Color(0xFF5E35B1),
                    onClick = { viewModel.setTab(MainTab.KEAMANAN) }
                )
            }
        }

        // Recent Community Announcements from Backend
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pengumuman & Informasi Warga",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${announcements.size} Pengumuman",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        items(announcements) { announcement ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = announcement.priority)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = announcement.category,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = viewModel.formatShortDate(announcement.date),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = announcement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = announcement.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // SOS Panic Dialog Confirmation
    if (showSosDialog) {
        var selectedEmergencyCategory by remember { mutableStateOf("Keamanan / Pencurian") }
        val categories = listOf("Keamanan / Pencurian", "Kebakaran", "Medis / Ambulans", "Bencana Alam")

        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Konfirmasi Alarm SOS", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Sinyal darurat akan dikirimkan ke Pos Keamanan & Pengurus RT dengan bukti tanda terima dari server.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("Pilih Jenis Darurat:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    categories.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEmergencyCategory = cat }
                        ) {
                            RadioButton(
                                selected = selectedEmergencyCategory == cat,
                                onClick = { selectedEmergencyCategory = cat }
                            )
                            Text(text = cat, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerPanicSos(
                            selectedEmergencyCategory,
                            "Lingkungan RT ${activeContext?.rtNumber ?: "02"} / RW ${activeContext?.rwNumber ?: "05"}"
                        )
                        showSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("KIRIM SINYAL DARURAT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(78.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(0.8.dp, iconColor.copy(alpha = 0.22f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
