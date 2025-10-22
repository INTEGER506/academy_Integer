// ✅ 숫자 2자리 포맷
function pad(n) {
    return n < 10 ? "0" + n : n;
}

// ✅ DB에서 가져온 일정
const ALL_EVENTS = window.ALL_EVENTS || [];
let calendar;

/**
 * 📌 이번 달 일정 사이드 목록 빌드
 */
function buildMonthList(currentDate){
    const y = currentDate.getFullYear(), m = currentDate.getMonth();
    const start = new Date(y, m, 1), next = new Date(y, m+1, 1);

    const items = ALL_EVENTS.map(ev=>{
        const s = new Date(ev.start);
        const e = ev.end ? new Date(ev.end) : new Date(ev.start);
        if (e < start || s >= next) return null;
        return { s, e, title: ev.title };
    }).filter(Boolean).sort((a,b)=> a.s - b.s);

    const list = document.getElementById("monthList");
    list.innerHTML = "";
    if(!items.length){
        list.innerHTML = '<div class="p-3 text-gray-500 small">이달의 일정이 없습니다.</div>';
        return;
    }

    items.forEach(it=>{
        const dateStr = it.s.toDateString()===it.e.toDateString()
            ? `${pad(it.s.getMonth()+1)}.${pad(it.s.getDate())}`
            : `${pad(it.s.getMonth()+1)}.${pad(it.s.getDate())} ~ ${pad(it.e.getMonth()+1)}.${pad(it.e.getDate())}`;
        const row = document.createElement("div");
        row.className = "item";
        row.innerHTML = `<div class="date">${dateStr}</div><div class="title">${it.title}</div>`;
        list.appendChild(row);
    });
}

/**
 * 📌 날짜 셀에 이벤트 있으면 점 표시
 */
function paintDots(view){
    const monthStart = view.currentStart;
    const nextMonthStart = view.currentEnd;
    document.querySelectorAll("#calendar .fc-daygrid-day").forEach(cell=>{
        cell.classList.remove("has-dot");
        const dateStr = cell.getAttribute("data-date");
        if(!dateStr) return;
        const d = new Date(dateStr);
        if(!(d >= monthStart && d < nextMonthStart)) return;
        const has = ALL_EVENTS.some(ev=>{
            const s = new Date(ev.start);
            const e = ev.end ? new Date(ev.end) : new Date(ev.start);
            return d >= s && d <= e;
        });
        if(has) cell.classList.add("has-dot");
    });
}

document.addEventListener("DOMContentLoaded", function(){
    const el = document.getElementById("calendar");

    calendar = new FullCalendar.Calendar(el, {
        themeSystem: "bootstrap5",   // ✅ Bootstrap5 스타일 적용
        initialView: "dayGridMonth",
        locale: "ko",
        headerToolbar: { left: "prev,next today", center: "title", right: "" },
        events: ALL_EVENTS,
        height: "auto",
        expandRows: true,

        // ✅ 월 바뀔 때마다 제목/목록/점 표시 갱신
        datesSet(info){
            const d = info.view.currentStart;
            document.getElementById("monthTitle").textContent =
                `${d.getFullYear()}년 ${d.getMonth()+1}월 학사일정`;
            buildMonthList(d);
            paintDots(info.view);
        },

        // ✅ 이벤트 클릭 → 상세보기 (추후 연동 가능)
        eventClick: function(info) {
            alert("제목: " + info.event.title + "\n시작일: " + info.event.startStr);
        },

        // ✅ 날짜 클릭 → 일정 추가 페이지 이동 (추후 모달 연동 가능)
        dateClick: function(info) {
            if (confirm(info.dateStr + "에 새 일정을 추가하시겠습니까?")) {
                window.location.href = "/calendar/add?date=" + info.dateStr;
            }
        }
    });

    calendar.render();
});
