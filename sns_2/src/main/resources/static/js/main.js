$(document).ready(function () {
    let notificationsMarkedAsRead = false;
    let previewList = [];
    let currentIndex = 0;

    // 알림 패널 토글
    $("#notification-bell").on("click", function (e) {
        e.preventDefault();
        const panel = $("#notificationPanel");
        const sidebar = $(".sidebar");

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
                fetchNotificationCount();
            });
        }

        $.get("/notifications", function (html) {
            const newContent = $(html).find("#notificationPanel").html();
            $("#notificationPanel").html(newContent).removeClass("hidden").addClass("show");
            sidebar.addClass("force-compact").removeClass("animate-text");
            updateLogoImage(true);
            $("#logoImage").css("opacity", "1");
        });
    });

    // 알림 읽음 처리 후 이동
    $(document).on("click", ".notification-item", function () {
        const $item = $(this);
        const href = $item.data("href");
        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: `/notifications/${$item.data("id")}/read`,
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function () {
                if (href) window.location.href = href;
            },
            error: function (err) {
                console.error("❌ 읽음 처리 실패:", err);
            }
        });
    });

    // 알림 외부 클릭 시 닫기
    $(document).on("click", function (e) {
        const panel = $("#notificationPanel")[0];
        const bell = $("#notification-bell")[0];
        const sidebar = $(".sidebar");

        const panelClicked = panel && panel.contains(e.target);
        const bellClicked = bell && bell.contains(e.target);

        if (!panelClicked && !bellClicked) {
            $("#notificationPanel").removeClass("show");
            setTimeout(() => $("#notificationPanel").addClass("hidden"), 300);
            updateLogoImage(false);
            sidebar.removeClass("force-compact");
            setTimeout(() => sidebar.addClass("animate-text"), 200);
        }
    });


    function updateLogoImage(forceSmallLogo = null) {
        const $logo = $("#logoImage");
        if ($logo.length === 0) return;
        const width = window.innerWidth;
        let src = (forceSmallLogo === true || width <= 1263) ? "/icon/logo2.png" : "/icon/logo.png";
        if ($logo.attr("src") !== src) {
            $logo.css("opacity", "0").attr("src", src);
            setTimeout(() => $logo.css("opacity", "1"), 50);
        }
    }

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
            toggleBtn.text(replies.length > 1 ? `${replies.length}개의 답글 더보기` : "답글 보기");
            toggleBtn.show().attr("data-expanded", "false");
        } else {
            toggleBtn.hide();
        }
    });

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

    function toggleReplies(button) {
        const replyList = button.closest(".reply-comment-list");
        const replies = replyList.querySelectorAll(".reply-item");
        const isExpanded = button.getAttribute("data-expanded") === "true";

        if (isExpanded) {
            replies.forEach(reply => reply.classList.add("hidden"));
            button.textContent = replies.length > 1 ? `${replies.length}개의 답글 더보기` : "답글 보기";
            button.setAttribute("data-expanded", "false");
        } else {
            replies.forEach(reply => reply.classList.remove("hidden"));
            button.textContent = "답글 접기";
            button.setAttribute("data-expanded", "true");
        }
    }

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
                likeBtn.html(data.likedByMe ? '❤️ 좋아요' : '♡ 좋아요');
            },
            error: function (xhr, status, error) {
                alert("좋아요 중 오류 발생: " + error);
            }
        });
    }

    function confirmLogout() {
        return confirm("정말 로그아웃 하시겠습니까?");
    }

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

    function openModal(event) {
        if (event) event.stopPropagation();
        $("#uploadModal").removeClass("hidden");
        $("#uploadPlaceholder").show();
        $("#goToWriteStep").hide();
        $("#uploadWriteStep").hide();
        $("#imageUpload").val("");
    }

    function closeModal() {
        $("#uploadModal").addClass("hidden");
        $("#uploadPlaceholder").show();
        $("#goToWriteStep").hide();
        $("#uploadWriteStep").hide();
        $("#imageUpload").val("");
    }

    function openEditModal(event) {
        if (event) event.stopPropagation();
        $("#editModal").removeClass("hidden");
    }

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

    $("#modalTrigger").on("click", openModal);
    $("#goToWriteStep").on("click", function () {
        $("#goToWriteStep").hide();
        $("#uploadWriteStep").show();
    });

        $("#imageUpload").on("change", function () {
            const newFiles = Array.from(this.files);
            previewList = [];
        const $carousel = $("#previewCarousel");
        const $indicators = $("#carouselIndicators");

        let startIndex = previewList.length; // 새로 추가될 위치부터 시작

        newFiles.forEach((file, i) => {
            const url = URL.createObjectURL(file);
            const isVideo = file.type.startsWith("video");

            if (isVideo) {
                const video = document.createElement("video");
                video.src = url;
                video.preload = "metadata";
                video.onloadedmetadata = () => {
                    if (video.duration > 60) {
                        alert("60초 이하의 영상만 업로드 가능합니다.");
                        URL.revokeObjectURL(url);
                        return;
                    }
                    previewList.push({ file, type: "video" });
                    renderMediaToCarousel(url, "video", startIndex++);
                };
            } else {
                previewList.push({ file, type: "image" });
                renderMediaToCarousel(url, "image", startIndex++);
            }
        });

        if (previewList.length > 0) {
            $("#uploadPlaceholder").hide();
            $("#carouselContainer").removeClass("hidden");  // ← 여기를 정확히 추가
            $("#goToWriteStep").show();
        } else {
            $("#uploadPlaceholder").show();
            $("#carouselContainer").addClass("hidden");
            $("#goToWriteStep").hide();
        }



        this.value = ""; // 같은 파일도 다시 선택 가능하도록 초기화
    });

    $("#nextSlide").on("click", function () {
        const track = $("#previewCarousel");
        const items = track.children().length;
        if (currentIndex < items - 1) {
            currentIndex++;
            track.css("transform", `translateX(-${currentIndex * 100}%)`);
            updateIndicators(currentIndex);
        }
    });

    $("#prevSlide").on("click", function () {
        const track = $("#previewCarousel");
        if (currentIndex > 0) {
            currentIndex--;
            track.css("transform", `translateX(-${currentIndex * 100}%)`);
            updateIndicators(currentIndex);
        }
    });

    function updateIndicators(index) {
        const indicators = $("#carouselIndicators span");
        indicators.removeClass("active").eq(index).addClass("active");
    }

    $("#postForm").on("submit", async function (e) {
        e.preventDefault();
        const formData = new FormData();
        previewList.forEach(media => {
            formData.append("mediaFiles", media.file);
        });
        formData.append("content", $("textarea[name='content']").val());

        try {
            const res = await fetch("/posts", {
                method: "POST",
                body: formData
            });
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

    setInterval(fetchNotificationCount, 1000);
    window.moveSlide = moveSlide;
    window.goToSlide = goToSlide;
    window.toggleMoreMenu = toggleMoreMenu;
    window.confirmLogout = confirmLogout;
    window.likePost = likePost;
    window.openModal = openModal;
    window.closeModal = closeModal;
    window.toggleComments = toggleComments;
    window.toggleReplies = toggleReplies;

    updateLogoImage();

    $(window).on("resize", function () {
        const isNotificationOpen = $("#notificationPanel").hasClass("show");
        updateLogoImage(isNotificationOpen);
    });

    $(window).on("load", function () {
        const savedScroll = sessionStorage.getItem("scrollY");
        if (savedScroll !== null) {
            window.scrollTo(0, parseInt(savedScroll));
            sessionStorage.removeItem("scrollY");
        }

        const hash = window.location.hash;
        if (hash && hash.startsWith("#comment-")) {
            const $targetComment = $(hash);
            if ($targetComment.length > 0 && $targetComment.hasClass("hidden")) {
                const $commentList = $targetComment.closest(".comment-list");
                const $toggleBtn = $commentList.find(".toggle-comments-btn");
                if ($toggleBtn.length > 0) toggleComments($toggleBtn[0]);
                setTimeout(() => {
                    document.querySelector(hash)?.scrollIntoView({ behavior: "smooth", block: "center" });
                }, 300);
            }
        }
    });

    $("form[action='/comment/add'], form[action='/comment/reply']").on("submit", function () {
        sessionStorage.setItem("scrollY", window.scrollY);
    });

    function updateCarouselTrackWidth() {
        const $carousel = $("#previewCarousel");
        const $slides = $carousel.children();
        const count = $slides.length;

        const containerWidth = $("#carouselContainer").width(); // 슬라이드 영역의 실제 폭
        $carousel.css("width", `${count * containerWidth}px`);

        $slides.each(function () {
            $(this).css({
                width: `${containerWidth}px`,
                flex: `0 0 ${containerWidth}px`
            });
        });
    }


    function renderMediaToCarousel(url, type, index) {
        const $carousel = $("#previewCarousel");
        const $indicators = $("#carouselIndicators");
        const $slide = $("<div>").addClass("carousel-slide");
        $slide.append(type === "video"
            ? $("<video>").attr("src", url).attr("controls", true)
            : $("<img>").attr("src", url));
        $carousel.append($slide);

        const $indicator = $("<span>");
        if (index === 0) $indicator.addClass("active");
        $indicators.append($indicator);


        // ✅ 첫 슬라이드일 경우 강제 초기화
        if ($carousel.children().length === 1) {
            $carousel.css("transform", "translateX(0)");
            updateIndicators(0);
            currentIndex = 0;
        }
    }

   function moveSlide(direction, postId) {
       const track = document.getElementById(`carousel-track-${postId}`);
       const slides = track.children;
       const total = slides.length;
       if (total <= 1) return;

       let currentIndex = Array.from(slides).findIndex(slide => slide.classList.contains("active-slide"));
       if (currentIndex === -1) currentIndex = 0;

       let newIndex = currentIndex + direction;
       if (newIndex < 0) newIndex = total - 1;
       if (newIndex >= total) newIndex = 0;

       updateCarousel(track, postId, newIndex);
   }

   function goToSlide(postId, index) {
       const track = document.getElementById(`carousel-track-${postId}`);
       updateCarousel(track, postId, index);
   }

    function updateCarousel(track, postId, index) {
        const slides = track.children;
        const total = slides.length;

        // 부모 너비 기준 슬라이드 하나 크기 계산
        const containerWidth = track.closest(".carousel-container").clientWidth;

        // ✅ 트랙 전체 너비를 정확한 px로 지정
        track.style.width = `${containerWidth * total}px`;

        for (let slide of slides) {
            slide.classList.remove("active-slide");
            slide.style.flex = `0 0 ${containerWidth}px`;  // 정확한 픽셀 지정
            slide.style.width = `${containerWidth}px`;
            slide.style.height = "100%";
        }

        slides[index].classList.add("active-slide");

        // ✅ 정확한 위치로 이동
        track.style.transform = `translateX(-${index * containerWidth}px)`;

        const indicators = document.querySelectorAll(`#carousel-indicators-${postId} span`);
        indicators.forEach((dot, i) => {
            dot.classList.toggle("active", i === index);
        });
    }





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
});
