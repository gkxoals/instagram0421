document.addEventListener("DOMContentLoaded", function () {
    const page = document.body;
    const roomId = page.dataset.roomId;
    const myUserId = Number(page.dataset.userId);
    const myNickname = page.dataset.nickname;

    if (!roomId || !myUserId || !myNickname) return;

    const input = document.getElementById("messageInput");
    const socket = new SockJS("/ws");
    const stompClient = Stomp.over(socket);

    // ✅ 자동 높이 조절
    input.addEventListener("input", () => {
        input.style.height = "auto";
        input.style.height = input.scrollHeight + "px";
    });

    stompClient.connect({}, () => {
        stompClient.subscribe("/topic/chat/" + roomId, (message) => {
            const payload = JSON.parse(message.body);
            const chatBox = document.getElementById("chatBox");

            const wrapper = document.createElement("div");
            wrapper.classList.add("chat-message", payload.senderId === myUserId ? "me" : "you");
            wrapper.dataset.messageId = payload.id;

            const messageWrapper = document.createElement("div");
            messageWrapper.classList.add("message-wrapper");

            if (payload.senderId === myUserId && !payload.deleted) {
                const menuWrapper = document.createElement("div");
                menuWrapper.classList.add("message-menu-outside");

                const menuBtn = document.createElement("button");
                menuBtn.classList.add("menu-btn");
                menuBtn.type = "button";
                menuBtn.textContent = "⋯";

                const dropdown = document.createElement("div");
                dropdown.classList.add("menu-dropdown", "hidden");

                const deleteBtn = document.createElement("button");
                deleteBtn.classList.add("delete-btn");
                deleteBtn.type = "button";
                deleteBtn.textContent = "삭제";

                dropdown.appendChild(deleteBtn);
                menuWrapper.appendChild(menuBtn);
                menuWrapper.appendChild(dropdown);
                messageWrapper.appendChild(menuWrapper);
            }

            const bubble = document.createElement("div");
            bubble.classList.add("message-bubble");

            if (payload.senderId !== myUserId) {
                const nickname = document.createElement("strong");
                nickname.textContent = payload.nickname;
                bubble.appendChild(nickname);
            }

            const messageText = document.createElement("div");
            messageText.classList.add("chat-message-text");
            messageText.textContent = payload.content;
            bubble.appendChild(messageText);

            const time = document.createElement("div");
            time.classList.add("chat-time");
            time.textContent = payload.sentAt ? new Date(payload.sentAt).toLocaleTimeString("ko-KR", {
                hour: 'numeric',
                minute: '2-digit',
                second: '2-digit',
                hour12: true
            }) : "";
            bubble.appendChild(time);

            messageWrapper.appendChild(bubble);
            wrapper.appendChild(messageWrapper);
            chatBox.appendChild(wrapper);
            chatBox.scrollTo({ top: chatBox.scrollHeight, behavior: 'smooth' });
        });
    });

    function sendMessage() {
        const content = input.value.trim();
        if (!content) return;

        const payload = {
            senderId: myUserId,
            nickname: myNickname,
            content: content
        };

        stompClient.send("/app/chat.send/" + roomId, {}, JSON.stringify(payload));
        input.value = "";
        input.style.height = "auto"; // 높이 초기화
    }

    document.getElementById("sendBtn").addEventListener("click", sendMessage);

    input.addEventListener("keypress", function (e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    document.getElementById("chatBox").addEventListener("click", function (event) {
        if (event.target.classList.contains("menu-btn")) {
            const menu = event.target.nextElementSibling;
            menu.classList.toggle("hidden");
            document.querySelectorAll(".menu-dropdown").forEach(otherMenu => {
                if (otherMenu !== menu) otherMenu.classList.add("hidden");
            });
        }

        if (event.target.classList.contains("delete-btn")) {
            const messageDiv = event.target.closest(".chat-message");
            const messageId = messageDiv.dataset.messageId;

            if (confirm("이 메시지를 삭제하시겠습니까?")) {
                fetch(`/dm/messages/${messageId}/delete`, {
                    method: "POST"
                }).then(res => {
                    if (res.ok) {
                        messageDiv.querySelector(".chat-message-text").textContent = "[삭제된 메세지]";
                        const menuWrapper = event.target.closest(".message-menu") || event.target.closest(".message-menu-outside");
                        if (menuWrapper) menuWrapper.remove();
                    } else {
                        alert("삭제 실패");
                    }
                });
            }
        }
    });

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
