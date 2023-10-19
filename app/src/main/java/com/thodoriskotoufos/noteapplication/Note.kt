package com.thodoriskotoufos.noteapplication

import java.time.LocalDate

data class Note(var title: String? = null, var content: String? = null, var datetime: LocalDate?)
