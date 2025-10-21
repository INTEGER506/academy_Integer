<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>강의 수정</title>
    <style>
        .schedule-group {
            border: 1px solid #ccc;
            padding: 10px;
            border-radius: 6px;
            margin-bottom: 10px;
        }
        .removeSchedule {
            background-color: #ff7f7f;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            padding: 4px 8px;
        }
        .removeSchedule:hover { background-color: #ff4c4c; }
        button { cursor: pointer; }
    </style>
</head>
<body>
<h1>강의 수정</h1>

<form id="editForm">
    <input type="hidden" id="courseId" value="${courseUpdateRequestDTO.id}" />

    <div>
        <label>수강 정원:</label>
        <input type="number" id="capacity" value="${courseUpdateRequestDTO.capacity}" min="1" required />
    </div>

    <div id="scheduleContainer">
        <!-- 기존 스케줄 자동 표시 -->
        <c:forEach var="schedule" items="${courseUpdateRequestDTO.scheduleList}" varStatus="status">
            <div class="schedule-group">
                <label>요일:
                    <select class="dayOfWeek" required>
                        <option value="">선택</option>
                        <option value="월" ${schedule.dayOfWeek == '월' ? 'selected' : ''}>월</option>
                        <option value="화" ${schedule.dayOfWeek == '화' ? 'selected' : ''}>화</option>
                        <option value="수" ${schedule.dayOfWeek == '수' ? 'selected' : ''}>수</option>
                        <option value="목" ${schedule.dayOfWeek == '목' ? 'selected' : ''}>목</option>
                        <option value="금" ${schedule.dayOfWeek == '금' ? 'selected' : ''}>금</option>
                    </select>
                </label><br/>

                <label>시간:
                    <input type="text" class="time" value="${schedule.time}" placeholder="예: 09:00~09:50" required>
                </label><br/>

                <label>강의실:
                    <input type="text" class="place" value="${schedule.place}" placeholder="예: A101" required>
                </label>
                <button type="button" class="removeSchedule">삭제</button>
            </div>
        </c:forEach>
    </div>

    <button type="button" id="addSchedule">+ 추가</button><br/><br/>
    <button type="submit">수정 완료</button>
</form>

<div id="resultMsg" style="color:red; margin-top:10px;"></div>

<script>
    const container = document.getElementById('scheduleContainer');
    const addButton = document.getElementById('addSchedule');
    const maxSchedules = 3;

    // 삭제 버튼 (기존 스케줄 포함)
    document.querySelectorAll('.removeSchedule').forEach(btn => {
        btn.addEventListener('click', e => e.target.closest('.schedule-group').remove());
    });

    // 추가 버튼
    addButton.addEventListener('click', () => {
        const count = container.querySelectorAll('.schedule-group').length;
        if (count >= maxSchedules) {
            alert("요일/시간/강의실은 최대 3개까지만 추가할 수 있습니다.");
            return;
        }

        const group = document.createElement('div');
        group.classList.add('schedule-group');
        group.innerHTML = `
            <label>요일:
                <select class="dayOfWeek" required>
                    <option value="">선택</option>
                    <option value="월">월</option>
                    <option value="화">화</option>
                    <option value="수">수</option>
                    <option value="목">목</option>
                    <option value="금">금</option>
                </select>
            </label><br/>
            <label>시간: <input type="text" class="time" placeholder="예: 10:00~10:50" required></label><br/>
            <label>강의실: <input type="text" class="place" placeholder="예: B202" required></label>
            <button type="button" class="removeSchedule">삭제</button>
        `;
        container.appendChild(group);

        group.querySelector('.removeSchedule').addEventListener('click', () => group.remove());
    });

    // 수정 처리 (비동기 PATCH 요청)
    document.getElementById('editForm').addEventListener('submit', async e => {
        e.preventDefault();

        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert("로그인 후 이용해주세요.");
            window.location.href = '/auth/login';
            return;
        }

        const id = document.getElementById('courseId').value;
        const capacity = Number(document.getElementById('capacity').value);

        const scheduleList = [];
        document.querySelectorAll('.schedule-group').forEach(group => {
            const day = group.querySelector('.dayOfWeek').value.trim();
            const time = group.querySelector('.time').value.trim();
            const place = group.querySelector('.place').value.trim();
            if (day && time && place) {
                scheduleList.push({ dayOfWeek: day, time: time, place: place });
            }
        });

        const courseData = { capacity: capacity, scheduleList: scheduleList };

        try {
            const res = await fetch(`/api/courses/${id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(courseData)
            });

            if (res.ok) {
                alert("강의가 수정되었습니다.");
                window.location.href = '/courses';
            } else {
                const errText = await res.text();
                document.getElementById('resultMsg').innerText = "오류: " + errText;
            }
        } catch (err) {
            document.getElementById('resultMsg').innerText = "네트워크 오류: " + err.message;
        }
    });
</script>
</body>
</html>
