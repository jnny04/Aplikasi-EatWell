package com.example.recappage.ui.register // 👈 Pastikan package Anda konsisten

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.components.TopBorder


@Composable
fun PersonalInfo2Screen(navController: NavHostController, viewModel: RegistrationViewModel) { // 👈 Tambah viewModel
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // 🔽 TAMBAHKAN 'topBar' INI 🔽
        topBar = {
            TopBorder(navController = navController, showProfile = false)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White), // 👈 TAMBAHKAN BACKGROUND DI SINI
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // 👈 PERUBAHAN 1: Buat logika Toggle untuk Alergi
                val onAllergyToggle: (String) -> Unit = { allergyName ->
                    val currentAllergies = viewModel.allergies.value
                    viewModel.allergies.value = if (currentAllergies.contains(allergyName)) {
                        currentAllergies - allergyName // Hapus jika sudah ada
                    } else {
                        currentAllergies + allergyName // Tambah jika belum ada
                    }
                }

                // 1. Alergi Section
                // 👈 Berikan state dan event ke AllergiesSection
                AllergiesSection(
                    selectedAllergies = viewModel.allergies.value,
                    onAllergyToggle = onAllergyToggle
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 👈 PERUBAHAN 2: Buat logika Toggle untuk Diet
                val onDietToggle: (String) -> Unit = { dietName ->
                    val currentDiets = viewModel.diets.value
                    viewModel.diets.value = if (currentDiets.contains(dietName)) {
                        currentDiets - dietName // Hapus jika sudah ada
                    } else {
                        currentDiets + dietName // Tambah jika belum ada
                    }
                }

                // 2. Dietary Style Section
                // 👈 Berikan state dan event ke DietarySection
                DietarySection(
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    selectedDiets = viewModel.diets.value, // 👈 Beri state
                    onDietToggle = onDietToggle // 👈 Beri event
                )
            }
        }
    }
}

// =================================================================
// KOMPONEN: ALERGI
// =================================================================

@Composable
// 👈 PERUBAHAN 3: AllergiesSection menerima state
fun AllergiesSection(
    selectedAllergies: Set<String>,
    onAllergyToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (Teks Judul tidak berubah)
        Text(
            text = "Do you have any food allergies?",
            fontSize = 23.sp,
            color = Color(0xFFFC7100),
            fontWeight = FontWeight.Bold,
            fontFamily = SourceSerifPro,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "You can select more than one",
            fontSize = 16.sp,
            color = Color(0xFF555555),
            fontFamily = SourceSerifPro,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 2. Checkbox Grid
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(60.dp)
        ) {
            // Kolom 1
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                // 👈 PERUBAHAN 4: Hubungkan state ke AllergyCheckbox
                AllergyCheckbox(
                    label = "Dairy",
                    isChecked = selectedAllergies.contains("Dairy"), // 👈 Gunakan parameter
                    onToggle = { onAllergyToggle("Dairy") } // 👈 Gunakan event
                )
                AllergyCheckbox(
                    label = "Seafood",
                    isChecked = selectedAllergies.contains("Seafood"), // 👈 Gunakan parameter
                    onToggle = { onAllergyToggle("Seafood") } // 👈 Gunakan event
                )
                AllergyCheckbox(
                    label = "Eggs",
                    isChecked = selectedAllergies.contains("Eggs"), // 👈 Gunakan parameter
                    onToggle = { onAllergyToggle("Eggs") } // 👈 Gunakan event
                )
            }
            // Kolom 2
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                AllergyCheckbox(
                    label = "Nuts",
                    isChecked = selectedAllergies.contains("Nuts"), // 👈 Gunakan parameter
                    onToggle = { onAllergyToggle("Nuts") } // 👈 Gunakan event
                )
                AllergyCheckbox(
                    label = "Gluten",
                    isChecked = selectedAllergies.contains("Gluten"), // 👈 Gunakan parameter
                    onToggle = { onAllergyToggle("Gluten") } // 👈 Gunakan event
                )
                AllergyCheckbox(
                    label = "Soy",
                    isChecked = selectedAllergies.contains("Soy"), // 👈 Gunakan parameter
                    onToggle = { onAllergyToggle("Soy") } // 👈 Gunakan event
                )
            }
        }
    }
}

@Composable
// 👈 PERUBAHAN 5: AllergyCheckbox menerima state (menjadi "bodoh")
fun AllergyCheckbox(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit // 👈 Event diubah menjadi onToggle
) {
    // 👈 HAPUS state internal
    // var isChecked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 0.dp)
            .clickable { onToggle() } // 👈 Buat seluruh baris bisa diklik
    ) {
        CustomImageCheckbox(
            isChecked = isChecked, // 👈 Gunakan parameter
            onCheckedChange = { _ -> onToggle() }, // 👈 Panggil event onToggle
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}


@Composable
// (CustomImageCheckbox TIDAK BERUBAH, sudah "bodoh" dan benar)
fun CustomImageCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = if (isChecked) R.drawable.checked_checkbox else R.drawable.checkbox

    Box(
        modifier = modifier
            .size(32.dp)
            .clickable { onCheckedChange(!isChecked) }, // 👈 Logika ini sudah benar
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = if (isChecked) "Checked" else "Unchecked",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
// =================================================================
// KOMPONEN: GAYA DIET
// =================================================================

@Composable
// 👈 PERUBAHAN 6: DietarySection menerima state
fun DietarySection(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    selectedDiets: Set<String>,
    onDietToggle: (String) -> Unit
) {
    // 👈 HAPUS state internal
    // var selectedDiets by remember { mutableStateOf(emptySet<String>()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            // ... (Gambar Background tidak berubah)
            Image(
                painter = painterResource(id = R.drawable.dietarybox),
                contentDescription = "Dietary Section Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // ... (Teks Judul tidak berubah)
                Text(
                    text = "Choose your dietary style",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = SourceSerifPro,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Tombol Diet
                val dietaryStyles = listOf(
                    "Vegetarian", "Vegan", "Halal", "Low Carb", "Pescatarian", "High Protein"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    dietaryStyles.forEach { style ->
                        // 👈 PERUBAHAN 7: Hubungkan state dan event ke DietaryButton
                        val isSelected = selectedDiets.contains(style) // 👈 Gunakan parameter
                        DietaryButton(
                            label = style,
                            isSelected = isSelected,
                            onClick = { onDietToggle(style) } // 👈 Panggil event
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                NextButton(navController)
            }
        }
    }
}

// (DietaryButton TIDAK BERUBAH, sudah "bodoh" dan benar)
@Composable
fun DietaryButton(label: String, isSelected: Boolean, onClick: () -> Unit) {

    val imageRes = if (isSelected) R.drawable.dietarybuttonon else R.drawable.dietarybutton
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick) // 👈 Ini sudah benar
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // ... (Implementasi Image dan Text tidak berubah)
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Dietary style: $label",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = label,
            fontSize = 16.sp,
            color = textColor,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Medium
        )
    }
}

// (NextButton TIDAK BERUBAH, hanya navigasi)
@Composable
fun NextButton(navController: NavHostController) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { navController.navigate("types_foods") }, // 👈 Navigasi sudah benar
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.next),
            contentDescription = "Next Button",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}