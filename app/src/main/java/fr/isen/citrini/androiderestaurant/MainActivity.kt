package fr.isen.citrini.androiderestaurant

import Category
import CategoryList
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.citrini.androiderestaurant.service.Cart
import fr.isen.citrini.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject

enum class DishType(val typeFr: String) {
    STARTER("Entrées"),
    MAIN("Plats"),
    DESSERT("Desserts"),
}

class MainActivity : ComponentActivity() {
    private var cartItemCountState = mutableIntStateOf(0)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                SetupMenu();
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartItemCountState.value = Cart.getNumberOfItems()
    }

    /**
     * Display the menu of the application
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun SetupMenu() {
        var errorState = remember { mutableStateOf(false) }
        val context = LocalContext.current;
        Scaffold(
            topBar = {
                header(cartItemCountState = cartItemCountState)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding() + 20.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            )
            {
                Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo",
                    Modifier
                        .size(200.dp)
                        .padding(bottom = 20.dp))
                menuButton(R.string.starter) { requestCategory(DishType.STARTER, errorState) }
                menuButton(R.string.main) { requestCategory(DishType.MAIN, errorState) }
                menuButton(R.string.dessert) { requestCategory(DishType.DESSERT, errorState) }
            }
            if (errorState.value) {
                errorState.value = false
                Toast.makeText(context, "Impossible to get the data from server...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Display a button with the name of the category
     * @param name: Int - Name of the category
     */
    @Composable
    fun menuButton(name: Int, onClick : () -> Unit) {
        val context = LocalContext.current;
        Button(onClick = {
            onClick()
        },
            modifier = Modifier.padding(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        ) {
            Text(
                text = context.getString(name),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }

    /**
     * Request the category to the server
     * @param type: DishType - Type of the category
     * @param errorState: MutableState<Boolean> - State of the error
     */
    private fun requestCategory(type: DishType, errorState: MutableState<Boolean>) {
        // Request to the server
        val requestBody = JSONObject()
        requestBody.put("id_shop", 1)
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val cacheKey = "cached_category_${type.typeFr}"

        // Check if the result is already cached
        val cachedCategory = getCachedCategory(cacheKey)
        if (cachedCategory != null) {
            // Use cached result
            navigateToCategoryActivity(cachedCategory)
            return
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            { response ->
                val gson = Gson()
                var listCategory = gson.fromJson(response.toString(), CategoryList::class.java).data.find { it.nameFr == type.typeFr }

                // Cache the result
                cacheCategory(this, cacheKey, listCategory)

                navigateToCategoryActivity(listCategory)
            },
            { _ ->
                errorState.value = true
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    /**
     * Get the cached category, if it exists
     * @param cacheKey: String - Key of the cache
     */
    private fun getCachedCategory(cacheKey: String): Category? {
        val sharedPreferences = getSharedPreferences("MyCache", Context.MODE_PRIVATE)
        val cachedCategoryJson = sharedPreferences.getString(cacheKey, null)
        return if (cachedCategoryJson != null) {
            Gson().fromJson(cachedCategoryJson, Category::class.java)
        } else {
            null
        }
    }

    /**
     * Navigate to the category activity
     * @param category: Category? - Category to display
     */
    private fun navigateToCategoryActivity(category: Category?) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("category", Gson().toJson(category))
        startActivity(intent)
    }

}

/**
 * Put the category in the cache
 * @param cacheKey: String - Key of the cache
 * @param category: Category? - Category to cache
 */
fun cacheCategory(context:Context, cacheKey: String, category: Category?) {
    val sharedPreferences = context.getSharedPreferences("MyCache", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    if (category != null) {
        val categoryJson = Gson().toJson(category)
        editor.putString(cacheKey, categoryJson)
    } else {
        editor.remove(cacheKey)
    }

    editor.apply()
}

@Composable
/**
 * Take the best image from the list and display it and keep it in memory
 * @param images: List<String> - List of images URL
 * @param imageSize: Int - Size of the image
 */
fun ImageHandler(images: List<String>, imageSize: Int = 100) {
    val placeholderImage = images.getOrNull(1)

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(images[0])
            .apply<ImageRequest.Builder>(fun ImageRequest.Builder.() {
                crossfade(true)
                size(imageSize)
                memoryCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
            }).build(),
        placeholder = painterResource(R.drawable.ic_launcher_background),
        error = if (placeholderImage.isNullOrBlank()) {
            painterResource(R.drawable.ic_launcher_background)
        } else {
            rememberAsyncImagePainter(images[1])
        }
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(imageSize.dp), // Specify the size here
        contentScale = ContentScale.Crop,
    )
}

/**
 * Display the image and keep it in memory
 * @param image: String - Image URL
 */
@Composable
fun TakeTheBestImage(image: String) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(image)
            .apply<ImageRequest.Builder>(fun ImageRequest.Builder.() {
                crossfade(true)
                memoryCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
            }).build(),
        placeholder = painterResource(R.drawable.ic_launcher_background),
        error = painterResource(R.drawable.ic_launcher_background)
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth(),
        contentScale = ContentScale.Crop,
    )
}


/**
 * Display the header of the application
 * @param title: String - Title of the application
 * @param cartItemCountState: MutableState<Int> - Number of items in the cart
 * @return TopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun header(title: String = "PizzaHouse", cartItemCountState : MutableState<Int> = mutableIntStateOf(0)) {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Row (
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo",
                    Modifier
                        .size(60.dp)
                        .padding(end = 10.dp))
                Text(text = title, color = Color.White)
            }
        },
        actions = {
            // Shopping cart svg
            IconButton(
                onClick = {
                    val intent = Intent(context, ShoppingCartActivity::class.java)
                    context.startActivity(intent)
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_shopping_cart_24),
                    contentDescription = "Shopping cart",
                    modifier = Modifier.size(30.dp)
                )
                Badge(
                    modifier = Modifier.padding(bottom = 20.dp, start = 15.dp),

                ){
                    Text(text = cartItemCountState.value.toString())
                }
            }

        }
    )
}



