package com.example.recappage.ui.components.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import androidx.compose.foundation.Canvas // ✅ Tambahkan import ini
import androidx.compose.ui.graphics.StrokeCap // ✅ Tambahkan import ini
import androidx.compose.ui.graphics.drawscope.Stroke // ✅ Tambahkan import ini

/**
 * =========================================================
 *  MAIN WRAPPER — HOME HORIZONTAL CARDS (3 Cards Pager)
 * =========================================================
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeHorizontalCards(
    navController: NavHostController,
    dailyGoal: Int,
    consumed: Int,
    savedCount: Int // <--- TAMBAHAN PARAMETER BARU
) {

    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = (-10).dp,
            contentPadding = PaddingValues(horizontal = 40.dp)
        ) { page ->

            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = 1f - kotlin.math.abs(pageOffset) * 0.15f

            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.coerceIn(0.85f, 1f)
                    scaleY = scale.coerceIn(0.85f, 1f)
                }
            ) {
                when (page) {
                    0 -> CaloriesCard(
                        baseGoal = dailyGoal,
                        consumed = consumed,
                        savedRecipe = savedCount, // ✅ GUNAKAN DATA DARI PARAMETER
                        onClick = { navController.navigate("intake_detail") }
                    )
                    1 -> RecommendationCard(navController)
                    2 -> MacrosCard(carbs = 50, protein = 80, fat = 100)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { index ->
                val size by animateDpAsState(
                    targetValue = if (pagerState.currentPage == index) 12.dp else 8.dp,
                    animationSpec = tween(300),
                    label = ""
                )
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Color(0xFF5CA135)
                            else Color(0xFFD9D9D9)
                        )
                )
            }
        }
    }
}

/**
 * =========================================================
 *  CARD 1 — CALORIES CARD
 * =========================================================
 */
@Composable
fun CaloriesCard(
    baseGoal: Int,
    consumed: Int,
    savedRecipe: Int,
    onClick: () -> Unit
) {
    val remaining = (baseGoal - consumed).coerceAtLeast(0)
    val progress = if (baseGoal > 0) consumed.toFloat() / baseGoal.toFloat() else 0f
    val strokeThickness = 22f

    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.70f),
                spotColor = Color.Black.copy(alpha = 0.70f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(20.dp)
    )
    {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {

            // --- KIRI: LINGKARAN PROGRESS (TETAP) ---
            Column(horizontalAlignment = Alignment.Start) {

                Text(
                    text = "Calories",
                    fontFamily = SourceSerifPro,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )

                Text(
                    text = "Tap to see intake details",
                    fontFamily = SourceSans3,
                    fontSize = 8.sp,
                    color = Color(0xFF555555),
                    modifier = Modifier.offset(y = (-8).dp)
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawArc(
                            color = Color(0xFFEDEDED),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeThickness)
                        )
                        drawArc(
                            color = Color(0xFFFC7100),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = strokeThickness, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = valFormatted(remaining),
                            fontSize = 26.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.offset(y = 2.dp)
                        )
                        Text(
                            text = "Remaining",
                            fontSize = 12.sp,
                            fontFamily = SourceSans3,
                            color = Color.Gray,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    }
                }
            }

            // --- KANAN: BASE GOAL & SAVED (POSISI DISESUAIKAN) ---
            Column(
                // 1. Mengurangi padding atas drastis agar naik ke atas
                modifier = Modifier.padding(top = 30.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar elemen lebih rapat
            ) {

                // --- BASE GOAL ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.base_goal),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "Base Goal",
                            fontSize = 8.sp,
                            color = Color(0xFF555555),
                            fontFamily = SourceSans3
                        )

                        Row(modifier = Modifier.offset(y = (-6).dp)) { // Tarik teks angka naik sedikit
                            Text(
                                text = valFormatted(baseGoal),
                                fontSize = 12.sp,
                                fontFamily = SourceSerifPro,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.alignByBaseline()
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Calories",
                                fontSize = 8.sp,
                                fontFamily = SourceSerifPro,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }

                // --- SAVED RECIPE (POSISI NAIK KARENA PADDING DIATAS) ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.saved_recipe),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "Saved Recipe",
                            fontSize = 8.sp,
                            color = Color(0xFF555555),
                            fontFamily = SourceSans3
                        )

                        Row(modifier = Modifier.offset(y = (-6).dp)) { // Tarik teks angka naik sedikit
                            Text(
                                text = "$savedRecipe",
                                fontSize = 12.sp,
                                fontFamily = SourceSerifPro,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.alignByBaseline()
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Recipes",
                                fontSize = 8.sp,
                                fontFamily = SourceSerifPro,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }

                // Jarak sebelum tombol (Spacer)
                Spacer(modifier = Modifier.height(2.dp))

                // --- BUTTON LOG MANUALLY (UKURAN DISESUAIKAN) ---
                Box(
                    modifier = Modifier
                        .width(100.dp)  // Lebar diperbesar sesuai permintaan
                        .height(40.dp)  // Tinggi disesuaikan
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF5CA135)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Log manually",
                            color = Color.White,
                            fontSize = 10.sp, // Font sedikit dibesarkan agar proporsional dengan tombol
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


private fun valFormatted(value: Int): String =
    String.format("%,d", value).replace(",", ".")

/**
 * =========================================================
 *  CARD 2 — RECOMMENDATION CARD
 * =========================================================
 */
@Composable
fun RecommendationCard(navController: NavHostController) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Gray)
    ) {

        Image(
            painter = painterResource(id = R.drawable.foodbox),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = 1.2f
                    scaleY = 1.2f
                },
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "So… what’s on your plate for today?",
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = SourceSerifPro,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Today's recommendation is here to spark your appetite and satisfy your cravings.",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = SourceSerifPro,
                fontStyle = FontStyle.Italic,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .width(78.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFF7A00))
                    .clickable { navController.navigate("foodLibrary/_") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Explore",
                    color = Color.White,
                    fontFamily = SourceSerifPro,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * =========================================================
 *  CARD 3 — MACROS CARD
 * =========================================================
 */
@Composable
fun MacrosCard(carbs: Int, protein: Int, fat: Int) {
    Column(
        modifier = Modifier
            .width(270.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            "Macros",
            fontFamily = SourceSerifPro,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        MacroBar("Carbohydrates", carbs)
        Spacer(Modifier.height(8.dp))

        MacroBar("Protein", protein)
        Spacer(Modifier.height(8.dp))

        MacroBar("Fat", fat)
    }
}

@Composable
fun MacroBar(label: String, value: Int) {
    Column {
        Text(label, fontSize = 12.sp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF6FC9BA))
            )
        }
    }
}
