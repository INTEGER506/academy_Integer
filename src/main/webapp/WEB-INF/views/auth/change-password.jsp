<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>비밀번호 변경</title>

</head>

<body>
<div id="message" role="alert" class="hidden"></div>

<form id="passwordChangeForm" method="post">
    <div>
        <label for="currentPassword">현재 비밀번호: </label>
        <input type="password" id="currentPassword" name="currentPassword" required>
    </div>
    <div>
        <label for="newPassword">새 비밀번호: </label>
        <input type="password" id="newPassword" name="newPassword" required>
    </div>
    <div>
        <label for="confirmPassword">새 비밀번호 확인: </label>
        <input type="password" id="confirmPassword" name="confirmPassword" required>
    </div>
    <div>
        <button type="submit">비밀번호 변경</button>
    </div>
</form>

<script>
    const form = document.getElementById('passwordChangeForm');
    const messageDiv = document.getElementById('message');

    form.addEventListener('submit', function (event){
        event.preventDefault();

        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        //클라이언트 유효성 검증
        if(newPassword !== confirmPassword){
            showMessage("새 비밀번호가 일치하지 않습니다.", "error");
            return;
        }
        if(newPassword.length < 8){
            showMessage("새 비밀번호는 최소 8자 이상이어야 합니다.", "error");
            return;
        }

        fetch('${pageContext.request.contextPath}/api/auth/change-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword
            })
        })
            .then(response => {
                if(!response.ok){
                    return response.text().then(text => { throw new Error(text); });
                }
                return response.text();
            })
            .then(data => {
                showMessage(data, "success");
                form.reset();
            })
            .catch(error => {
                showMessage(error.message, "error");
            });
    });
    function showMessage(text, type) {
        messageDiv.textContent = text;
        messageDiv.className = `message ${type}`;
        messageDiv.classList.remove('hidden');
    }
</script>
</body>
</html>
