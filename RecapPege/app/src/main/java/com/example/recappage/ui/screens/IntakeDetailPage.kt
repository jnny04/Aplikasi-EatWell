package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder


@Composable
fun IntakeDetailPage(
    navController: NavHostController, // ✅ navController wajib
    modifier: Modifier = Modifier
) {
    var showSuccess by remember { mutableStateOf(false) }

    val serifFont = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // === Konten utama ===
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 118.dp, bottom = 90.dp, start = 16.dp, end = 16.dp)
        ) {
            // Title
            Text(
                text = "My Intake",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = serifBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // === Total Calories ===
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Total Calories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = serifBold
                )
                Spacer(Modifier.height(1.dp))

                val density = LocalDensity.current
                var zeroWidthPx by remember { mutableStateOf(0) }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    // 0 — tepat di tengah layar
                    Text(
                        text = "0",
                        color = Color(0xfffc7100),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = serifBold,
                        modifier = Modifier.onGloballyPositioned { zeroWidthPx = it.size.width }
                    )

                    // teks kecil di kanan 0
                    val zeroWidthDp = with(density) { zeroWidthPx.toDp() }
                    Text(
                        text = "/1.980 calories",
                        color = Color(0xfffc7100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = serifFont,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(
                                x = zeroWidthDp + 37.dp,
                                y = 10.dp
                            ) // geser ke kanan & sedikit turun
                    )
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            // === Details title ===
            Text(
                text = "Details",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = serifFont,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))    // jarak kecil sebelum macros


            // === Macros ===
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                MacroItem("Carbohydrates", "124/248 (g)", R.drawable.carbs, Color(0xFF69DBED), serifBold)
                MacroItem("Protein", "80/100 (g)", R.drawable.protein, Color(0xFFF0DB54), serifBold)
                MacroItem("Fat", "66/66 (g)", R.drawable.fat, Color(0xFF80EAC5), serifBold)
            }

            // ruang setelah macros
            Spacer(Modifier.height(35.dp))

            // garis pemisah yang lebih transparan & tipis
            Divider(
                color = Color.Black.copy(alpha = 0.12f), // ~12% opacity
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // ruang sebelum judul "Log Manually"
            Spacer(Modifier.height(24.dp))

            Text(
                "Log Manually",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = serifBold
            )
            Text(
                "Didn't find it on our menu? Add your today's intake manually here!",
                fontSize = 10.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))
// === Menu: underline pendek + icon di sisi kanan ===
            var menu by remember { mutableStateOf("") }
            val isFilled = menu.trim().isNotEmpty()

            Text(
                text = "Menu",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = serifBold            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                val fieldWidth = 220.dp // atur panjang garis

                Box(modifier = Modifier.width(fieldWidth)) {
                    BasicTextField(
                        value = menu,
                        onValueChange = { menu = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 12.sp,
                            fontFamily = serifFont,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp),  // rapat ke garis
                        decorationBox = { inner ->
                            Box {
                                if (menu.isBlank()) {
                                    Text(
                                        "Type menu name",
                                        fontSize = 12.sp,
                                        fontFamily = serifFont,
                                        color = Color.Black.copy(alpha = 0.35f)
                                    )
                                }
                                inner()
                            }
                        }
                    )
                    Divider(
                        color = Color.Black.copy(alpha = 0.6f),
                        thickness = 1.3.dp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                    )
                }

                Spacer(Modifier.width(6.dp))

                Image(
                    painter = painterResource(id = if (isFilled) R.drawable.okkay_green else R.drawable.okkay),
                    contentDescription = if (isFilled) "valid" else "not filled",
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

// state lama boleh dipakai ulang
            var carbs by remember { mutableStateOf("") }
            var protein by remember { mutableStateOf("") }
            var fat by remember { mutableStateOf("") }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ){
                MacroInput("Carbohydrates", carbs, { carbs = it }, Color(0xFF69DBED), serifFont)
                MacroInput("Protein", protein, { protein = it }, Color(0xFFF0DB54), serifFont)
                MacroInput("Fat", fat, { fat = it }, Color(0xFF80EAC5), serifFont)
            }

            Spacer(Modifier.height(16.dp))
            Text("Quantity", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = serifBold)
            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                var qty by remember { mutableStateOf("") }

                // kotak kecil quantity
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(34.dp)
                        .border(1.5.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = qty,
                        onValueChange = { qty = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            fontFamily = serifFont,
                            color = Color.Black
                        )
                    )
                }

                Spacer(Modifier.width(8.dp))
                Text("Serving(s)", fontSize = 10.sp, fontFamily = serifFont, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === Add to today's intake (pakai image) ===
            Image(
                painter = painterResource(id = R.drawable.todays_intake),
                contentDescription = "Add to today's intake",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
                    .height(36.dp)
                    .clickable { showSuccess = true }   // ✅ munculkan popup
            )

        }

        // === Header tetap terang ===
        TopBorder(navController = navController)
        // === Bottom navigation tetap terang ===
        Component18(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
        if (showSuccess) {
            // backdrop gelap (tidak clickable)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                // kartu popup + tombol X
                Box(
                    modifier = Modifier
                        .width(260.dp)
                        .wrapContentHeight()
                ) {
                    // gambar success (TIDAK clickable)
                    Image(
                        painter = painterResource(id = R.drawable.success_add),
                        contentDescription = "Successfully Added",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    )

                    // tombol X (pakai image "x") di pojok kanan atas
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)    // jarak dari tepi kartu
                            .size(28.dp)       // area sentuh nyaman
                            .clickable { showSuccess = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.x),
                            contentDescription = "Close",
                            modifier = Modifier.size(16.dp) // ukuran ikon
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    accent: Color,
    fontFamily: FontFamily

) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp) // samain lebar biar rapih
    ) {
        // Label rata tengah, bisa digeser manual kalau perlu
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = if (label != "Carbohydrates") (-7).dp else 0.dp)
            // Protein & Fat geser dikit ke kiri
        )

        Spacer(Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Kotak input
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(34.dp)
                    .border(1.5.dp, accent, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(

                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.width(4.dp))
            Text("g", fontSize = 12.sp, fontFamily = fontFamily, color = accent, modifier = Modifier.offset(y = 9.dp))
        }
    }
}

@Composable
fun MacroItem(title: String, value: String, iconRes: Int, valueColor: Color, fontFamily: FontFamily) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
        Spacer(Modifier.height(6.dp))
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(36.dp),
            colorFilter = ColorFilter.tint(Color.Black)
        )
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, color = valueColor)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun IntakeDetailPreview() {
    val fakeNavController = rememberNavController()
    IntakeDetailPage(navController = fakeNavController)
}
