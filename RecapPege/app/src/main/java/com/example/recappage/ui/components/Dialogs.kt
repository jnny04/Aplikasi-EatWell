// ===========================================
// File: Dialogs.kt (Wajib Dibuat/Update)
// ===========================================
package com.example.recappage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // <-- IMPORT INI YANG HILANG!
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.recappage.R

// ===========================================
// FONT & WARNA (Gunakan 'val' agar dapat diakses publik)
// ===========================================
val SourceSans3 = FontFamily(
    Font(R.font.source_sans3_regular, FontWeight.Normal),
    Font(R.font.source_sans3_medium, FontWeight.Medium),
    Font(R.font.source_sans3_bold, FontWeight.Bold),
    Font(R.font.source_sans3_extrabold, FontWeight.ExtraBold)
)

val SourceSerifPro = FontFamily(
    Font(R.font.source_serif_pro_regular, FontWeight.Normal),
    Font(R.font.source_serif_pro_semibold, FontWeight.SemiBold),
    Font(R.font.source_serif_pro_bold, FontWeight.Bold)
)

val OrangeColor = Color(0xFFE56A35)
val GreenCheckColor = Color(0xFF5CA135)

// ===========================================
// SUCCESS DIALOG COMPONENT
// ===========================================

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tombol Tutup (X)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.buttoncancelx),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onDismiss)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ikon Centang Besar
            Image(
                painter = painterResource(id = R.drawable.saved),
                contentDescription = "Successfully Added",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Teks "Successfully Added!"
            Text(
                text = "Successfully Added!",
                fontFamily = SourceSerifPro,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = GreenCheckColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Teks Deskripsi
            Text(
                text = "Your meal has been added to today's intake record",
                fontFamily = SourceSans3,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}