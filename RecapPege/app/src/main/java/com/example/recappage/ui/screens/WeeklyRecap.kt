package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.navigation.Screen


@Composable
fun IntakeRecapPageWeekly(
    navController: NavHostController, // ✅ navController wajib
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
    onSeeDetailsClick: () -> Unit = {}

) {
    val serifFont = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    Box(
        modifier = modifier
            .fillMaxSize() // ✅ ini biar fleksibel
            .background(Color.White)
    ) {
        // === HEADER ===
        TopBorder(navController = navController)


        Text(
            text = "My Intake",
            color = Color.Black,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = serifBold
            ),            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 118.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 360.dp, y = 154.dp)
                .requiredWidth(36.dp)
                .clickable {
                    navController.navigate(Screen.MonthlyRecap.route) // ✅ pindah page
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.icroundreadmore),
                contentDescription = "More",
                colorFilter = ColorFilter.tint(Color(0xff5ca135)),
                modifier = Modifier.requiredSize(36.dp)
            )
            Text(
                text = "More",
                color = Color(0xff5ca135),
                textAlign = TextAlign.Center,
                fontSize = 8.sp,
                modifier = Modifier.offset(y = (-10).dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.iconamoonhistoryfill),
            contentDescription = "iconamoon:history-fill",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 164.dp)
                .requiredSize(20.dp)
        )

        Text(
            text = "This Week",
            color = Color.Black,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = serifBold
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 41.dp, y = 165.dp)
        )

        Text(
            text = "Calories Taken",
            color = Color.Black,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = serifBold
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 150.dp, y = 201.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 198.dp, y = 223.dp)
                .requiredWidth(98.dp)
                .requiredHeight(35.dp)
        ) {
            Text(
                text = "0",
                color = Color(0xfffc7100),
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = serifBold
                )
            )
            Text(
                text = "/1.980 calories",
                color = Color(0xfffc7100),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = serifFont
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 18.dp, y = 18.dp) // dirapatkan sedikit
            )
        }



        // === BUTTON LOG MANUALLY ===
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 280.dp)
                .height(40.dp) // ⬅️ kasih tinggi tetap
                .clip(RoundedCornerShape(20))
                .background(Color(0xff5ca135))
                .clickable { navController.navigate(Screen.IntakeDetail.route) }
                .padding(horizontal = 16.dp) // hanya horizontal padding
                .shadow(0.dp, RoundedCornerShape(50))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight() // isi penuh tinggi box
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ggadd),
                    contentDescription = "add",
                    modifier = Modifier.size(12.dp), // kecilin dikit biar pas
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Log manually",
                    color = Color.White,
                    fontSize = 11.sp, // kecilin biar proporsional
                    fontWeight = FontWeight.Bold,
                    fontFamily = serifBold
                )
            }
        }


        // === SEE DETAILS ===
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 334.dp)
                .padding(bottom = 25.dp) // ✅ jarak dengan grid
                .clickable { navController.navigate(Screen.IntakeDetail.route) } // ✅ navigate ke detail
                .zIndex(1f) // ✅ ini penting biar gak ketutup grid

        ) {
            Text(
                text = "See details",
                color = Color(0xff555555),
                textDecoration = TextDecoration.Underline,
                fontSize = 10.sp,
            )
            Image(
                painter = painterResource(id = R.drawable.materialsymbolsdoublearrowrounded),
                contentDescription = "double-arrow",
                colorFilter = ColorFilter.tint(Color(0xff555555)),
                modifier = Modifier.requiredSize(12.dp)
            )
        }

        // === LIST MAKANAN ===
        val foodList = listOf(
            "Caesar Salad",
            "Rendang",
            "Potato Salad",
            "Sushi",
            "Steak",
            "Nasi Goreng"
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .offset(y = 40.dp)
                .align(Alignment.BottomCenter)
        ) {
            items(foodList) { food ->
                Box(
                    modifier = Modifier
                        .requiredSize(182.dp)
                        .clip(RoundedCornerShape(12.dp)) // ⬅️ tambah sudut membulat
                        .background(Color.LightGray)     // fallback
                        .clickable { /* navigasi detail */ },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // 🖼️ Gambar makanan tampil penuh
                    Image(
                        painter = painterResource(
                            id = when (food) {
                                "Caesar Salad" -> R.drawable.chicsalad
                                "Rendang" -> R.drawable.rendang
                                "Potato Salad" -> R.drawable.potatosld
                                "Sushi" -> R.drawable.sushi
                                "Steak" -> R.drawable.steak
                                "Nasi Goreng" -> R.drawable.nasigoreng
                                else -> R.drawable.chicsalad
                            }
                        ),
                        contentDescription = food,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // ⬜ Overlay putih transparan di bawah untuk teks
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(Color(0xAAFFFFFF))
                            .align(Alignment.BottomCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = food,
                            color = Color.Black,
                            fontFamily = serifFont,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }


        Divider(
            color = Color.Black.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 397.dp, y = 361.dp)
                .requiredWidth(120.dp)
                .rotate(-90f)
        )

        // === BOTTOM NAVIGATION (Component18 dipanggil sekali) ===
        Component18(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}



@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun IntakeRecapPageWeeklyPreview() {
    val fakeNavController = rememberNavController() // ✅ dummy supaya preview jalan
    IntakeRecapPageWeekly(navController = fakeNavController)
}
