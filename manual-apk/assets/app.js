// ============================================================
//  죽음의 커피 룰렛 — app.js
// ============================================================

(() => {
"use strict";

// ── 기본 멤버 (무작위 순서) ──
const DEFAULT_MEMBERS = [
    "이동근","김승학","김동욱","김재훈","윤재훈","장하니","김현서"
];
function shuffle(arr) {
    const a = [...arr];
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
}

// ── 상태 ──
const state = {
    members: shuffle(DEFAULT_MEMBERS),
    currentTarget: 0,
    spinning: false,
    winner: null,
    animFrame: null,
};

// 캐릭터별 의상/모자 색상 팔레트
const COLORS = [
    "#e74c3c","#3498db","#2ecc71","#f39c12","#9b59b6","#1abc9c",
    "#e67e22","#e91e63","#00bcd4","#8bc34a","#ff5722","#607d8b"
];
// 캐릭터별 모자 스타일 (다양하게)
const HAT_STYLES = ["beanie","cap","ushanka","headband","tophat","none","beret","cap","beanie","headband","tophat","none"];

// ── DOM ──
const $ = (sel) => document.querySelector(sel);
const screens = {
    intro:  $("#screen-intro"),
    member: $("#screen-member"),
    game:   $("#screen-game"),
    result: $("#screen-result"),
};

// ============================================================
//  8비트 배경음악 (Web Audio API 칩튠)
// ============================================================
const audio = {
    ctx: null,
    masterGain: null,
    playing: false,
    timers: [],
};

function initAudioCtx() {
    if (audio.ctx) return;
    audio.ctx = new (window.AudioContext || window.webkitAudioContext)();
    audio.masterGain = audio.ctx.createGain();
    audio.masterGain.gain.value = 0.18; // 기본 음량 (낮게)
    audio.masterGain.connect(audio.ctx.destination);
}

// 8비트 음 하나 재생
function playNote(freq, startTime, duration, type, vol) {
    const ac = audio.ctx;
    const osc = ac.createOscillator();
    const gain = ac.createGain();
    osc.type = type || "square";
    osc.frequency.value = freq;
    gain.gain.setValueAtTime(vol || 0.3, startTime);
    gain.gain.exponentialRampToValueAtTime(0.001, startTime + duration);
    osc.connect(gain);
    gain.connect(audio.masterGain);
    osc.start(startTime);
    osc.stop(startTime + duration);
}

// 긴장감 있는 서부 스타일 8비트 루프
function playGameBGM() {
    if (audio.playing) return;
    initAudioCtx();
    if (audio.ctx.state === "suspended") audio.ctx.resume();
    audio.playing = true;

    // 멜로디 노트 (서부 긴장감 - 단조)
    const melody = [
        // bar 1: 긴장감 상승
        {f: 164.81, d: 0.2},  // E3
        {f: 196.00, d: 0.2},  // G3
        {f: 185.00, d: 0.15}, // F#3
        {f: 164.81, d: 0.25}, // E3
        {f: 0, d: 0.1},      // 쉼표
        {f: 130.81, d: 0.3},  // C3
        {f: 146.83, d: 0.2},  // D3
        {f: 164.81, d: 0.4},  // E3
        // bar 2: 반복 변형
        {f: 196.00, d: 0.2},  // G3
        {f: 220.00, d: 0.2},  // A3
        {f: 196.00, d: 0.15}, // G3
        {f: 164.81, d: 0.25}, // E3
        {f: 0, d: 0.1},
        {f: 146.83, d: 0.2},  // D3
        {f: 130.81, d: 0.3},  // C3
        {f: 123.47, d: 0.5},  // B2
        // bar 3: 클라이맥스
        {f: 196.00, d: 0.15}, // G3
        {f: 220.00, d: 0.15}, // A3
        {f: 246.94, d: 0.2},  // B3
        {f: 261.63, d: 0.3},  // C4
        {f: 246.94, d: 0.15}, // B3
        {f: 220.00, d: 0.15}, // A3
        {f: 196.00, d: 0.2},  // G3
        {f: 164.81, d: 0.5},  // E3
        // bar 4: 다운
        {f: 130.81, d: 0.25}, // C3
        {f: 146.83, d: 0.25}, // D3
        {f: 164.81, d: 0.3},  // E3
        {f: 0, d: 0.15},
        {f: 123.47, d: 0.2},  // B2
        {f: 110.00, d: 0.3},  // A2
        {f: 123.47, d: 0.2},  // B2
        {f: 130.81, d: 0.55}, // C3
    ];

    // 베이스 라인
    const bass = [
        {f: 65.41, d: 0.5},   // C2
        {f: 65.41, d: 0.5},
        {f: 82.41, d: 0.5},   // E2
        {f: 73.42, d: 0.5},   // D2
        {f: 65.41, d: 0.5},
        {f: 55.00, d: 0.5},   // A1
        {f: 61.74, d: 0.5},   // B1
        {f: 65.41, d: 0.5},
    ];

    function scheduleLoop() {
        if (!audio.playing) return;
        const ac = audio.ctx;
        const now = ac.currentTime + 0.05;

        // 멜로디
        let t = now;
        melody.forEach(n => {
            if (n.f > 0) playNote(n.f, t, n.d * 0.9, "square", 0.25);
            t += n.d;
        });

        // 베이스
        let bt = now;
        const loopLen = melody.reduce((s, n) => s + n.d, 0);
        const bassTotalD = bass.reduce((s, n) => s + n.d, 0);
        const bassRepeats = Math.ceil(loopLen / bassTotalD);
        for (let rep = 0; rep < bassRepeats && bt < now + loopLen; rep++) {
            bass.forEach(n => {
                if (bt < now + loopLen) {
                    playNote(n.f, bt, n.d * 0.85, "triangle", 0.35);
                }
                bt += n.d;
            });
        }

        // 드럼 (노이즈 킥)
        for (let i = 0; i < loopLen; i += 0.5) {
            const kickTime = now + i;
            // 킥
            const kickOsc = ac.createOscillator();
            const kickGain = ac.createGain();
            kickOsc.type = "sine";
            kickOsc.frequency.setValueAtTime(150, kickTime);
            kickOsc.frequency.exponentialRampToValueAtTime(30, kickTime + 0.12);
            kickGain.gain.setValueAtTime(0.4, kickTime);
            kickGain.gain.exponentialRampToValueAtTime(0.001, kickTime + 0.12);
            kickOsc.connect(kickGain);
            kickGain.connect(audio.masterGain);
            kickOsc.start(kickTime);
            kickOsc.stop(kickTime + 0.15);

            // 하이햇 (오프비트)
            if (i + 0.25 < loopLen) {
                const hhTime = now + i + 0.25;
                const bufSize = ac.sampleRate * 0.04;
                const noiseBuf = ac.createBuffer(1, bufSize, ac.sampleRate);
                const data = noiseBuf.getChannelData(0);
                for (let j = 0; j < bufSize; j++) data[j] = Math.random() * 2 - 1;
                const noiseSrc = ac.createBufferSource();
                noiseSrc.buffer = noiseBuf;
                const hhGain = ac.createGain();
                hhGain.gain.setValueAtTime(0.08, hhTime);
                hhGain.gain.exponentialRampToValueAtTime(0.001, hhTime + 0.04);
                noiseSrc.connect(hhGain);
                hhGain.connect(audio.masterGain);
                noiseSrc.start(hhTime);
                noiseSrc.stop(hhTime + 0.05);
            }
        }

        // 루프 예약
        const timerId = setTimeout(scheduleLoop, loopLen * 1000 - 200);
        audio.timers.push(timerId);
    }

    scheduleLoop();
}

// 총소리 효과
function playShotSound() {
    initAudioCtx();
    if (audio.ctx.state === "suspended") audio.ctx.resume();
    const ac = audio.ctx;
    const now = ac.currentTime;

    // 폭발음
    const bufSize = ac.sampleRate * 0.3;
    const noiseBuf = ac.createBuffer(1, bufSize, ac.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < bufSize; i++) data[i] = Math.random() * 2 - 1;
    const src = ac.createBufferSource();
    src.buffer = noiseBuf;
    const shotGain = ac.createGain();
    shotGain.gain.setValueAtTime(0.5, now);
    shotGain.gain.exponentialRampToValueAtTime(0.001, now + 0.3);
    const filter = ac.createBiquadFilter();
    filter.type = "lowpass";
    filter.frequency.setValueAtTime(3000, now);
    filter.frequency.exponentialRampToValueAtTime(200, now + 0.25);
    src.connect(filter);
    filter.connect(shotGain);
    shotGain.connect(audio.masterGain);
    src.start(now);
    src.stop(now + 0.35);
}

function stopBGM() {
    audio.playing = false;
    audio.timers.forEach(t => clearTimeout(t));
    audio.timers = [];
}

// ============================================================
//  화면 전환
// ============================================================
function showScreen(name) {
    Object.values(screens).forEach(s => s.classList.remove("active","fade-in"));
    screens[name].classList.add("active","fade-in");
    if (name === "intro")  { stopBGM(); initIntro(); }
    if (name === "game")   { initGame(); playGameBGM(); }
    if (name === "result") { stopBGM(); initResult(); }
    if (name === "member") stopBGM();
}

// ============================================================
//  1. 인트로 (스플래시) — 폭탄 폭발 Canvas
// ============================================================
let introAnim;
function initIntro() {
    const canvas = $("#intro-canvas");
    const ctx = canvas.getContext("2d");
    resize(canvas);

    const particles = [];
    for (let i = 0; i < 60; i++) {
        particles.push({
            x: canvas.width / 2,
            y: canvas.height / 2,
            vx: (Math.random() - 0.5) * 8,
            vy: (Math.random() - 0.5) * 8,
            r: Math.random() * 4 + 2,
            life: 1,
            decay: Math.random() * 0.015 + 0.005,
            color: Math.random() > 0.5 ? "#ff4444" : "#ffd700",
        });
    }

    cancelAnimationFrame(introAnim);
    (function loop() {
        introAnim = requestAnimationFrame(loop);
        ctx.fillStyle = "rgba(26,26,46,0.15)";
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        particles.forEach(p => {
            p.x += p.vx;
            p.y += p.vy;
            p.vy += 0.02;
            p.life -= p.decay;
            if (p.life <= 0) {
                p.x = canvas.width / 2 + (Math.random() - 0.5) * 40;
                p.y = canvas.height / 2 + (Math.random() - 0.5) * 40;
                p.vx = (Math.random() - 0.5) * 8;
                p.vy = (Math.random() - 0.5) * 8;
                p.life = 1;
            }
            ctx.globalAlpha = p.life;
            ctx.beginPath();
            ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
            ctx.fillStyle = p.color;
            ctx.fill();
        });
        ctx.globalAlpha = 1;
    })();
}

// ── 인트로 클릭 ──
screens.intro.addEventListener("click", () => {
    cancelAnimationFrame(introAnim);
    showScreen("member");
});

// ============================================================
//  2. 멤버 선택
// ============================================================
const nameInput   = $("#name-input");
const addBtn      = $("#add-btn");
const memberList  = $("#member-list");
const memberCount = $("#member-count");
const startBtn    = $("#start-game-btn");

function addMember(name) {
    name = name.trim();
    if (!name || state.members.length >= 12) return;
    if (state.members.includes(name)) return;
    state.members.push(name);
    renderMembers();
    nameInput.value = "";
    nameInput.focus();
}

function removeMember(idx) {
    state.members.splice(idx, 1);
    renderMembers();
}

function renderMembers() {
    memberList.innerHTML = state.members.map((m, i) => `
        <div class="member-item">
            <div class="member-avatar" style="background:${COLORS[i % COLORS.length]}">${m[0]}</div>
            <span class="member-name">${esc(m)}</span>
            <button class="btn-remove" data-idx="${i}">✕</button>
        </div>
    `).join("");
    memberCount.textContent = `참가자: ${state.members.length}명`;
    startBtn.disabled = state.members.length < 2;
}

function esc(s) {
    const d = document.createElement("div");
    d.textContent = s;
    return d.innerHTML;
}

addBtn.addEventListener("click", () => addMember(nameInput.value));
nameInput.addEventListener("keydown", e => { if (e.key === "Enter") addMember(nameInput.value); });
memberList.addEventListener("click", e => {
    const btn = e.target.closest(".btn-remove");
    if (btn) removeMember(Number(btn.dataset.idx));
});
startBtn.addEventListener("click", () => {
    if (state.members.length >= 2) showScreen("game");
});

// ============================================================
//  3. 게임 화면 — 1인칭 FPS + 사우스파크 캐릭터
// ============================================================
function initGame() {
    const canvas = $("#game-canvas");
    const ctx = canvas.getContext("2d");
    resize(canvas);

    const W = canvas.width, H = canvas.height;
    const members = state.members;
    const n = members.length;

    state.currentTarget = 0;
    state.spinning = true;
    state.winner = null;

    // 캐릭터 크기 (화면 비례)
    const charH = H * 0.52;  // 캐릭터 전체 높이
    const charCenterY = H * 0.42; // 캐릭터 중심 Y

    // 스핀 상태
    let speed = 80;
    let lastSwitch = 0;
    const totalSteps = 11 + Math.floor(Math.random() * 9);
    let stepCount = 0;
    let fired = false;
    let fireFlash = 0;
    let cameraShake = {x: 0, y: 0};

    // 카메라 패닝 (부드러운 슬라이드)
    let cameraX = 0;       // 현재 카메라 X
    let targetCameraX = 0; // 목표 카메라 X
    const slotWidth = W;   // 각 인물 간격 = 화면 너비 1개

    cancelAnimationFrame(state.animFrame);

    function draw(time) {
        state.animFrame = requestAnimationFrame(draw);

        // 카메라 스무스 이동
        cameraX += (targetCameraX - cameraX) * 0.18;

        // 화면 흔들림
        if (cameraShake.x !== 0 || cameraShake.y !== 0) {
            cameraShake.x *= 0.85;
            cameraShake.y *= 0.85;
            if (Math.abs(cameraShake.x) < 0.5) cameraShake.x = 0;
            if (Math.abs(cameraShake.y) < 0.5) cameraShake.y = 0;
        }

        ctx.save();
        ctx.translate(cameraShake.x, cameraShake.y);

        // ── 배경: 서부 석양 (카메라 X에 따라 패럴랙스) ──
        drawWesternBgFPS(ctx, W, H, cameraX);

        // ── 현재 보이는 캐릭터 (카메라 위치 기반) ──
        for (let i = 0; i < n; i++) {
            const posX = W / 2 + (i * slotWidth - cameraX);
            // 화면 밖이면 스킵
            if (posX < -W * 0.5 || posX > W * 1.5) continue;

            const isTarget = (i === state.currentTarget) && !fired;
            const isWinner = fired && (i === state.currentTarget);
            drawSouthParkChar(ctx, posX, charCenterY, charH, i, members[i], isTarget, isWinner);
        }

        // ── 1인칭 총 (화면 하단 고정, 카메라 흔들림과만 같이 움직임) ──
        ctx.restore(); // 흔들림 해제
        drawFPSGun(ctx, W, H, fireFlash > 0);

        if (fireFlash > 0) fireFlash--;

        // ── 회전 (타겟 전환) 로직 ──
        if (state.spinning && !fired) {
            if (time - lastSwitch > speed) {
                lastSwitch = time;
                state.currentTarget = (state.currentTarget + 1) % n;
                targetCameraX = state.currentTarget * slotWidth;
                stepCount++;

                const progress = stepCount / totalSteps;
                speed = 80 + (730 - 80) * Math.pow(progress, 2.5);

                if (stepCount >= totalSteps) {
                    // 당첨자를 1초간 겨눔
                    state.spinning = false;
                    state.winner = members[state.currentTarget];
                    setTimeout(() => {
                        // 1초 후 발사!
                        fired = true;
                        fireFlash = 18;
                        cameraShake = {x: (Math.random()-0.5)*25, y: -15};
                        stopBGM();
                        playShotSound();
                        setTimeout(() => {
                            cancelAnimationFrame(state.animFrame);
                            showScreen("result");
                        }, 1500);
                    }, 1000);
                }
            }
        }
    }

    // 초기 카메라 위치
    targetCameraX = state.currentTarget * slotWidth;
    cameraX = targetCameraX;

    state.animFrame = requestAnimationFrame(draw);
}

// ── 서부 배경 (FPS 패럴랙스) ──
function drawWesternBgFPS(ctx, W, H, camX) {
    // 하늘 그라데이션
    const sky = ctx.createLinearGradient(0, 0, 0, H * 0.7);
    sky.addColorStop(0, "#1a0a2e");
    sky.addColorStop(0.3, "#4a1942");
    sky.addColorStop(0.6, "#c84b31");
    sky.addColorStop(1, "#ecdbba");
    ctx.fillStyle = sky;
    ctx.fillRect(0, 0, W, H);

    // 태양 (느린 패럴랙스)
    const sunOffX = -(camX * 0.05) % W;
    const sunX = W * 0.75 + sunOffX, sunY = H * 0.22;
    const sunR = Math.min(W, H) * 0.07;
    const sunGlow = ctx.createRadialGradient(sunX, sunY, 0, sunX, sunY, sunR * 3);
    sunGlow.addColorStop(0, "rgba(255,200,50,0.8)");
    sunGlow.addColorStop(0.5, "rgba(255,100,0,0.2)");
    sunGlow.addColorStop(1, "transparent");
    ctx.fillStyle = sunGlow;
    ctx.fillRect(sunX - sunR * 3, sunY - sunR * 3, sunR * 6, sunR * 6);
    ctx.beginPath();
    ctx.arc(sunX, sunY, sunR, 0, Math.PI * 2);
    ctx.fillStyle = "#ffd700";
    ctx.fill();

    // 산 실루엣 (중간 패럴랙스)
    const mOff = -(camX * 0.12) % (W * 2);
    ctx.fillStyle = "#2d1b00";
    ctx.beginPath();
    ctx.moveTo(0, H * 0.65);
    for (let i = 0; i < 8; i++) {
        const px = W * (i * 0.18) + mOff;
        const py = H * (0.42 + Math.sin(i * 1.7) * 0.08);
        ctx.lineTo(px, py);
    }
    ctx.lineTo(W, H * 0.55);
    ctx.lineTo(W, H * 0.65);
    ctx.fill();

    // 사막 바닥
    ctx.fillStyle = "#3d2b1f";
    ctx.fillRect(0, H * 0.65, W, H * 0.35);

    // 바닥 텍스처 라인
    ctx.strokeStyle = "rgba(0,0,0,0.15)";
    ctx.lineWidth = 1;
    for (let i = 0; i < 5; i++) {
        const ly = H * (0.7 + i * 0.06);
        ctx.beginPath();
        ctx.moveTo(0, ly);
        ctx.lineTo(W, ly);
        ctx.stroke();
    }

    // 건물 실루엣 (빠른 패럴랙스)
    const bOff = -(camX * 0.25) % (W * 2);
    const bw = Math.min(W, H) * 0.08;
    ctx.fillStyle = "#1a0e00";
    ctx.fillRect(W * 0.05 + bOff, H * 0.48, bw, H * 0.17);
    ctx.fillRect(W * 0.05 + bOff - bw * 0.15, H * 0.45, bw * 1.3, H * 0.03);
    ctx.fillRect(W * 0.88 + bOff * 0.5, H * 0.5, bw, H * 0.15);
    ctx.fillRect(W * 0.88 + bOff * 0.5 - bw * 0.1, H * 0.47, bw * 1.2, H * 0.03);

    // 선인장
    const cOff = -(camX * 0.3) % (W * 2);
    drawCactus(ctx, W * 0.15 + cOff, H * 0.63, Math.min(W, H) * 0.025);
    drawCactus(ctx, W * 0.85 + cOff * 0.6, H * 0.62, Math.min(W, H) * 0.02);
}

function drawCactus(ctx, x, y, s) {
    ctx.fillStyle = "#1a4d1a";
    ctx.fillRect(x - s * 0.3, y - s * 3, s * 0.6, s * 3);
    ctx.fillRect(x - s * 1.5, y - s * 2.2, s * 1.2, s * 0.5);
    ctx.fillRect(x + s * 0.3, y - s * 1.8, s * 1, s * 0.5);
    ctx.fillRect(x - s * 1.5, y - s * 2.7, s * 0.5, s * 0.8);
    ctx.fillRect(x + s * 0.8, y - s * 2.5, s * 0.5, s * 1);
}

// ── 사우스파크 스타일 캐릭터 (참고 이미지 기반) ──
function drawSouthParkChar(ctx, x, y, totalH, idx, name, isTarget, isDead) {
    const color = COLORS[idx % COLORS.length];
    const hatStyle = HAT_STYLES[idx % HAT_STYLES.length];

    // 비율: 머리 45%, 몸통 35%, 다리 20%
    const headR = totalH * 0.225;    // 머리 반지름
    const headY = y - totalH * 0.28; // 머리 중심 Y
    const bodyW = headR * 1.8;       // 몸통 폭
    const bodyH = totalH * 0.30;     // 몸통 높이
    const bodyTop = headY + headR * 0.75; // 몸통 시작
    const legH = totalH * 0.12;      // 다리 높이 (아주 짧은 스텁)
    const legW = bodyW * 0.25;

    ctx.save();

    // 타겟 하이라이트 (밝은 글로우만)
    if (isTarget) {
        ctx.shadowColor = "rgba(255,50,50,0.6)";
        ctx.shadowBlur = 25;
    }

    // ── 다리 (아주 짧은 스텁, 검정) ──
    ctx.fillStyle = "#222";
    const legTop = bodyTop + bodyH - 2;
    ctx.fillRect(x - bodyW * 0.3, legTop, legW, legH);
    ctx.fillRect(x + bodyW * 0.3 - legW, legTop, legW, legH);
    // 신발
    ctx.fillStyle = "#111";
    ctx.fillRect(x - bodyW * 0.3 - 2, legTop + legH - 4, legW + 4, 5);
    ctx.fillRect(x + bodyW * 0.3 - legW - 2, legTop + legH - 4, legW + 4, 5);

    // ── 몸통 (단순 사각형, 의상 색) ──
    ctx.fillStyle = color;
    roundRect(ctx, x - bodyW / 2, bodyTop, bodyW, bodyH, 4);
    ctx.fill();
    // 몸통 아웃라인
    ctx.strokeStyle = shadeColor(color, -40);
    ctx.lineWidth = 1.5;
    roundRect(ctx, x - bodyW / 2, bodyTop, bodyW, bodyH, 4);
    ctx.stroke();

    // 팔 (단순 막대, 몸통 양쪽)
    ctx.strokeStyle = color;
    ctx.lineWidth = totalH * 0.035;
    ctx.lineCap = "round";
    // 왼팔
    ctx.beginPath();
    ctx.moveTo(x - bodyW / 2, bodyTop + bodyH * 0.2);
    ctx.lineTo(x - bodyW / 2 - bodyW * 0.25, bodyTop + bodyH * 0.65);
    ctx.stroke();
    // 오른팔
    ctx.beginPath();
    ctx.moveTo(x + bodyW / 2, bodyTop + bodyH * 0.2);
    ctx.lineTo(x + bodyW / 2 + bodyW * 0.25, bodyTop + bodyH * 0.65);
    ctx.stroke();
    // 손 (살색 원)
    ctx.fillStyle = "#ffdbac";
    const handR = totalH * 0.022;
    ctx.beginPath(); ctx.arc(x - bodyW/2 - bodyW*0.25, bodyTop + bodyH*0.65, handR, 0, Math.PI*2); ctx.fill();
    ctx.beginPath(); ctx.arc(x + bodyW/2 + bodyW*0.25, bodyTop + bodyH*0.65, handR, 0, Math.PI*2); ctx.fill();

    // ── 머리 (큰 원, 살색) ──
    ctx.fillStyle = "#ffdbac";
    ctx.strokeStyle = "#d4a574";
    ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.arc(x, headY, headR, 0, Math.PI * 2);
    ctx.fill();
    ctx.stroke();

    // 머리카락 (머리 위쪽, 모자 아래로 삐져나옴)
    const hairColor = ["#2c1810","#5c3317","#1a1a1a","#8B4513","#2c1810","#3d2b1f","#1a1a1a"][idx % 7];
    ctx.fillStyle = hairColor;
    // 이마 위 머리카락
    ctx.beginPath();
    ctx.arc(x, headY - headR * 0.1, headR * 0.92, Math.PI * 1.15, Math.PI * 1.85);
    ctx.lineTo(x + headR * 0.7, headY - headR * 0.55);
    ctx.lineTo(x - headR * 0.7, headY - headR * 0.55);
    ctx.fill();

    // ── 눈 (거대한 흰 타원 — 사우스파크 핵심!) ──
    const eyeW = headR * 0.52;   // 눈 가로 반지름
    const eyeH = headR * 0.62;   // 눈 세로 반지름
    const eyeY = headY + headR * 0.0;  // 눈 중심 Y
    const eyeGap = headR * 0.38; // 눈 사이 간격

    if (isDead) {
        // X 눈 (사망)
        ctx.strokeStyle = "#000";
        ctx.lineWidth = Math.max(2.5, headR * 0.08);
        const xs = headR * 0.22;
        // 왼쪽 X
        ctx.beginPath();
        ctx.moveTo(x - eyeGap - xs, eyeY - xs); ctx.lineTo(x - eyeGap + xs, eyeY + xs); ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(x - eyeGap + xs, eyeY - xs); ctx.lineTo(x - eyeGap - xs, eyeY + xs); ctx.stroke();
        // 오른쪽 X
        ctx.beginPath();
        ctx.moveTo(x + eyeGap - xs, eyeY - xs); ctx.lineTo(x + eyeGap + xs, eyeY + xs); ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(x + eyeGap + xs, eyeY - xs); ctx.lineTo(x + eyeGap - xs, eyeY + xs); ctx.stroke();
    } else {
        // 흰 타원 눈
        ctx.fillStyle = "#fff";
        ctx.strokeStyle = "#000";
        ctx.lineWidth = 1.5;
        // 왼쪽 눈
        ctx.beginPath();
        ctx.ellipse(x - eyeGap, eyeY, eyeW, eyeH, 0, 0, Math.PI * 2);
        ctx.fill(); ctx.stroke();
        // 오른쪽 눈
        ctx.beginPath();
        ctx.ellipse(x + eyeGap, eyeY, eyeW, eyeH, 0, 0, Math.PI * 2);
        ctx.fill(); ctx.stroke();

        // 동공 (작은 검은 점)
        ctx.fillStyle = "#000";
        const pupilR = headR * 0.09;
        ctx.beginPath(); ctx.arc(x - eyeGap + pupilR * 0.3, eyeY + pupilR * 0.5, pupilR, 0, Math.PI * 2); ctx.fill();
        ctx.beginPath(); ctx.arc(x + eyeGap + pupilR * 0.3, eyeY + pupilR * 0.5, pupilR, 0, Math.PI * 2); ctx.fill();

        // 눈썹 (단순 선)
        ctx.strokeStyle = "#000";
        ctx.lineWidth = Math.max(1.5, headR * 0.05);
        ctx.beginPath();
        ctx.moveTo(x - eyeGap - eyeW * 0.5, eyeY - eyeH - headR * 0.04);
        ctx.lineTo(x - eyeGap + eyeW * 0.3, eyeY - eyeH - headR * 0.08);
        ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(x + eyeGap - eyeW * 0.3, eyeY - eyeH - headR * 0.08);
        ctx.lineTo(x + eyeGap + eyeW * 0.5, eyeY - eyeH - headR * 0.04);
        ctx.stroke();
    }

    // ── 입 (작은 선 또는 호) ──
    ctx.strokeStyle = "#000";
    ctx.lineWidth = 1.5;
    if (isDead) {
        // 죽은 표정: 물결 입
        ctx.beginPath();
        ctx.moveTo(x - headR * 0.2, headY + headR * 0.5);
        ctx.quadraticCurveTo(x - headR * 0.1, headY + headR * 0.55, x, headY + headR * 0.48);
        ctx.quadraticCurveTo(x + headR * 0.1, headY + headR * 0.42, x + headR * 0.2, headY + headR * 0.5);
        ctx.stroke();
    } else {
        // 평상시: 작은 선
        ctx.beginPath();
        ctx.moveTo(x - headR * 0.15, headY + headR * 0.48);
        ctx.lineTo(x + headR * 0.15, headY + headR * 0.48);
        ctx.stroke();
    }

    // ── 모자 (캐릭터별 다양) ──
    drawHat(ctx, x, headY, headR, hatStyle, shadeColor(color, -25));

    // ── 이름표 ──
    const fontSize = Math.max(12, totalH * 0.085);
    ctx.font = `bold ${fontSize}px 'Malgun Gothic', sans-serif`;
    ctx.textAlign = "center";
    ctx.fillStyle = "#fff";
    ctx.strokeStyle = "#000";
    ctx.lineWidth = Math.max(3, fontSize * 0.25);
    ctx.strokeText(name, x, legTop + legH + fontSize + 8);
    ctx.fillText(name, x, legTop + legH + fontSize + 8);

    ctx.restore();
}

// ── 모자 그리기 ──
function drawHat(ctx, x, headY, headR, style, color) {
    ctx.fillStyle = color;
    ctx.strokeStyle = shadeColor(color, -30);
    ctx.lineWidth = 1;

    switch (style) {
        case "beanie": // 비니
            ctx.beginPath();
            ctx.arc(x, headY - headR * 0.9, headR * 0.75, Math.PI, 0);
            ctx.lineTo(x + headR * 0.85, headY - headR * 0.55);
            ctx.lineTo(x - headR * 0.85, headY - headR * 0.55);
            ctx.fill(); ctx.stroke();
            // 비니 밴드
            ctx.fillStyle = shadeColor(color, 30);
            ctx.fillRect(x - headR * 0.85, headY - headR * 0.7, headR * 1.7, headR * 0.15);
            // 방울
            ctx.fillStyle = color;
            ctx.beginPath();
            ctx.arc(x, headY - headR * 1.3, headR * 0.12, 0, Math.PI * 2);
            ctx.fill();
            break;
        case "cap": // 야구모자
            ctx.beginPath();
            ctx.arc(x, headY - headR * 0.7, headR * 0.7, Math.PI, 0);
            ctx.fill(); ctx.stroke();
            // 챙
            ctx.fillStyle = shadeColor(color, -15);
            ctx.beginPath();
            ctx.ellipse(x + headR * 0.3, headY - headR * 0.65, headR * 0.7, headR * 0.15, 0.15, 0, Math.PI * 2);
            ctx.fill();
            break;
        case "ushanka": // 귀달이 모자
            ctx.beginPath();
            ctx.arc(x, headY - headR * 0.85, headR * 0.8, Math.PI, 0);
            ctx.lineTo(x + headR * 0.9, headY - headR * 0.4);
            ctx.lineTo(x - headR * 0.9, headY - headR * 0.4);
            ctx.fill(); ctx.stroke();
            // 귀덮개
            ctx.fillStyle = shadeColor(color, 15);
            ctx.fillRect(x - headR * 1.0, headY - headR * 0.5, headR * 0.35, headR * 0.6);
            ctx.fillRect(x + headR * 0.65, headY - headR * 0.5, headR * 0.35, headR * 0.6);
            break;
        case "headband": // 머리띠
            ctx.fillRect(x - headR * 0.9, headY - headR * 0.75, headR * 1.8, headR * 0.2);
            break;
        case "tophat": // 높은 모자
            ctx.fillRect(x - headR * 0.5, headY - headR * 1.6, headR * 1.0, headR * 0.8);
            ctx.fillRect(x - headR * 0.8, headY - headR * 0.85, headR * 1.6, headR * 0.15);
            ctx.strokeRect(x - headR * 0.5, headY - headR * 1.6, headR * 1.0, headR * 0.8);
            break;
        case "beret": // 베레모
            ctx.beginPath();
            ctx.ellipse(x - headR * 0.1, headY - headR * 0.9, headR * 0.75, headR * 0.35, -0.15, 0, Math.PI * 2);
            ctx.fill(); ctx.stroke();
            break;
        default: // 모자 없음
            break;
    }
}

// ── 1인칭 스코프 뷰 (정면에서 보는 구도) ──
function drawFPSGun(ctx, W, H, flash) {
    const cx = W / 2;
    const cy = H / 2;
    const r = Math.min(W, H) * 0.40;

    ctx.save();

    // === 스코프 바깥 어둡게 (비네팅) ===
    ctx.fillStyle = "rgba(0,0,0,0.7)";
    ctx.beginPath();
    ctx.rect(0, 0, W, H);
    ctx.arc(cx, cy, r, 0, Math.PI * 2, true);
    ctx.fill();

    // === 스코프 렌즈 테두리 (두꺼운 금속 링) ===
    ctx.strokeStyle = "#1a1a1a";
    ctx.lineWidth = r * 0.07;
    ctx.beginPath();
    ctx.arc(cx, cy, r, 0, Math.PI * 2);
    ctx.stroke();
    // 내부 링
    ctx.strokeStyle = "#333";
    ctx.lineWidth = r * 0.025;
    ctx.beginPath();
    ctx.arc(cx, cy, r * 0.96, 0, Math.PI * 2);
    ctx.stroke();
    // 반사 하이라이트
    ctx.strokeStyle = "rgba(255,255,255,0.1)";
    ctx.lineWidth = r * 0.015;
    ctx.beginPath();
    ctx.arc(cx, cy, r * 0.95, Math.PI * 1.15, Math.PI * 1.75);
    ctx.stroke();

    // === 십자선 (가늘고 선명) ===
    ctx.strokeStyle = "rgba(0,0,0,0.85)";
    ctx.lineWidth = 1.5;
    // 가로선 (중앙 끊김)
    ctx.beginPath();
    ctx.moveTo(cx - r * 0.92, cy);
    ctx.lineTo(cx - r * 0.06, cy);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(cx + r * 0.06, cy);
    ctx.lineTo(cx + r * 0.92, cy);
    ctx.stroke();
    // 세로선 (중앙 끊김)
    ctx.beginPath();
    ctx.moveTo(cx, cy - r * 0.92);
    ctx.lineTo(cx, cy - r * 0.06);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(cx, cy + r * 0.06);
    ctx.lineTo(cx, cy + r * 0.92);
    ctx.stroke();

    // 십자 눈금 (거리 표시)
    ctx.lineWidth = 1;
    for (let i = 1; i <= 4; i++) {
        const d = r * 0.18 * i;
        const tick = r * 0.03;
        // 가로 눈금
        ctx.beginPath();
        ctx.moveTo(cx - d, cy - tick); ctx.lineTo(cx - d, cy + tick); ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(cx + d, cy - tick); ctx.lineTo(cx + d, cy + tick); ctx.stroke();
        // 세로 눈금
        ctx.beginPath();
        ctx.moveTo(cx - tick, cy - d); ctx.lineTo(cx + tick, cy - d); ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(cx - tick, cy + d); ctx.lineTo(cx + tick, cy + d); ctx.stroke();
    }

    // === 중앙 조준점 (빨간 도트) ===
    ctx.fillStyle = "#ff0000";
    ctx.beginPath();
    ctx.arc(cx, cy, 3, 0, Math.PI * 2);
    ctx.fill();
    // 도트 글로
    ctx.fillStyle = "rgba(255,0,0,0.3)";
    ctx.beginPath();
    ctx.arc(cx, cy, 7, 0, Math.PI * 2);
    ctx.fill();

    // === 우하단: 리볼버 실린더 (정면에서 본 탄창) ===
    const cylCx = W * 0.83;
    const cylCy = H * 0.83;
    const cylR = Math.min(W, H) * 0.095;

    // 실린더 본체 (금속 그라디언트)
    const cylGrad = ctx.createRadialGradient(
        cylCx - cylR * 0.3, cylCy - cylR * 0.3, 0,
        cylCx, cylCy, cylR
    );
    cylGrad.addColorStop(0, "#b0bec5");
    cylGrad.addColorStop(0.6, "#78909c");
    cylGrad.addColorStop(1, "#455a64");
    ctx.fillStyle = cylGrad;
    ctx.beginPath();
    ctx.arc(cylCx, cylCy, cylR, 0, Math.PI * 2);
    ctx.fill();
    // 외곽
    ctx.strokeStyle = "#263238";
    ctx.lineWidth = 2.5;
    ctx.stroke();

    // 중심축
    ctx.fillStyle = "#607d8b";
    ctx.beginPath();
    ctx.arc(cylCx, cylCy, cylR * 0.16, 0, Math.PI * 2);
    ctx.fill();
    ctx.strokeStyle = "#37474f";
    ctx.lineWidth = 1;
    ctx.stroke();

    // 6발 탄창
    for (let i = 0; i < 6; i++) {
        const a = Math.PI * 2 * i / 6 - Math.PI / 2;
        const bx = cylCx + Math.cos(a) * cylR * 0.58;
        const by = cylCy + Math.sin(a) * cylR * 0.58;
        // 탄창 구멍
        ctx.fillStyle = "#1a1a1a";
        ctx.beginPath();
        ctx.arc(bx, by, cylR * 0.22, 0, Math.PI * 2);
        ctx.fill();
        // 테두리
        ctx.strokeStyle = "#37474f";
        ctx.lineWidth = 1.5;
        ctx.stroke();
        // 탄환 케이스
        ctx.fillStyle = "#8d6e63";
        ctx.beginPath();
        ctx.arc(bx, by, cylR * 0.12, 0, Math.PI * 2);
        ctx.fill();
        // 뇌관
        ctx.fillStyle = "#bcaaa4";
        ctx.beginPath();
        ctx.arc(bx, by, cylR * 0.045, 0, Math.PI * 2);
        ctx.fill();
    }

    // === 발사 이펙트 ===
    if (flash) {
        // 화면 전체 섬광
        ctx.fillStyle = "rgba(255,255,200,0.3)";
        ctx.fillRect(0, 0, W, H);
        // 중앙 머즐 플래시
        const fg = ctx.createRadialGradient(cx, cy, 0, cx, cy, r * 0.6);
        fg.addColorStop(0, "rgba(255,255,200,0.95)");
        fg.addColorStop(0.2, "rgba(255,180,50,0.6)");
        fg.addColorStop(0.5, "rgba(255,80,0,0.2)");
        fg.addColorStop(1, "transparent");
        ctx.fillStyle = fg;
        ctx.beginPath();
        ctx.arc(cx, cy, r * 0.6, 0, Math.PI * 2);
        ctx.fill();
    }

    ctx.restore();
}

// ============================================================
//  4. 결과 화면
// ============================================================
function initResult() {
    const canvas = $("#result-canvas");
    const ctx = canvas.getContext("2d");
    resize(canvas);

    const W = canvas.width, H = canvas.height;

    // 스포트라이트 배경
    const spot = ctx.createRadialGradient(W/2, H*0.35, 0, W/2, H*0.35, Math.min(W,H)*0.6);
    spot.addColorStop(0, "rgba(255,215,0,0.15)");
    spot.addColorStop(1, "rgba(0,0,0,0.95)");
    ctx.fillStyle = "#0a0a0a";
    ctx.fillRect(0, 0, W, H);
    ctx.fillStyle = spot;
    ctx.fillRect(0, 0, W, H);

    // 당첨 캐릭터 (사우스파크 스타일, X눈)
    const winIdx = state.members.indexOf(state.winner);
    const charTotalH = Math.min(W, H) * 0.55;
    drawSouthParkChar(ctx, W/2, H*0.38, charTotalH, winIdx, "", false, true);

    // 이름 표시
    $("#result-name").textContent = state.winner;
}

// ── 다시하기 ──
$("#retry-btn").addEventListener("click", () => showScreen("member"));

// ============================================================
//  유틸
// ============================================================
function resize(canvas) {
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.parentElement.getBoundingClientRect();
    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    canvas.style.width = rect.width + "px";
    canvas.style.height = rect.height + "px";
    canvas.getContext("2d").scale(dpr, dpr);
    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    canvas.getContext("2d").setTransform(dpr, 0, 0, dpr, 0, 0);
}

function roundRect(ctx, x, y, w, h, r) {
    ctx.beginPath();
    ctx.moveTo(x + r, y);
    ctx.lineTo(x + w - r, y);
    ctx.quadraticCurveTo(x + w, y, x + w, y + r);
    ctx.lineTo(x + w, y + h - r);
    ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    ctx.lineTo(x + r, y + h);
    ctx.quadraticCurveTo(x, y + h, x, y + h - r);
    ctx.lineTo(x, y + r);
    ctx.quadraticCurveTo(x, y, x + r, y);
    ctx.closePath();
}

function shadeColor(hex, percent) {
    let r = parseInt(hex.slice(1,3),16),
        g = parseInt(hex.slice(3,5),16),
        b = parseInt(hex.slice(5,7),16);
    r = Math.min(255, Math.max(0, r + percent));
    g = Math.min(255, Math.max(0, g + percent));
    b = Math.min(255, Math.max(0, b + percent));
    return `rgb(${r},${g},${b})`;
}

// ── 리사이즈 대응 ──
window.addEventListener("resize", () => {
    const active = document.querySelector(".screen.active");
    if (active) {
        const canvas = active.querySelector("canvas");
        if (canvas && active.id === "screen-intro") initIntro();
        // 게임 중에는 리사이즈 무시 (진행 상태 보존)
    }
});

// ── 앱 시작: 기본 멤버 렌더 후 인트로 ──
renderMembers();
showScreen("intro");

})();
