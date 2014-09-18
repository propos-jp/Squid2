package jp.co.and_ex.squid2.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ProviderTestCase2;

import android.net.Uri;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ObserveDataProviderTest extends ProviderTestCase2<ObserveDataProvider> {
   private ObserveDataProvider provider;

    public ObserveDataProviderTest(){
        super(ObserveDataProvider.class,ObserveDataContract.AUTHORITY);
    }

    public ObserveDataProviderTest(Class<ObserveDataProvider> providerClass, String providerAuthority) {
        super(providerClass,providerAuthority);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testQuery() throws Exception {
        Uri uri =provider.CONTENT_URI;

        Cursor cursor = provider.query(uri,null,null,null,null);
        try {
            assertNotNull(cursor);
            assertEquals(3, cursor.getCount());
        }finally {
            cursor.close();
        }
    }

    public void testInsert() throws Exception {
        Uri uri = provider.CONTENT_URI;

        provider = getProvider();

        ContentValues values = new ContentValues();
        values.put(ObserveDataContract.KEY_GLOBAL_ID, "id:xxx");
        values.put(ObserveDataContract.KEY_OBSERVE_DATE, "date:xxx");
        provider.insert(uri,values);

        Cursor cursor = provider.query(uri,null,null,null,null);
        try{
            assertNotNull(cursor);
            assertEquals(4,cursor.getCount());
        }finally {
            cursor.close();
        }


    }

    public void testDelete() throws Exception {
        Uri uri = provider.CONTENT_URI;
        provider = getProvider();

        Cursor cursor = provider.query(uri,null,null,null,null);

        try {
            assertNotNull(cursor);
            assertEquals(3,cursor.getCount());

        }finally {
            cursor.close();
        }
        provider.delete(uri, ObserveDataContract.KEY_GLOBAL_ID + " = ?",new String[]{"id:1", "id:3"});

        cursor = provider.query(uri,null,null,null,null);

        try {
            assertNotNull(cursor);
            assertEquals(1,cursor.getCount());
        }finally {
            cursor.close();
        }


    }


}