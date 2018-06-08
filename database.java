public class Database {

private static class DBHelper extends SQLiteOpenHelper {

    /**
     * Database name
     */
    private static final String DB_NAME = "db_name";

    /**
     * Table Names
     */
    public static final String TABLE_CART = "DB_CART";


    /**
     *  Cart Table Columns
     */
    public static final String CART_ID_PK = "_id";// Primary key

    public static final String CART_DISH_NAME = "dish_name";
    public static final String CART_DISH_ID = "menu_item_id";
    public static final String CART_DISH_QTY = "dish_qty";
    public static final String CART_DISH_PRICE = "dish_price";

    /**
     * String to create reservation tabs table
     */
    private final String CREATE_TABLE_CART = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CART + " ( "
            + CART_ID_PK + " INTEGER PRIMARY KEY, "
            + CART_DISH_NAME + " TEXT , "
            + CART_DISH_ID + " TEXT , "
            + CART_DISH_QTY + " TEXT , "
            + CART_DISH_PRICE + " TEXT);";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CART);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CART);
        onCreate(db);
    }

}


     /**
      * CART handler
      */
      public static class Cart {


    /**
     * Check if Cart is available or not
     *
     * @param context
     * @return
     */
    public static boolean isCartAvailable(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = false;

        try {
            String query = "SELECT * FROM " + DBHelper.TABLE_CART;
            Cursor cursor = db.rawQuery(query, null);
            exists = (cursor.getCount() > 0);
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            db.close();
        }

        return exists;
    }


    /**
     * Insert values in cart table
     *
     * @param context
     * @param dishName
     * @param dishPrice
     * @param dishQty
     * @return
     */
    public static boolean insertItem(Context context, String itemId, String dishName, String dishPrice, String dishQty) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CART_DISH_ID, "" + itemId);
        values.put(DBHelper.CART_DISH_NAME, "" + dishName);
        values.put(DBHelper.CART_DISH_PRICE, "" + dishPrice);
        values.put(DBHelper.CART_DISH_QTY, "" + dishQty);

        try {
            db.insert(DBHelper.TABLE_CART, null, values);
            db.close();
            return true;
        } catch (SQLiteException e) {
            db.close();
            return false;
        }
    }

    /**
     * Check for specific record by name
     *
     * @param context
     * @param dishName
     * @return
     */
    public static boolean isItemAvailable(Context context, String dishName) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = false;

        String query = "SELECT * FROM " + DBHelper.TABLE_CART + " WHERE "
                + DBHelper.CART_DISH_NAME + " = '" + String.valueOf(dishName) + "'";


        try {
            Cursor cursor = db.rawQuery(query, null);

            exists = (cursor.getCount() > 0);
            cursor.close();

        } catch (SQLiteException e) {

            e.printStackTrace();
            db.close();

        }

        return exists;
    }

    /**
     * Update cart item by item name
     *
     * @param context
     * @param dishName
     * @param dishPrice
     * @param dishQty
     * @return
     */
    public static boolean updateItem(Context context, String itemId, String dishName, String dishPrice, String dishQty) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CART_DISH_ID, itemId);
        values.put(DBHelper.CART_DISH_NAME, dishName);
        values.put(DBHelper.CART_DISH_PRICE, dishPrice);
        values.put(DBHelper.CART_DISH_QTY, dishQty);

        try {

            String[] args = new String[]{dishName};
            db.update(DBHelper.TABLE_CART, values, DBHelper.CART_DISH_NAME + "=?", args);

            db.close();


            return true;
        } catch (SQLiteException e) {
            db.close();

            return false;
        }
    }

    /**
     * Get cart list
     *
     * @param context
     * @return
     */
    public static ArrayList<CartModel> getCartList(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<CartModel> cartList = new ArrayList<>();

        try {
            String query = "SELECT * FROM " + DBHelper.TABLE_CART + ";";

            Cursor cursor = db.rawQuery(query, null);


            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                cartList.add(new CartModel(
                        cursor.getString(cursor.getColumnIndex(DBHelper.CART_DISH_ID)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.CART_DISH_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.CART_DISH_QTY)),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.CART_DISH_PRICE)))
                ));
            }

            db.close();

        } catch (SQLiteException e) {
            db.close();
        }
        return cartList;
    }

   /**
     * Get total amount of cart items
     *
     * @param context
     * @return
     */
    public static String getTotalAmount(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        double totalAmount = 0.0;

        try {
            String query = "SELECT * FROM " + DBHelper.TABLE_CART + ";";

            Cursor cursor = db.rawQuery(query, null);


            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                totalAmount = totalAmount + Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBHelper.CART_DISH_PRICE))) *
                        Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBHelper.CART_DISH_QTY)));
            }

            db.close();


        } catch (SQLiteException e) {
            db.close();
        }


        if (totalAmount == 0.0)
            return "";
        else
            return "" + totalAmount;
    }


    /**
     * Get item quantity
     *
     * @param context
     * @param dishName
     * @return
     */
    public static String getItemQty(Context context, String dishName) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        String query = "SELECT * FROM " + DBHelper.TABLE_CART + " WHERE " + DBHelper.CART_DISH_NAME + " = '" + dishName + "';";
        String quantity = "0";

        try {
            cursor = db.rawQuery(query, null);

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                quantity = cursor.getString(cursor
                        .getColumnIndex(DBHelper.CART_DISH_QTY));

                return quantity;
            }


        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return quantity;
    }


    /**
     * Delete cart item by name
     *
     * @param context
     * @param dishName
     */
    public static void deleteCartItem(Context context, String dishName) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            String[] args = new String[]{dishName};
            db.delete(DBHelper.TABLE_CART, DBHelper.CART_DISH_NAME + "=?", args);

            db.close();
        } catch (SQLiteException e) {
            db.close();
            e.printStackTrace();
        }

    }


}//End of cart class

/**
 * Delete database table
 *
 * @param context
 */
public static void deleteCart(Context context) {
    DBHelper dbHelper = new DBHelper(context);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    try {

        db.execSQL("DELETE FROM " + DBHelper.TABLE_CART);

    } catch (SQLiteException e) {
        e.printStackTrace();
    }

}
