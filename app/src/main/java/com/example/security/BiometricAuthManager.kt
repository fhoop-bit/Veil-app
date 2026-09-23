package com.example.security

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * BiometricAuthManager provides a secure, hardware-backed authentication layer
 * using AndroidX BiometricPrompt API to unlock the app and encrypted messages.
 */
object BiometricAuthManager {
  private const val TAG = "VeilBiometricAuth"

  enum class BiometricAvailability {
    AVAILABLE,
    NOT_ENROLLED,
    NO_HARDWARE,
    UNAVAILABLE
  }

  /**
   * Checks whether hardware biometric sensors (fingerprint, face, iris)
   * or device secure credentials are ready for authentication.
   */
  fun checkBiometricAvailability(context: Context): BiometricAvailability {
    val biometricManager = BiometricManager.from(context)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
      BiometricManager.Authenticators.DEVICE_CREDENTIAL

    return when (biometricManager.canAuthenticate(authenticators)) {
      BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NO_HARDWARE
      else -> BiometricAvailability.UNAVAILABLE
    }
  }

  /**
   * Displays the native Android BiometricPrompt for unlocking the secure enclave
   * and decrypting messages.
   */
  fun promptBiometricUnlock(
    activity: FragmentActivity,
    title: String = "Unlock Optical Enclave",
    subtitle: String = "Hardware-backed biometric verification",
    description: String = "Authenticate with biometric sensor or system PIN to access encrypted messages",
    onSuccess: () -> Unit,
    onError: (errorCode: Int, errString: String) -> Unit = { _, _ -> },
    onFailed: () -> Unit = {}
  ) {
    try {
      val executor = ContextCompat.getMainExecutor(activity)

      val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
          super.onAuthenticationSucceeded(result)
          Log.d(TAG, "Biometric authentication succeeded")
          onSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
          super.onAuthenticationError(errorCode, errString)
          Log.w(TAG, "Biometric authentication error [$errorCode]: $errString")
          onError(errorCode, errString.toString())
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          Log.w(TAG, "Biometric verification failed (unrecognized biometric)")
          onFailed()
        }
      }

      val biometricPrompt = BiometricPrompt(activity, executor, callback)

      val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setDescription(description)

      // When DEVICE_CREDENTIAL is included, negative button must NOT be set
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        promptInfoBuilder.setAllowedAuthenticators(
          BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
      } else {
        promptInfoBuilder.setAllowedAuthenticators(
          BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
      }

      val promptInfo = promptInfoBuilder.build()
      biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
      Log.e(TAG, "Error displaying BiometricPrompt", e)
      // Fallback: If device credential cannot be used or exception occurs, try with negative button
      tryFallbackPrompt(activity, title, subtitle, description, onSuccess, onError, onFailed)
    }
  }

  private fun tryFallbackPrompt(
    activity: FragmentActivity,
    title: String,
    subtitle: String,
    description: String,
    onSuccess: () -> Unit,
    onError: (errorCode: Int, errString: String) -> Unit,
    onFailed: () -> Unit
  ) {
    try {
      val executor = ContextCompat.getMainExecutor(activity)
      val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
          super.onAuthenticationSucceeded(result)
          onSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
          super.onAuthenticationError(errorCode, errString)
          onError(errorCode, errString.toString())
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          onFailed()
        }
      }

      val prompt = BiometricPrompt(activity, executor, callback)
      val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setDescription(description)
        .setNegativeButtonText("Use Enclave PIN")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .build()

      prompt.authenticate(promptInfo)
    } catch (fallbackError: Exception) {
      Log.e(TAG, "Fallback BiometricPrompt also failed", fallbackError)
      onError(-1, fallbackError.localizedMessage ?: "Biometric prompt unavailable")
    }
  }
}
