<%--
  Created by IntelliJ IDEA.
  User: goott3-1s
  Date: 2025-09-17
  Time: 오후 4:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>시스템 관리 - 로그 모니터링</title>
</head>
<body>

<div>
    <button onclick="loadLogs()">새로고침</button>
    <pre id="logArea" style="background: black; color: white; padding: 10px; height: 400px;"></pre>
</div>

<script>
    async function loadLogs(){
        try{
            const res = await fetch('/api/admin/logs/monitor?lines=100&filter=ERROR');
            const logs = await res.json();

            const logArea = document.getElementById('logArea');
            logArea.innerText = logs.json("\n");

            //맨 아래로 자동 스크롤
            logArea.scrollTop = logArea.scrollHeight;
        } catch (e) {
            console.error("로그 로딩 실패: ", e);
        }
    }
    //페이지 로드 시 바로 실행
    loadLogs();

    //5초마다 자동 새로고침
    setInterval(loadLogs, 5000);
</script>

</body>
</html>
