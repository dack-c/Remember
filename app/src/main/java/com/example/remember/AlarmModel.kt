package com.example.remember

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmModel(
    val id:Int,
    val name:String,
    val hour:Int,
    val minute:Int,
    val daysOfWeek:List<Boolean>?,
    val latitude:Double?,
    val longitude:Double?,
    val radius:Double?,
    val enterMode:Boolean?,
    val volume:Double?
    ): Parcelable

/* <activity 간에 객체를 intent로 전달하는 방법>
안드로이드 스튜디오에서 액티비티 간에 객체를 전달하는 방법은 여러 가지가 있습니다. 그 중 하나는 `Intent`를 사용하는 것입니다. 아래는 코틀린 코드 예제입니다:

```kotlin
// 첫 번째 액티비티에서
val intent = Intent(this, SecondActivity::class.java)
val person = Person("John Doe", 30) // 전달하려는 객체
intent.putExtra("person_key", person)
startActivity(intent)

// 두 번째 액티비티에서
val person = intent.getParcelableExtra<Person>("person_key")
```

위의 코드에서 `Person` 클래스는 `Parcelable` 인터페이스를 구현해야 합니다. 이는 안드로이드 시스템이 객체를 전달할 수 있도록 객체의 데이터를 '포장'하는 역할을 합니다.

```kotlin
@Parcelize
data class Person(val name: String, val age: Int) : Parcelable
```

`@Parcelize` 어노테이션은 코틀린 Android Extensions 플러그인의 일부로, `Parcelable` 구현을 자동화합니다.

 */