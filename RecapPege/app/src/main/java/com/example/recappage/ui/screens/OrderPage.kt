package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.SourceSans3
import com.example.recappage.ui.components.SourceSerifPro
import com.example.recappage.ui.components.SuccessDialog
import com.example.recappage.ui.components.TopBorder

// --- MOCK DATA ---
data class NutritionInfo(val calories: String, val carbs: String, val protein: String, val fat: String)

val mockNutrition = NutritionInfo(
    calories = "462 kCals",
    carbs = "100 g",
    protein = "100 g",
    fat = "100 g"
)

val mockIngredientsText = "Organic Romaine, Oat-Breaded Chicken, Beef, Bac, Cheddar, Eggs, Avocado, Cherry Tomatoes, Red Onions, Ranch"
// -----------------


@Composable
fun OrderPage(navController: NavHostController) {
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }
    var isAdded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBorder(navController = navController, showProfile = true)

        // ✅ GANTI DARI COLUMN MENJADI BOX UNTUK MENEMPATKAN COMPONENT18 DI BAWAH
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth() // Gunakan Box untuk layering (scrollable content + bottom nav)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(Color.White)
                    .padding(bottom = 90.dp) // ✅ Tambahkan padding di bawah agar konten tidak tertutup Component18
            ) {
                OrderPageHeader(
                    navController = navController,
                    isAdded = isAdded,
                    onAddClick = {
                        if (!isAdded) {
                            showDialog = true
                        }
                        isAdded = !isAdded
                    }
                )

                OrderContentBody(
                    nutrition = mockNutrition,
                    ingredients = mockIngredientsText,
                )

                Spacer(modifier = Modifier.height(80.dp))
            }

            if (showDialog) {
                SuccessDialog(
                    onDismiss = { showDialog = false }
                )
            }

            // ✅ PANGGIL COMPONENT18 DI DALAM BOX, DI BAWAH SEMUA KONTEN
            Component18(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

    }
}


// ===========================================
// ORDER PAGE COMPONENTS
// ===========================================

@Composable
fun OrderPageHeader(navController: NavHostController, isAdded: Boolean, onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Back Button + Header Text ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.backbutton),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() },
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.7f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "We would like to recomend you this",
                    fontFamily = SourceSerifPro, // ✅ Ganti font ke Source Serif Pro
                    fontWeight = FontWeight.Bold, // ✅ Ganti weight ke Bold
                    fontSize = 16.sp, // ✅ Ukuran tetap 16.sp
                    color = Color(0xFF5CA135), // ✅ Ganti warna ke 5CA135 (Green)
                    textAlign = TextAlign.Center, // ✅ Pusatkan teks
                    modifier = Modifier
                        .fillMaxWidth() // Pastikan Text mengisi lebar penuh
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Sesuaikan padding
                )
            }

            // --- Gambar Makanan + Tombol Add + Favorite ---
            Box(
                modifier = Modifier
                    .width(340.dp)
                    .height(255.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFCCCCCC)),
                contentAlignment = Alignment.Center
            ) {
                // Tombol Add (lebih rapat ke bawah dan center sempurna)
                AddToIntakeButtonOrder(
                    isAdded = isAdded,
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )

                // Tombol Favorite (geser sedikit dari pojok agar proporsional)
                Image(
                    painter = painterResource(id = R.drawable.heartmenudetails),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 12.dp)
                        .clickable { /* Handle favorite click */ }
                )
            }
        }
    }
}
// ===========================================
// ADD TO INTAKE BUTTON
// ===========================================
@Composable
fun AddToIntakeButtonOrder(
    isAdded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = if (isAdded) R.drawable.added_off else R.drawable.todays_intake

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = if (isAdded) "Added to intake" else "Add to today's intake",
        modifier = if (isAdded) {
            // 🟢 Pakai ukuran asli untuk added_off
            modifier
                .width(160.dp)
                .height(48.dp)
                .clickable { onClick() }
        } else {
            // ⚪ Ukuran tetap untuk todays_intake
            modifier
                .width(160.dp) // ⬅️ Disamakan dengan added_off
                .height(48.dp) // ⬅️ Disamakan dengan added_off
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
        },
        contentScale = if (isAdded) ContentScale.Fit else ContentScale.Fit
    )
}


// ===========================================
// BODY CONTENT (Nutrition + Ingredients)
// ===========================================
@Composable
fun OrderContentBody(
    nutrition: NutritionInfo,
    ingredients: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Chicken Salad Wrap",
            fontSize = 28.sp,
            fontFamily = SourceSerifPro,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )

        // --- Nutrition Info ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calories",
                fontSize = 24.sp,
                fontFamily = SourceSerifPro,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
            )
            Text(
                text = nutrition.calories,
                fontSize = 18.sp,
                fontFamily = SourceSans3,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 5.dp)
            )

            listOf(
                "Carbs:" to nutrition.carbs,
                "Protein:" to nutrition.protein,
                "Fat:" to nutrition.fat
            ).forEach { (label, value) ->
                Row(
                    modifier = Modifier.width(100.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontFamily = SourceSans3,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF555555) // ✅ Warna 555555
                    )
                    Text(
                        text = value, // "100 g"
                        fontSize = 12.sp, // ✅ Ukuran 12
                        fontFamily = SourceSans3, // ✅ Font Source Sans 3
                        fontWeight = FontWeight.Normal, // ✅ Weight Regular
                        color = Color(0xFF555555) // ✅ Warna 555555
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // --- Ingredients ---
        Text(
            text = ingredients,
            fontSize = 16.sp,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color(0xFF555555), // ✅ WARNA DIGANTI MENJADI 555555
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Order On ---
        Text(
            text = "Order on",
            fontSize = 16.sp,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            color = Color(0xFF555555), // ✅ Warna diganti menjadi 555555
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrderButton(
                iconResId = R.drawable.saladstop,
                contentDescription = "Order on SaladStop!",
                onClick = { /* Navigate to SaladStop link */ }
            )
            Spacer(modifier = Modifier.width(32.dp))
            OrderButton(
                iconResId = R.drawable.grab,
                contentDescription = "Order on Grab",
                onClick = { /* Navigate to Grab link */ }
            )
        }
    }
}


// ===========================================
// ORDER BUTTON
// ===========================================
@Composable
fun OrderButton(iconResId: Int, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(90.dp) // Ukuran Box (lingkaran luar) tetap 90.dp
//            .clip(CircleShape)
//            .background(GreenCheckColor)
//            .border(2.dp, GreenCheckColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            // ✅ UBAH fillMaxSize() menjadi ukuran spesifik yang lebih kecil
            modifier = Modifier.size(70.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewOrderPage() {
    OrderPage(navController = rememberNavController())
}
