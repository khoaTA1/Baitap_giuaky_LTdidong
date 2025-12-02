package com.example.bt1.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bt1.models.Product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME = "HealthyMultiDB.db";

    // thông tin bảng user
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_FULLNAME = "fullname";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_PASSWORD = "hashedpassword";
    public static final String USER_COLUMN_PHONE = "phone";
    public static final String USER_COLUMN_ADDRESS = "address";
    public static final String USER_COLUMN_AVATAR = "avatar";
    public static final String USER_COLUMN_CREATEDATE = "createdate";
    public static final String USER_COLUMN_SALT = "salt";

    // thông tin bảng product
    public static final String PRODUCT_TABLE_NAME = "products";
    public static final String PRODUCT_COLUMN_ID = "id";
    public static final String PRODUCT_COLUMN_NAME = "name";
    public static final String PRODUCT_COLUMN_BRAND = "brand";
    public static final String PRODUCT_COLUMN_PRICE = "price";
    public static final String PRODUCT_COLUMN_STOCK = "stock";
    public static final String PRODUCT_COLUMN_IMAGE = "image";
    public static final String PRODUCT_COLUMN_ONDEAL = "onDeal";
    public static final String PRODUCT_COLUMN_DISCOUNT = "discountPercent";
    public static final String PRODUCT_COLUMN_ISACTIVE = "isActive";
    public static final String PRODUCT_COLUMN_ORIGINAL = "original";
    public static final String PRODUCT_COLUMN_CATEGORY = "category";
    public static final String PRODUCT_COLUMN_DOSAGEFORM = "dosageForm";
    public static final String PRODUCT_COLUMN_INCLUDE = "include";
    public static final String PRODUCT_COLUMN_INGREDIENT = "ingredient";
    public static final String PRODUCT_COLUMN_RATING = "rating";
    public static final String PRODUCT_COLUMN_DESCRIPT = "description";
    public static final String PRODUCT_COLUMN_USE = "use";
    public static final String PRODUCT_COLUMN_SIDEEFFECTS = "sideEffects";
    public static final String PRODUCT_COLUMN_OBJECT = "object";

    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, 2);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + USER_TABLE_NAME + " ("
                + USER_COLUMN_ID + " integer primary key, "
                + USER_COLUMN_EMAIL + " text, "
                + USER_COLUMN_FULLNAME + " text, "
                + USER_COLUMN_PHONE + " text, "
                + USER_COLUMN_PASSWORD + " text, "
                + USER_COLUMN_SALT + " text, "
                + USER_COLUMN_AVATAR + " text, "
                + USER_COLUMN_ADDRESS + " text, "
                + USER_COLUMN_CREATEDATE + " text"
                + ")";

        String createProductTableQuery = "create table " + PRODUCT_TABLE_NAME + " ("
                + PRODUCT_COLUMN_ID + " integer primary key, "
                + PRODUCT_COLUMN_NAME + " text, "
                + PRODUCT_COLUMN_BRAND + " text, "
                + PRODUCT_COLUMN_PRICE + " integer, "
                + PRODUCT_COLUMN_STOCK + " integer, "
                + PRODUCT_COLUMN_IMAGE + " text, "
                + PRODUCT_COLUMN_ONDEAL + " boolean, "
                + PRODUCT_COLUMN_DISCOUNT + " integer, "
                + PRODUCT_COLUMN_ISACTIVE + " boolean, "
                + PRODUCT_COLUMN_ORIGINAL + " text, "
                + PRODUCT_COLUMN_CATEGORY + " text, "
                + PRODUCT_COLUMN_DOSAGEFORM + " text, "
                + PRODUCT_COLUMN_INCLUDE + " text, "
                + PRODUCT_COLUMN_INGREDIENT + " text, "
                + PRODUCT_COLUMN_RATING + " real, "
                + PRODUCT_COLUMN_DESCRIPT + " text, "
                + PRODUCT_COLUMN_USE + " text, "
                + PRODUCT_COLUMN_SIDEEFFECTS + " text, "
                + PRODUCT_COLUMN_OBJECT + " text"
                + ")";
        db.execSQL(createProductTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + PRODUCT_TABLE_NAME);
        db.execSQL("drop table if exists " + USER_TABLE_NAME);
        onCreate(db);
    }

    public void insertProducts(List<Product> productList) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            Log.d(">>> SQLite", "insertProducts: bắt đầu insert sản phẩm vào sqlite");
            for (Product product : productList) {
                ContentValues values = new ContentValues();
                values.put(PRODUCT_COLUMN_ID, product.getId());
                values.put(PRODUCT_COLUMN_NAME, product.getName());
                values.put(PRODUCT_COLUMN_BRAND, product.getBrand());
                values.put(PRODUCT_COLUMN_PRICE, product.getPrice());
                values.put(PRODUCT_COLUMN_STOCK, product.getStock());
                values.put(PRODUCT_COLUMN_IMAGE, product.getImagePath());
                values.put(PRODUCT_COLUMN_ONDEAL, product.getOnDeal() ? 1 : 0);
                values.put(PRODUCT_COLUMN_DISCOUNT, product.getDiscountPercent());
                values.put(PRODUCT_COLUMN_ISACTIVE, product.getActive() ? 1 : 0);
                values.put(PRODUCT_COLUMN_ORIGINAL, product.getOriginal());
                values.put(PRODUCT_COLUMN_CATEGORY, product.getCategory());
                values.put(PRODUCT_COLUMN_DOSAGEFORM, product.getDosageForm());
                values.put(PRODUCT_COLUMN_INCLUDE, product.getInclude());
                values.put(PRODUCT_COLUMN_INGREDIENT, product.getIngredient());
                values.put(PRODUCT_COLUMN_RATING, product.getRating());
                values.put(PRODUCT_COLUMN_DESCRIPT, product.getDescription());
                values.put(PRODUCT_COLUMN_USE, product.getUse());
                values.put(PRODUCT_COLUMN_SIDEEFFECTS, product.getSideEffects());
                values.put(PRODUCT_COLUMN_OBJECT, product.getObject());

                db.insertWithOnConflict(PRODUCT_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
            Log.d(">>> SQLite", "Đã thêm danh sách sản phẩm");
        } catch (Exception e) {
            Log.e("!!! SQLite", "Không thể thêm danh sách sản phẩm: ", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Long> getAllProductIds() {
        List<Long> ids = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT " + PRODUCT_COLUMN_ID + " FROM " + PRODUCT_TABLE_NAME, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getLong(0));
                } while (cursor.moveToNext());
            }

            Log.d(">>> SQLite", "Danh sách id sản phẩm trong local: " + ids.toString());
        } catch (Exception e) {
            Log.e("!!! SQLite", "Lỗi:", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return ids;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PRODUCT_TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            Log.d(">>> SQLite", "getAllProducts: bắt đầu nạp sản phẩm local");
            do {
                Product product = new Product();
                product.setId(cursor.getLong(cursor.getColumnIndex(PRODUCT_COLUMN_ID)));
                product.setName(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_NAME)));
                product.setBrand(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_BRAND)));
                product.setPrice(cursor.getInt(cursor.getColumnIndex(PRODUCT_COLUMN_PRICE)));
                product.setStock(cursor.getInt(cursor.getColumnIndex(PRODUCT_COLUMN_STOCK)));
                product.setImagePath(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_IMAGE)));
                product.setOnDeal(cursor.getInt(cursor.getColumnIndex(PRODUCT_COLUMN_ONDEAL)) == 1);
                product.setDiscountPercent(cursor.getInt(cursor.getColumnIndex(PRODUCT_COLUMN_DISCOUNT)));
                product.setActive(cursor.getInt(cursor.getColumnIndex(PRODUCT_COLUMN_ISACTIVE)) == 1);
                product.setOriginal(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_ORIGINAL)));
                product.setCategory(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_CATEGORY)));
                product.setDosageForm(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_DOSAGEFORM)));
                product.setInclude(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_INCLUDE)));
                product.setIngredient(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_INGREDIENT)));
                product.setRating(cursor.getFloat(cursor.getColumnIndex(PRODUCT_COLUMN_RATING)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_DESCRIPT)));
                product.setUse(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_USE)));
                product.setSideEffects(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_SIDEEFFECTS)));
                product.setObject(cursor.getString(cursor.getColumnIndex(PRODUCT_COLUMN_OBJECT)));

                products.add(product);
                Log.d(">>> SQLite", "getAllProducts: nạp 1 sản phẩm");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return products;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_COLUMN_NAME, product.getName());
        values.put(PRODUCT_COLUMN_BRAND, product.getBrand());
        values.put(PRODUCT_COLUMN_PRICE, product.getPrice());
        values.put(PRODUCT_COLUMN_STOCK, product.getStock());
        values.put(PRODUCT_COLUMN_IMAGE, product.getImagePath());
        values.put(PRODUCT_COLUMN_ONDEAL, product.getOnDeal() ? 1 : 0);
        values.put(PRODUCT_COLUMN_DISCOUNT, product.getDiscountPercent());
        values.put(PRODUCT_COLUMN_ISACTIVE, product.getActive() ? 1 : 0);
        values.put(PRODUCT_COLUMN_ORIGINAL, product.getOriginal());
        values.put(PRODUCT_COLUMN_CATEGORY, product.getCategory());
        values.put(PRODUCT_COLUMN_DOSAGEFORM, product.getDosageForm());
        values.put(PRODUCT_COLUMN_INCLUDE, product.getInclude());
        values.put(PRODUCT_COLUMN_INGREDIENT, product.getIngredient());
        values.put(PRODUCT_COLUMN_RATING, product.getRating());
        values.put(PRODUCT_COLUMN_DESCRIPT, product.getDescription());
        values.put(PRODUCT_COLUMN_USE, product.getUse());
        values.put(PRODUCT_COLUMN_SIDEEFFECTS, product.getSideEffects());
        values.put(PRODUCT_COLUMN_OBJECT, product.getObject());

        int rowsAffected = db.update(PRODUCT_TABLE_NAME, values, PRODUCT_COLUMN_ID + "=?",
                new String[]{String.valueOf(product.getId())});
        db.close();
        return rowsAffected;
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRODUCT_TABLE_NAME, null, null);
        db.close();

        // Xóa ảnh trong cache dir
        File cacheDir = context.getCacheDir();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isImageFile(file)) file.delete();
            }
        }

    }

    private boolean isImageFile(File file) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        for (String extension : imageExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
