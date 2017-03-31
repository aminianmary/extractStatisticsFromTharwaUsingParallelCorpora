package utilities;

import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Maryam Aminian
 * Date: 11/2/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class TharwaRow implements Comparable {
    HashSet<Integer> EGY_words;
    HashSet<Integer> MSA_words;
    HashSet<Integer> En_equivs;
    Integer MSA_POS_LDC;
    Integer EGY_POS_LDC;

    public TharwaRow(HashSet<Integer> EGY_words, HashSet<Integer> MSA_words, HashSet<Integer> En_equivs, Integer EGY_POS_LDC, Integer MSA_POS_LDC){
        this.EGY_words = EGY_words;
        this.MSA_words =MSA_words;
        this.En_equivs =En_equivs;
        this.EGY_POS_LDC= EGY_POS_LDC;
        this.MSA_POS_LDC= MSA_POS_LDC;
    }

    public HashSet<Integer> getEGY_words() {return EGY_words;}

    public HashSet<Integer> getMSA_words(){
        return MSA_words;
    }

    public HashSet<Integer> getEn_equivs(){
        return En_equivs;
    }

    public Integer getMSA_POS_LDC() {return MSA_POS_LDC;}

    public Integer getEGY_POS_LDC() {return EGY_POS_LDC;}

    @Override
    public int compareTo(Object o) {
        return hashCode()-o.hashCode();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean equals(Object o){
        if(o instanceof TharwaRow){
            TharwaRow tuple=(TharwaRow)o;
            return tuple.getEGY_words().equals(EGY_words) &&  tuple.getMSA_words().equals(MSA_words) && tuple.getEn_equivs().equals(En_equivs) &&
                    tuple.getMSA_POS_LDC().equals(MSA_POS_LDC) && tuple.getEGY_POS_LDC().equals(EGY_POS_LDC);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return getEGY_words().hashCode() + getMSA_words().hashCode()+ getEn_equivs().hashCode()+ getEGY_POS_LDC().hashCode()+ getMSA_POS_LDC().hashCode();
    }
}
