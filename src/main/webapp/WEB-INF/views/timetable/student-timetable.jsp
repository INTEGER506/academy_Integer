<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>학생 시간표</title>

  <!-- ✅ 공통 리소스 -->
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"/>
  <link rel="stylesheet" href="<c:url value='/css/style.css'/>"/>
  <link rel="icon" href="data:,">

  <!-- ✅ 타임테이블 스타일 (교수용과 동일 구조) -->
  <style>
    .timetable {
      table-layout: fixed;
      border-collapse: collapse;
      width: 100%;
    }
    .timetable th, .timetable td {
      border: 1px solid #dee2e6;
      height: 50px;
      vertical-align: middle;
      text-align: center;
    }
    .timetable th {
      background-color: #f8f9fa;
      font-weight: 600;
    }
    .course-cell {
      background-color: #cfe2ff;
      border-radius: 6px;
      padding: 2px 4px;
      font-size: 0.85rem;
      line-height: 1.3;
    }
    .course-cell small {
      color: #555;
      display: block;
      font-size: 0.8rem;
    }
  </style>
</head>

<body class="bg-page">

<%-- ✅ 헤더 --%>
<%@ include file="/WEB-INF/views/components/header.jsp" %>

<main class="py-4">
  <div class="container-1200 d-flex gap-24">

    <%-- ✅ 사이드바 --%>
    <%@ include file="/WEB-INF/views/components/sidebar.jsp" %>

    <%-- ✅ 메인 콘텐츠 --%>
    <section class="flex-1">
      <div class="card-white p-20">
        <h3 class="fw-700 text-navy mb-4">🎓 학생 시간표</h3>

        <!-- ✅ 타임테이블 (구조 동일, 내용만 학생용으로) -->
        <div class="container">
          <c:set var="days" value="${['월','화','수','목','금']}"/>
          <c:set var="periods" value="${[
                '1교시 (09:00~09:50)',
                '2교시 (10:00~10:50)',
                '3교시 (11:00~11:50)',
                '4교시 (12:00~12:50)',
                '5교시 (13:00~13:50)',
                '6교시 (14:00~14:50)',
                '7교시 (15:00~15:50)',
                '8교시 (16:00~16:50)',
                '9교시 (17:00~17:50)',
                '10교시 (18:00~18:50)'
          ]}"/>
          <c:set var="timeSlots" value="${[
                '09:00~09:50',
                '10:00~10:50',
                '11:00~11:50',
                '12:00~12:50',
                '13:00~13:50',
                '14:00~14:50',
                '15:00~15:50',
                '16:00~16:50',
                '17:00~17:50',
                '18:00~18:50'
          ]}"/>

          <div class="table-responsive">
            <table class="table timetable">
              <thead>
              <tr>
                <th style="width: 60px;">교시</th>
                <c:forEach var="day" items="${days}">
                  <th style="width: 80px;">${day}</th>
                </c:forEach>
              </tr>
              </thead>

              <tbody>
              <c:forEach var="period" items="${periods}" varStatus="status">
                <tr>
                  <th>${period}</th>
                  <c:forEach var="day" items="${days}">
                    <td>
                      <c:set var="slot" value="${timeSlots[status.index]}"/>
                      <c:set var="slotStart" value="${fn:substringBefore(slot, '~')}"/>
                      <c:set var="slotStartNum" value="${fn:replace(slotStart, ':', '')}"/>

                      <c:forEach var="course" items="${courses}">
                        <c:if test="${course.dayOfWeek eq day}">
                          <c:set var="start" value="${empty course.startTime ? fn:substringBefore(course.time, '~') : course.startTime}" />
                          <c:set var="startNum" value="${fn:replace(start, ':', '')}"/>

                          <c:if test="${slotStartNum eq startNum}">
                            <div class="course-cell">
                              ${course.subjectName}
                              <small>${course.professorName}</small>
                              <small>${course.place}</small>
                            </div>
                          </c:if>
                        </c:if>
                      </c:forEach>
                    </td>
                  </c:forEach>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>
  </div>
</main>

<%-- ✅ 푸터 --%>
<%@ include file="/WEB-INF/views/components/footer.jsp" %>

<!-- ✅ JS -->
<script src="<c:url value='/vendor/jquery/jquery-3.7.1.min.js'/>"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
