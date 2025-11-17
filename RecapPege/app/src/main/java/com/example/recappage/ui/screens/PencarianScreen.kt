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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.model.FoodRecipes
import com.example.recappage.ui.theme.SourceSans3
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
                painter = painterResource(id = R.drawable.kri),
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
                        fontSize = 10.sp,
                        fontFamily = SourceSans3
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (query.isNotEmpty()) {
                                navController.navigate("foodLibrary/$query")
                            }
                        }
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 14.dp, end = 40.dp)
                ) { innerTextField ->

                    if (query.isEmpty()) {
                        Text(
                            "Got something on your mind?",
                            fontSize = 8.sp,
                            color = Color.Gray,
                            fontFamily = SourceSans3
                        )
                    }

                    innerTextField()
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
                                navController.navigate("foodLibrary/$query")
                            }
                        }
                )
            }
        }

        Spacer(Modifier.height(16.dp))


        /** 🟠 REAL-TIME SUGGESTION LIST **/
        if (query.isNotEmpty() && suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Text(
                    "Suggestions",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                suggestions.forEachIndexed { index, title ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("foodLibrary/$title")
                            }
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontFamily = SourceSans3
                        )
                    }

                    if (index != suggestions.lastIndex) {
                        Divider(color = Color(0xFFE0E0E0))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }


        /** 🔵 RESULT LIST AFTER SEARCH **/
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
                                .clickable {
                                    navController.navigate("menuDetails/${recipe.id}")
                                }
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
                    Text(
                        "No results found.",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                is NetworkResult.Loading -> {
                    Text("Searching...", color = Color.Gray, fontSize = 12.sp)
                }

                null -> {}
            }
        }
    }
}
