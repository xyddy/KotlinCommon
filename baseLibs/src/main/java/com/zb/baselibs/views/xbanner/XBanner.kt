package com.zb.baselibs.views.xbanner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.zb.baselibs.R
import com.zb.baselibs.bean.Ads
import com.zb.baselibs.databinding.XbannerBinding
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.ref.WeakReference
import java.util.*

class XBanner : RelativeLayout {
    private var delayTime = 3000
    private var titleTextSize = 16
    private var sizeNumIndicator = 12
    private var indicatorMargin = 10
    private var titleHeight = 100
    private var titleMarginStart = 20
    var pageTransformerDelayIdle = 600
    private var mContext: Context
    private var mTitleHeight = 0
    private var mTitleWidth = 0
    private var mIndicatorSelected = 0
    private var mIndicatorUnselected = 0
    private var mDelayTime = 0
    private var mIsAutoPlay = false
    private var mIsPlaying = false
    private var mIsTitlebgAlpha = false
    private var mIndicatorSet = false
    private var mSizeTitleText = 0
    private var mColorTitle = 0
    private var mGravity = 0
    private var mImageCount = 0
    private var mBannerType = 0
    private var mScaleType: ImageView.ScaleType? = null
    private var mAdapter: XBPagerAdapter? = null
    private var mBannerTitle: TextView? = null
    private var mNumIndicator: TextView? = null
    private var mIndicators: MutableList<ImageView>? = null
    private var mBannerImages: MutableList<View>? = null
    private var mTitles: MutableList<String>? = null
    private var mUrls: MutableList<String>? = null
    private var adViewList: MutableList<View>? = null
    private var mBannerPageListner: XBPagerAdapter.BannerPageListener? = null
    private var mImageLoader: ImageLoader? = null
    private var xbannerScroller: XBannerScroller? = null
    private var mRunnable: ViewPagerRunnable? = null
    private var adsList: List<Ads>? = null

    constructor(context: Context) : super(context) {
        mContext = context
        initValues()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initValues()
        getTypeArrayValue(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        initValues()
        getTypeArrayValue(context, attrs)
    }

    /**
     * Set the gravity of the indicators,START by default
     * Gravity will be END when with a title inside
     * to change the gravity of the indicator
     * Use [.setIndicatorGravity]
     */
    @IntDef(INDICATOR_START, INDICATOR_CENTER, INDICATOR_END)
    @Retention(RetentionPolicy.SOURCE)
    annotation class INDICATOR_GRAVITY()

    fun setIndicatorGravity(@INDICATOR_GRAVITY gravity: Int): XBanner {
        mGravity = gravity
        return this
    }

    /**
     * Set the indicator type here
     */
    @IntDef(
        CIRCLE_INDICATOR,
        CIRCLE_INDICATOR_TITLE,
        CUBE_INDICATOR,
        NUM_INDICATOR,
        NUM_INDICATOR_TITLE,
        INDICATOR_NON
    )
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class BANNER_TYPE()

    fun setBannerTypes(@BANNER_TYPE bannerType: Int): XBanner {
        mBannerType = bannerType
        return this
    }

    private fun getTypeArrayValue(context: Context, attr: AttributeSet?) {
        if (attr == null) {
            return
        }
        val typedArray =
            context.obtainStyledAttributes(attr, R.styleable.XBanner)
        mTitleHeight = typedArray.getDimensionPixelSize(
            R.styleable.XBanner_title_height,
            titleHeight
        )
        mDelayTime =
            typedArray.getInteger(R.styleable.XBanner_delay_time, delayTime)
        mIsAutoPlay =
            typedArray.getBoolean(R.styleable.XBanner_is_auto_play, false)
        mSizeTitleText = typedArray.getInteger(
            R.styleable.XBanner_size_title_text,
            titleTextSize
        )
        mGravity = typedArray.getInteger(
            R.styleable.XBanner_indicator_gravity,
            INDICATOR_CENTER
        )
        mColorTitle =
            typedArray.getColor(R.styleable.XBanner_color_title, Color.WHITE)
        typedArray.recycle()
    }

    private fun initValues() {
        val dm = mContext.resources.displayMetrics
        mTitleWidth = dm.widthPixels * 3 / 4
        mImageCount = 0
        mDelayTime = 4000
        mBannerType = CIRCLE_INDICATOR
        mColorTitle = Color.WHITE
        mGravity = INDICATOR_CENTER
        mScaleType = ImageView.ScaleType.FIT_XY
        mIndicators = ArrayList()
        mBannerImages = ArrayList()
        mTitles = ArrayList()
        mUrls = ArrayList()
        adViewList = ArrayList()
        adsList = ArrayList<Ads>()
        mIsPlaying = false
        mIsTitlebgAlpha = false
        mIndicatorSet = false
    }

    private fun initView() {
        bindView()
        //Need to set banner type to title types to avoid some logic errors
        val exceptionTitle =
            "XBanner: " + "Banner type must be set to CIRCLE_INDICATOR_TITLE or NUM_INDICATOR_TITLE to set titles" + ",the default banner type is set to CIRCLE_INDICATOR."
        initScroller()
        if (mBannerType == CIRCLE_INDICATOR_TITLE || mBannerType == NUM_INDICATOR_TITLE) {
            initViewforTitleType()
        } else if (mBannerType == INDICATOR_NON) {
        } else if (mTitles!!.size > 0) {
            throw RuntimeException(exceptionTitle)
        }
        initIndicatorContainer()
        initViewPagerAdapter()
    }

    private fun initViewforTitleType() {
        setIndicatorGravity(INDICATOR_END)
        initBannerTitle()
        addView(mBannerTitle)
    }

    private fun initScroller() {
        try {
            val xScroller = ViewPager::class.java.getDeclaredField("mScroller")
            xScroller.isAccessible = true
            xbannerScroller = XBannerScroller(mContext, DecelerateInterpolator())
            xbannerScroller!!.duration = 600
            xScroller[binding.viewpager] = xbannerScroller
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeScroller() {
        try {
            val xScroller = ViewPager::class.java.getDeclaredField("mScroller")
            xScroller.isAccessible = true
            xScroller[binding.viewpager] = null
            xbannerScroller = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Attention,this method must be called before the images set
     * or it does not work
     * the default scale type is FIT_XY
     */
    fun setImageScaleType(scaleType: ImageView.ScaleType?): XBanner {
        mScaleType = scaleType
        return this
    }

    /**
     * Create indicators,must be called after banner images set
     *
     * @return ImageView list of indicators
     */
    private fun createIndicators(): List<ImageView> {
        val images: MutableList<ImageView> = ArrayList()
        for (i in 0 until mImageCount) {
            images.add(newIndicator(i))
        }
        return images
    }

    /**
     * new an indicator with the given resId
     *
     * @param index the index of the Image
     */
    private fun newIndicator(index: Int): ImageView {
        val indicator = ImageView(mContext)
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(14, 14)
        if (mIndicatorSet) {
            indicator.setImageResource(if (index == 0) mIndicatorSelected else mIndicatorUnselected)
        } else if (mBannerType == CIRCLE_INDICATOR_TITLE || mBannerType == CIRCLE_INDICATOR) {
            indicator.setImageResource(if (index == 0) R.drawable.indicator_selected else R.drawable.indicator_unselected)
        } else if (mBannerType == CUBE_INDICATOR) {
            indicator.setImageResource(if (index == 0) R.drawable.indicator_cube_selected else R.drawable.indicator_cube_unselected)
        }
        params.leftMargin = indicatorMargin
        params.rightMargin = indicatorMargin
        indicator.layoutParams = params
        return indicator
    }

    @SuppressLint("SetTextI18n")
    private fun createNumIndicator(): TextView {
        val indicator = TextView(mContext)
        indicator.textSize = sizeNumIndicator.toFloat()
        indicator.text = "1/$mImageCount"
        indicator.setTextColor(Color.WHITE)
        return indicator
    }

    fun setImageRes(images: IntArray): XBanner {
        mImageCount = images.size
        if (mImageCount > 1) {
            mBannerImages!!.add(newImageFromRes(images[mImageCount - 1]))
            for (i in 0 until mImageCount) {
                mBannerImages!!.add(newImageFromRes(images[i]))
            }
        }
        mBannerImages!!.add(newImageFromRes(images[0]))
        return this
    }

    private fun newImageFromRes(res: Int): ImageView {
        val image = ImageView(mContext)
        image.setImageResource(res)
        image.scaleType = mScaleType
        return image
    }

    fun setAds(adsList: List<Ads>): XBanner {
        this.adsList = adsList
        for (ads: Ads in adsList) {
            mUrls!!.add(ads.smallImage)
            ads.view?.let { adViewList!!.add(it) }
            mTitles!!.add("")
        }
        if (mImageCount == 0) {
            mImageCount = adsList.size
        }
        return this
    }

    fun setDelay(delay: Int): XBanner {
        //default delay time is 3000ms
        if (mDelayTime < 0) {
            mDelayTime = titleTextSize
        } else {
            mDelayTime = delay
        }
        return this
    }

    /**
     * Set the res of indicator manually
     * the width and height can be set
     *
     * @param selected   the res of indicator when selected
     * @param unselected the res of indicator when unselected
     */
    fun setUpIndicators(selected: Int, unselected: Int): XBanner {
        mIndicatorSelected = selected
        mIndicatorUnselected = unselected
        mIndicatorSet = true
        return this
    }

    fun isAutoPlay(isAutoPlay: Boolean): XBanner {
        mIsAutoPlay = isAutoPlay
        return this
    }

    private fun showIndicators() {
        if (mBannerType == NUM_INDICATOR || mBannerType == NUM_INDICATOR_TITLE) {
            mNumIndicator = createNumIndicator()
            if (mBannerType == NUM_INDICATOR) {
                applyIndicatorGravity()
            } else {
                binding.indicatorContainer.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
            binding.indicatorContainer.addView(mNumIndicator)
        } else if (mBannerType == INDICATOR_NON) {
            binding.indicatorContainer.visibility = GONE
        } else {
            mIndicators!!.addAll(createIndicators())
            if (mIndicators!!.size == 1) {
                binding.indicatorContainer.visibility = GONE
            } else {
                binding.indicatorContainer.visibility = VISIBLE
                for (i in mIndicators!!.indices) {
                    binding.indicatorContainer.addView(mIndicators!![i])
                }
            }
        }
    }

    private fun applyIndicatorGravity() {
        if (mGravity == INDICATOR_START) {
            binding.indicatorContainer.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        } else if (mGravity == INDICATOR_CENTER) {
            binding.indicatorContainer.gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL
        } else if (mGravity == INDICATOR_END) {
            binding.indicatorContainer.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        }
    }

    private fun initBannerTitle() {
        mBannerTitle = TextView(mContext)
        mBannerTitle!!.setTextColor(mColorTitle)
        mBannerTitle!!.text = mTitles!!.get(0)
        mBannerTitle!!.gravity = Gravity.CENTER_VERTICAL
        mBannerTitle!!.setSingleLine()
        mBannerTitle!!.textSize = mSizeTitleText.toFloat()
        val params = LayoutParams(mTitleWidth, mTitleHeight)
        params.addRule(ALIGN_PARENT_BOTTOM)
        params.leftMargin = titleMarginStart
        mBannerTitle!!.layoutParams = params
    }

    private fun initIndicatorContainer() {
        val params = binding.indicatorContainer.layoutParams as LayoutParams
        params.height = 40
    }

    private lateinit var binding: XbannerBinding
    private lateinit var gradientDrawable: GradientDrawable
    private var colors = intArrayOf(0x00000000, 0x00000000)

    @SuppressLint("WrongConstant")
    private fun bindView() {
        binding =
            DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.xbanner, this, true)
        gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        val radii = FloatArray(8)
        radii[1] = 20f
        radii[0] = radii[1]
        radii[3] = 20f
        radii[2] = radii[3]
        radii[5] = 0f
        radii[4] = radii[5]
        radii[7] = 0f
        radii[6] = radii[7]
        gradientDrawable.cornerRadii = radii
        gradientDrawable.gradientType = GradientDrawable.RECTANGLE
        setBgRes(type - 1)
        if (mIsTitlebgAlpha) {
            binding.indicatorContainer.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private var showBg = false
    private var type = 0
    fun setBannerBg(position: Int) {
        if (showBg && type == 0) {
            setBgRes(position - 1)
        }
    }

    fun setShowBg(showBg: Boolean): XBanner {
        this.showBg = showBg
        return this
    }

    fun setType(type: Int): XBanner {
        this.type = type
        return this
    }

    private fun setBgRes(position: Int) {
        when (position) {
            -1 -> {
                colors = intArrayOf(Color.argb(0, 0, 0, 0), Color.argb(0, 0, 0, 0))
            }
            0 -> {
                colors = intArrayOf(Color.argb(255, 255, 221, 176), Color.argb(255, 255, 255, 255))
            }
            1 -> {
                colors = intArrayOf(Color.argb(255, 176, 193, 255), Color.argb(255, 255, 255, 255))
            }
            2 -> {
                colors = intArrayOf(Color.argb(255, 255, 176, 190), Color.argb(255, 255, 255, 255))
            }
            3 -> {
                colors = intArrayOf(Color.argb(255, 194, 176, 255), Color.argb(255, 255, 255, 255))
            }
            4 -> {
                colors = intArrayOf(Color.argb(255, 198, 255, 176), Color.argb(255, 255, 255, 255))
            }
            5 -> {
                colors = intArrayOf(Color.argb(255, 255, 203, 176), Color.argb(255, 255, 255, 255))
            }
            else -> {
                colors = intArrayOf(Color.argb(255, 176, 188, 255), Color.argb(255, 255, 255, 255))
            }
        }
        gradientDrawable.colors = colors
        binding.viewpager.background = gradientDrawable
    }

    private fun initViewPagerAdapter() {
        if (mAdapter == null) {
            mAdapter = XBPagerAdapter(mBannerPageListner, mImageCount)
        }
        binding.viewpager.adapter = mAdapter
        mAdapter!!.setData(mBannerImages)
        if (mImageCount > 1) {
            binding.viewpager.currentItem = 1
        } else {
            binding.viewpager.currentItem = 0
        }
        setBannerBg(binding.viewpager.currentItem)
    }

    /**
     * Get the true position of the indicator
     *
     * @param pos the pos of the viewpager now
     */
    private fun getTruePos(pos: Int): Int {
        //get the position of the indicator
        var truepos = (pos - 1) % mImageCount
        if (truepos < 0) {
            truepos = mImageCount - 1
        }
        return truepos
    }

    /**
     * Config the viewpager listener to the viewpager
     * Only if the image count is greater than 1
     */
    private fun setBg(position: Int, positionOffset: Float) {
        if (positionOffset == 0f) return
        when (position) {
            1 -> {
                val red = ((255 - 176) * positionOffset).toInt()
                val green = ((221 - 193) * positionOffset).toInt()
                val blue = ((255 - 176) * positionOffset).toInt()
                colors = intArrayOf(
                    Color.argb(255, 255 - red, 221 - green, 176 + blue),
                    Color.argb(255, 255, 255, 255)
                )
            }
            2 -> {
                val red = ((255 - 176) * positionOffset).toInt()
                val green = ((193 - 176) * positionOffset).toInt()
                val blue = ((255 - 190) * positionOffset).toInt()
                colors = intArrayOf(
                    Color.argb(255, 176 + red, 193 - green, 255 - blue),
                    Color.argb(255, 255, 255, 255)
                )
            }
            3 -> {
                val red = ((255 - 194) * positionOffset).toInt()
                val blue = ((255 - 190) * positionOffset).toInt()
                colors = intArrayOf(
                    Color.argb(255, 255 - red, 176, 190 + blue),
                    Color.argb(255, 255, 255, 255)
                )
            }
            4 -> {
                val red = ((198 - 194) * positionOffset).toInt()
                val green = ((255 - 176) * positionOffset).toInt()
                val blue = ((255 - 176) * positionOffset).toInt()
                colors = intArrayOf(
                    Color.argb(255, 194 + red, 176 + green, 255 - blue),
                    Color.argb(255, 255, 255, 255)
                )
            }
            5 -> {
                val red = ((255 - 198) * positionOffset).toInt()
                val green = ((255 - 203) * positionOffset).toInt()
                colors = intArrayOf(
                    Color.argb(255, 198 + red, 255 - green, 176),
                    Color.argb(255, 255, 255, 255)
                )
            }
            6 -> {
                val red = ((255 - 176) * positionOffset).toInt()
                val green = ((203 - 188) * positionOffset).toInt()
                val blue = ((255 - 176) * positionOffset).toInt()
                colors = intArrayOf(
                    Color.argb(255, 255 - red, 203 - green, 176 + blue),
                    Color.argb(255, 255, 255, 255)
                )
            }
            else -> {
                // 最后一位和第一位左右滑动
                val red = ((255 - 176) * positionOffset).toInt()
                val green = ((211 - 188) * positionOffset).toInt()
                val blue = ((255 - 176) * positionOffset).toInt()
                colors = intArrayOf(
                    Color.argb(255, 176 + red, 188 + green, 255 - blue),
                    Color.argb(255, 255, 255, 255)
                )
            }
        }
        //        if (position == -1) {
//            colors = new int[]{Color.argb(0, 0, 0, 0), Color.argb(0, 0, 0, 0)};
//        } else if (position == 0) {
//            colors = new int[]{Color.argb(255, 255, 221, 176), Color.argb(255, 255, 255, 255)};
//        } else if (position == 1) {
//            colors = new int[]{Color.argb(255, 176, 193, 255), Color.argb(255, 255, 255, 255)};
//        } else if (position == 2) {
//            colors = new int[]{Color.argb(255, 255, 176, 190), Color.argb(255, 255, 255, 255)};
//        } else if (position == 3) {
//            colors = new int[]{Color.argb(255, 194, 176, 255), Color.argb(255, 255, 255, 255)};
//        } else if (position == 4) {
//            colors = new int[]{Color.argb(255, 198, 255, 176), Color.argb(255, 255, 255, 255)};
//         } else if (position == 5) {
//            colors = new int[]{Color.argb(255, 255, 203, 176), Color.argb(255, 255, 255, 255)};
//        } else {
//            colors = new int[]{Color.argb(255, 176, 188, 255), Color.argb(255, 255, 255, 255)};
//        }
        gradientDrawable.colors = colors
        binding.viewpager.background = gradientDrawable
    }

    private fun applyViewPagerAdapterListener() {
        if (mImageCount <= 1) {
            return
        }
        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (showBg) setBg(position, positionOffset)
            }

            override fun onPageSelected(position: Int) {
                onIndicatorChange(position)
                if (mCallBack != null) mCallBack!!.selectPosition(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                val current: Int = binding.viewpager.currentItem
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> {
                        mBannerPageListner?.onBannerDragging(current)
                        if (xbannerScroller != null) {
                            xbannerScroller!!.duration = PAGE_TRANSFORM_DELAY_DRAGGING
                        }
                        if (current == 0) {
                            binding.viewpager.setCurrentItem(mImageCount, false)
                        }
                        if (current == mImageCount + 1) {
                            binding.viewpager.setCurrentItem(1, false)
                        }
                        setBannerBg(binding.viewpager.currentItem)
                    }
                    ViewPager.SCROLL_STATE_IDLE -> {
                        mBannerPageListner?.onBannerIdle(current)
                        if (xbannerScroller != null) {
                            xbannerScroller!!.duration = pageTransformerDelayIdle
                        }
                        if (current == mImageCount + 1) {
                            binding.viewpager.setCurrentItem(1, false)
                        } else if (current == 0) {
                            binding.viewpager.setCurrentItem(mImageCount, false)
                        }
                        setBannerBg(binding.viewpager.currentItem)
                    }
                    else -> {
                    }
                }
            }
        })
    }

    private var mCallBack: CallBack? = null
    fun setCallBack(callBack: CallBack?): XBanner {
        mCallBack = callBack
        return this
    }

    interface CallBack {
        fun selectPosition(position: Int)
    }

    fun setCurrentItem(currentItem: Int) {
        binding.viewpager.setCurrentItem(currentItem, false)
    }

    private fun onIndicatorChange(position: Int) {
        when (mBannerType) {
            CIRCLE_INDICATOR -> onCircleIndicatorChange(position)
            CIRCLE_INDICATOR_TITLE -> {
                onCircleIndicatorChange(position)
                mBannerTitle!!.text = mTitles!![getTruePos(position)]
            }
            CUBE_INDICATOR -> onCubeIndicatorChange(position)
            NUM_INDICATOR -> onNumIndicatorChange(position)
            NUM_INDICATOR_TITLE -> {
                onNumIndicatorChange(position)
                mBannerTitle!!.text = mTitles!![getTruePos(position)]
            }
            else -> {
            }
        }
    }

    private fun onCircleIndicatorChange(position: Int) {
        mIndicators!![getTruePos(position)].setImageResource(if (mIndicatorSet) mIndicatorSelected else R.drawable.indicator_selected)
        for (i in mIndicators!!.indices) {
            if (i != getTruePos(position)) {
                mIndicators!![i].setImageResource(if (mIndicatorSet) mIndicatorUnselected else R.drawable.indicator_unselected)
            }
        }
    }

    private fun onCubeIndicatorChange(position: Int) {
        mIndicators!![getTruePos(position)].setImageResource(if (mIndicatorSet) mIndicatorSelected else R.drawable.indicator_cube_selected)
        for (i in mIndicators!!.indices) {
            if (i != getTruePos(position)) {
                mIndicators!![i].setImageResource(if (mIndicatorSet) mIndicatorUnselected else R.drawable.indicator_cube_unselected)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onNumIndicatorChange(position: Int) {
        var i = position
        if (i == 0) {
            i = mImageCount
        }
        if (position > mImageCount) {
            i = 1
        }
        mNumIndicator!!.text = "$i/$mImageCount"
    }

    /**
     * start this banner
     */
    fun start() {
        checkImageAndTitleNum()
        loadFromUrlsIfNeeded()
        initView()
        showIndicators()
        applyViewPagerAdapterListener()
        startPlayIfNeeded()
    }

    private fun startPlay() {
        if (mIsAutoPlay) {
            mIsPlaying = true
            mRunnable = ViewPagerRunnable(binding.viewpager, mImageCount, mDelayTime)
            mHandler.postDelayed(mRunnable!!, mDelayTime.toLong())
        }
    }

    private fun startPlayIfNeeded() {
        if (mImageCount > 1 && mIsAutoPlay) {
            startPlay()
        }
    }

    /**
     * check the number of titles and images
     * titles and images must have the same size to avoid some logic error
     */
    private fun checkImageAndTitleNum() {
        if (mImageCount != mTitles!!.size && (mBannerType == CIRCLE_INDICATOR_TITLE || mBannerType == NUM_INDICATOR_TITLE)) {
            throw RuntimeException(
                "image size and title size is not the same\n"
                        + "size of images: " + mImageCount + "\n"
                        + "size of titles: " + mTitles!!.size + "\n"
                        + "if you do Not need titles,please set the banner type to non-title type"
            )
        }
    }

    /**
     * Load image from urls
     * need to apply an imageloader,see[ImageLoader]
     */
    private fun loadFromUrlsIfNeeded() {
        if (mImageLoader == null) {
            return
        }
        if (showBg) {
            if (mBannerImages!!.isEmpty() && !adViewList!!.isEmpty()) {
                if (mImageCount > 1) {
                    mBannerImages!!.add(newView(adsList!![mImageCount - 1]))
                    for (i in adViewList!!.indices) {
                        mBannerImages!!.add(newView(adsList!![i]))
                    }
                }
                mBannerImages!!.add(newView(adsList!![0]))
            }
        } else {
            if (mBannerImages!!.isEmpty() && mUrls!!.isNotEmpty()) {
                if (mImageCount > 1) {
                    val ads: Ads = adsList!![mImageCount - 1]
                    if (ads.smallImage.contains(".mp4")
                    ) mBannerImages!!.add(newVideoViewFroUrl(ads)) else mBannerImages!!.add(
                        newImageFroUrl(ads, mImageCount - 1)
                    )
                    for (i in mUrls!!.indices) {
                        val ads1: Ads = adsList!![i]
                        if (ads1.smallImage.contains(".mp4")) mBannerImages!!.add(
                            newVideoViewFroUrl(ads1)
                        ) else mBannerImages!!.add(newImageFroUrl(ads1, i))
                    }
                    val ads2: Ads = adsList!![0]
                    if (ads2.smallImage.contains(".mp4")) mBannerImages!!.add(
                        newVideoViewFroUrl(ads2)
                    ) else mBannerImages!!.add(newImageFroUrl(ads2, 0))
                } else {
                    val ads: Ads = adsList!![0]
                    if (ads.smallImage.contains(".mp4")
                    ) mBannerImages!!.add(newVideoViewFroUrl(ads)) else mBannerImages!!.add(
                        newImageFroUrl(ads, 0)
                    )
                }
            }
        }
    }

    private fun newImageFroUrl(ads: Ads, position: Int): ImageView {
        val image = ImageView(mContext)
        image.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        image.scaleType = mScaleType
        mImageLoader!!.loadImages(mContext, ads, image, position)
        return image
    }

    private fun newVideoViewFroUrl(ads: Ads): VideoView {
        val videoView = VideoView(mContext)
        videoView.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mImageLoader!!.loadVideoViews(mContext, ads, videoView)
        return videoView
    }

    private fun newView(ads: Ads): View {
        val linearLayout = LinearLayout(mContext)
        linearLayout.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mImageLoader!!.loadView(linearLayout, ads.view)
        return ads.view!!
    }

    /**
     * the listener interface for banner event,includeing clicked,dragging and idled
     * the index starts from 0,which item's value starts from 0
     */
    fun setBannerPageListener(listener: XBPagerAdapter.BannerPageListener?): XBanner {
        mBannerPageListner = listener
        return this
    }

    fun setImageLoader(imageLoader: ImageLoader?): XBanner {
        mImageLoader = imageLoader
        return this
    }

    private inner class ViewPagerRunnable internal constructor(
        viewPager: ViewPager?,
        imagecount: Int,
        delay: Int
    ) :
        Runnable {
        //avoid memory leak
        private val mViewPager: WeakReference<ViewPager?> = WeakReference(viewPager)
        var count: Int = imagecount
        var delaytime: Int = delay
        override fun run() {
            if (count > 1) {
                if (mViewPager.get() != null) {
                    val current = mViewPager.get()!!.currentItem
                    if (current == count + 1) {
                        mViewPager.get()!!.setCurrentItem(1, false)
                        mHandler.post(this)
                    } else {
                        mViewPager.get()!!.currentItem = current + 1
                        mHandler.postDelayed(this, delaytime.toLong())
                    }
                    setBannerBg(mViewPager.get()!!.currentItem)
                }
            }
        }

    }

    override fun dispatchTouchEvent(me: MotionEvent): Boolean {
        super.dispatchTouchEvent(me)
        if (me.action == MotionEvent.ACTION_DOWN) {
            if (mIsPlaying) {
                if (mRunnable != null) {
                    mHandler.removeCallbacks(mRunnable!!)
                    mRunnable = null
                }
                mIsPlaying = false
            }
        }
        if ((me.action == MotionEvent.ACTION_UP) || (me.action == MotionEvent.ACTION_CANCEL
                    ) || ((me.action == MotionEvent.ACTION_OUTSIDE) && !mIsPlaying && mIsAutoPlay)
        ) {
            mIsPlaying = true
            startPlay()
        }
        return true
    }

    //release banner here
    fun releaseBanner() {
        try {
            if (mBannerImages != null) {
                mBannerImages!!.clear()
            }
            if (mIndicators != null) {
                mIndicators!!.clear()
            }
            if (mRunnable != null) {
                mHandler.removeCallbacks(mRunnable!!)
                mRunnable = null
            }
            if (mAdapter != null) mAdapter!!.releaseAdapter()
            mAdapter = null
            mBannerPageListner = null
            mImageLoader = null

            val f = ViewPager::class.java.getDeclaredField("mOnPageChangeListeners")
            f.isAccessible = true
            f[binding.viewpager] = null
            binding.viewpager.adapter = null
            binding.viewpager.removeAllViews()
            System.gc()
            System.runFinalization()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        //transform delay set to 250ms when dragging
        const val PAGE_TRANSFORM_DELAY_DRAGGING = 250

        //indicator gravity types
        const val INDICATOR_START = 0
        const val INDICATOR_CENTER = 1
        const val INDICATOR_END = 2

        //banner styles
        const val CIRCLE_INDICATOR = 0
        const val CIRCLE_INDICATOR_TITLE = 1
        const val CUBE_INDICATOR = 2
        const val NUM_INDICATOR = 3
        const val NUM_INDICATOR_TITLE = 4
        const val INDICATOR_NON = 5
        private val mHandler = Handler()
    }
}