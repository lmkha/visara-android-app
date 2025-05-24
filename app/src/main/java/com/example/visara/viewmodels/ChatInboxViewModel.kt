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
            delay(1000)
            _uiState.update {
                val messages = listOf<MessageModel>(
                    MessageModel("Kịp không v tr"),
                    MessageModel("Đã đông mà tg còn ít nữa vz"),
                    MessageModel("Lên chưa á Thương"),
                    MessageModel("A5 - 301 nha m"),
                    MessageModel("Ok", false),
                    MessageModel("Tới r", false),
                    MessageModel("T mới sửa lại cái API random, với thêm API following videos rồi nha", false),
                    MessageModel("Okok để t pull về"),
                    MessageModel("Tuần này làm kịp thêm cái chat với thông báo kịp ko ta"),
                    MessageModel("Hông biết nữa, thông báo chắc t sửa kịp, chat vẫn đang mế:v", false),
                    MessageModel("Um v m cứ nghiên cứu thêm phần đó đi ha"),
                    MessageModel("Cuối tuần này chắc t vào lại sg để nộp báo cáo thực tập"),
                    MessageModel("Ok ok", false),
                    MessageModel("Nếu có chỗ nào cần trao đổi trực tiếp cho tiện thì t7 cn tuần này m ha"),
                    MessageModel(
                        "Oke bên FCM chắc t cũng cần trao đổi thêm bên m, chứ t ko biết test sao:v",
                        false
                    ),
                    MessageModel("Um phần đó test thì bữa giờ t test qua máy ảo các thứ"),
                    MessageModel("mà để t nghiên cứu thêm có gì mai t báo lại m ha"),
                    MessageModel("Oke", false),
                    MessageModel("t mới tạo riêng 1 project front end nhận được thông báo FCM nha"),
                    MessageModel("Khi nào m rảnh hú t vô meet có gì t trao đổi cách để m test cái nhận thông báo nha"),
                    MessageModel("Ủa sao hổng tạo nhánh th", false),
                    MessageModel("T tạo project mới luôn cho nó nhẹ á mà kkk"),
                    MessageModel("Tối nay cafe hong", false),
                    MessageModel("Gặp nhau cho dễ trao đổi", false),
                    MessageModel("T còn ở quê m ơi"),
                    MessageModel("sáng t6 t vào lại á"),
                    MessageModel("À à dẫy chắc thôi", false),
                    MessageModel("Để t cập nhật postman rồi nhờ m test", false),
                    MessageModel("Chiều t6 hay ngày t7 m rảnh hong"),
                    MessageModel("Ngày t7 rảnh", false),
                    MessageModel("Hoặc tối t6 cũng đcc", false),
                    MessageModel("T6 t vào r nên 2 ngày đó đc á"),
                    MessageModel("M thấy bữa nào oke nhắn t ha"),
                    MessageModel("Maf có chỗ nào cần gấp ko tối nay meet trc cũng đc m"),
                    MessageModel("Oke để t xem thử", false),
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
                val newMessage = MessageModel(content = content)
                val newMessages = oldState.messages + listOf(newMessage)
                oldState.copy(messages = newMessages, firstTime = false)
            }
        }
    }
}

data class ChatInboxScreenUiState(
    val isLoading: Boolean = false,
    val firstTime: Boolean = true,
    val messages: List<MessageModel> = emptyList(),
)
