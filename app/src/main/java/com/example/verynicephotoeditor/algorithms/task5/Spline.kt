package com.example.verynicephotoeditor.algorithms.task5

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.example.verynicephotoeditor.databinding.FragmentSplinesBinding
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Spline {

    val mainDotSize = 20
    val anchorDotSize = 10

    fun getIndexOfDot(
        mainDotsList: MutableList<MainDot>,
        startX: Float,
        startY: Float,
        mainDotSize: Int,
        searchPrevAnchor: Boolean,
        searchNextAnchor: Boolean
    ): Int {

        for (i in mainDotsList.indices) {

            val dot = mainDotsList[i]

            if (!searchPrevAnchor && !searchNextAnchor) {

                if (dist(
                        dot.getX().toDouble(),
                        dot.getY().toDouble(),
                        startX.toDouble(),
                        startY.toDouble()
                    ) < mainDotSize
                ) {
                    return i
                }

            }

            else {

                val prevDot = dot.getPrevDot()
                val nextDot = dot.getNextDot()

                if (prevDot != null) {
                    if (dist(
                            prevDot.getX().toDouble(),
                            prevDot.getY().toDouble(),
                            startX.toDouble(),
                            startY.toDouble()
                        ) < mainDotSize && searchPrevAnchor) {
                        return i
                    }
                }

                if (nextDot != null) {
                    if (dist(
                            nextDot.getX().toDouble(),
                            nextDot.getY().toDouble(),
                            startX.toDouble(),
                            startY.toDouble()
                        ) < mainDotSize && searchNextAnchor) {
                        return i
                    }
                }
            }
        }

        return -1
    }

     fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun drawDot(mutableBitmap: Bitmap, dotSize: Int, x: Int, y: Int, color: Int) {

        for (xx in x - dotSize + 1..<x + dotSize) {
            for (yy in y - dotSize + 1..<y + dotSize) {

                val currX = Math.max(Math.min(xx, mutableBitmap.width - 1), 0)
                val currY = Math.max(Math.min(yy, mutableBitmap.height - 1), 0)

                if (dist(
                        xx.toDouble(),
                        yy.toDouble(),
                        x.toDouble(),
                        y.toDouble()
                    ) < dotSize) {

                    mutableBitmap.setPixel(currX, currY, color)
                }
            }
        }
    }

    fun dist(x1: Double, y1: Double, x2: Double, y2: Double) : Double {
        return sqrt(((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0)))
    }

    fun update(
        dotsList: MutableList<MainDot>,
        mutableBitmap: Bitmap,
        layoutBinding: FragmentSplinesBinding,
        isRectMode: Boolean,
        isFXAAon: Boolean
    ) {

        for (dot in dotsList) {
            val prevDot = dot.getPrevDot()
            val nextDot = dot.getNextDot()

            if (prevDot != null) {
                drawLine(mutableBitmap, prevDot, dot, 1, Color.LTGRAY)
            }

            if (nextDot != null) {
                drawLine(mutableBitmap, nextDot, dot, 1, Color.LTGRAY)
            }

            drawDot(mutableBitmap, mainDotSize, dot.getX().toInt(), dot.getY().toInt(), Color.BLACK)

            if (prevDot != null) {
                drawDot(mutableBitmap, anchorDotSize, prevDot.getX().toInt(), prevDot.getY().toInt(), Color.GRAY)
            }

            if (nextDot != null) {
                drawDot(mutableBitmap, anchorDotSize, nextDot.getX().toInt(), nextDot.getY().toInt(), Color.GRAY)
            }
        }

        if (isRectMode)
            drawRectangle(dotsList, mutableBitmap, isFXAAon)

        else
            drawSpline(dotsList, mutableBitmap, isFXAAon)

        layoutBinding.splineCanvas.setImageBitmap(mutableBitmap)
    }

    private fun getDotAt(d1: Dot?, d2: Dot?, pos: Float) : Dot{
        if (d1 != null) {
            if (d2 != null) {
                return Dot (
                    d1.getX() + (d2.getX() - d1.getX()) * pos,
                    d1.getY() + (d2.getY() - d1.getY()) * pos
                )
            }
        }

        return Dot(0.0f, 0.0f)
    }

    private fun drawSpline(dotsList: MutableList<MainDot>, mutableBitmap: Bitmap, isFXAAon: Boolean) {

        val width = mutableBitmap.width
        val height = mutableBitmap.height

        if (dotsList.size > 1) {

            for (i in dotsList.indices) {

                if (i == dotsList.size - 1) break

                val dot0 = dotsList[i]
                val dot1 = dot0.getNextDot()

                val dot3 = dotsList[i + 1]
                val dot2 = dot3.getPrevDot()


                var prevDot : Dot = dotsList[i]

                val iterations = 100

                for (time in 0..iterations) {

                    val t = time.toFloat() / iterations

                    val sqrDot0 = getDotAt(dot0, dot1, t)
                    val sqrDot1 = getDotAt(dot1, dot2, t)
                    val sqrDot2 = getDotAt(dot2, dot3, t)

                    val cubDot0 = getDotAt(sqrDot0, sqrDot1, t)
                    val cubDot1 = getDotAt(sqrDot1, sqrDot2, t)

                    val dot = getDotAt(cubDot0, cubDot1, t)

                    drawLine(mutableBitmap, prevDot, dot, 2, Color.BLACK)
                    prevDot = dot
                }

                if (isFXAAon) {

                    prevDot = dotsList[i]

                    val destPixels = IntArray(width * height)
                    mutableBitmap.getPixels(destPixels, 0, width, 0, 0, width, height)

                    for (time in 0..iterations) {

                        val t = time.toFloat() / iterations

                        val sqrDot0 = getDotAt(dot0, dot1, t)
                        val sqrDot1 = getDotAt(dot1, dot2, t)
                        val sqrDot2 = getDotAt(dot2, dot3, t)

                        val cubDot0 = getDotAt(sqrDot0, sqrDot1, t)
                        val cubDot1 = getDotAt(sqrDot1, sqrDot2, t)

                        val dot = getDotAt(cubDot0, cubDot1, t)

                        lineFXAA(mutableBitmap, destPixels, prevDot, dot, 2)
                        mutableBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)
                        prevDot = dot
                    }
                }
            }
        }
    }

    private fun drawRectangle(dotsList: MutableList<MainDot>, mutableBitmap: Bitmap, isFXAAon: Boolean) {

        val width = mutableBitmap.width
        val height = mutableBitmap.height

        if (dotsList.size > 1) {

            for (i in dotsList.indices) {

                var dot0 : Dot
                var dot1 : Dot
                var dot2 : Dot
                var dot3 : Dot

                if (i == dotsList.size - 1) {
                    dot0 = dotsList[i]
                    dot1 = dot0.getNextDot()!!

                    dot3 = dotsList[0]
                    dot2 = dot3.getPrevDot()!!
                }

                else {
                    dot0 = dotsList[i]
                    dot1 = dot0.getNextDot()!!

                    dot3 = dotsList[i + 1]
                    dot2 = dot3.getPrevDot()!!
                }

                var prevDot: Dot = dotsList[i]

                val iterations = 100

                for (time in 0..iterations) {

                    val t = time.toFloat() / iterations

                    val sqrDot0 = getDotAt(dot0, dot1, t)
                    val sqrDot1 = getDotAt(dot1, dot2, t)
                    val sqrDot2 = getDotAt(dot2, dot3, t)

                    val cubDot0 = getDotAt(sqrDot0, sqrDot1, t)
                    val cubDot1 = getDotAt(sqrDot1, sqrDot2, t)

                    val dot = getDotAt(cubDot0, cubDot1, t)

                    drawLine(mutableBitmap, prevDot, dot, 2, Color.BLACK)
                    prevDot = dot
                }

                if (isFXAAon) {

                    prevDot = dotsList[i]

                    val destPixels = IntArray(width * height)
                    mutableBitmap.getPixels(destPixels, 0, width, 0, 0, width, height)

                    for (time in 0..iterations) {

                        val t = time.toFloat() / iterations

                        val sqrDot0 = getDotAt(dot0, dot1, t)
                        val sqrDot1 = getDotAt(dot1, dot2, t)
                        val sqrDot2 = getDotAt(dot2, dot3, t)

                        val cubDot0 = getDotAt(sqrDot0, sqrDot1, t)
                        val cubDot1 = getDotAt(sqrDot1, sqrDot2, t)

                        val dot = getDotAt(cubDot0, cubDot1, t)

                        lineFXAA(mutableBitmap, destPixels, prevDot, dot, 2)
                        mutableBitmap.setPixels(destPixels, 0, width, 0, 0, width, height)
                        prevDot = dot
                    }
                }
            }
        }
    }

    private fun drawLine(mutableBitmap: Bitmap, dot1: Dot, dot2: Dot, strokeSize: Int, color: Int) {

        val x1 = dot1.getX().toInt()
        val y1 = dot1.getY().toInt()
        val x2 = dot2.getX().toInt()
        val y2 = dot2.getY().toInt()

        val dx = Math.abs(x2 - x1)
        val dy = Math.abs(y2 - y1)

        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1

        var err = dx - dy
        var e2: Int

        var x = x1
        var y = y1

        while (true) {
            for (i in -strokeSize..strokeSize) {
                for (j in -strokeSize..strokeSize) {
                    if (x + i in 0 until mutableBitmap.width && y + j in 0 until mutableBitmap.height) {
                        mutableBitmap.setPixel(x + i, y + j, color)
                    }
                }
            }

            if (x == x2 && y == y2) break

            e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x += sx
            }
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }
    }

    private fun lineFXAA(mutableBitmap: Bitmap, destPixels: IntArray, dot1: Dot, dot2: Dot, strokeSize: Int) {

        val fxaaStroke = strokeSize + 1

        val width = mutableBitmap.width
        val height = mutableBitmap.height

        val x1 = dot1.getX().toInt()
        val y1 = dot1.getY().toInt()
        val x2 = dot2.getX().toInt()
        val y2 = dot2.getY().toInt()

        val dx = Math.abs(x2 - x1)
        val dy = Math.abs(y2 - y1)

        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1

        var err = dx - dy
        var e2: Int

        var x = x1
        var y = y1

        while (true) {

            fxaaCoroutine(mutableBitmap, destPixels, x, y, fxaaStroke)

            if (x == x2 && y == y2) break

            e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x += sx
            }
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }
    }

    private fun fxaaCoroutine(
        mutableBitmap: Bitmap,
        destPixels: IntArray,
        x: Int,
        y: Int,
        fxaaStroke: Int
    ): UInt = runBlocking {
        repeat( (fxaaStroke * 2 + 1) * (fxaaStroke * 2 + 1)) {

            if (x + (it % (fxaaStroke * 2 + 1) - fxaaStroke) in 0 until mutableBitmap.width
                && y + (it / (fxaaStroke * 2 + 1) - fxaaStroke) in 0 until mutableBitmap.height) {

                FXAA(
                    mutableBitmap,
                    destPixels,
                    x + (it % (fxaaStroke * 2 + 1) - fxaaStroke),
                    y + (it / (fxaaStroke * 2 + 1) - fxaaStroke)
                )

            }
        }
        0u
    }

    private fun FXAA(mutableBitmap: Bitmap, destPixels: IntArray, x: Int, y: Int) {

        val width = mutableBitmap.width
        val height = mutableBitmap.height

        val spanMax = 8.0
        val reduceMin = 1.0 / 128.0
        val reduceMul = 1.0 / 8.0

        val grayTL = getGrayscaleValue(mutableBitmap.getPixel(
            Math.min(Math.max(x - 1, 0), width - 1),
            Math.min(Math.max(y - 1, 0), height - 1)
        ))

        val grayTR = getGrayscaleValue(mutableBitmap.getPixel(
            Math.min(Math.max(x + 1, 0), width - 1),
            Math.min(Math.max(y - 1, 0), height - 1)
        ))

        val grayBL = getGrayscaleValue(mutableBitmap.getPixel(
            Math.min(Math.max(x - 1, 0), width - 1),
            Math.min(Math.max(y + 1, 0), height - 1)
        ))

        val grayBR = getGrayscaleValue(mutableBitmap.getPixel(
            Math.min(Math.max(x + 1, 0), width - 1),
            Math.min(Math.max(y + 1, 0), height - 1)
        ))

        val grayM = getGrayscaleValue(mutableBitmap.getPixel(
            Math.min(Math.max(x, 0), width - 1),
            Math.min(Math.max(y, 0), height - 1)
        ))

        val initDirX = -((grayTL + grayTR) - (grayBL + grayBR))
        val initDirY = ((grayTL + grayBL) - (grayTR + grayBR))

        val dirReduce = Math.max((grayTL + grayTR + grayBL + grayBR) * reduceMul * 0.25, reduceMin)
        val inverseDirAdjust = 1.0 / (Math.min(abs(initDirX), abs(initDirY)) + dirReduce)

        val dirX = Math.min(Math.max(initDirX * inverseDirAdjust, -spanMax), spanMax)
        val dirY = Math.min(Math.max(initDirY * inverseDirAdjust, -spanMax), spanMax)

        val pixel11 = mutableBitmap.getPixel(
            Math.min(Math.max(x + (dirX * (1.0 / 3.0 - 0.5)).toInt(), 0), width - 1),
            Math.min(Math.max(y + (dirY * (1.0 / 3.0 - 0.5)).toInt(), 0), height - 1)
        )

        val pixel12 = mutableBitmap.getPixel(
            Math.min(Math.max(x + (dirX * (2.0 / 3.0 - 0.5)).toInt(), 0), width - 1),
            Math.min(Math.max(y + (dirY * (2.0 / 3.0 - 0.5)).toInt(), 0), height - 1)
        )

        val pixel21 = mutableBitmap.getPixel(
            Math.min(Math.max(x + (dirX * -0.5).toInt(), 0), width - 1),
            Math.min(Math.max(y + (dirY * -0.5).toInt(), 0), height - 1)
        )

        val pixel22 = mutableBitmap.getPixel(
            Math.min(Math.max(x + (dirX * 0.5).toInt(), 0), width - 1),
            Math.min(Math.max(y + (dirY * 0.5).toInt(), 0), height - 1)
        )

        val result1 = Color.argb(
            255,
            (0.5 * (Color.red(pixel11) + Color.red(pixel12))).toInt(),
            (0.5 * (Color.green(pixel11) + Color.green(pixel12))).toInt(),
            (0.5 * (Color.blue(pixel11) + Color.blue(pixel12))).toInt()
        )

        val result2 = Color.argb(
            255,
            (Color.red(result1) * 0.5 + 0.25 * (Color.red(pixel21) + Color.red(pixel22))).toInt(),
            (Color.green(result1) * 0.5 + 0.25 * (Color.green(pixel21) + Color.green(pixel22))).toInt(),
            (Color.blue(result1) * 0.5 + 0.25 * (Color.blue(pixel21) + Color.blue(pixel22))).toInt()
        )

        val grayMin = Math.min(Math.min(Math.min(grayM, grayBR), Math.min(grayBL, grayTR)), grayTL)
        val grayMax = Math.max(Math.max(Math.max(grayM, grayBR), Math.max(grayBL, grayTR)), grayTL)

        val grayRes2 = getGrayscaleValue(result2)

        if (grayRes2 < grayMin || grayRes2 > grayMax) {
            destPixels[y * width + x] = result1
        }

        else {
            destPixels[y * width + x] = result2
        }
    }

    private fun getGrayscaleValue(pixel: Int): Int {
        return (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel)).toInt()
    }
}

class MainDot(
    x: Float,
    y: Float,
    private var prevDot: AnchorDot?,
    private var nextDot: AnchorDot?
) : Dot(x, y){

    fun setAnchorDots(prevDot: AnchorDot, nextDot: AnchorDot) {
        this.prevDot = prevDot
        this.nextDot = nextDot
    }

    fun getPrevDot() : AnchorDot? { return prevDot }
    fun getNextDot() : AnchorDot? { return nextDot }
}

class AnchorDot(
    x: Float,
    y: Float,
    private var mainDot: MainDot?
) : Dot(x, y){

}

open class Dot(
    private var x: Float,
    private var y: Float
) {
    fun getX() : Float {
        return x
    }

    fun changeX(newX: Float) {
        x = newX
    }

    fun getY() : Float {
        return y
    }

    fun changeY(newY: Float) {
        y = newY
    }
}