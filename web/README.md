# web — 静的サイト（プライバシーポリシー）

NewAlerm のプライバシーポリシーを掲載する静的サイトです。
Markdown を編集し、ビルドスクリプトで HTML に変換します。

## 構成

```
web/
├── src/            # Markdown ソース（ここを編集する）
│   └── index.md    # プライバシーポリシー本文
├── template.html   # HTML テンプレート（デザイン・共通レイアウト）
├── build.mjs       # Markdown → HTML 変換スクリプト（依存パッケージなし）
└── dist/           # 生成された HTML（公開対象）
    └── index.html
```

## ビルド方法

Node.js（v18 以上）が必要です。追加パッケージのインストールは不要です。

```bash
cd web
node build.mjs
```

`src/` 以下のすべての `*.md` が `dist/` に同名の `*.html` として出力されます。
ページのタイトルは各 Markdown の最初の見出し（`# ...`）から自動で設定されます。

## プレビュー

生成物はそのままブラウザで開けます。

```bash
open dist/index.html          # macOS
# もしくは簡易サーバーで確認
npx serve dist
```

## 編集のしかた

- 本文を変えたいときは `src/index.md` を編集して再ビルドします。
- ページを追加したいときは `src/xxx.md` を作成すると `dist/xxx.html` が生成されます。
- 見た目を変えたいときは `template.html` の `<style>` を編集します。

## 公開について

`dist/` を GitHub Pages などの静的ホスティングに配置すればそのまま公開できます。
公開前に `src/index.md` 内の連絡先メールアドレス（`your-email@example.com`）を
実際のものに置き換えてください。

## 対応している Markdown 記法

見出し / 段落 / 箇条書き（順序あり・なし）/ 太字 / 斜体 / インラインコード /
リンク / 引用 / 水平線。プライバシーポリシー用途に必要な範囲をカバーしています。
