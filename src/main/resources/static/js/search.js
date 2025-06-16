document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.querySelector(".search-toggle");
    const panel = document.getElementById("searchPanel");
    const sidebar = document.querySelector(".sidebar");
    const form = document.querySelector(".search-form");

    const userList = document.getElementById("searchUserList");
    const postList = document.getElementById("searchPostList");

    // ✅ 검색창 열기 / 닫기
    if (toggle) {
        toggle.addEventListener("click", () => {
            const isOpen = panel.classList.contains("show");

          if (isOpen) {
              panel.classList.remove("show");
              setTimeout(() => panel.classList.add("hidden"), 300);
              window.expandSidebar();   // 사이드바 원상복구 함수 호출
          } else {
              panel.classList.remove("hidden");
              panel.classList.add("show");
              window.collapseSidebar(); // 사이드바 축소 함수 호출
          }
        });
    }

    // ✅ ESC 키로 패널 닫기
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && panel.classList.contains("show")) {
            panel.classList.remove("show");
            setTimeout(() => panel.classList.add("hidden"), 300);
            window.expandSidebar();
        }
    });

    // ✅ 외부 클릭 시 닫기
    document.addEventListener("click", (e) => {
        if (panel.classList.contains("show") && !panel.contains(e.target) && !toggle.contains(e.target)) {
            panel.classList.remove("show");
            setTimeout(() => panel.classList.add("hidden"), 300);
            window.expandSidebar();
        }
    });
    // ✅ 검색 submit 이벤트
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const keyword = form.q.value.trim();
            if (!keyword) return;

            // 이전 결과 초기화
            userList.innerHTML = "";
            postList.innerHTML = "";

            try {
                const postRes = await fetch(`/api/search/posts?q=${encodeURIComponent(keyword)}`);
                const posts = await postRes.json();

                const userRes = await fetch(`/api/search/users?q=${encodeURIComponent(keyword)}`);
                const users = await userRes.json();

                if (posts.length === 0 && users.length === 0) {
                    userList.innerHTML = "<li>검색 결과가 없습니다.</li>";
                    postList.innerHTML = "<div>검색 결과가 없습니다.</div>";
                    return;
                }

                // ✅ 사용자 출력
                if (users.length > 0) {
                    users.forEach(user => {
                        const li = document.createElement("li");
                        const link = document.createElement("a");
                        link.href = `/profile/${user.userId}`;
                        link.textContent = `${user.name} (${user.email})`;
                        li.appendChild(link);
                        userList.appendChild(li);
                    });

                } else {
                    userList.innerHTML = "<li>사용자 결과가 없습니다.</li>";
                }

                // ✅ 게시물 출력 (이미지 썸네일 포함 정사각형, 링크 포함)
                if (posts.length > 0) {
                    const gridContainer = document.createElement("div");
                    gridContainer.classList.add("search-post-grid");

                    posts.forEach(post => {
                        const link = document.createElement("a");
                        link.href = `/profile/${post.userId}#post-${post.postId}`;
                        link.classList.add("search-post-item");

                        link.innerHTML = `
                            <img src="${escapeHtml(post.thumbnailUrl)}" alt="썸네일">
                            <div class="caption">${escapeHtml(post.content)}</div>
                        `;
                        gridContainer.appendChild(link);
                    });

                    postList.appendChild(gridContainer);
                } else {
                    postList.innerHTML = "<div>게시물 결과가 없습니다.</div>";
                }

            } catch (error) {
                console.error("검색 오류:", error);
                userList.innerHTML = "<li>검색 중 오류 발생</li>";
                postList.innerHTML = "<div>검색 중 오류 발생</div>";
            }
        });
    }

    function escapeHtml(str) {
        if (typeof str !== 'string') return '';
        return str.replace(/[&<>"]|'/g, function (match) {
            return {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#039;'
            }[match];
        });
    }

});
