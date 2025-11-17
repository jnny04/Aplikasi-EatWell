package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder

@Composable
fun MonthlyRecapPage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onDownloadClick: (String) -> Unit = {}
) {
    val serifFont = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    var expanded by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf("September") }

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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "My Intake",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = serifBold, // ✅ font serif bold
                    color = Color.Black
                ),
                modifier = Modifier.padding(start = 16.dp, top = 118.dp)
            )

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
                        onDismissRequest = { expanded = false }
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
                                    selectedMonth = month
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

            // Grid makanan
            LazyColumn(
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 90.dp)
                    .fillMaxWidth()
            ) {
                items(4) { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(2) { col ->
                            val index = row * 2 + col
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(182.dp)
                                    .background(Color(0xffd9d9d9), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = listOf(
                                        "Caesar Salad",
                                        "Thai Basil Chicken",
                                        "Chocolate Mousse",
                                        "Coconut Chia Pudding",
                                        "Banana Smoothie",
                                        "Coconut Chia Pudding",
                                        "Cheese Cake",
                                        "Donat JCO"
                                    )[index],
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = serifFont, // ✅ font serif
                                    color = Color(0xff555555),
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        // === Overlay gelap (nutup konten, tapi header & nav tetap terang) ===
        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f) // ✅ di atas bottom nav
                    .background(Color.Black.copy(alpha = 0.5f))
            )
        }

        // === Header tetap terang ===
        TopBorder(
            navController = navController,
            modifier = Modifier.zIndex(2f) // ✅ header paling atas
        )

        // === Bottom navigation tetap terang ===
        Component18(
            modifier = Modifier.align(Alignment.BottomCenter)
                .zIndex(0f),  // ✅ layer paling bawah ,
            navController = navController

        )

        // === Overlay download (proses) ===
// === Popup Downloading ===
        if (isDownloading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(260.dp)
                        .wrapContentHeight()
                ) {
                    // gambar downloading
                    Image(
                        painter = painterResource(id = R.drawable.downloading),
                        contentDescription = "Downloading",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    )

                    // ✅ hanya 1 tombol X di pojok kanan atas
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(28.dp)
                            .clickable { isDownloading = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.x),
                            contentDescription = "Close",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // progress circle + persen
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-40).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = progress / 100f,
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(100.dp),
                            color = Color(0xff5ca135)
                        )
                        Text(
                            text = "$progress%",
                            fontWeight = FontWeight.Bold,
                            fontFamily = serifBold, // ✅ font serif bold
                            fontSize = 18.sp,
                            color = Color(0xff5ca135)
                        )
                    }

                    // Cancel di bawah
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .clickable { isDownloading = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.Red,
                            fontWeight = FontWeight.Medium,
                            fontFamily = serifFont, // ✅ font serif
                            fontSize = 9.sp
                        )
                    }
                }
                LaunchedEffect(Unit) {
                    progress = 0
                    val step = 2        // naik berapa persen tiap iterasi
                    val delayMs = 150L  // jeda antar iterasi (ms)
                    while (progress < 100 && isDownloading) {
                        delay(delayMs)
                        progress += step
                    }
                    if (progress >= 100 && isDownloading) {
                        isDownloading = false
                        downloadComplete = true
                    }
                }
            }
        }
    }

// === Popup Download Complete ===
    if (downloadComplete) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .wrapContentHeight()
            ) {
                // gambar saved (TIDAK clickable)
                Image(
                    painter = painterResource(id = R.drawable.img_saved),
                    contentDescription = "Saved",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                )

                // ❌ tombol close di pojok kanan atas
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)    // jarak dari tepi gambar
                        .size(28.dp)       // area sentuh
                        .clickable { downloadComplete = false },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Helper untuk pilih drawable bulan.
 */
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
