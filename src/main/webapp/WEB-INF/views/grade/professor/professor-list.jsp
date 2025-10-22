<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강의별 학생 성적 관리</title>
<style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 1200px; margin: 0 auto; }
        .header { background: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .grade-rules { background: #e9ecef; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .score-rules { display: inline-block; margin-right: 20px; }
        .alphabet-rules { display: inline-block; }
        .rule-box { display: inline-block; background: #28a745; color: white; padding: 5px 10px; margin: 2px; border-radius: 3px; }

        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
        th { background: #f8f9fa; }

        .score-input { width: 60px; text-align: center; }
        .btn { padding: 5px 10px; margin: 2px; border: none; border-radius: 3px; cursor: pointer; }
        .btn-edit { background: #ffc107; color: black; }
        .btn-save { background: #28a745; color: white; }
        .btn-cancel { background: #6c757d; color: white; }
        .btn-delete { background: #dc3545; color: white; }

        .edit-mode .display-score { display: none !important; }
        .edit-mode .input-score { display: inline-block !important; }
        .normal-mode .display-score { display: inline-block !important; }
        .normal-mode .input-score { display: none !important; }

        .preview { background: #f8f9fa; padding: 5px; border-radius: 3px; margin: 2px; }
</style>
</head>
<body>
<div class="container">
    <!-- 헤더 -->
    <div class="header">
        <h2>강의별 학생 성적 관리</h2>
        <p><strong>선택된 강의:</strong> 강의ID ${param.courseId}, 과목ID ${param.subjectId}</p>
        <c:if test="${not empty result.data}">
            <p><strong>과목명:</strong> ${result.data[0].subjectName}</p>
        </c:if>
        <div style="margin-top: 10px;">
            <a href="/" class="btn btn-outline-secondary" style="margin-right: 10px;">🏠 메인페이지</a>
            <a href="${pageContext.request.contextPath}/grade/professor/courses">← 강의 목록으로 돌아가기</a>
        </div>
    </div>

    <!-- 성적 규정 정보 -->
    <div class="grade-rules">
        <h3>성적 규정 정보</h3>
        <div class="score-rules">
            <strong>점수 배율:</strong>
            <c:choose>
                <c:when test="${not empty gradeSystem}">
                    중간고사: ${gradeSystem.midExamRatio}% | 기말고사: ${gradeSystem.finalExamRatio}% | 과제: ${gradeSystem.assignmentRatio}% | 출석: ${gradeSystem.attendanceRatio}%
                </c:when>
                <c:otherwise>
                    중간고사: 30% | 기말고사: 40% | 과제: 20% | 출석: 10% (기본값)
                </c:otherwise>
            </c:choose>
        </div>
        <div class="alphabet-rules">
            <strong>학점 규정:</strong>
            <div style="margin-bottom: 10px;">
                <strong>평가 방식:</strong>
                <span style="color: #007bff;">5명 미만 = 절대평가</span> |
                <span style="color: #28a745;">5명 이상 = 상대평가</span>
            </div>
        <div>
                <strong>절대평가 (5명 미만):</strong>
                <span class="rule-box">A+: 95점 이상</span>
                <span class="rule-box">A: 90점 이상</span>
                <span class="rule-box">B+: 85점 이상</span>
                <span class="rule-box">B: 80점 이상</span>
                <span class="rule-box">C+: 75점 이상</span>
                <span class="rule-box">C: 70점 이상</span>
                <span class="rule-box">D: 60점 이상</span>
                <span class="rule-box" style="background: #dc3545;">F: 60점 미만 또는 총점 30점 미만</span>
                    </div>
            <div style="margin-top: 5px;">
                <strong>상대평가 (5명 이상):</strong>
                <c:choose>
                    <c:when test="${not empty relativeRules}">
                        <c:forEach var="rule" items="${relativeRules}">
                            <span class="rule-box">${rule.alphabet}: 상위 ${rule.boundary}%</span>
                        </c:forEach>
                        <span class="rule-box" style="background: #dc3545;">F: 총점 30점 미만</span>
                    </c:when>
                    <c:otherwise>
                        <span class="rule-box">A+: 상위 15%</span>
                        <span class="rule-box">A: 상위 30%</span>
                        <span class="rule-box">B+: 상위 50%</span>
                        <span class="rule-box">B: 상위 70%</span>
                        <span class="rule-box">C+: 상위 75%</span>
                        <span class="rule-box">C: 상위 80%</span>
                        <span class="rule-box">D: 상위 95%</span>
                        <span class="rule-box">F: 총점 30점 미만</span>
                    </c:otherwise>
                </c:choose>
                    </div>
        </div>
    </div>

<%-- =====================[ 검색 바 공용 include ]===================== --%>
<jsp:include page="/WEB-INF/views/common/searchBar.jsp">
    <jsp:param name="formAction"      value="${pageContext.request.contextPath}/grade/professor/list"/>
    <jsp:param name="optionValues"    value="studentName|studentNo"/>
    <jsp:param name="optionLabels"    value="학생명|학번"/>
    <jsp:param name="pageSizeOptions" value="5|10|20|50"/>
    <jsp:param name="placeHolder"     value="학생명 또는 학번 입력"/>
    <jsp:param name="keep"            value="courseId=${param.courseId}&subjectId=${param.subjectId}&professorId=${param.professorId}"/>
    <jsp:param name="req"             value="${req}"/>
</jsp:include>

    <!-- 학생 성적 테이블 -->
    <table id="gradeTable">
    <thead>
    <tr>
        <th>#</th>
        <th>학생명</th>
        <th>학번</th>
        <th>중간고사</th>
        <th>기말고사</th>
        <th>과제</th>
        <th>출석</th>
        <th>총점</th>
        <th>학점</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="g" items="${result.data}" varStatus="st">
                <tr id="row-${st.index}" data-enrollment-id="${g.enrollmentId}" data-grade-id="${g.id}" class="${g.id != null ? 'normal-mode' : 'edit-mode'}">
            <td>${(result.currentPage - 1) * result.pageSize + st.index + 1}</td>
            <td>${g.studentName}</td>
            <td>${g.studentNo}</td>

                    <!-- 중간고사 -->
                    <td>
                        <span class="display-score">${g.midExam != null ? g.midExam : '-'}</span>
                        <input class="input-score score-input" type="number" min="0" max="100"
                               name="midExam" value="${g.midExam != null ? g.midExam : ''}" data-field="midExam">
            </td>

                    <!-- 기말고사 -->
                    <td>
                        <span class="display-score">${g.finalExam != null ? g.finalExam : '-'}</span>
                        <input class="input-score score-input" type="number" min="0" max="100"
                               name="finalExam" value="${g.finalExam != null ? g.finalExam : ''}" data-field="finalExam">
            </td>

                    <!-- 과제 -->
                    <td>
                        <span class="display-score">${g.assignment != null ? g.assignment : '-'}</span>
                        <input class="input-score score-input" type="number" min="0" max="100"
                               name="assignment" value="${g.assignment != null ? g.assignment : ''}" data-field="assignment">
            </td>

                    <!-- 출석 -->
                    <td>
                        <span class="display-score">${g.attendance != null ? g.attendance : '-'}</span>
                        <input class="input-score score-input" type="number" min="0" max="100"
                               name="attendance" value="${g.attendance != null ? g.attendance : ''}" data-field="attendance"
                               placeholder="출석점수 입력">
            </td>

                    <!-- 총점 -->
                    <td>
                        <span class="total-score">${g.totalInt != null ? g.totalInt : '-'}</span>
            </td>

                    <!-- 학점 -->
                    <td>
                        <span class="alphabet-grade">${g.alphabet != null ? g.alphabet : '-'}</span>
            </td>

                    <!-- 관리 버튼 -->
            <td>
                <c:choose>
                    <c:when test="${g.id != null}">
                                <!-- 기존 성적이 있는 경우 -->
                                <button type="button" class="btn btn-edit" onclick="editGrade(${st.index})">수정</button>
                                <button type="button" class="btn btn-save" onclick="saveGrade(${st.index})" style="display: none;">저장</button>
                                <button type="button" class="btn btn-cancel" onclick="cancelEdit(${st.index})" style="display: none;">취소</button>
                                <button class="btn btn-delete" onclick="deleteGrade(${g.id})">삭제</button>
                    </c:when>
                    <c:otherwise>
                                <!-- 새로운 성적 등록 -->
                                <button type="button" class="btn btn-save" onclick="saveGrade(${st.index})">저장</button>
                                <button type="button" class="btn btn-cancel" onclick="cancelEdit(${st.index})">취소</button>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.data}">
        <tr>
            <td colspan="10" style="text-align: center; padding: 40px; color: #666;">
                <div style="font-size: 18px; margin-bottom: 10px;">📚</div>
                <div><strong>이 강의에 수강신청한 학생이 없습니다.</strong></div>
                <div style="margin-top: 10px; font-size: 14px; color: #999;">
                    수강신청 기간에 학생들이 이 강의를 신청하면 여기에 표시됩니다.
                </div>
                <div style="margin-top: 15px; font-size: 12px; color: #aaa;">
                    💡 수강인원이 5명 이상이면 상대평가, 5명 미만이면 절대평가로 처리됩니다.
                </div>
            </td>
        </tr>
    </c:if>
    </tbody>
</table>

<%-- =====================[ 페이징 바 공용 include ]===================== --%>
<jsp:include page="/WEB-INF/views/common/page.jsp">
    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/grade/professor/list"/>
    <jsp:param name="req" value="${req}"/>
    <jsp:param name="result" value="${result}"/>
    <jsp:param name="keepParams" value="courseId=${param.courseId}&subjectId=${param.subjectId}&professorId=${param.professorId}"/>
</jsp:include>

</div>

<script>
console.log('=== JavaScript 로딩 시작 ===');

// 함수 정의 확인
console.log('saveNewGrade 함수 정의 확인:', typeof saveNewGrade);

// 전역 변수 (EL 빈값 안전화)
const professorIdRaw = '${professorId}';
const courseIdRaw    = '${param.courseId}';
const subjectIdRaw   = '${param.subjectId}';

const professorId = professorIdRaw === '' ? null : Number(professorIdRaw);
const courseId    = courseIdRaw    === '' ? null : Number(courseIdRaw);
const subjectId   = subjectIdRaw   === '' ? null : Number(subjectIdRaw);

console.log('전역 변수:', { professorId, courseId, subjectId });

// 메시지 표시 함수들
function showSuccessMessage(message) {
    // 기존 메시지 제거
    const existingMessage = document.getElementById('message-alert');
    if (existingMessage) {
        existingMessage.remove();
    }

    // 성공 메시지 생성
    const alertDiv = document.createElement('div');
    alertDiv.id = 'message-alert';
    alertDiv.className = 'alert alert-success';
    alertDiv.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: space-between;">
            <span>✅ ${message}</span>
            <button type="button" onclick="this.parentElement.parentElement.remove()" style="background: none; border: none; font-size: 18px; cursor: pointer;">&times;</button>
        </div>
    `;

    document.body.appendChild(alertDiv);

    // 3초 후 자동 제거
    setTimeout(() => {
        if (alertDiv.parentElement) {
            alertDiv.remove();
        }
    }, 3000);
}

function showErrorMessage(message) {
    // 기존 메시지 제거
    const existingMessage = document.getElementById('message-alert');
    if (existingMessage) {
        existingMessage.remove();
    }

    // 에러 메시지 생성
    const alertDiv = document.createElement('div');
    alertDiv.id = 'message-alert';
    alertDiv.className = 'alert alert-danger';
    alertDiv.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: space-between;">
            <span>❌ ${message}</span>
            <button type="button" onclick="this.parentElement.parentElement.remove()" style="background: none; border: none; font-size: 18px; cursor: pointer;">&times;</button>
        </div>
    `;

    document.body.appendChild(alertDiv);

    // 5초 후 자동 제거
    setTimeout(() => {
        if (alertDiv.parentElement) {
            alertDiv.remove();
        }
    }, 5000);
}

// 학점 계산 함수 (5명 기준 절대평가/상대평가)
function calculateGrade(totalScore) {
    // 총 학생 수 확인 (실제로는 서버에서 가져와야 함)
    const totalStudents = document.querySelectorAll('tr[id^="row-"]').length;

    // 총점 30점 미만은 자동 F
    if (totalScore < 30) {
        return 'F';
    }

    // 5명 미만: 절대평가
    if (totalStudents < 5) {
        return calculateAbsoluteGrade(totalScore);
    }
    // 5명 이상: 상대평가 (상위 누적 비율)
    else {
        return calculateRelativeGrade(totalScore);
    }
}

// 절대평가 (점수 기준) - 서버 로직과 동일하게 수정
function calculateAbsoluteGrade(totalScore) {
    if (totalScore < 30) return 'F';
    if (totalScore >= 95) return 'A+';
    else if (totalScore >= 90) return 'A';
    else if (totalScore >= 85) return 'B+';
    else if (totalScore >= 80) return 'B';
    else if (totalScore >= 75) return 'C+';
    else if (totalScore >= 70) return 'C';
    else return 'D';
}

// 상대평가 (상위 누적 비율 기준)
function calculateRelativeGrade(totalScore) {
    // 상대평가는 서버에서만 정확히 계산 가능 (전체 학생 점수 비교 필요)
    // 클라이언트에서는 미리보기용으로 절대평가 사용
    // 실제 저장 시에는 서버에서 상대평가 적용됨
    return calculateAbsoluteGrade(totalScore);
}

// 편집 모드 전환
window.editGrade = function editGrade(rowIndex) {
    console.log('편집 모드 시작 - rowIndex:', rowIndex);
    const row = document.getElementById('row-' + rowIndex);
    if (!row) {
        console.error('행을 찾을 수 없습니다:', 'row-' + rowIndex);
        return;
    }

    row.classList.remove('normal-mode');
    row.classList.add('edit-mode');

    // 버튼 상태 변경
    const btnEdit = row.querySelector('.btn-edit');
    const btnSave = row.querySelector('.btn-save');
    const btnCancel = row.querySelector('.btn-cancel');
    const btnDelete = row.querySelector('.btn-delete');

    if (btnEdit) btnEdit.style.display = 'none';
    if (btnSave) btnSave.style.display = 'inline-block';
    if (btnCancel) btnCancel.style.display = 'inline-block';
    if (btnDelete) btnDelete.style.display = 'none';

    // 입력 필드 활성화 확인
    const inputs = row.querySelectorAll('.score-input');
    console.log('찾은 입력 필드 개수:', inputs.length);
    inputs.forEach(input => {
        input.disabled = false;
        input.readOnly = false;
        console.log('입력 필드 활성화:', {
            name: input.name,
            value: input.value,
            min: input.min,
            max: input.max,
            disabled: input.disabled,
            readOnly: input.readOnly
        });
    });

    // 미리보기 시작
    updatePreview(rowIndex);
}

// 편집 취소
window.cancelEdit = function cancelEdit(rowIndex) {
    console.log('편집 취소 - rowIndex:', rowIndex);
    const row = document.getElementById('row-' + rowIndex);
    const gradeId = row.getAttribute('data-grade-id');

    if (gradeId && gradeId !== 'null') {
        // 기존 성적이 있는 경우 - normal-mode로 전환
        row.classList.remove('edit-mode');
        row.classList.add('normal-mode');

        // 버튼 상태 복원
        row.querySelector('.btn-edit').style.display = 'inline-block';
        row.querySelector('.btn-save').style.display = 'none';
        row.querySelector('.btn-cancel').style.display = 'none';
        row.querySelector('.btn-delete').style.display = 'inline-block';
    } else {
        // 새로운 성적인 경우 - 페이지 새로고침으로 원래 상태로 돌아가기
        location.reload();
    }
}

// 성적 저장 (기존 성적 수정)
window.saveGrade = async function saveGrade(rowIndex) {
    console.log('=== saveGrade 함수 호출됨 ===');
    console.log('성적 저장 - rowIndex:', rowIndex);
    const row = document.getElementById('row-' + rowIndex);
    const gradeId = row.getAttribute('data-grade-id');

    // gradeId가 없으면 새로운 성적 등록으로 처리 (동일한 로직 사용)
    if (!gradeId || gradeId === 'null') {
        console.log('새로운 성적 등록으로 처리 - 동일한 로직 사용');
        // saveNewGrade 대신 동일한 로직으로 처리
        const scores = getScoresFromRow(row);
        if (!validateScores(scores)) return;

        try {
            const formData = new FormData();
            formData.append('enrollmentId', row.getAttribute('data-enrollment-id'));
            formData.append('midExam', scores.midExam);
            formData.append('finalExam', scores.finalExam);
            formData.append('assignment', scores.assignment);
            formData.append('attendance', scores.attendance);
            formData.append('professorId', professorId);
            formData.append('courseId', courseId);
            if (subjectId && subjectId !== 'null') {
                formData.append('subjectId', subjectId);
            }

            const token = localStorage.getItem("accessToken");
            const response = await fetch('${pageContext.request.contextPath}/api/grade/professor/add', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            if (response.ok) {
                const responseText = await response.text();
                showSuccessMessage('성적이 성공적으로 저장되었습니다.');
                console.log('새 성적 저장 성공! 실시간 업데이트');

                // 서버에서 계산된 결과 파싱
                const totalScoreMatch = responseText.match(/총점: (\d+)/);
                const alphabetMatch = responseText.match(/학점: ([A-Z+]+)/);
                const gpaMatch = responseText.match(/GPA: ([\d.]+)/);

                if (totalScoreMatch && alphabetMatch && gpaMatch) {
                    const totalScore = totalScoreMatch[1];
                    const alphabet = alphabetMatch[1];
                    const gpa = gpaMatch[1];

                    // 실시간 업데이트
                    updateRowDisplay(row, totalScore, alphabet, gpa);
                    
                    // normal-mode로 전환
                    row.classList.remove('edit-mode');
                    row.classList.add('normal-mode');
                    
                    // 수정/삭제 버튼으로 변경 (초록색 버튼 제거)
                    row.querySelector('.btn-save').style.display = 'none';
                    row.querySelector('.btn-cancel').style.display = 'none';
                    row.querySelector('.btn-edit').style.display = 'inline-block';
                    row.querySelector('.btn-delete').style.display = 'inline-block';
                } else {
                    console.error('응답 파싱 실패:', responseText);
                    location.reload();
                }
            } else {
                const errorText = await response.text();
                showErrorMessage('성적 저장 실패: ' + errorText);
                console.error('저장 실패:', response.status, errorText);
            }
        } catch (error) {
            showErrorMessage('성적 저장 중 오류가 발생했습니다: ' + error.message);
            console.error('저장 오류:', error);
        }
        return;
    }

    const scores = getScoresFromRow(row);
    if (!validateScores(scores)) return;

    try {
        const formData = new FormData();
        formData.append('id', gradeId);
        formData.append('enrollmentId', row.getAttribute('data-enrollment-id'));
        formData.append('midExam', scores.midExam);
        formData.append('finalExam', scores.finalExam);
        formData.append('assignment', scores.assignment);
        formData.append('attendance', scores.attendance);
        formData.append('professorId', professorId);
        formData.append('courseId', courseId);
        if (subjectId && subjectId !== 'null') {
            formData.append('subjectId', subjectId);
        }

        const token = localStorage.getItem("accessToken");
        const response = await fetch('${pageContext.request.contextPath}/api/grade/professor/edit', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (response.ok) {
            const responseText = await response.text();
            // 성공 메시지 표시
            showSuccessMessage('성적이 성공적으로 수정되었습니다.');
            console.log('수정 성공! 실시간 업데이트');

            // 서버에서 계산된 결과 파싱
            const totalScoreMatch = responseText.match(/총점: (\d+)/);
            const gradeMatch = responseText.match(/학점: ([A-F][+-]?)/);

            if (totalScoreMatch && gradeMatch) {
                const serverTotalScore = parseInt(totalScoreMatch[1]);
                const serverGrade = gradeMatch[1];
                updateGradeDisplayFromServer(rowIndex, serverTotalScore, serverGrade);
            } else {
                // 파싱 실패 시 클라이언트 계산 사용
                updateGradeDisplay(rowIndex, scores);
            }

            // 상대평가인 경우 전체 재계산이 필요하므로 페이지 새로고침
            const studentCount = document.querySelectorAll('tbody tr').length;
            if (studentCount >= 5) {
                console.log('상대평가 재계산 완료 - 페이지 새로고침');
                setTimeout(() => {
                    location.reload();
                }, 1000);
            }

        } else {
            const errorText = await response.text();
            showErrorMessage('성적 수정에 실패했습니다: ' + errorText);
        }
    } catch (error) {
        console.error('오류:', error);
        alert('오류가 발생했습니다: ' + error.message);
    }
}

// 새로운 성적 저장
window.saveNewGrade = async function saveNewGrade(rowIndex) {
    console.log('=== saveNewGrade 함수 호출됨 ===');
    console.log('새 성적 저장 - rowIndex:', rowIndex);
    console.log('함수 실행 시작');
    
    // 연속 클릭 방지
    const saveButton = document.querySelector(`#row-${rowIndex} .btn-save`);
    if (saveButton && saveButton.disabled) {
        console.log('이미 처리 중입니다.');
        return;
    }
    if (saveButton) {
        saveButton.disabled = true;
        saveButton.textContent = '저장 중...';
    }

    const row = document.getElementById('row-' + rowIndex);
    console.log('row 요소:', row);

    const scores = getScoresFromRow(row);
    console.log('점수 데이터:', scores);
    if (!validateScores(scores)) return;

    try {
        const formData = new FormData();
        formData.append('enrollmentId', row.getAttribute('data-enrollment-id'));
        formData.append('midExam', scores.midExam);
        formData.append('finalExam', scores.finalExam);
        formData.append('assignment', scores.assignment);
        formData.append('attendance', scores.attendance);
        formData.append('professorId', professorId);
        formData.append('courseId', courseId);
        if (subjectId && subjectId !== 'null') {
            formData.append('subjectId', subjectId);
        }

        console.log('=== AJAX 요청 시작 ===');
        console.log('요청 URL:', '${pageContext.request.contextPath}/api/grade/professor/add');
        console.log('FormData 내용:');
        for (let [key, value] of formData.entries()) {
            console.log(key + ':', value);
        }

        const token = localStorage.getItem("accessToken");
        const response = await fetch('${pageContext.request.contextPath}/api/grade/professor/add', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        console.log('=== AJAX 응답 받음 ===');
        console.log('응답 상태:', response.status);
        console.log('응답 OK:', response.ok);

        if (response.ok) {
            const responseText = await response.text();
            console.log('응답 텍스트:', responseText);
            showSuccessMessage('성적이 성공적으로 등록되었습니다.');
            console.log('성공! 실시간 업데이트');

            // 서버에서 계산된 결과 파싱
            const totalScoreMatch = responseText.match(/총점: (\d+)/);
            const gradeMatch = responseText.match(/학점: ([A-F][+-]?)/);

            if (totalScoreMatch && gradeMatch) {
                const serverTotalScore = parseInt(totalScoreMatch[1]);
                const serverGrade = gradeMatch[1];
                updateGradeDisplayFromServer(rowIndex, serverTotalScore, serverGrade);
            } else {
                // 파싱 실패 시 클라이언트 계산 사용
                updateGradeDisplay(rowIndex, scores);
            }

            // 버튼 상태 변경 (저장 → 수정)
            changeToEditMode(rowIndex);

            // 상대평가인 경우 전체 재계산이 필요하므로 페이지 새로고침
            const studentCount = document.querySelectorAll('tbody tr').length;
            if (studentCount >= 5) {
                console.log('상대평가 재계산 완료 - 페이지 새로고침');
                setTimeout(() => {
                    location.reload();
                }, 1000);
            }

        } else {
            const errorText = await response.text();
            console.error('=== 서버 에러 응답 ===');
            console.error('에러 상태:', response.status);
            console.error('에러 텍스트:', errorText);
            showErrorMessage('성적 등록에 실패했습니다: ' + errorText);
            // 실패 시 버튼 다시 활성화
            if (saveButton) {
                saveButton.disabled = false;
                saveButton.textContent = '저장';
            }
        }
    } catch (error) {
        console.error('=== JavaScript 에러 ===');
        console.error('에러 객체:', error);
        console.error('에러 메시지:', error.message);
        console.error('에러 스택:', error.stack);
        alert('오류가 발생했습니다: ' + error.message);
        // 에러 시 버튼 다시 활성화
        if (saveButton) {
            saveButton.disabled = false;
            saveButton.textContent = '저장';
        }
    }
}

// 성적 삭제
async function deleteGrade(gradeId) {
    console.log('성적 삭제 - gradeId:', gradeId);

    if (!confirm('정말로 이 성적을 삭제하시겠습니까?')) {
        return;
    }

    try {
        const formData = new FormData();
        formData.append('id', gradeId);
        formData.append('professorId', professorId);

        const token = localStorage.getItem("accessToken");
        const response = await fetch('${pageContext.request.contextPath}/grade/professor/delete', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

            if (response.ok) {
                showSuccessMessage('성적이 성공적으로 삭제되었습니다.');
                console.log('성적 삭제 성공');

                // 상대평가인 경우 전체 재계산이 필요하므로 페이지 새로고침
                const studentCount = document.querySelectorAll('tbody tr').length;
                if (studentCount >= 5) {
                    console.log('상대평가 재계산 필요 - 페이지 새로고침');
                    setTimeout(() => {
                        location.reload();
                    }, 1000);
                } else {
                    // 절대평가인 경우 즉시 새로고침
                    location.reload();
                }
            } else {
                const errorText = await response.text();
                showErrorMessage('성적 삭제에 실패했습니다: ' + errorText);
            }
    } catch (error) {
        console.error('오류:', error);
        alert('오류가 발생했습니다: ' + error.message);
    }
}

// 행에서 점수 가져오기
function getScoresFromRow(row) {
    const midExamInput = row.querySelector('[data-field="midExam"]');
    const finalExamInput = row.querySelector('[data-field="finalExam"]');
    const assignmentInput = row.querySelector('[data-field="assignment"]');
    const attendanceInput = row.querySelector('[data-field="attendance"]');

    return {
        midExam: midExamInput ? (midExamInput.value || 0) : 0,
        finalExam: finalExamInput ? (finalExamInput.value || 0) : 0,
        assignment: assignmentInput ? (assignmentInput.value || 0) : 0,
        attendance: attendanceInput ? (attendanceInput.value || 0) : 0
    };
}

// 점수 유효성 검사
function validateScores(scores) {
    const { midExam, finalExam, assignment, attendance } = scores;

    if (midExam < 0 || midExam > 100 || finalExam < 0 || finalExam > 100 ||
        assignment < 0 || assignment > 100 || attendance < 0 || attendance > 100) {
        alert('점수는 0-100 사이의 값이어야 합니다.');
        return false;
    }

    return true;
}

// 실시간 미리보기 업데이트
function updatePreview(rowIndex) {
    const row = document.getElementById('row-' + rowIndex);
    const scores = getScoresFromRow(row);

    // null 체크 및 기본값 설정
    const midExam = parseFloat(scores.midExam) || 0;
    const finalExam = parseFloat(scores.finalExam) || 0;
    const assignment = parseFloat(scores.assignment) || 0;
    const attendance = parseFloat(scores.attendance) || 0;

    // 서버에서 전달받은 점수 비율 사용 (과목별로 다를 수 있음)
    const midExamRatio = <c:out value="${gradeSystem != null && gradeSystem.midExamRatio != null ? gradeSystem.midExamRatio / 100.0 : 0.3}"/>;
    const finalExamRatio = <c:out value="${gradeSystem != null && gradeSystem.finalExamRatio != null ? gradeSystem.finalExamRatio / 100.0 : 0.4}"/>;
    const assignmentRatio = <c:out value="${gradeSystem != null && gradeSystem.assignmentRatio != null ? gradeSystem.assignmentRatio / 100.0 : 0.2}"/>;
    const attendanceRatio = <c:out value="${gradeSystem != null && gradeSystem.attendanceRatio != null ? gradeSystem.attendanceRatio / 100.0 : 0.1}"/>;

    // 총점 계산
    const totalScore = (midExam * midExamRatio) + (finalExam * finalExamRatio) +
                      (assignment * assignmentRatio) + (attendance * attendanceRatio);

    // 학점 계산 (5명 기준 절대평가/상대평가)
    let alphabetGrade = calculateGrade(totalScore);

    // 미리보기 표시 (null 체크)
    const totalScoreElement = row.querySelector('.total-score');
    const alphabetGradeElement = row.querySelector('.alphabet-grade');

    if (totalScoreElement) {
        const roundedScore = isNaN(totalScore) ? 0 : Math.round(totalScore);
        totalScoreElement.innerHTML = `${roundedScore} <small style="color: #666;">(미리보기)</small>`;
    }
    if (alphabetGradeElement) {
        alphabetGradeElement.innerHTML = `${alphabetGrade} <small style="color: #666;">(미리보기)</small>`;
    }
}

// 입력 필드 변경 시 미리보기 업데이트
document.addEventListener('DOMContentLoaded', function() {
    const inputs = document.querySelectorAll('.score-input');
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            const row = this.closest('tr');
            const rowIndex = row.id.replace('row-', '');
            if (row.classList.contains('edit-mode')) {
                updatePreview(rowIndex);
            }
        });
    });

});

// 저장 후 실제 값으로 업데이트하는 함수 (미리보기 제거)
function updateGradeDisplay(rowIndex, scores) {
    const row = document.getElementById('row-' + rowIndex);

    // 점수 계산 (서버와 동일한 비율 사용)
    const midExam = parseFloat(scores.midExam) || 0;
    const finalExam = parseFloat(scores.finalExam) || 0;
    const assignment = parseFloat(scores.assignment) || 0;
    const attendance = parseFloat(scores.attendance) || 0;

    // 서버에서 전달받은 점수 비율 사용 (과목별로 다를 수 있음)
    const midExamRatio = <c:out value="${gradeSystem != null && gradeSystem.midExamRatio != null ? gradeSystem.midExamRatio / 100.0 : 0.3}"/>;
    const finalExamRatio = <c:out value="${gradeSystem != null && gradeSystem.finalExamRatio != null ? gradeSystem.finalExamRatio / 100.0 : 0.4}"/>;
    const assignmentRatio = <c:out value="${gradeSystem != null && gradeSystem.assignmentRatio != null ? gradeSystem.assignmentRatio / 100.0 : 0.2}"/>;
    const attendanceRatio = <c:out value="${gradeSystem != null && gradeSystem.attendanceRatio != null ? gradeSystem.attendanceRatio / 100.0 : 0.1}"/>;

    // 총점 계산
    const totalScore = Math.round(
        (midExam * midExamRatio) +
        (finalExam * finalExamRatio) +
        (assignment * assignmentRatio) +
        (attendance * attendanceRatio)
    );

    // 학점 계산 (5명 이상이면 상대평가, 미만이면 절대평가)
    let grade;
    const studentCount = document.querySelectorAll('tbody tr').length;
    if (studentCount >= 5) {
        // 상대평가: 클라이언트에서는 미리보기용으로 절대평가 사용
        // 실제 저장 시에는 서버에서 상대평가 적용됨
        grade = calculateAbsoluteGrade(totalScore);
    } else {
        // 절대평가: 점수 기준으로 계산
        grade = calculateAbsoluteGrade(totalScore);
    }

    // 총점과 학점 표시 업데이트 (미리보기 제거)
    const totalScoreElement = row.querySelector('.total-score');
    const gradeElement = row.querySelector('.alphabet-grade');

    if (totalScoreElement) {
        totalScoreElement.textContent = totalScore;
    }
    if (gradeElement) {
        gradeElement.textContent = grade;
    }

    console.log('총점/학점 업데이트:', { totalScore, grade });
    console.log('사용된 점수 비율:', { midExamRatio, finalExamRatio, assignmentRatio, attendanceRatio });
}

// 서버에서 받은 결과로 업데이트하는 함수
function updateGradeDisplayFromServer(rowIndex, totalScore, grade) {
    const row = document.getElementById('row-' + rowIndex);

    // 총점과 학점 표시 업데이트
    const totalScoreElement = row.querySelector('.total-score');
    const gradeElement = row.querySelector('.alphabet-grade');

    if (totalScoreElement) {
        totalScoreElement.textContent = totalScore;
    }
    if (gradeElement) {
        gradeElement.textContent = grade;
    }

    console.log('서버 결과로 총점/학점 업데이트:', { totalScore, grade });
}

// 새로 등록된 성적을 수정 모드로 변경하는 함수
function changeToEditMode(rowIndex) {
    const row = document.getElementById('row-' + rowIndex);

    // data-grade-id 설정 (임시로 enrollment-id 사용)
    const enrollmentId = row.getAttribute('data-enrollment-id');
    row.setAttribute('data-grade-id', enrollmentId); // 임시 ID

    // normal-mode로 전환
    row.classList.remove('edit-mode');
    row.classList.add('normal-mode');

    // 버튼 상태 변경 (저장/취소 → 수정/삭제)
    const saveBtn = row.querySelector('.btn-save');
    const cancelBtn = row.querySelector('.btn-cancel');
    const editBtn = row.querySelector('.btn-edit');
    const deleteBtn = row.querySelector('.btn-delete');

    // 저장/취소 버튼 숨기기
    if (saveBtn) saveBtn.style.display = 'none';
    if (cancelBtn) cancelBtn.style.display = 'none';

    // 수정/삭제 버튼 보이기
    if (editBtn) editBtn.style.display = 'inline-block';
    if (deleteBtn) deleteBtn.style.display = 'inline-block';

    console.log('수정 모드로 변경 완료');
}

// 스크립트 로딩 완료 후 함수 정의 확인
console.log('=== 스크립트 로딩 완료 ===');
console.log('saveNewGrade 함수 정의 확인:', typeof saveNewGrade);
console.log('getScoresFromRow 함수 정의 확인:', typeof getScoresFromRow);
console.log('validateScores 함수 정의 확인:', typeof validateScores);

// 저장 버튼 이벤트 리스너 추가 (백업용)
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM 로딩 완료 - 저장 버튼 이벤트 리스너 추가');
    
    // 모든 저장 버튼에 이벤트 리스너 추가
    const saveButtons = document.querySelectorAll('.btn-save');
    console.log('찾은 저장 버튼 개수:', saveButtons.length);
    
    saveButtons.forEach((button, index) => {
        // 기존 onclick 제거
        button.removeAttribute('onclick');
        
        // 새로운 이벤트 리스너 추가
        button.addEventListener('click', function() {
            const rowIndex = this.getAttribute('data-row-index') || index;
            console.log('저장 버튼 클릭됨 - rowIndex:', rowIndex);
            saveNewGrade(rowIndex);
        });
        
        // data-row-index 속성 추가
        button.setAttribute('data-row-index', index);
    });
});
</script>

</body>
</html>
