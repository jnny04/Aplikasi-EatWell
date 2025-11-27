package com.example.recappage.ui.screens

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
import androidx.compose.ui.graphics.ColorFilter


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
@Composable
fun HomePage(navController: NavHostController) {

    val recViewModel: RecommendationViewModel = hiltViewModel()
    val randomFood by recViewModel.randomFood.collectAsState()

    // 1. PANGGIL REGISTRATION VIEWMODEL (Untuk ambil data user)
    val regViewModel: RegistrationViewModel = hiltViewModel()
    // Atau pakai: val regViewModel: RegistrationViewModel = viewModel() jika pakai navigation compose standard

    val intakeViewModel: IntakeViewModel = hiltViewModel()
    val favViewModel: FavouriteViewModel = hiltViewModel()
    // Asumsi: favViewModel.favourites adalah list yang bisa langsung diakses ukurannya
    val savedCount = favViewModel.favourites.size

    val consumedCalories by intakeViewModel.totalCaloriesToday.collectAsState()
    // 2. AMBIL DATA GOAL DARI STATE VIEWMODEL
    // (State ini otomatis update kalau loadUserProfile sukses)
    val userGoal = regViewModel.dailyCalorieGoal.intValue

    // ✅ AMBIL DATA MACROS (CONSUMED)
    val cCarbs by intakeViewModel.totalCarbsToday.collectAsState()
    val cProtein by intakeViewModel.totalProteinToday.collectAsState()
    val cFat by intakeViewModel.totalFatToday.collectAsState()

    // ✅ AMBIL DATA MACROS (TARGET)
    val tCarbs by intakeViewModel.targetCarbs.collectAsState()
    val tProtein by intakeViewModel.targetProtein.collectAsState()
    val tFat by intakeViewModel.targetFat.collectAsState()

    val profilePicUrl = regViewModel.profileImageUrl.value

    // 3. LOAD DATA SAAT HOMEPAGE DIBUKA
    LaunchedEffect(Unit) {
        recViewModel.loadFoods()
        regViewModel.loadUserProfile() // <--- Ambil data dari Firestore
    }

    var showResult by remember { mutableStateOf(false) }
    var dietary by remember { mutableStateOf("vegan") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { recViewModel.loadFoods() }

    // ====================================================
    // SCAFFOLD (BOTTOM BAR ADA DI DALAM)
    // ====================================================
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Component18(
                modifier = Modifier.fillMaxWidth(),
                navController = navController
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 5.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // 2. MASUKKAN KE TOPBORDER
                TopBorder(
                    navController = navController,
                    photoUrl = profilePicUrl // ✅ TAMBAHKAN INI
                )

                Spacer(Modifier.height(16.dp))
                TodayHeader(navController)
                Spacer(Modifier.height(16.dp))
                // 4. KIRIM DATA 'userGoal' KE KARTU
                HomeHorizontalCards(
                    navController = navController,
                    dailyGoal = userGoal,
                    consumed = consumedCalories,
                    savedCount = savedCount,
                    // ✅ PASSING DATA MACROS
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
                    setDietary = { dietary = it }
                )
            }
        }
    }

    // ====================================================
    // POPUP DI LUAR SCAFFOLD → SELALU DI ATAS BOTTOM BAR
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
@Composable
fun SpinWheelSection(
    navController: NavHostController,
    recViewModel: RecommendationViewModel,
    randomFood: Recipe?,
    showResult: Boolean,
    setShowResult: (Boolean) -> Unit,
    dietary: String,
    setDietary: (String) -> Unit
) {
    var spinning by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // State Rotasi
    var rotationValue by remember { mutableFloatStateOf(0f) }

    // Logika Spin
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
            // 1. KURANGI TINGGI BOX (Supaya tidak terlalu makan tempat ke bawah)
            // Dari 420.dp -> 380.dp
            .height(380.dp)
    ) {

        // 1. BAGIAN KIRI ATAS: MY DIETARY & TEXT
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 20.dp) // Padding top disesuaikan
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
                .padding(bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            SpinWheel(
                // 2. PERKECIL UKURAN RODA
                // Dari 340.dp -> 290.dp (Agar tidak nabrak teks)
                modifier = Modifier.size(290.dp),
                rotationTarget = rotationValue,
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
                // 3. SESUAIKAN POSISI BUBBLE
                // top dinaikkan ke 100.dp agar pas dengan roda yang mengecil
                .padding(end = 40.dp, top = 100.dp)
                .size(80.dp)
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
    onClick: () -> Unit
) {
    val animatedRotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec = tween(3000, easing = LinearOutSlowInEasing),
        label = "spin"
    )

    Box(
        modifier = modifier, // Ukuran dari luar (340.dp)
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: RODA PUTAR (Hijau/Jari-jari) - BERPUTAR
        // PENTING: sp2 harus di belakang frame sp1 jika sp1 adalah frame
        // TAPI: Berdasarkan gambar referensi (Orange memeluk Hijau),
        // Hijau (sp2) di layer bawah, Orange (sp1) di layer atas.

        Image(
            painter = painterResource(id = R.drawable.sp2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.9f) // Sedikit lebih kecil dari frame agar pas di dalam
                .graphicsLayer { rotationZ = animatedRotation }, // 🔥 INI YANG BERPUTAR
            contentScale = ContentScale.Fit
        )

        // LAYER 2: BASE / FRAME (Orange) - DIAM
        Image(
            painter = painterResource(id = R.drawable.sp1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(), // Mengisi penuh kotak 340dp
            contentScale = ContentScale.Fit
        )

        // LAYER 3: TOMBOL TENGAH "TAP" (Putih) - DIAM
        // Ini membuat tampilan persis seperti desain target
        Box(
            modifier = Modifier
                .size(65.dp) // Ukuran lingkaran tengah
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Color(0xFFF0F0F0), CircleShape) // Border halus
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TAP",
                color = Color(0xFFE0E0E0), // Warna abu-abu muda
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = SourceSerifPro
            )
        }
    }
}