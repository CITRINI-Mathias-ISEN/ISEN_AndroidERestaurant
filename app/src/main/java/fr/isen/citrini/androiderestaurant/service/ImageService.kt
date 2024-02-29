package fr.isen.citrini.androiderestaurant.service

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import fr.isen.citrini.androiderestaurant.R

class ImageService {
    companion object {
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
    }
}