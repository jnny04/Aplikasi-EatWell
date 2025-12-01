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
import androidx.compose.ui.draw.clip
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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.descope.Descope
import com.example.recappage.R
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.util.BiometricHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme // 👈 WAJIB IMPORT INI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavHostController) {

    // State Input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Setup Auth & Context
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 🔥 GANTI: containerColor statis → MaterialTheme.colorScheme.surfaceVariant
    val containerColor = MaterialTheme.colorScheme.surfaceVariant

    // --- SETUP BIOMETRIK ---
    val fragmentActivity = context as? FragmentActivity
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(context) }
    val showBiometricButton = isBiometricAvailable

    val performBiometricLogin = {
        if (fragmentActivity != null) {
            BiometricHelper.showBiometricPrompt(
                activity = fragmentActivity,
                onSuccess = {
                    Toast.makeText(context, "Login Biometrik Berhasil!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.HomePage.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onError = { errorMsg ->
                    Toast.makeText(context, "Gagal: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // --- SETUP DESCOPE (GOOGLE) ---
    val performDescopeLogin: () -> Unit = {
        val projectId = "P35deW4J1H5rkOUS9ZTwb0hD1pbd"
        val flowUrl = "https://api.descope.com/login/$projectId?flow=sign-up-or-in"

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

    // --- SETUP FIREBASE EMAIL/PASS ---
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
                        Toast.makeText(context, "Login Gagal. Cek email dan password.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBorder(navController = navController, showProfile = false)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // 🔥 GANTI: background(Color.White) → background(MaterialTheme.colorScheme.background)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.weight(0.1f))

            Image(
                painter = painterResource(id = R.drawable.eatwelllogo),
                contentDescription = "Eatwell Logo",
                modifier = Modifier.height(120.dp),
                contentScale = ContentScale.Fit
            )

            // 🔥 PERBAIKAN 2: Jarak spacer dikecilkan (16 -> 4dp) agar lebih mepet
            Spacer(modifier = Modifier.height(6.dp))

            WelcomeText()

            Spacer(modifier = Modifier.height(15.dp))

            // 🔹 Form Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Input Fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            // 🔥 GANTI: tint = Color.Gray → onSurface.copy
                            Icon(painter = painterResource(id = R.drawable.emailicon), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        },
                        // 🔥 GANTI: Warna teks input
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            // 🔥 GANTI: containerColor
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            // 🔥 GANTI: cursor & label colors
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            // 🔥 GANTI: tint = Color.Gray → onSurface.copy
                            Icon(painter = painterResource(id = R.drawable.pswordicon), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        },
                        // 🔥 GANTI: Warna teks input
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                // 🔥 GANTI: tint = Color.Gray → onSurface.copy
                                Icon(imageVector = image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            // 🔥 GANTI: containerColor
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            // 🔥 GANTI: cursor & label colors
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }

                // Forgot Password
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Forgot Password?",
                        // 🔥 GANTI: color = Color(0xFFFC7100) → primary
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        style = TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(top = 8.dp)
                            .clickable { navController.navigate(Screen.ForgotPass.route) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ✅ ROW TOMBOL UTAMA (SIGN IN + FINGERPRINT)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 1. Tombol Sign In
                    HijauImage(onClick = performSignIn)

                    // 2. Tombol Biometrik
                    if (showBiometricButton) {
                        Spacer(modifier = Modifier.width(16.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.fingerprint),
                            contentDescription = "Biometric Login",
                            // 🔥 GANTI: tint = Color(0xFF5CA135) → primary
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { performBiometricLogin() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // PEMBATAS "OR"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 🔥 GANTI: color = Color.Gray → onSurface.copy
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), thickness = 0.5.dp)
                    Text(
                        text = "  OR  ",
                        // 🔥 GANTI: color = Color.Gray → onSurface.copy
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    // 🔥 GANTI: color = Color.Gray → onSurface.copy
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), thickness = 0.5.dp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TOMBOL GOOGLE
                SocialLoginButton(
                    text = "Continue with Google",
                    iconResId = R.drawable.googleicon,
                    onClick = performDescopeLogin
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

// ---------------------------------------------------------
// KOMPONEN HELPER
// ---------------------------------------------------------

@Composable
fun WelcomeText() {
    Text(
        text = "Welcome Back!",
        // 🔥 GANTI: color = Color(0xFFFC7100) → primary
        color = MaterialTheme.colorScheme.primary,
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
            contentDescription = "Sign In Button",
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
    // Warna Biru Google (Kita biarkan statis karena itu brand external)
    val googleBlue = Color(0xFF4285F4)

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, googleBlue), // Border tetap biru
        colors = ButtonDefaults.outlinedButtonColors(
            // 🔥 GANTI: containerColor = Color.White → surface
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = googleBlue
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = googleBlue
            )
        }
    }
}

@Composable
fun BottomRegisterText(modifier: Modifier = Modifier, navController: NavHostController) {
    val annotatedText = buildAnnotatedString {
        withStyle(
            // 🔥 GANTI: color = Color.Black → onSurface
            style = SpanStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
        ) {
            append("Don’t have an account? ")
        }
        pushStringAnnotation(tag = "REGISTER", annotation = "register_screen")

        withStyle(style = SpanStyle(
            // 🔥 GANTI: color = Color(0xFFFC7100) → primary
            color = MaterialTheme.colorScheme.primary,
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