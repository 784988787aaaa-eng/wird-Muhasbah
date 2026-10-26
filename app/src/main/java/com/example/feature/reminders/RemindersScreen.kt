package com.example.feature.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdFilterChipRow
import com.example.core.designsystem.components.WirdSecondaryButton
import com.example.core.designsystem.theme.WirdDimensionTokens
import com.example.domain.model.ReminderCategory
import com.example.domain.model.UserPreferences

object WirdNotificationDispatcher {
    private const val CHANNEL_ID = "wird_gentle_reminders_channel"
    private const val CHANNEL_NAME = "تنبيهات وِرد الهادئة"
    private const val CHANNEL_DESC = "تذكيرات اختيارية لطيفة للصلاة والأذكار وأعمال الخير دون إلحاح"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
                enableVibration(false)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendGentlePreviewNotification(context: Context, category: ReminderCategory): Boolean {
        ensureChannel(context)
        if (!hasNotificationPermission(context)) return false
        return runCatching {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("وِرد • ${category.arabicTitle}")
                .setContentText(category.arabicSubtitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context).notify(3100 + category.ordinal, notification)
            true
        }.getOrDefault(false)
    }

    fun cancelAllReminders(context: Context) {
        runCatching {
            NotificationManagerCompat.from(context).cancelAll()
        }
    }
}

/**
 * Matches the «التنبيهات» screen in the bottom row of the Visual Reference Poster:
 * - Master Quiet Mode switch («وضع السكون / إيقاف جميع التذكيرات»)
 * - Independent switches & time selectors for:
 *   1. تذكير الصلوات
 *   2. الأذكار اليومية
 *   3. أعمال الخير والقرآن
 *   4. ختام اليوم
 * - Contextual permission explanation & graceful handling when POST_NOTIFICATIONS is denied.
 */
@Composable
fun RemindersScreen(
    preferences: UserPreferences,
    onUpdateQuietMode: (Boolean) -> Unit,
    onUpdateReminder: (ReminderCategory, Boolean, String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }
    var pendingCategoryToEnable by remember { mutableStateOf<ReminderCategory?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val targetCat = pendingCategoryToEnable
        if (granted && targetCat != null) {
            permissionDeniedMessage = null
            val currentItem = preferences.reminders[targetCat]
            onUpdateReminder(targetCat, true, currentItem?.timeHHmm ?: targetCat.defaultTime)
        } else if (!granted) {
            permissionDeniedMessage = "لم يتم منح إذن الإشعارات؛ ستظل جميع ميزات التطبيق وسجلاتك تعمل بشكل طبيعي، ويمكنك تفعيل الإذن لاحقًا متى شئت."
        }
        pendingCategoryToEnable = null
    }

    val presetTimes = listOf("05:30", "07:00", "12:30", "17:00", "20:30", "21:30")

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("reminders_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = WirdDimensionTokens.MaxContentWidth),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("reminders_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "رجوع"
                        )
                    }
                    Text(
                        text = "التنبيهات والتذكيرات الهادئة",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Contextual Permission Denied Banner (Non-blocking, respectful)
            if (permissionDeniedMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("permission_denied_info_banner")
                    ) {
                        Text(
                            text = permissionDeniedMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            // Master Quiet Mode («السكون الافتراضي / إيقاف شامل»)
            item {
                WirdCard(
                    containerColor = if (preferences.quietModeAllRemindersOff) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    modifier = Modifier.testTag("quiet_mode_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (preferences.quietModeAllRemindersOff) {
                                    Icons.Outlined.NotificationsOff
                                } else {
                                    Icons.Outlined.NotificationsActive
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "وضع السكون الشامل",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "إيقاف جميع الإشعارات والتذكيرات بلمسة واحدة دون فقدان إعداداتك",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = preferences.quietModeAllRemindersOff,
                            onCheckedChange = { quiet ->
                                if (quiet) {
                                    WirdNotificationDispatcher.cancelAllReminders(context)
                                }
                                onUpdateQuietMode(quiet)
                            },
                            modifier = Modifier.testTag("quiet_mode_master_switch")
                        )
                    }
                }
            }

            // Individual Reminder Categories (Matches «التنبيهات» Screen in Reference Poster)
            items(ReminderCategory.entries, key = { it.key }) { category ->
                val itemPref = preferences.reminders[category]
                val isEnabled = itemPref?.isEnabled == true && !preferences.quietModeAllRemindersOff
                val selectedTime = itemPref?.timeHHmm ?: category.defaultTime

                WirdCard(
                    modifier = Modifier.testTag("reminder_card_${category.key}")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.arabicTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = category.arabicSubtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Switch(
                                checked = isEnabled,
                                enabled = !preferences.quietModeAllRemindersOff,
                                onCheckedChange = { wantEnable ->
                                    if (wantEnable) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                            !WirdNotificationDispatcher.hasNotificationPermission(context)
                                        ) {
                                            pendingCategoryToEnable = category
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            onUpdateReminder(category, true, selectedTime)
                                        }
                                    } else {
                                        onUpdateReminder(category, false, selectedTime)
                                    }
                                },
                                modifier = Modifier.testTag("reminder_switch_${category.key}")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "التوقيت المفضل: $selectedTime",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag("reminder_time_label_${category.key}")
                            )

                            if (isEnabled) {
                                TextButton(
                                    onClick = {
                                        WirdNotificationDispatcher.sendGentlePreviewNotification(context, category)
                                    },
                                    modifier = Modifier.testTag("test_reminder_btn_${category.key}")
                                ) {
                                    Text("معاينة التنبيه الهادئ")
                                }
                            }
                        }

                        WirdFilterChipRow(
                            options = presetTimes.take(4).map { it to it },
                            selectedKey = selectedTime,
                            onSelect = { newTime ->
                                onUpdateReminder(category, itemPref?.isEnabled ?: false, newTime)
                            },
                            tagPrefix = "reminder_time_chip_${category.key}"
                        )
                    }
                }
            }

            item {
                WirdSecondaryButton(
                    text = "إيقاف ومسح جميع الإشعارات النشطة الآن",
                    onClick = {
                        WirdNotificationDispatcher.cancelAllReminders(context)
                        onUpdateQuietMode(true)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    tag = "cancel_all_notifications_button"
                )
            }
        }
    }
}
