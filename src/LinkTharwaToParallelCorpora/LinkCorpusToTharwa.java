package LinkTharwaToParallelCorpora;

/*
This class is designed to link parallel data (Ar-En) to the entries in Tharwa. Each EGY word is aligned to the sentences
containing that EGY word in a particular sense. Sense matching is done by matching En equivalents.

Maryam Aminian
 */


import java.io.*;
import java.util.HashMap;
import java.util.Vector;

public class LinkCorpusToTharwa {

    static HashMap<String, Integer> En_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> En_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<String, Integer> DIA_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> DIA_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<Integer, HashMap<Integer, Vector<Vector<Integer>>>> tharwaDic=new HashMap<Integer, HashMap<Integer, Vector<Vector<Integer>>>>();

    static int tharwaSize=0;
    static int DADataMatchCounter=0;
    static int MSADataMatchCounter=0;
    static int unfoundCounter=0;

    public static void main(String[] args) throws IOException{

        /*
        args[0] = DIA_En_Alignment_Path
        args[1] = MSA_En_Alignment_Path
        args[2] = tharwaPath
        args[3] = DIA_En_EngDataPath
        args[4] = DIA_En_ArDataPath
        args[5] = MSA_En_EngDataPath
        args[6] = MSA_En_ArDataPath
        args[7] = Alef-Ya normalized
        args[8] = lemmatized
        args[9] = diacritized
         */

        if (args.length < 10)
            System.out.println("MISSED ARGUMENT");

        String DIA_En_Alignment_Path = args [0];
        String MSA_En_Alignment_Path = args [1];
        String tharwaPath= args[2];

        ////////////////

        String DIA_En_EngDataPath =args[3];
        String DIA_En_ArDataPath =args[4];
        String MSA_En_EngDataPath =args[5];
        String MSA_En_ArDataPath =args[6];

        Boolean AlefYaNormalized= Boolean.parseBoolean(args[7]);
        String lemmatized="tok";
        String diacritized="undiac";
        if (args[8].equals("true"))
            lemmatized= "lem";
        if (args[9].equals("true"))
            diacritized="diac";
        String prefix= lemmatized+"_"+diacritized;

        //Creates TharwaDict from Tharwa
        boolean diac= Boolean.parseBoolean(args[9]);
        tharwaDic= ConvertTharwaToDic(tharwaPath, AlefYaNormalized, diac);

        // Creates Alignmenent data dictionary for MSA-En and Egy-En separately.
        HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>>  DA_AlignmentdDics =
                buildDA_EnAlignDic(DIA_En_ArDataPath, DIA_En_EngDataPath, DIA_En_Alignment_Path);

        HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>>  MSA_AlignmentdDics =
                buildDA_EnAlignDic(MSA_En_ArDataPath, MSA_En_EngDataPath, MSA_En_Alignment_Path);


        compare(tharwaDic, DA_AlignmentdDics, MSA_AlignmentdDics);
        writeTharwa(tharwaDic);

        System.out.println("# of Tharwa generated triples   "+ tharwaSize);
        System.out.println("# of Tharwa DA entries  "+ tharwaDic.size());
        System.out.println("DA Matched Entries  "+ DADataMatchCounter);
        System.out.println("MSA Matched Entries  "+ MSADataMatchCounter);
        System.out.println("Unfound Entries  "+ unfoundCounter);
    }

    /////////////////////////////////////////////////////////////
    ////////////////////////// MAIN FUNCTIONS ///////////////////
    /////////////////////////////////////////////////////////////

    public static HashMap<Integer, HashMap<Integer, Vector<Vector<Integer>>>> ConvertTharwaToDic(String tharwaPath, boolean AlefYaNormalized, boolean diacritized)
            throws IOException
    {
        System.out.println("***************************");
        System.out.println("Convert Tharwa To Dic Started...\n");

        BufferedWriter tharwaWriter=new BufferedWriter(new FileWriter("tharwa"));
        BufferedReader tharwaReader = new BufferedReader(new FileReader(tharwaPath));

        String DIA_word = "";
        String Eng_gloss="";

        Integer id= 0;

        String LineToRead= tharwaReader.readLine();
        while ((LineToRead =tharwaReader.readLine()) != null)
        {
            String[] splitedLine = LineToRead.split("\t");

            id= Integer.parseInt(splitedLine[0]);
            DIA_word = splitedLine[2]; // CODA column
            Eng_gloss= splitedLine[8]; // English-Equivalent column

            //Listing all En equivalents
            String [] temp_ENG_glosses= Eng_gloss.replace("##",";;").split(";;");
            Vector<String> ENG_glosses= new Vector<String>();
            for (String En_token:temp_ENG_glosses)
            {
                if (En_token.contains("/"))
                {
                    if (!En_token.contains(" "))
                    {
                        if (!ENG_glosses.contains(En_token.split("/")[0]))
                            ENG_glosses.add(En_token.split("/")[0]);
                        if (!ENG_glosses.contains(En_token.split("/")[1]))
                            ENG_glosses.add(En_token.split("/")[1]);
                    }
                    else
                    {
                        String[] spaceSeparatedTokens= En_token.split(" ");
                        String[] slashSeparatedToken_1= spaceSeparatedTokens[0].split("/");
                        String[] slashSeparatedToken_2= spaceSeparatedTokens[1].split("/");

                        for (int i=0;i< slashSeparatedToken_1.length;i++)
                        {
                            for (int j=0;j< slashSeparatedToken_2.length;j++)
                            {
                                if (!ENG_glosses.contains(slashSeparatedToken_1[i]+" "+slashSeparatedToken_2[j]))
                                    ENG_glosses.add(slashSeparatedToken_1[i]+" "+slashSeparatedToken_2[j]);
                            }
                        }
                    }
                }
                else
                {
                    if (!ENG_glosses.contains(En_token))
                        ENG_glosses.add(En_token.trim());

                }
            }


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

            //Creating Tharwa Dictionary

            for (int i=0;i<ENG_glosses.size();i++)
            {
                if(!ENG_glosses.get(i).equals("UNK") && ENG_glosses.get(i).length()>0)
                {

                    // creating the thatwaDic using indexed dictionaries
                    Integer DIA_index=0;
                    Integer En_index= 0;
                    //Using CODA word as DA word
                    DIA_word= DIA_word.trim();
                    if(!DIA_indexDic.containsKey(DIA_word))
                        update_DIA_Index_Dic(DIA_word);
                    if (!En_indexDic.containsKey(ENG_glosses.get(i)))
                        update_En_Index_Dic(ENG_glosses.get(i));

                    DIA_index = get_DIA_Index(DIA_word);
                    En_index = get_En_Index(ENG_glosses.get(i));

                    tharwaWriter.write(get_En_Word(En_index)+'\t'+ get_DIA_Word(DIA_index)+'\n');

                    if (!tharwaDic.containsKey(DIA_index))
                    {
                        HashMap<Integer, Vector<Vector<Integer>>> tempHashMap= new HashMap<Integer, Vector<Vector<Integer>>>();
                        Vector<Vector<Integer>> tempVector= new Vector<Vector<Integer>>();
                        tempHashMap.put(En_index, tempVector);
                        tharwaDic.put(DIA_index, tempHashMap);
                        tharwaSize++;
                    }
                    else if (tharwaDic.containsKey(DIA_index))
                    {
                        Boolean isTheSame=false;
                        for (Integer tharwa_En_index: tharwaDic.get(DIA_index).keySet())
                        {
                            if (tharwa_En_index.equals(En_index))
                            {
                                    isTheSame=true;
                                    break;
                            }
                        }

                        if (isTheSame.equals(false))
                        {
                            Vector<Vector<Integer>> tempVector= new Vector<Vector<Integer>>();
                            tharwaDic.get(DIA_index).put(En_index, tempVector);
                            tharwaSize++;
                        }
                    }

                }
            }


            if(id %10000==0)
                System.out.println(id);
        }
        tharwaWriter.flush();
        tharwaWriter.close();
        tharwaReader.close();

        System.out.println("ConvertTharwaToDic_EGYKey finished");
        System.out.println("Numbe of Tharwa Entries "+ tharwaDic.size());
        System.out.println("***************************");
        return tharwaDic;
    }


    public static HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>> buildDA_EnAlignDic(String Ar_corpus, String En_corpus,
                                              String Allign_file) throws IOException
    {

        System.out.println("Building DIA-En Alignment Dics Started...");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));


        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>>  Ar_En_Align_Dic = new  HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>>();

        int sentenceID = -1;
        int totalNumberTokens=0;
        int totalNumberEn_ArTuples=0;
        int totalNumberAr_EnTuples=0;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();


            sentenceID++;
            if(sentenceID %10000==0)
                System.out.println(sentenceID);

            String[] Ar_words= Ar_line_to_read.split(" ");
            String[] En_words= En_line_to_read.split(" ");
            String[] Allign_words= Allign_line_to_read.split(" ");
            totalNumberTokens += Ar_words.length;


            for (int i=0;i<Allign_words.length;i++)
            {
                // finding indexes from alignment data
                Integer ArabicAlignmentIndex=Integer.parseInt ( Allign_words[i].split("-")[0]);
                Integer EngAlignmentIndex= Integer.parseInt ( Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma=removeInsideWordPlus(Ar_words [ArabicAlignmentIndex]);
                String EngLemma=En_words[EngAlignmentIndex];


                // finging indexes in indexed dictionaries for DIA data
                Integer DIA_index=0;
                Integer En_index= 0;


                if(!DIA_indexDic.containsKey(ArabicLemma))
                    update_DIA_Index_Dic(ArabicLemma);
                if (! En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);

                DIA_index = get_DIA_Index(ArabicLemma);
                En_index = get_En_Index(EngLemma);


                //Update Ar_En_Align_Dic
                if (!Ar_En_Align_Dic.containsKey(DIA_index))
                {
                    Vector<Integer> sentenceVector=new Vector<Integer>();
                    sentenceVector.add(sentenceID);
                    sentenceVector.add(ArabicAlignmentIndex);
                    sentenceVector.add(EngAlignmentIndex);

                    Vector<Vector<Integer>> sentencesVector= new Vector<Vector<Integer>>();
                    sentencesVector.add(sentenceVector);

                    HashMap<Integer, Vector<Vector<Integer>>> EnGlosses= new HashMap<Integer, Vector<Vector<Integer>>>();
                    EnGlosses.put(En_index, sentencesVector);

                    Ar_En_Align_Dic.put(DIA_index, EnGlosses);
                    totalNumberAr_EnTuples += 1;
                }
                else
                {
                    if(!Ar_En_Align_Dic.get(DIA_index).keySet().contains(En_index))
                    {
                        Vector<Integer> sentenceVector=new Vector<Integer>();
                        sentenceVector.add(sentenceID);
                        sentenceVector.add(ArabicAlignmentIndex);
                        sentenceVector.add(EngAlignmentIndex);

                        Vector<Vector<Integer>> sentencesVector= new Vector<Vector<Integer>>();
                        sentencesVector.add(sentenceVector);

                        Ar_En_Align_Dic.get(DIA_index).put(En_index, sentencesVector);
                        totalNumberAr_EnTuples +=1;

                    }
                    else
                    {
                        Vector<Integer> sentenceVector=new Vector<Integer>();
                        sentenceVector.add(sentenceID);
                        sentenceVector.add(ArabicAlignmentIndex);
                        sentenceVector.add(EngAlignmentIndex);

                        Ar_En_Align_Dic.get(DIA_index).get(En_index).add(sentenceVector);
                        totalNumberAr_EnTuples +=1;
                    }
                }
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        System.out.println("Building DA-En Alignment Dics Finished!");
        System.out.println("En_Index_dic size " + En_indexDic.size());
        System.out.println("DA_Index_Dic_dic size " + DIA_indexDic.size()+"\n----------------\n");

        System.out.println("Total Number of Tokens in the Arabic side  " + totalNumberTokens);
        System.out.println("Total Number of Tuples in the En-Ar Alignment Dic  "+totalNumberEn_ArTuples);
        System.out.println("Total Number of Tuples in the Ar-En Alignment Dic  "+totalNumberAr_EnTuples+"\n" +
                "----------------\n");

        System.out.println("Total Number of Entries in En-DA Alignment Dic " + Ar_En_Align_Dic.size());

        System.out.println("***************************\n");

        return Ar_En_Align_Dic;

    }

    public static void compare (HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>> tharwaDic,
                                HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>> DA_alignmentDic,
                                HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>> MS_AalignmentDic) throws IOException
    {
        BufferedWriter unfoundWriter= new BufferedWriter(new FileWriter("unfoundDAEntries"));
        for (Integer tharwa_DIA: tharwaDic.keySet())
        {
            boolean DAMatched= false;
            boolean MSAMatched= false;

            if (DA_alignmentDic.containsKey(tharwa_DIA))
            {
                for (Integer tharwa_En:tharwaDic.get(tharwa_DIA).keySet())
                {
                    if (DA_alignmentDic.get(tharwa_DIA).containsKey(tharwa_En))
                    {
                        tharwaDic.get(tharwa_DIA).get(tharwa_En).addAll(DA_alignmentDic.get(tharwa_DIA).get(tharwa_En));
                        DAMatched = true;
                    }
                }
            }
            //Ar word is not found in DA data. So we search it in MSA data
            else  if (MS_AalignmentDic.containsKey(tharwa_DIA))
            {
                for (Integer tharwa_En:tharwaDic.get(tharwa_DIA).keySet())
                {
                    if (MS_AalignmentDic.get(tharwa_DIA).containsKey(tharwa_En))
                    {
                        tharwaDic.get(tharwa_DIA).get(tharwa_En).addAll(MS_AalignmentDic.get(tharwa_DIA).get(tharwa_En));
                        MSAMatched= true;
                    }
                }
            }
            else
            {
                unfoundCounter++;
                unfoundWriter.write(get_DIA_Word(tharwa_DIA)+"\n");
            }

            if (DAMatched== true)
                DADataMatchCounter++;
            else if (MSAMatched==true)
                MSADataMatchCounter++;
        }

        unfoundWriter.flush();
        unfoundWriter.close();
    }


    public static void writeTharwa(HashMap<Integer,HashMap<Integer, Vector<Vector<Integer>>>> tharwaDic) throws IOException
    {
        BufferedWriter tharwaWriter= new BufferedWriter(new FileWriter("LinkedTharwa"));
        int tharwaId=0;
        for (Integer ArIndex: tharwaDic.keySet())
        {
            tharwaId++;
            tharwaWriter.write(tharwaId+"\t"+get_DIA_Word(ArIndex)+"\n");
            for(Integer EnIndex: tharwaDic.get(ArIndex).keySet())
            {
                tharwaWriter.write("\t\t"+get_En_Word(EnIndex)+"\t");
                for (Vector<Integer> sentenceVector: tharwaDic.get(ArIndex).get(EnIndex))
                {
                    tharwaWriter.write("\t\t"+sentenceVector.get(0)+","+sentenceVector.get(1)+
                    ","+sentenceVector.get(2)+" ## ");
                }
                tharwaWriter.write("\n");
            }
            tharwaWriter.write("\n");
        }
    }



    ///////////////////////////////////////////////////////////
    /////////////////// SUPPPORTING FUNCTIONS /////////////////
    //////////////////////////////////////////////////////////

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

    public static String removeInsideWordPlus(String str){
        String cleanedStr=str;
        boolean hasLeadingPlus=false;
        boolean hasTailingPlus=false;
        if (str.charAt(0)=='+')
        {
            hasLeadingPlus=true;
            cleanedStr=str.substring(1,str.length());
        }
        if(str.charAt(str.length()-1)=='+'){
            hasTailingPlus=true;
            cleanedStr=str.substring(0,str.length()-1);
        }
        if (cleanedStr.contains("+"))
            cleanedStr=cleanedStr.replace("+","");

        if (hasLeadingPlus==true)
            cleanedStr="+"+cleanedStr;
        if(hasTailingPlus==true)
            cleanedStr=cleanedStr+"+";

        return cleanedStr;
    }

}

