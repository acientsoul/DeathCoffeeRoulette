package com.death.coffeeroulette.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.death.coffeeroulette.game.RouletteViewModel
import com.death.coffeeroulette.ui.screens.GameScreen
import com.death.coffeeroulette.ui.screens.IntroScreen
import com.death.coffeeroulette.ui.screens.MemberScreen
import com.death.coffeeroulette.ui.screens.ResultScreen

object Routes {
    const val INTRO = "intro"
    const val MEMBER_SELECT = "member_select"
    const val GAME = "game"
    const val RESULT = "result"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val viewModel: RouletteViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.INTRO) {

        // 인트로 화면 (폭발 + 제목)
        composable(Routes.INTRO) {
            IntroScreen(
                onStartClick = {
                    viewModel.goToMemberSelect()
                    navController.navigate(Routes.MEMBER_SELECT) {
                        popUpTo(Routes.INTRO) { inclusive = true }
                    }
                }
            )
        }

        // 멤버 선택 화면
        composable(Routes.MEMBER_SELECT) {
            MemberScreen(
                viewModel = viewModel,
                onStartGame = {
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.MEMBER_SELECT)
                    }
                }
            )
        }

        // 게임 화면 (리볼버 룰렛)
        composable(Routes.GAME) {
            GameScreen(viewModel = viewModel)

            // 게임 종료 시 결과 화면으로 자동 전환
            if (viewModel.gamePhase.value == com.death.coffeeroulette.game.GamePhase.RESULT) {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    navController.navigate(Routes.RESULT) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                }
            }
        }

        // 결과 화면
        composable(Routes.RESULT) {
            ResultScreen(
                viewModel = viewModel,
                onBack = {
                    viewModel.restartGame()
                    navController.navigate(Routes.MEMBER_SELECT) {
                        popUpTo(Routes.RESULT) { inclusive = true }
                    }
                }
            )
        }
    }
}
