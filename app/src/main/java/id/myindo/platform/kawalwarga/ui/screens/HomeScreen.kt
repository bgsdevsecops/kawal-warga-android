package id.myindo.platform.kawalwarga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var showSosDialog by remember { mutableStateOf(false) }

    val pendingLetters = letters.count { it.status == "Diajukan" || it.status == "Diproses" }
    val activeReports = reports.count { it.status != "Selesai" }
    val totalKasPaid = dues.filter { it.paymentStatus == "Lunas" }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Neighborhood Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    TealPrimary,
                                    Color(0xFF004D40)
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
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationCity,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "RUKUN TETANGGA 02 / RW 05",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF80CBC4),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Kelurahan Sukamaju",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Aktif",
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Selamat datang di Portal Pelayanan Warga Terpadu. Akses surat, keamanan siskamling, dan iuran dalam satu aplikasi.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // SOS Panic Button Alert Bar
                        Button(
                            onClick = { showSosDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("panic_sos_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "SOS Alert",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TOMBOL DARURAT / PANIC SOS RT",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Summary
        item {
            Text(
                text = "Ringkasan Wilayah RT 02",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Total Warga",
                    value = "${citizens.size} Jiwa",
                    subtitle = "${citizens.distinctBy { it.noKk }.size} Kepala Keluarga",
                    icon = Icons.Default.People,
                    accentColor = TealPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Surat Proses",
                    value = "$pendingLetters Surat",
                    subtitle = "${letters.size} Total Diajukan",
                    icon = Icons.Default.Description,
                    accentColor = AmberTertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Laporan Aktif",
                    value = "$activeReports Kasus",
                    subtitle = "Siskamling & Fasum",
                    icon = Icons.Default.Shield,
                    accentColor = Color(0xFFE11D48),
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Kas Terkumpul",
                    value = viewModel.formatRupiah(totalKasPaid),
                    subtitle = "Bulan September 2026",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = EmeraldSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Action Navigations
        item {
            Text(
                text = "Menu Layanan Utama",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickMenuCard(
                        title = "Daftar Warga",
                        desc = "Database KK & NIK Warga",
                        icon = Icons.Default.Groups,
                        color = TealPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_warga_card"),
                        onClick = { viewModel.setTab(MainTab.WARGA) }
                    )
                    QuickMenuCard(
                        title = "Surat Pengantar",
                        desc = "Buat & Cetak Surat RT",
                        icon = Icons.Default.Assignment,
                        color = Color(0xFF0284C7),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_surat_card"),
                        onClick = { viewModel.setTab(MainTab.SURAT) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickMenuCard(
                        title = "Keamanan & Ronda",
                        desc = "Laporan & Jadwal Pos Ronda",
                        icon = Icons.Default.Security,
                        color = Color(0xFFD97706),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_keamanan_card"),
                        onClick = { viewModel.setTab(MainTab.KEAMANAN) }
                    )
                    QuickMenuCard(
                        title = "Pembayaran Iuran",
                        desc = "Bayar QRIS, Kas & Kwitansi",
                        icon = Icons.Default.Payments,
                        color = EmeraldSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_iuran_card"),
                        onClick = { viewModel.setTab(MainTab.IURAN) }
                    )
                }
            }
        }

        // Announcements Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Warta & Pengumuman RT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${announcements.size} Berita",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
            }
        }

        items(announcements) { announcement ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (announcement.priority == "Penting") Color(0xFFFEE2E2) else Color(0xFFE0F2F1)
                        ) {
                            Text(
                                text = announcement.category,
                                color = if (announcement.priority == "Penting") Color(0xFFDC2626) else TealPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = viewModel.formatShortDate(announcement.date),
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = announcement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = announcement.content,
                        fontSize = 12.sp,
                        color = SlateTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }

    // Panic SOS Trigger Dialog
    if (showSosDialog) {
        var selectedEmergency by remember { mutableStateOf("Kebakaran / Asap Tebal") }
        var locationText by remember { mutableStateOf("Blok A / B / C Lingkungan RT 02") }

        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Aktivasi Tombol Darurat RT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Peringatan ini akan segera mengirim sinyal siaga darurat ke seluruh petugas satpam dan pengurus RT 02.",
                        fontSize = 13.sp,
                        color = SlateTextSecondary
                    )

                    Text(text = "Jenis Situasi Darurat:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    val emergencyTypes = listOf(
                        "Kebakaran / Asap Tebal",
                        "Pencurian / Maling Tertangkap",
                        "Darurat Medis / Butuh Ambulans",
                        "Bencana Banjir / Pohon Tumbang"
                    )

                    emergencyTypes.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedEmergency == type) Color(0xFFFEE2E2) else Color.Transparent)
                                .clickable { selectedEmergency = type }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedEmergency == type,
                                onClick = { selectedEmergency = type },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFDC2626))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = type, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    OutlinedTextField(
                        value = locationText,
                        onValueChange = { locationText = it },
                        label = { Text("Lokasi Kejadian") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerPanicSos(selectedEmergency, locationText)
                        showSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("KIRIM SINYAL SOS")
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
fun QuickMenuCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = SlateTextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
