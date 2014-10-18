package com.jamesg.windforecast;

import java.util.List;

/**
 * Created by James on 17/10/2014.
 */
public interface Injectable {
    public List<Object> getModules();

    public void inject(Object o);
}
