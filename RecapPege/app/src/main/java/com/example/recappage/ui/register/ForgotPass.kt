package com.example.recappage.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.ui.theme.SourceSerifPro // Asumsi Anda punya import ini
import com.example.recappage.ui.theme.SourceSans3 // Asumsi Anda punya import ini
import androidx.compose.ui.text.style.TextDecoration
import com.example.recappage.R


@Composable
fun ForgotPassScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header TopBorder (tanpa profile)
//            TopBorder(
//                navController = navController,
//                showProfile = false
//            )

            // Konten Forgot Password
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Ikon Tanda Seru
                Image(
                    painter = painterResource(id = R.drawable.forgetpass),
                    contentDescription = "Forgot Password Icon",
                    modifier = Modifier.size(110.dp) // Ukuran disesuaikan
                )

                // 2. Judul "Forgot Password?"
                Text(
                    text = "Forgot Password?",
                    color = Color(0xFFFC7100), // Warna oranye seperti Welcome Text di SignIn
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontFamily = SourceSerifPro) // Font Serif
                )

                // 3. Subteks
                Text(
                    text = "Enter your email and we'll send you a link to reset your password",
                    color = Color(0xFF555555), // ✅ Warna diganti menjadi 555555
                    fontSize = 12.sp, // Ukuran tetap 12.sp
                    textAlign = TextAlign.Center,
                    // ✅ Menggunakan SourceSerifPro Semibold
                    style = TextStyle(
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.SemiBold // ✅ Menambahkan Semibold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Input Fields
                ForgotPassForm()

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Tombol Submit
                SubmitButton {
                    // TODO: Implementasi logika reset password (misalnya navigasi ke HomePage)
                    navController.popBackStack() // Kembali ke layar sebelumnya (SignIn)
                }

                // 6. Teks "< Back to login"
                Text(
                    text = "< Back to login",
                    color = Color(0xFFFC7100), // Warna oranye
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(
                        fontFamily = SourceSans3,
                        textDecoration = TextDecoration.Underline // ✅ Tambahkan garis bawah di sini
                    ),
                    modifier = Modifier
                        .clickable { navController.popBackStack() } // Kembali ke layar sebelumnya
                )
            }
        }
    }
}

@Composable
fun SubmitButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            // Asumsi R.drawable.submit adalah gambar tombol Anda
            painter = painterResource(id = R.drawable.submit),
            contentDescription = "Submit Button",
            modifier = Modifier.fillMaxSize(),
            // Menggunakan ContentScale.Fit agar gambar submit.png tidak gepeng/terdistorsi
            contentScale = ContentScale.Fit
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassForm() {
    var email by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var reNewPass by remember { mutableStateOf("") }

    val containerColor = Color(0xFFD3D3D3) // Light gray color

    // Tinggi field ditingkatkan
    val fieldModifier = Modifier
        .fillMaxWidth()
        .height(50.dp)

    // Hapus definisi customPadding karena tidak digunakan sebagai parameter di OutlinedTextField
    // val customPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        cursorColor = Color.Gray,
        focusedLeadingIconColor = Color.Gray,
        unfocusedLeadingIconColor = Color.Gray
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Enter Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },

            // ❌ Hapus parameter contentPadding = customPadding,

            placeholder = {
                Text(
                    "Enter Email",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    // ✅ Tambahkan padding kiri dan sesuaikan posisi vertikal jika perlu
                    modifier = Modifier.padding(start = 0.dp)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.emailicon),
                    contentDescription = "Email Icon",
                    tint = Color.Gray
                    // ✅ Tambahkan padding untuk ikon jika ikon terlalu dekat ke atas
                    // modifier = Modifier.padding(top = 2.dp)
                )
            },
            modifier = fieldModifier,
            shape = RoundedCornerShape(8.dp),
            colors = colors,
            textStyle = TextStyle(fontSize = 14.sp)
            // Hapus contentPadding di sini
        )

        // 2. Enter new pass
        OutlinedTextField(
            value = newPass,
            onValueChange = { newPass = it },
            // ❌ Hapus parameter contentPadding = customPadding,

            placeholder = {
                Text(
                    "Enter new pass",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 0.dp)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.pswordicon),
                    contentDescription = "New Password Icon",
                    tint = Color.Gray
                )
            },
            modifier = fieldModifier,
            shape = RoundedCornerShape(8.dp),
            colors = colors,
            textStyle = TextStyle(fontSize = 14.sp)
        )

        // 3. Re-enter new pass
        OutlinedTextField(
            value = reNewPass,
            onValueChange = { reNewPass = it },
            // ❌ Hapus parameter contentPadding = customPadding,

            placeholder = {
                Text(
                    "Re-enter new pass",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 0.dp)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.pswordicon),
                    contentDescription = "Re-enter Password Icon",
                    tint = Color.Gray
                )
            },
            modifier = fieldModifier,
            shape = RoundedCornerShape(8.dp),
            colors = colors,
            textStyle = TextStyle(fontSize = 14.sp)
        )
    }
}
