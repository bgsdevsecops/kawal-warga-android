package id.myindo.platform.kawalwarga.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.core.model.Role
import id.myindo.platform.kawalwarga.data.model.LetterRequest
import id.myindo.platform.kawalwarga.ui.components.StatusBadge
import id.myindo.platform.kawalwarga.ui.theme.*
import id.myindo.platform.kawalwarga.ui.viewmodel.RtRwViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterScreen(
    viewModel: RtRwViewModel,
    modifier: Modifier = Modifier
) {
    val allLetters by viewModel.allLetters.collectAsState()
    val letters by viewModel.filteredLetters.collectAsState()
    val statusFilter by viewModel.letterStatusFilter.collectAsState()
    val letterTypes by viewModel.letterTypes.collectAsState()
    val activeContext by viewModel.activeContext.collectAsState()

    val isAddingLetter by viewModel.isAddingLetter.collectAsState()
    val previewLetter by viewModel.previewLetter.collectAsState()

    val isPengurus = activeContext?.role == Role.KETUA_RT ||
            activeContext?.role == Role.KETUA_RW ||
            activeContext?.role == Role.SEKRETARIS

    val pendingCount = allLetters.count { it.status == "Diajukan" }
    val progressCount = allLetters.count { it.status == "Diproses" }
    val approvedCount = allLetters.count { it.status == "Disetujui" }

    var rejectLetterTarget by remember { mutableStateOf<LetterRequest?>(null) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddLetter() },
                containerColor = TealPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                text = { Text("Ajukan Surat") },
                modifier = Modifier.testTag("add_letter_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("letter_list_view"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CitizenStatItem("Diajukan", "$pendingCount", AmberTertiary)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Diproses", "$progressCount", Color(0xFF0284C7))
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Disetujui", "$approvedCount", EmeraldSecondary)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Total", "${allLetters.size}", TealPrimary)
                    }
                }
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Semua", "Diajukan", "Diproses", "Disetujui", "Ditolak").forEach { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { viewModel.updateLetterStatusFilter(status) },
                            label = { Text(status, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Letters List
            if (letters.isEmpty()) {
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
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada pengajuan surat",
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(letters, key = { it.id }) { letter ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openLetterPreview(letter) },
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
                                StatusBadge(status = letter.status)
                                Text(
                                    text = viewModel.formatShortDate(letter.requestDate),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = letter.letterType,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pemohon: ${letter.citizenName}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Keperluan: ${letter.purpose}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 2
                            )
                            if (letter.rejectionReason != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Alasan Penolakan: ${letter.rejectionReason}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Pengurus Quick Action Buttons
                            if (isPengurus && (letter.status == "Diajukan" || letter.status == "Diproses")) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            rejectLetterTarget = letter
                                            rejectionReasonInput = ""
                                        }
                                    ) {
                                        Text("Tolak", color = Color(0xFFD32F2F), fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (letter.status == "Diajukan") {
                                        Button(
                                            onClick = { viewModel.transitionLetter(letter, "PROCESS") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Proses", fontSize = 12.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.transitionLetter(letter, "APPROVE") },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Setujui", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Ajukan Surat Baru (Dynamic Types from server)
    if (isAddingLetter) {
        var selectedTypeCode by remember { mutableStateOf(letterTypes.firstOrNull()?.code ?: "SP_DOMISILI") }
        var purpose by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.closeAddLetter() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pengajuan Surat Pengantar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Pilih jenis surat pengantar resmi yang akan diajukan ke Pengurus RT:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text("Jenis Surat:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    letterTypes.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTypeCode = type.code }
                        ) {
                            RadioButton(
                                selected = selectedTypeCode == type.code,
                                onClick = { selectedTypeCode = type.code }
                            )
                            Column {
                                Text(type.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text(type.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = purpose,
                        onValueChange = { purpose = it },
                        label = { Text("Keperluan Pengajuan *") },
                        placeholder = { Text("Contoh: Persyaratan administrasi KTP baru") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan / Lampiran Dokumen") },
                        placeholder = { Text("Contoh: Fotokopi KK & akta kelahiran sudah disiapkan") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (purpose.isNotBlank()) {
                            viewModel.submitLetterRequest(selectedTypeCode, purpose, notes)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Kirim Pengajuan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddLetter() }) {
                    Text("Batal")
                }
            }
        )
    }

    // Reject Letter Dialog
    rejectLetterTarget?.let { letter ->
        AlertDialog(
            onDismissRequest = { rejectLetterTarget = null },
            title = {
                Text("Tolak Pengajuan Surat", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Masukkan alasan penolakan surat untuk pemohon ${letter.citizenName}:", fontSize = 13.sp)
                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = { rejectionReasonInput = it },
                        label = { Text("Alasan Penolakan") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.transitionLetter(letter, "REJECT", rejectionReasonInput.ifBlank { "Data pendukung belum lengkap" })
                        rejectLetterTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Tolak Surat")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectLetterTarget = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Letter Preview / Issued Document
    previewLetter?.let { letter ->
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { viewModel.closeLetterPreview() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Article, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detail Surat Pengantar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Formal Letter Header Mock
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .background(Color(0xFFFAFAFA))
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("RUKUN TETANGGA ${letter.rt} / RW ${letter.rw}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                            Text("SURAT PENGANTAR RESMI", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.Black)
                            Text("Nomor: ${letter.letterNumber}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }

                    DetailFieldRow("Pemohon", letter.citizenName)
                    DetailFieldRow("NIK Pemohon", letter.citizenNik)
                    DetailFieldRow("Alamat", letter.citizenAddress)
                    DetailFieldRow("Jenis Surat", letter.letterType)
                    DetailFieldRow("Keperluan", letter.purpose)
                    DetailFieldRow("Status", letter.status)
                    if (letter.notes.isNotBlank()) {
                        DetailFieldRow("Catatan Dokumen", letter.notes)
                    }
                    if (letter.status == "Disetujui") {
                        DetailFieldRow("Pejabat Penandatangan", letter.approverName)
                        DetailFieldRow("Tanggal Terbit", viewModel.formatDate(letter.approvedDate ?: letter.requestDate))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeLetterPreview() }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                if (letter.status == "Disetujui") {
                    TextButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Surat Pengantar", "Nomor Surat: ${letter.letterNumber}\nPemohon: ${letter.citizenName}\nJenis: ${letter.letterType}\nStatus: Disetujui")
                        clipboard.setPrimaryClip(clip)
                        viewModel.showMessage("Informasi surat disalin ke clipboard")
                    }) {
                        Text("Salin Info Surat", color = TealPrimary)
                    }
                }
            }
        )
    }
}
