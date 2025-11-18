package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.model.Recipe
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.viewmodel.FavouriteViewModel

@Composable
fun FavouritePage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    favVM: FavouriteViewModel
) {
    val serifBold = FontFamily.Serif

    // 🔥 REALTIME – favourites akan auto-update
    val favourites = favVM.favourites

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBorder(navController = navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            Spacer(Modifier.height(120.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "My Favourite Meals",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = serifBold,
                    color = Color.Black
                )

                Spacer(Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.heartmenudetails),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            if (favourites.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No favourite meals yet.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .heightIn(min = 0.dp, max = 3000.dp)
                ) {
                    items(favourites) { recipe ->
                        FavouriteRecipeCard(
                            recipe = recipe,
                            favVM = favVM
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        Component18(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}


@Composable
fun FavouriteRecipeCard(
    recipe: Recipe,
    favVM: FavouriteViewModel
) {
    val serifBold = FontFamily.Serif

    Box(
        modifier = Modifier
            .requiredSize(182.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xffd9d9d9))
    ) {
        AsyncImage(
            model = recipe.image,
            contentDescription = recipe.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xAAFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                recipe.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = serifBold,
                maxLines = 2
            )
        }

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
