package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val mobile: String,
    val email: String,
    val role: String, // "ADMIN" or "OPERATOR"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey val memberId: String, // Custom ID, e.g., "MEM-001"
    val name: String,
    val mobile: String,
    val village: String,
    val address: String,
    val aadhaar: String,
    val bankAccount: String,
    val ifsc: String,
    val joiningDate: String, // "yyyy-MM-dd"
    val status: String, // "Active" or "Inactive"
    val profilePhotoUri: String? = null
)

@Entity(tableName = "milk_collections")
data class MilkCollectionEntity(
    @PrimaryKey val collectionId: String,
    val memberId: String,
    val date: String, // "yyyy-MM-dd"
    val shift: String, // "Morning" or "Evening"
    val milkType: String, // "Cow" or "Buffalo"
    val quantity: Double, // in liters
    val fat: Double, // percentage
    val snf: Double, // percentage
    val rate: Double, // Rate calculated per liter
    val amount: Double // Total collection amount (quantity * rate)
)

@Entity(tableName = "rates")
data class RateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fatPercentage: Double,
    val cowRate: Double,
    val buffaloRate: Double
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val memberId: String,
    val amount: Double,
    val paymentDate: String, // "yyyy-MM-dd"
    val period: String, // e.g. "2026-06-01 to 2026-06-07" or "June 2026"
    val status: String // "Paid" or "Unpaid"
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val expenseId: String,
    val title: String,
    val amount: Double,
    val date: String, // "yyyy-MM-dd"
    val category: String // "Electricity", "Staff Salary", "Transport", "Maintenance", "Feed Cost", "Other"
)
