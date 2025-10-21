// /script/app.js
// ================================================================
// 0) Mock JSON (서버 연동 전 단계 데모)
//    TODO(DB 연동 시 수정): 아래 MOCK 제거하고 AJAX로 교체
//    - 공지:  GET /api/notices?q=&page=&size=
//    - 알림:  GET /api/notifications?q=&page=&size=
//    - 강좌:  GET /api/courses?q=&... (필요 시)
// ================================================================
const MOCK = {
  notices: [
    { id: 101, title: "[공지] 2학기 수강신청 안내", date: "2025-09-08", dept: "전체", urgent: 1 },
    { id: 102, title: "[장학] 국가장학금 2차 신청 마감", date: "2025-09-07", dept: "전체", urgent: 0 },
    { id: 103, title: "[학사] 수업평가 실시 안내", date: "2025-09-03", dept: "컴퓨터공학과", urgent: 0 },
    { id: 104, title: "[안내] 개인정보 변경 요청", date: "2025-09-01", dept: "전체", urgent: 0 },
    { id: 105, title: "[공지] 휴학/복학 신청 기간", date: "2025-08-28", dept: "전체", urgent: 0 },
    { id: 106, title: "[공지] 도서관 야간 개방",   date: "2025-08-25", dept: "전체", urgent: 0 },
    { id: 107, title: "[학사] 졸업심사 서류 제출", date: "2025-08-21", dept: "전체", urgent: 0 },
    { id: 108, title: "[안내] 캠퍼스 주차 요금 인상", date: "2025-08-18", dept: "전체", urgent: 0 },
    { id: 109, title: "[학사] 성적 이의신청 기간", date: "2025-08-10", dept: "전체", urgent: 0 },
    { id: 110, title: "[공지] 정전 점검 안내",     date: "2025-08-02", dept: "전체", urgent: 0 },
  ],

  // 알림은 검색만 + 긴급/일반 구조로 단순화
  notifications: [
    // TODO(DB 연동 시 수정): 응답 스키마 {id,title,desc,date,urgent:boolean,unread:boolean}
    { id: 1, title: "백업 완료", desc: "정기 백업이 성공적으로 완료되었습니다.", date: "2025-09-08 05:30", urgent: false, unread: true },
    { id: 2, title: "졸업 심사 마감 임박", desc: "2월 졸업 심사 마감이 다가옵니다.", date: "2025-09-07 10:20", urgent: true,  unread: false },
    { id: 3, title: "강의실 예약 취소", desc: "301호 예약이 취소되었습니다.", date: "2025-09-05 12:15", urgent: true,  unread: false },
    { id: 4, title: "성적 입력 완료", desc: "2025-1 성적 입력이 완료되었습니다.", date: "2025-09-01 09:00", urgent: false, unread: true },
  ],

  courses: [
    { id: 11, name: "데이터베이스", code:"CSE301", dept:"컴퓨터공학과", prof:"이영희", capacity:40, applied:35, weekday:"화·목", period:"2-3", room:"공학관 301", status:"진행중", progress:70 },
    { id: 12, name: "운영체제",   code:"CSE302", dept:"컴퓨터공학과", prof:"박철수", capacity:35, applied:35, weekday:"월·수", period:"4-5", room:"공학관 204", status:"진행중", progress:46 },
    { id: 13, name: "마케팅 원론", code:"BUS101", dept:"경영학과",   prof:"홍길동", capacity:50, applied:50, weekday:"화·목", period:"1-2", room:"경영관 202", status:"마감",   progress:80 }
  ]
};

// ================================================================
// (공용) 관리자 공지 시드: 관리자/사용자 공지 리스트가 동일 데이터 보이도록 공유
//    TODO(DB 연동 시 수정): 서버 데이터로 교체
// ================================================================
function seedAdminNotices(){
  return [
    { id:110, title:"[공지] 2학기 수강신청 안내", content:"세부 일정 확인", dept:"전체", startDate:"2025-09-01", endDate:"2025-09-20", urgent:true,  status:"PUBLISHED" },
    { id:109, title:"[장학] 국가장학금 2차 신청", content:"마감 임박",       dept:"전체", startDate:"2025-09-05", endDate:"2025-09-10", urgent:false, status:"PUBLISHED" },
    { id:108, title:"[학사] 수업평가 실시 안내",   content:"평가 참여",       dept:"컴퓨터공학과", startDate:"2025-09-03", endDate:"2025-09-25", urgent:false, status:"HIDDEN" },
    { id:107, title:"[안내] 개인정보 변경 요청",   content:"정보 최신화",     dept:"전체", startDate:"2025-09-01", endDate:"2025-09-15", urgent:false, status:"DRAFT" },
    { id:106, title:"[공지] 휴학/복학 신청 기간",  content:"학사일정 참고",   dept:"전체", startDate:"2025-08-28", endDate:"2025-09-30", urgent:true,  status:"PUBLISHED" },
    { id:105, title:"[공지] 도서관 야간 개방",     content:"운영 시간",       dept:"전체", startDate:"2025-08-25", endDate:"2025-12-31", urgent:false, status:"PUBLISHED" },
    { id:104, title:"[학사] 졸업심사 서류 제출",   content:"제출 안내",       dept:"전체", startDate:"2025-08-21", endDate:"2025-09-10", urgent:false, status:"PUBLISHED" },
    { id:103, title:"[안내] 캠퍼스 주차 요금",     content:"요금 변경",       dept:"전체", startDate:"2025-08-18", endDate:"2025-12-31", urgent:false, status:"HIDDEN" },
    { id:102, title:"[학사] 성적 이의신청 기간",  content:"이의신청 안내",   dept:"전체", startDate:"2025-08-10", endDate:"2025-08-20", urgent:false, status:"PUBLISHED" },
    { id:101, title:"[공지] 정전 점검 안내",       content:"점검 시간",       dept:"전체", startDate:"2025-08-02", endDate:"2025-08-02", urgent:true,  status:"PUBLISHED" },
  ];
}

// ================================================================
// 1) 공통: 헤더 검색 -> 페이지 내 카드(.card-filter-item) 텍스트 필터
// ================================================================
function attachHeaderSearch() {
  const $input = $(".header-search input");
  if (!$input.length) return;

  $input.on("input", function () {
    const q = $(this).val().trim().toLowerCase();
    $(".card-filter-item").each(function () {
      const text = $(this).text().toLowerCase();
      $(this).toggle(text.indexOf(q) !== -1);
    });
  });
}

// ================================================================
// 2) 공지(Notice) 목록 + 검색 + 페이징 (클라이언트)
//    대상 요소: #noticeList, #noticePagination, #noticeSearch, #noticeDept(옵션)
// ================================================================
const NoticePager = {
  data: [],
  page: 1,
  pageSize: 5,
  filtered: [],

  init() {
    const $list = $("#noticeList");
    if (!$list.length) return; // 페이지에 notice 섹션이 없으면 skip

    // TODO(DB 연동 시 수정): AJAX로 대체
    // $.getJSON('/api/notices', { q:'', page:1, size:this.pageSize }, res => { this.data = res.items; ... });
    this.data = MOCK.notices.slice().sort((a,b)=> b.id - a.id);
    this.filtered = this.data;
    this.bind();
    this.render();
  },

  bind() {
    $("#noticeSearch").on("input", () => this.applyFilter());
    $("#noticeDept").on("change", () => this.applyFilter()); // 없으면 noop
  },

  applyFilter() {
    const q = ($("#noticeSearch").val() || "").toLowerCase();
    const dept = $("#noticeDept").val() || "ALL";

    this.filtered = this.data.filter(n => {
      const okDept = (dept === "ALL" || n.dept === dept || n.dept === "전체");
      const okText = n.title.toLowerCase().includes(q);
      return okDept && okText;
    });
    this.page = 1;
    this.render();
  },

  render() {
    const $list = $("#noticeList").empty();
    const start = (this.page - 1) * this.pageSize;
    const pageItems = this.filtered.slice(start, start + this.pageSize);

    if (!pageItems.length) {
      $list.append(`<div class="text-center text-gray-500 small py-4">검색 결과가 없습니다.</div>`);
    } else {
      pageItems.forEach(n => {
        const urgentBadge = n.urgent ? `<span class="pill pill-urgent ms-2">긴급</span>` : `<span class="pill pill-normal ms-2">일반</span>`;
        $list.append(`
          <div class="card-white p-16 mb-12 card-filter-item">
            <div class="d-flex justify-content-between align-items-start gap-12">
              <div>
                <div class="fw-700">${n.title}${urgentBadge}</div>
                <div class="xsmall text-gray-500 mt-1">${n.date} · ${n.dept}</div>
              </div>
              <a href="#!" class="btn btn-white rounded-pill h-40 px-3">상세</a>
            </div>
          </div>
        `);
      });
    }
    this.renderPagination();
  },

  renderPagination() {
    const $p = $("#noticePagination").empty();
    const total = this.filtered.length;
    const pages = Math.max(1, Math.ceil(total / this.pageSize));
    const mk = (p, label = p, active=false, disabled=false) =>
      `<li class="page-item ${active ? "active":""} ${disabled?"disabled":""}">
         <a class="page-link" href="#" data-page="${p}">${label}</a>
       </li>`;

    const prev = this.page - 1;
    const next = this.page + 1;

    $p.append(mk(prev, "«", false, this.page===1));
    for (let i=1;i<=pages;i++){ $p.append(mk(i, String(i), i===this.page)); }
    $p.append(mk(next, "»", false, this.page===pages));

    $p.find("a.page-link").on("click", (e)=>{
      e.preventDefault();
      const to = parseInt($(e.currentTarget).data("page"),10);
      if (to>=1 && to<=pages){ this.page = to; this.render(); }
    });
  }
};

// ================================================================
// 3) 알림(Notification) - 검색만 사용, 긴급/일반 배지
//    대상 요소: #notificationList, #notiSearch
// ================================================================
const NotiFilter = {
  init() {
    const $wrap = $("#notificationList");
    if (!$wrap.length) return;

    this.$wrap = $wrap;

    // TODO(DB 연동 시 수정): 초기 로드를 AJAX로 교체
    // $.getJSON('/api/notifications', { q:'', page:1, size:50 }, res => { this.data = res.items; this.bind(); this.render(this.data); });
    this.data = MOCK.notifications.slice();
    this.bind();
    this.render(this.data);
  },

  bind() {
    $("#notiSearch").on("input", () => {
      const q = ($("#notiSearch").val() || "").toLowerCase();

      // TODO(DB 연동 시 수정): 서버 검색으로 교체 가능
      const filtered = this.data.filter(n =>
        (n.title + " " + n.desc).toLowerCase().includes(q)
      );
      this.render(filtered);
    });
  },

  render(items) {
    this.$wrap.empty();
    if (!items.length){
      this.$wrap.append(`<div class="text-center text-gray-500 small py-4">알림이 없습니다.</div>`);
      return;
    }
    items.forEach(n=>{
      const unreadCls = n.unread ? "unread" : "";
      const badge = n.urgent
        ? `<span class="pill pill-urgent ms-2">긴급</span>`
        : `<span class="pill pill-normal ms-2">일반</span>`;

      this.$wrap.append(`
        <div class="notification-card ${unreadCls} card-filter-item">
          <div class="notification-title">${n.title} ${badge}</div>
          <div class="notification-desc">${n.desc}</div>
          <div class="notification-meta"><span>${n.date}</span></div>
        </div>
      `);
    });
  }
};

// ================================================================
// 4) 강좌(Course) 검색/필터 (클라이언트)
//    대상 요소: .course-card, #courseSearch, #courseDept, #courseWeekday, #courseStatus
// ================================================================
const CourseFilter = {
  init(){
    const $wrap = $("#courseListWrap");
    if (!$wrap.length) return;
    this.bind();
  },
  bind(){
    $("#courseSearch, #courseDept, #courseWeekday, #courseStatus").on("input change", () => {
      const q = ($("#courseSearch").val() || "").toLowerCase();
      const dept = $("#courseDept").val() || "ALL";
      const weekday = $("#courseWeekday").val() || "ALL";
      const status = $("#courseStatus").val() || "ALL";

      $(".course-card").each(function(){
        const $c = $(this);
        const text = $c.text().toLowerCase();
        const okText = text.includes(q);
        const okDept = (dept==="ALL" || text.includes(dept.toLowerCase()));
        const okWeek = (weekday==="ALL" || text.includes(weekday.toLowerCase()));
        const okStatus = (status==="ALL" || text.includes(status.toLowerCase()));
        $c.toggle(okText && okDept && okWeek && okStatus);
      });
    });
  }
};

/* =================================================================
   5) AdminNoticeList: 공지 관리(목록) - 검색/페이징/편집/삭제 (관리자용)
   TODO(DB 연동 시 수정): 아래 MOCK 제거, AJAX로 교체
================================================================= */
const AdminNoticeList = {
  data: [],
  filtered: [],
  page: 1,
  pageSize: 8,

  init() {
    const $tbody = $("#admNoticeTbody");
    if (!$tbody.length) return;

    // TODO(DB 연동 시 수정):
    // $.getJSON('/api/admin/notices', { q:'', page:1, size:this.pageSize }, res => {
    //   this.data = res.items; this.filtered=this.data.slice(); this.bind(); this.render();
    // });
    this.data = (window.MOCK && MOCK.noticesForAdmin) ? MOCK.noticesForAdmin.slice() : this.mockSeed();
    this.filtered = this.data.slice();

    this.bind();
    this.render();
  },

  bind() {
    // 검색(제목/내용/학과)
    $("#admNoticeSearch").on("input", () => {
      const q = ($("#admNoticeSearch").val()||"").toLowerCase();
      // TODO(DB 연동 시 수정): 서버 검색으로 교체 가능
      this.filtered = this.data.filter(n => (n.title + " " + n.content + " " + (n.dept||"")).toLowerCase().includes(q));
      this.page = 1;
      this.render();
    });

    // 편집 버튼
    $("#admNoticeTbody").on("click", "[data-role='edit']", (e) => {
      const id = $(e.currentTarget).closest("tr").data("id");
      location.href = `ad-notice.html?id=${id}`;
    });

    // 삭제 버튼
    $("#admNoticeTbody").on("click", "[data-role='del']", (e) => {
      const id = $(e.currentTarget).closest("tr").data("id");
      if (!confirm("삭제하시겠습니까?")) return;

      // TODO(DB 연동 시 수정): DELETE /api/admin/notices/{id}
      // $.ajax({ url:`/api/admin/notices/${id}`, method:'DELETE' })
      //   .done(()=> { this.data = this.data.filter(n=>n.id!==id); this.applySearchAgain(); });

      // 데모 동작
      this.data = this.data.filter(n=>n.id!==id);
      this.applySearchAgain();
    });
  },

  applySearchAgain(){
    const q = ($("#admNoticeSearch").val()||"").toLowerCase();
    this.filtered = this.data.filter(n => (n.title + " " + n.content + " " + (n.dept||"")).toLowerCase().includes(q));
    this.page = 1;
    this.render();
  },

  render() {
    const $tbody = $("#admNoticeTbody").empty();
    const start = (this.page-1)*this.pageSize;
    const items = this.filtered.slice(start, start+this.pageSize);

    if (!items.length) {
      $tbody.append(`<tr><td colspan="7" class="text-center text-gray-500 small py-4">목록이 없습니다.</td></tr>`);
    } else {
      items.forEach(n=>{
        const badge = n.urgent ? `<span class="pill pill-urgent">긴급</span>` : `<span class="pill pill-normal">일반</span>`;
        const period = `${n.startDate||'-'} ~ ${n.endDate||'-'}`;
        const titleCell = `<span class="truncate-1">${n.title}</span>`;
        $tbody.append(`
          <tr class="card-filter-item" data-id="${n.id}">
            <td>${n.id}</td>
            <td title="${n.title}">${titleCell}</td>
            <td>${n.dept||'전체'}</td>
            <td>${period}</td>
            <td>${n.status||'-'}</td>
            <td>${badge}</td>
            <td class="text-center">
              <button class="btn btn-white btn-sm rounded-pill me-1" data-role="edit"><i class="bi bi-pencil-square me-1"></i>편집</button>
              <button class="btn btn-white btn-sm rounded-pill" data-role="del"><i class="bi bi-trash3 me-1"></i>삭제</button>
            </td>
          </tr>
        `);
      });
    }
    this.renderPagination();
  },

  renderPagination() {
    const $p = $("#admNoticePagination").empty();
    const total = this.filtered.length;
    const pages = Math.max(1, Math.ceil(total/this.pageSize));
    const mk = (p, label=p, active=false, disabled=false)=>
      `<li class="page-item ${active?'active':''} ${disabled?'disabled':''}">
         <a class="page-link" href="#" data-page="${p}">${label}</a>
       </li>`;

    const prev = this.page-1, next = this.page+1;
    $p.append(mk(prev, "«", false, this.page===1));
    for(let i=1;i<=pages;i++){ $p.append(mk(i, i, i===this.page)); }
    $p.append(mk(next, "»", false, this.page===pages));

    $p.find("a.page-link").on("click",(e)=>{
      e.preventDefault();
      const to = parseInt($(e.currentTarget).data("page"),10);
      if (to>=1 && to<=pages){ this.page = to; this.render(); }
    });
  },

  mockSeed(){
    return seedAdminNotices();
  }
};

/* =================================================================
   6) UserNoticeList: 공지사항(조회 전용, 사용자용 notice.html)
   - 관리자 리스트와 동일한 데이터(시드/서버)를 사용하되, 편집/삭제 없음
   TODO(DB 연동 시 수정): AJAX로 교체
================================================================= */
const UserNoticeList = {
  data: [],
  filtered: [],
  page: 1,
  pageSize: 8,

  init() {
    const $tbody = $("#noticeUserTbody");
    if (!$tbody.length) return;

    // TODO(DB 연동 시 수정): 서버 공용 엔드포인트/쿼리로 교체
    this.data = (window.MOCK && MOCK.noticesForAdmin) ? MOCK.noticesForAdmin.slice() : seedAdminNotices();
    this.filtered = this.data.slice();

    this.bind();
    this.render();
  },

  bind() {
    $("#noticeSearchUser").on("input", () => {
      const q = ($("#noticeSearchUser").val()||"").toLowerCase();
      this.filtered = this.data.filter(n => 
        (n.title + " " + (n.content||"") + " " + (n.dept||""))
        .toLowerCase().includes(q)
      );
      this.page = 1;
      this.render();
    });
  },

  render() {
    const $tbody = $("#noticeUserTbody").empty();
    const start = (this.page-1)*this.pageSize;
    const items = this.filtered.slice(start, start+this.pageSize);

    if (!items.length) {
      $tbody.append(`<tr><td colspan="6" class="text-center text-gray-500 small py-4">공지사항이 없습니다.</td></tr>`);
    } else {
      items.forEach(n=>{
        const badge = n.urgent ? `<span class="pill pill-urgent">긴급</span>` : `<span class="pill pill-normal">일반</span>`;
        const period = `${n.startDate||'-'} ~ ${n.endDate||'-'}`;
        const titleCell = `<span class="truncate-1">${n.title}</span>`;
        $tbody.append(`
          <tr class="card-filter-item">
            <td>${n.id}</td>
            <td title="${n.title}">${titleCell}</td>
            <td>${n.dept||'전체'}</td>
            <td>${period}</td>
            <td>${n.status||'-'}</td>
            <td>${badge}</td>
          </tr>
        `);
      });
    }
    this.renderPagination();
  },

  renderPagination() {
    const $p = $("#noticeUserPagination").empty();
    const total = this.filtered.length;
    const pages = Math.max(1, Math.ceil(total/this.pageSize));
    const mk = (p, label=p, active=false, disabled=false)=>
      `<li class="page-item ${active?'active':''} ${disabled?'disabled':''}">
         <a class="page-link" href="#" data-page="${p}">${label}</a>
       </li>`;

    const prev = this.page-1, next = this.page+1;
    $p.append(mk(prev, "«", false, this.page===1));
    for(let i=1;i<=pages;i++){ $p.append(mk(i, i, i===this.page)); }
    $p.append(mk(next, "»", false, this.page===pages));

    $p.find("a.page-link").on("click",(e)=>{
      e.preventDefault();
      const to = parseInt($(e.currentTarget).data("page"),10);
      if (to>=1 && to<=pages){ this.page = to; this.render(); }
    });
  }
};

// ================================================================
// 7) 단일 DOM Ready (중복 제거)
//    - 페이지에 존재하는 섹션만 안전하게 초기화됨
// ================================================================
$(function(){
  attachHeaderSearch();
  NoticePager.init();      // 대시보드/일반 카드형 공지 프리뷰
  NotiFilter.init();       // 알림 센터
  CourseFilter.init();     // 강좌 필터
  AdminNoticeList.init();  // 관리자 공지 목록
  UserNoticeList.init();   // 사용자 공지 목록(조회 전용)
});
