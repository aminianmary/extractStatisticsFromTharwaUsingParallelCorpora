package ExtractStatsUsingParallelCorpora;

import utilities.IdFreqTuple;
import utilities.MSAEnTuple;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.ArrayList;

public class ExtractStatsUsingSurfaceForms {

    static HashMap<String, Integer> En_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> En_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<String, Integer> MSA_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> MSA_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<String, Integer> DIA_indexDic= new HashMap<String, Integer>();
    static HashMap<Integer,String> DIA_indexDic_shadow=new HashMap<Integer, String>();
    static HashMap<Integer, Vector<Integer>> En_lemma_surf_Dic= new HashMap<Integer, Vector<Integer>>();
    static HashMap<String, IdFreqTuple> CODA_freq= new HashMap<String,IdFreqTuple>();

    //tharwa information
    static ArrayList<Integer> tharwa_DIA = new ArrayList<Integer>();
    static ArrayList<String> tharwa_DIA_MSA= new ArrayList<String>();
    static ArrayList<String> tharwa_DIA_MSA_EN= new ArrayList<String>();

    public static void main(String[] args) throws IOException
    {
        /*
        args[0] = root
        args[1] = DIA_En_Allignment_Path
        args[2] = MSA_En_Allignment_Path
        args[3] = tharwaPath
        args[4] = DIA_En_EngDataPath
        args[5] = DIA_En_ArDataPath
        args[6] = MSA_En_EngDataPath
        args[7] = MSA_En_ArDataPath
        args[8]= is_Surface_Form_Included (True/False)
         */

        if (args.length < 8)
            System.out.println("One of the input arguments missing");
        String DIA_En_Allignment_Path = args[0] + args [1];
        String MSA_En_Allignment_Path =args[0] + args [2];
        String tharwaPath= args[0] + args[3];
        ////////////////

        String DIA_En_EngDataPath =args[0] + args[4];
        String DIA_En_ArDataPath =args[0] + args[5];
        String DIA_En_surfacePath= DIA_En_EngDataPath+".srf";

        String MSA_En_EngDataPath =args[0] + args[6];
        String MSA_En_ArDataPath =args[0] + args [7];
        String MSA_En_surfacePath= MSA_En_EngDataPath+".srf";

        boolean isSrfFromIncluded=Boolean.parseBoolean(args[8]);

        //Creates TharwaDict from ExtractStats.Tharwa
        HashMap<Integer, Vector<MSAEnTuple>> tharwaDic = ConvertTharwaToDic(tharwaPath);

        // Creates Alignmenent data dictionary for MSA-En and Egy-En separately.
        HashMap<Integer, Vector<Integer>> MSA_En_Alignment_Dic=new HashMap<Integer, Vector<Integer>>();
        ArrayList<ArrayList<Integer>> MSA_En_Alignment_ArrayList=new ArrayList<ArrayList<Integer>>();
        HashMap<Integer, Vector<Integer>> DIA_En_Alignment_Dic=new HashMap<Integer, Vector<Integer>>();
        ArrayList<ArrayList<Integer>> DIA_En_Alignment_ArrayList=new ArrayList<ArrayList<Integer>>();



        if (isSrfFromIncluded== true){
            MSA_En_Alignment_Dic= creatMSAEnAllignDataDicWithSrfForms(MSA_En_ArDataPath, MSA_En_EngDataPath,
                    MSA_En_Allignment_Path,MSA_En_surfacePath );

            DIA_En_Alignment_Dic = creatDIAEnAllignmentDataDicWithSrfForms(DIA_En_ArDataPath, DIA_En_EngDataPath,
                    DIA_En_Allignment_Path, DIA_En_surfacePath);
        }
        else if (isSrfFromIncluded==false) {
            MSA_En_Alignment_Dic= creatMSAEnAllignDataDicWithoutSrfForms(MSA_En_ArDataPath, MSA_En_EngDataPath,
                    MSA_En_Allignment_Path);
            // MSA_En_Alignment_ArrayList=creatMSAEnAllignDataDicWithoutSrfForms_usingArrayList(MSA_En_ArDataPath, MSA_En_EngDataPath,
            //                   MSA_En_Allignment_Path,En_indexDic.size());

            DIA_En_Alignment_Dic = creatDIAEnAllignmentDataDicWithoutSrfForms(DIA_En_ArDataPath, DIA_En_EngDataPath,
                    DIA_En_Allignment_Path);

            //  DIA_En_Alignment_ArrayList = creatDIAEnAllignmentDataDicWithoutSrfForms_usingArrayList(DIA_En_ArDataPath, DIA_En_EngDataPath,
            //           DIA_En_Allignment_Path, En_indexDic.size());
        }

        //writing CODA_freq Dic into a text file
        /*
        write_CODA_freq("CODA_Freq");
        write_index_dic(En_indexDic, "En_index_dic");
        write_index_dic(MSA_indexDic,"MSA_index_dic");
        write_index_dic(DIA_indexDic,"DIA_index_dic");
        */

        // Merges MSA-En and Egy-En alignment dictionary together to create Transdict!!

        //    integrate(DIA_En_Alignment_Dic, MSA_En_Alignment_Dic);
        //  HashMap<Integer, Vector<Vector<Integer>>> transDic =integrateAlignmentCompareWithoutWritingPhase(DIA_En_Alignment_Dic, MSA_En_Alignment_Dic);
        // ArrayList<ArrayList<int[]>> transDic =integrateAllignmentCompare_usingArrayList(DIA_En_Alignment_ArrayList, MSA_En_Alignment_ArrayList,DIA_indexDic.size());

        // writeTokenizedTransDic(transDic);
        // writeIndexedTransDic(transDic);

        //Vector <Integer> Statistics= compare_Tharwa_With_Extracted_Tripels(tharwaDic,transDic,isSrfFromIncluded,"stats.out");
        //System.out.println(Statistics.toString());
    }

    public static void integrate(HashMap<Integer, Vector<Integer>> EGY_Eng_AlignmentDataDic,HashMap<Integer, Vector<Integer>> MSA_Eng_AlignmentDataDic) throws IOException {
        BufferedWriter englishMsaWriter=new BufferedWriter(new FileWriter("En-MSA"));
        BufferedWriter englishEgyptianWriter=new BufferedWriter(new FileWriter("En-DA"));
        BufferedWriter englishMsaEgyptianWriter=new BufferedWriter(new FileWriter("En-DA-MSA"));

        System.out.println("DA Data");

        int counter=0;
        for (Integer english: EGY_Eng_AlignmentDataDic.keySet()){
            for(Integer egyptian: EGY_Eng_AlignmentDataDic.get(english)){
                englishEgyptianWriter.write(english+"\t"+egyptian+"\n");
                counter++;
                if(counter%10000==0)
                    System.out.println(counter);
            }
        }
        englishEgyptianWriter.flush();
        englishEgyptianWriter.close();

        System.out.println("MSA Data");
        counter=0;
        for (Integer english: MSA_Eng_AlignmentDataDic.keySet()){
            for(Integer msa: MSA_Eng_AlignmentDataDic.get(english)){
                englishMsaWriter.write(english+"\t"+msa+"\n");
                counter++;
                if(counter%10000==0)
                    System.out.println(counter);
            }
            englishMsaWriter.flush();
        }

        englishMsaWriter.close();

        System.out.println("Both data");
        counter=0;
        int diff_Eng_glosses=0;
        for (Integer english: EGY_Eng_AlignmentDataDic.keySet())
        {
            if(MSA_Eng_AlignmentDataDic.containsKey(english))
            {
                for(Integer egyptian: EGY_Eng_AlignmentDataDic.get(english)){
                    for(Integer msa: MSA_Eng_AlignmentDataDic.get(english)){
                        englishMsaEgyptianWriter.write(english+"\t"+egyptian+"\t"+msa+"\n");
                        counter++;
                        if(counter%10000000==0)
                            System.out.println(counter);
                    }
                }
            }
            else
                diff_Eng_glosses++;

        }

        System.out.println( diff_Eng_glosses);
        englishMsaEgyptianWriter.flush();
        englishMsaEgyptianWriter.close();


    }


    public static HashMap<Integer, Vector<Integer>> creatDIAEnAllignmentDataDicWithSrfForms(String Ar_corpus,String En_corpus,
                                                                                            String Allign_file, String En_srface_file ) throws IOException
    {

        System.out.println("creat_DIA_En_Allignment_Data_Dic started");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));
        BufferedReader En_surface_form_reader= new BufferedReader(new FileReader(En_srface_file));


        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";
        String En_srface_line_to_read="";


        HashMap<Integer, Vector<Integer>>  Align_Dic = new  HashMap<Integer, Vector<Integer>>();
        int sentenceID = -1;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();
            En_srface_line_to_read= En_surface_form_reader.readLine();


            sentenceID++;
            if(sentenceID %100==0)
                System.out.println(sentenceID);

            String[] Ar_words= Ar_line_to_read.split(" ");
            String[] En_words= En_line_to_read.split(" ");
            String[] Allign_words= Allign_line_to_read.split(" ");
            String[] En_surface_words= En_srface_line_to_read.split(" ");

            // find CODA words frequencies in the Ar_words

            update_CODA_freq (Ar_words);



            for (int i=0;i<Allign_words.length;i++)
            {
                // finding indexes from alignment data
                Integer ArabicIndex=Integer.parseInt ( Allign_words[i].split("-")[0]);
                Integer EngIndex= Integer.parseInt ( Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma= removeInsideWordPlus(Ar_words [ArabicIndex]);
                String EngLemma=En_words[EngIndex];
                String EngSurface= En_surface_words [EngIndex];


                // finging indexes in indexed dictionaries for DIA data

                Integer DIA_index=0;
                Integer En_index= 0;
                Integer En_surface_index=0;


                if(!DIA_indexDic.containsKey(ArabicLemma))
                    update_DIA_Index_Dic(ArabicLemma);
                if (! En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);
                if (!En_indexDic.containsValue(EngSurface))
                    update_En_Index_Dic(EngSurface);

                DIA_index = get_DIA_Index(ArabicLemma);
                En_index = get_En_Index(EngLemma);
                En_surface_index= get_En_Index(EngSurface);

                //Updating En_surface_dic

                update_lemma_surface_dic (En_index, En_surface_index);



                if (!Align_Dic.containsKey(En_index))
                {
                    Vector<Integer> tempVector=new Vector<Integer>();
                    tempVector.add(DIA_index);
                    Align_Dic.put(En_index,tempVector);
                }
                else
                {
                    if(!Align_Dic.get(En_index).contains(DIA_index))
                        Align_Dic.get(En_index).add(DIA_index);
                }
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();
        En_surface_form_reader.close();


        System.out.println("creat_DIA_En_dic finished");
        System.out.println("DIA_En_dic size" + Align_Dic.size()+"\n*************************");
        return  Align_Dic;

    }

    public static HashMap<Integer, Vector<Integer>> creatDIAEnAllignmentDataDicWithoutSrfForms(String Ar_corpus,String En_corpus,
                                                                                               String Allign_file) throws IOException
    {

        System.out.println("Creat_DIA_En_Allignment_Data_Dic started");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));


        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        HashMap<Integer, Vector<Integer>>  Align_Dic = new  HashMap<Integer, Vector<Integer>>();
        int sentenceID = -1;
        int totalNumberTokens=0;
        int totalNumberTuples=0;

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

            // find CODA words frequencies in the Ar_words

            update_CODA_freq (Ar_words);



            for (int i=0;i<Allign_words.length;i++)
            {
                // finding indexes from alignment data
                Integer ArabicIndex=Integer.parseInt ( Allign_words[i].split("-")[0]);
                Integer EngIndex= Integer.parseInt ( Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma=removeInsideWordPlus(Ar_words [ArabicIndex]);
                String EngLemma=En_words[EngIndex];


                // finging indexes in indexed dictionaries for DIA data

                Integer DIA_index=0;
                Integer En_index= 0;


                if(!DIA_indexDic.containsKey(ArabicLemma))
                    update_DIA_Index_Dic(ArabicLemma);
                if (! En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);

                DIA_index = get_DIA_Index(ArabicLemma);
                En_index = get_En_Index(EngLemma);

                if (!Align_Dic.containsKey(En_index))
                {
                    Vector<Integer> tempVector=new Vector<Integer>();
                    tempVector.add(DIA_index);
                    Align_Dic.put(En_index,tempVector);
                    totalNumberTuples += tempVector.size();
                }
                else
                {
                    if(!Align_Dic.get(En_index).contains(DIA_index))
                        Align_Dic.get(En_index).add(DIA_index);
                    totalNumberTuples +=1;
                }
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        System.out.println("creat_DIA_En_dic finished");
        System.out.println("DIA_index_Dic size: " + DIA_indexDic.size());
        System.out.println("En_index_Dic size: " + En_indexDic.size());
        System.out.println("Total Number of Tokens"+ totalNumberTokens);
        System.out.println("Total Number of Tuples: "+totalNumberTuples);
        System.out.println("Total Number of Entries in DIA-En Dic " + Align_Dic.size()+"\n*********************************");
        return  Align_Dic;

    }


    public static ArrayList<ArrayList<Integer>> creatDIAEnAllignmentDataDicWithoutSrfForms_usingArrayList(String Ar_corpus,String En_corpus,
                                                                                                          String Allign_file, int En_index_Dic_size) throws IOException
    {

        System.out.println("Creat_DIA_En_Allignment_Data_Dic started");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));


        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        ArrayList<ArrayList<Integer>>  Align_Dic = new  ArrayList<ArrayList<Integer>>();
        //initializing Dic with some empty nodes
        for (int p=0;p<En_index_Dic_size;p++)
        {
            Align_Dic.add(new ArrayList<Integer>());
        }

        int sentenceID = -1;
        int totalNumberTokens=0;

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

            // find CODA words frequencies in the Ar_words

            update_CODA_freq (Ar_words);



            for (int i=0;i<Allign_words.length;i++)
            {
                // finding indexes from alignment data
                Integer ArabicIndex=Integer.parseInt ( Allign_words[i].split("-")[0]);
                Integer EngIndex= Integer.parseInt ( Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma=removeInsideWordPlus(Ar_words [ArabicIndex]);
                String EngLemma=En_words[EngIndex];


                // finging indexes in indexed dictionaries for DIA data

                Integer DIA_index=0;
                Integer En_index= 0;

                if(!DIA_indexDic.containsKey(ArabicLemma))
                    update_DIA_Index_Dic(ArabicLemma);
                if (! En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);

                DIA_index = get_DIA_Index(ArabicLemma);
                En_index = get_En_Index(EngLemma);

                // Constructing Alignment Dictionary
                if (Align_Dic.size() > En_index)
                {
                    if(!Align_Dic.get(En_index).contains(DIA_index))
                        Align_Dic.get(En_index).add(DIA_index);
                }
                else
                //we have not seen this En index before
                {
                    ArrayList<Integer> tempArray= new ArrayList<Integer>();
                    tempArray.add(DIA_index);
                    Align_Dic.add(En_index, tempArray);
                }
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        System.out.println("creat_DIA_En_dic finished");
        System.out.println("DIA_index_Dic size: " + DIA_indexDic.size());
        System.out.println("totalNumberTokens"+ totalNumberTokens);
        System.out.println("DIA_En_dic size " + Align_Dic.size()+"\n*********************************");
        return  Align_Dic;

    }

    public static HashMap<Integer, Vector<Integer>> creatMSAEnAllignDataDicWithSrfForms(String Ar_corpus,String En_corpus,
                                                                                        String Allign_file, String En_srface_file) throws IOException
    {

        System.out.println("creat_MSA_En_Allignment_Data_Dic started");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));
        BufferedReader En_surface_form_reader= new BufferedReader(new FileReader(En_srface_file));

        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";
        String En_srface_line_to_read="";

        HashMap<Integer, Vector<Integer>>  Align_Dic = new  HashMap<Integer, Vector<Integer>>();
        int sentenceID = -1;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();
            En_srface_line_to_read= En_surface_form_reader.readLine();

            sentenceID++;

            String[] Ar_words= Ar_line_to_read.split(" ");
            String[] En_words= En_line_to_read.split(" ");
            String[] Allign_words= Allign_line_to_read.split(" ");
            String[] En_surface_words= En_srface_line_to_read.split(" ");

            for (int i=0;i<Allign_words.length;i++)
            {
                // finding indexes from alignment data
                Integer ArabicIndex=Integer.parseInt ( Allign_words[i].split("-")[0]);
                Integer EngIndex= Integer.parseInt ( Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma= removeInsideWordPlus(Ar_words [ArabicIndex]);
                String EngLemma=En_words[EngIndex];
                String EngSurface= En_surface_words [EngIndex];

                // finging indexes in indexed dictionaries for DIA data

                Integer MSA_index=0;
                Integer En_index= 0;
                Integer En_surface_index=0;

                if(!MSA_indexDic.containsKey(ArabicLemma))
                    update_MSA_Index_Dic(ArabicLemma);
                if (!En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);
                if (!En_indexDic.containsKey(EngSurface))
                    update_En_Index_Dic(EngSurface);

                MSA_index = get_MSA_Index(ArabicLemma);
                En_index = get_En_Index(EngLemma);
                En_surface_index= get_En_Index(EngSurface);

                //Updating En_surface_dic

                update_lemma_surface_dic (En_index, En_surface_index);

                // Constructing Alignment Dictionary
                if (!Align_Dic.containsKey(En_index))
                {
                    Vector<Integer> tempVector=new Vector<Integer>();
                    tempVector.add(MSA_index);
                    Align_Dic.put(En_index,tempVector);
                }
                else
                {
                    if(!Align_Dic.get(En_index).contains(MSA_index))
                        Align_Dic.get(En_index).add(MSA_index);
                }
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();
        En_surface_form_reader.close();

        System.out.println("creat_MSA_En_dic finished");
        System.out.println("MSA_En alignment dic size " + Align_Dic.size());
        System.out.println("***************************");

        return  Align_Dic;

    }

    public static HashMap<Integer, Vector<Integer>> creatMSAEnAllignDataDicWithoutSrfForms(String Ar_corpus,String En_corpus,
                                                                                           String Allign_file) throws IOException
    {

        System.out.println("Creat_MSA_En_Allignment_Data_Dic started");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));

        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        HashMap<Integer, Vector<Integer>>  Align_Dic = new  HashMap<Integer, Vector<Integer>>();
        int totalNumberTuples=0;
        int sentenceID = -1;
        int totalNumberTokens=0;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();

            sentenceID++;

            String[] Ar_words= Ar_line_to_read.split(" ");
            String[] En_words= En_line_to_read.split(" ");
            String[] Allign_words= Allign_line_to_read.split(" ");
            totalNumberTokens += Ar_words.length;

            for (int i=0;i<Allign_words.length;i++)
            {
                // finding indexes from alignment data
                Integer ArabicIndex=Integer.parseInt ( Allign_words[i].split("-")[0]);
                Integer EngIndex= Integer.parseInt ( Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma=removeInsideWordPlus(Ar_words [ArabicIndex]);
                String EngLemma=En_words[EngIndex];

                // finging indexes in indexed dictionaries for DIA data

                Integer MSA_index=0;
                Integer En_index= 0;
                Integer En_surface_index=0;

                if(!MSA_indexDic.containsKey(ArabicLemma))
                    update_MSA_Index_Dic(ArabicLemma);
                if (! En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);

                MSA_index = get_MSA_Index(ArabicLemma);
                En_index = get_En_Index(EngLemma);

                // Constructing Alignment Dictionary
                if (!Align_Dic.containsKey(En_index))
                {
                    Vector<Integer> tempVector=new Vector<Integer>();
                    tempVector.add(MSA_index);
                    Align_Dic.put(En_index,tempVector);
                    totalNumberTuples += tempVector.size();
                }
                else
                {
                    if(!Align_Dic.get(En_index).contains(MSA_index))
                        Align_Dic.get(En_index).add(MSA_index);
                    totalNumberTuples +=1;
                }
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        System.out.println("creat_MSA_En_dic finished");
        System.out.println("En_Index_dic size " + En_indexDic.size());
        System.out.println("MSA_Index_Dic_dic size " + MSA_indexDic.size());
        System.out.println("Total Number of Tokens " + totalNumberTokens);
        System.out.println("Total Number of Tuples:  "+totalNumberTuples);
        System.out.println("Total Number of Entries in MSA-En Dix " + Align_Dic.size());

        System.out.println("***************************");

        return  Align_Dic;

    }


    public static ArrayList<ArrayList<Integer>> creatMSAEnAllignDataDicWithoutSrfForms_usingArrayList(String Ar_corpus,String En_corpus,
                                                                                                      String Allign_file, int En_index_Dic_size) throws IOException
    {

        System.out.println("Creat_MSA_En_Allignment_Data_Dic started");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));

        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        ArrayList<ArrayList<Integer>>  Align_Dic = new  ArrayList<ArrayList<Integer>>();
        for (int p=0;p<En_index_Dic_size;p++)
        {
            Align_Dic.add(new ArrayList<Integer>());
        }

        int sentenceID = -1;
        int totalNumberTokens=0;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();

            sentenceID++;

            String[] Ar_words= Ar_line_to_read.split(" ");
            String[] En_words= En_line_to_read.split(" ");
            String[] Allign_words= Allign_line_to_read.split(" ");
            totalNumberTokens += Ar_words.length;

            for (int i=0;i<Allign_words.length;i++)
            {
                // finding indexes from alignment data
                Integer ArabicIndex=Integer.parseInt ( Allign_words[i].split("-")[0]);
                Integer EngIndex= Integer.parseInt ( Allign_words[i].toString().split("-")[1]);

                // finding words
                String ArabicLemma=removeInsideWordPlus(Ar_words [ArabicIndex]);
                String EngLemma=En_words[EngIndex];

                // finging indexes in indexed dictionaries for DIA data

                Integer MSA_index=0;
                Integer En_index= 0;
                Integer En_surface_index=0;

                if(!MSA_indexDic.containsKey(ArabicLemma))
                    update_MSA_Index_Dic(ArabicLemma);
                if (! En_indexDic.containsKey(EngLemma))
                    update_En_Index_Dic(EngLemma);

                MSA_index = get_MSA_Index(ArabicLemma);
                En_index = get_En_Index(EngLemma);

                // Constructing Alignment Dictionary
                if (Align_Dic.size() > En_index)
                {
                    //We have seen this En gloss before
                    if (Align_Dic.get(En_index).isEmpty())
                    //We have not had this En in the list before
                    {
                        Align_Dic.get(En_index).add(MSA_index);
                    }
                    else
                    // we have seen this En gloss before
                    {
                        if(!Align_Dic.get(En_index).contains(MSA_index))
                            Align_Dic.get(En_index).add(MSA_index);
                    }
                }
                else
                //we have not seen this En index before
                {
                    ArrayList<Integer> tempArray= new ArrayList<Integer>();
                    tempArray.add(MSA_index);
                    Align_Dic.add(En_index, tempArray);
                }

            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        System.out.println("creat_MSA_En_dic finished");
        System.out.println("MSA_En_dic size " + Align_Dic.size());
        System.out.println("MSA_Index_Dic_dic size " + MSA_indexDic.size());
        System.out.println("totalNumberTokens " + totalNumberTokens);
        System.out.println("***************************");

        return  Align_Dic;

    }


    public static HashMap<Integer, Vector<MSAEnTuple>> integrateAllignment_dics(HashMap<Integer, Vector<Integer>> DIA_Eng_AlignmentDataDic,
                                                                                HashMap<Integer, Vector<Integer>> MSA_Eng_AlignmentDataDic) throws IOException
    {
        System.out.println("integrateAllignment_dics started");
        System.out.println("Writing transDic started...");

        //First we write the integrated dictionary in a text file and then read and convert the text file based on DIA as a pivot
        BufferedWriter TransDic_index_writer = new BufferedWriter(new FileWriter("transDic_indexed"));
        BufferedWriter TransDic_normal_writer = new BufferedWriter(new FileWriter("transDict_tokens"));
        BufferedWriter En_rulled_out_from_DIA_writer= new BufferedWriter(new FileWriter("Eng_rulled_out_from_DIA"));
        BufferedWriter En_rulled_out_from_MSA_writer= new BufferedWriter(new FileWriter("Eng_rulled_out_from_MSA"));

        TransDic_index_writer.write("DIA" + "\t" + "MSA" + "\t" + "En" + "\n");
        TransDic_normal_writer.write("DIA" + "\t" + "MSA" + "\t" + "En" + "\n");
        En_rulled_out_from_DIA_writer.write("En" +"\t" +"DIA" + "\n");
        En_rulled_out_from_MSA_writer.write("En" + "\t" + "MSA" + "\n");

        int counter=0;

        //writing triples with the same Eng glosses
        for (Integer En:DIA_Eng_AlignmentDataDic.keySet())
        {
            if(MSA_Eng_AlignmentDataDic.containsKey(En))
            {
                for(Integer DIA: DIA_Eng_AlignmentDataDic.get(En))
                {
                    for(Integer MSA: MSA_Eng_AlignmentDataDic.get(En))
                    {
                        TransDic_index_writer.write(DIA.toString() + "\t" + MSA.toString() + "\t" +
                                En.toString() + "\n");
                        TransDic_normal_writer.write(get_DIA_Word(DIA) + "\t" + get_MSA_Word(MSA) + "\t" +
                                get_En_Word(En) + "\n");
                        counter++;
                        if(counter%10000000==0)
                            System.out.println(counter);
                    }
                }
            }
            else  // we should keep track of the Eng glosses that are rulled out from DIA_En alignment Dic here
            {
                for (Integer DIA:DIA_Eng_AlignmentDataDic.get(En))
                {
                    En_rulled_out_from_DIA_writer.write( get_En_Word(En) + '\t' +get_DIA_Word(DIA) + '\n');
                }
            }

        }

        for (Integer En: MSA_Eng_AlignmentDataDic.keySet())
        {
            if (!DIA_Eng_AlignmentDataDic.containsKey(En))
            {
                for (Integer MSA:MSA_Eng_AlignmentDataDic.get(En))
                {
                    En_rulled_out_from_MSA_writer.write(get_En_Word(En) + '\t' + get_MSA_Word(MSA)+ '\n');
                }
            }

        }

        TransDic_index_writer.flush();
        TransDic_index_writer.close();
        TransDic_normal_writer.flush();
        TransDic_normal_writer.close();
        En_rulled_out_from_DIA_writer.flush();
        En_rulled_out_from_DIA_writer.close();
        En_rulled_out_from_MSA_writer.flush();
        En_rulled_out_from_MSA_writer.close();

        System.out.println("Writing Transdic finshed\nTransDic size "+counter);
        System.out.println("Converting TransDic to Dic started");

        //After writing Transdict now we should convert that to the same format that we convert ExtractStats.Tharwa in the form of a dic <DIA_index, Vector <MSAEntuple>>

        BufferedReader transDic_reader= new BufferedReader(new FileReader("transDic_indexed"));
        HashMap<Integer, Vector<MSAEnTuple>> transdic=new HashMap<Integer, Vector<MSAEnTuple>>();
        String transdic_lineToRead= transDic_reader.readLine();
        Integer DIA_index=0;
        Integer MSA_index=0;
        Integer En_index=0;

        int transDicCounter=0;
        while ((transdic_lineToRead= transDic_reader.readLine()) != null)
        {
            transDicCounter++;
            if(transDicCounter%10000000==0)
                System.out.println(transDicCounter);

            String[] splitedLine = transdic_lineToRead.split("\t");
            DIA_index = Integer.parseInt(splitedLine[0]);
            MSA_index= Integer.parseInt(splitedLine[1]);
            En_index= Integer.parseInt(splitedLine[2]);
            MSAEnTuple touple= new MSAEnTuple(MSA_index,En_index);

            if (!transdic.containsKey(DIA_index))
            {
                Vector<MSAEnTuple> tempVector= new Vector<MSAEnTuple>();
                tempVector.add(touple);
                transdic.put(DIA_index, tempVector);
            }
            else if (transdic.containsKey(DIA_index))
            {
                Boolean isTheSame=false;
                for (int i=0;i< transdic.get(DIA_index).size();i++)
                {
                    if (transdic.get(DIA_index).elementAt(i).getMSA().equals(touple.getMSA()) &&
                            transdic.get(DIA_index).elementAt(i).getEn().equals(touple.getEn()))
                    {
                        isTheSame=true;
                        break;

                    }
                }
                if (isTheSame.equals(false))
                    transdic.get(DIA_index).add(touple);
            }
        }

        System.out.println("TransDic Created\nTransDic Size "+ transdic.size()+"\n***********************************");
        return transdic;
    }


    public static HashMap<Integer, Vector<Vector<Integer>>> integrateAllignmentDicsWithoutWritingPhase(HashMap<Integer, Vector<Integer>> DIA_Eng_AlignmentDataDic,
                                                                                                       HashMap<Integer, Vector<Integer>> MSA_Eng_AlignmentDataDic) throws IOException
    {
        System.out.println("integrateAllignment_dics started");

        //First we write the integrated dictionary in a text file and then read and convert the text file based on DIA as a pivot
        // BufferedWriter En_rulled_out_from_DIA_writer= new BufferedWriter(new FileWriter("Eng_rulled_out_from_DIA"));
        // BufferedWriter En_rulled_out_from_MSA_writer= new BufferedWriter(new FileWriter("Eng_rulled_out_from_MSA"));

        // En_rulled_out_from_DIA_writer.write("En" +"\t" +"DIA" + "\n");
        // En_rulled_out_from_MSA_writer.write("En" + "\t" + "MSA" + "\n");

        HashMap<Integer, Vector<Vector<Integer>>> transdic=new HashMap<Integer, Vector<Vector<Integer>>>();
        int counter=0;
        int NoEnMatches=0;

        //writing triples with the same Eng glosses
        for (Integer En:DIA_Eng_AlignmentDataDic.keySet())
        {
            if(MSA_Eng_AlignmentDataDic.containsKey(En))
            {
                NoEnMatches++;

                for(Integer DIA: DIA_Eng_AlignmentDataDic.get(En))
                {
                    for(Integer MSA: MSA_Eng_AlignmentDataDic.get(En))
                    {
                        counter++;
                        if(counter%10000000==0)
                            System.out.println(counter);

                        // utilities.MSAEnTuple touple= new utilities.MSAEnTuple(MSA,En);
                        Vector<Integer> MSAEnTuple= new Vector<Integer>();
                        MSAEnTuple.add(MSA);
                        MSAEnTuple.add(En);
                        if (!transdic.containsKey(DIA))
                        {
                            Vector<Vector<Integer>> tempVector= new Vector<Vector<Integer>>();
                            tempVector.add(MSAEnTuple);
                            transdic.put(DIA, tempVector);
                        }
                        else if (transdic.containsKey(DIA))
                        {
                            Boolean isTheSame=false;
                            for (Vector<Integer> transDicTuple: transdic.get(DIA))
                            {
                                if (transDicTuple.elementAt(0).equals(MSA) && transDicTuple.elementAt(1).equals(En))
                                {
                                    isTheSame=true;
                                    break;

                                }
                            }
                            if (isTheSame.equals(false))
                                transdic.get(DIA).add(MSAEnTuple);
                        }
                    }
                }

            }
            /*
            else  // we should keep track of the Eng glosses that are rulled out from DIA_En alignment Dic here
            {
                for (Integer DIA:DIA_Eng_AlignmentDataDic.get(En))
                {
                    En_rulled_out_from_DIA_writer.write( get_En_Word(En) + '\t' +get_DIA_Word(DIA) + '\n');
                }
            } */

        }
        /*

        for (Integer En: MSA_Eng_AlignmentDataDic.keySet())
        {
            if (!DIA_Eng_AlignmentDataDic.containsKey(En))
            {
                for (Integer MSA:MSA_Eng_AlignmentDataDic.get(En))
                {
                    En_rulled_out_from_MSA_writer.write(get_En_Word(En) + '\t' + get_MSA_Word(MSA)+ '\n');
                }
            }

        }
        En_rulled_out_from_DIA_writer.flush();
        En_rulled_out_from_DIA_writer.close();
        En_rulled_out_from_MSA_writer.flush();
        En_rulled_out_from_MSA_writer.close();
        */
        System.out.println("NoEnMatches "+ NoEnMatches);
        System.out.println("Creating Transdic finshed\nTransDic size "+transdic.size()+"\n*****************************");
        return transdic;
    }



    public static HashMap<Integer, Vector<Vector<Integer>>> integrateAlignmentCompareWithoutWritingPhase(HashMap<Integer, Vector<Integer>> DIA_Eng_AlignmentDataDic,
                                                                                                         HashMap<Integer, Vector<Integer>> MSA_Eng_AlignmentDataDic) throws IOException
    {
        System.out.println("IntegrateAllignment and Compare Using HashMap started");

        HashMap<Integer, Vector<Vector<Integer>>> transdic=new HashMap<Integer, Vector<Vector<Integer>>>();
        int counter=0;
        int NoEnMatches=0;
        int DIA_same=0;
        int DIA_diff=0;
        int DIA_same_MSA_diff=0;
        int DIA_same_MSA_same=0;
        int DIA_same_MSA_same_En_diff=0;
        int DIA_same_MSA_same_En_same=0;


        ArrayList<Integer> DIA_Diff= new ArrayList<Integer>();
        ArrayList<String> DIA_Same_MSA_Diff= new ArrayList<String>();
        ArrayList<String> DIA_Same_MSA_Same_En_Diff = new ArrayList<String>();

        //writing triples with the same Eng glosses
        for (Integer En:DIA_Eng_AlignmentDataDic.keySet())
        {

            counter++;
            //System.out.println("First For on En");
            // if(counter%10==0)
            System.out.println(counter);
            if(MSA_Eng_AlignmentDataDic.containsKey(En))
            {
                NoEnMatches++;

                for(Integer DIA: DIA_Eng_AlignmentDataDic.get(En))
                {
                    //System.out.println("Second For on DIA");
                    if (!tharwa_DIA.contains(DIA))
                    {

                        //DIA Diff
                        if (!DIA_Diff.contains(DIA))
                        {
                            DIA_Diff.add(DIA);
                            DIA_diff++;
                        }

                    }
                    else
                    {
                        //DIA Same
                        DIA_same++;
                        for(Integer MSA: MSA_Eng_AlignmentDataDic.get(En))
                        {
                            // System.out.println("Third For on MSA");
                            if (!tharwa_DIA_MSA.contains(DIA+"\t"+MSA))
                            {

                                //DIA Same MSA Diff
                                if(!DIA_Same_MSA_Diff.contains(DIA+"\t"+MSA))
                                {
                                    DIA_Same_MSA_Diff.add(DIA+"\t"+MSA);
                                    DIA_same_MSA_diff++;
                                }

                            }
                            else
                            {
                                //DIA Same MSA Same
                                DIA_same_MSA_same++;

                                if (!tharwa_DIA_MSA_EN.contains(DIA+"\t"+MSA+"\t"+En))
                                {
                                   /*
                                    //DIA Same MSA Same En Diff
                                    if (!DIA_Same_MSA_Same_En_Diff.contains(DIA+"\t"+MSA+"\t"+En))
                                    {
                                        DIA_Same_MSA_Same_En_Diff.add(DIA+"\t"+MSA+"\t"+En);
                                        DIA_same_MSA_same_En_diff++;
                                    }
                                    */
                                }
                                else
                                {
                                    //DIA Same MSA Same En Same
                                    DIA_same_MSA_same_En_same++;
                                     /*
                                    // utilities.MSAEnTuple touple= new utilities.MSAEnTuple(MSA,En);
                                    Vector<Integer> utilities.MSAEnTuple= new Vector<Integer>();
                                    utilities.MSAEnTuple.add(MSA);
                                    utilities.MSAEnTuple.add(En);
                                    if (!transdic.containsKey(DIA))
                                    {
                                        Vector<Vector<Integer>> tempVector= new Vector<Vector<Integer>>();
                                        tempVector.add(utilities.MSAEnTuple);
                                        transdic.put(DIA, tempVector);
                                    }
                                    else if (transdic.containsKey(DIA))
                                    {
                                        Boolean isTheSame=false;
                                        for (Vector<Integer> transDicTuple: transdic.get(DIA))
                                        {
                                            if (transDicTuple.elementAt(0).equals(MSA) && transDicTuple.elementAt(1).equals(En))
                                            {
                                                isTheSame=true;
                                                break;

                                            }
                                        }
                                        if (isTheSame.equals(false))
                                            transdic.get(DIA).add(utilities.MSAEnTuple);
                                    }
                                    */

                                }
                            }
                        }
                    }
                }

            }

        }

        System.out.println("Writing Stats");
        System.out.println("DIA Same "+DIA_same);
        System.out.println("DIA Diff "+ DIA_diff);
        System.out.println("DIA Same MSA Diff "+DIA_same_MSA_diff);
        System.out.println("DIA Same MSA Same  "+DIA_same_MSA_same);
        System.out.println("DIA_same_MSA_same_En_diff "+DIA_same_MSA_same_En_diff);
        System.out.println("DIA_same_MSA_same_En_same "+DIA_same_MSA_same_En_same);


        System.out.println("NoEnMatches "+ NoEnMatches);
        System.out.println("Creating Transdic finshed\nTransDic size "+transdic.size()+"\n*****************************");
        return transdic;
    }




    public static ArrayList<ArrayList<int[]>> integrateAllignmentDicsWithoutWritingPhase_usingArrayList(ArrayList<ArrayList<Integer>> DIA_Eng_AlignmentDataDic,
                                                                                                        ArrayList<ArrayList<Integer>> MSA_Eng_AlignmentDataDic,
                                                                                                        int DIA_En_indexDic_size) throws IOException
    {
        System.out.println("integrateAllignment_dics started");

        ArrayList<ArrayList<int[]>> transdic=new ArrayList<ArrayList<int[]>>();
        //initializing transdic with some empty values
        for (int p=0;p<DIA_En_indexDic_size;p++)
        {
            transdic.add(new ArrayList<int[]>());
        }

        int counter=0;
        int NoEnMatches=0;

        int num_En_DIA_entries= DIA_Eng_AlignmentDataDic.size();
        int num_En_MSA_entries= MSA_Eng_AlignmentDataDic.size();

        //iterates on all En glooses in the DIA-En alinment Dic
        for (int EnIndex_MSA=0;EnIndex_MSA<num_En_MSA_entries;EnIndex_MSA++)
        {
            counter++;
            // if(counter%100==0)
            System.out.println(counter);

            //checks if MSA-En alignment Dic has this En
            if (DIA_Eng_AlignmentDataDic.size() > EnIndex_MSA)
            {
                NoEnMatches++;
                for (int DIA_index: DIA_Eng_AlignmentDataDic.get(EnIndex_MSA))
                {
                    for(int MSA_index:MSA_Eng_AlignmentDataDic.get(EnIndex_MSA))
                    {

                        //creating MSA-En tuple
                        int[] tempMSA_En_tuple= new int[2];
                        tempMSA_En_tuple[0]=MSA_index;
                        tempMSA_En_tuple[1]=EnIndex_MSA;

                        //adding this triple to transDic
                        //checks if transDic has already this DIA index or not?!
                        if (transdic.size() > DIA_index)
                        {
                            Boolean isTheSame=false;
                            for (int[] MSAEnTuple:transdic.get(DIA_index))
                            {
                                if (MSAEnTuple[0]==MSA_index && MSAEnTuple[1]==EnIndex_MSA)
                                {
                                    isTheSame=true;
                                    break;

                                }
                            }
                            if (isTheSame.equals(false))
                            {
                                transdic.get(DIA_index).add(tempMSA_En_tuple);

                            }
                        }
                        else
                        //transDic does not contain ths DIA lemma
                        {
                            ArrayList<int[]> tempArrayList= new ArrayList<int[]>();
                            tempArrayList.add(tempMSA_En_tuple);
                            transdic.add(DIA_index,tempArrayList);

                        }
                    }
                }

            }
        }

        System.out.println("NoEnMatches "+ NoEnMatches);
        System.out.println("Creating Transdic finshed\nTransDic size "+transdic.size()+"\n*****************************");
        return transdic;
    }



    public static ArrayList<ArrayList<int[]>> integrateAllignmentCompare_usingArrayList(ArrayList<ArrayList<Integer>> DIA_Eng_AlignmentDataDic,
                                                                                        ArrayList<ArrayList<Integer>> MSA_Eng_AlignmentDataDic,
                                                                                        int DIA_En_indexDic_size) throws IOException
    {
        System.out.println("IntegrateAllignments and Compare Started");

        ArrayList<ArrayList<int[]>> transdic=new ArrayList<ArrayList<int[]>>();
        //initializing transdic with some empty values
        for (int p=0;p<DIA_En_indexDic_size;p++)
        {
            transdic.add(new ArrayList<int[]>());
        }

        int counter=0;
        int NoEnMatches=0;
        int DIA_same=0;
        int MSA_same_En_same=0;
        int MSA_same_En_diff=0;
        int MSA_diff_En_diff=0;
        int MSA_diff_En_same=0;



        ArrayList<Integer> DIA_diff= new ArrayList<Integer>();
        ArrayList<String> DIA_Same_MSA_diff= new ArrayList<String>();
        ArrayList<String> DIA_Same_MSA_Same_En_diff = new ArrayList<String>();

        int num_En_MSA_entries= MSA_Eng_AlignmentDataDic.size();

        //iterates on all En glooses in the DIA-En alinment Dic
        for (int EnIndex_MSA=0;EnIndex_MSA<num_En_MSA_entries;EnIndex_MSA++)
        {
            counter++;
            if(counter%1000==0)
                System.out.println(counter);

            //checks if MSA-En alignment Dic has this En
            if (DIA_Eng_AlignmentDataDic.size() > EnIndex_MSA)
            {
                NoEnMatches++;
                for (int DIA_index: DIA_Eng_AlignmentDataDic.get(EnIndex_MSA))
                {
                    if (!tharwa_DIA.contains(DIA_index))
                    {
                        //DIA Diff
                        if (!DIA_diff.contains(DIA_index))
                            DIA_diff.add(DIA_index);
                    }
                    else
                    {
                        //DIA Same
                        DIA_same++;
                        for(int MSA_index:MSA_Eng_AlignmentDataDic.get(EnIndex_MSA))
                        {

                            if (!tharwa_DIA_MSA.contains(DIA_index+"\t"+MSA_index))
                            {
                                //DIA Same MSA Diff
                                if(!DIA_Same_MSA_diff.contains(DIA_index+"\t"+MSA_index))
                                    DIA_Same_MSA_diff.add(DIA_index+"\t"+MSA_index);
                            }
                            else
                            {
                                //DIA Same MSA Same

                                if (!tharwa_DIA_MSA_EN.contains(DIA_index+"\t"+MSA_index+"\t"+EnIndex_MSA))
                                {
                                    //DIA Same MSA Same En Diff
                                    if (!DIA_Same_MSA_Same_En_diff.contains(DIA_index+"\t"+MSA_index+"\t"+EnIndex_MSA))
                                        DIA_Same_MSA_Same_En_diff.add(DIA_index+"\t"+MSA_index+"\t"+EnIndex_MSA);
                                }
                                else
                                {
                                    //DIA Same MSA Same En Same
                                    int[] tempMSA_En_tuple= new int[2];
                                    tempMSA_En_tuple[0]=MSA_index;
                                    tempMSA_En_tuple[1]=EnIndex_MSA;

                                    //adding this triple to transDic
                                    //checks if transDic has already this DIA index or not?!
                                    if (transdic.size() > DIA_index)
                                    {
                                        Boolean isTheSame=false;
                                        for (int[] MSAEnTuple:transdic.get(DIA_index))
                                        {
                                            if (MSAEnTuple[0]==MSA_index && MSAEnTuple[1]==EnIndex_MSA)
                                            {
                                                isTheSame=true;
                                                break;

                                            }
                                        }
                                        if (isTheSame.equals(false))
                                        {
                                            transdic.get(DIA_index).add(tempMSA_En_tuple);

                                        }
                                    }
                                    else
                                    //transDic does not contain ths DIA lemma
                                    {
                                        ArrayList<int[]> tempArrayList= new ArrayList<int[]>();
                                        tempArrayList.add(tempMSA_En_tuple);
                                        transdic.add(DIA_index,tempArrayList);

                                    }
                                }

                            }

                        }
                    }
                }

            }
        }


        ////////////Writing Stats
        BufferedWriter DIA_Diff_writer= new BufferedWriter(new FileWriter("DIA_Diff",true));
        BufferedWriter DIA_Same_MSA_Diff_writer= new BufferedWriter(new FileWriter("DIA_Same_MSA_Diff",true));
        BufferedWriter DIA_Same_MSA_Same_En_Diff_writer= new BufferedWriter(new FileWriter("DIA_Same_MSA_Same_En_Diff",true));
        BufferedWriter DIA_Same_MSA_Same_En_Same_writer= new BufferedWriter(new FileWriter("DIA_Same_MSA_Same_En_Same",true));

        System.out.println("Writing DIA Diff");
        for(Integer DIA:DIA_diff)
        {
            DIA_Diff_writer.write(get_DIA_Word(DIA)+"\n");
        }
        System.out.println("Writing DIA_Same_MSA_Diff");
        for (String DIA_MSA:DIA_Same_MSA_diff)
        {
            DIA_Same_MSA_Diff_writer.write(get_DIA_Word(Integer.parseInt(DIA_MSA.split("\t")[0]))+"\t"+get_MSA_Word(Integer.parseInt(DIA_MSA.split("\t")[1]))+"\n");
        }
        System.out.println("Writing DIA_Same_MSA_Same_En_Diff");
        for (String DIA_MSA_En:DIA_Same_MSA_Same_En_diff)
        {
            DIA_Same_MSA_Same_En_Diff_writer.write(get_DIA_Word(Integer.parseInt(DIA_MSA_En.split("\t")[0]))+"\t"+get_MSA_Word(Integer.parseInt(DIA_MSA_En.split("\t")[1]))+"\t"+get_En_Word(Integer.parseInt(DIA_MSA_En.split("\t")[2])));
        }
        System.out.println("Writing DIA_Same_MSA_Same_En_Same");
        for (int i=0;i< transdic.size();i++)
        {
            for (int j=0;j<transdic.get(i).size();j++)
            {
                DIA_Same_MSA_Same_En_Same_writer.write(get_DIA_Word(i)+"\t"+get_MSA_Word(transdic.get(i).get(j)[0])+"\t"+get_En_Word(transdic.get(i).get(j)[1])+"\n");
            }
        }

        System.out.println("Writing Stats");
        System.out.println("DIA_MSA: "+ DIA_same);
        System.out.println("No En Matched between Alignment Dics: "+ NoEnMatches);

        DIA_Diff_writer.flush();
        DIA_Diff_writer.close();

        DIA_Same_MSA_Diff_writer.flush();
        DIA_Same_MSA_Diff_writer.close();

        DIA_Same_MSA_Same_En_Diff_writer.flush();
        DIA_Same_MSA_Same_En_Diff_writer.close();

        DIA_Same_MSA_Same_En_Same_writer.flush();
        DIA_Same_MSA_Same_En_Same_writer.close();


        System.out.println("Creating Transdic finshed\nTransDic size "+transdic.size()+"\n*****************************");
        return transdic;
    }



    // Using this function, we convert ExtractStats.Tharwa to dictionary and also we make a list

    public static HashMap<Integer, Vector<MSAEnTuple>> ConvertTharwaToDic(String tharwaPath) throws IOException
    {
        System.out.println("***************************");
        System.out.println("ConvertTharwaToDic_EGYKey started");

        BufferedWriter tharwaWriter=new BufferedWriter(new FileWriter("tharwa"));
        BufferedReader tharwaReader = new BufferedReader(new FileReader(tharwaPath));
        HashMap<Integer, Vector<MSAEnTuple>> tharwaDic=new HashMap<Integer, Vector<MSAEnTuple>>();

        String DIA_LEMMA = "";
        String MSA_Lemma = "";
        String CODA= "";
        Integer id= 0;
        String LineToRead= tharwaReader.readLine();
        while ((LineToRead =tharwaReader.readLine()) != null)
        {
            String[] splitedLine = LineToRead.split("\t");

            id= Integer.parseInt(splitedLine[0]);
            DIA_LEMMA = splitedLine[6]; // EGY_word column
            MSA_Lemma= splitedLine[4];  //MSA-Lemma column
            CODA= splitedLine[2];      //CODA column
            String [] ENG_gloss= splitedLine[11].replace(';',' ').replace('(',' ').replace(')',' ').split(" ");   // English-Equivalent column

            // adding CODA to the CODA_freq dictionary separatly

            add_CODA_freq_dic(CODA, id);

            // seprating Eng glosses: each entry might have different Eng equivalents seprated by ";"
            for (int i=0;i<ENG_gloss.length;i++)
            {
                if(!ENG_gloss[i].equals("UNK") && ENG_gloss[i].length()>0)
                {
                    // wrtining into a text file

                    // tharwaWriter.write(id.toString()+"\t"+DIA_LEMMA+'\t'+ MSA_Lemma+'\t'+ENG_gloss[i]+'\n');

                    // creating the thatwaDic using indexed dictionaries

                    Integer DIA_index=0;
                    Integer MSA_index = 0;
                    Integer En_index= 0;
                    if(!DIA_indexDic.containsKey(DIA_LEMMA))
                        update_DIA_Index_Dic(DIA_LEMMA);
                    if (!MSA_indexDic.containsKey(MSA_Lemma))
                        update_MSA_Index_Dic(MSA_Lemma);
                    if (! En_indexDic.containsKey(ENG_gloss[i]))
                        update_En_Index_Dic(ENG_gloss[i]);

                    DIA_index = get_DIA_Index(DIA_LEMMA);
                    MSA_index = get_MSA_Index(MSA_Lemma);
                    En_index = get_En_Index(ENG_gloss[i]);
                    ArrayList<Integer> ids= new ArrayList<Integer>();
                    ids.add(id);

                    tharwaWriter.write(En_index.toString()+'\t'+ DIA_index.toString()+'\t'+MSA_index.toString()+'\n');


                    MSAEnTuple touple= new MSAEnTuple(MSA_index,En_index,ids);

                    if (!tharwaDic.containsKey(DIA_index))
                    {
                        Vector<MSAEnTuple> tempVector= new Vector<MSAEnTuple>();
                        tempVector.add(touple);
                        tharwaDic.put(DIA_index, tempVector);

                        tharwa_DIA.add(DIA_index);
                        tharwa_DIA_MSA.add(DIA_index+"\t"+MSA_index);
                        tharwa_DIA_MSA_EN.add(DIA_index+"\t"+MSA_index+"\t"+En_index);
                    }
                    else if (tharwaDic.containsKey(DIA_index))
                    {
                        Boolean isTheSame=false;
                        for (int k=0;k<tharwaDic.get(DIA_index).size();k++)
                        {
                            if (tharwaDic.get(DIA_index).elementAt(k).getMSA().equals(touple.getMSA()))
                            {
                                if (tharwaDic.get(DIA_index).elementAt(k).getEn().equals(touple.getEn()))
                                {
                                    tharwaDic.get(DIA_index).elementAt(k).setIds(id);
                                    isTheSame=true;
                                    break;
                                }

                            }
                            else
                                tharwa_DIA_MSA.add(DIA_index+"\t"+MSA_index);

                        }
                        if (isTheSame.equals(false))
                        {
                            tharwaDic.get(DIA_index).add(touple);
                            tharwa_DIA_MSA_EN.add(DIA_index+"\t"+MSA_index+"\t"+En_index);
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
        System.out.println("***************************");
        return tharwaDic;
    }


    public static Vector<Integer>  compare_Tharwa_With_Extracted_Tripels (HashMap<Integer, Vector<MSAEnTuple>> tharwaDic,HashMap<Integer,
            Vector<Vector<Integer>>> transDic, boolean isSrfFormIncluded, String outputPath) throws  IOException
    {
        System.out.println("compare TharwaDic With TransDic started ");
        BufferedWriter statisticsWriter= new BufferedWriter(new FileWriter(outputPath));

        int total_DIA_diff=0;
        Vector<Integer> totalStatistics= new Vector<Integer>(5);
        for (int j=0;j<5;j++)
            totalStatistics.add(0);

        //Compare Thrwa with integrated triples of transdic
        int transDicCounter=0;
        for (Integer DIA: transDic.keySet())
        {
            transDicCounter++;
            if(transDicCounter%10000000==0)
                System.out.println(transDicCounter);

            // if tharwa containes this DIA entry
            if (tharwaDic.containsKey(DIA))
            {
                Vector<Integer> tempStatistics= compare(DIA, transDic.get(DIA),tharwaDic.get(DIA),
                        "MSA_same_En_same" , "MSA_same_En_diff","MSA_diff_En_diff","MSA_diff_En_same",isSrfFormIncluded);
                for (Integer i=0;i<tempStatistics.size();i++)
                {
                    Integer k=totalStatistics.get(i)+tempStatistics.get(i);
                    totalStatistics.set(i,k);
                }

            }
            //If ExtractStats.Tharwa does not have this DIA entry
            else
            {

                //  write_transdic_DIA_entry(DIA, transDic.get(DIA),"DIA_diff");
                total_DIA_diff++;
            }

        }
        statisticsWriter.write("MSA_same_En_same"+ totalStatistics.elementAt(0) +"\n"+
                "MSA_diff_En_same: "+totalStatistics.elementAt(1)+"\n"+
                "MSA_same_En_diff: "+totalStatistics.elementAt(2)+"\n"+
                "MSA_diff_En_diff: "+totalStatistics.elementAt(3)+"\n"+
                "total_DIA_diff: "+total_DIA_diff+"\n");

        statisticsWriter.flush();
        statisticsWriter.close();

        System.out.println("compare TharwaDic with TransDic finished\n******************* ");
        return totalStatistics;

    }

    public static Vector<Integer> compare(Integer DIA,Vector<Vector<Integer>> transdic_touples,Vector<MSAEnTuple> tharwa_touples, String MSA_same_En_same_ids,
                                          String MSA_same_En_diff_ids,String MSA_diff_En_diff_ids,String MSA_diff_En_same_ids, boolean isSrfFormIncluded)throws IOException
    {
        Vector<Integer> statistics= new Vector<Integer>();
        /*
        BufferedWriter MSA_same_En_same_writer= new BufferedWriter(new FileWriter(MSA_same_En_same_ids,true));
        BufferedWriter MSA_same_En_diff_writer= new BufferedWriter(new FileWriter(MSA_same_En_diff_ids,true));
        BufferedWriter MSA_diff_En_diff_writer= new BufferedWriter(new FileWriter(MSA_diff_En_diff_ids,true));
        BufferedWriter MSA_diff_En_same_writer= new BufferedWriter(new FileWriter(MSA_diff_En_same_ids,true));
        */

        Integer MSA_same_En_same=0;
        Integer MSA_same_En_diff=0;
        Integer MSA_diff_En_diff=0;
        Integer MSA_diff_En_same=0;

        for (Vector<Integer> transdic_touple:transdic_touples)
        {
            for (MSAEnTuple tharwa_touple:tharwa_touples)
            {
                Integer transDicMSA= transdic_touple.elementAt(0);
                Integer tranDicEn= transdic_touple.elementAt(1);
                if (transDicMSA.equals(tharwa_touple.getMSA()) &&
                        is_En_Equal(tranDicEn,tharwa_touple.getEn(),isSrfFormIncluded))
                {
                    MSA_same_En_same++;
                    // MSA_same_En_same_writer.write(tharwa_touple.getIds().toString() +"\t"+ get_DIA_Word(DIA) + "\t" + get_MSA_Word(tharwa_touple.getMSA_words())
                    //        +"\t"+ get_En_Word(tharwa_touple.getEn()) +"\n");
                }
                else if (transDicMSA.equals(tharwa_touple.getMSA()) &&
                        !is_En_Equal(tranDicEn, tharwa_touple.getEn(),isSrfFormIncluded))
                {
                    MSA_same_En_diff++;
                    //  MSA_same_En_diff_writer.write(tharwa_touple.getIds().toString() + "\t"+ get_DIA_Word(DIA) + "\t" + get_MSA_Word(tharwa_touple.getMSA_words())
                    //          +"\t"+ get_En_Word(tharwa_touple.getEn())+ "\n");
                }

                else if (!transDicMSA.equals(tharwa_touple.getMSA()) &&
                        is_En_Equal(tranDicEn, tharwa_touple.getEn(),isSrfFormIncluded))
                {
                    MSA_diff_En_same++;
                    // MSA_diff_En_same_writer.write(tharwa_touple.getIds().toString() +"\t"+ get_DIA_Word(DIA) + "\t" + get_MSA_Word(tharwa_touple.getMSA_words())
                    //        +"\t"+ get_En_Word(tharwa_touple.getEn()) + "\n");
                }

                else if (!transDicMSA.equals(tharwa_touple.getMSA()) &&
                        !is_En_Equal(tranDicEn, tharwa_touple.getEn(),isSrfFormIncluded))
                {
                    MSA_diff_En_diff++;
                    //  MSA_diff_En_diff_writer.write(tharwa_touple.getIds().toString() + "\t"+ get_DIA_Word(DIA) + "\t" + get_MSA_Word(tharwa_touple.getMSA_words())
                    //         +"\t"+ get_En_Word(tharwa_touple.getEn()) +"\n");
                }
            }
        }

        /*
        MSA_diff_En_diff_writer.flush();
        MSA_diff_En_diff_writer.close();
        MSA_same_En_diff_writer.flush();
        MSA_same_En_diff_writer.close();
        MSA_diff_En_same_writer.flush();
        MSA_diff_En_same_writer.close();
        MSA_same_En_same_writer.flush();
        MSA_same_En_same_writer.close();
        */

        statistics.add(MSA_same_En_same);
        statistics.add(MSA_diff_En_same);
        statistics.add(MSA_same_En_diff);
        statistics.add(MSA_diff_En_diff);
        return statistics;
    }


    public static boolean is_En_Equal (Integer transDicEnGloss, Integer TharwaEnGloss, boolean isSrfFormIncluded)
    {
        boolean is_Equal= false;
        if (isSrfFormIncluded==true)
        {
            if (transDicEnGloss.equals(TharwaEnGloss) || En_lemma_surf_Dic.get(transDicEnGloss).contains(TharwaEnGloss))
                is_Equal= true;
        }
        else
        {
            if (transDicEnGloss.equals(TharwaEnGloss))
                is_Equal=true;
        }

        return is_Equal;
    }

    public static void write_transdic_DIA_entry(Integer DIA_index, Vector<MSAEnTuple> transdic_tuples,String DIA_diff_ids) throws IOException
    {
        BufferedWriter DIA_diff_writer= new BufferedWriter( new FileWriter(DIA_diff_ids,true));
        for (MSAEnTuple tuple: transdic_tuples)
        {
            DIA_diff_writer.write(DIA_indexDic.get(DIA_index) + "\t" + MSA_indexDic.get(tuple.getMSA()) + "\t" + En_indexDic.get(tuple.getEn()) + "\n");
        }
        DIA_diff_writer.flush();
        DIA_diff_writer.close();
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



    public static void update_lemma_surface_dic (Integer En_lemma_index, Integer En_surface_index)
    {
        if (!En_lemma_surf_Dic.containsKey(En_lemma_index))
        {
            Vector <Integer> tempVector= new Vector<Integer>();
            tempVector.add(En_surface_index);
            En_lemma_surf_Dic.put(En_lemma_index, tempVector);
        }
        else if (En_lemma_surf_Dic.containsKey(En_lemma_index))
        {
            if (!En_lemma_surf_Dic.get(En_lemma_index).contains(En_surface_index))
                En_lemma_surf_Dic.get(En_lemma_index).add(En_surface_index);
        }

    }

    // this method will be called during constructing ExtractStats.Tharwa dictionary

    public static void add_CODA_freq_dic (String CODA, Integer id)
    {
        if(! CODA_freq.containsKey(CODA))
        {
            Vector <Integer> tempVector = new Vector<Integer>();
            tempVector. add(id);
            Integer freq= 0;
            IdFreqTuple idFreqTuple= new IdFreqTuple(tempVector, freq);
            CODA_freq.put(CODA, idFreqTuple);
        }
        else
        {
            CODA_freq.get(CODA).setId(id);
        }
    }



    // this method will be called to update_all_dics the frequency during scanning the ARZ data
    public static void update_CODA_freq (String[] Ar_words)
    {
        for (String word:Ar_words)
        {
            if (CODA_freq.containsKey(word))
            {
                int freq= CODA_freq.get(word).getFreq();
                CODA_freq .get(word).setFreq(++freq);
            }
        }
    }



    public static void write_CODA_freq ( String CODA_freq_path)throws  IOException
    {
        System.out.println("Write CODA-FREQ Dic Started");
        BufferedWriter writer= new BufferedWriter(new FileWriter(CODA_freq_path));
        for (String CODA: CODA_freq.keySet())
        {
            writer.write(CODA_freq.get(CODA).getIds().toString() + "\t" + CODA + "\t" + CODA_freq.get(CODA).getFreq().toString() + "\n");
        }
        System.out.println("Write CODA-FREQ Dic finished");
        System.out.println("CODA-FREQ Dic size "+ CODA_freq.size()+"\n**********************");
        writer.flush();
        writer.close();
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



    public static void writeIndexedTransDic(HashMap<Integer, Vector<MSAEnTuple>> transDic) throws IOException{
        System.out.println("Writing Indexed TransDic started");
        BufferedWriter TransDic_index_writer = new BufferedWriter(new FileWriter("transDic_indexed"));
        TransDic_index_writer.write("DIA\tMSA\tEn");
        for (int DIA:transDic.keySet())
        {
            for(MSAEnTuple tuple:transDic.get(DIA))
            {
                TransDic_index_writer.write(DIA+"\t"+tuple.getMSA()+"\t"+tuple.getEn());
            }
        }

        TransDic_index_writer.flush();
        TransDic_index_writer.close();
        System.out.println("Writing Indexed TransDic Finished\n*********************************");

    }



    public static void writeTokenizedTransDic(HashMap<Integer, Vector<String>> transDic) throws IOException{
        System.out.println("Writing Tokenized TransDic started");
        BufferedWriter TransDic_normal_writer = new BufferedWriter(new FileWriter("transDict_tokens"));
        TransDic_normal_writer.write("DIA\tMSA\tEn");
        for (int DIA:transDic.keySet())
        {
            for(String tuple:transDic.get(DIA))
            {
                //TransDic_normal_writer.write(get_DIA_Word(DIA)+"\t"+get_MSA_Word(tuple.getMSA_words())+"\t"+get_En_Word(tuple.getEn()));
                TransDic_normal_writer.write(get_DIA_Word(DIA)+"\t"+get_MSA_Word(Integer.parseInt(tuple.split("\t")[0]))+"\t"+get_En_Word(Integer.parseInt(tuple.split("\t")[1])));
            }
        }

        TransDic_normal_writer.flush();
        TransDic_normal_writer.close();
        System.out.println("Writing Tokenized TransDic Finished\n*********************************");
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
