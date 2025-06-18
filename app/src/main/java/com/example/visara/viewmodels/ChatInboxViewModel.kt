package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.ChatMessageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatInboxViewModel @Inject constructor(
) : ViewModel() {
    private val _uiState: MutableStateFlow<ChatInboxScreenUiState> = MutableStateFlow(ChatInboxScreenUiState())
    val uiState: StateFlow<ChatInboxScreenUiState> = _uiState.asStateFlow()

    init {
        observerNewMessage()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(500)
            _uiState.update {
                val messages: List<IMessageListItem> = listOf(
                    MessageItem(data = ChatMessageModel(content = "Kịp không v tr", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Đã đông mà tg còn ít nữa vz", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Lên chưa á Thương", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "A5 - 301 nha m", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Ok", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Tới r", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "T mới sửa lại cái API random, với thêm API following videos rồi nha", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Okok để t pull về", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Tuần này làm kịp thêm cái chat với thông báo kịp ko ta", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Hông biết nữa, thông báo chắc t sửa kịp, chat vẫn đang mế:v", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Um v m cứ nghiên cứu thêm phần đó đi ha", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Cuối tuần này chắc t vào lại sg để nộp báo cáo thực tập", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Ok ok", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Nếu có chỗ nào cần trao đổi trực tiếp cho tiện thì t7 cn tuần này m ha", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Oke bên FCM chắc t cũng cần trao đổi thêm bên m, chứ t ko biết test sao:v", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Um phần đó test thì bữa giờ t test qua máy ảo các thứ", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "mà để t nghiên cứu thêm có gì mai t báo lại m ha", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Oke", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "t mới tạo riêng 1 project front end nhận được thông báo FCM nha", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Khi nào m rảnh hú t vô meet có gì t trao đổi cách để m test cái nhận thông báo nha", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Ủa sao hổng tạo nhánh th", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "T tạo project mới luôn cho nó nhẹ á mà kkk", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Tối nay cafe hong", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Gặp nhau cho dễ trao đổi", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "T còn ở quê m ơi", senderUsername = "lmkha")),
                    TimeItem(data = "27/05/2025"),
                    MessageItem(data = ChatMessageModel(content = "sáng t6 t vào lại á", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "À à dẫy chắc thôi", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Để t cập nhật postman rồi nhờ m test", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Chiều t6 hay ngày t7 m rảnh hong", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Ngày t7 rảnh", senderUsername = "quythuong")),
                    MessageItem(data = ChatMessageModel(content = "Hoặc tối t6 cũng đcc", senderUsername = "quythuong")),
                    TimeItem(data = "27/05/2025"),
                    MessageItem(data = ChatMessageModel(content = "T6 t vào r nên 2 ngày đó đc á", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "M thấy bữa nào oke nhắn t ha", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Maf có chỗ nào cần gấp ko tối nay meet trc cũng đc m", senderUsername = "lmkha")),
                    MessageItem(data = ChatMessageModel(content = "Oke để t xem thử", senderUsername = "quythuong")),
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
        }
    }

    fun setPartnerUsername(username: String) {
        viewModelScope.launch {
        }
    }

    fun clearPartnerUsername() {
        viewModelScope.launch {
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            _uiState.update { oldState ->
                val newMessage = MessageItem(
                    data = ChatMessageModel(
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
    val data: ChatMessageModel,
) : IMessageListItem

interface IMessageListItem {
    val type: MessageListItemType
}

enum class MessageListItemType {
    MESSAGE,
    TIME,
}
