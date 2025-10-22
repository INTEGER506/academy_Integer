<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>학사정보관리시스템 - 강의 개설</title>

    <!-- 공통 리소스 -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
    <link rel="icon" href="data:,">

    <style>
        /* ================================
           🔹 강의 개설 페이지 전용 스타일
           (헤더/푸터/사이드바 영향 없음)
        ================================= */
        section.flex-1 .card-white {
            border-radius: 16px;
            background: #fff;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            padding: 40px;
            transition: box-shadow 0.2s ease;
        }

        section.flex-1 .card-white:hover {
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
        }

        section.flex-1 .form-title {
            font-weight: 700;
            color: #0F2940;
            margin-bottom: 1.5rem;
        }

        /* 🔹 입력 영역 */
        #courseForm .form-control-lg {
            border-radius: 10px;
            border: 1px solid #d0d7de;
            font-size: 15px;
            padding: 10px 14px;
        }

        #courseForm .form-control-lg:focus {
            border-color: #0F2940;
            box-shadow: 0 0 0 2px rgba(15, 41, 64, 0.15);
        }

        /* 🔹 강의 시간표 박스 */
        .schedule-wrapper {
            background-color: #f9fafb;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 28px 32px;
        }

        .schedule-wrapper h6 {
            color: #0F2940;
            font-weight: 600;
            margin-bottom: 1rem;
        }

        .schedule-group {
            background-color: #fff;
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            padding: 16px 20px;
            margin-bottom: 16px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.05);
        }

        .schedule-group .d-flex {
            flex-wrap: wrap;
            gap: 14px 20px;
            align-items: center;
        }

        /* 🔹 버튼 */
        .btn-navy {
            background-color: #0F2940;
            color: #fff;
            border: none;
            transition: all 0.2s ease;
        }

        .btn-navy:hover {
            background-color: #122f52;
            color: #fff;
        }

        .btn-outline-secondary {
            color: #495057;
            border-color: #d0d7de;
        }

        .btn-outline-secondary:hover {
            background-color: #f1f3f5;
            color: #0F2940;
        }

        .removeSchedule {
            background-color: #dc3545;
            color: #fff;
            border: none;
            border-radius: 6px;
            padding: 4px 10px;
            transition: all 0.2s;
        }

        .removeSchedule:hover {
            background-color: #bb2d3b;
        }

        /* 🔹 추가 버튼 */
        #addSchedule {
            font-weight: 600;
            color: #0F2940;
            background: transparent;
            border: none;
            padding: 4px 0;
            cursor: pointer;
        }

        #addSchedule:hover {
            text-decoration: underline;
        }

        /* 🔹 반응형 */
        @media (max-width: 768px) {
            .schedule-group .d-flex {
                flex-direction: column;
                align-items: flex-start;
            }
        }
    </style>
</head>

<body class="bg-page">
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-5">
    <div class="container-1200 d-flex gap-24">
        <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

        <!-- ✅ 메인 콘텐츠 (스타일만 적용) -->
        <section class="flex-1">
            <div class="card-white">
                <h3 class="form-title d-flex align-items-center gap-2">
                    <i class="bi bi-journal-plus text-navy"></i> 강의 개설
                </h3>

                <sec:authorize access="hasRole('PROFESSOR')">
                    <form id="courseForm">

                        <!-- 기본 정보 -->
                        <div class="row g-4">
                            <div class="col-md-4">
                                <label class="form-label fw-500">학기 ID</label>
                                <input type="number" id="semesterId" class="form-control form-control-lg" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-500">과목 ID</label>
                                <input type="number" id="subjectId" class="form-control form-control-lg" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-500">정원</label>
                                <input type="number" id="capacity" class="form-control form-control-lg" min="4" max="30" required>
                            </div>
                        </div>

                        <!-- 강의 시간표 -->
                        <div class="schedule-wrapper mt-3">
                            <h6><i class="bi bi-calendar-week"></i> 강의 시간표</h6>
                            <div id="scheduleContainer">
                                <div class="schedule-group">
                                    <div class="d-flex">
                                        <label>요일:
                                            <select class="dayOfWeek form-select form-select-sm d-inline w-auto ms-1" required>
                                                <option value="">선택</option>
                                                <option value="월">월</option>
                                                <option value="화">화</option>
                                                <option value="수">수</option>
                                                <option value="목">목</option>
                                                <option value="금">금</option>
                                            </select>
                                        </label>
                                        <label>시간:
                                            <select class="time form-select form-select-sm d-inline w-auto ms-1" required>
                                                <option value="">선택</option>
                                                <option value="09:00~09:50">09:00~09:50</option>
                                                <option value="10:00~10:50">10:00~10:50</option>
                                                <option value="11:00~11:50">11:00~11:50</option>
                                                <option value="12:00~12:50">12:00~12:50</option>
                                                <option value="13:00~13:50">13:00~13:50</option>
                                                <option value="14:00~14:50">14:00~14:50</option>
                                                <option value="15:00~15:50">15:00~15:50</option>
                                                <option value="16:00~16:50">16:00~16:50</option>
                                                <option value="17:00~17:50">17:00~17:50</option>
                                                <option value="18:00~18:50">18:00~18:50</option>
                                            </select>
                                        </label>
                                        <label>강의실:
                                            <input type="text" class="place form-control form-control-sm d-inline w-auto ms-1" placeholder="예: A101" required>
                                        </label>
                                        <button type="button" class="removeSchedule btn btn-sm ms-auto">
                                            <i class="bi bi-x-lg"></i> 삭제
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <button type="button" id="addSchedule" class="mt-2">
                                <i class="bi bi-plus-lg"></i> 시간 추가
                            </button>
                        </div>

                        <div id="resultMsg" class="text-danger small mt-1"></div>

                        <div class="btn-area mt-4 text-end">
                            <a href="<c:url value='/professor/courses'/>" class="btn btn-outline-secondary rounded-pill px-4 me-2">
                                <i class="bi bi-arrow-left"></i> 목록으로
                            </a>
                            <button type="submit" class="btn btn-navy rounded-pill px-4">
                                <i class="bi bi-save"></i> 강의 개설
                            </button>
                        </div>
                    </form>
                </sec:authorize>

                <sec:authorize access="isAnonymous()">
                    <p class="text-center text-gray-600">로그인 후 이용해주세요.</p>
                </sec:authorize>
            </div>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- 공통 스크립트 -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    const container = document.getElementById('scheduleContainer');
    const addButton = document.getElementById('addSchedule');
    const maxSchedules = 3;

    document.querySelectorAll('.removeSchedule').forEach(btn => {
        btn.addEventListener('click', e => e.target.closest('.schedule-group').remove());
    });

    addButton.addEventListener('click', () => {
        const count = container.querySelectorAll('.schedule-group').length;
        if (count >= maxSchedules) {
            alert("요일/시간/강의실은 최대 3개까지만 추가할 수 있습니다.");
            return;
        }

        const group = document.createElement('div');
        group.classList.add('schedule-group');
        group.innerHTML = `
            <div class="d-flex">
                <label>요일:
                    <select class="dayOfWeek form-select form-select-sm d-inline w-auto ms-1" required>
                        <option value="">선택</option>
                        <option value="월">월</option>
                        <option value="화">화</option>
                        <option value="수">수</option>
                        <option value="목">목</option>
                        <option value="금">금</option>
                    </select>
                </label>
                <label>시간:
                    <select class="time form-select form-select-sm d-inline w-auto ms-1" required>
                        <option value="">선택</option>
                        <option value="09:00~09:50">09:00~09:50</option>
                        <option value="10:00~10:50">10:00~10:50</option>
                        <option value="11:00~11:50">11:00~11:50</option>
                        <option value="12:00~12:50">12:00~12:50</option>
                        <option value="13:00~13:50">13:00~13:50</option>
                        <option value="14:00~14:50">14:00~14:50</option>
                        <option value="15:00~15:50">15:00~15:50</option>
                        <option value="16:00~16:50">16:00~16:50</option>
                        <option value="17:00~17:50">17:00~17:50</option>
                        <option value="18:00~18:50">18:00~18:50</option>
                    </select>
                </label>
                <label>강의실:
                    <input type="text" class="place form-control form-control-sm d-inline w-auto ms-1" placeholder="예: B202" required>
                </label>
                <button type="button" class="removeSchedule btn btn-sm btn-danger ms-auto">
                    <i class="bi bi-x-lg"></i> 삭제
                </button>
            </div>
        `;
        container.appendChild(group);
        group.querySelector('.removeSchedule').addEventListener('click', () => group.remove());
    });

    document.getElementById('courseForm').addEventListener('submit', async e => {
        e.preventDefault();

        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert("로그인 후 이용해주세요.");
            window.location.href = '/auth/login';
            return;
        }

        const semesterId = Number(document.getElementById('semesterId').value);
        const subjectId = Number(document.getElementById('subjectId').value);
        const capacity = Number(document.getElementById('capacity').value);

        const scheduleList = [];
        document.querySelectorAll('.schedule-group').forEach(group => {
            const day = group.querySelector('.dayOfWeek').value.trim();
            const time = group.querySelector('.time').value.trim();
            const place = group.querySelector('.place').value.trim();
            if (day && time && place) scheduleList.push({ dayOfWeek: day, time, place });
        });

        const courseData = { semesterId, subjectId, capacity, scheduleList };

        try {
            const res = await fetch('/api/courses', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(courseData)
            });

            if (res.status === 201) {
                alert("강의가 성공적으로 개설되었습니다.");
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
