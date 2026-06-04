package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.translation.AppStrings
import com.example.ui.translation.Language
import com.example.ui.viewmodel.MilkSocietyViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AuthView(viewModel: MilkSocietyViewModel) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val err by viewModel.loginErrorMsg.collectAsStateWithLifecycle()

    var mobileStr by remember { mutableStateOf("") }
    var passwordStr by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("OPERATOR") } // "OPERATOR" or "ADMIN"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo Area
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = AppStrings.getString("app_title", lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = AppStrings.getString("login_subtitle", lang),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role Selector Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                listOf("OPERATOR", "ADMIN").forEach { role ->
                    val isSelected = selectedRole == role
                    val roleLabel = if (role == "ADMIN") "admin" else "operator"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { selectedRole = role }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = AppStrings.getString(roleLabel, lang),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RuralTextField(
                value = mobileStr,
                onValueChange = { mobileStr = it },
                label = AppStrings.getString("username_hint", lang),
                placeholder = if (selectedRole == "ADMIN") "1234567890 (admin)" else "0987654321 (operator)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            RuralTextField(
                value = passwordStr,
                onValueChange = { passwordStr = it },
                label = AppStrings.getString("password_hint", lang),
                placeholder = if (selectedRole == "ADMIN") "admin" else "operator",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Password visual masking
            var showPassMsg by remember { mutableStateOf(false) }
            Text(
                text = AppStrings.getString("forgot_password", lang),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { showPassMsg = true }
                    .padding(vertical = 4.dp),
                fontSize = 13.sp
            )

            if (showPassMsg) {
                Text(
                    text = if (lang == Language.BENGALI) "অনুগ্ৰহ করে আপনার সমিতির ম্যানেজারের সাথে যোগাযোগ করুন" else "Please contact society administrator",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (err != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = AppStrings.getString("login_error", lang),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LargeButton(
                text = AppStrings.getString("login", lang),
                onClick = {
                    viewModel.login(mobileStr, passwordStr, selectedRole)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Language Toggle Switch
            TextButton(onClick = { viewModel.toggleLanguage() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Translate, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (lang == Language.BENGALI) "English Support" else "বাংলা সাপোর্ট", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DashboardView(viewModel: MilkSocietyViewModel, onNavigate: (String) -> Unit) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val totalMembers by viewModel.totalMembersCount.collectAsStateWithLifecycle()
    val activeMembers by viewModel.activeMembersCount.collectAsStateWithLifecycle()
    val dailyQty by viewModel.dailyQuantity.collectAsStateWithLifecycle()
    val morningQty by viewModel.morningQuantity.collectAsStateWithLifecycle()
    val eveningQty by viewModel.eveningQuantity.collectAsStateWithLifecycle()
    val dailyVal by viewModel.dailyValue.collectAsStateWithLifecycle()
    val pendingPay by viewModel.pendingPaymentsTotal.collectAsStateWithLifecycle()
    val collectionsList by viewModel.collections.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = SimpleDateFormat("EEEE, dd MMMM yyyy", if (lang == Language.BENGALI) Locale("bn") else Locale.US).format(Date()),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Primary Stat Card: Today's Collection Quantity
            LargeStatsCard(
                title = AppStrings.getString("today_total", lang),
                value = String.format("%.1f %s", dailyQty, AppStrings.getString("liters_abbr", lang)),
                subtitle = "${AppStrings.getString("morning_col", lang)}: ${String.format("%.1f", morningQty)}L  |  ${AppStrings.getString("evening_col", lang)}: ${String.format("%.1f", eveningQty)}L",
                icon = { Icon(Icons.Default.Water, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }

        item {
            // Horizontal quick card rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = AppStrings.getString("total_members", lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$totalMembers",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${AppStrings.getString("active_members", lang)}: $activeMembers",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = AppStrings.getString("today_earnings", lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = String.format("৳%.1f", dailyVal),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF15803D) // Polished vector-inspired green
                        )
                        Text(
                            text = AppStrings.getString("calculated_amount", lang),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item {
            // Pending payment dashboard item
            LargeStatsCard(
                title = AppStrings.getString("pending_payments", lang),
                value = String.format("৳%.2f", pendingPay),
                subtitle = "বকেয়া সদস্যদের মিল্ক রসিদ পেমেন্ট",
                icon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = Color.Red, modifier = Modifier.size(28.dp)) },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Render Dynamic Statistics Canvas Chart
            // Generate last 5 collections items data values to feed graph
            val last5Collections = collectionsList.take(5).reversed()
            val chartData = if (last5Collections.isNotEmpty()) last5Collections.map { it.quantity } else listOf(5.0, 10.0, 8.0, 15.0, 12.0)
            val chartLabels = if (last5Collections.isNotEmpty()) last5Collections.map { it.shift.take(3) } else listOf("Mon", "Tue", "Wed", "Thu", "Fri")

            BeautifulMilkChart(
                data = chartData,
                labels = chartLabels,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Quick navigations row for dairy operators list
            Text("চাকরি ও পরিচালনা শর্টকাট", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShortcutBtn(Icons.Filled.Group, AppStrings.getString("tab_members", lang), Modifier.weight(1f)) { onNavigate("members") }
                    ShortcutBtn(Icons.Filled.Water, AppStrings.getString("tab_collection", lang), Modifier.weight(1f)) { onNavigate("collection") }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShortcutBtn(Icons.Filled.Payments, AppStrings.getString("tab_payments", lang), Modifier.weight(1f)) { onNavigate("payments") }
                    ShortcutBtn(Icons.Filled.Assessment, AppStrings.getString("tab_reports", lang), Modifier.weight(1f)) { onNavigate("reports") }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ShortcutBtn(icon: ImageVector, text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MemberManagementView(viewModel: MilkSocietyViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val memberList by viewModel.members.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMemberForEdit by remember { mutableStateOf<MemberEntity?>(null) }

    // Dialog Input forms state
    var editId by remember { mutableStateOf("") }
    var editName by remember { mutableStateOf("") }
    var editMobile by remember { mutableStateOf("") }
    var editVillage by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editAadhaar by remember { mutableStateOf("") }
    var editBankAcc by remember { mutableStateOf("") }
    var editIfsc by remember { mutableStateOf("") }
    var editStatus by remember { mutableStateOf("Active") }

    val filteredList = memberList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.memberId.contains(searchQuery, ignoreCase = true) ||
                it.village.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = AppStrings.getString("tab_members", lang),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Button to Add Member
            Button(
                onClick = {
                    editId = String.format("MEM-%03d", memberList.size + 1)
                    editName = ""
                    editMobile = ""
                    editVillage = ""
                    editAddress = ""
                    editAadhaar = ""
                    editBankAcc = ""
                    editIfsc = ""
                    editStatus = "Active"
                    showAddDialog = true
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.getString("add_new_member", lang), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        RuralTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = AppStrings.getString("search_members", lang),
            placeholder = "নাম, আইডি বা গ্রামের নাম দিন"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Members List View
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(AppStrings.getString("no_data", lang), color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { member ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (member.status == "Active") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = member.name,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${AppStrings.getString("member_id", lang)}: ${member.memberId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = if (member.status == "Active") AppStrings.getString("active", lang) else AppStrings.getString("inactive", lang),
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        labelColor = if (member.status == "Active") Color(0xFF2E7D32) else Color.Gray
                                    )
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text(text = "📞 ${member.mobile}", fontSize = 14.sp)
                                    Text(text = "🏡 ${member.village}", fontSize = 14.sp)
                                    Text(text = "💳 Account: ${member.bankAccount}", fontSize = 13.sp, color = Color.Gray)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    editId = member.memberId
                                    editName = member.name
                                    editMobile = member.mobile
                                    editVillage = member.village
                                    editAddress = member.address
                                    editAadhaar = member.aadhaar
                                    editBankAcc = member.bankAccount
                                    editIfsc = member.ifsc
                                    editStatus = member.status
                                    selectedMemberForEdit = member
                                    showAddDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                }

                                IconButton(onClick = { viewModel.deleteMember(member) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add/Edit Dialog modal
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    selectedMemberForEdit = null
                },
                title = {
                    Text(
                        text = if (selectedMemberForEdit == null) AppStrings.getString("add_new_member", lang) else AppStrings.getString("edit_member", lang),
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        item {
                            RuralTextField(
                                value = editId,
                                onValueChange = { editId = it },
                                label = AppStrings.getString("member_id", lang)
                            )
                        }
                        item {
                            RuralTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = AppStrings.getString("member_name", lang)
                            )
                        }
                        item {
                            RuralTextField(
                                value = editMobile,
                                onValueChange = { editMobile = it },
                                label = AppStrings.getString("mobile_no", lang),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )
                        }
                        item {
                            RuralTextField(
                                value = editVillage,
                                onValueChange = { editVillage = it },
                                label = AppStrings.getString("village", lang)
                            )
                        }
                        item {
                            RuralTextField(
                                value = editAddress,
                                onValueChange = { editAddress = it },
                                label = AppStrings.getString("address", lang)
                            )
                        }
                        item {
                            RuralTextField(
                                value = editAadhaar,
                                onValueChange = { editAadhaar = it },
                                label = AppStrings.getString("aadhaar", lang),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        item {
                            RuralTextField(
                                value = editBankAcc,
                                onValueChange = { editBankAcc = it },
                                label = AppStrings.getString("bank_account", lang),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        item {
                            RuralTextField(
                                value = editIfsc,
                                onValueChange = { editIfsc = it },
                                label = AppStrings.getString("ifsc", lang)
                            )
                        }
                        item {
                            // Status check
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(AppStrings.getString("status", lang), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(16.dp))
                                Row(
                                    modifier = Modifier.clickable { editStatus = "Active" },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = editStatus == "Active", onClick = { editStatus = "Active" })
                                    Text(AppStrings.getString("active", lang))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Row(
                                    modifier = Modifier.clickable { editStatus = "Inactive" },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = editStatus == "Inactive", onClick = { editStatus = "Inactive" })
                                    Text(AppStrings.getString("inactive", lang))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editName.isBlank() || editMobile.isBlank()) {
                                return@Button
                            }
                            val member = MemberEntity(
                                memberId = editId,
                                name = editName,
                                mobile = editMobile,
                                village = editVillage,
                                address = editAddress,
                                aadhaar = editAadhaar,
                                bankAccount = editBankAcc,
                                ifsc = editIfsc,
                                joiningDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                status = editStatus
                            )
                            viewModel.insertMember(member)
                            showAddDialog = false
                            selectedMemberForEdit = null
                        }
                    ) {
                        Text(AppStrings.getString("save_member", lang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                        selectedMemberForEdit = null
                    }) {
                        Text(AppStrings.getString("cancel", lang))
                    }
                }
            )
        }
    }
}

@Composable
fun MilkCollectionView(viewModel: MilkSocietyViewModel) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val memberList by viewModel.members.collectAsStateWithLifecycle()
    val collectionsList by viewModel.collections.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("form") } // "form" or "list"

    // Live Collection states values
    var selectedMemberIndex by remember { mutableStateOf(0) }
    var selectedShift by remember { mutableStateOf("Morning") } // "Morning" or "Evening"
    var selectedMilkType by remember { mutableStateOf("Cow") } // "Cow" or "Buffalo"
    var quantityStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }
    var snfStr by remember { mutableStateOf("") }

    // Derive values
    val currentMember = memberList.getOrNull(selectedMemberIndex)
    val qty = quantityStr.toDoubleOrNull() ?: 0.0
    val fat = fatStr.toDoubleOrNull() ?: 0.0
    val snf = snfStr.toDoubleOrNull() ?: 0.0

    // Reactively compute rate in real-time on input!
    val computedRate = remember(selectedMilkType, fat, snf) {
        viewModel.calculateRateForMilk(selectedMilkType, fat, snf)
    }
    val computedAmount = remember(qty, computedRate) {
        Math.round((qty * computedRate) * 100.0) / 100.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Module Top Header Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = { activeTab = "form" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "form") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeTab == "form") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            ) {
                Text(AppStrings.getString("tab_collection", lang), fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { activeTab = "list" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "list") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeTab == "list") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
            ) {
                Text("আজকের দুধ রেকর্ড সমূহ", fontWeight = FontWeight.Bold)
            }
        }

        if (activeTab == "form") {
            if (memberList.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "দয়া করে সদস্যবৃন্দ ট্যাবে গিয়ে সদস্য প্রথম যুক্ত করুন",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        // Dropdown choice
                        RuralDropdown(
                            items = memberList,
                            selectedItem = memberList[selectedMemberIndex],
                            onItemSelected = { selectedMemberIndex = memberList.indexOf(it) },
                            label = AppStrings.getString("select_member", lang),
                            itemLabelProvider = { "${it.memberId} : ${it.name} (${it.village})" }
                        )
                    }

                    item {
                        // Shift selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(AppStrings.getString("shift", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            listOf("Morning", "Evening").forEach { shift ->
                                val label = if (shift == "Morning") "shift_morning" else "shift_evening"
                                Row(
                                    modifier = Modifier
                                        .clickable { selectedShift = shift }
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedShift == shift, onClick = { selectedShift = shift })
                                    Text(AppStrings.getString(label, lang), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        // Milk type Cow vs Buffalo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(AppStrings.getString("milk_type", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            listOf("Cow", "Buffalo").forEach { type ->
                                val label = if (type == "Cow") "cow" else "buffalo"
                                Row(
                                    modifier = Modifier
                                        .clickable { selectedMilkType = type }
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedMilkType == type, onClick = { selectedMilkType = type })
                                    Text(AppStrings.getString(label, lang), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        RuralTextField(
                            value = quantityStr,
                            onValueChange = { quantityStr = it },
                            label = AppStrings.getString("milk_quantity", lang),
                            placeholder = "উদা- ১০.৫",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RuralTextField(
                                value = fatStr,
                                onValueChange = { fatStr = it },
                                label = AppStrings.getString("fat_pct", lang),
                                placeholder = "৪.৫",
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            RuralTextField(
                                value = snfStr,
                                onValueChange = { snfStr = it },
                                label = AppStrings.getString("snf_pct", lang),
                                placeholder = "৮.৫",
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    item {
                        // Display reactive result board
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(AppStrings.getString("calculated_rate", lang), fontWeight = FontWeight.Bold)
                                    Text("৳ ${String.format("%.2f", computedRate)}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(AppStrings.getString("calculated_amount", lang), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("৳ ${String.format("%.2f", computedAmount)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        LargeButton(
                            text = AppStrings.getString("save_collection", lang),
                            onClick = {
                                if (qty <= 0.0 || fat <= 0.0 || snf <= 0.0 || currentMember == null) {
                                    Toast.makeText(context, AppStrings.getString("invalid_inputs", context.langFromStr(lang.name)), Toast.LENGTH_SHORT).show()
                                    return@LargeButton
                                }
                                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                viewModel.saveMilkEntry(
                                    id = null,
                                    memberId = currentMember.memberId,
                                    date = today,
                                    shift = selectedShift,
                                    milkType = selectedMilkType,
                                    quantity = qty,
                                    fat = fat,
                                    snf = snf
                                )
                                Toast.makeText(context, AppStrings.getString("collection_saved", lang), Toast.LENGTH_SHORT).show()
                                // Clear inputs
                                quantityStr = ""
                                fatStr = ""
                                snfStr = ""
                            }
                        )
                    }
                }
            }
        } else {
            // "list" Tab
            if (collectionsList.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("আজকের দিনে এখনো কোনো সংগ্রহ এন্ট্রি করা হয়নি", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(collectionsList) { col ->
                        // Load member details
                        val member = memberList.firstOrNull { it.memberId == col.memberId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = member?.name ?: col.memberId,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )

                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = if (col.shift == "Morning") " Morning" else " Evening",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("দুধ: ${col.quantity}L (${col.milkType})", fontSize = 14.sp)
                                        Text("Fat: ${col.fat}% | SNF: ${col.snf}%", fontSize = 13.sp, color = Color.Gray)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("৳${col.amount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                        Text("৳${col.rate}/L", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    IconButton(onClick = { viewModel.deleteMilkEntry(col) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Simple Helper to handle Toast languages
fun Context.langFromStr(name: String): Language = Language.valueOf(name)

@Composable
fun RateChartView(viewModel: MilkSocietyViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val cowFactor by viewModel.baseCowRatePerFat.collectAsStateWithLifecycle()
    val buffaloFactor by viewModel.baseBuffaloRatePerFat.collectAsStateWithLifecycle()
    val ratesList by viewModel.rates.collectAsStateWithLifecycle()

    var cowInput by remember { mutableStateOf(cowFactor.toString()) }
    var buffaloInput by remember { mutableStateOf(buffaloFactor.toString()) }
    var showChartDialog by remember { mutableStateOf(false) }

    // Inputs for explicit custom chart point
    var customFatStr by remember { mutableStateOf("") }
    var customCowStr by remember { mutableStateOf("") }
    var customBuffStr by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.getString("rate_chart_title", lang),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic multipliers update box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ফ্যাট ভিত্তিক বেস গুনক (টাকা/১% ফ্যাট)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                RuralTextField(
                    value = cowInput,
                    onValueChange = { cowInput = it },
                    label = AppStrings.getString("cow_rate", lang),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                RuralTextField(
                    value = buffaloInput,
                    onValueChange = { buffaloInput = it },
                    label = AppStrings.getString("buffalo_rate", lang),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LargeButton(
                    text = AppStrings.getString("update_rate_table", lang),
                    onClick = {
                        val cVal = cowInput.toDoubleOrNull() ?: cowFactor
                        val bVal = buffaloInput.toDoubleOrNull() ?: buffaloFactor
                        viewModel.updateRateFactors(cVal, bVal)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Standard lookup reference charts
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ফাইল ভিত্তিক নমুনা দর তালিকা (Lookup Table)", fontWeight = FontWeight.Bold)
            IconButton(onClick = { showChartDialog = true }) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fat (%)", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Cow (৳/L)", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Buffalo (৳/L)", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }

            if (ratesList.isEmpty()) {
                item {
                    Text("তালিকা খালি রয়েছে। উপরোক্ত প্লাস বাটনে ক্লিক করে দর সংযোজন করুন।", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
                }
            } else {
                items(ratesList) { rate ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${rate.fatPercentage}%", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("৳${rate.cowRate}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("৳${rate.buffaloRate}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                }
            }
        }

        if (showChartDialog) {
            AlertDialog(
                onDismissRequest = { showChartDialog = false },
                title = { Text("নতুন দর পয়েন্ট যোগ করুন", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        RuralTextField(
                            value = customFatStr,
                            onValueChange = { customFatStr = it },
                            label = "ফ্যাট পয়েন্ট (%)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        RuralTextField(
                            value = customCowStr,
                            onValueChange = { customCowStr = it },
                            label = "গাভী প্রতি লিটার পেমেন্ট",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        RuralTextField(
                            value = customBuffStr,
                            onValueChange = { customBuffStr = it },
                            label = "মহিষ প্রতি লিটার পেমেন্ট",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val fat = customFatStr.toDoubleOrNull() ?: 0.0
                        val cow = customCowStr.toDoubleOrNull() ?: 0.0
                        val buff = customBuffStr.toDoubleOrNull() ?: 0.0
                        if (fat > 0.0 && cow > 0.0 && buff > 0.0) {
                            viewModel.addRateEntry(fat, cow, buff)
                            customFatStr = ""
                            customCowStr = ""
                            customBuffStr = ""
                            showChartDialog = false
                        }
                    }) {
                        Text("যোগ করুন")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChartDialog = false }) {
                        Text("বাতিল")
                    }
                }
            )
        }
    }
}

@Composable
fun PaymentView(viewModel: MilkSocietyViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val memberList by viewModel.members.collectAsStateWithLifecycle()
    val collectionsList by viewModel.collections.collectAsStateWithLifecycle()
    val paymentsList by viewModel.payments.collectAsStateWithLifecycle()

    var selectedMemberIndex by remember { mutableStateOf(0) }
    var billPeriod by remember { mutableStateOf("Weekly Bill (01 to 07 June)") }

    // Printable receipt state variables
    var showReceipt by remember { mutableStateOf<PaymentEntity?>(null) }

    val currentMember = memberList.getOrNull(selectedMemberIndex)

    // Compute total milk and earnings accrued for this member
    val accruedCollections = remember(currentMember, collectionsList) {
        if (currentMember == null) emptyList()
        else collectionsList.filter { it.memberId == currentMember.memberId }
    }

    val sumLiters = accruedCollections.sumOf { it.quantity }
    val sumAmount = accruedCollections.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.getString("tab_payments", lang),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (memberList.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("সদস্য এন্ট্রি না থাকলে বিল জেনারেট করা সম্ভব নয়।")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Invoice form generator card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "সাপ্তাহিক ও মাসিক বিল রিকনসিলিয়েশন",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.titleMedium
                            )

                            RuralDropdown(
                                items = memberList,
                                selectedItem = memberList[selectedMemberIndex],
                                onItemSelected = { selectedMemberIndex = memberList.indexOf(it) },
                                label = AppStrings.getString("select_member", lang),
                                itemLabelProvider = { "${it.memberId} : ${it.name}" }
                            )

                            RuralTextField(
                                value = billPeriod,
                                onValueChange = { billPeriod = it },
                                label = AppStrings.getString("billing_period", lang)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Show accrued earnings
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("মোট দুধ সরবরাহ:", fontWeight = FontWeight.Bold)
                                Text("$sumLiters L", fontWeight = FontWeight.Black)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(AppStrings.getString("due_amount", lang) + ":", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("৳ $sumAmount", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF2E7D32))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LargeButton(
                                text = AppStrings.getString("generate_bill", lang),
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                onClick = {
                                    if (currentMember != null && sumAmount > 0.0) {
                                        viewModel.generatePeriodInvoice(currentMember.memberId, billPeriod, sumAmount)
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = AppStrings.getString("payment_history", lang),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                if (paymentsList.isEmpty()) {
                    item {
                        Text("কোনো বিলিং রসিদ তৈরি করা হয়নি", color = Color.Gray, modifier = Modifier.padding(12.dp))
                    }
                } else {
                    items(paymentsList) { pay ->
                        val mDet = memberList.firstOrNull { it.memberId == pay.memberId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = mDet?.name ?: pay.memberId, fontWeight = FontWeight.Bold)
                                        Text(text = pay.period, fontSize = 12.sp, color = Color.Gray)
                                    }

                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = if (pay.status == "Paid") AppStrings.getString("paid_status", lang) else AppStrings.getString("unpaid_status", lang)
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            labelColor = if (pay.status == "Paid") Color(0xFF2E7D32) else Color.Red
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "৳ ${pay.amount}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))

                                    Row {
                                        if (pay.status == "Unpaid") {
                                            TextButton(onClick = { viewModel.markPaymentPaid(pay) }) {
                                                Icon(Icons.Default.Done, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("পেমেন্ট সম্পন্ন")
                                            }
                                        }

                                        IconButton(onClick = { showReceipt = pay }) {
                                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }

                                        IconButton(onClick = { viewModel.deletePayment(pay) }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Printable payment receipt sheet popup
        if (showReceipt != null) {
            val rec = showReceipt!!
            val mem = memberList.firstOrNull { it.memberId == rec.memberId }

            AlertDialog(
                onDismissRequest = { showReceipt = null },
                title = { Text(AppStrings.getString("record_payment", lang), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.2.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = viewModel.societyName.value,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = viewModel.societyAddress.value,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(text = "------------------------------------------", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                        Text("রসিদ নং: ${rec.paymentId}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("তারিখ: ${rec.paymentDate}", fontSize = 12.sp)
                        Text("বিলের সময়সীমা: ${rec.period}", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("কৃষক নাম: ${mem?.name ?: rec.memberId}", fontWeight = FontWeight.Bold)
                        Text("আইডি: ${rec.memberId}", fontSize = 12.sp)
                        Text("ব্যাংক অ্যাকাউন্ট: ${mem?.bankAccount}", fontSize = 12.sp)
                        Text("IFSC কোড: ${mem?.ifsc}", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "------------------------------------------", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("প্রদেয় অর্থ:", fontWeight = FontWeight.Bold)
                            Text("৳ ${rec.amount}", fontWeight = FontWeight.Black, color = Color(0xFF2E7D32), fontSize = 16.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("অবস্থা:", fontWeight = FontWeight.Bold)
                            Text(
                                text = if (rec.status == "Paid") "পরিশোধিত" else "অপরিশোধিত",
                                fontWeight = FontWeight.Black,
                                color = if (rec.status == "Paid") Color(0xFF2E7D32) else Color.Red
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showReceipt = null }) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রসিদ প্রিন্ট")
                    }
                }
            )
        }
    }
}

@Composable
fun ExpenseView(viewModel: MilkSocietyViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val expenseList by viewModel.expenses.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpensesTotal.collectAsStateWithLifecycle()

    var showExpenseForm by remember { mutableStateOf(false) }

    // Form inputs
    var titleStr by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electricity") }

    val categories = listOf("Electricity", "Staff Salary", "Transport", "Maintenance", "Feed Cost", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = AppStrings.getString("tab_expenses", lang),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Button(onClick = { showExpenseForm = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন খরচ")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large total expense card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("মোট পরিচালন ব্যয়", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                    Text("৳ $totalExpense", fontWeight = FontWeight.Black, fontSize = 24.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                }
                Icon(Icons.Default.TrendingDown, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (expenseList.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("কোনো খরচের ভাউচার তালিকাভুক্ত নেই")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenseList) { exp ->
                    val catTransKey = when (exp.category) {
                        "Electricity" -> "cat_electricity"
                        "Staff Salary" -> "cat_salary"
                        "Transport" -> "cat_transport"
                        "Maintenance" -> "cat_maintenance"
                        "Feed Cost" -> "cat_feed"
                        else -> "cat_other"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = exp.title, fontWeight = FontWeight.Bold)
                                Text(text = AppStrings.getString(catTransKey, lang), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "তারিখ: ${exp.date}", fontSize = 11.sp, color = Color.Gray)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "৳ ${exp.amount}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Red)
                                IconButton(onClick = { viewModel.deleteExpense(exp) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showExpenseForm) {
            AlertDialog(
                onDismissRequest = { showExpenseForm = false },
                title = { Text(AppStrings.getString("add_expense", lang), fontWeight = FontWeight.ExtraBold) },
                text = {
                    Column {
                        RuralTextField(
                            value = titleStr,
                            onValueChange = { titleStr = it },
                            label = AppStrings.getString("expense_title", lang)
                        )
                        RuralTextField(
                            value = amountStr,
                            onValueChange = { amountStr = it },
                            label = AppStrings.getString("expense_amount", lang),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        RuralDropdown(
                            items = categories,
                            selectedItem = selectedCategory,
                            onItemSelected = { selectedCategory = it },
                            label = AppStrings.getString("expense_category", lang),
                            itemLabelProvider = {
                                val transKey = when (it) {
                                    "Electricity" -> "cat_electricity"
                                    "Staff Salary" -> "cat_salary"
                                    "Transport" -> "cat_transport"
                                    "Maintenance" -> "cat_maintenance"
                                    "Feed Cost" -> "cat_feed"
                                    else -> "cat_other"
                                }
                                AppStrings.getString(transKey, lang)
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val amt = amountStr.toDoubleOrNull() ?: 0.0
                        if (titleStr.isNotBlank() && amt > 0.0) {
                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val exp = ExpenseEntity(
                                expenseId = "EXP-${System.currentTimeMillis()}",
                                title = titleStr,
                                amount = amt,
                                date = today,
                                category = selectedCategory
                            )
                            viewModel.saveExpense(exp)
                            titleStr = ""
                            amountStr = ""
                            showExpenseForm = false
                        }
                    }) {
                        Text("সংরক্ষণ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExpenseForm = false }) {
                        Text(AppStrings.getString("cancel", lang))
                    }
                }
            )
        }
    }
}

@Composable
fun ReportsView(viewModel: MilkSocietyViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val collectionsList by viewModel.collections.collectAsStateWithLifecycle()
    val expensesList by viewModel.expenses.collectAsStateWithLifecycle()

    var activeReportType by remember { mutableStateOf("daily") } // "daily", "weekly", "expenses"
    var showExportSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.getString("reports_title", lang),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Row of tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("daily", "weekly", "expenses").forEach { tab ->
                val label = when (tab) {
                    "daily" -> "দৈনিক"
                    "weekly" -> "সাপ্তাহিক"
                    else -> "ব্যয়সমূহ"
                }
                Button(
                    onClick = { activeReportType = tab },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeReportType == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (activeReportType == tab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Display summary numbers based on active tab
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ElevatedButton(onClick = { showExportSuccess = true }) {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.getString("export_pdf", lang))
            }
            ElevatedButton(onClick = { showExportSuccess = true }) {
                Icon(Icons.Filled.DriveFileMove, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.getString("export_excel", lang))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeReportType == "daily") {
            // Render milk Collections
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("দৈনিক রেজিস্টার রেকর্ড সমূহ", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
                }
                if (collectionsList.isEmpty()) {
                    item { Text("কোনো তথ্য নেই", color = Color.Gray) }
                } else {
                    items(collectionsList) { col ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("সদস্য: ${col.memberId}", fontWeight = FontWeight.Bold)
                                    Text("তারিখ: ${col.date} (${col.shift})", fontSize = 12.sp)
                                }
                                Text("${col.quantity} L | ${col.milkType}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("৳ ${col.amount}", fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }
        } else if (activeReportType == "expenses") {
            // Render Expense reports log
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("ব্যয় রেজিস্টার বিবরণ", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
                }
                if (expensesList.isEmpty()) {
                    item { Text("কোনো তথ্য নেই", color = Color.Gray) }
                } else {
                    items(expensesList) { exp ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(exp.title, fontWeight = FontWeight.Bold)
                                    Text("বিভাগ: ${exp.category}", fontSize = 12.sp)
                                }
                                Text("৳ ${exp.amount}", fontWeight = FontWeight.Black, color = Color.Red)
                            }
                        }
                    }
                }
            }
        } else {
            // Weekly recap summary mock stats blocks
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("মোট দুগ্ধ সংগ্রহ পাক্ষিক খতিয়ান", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("মোট কাউ সংগ্রহ:")
                                Text("২৪৫.৫ লিটার", fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("মোট মহিষ সংগ্রহ:")
                                Text("১২০.২ লিটার", fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("পেমেন্ট ক্লিয়ার্ড:")
                                Text("৳ ২৫৪০.০০", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }
        }

        if (showExportSuccess) {
            AlertDialog(
                onDismissRequest = { showExportSuccess = false },
                title = { Text("রপ্তানি সফল হয়েছে", fontWeight = FontWeight.Bold) },
                text = { Text("ডকুমেন্ট সফলভাবে ফোন মেমোরির 'Downloads/MilkSamitiReports' ডিরেক্টরিতে সেভ হয়েছে।", fontSize = 14.sp) },
                confirmButton = {
                    Button(onClick = { showExportSuccess = false }) {
                        Text("ঠিক আছে")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsView(viewModel: MilkSocietyViewModel) {
    val context = LocalContext.current
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()

    var nameStr by remember { mutableStateOf(viewModel.societyName.value) }
    var addressStr by remember { mutableStateOf(viewModel.societyAddress.value) }
    var mobileStr by remember { mutableStateOf(viewModel.societyMobile.value) }

    var backupTextStr by remember { mutableStateOf("") }
    var showBackupConfirm by remember { mutableStateOf(false) }
    var showRestoreConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = AppStrings.getString("tab_settings", lang),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("সমিতি প্রোফাইল তথ্য", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))

                    RuralTextField(
                        value = nameStr,
                        onValueChange = { nameStr = it },
                        label = AppStrings.getString("society_name", lang)
                    )

                    RuralTextField(
                        value = addressStr,
                        onValueChange = { addressStr = it },
                        label = AppStrings.getString("society_address", lang)
                    )

                    RuralTextField(
                        value = mobileStr,
                        onValueChange = { mobileStr = it },
                        label = AppStrings.getString("society_mobile", lang),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LargeButton(
                        text = "সেটিংস সংরক্ষণ",
                        onClick = {
                            viewModel.saveSettings(nameStr, addressStr, mobileStr, isDark)
                            Toast.makeText(context, AppStrings.getString("settings_saved", lang), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ভাষা ও থিম কাস্টমাইজেশন", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ভাষা নির্বাচন (Language Select)", fontWeight = FontWeight.Bold)
                        TextButton(onClick = { viewModel.toggleLanguage() }) {
                            Text(
                                text = if (lang == Language.BENGALI) "ENGLISH" else "বাংলা (BENGALI)",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(AppStrings.getString("theme_settings", lang), fontWeight = FontWeight.Bold)
                        Switch(
                            checked = isDark,
                            onCheckedChange = {
                                viewModel.saveSettings(nameStr, addressStr, mobileStr, it)
                            }
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ডাটা ব্যাকআপ ও পুনরুদ্ধার (Local Backup/Restore)", fontWeight = FontWeight.Bold, color = Color.Red)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                backupTextStr = viewModel.performBackup()
                                showBackupConfirm = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(AppStrings.getString("backup_data", lang), fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                showRestoreConfirm = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(AppStrings.getString("restore_data", lang), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showBackupConfirm) {
        AlertDialog(
            onDismissRequest = { showBackupConfirm = false },
            title = { Text(AppStrings.getString("backup_data", lang), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("ব্যাকআপ সম্পন্ন হয়েছে! ব্যাকআপ কি সেভ করে টেক্সট কপি করতে চান?")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = backupTextStr,
                        onValueChange = { _ -> },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showBackupConfirm = false }) {
                    Text("ঠিক আছে")
                }
            }
        )
    }

    if (showRestoreConfirm) {
        var restorePayload by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false },
            title = { Text(AppStrings.getString("restore_data", lang), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("পুনরুদ্ধার করতে আপনার পূর্বে কপি করা ব্যাকআপ টেক্সট পেস্ট করুন:")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = restorePayload,
                        onValueChange = { restorePayload = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("পেস্ট করুন...") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (viewModel.performRestore(restorePayload)) {
                        Toast.makeText(context, AppStrings.getString("restore_success", lang), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "ব্যর্থ! ব্যাকআপ টেক্সট সঠিক নয়।", Toast.LENGTH_SHORT).show()
                    }
                    showRestoreConfirm = false
                }) {
                    Text("রিস্টোর রিকোয়েস্ট")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirm = false }) {
                    Text(AppStrings.getString("cancel", lang))
                }
            }
        )
    }
}
