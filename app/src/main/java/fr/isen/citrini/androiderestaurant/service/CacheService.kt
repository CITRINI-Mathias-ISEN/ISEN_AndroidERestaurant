package fr.isen.citrini.androiderestaurant.service

import Category
import android.content.Context
import com.google.gson.Gson

class CacheService {
    companion object {
        /**
         * Put the category in the cache
         * @param cacheKey: String - Key of the cache
         * @param category: Category? - Category to cache
         */
         fun cacheCategory(context: Context, cacheKey: String, category: Category?) {
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

        /**
         * Get the cached category, if it exists
         * @param cacheKey: String - Key of the cache
         */
        fun getCachedCategory(context: Context, cacheKey: String): Category? {
            val sharedPreferences = context.getSharedPreferences("MyCache", Context.MODE_PRIVATE)
            val cachedCategoryJson = sharedPreferences.getString(cacheKey, null)
            return if (cachedCategoryJson != null) {
                Gson().fromJson(cachedCategoryJson, Category::class.java)
            } else {
                null
            }
        }
    }
}