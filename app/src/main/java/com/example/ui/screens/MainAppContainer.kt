package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.translation.AppStrings
import com.example.ui.translation.Language
import com.example.ui.viewmodel.MilkSocietyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: MilkSocietyViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf("dashboard") }

    if (currentUser == null) {
        AuthView(viewModel = viewModel)
    } else {
        val user = currentUser!!
        val isOperator = user.role == "OPERATOR"

        // Modal Drawer layout structure
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(320.dp),
                    drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                ) {
                    // Header for cooperative name and operator status
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = viewModel.societyName.value,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${AppStrings.getString("operator", lang)}: ${user.name}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Menu items
                    val menuItems = listOf(
                        Triple("dashboard", Icons.Default.Dashboard, "tab_dashboard"),
                        Triple("members", Icons.Default.Group, "tab_members"),
                        Triple("collection", Icons.Default.Water, "tab_collection"),
                        Triple("rates", Icons.Default.TrendingUp, "tab_rates"),
                        Triple("payments", Icons.Default.Payments, "tab_payments"),
                        Triple("expenses", Icons.Default.Receipt, "tab_expenses"),
                        Triple("reports", Icons.Default.Assessment, "tab_reports"),
                        Triple("settings", Icons.Default.Settings, "tab_settings")
                    )

                    lazyListOfMenus(menuItems, currentTab, isOperator, lang) { tabId ->
                        currentTab = tabId
                        scope.launch { drawerState.close() }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout Area
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                        label = { Text(AppStrings.getString("logout", lang), color = Color.Red, fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = { viewModel.logout() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        ) {
            Scaffold(
                topBar = {
                    val headerTitle = when (currentTab) {
                        "dashboard" -> "tab_dashboard"
                        "members" -> "tab_members"
                        "collection" -> "tab_collection"
                        "rates" -> "tab_rates"
                        "payments" -> "tab_payments"
                        "expenses" -> "tab_expenses"
                        "reports" -> "tab_reports"
                        else -> "tab_settings"
                    }

                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = AppStrings.getString(headerTitle, lang),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            // Instant header language picker
                            IconButton(onClick = { viewModel.toggleLanguage() }) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Language",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                        )
                    )
                },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        "dashboard" -> DashboardView(viewModel = viewModel, onNavigate = { currentTab = it })
                        "members" -> MemberManagementView(viewModel = viewModel)
                        "collection" -> MilkCollectionView(viewModel = viewModel)
                        "rates" -> RateChartView(viewModel = viewModel)
                        "payments" -> PaymentView(viewModel = viewModel)
                        "expenses" -> ExpenseView(viewModel = viewModel)
                        "reports" -> ReportsView(viewModel = viewModel)
                        "settings" -> SettingsView(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.lazyListOfMenus(
    items: List<Triple<String, androidx.compose.ui.graphics.vector.ImageVector, String>>,
    selectedTab: String,
    isOperator: Boolean,
    lang: Language,
    onSelect: (String) -> Unit
) {
    items.forEach { triple ->
        val tabId = triple.first
        val icon = triple.second
        val stringKey = triple.third

        // Simple Operator Role Access constraint check
        // An Operator from village staff is restricted to Dashboard, Members, Milk Collection and Reports
        val isAllowed = !isOperator || (tabId != "rates" && tabId != "settings")

        if (isAllowed) {
            NavigationDrawerItem(
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(AppStrings.getString(stringKey, lang), fontWeight = FontWeight.Bold) },
                selected = selectedTab == tabId,
                onClick = { onSelect(tabId) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }
    }
}
