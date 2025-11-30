package com.example.recappage.ui.screens

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.components.home.HomeHorizontalCards
import com.example.recappage.ui.components.FoodPreviewPopup
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recappage.model.Recipe
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.viewmodel.RecommendationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.viewmodel.IntakeViewModel // ✅ Import IntakeViewModel
import com.example.recappage.ui.viewmodel.FavouriteViewModel
import androidx.compose.runtime.getValue // Pastikan ini ada untuk 'by' delegate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import com.example.recappage.util.ShakeDetector
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import com.example.recappage.ui.theme.TapRing
import com.example.recappage.ui.theme.TapText


fun Scaffold(
    modifier: Modifier,
    containerColor: Modifier,
    contentWindowInsets: WindowInsets,
    bottomBar: () -> Unit,
    content: (PaddingValues) -> Unit
) {
}

// -------------------------------------------------------------
// MAIN HOMEPAGE
// -------------------------------------------------------------
// -------------------------------------------------------------
// MAIN HOMEPAGE (FIXED TOP BORDER)
// -------------------------------------------------------------
@Composable
fun HomePage(navController: NavHostController) {

    val appcontext = LocalContext.current

    val batteryStatus = appcontext.registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    )

    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
    val batteryLevel = (level * 100) / scale

    val isLowBattery = batteryLevel <= 20

    android.util.Log.d("BATTERY_MONITOR", "Level Baterai: $batteryLevel%. Mode Hemat Daya: $isLowBattery. Durasi Animasi: ${if (isLowBattery) 800 else 3000}ms")

    val recViewModel: RecommendationViewModel = hiltViewModel()
    val randomFood by recViewModel.randomFood.collectAsState()

    // 1. PANGGIL REGISTRATION VIEWMODEL (Untuk ambil data user)
    val regViewModel: RegistrationViewModel = hiltViewModel()

    val intakeViewModel: IntakeViewModel = hiltViewModel()
    val favViewModel: FavouriteViewModel = hiltViewModel()
    val savedCount = favViewModel.favourites.size

    val consumedCalories by intakeViewModel.totalCaloriesToday.collectAsState()
    val userGoal = regViewModel.dailyCalorieGoal.intValue

    // ✅ AMBIL DATA MACROS
    val cCarbs by intakeViewModel.totalCarbsToday.collectAsState()
    val cProtein by intakeViewModel.totalProteinToday.collectAsState()
    val cFat by intakeViewModel.totalFatToday.collectAsState()

    val tCarbs by intakeViewModel.targetCarbs.collectAsState()
    val tProtein by intakeViewModel.targetProtein.collectAsState()
    val tFat by intakeViewModel.targetFat.collectAsState()

    val profilePicUrl = regViewModel.profileImageUrl.value

    // 3. LOAD DATA SAAT HOMEPAGE DIBUKA
    LaunchedEffect(Unit) {
        recViewModel.loadFoods()
        regViewModel.loadUserProfile()
    }

    var showResult by remember { mutableStateOf(false) }
    var dietary by remember { mutableStateOf("vegan") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { recViewModel.loadFoods() }

    // ====================================================
    // SCAFFOLD (TOP BAR & BOTTOM BAR FIXED)
    // ====================================================
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        // 🔥 PERUBAHAN 1: TopBorder dipindah ke sini agar diam di atas
        topBar = {
            TopBorder(
                navController = navController,
                photoUrl = profilePicUrl
            )
        },

        bottomBar = {
            Component18(
                modifier = Modifier.fillMaxWidth(),
                navController = navController
            )
        }
    ) { innerPadding ->

        // 🔥 PERUBAHAN 2: Gunakan innerPadding agar konten turun ke bawah TopBar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Ini penting agar tidak tertutup TopBar/BottomBar
                .background(MaterialTheme.colorScheme.background)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 5.dp)
                    .verticalScroll(rememberScrollState()) // Scroll hanya berlaku di sini
            ) {

                // ❌ TopBorder LAMA DIHAPUS DARI SINI

                Spacer(Modifier.height(16.dp))
                TodayHeader(navController)
                Spacer(Modifier.height(16.dp))

                HomeHorizontalCards(
                    navController = navController,
                    dailyGoal = userGoal,
                    consumed = consumedCalories,
                    savedCount = savedCount,
                    consumedCarbs = cCarbs, targetCarbs = tCarbs,
                    consumedProtein = cProtein, targetProtein = tProtein,
                    consumedFat = cFat, targetFat = tFat
                )
                Spacer(Modifier.height(24.dp))

                SpinWheelSection(
                    navController = navController,
                    recViewModel = recViewModel,
                    randomFood = randomFood,
                    showResult = showResult,
                    setShowResult = { showResult = it },
                    dietary = dietary,
                    setDietary = { dietary = it },
                    isLowBattery = isLowBattery
                )
            }
        }
    }

    // ====================================================
    // POPUP DI LUAR SCAFFOLD
    // ====================================================
    val food = randomFood
    val context = androidx.compose.ui.platform.LocalContext.current
    if (showResult && food != null) {
        FoodPreviewPopup(
            image = food.image,
            title = food.title,
            detail = null,
            summary = food.summary,
            onClose = { showResult = false },
            onRecipeClick = {
                showResult = false
                navController.navigate(Screen.MenuDetails.createRoute(food.id))
            },
            onOrderClick = {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://food.grab.com/id/en/"))
                context.startActivity(intent)
            },
            fromHomePage = true,
            onSpinAgain = {
                showResult = false
                recViewModel.spin(dietary)
                scope.launch {
                    delay(3000)
                    showResult = true
                }
            }
        )
    }
}

// -------------------------------------------------------------
// HEADER (TODAY + SEARCH BAR)
// -------------------------------------------------------------
@Composable
fun TodayHeader(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "Today",
            fontFamily = SourceSerifPro,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground        )
        Row(
            modifier = Modifier
                .width(220.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFFFF7A00),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { navController.navigate("pencarian") }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Got something on your mind?",
                fontSize = 8.sp,
                fontFamily = SourceSans3,
                color = Color(0xFF8C8C8C),
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            Image(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = null,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// SPIN WHEEL SECTION
// -------------------------------------------------------------
// File: HomePage.kt

@Composable
fun SpinWheelSection(
    navController: NavHostController,
    recViewModel: RecommendationViewModel,
    randomFood: Recipe?,
    showResult: Boolean,
    setShowResult: (Boolean) -> Unit,
    dietary: String,
    setDietary: (String) -> Unit,
    isLowBattery: Boolean // ✅ TAMBAHAN
) {
    var spinning by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var rotationValue by remember { mutableFloatStateOf(0f) }

    val performSpin = {
        if (!spinning && !showResult) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            rotationValue += (720..1800).random().toFloat()
            spinning = true
            recViewModel.spin(dietary)

            coroutine.launch {
                delay(3000)
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                setShowResult(true)
            }
            coroutine.launch {
                delay(3000)
                spinning = false
            }
        }
    }

    val shakeDetector = remember { ShakeDetector(context) }
    DisposableEffect(Unit) {
        shakeDetector.start { performSpin() }
        onDispose { shakeDetector.stop() }
    }

    // ==========================================
    // LAYOUT UTAMA
    // ==========================================
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // 🔥 1. KURANGI TINGGI BOX (Supaya roda naik ke atas)
            // Dari 380.dp -> 320.dp
            .height(320.dp)
    ) {

        // 1. BAGIAN KIRI ATAS: MY DIETARY & TEXT
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 10.dp) // Padding top dikurangi dikit
        ) {
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF5CA135))
                    .clickable { showFilterSheet = true },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "My Dietary",
                        fontSize = 16.sp,
                        fontFamily = SourceSerifPro,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Image(
                        painter = painterResource(id = R.drawable.downarrow),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Hungry but can’t decide? Tap\nhere to discover options!",
                fontFamily = SourceSans3,
                fontSize = 11.sp,
                color = Color(0xFF5CA135),
                lineHeight = 14.sp
            )
        }

        // 2. BAGIAN TENGAH BAWAH: RODA PUTAR
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp), // Padding bawah dikurangi
            contentAlignment = Alignment.Center
        ) {
            SpinWheel(
                modifier = Modifier.size(270.dp),
                rotationTarget = rotationValue,
                isLowBattery = isLowBattery, // ✅ MASUKKAN INI
                onClick = { performSpin() }
            )
        }

        // 3. BAGIAN KANAN ATAS: BUBBLE "SPIN ME!"
        Image(
            painter = painterResource(
                id = if (spinning) R.drawable.spin2 else R.drawable.spin
            ),
            contentDescription = "Spin Me",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 40.dp, top = 50.dp)
                .size(80.dp)
                .clickable { performSpin() }   // ⬅️ ini aja
        )
    }

    if (showFilterSheet) {
        com.example.recappage.ui.components.FilterDialog(
            onDismiss = { showFilterSheet = false },
            onApply = { selected ->
                setDietary(
                    buildString {
                        if (selected.vegan) append("vegan,")
                        if (selected.vegetarian) append("vegetarian,")
                        if (selected.halal) append("halal,")
                        if (selected.lowCarb) append("low carb,")
                        if (selected.pescatarian) append("pescatarian,")
                        if (selected.nutsFree) append("nuts-free,")
                        if (selected.dairyFree) append("dairy-free,")
                        if (selected.glutenFree) append("gluten-free,")
                        if (selected.eggFree) append("egg-free,")
                        if (selected.noSeafood) append("no-seafood,")
                        if (selected.asian) append("asian,")
                        if (selected.european) append("european,")
                        if (selected.thai) append("thai,")
                        if (selected.chinese) append("chinese,")
                        if (selected.korean) append("korean,")
                        if (selected.japanese) append("japanese,")
                        if (selected.italian) append("italian,")
                        if (selected.indian) append("indian,")
                    }.removeSuffix(",")
                )
                showFilterSheet = false
            }
        )
    }
}

// -------------------------------------------------------------
// SPIN WHEEL IMAGE & ANIMATION
// -------------------------------------------------------------
@Composable
fun SpinWheel(
    modifier: Modifier = Modifier,
    rotationTarget: Float,
    isLowBattery: Boolean, // ✅ TAMBAHAN BARU
    onClick: () -> Unit
) {
    // ⬇️ Durasi animasi berubah sesuai baterai
    val animationDuration = if (isLowBattery) 800 else 3000

    val animatedRotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = LinearOutSlowInEasing
        ),
        label = "spin"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sp2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(1.0f)
                .offset(y = 2.dp)
                .graphicsLayer { rotationZ = animatedRotation },
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center
        )

        Image(
            painter = painterResource(id = R.drawable.sp1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.97f)
                .offset(y = 15.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(y = 5.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        val radius = size.minDimension / 2f * 1.35f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x33000000),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = radius
                            )
                        )
                    }
            )

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        color = TapRing,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TAP",
                        color = TapText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        fontFamily = SourceSerifPro
                    )
                }
            }
        }
    }
}
