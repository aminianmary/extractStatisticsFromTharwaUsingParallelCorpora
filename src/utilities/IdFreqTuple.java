package utilities;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Maryam Aminian
 * Date: 1/24/14
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdFreqTuple {
    Vector<Integer> ids;
    Integer freq;

    public IdFreqTuple (Vector ids, Integer freq)
    {
        this.ids= ids;
        this.freq= freq;
    }
    public Integer getFreq()
    {
        return freq;
    }
    public Vector<Integer> getIds ()
    {
        return ids;
    }
    public void setFreq (Integer freq)
    {
        this.freq = freq;
    }
    public void setId (Integer id)
    {
        this.ids.add(id);
    }


}