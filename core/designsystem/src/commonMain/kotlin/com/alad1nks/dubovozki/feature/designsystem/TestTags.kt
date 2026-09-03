package com.alad1nks.dubovozki.feature.designsystem

object TestTags {
    const val APP_CONTENT = "app.content"
    const val NAVIGATION_BAR = "app.navigation.bottom"
    const val NAVIGATION_RAIL = "app.navigation.rail"
    const val NAV_SERVICES = "nav.services"
    const val NAV_SCHEDULE = "nav.schedule"
    const val NAV_SETTINGS = "nav.settings"

    const val BUS_FILTER_STATION = "bus.filter.station"
    const val BUS_FILTER_DAY = "bus.filter.day"
    const val BUS_REFRESH = "bus.refresh"
    const val BUS_TAB_MOSCOW = "bus.tab.moscow"
    const val BUS_TAB_DUBKI = "bus.tab.dubki"
    const val BUS_PAGER = "bus.pager"
    const val BUS_EMPTY = "bus.empty"
    const val BUS_RESET_FILTERS = "bus.reset_filters"
    const val BUS_REMINDER_DIALOG = "bus.reminder.dialog"
    const val BUS_REMINDER_MINUTES = "bus.reminder.minutes"
    const val BUS_REMINDER_SET = "bus.reminder.set"

    const val SERVICES_REFRESH = "services.refresh"
    const val SERVICES_LINEN = "services.linen"
    const val SERVICES_CONTACT = "services.contact"
    const val SERVICES_DONATE = "services.donate"
    const val SERVICES_LINKS_UNAVAILABLE = "services.links_unavailable"

    const val SERVICE_SCHEDULE_BACK = "service_schedule.back"
    const val SERVICE_SCHEDULE_REFRESH = "service_schedule.refresh"
    const val SERVICE_SCHEDULE_BUILDING_1 = "service_schedule.building.1"
    const val SERVICE_SCHEDULE_BUILDING_2 = "service_schedule.building.2"
    const val SERVICE_SCHEDULE_BUILDING_3 = "service_schedule.building.3"
    const val SERVICE_SCHEDULE_EMPTY = "service_schedule.empty"

    const val SETTINGS_THEME = "settings.theme"
    const val SETTINGS_LANGUAGE = "settings.language"

    const val COMMON_LOADING = "common.loading"
    const val COMMON_ERROR = "common.error"
    const val COMMON_RETRY = "common.retry"
    const val COMMON_OFFLINE = "common.offline"

    fun bus(id: Int) = "bus.item.$id"

    fun stationFilter(value: String) = "bus.filter.station.${value.lowercase()}"

    fun dayFilter(value: String) = "bus.filter.day.${value.lowercase()}"

    fun theme(value: String) = "settings.theme.${value.lowercase()}"

    fun language(value: String) = "settings.language.${value.lowercase()}"

    fun currentTheme(value: String) = "settings.theme.current.${value.lowercase()}"

    fun currentLanguage(value: String) = "settings.language.current.${value.lowercase()}"

    fun appTheme(isDark: Boolean) = "app.theme.${if (isDark) "dark" else "light"}"

    fun serviceScheduleDay(
        day: Int,
        isToday: Boolean,
    ) = "service_schedule.day.$day.${if (isToday) "today" else "regular"}"
}
