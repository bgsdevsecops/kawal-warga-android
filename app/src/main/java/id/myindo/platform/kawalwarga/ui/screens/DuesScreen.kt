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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.core.model.Role
import id.myindo.platform.kawalwarga.data.model.DuesPayment
import id.myindo.platform.kawalwarga.ui.components.MetricStatCard
import id.myindo.platform.kawalwarga.ui.components.StatusBadge
import id.myindo.platform.kawalwarga.ui.theme.*
import id.myindo.platform.kawalwarga.ui.viewmodel.RtRwViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuesScreen(
    viewModel: RtRwViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDues by viewModel.allDues.collectAsState()
    val duesList by viewModel.filteredDues.collectAsState()
    val statusFilter by viewModel.duesStatusFilter.collectAsState()
    val monthFilter by viewModel.duesMonthFilter.collectAsState()
    val activeContext by viewModel.activeContext.collectAsState()

    val activePaymentDue by viewModel.activePaymentDue.collectAsState()
    val isAddingDues by viewModel.isAddingDuesRecord.collectAsState()
    val receiptPayment by viewModel.receiptPayment.collectAsState()

    val isBendahara = activeContext?.role == Role.BENDAHARA || activeContext?.role == Role.KETUA_RT

    val totalPaid = allDues.filter { it.paymentStatus == "Lunas" }.sumOf { it.totalAmount }
    val totalUnpaid = allDues.filter { it.paymentStatus == "Belum Bayar" }.sumOf { it.totalAmount }
    val pendingVerifCount = allDues.count { it.paymentStatus == "Menunggu Verifikasi" }
    val lunasCount = allDues.count { it.paymentStatus == "Lunas" }

    var verificationTarget by remember { mutableStateOf<DuesPayment?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (isBendahara) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.openAddDuesRecord() },
                    containerColor = EmeraldSecondary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.AddCard, contentDescription = null) },
                    text = { Text("Catat Tunai") },
                    modifier = Modifier.testTag("add_dues_fab")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("dues_list_view"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Kas RT Financial Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Kas Terkumpul",
                        value = viewModel.formatRupiah(totalPaid),
                        subtitle = "$lunasCount KK Lunas",
                        icon = Icons.Default.CheckCircle,
                        accentColor = EmeraldSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = if (isBendahara) "Antrean Verif" else "Tunggakan",
                        value = if (isBendahara) "$pendingVerifCount Bukti" else viewModel.formatRupiah(totalUnpaid),
                        subtitle = if (isBendahara) "Perlu dicek" else "Belum dibayar",
                        icon = if (isBendahara) Icons.Default.PendingActions else Icons.Default.Warning,
                        accentColor = if (isBendahara) Color(0xFFF57C00) else Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Filters Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Semua", "Belum Bayar", "Menunggu Verifikasi", "Lunas").forEach { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { viewModel.updateDuesStatusFilter(status) },
                            label = { Text(status, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Dues list items
            items(duesList, key = { it.id }) { due ->
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
                            StatusBadge(status = due.paymentStatus)
                            Text(
                                text = "Periode ${due.month} ${due.year}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = due.citizenName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Rumah: ${due.houseNumber} · RT ${due.rt}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = viewModel.formatRupiah(due.totalAmount),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Breakdown components
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text("Keamanan: 50rb", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Kebersihan: 35rb", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Kas RT: 25rb", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Sosial: 15rb", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Actions for Warga / Bendahara
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (due.paymentStatus == "Lunas") {
                                OutlinedButton(
                                    onClick = { viewModel.openReceipt(due) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lihat Kwitansi", fontSize = 12.sp)
                                }
                            } else if (due.paymentStatus == "Menunggu Verifikasi") {
                                if (isBendahara) {
                                    Button(
                                        onClick = { verificationTarget = due },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verifikasi Bukti", fontSize = 12.sp)
                                    }
                                } else {
                                    Text(
                                        text = "Bukti sedang ditinjau Bendahara",
                                        fontSize = 12.sp,
                                        color = Color(0xFFF57C00),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                // Belum Bayar
                                Button(
                                    onClick = { viewModel.openPaymentFlow(due) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Bayar Tagihan", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Pembayaran & Unggah Bukti Transfer Resmi
    activePaymentDue?.let { due ->
        var selectedBank by remember { mutableStateOf("BCA") }
        val bankAccounts = mapOf(
            "BCA" to "7120-1928-33 (a.n. Kas Paguyuban RT 02)",
            "Mandiri" to "133-00-9876543-2 (a.n. Bendahara RT)",
            "BRI" to "0341-01-002345-50-8 (a.n. RT 02 RW 05)",
            "Bank DKI" to "101-23-45678-9 (a.n. Kas Paguyuban Warga)"
        )

        AlertDialog(
            onDismissRequest = { viewModel.closePaymentFlow() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = EmeraldSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pembayaran Iuran RT", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                        text = "Total tagihan: ${viewModel.formatRupiah(due.totalAmount)} (${due.month} ${due.year})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Transfer ke rekening resmi RT/RW dan unggah konfirmasi bukti transfer:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text("Pilih Rekening Tujuan:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    bankAccounts.forEach { (bank, acc) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedBank = bank }
                        ) {
                            RadioButton(selected = selectedBank == bank, onClick = { selectedBank = bank })
                            Column {
                                Text(bank, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(acc, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("No. Rekening $selectedBank", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    text = bankAccounts[selectedBank]?.substringBefore(" (") ?: "",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            TextButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("No Rekening", bankAccounts[selectedBank]?.substringBefore(" (") ?: "")
                                clipboard.setPrimaryClip(clip)
                                viewModel.showMessage("Nomor rekening $selectedBank berhasil disalin")
                            }) {
                                Text("Salin", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.uploadPaymentProof(due, "Transfer Bank ($selectedBank)")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
                ) {
                    Text("Konfirmasi Bukti Transfer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closePaymentFlow() }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Verifikasi Bukti Transfer oleh Bendahara
    verificationTarget?.let { due ->
        AlertDialog(
            onDismissRequest = { verificationTarget = null },
            title = {
                Text("Verifikasi Bukti Pembayaran", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Warga: ${due.citizenName} (${due.houseNumber})", fontWeight = FontWeight.Bold)
                    Text("Nominal: ${viewModel.formatRupiah(due.totalAmount)}")
                    Text("Metode: ${due.paymentMethod}")
                    Text("Tanggal Upload: ${viewModel.formatDate(due.paymentDate ?: System.currentTimeMillis())}")
                    Text("Apakah bukti transfer telah sesuai dan dana telah masuk ke rekening kas RT?", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyPayment(due, isApproved = true)
                        verificationTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
                ) {
                    Text("Verifikasi LUNAS", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        viewModel.verifyPayment(due, isApproved = false)
                        verificationTarget = null
                    }) {
                        Text("Tolak Bukti", color = Color(0xFFD32F2F))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = { verificationTarget = null }) {
                        Text("Batal")
                    }
                }
            }
        )
    }

    // Modal Catat Pembayaran Tunai (Bendahara Quick Entry)
    if (isAddingDues) {
        var citizenName by remember { mutableStateOf("") }
        var houseNumber by remember { mutableStateOf("") }
        var rtNumber by remember { mutableStateOf("02") }
        var month by remember { mutableStateOf("September") }
        var amountText by remember { mutableStateOf("125000") }

        AlertDialog(
            onDismissRequest = { viewModel.closeAddDuesRecord() },
            title = {
                Text("Catat Pembayaran Tunai (Cash)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = citizenName,
                        onValueChange = { citizenName = it },
                        label = { Text("Nama Warga *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = houseNumber,
                        onValueChange = { houseNumber = it },
                        label = { Text("No. Rumah / Blok *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it },
                        label = { Text("Bulan Pembayaran") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Nominal (Rp) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (citizenName.isNotBlank() && houseNumber.isNotBlank()) {
                            val amt = amountText.toDoubleOrNull() ?: 125000.0
                            viewModel.recordCashPayment(
                                citizenName = citizenName,
                                houseNumber = houseNumber,
                                rt = rtNumber,
                                month = month,
                                year = 2026,
                                amount = amt
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
                ) {
                    Text("Catat LUNAS & Terbitkan Kwitansi", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddDuesRecord() }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Digital Kwitansi
    receiptPayment?.let { due ->
        AlertDialog(
            onDismissRequest = { viewModel.closeReceipt() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = EmeraldSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kwitansi Pembayaran Resmi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .background(Color(0xFFFAFAFA))
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("PAGUYUBAN RUKUN TETANGGA 02 / RW 05", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            Text("TANDA BUKTI PEMBAYARAN IURAN", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.Black)
                            Text("No: ${due.invoiceNumber}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                    DetailFieldRow("Telah Terima Dari", due.citizenName)
                    DetailFieldRow("Alamat / Rumah", "${due.houseNumber} · RT ${due.rt}")
                    DetailFieldRow("Untuk Pembayaran", "Iuran Warga Periode ${due.month} ${due.year}")
                    DetailFieldRow("Jumlah Nominal", viewModel.formatRupiah(due.totalAmount))
                    DetailFieldRow("Metode Pembayaran", due.paymentMethod)
                    DetailFieldRow("Tanggal Lunas", viewModel.formatDate(due.paymentDate ?: System.currentTimeMillis()))
                    DetailFieldRow("Diverifikasi Oleh", due.collectorName)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeReceipt() }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
