<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>점수 분배 비율 설정</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h2 {
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .rule-section {
            background-color: #e3f2fd;
            border: 1px solid #2196f3;
            border-radius: 4px;
            padding: 15px;
            margin-bottom: 20px;
        }
        .rule-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
        }
        .rule-table th, .rule-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }
        .rule-table th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .form-section {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            margin-right: 10px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
    </style>
</head>
<body>
        <div class="container">
            <h2>점수 분배 비율 설정</h2>
            <c:if test="${subjectName != null}">
                <div style="background-color: #e3f2fd; border: 1px solid #2196f3; border-radius: 4px; padding: 15px; margin-bottom: 20px;">
                    <h3 style="margin-top: 0; color: #1976d2;">📚 과목: ${subjectName}</h3>
                    <p style="margin-bottom: 10px; color: #666;">이 과목의 점수 분배 비율을 설정합니다.</p>
                    
                    <!-- 현재 GradeSystem 정보 표시 -->
                    <c:if test="${gs != null && gs.id != null}">
                        <div style="background-color: #f8f9fa; border: 1px solid #dee2e6; border-radius: 4px; padding: 12px; margin-top: 10px;">
                            <h4 style="margin-top: 0; margin-bottom: 8px; color: #495057; font-size: 14px;">📊 현재 설정된 비율</h4>
                            <div style="display: flex; gap: 15px; flex-wrap: wrap;">
                                <span style="color: #28a745; font-weight: bold;">중간고사: ${gs.midExamRatio.intValue()}%</span>
                                <span style="color: #007bff; font-weight: bold;">기말고사: ${gs.finalExamRatio.intValue()}%</span>
                                <span style="color: #ffc107; font-weight: bold;">과제: ${gs.assignmentRatio.intValue()}%</span>
                                <span style="color: #6c757d; font-weight: bold;">출석: ${gs.attendanceRatio.intValue()}%</span>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${gs == null || gs.id == null}">
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 12px; margin-top: 10px;">
                            <h4 style="margin-top: 0; margin-bottom: 8px; color: #856404; font-size: 14px;">⚠️ 아직 설정되지 않음</h4>
                            <p style="margin-bottom: 0; color: #856404; font-size: 13px;">이 과목의 점수 분배 비율이 아직 설정되지 않았습니다. 아래에서 설정해주세요.</p>
                        </div>
                    </c:if>
                </div>
            </c:if>
        
        <!-- 현재 과목 성적 규정 표시 -->
        <div class="rule-section">
            <h3 style="margin-top: 0; color: #1976d2;">📊 현재 과목 성적 규정 (상위 누적 비율)</h3>
            <p style="color: #666; margin-bottom: 20px;">이 과목에 적용되는 학점 규정입니다. 커스텀 규정이 없으면 글로벌 규정이 적용됩니다.</p>
            
            <table class="rule-table">
                <thead>
                    <tr>
                        <th>등급</th>
                        <th>상위 누적 비율 (%)</th>
                        <th>설명</th>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="rulesToDisplay" value="${subjectRules != null && not empty subjectRules ? subjectRules : globalRules}"/>
                    <c:if test="${empty rulesToDisplay}">
                        <tr>
                            <td colspan="3">규정 정보가 없습니다.</td>
                        </tr>
                    </c:if>
                    <c:forEach var="rule" items="${rulesToDisplay}">
                        <tr>
                            <td class="grade-label">${rule.alphabet}</td>
                            <td class="boundary-value"><fmt:formatNumber value="${rule.boundary}" pattern="0"/>%</td>
                            <td>${rule.description}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <p style="margin-top: 15px; font-size: 0.9em; color: #666;">총점 30점 이하인 경우 자동으로 F 학점이 부여됩니다.</p>
        </div>

        <!-- 점수 비율 설정 폼 -->
        <div class="form-section">
            <h3 style="margin-top: 0; color: #28a745;">⚙️ 점수 비율 설정</h3>

<form method="post" action="${pageContext.request.contextPath}/grade/professor/system/edit">
    <input type="hidden" name="id" value="${gs.id}"/>
    <input type="hidden" name="courseId" value="${param.courseId != null ? param.courseId : gs.courseId}"/>
    <input type="hidden" name="subjectId" value="${param.subjectId}"/>
    <input type="hidden" name="professorId" value="${professorId}"/>

    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">중간고사 비율 (%)</label>
        <input type="number" name="midExamRatio" min="0" max="100" step="1" 
               value="${gs.midExamRatio != null ? gs.midExamRatio : 30}" required style="width: 100px;"/>
    </div>
    
    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">기말고사 비율 (%)</label>
        <input type="number" name="finalExamRatio" min="0" max="100" step="1" 
               value="${gs.finalExamRatio != null ? gs.finalExamRatio : 40}" required style="width: 100px;"/>
    </div>
    
    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">과제 비율 (%)</label>
        <input type="number" name="assignmentRatio" min="0" max="100" step="1" 
               value="${gs.assignmentRatio != null ? gs.assignmentRatio : 20}" required style="width: 100px;"/>
    </div>
    
    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">출석 비율 (%)</label>
        <input type="number" name="attendanceRatio" min="0" max="100" step="1" 
               value="${gs.attendanceRatio != null ? gs.attendanceRatio : 10}" required style="width: 100px;"/>
    </div>

    <!-- 총합 표시 및 검증 -->
    <div id="totalDisplay" style="margin: 20px 0; padding: 10px; background-color: #f0f0f0; border-radius: 5px;">
        <strong>총합: <span id="totalSum">0</span>%</strong>
        <span id="validationMessage" style="margin-left: 10px;"></span>
    </div>

            <div style="margin-top: 20px;">
                <button type="submit" id="submitBtn" class="btn" disabled>저장</button>
                <a href="${pageContext.request.contextPath}/grade/professor/system-list?professorId=${professorId}" 
                   class="btn btn-secondary">목록으로</a>
            </div>
        </form>
    </div>
</div>

<script>
    // 실시간 총합 계산 및 검증
    function updateTotal() {
        const midExam = Number(document.querySelector('input[name="midExamRatio"]').value || 0);
        const finalExam = Number(document.querySelector('input[name="finalExamRatio"]').value || 0);
        const assignment = Number(document.querySelector('input[name="assignmentRatio"]').value || 0);
        const attendance = Number(document.querySelector('input[name="attendanceRatio"]').value || 0);
        
        const total = midExam + finalExam + assignment + attendance;
        
        document.getElementById('totalSum').textContent = total;
        
        const validationMessage = document.getElementById('validationMessage');
        const submitBtn = document.getElementById('submitBtn');
        
        if (total === 100) {
            validationMessage.textContent = '✓ 정상';
            validationMessage.style.color = 'green';
            submitBtn.disabled = false;
        } else {
            validationMessage.textContent = '✗ 총합이 100%가 아닙니다';
            validationMessage.style.color = 'red';
            submitBtn.disabled = true;
        }
    }

    // 모든 입력 필드에 이벤트 리스너 추가
    document.querySelectorAll('input[type="number"]').forEach(input => {
        input.addEventListener('input', updateTotal);
    });

    // 페이지 로드 시 초기 계산
    updateTotal();
</script>
