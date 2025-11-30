package com.example.recappage.ui.components.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro

/**
 * =========================================================
 * MAIN WRAPPER
 * =========================================================
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeHorizontalCards(
    navController: NavHostController,
    dailyGoal: Int,
    consumed: Int,
    savedCount: Int,
    consumedCarbs: Int, targetCarbs: Int,
    consumedProtein: Int, targetProtein: Int,
    consumedFat: Int, targetFat: Int
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
                        savedRecipe = savedCount,
                        onClick = { navController.navigate("intake_detail") }
                    )
                    1 -> RecommendationCard(navController)
                    2 -> MacrosCard(
                        cCarbs = consumedCarbs, tCarbs = targetCarbs,
                        cProtein = consumedProtein, tProtein = targetProtein,
                        cFat = consumedFat, tFat = targetFat
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Indikator Pager
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
                            else Color.LightGray
                        )
                )
            }
        }
    }
}

/**
 * =========================================================
 * CARD 1 — CALORIES CARD
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

    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .graphicsLayer {
                shadowElevation = 12.dp.toPx()
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
            .background(cardColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // --- KIRI: JUDUL & LINGKARAN ---
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Calories",
                    fontFamily = SourceSerifPro,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "Tap to see details",
                    fontFamily = SourceSans3,
                    fontSize = 10.sp,
                    color = subTextColor,
                    modifier = Modifier.offset(y = (-4).dp)
                )

                Spacer(Modifier.height(16.dp))

                val circleSize = 115.dp

                Box(
                    modifier = Modifier.size(circleSize),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(circleSize)) {
                        drawArc(
                            color = trackColor,
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
                            fontSize = 22.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "Remaining",
                            fontSize = 12.sp,
                            fontFamily = SourceSans3,
                            color = subTextColor
                        )
                    }
                }
            }

            // --- KANAN: INFO & TOMBOL ---
            Column(
                modifier = Modifier
                    .padding(top = 55.dp, start = 10.dp),   // 🔥 tambah start padding
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.Start
            ){

                // 1. Info Base Goal
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.base_goal),
                        contentDescription = null,
                        // 🔥 Pastikan ukuran icon sama persis dengan bawahnya
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Base Goal",
                            fontSize = 10.sp,
                            color = subTextColor,
                            fontFamily = SourceSans3,
                            lineHeight = 12.sp
                        )
                        Text(
                            text = "${valFormatted(baseGoal)} cal",
                            fontSize = 14.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                // 2. Info Saved Recipes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.saved_recipe),
                        contentDescription = null,
                        // 🔥 Pastikan ukuran icon sama persis (32.dp) agar teksnya sejajar
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Saved",
                            fontSize = 10.sp,
                            color = subTextColor,
                            fontFamily = SourceSans3,
                            lineHeight = 12.sp
                        )
                        Text(
                            text = "$savedRecipe Recipes",
                            fontSize = 14.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 3. Tombol Log Manually
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF5CA135))
                        // 🔥 PERBAIKAN: Padding horizontal dikurangi (16 -> 12) agar muat 1 baris
                        .padding(horizontal = 12.dp)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(Modifier.width(3.dp)) // Jarak icon ke teks sedikit dirapatkan
                        Text(
                            text = "Log manually",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            // 🔥 PERBAIKAN: Paksa 1 baris
                            maxLines = 1,
                            overflow = TextOverflow.Visible
                        )
                    }
                }
            }
        }
    }
}

private fun valFormatted(value: Int): String = String.format("%,d", value).replace(",", ".")

/**
 * =========================================================
 * CARD 2 — RECOMMENDATION CARD
 * =========================================================
 */
@Composable
fun RecommendationCard(navController: NavHostController) {

    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
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
                textAlign = TextAlign.Center,
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
                textAlign = TextAlign.Center,
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
 * CARD 3 — MACROS CARD
 * =========================================================
 */
@Composable
fun MacrosCard(
    cCarbs: Int, tCarbs: Int,
    cProtein: Int, tProtein: Int,
    cFat: Int, tFat: Int
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .graphicsLayer {
                shadowElevation = 12.dp.toPx()
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
            .background(cardColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Macros",
                fontFamily = SourceSerifPro,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            MacroRowItem(
                label = "Carbohydrates",
                consumed = cCarbs,
                target = tCarbs,
                color = Color(0xFF69DBED),
                iconRes = R.drawable.carbs
            )

            MacroRowItem(
                label = "Protein",
                consumed = cProtein,
                target = tProtein,
                color = Color(0xFFF0DB54),
                iconRes = R.drawable.protein
            )

            MacroRowItem(
                label = "Fat",
                consumed = cFat,
                target = tFat,
                color = Color(0xFF80EAC5),
                iconRes = R.drawable.fat
            )
        }
    }
}

@Composable
fun MacroRowItem(
    label: String,
    consumed: Int,
    target: Int,
    color: Color,
    iconRes: Int
) {
    val progress = if (target > 0) (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = if (target > 0) ((consumed.toFloat() / target.toFloat()) * 100).toInt() else 0

    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = SourceSans3,
                fontSize = 10.sp,
                color = subTextColor,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(trackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(color)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = "$percentage%",
                fontFamily = SourceSerifPro,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "$consumed/$target g",
                fontFamily = SourceSans3,
                fontSize = 9.sp,
                color = subTextColor
            )
        }
    }
}