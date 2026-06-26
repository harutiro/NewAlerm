# NewAlerm

指定した時間範囲のあいだ、一定間隔でアラームを鳴らし続ける Android アプリです。

たとえば「7:00〜7:30 の間、5分おきに鳴らす」のように設定すると、範囲内の各時刻でアラームが発火します。範囲の最後のアラームだけ別の音（着信音など）に切り替えて、二度寝防止の「最後の合図」にできます。

## 主な機能

- **時間範囲 × 間隔のアラーム** — 開始時刻・終了時刻・鳴動間隔（分）を指定して一括スケジュール。
- **日跨ぎ対応** — 終了時刻が開始時刻以前の場合は翌日扱いとして解釈。
- **音の出し分け** — 通常のアラーム音と、最後の1回だけ鳴らす音を個別に設定可能（標準アラーム音／標準着信音／標準通知音から選択）。
- **ロック画面での全画面通知** — 発火時はロック画面でも全画面のアラーム画面を表示し、画面を点灯。
- **稼働状態に応じたホーム画面** — アイドル時は設定UI、稼働中はキャンセルのみのシンプル表示に切り替え。

## 動作要件

- Android 7.0 (API 24) 以上
- `compileSdk` / `targetSdk`: 36
- 正確なアラームのため `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`、全画面通知のため `USE_FULL_SCREEN_INTENT` などの権限を使用します。

## 技術スタック

- **言語**: Kotlin
- **UI**: Jetbrains Compose (Material3) + Navigation Compose
- **アーキテクチャ**: MVVM + Clean Architecture（Feature-First 構成）
- **アラーム**: `AlarmManager`（`setExactAndAllowWhileIdle`）+ `BroadcastReceiver` + フォアグラウンドサービス

## プロジェクト構成

```
app/src/main/java/net/harutiro/newalerm/
├── MainActivity.kt
├── core/
│   ├── entities/         # ScreenRoute など
│   ├── router/           # Router.kt（画面遷移・権限要求・アラーム開始/キャンセルを集約）
│   ├── utils/            # AlarmPermissions など
│   └── presenter/        # UI層（画面ごとに page / viewModel、共通 widget）
│       ├── home/         # ホーム画面（設定 or 稼働中表示）
│       ├── ringing/      # アラーム鳴動画面（全画面 Activity）
│       ├── soundSetting/ # アラーム音設定画面
│       └── widget/       # 共通 Composable
├── features/alarm/       # データ層（interface + Impl ペア）
│   ├── apis/             # AlarmScheduler / SoundPlayer など
│   ├── entities/         # AlarmConfig, AlarmSound
│   └── repositories/     # AlarmRepository, 音設定の永続化
├── service/              # AlarmForegroundService, AlarmReceiver
└── ui/theme/             # テーマ
```

各機能はデータ層を `interface` + `Impl` のペアで構成し、DI／テスト時のモック差し替えを可能にしています。ViewModel はテスト用に repository をコンストラクタインジェクションしています。

## ビルド & 実行

Android Studio でプロジェクトを開き、`app` 構成を実行してください。

CLI からビルドする場合:

```bash
./gradlew assembleDebug      # デバッグ APK をビルド
./gradlew installDebug       # 接続中の端末／エミュレータへインストール
```

