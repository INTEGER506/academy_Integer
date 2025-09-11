<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>로그인 Test</title>
</head>
<body>
<h2>로그인 테스트</h2>
<hr>

<form id="loginForm">
    <label>아이디: <input type="text" id="username" name="username" required /></label><br/>
    <label>비밀번호: <input type="password" id="password" name="password" required /></label><br/>
    <button type="submit">로그인</button>
</form>

<script>
    document.getElementById("loginForm").addEventListener("submit", function (e) {
        e.preventDefault(); // 기본 form 제출 막기
        login();
    });
    function login() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        const data = { username, password };

        fetch('/api/auth/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        })
            .then(response => {
                if(!response.ok){
                    throw new Error("로그인 실패");
                }
                return response.json();
            })
            .then(result => {
                const accessToken = result.accessToken;
                alert("로그인 성공! 토큰: " + accessToken);

                localStorage.setItem("jwtToken", accessToken);
            })
            .catch(error => {
                console.error('Error', error);
                alert(error.message);
            });
    }
</script>

</body>
</html>
