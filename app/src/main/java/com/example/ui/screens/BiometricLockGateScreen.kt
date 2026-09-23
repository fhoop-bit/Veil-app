package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.theme.VeilBgGradientEnd
import com.example.ui.theme.VeilBgGradientMid
import com.example.ui.theme.VeilBgGradientStart
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.VeilViewModel

/**
 * BiometricLockGateScreen provides a secure, hardware-backed authentication barrier
 * that seals encrypted messages until verified by Android BiometricPrompt or hardware PIN.
 */
@Composable
fun BiometricLockGateScreen(
  viewModel: VeilViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? FragmentActivity
  val securityState by viewModel.securityState.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  // Auto-prompt biometrics when gate appears
  LaunchedEffect(Unit) {
    if (activity != null && securityState.biometricAuthEnabled) {
      viewModel.triggerBiometricUnlock(
        activity = activity,
        title = "Unlock Veil Optical Enclave",
        subtitle = "Biometric hardware authentication required",
        onDismissOrError = { /* Stay on gate screen */ }
      )
    }
  }

  // Pulsing animation for fingerprint aura
  val infiniteTransition = rememberInfiniteTransition(label = "fingerprint_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

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
      .padding(horizontal = 24.dp, vertical = 32.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Top Security Shield Badge
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, VeilGlassBorder),
        shadowElevation = 0.5.dp
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = VeilEmeraldShield,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "HARDWARE ENCLAVE LOCKED",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              fontSize = 11.sp
            ),
            color = VeilEmeraldShield
          )
        }
      }

      Spacer(modifier = Modifier.height(36.dp))

      // Glowing Biometric Sensor Target
      Box(
        modifier = Modifier
          .size(128.dp)
          .scale(pulseScale)
          .shadow(16.dp, CircleShape, spotColor = VeilPrimary.copy(alpha = 0.25f))
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFE2E8F0).copy(alpha = 0.8f)
              )
            )
          )
          .border(2.5.dp, VeilPrimary.copy(alpha = 0.4f), CircleShape)
          .clickable {
            if (activity != null) {
              viewModel.triggerBiometricUnlock(activity)
            } else {
              viewModel.authenticateWithBiometrics()
            }
          }
          .testTag("btn_biometric_gate_sensor"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Fingerprint,
          contentDescription = "Biometric Sensor",
          tint = VeilPrimary,
          modifier = Modifier.size(68.dp)
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "Veil Optical Enclave",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.5).sp,
          fontSize = 24.sp
        ),
        color = VeilPrimary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      val operativeName = currentUser?.displayName ?: "Operative"
      Text(
        text = "Welcome, $operativeName. Hardware cryptographic authentication is required to decrypt messages and access keys.",
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 13.sp,
          lineHeight = 19.sp
        ),
        color = VeilSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(36.dp))

      // Main Biometric Unlock CTA Button
      Button(
        onClick = {
          if (activity != null) {
            viewModel.triggerBiometricUnlock(activity)
          } else {
            viewModel.authenticateWithBiometrics()
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = VeilPrimaryContainer,
          contentColor = VeilOnPrimary
        ),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_biometric_gate_unlock")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Fingerprint,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Unlock with Biometrics",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Fallback PIN button
      Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, VeilGlassBorder),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .clickable { viewModel.openEnclaveModal() }
          .testTag("btn_biometric_gate_pin")
      ) {
        Row(
          modifier = Modifier.fillMaxSize(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Pin,
            contentDescription = null,
            tint = VeilPrimary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Enter Optical PIN Keypad",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp
            ),
            color = VeilPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Hardware Security Specs Indicator
      CrystallineGlassBox(
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = VeilSecondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Biometric Sensor: Strong / Keystore",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = VeilSecondary
            )
          }
          Text(
            text = "FIPS 140-3",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            color = VeilPrimary
          )
        }
      }
    }
  }
}
