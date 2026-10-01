package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

object SmsHelper {

    private const val CHANNEL_ID = "sms_otp_channel"
    private const val CHANNEL_NAME = "رسائل التحقق SMS"

    fun sendSmsNotification(context: Context, phoneNumber: String, code: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات رموز التحقق SMS لتسجيل الحسابات"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Open SMS app intent if clicked
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", "رمز التحقق لتطبيق حجز كورة: $code")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            code.hashCode(),
            smsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("📩 رسالة SMS: رمز التحقق لتطبيق حجز كورة")
            .setContentText("كود التحقق الخاص برقمك ($phoneNumber) هو: $code")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("كود التحقق الخاص برقمك ($phoneNumber) لتطبيق حجز كورة هو: $code\n\nيرجى إدخال هذا الرمز لإتمام التحقق من هويتك وتأمين حسابك. لا تشاركه مع أي شخص.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(777, notification)
    }

    fun openNativeSmsApp(context: Context, phoneNumber: String, code: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", "كود التحقق لتطبيق حجز كورة: $code")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback generic SMS
            val fallback = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:$phoneNumber?body=كود التحقق: $code")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String, code: String) {
        try {
            val cleanPhone = phoneNumber.filter { it.isDigit() }
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode("كود التحقق لتطبيق حجز كورة هو: $code")}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
