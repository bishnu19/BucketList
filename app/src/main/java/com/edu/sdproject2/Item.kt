package com.edu.sdproject2

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s):
 * Description: App 02 - Item (model) class
 */

import java.util.*

class Item(
    var id: Int,
    var description: String,
    var creationDate: Date,
    var updateDate: Date,
    status: Int): Comparable<Item> {

    var status = if (status in 0..2) status else SCHEDULED
        set(value) {
            if (status in 0..2)
                field = value
        }

    companion object {
        const val SCHEDULED = 0
        const val COMPLETED = 1
        const val ARCHIVED  = 2

        val STATUS_DESCRIPTIONS = arrayOf("scheduled", "completed", "archived")
    }

    fun statusAsString(): String {
        return STATUS_DESCRIPTIONS[status];
    }

    override fun compareTo(other: Item): Int {
        if (status == other.status)
            return creationDate.compareTo(other.creationDate)
        return status - other.status
    }
}