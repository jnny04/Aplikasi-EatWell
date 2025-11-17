package com.example.recappage.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.layout.ContentScale // Tambahkan import ini jika belum ada
import androidx.compose.ui.text.TextStyle
import com.example.recappage.ui.theme.SourceSerifPro
import android.widget.Toast // 👈 TAMBAHAN
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext // 👈 TAMBAHAN
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.recappage.R
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.components.TopBorder


@Composable
fun RegisterScreen(navController: NavHostController, viewModel: RegistrationViewModel) { // 👈 Ini sudah benar
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(), // 👈 .background(Color.White) DIHAPUS DARI SINI

        // 🔽 PINDAHKAN TopBorder KE SLOT 'topBar' 🔽
        topBar = {
            TopBorder(navController = navController, showProfile = false)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 👈 Padding ini sekarang sudah benar
                .background(Color.White), // 👈 .background(Color.White) PINDAH KE SINI
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ❌ TopBorder(...) DIHAPUS DARI SINI

            Column(
                // ... sisa konten
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 32.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // ... (Teks Judul tidak berubah)
                Text(
                    text = "Your journey starts here!\nTake the first step",
                    fontSize = 24.sp,
                    color = Color(0xFFFC7100),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 👈 PERUBAHAN 1: Panggil RegisterForm dengan state dari ViewModel
                RegisterForm(
                    email = viewModel.email.value,
                    onEmailChange = { viewModel.email.value = it },
                    username = viewModel.username.value,
                    onUsernameChange = { viewModel.username.value = it },
                    password = viewModel.password.value,
                    onPasswordChange = { viewModel.password.value = it },
                    confirmPassword = viewModel.confirmPassword.value,
                    onConfirmPasswordChange = { viewModel.confirmPassword.value = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Tombol Sign Up (Tidak berubah, ini hanya navigasi)
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            // 1. Ambil nilai terbaru dari ViewModel
                            val email = viewModel.email.value
                            val password = viewModel.password.value
                            val confirmPassword = viewModel.confirmPassword.value

                            // 2. Validasi Email
                            if (!email.endsWith("@gmail.com", ignoreCase = true)) {
                                Toast.makeText(context, "Email harus menggunakan @gmail.com", Toast.LENGTH_LONG).show()
                                return@clickable // Hentikan proses
                            }

                            // 3. Validasi Password
                            if (password.length < 6) {
                                Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_LONG).show()
                                return@clickable
                            }
                            if (!password.any { it.isUpperCase() }) {
                                Toast.makeText(context, "Password harus memiliki minimal 1 huruf kapital", Toast.LENGTH_LONG).show()
                                return@clickable
                            }
                            if (!password.any { it.isLowerCase() }) {
                                Toast.makeText(context, "Password harus memiliki minimal 1 huruf kecil", Toast.LENGTH_LONG).show()
                                return@clickable
                            }
                            if (!password.any { !it.isLetterOrDigit() }) {
                                Toast.makeText(context, "Password harus memiliki minimal 1 simbol (contoh: @, !, #)", Toast.LENGTH_LONG).show()
                                return@clickable
                            }

                            // 4. Validasi Konfirmasi Password
                            if (password != confirmPassword) {
                                Toast.makeText(context, "Password dan Konfirmasi Password tidak cocok", Toast.LENGTH_LONG).show()
                                return@clickable
                            }

                            // 5. JIKA LOLOS SEMUA VALIDASI:
                            // Baru izinkan navigasi
                            navController.navigate("personal_info")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.signup),
                        contentDescription = "Sign Up Button",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // ... (BottomSignInText tidak berubah)
            BottomSignInText(navController = navController)
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// =================================================================
// KOMPONEN PENDUKUNG
// =================================================================


@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 👈 PERUBAHAN 2: RegisterForm sekarang menerima parameter (menjadi "bodoh")
fun RegisterForm(
    email: String,
    onEmailChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit
) {
    // 👈 PERUBAHAN 3: HAPUS state yang sudah diangkat ke ViewModel
    // var email by remember { mutableStateOf("") }
    // var username by remember { mutableStateOf("") }
    // var password by remember { mutableStateOf("") }
    // var confirmPassword by remember { mutableStateOf("") }

    // State untuk UI (visibility password) tetap di sini, karena tidak perlu disimpan
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val containerColor = Color(0xFFE0E0E0)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 👈 PERUBAHAN 4: Hubungkan InputField ke parameter
        // 1. Email
        RegisterInputField(
            value = email, // 👈 Gunakan parameter
            onValueChange = onEmailChange, // 👈 Gunakan parameter
            label = "Email",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            keyboardType = KeyboardType.Email,
            containerColor = containerColor
        )

        // 2. Username
        RegisterInputField(
            value = username, // 👈 Gunakan parameter
            onValueChange = onUsernameChange, // 👈 Gunakan parameter
            label = "Username",
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
            keyboardType = KeyboardType.Text,
            containerColor = containerColor
        )

        // 3. Password
        RegisterPasswordInputField(
            value = password, // 👈 Gunakan parameter
            onValueChange = onPasswordChange, // 👈 Gunakan parameter
            label = "Password",
            passwordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            containerColor = containerColor
        )

        // 4. Confirm Password
        RegisterPasswordInputField(
            value = confirmPassword, // 👈 Gunakan parameter
            onValueChange = onConfirmPasswordChange, // 👈 Gunakan parameter
            label = "Confirm password",
            passwordVisible = confirmPasswordVisible,
            onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
            containerColor = containerColor
        )
    }
}

// Composable untuk Input Field umum (TIDAK BERUBAH)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable () -> Unit,
    keyboardType: KeyboardType,
    containerColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.Gray,
            cursorColor = Color.Gray
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

// Composable untuk Password Field (TIDAK BERUBAH)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    containerColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
        trailingIcon = {
            val imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onVisibilityToggle) {
                Icon(imageVector = imageVector, contentDescription = "Toggle password visibility", tint = Color.Gray)
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.Gray,
            cursorColor = Color.Gray
        )
    )
}

// (TIDAK BERUBAH)
@Composable
fun BottomSignInText(navController: NavHostController) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Black, fontSize = 16.sp)) {
            append("Already Have an Account? ")
        }
        pushStringAnnotation(tag = "SIGNIN", annotation = "sign_in")
        // 🔽 PERUBAHAN DI SINI 🔽
        withStyle(style = SpanStyle(
            color = Color(0xFFFC7100),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textDecoration = TextDecoration.Underline // 👈 TAMBAHKAN BARIS INI
        )) {
            append("Sign in")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "SIGNIN", start = offset, end = offset)
                .firstOrNull()?.let {
                    // Navigasi kembali ke login_screen
                    navController.navigate(it.item) {
                        popUpTo("sign_in") { inclusive = true } // Clear back stack jika perlu, atau gunakan popBackStack()
                    }
                }
        }
    )
}