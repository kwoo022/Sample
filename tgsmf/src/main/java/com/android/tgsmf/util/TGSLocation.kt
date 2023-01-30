package com.android.tgsmf.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


object TGSLocation {
    val TAG = javaClass.simpleName

    //---------------------------------------------------------------------------------
    // 위치 정보 접근 권한 확인
    fun checkLocationPermission(_activity: Activity):Boolean {
        if(_activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true

        val baseMultiplePermissionsListener: MultiplePermissionsListener = object :
            MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if(report.areAllPermissionsGranted()) {
                        //showTakePictureFragment()
                    }
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }
        }

        val dialogMultiplePermissionsListener: MultiplePermissionsListener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
            .withContext(_activity)
            .withTitle("위치정보 접근 permission")
            .withMessage("[내 위치 공유] 기능을 사용하기 위해 위치 정보 접근 권한이 필요합니다.")
            .withButtonText("OK")
            //.withIcon(R.drawable.ic_menu_camera)
            .build()

        val compositePermissionsListener: MultiplePermissionsListener = CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, baseMultiplePermissionsListener)
        Dexter.withContext(_activity)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(compositePermissionsListener)
            //.onSameThread()
            .check()

        return false
    }

    //---------------------------------------------------------------------------------
    // 가장최근 위치정보 가져오기
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(_activity: Activity, callback:(lon:Double, lat:Double)-> Unit) {
        val locationManager = _activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(locationManager == null)
            return

        // GPS로 캐싱된 위치가 없다면 Network로 가져옴
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
       // val location: Location? = manager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(location != null) {
            TGSLog.d("[${TAG} _getCurrentLocation] 위치정보 : ${location.provider}, 위도 : ${location.longitude}, 경도 : ${location.latitude}, 고도 : ${location.altitude}")
            callback(location.longitude, location.latitude)
            return
        }

        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다
                TGSLog.d("[${TAG} _getCurrentLocation] 위치정보 : ${location.provider}, 위도 : ${location.longitude}, 경도 : ${location.latitude}, 고도 : ${location.altitude}")

                locationManager.removeUpdates(this)
                callback(location.longitude, location.latitude)
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // 위치정보를 원하는 시간, 거리마다 갱신해준다.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            1000L,
            1f,
            gpsLocationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            1000L,
            1f,
            gpsLocationListener)
    }



}