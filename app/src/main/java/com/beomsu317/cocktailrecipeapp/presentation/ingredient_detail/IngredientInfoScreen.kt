package com.beomsu317.cocktailrecipeapp.presentation.ingredient_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.beomsu317.cocktailrecipeapp.common.Constants
import com.beomsu317.cocktailrecipeapp.domain.model.Ingredient
import com.beomsu317.cocktailrecipeapp.presentation.components.ShimmerAnimation
import com.beomsu317.cocktailrecipeapp.presentation.ingredient_detail.components.IngredientInfoTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun IngredientInfoScreen(
    onBackClick: () -> Unit,
    scaffoldState: ScaffoldState,
    viewModel: IngredientInfoViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val oneTimeEventFlow = viewModel.oneTimeEventFlow

    LaunchedEffect(key1 = oneTimeEventFlow) {
        oneTimeEventFlow.collectLatest { oneTimeEvent ->
            when (oneTimeEvent) {
                is IngredientInfoOneTimeEvent.ErrorEvent -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        oneTimeEvent.error,
                        null,
                        SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IngredientInfoTopBar(onBackClick = onBackClick)
        Spacer(modifier = Modifier.height(16.dp))
        IngredientImageSection(
            ingredient = state.ingredient,
            modifier = Modifier
                .weight(0.4f)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        IngredientDescriptionSection(
            ingredient = state.ingredient,
            isLoading = state.isLoading,
            modifier = Modifier.weight(0.6f)
        )

    }
}

@Composable
fun IngredientImageSection(
    ingredient: Ingredient,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("${Constants.BASE_URL}/images/ingredients/${ingredient.strIngredient}.png")
                .crossfade(true)
                .build(),
            contentDescription = ingredient.strIngredient,
            modifier = modifier.align(Center),
            contentScale = ContentScale.Fit,
            loading = {
                ShimmerAnimation { brush ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = brush)
                    )
                }
            }
        )
        if (ingredient.strAlcohol.equals("Yes")) {
            Box(
                modifier = Modifier
                    .align(TopEnd)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), clip = true)
                    .background(MaterialTheme.colors.primary)
            ) {
                Text(
                    text = if (ingredient.strABV.isNullOrEmpty()) {
                        "Alcohol"
                    } else {
                        "Alcohol ${ingredient.strABV} %"
                    },
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun IngredientDescriptionSection(
    ingredient: Ingredient,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = ingredient.strIngredient,
                style = MaterialTheme.typography.h2,
                fontWeight = FontWeight.Bold,
            )

            val scrollState = rememberScrollState()
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = ingredient.strDescription ?: "No description.",
                    style = MaterialTheme.typography.body1
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.align(Center)
            )
        }
    }
}