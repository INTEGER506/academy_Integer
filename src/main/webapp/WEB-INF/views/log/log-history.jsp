<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 대시보드</title>

    <!-- 폰트/부트스트랩 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="data:,">
</head>

<body class="bg-page">

<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
    <div class="container-1200 d-flex gap-24">

        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <section class="flex-1">
            <div class="card-white p-20">
                <h4 class="mb-3">접속 기록 관리</h4>

                <div class="table-responsive" style="max-height: 500px; overflow-y: auto;">
                    <table class="table table-striped table-hover align-middle">
                        <thead class="table-light">
                        <tr>
                            <th scope="col">로그 ID</th>
                            <th scope="col">사용자 ID</th>
                            <th scope="col">IP 주소</th>
                            <th scope="col">로그인 시간</th>
                            <th scope="col">로그아웃 시간</th>
                        </tr>
                        </thead>
                        <tbody id="tbody">
                        <tr>
                            <td colspan="5">데이터 로딩중...</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<script>
    function loadHistory(){
        const tableBody = document.getElementById('tbody');
        const token = localStorage.getItem('accessToken');
        console.log("읽은 토큰:", token);

        if(!token){
            tableBody.innerHTML = '<tr><td colspan="5">인증 정보가 없습니다.</td></tr>';
            return;
        }

        fetch('/api/admin/logs/history', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if(!response.ok){
                    throw new Error('네트워크 응답이 올바르지 않습니다.');
                }
                return response.json();
            })
            .then(logs => {
                console.table(logs);    //데이터 확인용

                if(logs.length > 0){
                    let rows = '';

                    try{
                        logs.forEach(log => {
                            const id = log.id;
                            const username = log.username;
                            const ipAddress = log.ipAddress;
                            const loginTime = log.loginTimeStr || '-';
                            const logoutTime = log.logoutTimeStr || '-';
                            rows += '<tr>'
                                + '<td>' + id + '</td>'
                                + '<td>' + username + '</td>'
                                + '<td>' + ipAddress + '</td>'
                                + '<td>' + loginTime + '</td>'
                                + '<td>' + logoutTime + '</td>'
                                + '</tr>';
                        });
                        tableBody.innerHTML = rows;
                    }catch (e) {
                        //에러가 발생하더라도 로그는 찍히도록
                        console.error('데이터 바인딩중 에러 발생: ', e.message);
                        tableBody.innerHTML = '<tr><td colspan="5>데이터 출력중 오류 발생, 콘솔 확인"</td></tr>';
                    }
                }else {
                    tableBody.innerHTML = '<tr><td colspan="5">접속 기록이 없습니다.</td></tr>';
                }
            })
            .catch(error => {
                console.error('데이터를 가져오는 중 오류가 발생: ', error);
                tableBody.innerHTML = '<tr><td colspan="5">데이터 로딩에 실패했습니다.</td></tr>';
            });
    }

    document.addEventListener('DOMContentLoaded', function (){
        loadHistory();      //페이지 로드 시 바로 실행
        setInterval(loadHistory, 5000); //5초마다 자동 새로고침
    });
</script>
</body>
</html>
