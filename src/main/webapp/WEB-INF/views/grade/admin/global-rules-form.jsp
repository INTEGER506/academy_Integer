<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>글로벌 규정 설정</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 800px; margin: 0 auto; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input, select, textarea { width: 100%; padding: 5px; border: 1px solid #ccc; }
        .btn { padding: 5px 10px; background-color: #f0f0f0; color: #333; border: 1px solid #ccc; cursor: pointer; text-decoration: none; display: inline-block; margin-right: 10px; }
        .grade-row { margin-bottom: 10px; }
        .grade-row input { width: 20%; margin-right: 2%; }
        .grade-row label { display: inline-block; width: 8%; }
        .info-box { background-color: #f9f9f9; border: 1px solid #ddd; padding: 10px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>글로벌 성적 규정 설정</h1>
        
        <div class="info-box">
            <strong>정보:</strong> 글로벌 규정은 모든 과목에 공통으로 적용되는 기본 성적 규정입니다.
        </div>
        
        <form action="${pageContext.request.contextPath}/grade/admin/global-rules/save" method="post">
            <div id="gradeRules">
                <!-- 기본 규정들 -->
                <div class="grade-row">
                    <label>A+</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="95" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="100" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="4.5" required>
                    <input type="text" name="description" placeholder="설명" value="우수">
                    <input type="hidden" name="gradeName" value="A+">
                </div>
                
                <div class="grade-row">
                    <label>A</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="90" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="94" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="4.0" required>
                    <input type="text" name="description" placeholder="설명" value="우수">
                    <input type="hidden" name="gradeName" value="A">
                </div>
                
                <div class="grade-row">
                    <label>B+</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="85" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="89" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="3.5" required>
                    <input type="text" name="description" placeholder="설명" value="양호">
                    <input type="hidden" name="gradeName" value="B+">
                </div>
                
                <div class="grade-row">
                    <label>B</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="80" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="84" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="3.0" required>
                    <input type="text" name="description" placeholder="설명" value="양호">
                    <input type="hidden" name="gradeName" value="B">
                </div>
                
                <div class="grade-row">
                    <label>C+</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="75" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="79" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="2.5" required>
                    <input type="text" name="description" placeholder="설명" value="보통">
                    <input type="hidden" name="gradeName" value="C+">
                </div>
                
                <div class="grade-row">
                    <label>C</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="70" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="74" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="2.0" required>
                    <input type="text" name="description" placeholder="설명" value="보통">
                    <input type="hidden" name="gradeName" value="C">
                </div>
                
                <div class="grade-row">
                    <label>D+</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="65" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="69" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="1.5" required>
                    <input type="text" name="description" placeholder="설명" value="미흡">
                    <input type="hidden" name="gradeName" value="D+">
                </div>
                
                <div class="grade-row">
                    <label>D</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="60" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="64" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="1.0" required>
                    <input type="text" name="description" placeholder="설명" value="미흡">
                    <input type="hidden" name="gradeName" value="D">
                </div>
                
                <div class="grade-row">
                    <label>F</label>
                    <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" value="0" required>
                    <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" value="59" required>
                    <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" value="0.0" required>
                    <input type="text" name="description" placeholder="설명" value="불량">
                    <input type="hidden" name="gradeName" value="F">
                </div>
            </div>
            
            <div style="margin-top: 30px;">
                <button type="submit" class="btn btn-success">규정 저장</button>
                <a href="${pageContext.request.contextPath}/grade/admin/rule/global" class="btn btn-secondary">규정 목록</a>
                <a href="${pageContext.request.contextPath}/test-index" class="btn btn-secondary">테스트 페이지로</a>
            </div>
        </form>
    </div>
    
    <script>
        // 동적으로 학점 규정 추가 기능
        function addGradeRule() {
            const container = document.getElementById('gradeRules');
            const gradeRow = document.createElement('div');
            gradeRow.className = 'grade-row';
            gradeRow.innerHTML = `
                <label>학점</label>
                <input type="text" name="gradeName" placeholder="학점명" required>
                <input type="number" name="minScore" placeholder="최소점수" min="0" max="100" required>
                <input type="number" name="maxScore" placeholder="최대점수" min="0" max="100" required>
                <input type="number" name="gpa" placeholder="GPA" step="0.1" min="0" max="4.5" required>
                <input type="text" name="description" placeholder="설명">
                <button type="button" onclick="this.parentElement.remove()" style="background-color: #dc3545; color: white; border: none; padding: 8px; border-radius: 4px; cursor: pointer;">삭제</button>
            `;
            container.appendChild(gradeRow);
        }
        
        // 추가 버튼 생성
        const addButton = document.createElement('button');
        addButton.type = 'button';
        addButton.className = 'add-grade';
        addButton.textContent = '학점 규정 추가';
        addButton.onclick = addGradeRule;
        document.getElementById('gradeRules').appendChild(addButton);
    </script>
</body>
</html>
