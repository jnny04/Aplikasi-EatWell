package com.example.recappage.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.draw.clip
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.theme.SourceSans3
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Divider
import androidx.compose.ui.text.style.TextAlign // Tambahkan import TextAlign
import com.example.recappage.R
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.components.TopBorder

// Enum untuk mengelola status gender
enum class Gender { MALE, FEMALE }


@Composable
fun PersonalInfoScreen(navController: NavHostController, viewModel: RegistrationViewModel) { // 👈 Tambah viewModel
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                // ... (Judul tidak berubah)
                Text(
                    text = "Personal Information",
                    fontSize = 24.sp,
                    color = Color(0xFFFC7100),
                    fontWeight = FontWeight.Bold,
                    fontFamily = SourceSerifPro,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Tell us more about you",
                    fontSize = 16.sp,
                    color = Color(0xFF555555),
                    fontFamily = SourceSerifPro,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 👈 PERUBAHAN 1: Hubungkan GenderSelector ke ViewModel
                // Mapping dari String? (di ViewModel) ke Gender? (di Composable)
                val selectedGender = when (viewModel.gender.value) {
                    "MALE" -> Gender.MALE
                    "FEMALE" -> Gender.FEMALE
                    else -> null
                }

                GenderSelector(
                    selectedGender = selectedGender,
                    onGenderSelected = { newGender ->
                        // Mapping dari Gender (dari Composable) ke String (untuk ViewModel)
                        viewModel.gender.value = newGender.name
                    }
                )

                Spacer(modifier = Modifier.height(35.dp))

                // 👈 PERUBAHAN 2: Hubungkan SimpleInputRow ke ViewModel
                // Age
                SimpleInputRow(
                    label = "Age", unit = "years", min = 0, max = 100,
                    value = viewModel.age.value, // 👈 Beri state dari ViewModel
                    onValueChange = { viewModel.age.value = it } // 👈 Beri cara ubah state
                )
                Spacer(modifier = Modifier.height(35.dp))

                // Height
                SimpleInputRow(
                    label = "Height", unit = "cm", min = 0, max = 300,
                    value = viewModel.height.value, // 👈 Beri state dari ViewModel
                    onValueChange = { viewModel.height.value = it } // 👈 Beri cara ubah state
                )
                Spacer(modifier = Modifier.height(35.dp))

                // Weight
                SimpleInputRow(
                    label = "Weight", unit = "kg", min = 0, max = 700,
                    value = viewModel.weight.value, // 👈 Beri state dari ViewModel
                    onValueChange = { viewModel.weight.value = it } // 👈 Beri cara ubah state
                )

                Spacer(modifier = Modifier.height(35.dp))

                // 👈 PERUBAHAN 3: Hubungkan BirthdayInputRow ke ViewModel
                BirthdayInputRow(
                    year = viewModel.birthYear.value,
                    onYearChange = { viewModel.birthYear.value = it },
                    month = viewModel.birthMonth.value,
                    onMonthChange = { viewModel.birthMonth.value = it },
                    date = viewModel.birthDate.value,
                    onDateChange = { viewModel.birthDate.value = it }
                )

                Spacer(modifier = Modifier.weight(1f))

                // ... (Tombol Start tidak berubah, hanya navigasi)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Box (Tombol Start!) sekarang di atas
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { navController.navigate("personal_info_2") },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.start),
                            contentDescription = "Start Button",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // 2. Beri jarak 8.dp
//                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Text sekarang di bawah
                    Text(
                        text = "Time to add your preferences! Press start to continue",
                        fontSize = 10.sp,
                        color = Color.Black,
                        fontFamily = SourceSans3,
                        fontWeight = FontWeight.Light
                        // 👈 modifier padding bottom dihapus
                    )

                    // 4. Spacer bawah tetap ada
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

// =================================================================
// KOMPONEN PENDUKUNG PERSONAL INFO SCREEN
// =================================================================

@Composable
// 👈 PERUBAHAN 4: GenderSelector menerima state
fun GenderSelector(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit
) {
    // 👈 HAPUS state internal
    // var selectedGender by remember { mutableStateOf<Gender?>(null) }

    Text(
        text = "Gender",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        fontFamily = SourceSans3
    )
    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Male Selector
        GenderButton(
            label = "Male",
            iconOff = R.drawable.maleoff,
            iconOn = R.drawable.maleon,
            isSelected = selectedGender == Gender.MALE, // 👈 Gunakan parameter
            onClick = { onGenderSelected(Gender.MALE) } // 👈 Panggil event
        )

        Spacer(modifier = Modifier.width(48.dp))

        // Female Selector
        GenderButton(
            label = "Female",
            iconOff = R.drawable.femaleoff,
            iconOn = R.drawable.femaleon,
            isSelected = selectedGender == Gender.FEMALE, // 👈 Gunakan parameter
            onClick = { onGenderSelected(Gender.FEMALE) } // 👈 Panggil event
        )
    }
}

// (GenderButton tidak perlu diubah, sudah benar)
@Composable
fun GenderButton(
    label: String,
    iconOff: Int,
    iconOn: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // ... (kode implementasi tidak berubah)
    val imageRes = if (isSelected) iconOn else iconOff
    val boxColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFE0E0E0)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(110.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(boxColor)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier.size(110.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SourceSans3,
            modifier = Modifier.offset(y = (-20).dp)
        )
    }
}


// 👈 PERUBAHAN 5: SimpleInputRow menerima state
@Composable
fun SimpleInputRow(
    label: String, unit: String, min: Int, max: Int,
    value: String, // 👈 Terima state
    onValueChange: (String) -> Unit // 👈 Terima event
) {
    // 👈 HAPUS state internal
    // var value by remember { mutableStateOf("") }

    Text(
        text = label,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        fontFamily = SourceSans3
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.width(70.dp)) {
            BasicTextField(
                value = value, // 👈 Gunakan parameter
                onValueChange = { newValue ->
                    // Logika validasi tetap di sini, tapi panggil event onValueChange
                    if (newValue.all { it.isDigit() } && newValue.isNotEmpty()) {
                        val intValue = newValue.toIntOrNull()
                        if (intValue != null && intValue >= min && intValue <= max) {
                            onValueChange(newValue) // 👈 Panggil event
                        } else if (newValue.length < value.length) {
                            onValueChange(newValue) // 👈 Panggil event (untuk hapus)
                        }
                    } else if (newValue.isEmpty()) {
                        onValueChange(newValue) // 👈 Panggil event (untuk string kosong)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = unit,
            color = Color.Black,
            fontSize = 12.sp,
            fontFamily = SourceSerifPro,
            fontWeight = FontWeight.Normal
        )
    }
}

// 👈 PERUBAHAN 6: BirthdayInputRow menerima state
@Composable
fun BirthdayInputRow(
    year: String, onYearChange: (String) -> Unit,
    month: String, onMonthChange: (String) -> Unit,
    date: String, onDateChange: (String) -> Unit
) {
    Text(
        text = "Birthday",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        fontFamily = SourceSans3
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // 1. Year Input (1900-2025)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom
        ) {
            BirthdayInputField(
                modifier = Modifier.weight(1f), min = 0, max = 2025, // 👈 Saya perbaiki min jadi 1900
                value = year, // 👈 Gunakan parameter
                onValueChange = onYearChange // 👈 Gunakan parameter
            )
            Text(
                text = "year",
                fontSize = 12.sp,
                color = Color.Black,
                fontFamily = SourceSerifPro,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // 2. Month Input (1-12)
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom
        ) {
            BirthdayInputField(
                modifier = Modifier.weight(1f), min = 1, max = 12,
                value = month, // 👈 Gunakan parameter
                onValueChange = onMonthChange // 👈 Gunakan parameter
            )
            Text(
                text = "month",
                fontSize = 12.sp,
                color = Color.Black,
                fontFamily = SourceSerifPro,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // 3. Date Input (1-31)
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom
        ) {
            BirthdayInputField(
                modifier = Modifier.weight(1f), min = 1, max = 31,
                value = date, // 👈 Gunakan parameter
                onValueChange = onDateChange // 👈 Gunakan parameter
            )
            Text(
                text = "date",
                fontSize = 12.sp,
                color = Color.Black,
                fontFamily = SourceSerifPro,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// 👈 PERUBAHAN 7: BirthdayInputField menerima state
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayInputField(
    modifier: Modifier, min: Int, max: Int,
    value: String, // 👈 Terima state
    onValueChange: (String) -> Unit // 👈 Terima event
) {
    // 👈 HAPUS state internal
    // var value by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value, // 👈 Gunakan parameter
            onValueChange = { newValue ->
                // Logika validasi tetap di sini, tapi panggil event onValueChange
                if (newValue.all { it.isDigit() } && newValue.isNotEmpty()) {
                    val intValue = newValue.toIntOrNull()
                    if (intValue != null && intValue >= min && intValue <= max) {
                        onValueChange(newValue) // 👈 Panggil event
                    } else if (newValue.length < value.length) {
                        onValueChange(newValue) // 👈 Panggil event (untuk hapus)
                    }
                } else if (newValue.isEmpty()) {
                    onValueChange(newValue) // 👈 Panggil event (untuk string kosong)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                fontFamily = SourceSans3,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}