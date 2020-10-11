package com.usenergysolutions.energybroker.view.user

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.utils.FileUtils
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.dialog_image_picker.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Ref: https://stackoverflow.com/questions/19219604/how-to-set-cancellable-false-to-an-activity-when-it-is-opened-as-a-dialog
// Ref: https://stackoverflow.com/questions/1362723/how-can-i-get-a-dialog-style-activity-window-to-fill-the-screen
// Ref: https://stackoverflow.com/questions/6325018/android-activity-as-dialog-but-without-a-title-bar
// Ref: https://stackoverflow.com/questions/1979369/android-activity-as-a-dialog
// Ref: https://github.com/naver/android-imagecropview
// Ref: https://stackoverflow.com/questions/2975197/convert-file-uri-to-file-in-android
// Ref: https://stackoverflow.com/questions/21306720/uploading-image-from-android-to-php-server
// Ref: https://developer.android.com/training/camera/photobasics

class ImagePickerDialog : AppCompatActivity() {
    private val TAG: String = "ImagePickerDialog"

    // Image Source request Code
    private val RC_SOURCE_CAMERA = 123
    private val RC_SOURCE_GALLERY = 124
    private val RC_CROP_IMAGE = 125

    private lateinit var context: Context

    private var avatarUrl: Uri? = null
    private var uploadNeeded: Boolean = false

    //  Communication
    private var viewModel: UserViewModel? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_image_picker)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.setFinishOnTouchOutside(false)
        Log.d(TAG, "onCreate")

        context = this

        // Fabric.with(this, Crashlytics())
        Crashlytics.log("$TAG onCreate")

        // Bind the Map Model View
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        if (intent != null && intent.data != null) {
            avatarUrl = intent.data
            if (avatarUrl != null) {
                Glide.with(context as ImagePickerDialog).load(avatarUrl).apply(RequestOptions.circleCropTransform())
                    .into(avatarImageView)
                editButton.isEnabled = true
            }
        }

        // Get Image from camera
        // Ref: https://developer.android.com/training/camera/photobasics
        cameraButton.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, RC_SOURCE_CAMERA)
                }
            }
        }


        // Get Image from gallery
        galleryButton.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, RC_SOURCE_GALLERY)
        }

        cancelButton.setOnClickListener {
            finish()
        }

        editButton.setOnClickListener {
            if (avatarUrl != null) {
                startCrop(avatarUrl!!)
            }
        }

        saveButton.setOnClickListener {
            showLoadingDialog()
            if (uploadNeeded && avatarUrl != null) {
                val imagePath: String? = avatarUrl!!.path
                val fileToUpload = File(imagePath)
                viewModel?.uploadFile(fileToUpload)?.observe(this@ImagePickerDialog,
                    Observer<DataWrapper<String>> { t ->
                        progressDialog!!.dismiss()
                        if (t?.data != null) {
                            avatarUrl = Uri.parse(t.data)
                            if (avatarUrl != null) {
                                Glide.with(context as ImagePickerDialog).load(avatarUrl)
                                    .apply(RequestOptions.circleCropTransform()).into(avatarImageView)
                                Toast.makeText(
                                    context,
                                    "Image uploaded successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent: Intent = Intent()
                                intent.data = avatarUrl
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                                overridePendingTransition(R.anim.activity_slide_in_rev, R.anim.activity_slide_out_rev)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to upload image " + t?.throwable?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            } else {
                finish()
            }
        }
    }

    private fun showLoadingDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
            val frag: Fragment? = supportFragmentManager.findFragmentByTag(progressDialog!!::class.java.simpleName)
            if (frag != null) {
                supportFragmentManager.beginTransaction().remove(frag).commit()
            }
        }
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
    }

    private fun startCrop(imageUri: Uri) {
        val intent = Intent(this@ImagePickerDialog, CropActivity::class.java)
        intent.data = imageUri
        startActivityForResult(intent, RC_CROP_IMAGE)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    // Ref: https://developer.android.com/training/camera/photobasics
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        editButton.isEnabled = false
        avatarUrl = null
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_SOURCE_CAMERA) {
                val imageBitmap: Bitmap? = data?.extras?.get("data") as Bitmap?
                if (imageBitmap != null) {
                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val filename: String = "IMG_$timeStamp.jpg"
                    val bitmapFilePath: String? =
                        FileUtils.writeBitmapToFile(Constants.BASE_PATH, filename, imageBitmap)
                    if (bitmapFilePath != null) {
                        val selectedImage: Uri? =
                            Uri.parse(bitmapFilePath) // intent.data //extras.get("data") as Uri?
                        avatarUrl = selectedImage
                        if (selectedImage != null) {
                            uploadNeeded = true
                            Glide.with(this@ImagePickerDialog).load(imageBitmap)
                                .apply(RequestOptions.circleCropTransform()).into(avatarImageView)
                            editButton.isEnabled = true
                        }
                    }
                }
            }
            if (requestCode == RC_SOURCE_GALLERY) {
                val selectedImage: Uri? = data?.data
                val realPath: String = FileUtils.getRealPathFromURI(context, selectedImage)
                avatarUrl = Uri.parse(realPath)
                if (selectedImage != null) {
                    uploadNeeded = true
                    Glide.with(this@ImagePickerDialog).load(selectedImage).apply(RequestOptions.circleCropTransform())
                        .into(avatarImageView)
                    editButton.isEnabled = true
                }
            }
            if (requestCode == RC_CROP_IMAGE) {
                val selectedImage = data?.data
                avatarUrl = selectedImage
                if (selectedImage != null) {
                    uploadNeeded = true
                    Glide.with(this@ImagePickerDialog).load(selectedImage).apply(RequestOptions.circleCropTransform())
                        .into(avatarImageView)
                    editButton.isEnabled = true
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.activity_slide_in_rev, R.anim.activity_slide_out_rev)
    }
}