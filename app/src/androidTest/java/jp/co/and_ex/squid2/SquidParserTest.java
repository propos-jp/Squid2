package jp.co.and_ex.squid2;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.and_ex.squid2.observe.SquidParser;
import jp.co.and_ex.squid2.observe.SquidReader;


/**
 * Created by obata on 2014/09/17.
 */
public class SquidParserTest extends AndroidTestCase implements SquidReader {

    int getCalled = 0;
    int setCalled = 0;
    int okReturned = 0;
    int line200Returned = 0;
    int dataReturned = 0;
    int errorReturned = 0;
    StringBuffer dataBuffer = null;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        getCalled = 0;
        setCalled = 0;
        okReturned = 0;
        line200Returned = 0;
        dataReturned = 0;
        dataBuffer = new StringBuffer();

    }

    public void testEX1() {
        Pattern p = SquidParser.getObserve_pattern();
        Matcher m = p.matcher("0000,1111,2222\r\n");
        assertTrue(m.find());
        m = p.matcher("000,111,222\r");
        assertFalse(m.find());


    }

    public void testEX2() {
        SquidParser parser = new SquidParser(this);

        parser.putData("@SET\r\n");
        assertEquals(0, getCalled);
        assertEquals(1, setCalled);
        assertEquals(0, okReturned);
        assertEquals(0, line200Returned);
        assertEquals(0,dataReturned);
        assertEquals(dataBuffer.length(), 0);
    }


    public void testEX3() {
        SquidParser parser = new SquidParser(this);

        parser.putData("@SET\r\n@GET\r\nOK\r\n200 LINE(S)\r\n");
        assertEquals(1, getCalled);
        assertEquals(1, setCalled);
        assertEquals(1, okReturned);
        assertEquals(1, line200Returned);
        assertEquals(dataBuffer.length(), 0);
        assertEquals(0,dataReturned);
        assertEquals(0, errorReturned);
    }

    public void testEX4() {
        SquidParser parser = new SquidParser(this);

        String data = "0000,1111,2222\r\n2222,3333,4444\r\n";

        parser.putData("@GET\r\n" + data + "200 LINE(S)\r\n");
        assertEquals(1, getCalled);
        assertEquals(0, setCalled);
        assertEquals(0, okReturned);
        assertEquals(1, line200Returned);
        assertEquals(data.length(), dataBuffer.length());
        assertEquals(data, dataBuffer.toString());
        assertEquals(2, dataReturned);
        assertEquals(0, errorReturned);
    }

    public void testEX5() {
        SquidParser parser = new SquidParser(this);

        String data1 = "0000,1111,2222\r\n2222,33";
        String data2 = "33,4444\r\n4560,7890,1230\r\n";
        String data3 = "9909,8907,5604\r\n";
        String data = data1 + data2 + data3;

        parser.putData("@");
        parser.putData("GET\r\n");
        parser.putData(data1);
        parser.putData(data2);
        parser.putData(data3);
        parser.putData("200 LINE(S)\r\n");

        assertEquals(1, getCalled);
        assertEquals(0, setCalled);
        assertEquals(0, okReturned);
        assertEquals(1, line200Returned);

        assertEquals(data, dataBuffer.toString());
        assertEquals(data.length(), dataBuffer.length());
        assertEquals(4,dataReturned);
        assertEquals(0, errorReturned);
    }

    public void testCal(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);        //(2)現在の年を取得
        int month = cal.get(Calendar.MONTH) + 1;  //(3)現在の月を取得
        int day = cal.get(Calendar.DATE);         //(4)現在の日を取得
        int hour = cal.get(Calendar.HOUR_OF_DAY); //(5)現在の時を取得
        int minute = cal.get(Calendar.MINUTE);    //(6)現在の分を取得
        int second = cal.get(Calendar.SECOND);    //(7)現在の秒を取得
        String str = String.format("%04d/%02d%02d,%02d:%02d:%02d",year,month,day,hour,minute,second);
        Log.i("test",str);
    }

    @Override
    public void tokenGet(String str) {
        getCalled++;
    }

    @Override
    public void tokenSet(String str) {
        setCalled++;
    }

    @Override
    public void tokenOK(String str) {
        okReturned++;
    }

    @Override
    public void token200(String str) {
        line200Returned++;
    }

    @Override
    public void tokenData(String str) {
        dataBuffer.append(str);
        dataReturned++;
    }

    @Override
    public void tokenError(String str) {
        errorReturned++;
    }

}


