package com.example.recappage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.recappage.R
import com.example.recappage.ui.navigation.Screen

@Composable
fun Component18(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isHomeSelected = currentRoute == Screen.HomePage.route
    val isFoodLibrarySelected = currentRoute == Screen.FoodLibrary.route
    val isFavoritesSelected = currentRoute == Screen.Favorites.route
    val isRecapSelected = currentRoute == Screen.WeeklyRecap.route

    Box(
        modifier = modifier
            .requiredWidth(412.dp)
            .requiredHeight(91.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.navigation),
            contentDescription = "Navigation Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(91.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.scan),
            contentDescription = "Scan",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 15.dp)
                .requiredSize(55.dp)
                .clickable {
                    navController.navigate(Screen.Scanner.route) {
                        launchSingleTop = true
                    }
                }
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset(y = 6.dp)
                .padding(horizontal = 32.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ---------- LEFT GROUP ----------
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {

                // HOME
                NavItem(
                    label = "Home",
                    iconOn = R.drawable.homeon,
                    iconOff = R.drawable.fa7regularhomealt,
                    isSelected = isHomeSelected
                ) {
                    navController.navigate(Screen.HomePage.route) {
                        popUpTo(Screen.HomePage.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                // FOOD LIBRARY
                NavItem(
                    label = "Food Library",
                    iconOn = R.drawable.foodlibraryon,
                    iconOff = R.drawable.library,
                    isSelected = isFoodLibrarySelected
                ) {
                    navController.navigate(Screen.FoodLibrary.route) {
                        popUpTo(Screen.HomePage.route)
                        launchSingleTop = true
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ---------- RIGHT GROUP ----------
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {

                // FAVORITES
                NavItem(
                    label = "Favorites",
                    iconOn = R.drawable.favoriteson,
                    iconOff = R.drawable.mdibookloveoutline,
                    isSelected = isFavoritesSelected
                ) {
                    navController.navigate(Screen.Favorites.route) {
                        popUpTo(Screen.HomePage.route)
                        launchSingleTop = true
                    }
                }

                // MY RECAP
                NavItem(
                    label = "My Recap",
                    iconOn = R.drawable.recap,
                    iconOff = R.drawable.recapoff,
                    isSelected = isRecapSelected
                ) {
                    navController.navigate(Screen.WeeklyRecap.route) {
                        popUpTo(Screen.HomePage.route)
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    iconOn: Int,
    iconOff: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        val icon = if (isSelected) iconOn else iconOff

        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.requiredSize(28.dp),
            colorFilter = if (isSelected) null else ColorFilter.tint(Color.White)
        )

        Text(
            label,
            fontSize = 10.sp,
            color = Color.White
        )
    }
}
