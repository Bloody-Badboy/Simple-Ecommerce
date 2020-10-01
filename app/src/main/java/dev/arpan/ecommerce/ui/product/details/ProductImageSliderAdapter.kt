package dev.arpan.ecommerce.ui.product.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import dev.arpan.ecommerce.bindImageUrl
import dev.arpan.ecommerce.databinding.ItemProductImageSliderBinding

class ProductImageSliderAdapter : PagerAdapter() {
    var imageUrls: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount() = imageUrls.size

    override fun isViewFromObject(view: View, obj: Any) = (obj as? View) == view

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return ItemProductImageSliderBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false
        ).apply {
            ivPhoto.bindImageUrl(imageUrls[position])

            container.addView(root, 0)
        }.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (obj as? View)?.let {
            container.removeView(it)
        }
    }
}
