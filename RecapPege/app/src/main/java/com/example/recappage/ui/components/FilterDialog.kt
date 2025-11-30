package com.example.recappage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.viewmodel.FilterState

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    var local by remember { mutableStateOf(FilterState()) }

    val hasSelection = with(local) {
        listOf(
            vegetarian, vegan, halal, lowCarb, pescatarian,
            nutsFree, dairyFree, glutenFree, eggFree, noSeafood,

            asian, european, thai, chinese, korean,
            japanese, italian, indian
        ).any { it }
    }

    // Properti ini membuat Dialog bisa menggunakan lebar penuh layar
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                // 🔥 PERUBAHAN DI SINI:
                // Ganti width(380.dp) menjadi fillMaxWidth(0.92f)
                // Artinya: Ambil 92% dari lebar layar HP apapun.
                .fillMaxWidth(0.92f)

                // 🔥 PERUBAHAN DI SINI:
                // Ganti height(565.dp) menjadi heightIn(...)
                // wrapContentHeight: Tinggi menyesuaikan isi konten
                // heightIn(max): Membatasi agar tidak terlalu panjang di tablet/layar besar
                .wrapContentHeight()
                .heightIn(max = 600.dp)

                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // ---------------- HEADER ----------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Picks",
                        fontFamily = SourceSerifPro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF5CA135)
                    )
                    TextButton(onClick = {
                        local = FilterState()
                    }) {
                        Text(
                            "Reset",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Bagian Dietary & Cuisine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Dietary",
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            DietaryList(local) { local = it }
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 25.dp)
                    ) {
                        Text(
                            "Cuisine",
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CuisineList(local) { local = it }
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { onApply(local); onDismiss() },
                        enabled = hasSelection,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasSelection) Color(0xFF5CA135)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(108.dp)
                            .height(40.dp)
                    ) {
                        Text(
                            "Apply",
                            fontFamily = SourceSerifPro,
                            fontWeight = FontWeight.Bold,
                            color = if (hasSelection)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// --- BAGIAN DI BAWAH INI TIDAK ADA YANG DIUBAH ---

@Composable
fun DietaryList(
    local: FilterState,
    onUpdate: (FilterState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        FilterCheckBox("Vegetarian", local.vegetarian) { onUpdate(local.copy(vegetarian = !local.vegetarian)) }
        FilterCheckBox("Vegan", local.vegan) { onUpdate(local.copy(vegan = !local.vegan)) }
        FilterCheckBox("Halal", local.halal) { onUpdate(local.copy(halal = !local.halal)) }
        FilterCheckBox("Low Carb", local.lowCarb) { onUpdate(local.copy(lowCarb = !local.lowCarb)) }
        FilterCheckBox("Pescatarian", local.pescatarian) { onUpdate(local.copy(pescatarian = !local.pescatarian)) }
        FilterCheckBox("Nuts-Free", local.nutsFree) { onUpdate(local.copy(nutsFree = !local.nutsFree)) }
        FilterCheckBox("Dairy-Free", local.dairyFree) { onUpdate(local.copy(dairyFree = !local.dairyFree)) }
        FilterCheckBox("Gluten-Free", local.glutenFree) { onUpdate(local.copy(glutenFree = !local.glutenFree)) }
        FilterCheckBox("Egg-Free", local.eggFree) { onUpdate(local.copy(eggFree = !local.eggFree)) }
        FilterCheckBox("No Seafood", local.noSeafood) { onUpdate(local.copy(noSeafood = !local.noSeafood)) }
    }
}

@Composable
fun CuisineList(
    local: FilterState,
    onUpdate: (FilterState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        FilterCheckBox("Asian", local.asian) { onUpdate(local.copy(asian = !local.asian)) }
        FilterCheckBox("European", local.european) { onUpdate(local.copy(european = !local.european)) }
        FilterCheckBox("Thai", local.thai) { onUpdate(local.copy(thai = !local.thai)) }
        FilterCheckBox("Chinese", local.chinese) { onUpdate(local.copy(chinese = !local.chinese)) }
        FilterCheckBox("Korean", local.korean) { onUpdate(local.copy(korean = !local.korean)) }
        FilterCheckBox("Japanese", local.japanese) { onUpdate(local.copy(japanese = !local.japanese)) }
        FilterCheckBox("Italian", local.italian) { onUpdate(local.copy(italian = !local.italian)) }
        FilterCheckBox("Indian", local.indian) { onUpdate(local.copy(indian = !local.indian)) }
    }
}

@Composable
fun FilterCheckBox(
    label: String,
    checked: Boolean,
    onCheck: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomCheckBox(
            checked = checked,
            onCheckedChange = onCheck
        )
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CustomCheckBox(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (checked) 2.dp else 1.dp,
                color = if (checked)
                    Color(0xFFFC7100)
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onCheckedChange() },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = Color(0xFFFC7100),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}