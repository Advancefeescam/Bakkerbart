package nl.bakkerbart.production

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.Button
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    private val notificationPermissionRequestCode = 1
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        createNotificationChannel()
        checkServerStatus()


        val testNotificationButton: Button = findViewById(R.id.testNotification)
        testNotificationButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    sendTestNotification()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        notificationPermissionRequestCode
                    )
                }
            } else {
                sendTestNotification()
            }
        }
    }

    private fun checkServerStatus() {
        val serverStatusTextView: TextView = findViewById(R.id.serverStatusTextView)


        val request = Request.Builder()
            .url("http://192.168.206.122/register_app/server_status.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    serverStatusTextView.text = "\uD83D\uDD34 Server Offline"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Handler(Looper.getMainLooper()).post {
                    if (response.isSuccessful) {
                        try {
                            val jsonResponse =
                                JSONObject(response.body?.string())
                            val serverStatus =
                                jsonResponse.getString("status")

                            if (serverStatus == "online") {
                                serverStatusTextView.text = "\uD83D\uDFE2 Server Online"
                            } else {
                                serverStatusTextView.text = "\uD83D\uDD34 Server Offline"
                            }
                        } catch (e: Exception) {
                            serverStatusTextView.text = "\uD83D\uDD34 Server Offline"
                        }
                    } else {
                        serverStatusTextView.text = "\uD83D\uDD34 Server Offline"
                    }
                }
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Testnotificaties"
            val descriptionText =
                "Kanaal voor testnotificaties, je kunt deze functie testen door naar instellingen te gaan in de app en op de testnotificatie-knop te drukken."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("test_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTestNotification() {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification = NotificationCompat.Builder(this, "test_channel")
            .setContentTitle("Test-notificatie")
            .setContentText("Dit is een pushmelding om de functionaliteit van pushmeldingen te testen.")
            .setSmallIcon(R.drawable.logo_465x320_1156024572)
            .build()

        notificationManager.notify(0, notification)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == notificationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendTestNotification()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Toegang tot meldingen geblokkeerd")
            .setMessage("Notificatie toestemming is nodig om (push-)meldingen te ontvangen, geef toestemming in je instellingen voor apps.")
            .setPositiveButton("Ga naar instellingen") { dialog, _ ->
                openAppSettings()
                dialog.dismiss()
            }
            .setNegativeButton("Sluit") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
