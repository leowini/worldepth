package com.example.leodw.worldepth.dagger;

import com.example.leodw.worldepth.WorldepthApplication;
import com.example.leodw.worldepth.ui.BaseActivity;
import com.example.leodw.worldepth.ui.MainActivity;

/**
 * Here are listed all the loations where injection is needed.
 * Created by becze on 9/17/2015.
 */
public interface DaggerComponentGraph {


    void inject(WorldepthApplication app);

    void inject(BaseActivity baseActivity);

    void inject(MainActivity baseActivity);
}
