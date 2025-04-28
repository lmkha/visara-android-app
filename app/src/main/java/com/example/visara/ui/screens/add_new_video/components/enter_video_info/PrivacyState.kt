package com.example.visara.ui.screens.add_new_video.components.enter_video_info

import com.example.visara.R
import com.example.visara.data.model.Privacy

enum class PrivacyState(val label: String, val iconId: Int, val detailInfo: String, val value: Privacy) {
    ALL(
        label = "Everyone",
        iconId = R.drawable.public_24px,
        detailInfo = "Everyone can search and see this video",
        value = Privacy.ALL,
    ),
    FOLLOWER(
        label = "Followers",
        iconId = R.drawable.group_24px,
        detailInfo = "Users who are following you can see this video",
        value = Privacy.FOLLOWER,
    ),
    ONLY_ME(
        label = "Only me",
        iconId = R.drawable.lock_24px,
        detailInfo = "Only me can see this video",
        value = Privacy.ONLY_ME,
    );
}
