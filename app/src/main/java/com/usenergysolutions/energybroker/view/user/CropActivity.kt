package com.usenergysolutions.energybroker.view.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CropActivity : AppCompatActivity() {
    private val TAG: String = "CropActivity"

    // UI Components
    // private lateinit var imageCropView: ImageCropView // TODO Remove if not necessary

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        Log.d(TAG, "onCreate")

        context = this

        // Fabric.with(this, Crashlytics())
        Crashlytics.log("$TAG onCreate")

        // Get Image URI
        val imageUri: Uri? = intent?.data
        if (imageUri == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        imageCropView.setImageFilePath(imageUri.toString())
        imageCropView.setAspectRatio(1, 1)

        ratio11btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click 1 : 1")
                if (isPossibleCrop(1, 1)) {
                    imageCropView.setAspectRatio(1, 1)
                } else {
                    Toast.makeText(context, R.string.can_not_crop, Toast.LENGTH_SHORT).show()
                }
            }
        })

        ratio34btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click 3 : 4")
                if (isPossibleCrop(3, 4)) {
                    imageCropView.setAspectRatio(3, 4)
                } else {
                    Toast.makeText(context, R.string.can_not_crop, Toast.LENGTH_SHORT).show()
                }
            }
        })

        ratio43btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click 4 : 3")
                if (isPossibleCrop(4, 3)) {
                    imageCropView.setAspectRatio(4, 3)
                } else {
                    Toast.makeText(context, R.string.can_not_crop, Toast.LENGTH_SHORT).show()
                }
            }
        })

        ratio169btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click 16 : 9")
                if (isPossibleCrop(16, 9)) {
                    imageCropView.setAspectRatio(16, 9)
                } else {
                    Toast.makeText(context, R.string.can_not_crop, Toast.LENGTH_SHORT).show()
                }
            }
        })

        ratio916btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click 9 : 16")
                if (isPossibleCrop(9, 16)) {
                    imageCropView.setAspectRatio(9, 16)
                } else {
                    Toast.makeText(context, R.string.can_not_crop, Toast.LENGTH_SHORT).show()
                }
            }
        })

        crop_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click Crop")
                if (!imageCropView.isChangingScale) {
                    val b: Bitmap? = imageCropView.croppedImage
                    if (b != null) {
                        val bitmapFile = bitmapConvertToFile(b)
                        if (bitmapFile != null) {
                            var intent: Intent = Intent()
                            intent.data = Uri.fromFile(bitmapFile)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                            overridePendingTransition(R.anim.activity_slide_in_rev, R.anim.activity_slide_out_rev)
                        }
                    } else {
                        Toast.makeText(context, R.string.fail_to_crop, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        restore_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click Restore")
                imageCropView.restoreState()
            }
        })

        save_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG, "click Save")
                imageCropView.saveState()
                if (!restore_btn.isEnabled) {
                    restore_btn.isEnabled = true
                }
            }
        })
    }

    private fun isPossibleCrop(widthRatio: Int, heightRatio: Int): Boolean {
        val bitmap: Bitmap = imageCropView.viewBitmap ?: return false
        val bitmapWidth: Int = bitmap.width
        val bitmapHeight: Int = bitmap.height
        return !(bitmapWidth < widthRatio && bitmapHeight < heightRatio)
    }

    private fun bitmapConvertToFile(bitmap: Bitmap): File? {
        var fileOutputStream: FileOutputStream? = null
        var bitmapFile: File? = null

        try {
            var file = File(Environment.getExternalStoragePublicDirectory("energy_broker_images"), "")
            if (!file.exists()) {
                file.mkdir()
            }

            bitmapFile =
                File(file, "IMG_" + (SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().time) + ".jpg")
            fileOutputStream = FileOutputStream(bitmapFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

            val contentUri: Uri = Uri.fromFile(bitmapFile)
            val mediaScanIntent: Intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)

//            MediaScannerConnection.scanFile(context, arrayOf(bitmapFile.absolutePath), null, object: MediaScannerConnection.MediaScannerConnectionClient{
//                override fun onMediaScannerConnected() {
//                    // Do nothing
//                }
//
//                override fun onScanCompleted(path: String?, uri: Uri?) {
//                    runOnUiThread(object: Runnable{
//                        override fun run() {
//                            Toast.makeText(context, "file saved", Toast.LENGTH_LONG).show()
//                        }
//                    })
//                }
//            })
        } catch (e: Throwable) {
            Log.e(TAG, "bitmapConvertToFile", e)
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }
        return bitmapFile
    }
}