<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>글로벌 성적 규정</h2>
<p>학교 전체에 적용되는 기본 성적 분배 비율입니다.</p>

<div style="max-width: 400px; margin: 20px auto;">
    <table style="width: 100%; border-collapse: collapse; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <thead>
        <tr style="background-color: #007bff; color: white;">
            <th style="padding: 15px; text-align: center; border-bottom: 1px solid #dee2e6;">학점</th>
            <th style="padding: 15px; text-align: center; border-bottom: 1px solid #dee2e6;">비율 (%)</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="row" items="${result.data}" varStatus="st">
            <tr style="background-color: ${st.index % 2 == 0 ? '#ffffff' : '#f8f9fa'};">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">${row.alphabet}</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="${row.alphabet}">${row.boundary}</span>
                    <input type="number" class="edit-mode" name="boundary_${row.alphabet}" value="${row.boundary}" 
                           min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_${row.alphabet}" value="${row.alphabet}">
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty result.data}">
            <!-- 기본 학점 규정 표시 -->
            <tr style="background-color: #ffffff;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">A+</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="A+">5</span>
                    <input type="number" class="edit-mode" name="boundary_A+" value="5" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_A+" value="A+">
                </td>
            </tr>
            <tr style="background-color: #f8f9fa;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">A</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="A">20</span>
                    <input type="number" class="edit-mode" name="boundary_A" value="20" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_A" value="A">
                </td>
            </tr>
            <tr style="background-color: #ffffff;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">B+</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="B+">20</span>
                    <input type="number" class="edit-mode" name="boundary_B+" value="20" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_B+" value="B+">
                </td>
            </tr>
            <tr style="background-color: #f8f9fa;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">B</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="B">20</span>
                    <input type="number" class="edit-mode" name="boundary_B" value="20" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_B" value="B">
                </td>
            </tr>
            <tr style="background-color: #ffffff;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">C+</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="C+">10</span>
                    <input type="number" class="edit-mode" name="boundary_C+" value="10" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_C+" value="C+">
                </td>
            </tr>
            <tr style="background-color: #f8f9fa;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">C</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="C">10</span>
                    <input type="number" class="edit-mode" name="boundary_C" value="10" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_C" value="C">
                </td>
            </tr>
            <tr style="background-color: #ffffff;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">D+</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="D+">3</span>
                    <input type="number" class="edit-mode" name="boundary_D+" value="3" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_D+" value="D+">
                </td>
            </tr>
            <tr style="background-color: #f8f9fa;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">D</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="D">2</span>
                    <input type="number" class="edit-mode" name="boundary_D" value="2" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_D" value="D">
                </td>
            </tr>
            <tr style="background-color: #ffffff;">
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-weight: bold; font-size: 16px;">F</td>
                <td style="padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6; font-size: 16px;">
                    <span class="display-mode" data-alphabet="F">10</span>
                    <input type="number" class="edit-mode" name="boundary_F" value="10" min="0" max="100" step="0.1" style="display: none; width: 80px; text-align: center; border: 1px solid #007bff; border-radius: 4px; padding: 4px;">
                    <input type="hidden" name="alphabet_F" value="F">
                </td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>

<div style="text-align: center; margin-top: 30px;">
    <!-- 편집 모드 버튼들 -->
    <div id="editButtons" style="display: none;">
        <button onclick="saveChanges()" 
                style="display: inline-block; background-color: #28a745; color: white; padding: 12px 24px; border: none; border-radius: 6px; margin: 0 10px; font-weight: bold; box-shadow: 0 2px 4px rgba(40,167,69,0.3); cursor: pointer;"
                onmouseover="this.style.backgroundColor='#1e7e34'" 
                onmouseout="this.style.backgroundColor='#28a745'">
            💾 저장
        </button>
        <button onclick="cancelEdit()" 
                style="display: inline-block; background-color: #6c757d; color: white; padding: 12px 24px; border: none; border-radius: 6px; margin: 0 10px; font-weight: bold; box-shadow: 0 2px 4px rgba(108,117,125,0.3); cursor: pointer;"
                onmouseover="this.style.backgroundColor='#545b62'" 
                onmouseout="this.style.backgroundColor='#6c757d'">
            ❌ 취소
        </button>
    </div>
    
    <!-- 조회 모드 버튼들 -->
    <div id="viewButtons">
        <button onclick="startEdit()" 
                style="display: inline-block; background-color: #007bff; color: white; padding: 12px 24px; border: none; border-radius: 6px; margin: 0 10px; font-weight: bold; box-shadow: 0 2px 4px rgba(0,123,255,0.3); cursor: pointer;"
                onmouseover="this.style.backgroundColor='#0056b3'" 
                onmouseout="this.style.backgroundColor='#007bff'">
            ✏️ 전체 규정 설정
        </button>
        <a href="${pageContext.request.contextPath}/grade/admin/subject-rules/list" 
           style="display: inline-block; background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; margin: 0 10px; font-weight: bold; box-shadow: 0 2px 4px rgba(40,167,69,0.3); transition: background-color 0.3s;"
           onmouseover="this.style.backgroundColor='#1e7e34'" 
           onmouseout="this.style.backgroundColor='#28a745'">
            📚 과목별 규정 관리
        </a>
    </div>
    
    <!-- 총합 표시 -->
    <div id="totalDisplay" style="margin-top: 15px; padding: 10px; background-color: #f8f9fa; border-radius: 5px; display: none;">
        <strong>총합: <span id="totalSum">0</span>%</strong>
        <span id="validationMessage" style="margin-left: 10px;"></span>
    </div>
</div>

<script>
let originalValues = {}; // 원본 값 저장

// 편집 모드 시작
function startEdit() {
    // 원본 값 저장
    originalValues = {};
    document.querySelectorAll('.display-mode').forEach(span => {
        originalValues[span.dataset.alphabet] = span.textContent;
    });
    
    // 조회 모드 숨기고 편집 모드 보이기
    document.querySelectorAll('.display-mode').forEach(span => span.style.display = 'none');
    document.querySelectorAll('.edit-mode').forEach(input => input.style.display = 'inline-block');
    
    // 버튼 전환
    document.getElementById('viewButtons').style.display = 'none';
    document.getElementById('editButtons').style.display = 'block';
    document.getElementById('totalDisplay').style.display = 'block';
    
    // 총합 계산
    calculateTotal();
    
    // 모든 입력 필드에 이벤트 리스너 추가
    document.querySelectorAll('.edit-mode').forEach(input => {
        input.addEventListener('input', calculateTotal);
    });
}

// 편집 취소
function cancelEdit() {
    // 원본 값으로 복원
    document.querySelectorAll('.display-mode').forEach(span => {
        span.textContent = originalValues[span.dataset.alphabet];
    });
    
    // 편집 모드 숨기고 조회 모드 보이기
    document.querySelectorAll('.display-mode').forEach(span => span.style.display = 'inline');
    document.querySelectorAll('.edit-mode').forEach(input => input.style.display = 'none');
    
    // 버튼 전환
    document.getElementById('editButtons').style.display = 'none';
    document.getElementById('viewButtons').style.display = 'block';
    document.getElementById('totalDisplay').style.display = 'none';
}

// 총합 계산
function calculateTotal() {
    let total = 0;
    document.querySelectorAll('.edit-mode').forEach(input => {
        total += Number(input.value || 0);
    });
    
    document.getElementById('totalSum').textContent = total;
    
    const validationMessage = document.getElementById('validationMessage');
    const saveBtn = document.querySelector('button[onclick="saveChanges()"]');
    
    if (total === 100) {
        validationMessage.textContent = '✓ 정상';
        validationMessage.style.color = 'green';
        saveBtn.disabled = false;
        saveBtn.style.backgroundColor = '#28a745';
    } else {
        validationMessage.textContent = '✗ 총합이 100%가 아닙니다';
        validationMessage.style.color = 'red';
        saveBtn.disabled = true;
        saveBtn.style.backgroundColor = '#dc3545';
    }
}

// 변경사항 저장
function saveChanges() {
    const total = Number(document.getElementById('totalSum').textContent);
    if (total !== 100) {
        alert('총합이 100%가 되어야 합니다. 현재: ' + total + '%');
        return;
    }
    
    // 클라이언트 사이드 검증 추가
    let hasError = false;
    let errorMessage = '';
    
    document.querySelectorAll('.edit-mode').forEach(input => {
        const value = parseFloat(input.value);
        if (isNaN(value)) {
            hasError = true;
            errorMessage += '유효하지 않은 값이 있습니다: ' + input.name + '\n';
        } else if (value < 0) {
            hasError = true;
            errorMessage += '음수 값은 허용되지 않습니다: ' + input.name + ' = ' + value + '%\n';
        } else if (value > 100) {
            hasError = true;
            errorMessage += '100%를 초과할 수 없습니다: ' + input.name + ' = ' + value + '%\n';
        }
    });
    
    if (hasError) {
        alert('입력 오류:\n' + errorMessage);
        return;
    }
    
    // 폼 데이터 생성
    const formData = new FormData();
    document.querySelectorAll('.edit-mode').forEach(input => {
        formData.append(input.name, input.value);
    });
    document.querySelectorAll('input[name^="alphabet_"]').forEach(input => {
        formData.append(input.name, input.value);
    });
    
    // AJAX로 서버에 전송 (RestController 사용)
    fetch('${pageContext.request.contextPath}/api/grade/admin/global-rules/save', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            return response.text();
        } else {
            throw new Error('저장 중 오류가 발생했습니다.');
        }
    })
    .then(message => {
        // 성공 시 조회 모드로 전환하고 데이터 다시 로드
        cancelEdit(); // 편집 모드 종료
        location.reload(); // 페이지 새로고침으로 최신 데이터 로드
        alert(message || '저장이 완료되었습니다.');
    })
    .catch(error => {
        console.error('Error:', error);
        alert('저장 중 오류가 발생했습니다.');
    });
}
</script>