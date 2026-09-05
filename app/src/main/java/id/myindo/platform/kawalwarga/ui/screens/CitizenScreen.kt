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
import id.myindo.platform.kawalwarga.core.model.Role
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.ui.theme.*
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
    val activeContext by viewModel.activeContext.collectAsState()
    val myHousehold by viewModel.myHousehold.collectAsState()

    val selectedCitizen by viewModel.selectedCitizenForDetail.collectAsState()
    val isAddingCitizen by viewModel.isAddingCitizen.collectAsState()
    val editingCitizen by viewModel.editingCitizen.collectAsState()
    val isCorrectionOpen by viewModel.isCorrectionRequestOpen.collectAsState()

    val isPengurus = activeContext?.role == Role.KETUA_RT ||
            activeContext?.role == Role.KETUA_RW ||
            activeContext?.role == Role.SEKRETARIS

    val totalCitizens = allCitizens.size
    val totalKk = allCitizens.distinctBy { it.noKk }.size
    val totalTetap = allCitizens.count { it.residenceStatus == "Warga Tetap" }
    val totalKontrak = allCitizens.count { it.residenceStatus != "Warga Tetap" }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (isPengurus) {
                FloatingActionButton(
                    onClick = { viewModel.openAddCitizen() },
                    containerColor = TealPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_citizen_fab")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Tambah Warga")
                }
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
            // WARGA VIEW: Prominently display "Keluarga Saya" (Household)
            if (!isPengurus && myHousehold != null) {
                val household = myHousehold!!
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("my_household_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FamilyRestroom,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Keluarga Saya (Kartu Keluarga)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                TextButton(onClick = { viewModel.openCorrectionRequest() }) {
                                    Text("Koreksi Data", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No. KK: ${household.kkNumberMasked}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Alamat: ${household.address} (RT ${household.rt} / RW ${household.rw})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Anggota Keluarga Terdaftar (${household.members.size} Jiwa):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            household.members.forEach { member ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = member.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "NIK: ${member.nikMasked}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.White.copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = member.relation,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TealPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // PENGURUS VIEW: Demographic Summary stats
            if (isPengurus) {
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
            }

            // Search Bar & Scope Indicator
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateCitizenSearch(it) },
                    placeholder = { Text("Cari nama atau blok rumah...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateCitizenSearch("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citizen_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )
            }

            // Filter Chips for Pengurus
            if (isPengurus) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Semua RT", "RT 01", "RT 02", "RT 03").forEach { rt ->
                            FilterChip(
                                selected = rtFilter == rt,
                                onClick = { viewModel.updateCitizenRtFilter(rt) },
                                label = { Text(rt, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // Header Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isPengurus) "Daftar Warga RT/RW (Scoped)" else "Direktori Komunitas Lingkungan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${citizens.size} Orang",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Citizens List (Privacy Protected)
            items(citizens, key = { it.id }) { citizen ->
                CitizenCardItem(
                    citizen = citizen,
                    isPengurus = isPengurus,
                    onClick = {
                        if (isPengurus) {
                            viewModel.openCitizenDetail(citizen)
                        }
                    }
                )
            }
        }
    }

    // Detail Dialog for Authorized Pengurus
    selectedCitizen?.let { citizen ->
        AlertDialog(
            onDismissRequest = { viewModel.closeCitizenDetail() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Data Lengkap Warga", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailFieldRow("Nama Lengkap", citizen.fullName)
                    DetailFieldRow("NIK (Masked)", citizen.nik)
                    DetailFieldRow("No. KK", citizen.noKk)
                    DetailFieldRow("Peran Keluarga", citizen.familyRole)
                    DetailFieldRow("Alamat Rumah", citizen.address)
                    DetailFieldRow("Wilayah", "RT ${citizen.rt} / RW ${citizen.rw}")
                    DetailFieldRow("No. Telepon", citizen.phone)
                    DetailFieldRow("Status Tinggal", citizen.residenceStatus)
                    DetailFieldRow("Pekerjaan", citizen.occupation)
                    if (citizen.notes.isNotBlank()) {
                        DetailFieldRow("Catatan", citizen.notes)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeCitizenDetail() }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        viewModel.closeCitizenDetail()
                        viewModel.openEditCitizen(citizen)
                    }) {
                        Text("Edit Data", color = TealPrimary)
                    }
                }
            }
        )
    }

    // Koreksi Data Dialog for Warga
    if (isCorrectionOpen) {
        var fieldToCorrect by remember { mutableStateOf("Alamat / Blok Rumah") }
        var requestedVal by remember { mutableStateOf("") }
        var reason by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.closeCorrectionRequest() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Permohonan Koreksi Data", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Data kependudukan (NIK, KK, nama, alamat) dilindungi dan memerlukan verifikasi Pengurus RT sebelum diperbarui.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = fieldToCorrect,
                        onValueChange = { fieldToCorrect = it },
                        label = { Text("Bagian yang dikoreksi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = requestedVal,
                        onValueChange = { requestedVal = it },
                        label = { Text("Nilai yang benar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Alasan koreksi") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitCorrectionRequest(fieldToCorrect, requestedVal, reason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Kirim Permohonan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeCorrectionRequest() }) {
                    Text("Batal")
                }
            }
        )
    }

    // Form Dialog for Adding / Editing Citizen (Pengurus only)
    if (isAddingCitizen) {
        var fullName by remember { mutableStateOf(editingCitizen?.fullName ?: "") }
        var nik by remember { mutableStateOf(editingCitizen?.nik ?: "") }
        var noKk by remember { mutableStateOf(editingCitizen?.noKk ?: "") }
        var gender by remember { mutableStateOf(editingCitizen?.gender ?: "Laki-laki") }
        var birthPlace by remember { mutableStateOf(editingCitizen?.birthPlace ?: "") }
        var birthDate by remember { mutableStateOf(editingCitizen?.birthDate ?: "01/01/1990") }
        var religion by remember { mutableStateOf(editingCitizen?.religion ?: "Islam") }
        var familyRole by remember { mutableStateOf(editingCitizen?.familyRole ?: "Kepala Keluarga") }
        var address by remember { mutableStateOf(editingCitizen?.address ?: "") }
        var rt by remember { mutableStateOf(editingCitizen?.rt ?: "02") }
        var rw by remember { mutableStateOf(editingCitizen?.rw ?: "05") }
        var phone by remember { mutableStateOf(editingCitizen?.phone ?: "") }
        var occupation by remember { mutableStateOf(editingCitizen?.occupation ?: "") }
        var residenceStatus by remember { mutableStateOf(editingCitizen?.residenceStatus ?: "Warga Tetap") }

        AlertDialog(
            onDismissRequest = { viewModel.closeCitizenForm() },
            title = {
                Text(
                    text = if (editingCitizen == null) "Tambah Warga Baru" else "Edit Data Warga",
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
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nik,
                        onValueChange = { nik = it },
                        label = { Text("NIK (16 Digit)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = noKk,
                        onValueChange = { noKk = it },
                        label = { Text("Nomor KK (16 Digit)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Alamat Rumah / Blok") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("No. Handphone / WhatsApp") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = occupation,
                        onValueChange = { occupation = it },
                        label = { Text("Pekerjaan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fullName.isNotBlank()) {
                            val citizen = editingCitizen?.copy(
                                fullName = fullName,
                                nik = nik,
                                noKk = noKk,
                                address = address,
                                phone = phone,
                                occupation = occupation
                            ) ?: Citizen(
                                fullName = fullName,
                                nik = nik,
                                noKk = noKk,
                                gender = gender,
                                birthPlace = birthPlace,
                                birthDate = birthDate,
                                religion = religion,
                                familyRole = familyRole,
                                address = address,
                                rt = rt,
                                rw = rw,
                                phone = phone,
                                occupation = occupation,
                                residenceStatus = residenceStatus
                            )
                            viewModel.saveCitizen(citizen)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeCitizenForm() }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun CitizenCardItem(
    citizen: Citizen,
    isPengurus: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        .background(TealPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = citizen.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${citizen.address} · RT ${citizen.rt}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isPengurus) {
                        Text(
                            text = "NIK: ${citizen.nik}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (citizen.residenceStatus == "Warga Tetap") Color(0xFFE0F2F1) else Color(0xFFFFF3E0)
            ) {
                Text(
                    text = citizen.residenceStatus,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (citizen.residenceStatus == "Warga Tetap") TealPrimary else AmberTertiary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CitizenStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = color)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DetailFieldRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
