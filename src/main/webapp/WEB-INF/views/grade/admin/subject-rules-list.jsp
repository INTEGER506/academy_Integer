<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>과목별 규정 목록</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f8f9fa;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .search-bar {
            margin-bottom: 20px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 5px;
        }
        .search-bar select, .search-bar input {
            padding: 8px;
            margin-right: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .search-bar button {
            padding: 8px 16px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .search-bar button:hover {
            background-color: #0056b3;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .grade-rules {
            display: flex;
            flex-wrap: wrap;
            gap: 5px;
            margin-bottom: 10px;
        }
        .grade-rule {
            display: inline-flex;
            align-items: center;
            background-color: #e9ecef;
            padding: 2px 6px;
            border-radius: 3px;
            margin-right: 3px;
            font-size: 12px;
        }
        .grade-label {
            font-weight: bold;
            margin-right: 3px;
            color: #495057;
        }
        .grade-percentage {
            color: #007bff;
        }
        .total-percentage {
            font-weight: bold;
            font-size: 14px;
        }
        .edit-mode .grade-percentage {
            display: none;
        }
        .edit-mode .grade-label {
            display: inline;
        }
        .edit-mode input[type="number"] {
            width: 60px;
            padding: 2px 4px;
            border: 1px solid #ddd;
            border-radius: 3px;
            margin-left: 3px;
        }
        .btn-edit, .btn-reset, .btn-save, .btn-cancel {
            padding: 4px 8px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
            margin-right: 5px;
        }
        .btn-edit { background-color: #007bff; color: white; }
        .btn-reset { background-color: #ffc107; color: black; }
        .btn-save { background-color: #28a745; color: white; }
        .btn-cancel { background-color: #dc3545; color: white; }
        .edit-controls {
            display: none;
        }
        .edit-mode .edit-controls {
            display: block;
        }
        .edit-mode .btn-edit,
        .edit-mode .btn-reset {
            display: none;
        }
        .pagination {
            text-align: center;
            margin-top: 20px;
        }
        .pagination button {
            padding: 8px 16px;
            margin: 0 5px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .pagination button:hover {
            background-color: #0056b3;
        }
        .pagination button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>과목별 규정 목록</h1>
        
        <!-- 검색 바 -->
        <div class="search-bar">
            <select id="searchType">
                    <option value="">검색 조건</option>
                    <option value="subject">과목명</option>
                <option value="id">과목ID</option>
                </select>
            <input type="text" id="searchKeyword" placeholder="검색어를 입력하세요">
            <button onclick="searchSubjects()">검색</button>
        </div>
        
        <!-- 과목 목록 테이블 -->
        <table>
    <thead>
                <tr>
                    <th>번호</th>
                    <th>과목명</th>
                    <th>학점</th>
                    <th>규정 상태</th>
                    <th>현재 규정</th>
                    <th>작업</th>
    </tr>
    </thead>
    <tbody>
                <!-- 임시 하드코딩된 과목 데이터 -->
                <tr data-subject-id="1">
                    <td>1</td>
                    <td style="font-weight: bold;">알고리즘</td>
                    <td>3학점</td>
                    <td>
                        <span style="color: #28a745; font-weight: bold;">🌐 글로벌 규정</span>
                    </td>
                    <td>
                        <div class="rules-display">
                            <div class="grade-rules">
                                <span class="grade-rule">
                                    <span class="grade-label">A+</span>
                                    <span class="grade-percentage">${globalRules['A+'] != null ? globalRules['A+'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">A</span>
                                    <span class="grade-percentage">${globalRules['A'] != null ? globalRules['A'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">B+</span>
                                    <span class="grade-percentage">${globalRules['B+'] != null ? globalRules['B+'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">B</span>
                                    <span class="grade-percentage">${globalRules['B'] != null ? globalRules['B'] : 20.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">C+</span>
                                    <span class="grade-percentage">${globalRules['C+'] != null ? globalRules['C+'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">C</span>
                                    <span class="grade-percentage">${globalRules['C'] != null ? globalRules['C'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">D+</span>
                                    <span class="grade-percentage">${globalRules['D+'] != null ? globalRules['D+'] : 7.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">D</span>
                                    <span class="grade-percentage">${globalRules['D'] != null ? globalRules['D'] : 8.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">F</span>
                                    <span class="grade-percentage">${globalRules['F'] != null ? globalRules['F'] : 15.0}%</span>
                                </span>
                            </div>
                            <div class="total-percentage">
                                총합: <span class="total-value">100.0</span>%
                            </div>
                        </div>
                    </td>
                    <td>
                        <button onclick="startInlineEdit(1)" 
                                class="btn-edit">
                            ✏️ 수정
                        </button>
                        <button onclick="resetToGlobal(1)" 
                                class="btn-reset">
                            🔄 초기화
                        </button>
                        <div class="edit-controls">
                            <button onclick="saveInlineEdit(1)" 
                                    class="btn-save">
                                💾 저장
                            </button>
                            <button onclick="cancelInlineEdit(1)" 
                                    class="btn-cancel">
                                ❌ 취소
                            </button>
                        </div>
            </td>
        </tr>
                <tr data-subject-id="2">
                    <td>2</td>
                    <td style="font-weight: bold;">자료구조</td>
                    <td>3학점</td>
                    <td>
                                                    <span style="color: #28a745; font-weight: bold;">🌐 글로벌 규정</span>
                                </td>
                                <td>
                        <div class="rules-display">
                            <div class="grade-rules">
                                <span class="grade-rule">
                                    <span class="grade-label">A+</span>
                                    <span class="grade-percentage">${globalRules['A+'] != null ? globalRules['A+'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">A</span>
                                    <span class="grade-percentage">${globalRules['A'] != null ? globalRules['A'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">B+</span>
                                    <span class="grade-percentage">${globalRules['B+'] != null ? globalRules['B+'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">B</span>
                                    <span class="grade-percentage">${globalRules['B'] != null ? globalRules['B'] : 20.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">C+</span>
                                    <span class="grade-percentage">${globalRules['C+'] != null ? globalRules['C+'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">C</span>
                                    <span class="grade-percentage">${globalRules['C'] != null ? globalRules['C'] : 10.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">D+</span>
                                    <span class="grade-percentage">${globalRules['D+'] != null ? globalRules['D+'] : 7.0}%</span>
                                </span>
                                <span class="grade-rule">
                                    <span class="grade-label">D</span>
                                    <span class="grade-percentage">${globalRules['D'] != null ? globalRules['D'] : 8.0}%</span>
                                </span>
                                                        <span class="grade-rule">
                                    <span class="grade-label">F</span>
                                    <span class="grade-percentage">${globalRules['F'] != null ? globalRules['F'] : 15.0}%</span>
                                                        </span>
                                                    </div>
                            <div class="total-percentage">
                                총합: <span class="total-value">100.0</span>%
                                                    </div>
                                    </div>
                                </td>
                                <td>
                        <button onclick="startInlineEdit(2)" 
                                class="btn-edit">
                                        ✏️ 수정
                                    </button>
                        <button onclick="resetToGlobal(2)" 
                                class="btn-reset">
                                        🔄 초기화
                                    </button>
                        <div class="edit-controls">
                            <button onclick="saveInlineEdit(2)" 
                                    class="btn-save">
                                            💾 저장
                                        </button>
                            <button onclick="cancelInlineEdit(2)" 
                                    class="btn-cancel">
                                            ❌ 취소
                                        </button>
                                    </div>
            </td>
        </tr>
    </tbody>
</table>

        <!-- 페이지네이션 -->
        <div class="pagination">
            <button onclick="goToPage(1)">첫 페이지</button>
            <button onclick="goToPage(1)">이전</button>
            <span>페이지 1/1</span>
            <button onclick="goToPage(1)">다음</button>
            <button onclick="goToPage(1)">마지막 페이지</button>
        </div>
        
        <!-- 하단 버튼들 -->
        <div style="text-align: center; margin-top: 20px;">
            <button onclick="goToGlobalRules()" style="padding: 10px 20px; margin-right: 10px; background-color: #6c757d; color: white; border: none; border-radius: 4px; cursor: pointer;">
                글로벌 규정 목록
            </button>
            <button onclick="goToTestPage()" style="padding: 10px 20px; background-color: #17a2b8; color: white; border: none; border-radius: 4px; cursor: pointer;">
                테스트 페이지로
            </button>
        </div>
</div>

<script>
        // 원본 값 저장용
        const originalValues = {};
        // 저장된 값 저장용
        const savedValues = {};
        // 글로벌 규정 저장용 (서버에서 전달받은 값으로 초기화)
        let globalRules = {
            'A+': ${globalRules['A+'] != null ? globalRules['A+'] : 10.0},
            'A': ${globalRules['A'] != null ? globalRules['A'] : 10.0},
            'B+': ${globalRules['B+'] != null ? globalRules['B+'] : 10.0},
            'B': ${globalRules['B'] != null ? globalRules['B'] : 20.0},
            'C+': ${globalRules['C+'] != null ? globalRules['C+'] : 10.0},
            'C': ${globalRules['C'] != null ? globalRules['C'] : 10.0},
            'D+': ${globalRules['D+'] != null ? globalRules['D+'] : 7.0},
            'D': ${globalRules['D'] != null ? globalRules['D'] : 8.0},
            'F': ${globalRules['F'] != null ? globalRules['F'] : 15.0}
        };
        
        console.log('서버에서 전달받은 글로벌 규정:', globalRules);


        // 검색 함수
        function searchSubjects() {
            const searchType = document.getElementById('searchType').value;
            const searchKeyword = document.getElementById('searchKeyword').value;
            
            if (searchType && searchKeyword) {
                window.location.href = '${pageContext.request.contextPath}/grade/admin/subject-rules?searchType=' + searchType + '&searchKeyword=' + encodeURIComponent(searchKeyword);
            } else {
                window.location.href = '${pageContext.request.contextPath}/grade/admin/subject-rules';
    }
}

// 인라인 편집 시작
function startInlineEdit(subjectId) {
    console.log('startInlineEdit called with subjectId:', subjectId);
            
            // 다른 과목의 편집 모드 종료
            const allRows = document.querySelectorAll('tr[data-subject-id]');
            allRows.forEach(row => {
                const otherSubjectId = row.dataset.subjectId;
                if (otherSubjectId && otherSubjectId != subjectId) {
                    const otherRulesDisplay = row.querySelector('.rules-display');
                    const otherEditControls = row.querySelector('.edit-controls');
                    const otherEditButton = row.querySelector('.btn-edit');
                    const otherResetButton = row.querySelector('.btn-reset');
                    
                    if (otherRulesDisplay && otherRulesDisplay.classList.contains('edit-mode')) {
                        console.log('종료 중인 과목:', otherSubjectId);
                        otherRulesDisplay.classList.remove('edit-mode');
                        
                        const inputs = otherRulesDisplay.querySelectorAll('input[type="number"]');
                        inputs.forEach(input => input.remove());
                        
                        if (originalValues[otherSubjectId]) {
                            const percentages = otherRulesDisplay.querySelectorAll('.grade-percentage');
                            const grades = ['A+', 'A', 'B+', 'B', 'C+', 'C', 'D+', 'D', 'F'];
                            percentages.forEach((span, index) => {
                                const grade = grades[index];
                                if (grade && originalValues[otherSubjectId][grade]) {
                                    span.textContent = originalValues[otherSubjectId][grade];
                                }
                                span.style.display = 'inline';
                            });
                        }
                        
                        if (otherEditButton) otherEditButton.style.display = 'inline-block';
                        if (otherResetButton) otherResetButton.style.display = 'inline-block';
                        if (otherEditControls) otherEditControls.style.display = 'none';
                    }
                }
            });
            
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
    console.log('Found row:', row);
    if (!row) {
        console.error('Row not found for subjectId:', subjectId);
        return;
    }
    
    const rulesDisplay = row.querySelector('.rules-display');
    const editControls = row.querySelector('.edit-controls');
    const editButton = row.querySelector('.btn-edit');
    const resetButton = row.querySelector('.btn-reset');
    
    // 편집 모드 활성화
    rulesDisplay.classList.add('edit-mode');
            
            // 기존 입력 필드가 있으면 제거
            const existingInputs = rulesDisplay.querySelectorAll('input[type="number"]');
            existingInputs.forEach(input => input.remove());
    
    // 원본 값 저장
    originalValues[subjectId] = {};
    const percentages = rulesDisplay.querySelectorAll('.grade-percentage');
    
    // 입력 필드 생성
            const grades = ['A+', 'A', 'B+', 'B', 'C+', 'C', 'D+', 'D', 'F'];
            percentages.forEach((span, index) => {
                console.log(`Processing span ${index}:`, span);
                console.log('span.textContent:', span.textContent);
                
                const grade = grades[index];
                console.log('Using grade from array:', grade);
                
                if (!grade) {
                    console.error('Grade not found for index:', index);
                    return;
                }
                
                // 원본 값 저장
                originalValues[subjectId][grade] = span.textContent;
                
        console.log('Creating input for grade:', grade, 'value:', span.textContent);
        const input = document.createElement('input');
        input.type = 'number';
        input.name = 'boundary_' + grade;
        input.value = span.textContent.replace('%', '');
        input.min = '0';
        input.max = '100';
        input.step = '0.1';
                input.setAttribute('data-grade', grade);
        console.log('Input created for grade:', grade, 'name:', input.name, 'value:', input.value);
                
        input.addEventListener('input', () => {
            if (input.value.includes('-')) {
                input.value = input.value.replace(/[^0-9.]/g, '');
            }
            updateTotalPercentage(subjectId);
        });
        
                // span 숨기기 (학점 라벨은 유지)
                span.style.display = 'none';
                // 입력 필드를 span 다음에 삽입
        span.parentNode.insertBefore(input, span.nextSibling);
        console.log('Input field inserted for grade:', grade);
    });
    
            // 버튼 표시/숨김
            if (editButton) editButton.style.display = 'none';
            if (resetButton) resetButton.style.display = 'none';
            if (editControls) editControls.style.display = 'block';
            
            // 총 비율 업데이트
    updateTotalPercentage(subjectId);
    
            console.log('startInlineEdit completed for subjectId:', subjectId);
}

        // 총 비율 업데이트
function updateTotalPercentage(subjectId) {
    console.log('updateTotalPercentage 호출됨 - subjectId:', subjectId);
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
    if (!row) {
        console.error('Row not found for subjectId:', subjectId);
        return;
    }
    
    let total = 0;
            const rulesDisplay = row.querySelector('.rules-display');
    
            if (rulesDisplay.classList.contains('edit-mode')) {
                // 편집 모드: 입력 필드 값으로 계산
                const inputs = rulesDisplay.querySelectorAll('input[type="number"]');
    console.log('Input fields found:', inputs.length);
        inputs.forEach(input => {
            const value = parseFloat(input.value) || 0;
            total += value;
            console.log('Input value:', input.value, 'Parsed:', value);
        });
    } else {
        // 일반 모드: span 값으로 계산
        const percentages = row.querySelectorAll('.grade-percentage');
        console.log('Percentage spans found:', percentages.length);
        percentages.forEach(span => {
            const value = parseFloat(span.textContent.replace('%', '')) || 0;
            total += value;
            console.log('Span value:', span.textContent, 'Parsed:', value);
        });
    }
    
    console.log('Total calculated:', total);
    
    const totalValueSpan = row.querySelector('.total-value');
    if (totalValueSpan) {
        totalValueSpan.textContent = total.toFixed(1);
        console.log('Total updated to:', totalValueSpan.textContent);
        
        // 색상 변경
        if (Math.abs(total - 100) < 0.1) {
            totalValueSpan.parentElement.style.backgroundColor = '#d4edda';
            totalValueSpan.parentElement.style.color = '#155724';
        } else {
            totalValueSpan.parentElement.style.backgroundColor = '#f8d7da';
            totalValueSpan.parentElement.style.color = '#721c24';
        }
    } else {
        console.error('Total value span not found');
    }
}

// 인라인 편집 취소
function cancelInlineEdit(subjectId) {
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
    if (!row) return;
    
    const rulesDisplay = row.querySelector('.rules-display');
    const editControls = row.querySelector('.edit-controls');
    const editButton = row.querySelector('.btn-edit');
    const resetButton = row.querySelector('.btn-reset');
    
    // 편집 모드 비활성화
    rulesDisplay.classList.remove('edit-mode');
    
    // 입력 필드 제거
    const inputs = rulesDisplay.querySelectorAll('input[type="number"]');
    inputs.forEach(input => input.remove());
    
    // 원본 값 복원 및 span 다시 표시
    if (originalValues[subjectId]) {
        const percentages = rulesDisplay.querySelectorAll('.grade-percentage');
                const grades = ['A+', 'A', 'B+', 'B', 'C+', 'C', 'D+', 'D', 'F'];
                percentages.forEach((span, index) => {
                    const grade = grades[index];
                    if (grade && originalValues[subjectId][grade]) {
                span.textContent = originalValues[subjectId][grade];
            }
                    span.style.display = 'inline';
        });
    }
    
    // 버튼 표시/숨김
            if (editButton) editButton.style.display = 'inline-block';
            if (resetButton) resetButton.style.display = 'inline-block';
            if (editControls) editControls.style.display = 'none';
            
            // 총 비율 업데이트
            updateTotalPercentage(subjectId);
}

// 인라인 편집 저장
function saveInlineEdit(subjectId) {
            console.log('=== saveInlineEdit 시작 ===');
            console.log('subjectId:', subjectId);
            
    const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
            console.log('찾은 row:', row);
            if (!row) {
                console.error('Row를 찾을 수 없습니다!');
                return;
            }
    
    const inputs = row.querySelectorAll('input[type="number"]');
            console.log('찾은 input 개수:', inputs.length);
            
    const params = new URLSearchParams();
    
    // subjectId 검증 및 추가
    if (subjectId && subjectId !== 'undefined') {
        params.append('subjectId', subjectId);
                console.log('subjectId 추가됨:', subjectId);
    } else {
                console.error('subjectId가 유효하지 않습니다:', subjectId);
        alert('과목 ID가 유효하지 않습니다.');
        return;
    }
    
    let totalPercentage = 0;
            // 저장된 값들을 저장
            savedValues[subjectId] = [];
            
            inputs.forEach((input, index) => {
        const percentage = parseFloat(input.value) || 0;
        totalPercentage += percentage;
                
                // 저장된 값 저장
                savedValues[subjectId][index] = input.value + '%';
                
                // data-grade 속성에서 학점 추출
                const grade = input.dataset.grade;
                console.log(`Input ${index}: grade=${grade}, value=${input.value}, percentage=${percentage}`);
                
                if (grade) {
                    const paramName = 'boundary_' + grade;
                    params.append(paramName, input.value);
                    console.log(`Added to params: ${paramName} = ${input.value}`);
                } else {
                    console.error('Grade not found for input:', input);
                }
            });
            
            console.log('Saved values for subjectId', subjectId, ':', savedValues[subjectId]);
            
            console.log('총 비율:', totalPercentage);
            console.log('전송할 params:', params.toString());
    
    if (Math.abs(totalPercentage - 100) > 0.1) {
                console.error('총 비율이 100%가 아닙니다:', totalPercentage);
                alert('총 비율이 100%가 아닙니다. 현재: ' + totalPercentage.toFixed(1) + '%');
        return;
    }
    
    // AJAX로 저장
    fetch('${pageContext.request.contextPath}/api/grade/admin/subject-rules/inline-save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
                console.log('Response status:', response.status);
                console.log('Response ok:', response.ok);
        if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('HTTP ' + response.status);
                }
            })
            .then(data => {
                console.log('Response data:', data);
                if (data === 'success') {
                    alert('저장되었습니다!');
                    // 편집 모드 종료
                    cancelInlineEdit(subjectId);
                    // 저장된 값으로 화면 업데이트
                    updateDisplayWithSavedValues(subjectId);
        } else {
                    console.log('Unexpected response:', data);
                    alert('저장되었습니다! (응답: ' + data + ')');
                    // 편집 모드 종료
                    cancelInlineEdit(subjectId);
                    // 저장된 값으로 화면 업데이트
                    updateDisplayWithSavedValues(subjectId);
        }
    })
    .catch(error => {
        console.error('Error:', error);
                alert('저장 중 오류가 발생했습니다.');
    });
}

// 글로벌 규정으로 초기화
function resetToGlobal(subjectId) {
            if (!confirm('정말로 글로벌 규정으로 초기화하시겠습니까?')) {
                return;
            }
            
            const params = new URLSearchParams();
            params.append('subjectId', subjectId);
            
        fetch('${pageContext.request.contextPath}/api/grade/admin/subject-rules/reset', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
                body: params.toString()
        })
        .then(response => {
                console.log('Reset response status:', response.status);
            if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('HTTP ' + response.status);
                }
            })
            .then(data => {
                console.log('Reset response data:', data);
                if (data === 'success') {
                    alert('초기화되었습니다!');
                    // 글로벌 규정으로 화면 업데이트
                    resetDisplayToGlobal(subjectId);
            } else {
                    console.log('Unexpected reset response:', data);
                    alert('초기화되었습니다! (응답: ' + data + ')');
                    // 글로벌 규정으로 화면 업데이트
                    resetDisplayToGlobal(subjectId);
            }
        })
        .catch(error => {
            console.error('Error:', error);
                alert('초기화 중 오류가 발생했습니다.');
            });
        }

        // 저장된 값으로 화면 업데이트
        function updateDisplayWithSavedValues(subjectId) {
            console.log('updateDisplayWithSavedValues called for subjectId:', subjectId);
            const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
            if (!row) {
                console.error('Row not found for subjectId:', subjectId);
                return;
            }
            
    const rulesDisplay = row.querySelector('.rules-display');
            const percentages = rulesDisplay.querySelectorAll('.grade-percentage');
            
            // 저장된 값들을 가져와서 화면에 반영
            const values = savedValues[subjectId];
            console.log('Using saved values for subjectId', subjectId, ':', values);
            
            if (values && values.length > 0) {
                percentages.forEach((span, index) => {
                    if (values[index]) {
                        span.textContent = values[index];
                        console.log('Updated span', index, 'to:', values[index]);
                    }
                });
            } else {
                console.log('No saved values found, using default values');
                // 기본값 사용
                const defaultValues = ['7.0%', '7.0%', '7.0%', '7.0%', '7.0%', '7.0%', '7.0%', '7.0%', '44.0%'];
                percentages.forEach((span, index) => {
                    if (defaultValues[index]) {
                        span.textContent = defaultValues[index];
                        console.log('Updated span', index, 'to default:', defaultValues[index]);
                    }
                });
            }
            
            // 총 비율 업데이트
            updateTotalPercentage(subjectId);
            
            // 규정 상태를 커스텀으로 변경
            const ruleStatusCell = row.querySelector('td:nth-child(4)');
            if (ruleStatusCell) {
                ruleStatusCell.innerHTML = '<span style="color: #007bff; font-weight: bold;">📝 커스텀 규정</span>';
            }
            
            console.log('Display updated for subjectId:', subjectId);
        }

        // 글로벌 규정으로 화면 초기화
        function resetDisplayToGlobal(subjectId) {
            console.log('resetDisplayToGlobal called for subjectId:', subjectId);
            const row = document.querySelector('tr[data-subject-id="' + subjectId + '"]');
            if (!row) {
                console.error('Row not found for subjectId:', subjectId);
                return;
            }
            
            const rulesDisplay = row.querySelector('.rules-display');
            const percentages = rulesDisplay.querySelectorAll('.grade-percentage');
            
            // 서버에서 가져온 글로벌 규정 값으로 초기화
            const grades = ['A+', 'A', 'B+', 'B', 'C+', 'C', 'D+', 'D', 'F'];
            const globalValues = grades.map(grade => {
                const value = globalRules[grade] || 0;
                return value + '%';
            });
            
            console.log('Using global rules for reset:', globalRules);
            console.log('Global values array:', globalValues);
            
            percentages.forEach((span, index) => {
                if (globalValues[index]) {
                    span.textContent = globalValues[index];
                    console.log('Reset span', index, 'to:', globalValues[index]);
                }
            });
            
            // 총 비율 업데이트
            updateTotalPercentage(subjectId);
            
            // 규정 상태를 글로벌로 변경
            const ruleStatusCell = row.querySelector('td:nth-child(4)');
            if (ruleStatusCell) {
                ruleStatusCell.innerHTML = '<span style="color: #28a745; font-weight: bold;">🌐 글로벌 규정</span>';
            }
            
            console.log('Display reset to global for subjectId:', subjectId);
        }

        // 페이지 이동 함수들
        function goToPage(page) {
            window.location.href = '${pageContext.request.contextPath}/grade/admin/subject-rules?page=' + page;
        }

        function goToGlobalRules() {
            window.location.href = '${pageContext.request.contextPath}/grade/admin/global-rules';
        }

        function goToTestPage() {
            window.location.href = '${pageContext.request.contextPath}/test';
        }

        // 페이지 로드 시 실행
        document.addEventListener('DOMContentLoaded', function() {
            console.log('페이지 로드 완료');
        });
</script>
</body>
</html>