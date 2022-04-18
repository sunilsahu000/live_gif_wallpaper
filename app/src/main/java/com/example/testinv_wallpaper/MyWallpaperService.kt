package com.example.testinv_wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import java.util.*


class MyWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return MyWallpaperEngine()
    }

    private inner class MyWallpaperEngine : Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private val circles: MutableList<MyPoint>
        private val paint = Paint()
        private var width = 0
        var height = 0
        private var visible = true
        private val maxNumber: Int
        private val touchEnabled: Boolean
        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder, format: Int,
            width: Int, height: Int
        ) {
            this.width = width
            this.height = height
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (touchEnabled) {
                val x = event.x
                val y = event.y
                val holder = surfaceHolder
                var canvas: Canvas? = null
                try {
                    canvas = holder.lockCanvas()
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK)
                        circles.clear()
                        circles.add(MyPoint((circles.size + 1).toString(), x.toInt(), y.toInt()))
                        drawCircles(canvas, circles)
                    }
                } finally {
                    if (canvas != null) holder.unlockCanvasAndPost(canvas)
                }
                super.onTouchEvent(event)
            }
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    if (circles.size >= maxNumber) {
                        circles.clear()
                    }
                    val x = (width * Math.random()).toInt()
                    val y = (height * Math.random()).toInt()
                    circles.add(
                        MyPoint(
                            (circles.size + 1).toString(),
                            x, y
                        )
                    )
                    drawCircles(canvas, circles)
                }
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
            if (visible) {
                handler.postDelayed(drawRunner, 5000)
            }
        }

        // Surface view requires that all elements are drawn completely
        private fun drawCircles(canvas: Canvas, circles: List<MyPoint>) {
            canvas.drawColor(Color.BLACK)
            for (point in circles) {
                canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 20.0f, paint)
            }
        }

        init {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(this@MyWallpaperService)
            maxNumber = Integer
                .valueOf(prefs.getString("numberOfCircles", "4"))
            touchEnabled = prefs.getBoolean("touch", false)
            circles = ArrayList<MyPoint>()
            paint.isAntiAlias = true
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = 10f
            handler.post(drawRunner)
        }
    }
}