$(document).ready(function () {

    let notificationsMarkedAsRead = false; //최초 알림 읽음 처리했는지 여부 알려주는 놈

    // ✅ 알림 패널 토글 (사이드바 + 로고 반응형 포함)
    $("#notification-bell").on("click", function (e) {
        e.preventDefault();
        const panel = $("#notificationPanel");
        const sidebar = $(".sidebar");

        // 이미 열려있으면 닫기만
        if (panel.hasClass("show")) {
            panel.removeClass("show");
            setTimeout(() => panel.addClass("hidden"), 300);

            updateLogoImage(false);
            $("#logoImage").css("opacity", "0");
            sidebar.removeClass("force-compact");
            setTimeout(() => sidebar.addClass("animate-text"), 200);
            setTimeout(() => $("#logoImage").css("opacity", "1"), 400);
            return;
        }

        if (!notificationsMarkedAsRead) {
            $.post("/notifications/mark-read", function () {
                notificationsMarkedAsRead = true;
                fetchNotificationCount(); // 알림 배지도 갱신
            });
        }

        // 알림 HTML을 Ajax로 불러오기
        $.get("/notifications", function (html) {
            const newContent = $(html).find("#notificationPanel").html();
            $("#notificationPanel").html(newContent).removeClass("hidden").addClass("show");

            sidebar.addClass("force-compact").removeClass("animate-text");
            updateLogoImage(true);
            $("#logoImage").css("opacity", "1");


        });
    });

    // ✅ 알림 항목 클릭 시 읽음 처리 후 이동
    $(document).on("click", ".notification-item", function () {
        const $item = $(this);
        const href = $item.data("href"); // data-href에 저장된 URL을 읽어옵니다.

        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: `/notifications/${$item.data("id")}/read`,
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function () {
                // 데이터 처리 후, 해당 게시물로 이동합니다.
                if (href) {
                    window.location.href = href; // 댓글 위치가 포함된 data-href로 이동
                }
            },
            error: function (err) {
                console.error("❌ 읽음 처리 실패:", err);
            }
        });
    });

    // ✅ 바깥 클릭 시 알림 패널 닫고 사이드바 복구
    $(document).on("click", function (e) {
        const panel = $("#notificationPanel")[0];
        const bell = $("#notification-bell")[0];
        const sidebar = $(".sidebar");

        if (!panel.contains(e.target) && !bell.contains(e.target)) {
            $("#notificationPanel").removeClass("show");
            setTimeout(() => $("#notificationPanel").addClass("hidden"), 300);

            updateLogoImage(false);
            sidebar.removeClass("force-compact");

            setTimeout(() => {
                sidebar.addClass("animate-text");
            }, 200);
        }
    });

    // ✅ 로고 반응형 이미지 변경
    function updateLogoImage(forceSmallLogo = null) {
        const $logo = $("#logoImage");
        if ($logo.length === 0) return;

        const width = window.innerWidth;
        let src;

        if (forceSmallLogo === true || width <= 1263) {
            src = "/icon/logo2.png"; // 작은 로고
        } else {
            src = "/icon/logo.png";  // 큰 로고
        }

        if ($logo.attr("src") !== src) {
            $logo.css("opacity", "0").attr("src", src);
            setTimeout(() => {
                $logo.css("opacity", "1");
            }, 50);
        }
    }

    // ✅ 댓글/대댓글 더보기 초기화
    $(".comment-list").each(function () {
        const allComments = $(this).find(".comment-item");
        const toggleBtn = $(this).find(".toggle-comments-btn");

        if (allComments.length > 1) {
            allComments.slice(1).addClass("hidden");
            toggleBtn.show().attr("data-expanded", "false");
            toggleBtn.text(`${allComments.length - 1}개의 댓글 더보기`);
        } else {
            toggleBtn.hide();
        }
    });

    $(".reply-comment-list").each(function () {
        const replies = $(this).find(".reply-item");
        const toggleBtn = $(this).find(".toggle-replies-btn");

        if (replies.length > 0) {
            replies.addClass("hidden");
            const hiddenCount = replies.length;
            toggleBtn.text(hiddenCount > 1 ? `${hiddenCount}개의 답글 더보기` : "답글 보기");
            toggleBtn.show().attr("data-expanded", "false");
        } else {
            toggleBtn.hide();
        }
    });

    // ✅ 댓글 토글 함수
    function toggleComments(button) {
        const commentList = button.closest(".comment-list");
        const allComments = commentList.querySelectorAll(".comment-item");
        const isExpanded = button.getAttribute("data-expanded") === "true";

        if (isExpanded) {
            for (let i = 1; i < allComments.length; i++) allComments[i].classList.add("hidden");
            button.textContent = `${allComments.length - 1}개의 댓글 더보기`;
            button.setAttribute("data-expanded", "false");
        } else {
            allComments.forEach(el => el.classList.remove("hidden"));
            button.textContent = "접기";
            button.setAttribute("data-expanded", "true");
        }
    }

    // ✅ 대댓글 토글 함수
    function toggleReplies(button) {
        const replyList = button.closest(".reply-comment-list");
        const replies = replyList.querySelectorAll(".reply-item");
        const isExpanded = button.getAttribute("data-expanded") === "true";
        const hiddenCount = replies.length;

        if (isExpanded) {
            replies.forEach(reply => reply.classList.add("hidden"));
            button.textContent = hiddenCount > 1 ? `${hiddenCount}개의 답글 더보기` : "답글 보기";
            button.setAttribute("data-expanded", "false");
        } else {
            replies.forEach(reply => reply.classList.remove("hidden"));
            button.textContent = "답글 접기";
            button.setAttribute("data-expanded", "true");
        }
    }

    // ✅ 게시글 좋아요 요청 및 UI 갱신
    function likePost(postId) {
        $.ajax({
            url: "/likes",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({ targetId: postId, targetType: "POST" }),
            success: function (data) {
                const likeBtn = $("#like-btn-" + postId);
                const likeCount = $("#like-count-" + postId);

                likeCount.text(data.likeCount + "명");

                if (data.likedByMe) {
                    likeBtn.html('❤️ 좋아요');
                } else {
                    likeBtn.html('♡ 좋아요');
                }
            },
            error: function (xhr, status, error) {
                alert("좋아요 중 오류 발생: " + error);
            }
        });
    }

    // ✅ 로그아웃 확인 알림
    function confirmLogout() {
        return confirm("정말 로그아웃 하시겠습니까?");
    }

    // ✅ 더보기 메뉴 열기/닫기
    function toggleMoreMenu() {
        $("#moreMenu").toggleClass("show");
    }

    $(document).on("click", function (event) {
        const menu = $("#moreMenu")[0];
        const toggle = $(".more-menu-container > a")[0];
        if (!menu.contains(event.target) && !toggle.contains(event.target)) {
            $("#moreMenu").removeClass("show");
        }
    });

    // ✅ 업로드 모달 열기
    function openModal(event) {
        if (event) event.stopPropagation();
        $("#uploadModal").removeClass("hidden");

        // 초기화
        $("#uploadPlaceholder").show();    // 사진 올리기 안내 다시 보이게
        $("#imagePreview").hide();         // 사진 미리보기 숨김
        $("#goToWriteStep").hide();         // '다음' 버튼 숨김
        $("#uploadWriteStep").hide();      // 글쓰기 폼 숨김
        $("#imageUpload").val("");         // 파일 입력 리셋
    }

    // ✅ 업로드 모달 닫기
    function closeModal() {
        $("#uploadModal").addClass("hidden");

        // 리셋
        $("#uploadPlaceholder").show();
        $("#imagePreview").hide();
        $("#goToWriteStep").hide();
        $("#uploadWriteStep").hide();
        $("#imageUpload").val("");
    }

    $("#modalTrigger").on("click", openModal);

    $(document).on("click", function (e) {
        const $modal = $("#uploadModal");
        const $modalBox = $(".instagram-modal"); // 모달 내부 박스

        if (!$modal.hasClass("hidden") && !$modalBox.is(e.target) && $modalBox.has(e.target).length === 0) {
            closeModal();
        }
    });

    $("#imageUpload").on("change", function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $("#imagePreview").attr("src", e.target.result).show();
                $("#uploadPlaceholder").hide();
                $("#goToWriteStep").hide();
                $("#uploadWriteStep").show();
            }
            reader.readAsDataURL(file);
        }
    });

    // ✅ 게시글 업로드
    $("#postForm").on("submit", async function (e) {
        e.preventDefault();

        const formData = new FormData();
        const fileInput = document.getElementById("imageUpload");
        if (fileInput.files.length > 0) {
            formData.append("image", fileInput.files[0]);
        }
        formData.append("content", $("textarea[name='content']").val());

        try {
            const res = await fetch("/posts", { method: "POST", body: formData });
            if (res.ok) {
                alert("업로드 성공!");
                closeModal();
                location.reload();
            } else {
                alert("업로드 실패");
            }
        } catch (err) {
            alert("오류 발생");
        }
    });

    // ✅ 게시글 수정 모달 열기
    function openEditModal(event) {
        if (event) event.stopPropagation();
        $("#editModal").removeClass("hidden");
    }

    // ✅ 게시글 수정 모달 닫기
    function closeEditModal() {
        $("#editModal").addClass("hidden");
    }

    $(document).on("click", function (e) {
        const $modal = $("#editModal");
        const $modalContent = $("#editModal .modal-content");
        if (!$modal.hasClass("hidden") && !$modalContent.is(e.target) && $modalContent.has(e.target).length === 0) {
            closeEditModal();
        }
    });

    // ✅ 알림 개수 비동기적으로 가져오기
    function fetchNotificationCount() {
        $.get("/notifications/count", function (count) {
            const badge = $("#notification-count");
            if (count > 0) {
                badge.text(count).removeClass("hidden");
            } else {
                badge.addClass("hidden");
            }
        });
    }

    // ✅ 1초마다 알림 개수 새로고침
    setInterval(fetchNotificationCount, 1000);

    // ✅ 댓글 영역 벗어나면 자동 접기 위한 IntersectionObserver 설정
    const commentLists = document.querySelectorAll(".comment-list");
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (!entry.isIntersecting) {
                const button = entry.target.querySelector(".toggle-comments-btn");
                if (button && button.getAttribute("data-expanded") === "true") toggleComments(button);
                const replyButtons = entry.target.querySelectorAll(".toggle-replies-btn[data-expanded='true']");
                replyButtons.forEach(btn => toggleReplies(btn));
            }
        });
    }, { threshold: 0 });

    commentLists.forEach(cl => observer.observe(cl));

    // ✅ 댓글 작성 시 현재 스크롤 위치 저장
    $("form[action='/comment/add'], form[action='/comment/reply']").on("submit", function () {
        sessionStorage.setItem("scrollY", window.scrollY);
    });

    // ✅ 페이지 로드 후 이전 스크롤 위치 복구
    $(window).on("load", function () {
        const savedScroll = sessionStorage.getItem("scrollY");
        if (savedScroll !== null) {
            window.scrollTo(0, parseInt(savedScroll));
            sessionStorage.removeItem("scrollY");
        }
    });

    // ✅ 알림 클릭 시 숨겨진 댓글 자동 펼치고 이동
    $(window).on("load", function () {
        const hash = window.location.hash;

        if (hash && hash.startsWith("#comment-")) {
            const $targetComment = $(hash);

            if ($targetComment.length > 0 && $targetComment.hasClass("hidden")) {
                const $commentList = $targetComment.closest(".comment-list");
                const $toggleBtn = $commentList.find(".toggle-comments-btn");

                if ($toggleBtn.length > 0) {
                    toggleComments($toggleBtn[0]);
                }

                setTimeout(() => {
                    document.querySelector(hash)?.scrollIntoView({
                        behavior: "smooth",
                        block: "center"
                    });
                }, 300);
            }
        }
    });

    // ✅ 전역 함수 등록
    window.toggleMoreMenu = toggleMoreMenu;
    window.confirmLogout = confirmLogout;
    window.likePost = likePost;
    window.openModal = openModal;
    window.closeModal = closeModal;
    window.toggleComments = toggleComments;
    window.toggleReplies = toggleReplies;

    updateLogoImage(); // 페이지 진입 시 강제 로고 업데이트

    // ✅ 창 크기 조정 시 로고 이미지 업데이트
    $(window).on("resize", function () {
        const isNotificationOpen = $("#notificationPanel").hasClass("show");
        updateLogoImage(isNotificationOpen);
    });

});