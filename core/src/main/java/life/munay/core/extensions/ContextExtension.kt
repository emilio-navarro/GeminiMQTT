package life.munay.core.extensions

import android.content.Context

fun Context.getApplicationVersionInfo(): String =
    runCatching {
        packageManager.getPackageInfo(packageName, 0).let { packageInfo ->
            "v${packageInfo.versionName} (${packageInfo.longVersionCode})"
        }
    }.getOrDefault("")
