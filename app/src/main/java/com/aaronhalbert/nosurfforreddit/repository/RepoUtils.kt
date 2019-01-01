package com.aaronhalbert.nosurfforreddit.repository

import com.aaronhalbert.nosurfforreddit.room.ClickedPostId

class RepoUtils {

    fun getListOfClickedPostIdStrings(input: List<ClickedPostId>): List<String> {
        val list = mutableListOf<String>()

        input.forEach { list.add(it.clickedPostId) }

        return list
    }
}
