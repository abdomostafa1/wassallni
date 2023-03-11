package com.wassallni.data.datasource

import com.wassallni.data.model.Trip
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MainDataSource @Inject constructor(){

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val state = _state.asStateFlow()

    suspend fun getTrips() {
        _state.emit(MainUiState.Loading)
        delay(15000)
        _state.emit(MainUiState.Success(list))
    }

    val list = listOf(
        Trip(
            23987492,
            "كوبري المشاه طريق صلاح سالم",
            "التوحيد والنور شارع الحجاز",
            1677857400,
            1677860100,
            5.5
        ),
        Trip(
            23987492,
            "الوسطي",
            "شرق النيل",
            1677862800,
            1677865500,
            10.0
        ),
        Trip(
            23987492,
            "العيور",
            "ببا",
            1677873600,
            1677876300,
            5.5
        ),
        Trip(
            23987492,
            "تعليم صناعي",
            "الواسطي",
            1677880800,
            1677884400,
            11.0
        ),
        Trip(
            23987492,
            "اهناسيا المدينة",
            "العبور",
            1677857400,
            1677860100,
            5.5
        ),
        Trip(
            23987492,
            "جامعة النهضة",
            "الفشن",
            1677857400,
            1677860100,
            10.0
        ),
        Trip(
            23987492,
            "بني سويف",
            "الواسطي",
            1677857400,
            1677860100,
            5.5
        ),
        Trip(
            23987492,
            "مركز ناصر",
            "كلية الهندسة والتربية",
            1677857400,
            1677860100,
            10.0
        ),
        Trip(23987492,
            "بني سويف",
            "شرق النيل المحافظة",
            1677857400,
            1677860100,
            5.5
        ),
        Trip(23987492,
            "شارع البنوك",
            "صلاح سالم",
            1677857400,
            1677860100,
            5.5
        )

    )

}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val trips: List<Trip>) : MainUiState()
    data class Error(val errorMsg: String) : MainUiState()
}