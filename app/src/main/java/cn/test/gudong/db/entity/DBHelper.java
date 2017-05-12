package cn.test.gudong.db.entity;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by jiahaodong on 2017/5/5-14:00.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class DBHelper {
    static DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("gudong.db")
            // 不设置dbDir时, 默认存储在app的私有目录.
            //.setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
            .setDbVersion(1)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    // 开启WAL, 对写入加速提升巨大
                    db.getDatabase().enableWriteAheadLogging();
                }
            })
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    // TODO: ...
                    // db.addColumn(...);
                    // db.dropTable(...);
                    // ...
                    // or
                    // db.dropDb();
                }
            });

    public static List<Track> seleteAllTrack() throws DbException {
        DbManager db= x.getDb(daoConfig);
        List<Track> tracks=db.selector(Track.class).findAll();
        return  tracks;
    }
    public static void insertTrack(Track track) throws DbException {
        DbManager db= x.getDb(daoConfig);
        db.save(track);
    }

}
