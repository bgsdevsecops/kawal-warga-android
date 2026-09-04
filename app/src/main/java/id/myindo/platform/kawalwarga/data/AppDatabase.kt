package id.myindo.platform.kawalwarga.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import id.myindo.platform.kawalwarga.data.dao.AnnouncementDao
import id.myindo.platform.kawalwarga.data.dao.CitizenDao
import id.myindo.platform.kawalwarga.data.dao.DuesPaymentDao
import id.myindo.platform.kawalwarga.data.dao.LetterRequestDao
import id.myindo.platform.kawalwarga.data.dao.SecurityReportDao
import id.myindo.platform.kawalwarga.data.model.Announcement
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.data.model.DuesPayment
import id.myindo.platform.kawalwarga.data.model.LetterRequest
import id.myindo.platform.kawalwarga.data.model.SecurityReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Citizen::class,
        LetterRequest::class,
        SecurityReport::class,
        DuesPayment::class,
        Announcement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun citizenDao(): CitizenDao
    abstract fun letterRequestDao(): LetterRequestDao
    abstract fun securityReportDao(): SecurityReportDao
    abstract fun duesPaymentDao(): DuesPaymentDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rtrw_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val citizenDao = db.citizenDao()
            val letterDao = db.letterRequestDao()
            val reportDao = db.securityReportDao()
            val duesDao = db.duesPaymentDao()
            val announcementDao = db.announcementDao()

            val initialCitizens = listOf(
                Citizen(
                    nik = "3275011504820001",
                    noKk = "3275010901120005",
                    fullName = "H. Sutrisno Wibowo",
                    gender = "Laki-laki",
                    birthPlace = "Surabaya",
                    birthDate = "15/04/1982",
                    religion = "Islam",
                    familyRole = "Kepala Keluarga",
                    address = "Jl. Mawar No. 01, Blok A",
                    rt = "02",
                    rw = "05",
                    phone = "081287654321",
                    occupation = "PNS / Ketua RT 02",
                    residenceStatus = "Warga Tetap",
                    emergencyContact = "081299887766 (Istri)",
                    notes = "Ketua RT 02 Periode 2024-2027"
                ),
                Citizen(
                    nik = "3275015208850002",
                    noKk = "3275010901120005",
                    fullName = "Hj. Siti Aminah",
                    gender = "Perempuan",
                    birthPlace = "Bandung",
                    birthDate = "12/08/1985",
                    religion = "Islam",
                    familyRole = "Istri",
                    address = "Jl. Mawar No. 01, Blok A",
                    rt = "02",
                    rw = "05",
                    phone = "081299887766",
                    occupation = "Guru SMA / Bendahara RT",
                    residenceStatus = "Warga Tetap",
                    emergencyContact = "081287654321 (Suami)"
                ),
                Citizen(
                    nik = "3275011903900003",
                    noKk = "3275011203150008",
                    fullName = "Budi Santoso, S.Kom",
                    gender = "Laki-laki",
                    birthPlace = "Jakarta",
                    birthDate = "19/03/1990",
                    religion = "Islam",
                    familyRole = "Kepala Keluarga",
                    address = "Jl. Melati No. 08, Blok B",
                    rt = "02",
                    rw = "05",
                    phone = "081311223344",
                    occupation = "Software Engineer",
                    residenceStatus = "Warga Tetap",
                    emergencyContact = "081377889900 (Istri)"
                ),
                Citizen(
                    nik = "3275016509920004",
                    noKk = "3275011203150008",
                    fullName = "Rina Ratnasari, S.Farm",
                    gender = "Perempuan",
                    birthPlace = "Bogor",
                    birthDate = "25/09/1992",
                    religion = "Islam",
                    familyRole = "Istri",
                    address = "Jl. Melati No. 08, Blok B",
                    rt = "02",
                    rw = "05",
                    phone = "081377889900",
                    occupation = "Apoteker",
                    residenceStatus = "Warga Tetap",
                    emergencyContact = "081311223344"
                ),
                Citizen(
                    nik = "3275011011950007",
                    noKk = "3275012011950011",
                    fullName = "Dimas Arya Pratama",
                    gender = "Laki-laki",
                    birthPlace = "Yogyakarta",
                    birthDate = "10/11/1995",
                    religion = "Islam",
                    familyRole = "Kepala Keluarga",
                    address = "Jl. Kenanga No. 14, Blok C (Kos Ibu Maryati)",
                    rt = "02",
                    rw = "05",
                    phone = "085712345678",
                    occupation = "Wiraswasta / Desainer Grafis",
                    residenceStatus = "Kos",
                    emergencyContact = "081234445555 (Ibu Maryati)"
                ),
                Citizen(
                    nik = "3275012206880009",
                    noKk = "3275010408100019",
                    fullName = "Agus Prasetyo",
                    gender = "Laki-laki",
                    birthPlace = "Semarang",
                    birthDate = "22/06/1988",
                    religion = "Kristen",
                    familyRole = "Kepala Keluarga",
                    address = "Jl. Anggrek No. 05, Blok D",
                    rt = "01",
                    rw = "05",
                    phone = "081822334455",
                    occupation = "Manajer Operasional",
                    residenceStatus = "Kontrak",
                    emergencyContact = "081899001122 (Istri)"
                )
            )
            citizenDao.insertCitizens(initialCitizens)

            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            val initialLetters = listOf(
                LetterRequest(
                    letterNumber = "014/SP-RT02/RW05/IX/2026",
                    citizenId = 3,
                    citizenName = "Budi Santoso, S.Kom",
                    citizenNik = "3275011903900003",
                    citizenAddress = "Jl. Melati No. 08, Blok B",
                    rt = "02",
                    rw = "05",
                    letterType = "Surat Pengantar SKCK",
                    purpose = "Persyaratan berkas perpanjangan kontrak kerja instansi BUMN",
                    notes = "Mohon diproses untuk pengajuan ke Polsek Sukamaju",
                    status = "Disetujui",
                    requestDate = now - (2 * dayMs),
                    processedDate = now - (1 * dayMs),
                    approvedDate = now - (12 * 3600000L),
                    approverName = "H. Sutrisno Wibowo (Ketua RT 02)"
                ),
                LetterRequest(
                    letterNumber = "015/SP-RT02/RW05/IX/2026",
                    citizenId = 5,
                    citizenName = "Dimas Arya Pratama",
                    citizenNik = "3275011011950007",
                    citizenAddress = "Jl. Kenanga No. 14, Blok C",
                    rt = "02",
                    rw = "05",
                    letterType = "Surat Keterangan Domisili",
                    purpose = "Pembukaan rekening tabungan Bank Mandiri dan kelengkapan domisili kos",
                    notes = "Membawa surat pengantar dari pemilik kos Ibu Maryati",
                    status = "Diproses",
                    requestDate = now - (1 * dayMs),
                    processedDate = now - (6 * 3600000L),
                    approverName = "H. Sutrisno Wibowo (Ketua RT 02)"
                ),
                LetterRequest(
                    letterNumber = "DRAF",
                    citizenId = 6,
                    citizenName = "Agus Prasetyo",
                    citizenNik = "3275012206880009",
                    citizenAddress = "Jl. Anggrek No. 05, Blok D",
                    rt = "01",
                    rw = "05",
                    letterType = "Surat Keterangan Usaha (SKU)",
                    purpose = "Pengajuan pinjaman KUR BRI untuk pengembangan toko kelontong warga",
                    notes = "Lampiran foto usaha toko sudah disiapkan",
                    status = "Diajukan",
                    requestDate = now - (3 * 3600000L),
                    approverName = "H. Sutrisno Wibowo (Ketua RT 02)"
                )
            )
            letterDao.insertLetterRequests(initialLetters)

            val initialReports = listOf(
                SecurityReport(
                    reporterName = "Budi Santoso",
                    reporterPhone = "081311223344",
                    category = "Penerangan / Lampu Padam",
                    urgency = "Normal",
                    location = "Tiang Listrik Gang Melati Blok B dekat Pos Ronda",
                    description = "Lampu penerangan jalan umum (PJU) mati sudah 2 hari, kondisi jalan sangat gelap saat malam hari.",
                    status = "Sedang Ditangani",
                    responseNote = "Sudah dilaporkan ke petugas PLN & seksi sarpras RT, bohlam pengganti sedang dipasang.",
                    timestamp = now - (1 * dayMs)
                ),
                SecurityReport(
                    reporterName = "Dimas Arya Pratama",
                    reporterPhone = "085712345678",
                    category = "Keamanan / Kriminal",
                    urgency = "Penting",
                    location = "Depan Gang Kenanga Blok C",
                    description = "Terlihat 2 orang mencurigakan mondar-mandir menggunakan motor tanpa plat nomor pukul 23.30 WIB.",
                    status = "Selesai",
                    responseNote = "Petugas ronda malam (Pak Bambang dkk) sudah mengecek lokasi dan patroli keliling, situasi aman terkendali.",
                    timestamp = now - (2 * dayMs)
                ),
                SecurityReport(
                    reporterName = "Hj. Siti Aminah",
                    reporterPhone = "081299887766",
                    category = "Sampah / Kebersihan",
                    urgency = "Normal",
                    location = "Tanah Kosong Ujung Blok A",
                    description = "Ada tumpukan ranting pohon dan sampah plastik bekas kerja bakti yang belum terangkut truk kebersihan.",
                    status = "Menunggu Respon",
                    responseNote = null,
                    timestamp = now - (4 * 3600000L)
                )
            )
            reportDao.insertReports(initialReports)

            val initialDues = listOf(
                DuesPayment(
                    invoiceNumber = "INV-202609-001",
                    citizenId = 1,
                    citizenName = "H. Sutrisno Wibowo",
                    houseNumber = "Blok A No. 01",
                    rt = "02",
                    month = "September",
                    year = 2026,
                    amountKebersihan = 35000.0,
                    amountKeamanan = 50000.0,
                    amountKasRt = 25000.0,
                    amountSosial = 15000.0,
                    totalAmount = 125000.0,
                    paymentStatus = "Lunas",
                    paymentMethod = "Transfer BCA",
                    paymentDate = now - (3 * dayMs),
                    collectorName = "Hj. Siti Aminah (Bendahara RT)"
                ),
                DuesPayment(
                    invoiceNumber = "INV-202609-002",
                    citizenId = 3,
                    citizenName = "Budi Santoso, S.Kom",
                    houseNumber = "Blok B No. 08",
                    rt = "02",
                    month = "September",
                    year = 2026,
                    amountKebersihan = 35000.0,
                    amountKeamanan = 50000.0,
                    amountKasRt = 25000.0,
                    amountSosial = 15000.0,
                    totalAmount = 125000.0,
                    paymentStatus = "Lunas",
                    paymentMethod = "QRIS",
                    paymentDate = now - (1 * dayMs),
                    collectorName = "Hj. Siti Aminah (Bendahara RT)"
                ),
                DuesPayment(
                    invoiceNumber = "INV-202609-003",
                    citizenId = 5,
                    citizenName = "Dimas Arya Pratama",
                    houseNumber = "Blok C No. 14",
                    rt = "02",
                    month = "September",
                    year = 2026,
                    amountKebersihan = 35000.0,
                    amountKeamanan = 50000.0,
                    amountKasRt = 25000.0,
                    amountSosial = 15000.0,
                    totalAmount = 125000.0,
                    paymentStatus = "Belum Bayar",
                    paymentMethod = "QRIS",
                    paymentDate = null,
                    collectorName = "Hj. Siti Aminah (Bendahara RT)"
                ),
                DuesPayment(
                    invoiceNumber = "INV-202609-004",
                    citizenId = 6,
                    citizenName = "Agus Prasetyo",
                    houseNumber = "Blok D No. 05",
                    rt = "01",
                    month = "September",
                    year = 2026,
                    amountKebersihan = 35000.0,
                    amountKeamanan = 50000.0,
                    amountKasRt = 25000.0,
                    amountSosial = 15000.0,
                    totalAmount = 125000.0,
                    paymentStatus = "Belum Bayar",
                    paymentMethod = "Tunai",
                    paymentDate = null,
                    collectorName = "Hj. Siti Aminah (Bendahara RT)"
                )
            )
            duesDao.insertDues(initialDues)

            val initialAnnouncements = listOf(
                Announcement(
                    title = "Kerja Bakti Bersama & Fogging Nyamuk DBD",
                    content = "Dihimbau seluruh warga RT 02 / RW 05 untuk mengikuti kegiatan kerja bakti pembersihan saluran air dan fogging serentak pada hari Minggu pukul 07.00 WIB. Titik kumpul di Lapangan Balai Warga.",
                    category = "Kerja Bakti",
                    priority = "Penting",
                    date = now - (1 * dayMs)
                ),
                Announcement(
                    title = "Jadwal Pelayanan Posyandu Balita & Lansia",
                    content = "Pelayanan kesehatan penimbangan balita dan cek gula darah gratis untuk lansia dilaksanakan hari Rabu pekan ini pukul 08.30 - 11.30 WIB di Pos RW 05.",
                    category = "Posyandu",
                    priority = "Info",
                    date = now - (3 * dayMs)
                ),
                Announcement(
                    title = "Peningkatan Kewaspadaan & Jadwal Ronda Malam",
                    content = "Mengingat maraknya pencurian kendaraan di kelurahan sebelah, diharapkan warga memastikan pintu gerbang terkunci gembok dan kendaraan dipasang kunci ganda.",
                    category = "Keamanan",
                    priority = "Penting",
                    date = now - (5 * dayMs)
                )
            )
            announcementDao.insertAnnouncements(initialAnnouncements)
        }
    }
}
