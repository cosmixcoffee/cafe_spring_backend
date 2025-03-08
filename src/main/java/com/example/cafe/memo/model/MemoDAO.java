package com.example.cafe.memo.model;

import java.util.List;

public interface MemoDAO {
	
	//리스트 조회
	//그냥 해당 카페 옆에 있는 메모버튼-> cf_number받아서 해당 카페에 대한 메모만 출력
	//그 외에는 기본값 0을 받아서 id만 넘겨줘 전체 메모 표시
	List<MemoDTO> listMemo(String userid, long cf_number);
	
	//메모삭제
	String deleteMemo(long memo_idx);
	
	//순서 이미지 받기-> 이진화->String? 으로 변환-> Blob으로 변환 ->DB에 전달
    //여기서는 사진을 전달하지 않는데 가능하면 String 이용해서 Blob으로 변환해서 전달해줄 수 있게 하기
	String insertMemo(MemoDTO dto);
		
	MemoDTO detailMemo(long memo_idx);
}
