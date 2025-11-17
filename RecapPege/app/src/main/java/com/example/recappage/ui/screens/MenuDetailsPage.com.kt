package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.recappage.ui.components.*
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.viewmodel.MainViewModel
import com.example.recappage.ui.viewmodel.IntakeViewModel


@Composable
fun MenuDetailsPage(
    navController: NavHostController,
    foodId: Int,
    viewModel: MainViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    // 🔥 Observasi detail resep + nutrisi
    val recipe = viewModel.recipeDetail.collectAsState().value
    val nutrition = viewModel.nutritionDetail.collectAsState().value
    val macroData = viewModel.macroData.collectAsState().value
    val intakeViewModel: IntakeViewModel = hiltViewModel()


    // 🔥 Load data ketika halaman dibuka
    LaunchedEffect(foodId) {
        viewModel.loadRecipeDetail(foodId)
    }

    Scaffold(
        topBar = {
            TopBorder(navController = navController, showProfile = true)
        },
        bottomBar = {
            Component18(navController = navController)
        },
        containerColor = Color.White
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // 🖼️ GAMBAR UTAMA
            AsyncImage(
                model = recipe?.image,
                contentDescription = recipe?.title ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.chicsaladwrap)
            )

            // 📜 KONTEN SCROLLABLE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 260.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                // 🔥 Nama Makanan
                Text(
                    text = recipe?.title ?: "Loading...",
                    fontSize = 28.sp,
                    fontFamily = SourceSerifPro,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // 🔥 Total Calories
                macroData?.let {
                    Text(
                        text = "Total Calories ${it.calories}",
                        fontSize = 16.sp,
                        fontFamily = SourceSans3,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        Column {
                            Text("Carbs", color = Color(0xFF5CA135))
                            Text(it.carbs)
                        }
                        Column {
                            Text("Protein", color = Color(0xFF5CA135))
                            Text(it.protein)
                        }
                        Column {
                            Text("Fat", color = Color(0xFF5CA135))
                            Text(it.fat)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 🔥 INGREDIENTS
                Text(
                    text = "Ingredients",
                    fontSize = 16.sp,
                    fontFamily = SourceSerifPro,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFC7100),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                recipe?.extendedIngredients?.forEachIndexed { index, ing ->
                    Text(
                        text = "${index + 1}. ${ing.original}",
                        fontSize = 12.sp,
                        fontFamily = SourceSans3,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 🔥 STEPS
                Text(
                    text = "Step by step",
                    fontSize = 16.sp,
                    fontFamily = SourceSerifPro,
                    fontWeight = FontWeight.Bold,
                    color = GreenCheckColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (!recipe?.instructions.isNullOrBlank()) {
                    recipe!!.instructions.split(".").forEachIndexed { idx, step ->
                        if (step.isNotBlank()) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "Step ${idx + 1}",
                                    fontSize = 12.sp,
                                    fontFamily = SourceSans3,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = step.trim(),
                                    fontSize = 12.sp,
                                    fontFamily = SourceSans3,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AddToIntakeButton(
                    onClick = {
                        val title = recipe?.title ?: "Unknown Food"
                        val calories = macroData?.calories?.toIntOrNull() ?: 0

                        intakeViewModel.addIntake(title, calories)

                        showDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 10.dp)
                )
            }

            // 🔙 BACK BUTTON
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.backbutton),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }

            // ❤️ FAVORITE BUTTON
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.heartmenudetails),
                    contentDescription = "Favorite",
                    modifier = Modifier.size(30.dp)
                )
            }

            if (showDialog) {
                SuccessDialog(onDismiss = { showDialog = false })
            }
        }
    }
}

// BUTTON
@Composable
fun AddToIntakeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 150.dp, height = 40.dp)       // 🔥 ukuran fix
            .clip(RoundedCornerShape(12.dp))            // 🔥 radius 12dp
            .background(Color(0xFFFC7100))              // 🔥 warna oranye
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Add to Today's Intake",
            fontFamily = SourceSerifPro,                // 🔥 sesuai permintaan
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}
