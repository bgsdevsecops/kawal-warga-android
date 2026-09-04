package id.myindo.platform.kawalwarga.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import id.myindo.platform.kawalwarga.data.model.Citizen
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
    val citizens by viewModel.allCitizens.collectAsState()

    val isAddingLetter by viewModel.isAddingLetter.collectAsState()
    val previewLetter by viewModel.previewLetter.collectAsState()

    val pendingCount = allLetters.count { it.status == "Diajukan" }
    val progressCount = allLetters.count { it.status == "Diproses" }
    val approvedCount = allLetters.count { it.status == "Disetujui" }

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
                        CitizenStatItem("Menunggu", "$pendingCount", AmberTertiary)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Diproses", "$progressCount", Color(0xFF0284C7))
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Disetujui", "$approvedCount", EmeraldSecondary)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Total", "${allLetters.size}", TealPrimary)
                    }
                }
            }

            // Filter status chips
            item {
                val filters = listOf("Semua", "Diajukan", "Diproses", "Disetujui", "Ditolak")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { f ->
                        FilterChip(
                            selected = statusFilter == f,
                            onClick = { viewModel.updateLetterStatusFilter(f) },
                            label = { Text(f, fontSize = 12.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            if (letters.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = SlateTextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Belum ada pengajuan surat pengantar",
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                            Text(
                                text = "Klik tombol Ajukan Surat untuk membuat surat baru",
                                fontSize = 12.sp,
                                color = SlateTextMuted
                            )
                        }
                    }
                }
            } else {
                items(letters, key = { it.id }) { letter ->
                    LetterCardItem(
                        letter = letter,
                        onClick = { viewModel.openLetterPreview(letter) },
                        onApprove = { viewModel.updateLetterStatus(letter, "Disetujui") },
                        onProcess = { viewModel.updateLetterStatus(letter, "Diproses") },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Add Letter Request Dialog
    if (isAddingLetter) {
        AddLetterDialog(
            citizens = citizens,
            onDismiss = { viewModel.closeAddLetter() },
            onSubmit = { citizen, letterType, purpose, notes ->
                viewModel.submitLetterRequest(citizen, letterType, purpose, notes)
            }
        )
    }

    // Official Letter Preview Dialog
    previewLetter?.let { letter ->
        OfficialLetterPreviewDialog(
            letter = letter,
            viewModel = viewModel,
            onDismiss = { viewModel.closeLetterPreview() },
            onUpdateStatus = { newStatus, reason ->
                viewModel.updateLetterStatus(letter, newStatus, reason)
            }
        )
    }
}

@Composable
fun LetterCardItem(
    letter: LetterRequest,
    onClick: () -> Unit,
    onApprove: () -> Unit,
    onProcess: () -> Unit,
    viewModel: RtRwViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("letter_card_${letter.id}"),
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
                            .background(TealPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = letter.letterType,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "No: ${letter.letterNumber}",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }
                }

                StatusBadge(status = letter.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SlateBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pemohon: ${letter.citizenName} (NIK: ${letter.citizenNik})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Keperluan: ${letter.purpose}",
                    fontSize = 12.sp,
                    color = SlateTextSecondary,
                    lineHeight = 16.sp
                )
                Text(
                    text = "Diajukan pada: ${viewModel.formatDate(letter.requestDate)}",
                    fontSize = 10.sp,
                    color = SlateTextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lihat Format Surat", fontSize = 11.sp)
                }

                if (letter.status == "Diajukan") {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalButton(
                            onClick = onProcess,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Proses", fontSize = 11.sp)
                        }
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Setujui", fontSize = 11.sp)
                        }
                    }
                } else if (letter.status == "Diproses") {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Setujui Surat", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLetterDialog(
    citizens: List<Citizen>,
    onDismiss: () -> Unit,
    onSubmit: (Citizen, String, String, String) -> Unit
) {
    var selectedCitizen by remember { mutableStateOf(citizens.firstOrNull()) }
    var letterType by remember { mutableStateOf("Surat Pengantar SKCK") }
    var purpose by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var expandedCitizenDropdown by remember { mutableStateOf(false) }

    val letterTypes = listOf(
        "Surat Pengantar SKCK",
        "Surat Pengantar KTP / KK Baru",
        "Surat Keterangan Domisili",
        "Surat Keterangan Usaha (SKU)",
        "Surat Keterangan Belum Menikah",
        "Surat Keterangan Tidak Mampu (SKTM)",
        "Surat Izin Keramaian / Acara"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Ajukan Surat Pengantar RT", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

                // Citizen Selector
                Text(text = "Pilih Warga Pemohon *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                ExposedDropdownMenuBox(
                    expanded = expandedCitizenDropdown,
                    onExpandedChange = { expandedCitizenDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCitizen?.fullName ?: "Pilih Warga",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCitizenDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCitizenDropdown,
                        onDismissRequest = { expandedCitizenDropdown = false }
                    ) {
                        citizens.forEach { citizen ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = citizen.fullName, fontWeight = FontWeight.SemiBold)
                                        Text(text = "NIK: ${citizen.nik} • ${citizen.address}", fontSize = 10.sp, color = SlateTextMuted)
                                    }
                                },
                                onClick = {
                                    selectedCitizen = citizen
                                    expandedCitizenDropdown = false
                                }
                            )
                        }
                    }
                }

                // Letter Type Selection
                Text(text = "Jenis Surat Pengantar *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(letterTypes) { type ->
                        FilterChip(
                            selected = letterType == type,
                            onClick = { letterType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Keperluan / Tujuan Pembuatan Surat *") },
                    placeholder = { Text("Contoh: Persyaratan perpanjangan SKCK di Polsek") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan / Keterangan Tambahan") },
                    placeholder = { Text("Opsional: lampiran berkas, dll.") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val citizen = selectedCitizen
                    if (citizen == null) {
                        errorMessage = "Pilih warga pemohon terlebih dahulu."
                        return@Button
                    }
                    if (purpose.isBlank()) {
                        errorMessage = "Keperluan surat wajib diisi."
                        return@Button
                    }
                    onSubmit(citizen, letterType, purpose, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Kirim Pengajuan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun OfficialLetterPreviewDialog(
    letter: LetterRequest,
    viewModel: RtRwViewModel,
    onDismiss: () -> Unit,
    onUpdateStatus: (String, String?) -> Unit
) {
    val context = LocalContext.current
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }

    val letterFormattedText = remember(letter) {
        """
        RUKUN TETANGGA 02 / RUKUN WARGA 05
        KELURAHAN SUKAMAJU, KECAMATAN CILODONG
        KOTA DEPOK - PROVINSI JAWA BARAT
        ====================================================
        
        SURAT PENGANTAR RT/RW
        Nomor: ${letter.letterNumber}
        
        Yang bertanda tangan di bawah ini Ketua RT 02 / RW 05 Kelurahan Sukamaju, menerangkan bahwa:
        
        Nama Lengkap       : ${letter.citizenName}
        NIK                : ${letter.citizenNik}
        Alamat             : ${letter.citizenAddress}, RT ${letter.rt} / RW ${letter.rw}
        Jenis Surat        : ${letter.letterType}
        Keperluan          : ${letter.purpose}
        
        Adalah benar warga kami yang bertempat tinggal di alamat tersebut di atas dan berkelakuan baik. Surat pengantar ini diberikan untuk dipergunakan sebagaimana mestinya.
        
        Sukamaju, ${viewModel.formatShortDate(letter.approvedDate ?: letter.requestDate)}
        Ketua RT 02 / RW 05
        
        ( ${letter.approverName} )
        Status Surat: ${letter.status.uppercase()}
        """.trimIndent()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Surat Pengantar Resmi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                // Official Document Paper Style Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFDFD))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Official Letterhead (Kop Surat)
                        Text(
                            text = "PENGURUS RUKUN TETANGGA 02 / RW 05",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "KELURAHAN SUKAMAJU - KECAMATAN CILODONG",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "KOTA DEPOK, JAWA BARAT 16415",
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Divider(thickness = 2.dp, color = Color(0xFF0F172A))
                        Divider(thickness = 0.5.dp, color = Color(0xFF0F172A), modifier = Modifier.padding(top = 1.dp))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Title & Number
                        Text(
                            text = "SURAT PENGANTAR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Nomor: ${letter.letterNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Statement Body
                        Text(
                            text = "Yang bertanda tangan di bawah ini Ketua RT 02 / RW 05 Kelurahan Sukamaju, dengan ini menerangkan bahwa:",
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            lineHeight = 15.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Form Fields in Official Letter
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            LetterOfficialRow("Nama Lengkap", letter.citizenName)
                            LetterOfficialRow("NIK", letter.citizenNik)
                            LetterOfficialRow("Alamat", "${letter.citizenAddress}, RT ${letter.rt}/RW ${letter.rw}")
                            LetterOfficialRow("Perihal Surat", letter.letterType)
                            LetterOfficialRow("Keperluan", letter.purpose)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Adalah benar warga kami yang tercatat bertempat tinggal di lingkungan RT 02 / RW 05 dan surat pengantar ini dibuat untuk dipergunakan sebagaimana mestinya.",
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            lineHeight = 15.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stamp and Signature Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Digital Validation Stamp
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 1.5.dp,
                                        color = if (letter.status == "Disetujui") Color(0xFF15803D) else Color(0xFFD97706),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "RT 02 / RW 05",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (letter.status == "Disetujui") Color(0xFF15803D) else Color(0xFFD97706)
                                    )
                                    Text(
                                        text = if (letter.status == "Disetujui") "TERVALIDASI DIGITAL" else letter.status.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (letter.status == "Disetujui") Color(0xFF15803D) else Color(0xFFD97706)
                                    )
                                }
                            }

                            // Signer
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sukamaju, ${viewModel.formatShortDate(letter.approvedDate ?: letter.requestDate)}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "Ketua RT 02",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(28.dp))
                                Text(
                                    text = "( ${letter.approverName} )",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }

                // Quick Action Buttons for Letter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Surat Pengantar RT", letterFormattedText)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showMessage("Format teks surat telah disalin ke clipboard")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin Teks", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, letterFormattedText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Surat Pengantar RT")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bagikan", fontSize = 11.sp)
                    }
                }

                // Administrative Status Approval Buttons
                if (letter.status != "Disetujui") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tindakan Pengurus RT:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRejectDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tolak Surat")
                        }

                        Button(
                            onClick = { onUpdateStatus("Disetujui", null) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Setujui & Validasi")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Tolak Pengajuan Surat") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Berikan alasan penolakan untuk pemohon:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("Contoh: Berkas persyaratan belum lengkap") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStatus("Ditolak", rejectReason.ifBlank { "Persyaratan belum lengkap" })
                        showRejectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Konfirmasi Tolak")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun LetterOfficialRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.width(90.dp)
        )
        Text(text = ": ", fontSize = 11.sp, color = Color(0xFF64748B))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )
    }
}
