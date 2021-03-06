package com.thingword.alphonso.materialmanage.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thingword.alphonso.materialmanage.app.MApplication;
import com.thingword.alphonso.materialmanage.bean.dbbean.DistributionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.LoadingInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductDetail;
import com.thingword.alphonso.materialmanage.bean.dbbean.ProductionInfo;
import com.thingword.alphonso.materialmanage.bean.dbbean.UnLoadingInfo;
import com.thingword.alphonso.materialmanage.bean.User;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DataProvider extends ContentProvider {
    public static final Object obj = new Object();
    public static final String AUTHORITY = "com.thingword.mam";
    public static final String SCHEME = "content://";

    private static final int LOADING_TABLE = 1;
    private static final int UNLOADING_TABLE = 2;
    private static final int DISTRIBUTION_TABLE = 3;
    private static final int PRODUCTION_TABLE = 4;
    private static final int PRODUCDETAIL_TABLE = 5;

    public static final String PATH_LOADING_TABLE = "/loadinginfo";
    public static final String PATH_UNLOADING_TABLE = "/unloadinginfo";
    public static final String PATH_DISTRIBUTION_TABLE = "/distributioninfo";
    public static final String PATH_PRODUCTION_TABLE = "/productioninfo";
    public static final String PATH_PRODUCDETAIL_TABLE = "/productdetail";

    public static final Uri LOADING_TABLE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_LOADING_TABLE);
    public static final Uri UNLOADING_TABLE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_UNLOADING_TABLE);
    public static final Uri DISTRIBUTION_TABLE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_DISTRIBUTION_TABLE);
    public static final Uri PRODUCTION_TABLE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_PRODUCTION_TABLE);
    public static final Uri PRODUCDETAIL_TABLE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_PRODUCDETAIL_TABLE);

    public static final String LOADING_TABLE_CONTENT_TYPE = "com.task.loading";
    public static final String UNLOADING_TABLE_CONTENT_TYPE = "com.task.unloading";
    public static final String DISTRIBUTION_TABLE_CONTENT_TYPE = "com.task.distribution";
    public static final String PRODUCTION_TABLE_CONTENT_TYPE = "com.task.production";
    public static final String PRODUCDETAIL_TABLE_CONTENT_TYPE = "com.task.productdetail";

    private static CupboardDBHelper mDBHelper;

    private static final UriMatcher sUriMATCHER = new UriMatcher(UriMatcher.NO_MATCH) {{
        addURI(AUTHORITY, "loadinginfo", LOADING_TABLE);
        addURI(AUTHORITY, "unloadinginfo", UNLOADING_TABLE);
        addURI(AUTHORITY, "distributioninfo", DISTRIBUTION_TABLE);
        addURI(AUTHORITY, "productioninfo", PRODUCTION_TABLE);
        addURI(AUTHORITY, "productdetail", PRODUCDETAIL_TABLE);
    }};

    public static CupboardDBHelper getDBHelper() {
        if (mDBHelper == null) {
            User user = UserSharedPreferences.getCusUser(MApplication.getContext());
            mDBHelper = new CupboardDBHelper(MApplication.getContext(), user.getUsername());
        }
        return mDBHelper;
    }

    public static void resetDBHelper() {
        User user = UserSharedPreferences.getCusUser(MApplication.getContext());
        mDBHelper = new CupboardDBHelper(MApplication.getContext(), user.getUsername());
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        synchronized (obj) {
            Cursor cursor;
            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            switch (sUriMATCHER.match(uri)) {
                case LOADING_TABLE://Demo列表
                    cursor = cupboard().withDatabase(db).query(LoadingInfo.class).
                            withProjection(projection).
                            withSelection(selection, selectionArgs).
                            orderBy(sortOrder).
                            getCursor();
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    break;
                case UNLOADING_TABLE://Demo列表
                    cursor = cupboard().withDatabase(db).query(UnLoadingInfo.class).
                            withProjection(projection).
                            withSelection(selection, selectionArgs).
                            orderBy(sortOrder).
                            getCursor();
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    break;
                case DISTRIBUTION_TABLE://Demo列表
                    cursor = cupboard().withDatabase(db).query(DistributionInfo.class).
                            withProjection(projection).
                            withSelection(selection, selectionArgs).
                            orderBy(sortOrder).
                            getCursor();
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    break;
                case PRODUCTION_TABLE://Demo列表
                    cursor = cupboard().withDatabase(db).query(ProductionInfo.class).
                            withProjection(projection).
                            withSelection(selection, selectionArgs).
                            orderBy(sortOrder).
                            getCursor();
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    break;
                case PRODUCDETAIL_TABLE://Demo列表
                    cursor = cupboard().withDatabase(db).query(ProductDetail.class).
                            withProjection(projection).
                            withSelection(selection, selectionArgs).
                            orderBy(sortOrder).
                            getCursor();
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Uri" + uri);
            }
            return cursor;
        }
    }


    private String matchTable(Uri uri) {
        String table;
        switch (sUriMATCHER.match(uri)) {
            case LOADING_TABLE:
                table = LoadingInfoDataHelper.TABLE_NAME;
                break;
            case UNLOADING_TABLE:
                table = UnLoadingInfoDataHelper.TABLE_NAME;
                break;
            case DISTRIBUTION_TABLE:
                table = DistributionInfoDataHelper.TABLE_NAME;
                break;
            case PRODUCTION_TABLE:
                table = ProductionInfoDataHelper.TABLE_NAME;
                break;
            case PRODUCDETAIL_TABLE:
                table = ProductDetailDataHelper.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
        return table;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMATCHER.match(uri)) {
            case LOADING_TABLE:
                return LOADING_TABLE_CONTENT_TYPE;
            case UNLOADING_TABLE:
                return UNLOADING_TABLE_CONTENT_TYPE;
            case DISTRIBUTION_TABLE:
                return DISTRIBUTION_TABLE_CONTENT_TYPE;
            case PRODUCTION_TABLE:
                return PRODUCTION_TABLE_CONTENT_TYPE;
            case PRODUCDETAIL_TABLE:
                return PRODUCDETAIL_TABLE_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (obj) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId = db.insert(matchTable(uri), null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
            if (rowId > 0) {
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        synchronized (obj) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for (ContentValues contentValues : values) {
                    db.insertWithOnConflict(matchTable(uri), BaseColumns._ID, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                return values.length;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (obj) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count = 0;
            db.beginTransaction();
            try {
                count = db.delete(matchTable(uri), selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (obj) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count;
            db.beginTransaction();
            try {
                count = db.update(matchTable(uri), values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    public static void clearDBCache() {
        synchronized (DataProvider.obj) {
//            DBHelper mDBHelper = DataProvider.getDBHelper();
//            SQLiteDatabase db = mDBHelper.getWritableDatabase();
//            db.delete(TaskDataHelper.TABLE_NAME, null, null);
        }
    }
}
