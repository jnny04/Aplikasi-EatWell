package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Pastikan import ini ada
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
// Pastikan import ViewModel benar
import com.example.recappage.ui.viewmodel.IntakeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recappage.ui.theme.SourceSans3

@Composable
fun IntakeDetailPage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: IntakeViewModel = hiltViewModel()
){
    var showSuccess by remember { mutableStateOf(false) }
    val serifFont = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    // State Variables
    var menu by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }

    // ✅ 1. AMBIL DATA DARI VIEWMODEL
    val totalCaloriesToday by viewModel.totalCaloriesToday.collectAsState()
    val targetCalories by viewModel.userGoal.collectAsState()

    val tCarbs by viewModel.targetCarbs.collectAsState()
    val tProtein by viewModel.targetProtein.collectAsState()
    val tFat by viewModel.targetFat.collectAsState()

    // ✅ AMBIL TOTAL YANG SUDAH DIMAKAN
    val eatenCarbs by viewModel.totalCarbsToday.collectAsState()
    val eatenProtein by viewModel.totalProteinToday.collectAsState()
    val eatenFat by viewModel.totalFatToday.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // === Konten Utama ===
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 118.dp, bottom = 90.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "My Intake",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = serifBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

            // === HEADER TOTAL CALORIES (SUDAH DIPERBAIKI) ===
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Total Calories", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold)
                Spacer(Modifier.height(1.dp))

                // ✅ Gunakan ROW agar tidak tertumpuk
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Angka Kalori Saat Ini
                    Text(
                        text = "$totalCaloriesToday",
                        color = Color(0xfffc7100),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = serifBold
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    // Angka Target User (Dinamis)
                    Text(
                        text = "/$targetCalories calories", // ✅ Sesuai User Goal
                        color = Color(0xfffc7100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = serifFont,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Details", fontSize = 10.sp, fontFamily = serifFont, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(start = 4.dp))
            Spacer(modifier = Modifier.height(2.dp))

            // === DETAILS MACRO (SUDAH DINAMIS) ===
            // Note: Bagian kiri (0) masih 0 karena database belum simpan data macro per item
            // Tapi bagian kanan (/250) sudah sesuai target user.
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                // Tampilkan: [Yang Dimakan] / [Target] (g)
                MacroItem("Carbohydrates", "$eatenCarbs / $tCarbs (g)", R.drawable.carbs, Color(0xFF69DBED), serifBold)
                MacroItem("Protein", "$eatenProtein / $tProtein (g)", R.drawable.protein, Color(0xFFF0DB54), serifBold)
                MacroItem("Fat", "$eatenFat / $tFat (g)", R.drawable.fat, Color(0xFF80EAC5), serifBold)
            }

            Spacer(Modifier.height(35.dp))
            Divider(color = Color.Black.copy(alpha = 0.12f), thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))

            // Log Manually Section
            Text("Log Manually", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = serifBold)
            Text("Didn't find it on our menu? Add your today's intake manually here!", fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            // Menu Input
            Text("Menu", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = serifBold)
            val isFilled = menu.trim().isNotEmpty()
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                val fieldWidth = 220.dp
                Box(modifier = Modifier.width(fieldWidth)) {
                    BasicTextField(
                        value = menu,
                        onValueChange = { menu = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontFamily = serifFont, color = Color.Black),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                        decorationBox = { inner ->
                            Box {
                                if (menu.isBlank()) Text("Type menu name", fontSize = 12.sp, fontFamily = serifFont, color = Color.Black.copy(alpha = 0.35f))
                                inner()
                            }
                        }
                    )
                    Divider(color = Color.Black.copy(alpha = 0.6f), thickness = 1.3.dp, modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth())
                }
                Spacer(Modifier.width(6.dp))
                Image(painter = painterResource(id = if (isFilled) R.drawable.okkay_green else R.drawable.okkay), contentDescription = null, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Macros Input
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom){
                MacroInput("Carbohydrates", carbs, { carbs = it }, Color(0xFF69DBED), serifFont)
                MacroInput("Protein", protein, { protein = it }, Color(0xFFF0DB54), serifFont)
                MacroInput("Fat", fat, { fat = it }, Color(0xFF80EAC5), serifFont)
            }

//            Spacer(Modifier.height(16.dp))
//            Text("Quantity", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = serifBold)
//            Spacer(Modifier.height(8.dp))
//
//            // Quantity Input
//            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
//                Box(modifier = Modifier.width(80.dp).height(34.dp).border(1.5.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 6.dp), contentAlignment = Alignment.CenterStart) {
//                    BasicTextField(value = qty, onValueChange = { qty = it }, singleLine = true, textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontFamily = serifFont, color = Color.Black))
//                }
//                Spacer(Modifier.width(8.dp))
//                Text("Serving(s)", fontSize = 10.sp, fontFamily = serifFont, color = Color.Gray)
//            }
            Spacer(Modifier.height(30.dp))

            // === HITUNG KALORI OTOMATIS ===
            val cVal = carbs.toIntOrNull() ?: 0
            val pVal = protein.toIntOrNull() ?: 0
            val fVal = fat.toIntOrNull() ?: 0
            val autoCal = (cVal * 4) + (pVal * 4) + (fVal * 9)

            // === KOLOM KALORI (TENGAH) ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom // ✅ Tulisan "cal" turun ke bawah
            ) {
                // Kotak Angka
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(50.dp)
                        .border(1.5.dp, Color.Black, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (autoCal > 0) "$autoCal" else "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = serifBold,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Teks "cal"
                Text(
                    text = "cal",
                    fontSize = 12.sp,           // ✅ Ukuran 12
                    fontWeight = FontWeight.Medium, // ✅ Medium
                    fontFamily = SourceSans3,   // ✅ Source Sans 3
                    color = Color.Black,
                    // Sedikit padding bawah agar sejajar manis dengan border kotak
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // ✅ Jarak ke tombol Add didekatkan (tadinya 30.dp jadi 20.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // === TOMBOL ADD (LOGIC DIHUBUNGKAN KE VIEWMODEL) ===
            Image(
                painter = painterResource(id = R.drawable.todays_intake),
                contentDescription = "Add to today's intake",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
                    .height(40.dp)
                    .clickable {
                        // 1. Ambil data input
                        val cVal = carbs.toIntOrNull() ?: 0
                        val pVal = protein.toIntOrNull() ?: 0
                        val fVal = fat.toIntOrNull() ?: 0

                        // 2. Cek validasi
                        if (menu.isNotEmpty() || (cVal + pVal + fVal) > 0) {

                            // 3. PANGGIL VIEWMODEL (Bukan Repository langsung)
                            viewModel.addEntry(
                                name = menu,
                                carbs = cVal,
                                protein = pVal,
                                fat = fVal
                            )

                            // 4. Reset UI
                            showSuccess = true
                            menu = ""
                            carbs = ""
                            protein = ""
                            fat = ""
                            qty = ""
                        }
                    }
            )
        }

        TopBorder(navController = navController)
        Component18(modifier = Modifier.align(Alignment.BottomCenter), navController = navController)

        // Popup Success
        if (showSuccess) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.width(260.dp).wrapContentHeight()) {
                    Image(painter = painterResource(id = R.drawable.success_add), contentDescription = null, contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)))
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(28.dp).clickable { showSuccess = false }, contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(id = R.drawable.x), contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
// (Function MacroInput & MacroItem biarkan sama)

// (Helper functions MacroInput dan MacroItem tetap sama seperti sebelumnya)
@Composable
fun MacroInput(label: String, value: String, onValueChange: (String) -> Unit, accent: Color, fontFamily: FontFamily) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = fontFamily, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().offset(x = if (label != "Carbohydrates") (-7).dp else 0.dp))
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(60.dp).height(34.dp).border(1.5.dp, accent, RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                BasicTextField(value = value, onValueChange = onValueChange, singleLine = true, textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontFamily = fontFamily, color = Color.Black, textAlign = TextAlign.Center), modifier = Modifier.fillMaxWidth())
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
        Image(painter = painterResource(id = iconRes), contentDescription = title, modifier = Modifier.size(36.dp), colorFilter = ColorFilter.tint(Color.Black))
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, color = valueColor)
    }
}