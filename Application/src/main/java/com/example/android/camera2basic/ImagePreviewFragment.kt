package com.example.android.camera2basic

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cloudinary.Transformation
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.android.synthetic.main.fragment_image_preview.*
import java.io.File


/**
 * Created by idorenyin on 1/21/18.
 */
class ImagePreviewFragment:Fragment(), ViewPager.OnPageChangeListener {

    val TAG = "ImagePreviewFragment"

    lateinit var adapter: ViewPagerAdapter

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var viewPager: ViewPager
    lateinit var imageUri: Uri
    lateinit var publicId: String

    companion object {
        fun newInstance(file: String): ImagePreviewFragment {
            val bundle = Bundle()
            bundle.putString("file_location", file)
            val fragment = ImagePreviewFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): ImagePreviewFragment {
            return ImagePreviewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = LayoutInflater.from(activity!!).inflate(R.layout.fragment_image_preview, container, false)

        adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        imageView = view.findViewById(R.id.imageView)
        progressBar = view.findViewById(R.id.progress_bar)
        viewPager = view.findViewById(R.id.view_pager)

        viewPager.addOnPageChangeListener(this)


        progressBar.visibility=View.VISIBLE


        if (arguments != null) {
            val file = File(arguments!!.getString("file_location"))
            imageUri = Uri.fromFile(file)

            Glide.with(activity!!)
                    .load(file)
                    .apply(RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(imageView)


            uploadToCloudinary(imageUri)
        }

        return view
    }

    private fun uploadToCloudinary(uri:Uri) {

        MediaManager.get()
                .upload(uri)
                .unsigned("ddsfdcvz")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "upload onStart")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        Log.d(TAG, bytes.toString() + "-" + totalBytes.toString())
                    }

                    //@Suppress("INACCESSIBLE_TYPE")
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        progressBar.visibility= View.INVISIBLE
                        publicId = resultData["public_id"] as String
                        Toast.makeText(activity, "Upload successful", Toast.LENGTH_LONG).show()
                        Log.d(TAG, resultData.toString())
                        viewPager.adapter=adapter

                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.d(TAG, error.description)
                        progress_bar.visibility = View.INVISIBLE
                        Toast.makeText(activity, "Upload was not successful", Toast.LENGTH_LONG).show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.d(TAG, "onReschedule")
                        Toast.makeText(activity, "Upload was not successful", Toast.LENGTH_LONG).show()

                    }
                }).dispatch()

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

        lateinit var url:String

        when (position) {

            0 -> {
                // default image
                val file = File(arguments!!.getString("file_location"))
                imageUri = Uri.fromFile(file)

                Glide.with(activity!!)
                        .load(file)
                        .apply(RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .into(imageView)

            }
            1 -> {
                // grayscale
                url = MediaManager.get()
                        .url()
                        .transformation(Transformation<Transformation<out Transformation<*>>?>().effect("grayscale")!!.quality("auto"))
                        .generate("$publicId.png")

            }
            2 -> {

                // mask on face
                url = MediaManager.get()
                        .url()
                        .transformation(Transformation<Transformation<out Transformation<*>>?>()
                                .flags("region_relative")
                                !!.gravity("faces")!!.overlay("mask")
                                .width(0.7)!!.quality("auto"))
                        .generate("$publicId.png")
                Log.d("TAG",url)

            }
            3 -> {

                // red rock
                url = MediaManager.get()
                        .url()
                        .transformation(Transformation<Transformation<out Transformation<*>>?>().effect("art:red_rock")!!.quality("auto"))
                        .generate("$publicId.png")


            }
            4 -> {

                // Oil paint
                url = MediaManager.get()
                        .url()
                        .transformation(Transformation<Transformation<out Transformation<*>>?>()
                                .effect("pixelate_faces:9")
                        !!.quality("auto"))
                        .generate("$publicId.png")
            }

        }

        if (position!=0)
            Glide.with(activity!!).load(url).into(imageView)

    }


}
