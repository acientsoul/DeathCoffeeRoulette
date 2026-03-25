/**
 * 책 표지 이미지 다운로드 스크립트
 * Open Library 검색 API (영어 제목) → cover_i → 이미지 다운로드 → webapp/covers/
 */
const https = require('https');
const fs = require('fs');
const path = require('path');

const COVERS_DIR = path.join(__dirname, 'webapp', 'covers');
if (!fs.existsSync(COVERS_DIR)) fs.mkdirSync(COVERS_DIR, { recursive: true });

const books = [
  { id: 'moon_sixpence', search: 'The Moon and Sixpence Maugham' },
  { id: 'eleanor', search: 'Eleanor Oliphant Is Completely Fine Honeyman' },
  { id: 'light_empire', search: 'Your Republic Is Calling You Kim Young-ha' },
  { id: 'never_let_me', search: 'Never Let Me Go Ishiguro' },
  { id: 'remains_day', search: 'The Remains of the Day Ishiguro' },
  { id: 'god_small', search: 'The God of Small Things Arundhati Roy' },
  { id: 'invisible_cities', search: 'Invisible Cities Italo Calvino' },
  { id: 'woman_dunes', search: 'The Woman in the Dunes Abe Kobo' },
  { id: 'kokoro', search: 'Kokoro Natsume Soseki' },
  { id: 'blindness_city', search: 'Blindness Jose Saramago' },
  { id: 'grapes_wrath', search: 'The Grapes of Wrath Steinbeck' },
  { id: 'clockwork', search: 'A Clockwork Orange Anthony Burgess' },
  { id: 'secret_history', search: 'The Secret History Donna Tartt' },
  { id: 'name_rose', search: 'The Name of the Rose Umberto Eco' },
  { id: 'paper_menagerie', search: 'The Paper Menagerie Ken Liu' },
  { id: 'convenience', search: 'Convenience Store Woman Sayaka Murata' },
  { id: 'i_am_cat', search: 'I Am a Cat Natsume Soseki' },
  { id: 'trapeze', search: 'In the Pool Okuda Hide' },
  { id: 'handmaids', search: "The Handmaid's Tale Margaret Atwood" },
  { id: 'bell_jar', search: 'The Bell Jar Sylvia Plath' },
  { id: 'light_speed', search: 'If We Cannot Travel at the Speed of Light Kim Cho-yeop' },
  { id: 'snow_country', search: 'Snow Country Kawabata Yasunari' },
  { id: 'golden_pavilion', search: 'The Temple of the Golden Pavilion Mishima' },
  { id: 'south_border', search: 'South of the Border West of the Sun Murakami' },
  { id: 'night_circus', search: 'The Night Circus Erin Morgenstern' },
  { id: 'dark_matter', search: 'Dark Matter Blake Crouch' },
  { id: 'slaughterhouse', search: 'Slaughterhouse Five Kurt Vonnegut' },
  { id: 'stranger', search: 'The Stranger Albert Camus' },
  { id: 'notes_underground', search: 'Notes from Underground Dostoevsky' },
  { id: 'strait_gate', search: 'Strait Is the Gate Andre Gide' },
];

function fetchJSON(url) {
  return new Promise((resolve, reject) => {
    https.get(url, { headers: { 'User-Agent': 'BookCoverDownloader/1.0' } }, res => {
      let data = '';
      res.on('data', c => data += c);
      res.on('end', () => {
        try { resolve(JSON.parse(data)); }
        catch { reject(new Error('JSON parse error')); }
      });
    }).on('error', reject);
  });
}

function downloadBuffer(url, redirects = 0) {
  return new Promise((resolve, reject) => {
    if (redirects > 5) return reject(new Error('Too many redirects'));
    const mod = url.startsWith('https') ? https : require('http');
    mod.get(url, { headers: { 'User-Agent': 'BookCoverDownloader/1.0' } }, res => {
      if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        return downloadBuffer(res.headers.location, redirects + 1).then(resolve).catch(reject);
      }
      if (res.statusCode !== 200) return reject(new Error(`HTTP ${res.statusCode}`));
      const chunks = [];
      res.on('data', c => chunks.push(c));
      res.on('end', () => resolve(Buffer.concat(chunks)));
    }).on('error', reject);
  });
}

async function downloadBook(book, idx) {
  const outPath = path.join(COVERS_DIR, `${book.id}.jpg`);
  if (fs.existsSync(outPath) && fs.statSync(outPath).size > 1000) {
    console.log(`[${idx+1}/${books.length}] SKIP ${book.id} (exists)`);
    return true;
  }

  try {
    const q = encodeURIComponent(book.search);
    const json = await fetchJSON(`https://openlibrary.org/search.json?q=${q}&limit=1&fields=title,cover_i`);
    const coverId = json.docs?.[0]?.cover_i;
    if (!coverId) {
      console.log(`[${idx+1}/${books.length}] FAIL ${book.id} - no cover_i`);
      return false;
    }
    const imgUrl = `https://covers.openlibrary.org/b/id/${coverId}-L.jpg`;
    const buf = await downloadBuffer(imgUrl);
    if (buf.length < 1000) {
      console.log(`[${idx+1}/${books.length}] FAIL ${book.id} - too small (${buf.length}b)`);
      return false;
    }
    fs.writeFileSync(outPath, buf);
    console.log(`[${idx+1}/${books.length}] OK ${book.id} (${buf.length} bytes)`);
    return true;
  } catch (e) {
    console.log(`[${idx+1}/${books.length}] FAIL ${book.id} - ${e.message}`);
    return false;
  }
}

(async () => {
  console.log('=== 책 표지 다운로드 시작 (Open Library) ===\n');
  let ok = 0, fail = 0;
  for (let i = 0; i < books.length; i++) {
    if (await downloadBook(books[i], i)) ok++; else fail++;
    if (i < books.length - 1) await new Promise(r => setTimeout(r, 400));
  }
  console.log(`\n=== 완료: ${ok}개 성공, ${fail}개 실패 ===`);
})();
