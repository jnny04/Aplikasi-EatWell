package com.example.recappage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.model.DetectionItem
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro

@Composable
fun DetectionResultPopup(
    item: DetectionItem,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit,
    onRetakeClick: () -> Unit
) {
    // 1. Background Overlay FULL PUTIH (Sedikit transparan agar estetik)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.92f)) // ✅ Background Putih
            .zIndex(10f)
            .clickable(enabled = true) { /* Optional: klik luar untuk dismiss */ },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // 2. Teks Judul
            Text(
                text = "Here’s your estimated meal calories!",
                fontFamily = SourceSerifPro,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF5CA135),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // 3. Kartu Putih dengan SHADOW POP UP
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    // 🔥 EFEK SHADOW POP UP (PENTING UNTUK BACKGROUND PUTIH)
                    .shadow(
                        elevation = 20.dp, // Shadow tebal agar terlihat melayang
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 1f), // Warna bayangan hitam
                        ambientColor = Color.Black.copy(alpha = 1f)
                    )
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), // Card Putih
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Reset default, pakai shadow custom di atas
            ) {
                // Gunakan Box agar bisa menempatkan icon close secara absolut di pojok
                Box(modifier = Modifier.fillMaxWidth()) {

                    // --- KONTEN UTAMA ---
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Gambar Makanan
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.namaMakanan,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEEEEE))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Judul Makanan
                        Text(
                            text = "Estimated Calorie Count",
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = Color.Black
                        )
                        Text(
                            text = item.namaMakanan.replace("_", " ").uppercase(),
                            fontFamily = SourceSans3,
                            fontSize = 20.sp,
                            color = Color(0xFFFC7100),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Detail Nutrisi
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                NutrientText("Carbs")
                                NutrientText("Protein")
                                NutrientText("Fat")
                                Spacer(Modifier.height(4.dp))
                                Text("Total Calories", fontFamily = SourceSans3, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                NutrientValue(item.nutrisi.karbo ?: "0", "g")
                                NutrientValue(item.nutrisi.protein ?: "0", "g")
                                NutrientValue(item.nutrisi.lemak ?: "0", "g")
                                Spacer(Modifier.height(4.dp))
                                Text("${parseCalories(item.nutrisi.energi)} kcal", fontFamily = SourceSans3, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tombol Add
                        Button(
                            onClick = onAddClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC7100)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(40.dp)
                        ) {
                            Icon(painter = painterResource(id = R.drawable.plus), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add to today's intake", fontFamily = SourceSerifPro, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tombol Re-take
                        Image(
                            painter = painterResource(id = R.drawable.retake),
                            contentDescription = "Re-take",
                            modifier = Modifier
                                .height(36.dp)
                                .wrapContentWidth()
                                .clickable { onRetakeClick() }
                        )
                    }

                    // TOMBOL X (Pojok Kanan Atas)
                    Icon(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(16.dp)
                            .clickable { onDismiss() }
                    )
                }
            }
        }
    }
}

// Helper Text Kecil
@Composable
fun NutrientText(text: String) {
    Text(text, fontFamily = SourceSans3, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 2.dp))
}

@Composable
fun NutrientValue(value: String, unit: String) {
    val clean = value.replace(Regex("[^0-9.]"), "").ifEmpty { "0" }
    Text("$clean $unit", fontFamily = SourceSans3, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 2.dp))
}

fun parseCalories(value: String?): String {
    return value?.replace(Regex("[^0-9.]"), "") ?: "0"
}