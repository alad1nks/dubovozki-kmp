package com.alad1nks.dubovozki.core.model

data class Bus(
    val id: Int,
    val dayOfWeek: DayOfWeek,
    val dayTime: Int,
    val dayTimeString: String,
    val station: Station,
    val direction: Direction,
) {
    enum class DayOfWeek {
        MONDAY,
        WEEKDAYS,
        SATURDAY,
        SUNDAY,
    }

    enum class Station {
        ODINTSOVO,
        SLAVYANSKY_BULVAR,
        MOLODYOZHNAYA,
    }

    enum class Direction {
        MOSCOW,
        DUBKI,
    }
}
