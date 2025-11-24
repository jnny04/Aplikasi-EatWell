package com.example.recappage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.recappage.ui.register.*
import com.example.recappage.ui.screens.*
import com.example.recappage.ui.viewmodel.FavouriteViewModel
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.viewmodel.StreakViewModel
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {

    object WeeklyRecap : Screen("weekly_recap")
    object MonthlyRecap : Screen("monthly_recap")
    object IntakeDetail : Screen("intake_detail")
    object HomePage : Screen("home_page")
    object FoodLibrary : Screen("foodLibrary") {
        fun createRoute(query: String): String =
            "foodLibrary/${URLEncoder.encode(query, "UTF-8")}"
    }
    object Favorites : Screen("favorites")
    object Scanner : Screen("scanner")
    object MenuDetails : Screen("menuDetails/{id}") {
        fun createRoute(id: Int): String = "menuDetails/$id"
    }
    object SignIn : Screen("sign_in")
    object Register : Screen("register_screen")
    object PersonalInfo : Screen("personal_info")
    object PersonalInfo2 : Screen("personal_info_2")
    object TypesFoods : Screen("types_foods")
    object MainGoal : Screen("main_goal")
    object SetupScreen : Screen("setup_screen_route")
    object Profile : Screen("profile")
    object PencarianScreen : Screen("pencarian")
    object ForgotPass : Screen("forgot_pass") // ✅ Pastikan baris ini ada
    object OrderPage : Screen("order_page")
}

@Composable
fun AppNavigation(navController: NavHostController) {

    val regViewModel: RegistrationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.HomePage.route
    ) {

        // ---------------- AUTH ----------------
        composable(Screen.SignIn.route) {
            SignInScreen(navController)
        }
        composable(Screen.ForgotPass.route) {
            ForgotPassScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, regViewModel)
        }

        composable(Screen.PersonalInfo.route) {
            PersonalInfoScreen(navController, regViewModel)
        }

        composable(Screen.PersonalInfo2.route) {
            PersonalInfo2Screen(navController, regViewModel)
        }

        composable(Screen.TypesFoods.route) {
            TypesFoodsScreen(navController, regViewModel)
        }

        composable(Screen.MainGoal.route) {
            MainGoalScreen(navController, regViewModel)
        }

        composable(Screen.SetupScreen.route) {
            SetupScreen(navController, regViewModel)
        }

        // ---------------- MAIN PAGES ----------------
        composable(Screen.HomePage.route) {
            HomePage(navController)
        }

        // ---------------- FOOD LIBRARY ----------------
        // ---------------- FOOD LIBRARY tanpa query (bottom nav) ----------------
        composable(
            route = Screen.FoodLibrary.route
        ) {
            val favVM: FavouriteViewModel = hiltViewModel()
            FoodLibraryPage(
                navController = navController,
                favVM = favVM,
                searchQuery = null
            )
        }

// ---------------- FOOD LIBRARY dengan query (search) ----------------
        composable(
            route = "foodLibrary/{query}",
            arguments = listOf(
                navArgument("query") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val favVM: FavouriteViewModel = hiltViewModel(backStackEntry)

            val raw = backStackEntry.arguments?.getString("query") ?: ""
            val decoded = URLDecoder.decode(raw, "UTF-8")

            FoodLibraryPage(
                navController = navController,
                favVM = favVM,
                searchQuery = decoded
            )
        }
        composable(Screen.Scanner.route) {
            ScannerPage(navController)
        }

        composable(Screen.Favorites.route) {
            // Inisialisasi ViewModel khusus untuk halaman ini
            val favVM: FavouriteViewModel = hiltViewModel()

            FavouritePage(
                navController = navController,
                favVM = favVM
            )
        }
        // ---------------- PROFILE ----------------
        composable(Screen.Profile.route) {

            val context = LocalContext.current
            val streakVM = StreakViewModel(context)

            regViewModel.loadUserProfile()

            ProfileScreen(
                navController = navController,
                streakViewModel = streakVM,
                regViewModel = regViewModel
            )
        }

        composable(Screen.PencarianScreen.route) {
            PencarianScreen(navController)
        }

        composable(Screen.OrderPage.route) {
            OrderPage(navController)
        }

        // ---------------- INTAKE & RECAP ----------------
        composable(Screen.WeeklyRecap.route) {
            IntakeRecapPageWeekly(navController)
        }

        composable(Screen.MonthlyRecap.route) {
            MonthlyRecapPage(navController)
        }

        composable(Screen.IntakeDetail.route) {
            IntakeDetailPage(navController)
        }

        // ---------------- MENU DETAILS ----------------
        composable(
            route = Screen.MenuDetails.route,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            MenuDetailsPage(
                navController = navController,
                foodId = id
            )
        }
    }
}