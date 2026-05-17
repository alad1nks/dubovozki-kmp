package com.alad1nks.dubovozki.core.model

data class ServicesSchedule(
    val firstBuildingSchedule: List<ServicesScheduleItem>,
    val secondBuildingSchedule: List<ServicesScheduleItem>,
    val thirdBuildingSchedule: List<ServicesScheduleItem>,
)
