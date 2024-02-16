package fr.isen.citrini.androiderestaurant

import Category
import CategoryList
import Dish
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import fr.isen.citrini.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject


class CategoryActivity : ComponentActivity() {
    private var category:Category = Category("","", listOf())
    private var cartItemCountState = mutableIntStateOf(Cart.getNumberOfItems())

    override fun onCreate(savedInstanceState: Bundle?) {
        Cart.setContext(this)
        category = Gson().fromJson(intent.getStringExtra("category"), Category::class.java)

        setContent {
            AndroidERestaurantTheme {
                categoryView()
            }
        }

        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        Cart.setContext(this)
        cartItemCountState.value = Cart.getNumberOfItems()
    }

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
    private fun categoryView() {
        var categoryState = mutableStateOf(category)
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = false),
            onRefresh = {
                refreshCategory(category.nameFr, categoryState)
            },
            content = {
                Scaffold(
                    topBar = {
                        header(categoryState.value?.nameFr ?: "Category", cartItemCountState = cartItemCountState)
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = it.calculateTopPadding())
                    ) {
                        categoryState.value?.let {category ->
                            items(category.items) { index ->
                                DishCell(index)
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun DishCell(dish: Dish) {
        // Take the field nameFr from the ingredients list
        var ingredients = dish.ingredients.map { it.nameFr }
        ListItem(
            headlineContent = { Text(dish.nameFr) },
            supportingContent = { Text(ingredients.joinToString()) },
            trailingContent = {
                if (dish.prices.size > 1)
                    Text(dish.prices.minOf { it.price.toString() } + "€ - " + dish.prices.maxOf { it.price } + "€")
                else
                    Text(dish.prices[0].price.toString() + "€")
                Text(dish.prices[0].price.toString() + "€")
            },
            leadingContent = { ImageHandler(dish.images) },
            modifier = Modifier.clickable { onClick(dish) }
        )
    }

    private fun onClick(dish: Dish) {
        val intent = Intent(this, DishDetailActivity::class.java)
        intent.putExtra("dish", Gson().toJson(dish))
        startActivity(intent)
    }

    private fun refreshCategory(type: String, categoryState: MutableState<Category>) {
        val requestBody = JSONObject()
        requestBody.put("id_shop", 1)
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val cacheKey = "cached_category_${type}"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            { response ->
                val gson = Gson()
                var listCategory = gson.fromJson(response.toString(), CategoryList::class.java).data.find { it.nameFr == type }

                // Cache the result
                cacheCategory(cacheKey, listCategory)
                if (listCategory != null) {
                    categoryState.value = listCategory
                }
                Log.d("Refresh", "requestCategory")

            },
            { _ ->
                Log.d("Refresh", "requestCategory error")
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun cacheCategory(cacheKey: String, category: Category?) {
        val sharedPreferences = getSharedPreferences("MyCache", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (category != null) {
            val categoryJson = Gson().toJson(category)
            editor.putString(cacheKey, categoryJson)
        } else {
            editor.remove(cacheKey)
        }

        editor.apply()
    }
}