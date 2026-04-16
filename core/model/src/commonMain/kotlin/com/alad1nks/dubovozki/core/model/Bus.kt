package com.alad1nks.dubovozki.core.model

data class Bus(
    val id: Int,
    val dayOfWeek: DayOfWeek,
    val dayTime: Long,
    val dayTimeString: String,
    val station: Station,
    val direction: Direction,
) {
    enum class DayOfWeek {
        MONDAY,
        WEEKDAY,
        SATURDAY,
        SUNDAY,
    }

    enum class Station {
        ODINTSOVO,
        SLAVYANKA,
        MOLODYOZHKA,
    }

    enum class Direction {
        MOSCOW,
        DUBKI,
    }
}
