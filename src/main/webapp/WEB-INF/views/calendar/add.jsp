<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>새 학사일정 추가</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div class="container">
    <h1>새 학사일정 추가</h1>

    <form id="addForm">
        <div>
            <label for="content">내용:</label>
            <input type="text" id="content" name="content" required>
        </div>
        <div>
            <label for="startDate">시작일:</label>
            <input type="date" id="startDate" name="startDate" required>
        </div>
        <div>
            <label for="endDate">종료일:</label>
            <input type="date" id="endDate" name="endDate">
        </div>
        <div>
            <label for="color">색상:</label>
            <input type="color" id="color" name="color">
        </div>

        <button type="submit">저장</button>
        <a href="/calendar/list">취소</a>
    </form>
</div>

<script>
    $(document).ready(function() {
        $('#addForm').on('submit', function(e) {
            e.preventDefault(); // 폼 기본 제출 방지

            const formData = {
                content: $('#content').val(),
                startDate: $('#startDate').val(),
                endDate: $('#endDate').val(),
                color: $('#color').val() || null // 값이 없으면 null 전송 (Controller에서 처리)
            };

            // REST API POST 요청
            $.ajax({
                url: '/api/calendar',
                type: 'POST',
                contentType: 'application/json', // JSON 형식으로 데이터 전송
                data: JSON.stringify(formData),
                success: function(response) {
                    alert(response); // 성공 메시지 ("일정이 성공적으로 저장되었습니다.")
                    location.href = '/calendar/list'; // 목록 페이지로 이동
                },
                error: function(xhr) {
                    let errorMessage = '일정 저장 중 오류가 발생했습니다.';
                    if (xhr.status === 403) {
                        errorMessage = '권한이 없습니다. 교직원/관리자만 일정을 등록할 수 있습니다.';
                    } else if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMessage = xhr.responseJSON.message;
                    }
                    alert(errorMessage);
                }
            });
        });
    });
</script>
</body>
</html>