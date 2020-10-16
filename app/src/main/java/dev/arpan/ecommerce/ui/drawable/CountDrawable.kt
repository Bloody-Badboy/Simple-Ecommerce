/*
 * Copyright 2020 Arpan Sarkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arpan.ecommerce.ui.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import dev.arpan.ecommerce.R
import kotlin.math.max

class CountDrawable(context: Context) : Drawable() {

    private val badgePaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context.applicationContext, R.color.colorSecondary)
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val textPaint: Paint = Paint().apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT
        textSize = context.resources.getDimension(R.dimen.badge_count_textsize)
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val textBounds = Rect()
    private var shouldDraw = false
    var count = 0
        set(value) {
            field = value
            shouldDraw = count > 0
            invalidateSelf()
        }

    override fun draw(canvas: Canvas) {
        if (!shouldDraw) return

        val radius = max(bounds.width(), bounds.height()) / 2f / 2f
        val centerX = bounds.width() - radius - 1 + 5
        val centerY = radius - 5
        val textLength = count.toString().length

        if (textLength <= 2) {
            canvas.drawCircle(centerX, centerY, (radius + 5.5f), badgePaint)
        } else {
            canvas.drawCircle(centerX, centerY, (radius + 6.5f), badgePaint)
        }

        textPaint.getTextBounds(count.toString(), 0, textLength, textBounds)
        val textHeight = textBounds.bottom - textBounds.top
        val textY = centerY + textHeight / 2f
        if (textLength > 2)
            canvas.drawText("99+", centerX, textY, textPaint)
        else
            canvas.drawText(count.toString(), centerX, textY, textPaint)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}
