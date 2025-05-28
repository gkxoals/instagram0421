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

// ✅ 더보기 외부 클릭 시 닫기 (에러 방지 완전 처리)
document.addEventListener("click", function (event) {
    const menu = document.getElementById("moreMenu");
    const toggle = document.querySelector(".more-menu-container > a");

    const menuClicked = menu && menu.contains(event.target);
    const toggleClicked = toggle && toggle.contains(event.target);

    if (menu && !menuClicked && !toggleClicked) {
        menu.classList.remove("show");
    }
});

// ✅ DOMContentLoaded
document.addEventListener("DOMContentLoaded", function () {
    const bioInput = document.getElementById('bio');
    const bioCount = document.getElementById('bioCount');

    if (bioInput && bioCount) {
        bioCount.textContent = `${bioInput.value.length} / 150`;
        bioInput.addEventListener('input', () => {
            bioCount.textContent = `${bioInput.value.length} / 150`;
        });
    }

    // ✅ 프로필 이미지 삭제 버튼 이벤트
    const deleteBtn = document.getElementById("deleteProfileImageButton");
       if (deleteBtn) {
        deleteBtn.addEventListener("click", function () {
            if (confirm("정말 프로필 사진을 삭제하시겠습니까?")) {
                fetch("/profile/deleteImage", {
                    method: "POST"
                }).then(res => res.text())
                  .then(data => {
                    if (data.includes("성공")) {
                        alert("프로필 사진이 삭제되었습니다.");
                        // 캐시 우회: 강제로 이미지 새로 로딩
                        const img = document.getElementById("profileImageElement");
                        if (img) {
                            img.src = "/images/default-profile.png?rand=" + Math.random();
                        }
                    } else {
                        alert("프로필 사진 삭제 실패: " + data);
                    }
                }).catch(err => {
                    console.error("삭제 중 오류 발생:", err);
                    alert("오류가 발생했습니다.");
                });
            }
        });
      }
    });

// ✅ jQuery DOM 준비
$(document).ready(function () {
    // 닉네임, 소개글 수정
    $("#editNicknameButton").on("click", function (e) {
        e.preventDefault();
        $("#nickname").removeAttr("readonly");
    });

    $("#editBioButton").on("click", function (e) {
        e.preventDefault();
        $("#bio").removeAttr("readonly");
    });

    // 프로필 이미지 변경 시 유효성 검사
    $("#profileImage").on("change", function () {
        validateProfileImage();
    });
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
