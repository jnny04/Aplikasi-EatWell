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

/**
 * =========================================================
 *  MAIN WRAPPER — HOME HORIZONTAL CARDS (3 Cards Pager)
 * =========================================================
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeHorizontalCards(navController: NavHostController) {

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
                        baseGoal = 0,
                        savedRecipe = 0,
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
    savedRecipe: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .shadow(
                elevation = 8.dp,                       // 🔥 lebih tinggi
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.70f),  // 🔥 lebih gelap
                spotColor = Color.Black.copy(alpha = 0.70f)       // 🔥 lebih pekat
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

            // ===============================
            // LEFT SIDE — CALORIES CIRCLE
            // ===============================
            Column(horizontalAlignment = Alignment.Start) {

                Text(
                    "Calories",
                    fontFamily = SourceSerifPro,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(1.dp))

                Text(
                    "Tap to see intake details",
                    fontFamily = SourceSans3,
                    fontSize = 8.sp,
                    color = Color(0xFF555555)
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier.size(138.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(138.dp)
                            .drawBehind {
                                drawCircle(
                                    color = Color(0xFFEDEDED),
                                    radius = size.minDimension / 2f
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                valFormatted(baseGoal),
                                fontSize = 20.sp,
                                fontFamily = SourceSerifPro,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Remaining",
                                fontSize = 10.sp,
                                fontFamily = SourceSans3
                            )
                        }
                    }
                }
            }

            // ===============================
            // RIGHT SIDE — BASE GOAL + SAVED
            // ===============================
            Column(
                modifier = Modifier.padding(top = 45.dp),  // naik dari 55dp → 45dp
                verticalArrangement = Arrangement.spacedBy(14.dp) // lebih rapat dari 18dp
            ) {

                // =================================
                // BASE GOAL (rapat versi final)
                // =================================
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Image(
                        painter = painterResource(id = R.drawable.base_goal),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            "Base Goal",
                            fontSize = 9.sp,
                            color = Color(0xFF555555),
                            fontFamily = SourceSans3
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                valFormatted(baseGoal),
                                fontSize = 14.sp,
                                fontFamily = SourceSerifPro,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.width(2.dp))

                            Text(
                                "Calories",
                                fontSize = 10.sp,
                                fontFamily = SourceSerifPro,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                        }
                    }
                }

                // =================================
                // SAVED RECIPE (rapat versi final)
                // =================================
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Image(
                        painter = painterResource(id = R.drawable.saved_recipe),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Saved Recipe",
                            fontSize = 9.sp,
                            color = Color(0xFF555555),
                            fontFamily = SourceSans3
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                "$savedRecipe",
                                fontSize = 14.sp,
                                fontFamily = SourceSerifPro,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.width(2.dp))

                            Text(
                                "Recipes",
                                fontSize = 10.sp,
                                fontFamily = SourceSerifPro,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                        }
                    }
                }

                // =================================
                // BUTTON — LOG MANUALLY
                // otomatis naik karena konten di atas lebih compact
                // =================================
                Box(
                    modifier = Modifier
                        .size(width = 89.dp, height = 32.dp)
                        .clip(RoundedCornerShape(8.dp))
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
                            "Log manually",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = SourceSans3
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
