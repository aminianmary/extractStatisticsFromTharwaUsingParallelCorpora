package LinkTharwaToParallelCorpora;

/**
This java class aims to extract example sentences for EGY-MSA pairs in Tharwa. ENG equivalents are incorporated to consider different
 senses of words. As the result, each EGY word in Tharwa is connected to the sentences from parallel data which have that particular EGY
 word in a certain sense. Respectively, equivalent MSA word is connected to the sentences which implies the same sense.

 In both cases (EGY and MSA), Eng equivalent is meant to be sense indicator.
 */


import utilities.Preprocessing;

import java.io.*;
import java.util.*;

public class LinkCorpusToTharwa_on_EGY_MSA {

    static HashMap<String, Integer> En_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> En_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<String, Integer> DIA_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> DIA_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<String, Integer> MSA_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> MSA_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<Integer, HashMap<Integer, HashMap<Integer, Set<Integer>>>> tharwaDic=new HashMap<Integer, HashMap<Integer, HashMap<Integer, Set<Integer>>>>();

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

        Boolean isNormalized= Boolean.parseBoolean(args[7]);
        Boolean isLemmetied= Boolean.parseBoolean(args[8]);
        Boolean isDiacratized= Boolean.parseBoolean(args[9]);

        String normalized="0";
        String lemmatized="0";
        String diacritized="undiac";

        if (isNormalized)
            normalized="AlefYaNormal";
        if (isLemmetied)
            lemmatized= "lem";
        if (isDiacratized)
            diacritized="diac";

        String prefix= normalized+"_"+lemmatized+"_"+diacritized;

        //Creates TharwaDict from Tharwa
        tharwaDic= ConvertTharwaToDic(tharwaPath, isNormalized, isDiacratized);

        // Creates Alignmenent data dictionary for MSA-En and Egy-En separately.
        HashMap<Integer,HashMap<Integer, Set<ArrayList<Integer>>>>  DA_AlignmentdDics =
                build_Ar_EnAlignDic(DIA_En_ArDataPath, DIA_En_EngDataPath, DIA_En_Alignment_Path,"DA",isNormalized, isDiacratized);

        HashMap<Integer,HashMap<Integer, Set<ArrayList<Integer>>>>  MSA_AlignmentdDics =
                build_Ar_EnAlignDic(MSA_En_ArDataPath, MSA_En_EngDataPath, MSA_En_Alignment_Path,"MSA", isNormalized, isDiacratized);


        compare(tharwaDic, DA_AlignmentdDics, MSA_AlignmentdDics);

        System.out.println("# of Tharwa generated triples   "+ tharwaSize);
        System.out.println("# of Tharwa DA entries  "+ tharwaDic.size());
        System.out.println("DA Matched Entries  "+ DADataMatchCounter);
        System.out.println("MSA Matched Entries  "+ MSADataMatchCounter);
        System.out.println("Unfound Entries  "+ unfoundCounter);
    }

    /////////////////////////////////////////////////////////////
    ////////////////////////// MAIN FUNCTIONS ///////////////////
    /////////////////////////////////////////////////////////////

    public static HashMap<Integer, HashMap<Integer, HashMap<Integer, Set<Integer>>>> ConvertTharwaToDic(String tharwaPath,
                                                                                                        boolean AlefYaNormalized,
                                                                                                        boolean diacritized)
            throws IOException
    {
        System.out.println("***************************");
        System.out.println("Convert Tharwa To Dic Started...\n");

        BufferedWriter tharwaWriter=new BufferedWriter(new FileWriter("tharwa"));
        BufferedReader tharwaReader = new BufferedReader(new FileReader(tharwaPath));
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Set<Integer>>>> tharwaDic=new HashMap<Integer, HashMap<Integer, HashMap<Integer, Set<Integer>>>>();

        String DA_word = "";
        String MSA_word = "";
        String Eng_glosses="";
        Integer id= 0;
        String LineToRead= tharwaReader.readLine();
        while ((LineToRead =tharwaReader.readLine()) != null) {
            String[] splitedLine = LineToRead.split("\t");

            id = Integer.parseInt(splitedLine[0]);
            DA_word = splitedLine[2]; // EGY_word column (CODA column)
            MSA_word = splitedLine[4];  //MSA-Lemma column
            Eng_glosses = splitedLine[7]; // English-Equivalent column

            if (!DA_word.equals("UNK"))
            {

            String[] temp_ENG_glosses = Eng_glosses.replace("##", ";;").split(";;");

            ArrayList<String> Eng_Glosses_List = new ArrayList<String>();
            for (String En_token : temp_ENG_glosses) {
                if (En_token.contains("/")) {
                    if (!En_token.contains(" ")) {
                        if (!Eng_Glosses_List.contains(En_token.split("/")[0]))
                            Eng_Glosses_List.add(En_token.split("/")[0]);
                        if (!Eng_Glosses_List.contains(En_token.split("/")[1]))
                            Eng_Glosses_List.add(En_token.split("/")[1]);
                    } else {
                        String[] spaceSeparatedTokens = En_token.split(" ");
                        String[] slashSeparatedToken_1 = spaceSeparatedTokens[0].split("/");
                        String[] slashSeparatedToken_2 = spaceSeparatedTokens[1].split("/");

                        for (int i = 0; i < slashSeparatedToken_1.length; i++) {
                            for (int j = 0; j < slashSeparatedToken_2.length; j++) {
                                if (!Eng_Glosses_List.contains(slashSeparatedToken_1[i] + " " + slashSeparatedToken_2[j]))
                                    Eng_Glosses_List.add(slashSeparatedToken_1[i] + " " + slashSeparatedToken_2[j]);
                            }
                        }
                    }
                } else {
                    if (!Eng_Glosses_List.contains(En_token))
                        Eng_Glosses_List.add(En_token);

                }
            }

            System.out.println(Eng_Glosses_List);


                if (AlefYaNormalized == true) {
                    if (diacritized == false) {
                        MSA_word = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(MSA_word)));
                        DA_word = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(DA_word)));

                    } else {

                        MSA_word = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(MSA_word));
                        DA_word = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(DA_word));

                    }
                } else {
                    if (diacritized == false) {
                        MSA_word = Preprocessing.remove_last_vowel(Preprocessing.undiacratize(MSA_word));
                        DA_word = Preprocessing.remove_last_vowel(Preprocessing.undiacratize(DA_word));

                    }
                    else
                    {
                        MSA_word= Preprocessing.remove_last_vowel(MSA_word);
                        DA_word= Preprocessing.remove_last_vowel(DA_word);

                    }
                }

            //get index for the words
            Set<Integer> En_glosse_indexed = new HashSet<Integer>();
            int DA_index = 0;
            int MSA_index = 0;

            for (String En : Eng_Glosses_List) {
                int En_index = 0;
                if (!En_indexDic.containsKey(En))
                    update_En_Index_Dic(En);

                En_index = get_En_Index(En);
                En_glosse_indexed.add(En_index);
            }

            System.out.println(En_glosse_indexed);

            if (!DIA_indexDic.containsKey(DA_word))
                update_DIA_Index_Dic(DA_word);
            if (!MSA_indexDic.containsKey(MSA_word))
                update_MSA_Index_Dic(MSA_word);

            DA_index = get_DIA_Index(DA_word);
            MSA_index = get_MSA_Index(MSA_word);

            //writing down Tharwa
            tharwaWriter.write(get_DIA_Word(DA_index) + "\t" + get_MSA_Word(MSA_index) + "\t");
            for (int en : En_glosse_indexed)
                tharwaWriter.write(get_En_Word(en) + ",");
            tharwaWriter.write("\n");


            //Create Tharwa
            if (!tharwaDic.containsKey(DA_index)) {
                HashMap<Integer, HashMap<Integer, Set<Integer>>> EnHashMap = new HashMap<Integer, HashMap<Integer, Set<Integer>>>();
                HashMap<Integer, Set<Integer>> MSAHashMap = new HashMap<Integer, Set<Integer>>();
                Set<Integer> ids = new HashSet<Integer>();

                ids.add(id);
                MSAHashMap.put(MSA_index, ids);

                for (int En : En_glosse_indexed) {
                    EnHashMap.put(En, MSAHashMap);
                }

                tharwaDic.put(DA_index, EnHashMap);
            } else {
                Set<Integer> newEngGlosses = En_glosse_indexed;
                newEngGlosses.removeAll(tharwaDic.get(DA_index).keySet());

                if (newEngGlosses.size() > 0) {
                    HashMap<Integer, Set<Integer>> MSAHashMap = new HashMap<Integer, Set<Integer>>();
                    Set<Integer> ids = new HashSet<Integer>();

                    ids.add(id);
                    MSAHashMap.put(MSA_index, ids);
                    for (int En : newEngGlosses) {
                        tharwaDic.get(DA_index).put(En, MSAHashMap);
                    }

                } else {
                    for (Integer En : En_glosse_indexed) {
                        Set<Integer> tharwaMSA = tharwaDic.get(DA_index).get(En).keySet();
                        if (!tharwaMSA.contains(MSA_index)) {
                            Set<Integer> ids = new HashSet<Integer>();
                            ids.add(id);
                            tharwaDic.get(DA_index).get(En).put(MSA_index, ids);
                        } else {
                            tharwaDic.get(DA_index).get(En).get(MSA_index).add(id);
                        }
                    }
                }
            }


            if (id % 10000 == 0)
                System.out.println(id);
        }
        }
        tharwaWriter.flush();
        tharwaWriter.close();
        tharwaReader.close();

        System.out.println("ConvertTharwaToDic_EGYKey finished");
        System.out.println("Numbe of Tharwa Entries "+ tharwaDic.size());
        System.out.println("***************************");
        return tharwaDic;
    }


    public static HashMap<Integer,HashMap<Integer, Set<ArrayList<Integer>>>> build_Ar_EnAlignDic(String Ar_corpus,
                                                                                                 String En_corpus,
                                                                                                 String Allign_file,
                                                                                                 String parallelDataType,
                                                                                                 boolean AlefYaNormalized,
                                                                                                 boolean diacritized)
            throws IOException
    {

        System.out.println("Building DIA-En Alignment Dics Started...");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));


        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        HashMap<Integer,HashMap<Integer, Set<ArrayList<Integer>>>>  Ar_En_Align_Dic = new  HashMap<Integer,HashMap<Integer, Set<ArrayList<Integer>>>>();

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

            if (!Allign_line_to_read.trim().equals("") && !Ar_line_to_read.equals("") && !En_line_to_read.equals(""))
            {

            String[] Ar_words= Ar_line_to_read.split(" ");
            String[] En_words= En_line_to_read.split(" ");
            String[] Allign_words= Allign_line_to_read.split(" ");
            totalNumberTokens += Ar_words.length;


            for (int i=0;i<Allign_words.length;i++) {
                // finding indexes from alignment data
                Integer ArabicAlignmentIndex = Integer.parseInt(Allign_words[i].split("-")[0]);
                Integer EngAlignmentIndex = Integer.parseInt(Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma = Ar_words[ArabicAlignmentIndex];
                String EngLemma = En_words[EngAlignmentIndex];

                if (ArabicLemma.contains("@@pos@@"))
                    ArabicLemma= ArabicLemma.split("@@pos@@")[0];

                if (AlefYaNormalized==true)
                {
                    if (diacritized==false)
                    {
                        ArabicLemma =Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(ArabicLemma)));
                    }
                    else {
                        ArabicLemma = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(ArabicLemma));
                    }
                }
                else
                {
                    if (diacritized== false)
                    {
                        ArabicLemma= Preprocessing.remove_last_vowel(Preprocessing.undiacratize(ArabicLemma));
                    }
                    else
                    {
                        ArabicLemma= Preprocessing.remove_last_vowel(ArabicLemma);
                    }
                }


                // finging indexes in indexed dictionaries for DIA data
                Integer Arabic_index = 0;
                Integer En_index = 0;


                if (parallelDataType.equals("DA")) {
                    if (!DIA_indexDic.containsKey(ArabicLemma))
                        update_DIA_Index_Dic(ArabicLemma);
                    Arabic_index = get_DIA_Index(ArabicLemma);
                } else if (parallelDataType.equals("MSA")) {
                    if (!MSA_indexDic.containsKey(ArabicLemma))
                        update_MSA_Index_Dic(ArabicLemma);
                    Arabic_index = get_MSA_Index(ArabicLemma);
                } else
                    System.out.println("!!!NOTE!!! Parallel Data Type Undefined!");


                if (!En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);
                En_index = get_En_Index(EngLemma);


                //Update Ar_En_Align_Dic
                if (!Ar_En_Align_Dic.containsKey(Arabic_index)) {
                    ArrayList<Integer> sentenceVector = new ArrayList<Integer>();
                    sentenceVector.add(sentenceID);
                    sentenceVector.add(ArabicAlignmentIndex);
                    sentenceVector.add(EngAlignmentIndex);

                    Set<ArrayList<Integer>> sentencesVectors = new HashSet<ArrayList<Integer>>();
                    sentencesVectors.add(sentenceVector);

                    HashMap<Integer, Set<ArrayList<Integer>>> EnGlosses = new HashMap<Integer, Set<ArrayList<Integer>>>();
                    EnGlosses.put(En_index, sentencesVectors);

                    Ar_En_Align_Dic.put(Arabic_index, EnGlosses);
                    totalNumberAr_EnTuples += 1;
                } else {
                    if (!Ar_En_Align_Dic.get(Arabic_index).keySet().contains(En_index)) {
                        ArrayList<Integer> sentenceVector = new ArrayList<Integer>();
                        sentenceVector.add(sentenceID);
                        sentenceVector.add(ArabicAlignmentIndex);
                        sentenceVector.add(EngAlignmentIndex);

                        Set<ArrayList<Integer>> sentencesVector = new HashSet<ArrayList<Integer>>();
                        sentencesVector.add(sentenceVector);

                        Ar_En_Align_Dic.get(Arabic_index).put(En_index, sentencesVector);
                        totalNumberAr_EnTuples += 1;

                    } else {
                        ArrayList<Integer> sentenceVector = new ArrayList<Integer>();
                        sentenceVector.add(sentenceID);
                        sentenceVector.add(ArabicAlignmentIndex);
                        sentenceVector.add(EngAlignmentIndex);

                        Ar_En_Align_Dic.get(Arabic_index).get(En_index).add(sentenceVector);
                        totalNumberAr_EnTuples += 1;
                    }
                }
                }
            }
            else
            {
                if (Allign_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": alignment is empty");

                if (En_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": English is empty");

                if (Ar_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": Arabic is empty");
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


    public static void compare (HashMap<Integer, HashMap<Integer, HashMap<Integer, Set<Integer>>>> tharwaDic,
                                HashMap<Integer,HashMap<Integer, Set<ArrayList<Integer>>>> DA_alignmentDic,
                                HashMap<Integer,HashMap<Integer, Set<ArrayList<Integer>>>> MSA_alignmentDic) throws IOException
    {
        BufferedWriter tharwaWriter= new BufferedWriter(new FileWriter("LinkedTharwa"));

        for (int tharwa_DIA: tharwaDic.keySet()) {

            //for different senses of DA word
            for (int tharwa_En : tharwaDic.get(tharwa_DIA).keySet()) {

                Set<ArrayList<Integer>> exampleSentencesFromDAData = new HashSet<ArrayList<Integer>>();

                //if the DA word is found in DA_alignmentDic (word found in DA data and is aligned to an English word in our automatically extracted word alignments)
                if (DA_alignmentDic.containsKey(tharwa_DIA) && DA_alignmentDic.get(tharwa_DIA).containsKey(tharwa_En))
                {
                    exampleSentencesFromDAData = DA_alignmentDic.get(tharwa_DIA).get(tharwa_En);
                }

                //check if corresponding MSA equivalents are seen in MSA-En parallel data
                Set<Integer> tharwa_MSAList = tharwaDic.get(tharwa_DIA).get(tharwa_En).keySet();
                for (int tharwa_MSA : tharwa_MSAList)
                {
                    if (MSA_alignmentDic.containsKey(tharwa_MSA) && MSA_alignmentDic.get(tharwa_MSA).containsKey(tharwa_En))
                    {
                        Set<ArrayList<Integer>> exampleSentencesFromMSAData= MSA_alignmentDic.get(tharwa_MSA).get(tharwa_En);
                        tharwaWriter.write(tharwaDic.get(tharwa_DIA).get(tharwa_En).get(tharwa_MSA)+"\t"+get_DIA_Word(tharwa_DIA) + "\t" + get_En_Word(tharwa_En) + "\t" + get_MSA_Word(tharwa_MSA) + "\t");

                        int counter= exampleSentencesFromDAData.size();
                        if (counter > 20)
                            counter=20;

                        for (ArrayList<Integer> sentence:exampleSentencesFromDAData)
                        {
                            System.out.println(counter);
                            counter--;
                            if (counter > 0)
                                tharwaWriter.write(sentence.get(0)+"/"+sentence.get(1)+"/"+sentence.get(2)+";;");
                            else if (counter==0)
                                tharwaWriter.write(sentence.get(0)+"/"+sentence.get(1)+"/"+sentence.get(2));
                        }
                        tharwaWriter.write("##");

                        int counter2= exampleSentencesFromMSAData.size();
                        if (counter2 > 20)
                            counter2= 20;

                        for (ArrayList<Integer> sentence:exampleSentencesFromMSAData)
                        {
                            System.out.println(counter);
                            counter2--;
                            if (counter2 > 0)
                                tharwaWriter.write(sentence.get(0)+"/"+sentence.get(1)+"/"+sentence.get(2)+";;");
                            else if (counter2==0)
                                tharwaWriter.write(sentence.get(0)+"/"+sentence.get(1)+"/"+sentence.get(2));
                        }

                        tharwaWriter.write("\n\n");

                    }

                }

            }
        }

        tharwaWriter.flush();
        tharwaWriter.close();

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
    public  static Collection union(Collection coll1, Collection coll2) {
        Set union = new HashSet(coll1);
        union.addAll(new HashSet(coll2));
        return union;
    }

    public static Collection intersect(Collection coll1, Collection coll2) {
        Set intersection = new HashSet(coll1);
        intersection.retainAll(new HashSet(coll2));
        return intersection;
    }

    public static Collection nonOverLap(Collection coll1, Collection coll2) {
        Collection result = union(coll1, coll2);
        result.removeAll(intersect(coll1, coll2));
        return result;
    }

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

    public static String normalizeAlefYa_in_SafeBW (String originalWord)
    {
        return originalWord.replace("M","A").replace("I","A").replace("O","A").replace("L","A").replace("e","A").replace("Y","y");
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


    public static String get_MSA_Word(Integer index) {
        return MSA_indexDic_shadow.get(index);
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

