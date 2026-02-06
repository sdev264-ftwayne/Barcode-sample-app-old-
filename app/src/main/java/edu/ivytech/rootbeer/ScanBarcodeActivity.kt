package edu.ivytech.rootbeer


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.WindowInsets
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import edu.ivytech.rootbeer.databinding.ActivityScanBarcodeBinding


class ScanBarcodeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityScanBarcodeBinding
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    val REQUEST_CAMERA_PERMISSION = 201
    var intentData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        initializeDetectorsAndSources()
    }

    fun initializeDetectorsAndSources() {
        Toast.makeText(this,"Barcode scanner started", Toast.LENGTH_SHORT).show()
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        val size = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val windowInsets: WindowInsets = windowMetrics.windowInsets

            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout())
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom

            val b = windowMetrics.bounds
            size.x = b.width() - insetsWidth
            size.y = b.height() - insetsHeight
        } else {
            windowManager.defaultDisplay.getSize(size)
        }

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(size.y, size.x)
            .setAutoFocusEnabled(true)
            .build()

        binding.surfaceView.holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                if(ActivityCompat.checkSelfPermission(this@ScanBarcodeActivity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
                    cameraSource.start(binding.surfaceView.holder)
                else
                    ActivityCompat.requestPermissions(this@ScanBarcodeActivity, arrayOf(Manifest.permission.CAMERA),REQUEST_CAMERA_PERMISSION)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }

        })

        barcodeDetector.setProcessor(object: Detector.Processor<Barcode> {
            override fun release() {
                Log.i("Barcode Scan Activity", "The barcode scanner has been stopped")
            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>) {
                val barcodes: SparseArray<Barcode> = p0.detectedItems
                if(barcodes.size() != 0) {
                    binding.txtBarcodeValue.post {
                        intentData = barcodes.valueAt(0).displayValue
                        if(intentData.isNotEmpty()) {
                            val i = Intent()
                            i.putExtra("barcode", intentData)
                            setResult(Activity.RESULT_OK, i)
                            finish()
                        }
                    }
                }
            }
        })
    }


}