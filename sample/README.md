# Magistrate sample — Android (public)

A minimal Jetpack Compose app that runs the one-call attested policy decision:
`Octet.start` → `Magistrate(sdk).decide(license)` → Permit / Deny. Application id
**`com.octetproof.magistrate`**. Mirrored to `octet-magistrate-android` (`main/sample`)
on each release.

Tap **Decide** and the app starts OctetSDK, lets the location pipeline warm up, then
calls `Magistrate.decide` and shows the decision, reasons, obligations, and
`decisionRef`.

## Run

1. Register the app on your Octet license. OctetSDK signs in with hardware
   attestation, so there's no key to paste: the sample starts it with `OctetConfig()`.
2. Build a **release-signed** APK and run it on a **real device** — debug-build
   locations are flagged as mock and won't produce a usable proof; grant location when
   prompted.
3. Pick the published version with `-PmagistrateVersion=x.y.z`. It resolves
   `com.octetproof:magistrate` (and `com.octetproof:sdk` transitively) from the
   `mvn-repo` Maven source declared in `settings.gradle.kts`. Requires `minSdk 30`.

See [`INTEGRATION.md`](../INTEGRATION.md)
for the full API.
