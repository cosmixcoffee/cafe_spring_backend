$(document).ready(function() {
    console.log("board.js 로드 완료");

    // 목록 이동 함수
    function goToList() {
        $.ajax({
            type: "GET",
            url: "/board/list.do",
            success: function(response) {
                $("#resultDiv").html(response);
            },
            error: function(xhr, status, error) {
                console.error("목록 로드 실패:", status, error);
                alert("목록을 불러오는 데 실패했습니다.");
            }
        });
    }

    // 게시글 수정 함수
    function updatePost() {
        const idx = $("#idx").val();
        const title = $("input[name='title']").val();
        const contents = $("#contents").val();
        const writer = $("#writer").val();

        if (!idx || idx.trim() === "") {
            alert("⚠ 게시글 ID가 없습니다.");
            return;
        }

        console.log("게시글 수정 요청:", { idx, title, contents, writer });

        $.ajax({
            type: "POST",
            url: "/board/update.do",
            data: { idx, title, contents, writer },
            success: function(response) {
                alert(" 게시글이 수정되었습니다.");
                $("#resultDiv").html("");
                goToList(); // 수정 후 목록으로 이동
            },
            error: function(xhr, status, error) {
                console.error("수정 실패:", status, error);
                alert("게시글 수정에 실패했습니다.");
            }
        });
    }

    // 게시글 삭제 함수
    function deletePost() {
        const idx = $("#idx").val();

        if (!idx) {
            alert("⚠ 게시글 ID가 없습니다.");
            return;
        }

        if (!confirm("정말로 삭제하시겠습니까?")) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "/board/delete.do",
            data: { idx: idx },
            success: function(response) {
                alert("✅ 게시글이 삭제되었습니다.");
                $("#resultDiv").html("");
                goToList(); // 삭제 후 목록으로 이동
            },
            error: function(xhr, status, error) {
                console.error("삭제 실패:", status, error);
                alert("게시글 삭제에 실패했습니다.");
            }
        });
    }

    // 파일 정보 가져오기
    function getFileInfo(file_name) {
        const get_link = "/upload/display_file?file_name=" + file_name;
        const original_name = file_name.substr(file_name.indexOf("_") + 1);
        return { original_name, get_link, file_name };
    }

    // 첨부파일 목록 조회
    function listAttach() {
        $.ajax({
            type: "post",
            url: "/board/list_attach/${dto.idx}",
            success: function(list) {
                $(list).each(function() {
                    const fileInfo = getFileInfo(this);
                    let html = `<div>
                                    <a href="${fileInfo.get_link}">${fileInfo.original_name}</a>&nbsp;&nbsp;
                                    <a href="#" class="file_del" data-src="${this}">[삭제]</a>
                                </div>`;
                    $("#uploadedList").append(html);
                });
            }
        });
    }

    // 댓글 추가
    function reply() {
        const reply_text = $("#reply_text").val();
        const board_idx =  parseInt($("#boardIdx").val(), 10); 
        const params = { reply_text, board_idx };
		
		if (isNaN(board_idx)) {
		    alert("⚠ 게시글 ID가 올바르지 않습니다.");
		    return;
		}

        $.ajax({
            type: "post",
            url: "/reply/insert.do",
            data: params,
            success: function() {
                alert("✅ 댓글이 등록되었습니다.");
                listReply("1");
            }
        });
    }

    // 댓글 목록 조회
    function listReply(num) {
        $.ajax({
            url: "/reply/list.do?board_idx=${dto.idx}&curPage=" + num,
            success: function(result) {
                console.log("Writer 값: ", "${dto.writer}");
                $("#listReply").html(result);
            }
        });
    }

    // 댓글 수정 화면 표시
    function showModify(idx) {
        $.ajax({
            url: "/reply/detail/" + idx,
            success: function(result) {
                $("#modifyReply").html(result);
                $("#modifyReply").css("visibility", "visible");
            }
        });
    }

    // 파일 드롭 기능 추가
    $(".fileDrop").on("dragenter dragover", function(e) {
        e.preventDefault();
    });

    $(".fileDrop").on("drop", function(e) {
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
            success: function(data) {
                const fileInfo = getFileInfo(data);
                let html = `<a href='${fileInfo.get_link}'>${fileInfo.original_name}</a><br>
                            <input type='hidden' name='files' value='${fileInfo.file_name}'>`;
                $("#uploadedList").append(html);
            }
        });
    });

    // 파일 삭제 기능
    $("#uploadedList").on("click", ".file_del", function(e) {
        const that = $(this);
        $.ajax({
            type: "post",
            url: "/upload/delete_file",
            data: { file_name: $(this).attr("data-src") },
            dataType: "text",
            success: function(result) {
                if (result == "deleted") {
                    that.parent("div").remove();
                }
            }
        });
    });

    // 이벤트 리스너 등록
    $(document).on("click", "#btnAnsUpdate", updatePost);
    $(document).on("click", "#btnAnsDelete", deletePost);
    $(document).on("click", "#btnList", goToList);
    $(document).on("click", "#btnReply", reply);

    // 초기 실행
    listReply("1");
    listAttach();
});
