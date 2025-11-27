package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.model.Recipe
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.viewmodel.FavouriteViewModel
import com.example.recappage.ui.viewmodel.RegistrationViewModel

@Composable
fun FavouritePage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    favVM: FavouriteViewModel,
    regViewModel: RegistrationViewModel = hiltViewModel()
) {
    val serifBold = FontFamily.Serif

    // 🔥 REALTIME – favourites akan auto-update
    val favourites = favVM.favourites

    // Load Profile Data
    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }
    val profilePicUrl = regViewModel.profileImageUrl.value

    Scaffold(
        topBar = {
            TopBorder(
                navController = navController,
                photoUrl = profilePicUrl
            )
        },
        bottomBar = {
            Component18(navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp), // Padding kanan kiri container
            verticalArrangement = Arrangement.spacedBy(16.dp), // Jarak atas-bawah antar item
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Jarak kanan-kiri antar item
        ) {
            // 1. HEADER (Judul Halaman)
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Favourite Meals",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = serifBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(Modifier.width(8.dp))

                        Image(
                            painter = painterResource(id = R.drawable.heartmenudetails),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }

            // 2. KONTEN (Grid Makanan)
            if (favourites.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No favourite meals yet.",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(favourites) { recipe ->
                    FavouriteRecipeCard(
                        recipe = recipe,
                        favVM = favVM,
                        onClick = {
                            navController.navigate(Screen.MenuDetails.createRoute(recipe.id))
                        }
                    )
                }
            }

            // Spacer bawah tambahan agar tidak terlalu mepet bottom bar
            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(90.dp))
            }
        }
    }
}

@Composable
fun FavouriteRecipeCard(
    recipe: Recipe,
    favVM: FavouriteViewModel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            // ❌ HAPUS: .requiredSize(182.dp) -> Ini penyebab dempet
            // ✅ GANTI DENGAN INI:
            .fillMaxWidth() // Mengisi lebar kolom yang tersedia
            .height(182.dp) // Tinggi tetap agar rapi
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = recipe.image,
            contentDescription = recipe.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // Text Box di bawah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .align(Alignment.BottomCenter)
                // Background transparan menyesuaikan tema
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
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
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }

        // Tombol Unlove (Hati)
        Image(
            painter = painterResource(R.drawable.new_love),
            contentDescription = "Unlove",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(25.dp)
                .clickable {
                    favVM.toggleFavorite(recipe)
                },
            contentScale = ContentScale.Fit
        )
    }
}