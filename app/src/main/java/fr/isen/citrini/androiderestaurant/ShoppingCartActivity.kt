package fr.isen.citrini.androiderestaurant

import Dish
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import fr.isen.citrini.androiderestaurant.service.Cart
import fr.isen.citrini.androiderestaurant.service.ImageService
import fr.isen.citrini.androiderestaurant.ui.theme.AndroidERestaurantTheme

class ShoppingCartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Cart.setContext(this)
        setContent {
            AndroidERestaurantTheme {
                ShoppingView()
            }
        }
        super.onCreate(savedInstanceState)
    }

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
    private fun ShoppingView() {
        val context = LocalContext.current
        val cartState = mutableStateOf(Cart.getCart())
        val totalState = mutableFloatStateOf(Cart.getTotalPrice())
        Scaffold(
            topBar = {
                ShoppingHeader()
            },
            bottomBar = {
                var total = 0.0f
                for (item in cartState.value) {
                    total += item.dish.prices[0].price * item.quantity
                }
                Button(onClick = {
                    Toast.makeText(context, "Order for ${totalState.floatValue} €", Toast.LENGTH_SHORT).show()
                },
                    enabled = cartState.value.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()) {
                    Text("${getString(R.string.order)} ${totalState.floatValue} €", color = Color.White)
                    
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                cartState.value.let {items ->
                    items(items) { index ->
                        val dish = index.dish
                        val quantityState = remember { mutableIntStateOf(index.quantity) }
                        ListItem(
                            headlineContent = {
                                ListItem(
                                    headlineContent = { Text(dish.nameFr) },
                                    supportingContent = { Text(
                                        text="Price: ${dish.prices[0].price}€ x ${quantityState.intValue} = ${dish.prices[0].price * quantityState.intValue}€",
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    ) },
                                    leadingContent = { ImageService.ImageHandler(dish.images) },
                                )
                              },
                            supportingContent = {
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { Cart.removeItem(dish); quantityState.intValue = quantityState.intValue - 1; totalState.floatValue = Cart.getTotalPrice(); cartState.value = Cart.getCart() }) {
                                        Icon(painter = painterResource(id = R.drawable.baseline_remove_circle_24), contentDescription = "Remove from cart")
                                    }
                                    Text(text = quantityState.intValue.toString() , modifier = Modifier.padding(8.dp))
                                    IconButton(onClick = { Cart.addDish(dish, 1); quantityState.intValue = quantityState.intValue + 1; totalState.floatValue = Cart.getTotalPrice(); cartState.value = Cart.getCart()}) {
                                        Icon(painter = painterResource(id = R.drawable.baseline_add_circle_24), contentDescription = "Add to cart")
                                    }
                                    Button(onClick = { Cart.deleteItem(dish); totalState.floatValue = Cart.getTotalPrice(); cartState.value = Cart.getCart() }, modifier = Modifier.padding(8.dp)) {
                                        Text(text = getString(R.string.delete), color = Color.White)
                                    }
                                }
                            },
                            modifier = Modifier.clickable { onClick(dish) }
                        )
                    }
                }
            }
        }
    }

    private fun onClick(dish: Dish) {
        val intent = Intent(this, DishDetailActivity::class.java)
        intent.putExtra("dish", Gson().toJson(dish))
        startActivity(intent)
    }

    override fun onStart() {
        Log.d("CategoryActivity", "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("CategoryActivity", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("CategoryActivity", "onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("CategoryActivity", "onDestroy")
        super.onDestroy()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShoppingHeader() {
        TopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            title = {
                Text(text = getString(R.string.title_activity_shopping_cart), color = Color.White)
            },
        )
    }
}