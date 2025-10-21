<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>과목별 규정 목록</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        .grade-display {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .grade-item {
            display: flex;
            align-items: center;
            gap: 5px;
        }
        .grade-label {
            font-weight: bold;
            min-width: 25px;
        }
        .grade-percentage {
            background-color: #e9ecef;
            padding: 2px 6px;
            border-radius: 3px;
            min-width: 40px;
            text-align: center;
        }
        .edit-mode .grade-percentage {
            display: none;
        }
        .edit-mode input {
            width: 50px;
            padding: 2px 4px;
            border: 1px solid #ccc;
            border-radius: 3px;
        }
        .btn-edit, .btn-reset {
            padding: 6px 12px;
            margin: 2px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
        }
        .btn-edit {
            background-color: #007bff;
            color: white;
        }
        .btn-reset {
            background-color: #6c757d;
            color: white;
        }
        .edit-controls {
            display: none;
        }
        .edit-controls button {
            padding: 6px 12px;
            margin: 2px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
        }
        .btn-save {
            background-color: #28a745;
            color: white;
        }
        .btn-cancel {
            background-color: #dc3545;
            color: white;
        }
        .edit-mode .btn-edit,
        .edit-mode .btn-reset {
            display: none;
        }
        .edit-mode .edit-controls {
            display: block;
        }
        .total-percentage {
            font-weight: bold;
            color: #dc3545;
        }
        .navigation-buttons {
            text-align: center;
            margin-top: 20px;
        }
        .navigation-buttons button {
            padding: 10px 20px;
            margin: 0 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-global {
            background-color: #6c757d;
            color: white;
        }
        .btn-test {
            background-color: #17a2b8;
            color: white;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>과목별 규정 목록</h1>
        <p>각 과목별로 커스텀 성적 분배 비율을 설정할 수 있습니다. (상위 누적 비율)</p>
        
        <!-- 검색 바 - 범용 패턴 적용 -->
        <jsp:include page="/WEB-INF/views/common/searchBar.jsp">
            <jsp:param name="formAction" value="${pageContext.request.contextPath}/grade/admin/subject-rules/list"/>
            <jsp:param name="optionValues" value="subject|id"/>
            <jsp:param name="optionLabels" value="과목명|과목ID"/>
            <jsp:param name="pageSizeOptions" value="5|10|20|50"/>
            <jsp:param name="placeHolder" value="검색어를 입력하세요"/>
            <jsp:param name="keep" value=""/>
            <jsp:param name="req" value="${req}"/>
        </jsp:include>
        
        <!-- 과목 목록 테이블 -->
        <table>
    <thead>
                <tr>
                    <th>과목ID</th>
                    <th>과목명</th>
                    <th>규정 타입</th>
                    <th>등급별 상위 누적 비율</th>
                    <th>작업</th>
    </tr>
    </thead>
    <tbody>
                <c:choose>
                    <c:when test="${empty result.data}">
                        <tr>
                            <td colspan="5" style="text-align: center; padding: 20px;">
                                <p>등록된 과목이 없습니다.</p>
                                <p>과목 관리 기능에서 과목을 먼저 등록해주세요.</p>
            </td>
        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="subject" items="${result.data}" varStatus="status">
                            <tr data-subject-id="${subject.subjectId}">
                                <td>${subject.subjectId}</td>
                                <td>${subject.subjectName}</td>
                                <!-- 디버깅용 주석 -->
                                <!-- 과목 ${status.index + 1}: ID=${subject.subjectId}, 이름=${subject.subjectName}, 규정타입=${subject.ruleType} -->
                        <td>${subject.ruleType == 'CUSTOM' ? '커스텀' : '글로벌'}</td>
                                <td>
                        <div class="rules-display">
                                <div class="grade-display">
                                    <c:forEach var="grade" items="${['A+', 'A', 'B+', 'B', 'C+', 'C', 'D']}">
                                        <div class="grade-item">
                                            <span class="grade-label">${grade}</span>
                                            <span class="grade-percentage">
                                                <c:choose>
                                                    <c:when test="${subject.ruleType == 'CUSTOM'}">
                                                        <c:set var="foundValue" value="false" />
                                                        <c:forEach var="rule" items="${subject.rules}">
                                                            <c:if test="${rule.alphabet == grade}">
                                                                <fmt:formatNumber value="${rule.boundary}" pattern="0"/>%
                                                                <c:set var="foundValue" value="true" />
                                                            </c:if>
                                                        </c:forEach>
                                                        <c:if test="${!foundValue}">
                                                            <fmt:formatNumber value="${globalRules[grade]}" pattern="0"/>%
                                                        </c:if>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <fmt:formatNumber value="${globalRules[grade]}" pattern="0"/>%
                                                    </c:otherwise>
                                                </c:choose>
                                                        </span>
                                                    </div>
                                    </c:forEach>
                                </div>
                                <div class="edit-controls">
                                    <button onclick="saveInlineEdit(this)" class="btn-save" data-subject-id="${subject.subjectId}">💾 저장</button>
                                    <button onclick="cancelInlineEdit(this)" class="btn-cancel" data-subject-id="${subject.subjectId}">❌ 취소</button>
                                                    </div>
                                    </div>
                                </td>
                                <td>
                        <td>
                            <button onclick="startInlineEdit(this)" class="btn-edit" data-subject-id="${subject.subjectId}">✏️ 수정</button>
                            <button onclick="resetToGlobal(this)" class="btn-reset" data-subject-id="${subject.subjectId}">🔄 초기화</button>
            </td>
        </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
    </tbody>
</table>

        <!-- 페이지네이션 - 범용 패턴 적용 -->
        <jsp:include page="/WEB-INF/views/common/page.jsp">
            <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/admin/subject-rules/list"/>
            <jsp:param name="req" value="${req}"/>
            <jsp:param name="result" value="${result}"/>
            <jsp:param name="keepParams" value="${keepParams}"/>
        </jsp:include>
        
        <!-- 하단 버튼들 -->
        <div class="navigation-buttons">
            <button onclick="goToGlobalRules()" class="btn-global">글로벌 규정 목록</button>
            <button onclick="goToTestPage()" class="btn-test">테스트 페이지로</button>
        </div>
</div>

<script>
        // 원본 값 저장용
        const originalValues = {};
        
        // 글로벌 규정 저장용
        let globalRules = {
            'A+': ${globalRules['A+'] != null ? globalRules['A+'] : 0.0},
            'A': ${globalRules['A'] != null ? globalRules['A'] : 0.0},
            'B+': ${globalRules['B+'] != null ? globalRules['B+'] : 0.0},
            'B': ${globalRules['B'] != null ? globalRules['B'] : 0.0},
            'C+': ${globalRules['C+'] != null ? globalRules['C+'] : 0.0},
            'C': ${globalRules['C'] != null ? globalRules['C'] : 0.0},
            'D+': ${globalRules['D+'] != null ? globalRules['D+'] : 0.0},
            'D': ${globalRules['D'] != null ? globalRules['D'] : 0.0},
            'F': ${globalRules['F'] != null ? globalRules['F'] : 0.0}
        };
        
        console.log('서버에서 전달받은 글로벌 규정:', globalRules);

// 인라인 편집 시작
        function startInlineEdit(button) {
            const subjectId = button.getAttribute('data-subject-id');
    console.log('startInlineEdit called with subjectId:', subjectId);
            alert('수정 버튼 클릭됨! subjectId: ' + subjectId);
            
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
    if (!row) {
        console.error('Row not found for subjectId:', subjectId);
        return;
    }
    
    const rulesDisplay = row.querySelector('.rules-display');
            if (!rulesDisplay) {
                console.error('Rules display not found for subjectId:', subjectId);
                return;
            }
    
    // 편집 모드 활성화
    rulesDisplay.classList.add('edit-mode');
            
            // 기존 입력 필드 제거
            const existingInputs = rulesDisplay.querySelectorAll('input[type="number"]');
            existingInputs.forEach(input => input.remove());
    
    // 원본 값 저장
    originalValues[subjectId] = {};
    const percentages = rulesDisplay.querySelectorAll('.grade-percentage');
            const grades = ['A+', 'A', 'B+', 'B', 'C+', 'C', 'D'];
            
            // 입력 필드 생성
            percentages.forEach((span, index) => {
                const grade = grades[index];
                if (!grade) return;
                
                // 원본 값 저장
                originalValues[subjectId][grade] = span.textContent;
                
                // 입력 필드 생성
        const input = document.createElement('input');
        input.type = 'number';
        input.name = 'boundary_' + grade;
        input.value = span.textContent.replace('%', '');
        input.min = '0';
        input.max = '100';
        input.step = '1';
                input.setAttribute('data-grade', grade);
                
        input.addEventListener('input', () => {
                    // 소수점 제거 (정수만 허용)
                    if (input.value.includes('.')) {
                        input.value = Math.floor(parseFloat(input.value) || 0);
                    }
                    
                    // 음수 값 방지
                    if (input.value < 0) {
                        input.value = 0;
                    }
                    // 100 초과 값 방지
                    if (input.value > 100) {
                        input.value = 100;
            }
            updateTotalPercentage(subjectId);
        });
        
                // 키 입력 시 음수 및 소수점 방지
                input.addEventListener('keydown', (e) => {
                    // 마이너스(-), e, E, 소수점(.) 키 방지
                    if (e.key === '-' || e.key === 'e' || e.key === 'E' || e.key === '.') {
                        e.preventDefault();
                    }
                });
                
                // span 숨기기
                span.style.display = 'none';
                // 입력 필드를 span 다음에 삽입
        span.parentNode.insertBefore(input, span.nextSibling);
    });
            
    updateTotalPercentage(subjectId);
}

        // 총 비율 업데이트
function updateTotalPercentage(subjectId) {
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
            if (!row) return;
    
            const inputs = row.querySelectorAll('input[type="number"]');
    let total = 0;
    
        inputs.forEach(input => {
            const value = parseFloat(input.value) || 0;
            total += value;
            });
            
            const totalSpan = row.querySelector('.total-percentage');
            if (totalSpan) {
                totalSpan.textContent = total + '%';
                totalSpan.style.color = total === 100 ? '#28a745' : '#dc3545';
            }
        }

        // 편집 취소
        function cancelInlineEdit(button) {
            const subjectId = button.getAttribute('data-subject-id');
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
    if (!row) return;
    
    const rulesDisplay = row.querySelector('.rules-display');
            if (!rulesDisplay) return;
    
    // 편집 모드 비활성화
    rulesDisplay.classList.remove('edit-mode');
    
            // 입력 필드 제거하고 원본 값 복원
    const inputs = rulesDisplay.querySelectorAll('input[type="number"]');
            const percentages = rulesDisplay.querySelectorAll('.grade-percentage');
            
            inputs.forEach((input, index) => {
                const percentage = percentages[index];
                if (percentage && originalValues[subjectId]) {
                    const grade = input.getAttribute('data-grade');
                    percentage.textContent = originalValues[subjectId][grade];
                    percentage.style.display = 'inline';
                }
                input.remove();
            });
            
            // 총 비율 복원
            const totalSpan = row.querySelector('.total-percentage');
            if (totalSpan) {
                totalSpan.style.color = '#dc3545';
            }
        }

        // 편집 저장
        function saveInlineEdit(button) {
            const subjectId = button.getAttribute('data-subject-id');
            
            // subjectId 유효성 검사
            if (!subjectId || subjectId.trim() === '') {
                alert('과목 ID가 올바르지 않습니다.');
                return;
            }
            
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
            if (!row) {
                alert('해당 과목 행을 찾을 수 없습니다.');
                return;
            }
    
    const inputs = row.querySelectorAll('input[type="number"]');
            const formData = new FormData();
            
            // subjectId 추가 (기존 API에서 필요)
            formData.append('subjectId', subjectId);
            
            // 입력 값 유효성 검사 및 상위 누적 비율 순서 검증
            let hasError = false;
            let orderErrorMessage = '';
            const gradeOrder = ['A+', 'A', 'B+', 'B', 'C+', 'C', 'D'];
            const values = {};
            
            inputs.forEach(input => {
                const value = input.value.trim();
                if (value === '' || isNaN(value) || value < 0 || value > 100) {
                    alert('올바른 값을 입력해주세요: ' + input.getAttribute('data-grade') + ' (' + value + ')');
                    input.focus();
                    hasError = true;
                    return;
                }
                
                const grade = input.getAttribute('data-grade');
                values[grade] = Number(value);
                formData.append(input.name, value);
            });
            
            // 상위 누적 비율 순서 검증 (A+ < A < B+ < B < C+ < C < D)
            if (!hasError) {
                for (let i = 0; i < gradeOrder.length - 1; i++) {
                    const currentGrade = gradeOrder[i];
                    const nextGrade = gradeOrder[i + 1];
                    
                    if (values[currentGrade] && values[nextGrade] && values[currentGrade] >= values[nextGrade]) {
                        hasError = true;
                        orderErrorMessage = `${currentGrade}(${values[currentGrade]}%)는 ${nextGrade}(${values[nextGrade]}%)보다 작아야 합니다`;
                        break;
                    }
                }
                
                if (hasError && orderErrorMessage) {
                    alert('상위 누적 비율 순서 오류: ' + orderErrorMessage);
                    return;
                }
            }
            
            if (hasError) return;
            
            // AJAX로 저장 요청 (올바른 API 경로 사용)
    fetch('${pageContext.request.contextPath}/api/grade/admin/subject-rules/inline-save', {
        method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(data => {
                if (data === 'SUCCESS') {
                    alert('저장되었습니다.');
                    location.reload();
                } else {
                    alert('저장에 실패했습니다: ' + data);
        }
    })
    .catch(error => {
        console.error('Error:', error);
                alert('저장 중 오류가 발생했습니다.');
    });
}

// 글로벌 규정으로 초기화
        function resetToGlobal(button) {
            const subjectId = button.getAttribute('data-subject-id');
            if (!confirm('글로벌 규정으로 초기화하시겠습니까?')) {
                return;
            }
            
            // 서버에 초기화 요청 전송
            fetch('/api/grade/admin/subject-rules/reset', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'subjectId=' + subjectId
            })
            .then(response => response.text())
            .then(result => {
                if (result === 'SUCCESS') {
                    alert('글로벌 규정으로 초기화되었습니다.');
                    // 페이지 새로고침하여 서버에서 최신 데이터 가져오기
                    location.reload();
                } else {
                    alert('초기화에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('초기화 요청 실패:', error);
                alert('초기화 요청 중 오류가 발생했습니다.');
            });
        }

        // 글로벌 규정 목록으로 이동
        function goToGlobalRules() {
            console.log('글로벌 규정 목록 버튼 클릭됨');
            window.location.href = '${pageContext.request.contextPath}/grade/admin/rule/global';
        }

        // 테스트 페이지로 이동
        function goToTestPage() {
            window.location.href = '${pageContext.request.contextPath}/test-index';
        }


        // 페이지 로드 시 실행
        document.addEventListener('DOMContentLoaded', function() {
            console.log('페이지 로드 완료');
            console.log('result 객체:', '${result != null ? "존재" : "null"}');
            console.log('result.data:', '${result != null && result.data != null ? "존재" : "null"}');
            console.log('result.data 길이:', ${result != null && result.data != null ? result.data.size() : 0});
            
            <c:if test="${result != null && result.data != null}">
                console.log('과목 데이터 상세:');
                <c:forEach var="subject" items="${result.data}" varStatus="status">
                    console.log('과목 ${status.index + 1}:', {
                        subjectId: '${subject.subjectId}',
                        subjectName: '${subject.subjectName}',
                        ruleType: '${subject.ruleType}'
                    });
                </c:forEach>
            </c:if>
        });
</script>
</body>
</html>


