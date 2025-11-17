package com.example.recappage.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.ui.theme.SourceSerifPro
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold // 👈 TAMBAHKAN IMPORT
import com.example.recappage.R
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.components.TopBorder // 👈 TAMBAHKAN IMPORT


data class FoodCategory(
    val label: String,
    val iconRes: Int,
    val description: String // Tambahkan deskripsi jika perlu untuk aksesibilitas
)

@Composable
fun TypesFoodsScreen(navController: NavHostController, viewModel: RegistrationViewModel) { // 👈 Tambah viewModel
    // Definisi Kategori Makanan (TIDAK BERUBAH)
    val categories = listOf(
        FoodCategory("Vegetables", R.drawable.vegetables, "Icon Sayuran"),
        FoodCategory("Fruits", R.drawable.fruits, "Icon Buah"),
        FoodCategory("Grains", R.drawable.grains, "Icon Biji-bijian"),
        FoodCategory("Meat & Poultry", R.drawable.meat, "Icon Daging"),
        FoodCategory("Seafood", R.drawable.seafood, "Icon Makanan Laut"),
        FoodCategory("Dairy", R.drawable.dairy, "Icon Susu"),
        FoodCategory("Legumes & Nuts", R.drawable.nuts, "Icon Kacang-kacangan"),
        FoodCategory("Snacks & Sweets Food", R.drawable.sweetsfood, "Icon Makanan Ringan"),
        FoodCategory("Beverages", R.drawable.beverages, "Icon Minuman"),
        FoodCategory("Fastfood", R.drawable.fastfood, "Icon Makanan Cepat Saji"),
    )

    // 👈 PERUBAHAN 1: Buat logika Toggle untuk Makanan
    val onFoodToggle: (String) -> Unit = { foodName ->
        val currentFoods = viewModel.likedFoods.value
        viewModel.likedFoods.value = if (currentFoods.contains(foodName)) {
            currentFoods - foodName // Hapus jika sudah ada
        } else {
            currentFoods + foodName // Tambah jika belum ada
        }
    }

    // 👈 PERUBAHAN 2: Bungkus dengan Scaffold
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBorder(navController = navController, showProfile = false)
        }
    ) { innerPadding ->
        // Struktur utama: Box (Sekarang di dalam Scaffold)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 👈 Terapkan innerPadding
                .background(Color.White) // 👈 Tambahkan background di sini
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp), // 🎯 RUANG UNTUK TOMBOL NEXT
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header
                Text(
                    text = "What types of foods do you like?",
                    fontSize = 22.sp,
                    color = Color(0xFFFC7100),
                    fontWeight = FontWeight.Bold,
                    fontFamily = SourceSerifPro,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp) // 👈 KURANGI padding top (dari 70dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                Text(
                    text = "You can select more than one",
                    fontSize = 16.sp,
                    color = Color(0xFF555555),
                    fontFamily = SourceSerifPro,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )

                // 2. Grid Makanan (Scrollable)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories.size) { index ->
                        // 👈 PERUBAHAN 3: Hubungkan state dan event ke Button (Tidak berubah dari sebelumnya)
                        val category = categories[index]
                        val isSelected = viewModel.likedFoods.value.contains(category.label) // 👈 Baca state

                        FoodCategoryButton(
                            category = category,
                            isSelected = isSelected, // 👈 Beri state
                            onClick = { onFoodToggle(category.label) } // 👈 Beri event
                        )
                    }
                }
            }

            // 3. Tombol Next (TIDAK BERUBAH, hanya navigasi)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        navController.navigate("main_goal")
                    }
                    .padding(bottom = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            navController.navigate("main_goal")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nextorange),
                        contentDescription = "Next Button",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}


// 👈 PERUBAHAN 4: FoodCategoryButton menerima state (Tidak berubah dari sebelumnya)
@Composable
fun FoodCategoryButton(
    category: FoodCategory,
    isSelected: Boolean, // 👈 Terima state
    onClick: () -> Unit // 👈 Terima event
) {
    // 👈 PERUBAHAN 5: Ubah tampilan berdasarkan state isSelected (Tidak berubah dari sebelumnya)
    val borderColor = if (isSelected) Color(0xFFFC7100) else Color.LightGray // Oranye saat dipilih
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val backgroundColor = if (isSelected) Color(0xFFFFF7F0) else Color.White // Tint oranye muda

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor) // 👈 Gunakan background dinamis
            .border(borderWidth, borderColor, RoundedCornerShape(10.dp)) // 👈 Gunakan border dinamis
            .clickable(onClick = onClick), // 👈 Gunakan event yang diterima
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = category.iconRes),
                contentDescription = category.description,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}