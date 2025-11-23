package com.example.recappage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.model.FoodRecipes
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro // ✅ Pastikan import ini ada
import com.example.recappage.ui.viewmodel.SearchViewModel
import com.example.recappage.util.NetworkResult

@Composable
fun PencarianScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }

    val searchResult by viewModel.searchResult.observeAsState()
    val suggestions by viewModel.suggestions.observeAsState(emptyList())

    // ✅ 1. OBSERVE DATA HISTORY
    val historyList by viewModel.searchHistory.observeAsState(emptyList())

    // ✅ 2. LOAD HISTORY SAAT LAYAR DIBUKA
    LaunchedEffect(Unit) {
        viewModel.loadSearchHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .background(Color.White)
    ) {

        /** 🔍 SEARCH BAR **/
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.kri), // Icon panah back
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .border(2.dp, Color(0xFFFC7100), RoundedCornerShape(50))
                    .background(Color.White, RoundedCornerShape(50))
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.loadSuggestions(query)
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontFamily = SourceSans3
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (query.isNotEmpty()) {
                                viewModel.saveSearchHistory(query)
                                navController.navigate(Screen.FoodLibrary.createRoute(query))
                            }
                        }
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 14.dp, end = 40.dp)
                        .fillMaxWidth()
                ) { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Got something on your mind?",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontFamily = SourceSans3,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 14.dp)
                        .size(16.dp)
                        .clickable {
                            if (query.isNotEmpty()) {
                                viewModel.saveSearchHistory(query)
                                navController.navigate(Screen.FoodLibrary.createRoute(query))
                            }
                        }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        /** ==========================================================
         * LOGIKA TAMPILAN:
         * 1. Jika Query KOSONG -> Tampilkan HISTORY
         * 2. Jika Query ISI    -> Tampilkan SUGGESTION / RESULT
         * =========================================================== **/

        if (query.isEmpty()) {

            // ✅ TAMPILAN HISTORY LIST (Sesuai Request)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp) // Padding kiri kanan agak masuk
                    .verticalScroll(rememberScrollState())
            ) {
                // Loop data history
                historyList.forEach { historyItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp) // Jarak antar item
                            .clickable {
                                // Kalau history diklik, langsung cari lagi
                                query = historyItem
                                viewModel.saveSearchHistory(historyItem) // Update timestamp
                                navController.navigate(Screen.FoodLibrary.createRoute(historyItem))
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon History (Jam)
                        Image(
                            painter = painterResource(id = R.drawable.history), // ✅ Pastikan nama file history.png
                            contentDescription = "History",
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Text History
                        Text(
                            text = historyItem,
                            fontFamily = SourceSerifPro, // ✅ Font Source Serif Pro
                            fontSize = 16.sp,            // ✅ Ukuran 16sp
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                }
            }

        } else {

            // ✅ TAMPILAN SUGGESTIONS & SEARCH RESULT (Kode Lama)

            // 1. Suggestions
            if (suggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Samakan padding dengan History
                ) {
                    // Loop data suggestions
                    suggestions.forEach { title ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Simpan ke history & cari
                                    viewModel.saveSearchHistory(title)
                                    navController.navigate(Screen.FoodLibrary.createRoute(title))
                                }
                                .padding(vertical = 12.dp), // Jarak antar item (atas-bawah)
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ✅ ICON: searchsuggestion.png
                            Image(
                                painter = painterResource(id = R.drawable.searchsuggestion),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp)) // Jarak icon ke teks

                            // ✅ TEXT: Sesuai format gambar (Serif)
                            Text(
                                text = title,
                                fontFamily = SourceSerifPro, // Menggunakan font Serif
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    // Spacer bawah agar tidak mentok jika list panjang
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 2. Search Results
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (searchResult) {
                    is NetworkResult.Success -> {
                        val data = (searchResult as NetworkResult.Success<FoodRecipes>).data
                        val list = data?.recipes ?: emptyList()
                        list.forEach { recipe ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .clickable { navController.navigate("menuDetails/${recipe.id}") }
                            ) {
                                Text(
                                    recipe.title,
                                    fontFamily = SourceSans3,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        Text("No results found.", color = Color.Red, fontSize = 12.sp)
                    }
                    is NetworkResult.Loading -> {
                        Text("Searching...", color = Color.Gray, fontSize = 12.sp)
                    }
                    null -> {}
                }
            }
        }
    }
}