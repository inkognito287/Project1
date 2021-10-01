package com.example.qrreader.data

import com.google.mlkit.vision.barcode.Barcode

data class ScanResult(val barcodes: List<Barcode>, val imageWidth: Int, val imageHeight: Int)