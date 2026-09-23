package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.security.BiometricAuthManager
import com.example.data.model.AuthStatus
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.theme.VeilBgGradientEnd
import com.example.ui.theme.VeilBgGradientMid
import com.example.ui.theme.VeilBgGradientStart
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilError
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilOnSurface
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilQuantumCyan
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.VeilViewModel

@Composable
fun AuthScreen(
  viewModel: VeilViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val authStatus by viewModel.authStatus.collectAsState()

  var isSignUp by remember { mutableStateOf(false) }
  var email by remember { mutableStateOf("elena.vance@veil.secure") }
  var password by remember { mutableStateOf("crystalline2026") }
  var displayName by remember { mutableStateOf("Elena Vance") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var localErrorMessage by remember { mutableStateOf<String?>(null) }

  val isLoading = authStatus is AuthStatus.Loading

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
    // Ethereal radial aura
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(350.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color.White.copy(alpha = 0.9f),
              Color.Transparent
            ),
            radius = 700f
          )
        )
    )

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Crystal Emblem & Title
      item {
        Box(
          modifier = Modifier
            .size(76.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.8f))
            .border(1.5.dp, VeilGlassBorder, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_veil_emblem),
            contentDescription = "Veil Crystal Emblem",
            modifier = Modifier.size(48.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Veil",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            fontSize = 32.sp
          ),
          color = VeilPrimary
        )

        Text(
          text = "ZERO-KNOWLEDGE CRYPTOGRAPHIC ENCLAVE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontSize = 10.sp
          ),
          color = VeilSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))
      }

      // Segmented Switcher (Sign In / Create Account)
      item {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color.White.copy(alpha = 0.5f),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VeilGlassBorder, RoundedCornerShape(20.dp))
            .padding(4.dp)
        ) {
          Row(modifier = Modifier.fillMaxWidth()) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (!isSignUp) VeilPrimaryContainer else Color.Transparent)
                .clickable {
                  isSignUp = false
                  localErrorMessage = null
                }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Sign In",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp
                ),
                color = if (!isSignUp) Color.White else VeilSecondary
              )
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSignUp) VeilPrimaryContainer else Color.Transparent)
                .clickable {
                  isSignUp = true
                  localErrorMessage = null
                }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Create Account",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp
                ),
                color = if (isSignUp) Color.White else VeilSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))
      }

      // Input Form Container
      item {
        CrystallineGlassBox(
          shape = RoundedCornerShape(24.dp),
          backgroundColor = Color.White.copy(alpha = 0.72f),
          borderColor = VeilGlassBorder,
          elevation = 2.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            if (isSignUp) {
              OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Operative Name") },
                placeholder = { Text("Elena Vance") },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = VeilPrimary,
                    modifier = Modifier.size(20.dp)
                  )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VeilPrimary,
                  unfocusedBorderColor = VeilGlassBorder,
                  focusedContainerColor = Color.White.copy(alpha = 0.5f),
                  unfocusedContainerColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_auth_name")
              )
            }

            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Encrypted Enclave Email") },
              placeholder = { Text("operative@veil.secure") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Email,
                  contentDescription = null,
                  tint = VeilPrimary,
                  modifier = Modifier.size(20.dp)
                )
              },
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
              ),
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VeilPrimary,
                unfocusedBorderColor = VeilGlassBorder,
                focusedContainerColor = Color.White.copy(alpha = 0.5f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.3f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_email")
            )

            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              label = { Text("Hardware Master Password") },
              placeholder = { Text("••••••••••••") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = null,
                  tint = VeilPrimary,
                  modifier = Modifier.size(20.dp)
                )
              },
              trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                  Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    tint = VeilSecondary,
                    modifier = Modifier.size(20.dp)
                  )
                }
              },
              visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
              ),
              keyboardActions = KeyboardActions(
                onDone = {
                  if (isSignUp) {
                    viewModel.signUpWithEmail(email, password, displayName) { success, err ->
                      if (!success) localErrorMessage = err
                    }
                  } else {
                    viewModel.signInWithEmail(email, password) { success, err ->
                      if (!success) localErrorMessage = err
                    }
                  }
                }
              ),
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VeilPrimary,
                unfocusedBorderColor = VeilGlassBorder,
                focusedContainerColor = Color.White.copy(alpha = 0.5f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.3f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_password")
            )

            // Error display
            val errorText = localErrorMessage ?: (authStatus as? AuthStatus.Error)?.message
            AnimatedVisibility(visible = errorText != null) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = VeilError.copy(alpha = 0.12f),
                modifier = Modifier
                  .fillMaxWidth()
                  .border(0.5.dp, VeilError.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
              ) {
                Text(
                  text = errorText ?: "",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = VeilError,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
              }
            }

            // Primary Submit Button
            Button(
              onClick = {
                localErrorMessage = null
                if (email.isBlank() || password.isBlank()) {
                  localErrorMessage = "Please enter both email and password."
                  return@Button
                }
                if (isSignUp) {
                  viewModel.signUpWithEmail(email, password, displayName) { success, err ->
                    if (!success) localErrorMessage = err
                  }
                } else {
                  viewModel.signInWithEmail(email, password) { success, err ->
                    if (!success) localErrorMessage = err
                  }
                }
              },
              enabled = !isLoading,
              colors = ButtonDefaults.buttonColors(
                containerColor = VeilPrimaryContainer,
                contentColor = VeilOnPrimary
              ),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(2.dp, RoundedCornerShape(18.dp))
                .testTag("btn_auth_submit")
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                  color = Color.White,
                  modifier = Modifier.size(20.dp),
                  strokeWidth = 2.dp
                )
              } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (isSignUp) "Initialize Enclave & Register" else "Authenticate & Decrypt Vault",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))
      }

      // SSO & Enclave Passkey section
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(0.5.dp)
              .background(VeilGlassBorder)
          )
          Text(
            text = "OR ALTERNATIVE ACCESS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.sp,
              letterSpacing = 1.sp
            ),
            color = VeilSecondary,
            modifier = Modifier.padding(horizontal = 12.dp)
          )
          Box(
            modifier = Modifier
              .weight(1f)
              .height(0.5.dp)
              .background(VeilGlassBorder)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Google SSO Button
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = Color.White.copy(alpha = 0.7f),
          shadowElevation = 1.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .border(1.dp, VeilGlassBorder, RoundedCornerShape(18.dp))
            .clickable(enabled = !isLoading) {
              localErrorMessage = null
              viewModel.signInWithGoogle(context) { success, err ->
                if (!success && err != null) localErrorMessage = err
              }
            }
            .testTag("btn_google_signin")
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(VeilPrimary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "G",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Continue with Google SSO",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              ),
              color = VeilOnSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // FIDO2 Hardware Passkey Button
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = Color.White.copy(alpha = 0.7f),
          shadowElevation = 1.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .border(1.dp, VeilGlassBorder, RoundedCornerShape(18.dp))
            .clickable(enabled = !isLoading) {
              localErrorMessage = null
              val fragmentActivity = context as? FragmentActivity
              if (fragmentActivity != null) {
                BiometricAuthManager.promptBiometricUnlock(
                  activity = fragmentActivity,
                  title = "Hardware Passkey Verification",
                  subtitle = "Verify biometric identity for instant Enclave access",
                  description = "Hardware cryptographic passkey verification",
                  onSuccess = {
                    viewModel.signInWithHardwarePasskey { success, err ->
                      if (!success && err != null) localErrorMessage = err
                    }
                  },
                  onError = { _, err ->
                    localErrorMessage = err
                  },
                  onFailed = {
                    localErrorMessage = "Biometric credentials unverified"
                  }
                )
              } else {
                viewModel.signInWithHardwarePasskey { success, err ->
                  if (!success && err != null) localErrorMessage = err
                }
              }
            }
            .testTag("btn_passkey_signin")
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Fingerprint,
              contentDescription = null,
              tint = VeilPrimary,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Hardware Enclave Passkey (Offline)",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              ),
              color = VeilOnSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }

      // Security Protocol Attestation footer
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = VeilEmeraldShield.copy(alpha = 0.08f),
          modifier = Modifier.border(0.5.dp, VeilEmeraldShield.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = VeilEmeraldShield,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (viewModel.isFirebaseConfigured)
                "Firebase Cloud Sync Active • Curve25519 Armored"
              else
                "Local Hardware Isolated Enclave • Zero-Knowledge",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              ),
              color = VeilEmeraldShield
            )
          }
        }
      }
    }
  }
}
