package com.example.opensource_team6.diet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DietViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserInfoRepository(application.applicationContext)

    private val _userInfo = MutableLiveData<UserInfo?>()
    val userInfo: LiveData<UserInfo?> = _userInfo

    init {
        repository.fetchUserInfo { info ->
            _userInfo.postValue(info)
        }
    }

    data class Result(
        val dailyCalories: Int,
        val carbKcal: Int,
        val carbGram: Int,
        val proteinKcal: Int,
        val proteinGram: Int,
        val fatKcal: Int,
        val fatGram: Int
    )

    fun calculate(goalPeriodDays: Int, activityLevel: Double): Result? {
        val user = _userInfo.value ?: return null
        val bmr = if (user.gender == "male") {
            10 * user.currentWeight + 6.25 * user.height - 5 * user.age + 5
        } else {
            10 * user.currentWeight + 6.25 * user.height - 5 * user.age - 161
        }
        val tdee = bmr * activityLevel
        val totalLoss = (user.currentWeight - user.targetWeight) * 7700
        val dailyCal = tdee - (totalLoss / goalPeriodDays)
        val carbKcal = dailyCal * 0.5
        val proteinKcal = dailyCal * 0.3
        val fatKcal = dailyCal * 0.2
        return Result(
            dailyCal.toInt(),
            carbKcal.toInt(),
            (carbKcal / 4).toInt(),
            proteinKcal.toInt(),
            (proteinKcal / 4).toInt(),
            fatKcal.toInt(),
            (fatKcal / 9).toInt()
        )
    }
}
