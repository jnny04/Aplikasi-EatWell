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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.example.recappage.ui.viewmodel.RegistrationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel()
import com.example.recappage.ui.viewmodel.IntakeViewModel // ✅ Import IntakeViewModel
import com.example.recappage.ui.viewmodel.FavouriteViewModel

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
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 5.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                TopBorder(navController = navController)
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
            onOrderClick = {},
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
            color = Color.Black
        )

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp),
        contentAlignment = Alignment.TopStart
    ) {

        // SPIN WHEEL DISPLAY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            SpinWheel(
                modifier = Modifier.size(360.dp),
                onClick = {
                    if (!spinning) {
                        spinning = true
                        recViewModel.spin(dietary)

                        coroutine.launch {
                            delay(3000)
                            setShowResult(true)
                        }
                        coroutine.launch {
                            delay(3000)
                            spinning = false
                        }
                    }
                }
            )

            Image(
                painter = painterResource(
                    id = if (spinning) R.drawable.spin2 else R.drawable.spin
                ),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-20).dp, y = (-130).dp)
                    .size(82.dp)
            )
        }

        // MY DIETARY SECTION
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Box(
                modifier = Modifier
                    .width(122.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF5CA135))
                    .clickable { showFilterSheet = true },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "My Dietary",
                        fontSize = 16.sp,
                        fontFamily = SourceSerifPro,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(6.dp))
                    Image(
                        painter = painterResource(id = R.drawable.downarrow),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                "Hungry but can’t decide? Tap\nhere to discover options!",
                fontFamily = SourceSans3,
                fontSize = 10.sp,
                color = Color(0xFF5CA135),
                lineHeight = 12.sp
            )
        }
    }

    // FILTER SHEET (BELUM DIUBAH)
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
    onClick: () -> Unit = {}
) {
    var rotation by remember { mutableStateOf(0f) }
    var spinning by remember { mutableStateOf(false) }

    val animated by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(3000, easing = LinearOutSlowInEasing),
        finishedListener = { spinning = false },
        label = "spin"
    )

    Box(
        modifier = modifier.clickable(enabled = !spinning) {
            rotation += (720..1800).random().toFloat()
            spinning = true
            onClick()
        },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.sp2),
            contentDescription = null,
            modifier = Modifier
                .size(280.dp)
                .offset(y = (-13).dp)
                .graphicsLayer { rotationZ = animated },
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.sp1),
            contentDescription = null,
            modifier = Modifier.size(260.dp),
            contentScale = ContentScale.Fit
        )
    }
}
