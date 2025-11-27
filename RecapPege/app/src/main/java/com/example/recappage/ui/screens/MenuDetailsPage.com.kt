package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.model.Recipe
import com.example.recappage.model.RecipeInformation
import com.example.recappage.ui.components.*
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.viewmodel.MainViewModel
import com.example.recappage.ui.viewmodel.IntakeViewModel
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.viewmodel.FavouriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDetailsPage(
    navController: NavHostController,
    foodId: Int,
    viewModel: MainViewModel = hiltViewModel(),
    intakeViewModel: IntakeViewModel = hiltViewModel(),
    regViewModel: RegistrationViewModel = hiltViewModel(),
    favViewModel: FavouriteViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    val recipe = viewModel.recipeDetail.collectAsState().value
    val macroData = viewModel.macroData.collectAsState().value
    val isFav = favViewModel.favourites.any { it.id == foodId }

    // Load Profile
    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }
    val profilePicUrl = regViewModel.profileImageUrl.value

    // Load Recipe
    LaunchedEffect(foodId) {
        viewModel.loadRecipeDetail(foodId)
    }

    // State Bottom Sheet
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopBorder(
                navController = navController,
                showProfile = true,
                photoUrl = profilePicUrl
            )
        },
        bottomBar = {
            Component18(navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        // Gunakan Box untuk menumpuk gambar dan BottomSheetScaffold
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // 1. GAMBAR BACKGROUND (Di belakang Sheet)
            AsyncImage(
                model = recipe?.image,
                contentDescription = recipe?.title ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp) // Tinggi gambar fixed
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.chicsaladwrap)
            )

            // Tombol Navigasi Overlay (Back & Fav)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp) // Samakan tinggi dengan gambar
            ) {
                // Back Button
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF5CA135)),
                    modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp)
                        .size(28.dp)
                        .align(Alignment.TopStart)
                        .clickable { navController.popBackStack() }
                )

                // Favorite Button
                Image(
                    painter = painterResource(
                        id = if (isFav) R.drawable.new_love else R.drawable.heartmenudetails
                    ),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 20.dp)
                        .size(28.dp)
                        .clickable {
                            if (recipe != null) {
                                val recipeModel = mapRecipeInfoToRecipe(recipe)
                                favViewModel.toggleFavorite(recipeModel)
                            }
                        }
                )
            }

            // 2. BOTTOM SHEET SCAFFOLD
            // Gunakan backgroundColor Transparent agar gambar di belakang terlihat
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                // ✅ Ubah sheetContainerColor menjadi warna Surface (Putih/Abu Gelap)
                // Ini membuat handle drag menyatu dengan warna kartu
                sheetContainerColor = MaterialTheme.colorScheme.surface,
                sheetContentColor = MaterialTheme.colorScheme.onSurface,
                sheetShadowElevation = 8.dp, // Bayangan agar sheet terlihat menimpa gambar
                sheetPeekHeight = 350.dp, // ✅ Atur tinggi intipan agar pas di bawah gambar (sesuaikan jika perlu)
                sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                // Background scaffold dibuat transparan agar gambar di `Box` induk terlihat
                containerColor = Color.Transparent,
                sheetContent = {
                    // === ISI SHEET ===
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f) // Sheet bisa ditarik sampai 90% layar
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // JUDUL MAKANAN
                        Text(
                            text = recipe?.title ?: "Loading...",
                            fontSize = 24.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // TOTAL CALORIES ROW
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "Total Calories",
                                fontSize = 16.sp,
                                fontFamily = SourceSans3,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = macroData?.calories?.replace(" kcal", "") ?: "0",
                                fontSize = 16.sp,
                                fontFamily = SourceSans3,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "kcal",
                                fontSize = 12.sp,
                                fontFamily = SourceSans3,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // MACROS LIST
                        macroData?.let {
                            MacroRowDetail("Carbs", it.carbs, Color(0xFF40C4FF))
                            MacroRowDetail("Protein", it.protein, Color(0xFFFFEE58))
                            MacroRowDetail("Fat", it.fat, Color(0xFFB2DFDB))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // INGREDIENTS
                        Text(
                            text = "Ingredients",
                            fontSize = 18.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFC7100),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        recipe?.extendedIngredients?.forEachIndexed { index, ing ->
                            Text(
                                text = "${index + 1}. ${ing.original}",
                                fontSize = 13.sp,
                                fontFamily = SourceSans3,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // STEP BY STEP
                        Text(
                            text = "Step by step",
                            fontSize = 18.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5CA135),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (!recipe?.instructions.isNullOrBlank()) {
                            val cleanInstructions = recipe!!.instructions.replace(Regex("<.*?>"), "")
                            cleanInstructions.split(".").forEachIndexed { idx, step ->
                                if (step.isNotBlank() && step.length > 3) {
                                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                        Text(
                                            text = "Step ${idx + 1}",
                                            fontSize = 14.sp,
                                            fontFamily = SourceSans3,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = step.trim(),
                                            fontSize = 13.sp,
                                            fontFamily = SourceSans3,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        // ADD BUTTON
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AddToIntakeButton(
                                onClick = {
                                    val title = recipe?.title ?: "Unknown Food"
                                    val imageLink = recipe?.image
                                    val carbs = macroData?.carbs?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                                    val protein = macroData?.protein?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                                    val fat = macroData?.fat?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0

                                    intakeViewModel.addEntry(
                                        name = title,
                                        carbs = carbs,
                                        protein = protein,
                                        fat = fat,
                                        imageUrl = imageLink
                                    )
                                    showDialog = true
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            ) {
                // Kosongkan content scaffold, karena kita pakai Box di luar untuk gambar
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }

    if (showDialog) {
        SuccessDialog(onDismiss = { showDialog = false })
    }
}

// ✅ HELPER: Konversi RecipeInformation (Detail) ke Recipe (Model Simpan)
fun mapRecipeInfoToRecipe(info: RecipeInformation): Recipe {
    return Recipe(
        aggregateLikes = 0,
        analyzedInstructions = emptyList(),
        cheap = info.cheap,
        cookingMinutes = info.cookingMinutes,
        creditsText = info.creditsText,
        cuisines = info.cuisines.map { it.toString() },
        dairyFree = info.dairyFree,
        diets = info.diets.map { it.toString() },
        dishTypes = info.dishTypes,
        extendedIngredients = emptyList(), // Tidak perlu simpan detail ingredient di fav list
        gaps = info.gaps,
        glutenFree = info.glutenFree,
        healthScore = info.healthScore,
        id = info.id,
        image = info.image,
        imageType = info.imageType,
        instructions = info.instructions,
        license = info.license,
        lowFodmap = info.lowFodmap,
        occasions = emptyList(),
        originalId = 0,
        preparationMinutes = info.preparationMinutes,
        pricePerServing = info.pricePerServing,
        readyInMinutes = info.readyInMinutes,
        servings = info.servings,
        sourceName = info.sourceName,
        sourceUrl = info.sourceUrl,
        spoonacularScore = info.spoonacularScore,
        spoonacularSourceUrl = info.spoonacularSourceUrl,
        summary = info.summary,
        sustainable = info.sustainable,
        title = info.title,
        vegan = info.vegan,
        vegetarian = info.vegetarian,
        veryHealthy = info.veryHealthy,
        veryPopular = info.veryPopular,
        weightWatcherSmartPoints = info.weightWatcherSmartPoints
    )
}

@Composable
fun MacroRowDetail(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Normal,
            color = color
        )
    }
}

@Composable
fun AddToIntakeButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(45.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFF5CA135))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Add to Today's Intake",
            fontFamily = SourceSerifPro,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}