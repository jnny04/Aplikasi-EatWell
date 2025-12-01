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
import androidx.compose.material3.* // MENGANDUNG ExperimentalMaterial3Api, OutlinedTextField, dll.
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import com.example.recappage.ui.theme.SourceSerifPro
import android.widget.Toast
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.recappage.R
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.components.TopBorder
import androidx.compose.material3.MaterialTheme // WAJIB IMPORT INI


@Composable
fun RegisterScreen(navController: NavHostController, viewModel: RegistrationViewModel) {
    val context = LocalContext.current
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 32.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Text(
                    text = "Your journey starts here!\nTake the first step",
                    fontSize = 24.sp,
                    // 🔥 GANTI: color = Color(0xFFFC7100) → primary
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

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

                // Tombol Sign Up
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val email = viewModel.email.value
                            val password = viewModel.password.value
                            val confirmPassword = viewModel.confirmPassword.value

                            // 2. Validasi Email
                            if (!email.endsWith("@gmail.com", ignoreCase = true)) {
                                Toast.makeText(context, "Email harus menggunakan @gmail.com", Toast.LENGTH_LONG).show()
                                return@clickable
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
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // 🔥 GANTI: containerColor statis → MaterialTheme.colorScheme.surfaceVariant
    val containerColor = MaterialTheme.colorScheme.surfaceVariant

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Email
        RegisterInputField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            keyboardType = KeyboardType.Email,
            containerColor = containerColor
        )

        // 2. Username
        RegisterInputField(
            value = username,
            onValueChange = onUsernameChange,
            label = "Username",
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
            keyboardType = KeyboardType.Text,
            containerColor = containerColor
        )

        // 3. Password
        RegisterPasswordInputField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            passwordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            containerColor = containerColor
        )

        // 4. Confirm Password
        RegisterPasswordInputField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm password",
            passwordVisible = confirmPasswordVisible,
            onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
            containerColor = containerColor
        )
    }
}

// Composable untuk Input Field umum (FIXED OutlinedTextField)
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
        // 🔥 GANTI: label color dan text color
        label = { Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp) },
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
            // 🔥 GANTI: icon color
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            // 🔥 GANTI: cursor color
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        // 🔥 GANTI: textStyle agar teks yang diketik dinamis
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
    )
}

// Composable untuk Password Field (FIXED OutlinedTextField)
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
        // 🔥 GANTI: label color dan text color
        label = { Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
        trailingIcon = {
            val imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onVisibilityToggle) {
                // 🔥 GANTI: icon color
                Icon(imageVector = imageVector, contentDescription = "Toggle password visibility", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
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
            // 🔥 GANTI: icon color
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            // 🔥 GANTI: cursor color
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        // 🔥 GANTI: textStyle agar teks yang diketik dinamis
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
    )
}

// (BottomSignInText)
@Composable
fun BottomSignInText(navController: NavHostController) {
    val annotatedText = buildAnnotatedString {
        withStyle(
            // 🔥 GANTI: color = Color.Black → onSurface
            style = SpanStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
        ) {
            append("Already Have an Account? ")
        }
        pushStringAnnotation(tag = "SIGNIN", annotation = "sign_in")
        // 🔽 PERUBAHAN DI SINI 🔽
        withStyle(style = SpanStyle(
            // 🔥 GANTI: color = Color(0xFFFC7100) → primary
            color = MaterialTheme.colorScheme.primary,
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