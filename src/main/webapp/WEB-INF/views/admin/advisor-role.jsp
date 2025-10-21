<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>학과별 지도교수 지정/해제 - 관리자</title>
</head>
<body>
<form id="assignAdvisorForm">
    <label for="professorId">지도교수로 지정할 교수: </label>
    <br>
    <select id="professorId" name="professorUserId" required>
        <option value="">-- 교수 선택 --</option>

        <c:forEach var="professor" items="${professorList}">
            <option value="${professor.id}">
                ${professor.name} (${professor.username})
            </option>
        </c:forEach>
    </select>
    <br><br>

    <label for="deptId">대상 학과: </label>
    <br>
    <select id="deptId" name="deptId" required>
        <option value="">--학과 선택--</option>

        <c:forEach var="dept" items="${deptList}">
            <option value="${dept.id}">
                ${dept.deptName}
            </option>
        </c:forEach>
    </select>
    <br><br>

    <button type="submit">지도교수 지정</button>
</form>

<hr style="margin: 30px 0;">

<form id="revertAdvisorForm">
    <label for="professorIdRevert">권한 회수할 지도교수: </label>
    <br>
    <select id="professorIdRevert" name="professorUserId" required>
        <option value="">-- 지도교수 선택 --</option>

        <c:forEach var="professor" items="${professorList}">
            <option value="${professor.id}">
                ${professor.name} (${professor.username})
            </option>
        </c:forEach>
    </select>
    <br><br>

    <button type="submit">권한 회수</button>
</form>

<div id="message" style="margin-top: 20px;"></div>

<script>
    document.addEventListener('DOMContentLoaded', function (){
        const assignForm = document.getElementById('assignAdvisorForm');
        const revertForm = document.getElementById('revertAdvisorForm');
        const messageDiv = document.getElementById('message');

        //응답 처리 공통 함수
        function handleResponse(request, loadingMessage){
            //처리중 메세지
            messageDiv.innerHTML = loadingMessage;

            request
                .then(response => response.text())
                .then(text => {
                    //서버 응답 출력
                    messageDiv.innerHTML = text;
                })
                .catch(error => {
                    console.error(error);
                    messageDiv.innerHTML = "오류 발생";
                });
        }

        //지도교수 지정
        assignForm.addEventListener('submit', function (event){
            event.preventDefault();
            const formData = new FormData(assignForm);
            const params = new URLSearchParams(formData);

            const request = fetch('/api/admin/assign-advisor', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: params
            });
            handleResponse(request, '지도교수 지정 처리중...');
        });

        //지도교수 권한 회수
        revertForm.addEventListener('submit', function (event){
            event.preventDefault();
            const formData = new FormData(revertForm);
            const params = new URLSearchParams(formData);

            const request = fetch('/api/admin/revert-advisor-role', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: params
            });
            handleResponse(request, '권한 회수 처리중...');
        });
    });
</script>
</body>
</html>
