package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField // ✅ Import baru
import androidx.compose.foundation.text.KeyboardActions // ✅ Import baru
import androidx.compose.foundation.text.KeyboardOptions // ✅ Import baru
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
import androidx.compose.ui.text.TextStyle // ✅ Import baru
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction // ✅ Import baru
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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
import com.example.recappage.util.NetworkResult
import java.net.URLDecoder
import androidx.compose.runtime.getValue

@Composable
fun FoodLibraryPage(
    navController: NavHostController,
    searchQuery: String? = null,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    favVM: FavouriteViewModel
) {
    val regViewModel: RegistrationViewModel = hiltViewModel()

    val detail by viewModel.recipeDetail.collectAsState()
    val topCategories = listOf("All", "Breakfast", "Heavy Meal", "Snacks", "Dessert")
    var selectedTop by remember { mutableStateOf("All") }
    var showFilterPopup by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }

    var showPopup by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val filterIcon = if (showFilterPopup) R.drawable.filter_on else R.drawable.filter

    val decodedQuery = remember(searchQuery) {
        searchQuery?.let { URLDecoder.decode(it, "UTF-8") }
    }

    // ✅ State untuk text field pencarian
    var queryText by remember { mutableStateOf(decodedQuery ?: "") }

    val recipesState by viewModel.recipesResponse.observeAsState()

    // Load Data Profile
    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }
    val profilePicUrl = regViewModel.profileImageUrl.value

    // ✅ Update queryText jika ada decodedQuery dari navigasi
    LaunchedEffect(decodedQuery) {
        if (decodedQuery != null) {
            queryText = decodedQuery
            isSearchMode = true
            selectedTop = "All"
            viewModel.searchRecipesByKeyword(decodedQuery)
        } else {
            // Jika tidak ada query, load default (All)
            isSearchMode = false
            viewModel.loadAllRecipes()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().background(Color.White),
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
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {

            // 1. KATEGORI (LazyRow)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = 10.dp)
                    .fillMaxWidth()
            ) {
                items(topCategories.size) { index ->
                    val category = topCategories[index]

                    CategoryChip(
                        text = category,
                        selected = selectedTop == category,
                        onClick = {
                            // Reset search saat klik kategori
                            isSearchMode = false
                            queryText = "" // Kosongkan search bar
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

            // ✅ 2. SEARCH BAR & FILTER ROW (Posisi Baru)
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 60.dp) // Di bawah kategori
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BOX SEARCH BAR
                Box(
                    modifier = Modifier
                        .weight(1f) // Mengisi sisa ruang
                        .height(38.dp)
                        .border(1.dp, Color(0xFFFC7100), RoundedCornerShape(20.dp))
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Hint Text
                    if (queryText.isEmpty()) {
                        Text(
                            text = "Search recipes...",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontFamily = SourceSans3
                        )
                    }

                    // Input Text
                    BasicTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontFamily = SourceSans3
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (queryText.isNotEmpty()) {
                                    isSearchMode = true
                                    selectedTop = "All" // Reset kategori ke All saat search
                                    viewModel.searchRecipesByKeyword(queryText)
                                } else {
                                    isSearchMode = false
                                    viewModel.loadAllRecipes()
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth().padding(end = 24.dp) // Padding kanan agar tidak nabrak icon
                    )

                    // Search Icon (di dalam box, sebelah kanan)
                    Image(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "Search",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(16.dp)
                            .clickable {
                                if (queryText.isNotEmpty()) {
                                    isSearchMode = true
                                    selectedTop = "All"
                                    viewModel.searchRecipesByKeyword(queryText)
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // FILTER ICON (Dipindahkan ke sini)
                Image(
                    painter = painterResource(id = filterIcon),
                    contentDescription = "Filter",
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { showFilterPopup = true }
                )
            }

            // 3. GRID RESEP
            when (val state = recipesState) {

                is NetworkResult.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5CA135))
                    }
                }

                is NetworkResult.Success -> {
                    val recipes = state.data?.recipes.orEmpty()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            // ✅ Offset disesuaikan agar tidak tertutup search bar (115dp sudah pas)
                            .offset(y = 115.dp)
                            .padding(horizontal = 12.dp)
                    ) {
                        items(recipes) { recipe ->
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
                    }
                }

                is NetworkResult.Error<*> -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "⚠️ ${state.message ?: "Error loading recipes"}",
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                null -> {}
            }

            // (Kode Image Filter Icon yang lama DIHAPUS karena sudah dipindah ke Row di atas)

            // POPUP FILTER
            if (showFilterPopup) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(10f)
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
                            queryText = "" // Reset search bar jika pakai filter
                            viewModel.loadWithFilter(filter)
                            showFilterPopup = false
                        }
                    )
                }
            }

            // POPUP PREVIEW MAKANAN
            if (showPopup && selectedRecipe != null) {
                FoodPreviewPopup(
                    image = selectedRecipe!!.image,
                    title = selectedRecipe!!.title,
                    detail = detail,
                    onClose = { showPopup = false },
                    onRecipeClick = {
                        // Jangan tutup popup agar saat back tetap ada
                        navController.navigate(
                            Screen.MenuDetails.createRoute(selectedRecipe!!.id)
                        )
                    },
                    onOrderClick = {}
                )
            }

        }
    }
}

// ... (Komponen RecipeCard dan CategoryChip tetap sama)
@Composable
fun RecipeCard(
    recipe: Recipe,
    navController: NavHostController,
    favVM: FavouriteViewModel,
    onSelect: () -> Unit
) {
    val isFav by remember {
        derivedStateOf { favVM.isFavorite(recipe) }
    }

    Box(
        modifier = Modifier
            .width(172.dp)
            .height(172.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
    ) {

        AsyncImage(
            model = recipe.image,
            contentDescription = recipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .zIndex(1f)
                .clickable {
                    onSelect()
                }
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
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Image(
                painter = painterResource(
                    id = if (isFav) R.drawable.new_love else R.drawable.heartmenudetails
                ),
                contentDescription = "Favorite",
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        favVM.toggleFavorite(recipe)
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.7f))
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = recipe.title,
                fontSize = 13.sp,
                color = Color(0xFF333333),
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
            .background(if (selected) Color(0xFFFC7100) else Color(0xFFD9D9D9))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White else Color(0xFFBFBFBF),
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
            color = if (selected) Color.White else Color.Black
        )
    }
}