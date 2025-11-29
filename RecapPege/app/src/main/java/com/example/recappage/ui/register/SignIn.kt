package com.example.recappage.ui.register

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import com.google.firebase.auth.FirebaseAuth
import com.descope.Descope

// ✅ Tambahkan import coroutine (jika belum otomatis)
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavHostController) {

    // State Input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Inisialisasi Auth & Context
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    val containerColor = Color(0xFFD3D3D3)

    // ✅ 1. Buat Scope Coroutine (Untuk menjalankan fungsi suspend Descope)
    val coroutineScope = rememberCoroutineScope()

    // ---------------------------------------------------------
    // ✅ LOGIKA LOGIN DESCOPE (Fixed with Coroutine)
    // ---------------------------------------------------------
    val performDescopeLogin: () -> Unit = {
        val projectId = "P35deW4J1H5rkOUS9ZTwb0hD1pbd"
        val flowUrl = "https://api.descope.com/login/$projectId?flow=sign-up-or-in"

        // ✅ 2. Bungkus pemanggilan Descope dalam coroutineScope.launch
        coroutineScope.launch {
            try {
                Descope.flow.create(
                    flowUrl = flowUrl,
                    deepLinkUrl = "android-app://com.example.recappage/descope"
                ).start(context)
            } catch (e: Exception) {
                Toast.makeText(context, "Error starting Descope: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------------------------------------------------
    // LOGIKA LOGIN FIREBASE
    // ---------------------------------------------------------
    val performSignIn: () -> Unit = {
        if (!email.endsWith("@gmail.com", ignoreCase = true)) {
            Toast.makeText(context, "Hanya akun Gmail (@gmail.com) yang diizinkan untuk masuk.", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()
                        run {
                            navController.navigate(Screen.HomePage.route) {
                                popUpTo(Screen.SignIn.route) { inclusive = true }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Login Gagal. Cek kembali email dan password Anda.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBorder(
                navController = navController,
                showProfile = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.weight(0.1f))

            Image(
                painter = painterResource(id = R.drawable.eatwelllogo),
                contentDescription = "Eatwell Logo",
                modifier = Modifier.height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            WelcomeText()

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Form Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Group: Field + Forgot Password
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email Input
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.emailicon),
                                    contentDescription = "Email Icon",
                                    tint = Color.Gray
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = containerColor,
                                unfocusedContainerColor = containerColor,
                                cursorColor = Color.Gray,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray,
                                focusedLeadingIconColor = Color.Gray,
                                unfocusedLeadingIconColor = Color.Gray
                            )
                        )

                        // Password Input
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.pswordicon),
                                    contentDescription = "Password Icon",
                                    tint = Color.Gray
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = imageVector, contentDescription = "Toggle password visibility", tint = Color.Gray)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = containerColor,
                                unfocusedContainerColor = containerColor,
                                cursorColor = Color.Gray,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray,
                                focusedLeadingIconColor = Color.Gray,
                                unfocusedLeadingIconColor = Color.Gray
                            )
                        )
                    }

                    // Forgot Password Link
                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFFFC7100),
                        fontSize = 10.sp,
                        style = TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .align(Alignment.End)
                            .offset(y = (4).dp, x = (-5).dp)
                            .clickable { navController.navigate(Screen.ForgotPass.route) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Sign In (Firebase)
                HijauImage(onClick = performSignIn)

                Spacer(modifier = Modifier.height(24.dp))

                // Pembatas "OR"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray, thickness = 0.5.dp)
                    Text(
                        text = "  OR  ",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray, thickness = 0.5.dp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Continue with Google (Descope)
                SocialLoginButton(
                    text = "Continue with Google",
                    iconResId = R.drawable.googleicon,
                    onClick = {
                        performDescopeLogin()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BottomRegisterText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                navController = navController
            )
        }
    }
}

@Composable
fun WelcomeText() {
    Text(
        text = "Welcome Back!",
        color = Color(0xFFFC7100),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        style = TextStyle(fontFamily = SourceSerifPro)
    )
}

@Composable
fun HijauImage(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hijau),
            contentDescription = "Hijau Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    iconResId: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF4285F4)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF4285F4)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = "Social Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF4285F4)
            )
        }
    }
}

@Composable
fun BottomRegisterText(modifier: Modifier = Modifier, navController: NavHostController) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Black, fontSize = 16.sp)) {
            append("Don’t have an account? ")
        }
        pushStringAnnotation(tag = "REGISTER", annotation = "register_screen")

        withStyle(style = SpanStyle(
            color = Color(0xFFFC7100),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textDecoration = TextDecoration.Underline
        )) {
            append("Register now")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        modifier = modifier,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "REGISTER", start = offset, end = offset)
                .firstOrNull()?.let {
                    navController.navigate(it.item)
                }
        }
    )
}