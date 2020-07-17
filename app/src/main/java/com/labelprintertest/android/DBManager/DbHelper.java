package com.labelprintertest.android.DBManager;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DBを管理するSQLiteOpenHelperのClass
 */

public class DbHelper extends SQLiteOpenHelper {

    /** データベースの名前 */
    static final String DB_NAME = "db_printer_test";

    /** データベースのバージョン */
    static final int DB_VERSION = 1;

    public DbHelper(Activity act) {
        super(act.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         *   チケット定義情報
         *
         * 	サーバーのデータをクライアントにコピーする。
         * 	クライアントからサーバーへのコピーはしない。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_ticket(" +
                "ticketid INTEGER not null, " + //チケット(チケットホルダー等の物品含む）をユニークに表す数字を指定する
                "kenshumei text not null, " + //チケットの名称　「1日　大人」など 発券画面に表示する内容
                "kenshumeiinji text not null, " + //チケットの名称　「スノーエスカレータ」など チケットに印字する内容
                "nenreiso text not null, " + //チケットの年齢層　「幼児、小人、大人、シニア」を文字列を指定する
                "kakaku INTEGER not null, " + //価格を指定する
                "shohizeiritsu text not null, " + //消費税率を%で指定する
                "shohizeigaku INTEGER not null, " + //内税消費税額を指定する
                "seisansortno INTEGER not null, " + //清算情報表示欄に出力する順番を指定する
                "haikeishoku text, " + //チケット発券画面のボタンの背景色を指定。未指定の場合、標準の色を用いる。
                "mojishoku text, " + //チケット発券画面のボタンの文字色を指定。未指定の場合、標準の色を用いる。
                "yukokikanbi INTEGER, " + //発券日からの有効期間（日）を指定する
                "hannichikasan INTEGER, " + //n.5日券の場合に指定する。1:n.5日券 0 またはnull : n日券
                "profileno INTEGER, " + //チケットの印字プロファイルNoを指定する。
                "kikan_fr double not null, " + //このレコードが有効な期間の開始日
                "kikan_to double not null, " + //このレコードが有効な期間の終了日
                "sakuseiuserid text not null, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double not null, " + //新規作成した日時
                "koshinuserid text not null, " + //更新されたときのログインユーザーID
                "koshinnichiji double not null, " + //更新された日時
                "PRIMARY KEY ('ticketid', 'kikan_fr'));");

        /**
         *   チケットや領収書の印字レイアウト定義
         *
         * 	サーバーのデータをクライアントにコピーする。
         * 	クライアントからサーバーへのコピーはしない。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_ticketstyle(" +
                "profileno INTEGER, " + //チケットの印字プロファイルNoを指定する。
                "chohyokb text not null, " + //帳票の区分を示す(mst_kbn.kbtype = 'CHOHYOKB')
                "areano INTEGER not null, " + //印字エリアを示す番号
                "hyojikb text not null, " + //印字するかを指定する。(mst_kbn.kbtype = 'HYOJIKB')
                "injishubetsu text not null, " + //印字種別をセット (mst_kbn.kbtype = 'INJISHUBETSU')
                "fontmei text, " + //フォントを指定する。未指定の場合、標準のフォントを用いる。
                "fontsize INTEGER, " + //フォントをサイズをポイントで指定する。
                "fontstyle_shatai INTEGER, " + //フォントが斜体かを指定する。0斜体ではない。1斜体
                "fontstyle_bold INTEGER, " + //フォントが太字かを指定する。0太字ではない。1太字
                "shoshiki text, " + //出力書式を指定する。印字内容のパラメータについては区分定義を参照
                "xposfrom INTEGER not null, " + //x軸始点。チケット左端からの距離を0.1mm単位で指定。未指定の場合、0mm
                "yposfrom INTEGER not null, " + //y軸始点。チケット下端からの距離を0.1mm単位で指定。未指定の場合、0mm
                "xposto INTEGER, " + //x軸終点。チケット左端からの距離を0.1mm単位で指定。
                "yposto INTEGER, " + //y軸終点。チケット下端からの距離を0.1mm単位で指定。
                "filemei text, " + //画像ファイル名をセット。画像ではない場合はnull
                "img blob, " + //画像ファイルのバイナリデータをセット。画像ではない場合null
                "barcodetype INTEGER, " + //バーコードタイプ　CITIZEN Labelプリンターのバーコード種類を参照
                "barcodeheight INTEGER, " + //バーコードの高さ 0.1mm単位で指定
                "kikan_fr double not null, " + //このレコードが有効な期間の開始日
                "kikan_to double not null, " + //このレコードが有効な期間の終了日
                "sakuseiuserid text not null, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double not null, " + //新規作成した日時
                "koshinuserid text not null, " + //更新されたときのログインユーザーID
                "koshinnichiji double not null, " + //更新された日時
                "shironuki INTEGER" + //文字を白抜きにするか指定する。0:白抜きではない。1:白抜き
                ");");

        /**
         *   ログインユーザーの定義
         *
         * 	サーバーのデータをクライアントにコピーする。
         * 	クライアントからサーバーへのコピーはしない。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_user(" +
                "userid text not null, " + //ログインユーザーID
                "password text not null, " + //ハッシュ化(sha256)したパスワード。認証時は入力されたパスワードをハッシュ化し照合する。
                "username text not null, " + //ユーザー名
                "kengengrp text, " + //ユーザーの権限を指定する。詳細は区分定義を参照。未指定の場合、一般ユーザー権限とする。
                "sakuseiuserid text, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double, " + //新規作成した日時
                "koshinuserid text, " + //更新されたときのログインユーザーID
                "koshinnichiji double, " + //更新された日時
                "PRIMARY KEY ('userid'));");

        /**
         *   端末管理
         *
         *  サーバーのデータをクライアントにコピーしない。
         *  クライアントからサーバーへコピーする。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_tanmatsu(" +
                "tanmatsumei text not null, " + //WindowsのPC名をセット
                "tanmatsuno INTEGER not null, " + //端末を識別する番号。端末ごとに一意の値をセット
                "hanbaibasho text not null, " + //設置場所を表すコードをセット(mst_kbn.kbtype = 'HANBAIBASHO')
                "sakuseiuserid text not null, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double not null, " + //新規作成した日時
                "koshinuserid text not null, " + //更新されたときのログインユーザーID
                "koshinnichiji double not null, " + //更新された日時
                "PRIMARY KEY ('tanmatsumei'));");

        /**
         *   各種区分の定義
         *
         * 	サーバーのデータをクライアントにコピーする。
         * 	クライアントからサーバーへのコピーはしない。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_kbn(" +
                "kbtype text not null, " + //区分のタイプを指定する。
                "kbcd text not null, " + //区分のタイプの中で一意となるコードを指定する。
                "kbmei text not null, " + //区分名を指定指定する。
                "biko text, " + //区分に対する備考を指定する。
                "sakuseiuserid text not null, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double not null, " + //新規作成した日時
                "koshinuserid text not null, " + //更新されたときのログインユーザーID
                "koshinnichiji double not null, " + //更新された日時
                "PRIMARY KEY ('kbtype', 'kbcd'));");

        /**
         *   チケット発券画面の配置定義
         *
         * 	サーバーのデータをクライアントにコピーする。
         * 	クライアントからサーバーへのコピーはしない。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_ui_tabticket(" +
                "tickettypecd text not null, " + //チケット種類(mst_kbn.kbtype = 'TICKETTYPE')
                "xpos INTEGER not null, " + //チケット発券画面の横方向の表示位置を指定。
                "ypos INTEGER not null, " + //チケット発券画面の縦方向の表示位置を指定。
                "ticketid INTEGER not null, " + //チケット(チケットホルダー等の物品含む）をユニークに表す数字を指定する
                "kikan_fr double not null, " + //このレコードが有効な期間の開始日
                "kikan_to double not null, " + //このレコードが有効な期間の終了日
                "sakuseiuserid text not null, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double not null, " + //新規作成した日時
                "koshinuserid text not null, " + //更新されたときのログインユーザーID
                "koshinnichiji double not null, " + //更新された日時
                "PRIMARY KEY ('tickettypecd', 'xpos', 'ypos', 'kikan_fr'));");

        /**
         *   画面タブ表示定義
         *
         * 	サーバーのデータをクライアントにコピーする。
         * 	クライアントからサーバーへのコピーはしない。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_ui_tab(" +
                "tickettypecd text not null, " + //チケット種類(mst_kbn.kbtype = 'TICKETTYPE')
                "hyojikb text not null, " + //このレコードが示すタブを表示するか。(mst_kbn.kbtype = 'HYOJIKB')
                "sortno INTEGER not null, " + //タブの表示順を指定する。
                "kikan_fr double not null, " + //このレコードが有効な期間の開始日
                "kikan_to double not null, " + //このレコードが有効な期間の終了日
                "sakuseiuserid text not null, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double not null, " + //新規作成した日時
                "koshinuserid text not null, " + //更新されたときのログインユーザーID
                "koshinnichiji double not null, " + //更新された日時
                "PRIMARY KEY ('tickettypecd', 'kikan_fr'));");

        /**
         *   採番管理
         *
         *  サーバーのデータをクライアントにコピーしない。
         *  クライアントからサーバーへコピーする。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS mst_counter(" +
                "saibankb text not null, " + //採番区分をセット
                "saibancd text not null, " + //採番CD。採番区分が領収書の場合、端末名をセット
                "genzaino INTEGER not null, " + //採番済みの番号。採番されるごとにカウントアップする。
                "sakuseiuserid text not null, " + //新規作成した時のログインユーザーID
                "sakuseinichiji double not null, " + //新規作成した日時
                "koshinuserid text not null, " + //更新されたときのログインユーザーID
                "koshinnichiji double not null);"); //更新された日時
//                "PRIMARY KEY ('saibankb', 'saibancd'));");

        /**
         *   売上情報の記録
         *
         *  サーバーのデータをクライアントにコピーしない。
         *  クライアントからサーバーへコピーする。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS dat_record(" +
                "uriageno INTEGER not null, " + //売上情報のレコードをユニークに表す番号。カウンタマスタ.採番区分=URIAGE
                "gyono INTEGER not null, " + //発券画面で入力した順。
                "tanmatsumei text not null, " + //発券した端末名をセット。
                "hanbaibasho text not null, " + //チケットの販売場所。端末マスタの販売場所をセット。
                "uriagenichiji double not null, " + //売上げた日時。
                "ticketid INTEGER not null, " + //売上のあったチケットを識別するID。
                "uriagesuryo INTEGER not null, " + //売り上げた数量。
                "hanbaitanka INTEGER not null, " + //売り上げたチケットの単価。
                "hanbaikingaku INTEGER not null, " + //売り上げたチケットの金額。数量*単価をセット。
                "shohizeiritsu text not null, " + //消費税率を%単位でセット。
                "shohizeigaku INTEGER not null, " + //内税消費税額をセット。
                "tickettypecd text not null, " + //売り上げたチケット種類(mst_kbn.kbtype = 'TICKETTYPE')
                "meisho text not null, " + //売り上げたチケットの名称。
                "haraimodoshikb text not null, " + //払戻かをセット。(mst_kbn.kbtype = 'HARAIMODOSHIKB')
                "uriagekb text not null, " + //売上区分をセット。(mst_kbn.kbtype = 'URIAGEKB')
                "shimeno INTEGER not null, " + //締め処理済みの場合、清算情報のshimenoをセット。締め処理が行われていない場合0をセット
                "sakuseiuserid text not null, " + //データ作成時のログインユーザーのコードをセット。
                "sakuseinichiji double not null, " + //データ作成日時をセット。
                "koshinuserid text not null, " + //データ更新もしくは転記したログインユーザーのコードをセット。
                "koshinnichiji double not null, " + //データ更新日時もしくは、転記された日時をセット。
                "PRIMARY KEY ('uriageno', 'gyono', 'tanmatsumei'));");

        /**
         *   領収書の発行履歴
         *
         *  サーバーのデータをクライアントにコピーしない。
         *  クライアントからサーバーへコピーする。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS dat_ryoshu(" +
                "ryoshuno INTEGER not null, " + //領収書に特定する番号。連番で採番する。カウンタマスタ.採番区分=RYOSHUSHO
                "tanmatsumei text not null, " + //領収書を発行した端末名をセット。
                "hanbaibasho text not null, " + //領収書を発行した場所。端末マスタの販売場所をセット。
                "hakkonichiji double not null, " + //領収書を発行した日時。
                "ryoshukingaku INTEGER not null, " + //領収金額をセット。
                "tadashigaki text, " + //但し書きをセット。
                "uriageno INTEGER, " + //領収書の発行元の売上Noをセット。
                "sakuseiuserid text not null, " + //データ作成時のログインユーザーのコードをセット。
                "sakuseinichiji double not null, " + //データ作成日時をセット。
                "koshinuserid text not null, " + //データ更新もしくは転記したログインユーザーのコードをセット。
                "koshinnichijidat_expense double not null, " + //データ更新日時もしくは、転記された日時をセット。
                "PRIMARY KEY ('ryoshuno', 'tanmatsumei'));");

        /**
         *   清算情報の内訳記録
         *
         *  サーバーのデータをクライアントにコピーしない。
         *  クライアントからサーバーへコピーする。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS dat_expense(" +
                "shimeno INTEGER not null, " + //清算情報をユニークに表す番号。カウンタマスタ.採番区分=SEISAN
                "tanmatsumei text not null, " + //清算を行った端末名をセット。
                "hanbaibasho text not null, " + //チケットの販売場所。端末マスタの販売場所をセット。
                "shimebi double not null, " + //締め処理日。
                "uriagekb INTEGER not null, " + //売上区分。(mst_kbn.kbtype = 'URIAGEKB')
                "haraimodoshikb text not null, " + //払戻区分。(mst_kbn.kbtype = 'HARAIMODOSHIKB')
                "suryo INTEGER not null, " + //売上区分、払戻区分ごとに集計した数量。
                "kingaku INTEGER not null, " + //売上区分、払戻区分ごとに集計した金額。
                "shohizei INTEGER not null, " + //売上区分、払戻区分ごとに集計した消費税。
                "sakuseiuserid text not null, " + //データ作成時のログインユーザーのコードをセット。
                "sakuseinichiji double not null, " + //データ作成日時をセット。
                "koshinuserid text not null, " + //データ更新もしくは転記したログインユーザーのコードをセット。
                "koshinnichiji double not null, " + //データ更新日時もしくは、転記された日時をセット。
                "PRIMARY KEY ('shimeno', 'tanmatsumei', 'hanbaibasho', 'uriagekb', 'haraimodoshikb'));");

        /**
         *   XML更新履歴。XMLファイルの内容が更新された都度登録する。
         *
         *  サーバーのデータをクライアントにコピーしない。
         *  クライアントからサーバーへコピーする。
         * */
        db.execSQL("CREATE TABLE IF NOT EXISTS dat_history(" +
                "tekiyonichiji double not null, " + //XMLファイルを適用した日付。
                "tanmatsumei text not null, " + //XMLファイルを適用した端末名。
                "filemei text not null, " + //XMLファイル名。
                "filenaiyo text not null, " + //XMLファイルの内容。
                "sakuseiuserid text not null, " + //データ作成時のログインユーザーのコードをセット。
                "sakuseinichiji double not null, " + //データ作成日時をセット。
                "koshinuserid text not null, " + //データ更新もしくは転記したログインユーザーのコードをセット。
                "koshinnichiji double not null, " + //データ更新日時もしくは、転記された日時をセット。
                "PRIMARY KEY ('tekiyonichiji', 'tanmatsumei', 'filemei'));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}