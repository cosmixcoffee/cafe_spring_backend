<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table border="1" width="100%">
	    <tr>
	        <th>작성자</th>
	        <th>내용</th>
	        <th>평점</th>
	        <th>작성일</th>
	    </tr>
	    <c:choose>
	        <c:when test="${not empty reviewList}">
	            <c:forEach var="review" items="${reviewList}">
	                <tr>
	                    <td align="center">${review.userid}</td>
	                    <td align="center">${review.rv_content}</td>
	                    <td align="center">${review.cf_point}점</td>
	                    <td align="center">
                        	<fmt:formatDate value="${review.w_date}" pattern="yyyy-MM-dd" />
                   		</td>
	                </tr>
	            </c:forEach>
	        </c:when>
	        <c:otherwise>
	            <tr>
	                <td colspan="4" align="center">등록된 후기가 없습니다.</td>
	            </tr>
	        </c:otherwise>
	    </c:choose>
	    <tr align="center">
			<td colspan="10">
				<c:if test="${page.curPage > 1}">
					<a href="#" onclick="list('1')" >[처음]</a>
				</c:if>
				<c:if test="${page.curBlock > 1}">
					<a href="#" onclick="list('${page.prevPage}')">[이전]</a>
				</c:if>
				
				<c:forEach var="num" begin="${page.blockStart}" end = "${page.blockEnd}">
					<c:choose>
						<c:when test="${num == page.curPage}">
							<span style="color:black">${num}</span>
						</c:when>
						<c:otherwise>
							<a href="#" onclick="list('${num}')">${num}</a>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:if test="${page.curBlock < page.totBlock}">
					<a href="#" onclick="list('${page.nextPage}')">[다음]</a>
				</c:if>
				<c:if test="${page.curPage < page.totPage}">
					<a href="#" onclick="list('${page.totPage}')">[마지막]</a>
				</c:if>
			</td>
		</tr>
	</table>
	</body>
</html>