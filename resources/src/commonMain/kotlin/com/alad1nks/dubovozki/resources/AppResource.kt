package com.alad1nks.dubovozki.resources

import dubovozki.resources.generated.resources.Res
import dubovozki.resources.generated.resources.bus_schedule_day_of_week_filter_saturday
import dubovozki.resources.generated.resources.bus_schedule_day_of_week_filter_sunday
import dubovozki.resources.generated.resources.bus_schedule_day_of_week_filter_today
import dubovozki.resources.generated.resources.bus_schedule_day_of_week_filter_tomorrow
import dubovozki.resources.generated.resources.bus_schedule_day_of_week_filter_weekdays
import dubovozki.resources.generated.resources.bus_schedule_departed_time
import dubovozki.resources.generated.resources.bus_schedule_departed_time_with_hour
import dubovozki.resources.generated.resources.bus_schedule_duration_hours
import dubovozki.resources.generated.resources.bus_schedule_duration_minutes
import dubovozki.resources.generated.resources.bus_schedule_empty_supporting
import dubovozki.resources.generated.resources.bus_schedule_empty_title
import dubovozki.resources.generated.resources.bus_schedule_go_to_next
import dubovozki.resources.generated.resources.bus_schedule_navigation_bar_label
import dubovozki.resources.generated.resources.bus_schedule_next_departure
import dubovozki.resources.generated.resources.bus_schedule_no_more_today
import dubovozki.resources.generated.resources.bus_schedule_now
import dubovozki.resources.generated.resources.bus_schedule_reset_filters
import dubovozki.resources.generated.resources.bus_schedule_station_filter_all
import dubovozki.resources.generated.resources.bus_schedule_station_filter_molodyozhnaya
import dubovozki.resources.generated.resources.bus_schedule_station_filter_odintsovo
import dubovozki.resources.generated.resources.bus_schedule_station_filter_slavyansky_bulvar
import dubovozki.resources.generated.resources.bus_schedule_station_molodyozhnaya
import dubovozki.resources.generated.resources.bus_schedule_station_odintsovo
import dubovozki.resources.generated.resources.bus_schedule_station_slavyansky_bulvar
import dubovozki.resources.generated.resources.bus_schedule_tab_dubki
import dubovozki.resources.generated.resources.bus_schedule_tab_moscow
import dubovozki.resources.generated.resources.bus_schedule_time_ago
import dubovozki.resources.generated.resources.bus_schedule_time_until
import dubovozki.resources.generated.resources.bus_schedule_upcoming_time
import dubovozki.resources.generated.resources.bus_schedule_upcoming_time_with_hour
import dubovozki.resources.generated.resources.common_back
import dubovozki.resources.generated.resources.common_error_supporting
import dubovozki.resources.generated.resources.common_error_title
import dubovozki.resources.generated.resources.common_loading
import dubovozki.resources.generated.resources.common_offline_cached
import dubovozki.resources.generated.resources.common_offline_updated_at
import dubovozki.resources.generated.resources.common_refresh
import dubovozki.resources.generated.resources.common_retry
import dubovozki.resources.generated.resources.common_updated_at
import dubovozki.resources.generated.resources.services_contact_headline
import dubovozki.resources.generated.resources.services_contact_supporting
import dubovozki.resources.generated.resources.services_donut_headline
import dubovozki.resources.generated.resources.services_donut_supporting
import dubovozki.resources.generated.resources.services_linen_room_headline
import dubovozki.resources.generated.resources.services_linen_room_supporting
import dubovozki.resources.generated.resources.services_link_error
import dubovozki.resources.generated.resources.services_links_unavailable
import dubovozki.resources.generated.resources.services_navigation_bar_label
import dubovozki.resources.generated.resources.services_schedule_building_1
import dubovozki.resources.generated.resources.services_schedule_building_2
import dubovozki.resources.generated.resources.services_schedule_building_3
import dubovozki.resources.generated.resources.services_schedule_day_of_week_friday
import dubovozki.resources.generated.resources.services_schedule_day_of_week_monday
import dubovozki.resources.generated.resources.services_schedule_day_of_week_saturday
import dubovozki.resources.generated.resources.services_schedule_day_of_week_sunday
import dubovozki.resources.generated.resources.services_schedule_day_of_week_thursday
import dubovozki.resources.generated.resources.services_schedule_day_of_week_tuesday
import dubovozki.resources.generated.resources.services_schedule_day_of_week_wednesday
import dubovozki.resources.generated.resources.services_schedule_empty
import dubovozki.resources.generated.resources.services_top_app_bar
import dubovozki.resources.generated.resources.settings_language
import dubovozki.resources.generated.resources.settings_language_english
import dubovozki.resources.generated.resources.settings_language_kazakh
import dubovozki.resources.generated.resources.settings_language_russian
import dubovozki.resources.generated.resources.settings_language_system
import dubovozki.resources.generated.resources.settings_navigation_bar_label
import dubovozki.resources.generated.resources.settings_theme
import dubovozki.resources.generated.resources.settings_theme_dark
import dubovozki.resources.generated.resources.settings_theme_light
import dubovozki.resources.generated.resources.settings_theme_system
import dubovozki.resources.generated.resources.settings_top_app_bar

object AppResource {
    object String {
        val common_back = Res.string.common_back
        val common_error_supporting = Res.string.common_error_supporting
        val common_error_title = Res.string.common_error_title
        val common_loading = Res.string.common_loading
        val common_offline_updated_at = Res.string.common_offline_updated_at
        val common_offline_cached = Res.string.common_offline_cached
        val common_retry = Res.string.common_retry
        val common_refresh = Res.string.common_refresh
        val common_updated_at = Res.string.common_updated_at
        val bus_schedule_empty_supporting = Res.string.bus_schedule_empty_supporting
        val bus_schedule_empty_title = Res.string.bus_schedule_empty_title
        val bus_schedule_go_to_next = Res.string.bus_schedule_go_to_next
        val bus_schedule_next_departure = Res.string.bus_schedule_next_departure
        val bus_schedule_no_more_today = Res.string.bus_schedule_no_more_today
        val bus_schedule_now = Res.string.bus_schedule_now
        val bus_schedule_reset_filters = Res.string.bus_schedule_reset_filters
        val bus_schedule_time_ago = Res.string.bus_schedule_time_ago
        val bus_schedule_time_until = Res.string.bus_schedule_time_until
        val bus_schedule_day_of_week_filter_saturday = Res.string.bus_schedule_day_of_week_filter_saturday
        val bus_schedule_day_of_week_filter_sunday = Res.string.bus_schedule_day_of_week_filter_sunday
        val bus_schedule_day_of_week_filter_today = Res.string.bus_schedule_day_of_week_filter_today
        val bus_schedule_day_of_week_filter_tomorrow = Res.string.bus_schedule_day_of_week_filter_tomorrow
        val bus_schedule_day_of_week_filter_weekdays = Res.string.bus_schedule_day_of_week_filter_weekdays
        val bus_schedule_departed_time = Res.string.bus_schedule_departed_time
        val bus_schedule_departed_time_with_hour = Res.string.bus_schedule_departed_time_with_hour
        val bus_schedule_navigation_bar_label = Res.string.bus_schedule_navigation_bar_label
        val bus_schedule_station_filter_all = Res.string.bus_schedule_station_filter_all
        val bus_schedule_station_filter_molodyozhnaya = Res.string.bus_schedule_station_filter_molodyozhnaya
        val bus_schedule_station_filter_odintsovo = Res.string.bus_schedule_station_filter_odintsovo
        val bus_schedule_station_filter_slavyansky_bulvar = Res.string.bus_schedule_station_filter_slavyansky_bulvar
        val bus_schedule_station_molodyozhnaya = Res.string.bus_schedule_station_molodyozhnaya
        val bus_schedule_station_odintsovo = Res.string.bus_schedule_station_odintsovo
        val bus_schedule_station_slavyansky_bulvar = Res.string.bus_schedule_station_slavyansky_bulvar
        val bus_schedule_tab_dubki = Res.string.bus_schedule_tab_dubki
        val bus_schedule_tab_moscow = Res.string.bus_schedule_tab_moscow
        val bus_schedule_upcoming_time = Res.string.bus_schedule_upcoming_time
        val bus_schedule_upcoming_time_with_hour = Res.string.bus_schedule_upcoming_time_with_hour
        val services_contact_headline = Res.string.services_contact_headline
        val services_contact_supporting = Res.string.services_contact_supporting
        val services_donut_headline = Res.string.services_donut_headline
        val services_donut_supporting = Res.string.services_donut_supporting
        val services_linen_room_headline = Res.string.services_linen_room_headline
        val services_linen_room_supporting = Res.string.services_linen_room_supporting
        val services_navigation_bar_label = Res.string.services_navigation_bar_label
        val services_schedule_building_1 = Res.string.services_schedule_building_1
        val services_schedule_building_2 = Res.string.services_schedule_building_2
        val services_schedule_building_3 = Res.string.services_schedule_building_3
        val services_schedule_day_of_week_monday = Res.string.services_schedule_day_of_week_monday
        val services_schedule_day_of_week_tuesday = Res.string.services_schedule_day_of_week_tuesday
        val services_schedule_day_of_week_wednesday = Res.string.services_schedule_day_of_week_wednesday
        val services_schedule_day_of_week_thursday = Res.string.services_schedule_day_of_week_thursday
        val services_schedule_day_of_week_friday = Res.string.services_schedule_day_of_week_friday
        val services_schedule_day_of_week_saturday = Res.string.services_schedule_day_of_week_saturday
        val services_schedule_day_of_week_sunday = Res.string.services_schedule_day_of_week_sunday
        val services_top_app_bar = Res.string.services_top_app_bar
        val services_link_error = Res.string.services_link_error
        val services_links_unavailable = Res.string.services_links_unavailable
        val services_schedule_empty = Res.string.services_schedule_empty
        val settings_language = Res.string.settings_language
        val settings_language_english = Res.string.settings_language_english
        val settings_language_kazakh = Res.string.settings_language_kazakh
        val settings_language_russian = Res.string.settings_language_russian
        val settings_language_system = Res.string.settings_language_system
        val settings_navigation_bar_label = Res.string.settings_navigation_bar_label
        val settings_top_app_bar = Res.string.settings_top_app_bar
        val settings_theme = Res.string.settings_theme
        val settings_theme_dark = Res.string.settings_theme_dark
        val settings_theme_light = Res.string.settings_theme_light
        val settings_theme_system = Res.string.settings_theme_system
    }

    object Plural {
        val bus_schedule_duration_hours = Res.plurals.bus_schedule_duration_hours
        val bus_schedule_duration_minutes = Res.plurals.bus_schedule_duration_minutes
    }
}
