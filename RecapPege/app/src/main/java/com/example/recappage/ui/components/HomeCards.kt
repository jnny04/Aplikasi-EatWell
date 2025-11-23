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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro

/**
 * =========================================================
 * MAIN WRAPPER — HOME HORIZONTAL CARDS (3 Cards Pager)
 * =========================================================
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeHorizontalCards(
    navController: NavHostController,
    dailyGoal: Int,
    consumed: Int,
    savedCount: Int,
    // ✅ TAMBAHAN PARAMETER MACROS
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
                        // ✅ KIRIM DATA MACROS KE CARD
                        cCarbs = consumedCarbs, tCarbs = targetCarbs,
                        cProtein = consumedProtein, tProtein = targetProtein,
                        cFat = consumedFat, tFat = targetFat
                    )
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

    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(0.7f), spotColor = Color.Black.copy(0.7f))
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
            // --- KIRI: LINGKARAN PROGRESS ---
            Column(horizontalAlignment = Alignment.Start) {
                Text("Calories", fontFamily = SourceSerifPro, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
                Text("Tap to see intake details", fontFamily = SourceSans3, fontSize = 8.sp, color = Color(0xFF555555), modifier = Modifier.offset(y = (-8).dp))
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawArc(color = Color(0xFFEDEDED), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = strokeThickness))
                        drawArc(color = Color(0xFFFC7100), startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, style = Stroke(width = strokeThickness, cap = StrokeCap.Round))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = valFormatted(remaining), fontSize = 26.sp, fontFamily = SourceSerifPro, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.offset(y = 2.dp))
                        Text(text = "Remaining", fontSize = 12.sp, fontFamily = SourceSans3, color = Color.Gray, modifier = Modifier.offset(y = (-2).dp))
                    }
                }
            }
            // --- KANAN: BASE GOAL & SAVED ---
            Column(modifier = Modifier.padding(top = 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.base_goal), contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        Text("Base Goal", fontSize = 8.sp, color = Color(0xFF555555), fontFamily = SourceSans3)
                        Row(modifier = Modifier.offset(y = (-6).dp)) {
                            Text(text = valFormatted(baseGoal), fontSize = 12.sp, fontFamily = SourceSerifPro, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Calories", fontSize = 8.sp, fontFamily = SourceSerifPro, fontWeight = FontWeight.Normal, color = Color.Black)
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.saved_recipe), contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        Text("Saved Recipe", fontSize = 8.sp, color = Color(0xFF555555), fontFamily = SourceSans3)
                        Row(modifier = Modifier.offset(y = (-6).dp)) {
                            Text("$savedRecipe", fontSize = 12.sp, fontFamily = SourceSerifPro, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Recipes", fontSize = 8.sp, fontFamily = SourceSerifPro, fontWeight = FontWeight.Normal, color = Color.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Box(modifier = Modifier.width(100.dp).height(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF5CA135)), contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.plus), contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Log manually", color = Color.White, fontSize = 10.sp, fontFamily = SourceSerifPro, fontWeight = FontWeight.Bold)
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
    Box(modifier = Modifier.width(320.dp).height(240.dp).clip(RoundedCornerShape(10.dp)).background(Color.Gray)) {
        Image(painter = painterResource(id = R.drawable.foodbox), contentDescription = null, modifier = Modifier.matchParentSize().graphicsLayer { scaleX = 1.2f; scaleY = 1.2f }, contentScale = ContentScale.Crop)
        Column(modifier = Modifier.matchParentSize().padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("So… what’s on your plate for today?", color = Color.White, textAlign = TextAlign.Center, fontSize = 16.sp, fontFamily = SourceSerifPro, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("Today's recommendation is here to spark your appetite and satisfy your cravings.", color = Color.White, fontSize = 12.sp, fontFamily = SourceSerifPro, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, lineHeight = 14.sp)
            Spacer(Modifier.height(40.dp))
            Box(modifier = Modifier.width(78.dp).height(30.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFF7A00)).clickable { navController.navigate("foodLibrary/_") }, contentAlignment = Alignment.Center) {
                Text("Explore", color = Color.White, fontFamily = SourceSerifPro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * =========================================================
 * CARD 3 — MACROS CARD (UPDATED)
 * =========================================================
 */
@Composable
fun MacrosCard(
    cCarbs: Int, tCarbs: Int,
    cProtein: Int, tProtein: Int,
    cFat: Int, tFat: Int
) {
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp) // ✅ Samakan ukuran dengan Calories Card
            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(0.7f), spotColor = Color.Black.copy(0.7f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // HEADER
            Text(
                text = "Macros",
                fontFamily = SourceSerifPro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(24.dp))

            // ITEM 1: CARBOHYDRATES
            MacroRowItem(
                label = "Carbohydrates",
                consumed = cCarbs,
                target = tCarbs,
                color = Color(0xFF69DBED),
                iconRes = R.drawable.carbs
            )

            Spacer(Modifier.height(14.dp))

            // ITEM 2: PROTEIN
            MacroRowItem(
                label = "Protein",
                consumed = cProtein,
                target = tProtein,
                color = Color(0xFFF0DB54),
                iconRes = R.drawable.protein
            )

            Spacer(Modifier.height(14.dp))

            // ITEM 3: FAT
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
    // Hitung Persentase & Progress
    val percentage = if (target > 0) ((consumed.toFloat() / target.toFloat()) * 100).toInt() else 0
    val progress = if (target > 0) (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. ICON (Kiri)
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.width(12.dp))

        // 2. KOLOM TENGAH (Label + Progress Bar)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Label "Carbohydrates"
            Text(
                text = label,
                fontFamily = SourceSans3,
                fontSize = 8.sp,
                color = Color(0xFF555555),
                lineHeight = 8.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Progress Bar (Tanpa Teks di dalam)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFEAEAEA))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(color)
                )
            }
        }

        Spacer(Modifier.width(8.dp)) // Jarak sedikit ke kolom kanan

        // 3. KOLOM KANAN (Persen & Info)
        Column(
            horizontalAlignment = Alignment.End, // Rata Kanan
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(50.dp) // Lebar fix sedikit diperlebar agar teks muat
        ) {
            // Angka Persen
            Text(
                text = "$percentage %",
                fontFamily = SourceSerifPro,
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.End,
                lineHeight = 12.sp
            )

            // Info ".../... (g)" di bawahnya (Rapat)
            Text(
                text = "$consumed/$target (g)",
                fontFamily = SourceSans3,
                fontSize = 6.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.End,
                lineHeight = 6.sp // Pastikan rapat ke atas
            )
        }
    }
}