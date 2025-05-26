document.addEventListener("DOMContentLoaded", function () {
    const page = document.body;
    const roomId = page.dataset.roomId;
    const myUserId = Number(page.dataset.userId);
    const myNickname = page.dataset.nickname;

    if (!roomId || !myUserId || !myNickname) return;

    const socket = new SockJS("/ws");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        stompClient.subscribe("/topic/chat/" + roomId, (message) => {
            const payload = JSON.parse(message.body);
            const chatBox = document.getElementById("chatBox");

            const div = document.createElement("div");
            div.classList.add("chat-message");
            div.dataset.messageId = payload.id;

            const timeStr = payload.sentAt
                ? new Date(payload.sentAt).toLocaleTimeString("ko-KR", {
                    hour: 'numeric',
                    minute: '2-digit',
                    second: '2-digit',
                    hour12: true
                })
                : "";

            if (payload.senderId === myUserId) {
                div.classList.add("me");
                div.innerHTML = `
                    <strong class="sender">${payload.nickname}</strong>
                    <div class="chat-message-text">${payload.content}</div>
                    <div class="chat-time">${timeStr}</div>
                    <button class="delete-btn">삭제</button>
                `;
            } else {
                div.classList.add("you");
                div.innerHTML = `
                    <strong class="sender">${payload.nickname}</strong>
                    <div class="chat-message-text">${payload.content}</div>
                    <div class="chat-time">${timeStr}</div>
                `;
            }

            chatBox.appendChild(div);
            chatBox.scrollTo({ top: chatBox.scrollHeight, behavior: 'smooth' });
        });
    });

    function sendMessage() {
        const input = document.getElementById("messageInput");
        const content = input.value.trim();
        if (!content) return;

        const payload = {
            senderId: myUserId,
            nickname: myNickname,
            content: content
        };

        stompClient.send("/app/chat.send/" + roomId, {}, JSON.stringify(payload));
        input.value = "";
    }

    document.getElementById("sendBtn").addEventListener("click", sendMessage);

    document.getElementById("messageInput").addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            sendMessage();
        }
    });

    // 메시지 삭제 버튼 클릭 이벤트
    document.getElementById("chatBox").addEventListener("click", function (event) {
        if (event.target.classList.contains("delete-btn")) {
            const messageDiv = event.target.closest(".chat-message");
            const messageId = messageDiv.dataset.messageId;

            if (confirm("이 메시지를 삭제하시겠습니까?")) {
                fetch(`/dm/messages/${messageId}/delete`, {
                    method: "POST"
                }).then(res => {
                    if (res.ok) {
                        messageDiv.querySelector(".chat-message-text").textContent = "[삭제된 메세지]";
                        event.target.remove();
                    } else {
                        alert("삭제 실패");
                    }
                });
            }
        }
    });

    // 채팅방 삭제 버튼
    const deleteRoomBtn = document.getElementById("deleteChatRoomBtn");
    if (deleteRoomBtn) {
        deleteRoomBtn.addEventListener("click", function () {
            if (!confirm("채팅방을 삭제하시겠습니까?")) return;

            fetch(`/dm/chatrooms/${roomId}/delete`, {
                method: "POST"
            }).then(res => {
                if (res.ok) {
                    alert("채팅방이 삭제되었습니다.");
                    location.href = "/dm";
                } else {
                    alert("삭제 실패");
                }
            });
        });
    }
});
