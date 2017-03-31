package utilities;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * This Java calss aims to find the DA words in parallel data (EGY_words and MSA_words) which are not found in Tharwa CODA column.
 */
public class FindCorpusWordsNotFoundInTharwa {


    static HashMap<String, Integer> DIA_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> DIA_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<String, Integer> MSA_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> MSA_indexDic_shadow=new HashMap<Integer, String>();

    static int EGYDataMatched=0;
    static int MSADataMatched=0;


    public static void main(String[] args) throws IOException
    {
        /**
         * args[0]== Tharwa path
         * args[1]= alef-ya normalized
         * args[3]= diacratized
         * args[4]=
         */

        String TharwaPath= args[0];
        boolean AlefYaNormalized= Boolean.parseBoolean(args[1]);
        boolean diacratized= Boolean.parseBoolean(args[2]);
        String EGYDataPath= args[3];

        Vector<String> TharwaList= buildTharwaDAList(TharwaPath, AlefYaNormalized, diacratized);
        Vector<String> EGYDataList= buildDataWordList(EGYDataPath);
        compare(TharwaList, EGYDataList);

    }

    public static Vector<String> buildTharwaDAList(String TharwaPath, boolean AlefYaNormalized, boolean diacritized) throws IOException
    {
        System.out.print("Buliding Tharwa List.....\n");
        Vector<String> tharwaDAList= new Vector<String>();
        BufferedReader tharwaReader= new BufferedReader(new FileReader(TharwaPath));
        BufferedWriter tharwaWriter= new BufferedWriter(new FileWriter("tharwa.wordList"));

        String LineToRead= tharwaReader.readLine();
        String DIA_word = "";

        while ((LineToRead =tharwaReader.readLine()) != null)
        {
            String[] splitedLine = LineToRead.split("\t");
            DIA_word = splitedLine[2]; // CODA column
            DIA_word= DIA_word.trim();

            //Refine DA words based in demand
            if (AlefYaNormalized==true)
            {
                if (diacritized==false)
                {
                    DIA_word = getSafeBW(normalizeAlefYa(undiacratize(DIA_word)));
                }
                else {
                    DIA_word = getSafeBW(normalizeAlefYa(DIA_word));
                }
            }
            else
            {
                if (diacritized== false)
                {
                    DIA_word= getSafeBW(undiacratize(DIA_word));
                }
                else
                {
                    DIA_word= getSafeBW(DIA_word);
                }
            }
           /////////////////////////////////////
            /*
            // creating the ThatwaDAList using indexed dictionaries
            Integer tharwaDAIndex=0;
            Integer tharwaMSAIndex=0;
            Integer TharwaIndex=0;


            //Using CODA word as DA word
            if(!DIA_indexDic.containsKey(DIA_word))
                update_DIA_Index_Dic(DIA_word);
            if(!MSA_indexDic.containsKey(DIA_word))
                update_MSA_Index_Dic(DIA_word);

            tharwaDAIndex = get_DIA_Index(DIA_word);
            tharwaMSAIndex= get_MSA_Index(DIA_word);

            System.out.print("Tharwa Entry DA data index  "+ tharwaDAIndex+"\n");
            System.out.print("Tharwa Entry MSA_words data index  "+ tharwaMSAIndex+"\n");
            if (!tharwaDAIndex.equals(tharwaMSAIndex))
                System.out.print("Tharwa entry "+DIA_word+" DA_index and MSA_index differes!\n");
            */

            if (!tharwaDAList.contains(DIA_word)) {
                tharwaDAList.add(DIA_word);
                tharwaWriter.write(DIA_word+"\n");
            }
        }
        tharwaReader.close();
        tharwaWriter.flush();
        tharwaWriter.close();
        System.out.print("Tharwa List Building Finished...\n");
        System.out.print("Tharwa List Size: "+ tharwaDAList.size()+"\n");
        return tharwaDAList;
    }


    public static Vector<String> buildDataWordList(String EGYDataPath) throws IOException
    {
        System.out.print("Building EGY_words data list started.....\n");

        Vector<String> wordsList= new Vector<String>();
        BufferedReader dataReader= new BufferedReader(new FileReader(EGYDataPath));
        BufferedWriter dataWriter= new BufferedWriter(new FileWriter(EGYDataPath+".wordList"));

        String lineToRead="";
        while ((lineToRead= dataReader.readLine())!=null)
        {
            String[] splittedLine= lineToRead.split(" ");
            for (String word: splittedLine)
            {
                //if (!wordsList.contains(word))
               // {
                   if (!word.contains("+") && !word.contains("@@")) {
                       wordsList.add(word);
                       dataWriter.write(word + "\n");
                   }
               // }

            }
        }

        dataReader.close();
        dataWriter.flush();
        dataWriter.close();
        System.out.print("Building EGY_words data list finished.....\n");
        System.out.print("Number of EGY_words words: "+ wordsList.size()+"\n");
        return wordsList;

    }


    public static void compare(Vector<String> TharwaList, Vector<String> EGYDataList) throws IOException
    {
        BufferedWriter matchedWriter= new BufferedWriter(new FileWriter("matchedList"));
        BufferedWriter unmatchedWriter= new BufferedWriter(new FileWriter("unmatchedList"));

        System.out.print("Comparing tharwa and EGY_words list started...\n");
        int counter=0;
        for (String EGYWord: EGYDataList)
        {
            counter++;
            if (counter%10000==0)
                System.out.print("Number of EGY_words words compared  "+counter+"\n");

            if (TharwaList.contains(EGYWord))
                matchedWriter.write(EGYWord+"\n");
            else
                unmatchedWriter.write(EGYWord+"\n");
        }

        matchedWriter.flush();
        matchedWriter.close();
        unmatchedWriter.flush();
        unmatchedWriter.close();

        System.out.print("Comparing tharwa and EGY_words list finished...\n");

    }

///////////////////////////////////////////////////////////////////////////////////////////
    public static String getSafeBW(String BW)
    {
        String tempSafeBW="";
        tempSafeBW=BW;

        if (tempSafeBW.contains("|"))
            tempSafeBW= tempSafeBW.replace("|","M");
        if(tempSafeBW.contains("<"))
            tempSafeBW=tempSafeBW.replace("<","I");
        if(tempSafeBW.contains(">"))
            tempSafeBW=tempSafeBW.replace(">","O");
        if(tempSafeBW.contains("&"))
            tempSafeBW=tempSafeBW.replace("&","W");
        if(tempSafeBW.contains("}"))
            tempSafeBW=tempSafeBW.replace("}","Q");
        if(tempSafeBW.contains("*"))
            tempSafeBW=tempSafeBW.replace("*","V");
        if(tempSafeBW.contains("$"))
            tempSafeBW=tempSafeBW.replace("$","c");
        if(tempSafeBW.contains("'"))
            tempSafeBW=tempSafeBW.replace("'","C");
        if(tempSafeBW.contains("{"))
            tempSafeBW=tempSafeBW.replace("{","L");
        if(tempSafeBW.contains("`"))
            tempSafeBW=tempSafeBW.replace("`","e");

        return tempSafeBW;
    }


    public static String normalizeAlefYa (String originalWord)
    {
        return originalWord.replace("|","A").replace("<","A").replace(">","A").replace("{","A").replace("`","A").replace("Y","y");
    }

    public static String undiacratize (String originalWord)
    {
        return originalWord.replaceAll("[FNKauio~]", "");

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

}
