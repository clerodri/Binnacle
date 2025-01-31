package com.clerodri.binnacle.location.domain

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation():Location?
}