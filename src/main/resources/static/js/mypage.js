$(document).ready(function () {
    // 페이지 로드 시 기본 AJAX 요청
    $.ajax({
        type: "GET",
        url: "/mypage/mypageMain",
        success: function (response) {
            $("#resultDiv").html(response);
        },
        error: function (xhr, status, error) {
            console.error("초기 페이지 로드 실패:", status, error);
            alert("초기 페이지를 불러오는 데 실패했습니다.");
        }
    });

    let userid = $("#sessionUserid").val(); // sessionScope.userid 값 가져오기

    $("#listedit").click(function (event) {
        event.preventDefault();
        loadContent($(this).attr("href"));
    });

    $(document).on("click", "a[href^='/cafe_search/ans_servlet/view.do']", function (event) {
        event.preventDefault();
        const href = $(this).attr("href");
        const q_idx = new URLSearchParams(href.split("?")[1]).get("q_idx");

        if (!q_idx) {
            alert("q_idx 값이 비어 있습니다. 다시 확인해주세요.");
            return;
        }

        loadContent(href);
    });

    $("#listFavorite").click(function () {
        $.ajax({
            type: "GET",
            data: { userid: userid },
            url: "/listFavor.do",
            success: function (txt) {
                $("#resultDiv").html(txt);
            }
        });
    });

    $("#listMemo").click(function () {
        $.ajax({
            type: "GET",
            data: { userid: userid, cf_number: 0 },
            url: "/listMemo.do",
            success: function (txt) {
                $("#resultDiv").html(txt);
            },
            error: function (xhr, status, error) {
                console.error("AJAX 요청 실패:", status, error);
                alert("메모 목록을 불러오는 데 실패했습니다.");
            }
        });
    });

    $(document).on("click", "#cfinsert, #base, #cfdetailedit, #sessionTest", function (event) {
        event.preventDefault();
        loadContent($(this).attr("href"));
    });

    $(document).on("click", "#listBoard", function (event) {
        event.preventDefault();
        loadContent($(this).attr("href"));
    });
});

/**
 * 공통 AJAX 로드 함수
 */
function loadContent(url) {
    $.ajax({
        type: "GET",
        url: url,
        success: function (response) {
            $("#resultDiv").html(response);
        },
        error: function (xhr, status, error) {
            console.error("AJAX 요청 실패:", status, error);
            alert("페이지를 불러오는 데 실패했습니다.");
        }
    });
}
