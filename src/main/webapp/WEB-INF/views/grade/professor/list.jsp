<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
.grade-input {
    border: 1px solid #ddd;
    border-radius: 3px;
    padding: 4px;
    text-align: center;
    font-size: 12px;
}

.grade-input:focus {
    border-color: #007bff;
    outline: none;
    box-shadow: 0 0 3px rgba(0, 123, 255, 0.3);
}

.grade-display {
    font-weight: bold;
    color: #495057;
}

.total-score, .alphabet-grade, .gpa-score {
    font-weight: bold;
    text-align: center;
    padding: 4px;
}

.total-score {
    color: #007bff;
}

.alphabet-grade {
    color: #28a745;
}

.gpa-score {
    color: #6f42c1;
}

.btn-save, .btn-preview, .btn-edit, .btn-delete {
    cursor: pointer;
    transition: background-color 0.2s;
}

.btn-save:hover {
    background-color: #218838 !important;
}

.btn-preview:hover {
    background-color: #138496 !important;
}

.btn-edit:hover {
    background-color: #e0a800 !important;
}

.btn-delete:hover {
    background-color: #c82333 !important;
}
</style>

<h2>📊 ${param.courseId != null ? '강의별 성적 관리' : '성적 목록'}</h2>
    <c:if test="${param.courseId != null}">
        <p style="color: #6c757d; margin-bottom: 20px;">
            📖 <strong>선택된 강의:</strong> 강의ID ${param.courseId}, 과목ID ${param.subjectId}
            <a href="${pageContext.request.contextPath}/grade/professor/courses?professorId=${professorId}" 
               style="margin-left: 15px; color: #007bff; text-decoration: none;">← 강의 목록으로 돌아가기</a>
        </p>
    </c:if>

    <!-- 성적 규정 정보 표시 -->
    <div style="background-color: #f8f9fa; padding: 15px; margin-bottom: 20px; border-radius: 8px; border: 1px solid #dee2e6;">
        <h4 style="margin-top: 0; color: #495057;">📊 성적 규정 정보</h4>
        
        <!-- 점수 배율 정보 -->
        <div style="margin-bottom: 15px;">
            <h5 style="color: #6c757d; margin-bottom: 8px;">🎯 점수 배율</h5>
            <c:choose>
                <c:when test="${gradeSystem != null}">
                    <p style="margin: 5px 0;">
                        <strong>중간고사:</strong> ${gradeSystem.midExamRatio}% | 
                        <strong>기말고사:</strong> ${gradeSystem.finalExamRatio}% | 
                        <strong>과제:</strong> ${gradeSystem.assignmentRatio}% | 
                        <strong>출석:</strong> ${gradeSystem.attendanceRatio}%
                    </p>
                </c:when>
                <c:otherwise>
                    <p style="color: #dc3545; margin: 5px 0;">⚠️ 점수 배율이 설정되지 않았습니다.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Alphabet 규정 정보 -->
        <div>
            <h5 style="color: #6c757d; margin-bottom: 8px;">🏆 학점 규정</h5>
            <c:choose>
                <c:when test="${not empty subjectRules}">
                    <p style="margin: 5px 0; color: #28a745;"><strong>과목별 규정 적용:</strong></p>
                    <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                        <c:forEach var="rule" items="${subjectRules}">
                            <span style="background-color: #d4edda; padding: 4px 8px; border-radius: 4px; font-size: 0.9em;">
                                ${rule.alphabet}: ${rule.boundary}% 이상
                            </span>
                        </c:forEach>
                    </div>
                </c:when>
                <c:when test="${not empty globalRules}">
                    <p style="margin: 5px 0; color: #6c757d;"><strong>글로벌 규정 적용:</strong></p>
                    <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                        <c:forEach var="rule" items="${globalRules}">
                            <span style="background-color: #e2e3e5; padding: 4px 8px; border-radius: 4px; font-size: 0.9em;">
                                ${rule.alphabet}: ${rule.boundary}% 이상
                            </span>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #dc3545; margin: 5px 0;">⚠️ 학점 규정이 설정되지 않았습니다.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

<!-- 디버그 정보 -->
<div style="background-color: #f0f0f0; padding: 10px; margin: 10px 0; border: 1px solid #ccc;">
    <h4>🔍 디버그 정보</h4>
    <p><strong>professorId:</strong> ${professorId}</p>
    <p><strong>courseId:</strong> ${param.courseId}</p>
    <p><strong>subjectId:</strong> ${param.subjectId}</p>
    <p><strong>result.data size:</strong> ${result.data.size()}</p>
    <p><strong>result.totalCount:</strong> ${result.totalCount}</p>
    
    <!-- 규정 정보 디버그 -->
    <hr>
    <h5>📊 규정 정보 디버그</h5>
    <p><strong>gradeSystem:</strong> ${gradeSystem}</p>
    <p><strong>globalRules size:</strong> ${globalRules.size()}</p>
    <p><strong>subjectRules size:</strong> ${subjectRules.size()}</p>
    <c:if test="${not empty globalRules}">
        <p><strong>첫 번째 글로벌 규정:</strong> ${globalRules[0]}</p>
    </c:if>
    <c:if test="${not empty subjectRules}">
        <p><strong>첫 번째 과목별 규정:</strong> ${subjectRules[0]}</p>
    </c:if>
    
    <c:if test="${not empty result.data}">
        <p><strong>첫 번째 성적:</strong> ${result.data[0]}</p>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/professor/list"/>
    <jsp:param name="optionValues"    value="studentName|studentNo|subjectName|alphabet"/>
    <jsp:param name="optionLabels"    value="학생명|학번|과목명|학점"/>
    <jsp:param name="pageSizeOptions" value="10|20|50"/>
    <jsp:param name="placeHolder"     value="학생명/학번/과목명/학점"/>
    <jsp:param name="keep"            value="professorId=${professorId}&courseId=${param.courseId}&subjectId=${param.subjectId}"/>
    <jsp:param name="req"             value="${req}"/>
</jsp:include>

<table class="table">
    <thead>
    <tr>
        <th>#</th>
        <th>학생</th>
        <th>학번</th>
        <th>과목</th>
        <th>중간</th>
        <th>기말</th>
        <th>과제</th>
        <th>출석</th>
        <th>총점</th>
        <th>학점</th>
        <th>GPA</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="g" items="${result.data}" varStatus="st">
        <tr id="row-${g.enrollmentId}">
            <td>${(result.currentPage - 1) * result.pageSize + st.index + 1}</td>
            <td>${g.studentName}</td>
            <td>${g.studentNo}</td>
            <td>${g.subjectName}</td>
            <td>
                <c:choose>
                    <c:when test="${g.id != null}">
                        <!-- 성적이 있는 경우: 표시 -->
                        <span class="grade-display" data-enrollment="${g.enrollmentId}" data-type="midExam">${g.midExam}</span>
                    </c:when>
                    <c:otherwise>
                        <!-- 성적이 없는 경우: 입력 필드 -->
                        <input type="number" class="grade-input" data-enrollment="${g.enrollmentId}" data-type="midExam" 
                               min="0" max="100" placeholder="점수" style="width: 60px;" 
                               oninput="updatePreview(${g.enrollmentId})" onchange="updatePreview(${g.enrollmentId})">
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${g.id != null}">
                        <span class="grade-display" data-enrollment="${g.enrollmentId}" data-type="finalExam">${g.finalExam}</span>
                    </c:when>
                    <c:otherwise>
                        <input type="number" class="grade-input" data-enrollment="${g.enrollmentId}" data-type="finalExam" 
                               min="0" max="100" placeholder="점수" style="width: 60px;" 
                               oninput="updatePreview(${g.enrollmentId})" onchange="updatePreview(${g.enrollmentId})">
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${g.id != null}">
                        <span class="grade-display" data-enrollment="${g.enrollmentId}" data-type="assignment">${g.assignment}</span>
                    </c:when>
                    <c:otherwise>
                        <input type="number" class="grade-input" data-enrollment="${g.enrollmentId}" data-type="assignment" 
                               min="0" max="100" placeholder="점수" style="width: 60px;" 
                               oninput="updatePreview(${g.enrollmentId})" onchange="updatePreview(${g.enrollmentId})">
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${g.id != null}">
                        <span class="grade-display" data-enrollment="${g.enrollmentId}" data-type="attendance">${g.attendance}</span>
                    </c:when>
                    <c:otherwise>
                        <input type="number" class="grade-input" data-enrollment="${g.enrollmentId}" data-type="attendance" 
                               min="0" max="100" placeholder="점수" style="width: 60px;" 
                               oninput="updatePreview(${g.enrollmentId})" onchange="updatePreview(${g.enrollmentId})">
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <span class="total-score" data-enrollment="${g.enrollmentId}">
                    <c:choose>
                        <c:when test="${g.id != null}">${g.totalInt}</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </td>
            <td>
                <span class="alphabet-grade" data-enrollment="${g.enrollmentId}">
                    <c:choose>
                        <c:when test="${g.id != null}">${g.alphabet}</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </td>
            <td>
                <span class="gpa-score" data-enrollment="${g.enrollmentId}">
                    <c:choose>
                        <c:when test="${g.id != null}"><c:out value="${g.gpa}"/></c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </span>
            </td>
            <td>
                <c:choose>
                    <c:when test="${g.id != null}">
                        <!-- 성적이 있는 경우: 수정/삭제 가능 -->
                        <button class="btn-edit" onclick="editGrade(${g.enrollmentId})" style="background: #ffc107; color: white; border: none; padding: 4px 8px; border-radius: 3px; margin-right: 5px;">수정</button>
                        <button class="btn-delete" onclick="deleteGrade(${g.id})" style="background: #dc3545; color: white; border: none; padding: 4px 8px; border-radius: 3px;">삭제</button>
                    </c:when>
                    <c:otherwise>
                        <!-- 성적이 없는 경우: 등록 버튼 -->
                        <button class="btn-save" onclick="saveGrade(${g.enrollmentId})" style="background: #28a745; color: white; border: none; padding: 4px 8px; border-radius: 3px; margin-right: 5px;">저장</button>
                        <button class="btn-preview" onclick="previewGrade(${g.enrollmentId})" style="background: #17a2b8; color: white; border: none; padding: 4px 8px; border-radius: 3px;">미리보기</button>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.data}">
        <tr><td colspan="12">데이터가 없습니다.</td></tr>
    </c:if>
    </tbody>
</table>


<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl"    value="${pageContext.request.contextPath}/grade/professor/list"/>
    <jsp:param name="req"        value="${req}"/>
    <jsp:param name="result"     value="${result}"/>
    <jsp:param name="keepParams" value="${keepParams}"/>
</jsp:include>

<script>
// 성적 규정 정보 (서버에서 전달받은 데이터)
const gradeSystem = {
    midExamRatio: ${gradeSystem != null ? gradeSystem.midExamRatio : 30},
    finalExamRatio: ${gradeSystem != null ? gradeSystem.finalExamRatio : 40},
    assignmentRatio: ${gradeSystem != null ? gradeSystem.assignmentRatio : 20},
    attendanceRatio: ${gradeSystem != null ? gradeSystem.attendanceRatio : 10}
};

const globalRules = [
    <c:forEach var="rule" items="${globalRules}" varStatus="status">
    {alphabet: "${rule.alphabet}", boundary: ${rule.boundary}}<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

// 점수 입력 검증 (100점 만점)
function validateScore(score, fieldName) {
    if (score < 0 || score > 100) {
        alert(`${fieldName} 점수는 0-100점 사이로 입력해주세요.`);
        return false;
    }
    return true;
}

// 성적 미리보기 계산
function calculateGrade(enrollmentId) {
    const midExamInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="midExam"]`);
    const finalExamInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="finalExam"]`);
    const assignmentInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="assignment"]`);
    const attendanceInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="attendance"]`);
    
    const midExam = parseFloat(midExamInput.value) || 0;
    const finalExam = parseFloat(finalExamInput.value) || 0;
    const assignment = parseFloat(assignmentInput.value) || 0;
    const attendance = parseFloat(attendanceInput.value) || 0;
    
    // 점수 검증
    if (!validateScore(midExam, "중간고사") || !validateScore(finalExam, "기말고사") || 
        !validateScore(assignment, "과제") || !validateScore(attendance, "출석")) {
        return {totalScore: 0, alphabet: 'F', gpa: 0.0};
    }
    
    // 가중 평균 계산
    const totalScore = (midExam * gradeSystem.midExamRatio + 
                       finalExam * gradeSystem.finalExamRatio + 
                       assignment * gradeSystem.assignmentRatio + 
                       attendance * gradeSystem.attendanceRatio) / 100;
    
    // 학점 계산
    let alphabet = 'F';
    let gpa = 0.0;
    
    for (let rule of globalRules) {
        if (totalScore >= rule.boundary) {
            alphabet = rule.alphabet;
            gpa = getGpaFromAlphabet(rule.alphabet);
            break;
        }
    }
    
    return {totalScore: Math.round(totalScore), alphabet: alphabet, gpa: gpa};
}

// 학점을 GPA로 변환
function getGpaFromAlphabet(alphabet) {
    const gpaMap = {
        'A+': 4.5, 'A': 4.0, 'B+': 3.5, 'B': 3.0,
        'C+': 2.5, 'C': 2.0, 'D+': 1.5, 'D': 1.0, 'F': 0.0
    };
    return gpaMap[alphabet] || 0.0;
}

// 실시간 미리보기 업데이트
function updatePreview(enrollmentId) {
    const result = calculateGrade(enrollmentId);
    
    document.querySelector(`span[data-enrollment="${enrollmentId}"].total-score`).textContent = result.totalScore;
    document.querySelector(`span[data-enrollment="${enrollmentId}"].alphabet-grade`).textContent = result.alphabet;
    document.querySelector(`span[data-enrollment="${enrollmentId}"].gpa-score`).textContent = result.gpa;
}

// 성적 미리보기 버튼
function previewGrade(enrollmentId) {
    console.log('previewGrade 호출됨:', enrollmentId);
    
    // 먼저 화면 업데이트
    updatePreview(enrollmentId);
    
    // 그 다음 미리보기 알림
    const result = calculateGrade(enrollmentId);
    alert('성적 미리보기:\n총점: ' + result.totalScore + '점\n학점: ' + result.alphabet + '\nGPA: ' + result.gpa);
}

// 성적 저장
function saveGrade(enrollmentId) {
    console.log('saveGrade 호출됨:', enrollmentId);
    
    const midExamInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="midExam"]`);
    const finalExamInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="finalExam"]`);
    const assignmentInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="assignment"]`);
    const attendanceInput = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="attendance"]`);
    
    if (!midExamInput || !finalExamInput || !assignmentInput || !attendanceInput) {
        alert('입력 필드를 찾을 수 없습니다.');
        return;
    }
    
    const midExam = midExamInput.value;
    const finalExam = finalExamInput.value;
    const assignment = assignmentInput.value;
    const attendance = attendanceInput.value;
    
    // 점수 검증
    if (!midExam || !finalExam || !assignment || !attendance) {
        alert('모든 점수를 입력해주세요.');
        return;
    }
    
    const midExamNum = parseFloat(midExam);
    const finalExamNum = parseFloat(finalExam);
    const assignmentNum = parseFloat(assignment);
    const attendanceNum = parseFloat(attendance);
    
    if (!validateScore(midExamNum, "중간고사") || !validateScore(finalExamNum, "기말고사") || 
        !validateScore(assignmentNum, "과제") || !validateScore(attendanceNum, "출석")) {
        return;
    }
    
    if (confirm('성적을 저장하시겠습니까?')) {
        // AJAX로 성적 저장
        const formData = new FormData();
        formData.append('enrollmentId', enrollmentId);
        formData.append('courseId', '${param.courseId}');
        formData.append('subjectId', '${param.subjectId}');
        formData.append('midExam', midExamNum);
        formData.append('finalExam', finalExamNum);
        formData.append('assignment', assignmentNum);
        formData.append('attendance', attendanceNum);
        
        fetch('${pageContext.request.contextPath}/grade/professor/add', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                alert('성적이 저장되었습니다.');
                location.reload(); // 페이지 새로고침
            } else {
                alert('성적 저장에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        });
    }
}

// 성적 수정 모드
function editGrade(enrollmentId) {
    // 입력 필드로 변경
    const row = document.querySelector(`#row-${enrollmentId}`);
    const inputs = row.querySelectorAll('.grade-display');
    
    inputs.forEach(span => {
        const type = span.getAttribute('data-type');
        const value = span.textContent;
        const input = document.createElement('input');
        input.type = 'number';
        input.className = 'grade-input';
        input.setAttribute('data-enrollment', enrollmentId);
        input.setAttribute('data-type', type);
        input.value = value;
        input.style.width = '60px';
        input.min = '0';
        input.max = '100';
        span.parentNode.replaceChild(input, span);
    });
    
    // 버튼 변경
    const btnEdit = row.querySelector('.btn-edit');
    const btnDelete = row.querySelector('.btn-delete');
    btnEdit.textContent = '저장';
    btnEdit.onclick = () => updateGrade(enrollmentId);
    btnDelete.textContent = '취소';
    btnDelete.onclick = () => location.reload();
}

// 성적 업데이트
function updateGrade(enrollmentId) {
    // 기존 성적 ID 가져오기 (실제 구현에서는 적절한 방법으로 ID를 전달해야 함)
    const gradeId = document.querySelector(`#row-${enrollmentId}`).querySelector('.btn-delete').getAttribute('onclick').match(/\d+/)[0];
    
    const midExam = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="midExam"]`).value;
    const finalExam = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="finalExam"]`).value;
    const assignment = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="assignment"]`).value;
    const attendance = document.querySelector(`input[data-enrollment="${enrollmentId}"][data-type="attendance"]`).value;
    
    if (!midExam || !finalExam || !assignment || !attendance) {
        alert('모든 점수를 입력해주세요.');
        return;
    }
    
    if (confirm('성적을 수정하시겠습니까?')) {
        // AJAX로 성적 수정
        const formData = new FormData();
        formData.append('id', gradeId);
        formData.append('midExam', midExam);
        formData.append('finalExam', finalExam);
        formData.append('assignment', assignment);
        formData.append('attendance', attendance);
        
        fetch('${pageContext.request.contextPath}/grade/professor/edit', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                alert('성적이 수정되었습니다.');
                location.reload();
            } else {
                alert('성적 수정에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        });
    }
}

// 성적 삭제
function deleteGrade(gradeId) {
    if (confirm('성적을 삭제하시겠습니까?')) {
        fetch('${pageContext.request.contextPath}/grade/professor/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `id=${gradeId}`
        })
        .then(response => {
            if (response.ok) {
                alert('성적이 삭제되었습니다.');
                location.reload();
            } else {
                alert('성적 삭제에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        });
    }
}

// 입력 필드 변경 시 실시간 미리보기
document.addEventListener('DOMContentLoaded', function() {
    const inputs = document.querySelectorAll('.grade-input');
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            const enrollmentId = this.getAttribute('data-enrollment');
            updatePreview(enrollmentId);
        });
    });
});
</script>