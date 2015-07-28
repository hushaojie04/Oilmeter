package sj.android.oilmeter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/27.
 */
public class Oilmeter {
    List<DialInfo> mDialInfoList = new ArrayList<DialInfo>();
    public void addDial(DialInfo info)
    {
        mDialInfoList.add(info);
    }
}
