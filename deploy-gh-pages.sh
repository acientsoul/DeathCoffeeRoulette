#!/bin/bash
# gh-pages 자동 배포 스크립트
set -e

# 1. webapp 폴더를 gh-pages 브랜치로 force push
REPO_URL=$(git config --get remote.origin.url)
TMP_DIR=$(mktemp -d)

cp -r webapp/* "$TMP_DIR" # 웹앱 파일 복사
cp ai_config.json "$TMP_DIR" 2>/dev/null || true # ai_config.json도 복사

cd "$TMP_DIR"
git init
git remote add origin "$REPO_URL"
git checkout -b gh-pages
git add .
git -c user.name='gh-pages-bot' -c user.email='gh-pages@local' commit -m "Deploy webapp to gh-pages"
git push --force origin gh-pages

cd -
rm -rf "$TMP_DIR"

echo "✅ gh-pages 브랜치로 배포 완료!"
