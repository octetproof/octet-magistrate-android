package com.octetproof.magistrate.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.octetproof.magistrate.Magistrate
import com.octetproof.sdk.api.Octet
import com.octetproof.sdk.api.OctetConfig
import com.octetproof.sdk.api.OctetSdk
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

// Magistrate sample (Android) — the one-call attested policy decision:
//   Octet.start  →  Magistrate(sdk).decide(license)  →  Permit / Deny.
// decide() runs the whole exchange in one call: fetch + verify the policy bundle,
// request a single-use challenge, prove the required regions bound to that challenge,
// submit, and return the decision. A deny is a returned result, not an exception.
//
// Register this app on your Octet license; OctetSDK 3.x signs in with hardware
// attestation, so there's no key to paste.
// Run a RELEASE-signed build on a real device: debug-build locations are flagged as mock.

class MainActivity : ComponentActivity() {
    private var sdk: OctetSdk? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var log by mutableStateOf("Tap to run the policy decision (Magistrate.decide).")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        fun line(s: String) { log += "\n$s" }

        fun runDecide() = lifecycleScope.launch {
            try {
                line("\n① Starting OctetSDK…")
                val s = sdk ?: Octet.start(
                    applicationContext,
                    // No licence key: OctetSDK signs in with hardware attestation (or a sandbox token).
                    OctetConfig(),
                ).also { sdk = it }
                val license = s.licenseStatus ?: run {
                    line("   ✗ no licenseStatus — activation didn't complete."); return@launch
                }
                line("   ✓ activated")

                // decide() needs the location pipeline warm enough to mint a fresh proof.
                line("② letting the location pipeline warm up…")
                delay(12_000)

                for (attempt in 1..6) {
                    try {
                        line("③ decide() attempt $attempt…")
                        val r = Magistrate(s).decide(license)
                        line(if (r.isPermit) "   ✓ PERMIT (decision=${r.decision})"
                             else "   ✗ DENY (decision=${r.decision})")
                        if (r.reasons.isNotEmpty()) line("   reasons: ${r.reasons.joinToString("; ")}")
                        if (r.obligations.isNotEmpty()) line("   obligations: ${r.obligations}")
                        line("   decisionRef=${r.decisionRef}")
                        return@launch
                    } catch (e: Throwable) {
                        line("   [$attempt] ${e.javaClass.simpleName}: ${e.message}")
                        if (attempt < 6) delay(15_000)
                    }
                }
                line("✗ couldn't produce a decision — check location permission and network.")
            } catch (e: Throwable) {
                // e.g. PolicyException.NotProvisioned / .LicenseIdUnavailable / .DecideRejected
                line("   ✗ ${e.javaClass.simpleName}: ${e.message}")
            }
        }

        setContent {
            MaterialTheme {
                Surface {
                    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                        Button(onClick = { runDecide() }) { Text("Decide (Magistrate.decide)") }
                        Text(log, Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
