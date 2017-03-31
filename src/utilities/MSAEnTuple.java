package utilities;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Maryam Aminian
 * Date: 11/2/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class MSAEnTuple implements Comparable {
    Integer MSA;
    Integer En;
    Integer MSA_POS_LDC;
    Integer EGY_POS_LDC;
    ArrayList<Integer> ids;

    public MSAEnTuple(Integer MSA, Integer En, ArrayList ids){
        this.MSA =MSA;
        this.En =En;
        this.ids= ids;
    }
    public MSAEnTuple(Integer MSA, Integer En){
        this.MSA =MSA;
        this.En =En;
        this.ids= null;
    }

    public MSAEnTuple(Integer MSA, Integer En, Integer MSA_POS_LDC, Integer EGY_POS_LDC){
        this.MSA =MSA;
        this.En =En;
        this.MSA_POS_LDC= MSA_POS_LDC;
        this.EGY_POS_LDC= EGY_POS_LDC;
        this.ids= null;
    }

    public MSAEnTuple(Integer MSA, Integer En,ArrayList ids, Integer MSA_POS_LDC, Integer EGY_POS_LDC){
        this.MSA =MSA;
        this.En =En;
        this.MSA_POS_LDC= MSA_POS_LDC;
        this.EGY_POS_LDC= EGY_POS_LDC;
        this.ids= ids;
    }

    public Integer getMSA(){
        return MSA;
    }

    public Integer getEn(){
        return En;
    }

    public Integer getMSA_POS_LDC() {return MSA_POS_LDC;}

    public Integer getEGY_POS_LDC() {return EGY_POS_LDC;}

    public ArrayList<Integer> getIds(){

    return ids;
   }

   public void setIds (Integer newId)
   {
       ids.add(newId);
   }
    @Override
    public int compareTo(Object o) {
        return hashCode()-o.hashCode();  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean equals(Object o){
        if(o instanceof MSAEnTuple){
            MSAEnTuple tuple=(MSAEnTuple)o;
            return tuple.getMSA().equals(MSA) && tuple.getEn().equals(En) &&
                    tuple.getMSA_POS_LDC().equals(MSA_POS_LDC) && tuple.getEGY_POS_LDC().equals(EGY_POS_LDC) && tuple.getIds().equals(ids);
        }
       return false;
    }


    @Override
    public int hashCode(){
        return getMSA().hashCode()+ getEn().hashCode()+ getIds().hashCode();
    }
}
