<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script src="http://code.jquery.com/jquery-3.7.1.min.js"></script>
<meta charset="UTF-8">
<title>CAFETITLE</title>
</head>
<script>
$(function(){
    // 페이지 로드 시 기본 AJAX 요청
    $.ajax({
        type: "GET",
        url: "/mypage/mypageMain", // 기본적으로 로드할 URL
        success: function(response) {
            $("#resultDiv").html(response); // 결과를 #resultDiv에 출력
        },
        error: function(xhr, status, error) {
            console.error("초기 페이지 로드 실패:", status, error);
            alert("초기 페이지를 불러오는 데 실패했습니다.");
        }
    });
    
	let userid = "${sessionScope.userid}";
	
	$("#listedit").click(function(event) {
        event.preventDefault(); // 기본 링크 이동 방지

        const url = $(this).attr("href"); // 클릭한 링크의 href 가져오기

        $.ajax({
            type: "GET", // GET 방식으로 edit.jsp의 내용을 요청
            url: url,
            success: function(response) {
                $("#resultDiv").html(response); // 가져온 내용을 resultDiv에 출력
            },
            error: function(xhr, status, error) {
                console.error("AJAX 요청 실패:", status, error);
                alert("정보를 불러오는 데 실패했습니다.");
            }
        });
    });
	
	
    $(document).on("click", "a[href^='/cafe_search/ans_servlet/view.do']", function(event) {
        event.preventDefault(); // 기본 동작 방지

        const href = $(this).attr("href"); // 클릭한 링크의 href 값 가져오기
        const q_idx = new URLSearchParams(href.split('?')[1]).get("q_idx"); // q_idx 값 추출

        // q_idx 값 검증
        if (!q_idx) {
            alert("q_idx 값이 비어 있습니다. 다시 확인해주세요.");
            return;
        }

        // AJAX 요청
        $.ajax({
            type: "GET",
            url: href, // href 값을 요청 URL로 사용
            success: function(response) {
                // 응답을 특정 영역에 출력
                $("#resultDiv").html(response);
            },
            error: function(xhr, status, error) {
                console.error("AJAX 요청 실패:", status, error);
                alert("게시글을 불러오는 데 실패했습니다.");
            }
        });
    });

	$("#listFavorite").click(function() {    	
		$.ajax({
	        type: "get",
	        data : {"userid" : userid},
	        url: "/listFavor.do", 
	        success: function(txt) {
	        	console.log(txt); // #resultDiv를 올바르게 선택
	        	$("#resultDiv").html(txt);
	        }
	    });
	});
	
	$("#listMemo").click(function() {    	
		$.ajax({
	        type: "GET",  // POST 대신 GET 사용
	        data: {
	            "userid": userid,
	            "cf_number": 0  // 전체 메모를 불러오기 위해 cf_number를 0으로 전달
	        },
	        url: "/listMemo.do", 
	        success: function(txt) {
	            console.log(txt);
	            $("#resultDiv").html(txt);
	        },
	        error: function(xhr, status, error) {
	            console.error("AJAX 요청 실패:", status, error);
	            alert("메모 목록을 불러오는 데 실패했습니다.");
	        }
	    });
	});
	
	$(document).on("click", "#cfinsert", function(event) {
	    event.preventDefault(); // 기본 링크 동작 방지
	    
	    const url = $(this).attr("href"); // href 값 가져오기

	    $.ajax({
	        type: "GET", // GET 요청 방식 사용
	        url: url, // 요청 URL
	        success: function(response) {
	            $("#resultDiv").html(response); // 가져온 내용을 #resultDiv에 출력
	        },
	        error: function(xhr, status, error) {
	            console.error("AJAX 요청 실패:", status, error);
	            alert("카페정보수정 페이지를 불러오는 데 실패했습니다.");
	        }
	    });
	});
	
	$(document).on("click", "#base", function(event) {
	    event.preventDefault(); // 기본 링크 동작 방지
	    
	    const url = $(this).attr("href"); // href 값 가져오기

	    $.ajax({
	        type: "GET", // GET 요청 방식 사용
	        url: url, // 요청 URL
	        success: function(response) {
	            $("#resultDiv").html(response); // 가져온 내용을 #resultDiv에 출력
	        },
	        error: function(xhr, status, error) {
	            console.error("AJAX 요청 실패:", status, error);
	            alert("카페정보수정 페이지를 불러오는 데 실패했습니다.");
	        }
	    });
	});

	
	$(document).on("click", "#cfdetailedit", function(event) {
	    event.preventDefault(); // 기본 링크 동작 방지
	    
	    const url = $(this).attr("href"); // href 값 가져오기

	    $.ajax({
	        type: "GET", // GET 요청 방식 사용
	        url: url, // 요청 URL
	        success: function(response) {
	            $("#resultDiv").html(response); // 가져온 내용을 #resultDiv에 출력
	        },
	        error: function(xhr, status, error) {
	            console.error("AJAX 요청 실패:", status, error);
	            alert("카페정보수정 페이지를 불러오는 데 실패했습니다.");
	        }
	    });
	});
	
	$(document).on("click", "#sessionTest", function(event) {
	    event.preventDefault(); // 기본 링크 동작 방지
	    
	    const url = $(this).attr("href"); // href 값 가져오기

	    $.ajax({
	        type: "GET", // GET 요청 방식 사용
	        url: url, // 요청 URL
	        success: function(response) {
	            $("#resultDiv").html(response); // 가져온 내용을 #resultDiv에 출력
	        },
	        error: function(xhr, status, error) {
	            console.error("AJAX 요청 실패:", status, error);
	            alert("세션 정보 페이지를 불러오는 데 실패했습니다.");
	        }
	    });
	});
	
});	

$(document).on("click", "#listBoard", function(event) {
    event.preventDefault(); // 기본 링크 이동 방지

    const url = $(this).attr("href"); // 클릭한 링크의 href 가져오기

    $.ajax({
        type: "GET",
        url: url,
        success: function(response) {
            $("#resultDiv").html(response); // 결과를 #resultDiv에 출력
        },
        error: function(xhr, status, error) {
            console.error("AJAX 요청 실패:", status, error);
            alert("건의사항 목록을 불러오는 데 실패했습니다.");
        }
    });
});


//list.jsp

$(function(){
	$(".fileDrop").on("dragenter dragover", function(e){
		e.preventDefault();	
	});
	$(".fileDrop").on("drop", function(e){
		e.preventDefault();
		const files=e.originalEvent.dataTransfer.files;
		const file=files[0];
		const form_data=new FormData();
		form_data.append("file",file);
		$.ajax({
			url:"/upload/ajax_upload",
			data: form_data,
			processData: false,
			contentType: false,
			type: "post",
			success: function(data){
				const fileInfo=getFileInfo(data);
				let html = "<a href='"+fileInfo.get_link+"'>"+fileInfo.original_name+"</a><br>";
				html+="<input type='hidden' name='files' value='"+fileInfo.file_name+"'>";
				$("#uploadedList").append(html);
			}
		});
	});

	$("#uploadedList").on("click",".file_del",function(e){
		const that=$(this);
		$.ajax({
			type: "post",
			url:"/upload/delete_file",
			data: {file_name:$(this).attr("data-src")},
			dataType: "text",
			success: function(result){
				if(result=="deleted"){
					that.parent("div").remove();
				}
			}
		});
	});
	
	 $(document).off("click", "#btnAnsUpdate").on("click", "#btnAnsUpdate", function(event) {
	    event.preventDefault(); // 기본 폼 전송 방지
	
	    const formData = new FormData($("#form1")[0]); // 파일 포함한 폼 데이터 생성
	
	    $.ajax({
	        type: "POST",
	        url: "/board/update.do",
	        data: formData,
	        processData: false, // 파일 전송을 위해 false 설정
	        contentType: false, // 파일 전송을 위해 false 설정
	        success: function(response) {
	            alert("게시글이 수정되었습니다.");
	            $("#resultDiv").html(""); // 기존 내용 초기화
	            $("#resultDiv").load("/board/list.do"); // 리스트 다시 로드
	        },
	        error: function(xhr, status, error) {
	            console.error("업데이트 실패:", status, error);
	            alert("게시글 수정에 실패했습니다.");
	        }
	    });
	});


	   $(document).on("click", "#btnReply", function() {
		    reply();
		});
	 
	 
	    // 삭제 버튼 클릭 시 AJAX 요청
	    $(document).off("click", "#btnAnsDelete").on("click", "#btnAnsDelete", function(event) {
	        event.preventDefault();

	        const idx = $("#idx").val();

	        if (!idx) {
	            alert("게시글 ID가 없습니다. 다시 시도해주세요.");
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
	                alert("게시글이 삭제되었습니다.");
	                $("#resultDiv").html(""); // 기존 내용 초기화
	                $("#resultDiv").load("/board/list.do"); // 목록 다시 로드
	            },
	            error: function(xhr, status, error) {
	                console.error("삭제 실패:", status, error);
	                alert("게시글 삭제에 실패했습니다.");
	            }
	        });
	    });
	    
	    
	    // 댓글 삭제
	    $(document).off("click", "#btnReplyDelete").on("click", "#btnReplyDelete", function(){
	    	const idx = $("#modifyReply").attr("data-idx"); 

	        if (!idx) {
	            alert("댓글 ID가 없습니다.");
	            return;
	        }

	        if (!confirm("정말 삭제하시겠습니까?")) {
	            return;
	        }

	        $.ajax({
	            type: "GET",
	            url: "/reply/delete/" + idx,
	            success: function(result){
	                if(result === "success"){
	                    alert("삭제되었습니다.");
	                    $("#modifyReply").hide();
	                    listReply(1);
	                }
	            },
	            error: function(xhr, status, error){
	                console.error("댓글 삭제 실패:", status, error);
	                alert("댓글 삭제에 실패했습니다.");
	            }
	        });
	    });

	    // 댓글 수정
	    $(document).off("click", "#btnReplyUpdate").on("click", "#btnReplyUpdate", function(){
	    	const idx = $("#modifyReply").attr("data-idx"); 
	        const reply_text = $("#detail_replytext").val();

	        if (!idx) {
	            alert("댓글 ID가 없습니다.");
	            return;
	        }

	        $.ajax({
	            type: "POST",
	            url: "/reply/update/" + idx,
	            headers: {"Content-Type": "application/json"},
	            data: JSON.stringify({ reply_text: reply_text }),
	            success: function(result){
	                if(result === "success"){
	                    alert("댓글이 수정되었습니다.");
	                    $("#modifyReply").hide();
	                    listReply(1);
	                }
	            },
	            error: function(xhr, status, error){
	                console.error("댓글 수정 실패:", status, error);
	                alert("댓글 수정에 실패했습니다.");
	            }
	        });
	    });

	    // 댓글 수정 창 닫기
	    $(document).off("click", "#btnReplyClose").on("click", "#btnReplyClose", function(){
	        $("#modifyReply").hide();
	    });
	    
	    $(".fileDrop").on("dragenter dragover", function(e){
			e.preventDefault();	
		});
		$(".fileDrop").on("drop", function(e){
			e.preventDefault();
			const files=e.originalEvent.dataTransfer.files;
			const file=files[0];
			const form_data=new FormData();
			form_data.append("file",file);
			$.ajax({
				url:"/upload/ajax_upload",
				data: form_data,
				processData: false,
				contentType: false,
				type: "post",
				success: function(data){
					const fileInfo=getFileInfo(data);
					let html = "<a href='"+fileInfo.get_link+"'>"+fileInfo.original_name+"</a><br>";
					html+="<input type='hidden' name='files' value='"+fileInfo.file_name+"'>";
					$("#uploadedList").append(html);
				}
			});
		});

});

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

//list.jsp
$(document).ready(function () {

    // 🔥 기존 이벤트 제거 후 다시 등록하여 중복 실행 방지
    $(document).off("click", "#btnWrite").on("click", "#btnWrite", function(event) {
        event.preventDefault(); // 기본 동작 방지

        $.ajax({
            type: "GET",
            url: "/board/write.do", // 글쓰기 페이지 URL
            success: function(response) {
                $("#resultDiv").html(response); // mypage.jsp의 resultDiv에 출력
            },
            error: function(xhr, status, error) {
                console.error("AJAX 요청 실패:", status, error);
                alert("글쓰기 페이지를 불러오는 데 실패했습니다.");
            }
        });
    });

    // 🔥 기존 이벤트 제거 후 다시 등록 (게시글 제목 클릭 시 `view.jsp` 불러오기)
    $(document).off("click", ".view-board").on("click", ".view-board", function(event) {
        event.preventDefault(); // 기본 동작 방지

        const idx = $(this).data("idx");
        const cur_page = $(this).data("cur_page");
        const search_option = $(this).data("search_option");
        const keyword = $(this).data("keyword");

        $.ajax({
            type: "GET",
            url: "/board/detail.do",
            data: {
                idx: idx,
                cur_page: cur_page,
                search_option: search_option,
                keyword: keyword
            },
            success: function(response) {
                $("#resultDiv").html(response); // `view.jsp`를 `resultDiv`에 로드
            },
            error: function(xhr, status, error) {
                console.error("AJAX 요청 실패:", status, error);
                alert("게시글을 불러오는 데 실패했습니다.");
            }
        });
    });

});

// 🔥 목록을 AJAX로 불러오는 함수
function list(page) {
    $("#resultDiv").load("/board/list.do?curPage=" + page + "&search_option=${map.search_option}&keyword=${map.keyword}");
}

function showModify(idx) {
    $.ajax({
        url: "/reply/detail/" + idx,
        success: function(result) {
            $("#modifyReply").html(result);
            $("#modifyReply").show();
            $("#modifyReply").attr("data-idx", idx);  // ✅ 수정: .data() 대신 .attr() 사용
            console.log("수정할 댓글 ID:", idx);
        },
        error: function(xhr, status, error) {
            console.error("댓글 수정 로딩 실패:", status, error);
        }
    });
}

function reply() {
    const board_idx = $("#btnReply").data("id");
    const reply_text = $("#reply_text").val();

    if (!reply_text.trim()) {
        alert("댓글을 입력해주세요.");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/reply/insert.do",
        data: {
            board_idx: board_idx,
            reply_text: reply_text
        },
        success: function(response) {
            alert("댓글이 작성되었습니다.");
            $("#reply_text").val(""); // 입력창 비우기
            listReply(1); // 댓글 목록 다시 불러오기
        },
        error: function(xhr, status, error) {
            console.error("댓글 작성 실패:", status, error);
            alert("댓글 작성에 실패했습니다.");
        }
    });
}

</script>
<!-- <script src="/js/write.js"></script> -->
<!-- <script src="/js/ans_board_reply.js"></script> -->
<body>
<%@ include file="../include/login_menuplus.jsp"%>
<div style="display: flex; justify-content: left; align-items: flex-start;">
    <div style="margin-right: 20px;"> <!-- 오른쪽 간격 추가 -->
    	<div style=" width: 150px; text-align: left; font-size: 20px; margin-bottom: 10px;  margin-top: 20px;">
    	<a href="/mypage/mypage" style="color: black; text-decoration: none;font-weight: bold; ">마이페이지</a>
        </div>
        <div style="text-align: left;font-size: 15px">
	        <a href="/info/memberEdit" id="listedit" style="color: black; text-decoration: none;">정보수정</a><br>
	        
	        <c:if test="${sessionScope.user_au_lv == 0 || sessionScope.user_au_lv == 1}">
		        <a href="/info/cafe_insert" id="cfinsert" style="color: black; text-decoration: none;">카페등록</a><br>
		        <a href="/info/cafeinfo_edit" id="cfdetailedit" style="color: black; text-decoration: none;">카페정보수정</a><br>
	        </c:if>
	
	        <a href="#" id="listFavorite" style="color: black; text-decoration: none;">즐겨찾기한 카페</a><br>
	        <a href="#" id="listMemo" style="color: black; text-decoration: none;">메모목록</a><br>
	        <a href="/board/list.do" id="listBoard" style="color: black; text-decoration: none;">건의사항</a><br>
	        <a href="/info/sessionTest" id="sessionTest" style="color: black; text-decoration: none;">세션테스트</a>
	        
    	</div>
	</div>
	<div id="resultDiv" style="flex-grow: 1;"></div>
</div>
</body>

</html>