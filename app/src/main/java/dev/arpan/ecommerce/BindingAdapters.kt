package dev.arpan.ecommerce

import android.graphics.Paint
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pixelcan.inkpageindicator.InkPageIndicator
import dev.arpan.ecommerce.ui.product.details.ProductImageSliderAdapter
import dev.arpan.ecommerce.ui.product.details.ProductSizesAdapter
import dev.arpan.ecommerce.ui.product.details.ZoomOutPageTransformer
import dev.arpan.ecommerce.utils.GlideApp

@BindingAdapter("imageUrl", requireAll = false)
fun ImageView.bindImageUrl(imageUrl: String?) {
    loadImageUrl(imageUrl)
}

@Suppress("SimpleRedundantLet")
fun ImageView.loadImageUrl(imageUrl: String?, placeholderDrawable: Drawable? = null) {
    val placeholder = placeholderDrawable?.let {
        it
    } ?: AppCompatResources.getDrawable(context, R.drawable.product_image_placeholder)
    when {
        imageUrl != null -> {
            val requestOptions: RequestOptions = RequestOptions()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

            GlideApp.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(this)
        }
        else -> {
            GlideApp.with(this)
                .load(placeholder)
                .into(this)
        }
    }
}

@BindingAdapter("productSizes")
fun RecyclerView.bindProductSizes(entries: List<String>?) {
    var timeLineAdapter = adapter as? ProductSizesAdapter?
    if (timeLineAdapter == null) {
        timeLineAdapter = ProductSizesAdapter()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = timeLineAdapter
    }
    if (entries != null) {
        timeLineAdapter.sizes = entries
    }
}

@BindingAdapter("strikeThrough")
fun TextView.strikeThrough(show: Boolean) {
    paintFlags = if (show) {
        paintFlags or STRIKE_THRU_TEXT_FLAG
    } else {
        paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("productRating")
fun ImageView.bindProductRating(
    @FloatRange(
        from = 0.toDouble(),
        to = 5.toDouble()
    ) rating: Double
) {
    val resId = when {
        rating >= 4 -> {
            R.drawable.ic_star_good
        }
        rating >= 3 -> {
            R.drawable.ic_star_average
        }
        else -> {
            R.drawable.ic_star_bad
        }
    }
    setImageResource(resId)
}

@BindingAdapter(value = ["productImages", "indicator"], requireAll = false)
fun ViewPager.bindImages(urls: List<String>?, indicator: InkPageIndicator?) {
    var pagerAdapter = adapter as? ProductImageSliderAdapter
    if (pagerAdapter == null) {
        pagerAdapter = ProductImageSliderAdapter()
        adapter = pagerAdapter
        setPageTransformer(true, ZoomOutPageTransformer())
        indicator?.setViewPager(this)
    }
    urls?.let {
        pagerAdapter.imageUrls = urls
    }
}