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
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.ui.theme.*
import id.myindo.platform.kawalwarga.ui.viewmodel.MainTab
import id.myindo.platform.kawalwarga.ui.viewmodel.RtRwViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenScreen(
    viewModel: RtRwViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allCitizens by viewModel.allCitizens.collectAsState()
    val citizens by viewModel.filteredCitizens.collectAsState()
    val searchQuery by viewModel.citizenSearchQuery.collectAsState()
    val rtFilter by viewModel.citizenRtFilter.collectAsState()
    val statusFilter by viewModel.citizenStatusFilter.collectAsState()

    val selectedCitizen by viewModel.selectedCitizenForDetail.collectAsState()
    val isAddingCitizen by viewModel.isAddingCitizen.collectAsState()
    val editingCitizen by viewModel.editingCitizen.collectAsState()

    val totalCitizens = allCitizens.size
    val totalKk = allCitizens.distinctBy { it.noKk }.size
    val totalTetap = allCitizens.count { it.residenceStatus == "Warga Tetap" }
    val totalKontrak = allCitizens.count { it.residenceStatus != "Warga Tetap" }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddCitizen() },
                containerColor = TealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_citizen_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Tambah Warga")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("citizen_list_view"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Chips Row
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
                        CitizenStatItem("Total Warga", "$totalCitizens Jiwa", TealPrimary)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Total KK", "$totalKk KK", EmeraldSecondary)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Tetap", "$totalTetap", Color(0xFF0284C7))
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        CitizenStatItem("Kontrak/Kos", "$totalKontrak", AmberTertiary)
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateCitizenSearch(it) },
                    placeholder = { Text("Cari nama, NIK, alamat, atau no HP...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateTextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateCitizenSearch("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citizen_search_input"),
                    singleLine = true
                )
            }

            // Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // RT Filter Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val rtOptions = listOf("Semua RT", "RT 01", "RT 02", "RT 03")
                        items(rtOptions) { option ->
                            FilterChip(
                                selected = rtFilter == option,
                                onClick = { viewModel.updateCitizenRtFilter(option) },
                                label = { Text(option, fontSize = 12.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Status Filter Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statusOptions = listOf("Semua Status", "Warga Tetap", "Kontrak", "Kos")
                        items(statusOptions) { status ->
                            FilterChip(
                                selected = statusFilter == status,
                                onClick = { viewModel.updateCitizenStatusFilter(status) },
                                label = { Text(status, fontSize = 12.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // Citizen Cards
            if (citizens.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PersonSearch,
                                contentDescription = null,
                                tint = SlateTextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tidak ada data warga ditemukan",
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                            Text(
                                text = "Coba ubah kata kunci atau filter pencarian",
                                fontSize = 12.sp,
                                color = SlateTextMuted
                            )
                        }
                    }
                }
            } else {
                items(citizens, key = { it.id }) { citizen ->
                    CitizenCardItem(
                        citizen = citizen,
                        onClick = { viewModel.openCitizenDetail(citizen) },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${citizen.phone}"))
                            context.startActivity(intent)
                        },
                        onAddLetter = {
                            viewModel.setTab(MainTab.SURAT)
                            viewModel.openAddLetter(citizen)
                        }
                    )
                }
            }
        }
    }

    // Citizen Detail Dialog
    selectedCitizen?.let { citizen ->
        CitizenDetailDialog(
            citizen = citizen,
            allCitizens = allCitizens,
            onDismiss = { viewModel.closeCitizenDetail() },
            onEdit = {
                viewModel.closeCitizenDetail()
                viewModel.openEditCitizen(citizen)
            },
            onDelete = { viewModel.deleteCitizen(citizen) },
            onCreateLetter = {
                viewModel.closeCitizenDetail()
                viewModel.setTab(MainTab.SURAT)
                viewModel.openAddLetter(citizen)
            }
        )
    }

    // Citizen Form Dialog (Add / Edit)
    if (isAddingCitizen) {
        CitizenFormDialog(
            initialCitizen = editingCitizen,
            onDismiss = { viewModel.closeCitizenForm() },
            onSave = { viewModel.saveCitizen(it) }
        )
    }
}

@Composable
fun CitizenStatItem(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, color = SlateTextMuted, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
}

@Composable
fun CitizenCardItem(
    citizen: Citizen,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onAddLetter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("citizen_card_${citizen.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar initial
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (citizen.gender == "Laki-laki") Color(0xFFE0F2FE) else Color(0xFFFCE7F3)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = citizen.fullName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (citizen.gender == "Laki-laki") Color(0xFF0369A1) else Color(0xFFBE185D)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = citizen.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "NIK: ${citizen.nik}",
                        fontSize = 11.sp,
                        color = SlateTextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (citizen.residenceStatus) {
                        "Warga Tetap" -> Color(0xFFDCFCE7)
                        "Kontrak" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFE0E7FF)
                    }
                ) {
                    Text(
                        text = citizen.residenceStatus,
                        color = when (citizen.residenceStatus) {
                            "Warga Tetap" -> Color(0xFF15803D)
                            "Kontrak" -> Color(0xFFB45309)
                            else -> Color(0xFF4338CA)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SlateBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = SlateTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${citizen.address} (RT ${citizen.rt} / RW ${citizen.rw})",
                            fontSize = 11.sp,
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = SlateTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${citizen.familyRole} • ${citizen.occupation}",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalIconButton(
                        onClick = onCall,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Telepon",
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    FilledTonalIconButton(
                        onClick = onAddLetter,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAdd,
                            contentDescription = "Buat Surat",
                            tint = EmeraldSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CitizenDetailDialog(
    citizen: Citizen,
    allCitizens: List<Citizen>,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCreateLetter: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val familyMembers = allCitizens.filter { it.noKk == citizen.noKk && it.id != citizen.id }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Detail Data Warga", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                // Main Header Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TealPrimaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = citizen.fullName,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = TealOnPrimaryContainer
                        )
                        Text(
                            text = "${citizen.familyRole} • Status: ${citizen.residenceStatus}",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                DetailRow("NIK", citizen.nik)
                DetailRow("No Kartu Keluarga (KK)", citizen.noKk)
                DetailRow("Jenis Kelamin", citizen.gender)
                DetailRow("Tempat, Tanggal Lahir", "${citizen.birthPlace}, ${citizen.birthDate}")
                DetailRow("Agama", citizen.religion)
                DetailRow("Pekerjaan", citizen.occupation)
                DetailRow("Alamat Lengkap", "${citizen.address}, RT ${citizen.rt} / RW ${citizen.rw}")
                DetailRow("No Telepon / WA", citizen.phone)
                if (citizen.emergencyContact.isNotBlank()) {
                    DetailRow("Kontak Darurat", citizen.emergencyContact)
                }
                if (citizen.notes.isNotBlank()) {
                    DetailRow("Catatan Khusus", citizen.notes)
                }

                // Family Members in same KK
                if (familyMembers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Anggota Keluarga (KK: ${citizen.noKk})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    familyMembers.forEach { member ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = SlateSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = member.fullName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text(text = "NIK: ${member.nik}", fontSize = 10.sp, color = SlateTextMuted)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White
                                ) {
                                    Text(
                                        text = member.familyRole,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus")
                }
                FilledTonalButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                Button(
                    onClick = onCreateLetter,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Surat")
                }
            }
        },
        dismissButton = {}
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Konfirmasi Hapus Warga") },
            text = { Text("Apakah Anda yakin ingin menghapus data warga ${citizen.fullName}? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = label, fontSize = 12.sp, color = SlateTextMuted, modifier = Modifier.weight(0.4f))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenFormDialog(
    initialCitizen: Citizen?,
    onDismiss: () -> Unit,
    onSave: (Citizen) -> Unit
) {
    var fullName by remember { mutableStateOf(initialCitizen?.fullName ?: "") }
    var nik by remember { mutableStateOf(initialCitizen?.nik ?: "") }
    var noKk by remember { mutableStateOf(initialCitizen?.noKk ?: "") }
    var gender by remember { mutableStateOf(initialCitizen?.gender ?: "Laki-laki") }
    var birthPlace by remember { mutableStateOf(initialCitizen?.birthPlace ?: "") }
    var birthDate by remember { mutableStateOf(initialCitizen?.birthDate ?: "") }
    var religion by remember { mutableStateOf(initialCitizen?.religion ?: "Islam") }
    var familyRole by remember { mutableStateOf(initialCitizen?.familyRole ?: "Kepala Keluarga") }
    var address by remember { mutableStateOf(initialCitizen?.address ?: "") }
    var rt by remember { mutableStateOf(initialCitizen?.rt ?: "02") }
    var rw by remember { mutableStateOf(initialCitizen?.rw ?: "05") }
    var phone by remember { mutableStateOf(initialCitizen?.phone ?: "") }
    var occupation by remember { mutableStateOf(initialCitizen?.occupation ?: "") }
    var residenceStatus by remember { mutableStateOf(initialCitizen?.residenceStatus ?: "Warga Tetap") }
    var emergencyContact by remember { mutableStateOf(initialCitizen?.emergencyContact ?: "") }
    var notes by remember { mutableStateOf(initialCitizen?.notes ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialCitizen == null) "Tambah Warga Baru" else "Edit Data Warga",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
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
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama Lengkap *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = nik,
                    onValueChange = { if (it.length <= 16) nik = it },
                    label = { Text("NIK (16 Digit) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("${nik.length}/16 digit") }
                )

                OutlinedTextField(
                    value = noKk,
                    onValueChange = { if (it.length <= 16) noKk = it },
                    label = { Text("Nomor Kartu Keluarga (KK) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("${noKk.length}/16 digit") }
                )

                Text(text = "Jenis Kelamin *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Laki-laki", "Perempuan").forEach { g ->
                        FilterChip(
                            selected = gender == g,
                            onClick = { gender = g },
                            label = { Text(g) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = birthPlace,
                        onValueChange = { birthPlace = it },
                        label = { Text("Tempat Lahir") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = { Text("Tgl Lahir (DD/MM/YYYY)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(text = "Status dalam Keluarga *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                val familyRoles = listOf("Kepala Keluarga", "Istri", "Anak", "Orang Tua", "Famili Lain")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(familyRoles) { role ->
                        FilterChip(
                            selected = familyRole == role,
                            onClick = { familyRole = role },
                            label = { Text(role, fontSize = 11.sp) }
                        )
                    }
                }

                Text(text = "Status Tempat Tinggal *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                val residenceStatuses = listOf("Warga Tetap", "Kontrak", "Kos")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    residenceStatuses.forEach { status ->
                        FilterChip(
                            selected = residenceStatus == status,
                            onClick = { residenceStatus = status },
                            label = { Text(status) }
                        )
                    }
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat / Blok / No. Rumah *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = rt,
                        onValueChange = { rt = it },
                        label = { Text("RT") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = rw,
                        onValueChange = { rw = it },
                        label = { Text("RW") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. Telepon / WhatsApp *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = { Text("Pekerjaan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Kontak Darurat (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        errorMessage = "Nama lengkap wajib diisi."
                        return@Button
                    }
                    if (nik.length < 16) {
                        errorMessage = "NIK harus berjumlah 16 digit."
                        return@Button
                    }
                    if (noKk.length < 16) {
                        errorMessage = "Nomor KK harus berjumlah 16 digit."
                        return@Button
                    }
                    if (address.isBlank()) {
                        errorMessage = "Alamat rumah wajib diisi."
                        return@Button
                    }
                    if (phone.isBlank()) {
                        errorMessage = "Nomor telepon/WA wajib diisi."
                        return@Button
                    }

                    val citizen = Citizen(
                        id = initialCitizen?.id ?: 0L,
                        nik = nik,
                        noKk = noKk,
                        fullName = fullName,
                        gender = gender,
                        birthPlace = birthPlace.ifBlank { "Jakarta" },
                        birthDate = birthDate.ifBlank { "01/01/1990" },
                        religion = religion,
                        familyRole = familyRole,
                        address = address,
                        rt = rt.ifBlank { "02" },
                        rw = rw.ifBlank { "05" },
                        phone = phone,
                        occupation = occupation.ifBlank { "Wiraswasta" },
                        residenceStatus = residenceStatus,
                        emergencyContact = emergencyContact,
                        notes = notes,
                        createdAt = initialCitizen?.createdAt ?: System.currentTimeMillis()
                    )
                    onSave(citizen)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(if (initialCitizen == null) "Simpan Warga" else "Update Data")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
