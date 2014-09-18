package jp.co.and_ex.squid2.observe;

/**
 * Created by obata on 2014/09/17.
 */
public interface SquidReader {
    public void tokenGet(String str);

    public void tokenSet(String str);

    public void tokenOK(String str);

    public void token200(String str);

    public void tokenData(String str);

    public void tokenError(String str);

}
