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
            <a href="${pageContext.request.contextPath}/grade/professor/courses?professorId=${professorId}">← 강의 목록으로 돌아가기</a>
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
                <strong>절대평가:</strong>
                <span class="rule-box">A+: 95점 이상</span>
                <span class="rule-box">A: 90점 이상</span>
                <span class="rule-box">B+: 85점 이상</span>
                <span class="rule-box">B: 80점 이상</span>
                <span class="rule-box">C+: 75점 이상</span>
                <span class="rule-box">C: 70점 이상</span>
                <span class="rule-box">D: 60점 이상</span>
                <span class="rule-box">F: 60점 미만 또는 총점 30점 미만</span>
                    </div>
            <div style="margin-top: 5px;">
                <strong>상대평가:</strong>
                <c:choose>
                    <c:when test="${not empty subjectRules}">
                        <c:forEach var="rule" items="${subjectRules}">
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
                                <button class="btn btn-edit" onclick="editGrade(${st.index})">수정</button>
                                <button class="btn btn-save" onclick="saveGrade(${st.index})" style="display: none;">저장</button>
                                <button class="btn btn-cancel" onclick="cancelEdit(${st.index})" style="display: none;">취소</button>
                                <button class="btn btn-delete" onclick="deleteGrade(${g.id})">삭제</button>
                    </c:when>
                    <c:otherwise>
                                <!-- 새로운 성적 등록 -->
                                <button class="btn btn-save" onclick="saveNewGrade(${st.index})">저장</button>
                                <button class="btn btn-cancel" onclick="cancelEdit(${st.index})">취소</button>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</div>

<script>
// 전역 변수
const professorId = ${professorId};
const courseId = ${param.courseId};
const subjectId = ${param.subjectId};

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

// 절대평가 (점수 기준)
function calculateAbsoluteGrade(totalScore) {
    if (totalScore >= 95) return 'A+';
    else if (totalScore >= 90) return 'A';
    else if (totalScore >= 85) return 'B+';
    else if (totalScore >= 80) return 'B';
    else if (totalScore >= 75) return 'C+';
    else if (totalScore >= 70) return 'C';
    else if (totalScore >= 60) return 'D';
    else return 'F';
}

// 상대평가 (상위 누적 비율 기준)
function calculateRelativeGrade(totalScore) {
    // 상대평가는 서버에서 계산해야 함 (전체규정/커스텀규정 적용)
    // 여기서는 임시로 절대평가를 사용하고, 실제 저장 시 서버에서 상대평가 적용
    return calculateAbsoluteGrade(totalScore);
}

// 편집 모드 전환
function editGrade(rowIndex) {
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
function cancelEdit(rowIndex) {
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
async function saveGrade(rowIndex) {
    console.log('성적 저장 - rowIndex:', rowIndex);
    const row = document.getElementById('row-' + rowIndex);
    const gradeId = row.getAttribute('data-grade-id');
    
    if (!gradeId || gradeId === 'null') {
        alert('성적 ID를 찾을 수 없습니다.');
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
        formData.append('subjectId', subjectId);
        
        const response = await fetch('${pageContext.request.contextPath}/grade/professor/edit', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            alert('성적이 수정되었습니다.');
            location.reload(); // 페이지 새로고침으로 서버에서 계산된 값 반영
        } else {
            alert('성적 수정에 실패했습니다.');
        }
    } catch (error) {
        console.error('오류:', error);
        alert('오류가 발생했습니다: ' + error.message);
    }
}

// 새로운 성적 저장
async function saveNewGrade(rowIndex) {
    console.log('새 성적 저장 - rowIndex:', rowIndex);
    const row = document.getElementById('row-' + rowIndex);
    
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
        formData.append('subjectId', subjectId);
        
        const response = await fetch('${pageContext.request.contextPath}/grade/professor/add', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            alert('성적이 등록되었습니다.');
            location.reload();
        } else {
            alert('성적 등록에 실패했습니다.');
        }
    } catch (error) {
        console.error('오류:', error);
        alert('오류가 발생했습니다: ' + error.message);
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
        
        const response = await fetch('${pageContext.request.contextPath}/grade/professor/delete', {
            method: 'POST',
            body: formData
        });
        
            if (response.ok) {
                alert('성적이 삭제되었습니다.');
                location.reload();
            } else {
                alert('성적 삭제에 실패했습니다.');
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
    
    // 점수 비율 (서버와 동일하게 맞춤)
    const midExamRatio = 0.3;    // 중간고사 30%
    const finalExamRatio = 0.4;  // 기말고사 40%
    const assignmentRatio = 0.2; // 과제 20%
    const attendanceRatio = 0.1; // 출석 10%
    
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
</script>
</body>
</html>
