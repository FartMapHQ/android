package xyz.fartmap.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import xyz.fartmap.data.Preferences
import xyz.fartmap.ui.navigation.Destinations
import java.io.ByteArrayOutputStream
import kotlin.math.min

@Composable
fun Record(nav: NavHostController, prefs: Preferences) {
    val token: String? by prefs.token.collectAsState(null)
    var isRecording by remember { mutableStateOf(false) }
    var audioData by remember { mutableStateOf<ByteArray?>(null) }

    var audioRecord: AudioRecord? by remember { mutableStateOf(null) }

    val sampleRate = 44100
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Log.d("Permission", "Audio record permission denied")
            }
        }
    )

    Column {
        Button(
            onClick = {
                scope.launch {
                    prefs.logout()
                    nav.navigate(Destinations.WELCOME)
                }
            }
        ) {
            Text("Logout")
        }

        Button(
            onClick = {
                if (isRecording) {
                    isRecording = false
                } else {
                    // Check if permission is granted
                    if (ContextCompat.checkSelfPermission(
                            nav.context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Initialize AudioRecord
                        audioRecord = AudioRecord(
                            MediaRecorder.AudioSource.DEFAULT,
                            sampleRate,
                            channelConfig,
                            audioFormat,
                            bufferSize
                        )

                        isRecording = true

                        // Start recording in a coroutine
                        scope.launch(Dispatchers.IO) {
                            audioRecord?.startRecording()
                            val outputStream = ByteArrayOutputStream()
                            val buffer = ByteArray(bufferSize)

                            while (isRecording && audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                                val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                                if (readBytes > 0) {
                                    outputStream.write(buffer, 0, min(readBytes, buffer.size))
                                }
                            }

                            audioRecord?.stop()
                            audioRecord?.release()

                            audioData = outputStream.toByteArray()
                            Log.d("AudioData", "Captured ${audioData?.size ?: 0} bytes")
                            audioRecord = null
                        }
                    } else {
                        // Request permission
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        ) {
            Text(if (isRecording) "STOP" else "REC")
        }
        Text("Token: ${token ?: "No token"}")
    }

    // Send audio data when recording stops
    LaunchedEffect(audioData) {
        audioData?.let { data ->
            // Replace with your HTTP request code to send `data`
            sendAudioData(data, token)
        }
    }
}

suspend fun sendAudioData(data: ByteArray, token: String?) {
    // Create an OkHttpClient instance
    val client = OkHttpClient()

    // Prepare the audio file body
    val audioRequestBody = data.toRequestBody(null, 0, data.size)

    // Prepare the MultipartBody containing the audio data and any additional parameters
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("audio", "fart.pcm", audioRequestBody)
        .build()

    // Build the HTTP request
    val request = Request.Builder()
        .url("https://0a57-46-193-4-24.ngrok-free.app/farts") // Replace with your server URL
        .header("Authorization", "Bearer $token")
        .post(requestBody)
        .build()

    // Send the request in a background thread
    withContext(Dispatchers.IO) {
        try {
            val response: Response = client.newCall(request).execute()

            // Log the response (always json)
            Log.d("HTTP", "Response: ${response.body?.string()}")

            response.close()
        } catch (e: Exception) {
            Log.e("HTTP", "Error sending audio data: ${e.message}")
        }
    }
}
