// ✅ 더보기 메뉴 토글
function toggleMoreMenu() {
    const menu = document.getElementById("moreMenu");
    if (menu) {
        menu.classList.toggle("show");
    }
}

// ✅ 로그아웃 확인
function confirmLogout() {
    return confirm("정말 로그아웃 하시겠습니까?");
}

// ✅ 외부 클릭 시 더보기 메뉴 닫기
document.addEventListener("click", function (event) {
    const menu = document.getElementById("moreMenu");
    const toggle = document.querySelector(".more-menu-container > a");

    const menuClicked = menu && menu.contains(event.target);
    const toggleClicked = toggle && toggle.contains(event.target);

    if (menu && !menuClicked && !toggleClicked) {
        menu.classList.remove("show");
    }
});

// ✅ 이미지 업로드 유효성 검사
function validateProfileImage() {
    const fileInput = document.getElementById("profileImage");
    const file = fileInput.files[0];
    if (!file) return true;

    const allowedTypes = ["image/jpeg", "image/png"];
    const maxSize = 5 * 1024 * 1024;

    if (!allowedTypes.includes(file.type)) {
        alert("JPG 또는 PNG 형식의 이미지만 업로드 가능합니다.");
        fileInput.value = "";
        return false;
    }

    if (file.size > maxSize) {
        alert("파일 크기가 5MB를 초과할 수 없습니다.");
        fileInput.value = "";
        return false;
    }

    return true;
}

// ✅ 모달 열기
function openPostModal(postId) {
    fetch(`/posts/${postId}/modal`)
        .then(res => res.text())
        .then(html => {
            document.getElementById("modalContentArea").innerHTML = html;
            document.getElementById("postModal").classList.remove("hidden");
        })
        .catch(err => {
            console.error("모달 로딩 실패:", err);
            alert("게시물 로딩 실패");
        });
}

// ✅ 모달 닫기
function closeModal() {
    document.getElementById("postModal").classList.add("hidden");
    document.getElementById("modalContentArea").innerHTML = "";
}

// ✅ DOMContentLoaded
document.addEventListener("DOMContentLoaded", function () {
    // 🔹 소개글 글자 수 카운팅
    const bioInput = document.getElementById('bio');
    const bioCount = document.getElementById('bioCount');
    if (bioInput && bioCount) {
        bioCount.textContent = `${bioInput.value.length} / 150`;
        bioInput.addEventListener('input', () => {
            bioCount.textContent = `${bioInput.value.length} / 150`;
        });
    }

    // 🔹 프로필 이미지 삭제
    const deleteBtn = document.getElementById("deleteProfileImageButton");
    if (deleteBtn) {
        deleteBtn.addEventListener("click", function () {
            if (confirm("정말 프로필 사진을 삭제하시겠습니까?")) {
                fetch("/profile/deleteImage", { method: "POST" })
                    .then(res => res.text())
                    .then(data => {
                        if (data.includes("성공")) {
                            alert("프로필 사진이 삭제되었습니다.");
                            const img = document.getElementById("profileImageElement");
                            if (img) {
                                img.src = "/images/default-profile.png?rand=" + Math.random();
                            }
                        } else {
                            alert("프로필 사진 삭제 실패: " + data);
                        }
                    })
                    .catch(err => {
                        console.error("삭제 중 오류 발생:", err);
                        alert("오류가 발생했습니다.");
                    });
            }
        });
    }

    // 🔹 게시물 썸네일 클릭 → 모달 열기 (profile 페이지에서만)
    if (window.location.pathname.startsWith("/profile")) {
        document.querySelectorAll(".post-item").forEach(item => {
            item.addEventListener("click", function (e) {
                e.preventDefault();
                const postId = this.getAttribute("data-post-id");
                openPostModal(postId);
            });
        });
    }

    // 🔹 모달 닫기 (X 버튼 + 바깥 영역 + ESC 키)
    const closeBtn = document.querySelector(".close-button");
    if (closeBtn) {
        closeBtn.addEventListener("click", closeModal);
    }

    const modal = document.getElementById("postModal");
    if (modal) {
        modal.addEventListener("click", function (e) {
            if (e.target.id === "postModal") {
                closeModal();
            }
        });
    }

    window.addEventListener("keydown", function (e) {
        if (e.key === "Escape") {
            closeModal();
        }
    });
});

// ✅ 페이지 완전히 로드된 후 해시로 자동 모달 열기 (e.g., /profile/2#post-5)
window.addEventListener("load", function () {
    const hash = window.location.hash;
    if (hash && hash.startsWith("#post-")) {
        const postId = hash.replace("#post-", "");
        openPostModal(postId);
    }
});

// ✅ jQuery (닉네임/소개글 편집 + 이미지 유효성 검사)
$(function () {
    $("#editNicknameButton").on("click", function (e) {
        e.preventDefault();
        $("#nickname").removeAttr("readonly");
    });

    $("#editBioButton").on("click", function (e) {
        e.preventDefault();
        $("#bio").removeAttr("readonly");
    });

    $("#profileImage").on("change", function () {
        validateProfileImage();
    });
});
