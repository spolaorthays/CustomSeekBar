package br.com.thays.customseekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.SeekBar
import androidx.core.content.ContextCompat

class ColorSlider @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.seekBarStyle,
            defStyleRes: Int = 0) : SeekBar(context, attrs, defStyleAttr, defStyleRes) {

    private var colors: ArrayList<Int> = arrayListOf(Color.RED, Color.YELLOW, Color.BLUE)
    private var listeners: ArrayList<(Int) -> Unit> = arrayListOf()

    private val w = getPixelValueFromDP(16f)
    private val h = getPixelValueFromDP(16f)
    private val halfWidth = if (w >= 0) w/2f else 1f
    private val halfHeight = if (h >= 0) h/2f else 1f
    private val paint = Paint()
    private var transparent: Drawable? = null //Criando o desenho para quando não quiser selecionar uma cor
        set(value) {
            w2 = value?.intrinsicWidth ?: 0
            h2 = value?.intrinsicHeight ?: 0
            halfWidth2 = if (w2 >= 0) w2/2 else 1
            halfHeight2 = if (h2 >= 0) h2/2 else 1
            value?.setBounds(-halfWidth2, -halfHeight2, halfWidth2, halfHeight2)
            field = value
        }
    var w2 = 0
    private var h2 = 0
    private var halfWidth2 = 1
    private var halfHeight2 = 1

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorSlider)
        try {
            colors = typedArray.getTextArray(R.styleable.ColorSlider_colors).map {
                Color.parseColor(it.toString())
            } as ArrayList<Int>
        } finally {
            typedArray.recycle()
        }
        colors.add(0, android.R.color.transparent) //Deixando o transparente na primeira posição da lista
        max = colors.size -1
        progressBackgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        progressTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        splitTrack = false
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + getPixelValueFromDP(16f).toInt())
        thumb = context.getDrawable(R.drawable.ic_arrow_drop_down_24)
        transparent = context.getDrawable(R.drawable.ic_baseline_clear_24)

        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listeners.forEach {
                    it(colors[progress])
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawTickMarks(canvas)
    }

    fun addListener(function: (Int) -> Unit) {
        listeners.add(function)
    }

    private fun drawTickMarks(canvas: Canvas?) {
        canvas?.let {
            val count = colors.size
            val saveCount = canvas.save() //Salva o ponto na tela que começou cada um dos desenhos
            canvas.translate(paddingLeft.toFloat(), (height / 2).toFloat() + getPixelValueFromDP(16f))
            if (count > 1) {
                val spacing = (width - paddingLeft - paddingRight) / (count - 1).toFloat()
                for (i in 0 until count) {
                    if (i == 0) {
                        transparent?.draw(canvas)
                    } else {
                        paint.color = colors[i]
                        canvas.drawRect(-halfWidth, -halfHeight, halfWidth, halfHeight, paint) //Aqui que definimos o desenho do quadrado
                    }
                    canvas.translate(spacing, 0f) //Aqui traduz o desenho para a tela, sendo assim é aqui que a "caneta" começa a desenhar de fato
                }
                canvas.restoreToCount(saveCount) //Faz com que a "caneta" que estava desenhando os quadrados volte a sua posição inicial
            }
        }
    }

    //Método necessário para coverter os valores passados de acordo com o dpi do dispositivo que estamos utilizando
    private fun getPixelValueFromDP(value: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.resources.displayMetrics)
    }
}