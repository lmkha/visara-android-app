package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.MessageModel
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(500)
            _uiState.update {
                val messages = listOf<IMessageListItem>(
                    MessageItem(data = MessageModel("Kịp không v tr")),
                    MessageItem(data = MessageModel("Đã đông mà tg còn ít nữa vz")),
                    MessageItem(data = MessageModel("Lên chưa á Thương")),
                    MessageItem(data = MessageModel("A5 - 301 nha m")),
                    MessageItem(data = MessageModel("Ok", false)),
                    MessageItem(data = MessageModel("Tới r", false)),
                    MessageItem(data = MessageModel("T mới sửa lại cái API random, với thêm API following videos rồi nha", false)),
                    MessageItem(data = MessageModel("Okok để t pull về")),
                    MessageItem(data = MessageModel("Tuần này làm kịp thêm cái chat với thông báo kịp ko ta")),
                    MessageItem(data = MessageModel("Hông biết nữa, thông báo chắc t sửa kịp, chat vẫn đang mế:v", false)),
                    MessageItem(data = MessageModel("Um v m cứ nghiên cứu thêm phần đó đi ha")),
                    MessageItem(data = MessageModel("Cuối tuần này chắc t vào lại sg để nộp báo cáo thực tập")),
                    MessageItem(data = MessageModel("Ok ok", false)),
                    MessageItem(data = MessageModel("Nếu có chỗ nào cần trao đổi trực tiếp cho tiện thì t7 cn tuần này m ha")),
                    MessageItem(data = MessageModel("Oke bên FCM chắc t cũng cần trao đổi thêm bên m, chứ t ko biết test sao:v", false)),
                    MessageItem(data = MessageModel("Um phần đó test thì bữa giờ t test qua máy ảo các thứ")),
                    MessageItem(data = MessageModel("mà để t nghiên cứu thêm có gì mai t báo lại m ha")),
                    MessageItem(data = MessageModel("Oke", false)),
                    MessageItem(data = MessageModel("t mới tạo riêng 1 project front end nhận được thông báo FCM nha")),
                    MessageItem(data = MessageModel("Khi nào m rảnh hú t vô meet có gì t trao đổi cách để m test cái nhận thông báo nha")),
                    MessageItem(data = MessageModel("Ủa sao hổng tạo nhánh th", false)),
                    MessageItem(data = MessageModel("T tạo project mới luôn cho nó nhẹ á mà kkk")),
                    MessageItem(data = MessageModel("Tối nay cafe hong", false)),
                    MessageItem(data = MessageModel("Gặp nhau cho dễ trao đổi", false)),
                    MessageItem(data = MessageModel("T còn ở quê m ơi")),
                    TimeItem(data = "27/05/2025"),
                    MessageItem(data = MessageModel("sáng t6 t vào lại á")),
                    MessageItem(data = MessageModel("À à dẫy chắc thôi", false)),
                    MessageItem(data = MessageModel("Để t cập nhật postman rồi nhờ m test", false)),
                    MessageItem(data = MessageModel("Chiều t6 hay ngày t7 m rảnh hong")),
                    MessageItem(data = MessageModel("Ngày t7 rảnh", false)),
                    MessageItem(data = MessageModel("Hoặc tối t6 cũng đcc", false)),
                    TimeItem(data = "27/05/2025"),
                    MessageItem(data = MessageModel("T6 t vào r nên 2 ngày đó đc á")),
                    MessageItem(data = MessageModel("M thấy bữa nào oke nhắn t ha")),
                    MessageItem(data = MessageModel("Maf có chỗ nào cần gấp ko tối nay meet trc cũng đc m")),
                    MessageItem(data = MessageModel("Oke để t xem thử", false)),
                )
                it.copy(
                    isLoading = false,
                    messages = messages,
                )
            }
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            _uiState.update { oldState ->
                val newMessage = MessageItem(data = MessageModel(content = content))
                val newMessages = oldState.messages + listOf(newMessage)
                oldState.copy(messages = newMessages, firstTime = false)
            }
        }
    }
}

data class ChatInboxScreenUiState(
    val isLoading: Boolean = false,
    val firstTime: Boolean = true,
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
