package com.example.recappage.ui.screens

import android.content.Intent
import android.net.Uri
import android.app.Activity
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recappage.R
import com.example.recappage.model.Recipe
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.FilterDialog
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.components.FoodPreviewPopup
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.viewmodel.MainViewModel
import com.example.recappage.ui.viewmodel.FavouriteViewModel
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.util.ImageHelper
import com.example.recappage.util.NetworkResult
import java.net.URLDecoder

@Composable
fun FoodLibraryPage(
    navController: NavHostController,
    searchQuery: String? = null,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    favVM: FavouriteViewModel
) {
    val regViewModel: RegistrationViewModel = hiltViewModel()
    val context = LocalContext.current

    val detail by viewModel.recipeDetail.collectAsState()
    val topCategories = listOf("All", "Breakfast", "Heavy Meal", "Snacks", "Dessert")
    var selectedTop by remember { mutableStateOf("All") }
    var showFilterPopup by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }

    var showPopup by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    // 🔥 STATE UNTUK AVAILABLE ORDER TOGGLE (UI ONLY)
    var isOrderAvailable by remember { mutableStateOf(false) }

    val filterIcon = if (showFilterPopup) R.drawable.filter_on else R.drawable.filter

    val decodedQuery = remember(searchQuery) {
        searchQuery?.let { URLDecoder.decode(it, "UTF-8") }
    }

    var queryText by remember { mutableStateOf(decodedQuery ?: "") }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)

            if (!spokenText.isNullOrBlank()) {
                queryText = spokenText
                isSearchMode = true
                selectedTop = "All"
                viewModel.searchRecipesByKeyword(spokenText)
            }
        }
    }

    val paginatedRecipes = viewModel.paginatedRecipes
    val isPaginating = viewModel.isPaginating
    val recipesState by viewModel.recipesResponse.observeAsState()

    val scrollState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }
    val profilePicUrl = regViewModel.profileImageUrl.value

    LaunchedEffect(decodedQuery) {
        if (decodedQuery != null) {
            queryText = decodedQuery
            isSearchMode = true
            selectedTop = "All"
            viewModel.searchRecipesByKeyword(decodedQuery)
        } else {
            isSearchMode = false
            viewModel.loadAllRecipes()
        }
    }

    val shouldPaginate = remember {
        derivedStateOf {
            val totalItemsCount = scrollState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex =
                scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItemsCount > 0 && lastVisibleItemIndex >= (totalItemsCount - 6)
        }
    }

    LaunchedEffect(shouldPaginate.value) {
        if (shouldPaginate.value && !isPaginating && isSearchMode && queryText.isNotEmpty()) {
            viewModel.loadNextPage(queryText)
        }
    }

    LaunchedEffect(recipesState) {
        if (recipesState is NetworkResult.Error<*>) {
            snackbarHostState.showSnackbar(
                message = "Terjadi masalah saat memuat resep. Periksa koneksi Anda dan coba lagi."
            )
        }
    }

    // ============================
    //  WRAPPER BOX UTAMA
    // ============================
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            topBar = {
                TopBorder(
                    navController = navController,
                    photoUrl = profilePicUrl
                )
            },
            bottomBar = {
                Component18(
                    modifier = Modifier.zIndex(3f),
                    navController = navController
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // =====================
//  KATEGORI + SEARCH ICON
// =====================
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .offset(y = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ============================
                    // 1. ROW KATEGORI + SEARCH ICON
                    // ============================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // === KATEGORI ===
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(topCategories.size) { index ->
                                val category = topCategories[index]
                                CategoryChip(
                                    text = category,
                                    selected = selectedTop == category,
                                    onClick = {
                                        isSearchMode = false
                                        queryText = ""
                                        selectedTop = category
                                        val tag = when (category) {
                                            "Breakfast" -> "breakfast"
                                            "Heavy Meal" -> "main course"
                                            "Snacks" -> "snack"
                                            "Dessert" -> "dessert"
                                            else -> null
                                        }
                                        if (category == "All") {
                                            viewModel.loadAllRecipes()
                                        } else {
                                            viewModel.loadRecipesByTag(tag)
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // === SEARCH ICON (tetap ada) ===
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    // 🔥 PERUBAHAN DI SINI: Navigasi ke layar pencarian
                                    navController.navigate("pencarian")
                                }
                        )
                    }
                }
                // =====================
//  AVAILABLE (kiri) + FILTER (kanan)
// =====================
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 60.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // === AVAILABLE ORDER (KIRI, DIPERBESAR PAKSA) ===
                    // === AVAILABLE ORDER (TANPA ANIMASI) ===
                    Image(
                        painter = painterResource(
                            id = if (isOrderAvailable) R.drawable.avail_order_on
                            else R.drawable.avail_order_off
                        ),
                        contentDescription = "Available For Order",

                        contentScale = ContentScale.FillBounds,

                        modifier = Modifier
                            .width(if (isOrderAvailable) 230.dp else 224.dp)
                            .height(if (isOrderAvailable) 40.dp else 36.dp)

                            .clickable {
                                isOrderAvailable = !isOrderAvailable
                            }
                    )

                    // === FILTER (KANAN, diperbesar) ===
                    Image(
                        painter = painterResource(id = filterIcon),
                        contentDescription = "Filter",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable { showFilterPopup = true }
                    )
                }

                // =====================
                //  GRID RESEP
                // =====================
                val state = recipesState
                if (state is NetworkResult.Loading && paginatedRecipes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5CA135))
                    }
                } else if (state is NetworkResult.Error<*> && paginatedRecipes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚠️ ${state.message ?: "Terjadi kesalahan saat memuat resep"}",
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    if (paginatedRecipes.isNotEmpty()) {
                        LazyVerticalGrid(
                            state = scrollState,
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(y = 110.dp)
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 80.dp)
                        ){
                            items(paginatedRecipes.size) { index ->
                                val recipe = paginatedRecipes[index]
                                RecipeCard(
                                    recipe = recipe,
                                    navController = navController,
                                    favVM = favVM,
                                    onSelect = {
                                        selectedRecipe = recipe
                                        viewModel.loadRecipeDetail(recipe.id)
                                        showPopup = true
                                    }
                                )
                            }
                            if (isPaginating) {
                                item(span = { GridItemSpan(2) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color(0xFF5CA135),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================
        //  FILTER POPUP (DI LUAR SCAFFOLD)
        // ==========================
        if (showFilterPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(20f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { showFilterPopup = false }
                )
                FilterDialog(
                    onDismiss = { showFilterPopup = false },
                    onApply = { filter ->
                        isSearchMode = false
                        selectedTop = "All"
                        queryText = ""
                        viewModel.loadWithFilter(filter)
                        showFilterPopup = false
                    }
                )
            }
        }

        // ==========================
        //  FOOD PREVIEW POPUP
        // ==========================
        if (showPopup && selectedRecipe != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(20f)
            ) {
                FoodPreviewPopup(
                    image = selectedRecipe!!.image,
                    title = selectedRecipe!!.title,
                    detail = detail,
                    onClose = { showPopup = false },
                    onRecipeClick = {
                        navController.navigate(
                            Screen.MenuDetails.createRoute(selectedRecipe!!.id)
                        )
                    },
                    onOrderClick = {
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://food.grab.com/id/en/")
                            )
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    navController: NavHostController,
    favVM: FavouriteViewModel,
    onSelect: () -> Unit
) {
    val isFav by remember { derivedStateOf { favVM.isFavorite(recipe) } }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(172.dp)
            .height(172.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(ImageHelper.optimizeUrl(recipe.image))
                .crossfade(true)
                .build(),
            contentDescription = recipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .zIndex(1f)
                .clickable { onSelect() }
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .zIndex(7f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ojek),
                contentDescription = "Order Grab",
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://food.grab.com/id/en/")
                            )
                        context.startActivity(intent)
                    }
            )

            Image(
                painter = painterResource(
                    id = if (isFav) R.drawable.new_love else R.drawable.heartmenudetails
                ),
                contentDescription = "Favorite",
                modifier = Modifier
                    .size(26.dp)
                    .clickable { favVM.toggleFavorite(recipe) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = recipe.title,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(92.dp)
            .height(36.dp)
            .shadow(
                elevation = if (selected) 7.dp else 2.dp,
                shape = RoundedCornerShape(50)
            )
            .clip(RoundedCornerShape(50))
            .background(if (selected) Color(0xFFFC7100) else MaterialTheme.colorScheme.surface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White else Color.Gray,
                shape = RoundedCornerShape(50)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = SourceSerifPro,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}