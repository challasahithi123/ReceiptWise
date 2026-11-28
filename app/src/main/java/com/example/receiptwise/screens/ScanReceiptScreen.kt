package com.example.receiptwise.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.receiptwise.scan.readReceipt
import java.io.File

// The scan screen. It opens the camera, takes a photo, reads it and then sends
// the shop, date and total to the next screen using onScanned.
@Composable
fun ScanReceiptScreen(onScanned: (String, String, String) -> Unit = { _, _, _ -> }) {

    val context = LocalContext.current
    // The camera is tied to the activity, so it switches off when the app is not on screen.
    // This saves battery.
    val lifecycleOwner = context as LifecycleOwner
    val imageCapture = remember { ImageCapture.Builder().build() }

    var showCamera by remember { mutableStateOf(false) }  // is the camera open?
    var busy by remember { mutableStateOf(false) }        // is a photo being read?
    var message by remember { mutableStateOf("") }        // message for the user

    // This asks the user for the camera permission and tells us the answer
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            message = ""
            showCamera = true
        } else {
            // The app keeps working. The user can still add an expense by hand.
            message = "The camera permission was refused. You can still add an expense by hand."
        }
    }

    // Takes the photo and reads it
    fun takePhoto() {
        busy = true
        message = ""

        // The photo goes into the private cache folder of the app, so other apps cannot open it
        val photoFile = File(context.cacheDir, "receipt_" + System.currentTimeMillis() + ".jpg")
        val options = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            options,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    readReceipt(context, photoFile) { info ->
                        // The photo is deleted straight away, so no picture of the receipt is kept
                        photoFile.delete()
                        busy = false

                        if (info == null) {
                            message = "No text was found. Try a clearer photo or add the expense by hand."
                        } else {
                            showCamera = false
                            onScanned(info.shop, info.date, info.total)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    busy = false
                    message = "The photo could not be taken. Please try again."
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scan Receipt", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (!showCamera) {
            Text("Take a photo of a receipt and the app will fill in the expense for you.")
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                // The permission is only asked now, when the user taps the button
                val allowed = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (allowed) {
                    showCamera = true
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("Take Photo")
            }
        } else {
            // Turn the camera off when the user leaves this screen
            DisposableEffect(Unit) {
                onDispose { ProcessCameraProvider.getInstance(context).get().unbindAll() }
            }

            // The live camera picture. AndroidView lets us use the normal Android PreviewView.
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val providerFuture = ProcessCameraProvider.getInstance(ctx)

                    providerFuture.addListener({
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)

                        // Use the back camera and connect it to the screen and the photo taker
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { takePhoto() }, enabled = !busy) {
                Text(if (busy) "Reading the receipt..." else "Capture")
            }
            TextButton(onClick = { showCamera = false }, enabled = !busy) {
                Text("Cancel")
            }
        }

        // Shows messages such as errors
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}