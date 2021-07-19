package com.example.qrreader

import android.hardware.Camera
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.qrreader.databinding.FragmentMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.camera.CameraManager


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    var scanBtn: Button? = null
    val formatTxt: TextView? = null
    var contentTxt: TextView? = null
    lateinit var binding: FragmentMainBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scan()

    }


    private fun scan() {

        IntentIntegrator(requireActivity()).setOrientationLocked(false)
            .setCaptureActivity(CustomScannerActivity::class.java).setBarcodeImageEnabled(true).setBarcodeImageEnabled(true).setPrompt("").setOrientationLocked(false).setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .initiateScan()

    }


}