package com.example.recappage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // ✅ Import MaterialTheme
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

    val cardHeight = if (fromHomePage) 652.dp else 576.dp
    val titleTopPadding = if (fromHomePage) 300.dp else 280.dp
    val imageOffset = 20.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)) // Gelapkan background overlay
            .zIndex(10f)
            .clickable { onClose() } // Klik luar untuk tutup
    ) {

        Box(modifier = Modifier.align(Alignment.Center).clickable(enabled = false) {}) {

            // =============================
            //   CARD (ADAPTIF DARK MODE)
            // =============================
            Box(
                modifier = Modifier
                    .width(380.dp)
                    .height(cardHeight)
                    .clip(RoundedCornerShape(18.dp))
                    // ✅ GANTI: Color.White -> MaterialTheme.colorScheme.surface
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
                        fontSize = 26.sp,
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp,
                        // ✅ GANTI: Default Black -> onSurface
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "What’s in it?",
                        fontSize = 14.sp,
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5CA135), // Hijau tetap
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = cleanedSummary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 6,
                        lineHeight = 16.sp,
                        // ✅ GANTI: Default Black -> onSurface
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onRecipeClick() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.recipe),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                            Text("Recipe", fontSize = 14.sp, color = Color(0xFFFC7100))
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onOrderClick() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ojekorange),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                            Text("Order", fontSize = 14.sp, color = Color(0xFFFC7100))
                        }
                    }

                    // ==========================
                    // SPIN AGAIN (HOME ONLY)
                    // ==========================
                    if (fromHomePage && onSpinAgain != null) {

                        Spacer(modifier = Modifier.height(28.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSpinAgain() },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // GANTI IKON JIKA PERLU (Opsional, tapi tinting lebih aman)
                            Icon(
                                painter = painterResource(id = R.drawable.refreshnobg),
                                contentDescription = null,
                                // ✅ GANTI: Tinting sesuai tema
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Spin Again",
                                fontSize = 14.sp,
                                // ✅ GANTI: Color.DarkGray -> onSurface
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // =============================
            // GAMBAR (Tetap sama)
            // =============================
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 340.dp, height = 255.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = imageOffset)
                    .clip(RoundedCornerShape(16.dp))
                    .zIndex(5f)
            )

            // =============================
            // CLOSE BUTTON (X)
            // =============================
            Icon(
                painter = painterResource(id = R.drawable.cancel),
                contentDescription = "Close",
                // ✅ GANTI: Tinting ke onSurface
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(24.dp) // Sedikit diperbesar agar mudah diklik
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = (16).dp)
                    .zIndex(6f)
                    .clickable { onClose() }
            )
        }
    }
}