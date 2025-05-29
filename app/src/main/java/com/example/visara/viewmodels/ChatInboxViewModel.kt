package com.example.visara.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.MessageModel
import com.example.visara.data.repository.InboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatInboxViewModel @Inject constructor(
    private val inboxRepository: InboxRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ChatInboxScreenUiState> = MutableStateFlow(ChatInboxScreenUiState())
    val uiState: StateFlow<ChatInboxScreenUiState> = _uiState.asStateFlow()

    init {
        observerNewMessage()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(500)
            _uiState.update {
                val messages = listOf<IMessageListItem>(
                    MessageItem(data = MessageModel(content = "Kịp không v tr", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Đã đông mà tg còn ít nữa vz", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Lên chưa á Thương", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "A5 - 301 nha m", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Ok", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Tới r", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "T mới sửa lại cái API random, với thêm API following videos rồi nha", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Okok để t pull về", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Tuần này làm kịp thêm cái chat với thông báo kịp ko ta", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Hông biết nữa, thông báo chắc t sửa kịp, chat vẫn đang mế:v", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Um v m cứ nghiên cứu thêm phần đó đi ha", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Cuối tuần này chắc t vào lại sg để nộp báo cáo thực tập", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Ok ok", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Nếu có chỗ nào cần trao đổi trực tiếp cho tiện thì t7 cn tuần này m ha", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Oke bên FCM chắc t cũng cần trao đổi thêm bên m, chứ t ko biết test sao:v", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Um phần đó test thì bữa giờ t test qua máy ảo các thứ", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "mà để t nghiên cứu thêm có gì mai t báo lại m ha", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Oke", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "t mới tạo riêng 1 project front end nhận được thông báo FCM nha", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Khi nào m rảnh hú t vô meet có gì t trao đổi cách để m test cái nhận thông báo nha", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Ủa sao hổng tạo nhánh th", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "T tạo project mới luôn cho nó nhẹ á mà kkk", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Tối nay cafe hong", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Gặp nhau cho dễ trao đổi", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "T còn ở quê m ơi", senderUsername = "lmkha")),
                    TimeItem(data = "27/05/2025"),
                    MessageItem(data = MessageModel(content = "sáng t6 t vào lại á", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "À à dẫy chắc thôi", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Để t cập nhật postman rồi nhờ m test", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Chiều t6 hay ngày t7 m rảnh hong", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Ngày t7 rảnh", senderUsername = "quythuong")),
                    MessageItem(data = MessageModel(content = "Hoặc tối t6 cũng đcc", senderUsername = "quythuong")),
                    TimeItem(data = "27/05/2025"),
                    MessageItem(data = MessageModel(content = "T6 t vào r nên 2 ngày đó đc á", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "M thấy bữa nào oke nhắn t ha", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Maf có chỗ nào cần gấp ko tối nay meet trc cũng đc m", senderUsername = "lmkha")),
                    MessageItem(data = MessageModel(content = "Oke để t xem thử", senderUsername = "quythuong")),
                )
                it.copy(
                    isLoading = false,
                    messages = messages,
                )
            }
        }
    }

    private fun observerNewMessage() {
        viewModelScope.launch {
            inboxRepository.newMessage.collectLatest {
                Log.i("CHECK_VAR", "collect in viewmodel, new message: $it")
            }
        }
    }

    fun setPartnerUsername(username: String) {
        viewModelScope.launch {
            inboxRepository.setActiveChatPartner(username)
            _uiState.update { it.copy(partnerUsername = username) }
        }
    }

    fun clearPartnerUsername() {
        viewModelScope.launch {
            inboxRepository.clearActiveChatPartner()
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            _uiState.update { oldState ->
                val newMessage = MessageItem(
                    data = MessageModel(
                        content = content,
                        senderUsername = "lmkha",
                    )
                )
                val newMessages = oldState.messages + listOf(newMessage)
                oldState.copy(messages = newMessages, firstTime = false)
            }
        }
    }

    fun isSentByCurrentUser(messageItem: IMessageListItem?) : Boolean {
        if (messageItem == null) return false
        if (messageItem.type != MessageListItemType.MESSAGE) return false
        val message = messageItem as MessageItem
        return message.data.senderUsername == "lmkha"
    }
}

data class ChatInboxScreenUiState(
    val isLoading: Boolean = false,
    val firstTime: Boolean = true,
    val partnerUsername: String = "",
    val messages: List<IMessageListItem> = emptyList(),
)

data class TimeItem(
    override val type: MessageListItemType = MessageListItemType.TIME,
    val data: String,
) : IMessageListItem

data class MessageItem(
    override val type: MessageListItemType = MessageListItemType.MESSAGE,
    val data: MessageModel,
) : IMessageListItem

interface IMessageListItem {
    val type: MessageListItemType
}

enum class MessageListItemType {
    MESSAGE,
    TIME,
}
