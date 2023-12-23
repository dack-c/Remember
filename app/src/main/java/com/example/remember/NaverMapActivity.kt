package com.example.remember

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
//import android.os.Build.VERSION_CODES.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.remember.databinding.ActivityNaverMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import java.util.Locale

class NaverMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var naverMap: NaverMap
    private var marker: Marker? = null
    private var selectedCoord: LatLng? = null
    private val circles = mutableListOf<CircleOverlay>()
    private var radius: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNaverMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "위치 설정하기"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            naverMap.locationSource = object : LocationSource {
                override fun activate(listener: LocationSource.OnLocationChangedListener) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            location?.let {
                                val currentPosition = LatLng(it.latitude, it.longitude)
                                val marker = Marker()
                                marker.position = currentPosition
                                marker.map = naverMap

                                val cameraUpdate = CameraUpdate.scrollTo(currentPosition)
                                naverMap.moveCamera(cameraUpdate)

                            }
                        }

                }

                override fun deactivate() {
                }
            }

            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_navigation, menu)
        return true
    }

    // 위도와 경도를 한글로 변환하는 함수
    private fun getKoreanAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.KOREA)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (addresses?.isNotEmpty() == true && addresses[0].getAddressLine(0) != null) {
            addresses[0].getAddressLine(0)
        } else {
            "주소를 찾을 수 없습니다."
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> {
                if (selectedCoord != null) {
                    val koreanAddress = getKoreanAddress(selectedCoord!!.latitude, selectedCoord!!.longitude)
                    // 클릭한 위치의 위도와 경도를 담은 Intent 생성
                    val intent = Intent(this, AlarmSettingActivity::class.java)
                    //intent.putExtra("latitude", selectedCoord!!.latitude)
                    //intent.putExtra("longitude", selectedCoord!!.longitude)
                    val extras = Bundle()

                    // 번들에 위도, 경도 정보 추가
                    selectedCoord?.let {
                        extras.putDouble("latitude", it.latitude)
                        extras.putDouble("longitude", it.longitude)
                    }
                    //intent.putExtra("koreanAddress", koreanAddress)
                    //intent.putExtra("radius", radius)
                    extras.putString("koreanAddress", koreanAddress)
                    extras.putDouble("radius", radius)

                    intent.putExtras(extras)

                    //intent전달 확인용도
                    val latitude = selectedCoord!!.latitude
                    val longitude = selectedCoord!!.longitude
                    val message = "위도: $latitude, 경도: $longitude, 반경: $radius, 위치: $koreanAddress"
                    //val message = "위치: $koreanAddress, 반경: $radius"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    // AlarmSettingActivity로 Intent 전달
                    startActivity(intent)
                    return true
                } else {
                    Toast.makeText(this, "위치를 선택하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            android.R.id.home -> {
                // 뒤로 가기 버튼 클릭 시 처리할 내용
                val intent = Intent(this, AlarmSettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 다이얼로그를 띄울 함수
    private fun showRadiusInputDialog(coord: LatLng) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setTitle("반경 입력")
        builder.setPositiveButton("확인") { _, _ ->
            val radiusString = input.text.toString()
            radius = radiusString.toDoubleOrNull() ?: 0.0
            if (radius != null && radius > 0) {
                // 이전 원 지우기
                circles.forEach { it.map = null }
                circles.clear()
                // 반경 값이 유효하면 마커와 함께 원 추가
                val circle = CircleOverlay()
                circle.center = coord
                circle.radius = radius
                circle.color = Color.argb(100, 255, 0, 0) // 원의 색 지정
                circle.map = naverMap

                circles.add(circle)
                selectedCoord = coord

                // 클릭한 위치에 마커 추가
                marker?.map = null
                marker = Marker()
                marker?.position = coord
                marker?.map = naverMap
            } else {
                Toast.makeText(this, "올바른 숫자를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        var locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        enableMyLocation()

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        naverMap.setOnMapClickListener { point, coord ->
            // 클릭한 위치의 위도와 경도 가져와서 Toast 메시지로 표시
            val latLngString = "위도: ${coord.latitude}, 경도: ${coord.longitude}"
            Toast.makeText(this, latLngString, Toast.LENGTH_SHORT).show()

            // 원반경 입력 다이얼로그 호출
            showRadiusInputDialog(coord)
        }
        // 사용자가 현재 위치 아이콘을 탭하여 이동할 수 있도록 지원하는 버튼 활성화
        naverMap.uiSettings.isLocationButtonEnabled = true

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
//dialog custom, toolbar custom, 현재위치 오버레이