package id.myindo.platform.kawalwarga.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.core.model.Role
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
    val context = LocalContext.current
    val allReports by viewModel.allSecurityReports.collectAsState()
    val rondaSchedules by viewModel.rondaSchedules.collectAsState()
    val activeSosAlerts by viewModel.activeSosAlerts.collectAsState()
    val activeContext by viewModel.activeContext.collectAsState()

    var selectedSubTab by remember { mutableStateOf("Laporan") } // "Laporan", "Ronda", "SOS"
    val isAddingReport by viewModel.isAddingSecurityReport.collectAsState()
    val selectedReport by viewModel.selectedSecurityReport.collectAsState()

    val isSecurityOfficerOrRT = activeContext?.role == Role.PETUGAS_KEAMANAN ||
            activeContext?.role == Role.KETUA_RT ||
            activeContext?.role == Role.KETUA_RW

    var showPanicSosDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectedSubTab == "Laporan") {
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
                .testTag("security_screen_view"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // SOS Alert Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    elevation = CardDefaults.cardElevation(2.dp)
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
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Tombol SOS Darurat",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "Verifikasi tanda terima server & kontak keamanan",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                        }
                        Button(
                            onClick = { showPanicSosDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("SOS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Sub Tab Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Laporan", "Ronda", "Darurat & Kontak").forEach { tab ->
                        FilterChip(
                            selected = selectedSubTab == tab,
                            onClick = { selectedSubTab = tab },
                            label = { Text(tab, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Sub Tab Content
            when (selectedSubTab) {
                "Laporan" -> {
                    if (allReports.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outlineVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Belum ada laporan keamanan / ketertiban",
                                        color = MaterialTheme.colorScheme.outline,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(allReports, key = { it.id }) { report ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openSecurityReportDetail(report) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            StatusBadge(status = report.status)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            UrgencyBadge(urgency = report.urgency)
                                        }
                                        Text(
                                            text = viewModel.formatShortDate(report.timestamp),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = report.category,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Lokasi: ${report.location}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = report.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                    if (!report.responseNote.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Tanggapan Petugas: ${report.responseNote}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF0284C7),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    // Responder Quick Action for Security Officer
                                    if (isSecurityOfficerOrRT && report.status != "Selesai") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            if (report.status == "Menunggu Respon") {
                                                Button(
                                                    onClick = {
                                                        viewModel.updateSecurityReportStatus(report, "Sedang Ditangani", "Petugas sedang menangani di lokasi")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Tangani Laporan", fontSize = 11.sp)
                                                }
                                            } else {
                                                Button(
                                                    onClick = {
                                                        viewModel.updateSecurityReportStatus(report, "Selesai", "Kendala telah terselesaikan dengan aman")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Tandai Selesai", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "Ronda" -> {
                    items(rondaSchedules) { shift ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.NightlightRound,
                                            contentDescription = null,
                                            tint = Color(0xFF5E35B1),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${shift.dayName} (${shift.dateFormatted})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (shift.isCheckedIn) Color(0xFFE0F2F1) else Color(0xFFEDE7F6)
                                    ) {
                                        Text(
                                            text = if (shift.isCheckedIn) "Sudah Hadir" else shift.timeRange,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (shift.isCheckedIn) TealPrimary else Color(0xFF5E35B1),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pos Jaga: ${shift.location} · RT ${shift.rt}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Petugas: ${shift.officers.joinToString(", ")}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Koordinator: ${shift.coordinatorName} (${shift.coordinatorPhone})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                // Check-in Button for security / patrol officer
                                if (isSecurityOfficerOrRT && !shift.isCheckedIn) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.checkInRonda(shift) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E35B1)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Presensi Ronda (Check-in Malam Ini)", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                "Darurat & Kontak" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Nomor Kontak Darurat Resmi Lingkungan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Dapat dihubungi langsung saat situasi genting atau saat perangkat sedang offline.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                EmergencyContactItem("Pos Keamanan & Ronda", "081233445566", "24 Jam", onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:081233445566"))
                                    context.startActivity(intent)
                                })
                                EmergencyContactItem("Polsek Cilodong / Call Center Polri", "110", "Darurat Polisi", onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:110"))
                                    context.startActivity(intent)
                                })
                                EmergencyContactItem("Ambulans & Gawat Darurat Medis", "118", "Layanan Medis", onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:118"))
                                    context.startActivity(intent)
                                })
                                EmergencyContactItem("Pemadam Kebakaran (Damkar)", "113", "Emergency Kebakaran", onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:113"))
                                    context.startActivity(intent)
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Security Report Dialog
    if (isAddingReport) {
        var selectedCategory by remember { mutableStateOf("Keamanan / Kriminal") }
        var selectedUrgency by remember { mutableStateOf("Normal") }
        var location by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        val categories = listOf(
            "Keamanan / Kriminal",
            "Penerangan / Lampu Padam",
            "Ketertiban Lingkungan",
            "Sampah & Kebersihan",
            "Kerusakan Fasum",
            "Bencana Alam / Lainnya"
        )

        AlertDialog(
            onDismissRequest = { viewModel.closeAddSecurityReport() },
            title = {
                Text("Lapor Kejadian Lingkungan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Kategori Laporan:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    categories.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = cat }
                        ) {
                            RadioButton(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat }
                            )
                            Text(cat, fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Lokasi Kejadian *") },
                        placeholder = { Text("Contoh: Depan Pos Ronda Blok B") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Keterangan Laporan *") },
                        placeholder = { Text("Jelaskan rincian kejadian dengan jelas...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (location.isNotBlank() && description.isNotBlank()) {
                            viewModel.submitSecurityReport(
                                category = selectedCategory,
                                urgency = selectedUrgency,
                                location = location,
                                description = description
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Text("Kirim Laporan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddSecurityReport() }) {
                    Text("Batal")
                }
            }
        )
    }

    // Detail Report Dialog
    selectedReport?.let { report ->
        AlertDialog(
            onDismissRequest = { viewModel.closeSecurityReportDetail() },
            title = {
                Text("Detail Laporan Kejadian", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailFieldRow("Pelapor", report.reporterName)
                    DetailFieldRow("Kategori", report.category)
                    DetailFieldRow("Urgensi", report.urgency)
                    DetailFieldRow("Lokasi", report.location)
                    DetailFieldRow("Waktu Lapor", viewModel.formatDate(report.timestamp))
                    DetailFieldRow("Keterangan", report.description)
                    DetailFieldRow("Status", report.status)
                    if (!report.responseNote.isNullOrBlank()) {
                        DetailFieldRow("Catatan Petugas", report.responseNote)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeSecurityReportDetail() }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Panic SOS Confirmation with Hold Instructions
    if (showPanicSosDialog) {
        var emergencyCat by remember { mutableStateOf("Keamanan / Pencurian") }
        AlertDialog(
            onDismissRequest = { showPanicSosDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aktivasi Alarm SOS Darurat", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Sinyal darurat memerlukan konfirmasi tanda terima dari server backend. Petugas keamanan terdekat akan langsung menerima notifikasi darurat.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf("Keamanan / Pencurian", "Kebakaran", "Medis Darurat", "Bencana Alam").forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { emergencyCat = cat }
                        ) {
                            RadioButton(selected = emergencyCat == cat, onClick = { emergencyCat = cat })
                            Text(cat, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerPanicSos(emergencyCat, "Lingkungan RT ${activeContext?.rtNumber ?: "02"}")
                        showPanicSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("KIRIM SEKARANG", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPanicSosDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun EmergencyContactItem(
    title: String,
    phoneNumber: String,
    note: String,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "$phoneNumber ($note)", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
            }
            IconButton(
                onClick = onCall,
                colors = IconButtonDefaults.iconButtonColors(containerColor = TealPrimary)
            ) {
                Icon(Icons.Default.Phone, contentDescription = "Panggil", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
