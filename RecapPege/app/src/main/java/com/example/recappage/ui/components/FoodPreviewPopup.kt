package com.example.recappage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.model.RecipeInformation
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.util.MealTypeMapper.cleanHtml

@Composable
fun FoodPreviewPopup(
    image: String,
    title: String,
    detail: RecipeInformation?,
    summary: String? = null,
    onClose: () -> Unit,
    onRecipeClick: () -> Unit,
    onOrderClick: () -> Unit,

    fromHomePage: Boolean = false,
    onSpinAgain: (() -> Unit)? = null
) {
    val cleanedSummary = remember(detail, summary) {
        cleanHtml(detail?.summary ?: (summary ?: ""))
    }

    // Ukuran Gambar Tetap
    val imageHeight = 230.dp
    val imageOffset = 20.dp
    // Padding atas konten (agar tidak nabrak gambar)
    val titleTopPadding = imageHeight + imageOffset + 12.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .zIndex(10f)
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {

            // =============================
            //   CARD UTAMA
            // =============================
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f) // Lebar 92% layar
                    // 🔥 UBAH TINGGI: Max dikecilkan jadi 580.dp (biar pendek)
                    .heightIn(min = 400.dp, max = 580.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = titleTopPadding, start = 20.dp, end = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // Jarak diperkecil

                    Text(
                        text = "What’s in it?",
                        fontSize = 14.sp,
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5CA135),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = cleanedSummary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 5, // Kurangi max lines biar hemat tempat
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 🔥 UBAH JARAK: Dikurangi dari 26.dp jadi 16.dp biar tombol naik
                    Spacer(modifier = Modifier.height(16.dp))

                    // ICON RECIPE & ORDER
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp), // Reset padding bawah
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onRecipeClick() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.recipe),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp) // Sedikit dikecilkan
                            )
                            Text("Recipe", fontSize = 13.sp, color = Color(0xFFFC7100))
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onOrderClick() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ojekorange),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Text("Order", fontSize = 13.sp, color = Color(0xFFFC7100))
                        }
                    }

                    // ==========================
                    // SPIN AGAIN (POSISI DINAIKKAN)
                    // ==========================
                    if (fromHomePage && onSpinAgain != null) {
                        // 🔥 UBAH JARAK: Jarak antara tombol order dan spin again diperkecil (8.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSpinAgain() }
                                .padding(bottom = 16.dp), // Padding bawah secukupnya
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.refreshnobg),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(28.dp) // Icon sedikit dikecilkan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Spin Again",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        // Jika tidak ada spin again, kasih jarak bawah dikit
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // =============================
            // GAMBAR (DINAMIS)
            // =============================
            AsyncImage(
                model = com.example.recappage.util.ImageHelper.optimizeUrl(image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(imageHeight)
                    .align(Alignment.TopCenter)
                    .offset(y = imageOffset)
                    .clip(RoundedCornerShape(16.dp))
                    .zIndex(5f)
            )

            // =============================
            // CLOSE BUTTON (X)
            // =============================
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(imageHeight)
                    .align(Alignment.TopCenter)
                    .zIndex(6f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .size(15.dp)
                        .clickable { onClose() }
                )
            }
        }
    }
}