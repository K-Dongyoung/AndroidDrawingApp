package com.practice.mydrawingapp

import android.Manifest
import android.app.Dialog
import android.app.usage.ExternalStorageStats
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.practice.mydrawingapp.databinding.ActivityMainBinding
import com.practice.mydrawingapp.databinding.DialogBrushSizeBinding

class MainActivity : AppCompatActivity() {

    private val requestPermission : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permmisions ->
            permmisions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    Toast.makeText(this@MainActivity,
                        "Permission granted now you can read the storage files.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                        Toast.makeText(this@MainActivity,
                            "Oops you just denied the permission.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private var drawingView : DrawingView? = null
    private var mImageButtonCurrentPaint : ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView = binding.drawingView
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColors = binding.llPaintColors
        mImageButtonCurrentPaint = linearLayoutPaintColors[6] as ImageButton

        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        binding.ibBrush.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        binding.ibGallery.setOnClickListener {
            requestStoragePermission()
        }

    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        var binding = DialogBrushSizeBinding.inflate(layoutInflater)
        brushDialog.setContentView(binding.root)
        brushDialog.setTitle("Brush size: ")

        brushDialog.show()

        binding.ibSmallBrush.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        binding.ibMediumBrush.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        binding.ibLargeBrush.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

    }

    fun paintClicked(view : View) {
        if (view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )

            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )

            mImageButtonCurrentPaint = view
        }
    }

    private fun requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
            this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            showRationaleDialog("Drawing App",
                "Drawing App needs to Access Your External Storage")
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }
    private fun showRationaleDialog(
        title : String,
        message : String
    ) {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}