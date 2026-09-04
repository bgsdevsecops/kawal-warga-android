package id.myindo.platform.kawalwarga.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.data.model.RondaSchedule
import id.myindo.platform.kawalwarga.data.model.SecurityReport
import id.myindo.platform.kawalwarga.ui.components.StatusBadge
import id.myindo.platform.kawalwarga.ui.components.UrgencyBadge
import id.myindo.platform.kawalwarga.ui.theme.*
import id.myindo.platform.kawalwarga.ui.viewmodel.RtRwViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    viewModel: RtRwViewModel,
    modifier: Modifier = Modifier
) {
    val allReports by viewModel.allSecurityReports.collectAsState()
    val reports by viewModel.filteredSecurityReports.collectAsState()
    val statusFilter by viewModel.securityStatusFilter.collectAsState()
    val subTab by viewModel.securitySubTab.collectAsState()
    val rondaSchedules = viewModel.rondaSchedules

    val isAddingReport by viewModel.isAddingSecurityReport.collectAsState()
    val selectedReport by viewModel.selectedSecurityReport.collectAsState()

    val waitingCount = allReports.count { it.status == "Menunggu Respon" }
    val progressCount = allReports.count { it.status == "Sedang Ditangani" }
    val resolvedCount = allReports.count { it.status == "Selesai" }

    var showPanicSosDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (subTab == "Laporan") {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.openAddSecurityReport() },
                    containerColor = Color(0xFFE11D48),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.AddAlert, contentDescription = null) },
                    text = { Text("Lapor Kejadian") },
                    modifier = Modifier.testTag("add_report_fab")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("security_list_view"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Panic Alert Quick Trigger Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pusat Siaga & Siskamling RT 02",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "Pencurian • Kebakaran • Darurat Medis",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }

                        Button(
                            onClick = { showPanicSosDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("SOS RT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Sub-tabs Segmented control: "Laporan Warga" & "Jadwal Pos Ronda"
            item {
                TabRow(
                    selectedTabIndex = if (subTab == "Laporan") 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = subTab == "Laporan",
                        onClick = { viewModel.updateSecuritySubTab("Laporan") },
                        text = {
                            Text(
                                text = "Laporan Kejadian ($waitingCount Baru)",
                                fontWeight = if (subTab == "Laporan") FontWeight.Bold else FontWeight.Normal,
                                color = if (subTab == "Laporan") TealPrimary else SlateTextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = subTab == "Jadwal Ronda",
                        onClick = { viewModel.updateSecuritySubTab("Jadwal Ronda") },
                        text = {
                            Text(
                                text = "Jadwal Pos Ronda",
                                fontWeight = if (subTab == "Jadwal Ronda") FontWeight.Bold else FontWeight.Normal,
                                color = if (subTab == "Jadwal Ronda") TealPrimary else SlateTextSecondary
                            )
                        }
                    )
                }
            }

            if (subTab == "Laporan") {
                // Status Filter Chips
                item {
                    val filters = listOf("Semua", "Menunggu Respon", "Sedang Ditangani", "Selesai")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { f ->
                            FilterChip(
                                selected = statusFilter == f,
                                onClick = { viewModel.updateSecurityStatusFilter(f) },
                                label = { Text(f, fontSize = 12.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                if (reports.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = SlateTextMuted,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Lingkungan aman & kondusif",
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextSecondary
                                )
                                Text(
                                    text = "Tidak ada laporan aktif untuk filter ini",
                                    fontSize = 12.sp,
                                    color = SlateTextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(reports, key = { it.id }) { report ->
                        SecurityReportCardItem(
                            report = report,
                            viewModel = viewModel,
                            onClick = { viewModel.openSecurityReportDetail(report) }
                        )
                    }
                }
            } else {
                // Pos Ronda Schedule Tab
                item {
                    Text(
                        text = "Jadwal Ronda Malam & Siskamling Pekan Ini",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(rondaSchedules) { schedule ->
                    RondaScheduleCard(schedule = schedule)
                }
            }
        }
    }

    // Add Security Report Dialog
    if (isAddingReport) {
        AddSecurityReportDialog(
            onDismiss = { viewModel.closeAddSecurityReport() },
            onSubmit = { name, phone, cat, urg, loc, desc ->
                viewModel.submitSecurityReport(name, phone, cat, urg, loc, desc)
            }
        )
    }

    // Detail & Handling Report Dialog
    selectedReport?.let { report ->
        SecurityReportDetailDialog(
            report = report,
            viewModel = viewModel,
            onDismiss = { viewModel.closeSecurityReportDetail() },
            onUpdateStatus = { newStatus, note ->
                viewModel.updateSecurityReportStatus(report, newStatus, note)
            }
        )
    }

    // Panic SOS Quick Alert Dialog
    if (showPanicSosDialog) {
        var selectedCategory by remember { mutableStateOf("Kebakaran") }
        var locationText by remember { mutableStateOf("Lingkungan RT 02 / RW 05") }

        AlertDialog(
            onDismissRequest = { showPanicSosDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(text = "Aktifkan Tombol Panik SOS", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pilih jenis kedaruratan untuk sinyal siaga lingkungan:", fontSize = 12.sp)
                    listOf("Pencurian / Maling", "Kebakaran", "Darurat Medis", "Gangguan Keamanan").forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory == cat) Color(0xFFFEE2E2) else Color.Transparent)
                                .clickable { selectedCategory = cat }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = cat, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    OutlinedTextField(
                        value = locationText,
                        onValueChange = { locationText = it },
                        label = { Text("Lokasi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerPanicSos(selectedCategory, locationText)
                        showPanicSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("KIRIM ALARM SEKARANG")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPanicSosDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun SecurityReportCardItem(
    report: SecurityReport,
    viewModel: RtRwViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("report_card_${report.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (report.urgency == "Darurat") Color(0xFFFFF1F2) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UrgencyBadge(urgency = report.urgency)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SlateSurfaceVariant
                    ) {
                        Text(
                            text = report.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                StatusBadge(status = report.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = report.description,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = SlateTextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = report.location,
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pelapor: ${report.reporterName}",
                    fontSize = 11.sp,
                    color = SlateTextMuted
                )
                Text(
                    text = viewModel.formatShortDate(report.timestamp),
                    fontSize = 10.sp,
                    color = SlateTextMuted
                )
            }

            if (!report.responseNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF0FDF4)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tanggapan Petugas: ${report.responseNote}",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RondaScheduleCard(schedule: RondaSchedule) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(TealPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightlightRound,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = schedule.day,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = schedule.timeRange,
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TealPrimaryContainer
                ) {
                    Text(
                        text = schedule.location,
                        color = TealOnPrimaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SlateBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Petugas Jaga Ronda:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                schedule.patrolOfficers.forEach { officer ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SlateSurfaceVariant
                    ) {
                        Text(
                            text = officer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Koordinator: ${schedule.coordinator}",
                    fontSize = 11.sp,
                    color = SlateTextMuted
                )
                TextButton(
                    onClick = {
                        val phone = schedule.coordinator.filter { it.isDigit() }
                        if (phone.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hubungi", fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSecurityReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String, String) -> Unit
) {
    var reporterName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Keamanan / Kriminal") }
    var urgency by remember { mutableStateOf("Penting") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Keamanan / Kriminal",
        "Penerangan / Lampu Padam",
        "Sampah / Kebersihan",
        "Ketertiban Lingkungan",
        "Kerusakan Sarana Fasum",
        "Bencana / Darurat"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Buat Laporan Keamanan / Lingkungan", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                errorMessage?.let {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = reporterName,
                    onValueChange = { reporterName = it },
                    label = { Text("Nama Pelapor *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. HP / WA Pelapor *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(text = "Kategori Masalah *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Text(text = "Tingkat Urgensi *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Normal", "Penting", "Darurat").forEach { urg ->
                        FilterChip(
                            selected = urgency == urg,
                            onClick = { urgency = urg },
                            label = { Text(urg) }
                        )
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi Kejadian *") },
                    placeholder = { Text("Contoh: Depan Blok B Gang Mawar") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi / Kronologi Kejadian *") },
                    placeholder = { Text("Jelaskan detail situasi atau keluhan...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reporterName.isBlank() || phone.isBlank() || location.isBlank() || description.isBlank()) {
                        errorMessage = "Harap lengkapi semua kolom bertanda bintang."
                        return@Button
                    }
                    onSubmit(reporterName, phone, category, urgency, location, description)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
            ) {
                Text("Kirim Laporan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun SecurityReportDetailDialog(
    report: SecurityReport,
    viewModel: RtRwViewModel,
    onDismiss: () -> Unit,
    onUpdateStatus: (String, String?) -> Unit
) {
    var responseNote by remember { mutableStateOf(report.responseNote ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Detail Laporan Keamanan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UrgencyBadge(urgency = report.urgency)
                    StatusBadge(status = report.status)
                }

                DetailRow("Kategori", report.category)
                DetailRow("Lokasi", report.location)
                DetailRow("Pelapor", "${report.reporterName} (${report.reporterPhone})")
                DetailRow("Waktu Kejadian", viewModel.formatDate(report.timestamp))

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Kronologi / Laporan:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = SlateSurfaceVariant
                ) {
                    Text(
                        text = report.description,
                        fontSize = 12.sp,
                        color = SlateTextPrimary,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Tanggapan Petugas Ronda / RT:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(
                    value = responseNote,
                    onValueChange = { responseNote = it },
                    placeholder = { Text("Tuliskan tindakan atau penanganan yang telah dilakukan...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (report.status != "Sedang Ditangani") {
                    FilledTonalButton(
                        onClick = {
                            onUpdateStatus("Sedang Ditangani", responseNote.ifBlank { "Petugas sedang menuju lokasi" })
                        }
                    ) {
                        Text("Proses")
                    }
                }

                Button(
                    onClick = {
                        onUpdateStatus("Selesai", responseNote.ifBlank { "Laporan telah ditangani oleh petugas ronda" })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Selesaikan")
                }
            }
        },
        dismissButton = {}
    )
}
