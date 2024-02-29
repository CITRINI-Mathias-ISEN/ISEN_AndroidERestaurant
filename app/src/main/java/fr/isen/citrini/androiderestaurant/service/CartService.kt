package fr.isen.citrini.androiderestaurant.service

import Dish
import android.content.Context
import android.util.Log
import com.google.gson.Gson

class Cart {
    companion object {
        private var cart: CartJSON = CartJSON(listOf())
        private lateinit var context: Context

        fun setContext(context: Context) {
            Companion.context = context
            loadCart()
        }

        private fun loadCart() {
            try {
                val json = context.openFileInput("cart.json")
                val carString = json.readBytes().toString(Charsets.UTF_8)
                cart = Gson().fromJson(carString, CartJSON::class.java)
                json.close()
            } catch (e: Exception) {
                Log.d("Cart", "Cart: $e")
                e.printStackTrace()
            }
        }

        fun getCart(): List<CartItem> {
            return cart.items
        }

        fun addDish(dish: Dish, quantity: Int) {
            val index = cart.items.indexOfFirst { it.dish.id == dish.id }
            if (index != -1) {
                cart.items[index].quantity += quantity
            } else {
                cart.items = cart.items + CartItem(dish, quantity)
            }
            saveCart(context)
        }

        fun removeItem(dish: Dish) {
            val index = cart.items.indexOfFirst { it.dish.id == dish.id }
            if (index != -1) {
                cart.items[index].quantity -= 1
                if (cart.items[index].quantity == 0) {
                    deleteItem(dish)
                }
            }
            saveCart(context)
        }

        fun deleteItem(dish: Dish) {
            val index = cart.items.indexOfFirst { it.dish.id == dish.id }
            if (index != -1) {
                cart.items = cart.items - cart.items[index]
            }
            saveCart(context)
        }

        private fun saveCart(context: Context) {
            try {
                val cartString = Gson().toJson(cart)
                context.openFileOutput("cart.json", Context.MODE_PRIVATE).use {
                    it.write(cartString.toByteArray())
                    it.close()
                }
                Log.d("Cart", "saveCart: $cartString")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getNumberOfItems(): Int {
            return cart.items.sumOf { it.quantity }
        }

        fun getTotalPrice(): Float {
            return cart.items.sumOf { it.dish.prices[0].price.toDouble() * it.quantity }.toFloat()
        }
    }
}

data class CartJSON(
    var items: List<CartItem>
)

data class CartItem(
    val dish: Dish,
    var quantity: Int
)