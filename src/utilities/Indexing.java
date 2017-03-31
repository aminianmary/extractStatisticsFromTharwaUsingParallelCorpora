package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by monadiab on 1/8/16.
 */
public class Indexing {

    //Index dictionaries
    public static HashMap<String, Integer> En_indexDic= new HashMap<String, Integer>();
    public static HashMap<Integer,String> En_indexDic_shadow=new HashMap<Integer, String>();
    public static HashMap<String, Integer> MSA_indexDic= new HashMap<String, Integer>();
    public static HashMap<Integer,String> MSA_indexDic_shadow=new HashMap<Integer, String>();
    public static HashMap<String, Integer> DIA_indexDic= new HashMap<String, Integer>();
    public static HashMap<Integer,String> DIA_indexDic_shadow=new HashMap<Integer, String>();
    public static HashMap<String, Integer> POS_indexDic= new HashMap<String, Integer>();
    public static HashMap<Integer, String> POS_indexDic_shadow= new HashMap<Integer, String>();
    public static HashMap<String, IdFreqTuple> CODA_freq= new HashMap<String, IdFreqTuple>();

    public static void update_En_Index_Dic(String newWord)throws IOException
    {
        if (! En_indexDic.containsKey(newWord))
        {
            int index= En_indexDic.size();
            En_indexDic.put(newWord, index);
            En_indexDic_shadow.put(index,newWord);
        }
    }


    public static Integer get_En_Index(String word)
    {
        return En_indexDic.get(word);
    }



    public static String get_En_Word(Integer index){
        return En_indexDic_shadow.get(index);
    }


    public static void update_MSA_Index_Dic(String newWord) throws IOException
    {
        if (! MSA_indexDic.containsKey(newWord))
        {
            int index= MSA_indexDic.size();
            MSA_indexDic.put(newWord, index);
            MSA_indexDic_shadow.put(index,newWord);
        }

    }


    public static Integer get_MSA_Index(String word)
    {
        return MSA_indexDic.get(word);
    }


    public static String get_MSA_Word(Integer index){
        return MSA_indexDic_shadow.get(index);
    }


    public static void update_DIA_Index_Dic(String newWord) throws IOException
    {
        if (! DIA_indexDic.containsKey(newWord))
        {
            int index= DIA_indexDic.size();
            DIA_indexDic.put(newWord, index);
            DIA_indexDic_shadow.put(index,newWord);
        }
    }


    public static Integer get_DIA_Index(String word)
    {
        return DIA_indexDic.get(word);
    }



    public static String get_DIA_Word(Integer index){
        return DIA_indexDic_shadow.get(index);
    }


    public static void update_POS_Index_Dic(String newPOS) throws IOException
    {
        //System.out.println("POS index Dic size "+ POS_indexDic.size());
        if (!newPOS.equals("") && !POS_indexDic.containsKey(newPOS))
        {
            int index= POS_indexDic.size();
            POS_indexDic.put(newPOS, index);
            POS_indexDic_shadow.put(index,newPOS);
        }
        //System.out.println("POS index Dic size "+ POS_indexDic.size());

    }

    public static Integer get_POS_Index(String POS)
    {
        //System.out.println("Getting POS index for "+ POS);
        //System.out.println("POS index Dic size "+ POS_indexDic.size());
        int index=-1;
        if (POS_indexDic.containsKey(POS))
            index= POS_indexDic.get(POS);
        return index;
    }

    public static String get_POS(Integer index){
        return POS_indexDic_shadow.get(index);
    }

    // this method will be called during constructing ExtractStats.Tharwa dictionary

    public static void add_CODA_freq_dic (String CODA, Integer id)
    {
        if(! CODA_freq.containsKey(CODA))
        {
            Vector<Integer> tempVector = new Vector<Integer>();
            tempVector. add(id);
            int freq= 0;
            IdFreqTuple idFreqTuple= new IdFreqTuple(tempVector, freq);
            CODA_freq.put(CODA, idFreqTuple);
        }
        else
        {
            CODA_freq.get(CODA).setId(id);
        }
    }

    public static void write_index_dic(HashMap<String, Integer> index_dic,String indexDicName)throws IOException{

        System.out.println("Write "+indexDicName+" started");
        BufferedWriter index_Dic_writer= new BufferedWriter(new FileWriter(indexDicName));
        for (String word :index_dic.keySet())
            index_Dic_writer.write(word+"\t"+index_dic.get(word)+"\n");
        System.out.println("Write "+indexDicName+" finished");
        System.out.println(indexDicName+" size "+ index_dic.size()+"\n*******************************");
        index_Dic_writer.flush();
        index_Dic_writer.close();
    }
}
