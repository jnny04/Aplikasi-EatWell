package com.example.recappage.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // ✅ Import TextAlign
import androidx.compose.ui.text.style.TextOverflow // ✅ Import TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.viewmodel.MonthlyRecapViewModel
import com.example.recappage.util.PdfGenerator
import kotlinx.coroutines.delay

@Composable
fun MonthlyRecapPage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MonthlyRecapViewModel = hiltViewModel()
) {
    val serifFont = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    // ✅ Ambil State dari ViewModel
    val monthlyList by viewModel.monthlyIntakes.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    // Context untuk PDF & Email
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }

    // State download
    var isDownloading by remember { mutableStateOf(false) }
    var downloadComplete by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // === Konten utama ===
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "My Intake",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = serifBold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(start = 16.dp, top = 118.dp)
            )

            // Baris Dropdown & Download
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dropdown bulan
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { expanded = true }
                    ) {
                        Image(
                            painter = painterResource(
                                id = getMonthDrawable(selectedMonth, true)
                            ),
                            contentDescription = selectedMonth,
                            modifier = Modifier
                                .height(24.dp)
                                .padding(end = 6.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.dropdown),
                            contentDescription = "Dropdown Arrow",
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(if (expanded) 180f else 0f)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        months.forEach { month ->
                            val isSelected = month == selectedMonth
                            DropdownMenuItem(
                                text = {
                                    Image(
                                        painter = painterResource(
                                            id = getMonthDrawable(month, isSelected)
                                        ),
                                        contentDescription = month,
                                        modifier = Modifier
                                            .height(25.dp)
                                            .fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    viewModel.selectMonth(month)
                                    expanded = false
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(25.dp)
                            )
                        }
                    }
                }

                // Tombol Download
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        if (monthlyList.isEmpty()) {
                            android.widget.Toast.makeText(context, "No data to download", android.widget.Toast.LENGTH_SHORT).show()
                            return@clickable
                        }
                        isDownloading = true
                        downloadComplete = false
                        progress = 0
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.download),
                        contentDescription = "Download",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // ✅ GRID MAKANAN (TAMPILAN DISESUAIKAN)
            if (monthlyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data for this month.",
                        color = Color.Gray,
                        fontFamily = serifFont
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 90.dp)
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(monthlyList) { item ->
                        // Card Item Makanan (Format baru)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(182.dp)
                                .clip(RoundedCornerShape(12.dp)) // ✅ Radius 12.dp
                                .background(Color(0xffd9d9d9))
                        ) {
                            // Gambar Makanan
                            if (item.imageUrl != null) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Box Text di Bawah (Format baru)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp) // ✅ Tinggi 44.dp
                                    .align(Alignment.BottomCenter)
                                    .background(Color.White.copy(alpha = 0.7f)), // ✅ Putih Transparan 0.7f
                                contentAlignment = Alignment.Center // ✅ Teks di tengah
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 13.sp,           // ✅ 13.sp
                                    color = Color(0xFF333333),  // ✅ Abu gelap
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = serifFont,
                                    textAlign = TextAlign.Center, // ✅ Rata tengah
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // === Overlay gelap (saat dropdown terbuka) ===
        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .background(Color.Black.copy(alpha = 0.5f))
            )
        }

        // === Header & Nav ===
        TopBorder(
            navController = navController,
            modifier = Modifier.zIndex(2f)
        )

        Component18(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(0f),
            navController = navController
        )

        // === Popup Downloading ===
        if (isDownloading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .zIndex(3f),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.width(260.dp).wrapContentHeight()) {
                    Image(painter = painterResource(id = R.drawable.downloading), contentDescription = null, contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)))
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(28.dp).clickable { isDownloading = false }, contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(id = R.drawable.x), contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                    Box(modifier = Modifier.align(Alignment.Center).offset(y = (-40).dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(progress = progress / 100f, strokeWidth = 6.dp, modifier = Modifier.size(100.dp), color = Color(0xff5ca135))
                        Text(text = "$progress%", fontWeight = FontWeight.Bold, fontFamily = serifBold, fontSize = 18.sp, color = Color(0xff5ca135))
                    }
                    Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp).clickable { isDownloading = false }) {
                        Text(text = "Cancel", color = Color.Red, fontWeight = FontWeight.Medium, fontFamily = serifFont, fontSize = 9.sp)
                    }
                }

                // 🔥 LOGIKA DOWNLOAD & EMAIL 🔥
                LaunchedEffect(Unit) {
                    progress = 0
                    while (progress < 90) {
                        delay(50)
                        progress += 10
                    }

                    // 1. GENERATE PDF
                    val pdfUri = PdfGenerator.generateAndGetUri(context, monthlyList, selectedMonth)

                    progress = 100
                    delay(500)

                    isDownloading = false

                    if (pdfUri != null) {
                        downloadComplete = true

                        // 2. BUKA APLIKASI EMAIL
                        val emailIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_SUBJECT, "My Monthly Intake Recap: $selectedMonth")
                            putExtra(Intent.EXTRA_TEXT, "Here is my calorie intake report for $selectedMonth generated by EatWell App.")
                            putExtra(Intent.EXTRA_STREAM, pdfUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send email using..."))
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "No email app found", android.widget.Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        android.widget.Toast.makeText(context, "Failed to generate PDF", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // === Popup Download Complete ===
        if (downloadComplete) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .zIndex(3f),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.width(260.dp).wrapContentHeight()) {
                    Image(painter = painterResource(id = R.drawable.img_saved), contentDescription = null, contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)))
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(28.dp).clickable { downloadComplete = false }, contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(id = R.drawable.x), contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// Helper untuk drawable bulan (tetap sama)
fun getMonthDrawable(month: String, isSelected: Boolean): Int {
    return when (month) {
        "January" -> if (isSelected) R.drawable.january_green else R.drawable.january
        "February" -> if (isSelected) R.drawable.february_green else R.drawable.february
        "March" -> if (isSelected) R.drawable.march_green else R.drawable.march
        "April" -> if (isSelected) R.drawable.april_green else R.drawable.april
        "May" -> if (isSelected) R.drawable.may_green else R.drawable.may
        "June" -> if (isSelected) R.drawable.june_green else R.drawable.june
        "July" -> if (isSelected) R.drawable.july_green else R.drawable.july
        "August" -> if (isSelected) R.drawable.august_green else R.drawable.august
        "September" -> if (isSelected) R.drawable.september_green else R.drawable.september
        "October" -> if (isSelected) R.drawable.october_green else R.drawable.october
        "November" -> if (isSelected) R.drawable.november_green else R.drawable.november
        "December" -> if (isSelected) R.drawable.december_green else R.drawable.december
        else -> R.drawable.january
    }
}