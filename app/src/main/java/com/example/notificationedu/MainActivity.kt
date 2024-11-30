package com.example.notificationedu

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.notificationedu.ui.theme.NotificationEduTheme
import com.example.notificationedu2.DetailActivity
import com.example.notificationedu2.SecondActivity
import com.example.notificationedu2.SettingActivity


class MainActivity : ComponentActivity() {

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted){
            Toast.makeText(applicationContext, "알림호출", Toast.LENGTH_SHORT).show()
        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)){
                AlertDialog.Builder(this)
                    .setTitle("권한 요청")
                    .setMessage("푸시알림 권한이 필요합니다.")
                    .setPositiveButton("확인") { _, _ ->
                        permissionRequest()
                    }
                    .setNegativeButton("취소") { _, _ ->
                        Toast.makeText(applicationContext, "사용 불가", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }else {
                Toast.makeText(applicationContext, "설정에서 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val channelID = "CodingDiary"
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        setContent {
            NotificationEduTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ){
                        Button(
                            onClick = {
                                permissionRequest()
                            }
                        ) {
                            Text(
                                text = "알림호출"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun permissionRequest(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }else {
                Toast.makeText(applicationContext, "알림호출", Toast.LENGTH_SHORT).show()
                showNotification()
            }
        }else {
            Toast.makeText(applicationContext, "알림호출", Toast.LENGTH_SHORT).show()
            showNotification()
        }
    }

    private fun showNotification(){
        val secondIntent = Intent(applicationContext, SecondActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            secondIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val detailIntent = Intent(applicationContext, DetailActivity::class.java)
        val detailPendingIntent = PendingIntent.getActivity(
            this,
            0,
            detailIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val action2 = NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "DetailActivity", detailPendingIntent).build()

        val settingIntent = Intent(applicationContext, SettingActivity::class.java)
        val settingPendingIntent = PendingIntent.getActivity(
            this,
            0,
            settingIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val action3 = NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "SettingActivity", settingPendingIntent).build()

        val remoteInput = RemoteInput.Builder("KEY").run {
            setLabel("메세지를 입력해 주세요")
            build()
        }
        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "Reply", pendingIntent).addRemoteInput(remoteInput).build()

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Test Notification")
            .setContentText("This is a test notification")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
            .addAction(action2)
            .addAction(action3)
            .addAction(replyAction)
            .build()

        notificationManager?.notify(100, notification)
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                channelID,
                "Notification",
                importance
            ).apply {
                description = "CodingDiary Notification Test"
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
