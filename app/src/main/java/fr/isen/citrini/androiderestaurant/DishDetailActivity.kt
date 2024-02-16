package fr.isen.citrini.androiderestaurant

import Category
import Dish
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import fr.isen.citrini.androiderestaurant.ui.theme.AndroidERestaurantTheme

class DishDetailActivity : ComponentActivity() {
    private var dish:Dish = Dish("","","","","","", listOf(), listOf(), listOf())
    private var cartItemCountState = mutableIntStateOf(Cart.getNumberOfItems())

    override fun onCreate(savedInstanceState: Bundle?) {
        Cart.setContext(this)
        dish = Gson().fromJson(intent.getStringExtra("dish"), Dish::class.java)
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                dishDetailView()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Cart.setContext(this)
        cartItemCountState.value = Cart.getNumberOfItems()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
    private fun dishDetailView() {
        var quantity = mutableIntStateOf(1)
        var price = mutableFloatStateOf(dish.prices[0].price)

        Scaffold(
            topBar = {
                header(cartItemCountState=cartItemCountState)
            },
            bottomBar = {
                Button(
                    onClick = {
                        Cart.addDish(dish, quantity.value)
                        cartItemCountState.value = Cart.getNumberOfItems()
                        AlertDialog.Builder(this)
                            .setTitle("Added to cart")
                            .setMessage(dish.nameEn + " x" + quantity.value + " added to cart !")
                            .setPositiveButton("OK") { dialog, which -> }
                            .show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors( containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Total: ${(price.value * quantity.value)}€",
                        color = Color.White
                    )
                }
            }
        ) {
            // Width of the screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        val pagerState = rememberPagerState(pageCount = {
                            dish.images.size
                        })
                        HorizontalPager(state = pagerState) { page ->
                            TakeTheBestImage(dish.images[page])
                        }
                    }
                }
                item {
                    Text(
                        text = dish.nameFr,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(4.dp)
                            .padding(horizontal = 2.dp)
                    )
                }
                item {
                    Text(
                        text = dish.ingredients.joinToString(", ") { it.nameFr },
                        modifier = Modifier
                            .padding(4.dp)
                            .padding(horizontal = 2.dp)
                    )
                }
                item {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (quantity.value > 1) quantity.value--}) {
                            Icon(painter = painterResource(id = R.drawable.baseline_remove_circle_24), contentDescription = "Add to cart")
                        }
                        Text(text = quantity.value.toString() , modifier = Modifier.padding(8.dp))
                        IconButton(onClick = { quantity.value++}) {
                            Icon(painter = painterResource(id = R.drawable.baseline_add_circle_24), contentDescription = "Add to favorite")
                        }
                    }
                }
            }
        }
    }
}