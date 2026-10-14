package cl.duoc.tacticaltrack.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fine || coarse
}

@SuppressLint("MissingPermission")
fun requestCurrentLocation(
    context: Context,
    onSuccess: (Location) -> Unit,
    onError: (String) -> Unit
) {
    if (!hasLocationPermission(context)) {
        onError("Se requiere permiso de ubicación.")
        return
    }

    val client = LocationServices.getFusedLocationProviderClient(context)
    val token = CancellationTokenSource()

    client.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        token.token
    ).addOnSuccessListener { location ->
        if (location != null) {
            onSuccess(location)
        } else {
            onError("No fue posible obtener la ubicación. Activa el GPS e inténtalo nuevamente.")
        }
    }.addOnFailureListener {
        onError(it.message ?: "Error al obtener la ubicación.")
    }
}
