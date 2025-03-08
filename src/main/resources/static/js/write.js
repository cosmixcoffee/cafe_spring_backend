// write.jsp
$(document).ready(function () {
    
    // 🔥 기존 이벤트 제거 후 등록 (이벤트 중복 실행 방지)
    $(document).off("dragenter dragover", ".fileDrop").on("dragenter dragover", ".fileDrop", function (e) {
        e.preventDefault();
    });

    $(document).off("drop", ".fileDrop").on("drop", ".fileDrop", function (e) {
        e.preventDefault();
        const files = e.originalEvent.dataTransfer.files;
        const file = files[0];
        const form_data = new FormData();
        form_data.append("file", file);

        $.ajax({
            url: "/upload/ajax_upload",
            data: form_data,
            processData: false,
            contentType: false,
            type: "post",
            success: function (data) {
                const fileInfo = getFileInfo(data);
                let html = "<a href='" + fileInfo.get_link + "'>" + fileInfo.original_name + "</a><br>";
                html += "<input type='hidden' name='files' value='" + fileInfo.file_name + "'>";
                $("#uploadedList").append(html);
            }
        });
    });

    // 🔥 기존 이벤트 제거 후 등록 (게시글 저장 이벤트)
    $(document).off("click", "#btnSave").on("click", "#btnSave", function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        const title = $("#title").val();
        const contents = $("#contents").val();

        if (title.trim() === "") {
            alert("제목을 입력하세요.");
            $("#title").focus();
            return;
        }

        $.ajax({
            type: "POST",
            url: "/board/insert.do",
            data: $("#form1").serialize(),
            success: function (response) {
                alert("글이 성공적으로 저장되었습니다.");
                $("#resultDiv").load("/board/list.do"); // list.jsp 다시 로드
            },
            error: function (xhr, status, error) {
                console.error("글 저장 실패:", status, error);
                alert("글 저장에 실패했습니다.");
            }
        });
    });

    // 🔥 기존 이벤트 제거 후 등록 (파일 삭제 이벤트)
    $(document).off("click", ".file_del").on("click", ".file_del", function (e) {
        e.preventDefault();
        const that = $(this);
        $.ajax({
            type: "POST",
            url: "/upload/delete_file",
            data: { file_name: $(this).attr("data-src") },
            dataType: "text",
            success: function (result) {
                if (result === "deleted") {
                    that.parent("div").remove();
                }
            }
        });
    });

    // 🔥 파일 정보 가져오기 함수
    function getFileInfo(file_name) {
        const get_link = "/upload/display_file?file_name=" + file_name;
        const original_name = file_name.substr(file_name.indexOf("_") + 1);
        return { original_name: original_name, get_link: get_link, file_name: file_name };
    }

});
