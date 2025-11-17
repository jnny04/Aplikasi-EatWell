package com.example.recappage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.recappage.R
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro

@Composable
fun NoMatchCard(
    onDismiss: () -> Unit,
    onSpinAgain: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(380.dp)
                .height(652.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Gambar utama
                Image(
                    painter = painterResource(id = R.drawable.nofood), // pastikan nama file-nya nofood.png
                    contentDescription = "No Match",
                    modifier = Modifier
                        .width(250.dp)
                        .height(250.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(24.dp))

                // Judul
                Text(
                    "No match detected!",
                    fontFamily = SourceSerifPro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                // Subjudul
                Text(
                    "We found no matching options",
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                // Deskripsi tambahan
                Text(
                    "Your current selections are a bit too specific. Try removing one or two filters to explore more choices!",
                    fontFamily = SourceSans3,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(Modifier.height(40.dp))

                // Tombol spin again
                Button(
                    onClick = onSpinAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CA135))
                ) {
                    Text("↻ Spin Again", fontFamily = SourceSerifPro, color = Color.White)
                }
            }

            // Tombol close (X)
            Text(
                "✕",
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onDismiss() }
                    .padding(8.dp)
            )
        }
    }
}
