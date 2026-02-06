package edu.ivytech.rootbeer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.ivytech.rootbeer.database.RootBeer
import edu.ivytech.rootbeer.databinding.FragmentRootBeerDetailBinding
import java.io.*
import java.util.*

class RootBeerDetailFragment : Fragment() {
    private lateinit var binding: FragmentRootBeerDetailBinding
    var rating = 5
    var quantity = 0

    private var rootBeer: RootBeer? = null
    private lateinit var photoFile:File
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var barcodeLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isNew = true
    private val rootBeerVM : RootBeerDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            if(it.containsKey(ARG_ITEM_ID)) {
                rootBeerVM.loadRootBeer(it.getSerializable(ARG_ITEM_ID) as UUID)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRootBeerDetailBinding.inflate(inflater, container, false)

        binding.quantityDownButton.setOnClickListener {
            if (quantity > 0)
                quantity--
            binding.quantityDownButton.isEnabled = rating > 0
            binding.quantityEditText.setText(quantity.toString())
        }
        binding.quantityUpButton.setOnClickListener {
            quantity++
            binding.quantityEditText.setText(quantity.toString())
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions -> permissions.entries.forEach {
            Log.e("DEBUG", "${it.key} = ${it.value}")
        }
        }
        //add rating bar listener using setOnRatingBarChangeListener and set the rating in the root beer object
        cameraLauncher =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if(result.resultCode == Activity.RESULT_OK) {
                val uri = FileProvider.getUriForFile(
                    requireActivity(),
                    "edu.ivytech.rootbeer.fileprovider",
                    photoFile
                )
                requireActivity().revokeUriPermission(
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
            }
        }
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            val data:Intent? = result.data
            if(result.resultCode == Activity.RESULT_OK && data != null) {

                val inputStream = requireActivity().contentResolver.openInputStream(data.data!!)
                val fileOutputStream = FileOutputStream(photoFile)
                inputStream?.copyTo(fileOutputStream)
                inputStream!!.close()
                fileOutputStream.close()
                updatePhotoView()

            }
        }
        barcodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            val data:Intent? = result.data
            if(result.resultCode == Activity.RESULT_OK) {
                //get the barcode from the intent and set the text on the barcodeEditText
                binding.barcodeEditText.setText(data!!.getStringExtra("barcode"))
            }
        }
        binding.pictureOfRootBeer.setOnClickListener { selectImage() }
        binding.scanbtn.setOnClickListener {
              val scanBarcode = Intent(requireActivity(),ScanBarcodeActivity::class.java)
               barcodeLauncher.launch(scanBarcode)

          }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
        rootBeerVM.rootBeerLiveData.observe(viewLifecycleOwner) {
                item -> rootBeer = item
            updateUI()
        }
        updateUI()

        binding.nameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rootBeer?.name = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.manufacturerEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rootBeer?.manufacturer = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.flavorEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rootBeer?.flavor = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.locationEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rootBeer?.location = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.barcodeEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rootBeer?.barcode = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.quantityEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rootBeer?.quantity = s.toString().toInt()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            rootBeer?.rating = rating
        }
    }

    override fun onStop() {
        super.onStop()
        if(isNew && rootBeer!!.name.isNotEmpty()) {
            rootBeerVM.addRootBeer(rootBeer!!)
        } else {
            rootBeerVM.saveRootBeer(rootBeer!!)
        }

    }

    private fun updateUI() {
        if(rootBeer != null){
            binding.toolbarLayout.title = "Edit Root Beer"
            binding.nameEditText.setText(rootBeer?.name)
            binding.barcodeEditText.setText(rootBeer?.barcode)
            binding.flavorEditText.setText(rootBeer?.flavor)
            binding.locationEditText.setText(rootBeer?.location)
            binding.manufacturerEditText.setText(rootBeer?.manufacturer)
            binding.quantityEditText.setText(rootBeer?.quantity.toString())
            binding.ratingBar.rating = rootBeer?.rating!!
            isNew = false;


        } else {
            binding.toolbarLayout.title = "New Root Beer"
            rootBeer = RootBeer()

        }
        val filesDir = requireActivity().applicationContext.filesDir
        photoFile = File(filesDir, rootBeer!!.photoFileName)
        updatePhotoView()
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a picture for the bottle")

        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {

                if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "edu.ivytech.rootbeer.fileprovider",
                        photoFile
                    )
                    val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)

                    val cameraActivities: List<ResolveInfo> =
                        requireActivity()
                            .packageManager.queryIntentActivities(
                                captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY
                            )
                    for (activity in cameraActivities) {
                        requireActivity().grantUriPermission(
                            activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                    cameraLauncher.launch(captureImage)
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            } else if (options[item] == "Choose from Gallery") {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                } else {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    )
                    galleryLauncher.launch(pickPhoto)
                }

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun updatePhotoView() {
        if (!photoFile.exists()) {
            return
        } else {
            val bitmap: Bitmap? = getScaledBitmap(
                photoFile.path,
                requireActivity()
            )
            binding.pictureOfRootBeer.setImageBitmap(bitmap)
        }
    }


    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}