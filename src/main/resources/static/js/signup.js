$(document).ready(function () {
    const $phoneInput = $("#phone");
    const $phoneError = $("<p></p>").css({ color: "red", display: "none" });

    $phoneInput.parent().append($phoneError); // 부모 요소에 에러 메시지 추가

    $phoneInput.on("blur", function () {
        const phoneValue = $phoneInput.val().trim();
        if (!phoneValue) return;

        $.ajax({
            url: `/api/user/check-phone?phone=${encodeURIComponent(phoneValue)}`,
            method: "GET",
            dataType: "json",
            success: function (data) {
                if (data.exists) {
                    $phoneError.text("이미 존재하는 전화번호입니다.").show();
                    $phoneInput[0].setCustomValidity("이미 존재하는 전화번호입니다.");
                } else {
                    $phoneError.hide();
                    $phoneInput[0].setCustomValidity("");
                }
            },
            error: function (xhr, status, error) {
                console.error("전화번호 확인 오류:", error);
                $phoneError.text("서버 오류로 확인할 수 없습니다.").show();
            }
        });
    });
});
