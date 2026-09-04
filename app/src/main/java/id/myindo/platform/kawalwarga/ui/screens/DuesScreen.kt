package id.myindo.platform.kawalwarga.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.myindo.platform.kawalwarga.data.model.Citizen
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
    val allDues by viewModel.allDues.collectAsState()
    val duesList by viewModel.filteredDues.collectAsState()
    val statusFilter by viewModel.duesStatusFilter.collectAsState()
    val monthFilter by viewModel.duesMonthFilter.collectAsState()
    val citizens by viewModel.allCitizens.collectAsState()

    val activePaymentDue by viewModel.activePaymentDue.collectAsState()
    val isAddingDues by viewModel.isAddingDuesRecord.collectAsState()
    val receiptPayment by viewModel.receiptPayment.collectAsState()

    val totalPaid = allDues.filter { it.paymentStatus == "Lunas" }.sumOf { it.totalAmount }
    val totalUnpaid = allDues.filter { it.paymentStatus == "Belum Bayar" }.sumOf { it.totalAmount }
    val lunasCount = allDues.count { it.paymentStatus == "Lunas" }
    val unpaidCount = allDues.count { it.paymentStatus == "Belum Bayar" }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddDuesRecord() },
                containerColor = EmeraldSecondary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AddCard, contentDescription = null) },
                text = { Text("Buat Tagihan") },
                modifier = Modifier.testTag("add_dues_fab")
            )
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
                        subtitle = "$lunasCount KK Sudah Lunas",
                        icon = Icons.Default.CheckCircle,
                        accentColor = EmeraldSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Belum Terbayar",
                        value = viewModel.formatRupiah(totalUnpaid),
                        subtitle = "$unpaidCount KK Menunggu",
                        icon = Icons.Default.PendingActions,
                        accentColor = AmberTertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fee Tariff Breakdown Info Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Komponen Iuran Warga Bulanan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TealPrimaryContainer
                            ) {
                                Text(
                                    text = "Total Rp 125.000 / bln",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TealOnPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FeeItemMini("Kebersihan", "Rp 35.000")
                            FeeItemMini("Keamanan", "Rp 50.000")
                            FeeItemMini("Kas RT", "Rp 25.000")
                            FeeItemMini("Sosial", "Rp 15.000")
                        }
                    }
                }
            }

            // Month & Status Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val months = listOf("Semua Bulan", "September", "Agustus", "Juli", "Oktober")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(months) { m ->
                            FilterChip(
                                selected = monthFilter == m,
                                onClick = { viewModel.updateDuesMonthFilter(m) },
                                label = { Text(m, fontSize = 11.sp) }
                            )
                        }
                    }

                    val statuses = listOf("Semua", "Belum Bayar", "Lunas")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        statuses.forEach { s ->
                            FilterChip(
                                selected = statusFilter == s,
                                onClick = { viewModel.updateDuesStatusFilter(s) },
                                label = { Text(s, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            if (duesList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = SlateTextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tidak ada catatan iuran",
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                            Text(
                                text = "Gunakan tombol Buat Tagihan untuk menambah data baru",
                                fontSize = 12.sp,
                                color = SlateTextMuted
                            )
                        }
                    }
                }
            } else {
                items(duesList, key = { it.id }) { due ->
                    DuesCardItem(
                        due = due,
                        viewModel = viewModel,
                        onPay = { viewModel.openPaymentFlow(due) },
                        onViewReceipt = { viewModel.openReceipt(due) }
                    )
                }
            }
        }
    }

    // Payment Flow Dialog (QRIS / Bank Transfer / Tunai)
    activePaymentDue?.let { due ->
        PaymentFlowDialog(
            due = due,
            viewModel = viewModel,
            onDismiss = { viewModel.closePaymentFlow() },
            onConfirm = { method ->
                viewModel.confirmPayment(due, method)
            }
        )
    }

    // Digital Kwitansi / Receipt Dialog
    receiptPayment?.let { due ->
        KwitansiReceiptDialog(
            due = due,
            viewModel = viewModel,
            onDismiss = { viewModel.closeReceipt() }
        )
    }

    // Add Dues Record Dialog
    if (isAddingDues) {
        AddDuesRecordDialog(
            citizens = citizens,
            onDismiss = { viewModel.closeAddDuesRecord() },
            onSubmit = { citizen, month, year, amountKebersihan, amountKeamanan, amountKasRt, amountSosial, status ->
                viewModel.createDuesInvoice(
                    citizen, month, year, amountKebersihan, amountKeamanan, amountKasRt, amountSosial, status
                )
            }
        )
    }
}

@Composable
fun FeeItemMini(title: String, price: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, color = SlateTextMuted)
        Text(text = price, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
    }
}

@Composable
fun DuesCardItem(
    due: DuesPayment,
    viewModel: RtRwViewModel,
    onPay: () -> Unit,
    onViewReceipt: () -> Unit
) {
    val isLunas = due.paymentStatus == "Lunas"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dues_card_${due.id}"),
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isLunas) EmeraldSecondaryContainer else AmberTertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLunas) Icons.Default.CheckCircle else Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = if (isLunas) EmeraldOnSecondaryContainer else AmberOnTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = due.citizenName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${due.houseNumber} • RT ${due.rt}",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }
                }

                StatusBadge(status = due.paymentStatus)
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
                    Text(
                        text = "Periode: ${due.month} ${due.year}",
                        fontSize = 11.sp,
                        color = SlateTextMuted
                    )
                    Text(
                        text = viewModel.formatRupiah(due.totalAmount),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (isLunas) EmeraldSecondary else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isLunas) {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kwitansi", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bayar Sekarang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFlowDialog(
    due: DuesPayment,
    viewModel: RtRwViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf("QRIS") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Pembayaran Iuran RT", fontWeight = FontWeight.Bold, fontSize = 17.sp)
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Billing overview card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = TealPrimaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Tagihan Warga: ${due.citizenName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TealOnPrimaryContainer)
                        Text(text = "Periode: ${due.month} ${due.year} (${due.houseNumber})", fontSize = 11.sp, color = SlateTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.formatRupiah(due.totalAmount),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = TealPrimary
                        )
                    }
                }

                Text(text = "Pilih Metode Pembayaran:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                val methods = listOf("QRIS", "Transfer BCA", "Transfer Mandiri", "Tunai")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(methods) { m ->
                        FilterChip(
                            selected = selectedMethod == m,
                            onClick = { selectedMethod = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                when (selectedMethod) {
                    "QRIS" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "QRIS RESMI RT 02 / RW 05",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFDC2626)
                                )
                                Text(
                                    text = "NMID: ID1029384756201 - SUKAMAJU",
                                    fontSize = 9.sp,
                                    color = SlateTextMuted
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Simulated QR code canvas graphic
                                QrCodeVisual(size = 140.dp)

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Scan QRIS menggunakan GoPay, OVO, Dana, ShopeePay, atau Mobile Banking apa saja.",
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }

                    "Transfer BCA" -> {
                        BankTransferCard(
                            bankName = "BANK CENTRAL ASIA (BCA)",
                            accountNumber = "8735 0912 3344",
                            accountName = "KAS RT 02 SUKAMAJU",
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("No Rekening BCA", "873509123344"))
                                viewModel.showMessage("Nomor rekening BCA disalin")
                            }
                        )
                    }

                    "Transfer Mandiri" -> {
                        BankTransferCard(
                            bankName = "BANK MANDIRI",
                            accountNumber = "156 00 9876 5432",
                            accountName = "KAS RT 02 RW 05",
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("No Rekening Mandiri", "1560098765432"))
                                viewModel.showMessage("Nomor rekening Mandiri disalin")
                            }
                        )
                    }

                    else -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = SlateSurfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Pembayaran Tunai / Langsung",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Serahkan uang tunai Rp 125.000 ke Bendahara RT (Ibu Hj. Siti Aminah / Blok A No. 01). Tekan konfirmasi untuk mencatat kwitansi lunas.",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Konfirmasi & Terbitkan Kwitansi Lunas")
            }
        },
        dismissButton = {}
    )
}

@Composable
fun BankTransferCard(
    bankName: String,
    accountNumber: String,
    accountName: String,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = bankName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TealPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = accountNumber, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                    Text(text = "a.n $accountName", fontSize = 11.sp, color = SlateTextMuted)
                }
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Salin Rekening", tint = TealPrimary)
                }
            }
        }
    }
}

@Composable
fun QrCodeVisual(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = this.size.width / 14f
            val darkColor = Color(0xFF0F172A)

            // Pattern corners
            drawRect(darkColor, Offset(0f, 0f), Size(step * 4, step * 4))
            drawRect(Color.White, Offset(step, step), Size(step * 2, step * 2))

            drawRect(darkColor, Offset(this.size.width - step * 4, 0f), Size(step * 4, step * 4))
            drawRect(Color.White, Offset(this.size.width - step * 3, step), Size(step * 2, step * 2))

            drawRect(darkColor, Offset(0f, this.size.height - step * 4), Size(step * 4, step * 4))
            drawRect(Color.White, Offset(step, this.size.height - step * 3), Size(step * 2, step * 2))

            // Random styled QR data blocks
            for (i in 5..9) {
                for (j in 1..12) {
                    if ((i + j) % 2 == 0 || (i * j) % 3 == 0) {
                        drawRect(darkColor, Offset(i * step, j * step), Size(step * 0.9f, step * 0.9f))
                    }
                }
            }
        }
        // Center RT Logo Icon
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(TealPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "RT", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}

@Composable
fun KwitansiReceiptDialog(
    due: DuesPayment,
    viewModel: RtRwViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val receiptText = remember(due) {
        """
        ====================================================
        BUKTI KWITANSI RESMI IURAN RT 02 / RW 05
        KELURAHAN SUKAMAJU - KECAMATAN CILODONG
        ====================================================
        Nomor Kwitansi : ${due.invoiceNumber}
        Waktu Bayar    : ${viewModel.formatDate(due.paymentDate ?: System.currentTimeMillis())}
        Warga / KK     : ${due.citizenName}
        Alamat         : ${due.houseNumber}
        Periode Iuran  : ${due.month} ${due.year}
        Metode Bayar   : ${due.paymentMethod}
        ----------------------------------------------------
        RINCIAN TAGIHAN:
        - Iuran Kebersihan & Sampah : ${viewModel.formatRupiah(due.amountKebersihan)}
        - Iuran Keamanan / Satpam   : ${viewModel.formatRupiah(due.amountKeamanan)}
        - Iuran Kas RT Lingkungan   : ${viewModel.formatRupiah(due.amountKasRt)}
        - Iuran Dana Sosial Warga   : ${viewModel.formatRupiah(due.amountSosial)}
        ----------------------------------------------------
        TOTAL LUNAS    : ${viewModel.formatRupiah(due.totalAmount)}
        STATUS         : [ LUNAS / TERVERIFIKASI ]
        Penerima       : ${due.collectorName}
        ====================================================
        Terima kasih atas partisipasi aktif Bapak/Ibu dalam menjaga kebersihan & keamanan lingkungan RT 02.
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
                Text(text = "Kwitansi Pembayaran Resmi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "KWITANSI RESMI IURAN RT 02 / RW 05",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "No: ${due.invoiceNumber}",
                            fontSize = 10.sp,
                            color = SlateTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(thickness = 1.dp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ReceiptRow("Telah terima dari", due.citizenName)
                            ReceiptRow("Alamat Rumah", due.houseNumber)
                            ReceiptRow("Periode Bulan", "${due.month} ${due.year}")
                            ReceiptRow("Metode Bayar", due.paymentMethod)
                            ReceiptRow("Tanggal Bayar", viewModel.formatDate(due.paymentDate ?: System.currentTimeMillis()))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(thickness = 0.5.dp, color = SlateBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            ReceiptAmountRow("1. Iuran Kebersihan & Sampah", viewModel.formatRupiah(due.amountKebersihan))
                            ReceiptAmountRow("2. Iuran Keamanan & Siskamling", viewModel.formatRupiah(due.amountKeamanan))
                            ReceiptAmountRow("3. Iuran Kas RT", viewModel.formatRupiah(due.amountKasRt))
                            ReceiptAmountRow("4. Iuran Sosial & Kematian", viewModel.formatRupiah(due.amountSosial))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(thickness = 1.dp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "TOTAL PEMBAYARAN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = viewModel.formatRupiah(due.totalAmount),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = EmeraldSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stamp Lunas & Bendahara Signature
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .border(2.dp, Color(0xFF15803D), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "LUNAS",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF15803D),
                                    letterSpacing = 2.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Bendahara RT 02", fontSize = 10.sp, color = SlateTextMuted)
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(text = "( ${due.collectorName} )", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Kwitansi Iuran RT", receiptText))
                            viewModel.showMessage("Teks kwitansi disalin")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Kwitansi Iuran RT")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bagikan", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.width(100.dp))
        Text(text = ": ", fontSize = 11.sp, color = SlateTextMuted)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
fun ReceiptAmountRow(label: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 10.sp, color = SlateTextSecondary)
        Text(text = amount, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDuesRecordDialog(
    citizens: List<Citizen>,
    onDismiss: () -> Unit,
    onSubmit: (Citizen, String, Int, Double, Double, Double, Double, String) -> Unit
) {
    var selectedCitizen by remember { mutableStateOf(citizens.firstOrNull()) }
    var month by remember { mutableStateOf("September") }
    var year by remember { mutableStateOf(2026) }
    var amountKebersihan by remember { mutableStateOf("35000") }
    var amountKeamanan by remember { mutableStateOf("50000") }
    var amountKasRt by remember { mutableStateOf("25000") }
    var amountSosial by remember { mutableStateOf("15000") }
    var status by remember { mutableStateOf("Belum Bayar") }
    var expandedCitizenDropdown by remember { mutableStateOf(false) }

    val months = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Buat Tagihan Iuran Warga", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Citizen selector
                Text(text = "Pilih Warga / KK *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                                text = { Text("${citizen.fullName} (${citizen.address})") },
                                onClick = {
                                    selectedCitizen = citizen
                                    expandedCitizenDropdown = false
                                }
                            )
                        }
                    }
                }

                Text(text = "Bulan Tagihan *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(months) { m ->
                        FilterChip(
                            selected = month == m,
                            onClick = { month = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountKebersihan,
                    onValueChange = { amountKebersihan = it },
                    label = { Text("Iuran Kebersihan (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountKeamanan,
                    onValueChange = { amountKeamanan = it },
                    label = { Text("Iuran Keamanan / Satpam (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountKasRt,
                    onValueChange = { amountKasRt = it },
                    label = { Text("Iuran Kas RT (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountSosial,
                    onValueChange = { amountSosial = it },
                    label = { Text("Iuran Sosial (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Status Awal *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Belum Bayar", "Lunas").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val citizen = selectedCitizen ?: return@Button
                    onSubmit(
                        citizen,
                        month,
                        year,
                        amountKebersihan.toDoubleOrNull() ?: 35000.0,
                        amountKeamanan.toDoubleOrNull() ?: 50000.0,
                        amountKasRt.toDoubleOrNull() ?: 25000.0,
                        amountSosial.toDoubleOrNull() ?: 15000.0,
                        status
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
            ) {
                Text("Simpan Tagihan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
