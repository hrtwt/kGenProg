<div align="center">
    <img src="../../assets/logo.png" width="40%" alt="logo">
</div>

<p align="center">
    <strong>kGenProg は Java プログラム向けの自動プログラム修正ツールです．</strong><br>
    C 言語向けの自動プログラム修正ツール GenProg の Java 向け実装です．
    遺伝的アルゴリズムを用いて修正を行います．
</p>

<p align=center>
    <a href="https://github.com/kusumotolab/kGenProg/releases/latest" alt="release"><img src="https://img.shields.io/github/release/kusumotolab/kGenProg.svg"></a>
    <a href="https://jitpack.io/#kusumotolab/kGenProg" alt="jitpack"><img src="https://jitpack.io/v/kusumotolab/kGenProg.svg"></a>
    <a href="https://github.com/kusumotolab/kGenProg/actions?query=workflow%3A%22Test+branches%22" alt="GitHub Actions"><img src="https://github.com/kusumotolab/kGenProg/workflows/Test%20branches/badge.svg"></a>
    <a href="https://github.com/kusumotolab/kGenProg/blob/master/LICENSE" alt="license"><img src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
</p>

<p align=center>
    <a href="../../README.md">:us:English</a> &nbsp; :jp:日本語
</p>

---

## 動作条件
- JDK11+

## インストール
kGenProg は単一の jar ファイルにまとめてあります．[ここ](https://github.com/kusumotolab/kGenProg/releases/latest)から jar ファイルをダウンロードしてください．

[kusumotolab/kGenProg-example](https://github.com/kusumotolab/kGenProg-example) リポジトリに kGenProg の動作確認用のバグをまとめてあります．
[ここ](https://github.com/kusumotolab/kGenProg-example/archive/master.zip)からすべてのバグをまとめた zip ファイルをダウンロードできます．


### Gradle
kGenProgはGradleの環境でも使いやすいように公開されています．
現在，Gradle用にはJitPackを利用しています．
GradleのビルドファイルでJitPackリポジトリを参照するには，build.gradleのrepositoriesに以下の'`maven {... `'で始まる行を追加してください．

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

次に，kGenProgへの依存を追加してください．

```gradle
dependencies {
    implementation 'com.github.kusumotolab:kGenProg:Tag'
}
```
上記の`Tag`は利用したいkGenProgのバージョンIDで置換してください．
より詳細な説明は[ここ](https://jitpack.io/#kusumotolab/kGenProg/)に英語で書かれています．


## 使用方法
```
$ java -jar path/to/kGenProg.jar [--config <path> | (-r <path> -s <path>... -t <path>...)]
    [-x <fqn>...] [-c <path>...] [-w <path>] [-o <path>] [-v | -q] [--headcount <num>]
    [--max-generation <num>] [--time-limit <sec>] [--test-time-limit <sec>]
    [--required-solutions <num>] [--random-seed <num>] [--fault-localization <name>]
```

### 使用例
[kGenProg/example](../../example) には kGenProg のテストに用いているバグが置いてあります．
[kGenProg/example/CloseToZero01](../../example/CloseToZero01) に対して kGenProg を実行するには次のコマンドを実行してください．

```sh
$ cd kGenProg/example/CloseToZero01
$ java -jar path/to/kGenProg.jar -r ./ -s src/example/CloseToZero.java -t src/example/CloseToZeroTest.java
```

`.toml` ファイルにパラメータをまとめておいて，実行時に指定することもできます．
[docs/kgenprog-config-template.toml](../kgenprog-config-template.toml) に設定ファイルのサンプルがあります．
```sh
$ java -jar path/to/kGenProg.jar --config kGenProg/example/CloseToZero01/kgenprog.toml
```

実行時にオプションを省略した場合は，カレントディレクトリの `kgenprog.toml` を読み込みます．
```sh
$ cd kGenProg/example/CloseToZero01
$ java -jar path/to/kGenProg.jar
```


### オプション
| オプション | 説明 | デフォルト値/デフォルト動作 |
|---|---|---|
| `--config` | 設定ファイルへのパス | カレントディレクトリの `kgenprog.toml` を読み込む |
| `-r`, `--root-dir` | 修正対象プロジェクトのルートディレクトリへのパス．テスト実行の都合上，対象プロジェクトのルートに移動した上でカレントディレクトリを指定することを推奨します． | なし |
| `-s`, `--src` | プロダクトコード（単体テスト用のコードを除く実装系のソースコード）へのパス，もしくはプロダクトコードを含むディレクトリへのパス．スペース区切りで複数指定可能． | なし |
| `-t`, `--test` | テストコード（単体テスト用のソースコード）へのパス，もしくはテストコードを含むディレクトリへのパス．スペース区切りで複数指定可能． | なし |
| `-x`, `--exec-test` | 遺伝的アルゴリズム中に実行されるテストクラスの完全限定名．バグを発現させるテストクラスを指定してください．スペース区切りで複数指定可能． | すべてのテストクラス |
| `-c`, `--cp` | 修正対象プロジェクトのビルドに必要なクラスパス．スペース区切りで複数指定可能． | なし |
| `-o`, `--out-dir` | kGenProg が結果を出力するディレクトリへのパス．出力ファイルはパッチファイルとGAの生成過程．それぞれ `--patch-output` と `--history-record` でon/offを切り替え可能． | `./kgenprog-out` |
| `-v`, `--verbose` | 詳細なログを出力する | `false` |
| `-q`, `--quiet` | エラー出力のみを行う | `false` |
| `--mutation-generating-count` | 遺伝的アルゴリズムの変異操作によって1つの世代に生成する個体の数 | 10 |
| `--crossover-generating-count` | 遺伝的アルゴリズムの交叉操作によって1つの世代に生成する個体の数 | 10 |
| `--headcount` | 遺伝的アルゴリズムの選択操作によって1世代に残される個体の最大数 | 100 |
| `--max-generation` | 遺伝的アルゴリズムを打ち切る世代数 | 10 |
| `--time-limit` | 遺伝的アルゴリズムを打ち切る時間（秒） | 60 |
| `--test-time-limit` | 各個体のビルドおよびテストを打ち切る時間（秒） | 10 |
| `--required-solutions` | 出力する解（修正パッチ）の数 | 1 |
| `--random-seed` | kGenProg 全体で用いる乱数のシード値 | 0 |
| `--scope` | 再利用候補の範囲（`PROJECT`，`PACKAGE`，`FILE`） | `PACKAGE` |
| `--fault-localization` | 欠陥限局の手法（Fault Localization）（`Ample`，`Jaccard`，`Ochiai`，`Tarantula`，`Zoltar`） | `Ochiai` |
| `--mutation-type` | 変異種別（`Simple`，`Heuristic`） | `Simple` |
| `--crossover-type` | 交叉種別（`Random`，`Single`，`Uniform`, `Cascade`） | `Random` |
| `--crossover-first-variant` | 交叉対象の第一バリアントの選択方法（`Elite`，`Random`） | `Random` |
| `--crossover-second-variant` | 交叉対象の第二バリアントの選択方法（`Elite`，`GeneSimilarity`，`Random`，`TestComplementary`) | `Random` |
| `--patch-output` | 解のパッチファイル生成の有無 | `false` |
| `--history-record` | 個体の生成過程および生成された全個体を記録するJSONファイルの記録の有無．この機能は実行速度の低下に繋がる点に注意 | `false` |
| `--version` | kGenProg のバーションを出力し終了する | `false` |


## 受賞
- [Best Poster Award - APSEC 2018](http://www.apsec2018.org/)  
kGenProg: A High-Performance, High-Extensibility and High-Portability APR System

