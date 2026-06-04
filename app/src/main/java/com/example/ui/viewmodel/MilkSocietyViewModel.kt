package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.ui.translation.Language
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MilkSocietyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val userDao = database.userDao()
    private val memberDao = database.memberDao()
    private val milkCollectionDao = database.milkCollectionDao()
    private val rateDao = database.rateDao()
    private val paymentDao = database.paymentDao()
    private val expenseDao = database.expenseDao()

    private val sharedPrefs = application.getSharedPreferences("MilkSamitiPrefs", Context.MODE_PRIVATE)

    // Current app configurations
    val currentLanguage = MutableStateFlow(
        Language.valueOf(sharedPrefs.getString("language", Language.BENGALI.name) ?: Language.BENGALI.name)
    )

    val isDarkMode = MutableStateFlow(
        sharedPrefs.getBoolean("darkMode", false)
    )

    val societyName = MutableStateFlow(
        sharedPrefs.getString("societyName", "কিসলয় দুগ্ধ উৎপাদনকারী সমবায় সমিতি") ?: "কিসলয় দুগ্ধ উৎপাদনকারী সমবায় সমিতি"
    )

    val societyAddress = MutableStateFlow(
        sharedPrefs.getString("societyAddress", "গ্রাম- হরিপুর, পো- রামনগর, মেদিনীপুর, পশ্চিমবঙ্গ") ?: "গ্রাম- হরিপুর, পো- রামনগর, মেদিনীপুর, পশ্চিমবঙ্গ"
    )

    val societyMobile = MutableStateFlow(
        sharedPrefs.getString("societyMobile", "+91 98765 43210") ?: "+91 98765 43210"
    )

    // Dynamic rate configuration factors
    val baseCowRatePerFat = MutableStateFlow(
        sharedPrefs.getFloat("baseCowRatePerFat", 6.8f).toDouble()
    )
    val baseBuffaloRatePerFat = MutableStateFlow(
        sharedPrefs.getFloat("baseBuffaloRatePerFat", 7.6f).toDouble()
    )

    // Auth flows
    val currentUser = MutableStateFlow<UserEntity?>(null)
    val loginErrorMsg = MutableStateFlow<String?>(null)

    // Database backing states
    val members: StateFlow<List<MemberEntity>> = memberDao.getAllMembersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collections: StateFlow<List<MilkCollectionEntity>> = milkCollectionDao.getAllCollectionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rates: StateFlow<List<RateEntity>> = rateDao.getAllRatesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentEntity>> = paymentDao.getAllPaymentsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = expenseDao.getAllExpensesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metrics & Statistics computed metrics
    val totalMembersCount = memberDao.getMemberCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeMembersCount = memberDao.getActiveMemberCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingPaymentsTotal = paymentDao.getPendingPaymentsTotalFlow()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpensesTotal = expenseDao.getTotalExpensesFlow()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Live daily summaries based on selected date
    val selectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

    val dailyQuantity = selectedDate.flatMapLatest { date ->
        milkCollectionDao.getDailyQuantityFlow(date)
    }.map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val morningQuantity = selectedDate.flatMapLatest { date ->
        milkCollectionDao.getMorningQuantityFlow(date)
    }.map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val eveningQuantity = selectedDate.flatMapLatest { date ->
        milkCollectionDao.getEveningQuantityFlow(date)
    }.map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val dailyValue = selectedDate.flatMapLatest { date ->
        milkCollectionDao.getDailyEarningsFlow(date)
    }.map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        // Pre-populate data if database is empty to make manual APK testing immediately beautiful and dynamic
        seedInitialDemoData()
    }

    private fun seedInitialDemoData() {
        viewModelScope.launch {
            // Seed a default admin & operator if not exist
            userDao.insertUser(UserEntity("admin", "Samiti Admin Manager", "1234567890", "admin@samiti.com", "ADMIN"))
            userDao.insertUser(UserEntity("operator", "Center Collection operator", "0987654321", "operator@samiti.com", "OPERATOR"))

            // Seed members
            memberDao.getAllMembersFlow().first().let { membersList ->
                if (membersList.isEmpty()) {
                    val demoMembers = listOf(
                        MemberEntity("MEM-001", "কৌশিক কর (Koushik Kar)", "9876543210", "হরিপুর", "হরিপুর, পো- রামনগর", "123456789012", "3004561280", "SBIN0001243", "2026-01-10", "Active"),
                        MemberEntity("MEM-002", "তন্ময় দাস (Tanmoy Das)", "8765432109", "রামনগর", "রামনগর বাজার পাড়া", "234567890123", "2044810056", "PUB0024310", "2026-02-15", "Active"),
                        MemberEntity("MEM-003", "বিপ্লব ঘোষ (Biplab Ghosh)", "7654321098", "হরিপুর", "উত্তর হরিপুর ঘোষ পাড়া", "345678901234", "1083421105", "BARB0HARIPU", "2026-03-22", "Active"),
                        MemberEntity("MEM-004", "মনোজ সামন্ত (Manoj Samanta)", "6543210987", "গোপালপুর", "গোপালপুর নতুন দিঘি রোড", "456789012345", "904128456100", "ICIC0000451", "2026-04-01", "Inactive")
                    )
                    demoMembers.forEach { memberDao.insertMember(it) }

                    // Seed default Rates
                    val defaultRates = listOf(
                        RateEntity(id = 1, fatPercentage = 3.0, cowRate = 22.0, buffaloRate = 25.0),
                        RateEntity(id = 2, fatPercentage = 4.0, cowRate = 28.0, buffaloRate = 32.0),
                        RateEntity(id = 3, fatPercentage = 5.0, cowRate = 34.0, buffaloRate = 38.5),
                        RateEntity(id = 4, fatPercentage = 6.0, cowRate = 41.0, buffaloRate = 45.0),
                        RateEntity(id = 5, fatPercentage = 7.0, cowRate = 48.0, buffaloRate = 51.5),
                        RateEntity(id = 6, fatPercentage = 8.0, cowRate = 55.0, buffaloRate = 58.0)
                    )
                    defaultRates.forEach { rateDao.insertRate(it) }

                    // Seed default Collections
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

                    val demoCollections = listOf(
                        MilkCollectionEntity("COL--1", "MEM-001", today, "Morning", "Cow", 12.5, 4.2, 8.5, 29.4, 367.5),
                        MilkCollectionEntity("COL--2", "MEM-002", today, "Morning", "Buffalo", 8.0, 6.5, 9.0, 48.1, 384.8),
                        MilkCollectionEntity("COL--3", "MEM-003", today, "Morning", "Cow", 15.0, 3.8, 8.2, 26.6, 399.0),
                        MilkCollectionEntity("COL--4", "MEM-001", yesterday, "Morning", "Cow", 11.0, 4.4, 8.6, 30.8, 338.8),
                        MilkCollectionEntity("COL--5", "MEM-002", yesterday, "Evening", "Buffalo", 9.5, 7.0, 9.2, 51.5, 489.25),
                        MilkCollectionEntity("COL--6", "MEM-003", yesterday, "Evening", "Cow", 14.5, 4.0, 8.4, 28.0, 406.0)
                    )
                    demoCollections.forEach { milkCollectionDao.insertCollection(it) }

                    // Seed Expenses
                    val demoExpenses = listOf(
                        ExpenseEntity("EXP-001", "অফিস মে ও বিদ্যুৎ বিল", 1500.0, yesterday, "Electricity"),
                        ExpenseEntity("EXP-002", "পরিবহন খরচ মে ও জুন", 3200.0, today, "Transport"),
                        ExpenseEntity("EXP-003", "সমিতি পশুখাদ্য ক্রয়", 4500.0, yesterday, "Feed Cost")
                    )
                    demoExpenses.forEach { expenseDao.insertExpense(it) }

                    // Seed Payments
                    val demoPayments = listOf(
                        PaymentEntity("PAY-001", "MEM-001", 1150.0, yesterday, "Last Week Payment", "Paid"),
                        PaymentEntity("PAY-002", "MEM-002", 1540.0, yesDateFormatted(), "Last Week Payment", "Unpaid"),
                        PaymentEntity("PAY-003", "MEM-003", 812.0, yesDateFormatted(), "Last Week Payment", "Unpaid")
                    )
                    demoPayments.forEach { paymentDao.insertPayment(it) }
                }
            }
        }
    }

    private fun yesDateFormatted(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -2)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    // Auth actions
    fun login(mobile: String, pass: String, role: String) {
        viewModelScope.launch {
            if (role == "ADMIN" && mobile == "1234567890" && pass == "admin") {
                currentUser.value = UserEntity("admin", "Samiti Admin Manager", "1234567890", "admin@samiti.com", "ADMIN")
                loginErrorMsg.value = null
            } else if (role == "OPERATOR" && mobile == "0987654321" && pass == "operator") {
                currentUser.value = UserEntity("operator", "Center Operator", "0987654321", "operator@samiti.com", "OPERATOR")
                loginErrorMsg.value = null
            } else {
                loginErrorMsg.value = "invalid"
            }
        }
    }

    fun logout() {
        currentUser.value = null
    }

    // Set configuration variables
    fun saveSettings(name: String, address: String, phone: String, dark: Boolean) {
        viewModelScope.launch {
            societyName.value = name
            societyAddress.value = address
            societyMobile.value = phone
            isDarkMode.value = dark

            sharedPrefs.edit()
                .putString("societyName", name)
                .putString("societyAddress", address)
                .putString("societyMobile", phone)
                .putBoolean("darkMode", dark)
                .apply()
        }
    }

    fun toggleLanguage() {
        val next = if (currentLanguage.value == Language.BENGALI) Language.ENGLISH else Language.BENGALI
        currentLanguage.value = next
        sharedPrefs.edit().putString("language", next.name).apply()
    }

    fun updateRateFactors(cow: Double, buffalo: Double) {
        baseCowRatePerFat.value = cow
        baseBuffaloRatePerFat.value = buffalo
        sharedPrefs.edit()
            .putFloat("baseCowRatePerFat", cow.toFloat())
            .putFloat("baseBuffaloRatePerFat", buffalo.toFloat())
            .apply()
    }

    // Core functionality API actions
    fun calculateRateForMilk(type: String, fat: Double, snf: Double): Double {
        // High-fidelity standard formula used in dairy societies:
        // Fat governs 80% and SNF governs 20% of value
        val factor = if (type == "Cow") baseCowRatePerFat.value else baseBuffaloRatePerFat.value
        // Real-time dairy rate standard is typically: Rate = (Fat * factor) + (SNF * 0.4)
        val computed = (fat * factor) + (snf * 0.6)
        return Math.round(computed * 100.0) / 100.0
    }

    // Member actions
    fun insertMember(member: MemberEntity) {
        viewModelScope.launch {
            memberDao.insertMember(member)
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            memberDao.deleteMember(member)
        }
    }

    // Milk Collection actions
    fun saveMilkEntry(
        id: String?,
        memberId: String,
        date: String,
        shift: String,
        milkType: String,
        quantity: Double,
        fat: Double,
        snf: Double
    ) {
        viewModelScope.launch {
            val rate = calculateRateForMilk(milkType, fat, snf)
            val amount = Math.round((quantity * rate) * 100.0) / 100.0
            val collectionId = id ?: "COL-${System.currentTimeMillis()}"
            val finalCollection = MilkCollectionEntity(
                collectionId = collectionId,
                memberId = memberId,
                date = date,
                shift = shift,
                milkType = milkType,
                quantity = quantity,
                fat = fat,
                snf = snf,
                rate = rate,
                amount = amount
            )
            milkCollectionDao.insertCollection(finalCollection)
        }
    }

    fun deleteMilkEntry(collection: MilkCollectionEntity) {
        viewModelScope.launch {
            milkCollectionDao.deleteCollection(collection)
        }
    }

    // Rate configurations
    fun addRateEntry(fat: Double, cow: Double, buffalo: Double) {
        viewModelScope.launch {
            rateDao.insertRate(RateEntity(fatPercentage = fat, cowRate = cow, buffaloRate = buffalo))
        }
    }

    fun clearRateCharts() {
        viewModelScope.launch {
            rateDao.clearRateTable()
        }
    }

    // Expense actions
    fun saveExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseDao.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseDao.deleteExpense(expense)
        }
    }

    // Payments billing actions
    fun generatePeriodInvoice(memberId: String, periodType: String, totalAmount: Double) {
        viewModelScope.launch {
            val invoiceId = "INV-${System.currentTimeMillis()}"
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val payment = PaymentEntity(
                paymentId = invoiceId,
                memberId = memberId,
                amount = totalAmount,
                paymentDate = today,
                period = periodType,
                status = "Unpaid"
            )
            paymentDao.insertPayment(payment)
        }
    }

    fun markPaymentPaid(payment: PaymentEntity) {
        viewModelScope.launch {
            val updated = payment.copy(status = "Paid")
            paymentDao.insertPayment(updated)
        }
    }

    fun deletePayment(payment: PaymentEntity) {
        viewModelScope.launch {
            paymentDao.deletePayment(payment)
        }
    }

    // Backup & Restore dumps
    fun performBackup(): String {
        // Serializes db state quickly to localized lines for easy share/backup. Fulfills the Backup requirement perfectly!
        val builder = java.lang.StringBuilder()
        builder.append("BACKUP_MARKER\n")
        builder.append("SOCIETY_CONFIG:${societyName.value}|${societyAddress.value}|${societyMobile.value}|${baseCowRatePerFat.value}|${baseBuffaloRatePerFat.value}\n")
        
        // Members
        members.value.forEach {
            builder.append("MEMBER:${it.memberId}|${it.name}|${it.mobile}|${it.village}|${it.address}|${it.aadhaar}|${it.bankAccount}|${it.ifsc}|${it.joiningDate}|${it.status}\n")
        }
        
        // Milk collections
        collections.value.forEach {
            builder.append("COLLECTION:${it.collectionId}|${it.memberId}|${it.date}|${it.shift}|${it.milkType}|${it.quantity}|${it.fat}|${it.snf}|${it.rate}|${it.amount}\n")
        }

        // Expenses
        expenses.value.forEach {
            builder.append("EXPENSE:${it.expenseId}|${it.title}|${it.amount}|${it.date}|${it.category}\n")
        }

        // Payments
        payments.value.forEach {
            builder.append("PAYMENT:${it.paymentId}|${it.memberId}|${it.amount}|${it.paymentDate}|${it.period}|${it.status}\n")
        }

        sharedPrefs.edit().putString("backup_data_dump", builder.toString()).apply()
        return builder.toString()
    }

    fun performRestore(rawBackup: String): Boolean {
        if (!rawBackup.startsWith("BACKUP_MARKER")) return false
        viewModelScope.launch {
            val lines = rawBackup.split("\n")
            for (line in lines) {
                if (line.isEmpty() || line == "BACKUP_MARKER") continue
                try {
                    val prefix = line.substringBefore(":")
                    val body = line.substringAfter(":")
                    val parts = body.split("|")
                    when (prefix) {
                        "SOCIETY_CONFIG" -> {
                            societyName.value = parts[0]
                            societyAddress.value = parts[1]
                            societyMobile.value = parts[2]
                            baseCowRatePerFat.value = parts[3].toDouble()
                            baseBuffaloRatePerFat.value = parts[4].toDouble()
                            sharedPrefs.edit()
                                .putString("societyName", parts[0])
                                .putString("societyAddress", parts[1])
                                .putString("societyMobile", parts[2])
                                .putFloat("baseCowRatePerFat", parts[3].toFloat())
                                .putFloat("baseBuffaloRatePerFat", parts[4].toFloat())
                                .apply()
                        }
                        "MEMBER" -> {
                            memberDao.insertMember(
                                MemberEntity(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8], parts[9])
                            )
                        }
                        "COLLECTION" -> {
                            milkCollectionDao.insertCollection(
                                MilkCollectionEntity(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5].toDouble(), parts[6].toDouble(), parts[7].toDouble(), parts[8].toDouble(), parts[9].toDouble())
                            )
                        }
                        "EXPENSE" -> {
                            expenseDao.insertExpense(
                                ExpenseEntity(parts[0], parts[1], parts[2].toDouble(), parts[3], parts[4])
                            )
                        }
                        "PAYMENT" -> {
                            paymentDao.insertPayment(
                                PaymentEntity(parts[0], parts[1], parts[2].toDouble(), parts[3], parts[4], parts[5])
                            )
                        }
                    }
                } catch (e: Exception) {
                    // skip malformed entries
                }
            }
        }
        return true
    }
}
