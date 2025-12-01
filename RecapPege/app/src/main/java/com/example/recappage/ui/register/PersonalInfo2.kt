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
import androidx.compose.material3.MaterialTheme // 👈 WAJIB IMPORT INI

@Composable
fun PersonalInfo2Screen(navController: NavHostController, viewModel: RegistrationViewModel) {
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

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // Logika Toggle untuk Alergi
                val onAllergyToggle: (String) -> Unit = { allergyName ->
                    val currentAllergies = viewModel.allergies.value
                    viewModel.allergies.value = if (currentAllergies.contains(allergyName)) {
                        currentAllergies - allergyName // Hapus jika sudah ada
                    } else {
                        currentAllergies + allergyName // Tambah jika belum ada
                    }
                }

                // 1. Alergi Section
                AllergiesSection(
                    selectedAllergies = viewModel.allergies.value,
                    onAllergyToggle = onAllergyToggle
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Logika Toggle untuk Diet
                val onDietToggle: (String) -> Unit = { dietName ->
                    val currentDiets = viewModel.diets.value
                    viewModel.diets.value = if (currentDiets.contains(dietName)) {
                        currentDiets - dietName // Hapus jika sudah ada
                    } else {
                        currentDiets + dietName // Tambah jika belum ada
                    }
                }

                // 2. Dietary Style Section
                DietarySection(
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    selectedDiets = viewModel.diets.value,
                    onDietToggle = onDietToggle
                )
            }
        }
    }
}

// =================================================================
// KOMPONEN: ALERGI
// =================================================================

@Composable
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
        Text(
            text = "Do you have any food allergies?",
            fontSize = 23.sp,
            // 🔥 GANTI: color = Color(0xFFFC7100) → MaterialTheme.colorScheme.primary
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontFamily = SourceSerifPro,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "You can select more than one",
            fontSize = 16.sp,
            // ✅ Warna ini (0xFF555555) biarkan statis
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
                AllergyCheckbox(
                    label = "Dairy",
                    isChecked = selectedAllergies.contains("Dairy"),
                    onToggle = { onAllergyToggle("Dairy") }
                )
                AllergyCheckbox(
                    label = "Seafood",
                    isChecked = selectedAllergies.contains("Seafood"),
                    onToggle = { onAllergyToggle("Seafood") }
                )
                AllergyCheckbox(
                    label = "Eggs",
                    isChecked = selectedAllergies.contains("Eggs"),
                    onToggle = { onAllergyToggle("Eggs") }
                )
            }
            // Kolom 2
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                AllergyCheckbox(
                    label = "Nuts",
                    isChecked = selectedAllergies.contains("Nuts"),
                    onToggle = { onAllergyToggle("Nuts") }
                )
                AllergyCheckbox(
                    label = "Gluten",
                    isChecked = selectedAllergies.contains("Gluten"),
                    onToggle = { onAllergyToggle("Gluten") }
                )
                AllergyCheckbox(
                    label = "Soy",
                    isChecked = selectedAllergies.contains("Soy"),
                    onToggle = { onAllergyToggle("Soy") }
                )
            }
        }
    }
}

@Composable
fun AllergyCheckbox(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 0.dp)
            .clickable { onToggle() }
    ) {
        CustomImageCheckbox(
            isChecked = isChecked,
            onCheckedChange = { _ -> onToggle() },
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = label,
            fontSize = 16.sp,
            // 🔥 GANTI: color = Color.Black → MaterialTheme.colorScheme.onSurface
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}


@Composable
fun CustomImageCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = if (isChecked) R.drawable.checked_checkbox else R.drawable.checkbox

    Box(
        modifier = modifier
            .size(32.dp)
            .clickable { onCheckedChange(!isChecked) },
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
fun DietarySection(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    selectedDiets: Set<String>,
    onDietToggle: (String) -> Unit
) {
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
                // 🔥 PERUBAHAN DI SINI: Judul disesuaikan stylenya (Hijau, 23sp, Bold)
                Text(
                    text = "Choose your dietary style",
                    // 🔥 GANTI: color = Color(0xFF5CA135) → MaterialTheme.colorScheme.onBackground
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 23.sp,
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
                        val isSelected = selectedDiets.contains(style)
                        DietaryButton(
                            label = style,
                            isSelected = isSelected,
                            onClick = { onDietToggle(style) }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                NextButton(navController)
            }
        }
    }
}

@Composable
fun DietaryButton(label: String, isSelected: Boolean, onClick: () -> Unit) {

    val imageRes = if (isSelected) R.drawable.dietarybuttonon else R.drawable.dietarybutton

    // 🔥 GANTI: textColor yang tidak dipilih menggunakan onSurface (DarkGray)
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
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

@Composable
fun NextButton(navController: NavHostController) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { navController.navigate("types_foods") },
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