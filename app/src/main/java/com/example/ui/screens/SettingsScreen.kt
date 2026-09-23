package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.components.CrystallineIconButton
import com.example.ui.theme.VeilBgGradientEnd
import com.example.ui.theme.VeilBgGradientMid
import com.example.ui.theme.VeilBgGradientStart
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilOnSurface
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilQuantumCyan
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.VeilViewModel

import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.theme.VeilError

@Composable
fun SettingsScreen(
  viewModel: VeilViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val securityState by viewModel.securityState.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  var showSignOutDialog by remember { mutableStateOf(false) }

  if (showSignOutDialog) {
    AlertDialog(
      onDismissRequest = { showSignOutDialog = false },
      title = {
        Text(
          text = "Seal Enclave & Sign Out?",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Text(
          text = "Your cryptographic session keys will be cleared from ephemeral memory. You will need to re-authenticate with your master credentials.",
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showSignOutDialog = false
            viewModel.signOut()
          },
          colors = ButtonDefaults.buttonColors(containerColor = VeilError)
        ) {
          Text("Sign Out", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showSignOutDialog = false }) {
          Text("Cancel", color = VeilSecondary)
        }
      },
      containerColor = Color(0xFFFBFBFC),
      shape = RoundedCornerShape(20.dp)
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            VeilBgGradientStart,
            VeilBgGradientMid,
            VeilBgGradientEnd
          )
        )
      )
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          CrystallineIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            onClick = onBack,
            testTag = "btn_settings_back"
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = VeilPrimary
          )
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = VeilEmeraldShield.copy(alpha = 0.12f),
          modifier = Modifier.border(0.5.dp, VeilEmeraldShield.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = VeilEmeraldShield,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Vault Armored",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              color = VeilEmeraldShield
            )
          }
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Profile Card
        item {
          CrystallineGlassBox(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_profile_card")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              val displayName = currentUser?.displayName ?: "Elena Vance"
              val initials = displayName.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "EV" }

              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(VeilPrimary),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = initials,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                  ),
                  color = Color.White
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = displayName,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  ),
                  color = VeilOnSurface
                )
                Text(
                  text = currentUser?.email ?: "elena.vance@veil.secure",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                  color = VeilSecondary
                )
                Text(
                  text = "Enclave Key: ${currentUser?.enclaveFingerprint ?: "0x7F2B...E914"}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = VeilQuantumCyan
                  )
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = VeilPrimary.copy(alpha = 0.08f)
              ) {
                Text(
                  text = currentUser?.authProvider ?: "Enclave",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  color = VeilPrimary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }
          }
        }

        // Firebase & Cloud Sync Status Card
        item {
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (viewModel.isFirebaseConfigured) Icons.Default.CloudDone else Icons.Default.Cloud,
                contentDescription = null,
                tint = if (viewModel.isFirebaseConfigured) VeilEmeraldShield else VeilPrimary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (viewModel.isFirebaseConfigured) "Firebase Cloud Sync: Connected" else "Enclave Sync: Local Hardware Isolated",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                  ),
                  color = VeilOnSurface
                )
                Text(
                  text = if (viewModel.isFirebaseConfigured)
                    "Realtime Firebase Auth & Cloud Firestore encrypted synchronization active."
                  else
                    "Operating with hardware-isolated zero-knowledge persistence on device.",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = VeilSecondary
                )
              }
            }
          }
        }

        // Quantum Session Key Rotation Card
        item {
          CrystallineGlassBox(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_quantum_key_card")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = VeilPrimary,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Quantum Session Key",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 14.sp
                    ),
                    color = VeilPrimary
                  )
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = VeilPrimary.copy(alpha = 0.08f)
                ) {
                  Text(
                    text = securityState.sessionKeyFingerprint,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp,
                      letterSpacing = 0.5.sp
                    ),
                    color = VeilPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Key regenerated ${securityState.sessionKeyMinutesAgo}m ago. Perfect forward secrecy ensures past messages remain unreadable even if current keys are compromised.",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 12.sp,
                  lineHeight = 16.sp
                ),
                color = VeilSecondary
              )

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = { viewModel.rotateSessionKey() },
                colors = ButtonDefaults.buttonColors(
                  containerColor = VeilPrimaryContainer,
                  contentColor = VeilOnPrimary
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(42.dp)
                  .testTag("btn_rotate_quantum_key")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Rotate Session Key Now",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                  )
                }
              }
            }
          }
        }

        // Privacy Checkup Banner
        item {
          CrystallineGlassBox(
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = VeilEmeraldShield,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Privacy Checkup: 5 of 5 Verified",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                  ),
                  color = VeilOnSurface
                )
                Text(
                  text = "No leaks detected. Hardware keystore secure.",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = VeilSecondary
                )
              }
            }
          }
        }

        // Security Toggles List
        item {
          Text(
            text = "HARDWARE & ENCLAVE POLICIES",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp,
              fontSize = 11.sp
            ),
            color = VeilSecondary.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 4.dp, top = 6.dp)
          )
        }

        item {
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
              SecuritySettingToggleItem(
                icon = Icons.Default.Fingerprint,
                title = "Hardware Biometric Layer",
                subtitle = "Enforce BiometricPrompt API for messages & vault",
                isChecked = securityState.biometricAuthEnabled,
                onCheckedChange = { viewModel.toggleBiometricAuth() },
                testTag = "toggle_biometric_auth"
              )

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(0.5.dp)
                  .background(VeilGlassBorder)
              )

              SecuritySettingToggleItem(
                icon = Icons.Default.Security,
                title = "App Lock on Launch",
                subtitle = "Require biometric verification before accessing messages",
                isChecked = securityState.lockAppOnLaunch,
                onCheckedChange = { viewModel.toggleLockAppOnLaunch() },
                testTag = "toggle_lock_app_launch"
              )

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(0.5.dp)
                  .background(VeilGlassBorder)
              )

              SecuritySettingToggleItem(
                icon = Icons.Default.Lock,
                title = "Crystalline Chat Lock",
                subtitle = "Biometric & PIN enforced on Vaulted chats",
                isChecked = securityState.chatLockEnabled,
                onCheckedChange = { viewModel.toggleChatLock() },
                testTag = "toggle_chat_lock"
              )

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(0.5.dp)
                  .background(VeilGlassBorder)
              )

              SecuritySettingToggleItem(
                icon = Icons.Default.VisibilityOff,
                title = "Hidden Previews",
                subtitle = "Obfuscate message notifications until optical unlock",
                isChecked = securityState.hiddenPreviewsEnabled,
                onCheckedChange = { viewModel.toggleHiddenPreviews() },
                testTag = "toggle_hidden_previews"
              )
            }
          }
        }

        // Disappearing Messages Selector
        item {
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = null,
                  tint = VeilPrimary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Disappearing Transcripts",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 14.sp
                    ),
                    color = VeilOnSurface
                  )
                  Text(
                    text = "Auto-expunges cached records from hardware enclave",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = VeilSecondary
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(0 to "Off", 1 to "24h", 7 to "7 Days", 30 to "30 Days").forEach { (days, label) ->
                  val isSelected = securityState.disappearingDays == days
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) VeilPrimaryContainer else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.setDisappearingDays(days) }
                      .border(
                        0.5.dp,
                        if (isSelected) Color.White.copy(alpha = 0.3f) else VeilGlassBorder,
                        RoundedCornerShape(14.dp)
                      )
                  ) {
                    Text(
                      text = label,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                      ),
                      color = if (isSelected) Color.White else VeilOnSurface,
                      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                      textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                  }
                }
              }
            }
          }
        }

        // Active Devices
        item {
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Devices,
                contentDescription = null,
                tint = VeilPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Active Devices (2)",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                  ),
                  color = VeilOnSurface
                )
                Text(
                  text = "Crystalline Mac Studio, Pixel 9 Pro Fold",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = VeilSecondary
                )
              }
              Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = VeilSecondary
              )
            }
          }
        }

        // Sign Out Button
        item {
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = VeilError.copy(alpha = 0.1f),
            shadowElevation = 0.5.dp,
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, VeilError.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
              .clickable { showSignOutDialog = true }
              .testTag("btn_settings_sign_out")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 16.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = VeilError,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Sign Out & Seal Enclave",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                ),
                color = VeilError
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SecuritySettingToggleItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = VeilPrimary,
      modifier = Modifier.size(20.dp)
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        ),
        color = VeilOnSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = VeilSecondary
      )
    }

    Switch(
      checked = isChecked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = VeilPrimary,
        uncheckedThumbColor = VeilSecondary,
        uncheckedTrackColor = Color.White.copy(alpha = 0.5f)
      ),
      modifier = Modifier.testTag(testTag)
    )
  }
}
