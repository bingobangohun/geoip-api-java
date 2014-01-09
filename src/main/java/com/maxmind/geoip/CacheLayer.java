/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maxmind.geoip;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author bingobango
 */
public class CacheLayer
{
    private final LookupService service;
    
    /**
     * Information about the database.
     */    
    DatabaseInfo databaseInfo = null;
    
    int databaseSegments[];
    int recordLength;

    byte dbbuffer[];
    byte index_cache[];

    int last_netmask;

    final HashMap hashmapcountryCodetoindex = new HashMap(512);
    final HashMap hashmapcountryNametoindex = new HashMap(512);
    
    byte databaseType = DatabaseInfo.COUNTRY_EDITION;
    
    long mtime;    
    
    final AtomicLong checkTime = new AtomicLong(System.currentTimeMillis());
    final AtomicBoolean initRun = new AtomicBoolean(false);
    static final long CHECK_INTERVAL_INSEC = 10;
    
    volatile boolean isCheck = true;
    
    public CacheLayer(LookupService service)
    {
        this.service = service;
        System.out.println("New LookupService" + service);
    }
    
    public void checkHandler()
    {
        if ( isCheck )
        {
            long actTime = System.currentTimeMillis();
            if ( checkTime.get() + CHECK_INTERVAL_INSEC * 1000 < actTime )
            {
                checkTime.set(actTime);
                if ( initRun.compareAndSet(false, true))
                {
                    System.out.println("Check");
                    this.service._check_mtime();
                    initRun.compareAndSet(true, false);
                }
            }
        }
    }
}
