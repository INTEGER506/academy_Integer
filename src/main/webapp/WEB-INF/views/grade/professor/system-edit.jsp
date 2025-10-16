<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
        
        <!-- 현재 과목 규정 표시 -->
        <div class="rule-section">
            <h3 style="margin-top: 0; color: #1976d2;">📊 현재 과목 성적 규정</h3>
            
            <c:if test="${ruleError != null}">
                <div class="error-message">
                    <strong>❌ 오류:</strong> ${ruleError}
                </div>
            </c:if>
            
            <c:if test="${subjectRule != null}">
                <p><strong>과목:</strong> ${subjectRule.subjectName}</p>
                <table class="rule-table">
                    <thead>
                        <tr>
                            <th>과목 이름</th>
                            <th>A+</th>
                            <th>A</th>
                            <th>B+</th>
                            <th>B</th>
                            <th>C+</th>
                            <th>C</th>
                            <th>D+</th>
                            <th>D</th>
                            <th>F</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>퍼센트</td>
                            <c:forEach var="entry" items="${subjectRule.percentages}">
                                <td>${entry.value}%</td>
                            </c:forEach>
                        </tr>
                    </tbody>
                </table>
            </c:if>
            
            <c:if test="${subjectRule == null && globalRules != null}">
                <p><strong>과목:</strong> 글로벌 규정 적용</p>
                <table class="rule-table">
                    <thead>
                        <tr>
                            <th>과목 이름</th>
                            <th>A+</th>
                            <th>A</th>
                            <th>B+</th>
                            <th>B</th>
                            <th>C+</th>
                            <th>C</th>
                            <th>D+</th>
                            <th>D</th>
                            <th>F</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>퍼센트</td>
                            <c:forEach var="rule" items="${globalRules}">
                                <td>${rule.boundary}%</td>
                            </c:forEach>
                        </tr>
                    </tbody>
                </table>
            </c:if>
        </div>

        <!-- 점수 비율 설정 폼 -->
        <div class="form-section">
            <h3 style="margin-top: 0; color: #28a745;">⚙️ 점수 비율 설정</h3>

<form method="post" action="${pageContext.request.contextPath}/grade/professor/system/edit">
    <input type="hidden" name="id" value="${gs.id}"/>
    <input type="hidden" name="courseId" value="${gs.courseId}"/>

    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">중간고사 비율 (%)</label>
        <input type="number" name="midExamRatio" min="0" max="100" step="1" 
               value="${gs.midExamRatio}" required style="width: 100px;"/>
    </div>
    
    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">기말고사 비율 (%)</label>
        <input type="number" name="finalExamRatio" min="0" max="100" step="1" 
               value="${gs.finalExamRatio}" required style="width: 100px;"/>
    </div>
    
    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">과제 비율 (%)</label>
        <input type="number" name="assignmentRatio" min="0" max="100" step="1" 
               value="${gs.assignmentRatio}" required style="width: 100px;"/>
    </div>
    
    <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 5px;">출석 비율 (%)</label>
        <input type="number" name="attendanceRatio" min="0" max="100" step="1" 
               value="${gs.attendanceRatio}" required style="width: 100px;"/>
    </div>

    <!-- 총합 표시 및 검증 -->
    <div id="totalDisplay" style="margin: 20px 0; padding: 10px; background-color: #f0f0f0; border-radius: 5px;">
        <strong>총합: <span id="totalSum">0</span>%</strong>
        <span id="validationMessage" style="margin-left: 10px;"></span>
    </div>

            <div style="margin-top: 20px;">
                <button type="submit" id="submitBtn" class="btn" disabled>저장</button>
                <a href="${pageContext.request.contextPath}/grade/professor/system-list?courseId=${gs.courseId}" 
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
