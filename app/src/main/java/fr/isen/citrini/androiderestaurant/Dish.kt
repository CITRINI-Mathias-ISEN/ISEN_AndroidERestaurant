import com.google.gson.annotations.SerializedName

data class CategoryList(
    @SerializedName("data") val data: List<Category>
)

data class Category(
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("items") val items: List<Dish>
)

data class Dish(
    @SerializedName("id") val id: String,
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("id_category") val categoryId: String,
    @SerializedName("categ_name_fr") val categoryNameFr: String,
    @SerializedName("categ_name_en") val categoryNameEn: String,
    @SerializedName("images") val images: List<String>,
    @SerializedName("ingredients") val ingredients: List<Ingredient>,
    @SerializedName("prices") val prices: List<Price>,
)

data class Ingredient(
    @SerializedName("id") val id: String,
    @SerializedName("id_shop") val shopId: String,
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("update_date") val updateDate: String,
    @SerializedName("id_pizza") val pizzaId: String
)

data class Price(
    @SerializedName("id") val id: String,
    @SerializedName("id_pizza") val pizzaId: String,
    @SerializedName("id_size") val sizeId: String,
    @SerializedName("price") val price: Float,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("update_date") val updateDate: String,
    @SerializedName("size") val size: String
)
