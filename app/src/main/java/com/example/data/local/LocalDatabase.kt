package com.example.data.local

import android.content.Context
import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
}

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY memberId ASC")
    fun getAllMembersFlow(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE memberId = :memberId LIMIT 1")
    suspend fun getMemberById(memberId: String): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("SELECT COUNT(*) FROM members")
    fun getMemberCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM members WHERE status = 'Active'")
    fun getActiveMemberCountFlow(): Flow<Int>
}

@Dao
interface MilkCollectionDao {
    @Query("SELECT * FROM milk_collections ORDER BY date DESC, shift DESC")
    fun getAllCollectionsFlow(): Flow<List<MilkCollectionEntity>>

    @Query("SELECT * FROM milk_collections WHERE memberId = :memberId ORDER BY date DESC")
    fun getCollectionsByMemberFlow(memberId: String): Flow<List<MilkCollectionEntity>>

    @Query("SELECT * FROM milk_collections WHERE date = :date")
    fun getCollectionsByDateFlow(date: String): Flow<List<MilkCollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: MilkCollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: MilkCollectionEntity)

    @Query("SELECT SUM(quantity) FROM milk_collections WHERE date = :date")
    fun getDailyQuantityFlow(date: String): Flow<Double?>

    @Query("SELECT SUM(quantity) FROM milk_collections WHERE date = :date AND shift = 'Morning'")
    fun getMorningQuantityFlow(date: String): Flow<Double?>

    @Query("SELECT SUM(quantity) FROM milk_collections WHERE date = :date AND shift = 'Evening'")
    fun getEveningQuantityFlow(date: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM milk_collections WHERE date = :date")
    fun getDailyEarningsFlow(date: String): Flow<Double?>

    @Query("SELECT * FROM milk_collections WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getCollectionsInDateRange(startDate: String, endDate: String): List<MilkCollectionEntity>
}

@Dao
interface RateDao {
    @Query("SELECT * FROM rates ORDER BY fatPercentage ASC")
    fun getAllRatesFlow(): Flow<List<RateEntity>>

    @Query("SELECT * FROM rates ORDER BY fatPercentage ASC")
    suspend fun getAllRatesStatic(): List<RateEntity>

    @Query("SELECT * FROM rates WHERE fatPercentage = :fat LIMIT 1")
    suspend fun getRateByFat(fat: Double): RateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: RateEntity)

    @Query("DELETE FROM rates")
    suspend fun clearRateTable()
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPaymentsFlow(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE memberId = :memberId")
    fun getPaymentsByMemberFlow(memberId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query("SELECT SUM(amount) FROM payments WHERE status = 'Unpaid'")
    fun getPendingPaymentsTotalFlow(): Flow<Double?>
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpensesFlow(): Flow<Double?>
}

@Database(
    entities = [
        UserEntity::class,
        MemberEntity::class,
        MilkCollectionEntity::class,
        RateEntity::class,
        PaymentEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun memberDao(): MemberDao
    abstract fun milkCollectionDao(): MilkCollectionDao
    abstract fun rateDao(): RateDao
    abstract fun paymentDao(): PaymentDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "milk_samiti_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
